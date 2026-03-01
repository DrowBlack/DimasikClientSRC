package lombok.javac.apt;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import lombok.core.DiagnosticsReceiver;
import lombok.javac.apt.InterceptingJavaFileObject;
import lombok.javac.apt.Javac6BaseFileObjectWrapper;
import lombok.javac.apt.Javac7BaseFileObjectWrapper;
import lombok.javac.apt.LombokFileObject;
import lombok.permit.Permit;

final class LombokFileObjects {
    private static final List<String> KNOWN_JAVA9_FILE_MANAGERS = Arrays.asList("com.google.errorprone.MaskedClassLoader$MaskedFileManager", "com.google.devtools.build.buildjar.javac.BlazeJavacMain$ClassloaderMaskingFileManager", "com.google.devtools.build.java.turbine.javac.JavacTurbineCompiler$ClassloaderMaskingFileManager", "org.netbeans.modules.java.source.parsing.ProxyFileManager", "com.sun.tools.javac.api.ClientCodeWrapper$WrappedStandardJavaFileManager", "com.sun.tools.javac.main.DelegatingJavaFileManager$DelegatingSJFM");
    private static Constructor<?> j9CompilerConstructor = null;

    static Method getDecoderMethod(String className) {
        try {
            return Permit.getMethod(Class.forName(className), "getDecoder", Boolean.TYPE);
        }
        catch (NoSuchMethodException noSuchMethodException) {
        }
        catch (ClassNotFoundException classNotFoundException) {}
        return null;
    }

    private LombokFileObjects() {
    }

    /*
     * Unable to fully structure code
     */
    static Compiler getCompiler(JavaFileManager jfm) {
        block19: {
            v0 = jfmClassName = jfm != null ? jfm.getClass().getName() : "null";
            if (jfmClassName.equals("com.sun.tools.javac.util.DefaultFileManager")) {
                return Compiler.JAVAC6;
            }
            if (jfmClassName.equals("com.sun.tools.javac.util.JavacFileManager")) {
                return Compiler.JAVAC6;
            }
            if (jfmClassName.equals("com.sun.tools.javac.file.JavacFileManager")) {
                try {
                    superType = Class.forName("com.sun.tools.javac.file.BaseFileManager");
                    if (superType.isInstance(jfm)) {
                        return LombokFileObjects.java9Compiler(jfm);
                    }
                }
                catch (Throwable v1) {}
                return Compiler.JAVAC7;
            }
            if (LombokFileObjects.KNOWN_JAVA9_FILE_MANAGERS.contains(jfmClassName)) {
                try {
                    return LombokFileObjects.java9Compiler(jfm);
                }
                catch (Throwable v2) {}
            }
            try {
                if (Class.forName("com.sun.tools.javac.file.PathFileObject") == null) {
                    throw new NullPointerException();
                }
                return LombokFileObjects.java9Compiler(jfm);
            }
            catch (Throwable v3) {
                try {
                    if (Class.forName("com.sun.tools.javac.file.BaseFileObject") == null) {
                        throw new NullPointerException();
                    }
                    return Compiler.JAVAC7;
                }
                catch (Throwable v4) {
                    try {
                        if (Class.forName("com.sun.tools.javac.util.BaseFileObject") == null) {
                            throw new NullPointerException();
                        }
                        return Compiler.JAVAC6;
                    }
                    catch (Throwable v5) {
                        sb = new StringBuilder(jfmClassName);
                        if (jfm == null) break block19;
                        sb.append(" extends ").append(jfm.getClass().getSuperclass().getName());
                        var6_4 = jfm.getClass().getInterfaces();
                        var5_5 = var6_4.length;
                        var4_6 = 0;
                        ** while (var4_6 < var5_5)
                    }
                }
            }
lbl-1000:
            // 1 sources

            {
                cls = var6_4[var4_6];
                sb.append(" implements ").append(cls.getName());
                ++var4_6;
                continue;
            }
        }
        throw new IllegalArgumentException(sb.toString());
    }

    static JavaFileObject createIntercepting(Compiler compiler, JavaFileObject delegate, String fileName, DiagnosticsReceiver diagnostics) {
        return compiler.wrap(new InterceptingJavaFileObject(delegate, fileName, diagnostics, compiler.getDecoderMethod()));
    }

    private static Compiler java9Compiler(JavaFileManager jfm) {
        try {
            if (j9CompilerConstructor == null) {
                j9CompilerConstructor = Class.forName("lombok.javac.apt.Java9Compiler").getConstructor(JavaFileManager.class);
            }
            return (Compiler)j9CompilerConstructor.newInstance(jfm);
        }
        catch (ClassNotFoundException classNotFoundException) {
            return null;
        }
        catch (NoSuchMethodException noSuchMethodException) {
            return null;
        }
        catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            throw new RuntimeException(t);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    static interface Compiler {
        public static final Compiler JAVAC6 = new Compiler(){
            private Method decoderMethod = null;
            private final AtomicBoolean decoderIsSet = new AtomicBoolean();

            @Override
            public JavaFileObject wrap(LombokFileObject fileObject) {
                return new Javac6BaseFileObjectWrapper(fileObject);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public Method getDecoderMethod() {
                AtomicBoolean atomicBoolean = this.decoderIsSet;
                synchronized (atomicBoolean) {
                    if (this.decoderIsSet.get()) {
                        return this.decoderMethod;
                    }
                    this.decoderMethod = LombokFileObjects.getDecoderMethod("com.sun.tools.javac.util.BaseFileObject");
                    this.decoderIsSet.set(true);
                    return this.decoderMethod;
                }
            }
        };
        public static final Compiler JAVAC7 = new Compiler(){
            private Method decoderMethod = null;
            private final AtomicBoolean decoderIsSet = new AtomicBoolean();

            @Override
            public JavaFileObject wrap(LombokFileObject fileObject) {
                return new Javac7BaseFileObjectWrapper(fileObject);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public Method getDecoderMethod() {
                AtomicBoolean atomicBoolean = this.decoderIsSet;
                synchronized (atomicBoolean) {
                    if (this.decoderIsSet.get()) {
                        return this.decoderMethod;
                    }
                    this.decoderMethod = LombokFileObjects.getDecoderMethod("com.sun.tools.javac.file.BaseFileObject");
                    this.decoderIsSet.set(true);
                    return this.decoderMethod;
                }
            }
        };

        public JavaFileObject wrap(LombokFileObject var1);

        public Method getDecoderMethod();
    }
}
