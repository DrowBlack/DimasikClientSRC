package ca.weblite.objc;

import ca.weblite.objc.Client;
import ca.weblite.objc.Message;
import ca.weblite.objc.PeerableRecipient;
import ca.weblite.objc.Proxy;
import ca.weblite.objc.Runtime;
import ca.weblite.objc.RuntimeUtils;
import ca.weblite.objc.TypeMapper;
import ca.weblite.objc.annotations.Msg;
import com.sun.jna.Function;
import com.sun.jna.Pointer;
import com.sun.jna.PointerTool;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class NSObject
extends Proxy
implements PeerableRecipient {
    public Pointer parent;
    private Pointer cls;
    private static Map<Class, Map<String, Method>> methodMap = new HashMap<Class, Map<String, Method>>();

    protected static Map<String, Method> getMethodMap(Class cls) {
        Map<String, Method> mm = methodMap.get(cls);
        if (mm == null) {
            mm = new HashMap<String, Method>();
            Method[] methods = cls.getMethods();
            for (int i = 0; i < methods.length; ++i) {
                Method method = methods[i];
                Msg message = method.getAnnotation(Msg.class);
                if (message == null) continue;
                mm.put(message.selector(), method);
            }
            methodMap.put(cls, mm);
        }
        return mm;
    }

    public NSObject(String className) {
        this();
        this.init(className);
    }

    public NSObject() {
    }

    public NSObject(Pointer peer) {
        super(peer);
    }

    public NSObject(Client c) {
        super(c);
    }

    public NSObject(Client c, Pointer peer) {
        super(c, peer);
    }

    public NSObject init(Pointer parent) {
        this.cls = Runtime.INSTANCE.object_getClass(parent);
        this.parent = parent;
        if (this.peer == Pointer.NULL) {
            this.peer = new Pointer(RuntimeUtils.createProxy(this));
        }
        return this;
    }

    public NSObject init(String cls) {
        Pointer res = Client.getRawClient().sendPointer(cls, "alloc", new Object[0]);
        Client.getRawClient().sendPointer(res, "init", new Object[0]);
        return this.init(res);
    }

    public Method methodForSelector(String selector) {
        return NSObject.getMethodMap(this.getClass()).get(selector);
    }

    public Pointer methodSignatureForSelector(Pointer selector) {
        long res = this.methodSignatureForSelector(PointerTool.getPeer(selector));
        return new Pointer(res);
    }

    @Override
    public long methodSignatureForSelector(long lselector) {
        Pointer selector = new Pointer(lselector);
        Method method = this.methodForSelector(RuntimeUtils.selName(selector));
        if (method != null) {
            Msg message = method.getAnnotation(Msg.class);
            if (!"".equals(message.signature())) {
                long res = PointerTool.getPeer(RuntimeUtils.msgPointer(RuntimeUtils.cls("NSMethodSignature"), "signatureWithObjCTypes:", message.signature()));
                return res;
            }
            if (!"".equals(message.like())) {
                String[] parts = message.like().split("\\.");
                Proxy instance = this.client.chain(parts[0], "alloc", new Object[0]).chain("init", new Object[0]);
                Pointer out = RuntimeUtils.msgPointer(instance.getPeer(), "methodSignatureForSelector:", RuntimeUtils.sel(parts[1]));
                return PointerTool.getPeer(out);
            }
        }
        return PointerTool.getPeer(RuntimeUtils.msgPointer(this.parent, "methodSignatureForSelector:", selector));
    }

    public void forwardInvocationToParent(Pointer invocation) {
        this.forwardInvocationToParent(PointerTool.getPeer(invocation));
    }

    public void forwardInvocationToParent(long linvocation) {
        Pointer invocation = new Pointer(linvocation);
        Client rawClient = Client.getRawClient();
        Pointer sig = RuntimeUtils.msgPointer(invocation, "methodSignature", new Object[0]);
        Proxy pSig = new Proxy(rawClient, sig);
        Pointer selector = RuntimeUtils.msgPointer(invocation, "selector", new Object[0]);
        long numArgs = (Long)pSig.send("numberOfArguments", new Object[0]);
        long respondsToSelector = RuntimeUtils.msg(this.parent, "respondsToSelector:", selector);
        if (respondsToSelector > 0L) {
            long impl = RuntimeUtils.msg(this.parent, "methodForSelector:", selector);
            Pointer pImpl = new Pointer(impl);
            Function func = Function.getFunction(pImpl);
            long returnType = (Long)pSig.send("methodReturnType", new Object[0]);
            String strReturnType = new Pointer(returnType).getString(0L);
            String prefixes = "rnNoORV";
            int offset = 0;
            while (prefixes.indexOf(strReturnType.charAt(offset)) != -1 && ++offset <= strReturnType.length() - 1) {
            }
            if (offset > 0) {
                strReturnType = strReturnType.substring(offset);
            }
            Object[] args = new Object[new Long(numArgs).intValue()];
            args[0] = this.peer;
            args[1] = this.parent;
            int i = 2;
            while ((long)i < numArgs) {
                long argumentSigAddr = (Long)pSig.send("getArgumentTypeAtIndex:", i);
                String argumentSignature = new Pointer(argumentSigAddr).getString(0L);
                LongByReference ptrRef = new LongByReference();
                RuntimeUtils.msg(invocation, "getArgument:atIndex:", ptrRef.getPointer(), i);
                args[i] = ptrRef.getValue();
                ++i;
            }
            char retTypeChar = strReturnType.charAt(0);
            Class<Object> retType = null;
            switch (retTypeChar) {
                case 'v': {
                    retType = Void.TYPE;
                    break;
                }
                case 'f': {
                    retType = Float.TYPE;
                    break;
                }
                case 'd': {
                    retType = Double.TYPE;
                    break;
                }
                case '*': {
                    retType = String.class;
                    break;
                }
                case 'B': 
                case 'C': 
                case 'I': 
                case 'S': 
                case 'c': 
                case 'i': 
                case 's': {
                    retType = Integer.TYPE;
                    break;
                }
                case 'L': 
                case 'Q': 
                case 'l': 
                case 'q': {
                    retType = Long.TYPE;
                    break;
                }
                case '#': 
                case ':': 
                case '?': 
                case '@': 
                case '^': {
                    retType = Pointer.class;
                    break;
                }
                default: {
                    RuntimeUtils.msg(invocation, "invokeWithTarget:", this.parent);
                    return;
                }
            }
            Object retVal = func.invoke(retType, args);
            if (!Void.TYPE.equals(retType)) {
                if (retVal == null) {
                    retVal = 0L;
                }
                Pointer retValRef = RuntimeUtils.getAsReference(retVal, strReturnType);
                RuntimeUtils.msg(invocation, "setReturnValue:", retValRef);
            }
        } else {
            throw new RuntimeException("Object does not handle selector " + RuntimeUtils.selName(selector));
        }
    }

    public void forwardInvocation(Pointer invocation) {
        this.forwardInvocation(PointerTool.getPeer(invocation));
    }

    @Override
    public void forwardInvocation(long linvocation) {
        Pointer invocation = new Pointer(linvocation);
        Client rawClient = Client.getRawClient();
        Pointer sig = RuntimeUtils.msgPointer(invocation, "methodSignature", new Object[0]);
        Proxy pSig = new Proxy(rawClient, sig);
        Pointer selector = RuntimeUtils.msgPointer(invocation, "selector", new Object[0]);
        long numArgs = (Long)pSig.send("numberOfArguments", new Object[0]);
        Method method = this.methodForSelector(RuntimeUtils.selName(selector));
        if (method != null) {
            Msg message = method.getAnnotation(Msg.class);
            Object[] args = new Object[new Long(numArgs).intValue() - 2];
            int i = 2;
            while ((long)i < numArgs) {
                ByReference ptrRef;
                long argumentSigAddr = (Long)pSig.send("getArgumentTypeAtIndex:", i);
                String argumentSignature = new Pointer(argumentSigAddr).getString(0L);
                if ("fd".indexOf(argumentSignature.substring(0, 1)) != -1) {
                    ptrRef = new DoubleByReference();
                    RuntimeUtils.msg(invocation, "getArgument:atIndex:", ptrRef.getPointer(), i);
                    args[i - 2] = TypeMapper.getInstance().cToJ(((DoubleByReference)ptrRef).getValue(), argumentSignature, TypeMapper.getInstance());
                } else {
                    ptrRef = new LongByReference();
                    RuntimeUtils.msg(invocation, "getArgument:atIndex:", ptrRef.getPointer(), i);
                    args[i - 2] = TypeMapper.getInstance().cToJ(((LongByReference)ptrRef).getValue(), argumentSignature, TypeMapper.getInstance());
                }
                ++i;
            }
            try {
                method.setAccessible(true);
                Object res = method.invoke((Object)this, args);
                for (int i2 = 0; i2 < args.length; ++i2) {
                    Proxy.release(args[i2]);
                }
                long returnType = (Long)pSig.send("methodReturnType", new Object[0]);
                String strReturnType = new Pointer(returnType).getString(0L);
                res = TypeMapper.getInstance().jToC(res, strReturnType, TypeMapper.getInstance());
                if (!"v".equals(strReturnType)) {
                    Pointer retVal = res == null ? new PointerByReference(Pointer.NULL).getPointer() : RuntimeUtils.getAsReference(res, strReturnType);
                    RuntimeUtils.msg(invocation, "setReturnValue:", retVal);
                }
                return;
            }
            catch (Exception ex) {
                ex.printStackTrace(System.err);
                throw new RuntimeException(ex);
            }
        }
        this.forwardInvocationToParent(invocation);
    }

    public boolean respondsToSelector(Pointer selector) {
        return this.respondsToSelector(PointerTool.getPeer(selector));
    }

    @Override
    public boolean respondsToSelector(long lselector) {
        Pointer selector = new Pointer(lselector);
        Method method = this.methodForSelector(RuntimeUtils.selName(selector));
        if (method != null) {
            return true;
        }
        return RuntimeUtils.msg(this.parent, "respondsToSelector:", selector) > 0L;
    }

    @Override
    public NSObject chain(Pointer selector, Object ... args) {
        return (NSObject)super.chain(selector, args);
    }

    @Override
    public NSObject chain(String selector, Object ... args) {
        return (NSObject)super.chain(selector, args);
    }

    @Override
    public NSObject chain(Message ... msgs) {
        return (NSObject)super.chain(msgs);
    }

    public NSObject dealloc() {
        this.send("dealloc", new Object[0]);
        return this;
    }
}
