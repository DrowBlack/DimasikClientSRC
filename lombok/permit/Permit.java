package lombok.permit;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import sun.misc.Unsafe;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Permit {
    private static final long ACCESSIBLE_OVERRIDE_FIELD_OFFSET;
    private static final IllegalAccessException INIT_ERROR;
    private static final Unsafe UNSAFE;

    static {
        Throwable ex;
        long g;
        UNSAFE = (Unsafe)Permit.reflectiveStaticFieldAccess(Unsafe.class, "theUnsafe");
        try {
            g = Permit.getOverrideFieldOffset();
            ex = null;
        }
        catch (Throwable t) {
            g = -1L;
            ex = t;
        }
        ACCESSIBLE_OVERRIDE_FIELD_OFFSET = g;
        if (ex == null) {
            INIT_ERROR = null;
        } else if (ex instanceof IllegalAccessException) {
            INIT_ERROR = (IllegalAccessException)ex;
        } else {
            INIT_ERROR = new IllegalAccessException("Cannot initialize Unsafe-based permit");
            INIT_ERROR.initCause(ex);
        }
    }

    private Permit() {
    }

    public static <T extends AccessibleObject> T setAccessible(T accessor) {
        if (INIT_ERROR == null) {
            UNSAFE.putBoolean(accessor, ACCESSIBLE_OVERRIDE_FIELD_OFFSET, true);
        } else {
            accessor.setAccessible(true);
        }
        return accessor;
    }

    private static long getOverrideFieldOffset() throws Throwable {
        Field f = null;
        Throwable saved = null;
        try {
            f = AccessibleObject.class.getDeclaredField("override");
        }
        catch (Throwable t) {
            saved = t;
        }
        if (f != null) {
            return UNSAFE.objectFieldOffset(f);
        }
        try {
            return UNSAFE.objectFieldOffset(Fake.class.getDeclaredField("override"));
        }
        catch (Throwable throwable) {
            throw saved;
        }
    }

    public static Method getMethod(Class<?> c, String mName, Class<?> ... parameterTypes) throws NoSuchMethodException {
        Method m = null;
        Class<?> oc = c;
        while (c != null) {
            try {
                m = c.getDeclaredMethod(mName, parameterTypes);
                break;
            }
            catch (NoSuchMethodException noSuchMethodException) {
                c = c.getSuperclass();
            }
        }
        if (m == null) {
            throw new NoSuchMethodException(String.valueOf(oc.getName()) + " :: " + mName + "(args)");
        }
        return Permit.setAccessible(m);
    }

    public static Method permissiveGetMethod(Class<?> c, String mName, Class<?> ... parameterTypes) {
        try {
            return Permit.getMethod(c, mName, parameterTypes);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static Field getField(Class<?> c, String fName) throws NoSuchFieldException {
        Field f = null;
        Class<?> oc = c;
        while (c != null) {
            try {
                f = c.getDeclaredField(fName);
                break;
            }
            catch (NoSuchFieldException noSuchFieldException) {
                c = c.getSuperclass();
            }
        }
        if (f == null) {
            throw new NoSuchFieldException(String.valueOf(oc.getName()) + " :: " + fName);
        }
        return Permit.setAccessible(f);
    }

    public static Field permissiveGetField(Class<?> c, String fName) {
        try {
            return Permit.getField(c, fName);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static <T> T permissiveReadField(Class<T> type, Field f, Object instance) {
        try {
            return type.cast(f.get(instance));
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static <T> Constructor<T> getConstructor(Class<T> c, Class<?> ... parameterTypes) throws NoSuchMethodException {
        return Permit.setAccessible(c.getDeclaredConstructor(parameterTypes));
    }

    private static Object reflectiveStaticFieldAccess(Class<?> c, String fName) {
        try {
            Field f = c.getDeclaredField(fName);
            f.setAccessible(true);
            return f.get(null);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static boolean isDebugReflection() {
        return !"false".equals(System.getProperty("lombok.debug.reflection", "false"));
    }

    public static void handleReflectionDebug(Throwable t, Throwable initError) {
        if (!Permit.isDebugReflection()) {
            return;
        }
        System.err.println("** LOMBOK REFLECTION exception: " + t.getClass() + ": " + (t.getMessage() == null ? "(no message)" : t.getMessage()));
        t.printStackTrace(System.err);
        if (initError != null) {
            System.err.println("*** ADDITIONALLY, exception occurred setting up reflection: ");
            initError.printStackTrace(System.err);
        }
    }

    public static Object invoke(Method m, Object receiver, Object ... args) throws IllegalAccessException, InvocationTargetException {
        return Permit.invoke(null, m, receiver, args);
    }

    public static Object invoke(Throwable initError, Method m, Object receiver, Object ... args) throws IllegalAccessException, InvocationTargetException {
        try {
            return m.invoke(receiver, args);
        }
        catch (IllegalAccessException e) {
            Permit.handleReflectionDebug(e, initError);
            throw e;
        }
        catch (RuntimeException e) {
            Permit.handleReflectionDebug(e, initError);
            throw e;
        }
        catch (Error e) {
            Permit.handleReflectionDebug(e, initError);
            throw e;
        }
    }

    public static Object invokeSneaky(Method m, Object receiver, Object ... args) {
        return Permit.invokeSneaky(null, m, receiver, args);
    }

    public static Object invokeSneaky(Throwable initError, Method m, Object receiver, Object ... args) {
        try {
            return m.invoke(receiver, args);
        }
        catch (NoClassDefFoundError e) {
            Permit.handleReflectionDebug(e, initError);
            return null;
        }
        catch (NullPointerException e) {
            Permit.handleReflectionDebug(e, initError);
            return null;
        }
        catch (IllegalAccessException e) {
            Permit.handleReflectionDebug(e, initError);
            throw Permit.sneakyThrow(e);
        }
        catch (InvocationTargetException e) {
            throw Permit.sneakyThrow(e.getCause());
        }
        catch (RuntimeException e) {
            Permit.handleReflectionDebug(e, initError);
            throw e;
        }
        catch (Error e) {
            Permit.handleReflectionDebug(e, initError);
            throw e;
        }
    }

    public static <T> T newInstance(Constructor<T> c, Object ... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return Permit.newInstance(null, c, args);
    }

    public static <T> T newInstance(Throwable initError, Constructor<T> c, Object ... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        try {
            return c.newInstance(args);
        }
        catch (IllegalAccessException e) {
            Permit.handleReflectionDebug(e, initError);
            throw e;
        }
        catch (InstantiationException e) {
            Permit.handleReflectionDebug(e, initError);
            throw e;
        }
        catch (RuntimeException e) {
            Permit.handleReflectionDebug(e, initError);
            throw e;
        }
        catch (Error e) {
            Permit.handleReflectionDebug(e, initError);
            throw e;
        }
    }

    public static <T> T newInstanceSneaky(Constructor<T> c, Object ... args) {
        return Permit.newInstanceSneaky(null, c, args);
    }

    public static <T> T newInstanceSneaky(Throwable initError, Constructor<T> c, Object ... args) {
        try {
            return c.newInstance(args);
        }
        catch (NoClassDefFoundError e) {
            Permit.handleReflectionDebug(e, initError);
            return null;
        }
        catch (NullPointerException e) {
            Permit.handleReflectionDebug(e, initError);
            return null;
        }
        catch (IllegalAccessException e) {
            Permit.handleReflectionDebug(e, initError);
            throw Permit.sneakyThrow(e);
        }
        catch (InstantiationException e) {
            Permit.handleReflectionDebug(e, initError);
            throw Permit.sneakyThrow(e);
        }
        catch (InvocationTargetException e) {
            throw Permit.sneakyThrow(e.getCause());
        }
        catch (RuntimeException e) {
            Permit.handleReflectionDebug(e, initError);
            throw e;
        }
        catch (Error e) {
            Permit.handleReflectionDebug(e, initError);
            throw e;
        }
    }

    public static <T> T get(Field f, Object receiver) throws IllegalAccessException {
        try {
            return (T)f.get(receiver);
        }
        catch (IllegalAccessException e) {
            Permit.handleReflectionDebug(e, null);
            throw e;
        }
        catch (RuntimeException e) {
            Permit.handleReflectionDebug(e, null);
            throw e;
        }
        catch (Error e) {
            Permit.handleReflectionDebug(e, null);
            throw e;
        }
    }

    public static void set(Field f, Object receiver, Object newValue) throws IllegalAccessException {
        try {
            f.set(receiver, newValue);
        }
        catch (IllegalAccessException e) {
            Permit.handleReflectionDebug(e, null);
            throw e;
        }
        catch (RuntimeException e) {
            Permit.handleReflectionDebug(e, null);
            throw e;
        }
        catch (Error e) {
            Permit.handleReflectionDebug(e, null);
            throw e;
        }
    }

    public static void reportReflectionProblem(Throwable initError, String msg) {
        if (!Permit.isDebugReflection()) {
            return;
        }
        System.err.println("** LOMBOK REFLECTION issue: " + msg);
        if (initError != null) {
            System.err.println("*** ADDITIONALLY, exception occurred setting up reflection: ");
            initError.printStackTrace(System.err);
        }
    }

    public static RuntimeException sneakyThrow(Throwable t) {
        if (t == null) {
            throw new NullPointerException("t");
        }
        return (RuntimeException)Permit.sneakyThrow0(t);
    }

    private static <T extends Throwable> T sneakyThrow0(Throwable t) throws T {
        throw t;
    }

    static class Fake {
        boolean override;
        Object accessCheckCache;

        Fake() {
        }
    }
}
