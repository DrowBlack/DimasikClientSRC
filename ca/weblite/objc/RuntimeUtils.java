package ca.weblite.objc;

import ca.weblite.nativeutils.NativeUtils;
import ca.weblite.objc.Message;
import ca.weblite.objc.Peerable;
import ca.weblite.objc.Proxy;
import ca.weblite.objc.Recipient;
import ca.weblite.objc.Runtime;
import ca.weblite.objc.TypeMapper;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;
import java.io.IOException;
import java.util.Arrays;

public class RuntimeUtils {
    public static Runtime rt = Runtime.INSTANCE;
    public static boolean loaded = false;

    public static Pointer cls(String name) {
        return rt.objc_lookUpClass(name);
    }

    public static Pointer cls(Peerable peer) {
        return peer.getPeer();
    }

    public static String clsName(Pointer cls) {
        return rt.class_getName(cls);
    }

    public static String clsName(Peerable peer) {
        return RuntimeUtils.clsName(peer.getPeer());
    }

    public static Pointer sel(String name) {
        return rt.sel_getUid(name);
    }

    public static Pointer sel(Peerable peer) {
        return peer.getPeer();
    }

    public static String selName(Pointer sel) {
        return rt.sel_getName(sel);
    }

    public static String selName(Peerable peer) {
        return RuntimeUtils.selName(peer.getPeer());
    }

    public static long msg(String cls, String msg, Object ... args) {
        return RuntimeUtils.msg(RuntimeUtils.cls(cls), msg, args);
    }

    public static long msg(String cls, Pointer msg, Object ... args) {
        return RuntimeUtils.msg(RuntimeUtils.cls(cls), msg, args);
    }

    public static long msg(Pointer receiver, String msg, Object ... args) {
        return rt.objc_msgSend(receiver, RuntimeUtils.sel(msg), args);
    }

    public static long msg(Pointer receiver, Pointer selector, Object ... args) {
        return rt.objc_msgSend(receiver, selector, args);
    }

    public static Pointer msgPointer(Pointer receiver, Pointer selector, Object ... args) {
        long res = RuntimeUtils.msg(receiver, selector, args);
        return new Pointer(res);
    }

    public static Pointer msgPointer(Pointer receiver, String selector, Object ... args) {
        return RuntimeUtils.msgPointer(receiver, RuntimeUtils.sel(selector), args);
    }

    public static Pointer msgPointer(String receiver, Pointer selector, Object ... args) {
        return RuntimeUtils.msgPointer(RuntimeUtils.cls(receiver), selector, args);
    }

    public static Pointer msgPointer(String receiver, String selector, Object ... args) {
        return RuntimeUtils.msgPointer(RuntimeUtils.cls(receiver), RuntimeUtils.sel(selector), args);
    }

    public static int msgInt(Pointer receiver, Pointer selector, Object ... args) {
        long res = RuntimeUtils.msg(receiver, selector, args);
        return new Long(res).intValue();
    }

    public static int msgInt(String receiver, Pointer selector, Object ... args) {
        return RuntimeUtils.msgInt(RuntimeUtils.cls(receiver), selector, args);
    }

    public static int msgInt(String receiver, String selector, Object ... args) {
        return RuntimeUtils.msgInt(RuntimeUtils.cls(receiver), RuntimeUtils.sel(selector), args);
    }

    public static int msgInt(Pointer receiver, String selector, Object ... args) {
        return RuntimeUtils.msgInt(receiver, RuntimeUtils.sel(selector), args);
    }

    public static boolean msgBoolean(Pointer receiver, Pointer selector, Object ... args) {
        long res = RuntimeUtils.msg(receiver, selector, args);
        return res > 0L;
    }

    public static boolean msgBoolean(String receiver, Pointer selector, Object ... args) {
        return RuntimeUtils.msgBoolean(RuntimeUtils.cls(receiver), selector, args);
    }

    public static boolean msgBoolean(String receiver, String selector, Object ... args) {
        return RuntimeUtils.msgBoolean(RuntimeUtils.cls(receiver), RuntimeUtils.sel(selector), args);
    }

    public static boolean msgBoolean(Pointer receiver, String selector, Object ... args) {
        return RuntimeUtils.msgBoolean(receiver, RuntimeUtils.sel(selector), args);
    }

    public static String msgString(Pointer receiver, Pointer selector, Object ... args) {
        long res = RuntimeUtils.msg(receiver, selector, args);
        return new Pointer(res).getString(0L);
    }

    public static String msgString(String receiver, Pointer selector, Object ... args) {
        return RuntimeUtils.msgString(RuntimeUtils.cls(receiver), selector, args);
    }

    public static String msgString(String receiver, String selector, Object ... args) {
        return RuntimeUtils.msgString(RuntimeUtils.cls(receiver), RuntimeUtils.sel(selector), args);
    }

    public static String msgString(Pointer receiver, String selector, Object ... args) {
        return RuntimeUtils.msgString(receiver, RuntimeUtils.sel(selector), args);
    }

    public static double msgDouble(Pointer receiver, Pointer selector, Object ... args) {
        return rt.objc_msgSend_fpret(receiver, selector, args);
    }

    public static double msgDouble(String receiver, Pointer selector, Object ... args) {
        return RuntimeUtils.msgDouble(RuntimeUtils.cls(receiver), selector, args);
    }

    public static double msgDouble(String receiver, String selector, Object ... args) {
        return RuntimeUtils.msgDouble(RuntimeUtils.cls(receiver), RuntimeUtils.sel(selector), args);
    }

    public static double msgDouble(Pointer receiver, String selector, Object ... args) {
        return RuntimeUtils.msgDouble(receiver, RuntimeUtils.sel(selector), args);
    }

    public static Object msg(boolean coerceReturn, boolean coerceArgs, Pointer receiver, Pointer selector, Object ... args) {
        String returnTypeFirstChar;
        Object[] originalArgs = args;
        Pointer methodSignature = RuntimeUtils.msgPointer(receiver, "methodSignatureForSelector:", selector);
        int numArgs = (int)RuntimeUtils.msg(methodSignature, "numberOfArguments", new Object[0]);
        if (numArgs == 2 && numArgs != args.length + 2) {
            throw new RuntimeException("Wrong argument count.  The selector " + RuntimeUtils.selName(selector) + " requires " + (numArgs - 2) + " arguments, but received " + args.length);
        }
        long returnTypePtr = RuntimeUtils.msg(methodSignature, "methodReturnType", new Object[0]);
        String returnTypeSignature = new Pointer(returnTypePtr).getString(0L);
        if (numArgs == 0 && returnTypeSignature == null) {
            return RuntimeUtils.msg(receiver, selector, args);
        }
        if (coerceArgs && args.length > 0) {
            originalArgs = Arrays.copyOf(args, args.length);
            for (int i = 0; i < args.length; ++i) {
                ByteByReference out = new ByteByReference();
                long out2 = RuntimeUtils.msg(methodSignature, "getArgumentTypeAtIndex:", i + 2);
                String argumentTypeSignature = new Pointer(out2).getString(0L);
                args[i] = TypeMapper.getInstance().jToC(args[i], argumentTypeSignature, TypeMapper.getInstance());
            }
        }
        String prefixes = "rnNoORV";
        int offset = 0;
        while (prefixes.indexOf(returnTypeSignature.charAt(offset)) != -1 && ++offset <= returnTypeSignature.length() - 1) {
        }
        if (offset > 0) {
            returnTypeSignature = returnTypeSignature.substring(offset);
        }
        if ("[{(".indexOf(returnTypeFirstChar = returnTypeSignature.substring(0, 1)) == -1) {
            if ("df".indexOf(returnTypeFirstChar) != -1) {
                Double res = RuntimeUtils.msgDouble(receiver, selector, args);
                for (int i = 0; i < args.length; ++i) {
                    Proxy.release(args[i]);
                }
                return res;
            }
            long result = RuntimeUtils.msg(receiver, selector, args);
            if (coerceReturn) {
                Object res2 = TypeMapper.getInstance().cToJ(result, returnTypeSignature, TypeMapper.getInstance());
                for (int i = 0; i < args.length; ++i) {
                    Proxy.release(args[i]);
                }
                return res2;
            }
            for (int i = 0; i < args.length; ++i) {
                Proxy.release(args[i]);
            }
            return result;
        }
        Long output = RuntimeUtils.msg(receiver, selector, args);
        for (int i = 0; i < args.length; ++i) {
            Proxy.release(args[i]);
        }
        return output;
    }

    public static int arraySize(String signature) {
        int typeIndex = 2;
        String digits = "0123456789";
        while (digits.indexOf(signature.charAt(typeIndex++)) != -1) {
        }
        return Integer.parseInt(signature.substring(1, typeIndex));
    }

    public static Pointer addr(Peerable peer) {
        return peer.getPeer();
    }

    public static Object msg(Message ... messages) {
        int i;
        for (i = 0; i < messages.length; ++i) {
            if (i > 0) {
                messages[i].previous = messages[i - 1];
            }
            if (i >= messages.length - 1) continue;
            messages[i].next = messages[i + 1];
        }
        for (i = 0; i < messages.length; ++i) {
            Message m = messages[i];
            if (m.receiver == Pointer.NULL) {
                m.receiver = (Pointer)m.previous.result;
            }
            m.beforeRequest();
            if (m.status == 1) continue;
            if (m.status == 2) break;
            boolean coerceInput = false;
            boolean coerceOutput = false;
            if (i == messages.length - 1) {
                coerceInput = m.coerceInput;
                coerceOutput = m.coerceOutput;
            }
            try {
                m.result = RuntimeUtils.msg(coerceOutput, coerceInput, m.receiver, m.selector, m.args.toArray(new Object[m.args.size()]));
            }
            catch (Exception ex) {
                m.error = ex;
            }
            m.status = 3;
        }
        if (messages.length > 0) {
            return messages[messages.length - 1].result;
        }
        throw new RuntimeException("Message queue was empty");
    }

    public static Pointer str(String str) {
        return RuntimeUtils.msgPointer("NSString", "stringWithUTF8String:", str);
    }

    public static String str(Pointer str) {
        long ptr = RuntimeUtils.msg(str, "UTF8String", new Object[0]);
        return new Pointer(ptr).getString(0L);
    }

    public static Pointer getAsReference(Object val, String signature) {
        return RuntimeUtils.getAsReferenceWrapper(val, signature).getPointer();
    }

    public static ByReference getAsReferenceWrapper(Object val, String signature) {
        String prefixes = "rnNoORV";
        int offset = 0;
        while (prefixes.indexOf(signature.charAt(offset)) != -1 && ++offset <= signature.length() - 1) {
        }
        if (offset > 0) {
            signature = signature.substring(offset);
        }
        String firstChar = signature.substring(0, 1);
        String numeric = "iIsSlLqQfd";
        switch (signature.charAt(0)) {
            case 'I': 
            case 'i': {
                if (!Integer.TYPE.isInstance(val)) {
                    if (Number.class.isInstance(val)) {
                        val = ((Number)val).intValue();
                    } else if (String.class.isInstance(val)) {
                        val = Integer.parseInt((String)val);
                    } else {
                        throw new RuntimeException("Attempt to pass ineligible value to int: " + val);
                    }
                }
                return new IntByReference((Integer)val);
            }
            case 'S': 
            case 's': {
                if (!Short.TYPE.isInstance(val)) {
                    if (Number.class.isInstance(val)) {
                        val = ((Number)val).shortValue();
                    } else if (String.class.isInstance(val)) {
                        val = new Integer(Integer.parseInt((String)val)).shortValue();
                    } else {
                        throw new RuntimeException("Attempt to pass ineligible value to short: " + val);
                    }
                }
                return new ShortByReference((Short)val);
            }
            case 'L': 
            case 'Q': 
            case 'l': 
            case 'q': {
                if (!Long.TYPE.isInstance(val)) {
                    if (Number.class.isInstance(val)) {
                        val = ((Number)val).longValue();
                    } else if (String.class.isInstance(val)) {
                        val = (long)new Long(Long.parseLong((String)val));
                    } else {
                        throw new RuntimeException("Attempt to pass ineligible value to long: " + val);
                    }
                }
                return new LongByReference((Long)val);
            }
            case 'f': {
                if (!Float.TYPE.isInstance(val)) {
                    if (Number.class.isInstance(val)) {
                        val = Float.valueOf(((Number)val).floatValue());
                    } else if (String.class.isInstance(val)) {
                        val = Float.valueOf(new Float(Float.parseFloat((String)val)).floatValue());
                    } else {
                        throw new RuntimeException("Attempt to pass ineligible value to long: " + val);
                    }
                }
                return new FloatByReference(((Float)val).floatValue());
            }
            case 'd': {
                if (!Double.TYPE.isInstance(val)) {
                    if (Number.class.isInstance(val)) {
                        val = ((Number)val).doubleValue();
                    } else if (String.class.isInstance(val)) {
                        val = (double)new Double(Double.parseDouble((String)val));
                    } else {
                        throw new RuntimeException("Attempt to pass ineligible value to long: " + val);
                    }
                }
                return new DoubleByReference((Double)val);
            }
            case 'B': 
            case 'C': 
            case 'b': 
            case 'c': {
                if (Number.class.isInstance(val)) {
                    val = ((Number)val).byteValue();
                } else if (String.class.isInstance(val)) {
                    val = (byte)new Byte(Byte.parseByte((String)val));
                } else {
                    throw new RuntimeException("Attempt to pass ineligible value to byte: " + val);
                }
                return new ByteByReference((Byte)val);
            }
            case 'v': {
                return null;
            }
        }
        if (val == null) {
            try {
                throw new RuntimeException("Checking stack trace for val " + val + " and signature " + signature);
            }
            catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
        if (Pointer.class.isInstance(val)) {
            return new PointerByReference((Pointer)val);
        }
        if (Long.class.isInstance(val) || Long.TYPE.isInstance(val)) {
            return new PointerByReference(new Pointer((Long)val));
        }
        throw new RuntimeException("Don't know what to do for conversion of value " + val + " and signature " + signature);
    }

    public static native void init();

    public static native long createProxy(Recipient var0);

    public static native Recipient getJavaPeer(long var0);

    static {
        try {
            NativeUtils.loadLibraryFromJar("/libjcocoa.dylib");
            loaded = true;
        }
        catch (UnsatisfiedLinkError err) {
            err.printStackTrace(System.err);
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        RuntimeUtils.init();
    }
}
