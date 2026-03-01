package org.openjdk.nashorn.internal.runtime.linker;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import jdk.dynalink.CallSiteDescriptor;
import jdk.dynalink.NamedOperation;
import jdk.dynalink.NamespaceOperation;
import jdk.dynalink.Operation;
import jdk.dynalink.StandardNamespace;
import jdk.dynalink.StandardOperation;
import org.openjdk.nashorn.internal.runtime.AccessControlContextFactory;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.linker.NameCodec;

public final class NashornCallSiteDescriptor
extends CallSiteDescriptor {
    public static final int GET_PROPERTY = 0;
    public static final int GET_ELEMENT = 1;
    public static final int GET_METHOD_PROPERTY = 2;
    public static final int GET_METHOD_ELEMENT = 3;
    public static final int SET_PROPERTY = 4;
    public static final int SET_ELEMENT = 5;
    public static final int REMOVE_PROPERTY = 6;
    public static final int REMOVE_ELEMENT = 7;
    public static final int CALL = 8;
    public static final int NEW = 9;
    private static final int OPERATION_MASK = 15;
    private static final Operation[] OPERATIONS = new Operation[]{StandardOperation.GET.withNamespaces(StandardNamespace.PROPERTY, StandardNamespace.ELEMENT, StandardNamespace.METHOD), StandardOperation.GET.withNamespaces(StandardNamespace.ELEMENT, StandardNamespace.PROPERTY, StandardNamespace.METHOD), StandardOperation.GET.withNamespaces(StandardNamespace.METHOD, StandardNamespace.PROPERTY, StandardNamespace.ELEMENT), StandardOperation.GET.withNamespaces(StandardNamespace.METHOD, StandardNamespace.ELEMENT, StandardNamespace.PROPERTY), StandardOperation.SET.withNamespaces(StandardNamespace.PROPERTY, StandardNamespace.ELEMENT), StandardOperation.SET.withNamespaces(StandardNamespace.ELEMENT, StandardNamespace.PROPERTY), StandardOperation.REMOVE.withNamespaces(StandardNamespace.PROPERTY, StandardNamespace.ELEMENT), StandardOperation.REMOVE.withNamespaces(StandardNamespace.ELEMENT, StandardNamespace.PROPERTY), StandardOperation.CALL, StandardOperation.NEW};
    public static final int CALLSITE_SCOPE = 16;
    public static final int CALLSITE_STRICT = 32;
    public static final int CALLSITE_FAST_SCOPE = 64;
    public static final int CALLSITE_OPTIMISTIC = 128;
    public static final int CALLSITE_APPLY_TO_CALL = 256;
    public static final int CALLSITE_DECLARE = 512;
    public static final int CALLSITE_PROFILE = 1024;
    public static final int CALLSITE_TRACE = 2048;
    public static final int CALLSITE_TRACE_MISSES = 4096;
    public static final int CALLSITE_TRACE_ENTEREXIT = 8192;
    public static final int CALLSITE_TRACE_VALUES = 16384;
    public static final int CALLSITE_PROGRAM_POINT_SHIFT = 15;
    public static final int MAX_PROGRAM_POINT_VALUE = 131071;
    public static final int FLAGS_MASK = Short.MAX_VALUE;
    private static final ClassValue<Map<NashornCallSiteDescriptor, NashornCallSiteDescriptor>> canonicals = new ClassValue<Map<NashornCallSiteDescriptor, NashornCallSiteDescriptor>>(){

        @Override
        protected Map<NashornCallSiteDescriptor, NashornCallSiteDescriptor> computeValue(Class<?> type) {
            return new ConcurrentHashMap<NashornCallSiteDescriptor, NashornCallSiteDescriptor>();
        }
    };
    private static final AccessControlContext GET_LOOKUP_PERMISSION_CONTEXT = AccessControlContextFactory.createAccessControlContext("dynalink.getLookup");
    private static final Map<String, Reference<NamedOperation>>[] NAMED_OPERATIONS = (Map[])Stream.generate(() -> Collections.synchronizedMap(new WeakHashMap())).limit(OPERATIONS.length).toArray(Map[]::new);
    private final int flags;

    public static void appendFlags(int flags, StringBuilder sb) {
        int pp = flags >> 15;
        if (pp != 0) {
            sb.append(" pp=").append(pp);
        }
        if ((flags & 0x10) != 0) {
            if ((flags & 0x40) != 0) {
                sb.append(" fastscope");
            } else {
                sb.append(" scope");
            }
            if ((flags & 0x200) != 0) {
                sb.append(" declare");
            }
        } else assert ((flags & 0x40) == 0) : "can't be fastscope without scope";
        if ((flags & 0x100) != 0) {
            sb.append(" apply2call");
        }
        if ((flags & 0x20) != 0) {
            sb.append(" strict");
        }
    }

    public static String getOperationName(int flags) {
        switch (flags & 0xF) {
            case 0: {
                return "GET_PROPERTY";
            }
            case 1: {
                return "GET_ELEMENT";
            }
            case 2: {
                return "GET_METHOD_PROPERTY";
            }
            case 3: {
                return "GET_METHOD_ELEMENT";
            }
            case 4: {
                return "SET_PROPERTY";
            }
            case 5: {
                return "SET_ELEMENT";
            }
            case 6: {
                return "REMOVE_PROPERTY";
            }
            case 7: {
                return "REMOVE_ELEMENT";
            }
            case 8: {
                return "CALL";
            }
            case 9: {
                return "NEW";
            }
        }
        throw new AssertionError();
    }

    public static NashornCallSiteDescriptor get(MethodHandles.Lookup lookup, String name, MethodType methodType, int flags) {
        int opIndex = flags & 0xF;
        Operation baseOp = OPERATIONS[opIndex];
        String decodedName = NameCodec.decode(name);
        Operation op = decodedName.isEmpty() ? baseOp : NashornCallSiteDescriptor.getNamedOperation(decodedName, opIndex, baseOp);
        return NashornCallSiteDescriptor.get(lookup, op, methodType, flags);
    }

    private static NamedOperation getNamedOperation(String name, int opIndex, Operation baseOp) {
        NamedOperation existing;
        Map<String, Reference<NamedOperation>> namedOps = NAMED_OPERATIONS[opIndex];
        Reference<NamedOperation> ref = namedOps.get(name);
        if (ref != null && (existing = ref.get()) != null) {
            return existing;
        }
        NamedOperation newOp = baseOp.named(name);
        namedOps.put(name, new WeakReference<NamedOperation>(newOp));
        return newOp;
    }

    private static NashornCallSiteDescriptor get(MethodHandles.Lookup lookup, Operation operation, MethodType methodType, int flags) {
        NashornCallSiteDescriptor csd = new NashornCallSiteDescriptor(lookup, operation, methodType, flags);
        if (csd.isOptimistic()) {
            return csd;
        }
        NashornCallSiteDescriptor canonical = canonicals.get(lookup.lookupClass()).putIfAbsent(csd, csd);
        return canonical != null ? canonical : csd;
    }

    private NashornCallSiteDescriptor(MethodHandles.Lookup lookup, Operation operation, MethodType methodType, int flags) {
        super(lookup, operation, methodType);
        this.flags = flags;
    }

    static MethodHandles.Lookup getLookupInternal(CallSiteDescriptor csd) {
        if (csd instanceof NashornCallSiteDescriptor) {
            return ((NashornCallSiteDescriptor)csd).getLookupPrivileged();
        }
        return AccessController.doPrivileged(csd::getLookup, GET_LOOKUP_PERMISSION_CONTEXT);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && this.flags == ((NashornCallSiteDescriptor)obj).flags;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.flags;
    }

    public static String getOperand(CallSiteDescriptor desc) {
        Operation operation = desc.getOperation();
        return operation instanceof NamedOperation ? ((NamedOperation)operation).getName().toString() : null;
    }

    private static StandardNamespace findFirstStandardNamespace(CallSiteDescriptor desc) {
        return StandardNamespace.findFirst(desc.getOperation());
    }

    public static boolean isMethodFirstOperation(CallSiteDescriptor desc) {
        return NashornCallSiteDescriptor.findFirstStandardNamespace(desc) == StandardNamespace.METHOD;
    }

    public static boolean hasStandardNamespace(CallSiteDescriptor desc) {
        return NashornCallSiteDescriptor.findFirstStandardNamespace(desc) != null;
    }

    public static Operation getBaseOperation(CallSiteDescriptor desc) {
        return NamespaceOperation.getBaseOperation(NamedOperation.getBaseOperation(desc.getOperation()));
    }

    public static StandardOperation getStandardOperation(CallSiteDescriptor desc) {
        return (StandardOperation)NashornCallSiteDescriptor.getBaseOperation(desc);
    }

    public static boolean contains(CallSiteDescriptor desc, StandardOperation operation, StandardNamespace namespace) {
        return NamespaceOperation.contains(NamedOperation.getBaseOperation(desc.getOperation()), operation, namespace);
    }

    private String getFunctionErrorMessage(Object obj) {
        String funcDesc = NashornCallSiteDescriptor.getOperand(this);
        return funcDesc != null ? funcDesc : ScriptRuntime.safeToString(obj);
    }

    public static String getFunctionErrorMessage(CallSiteDescriptor desc, Object obj) {
        return desc instanceof NashornCallSiteDescriptor ? ((NashornCallSiteDescriptor)desc).getFunctionErrorMessage(obj) : ScriptRuntime.safeToString(obj);
    }

    public static int getFlags(CallSiteDescriptor desc) {
        return desc instanceof NashornCallSiteDescriptor ? ((NashornCallSiteDescriptor)desc).flags : 0;
    }

    private boolean isFlag(int flag) {
        return (this.flags & flag) != 0;
    }

    private static boolean isFlag(CallSiteDescriptor desc, int flag) {
        return (NashornCallSiteDescriptor.getFlags(desc) & flag) != 0;
    }

    public static boolean isScope(CallSiteDescriptor desc) {
        return NashornCallSiteDescriptor.isFlag(desc, 16);
    }

    public static boolean isFastScope(CallSiteDescriptor desc) {
        return NashornCallSiteDescriptor.isFlag(desc, 64);
    }

    public static boolean isStrict(CallSiteDescriptor desc) {
        return NashornCallSiteDescriptor.isFlag(desc, 32);
    }

    public static boolean isApplyToCall(CallSiteDescriptor desc) {
        return NashornCallSiteDescriptor.isFlag(desc, 256);
    }

    public static boolean isOptimistic(CallSiteDescriptor desc) {
        return NashornCallSiteDescriptor.isFlag(desc, 128);
    }

    public static boolean isDeclaration(CallSiteDescriptor desc) {
        return NashornCallSiteDescriptor.isFlag(desc, 512);
    }

    public static boolean isStrictFlag(int flags) {
        return (flags & 0x20) != 0;
    }

    public static boolean isScopeFlag(int flags) {
        return (flags & 0x10) != 0;
    }

    public static boolean isDeclaration(int flags) {
        return (flags & 0x200) != 0;
    }

    public static int getProgramPoint(CallSiteDescriptor desc) {
        assert (NashornCallSiteDescriptor.isOptimistic(desc)) : "program point requested from non-optimistic descriptor " + desc;
        return NashornCallSiteDescriptor.getFlags(desc) >> 15;
    }

    boolean isProfile() {
        return this.isFlag(1024);
    }

    boolean isTrace() {
        return this.isFlag(2048);
    }

    boolean isTraceMisses() {
        return this.isFlag(4096);
    }

    boolean isTraceEnterExit() {
        return this.isFlag(8192);
    }

    boolean isTraceObjects() {
        return this.isFlag(16384);
    }

    boolean isOptimistic() {
        return this.isFlag(128);
    }

    @Override
    public CallSiteDescriptor changeMethodTypeInternal(MethodType newMethodType) {
        return NashornCallSiteDescriptor.get(this.getLookupPrivileged(), this.getOperation(), newMethodType, this.flags);
    }

    @Override
    protected CallSiteDescriptor changeOperationInternal(Operation newOperation) {
        return NashornCallSiteDescriptor.get(this.getLookupPrivileged(), newOperation, this.getMethodType(), this.flags);
    }
}
