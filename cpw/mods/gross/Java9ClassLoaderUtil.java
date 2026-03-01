package cpw.mods.gross;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import sun.misc.Unsafe;

public class Java9ClassLoaderUtil {
    public static URL[] getSystemClassPathURLs() {
        ClassLoader classLoader = Java9ClassLoaderUtil.class.getClassLoader();
        if (classLoader instanceof URLClassLoader) {
            return ((URLClassLoader)classLoader).getURLs();
        }
        if (classLoader.getClass().getName().startsWith("jdk.internal.loader.ClassLoaders$")) {
            try {
                Field field = Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                Unsafe unsafe = (Unsafe)field.get(null);
                Field ucpField = null;
                try {
                    ucpField = classLoader.getClass().getDeclaredField("ucp");
                }
                catch (NoSuchFieldException | SecurityException e) {
                    ucpField = classLoader.getClass().getSuperclass().getDeclaredField("ucp");
                }
                long ucpFieldOffset = unsafe.objectFieldOffset(ucpField);
                Object ucpObject = unsafe.getObject(classLoader, ucpFieldOffset);
                Field pathField = ucpField.getType().getDeclaredField("path");
                long pathFieldOffset = unsafe.objectFieldOffset(pathField);
                ArrayList path = (ArrayList)unsafe.getObject(ucpObject, pathFieldOffset);
                return path.toArray(new URL[0]);
            }
            catch (Throwable e) {
                throw new RuntimeException("Failed to find system class path URLs. Incompatible JDK?", e);
            }
        }
        return null;
    }
}
