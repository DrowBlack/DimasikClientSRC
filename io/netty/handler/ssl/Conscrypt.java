package io.netty.handler.ssl;

import io.netty.handler.ssl.ConscryptAlpnSslEngine;
import io.netty.util.internal.PlatformDependent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.net.ssl.SSLEngine;

final class Conscrypt {
    private static final Method IS_CONSCRYPT_SSLENGINE = Conscrypt.loadIsConscryptEngine();

    private static Method loadIsConscryptEngine() {
        try {
            Class<?> conscryptClass = Class.forName("org.conscrypt.Conscrypt", true, ConscryptAlpnSslEngine.class.getClassLoader());
            return conscryptClass.getMethod("isConscrypt", SSLEngine.class);
        }
        catch (Throwable ignore) {
            return null;
        }
    }

    static boolean isAvailable() {
        return IS_CONSCRYPT_SSLENGINE != null && PlatformDependent.javaVersion() >= 8;
    }

    static boolean isEngineSupported(SSLEngine engine) {
        return Conscrypt.isAvailable() && Conscrypt.isConscryptEngine(engine);
    }

    private static boolean isConscryptEngine(SSLEngine engine) {
        try {
            return (Boolean)IS_CONSCRYPT_SSLENGINE.invoke(null, engine);
        }
        catch (IllegalAccessException ignore) {
            return false;
        }
        catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Conscrypt() {
    }
}
