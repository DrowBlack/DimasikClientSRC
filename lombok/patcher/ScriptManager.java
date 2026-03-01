package lombok.patcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import lombok.patcher.Filter;
import lombok.patcher.PatchScript;
import lombok.patcher.TransplantMapper;

public class ScriptManager {
    private final List<PatchScript> scripts = new ArrayList<PatchScript>();
    private final Map<String, List<WitnessAction>> witnessActions = new HashMap<String, List<WitnessAction>>();
    private TransplantMapper transplantMapper = TransplantMapper.IDENTITY_MAPPER;
    private Filter filter = Filter.ALWAYS;
    private static final String DEBUG_PATCHING = System.getProperty("lombok.patcher.patchDebugDir", null);
    private static final boolean LOG_TO_STANDARD_ERR = false;
    private final OurClassFileTransformer transformer = new OurClassFileTransformer();

    public void addScript(PatchScript script) {
        this.scripts.add(script);
    }

    public void addScriptIfWitness(String[] witness, PatchScript script) {
        WitnessAction wa = new WitnessAction();
        wa.ifWitnessRemove = false;
        wa.script = script;
        String[] stringArray = witness;
        int n = witness.length;
        int n2 = 0;
        while (n2 < n) {
            String w = stringArray[n2];
            List<WitnessAction> list = this.witnessActions.get(w);
            if (list == null) {
                list = new ArrayList<WitnessAction>();
                this.witnessActions.put(w, list);
            }
            list.add(wa);
            ++n2;
        }
    }

    public void addScriptIfNotWitness(String[] witness, PatchScript script) {
        WitnessAction wa = new WitnessAction();
        wa.ifWitnessRemove = true;
        wa.script = script;
        this.scripts.add(script);
        String[] stringArray = witness;
        int n = witness.length;
        int n2 = 0;
        while (n2 < n) {
            String w = stringArray[n2];
            List<WitnessAction> list = this.witnessActions.get(w);
            if (list == null) {
                list = new ArrayList<WitnessAction>();
                this.witnessActions.put(w, list);
            }
            list.add(wa);
            ++n2;
        }
    }

    public void setFilter(Filter filter) {
        this.filter = filter == null ? Filter.ALWAYS : filter;
    }

    public void registerTransformer(Instrumentation instrumentation) {
        try {
            Method m = Instrumentation.class.getMethod("addTransformer", ClassFileTransformer.class, Boolean.TYPE);
            m.invoke((Object)instrumentation, this.transformer, true);
        }
        catch (Throwable throwable) {
            instrumentation.addTransformer(this.transformer);
        }
    }

    public void reloadClasses(Instrumentation instrumentation) {
        HashSet<String> toReload = new HashSet<String>();
        for (PatchScript s : this.scripts) {
            toReload.addAll(s.getClassesToReload());
        }
        Class[] classArray = instrumentation.getAllLoadedClasses();
        int n = classArray.length;
        int n2 = 0;
        while (n2 < n) {
            Class c = classArray[n2];
            if (toReload.contains(c.getName())) {
                try {
                    Instrumentation.class.getMethod("retransformClasses", Class[].class).invoke((Object)instrumentation, new Object[]{new Class[]{c}});
                }
                catch (InvocationTargetException e) {
                    throw new UnsupportedOperationException("The " + c.getName() + " class is already loaded and cannot be modified. " + "You'll have to restart the application to patch it. Reason: " + e.getCause());
                }
                catch (Throwable throwable) {
                    throw new UnsupportedOperationException("This appears to be a JVM v1.5, which cannot reload already loaded classes. You'll have to restart the application to patch it.");
                }
            }
            ++n2;
        }
    }

    private static boolean classpathContains(String property, String path) {
        String pathCanonical = new File(path).getAbsolutePath();
        try {
            pathCanonical = new File(path).getCanonicalPath();
        }
        catch (Exception exception) {}
        String[] stringArray = System.getProperty(property, "").split(File.pathSeparator);
        int n = stringArray.length;
        int n2 = 0;
        while (n2 < n) {
            String existingPath = stringArray[n2];
            String p = new File(existingPath).getAbsolutePath();
            try {
                p = new File(existingPath).getCanonicalPath();
            }
            catch (Throwable throwable) {}
            if (p.equals(pathCanonical)) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    public void addToSystemClasspath(Instrumentation instrumentation, String pathToJar) {
        if (pathToJar == null) {
            throw new NullPointerException("pathToJar");
        }
        if (ScriptManager.classpathContains("sun.boot.class.path", pathToJar)) {
            return;
        }
        if (ScriptManager.classpathContains("java.class.path", pathToJar)) {
            return;
        }
        try {
            Method m = instrumentation.getClass().getMethod("appendToSystemClassLoaderSearch", JarFile.class);
            m.invoke((Object)instrumentation, new JarFile(pathToJar));
        }
        catch (NoSuchMethodException noSuchMethodException) {
            throw new IllegalStateException("Adding to the classloader path is not possible on a v1.5 JVM");
        }
        catch (IOException e) {
            throw new IllegalArgumentException("not found or not a jar file: " + pathToJar, e);
        }
        catch (IllegalAccessException illegalAccessException) {
            throw new IllegalStateException("appendToSystemClassLoaderSearch isn't public? This isn't a JVM...");
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            throw new IllegalArgumentException("Unknown issue: " + cause, cause);
        }
    }

    public void addToBootClasspath(Instrumentation instrumentation, String pathToJar) {
        if (pathToJar == null) {
            throw new NullPointerException("pathToJar");
        }
        if (ScriptManager.classpathContains("sun.boot.class.path", pathToJar)) {
            return;
        }
        try {
            Method m = instrumentation.getClass().getMethod("appendToBootstrapClassLoaderSearch", JarFile.class);
            m.invoke((Object)instrumentation, new JarFile(pathToJar));
        }
        catch (NoSuchMethodException noSuchMethodException) {
            throw new IllegalStateException("Adding to the classloader path is not possible on a v1.5 JVM");
        }
        catch (IOException e) {
            throw new IllegalArgumentException("not found or not a jar file: " + pathToJar, e);
        }
        catch (IllegalAccessException illegalAccessException) {
            throw new IllegalStateException("appendToSystemClassLoaderSearch isn't public? This isn't a JVM...");
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            throw new IllegalArgumentException("Unknown issue: " + cause, cause);
        }
    }

    public void setTransplantMapper(TransplantMapper transplantMapper) {
        this.transplantMapper = transplantMapper == null ? TransplantMapper.IDENTITY_MAPPER : transplantMapper;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class OurClassFileTransformer
    implements ClassFileTransformer {
        private OurClassFileTransformer() {
        }

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            if (className == null) {
                return null;
            }
            List actions = (List)ScriptManager.this.witnessActions.get(className);
            if (actions != null) {
                for (WitnessAction wa : actions) {
                    if (wa.triggered) continue;
                    wa.triggered = true;
                    if (wa.ifWitnessRemove) {
                        ScriptManager.this.scripts.remove(wa.script);
                        continue;
                    }
                    ScriptManager.this.scripts.add(wa.script);
                }
            }
            if (!ScriptManager.this.filter.shouldTransform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer)) {
                return null;
            }
            byte[] byteCode = classfileBuffer;
            boolean patched = false;
            for (PatchScript script : ScriptManager.this.scripts) {
                byte[] transformed = null;
                try {
                    transformed = script.patch(className, byteCode, ScriptManager.this.transplantMapper);
                }
                catch (Throwable t) {
                    System.err.printf("Transformer %s failed on %s. Trace:\n", script.getPatchScriptName(), className);
                    t.printStackTrace();
                    transformed = null;
                }
                if (transformed == null) continue;
                patched = true;
                byteCode = transformed;
            }
            if (patched && DEBUG_PATCHING != null) {
                try {
                    this.writeArray(DEBUG_PATCHING, String.valueOf(className) + ".class", byteCode);
                    this.writeArray(DEBUG_PATCHING, String.valueOf(className) + "_OLD.class", classfileBuffer);
                }
                catch (IOException e) {
                    System.err.println("Can't log patch result.");
                    e.printStackTrace();
                }
            }
            return (byte[])(patched ? byteCode : null);
        }

        private void writeArray(String dir, String fileName, byte[] bytes) throws IOException {
            File f = new File(dir, fileName);
            f.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bytes);
            fos.close();
        }
    }

    private static final class WitnessAction {
        boolean triggered;
        boolean ifWitnessRemove;
        PatchScript script;

        private WitnessAction() {
        }
    }
}
