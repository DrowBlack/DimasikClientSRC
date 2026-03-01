package org.openjdk.nashorn.internal.codegen;

import java.util.Arrays;
import java.util.EnumSet;
import org.openjdk.nashorn.internal.codegen.ClassEmitter;
import org.openjdk.nashorn.internal.codegen.CompileUnit;
import org.openjdk.nashorn.internal.codegen.CompilerConstants;
import org.openjdk.nashorn.internal.codegen.Label;
import org.openjdk.nashorn.internal.codegen.MethodEmitter;
import org.openjdk.nashorn.internal.codegen.types.Type;
import org.openjdk.nashorn.internal.ir.Symbol;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.UnwarrantedOptimismException;
import org.openjdk.nashorn.internal.runtime.options.Options;

class SharedScopeCall {
    public static final int SHARED_CALL_THRESHOLD = Options.getIntProperty("nashorn.shared.scope.call.threshold", 5);
    public static final int SHARED_GET_THRESHOLD = Options.getIntProperty("nashorn.shared.scope.get.threshold", 100);
    private static final CompilerConstants.Call REPLACE_PROGRAM_POINT = CompilerConstants.virtualCallNoLookup(UnwarrantedOptimismException.class, "replaceProgramPoint", UnwarrantedOptimismException.class, Integer.TYPE);
    private static final int FIXED_PARAM_COUNT = 3;
    private final Type valueType;
    private final Symbol symbol;
    private final Type returnType;
    private final Type[] paramTypes;
    private final int flags;
    private final boolean isCall;
    private final boolean isOptimistic;
    private CompileUnit compileUnit;
    private String methodName;
    private String staticSignature;

    SharedScopeCall(Symbol symbol, Type valueType, Type returnType, Type[] paramTypes, int flags, boolean isOptimistic) {
        this.symbol = symbol;
        this.valueType = valueType;
        this.returnType = returnType;
        this.paramTypes = paramTypes;
        this.flags = flags;
        this.isCall = paramTypes != null;
        this.isOptimistic = isOptimistic;
    }

    public int hashCode() {
        return this.symbol.hashCode() ^ this.returnType.hashCode() ^ Arrays.hashCode(this.paramTypes) ^ this.flags ^ Boolean.hashCode(this.isOptimistic);
    }

    public boolean equals(Object obj) {
        if (obj instanceof SharedScopeCall) {
            SharedScopeCall c = (SharedScopeCall)obj;
            return this.symbol.equals(c.symbol) && this.flags == c.flags && this.returnType.equals(c.returnType) && Arrays.equals(this.paramTypes, c.paramTypes) && this.isOptimistic == c.isOptimistic;
        }
        return false;
    }

    protected void setClassAndName(CompileUnit compileUnit, String methodName) {
        this.compileUnit = compileUnit;
        this.methodName = methodName;
    }

    public void generateInvoke(MethodEmitter method) {
        method.invokestatic(this.compileUnit.getUnitClassName(), this.methodName, this.getStaticSignature());
    }

    protected void generateScopeCall() {
        Label catchLabel;
        Label endTry;
        Label beginTry;
        ClassEmitter classEmitter = this.compileUnit.getClassEmitter();
        EnumSet<ClassEmitter.Flag> methodFlags = EnumSet.of(ClassEmitter.Flag.STATIC);
        MethodEmitter method = classEmitter.method(methodFlags, this.methodName, this.getStaticSignature());
        method.begin();
        method.load(Type.OBJECT, 0);
        method.load(Type.INT, 1);
        method.invoke(ScriptObject.GET_PROTO_DEPTH);
        assert (!this.isCall || this.valueType.isObject());
        if (this.isOptimistic) {
            beginTry = new Label("begin_try");
            endTry = new Label("end_try");
            catchLabel = new Label("catch_label");
            method.label(beginTry);
            method._try(beginTry, endTry, catchLabel, UnwarrantedOptimismException.class, false);
        } else {
            catchLabel = null;
            endTry = null;
            beginTry = null;
        }
        int getFlags = this.isOptimistic && !this.isCall ? this.flags | 0x80 : this.flags;
        method.dynamicGet(this.valueType, this.symbol.getName(), getFlags, this.isCall, false);
        if (this.isCall) {
            method.convert(Type.OBJECT);
            method.loadUndefined(Type.OBJECT);
            int slot = 3;
            for (Type type : this.paramTypes) {
                method.load(type, slot);
                slot += type.getSlots();
            }
            int callFlags = this.isOptimistic ? this.flags | 0x80 : this.flags;
            method.dynamicCall(this.returnType, 2 + this.paramTypes.length, callFlags, this.symbol.getName());
        }
        if (this.isOptimistic) {
            method.label(endTry);
        }
        method._return(this.returnType);
        if (this.isOptimistic) {
            method._catch(catchLabel);
            method.load(Type.INT, 2);
            method.invoke(REPLACE_PROGRAM_POINT);
            method.athrow();
        }
        method.end();
    }

    private String getStaticSignature() {
        if (this.staticSignature == null) {
            if (this.paramTypes == null) {
                this.staticSignature = Type.getMethodDescriptor(this.returnType, Type.typeFor(ScriptObject.class), Type.INT, Type.INT);
            } else {
                Type[] params = new Type[this.paramTypes.length + 3];
                params[0] = Type.typeFor(ScriptObject.class);
                params[1] = Type.INT;
                params[2] = Type.INT;
                System.arraycopy(this.paramTypes, 0, params, 3, this.paramTypes.length);
                this.staticSignature = Type.getMethodDescriptor(this.returnType, params);
            }
        }
        return this.staticSignature;
    }

    public String toString() {
        return this.methodName + " " + this.staticSignature;
    }
}
