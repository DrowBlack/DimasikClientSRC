package org.openjdk.nashorn.internal.runtime;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.SwitchPoint;
import java.lang.invoke.TypeDescriptor;
import jdk.dynalink.CallSiteDescriptor;
import jdk.dynalink.NamedOperation;
import jdk.dynalink.Operation;
import jdk.dynalink.StandardOperation;
import jdk.dynalink.linker.GuardedInvocation;
import jdk.dynalink.linker.LinkRequest;
import org.openjdk.nashorn.api.scripting.AbstractJSObject;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import org.openjdk.nashorn.internal.lookup.Lookup;
import org.openjdk.nashorn.internal.runtime.FindProperty;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.Scope;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.linker.NashornCallSiteDescriptor;
import org.openjdk.nashorn.internal.runtime.linker.NashornGuards;

public final class WithObject
extends Scope {
    private static final MethodHandle WITHEXPRESSIONGUARD = WithObject.findOwnMH("withExpressionGuard", Boolean.TYPE, Object.class, PropertyMap.class, SwitchPoint[].class);
    private static final MethodHandle WITHEXPRESSIONFILTER = WithObject.findOwnMH("withFilterExpression", Object.class, Object.class);
    private static final MethodHandle WITHSCOPEFILTER = WithObject.findOwnMH("withFilterScope", Object.class, Object.class);
    private static final MethodHandle BIND_TO_EXPRESSION_OBJ = WithObject.findOwnMH("bindToExpression", Object.class, Object.class, Object.class);
    private static final MethodHandle BIND_TO_EXPRESSION_FN = WithObject.findOwnMH("bindToExpression", Object.class, ScriptFunction.class, Object.class);
    private final ScriptObject expression;

    WithObject(ScriptObject scope, ScriptObject expression) {
        super(scope, null);
        this.expression = expression;
        this.setIsInternal();
    }

    @Override
    public boolean delete(Object key, boolean strict) {
        ScriptObject self = this.expression;
        String propName = JSType.toString(key);
        FindProperty find = self.findProperty(propName, true);
        if (find != null) {
            return self.delete(propName, strict);
        }
        return false;
    }

    @Override
    public GuardedInvocation lookup(CallSiteDescriptor desc, LinkRequest request) {
        if (request.isCallSiteUnstable()) {
            return super.lookup(desc, request);
        }
        GuardedInvocation link = null;
        Operation op = desc.getOperation();
        assert (op instanceof NamedOperation);
        String name = ((NamedOperation)op).getName().toString();
        FindProperty find = this.expression.findProperty(name, true);
        if (find != null && (link = this.expression.lookup(desc, request)) != null) {
            return WithObject.fixExpressionCallSite(desc, link);
        }
        ScriptObject scope = this.getProto();
        find = scope.findProperty(name, true);
        if (find != null) {
            return this.fixScopeCallSite(scope.lookup(desc, request), name, find.getOwner());
        }
        Operation firstOp = NashornCallSiteDescriptor.getBaseOperation(desc);
        String fallBack = firstOp == StandardOperation.GET ? (NashornCallSiteDescriptor.isMethodFirstOperation(desc) ? "__noSuchMethod__" : "__noSuchProperty__") : null;
        if (fallBack != null && (find = this.expression.findProperty(fallBack, true)) != null) {
            if ("__noSuchMethod__".equals(fallBack)) {
                link = this.expression.noSuchMethod(desc, request).addSwitchPoint(this.getProtoSwitchPoint(name));
            } else if ("__noSuchProperty__".equals(fallBack)) {
                link = this.expression.noSuchProperty(desc, request).addSwitchPoint(this.getProtoSwitchPoint(name));
            }
        }
        if (link != null) {
            return WithObject.fixExpressionCallSite(desc, link);
        }
        link = scope.lookup(desc, request);
        if (link != null) {
            return this.fixScopeCallSite(link, name, null);
        }
        return null;
    }

    @Override
    protected FindProperty findProperty(Object key, boolean deep, boolean isScope, ScriptObject start) {
        FindProperty exprProperty = this.expression.findProperty(key, true, false, this.expression);
        if (exprProperty != null) {
            return exprProperty;
        }
        return super.findProperty(key, deep, isScope, start);
    }

    @Override
    protected Object invokeNoSuchProperty(Object key, boolean isScope, int programPoint) {
        Object func;
        FindProperty find = this.expression.findProperty("__noSuchProperty__", true);
        if (find != null && (func = find.getObjectValue()) instanceof ScriptFunction) {
            ScriptFunction sfunc = (ScriptFunction)func;
            ScriptObject self = isScope && sfunc.isStrict() ? ScriptRuntime.UNDEFINED : this.expression;
            return ScriptRuntime.apply(sfunc, self, key);
        }
        return this.getProto().invokeNoSuchProperty(key, isScope, programPoint);
    }

    @Override
    public void setSplitState(int state) {
        ((Scope)this.getNonWithParent()).setSplitState(state);
    }

    @Override
    public int getSplitState() {
        return ((Scope)this.getNonWithParent()).getSplitState();
    }

    @Override
    public void addBoundProperties(ScriptObject source, Property[] properties) {
        this.getNonWithParent().addBoundProperties(source, properties);
    }

    private ScriptObject getNonWithParent() {
        ScriptObject proto;
        for (proto = this.getProto(); proto != null && proto instanceof WithObject; proto = proto.getProto()) {
        }
        return proto;
    }

    private static GuardedInvocation fixReceiverType(GuardedInvocation link, MethodHandle filter) {
        MethodType invType = link.getInvocation().type();
        MethodType newInvType = invType.changeParameterType(0, (Class<?>)filter.type().returnType());
        return link.asType(newInvType);
    }

    private static GuardedInvocation fixExpressionCallSite(CallSiteDescriptor desc, GuardedInvocation link) {
        if (NashornCallSiteDescriptor.getBaseOperation(desc) != StandardOperation.GET || !NashornCallSiteDescriptor.isMethodFirstOperation(desc)) {
            return WithObject.fixReceiverType(link, WITHEXPRESSIONFILTER).filterArguments(0, WITHEXPRESSIONFILTER);
        }
        MethodHandle linkInvocation = link.getInvocation();
        MethodType linkType = linkInvocation.type();
        boolean linkReturnsFunction = ScriptFunction.class.isAssignableFrom((Class<?>)linkType.returnType());
        return link.replaceMethods(Lookup.MH.foldArguments(linkReturnsFunction ? BIND_TO_EXPRESSION_FN : BIND_TO_EXPRESSION_OBJ, WithObject.filterReceiver(linkInvocation.asType(linkType.changeReturnType(linkReturnsFunction ? ScriptFunction.class : Object.class).changeParameterType(0, Object.class)), WITHEXPRESSIONFILTER)), WithObject.filterGuardReceiver(link, WITHEXPRESSIONFILTER));
    }

    private GuardedInvocation fixScopeCallSite(GuardedInvocation link, String name, ScriptObject owner) {
        GuardedInvocation newLink = WithObject.fixReceiverType(link, WITHSCOPEFILTER);
        MethodHandle expressionGuard = this.expressionGuard(name, owner);
        MethodHandle filteredGuard = WithObject.filterGuardReceiver(newLink, WITHSCOPEFILTER);
        return link.replaceMethods(WithObject.filterReceiver(newLink.getInvocation(), WITHSCOPEFILTER), NashornGuards.combineGuards(expressionGuard, filteredGuard));
    }

    private static MethodHandle filterGuardReceiver(GuardedInvocation link, MethodHandle receiverFilter) {
        MethodHandle test = link.getGuard();
        if (test == null) {
            return null;
        }
        TypeDescriptor.OfField receiverType = test.type().parameterType(0);
        MethodHandle filter = Lookup.MH.asType(receiverFilter, receiverFilter.type().changeParameterType(0, (Class<?>)receiverType).changeReturnType((Class<?>)receiverType));
        return WithObject.filterReceiver(test, filter);
    }

    private static MethodHandle filterReceiver(MethodHandle mh, MethodHandle receiverFilter) {
        return Lookup.MH.filterArguments(mh, 0, receiverFilter.asType(receiverFilter.type().changeReturnType((Class<?>)mh.type().parameterType(0))));
    }

    public static Object withFilterExpression(Object receiver) {
        return ((WithObject)receiver).expression;
    }

    private static Object bindToExpression(Object fn, final Object receiver) {
        ScriptObjectMirror mirror;
        if (fn instanceof ScriptFunction) {
            return WithObject.bindToExpression((ScriptFunction)fn, receiver);
        }
        if (fn instanceof ScriptObjectMirror && (mirror = (ScriptObjectMirror)fn).isFunction()) {
            return new AbstractJSObject(){

                @Override
                public Object call(Object thiz, Object ... args) {
                    return mirror.call(WithObject.withFilterExpression(receiver), args);
                }
            };
        }
        return fn;
    }

    private static Object bindToExpression(ScriptFunction fn, Object receiver) {
        return fn.createBound(WithObject.withFilterExpression(receiver), ScriptRuntime.EMPTY_ARRAY);
    }

    private MethodHandle expressionGuard(String name, ScriptObject owner) {
        PropertyMap map = this.expression.getMap();
        SwitchPoint[] sp = this.expression.getProtoSwitchPoints(name, owner);
        return Lookup.MH.insertArguments(WITHEXPRESSIONGUARD, 1, map, sp);
    }

    private static boolean withExpressionGuard(Object receiver, PropertyMap map, SwitchPoint[] sp) {
        return ((WithObject)receiver).expression.getMap() == map && !WithObject.hasBeenInvalidated(sp);
    }

    private static boolean hasBeenInvalidated(SwitchPoint[] switchPoints) {
        if (switchPoints != null) {
            for (SwitchPoint switchPoint : switchPoints) {
                if (!switchPoint.hasBeenInvalidated()) continue;
                return true;
            }
        }
        return false;
    }

    public static Object withFilterScope(Object receiver) {
        return ((WithObject)receiver).getProto();
    }

    public ScriptObject getExpression() {
        return this.expression;
    }

    private static MethodHandle findOwnMH(String name, Class<?> rtype, Class<?> ... types) {
        return Lookup.MH.findStatic(MethodHandles.lookup(), WithObject.class, name, Lookup.MH.type(rtype, types));
    }
}
