package lombok.javac.apt;

import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.tools.javac.jvm.ClassWriter;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.processing.JavacFiler;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileManager;
import lombok.Lombok;
import lombok.core.CleanupRegistry;
import lombok.javac.Javac;
import lombok.javac.JavacTransformer;
import lombok.javac.apt.InterceptingJavaFileManager;
import lombok.javac.apt.MessagerDiagnosticsReceiver;
import lombok.javac.handlers.JavacHandlerUtil;
import lombok.permit.Permit;
import lombok.permit.dummy.Parent;
import sun.misc.Unsafe;

@SupportedAnnotationTypes(value={"*"})
public class LombokProcessor
extends AbstractProcessor {
    private ProcessingEnvironment processingEnv;
    private JavacProcessingEnvironment javacProcessingEnv;
    private JavacFiler javacFiler;
    private JavacTransformer transformer;
    private Trees trees;
    private boolean lombokDisabled = false;
    private static final String JPE = "com.sun.tools.javac.processing.JavacProcessingEnvironment";
    private static final Field javacProcessingEnvironment_discoveredProcs = LombokProcessor.getFieldAccessor("com.sun.tools.javac.processing.JavacProcessingEnvironment", "discoveredProcs");
    private static final Field discoveredProcessors_procStateList = LombokProcessor.getFieldAccessor("com.sun.tools.javac.processing.JavacProcessingEnvironment$DiscoveredProcessors", "procStateList");
    private static final Field processorState_processor = LombokProcessor.getFieldAccessor("com.sun.tools.javac.processing.JavacProcessingEnvironment$processor", "processor");
    private final IdentityHashMap<JCTree.JCCompilationUnit, Long> roots = new IdentityHashMap();
    private long[] priorityLevels;
    private Set<Long> priorityLevelsRequiringResolutionReset;
    private CleanupRegistry cleanup = new CleanupRegistry();
    private int dummyCount = 0;

    @Override
    public void init(ProcessingEnvironment procEnv) {
        super.init(procEnv);
        if (System.getProperty("lombok.disable") != null) {
            this.lombokDisabled = true;
            return;
        }
        this.processingEnv = procEnv;
        this.javacProcessingEnv = this.getJavacProcessingEnvironment(procEnv);
        this.javacFiler = this.getJavacFiler(procEnv.getFiler());
        this.placePostCompileAndDontMakeForceRoundDummiesHook();
        this.trees = Trees.instance(this.javacProcessingEnv);
        this.transformer = new JavacTransformer(procEnv.getMessager(), this.trees);
        SortedSet<Long> p = this.transformer.getPriorities();
        if (p.isEmpty()) {
            this.priorityLevels = new long[1];
            this.priorityLevelsRequiringResolutionReset = new HashSet<Long>();
        } else {
            this.priorityLevels = new long[p.size()];
            int i = 0;
            for (Long prio : p) {
                this.priorityLevels[i++] = prio;
            }
            this.priorityLevelsRequiringResolutionReset = this.transformer.getPrioritiesRequiringResolutionReset();
        }
    }

    private static final Field getFieldAccessor(String typeName, String fieldName) {
        try {
            return Permit.getField(Class.forName(typeName), fieldName);
        }
        catch (ClassNotFoundException classNotFoundException) {
            return null;
        }
        catch (NoSuchFieldException noSuchFieldException) {
            return null;
        }
    }

    private String listAnnotationProcessorsBeforeOurs() {
        ArrayList states;
        block6: {
            Object discoveredProcessors = javacProcessingEnvironment_discoveredProcs.get(this.javacProcessingEnv);
            states = (ArrayList)discoveredProcessors_procStateList.get(discoveredProcessors);
            if (states != null && !states.isEmpty()) break block6;
            return null;
        }
        try {
            if (states.size() == 1) {
                return processorState_processor.get(states.get(0)).getClass().getName();
            }
            int idx = 0;
            StringBuilder out = new StringBuilder();
            for (Object processState : states) {
                ++idx;
                String name = processorState_processor.get(processState).getClass().getName();
                if (out.length() > 0) {
                    out.append(", ");
                }
                out.append("[").append(idx).append("] ").append(name);
            }
            return out.toString();
        }
        catch (Exception exception) {
            return null;
        }
    }

    private void placePostCompileAndDontMakeForceRoundDummiesHook() {
        this.stopJavacProcessingEnvironmentFromClosingOurClassloader();
        this.forceMultipleRoundsInNetBeansEditor();
        Context context = this.javacProcessingEnv.getContext();
        this.disablePartialReparseInNetBeansEditor(context);
        try {
            Method keyMethod = Permit.getMethod(Context.class, "key", Class.class);
            Object key = Permit.invoke(keyMethod, context, JavaFileManager.class);
            Field htField = Permit.getField(Context.class, "ht");
            Map ht = (Map)Permit.get(htField, context);
            JavaFileManager originalFiler = (JavaFileManager)ht.get(key);
            if (!(originalFiler instanceof InterceptingJavaFileManager)) {
                Messager messager = this.processingEnv.getMessager();
                MessagerDiagnosticsReceiver receiver = new MessagerDiagnosticsReceiver(messager);
                InterceptingJavaFileManager newFilerManager = new InterceptingJavaFileManager(originalFiler, receiver);
                ht.put(key, newFilerManager);
                Field filerFileManagerField = Permit.getField(JavacFiler.class, "fileManager");
                filerFileManagerField.set(this.javacFiler, newFilerManager);
                if (Javac.getJavaCompilerVersion() > 8 && !JavacHandlerUtil.inNetbeansCompileOnSave(context)) {
                    this.replaceFileManagerJdk9(context, newFilerManager);
                }
            }
        }
        catch (Exception e) {
            throw Lombok.sneakyThrow(e);
        }
    }

    private void replaceFileManagerJdk9(Context context, JavaFileManager newFiler) {
        try {
            JavaCompiler compiler = (JavaCompiler)Permit.invoke(Permit.getMethod(JavaCompiler.class, "instance", Context.class), null, context);
            try {
                Field fileManagerField = Permit.getField(JavaCompiler.class, "fileManager");
                Permit.set(fileManagerField, compiler, newFiler);
            }
            catch (Exception exception) {}
            try {
                Field writerField = Permit.getField(JavaCompiler.class, "writer");
                ClassWriter writer = (ClassWriter)writerField.get(compiler);
                Field fileManagerField = Permit.getField(ClassWriter.class, "fileManager");
                Permit.set(fileManagerField, writer, newFiler);
            }
            catch (Exception exception) {}
        }
        catch (Exception exception) {}
    }

    private void forceMultipleRoundsInNetBeansEditor() {
        try {
            Field f = Permit.getField(JavacProcessingEnvironment.class, "isBackgroundCompilation");
            f.set(this.javacProcessingEnv, true);
        }
        catch (NoSuchFieldException noSuchFieldException) {
        }
        catch (Throwable t) {
            throw Lombok.sneakyThrow(t);
        }
    }

    private void disablePartialReparseInNetBeansEditor(Context context) {
        try {
            Class<?> cancelServiceClass = Class.forName("com.sun.tools.javac.util.CancelService");
            Method cancelServiceInstance = Permit.getMethod(cancelServiceClass, "instance", Context.class);
            Object cancelService = Permit.invoke(cancelServiceInstance, null, context);
            if (cancelService == null) {
                return;
            }
            Field parserField = Permit.getField(cancelService.getClass(), "parser");
            Object parser = parserField.get(cancelService);
            Field supportsReparseField = Permit.getField(parser.getClass(), "supportsReparse");
            supportsReparseField.set(parser, false);
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (NoSuchFieldException noSuchFieldException) {
        }
        catch (Throwable t) {
            throw Lombok.sneakyThrow(t);
        }
    }

    private static ClassLoader wrapClassLoader(final ClassLoader parent) {
        return new ClassLoader(){

            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                return parent.loadClass(name);
            }

            public String toString() {
                return parent.toString();
            }

            @Override
            public URL getResource(String name) {
                return parent.getResource(name);
            }

            @Override
            public Enumeration<URL> getResources(String name) throws IOException {
                return parent.getResources(name);
            }

            @Override
            public InputStream getResourceAsStream(String name) {
                return parent.getResourceAsStream(name);
            }

            @Override
            public void setDefaultAssertionStatus(boolean enabled) {
                parent.setDefaultAssertionStatus(enabled);
            }

            @Override
            public void setPackageAssertionStatus(String packageName, boolean enabled) {
                parent.setPackageAssertionStatus(packageName, enabled);
            }

            @Override
            public void setClassAssertionStatus(String className, boolean enabled) {
                parent.setClassAssertionStatus(className, enabled);
            }

            @Override
            public void clearAssertionStatus() {
                parent.clearAssertionStatus();
            }
        };
    }

    private void stopJavacProcessingEnvironmentFromClosingOurClassloader() {
        try {
            Field f = Permit.getField(JavacProcessingEnvironment.class, "processorClassLoader");
            ClassLoader unwrapped = (ClassLoader)f.get(this.javacProcessingEnv);
            if (unwrapped == null) {
                return;
            }
            ClassLoader wrapped = LombokProcessor.wrapClassLoader(unwrapped);
            f.set(this.javacProcessingEnv, wrapped);
        }
        catch (NoSuchFieldException noSuchFieldException) {
        }
        catch (Throwable t) {
            throw Lombok.sneakyThrow(t);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        HashSet<Long> hashSet;
        if (this.lombokDisabled) {
            return false;
        }
        if (roundEnv.processingOver()) {
            this.cleanup.run();
            return false;
        }
        for (Element element : roundEnv.getRootElements()) {
            JCTree.JCCompilationUnit unit = this.toUnit(element);
            if (unit == null || this.roots.containsKey(unit)) continue;
            this.roots.put(unit, this.priorityLevels[0]);
        }
        do {
            long[] lArray = this.priorityLevels;
            int n = this.priorityLevels.length;
            int unit = 0;
            while (unit < n) {
                long l = lArray[unit];
                ArrayList<JCTree.JCCompilationUnit> cusForThisRound = new ArrayList<JCTree.JCCompilationUnit>();
                for (Map.Entry<JCTree.JCCompilationUnit, Long> entry : this.roots.entrySet()) {
                    Long prioOfCu = entry.getValue();
                    if (prioOfCu == null || prioOfCu != l) continue;
                    cusForThisRound.add(entry.getKey());
                }
                this.transformer.transform(l, this.javacProcessingEnv.getContext(), cusForThisRound, this.cleanup);
                ++unit;
            }
            hashSet = new HashSet<Long>();
            int i = this.priorityLevels.length - 1;
            while (i >= 0) {
                Long curLevel = this.priorityLevels[i];
                Long nextLevel = i == this.priorityLevels.length - 1 ? null : Long.valueOf(this.priorityLevels[i + 1]);
                ArrayList<JCTree.JCCompilationUnit> cusToAdvance = new ArrayList<JCTree.JCCompilationUnit>();
                for (Map.Entry<JCTree.JCCompilationUnit, Long> entry : this.roots.entrySet()) {
                    if (!curLevel.equals(entry.getValue())) continue;
                    cusToAdvance.add(entry.getKey());
                    hashSet.add(nextLevel);
                }
                for (JCTree.JCCompilationUnit unit2 : cusToAdvance) {
                    this.roots.put(unit2, nextLevel);
                }
                --i;
            }
            hashSet.remove(null);
            if (hashSet.isEmpty()) {
                return false;
            }
            hashSet.retainAll(this.priorityLevelsRequiringResolutionReset);
        } while (hashSet.isEmpty());
        this.forceNewRound(this.javacFiler);
        return false;
    }

    private void forceNewRound(JavacFiler filer) {
        if (!filer.newFiles()) {
            try {
                filer.getGeneratedSourceNames().add("lombok.dummy.ForceNewRound" + this.dummyCount++);
            }
            catch (Exception e) {
                e.printStackTrace();
                this.processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Can't force a new processing round. Lombok won't work.");
            }
        }
    }

    private JCTree.JCCompilationUnit toUnit(Element element) {
        TreePath path = null;
        if (this.trees != null) {
            try {
                path = this.trees.getPath(element);
            }
            catch (NullPointerException nullPointerException) {}
        }
        if (path == null) {
            return null;
        }
        return (JCTree.JCCompilationUnit)path.getCompilationUnit();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    public JavacProcessingEnvironment getJavacProcessingEnvironment(Object procEnv) {
        LombokProcessor.addOpensForLombok();
        if (procEnv instanceof JavacProcessingEnvironment) {
            return (JavacProcessingEnvironment)procEnv;
        }
        Class<?> procEnvClass = procEnv.getClass();
        while (procEnvClass != null) {
            Object delegate = this.tryGetDelegateField(procEnvClass, procEnv);
            if (delegate == null) {
                delegate = this.tryGetProxyDelegateToField(procEnvClass, procEnv);
            }
            if (delegate == null) {
                delegate = this.tryGetProcessingEnvField(procEnvClass, procEnv);
            }
            if (delegate != null) {
                return this.getJavacProcessingEnvironment(delegate);
            }
            procEnvClass = procEnvClass.getSuperclass();
        }
        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Can't get the delegate of the gradle IncrementalProcessingEnvironment. Lombok won't work.");
        return null;
    }

    private static Object getOwnModule() {
        try {
            Method m = Permit.getMethod(Class.class, "getModule", new Class[0]);
            return m.invoke(LombokProcessor.class, new Object[0]);
        }
        catch (Exception exception) {
            return null;
        }
    }

    private static Object getJdkCompilerModule() {
        try {
            Class<?> cModuleLayer = Class.forName("java.lang.ModuleLayer");
            Method mBoot = cModuleLayer.getDeclaredMethod("boot", new Class[0]);
            Object bootLayer = mBoot.invoke(null, new Object[0]);
            Class<?> cOptional = Class.forName("java.util.Optional");
            Method mFindModule = cModuleLayer.getDeclaredMethod("findModule", String.class);
            Object oCompilerO = mFindModule.invoke(bootLayer, "jdk.compiler");
            return cOptional.getDeclaredMethod("get", new Class[0]).invoke(oCompilerO, new Object[0]);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static void addOpensForLombok() {
        Class<?> cModule;
        try {
            cModule = Class.forName("java.lang.Module");
        }
        catch (ClassNotFoundException classNotFoundException) {
            return;
        }
        Unsafe unsafe = LombokProcessor.getUnsafe();
        Object jdkCompilerModule = LombokProcessor.getJdkCompilerModule();
        Object ownModule = LombokProcessor.getOwnModule();
        String[] allPkgs = new String[]{"com.sun.tools.javac.code", "com.sun.tools.javac.comp", "com.sun.tools.javac.file", "com.sun.tools.javac.main", "com.sun.tools.javac.model", "com.sun.tools.javac.parser", "com.sun.tools.javac.processing", "com.sun.tools.javac.tree", "com.sun.tools.javac.util", "com.sun.tools.javac.jvm"};
        try {
            Method m = cModule.getDeclaredMethod("implAddOpens", String.class, cModule);
            long firstFieldOffset = LombokProcessor.getFirstFieldOffset(unsafe);
            unsafe.putBooleanVolatile(m, firstFieldOffset, true);
            String[] stringArray = allPkgs;
            int n = allPkgs.length;
            int n2 = 0;
            while (n2 < n) {
                String p = stringArray[n2];
                m.invoke(jdkCompilerModule, p, ownModule);
                ++n2;
            }
        }
        catch (Exception exception) {}
    }

    private static long getFirstFieldOffset(Unsafe unsafe) {
        try {
            return unsafe.objectFieldOffset(Parent.class.getDeclaredField("first"));
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static Unsafe getUnsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe)theUnsafe.get(null);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public JavacFiler getJavacFiler(Object filer) {
        if (filer instanceof JavacFiler) {
            return (JavacFiler)filer;
        }
        Class<?> filerClass = filer.getClass();
        while (filerClass != null) {
            Object delegate = this.tryGetDelegateField(filerClass, filer);
            if (delegate == null) {
                delegate = this.tryGetProxyDelegateToField(filerClass, filer);
            }
            if (delegate == null) {
                delegate = this.tryGetFilerField(filerClass, filer);
            }
            if (delegate != null) {
                return this.getJavacFiler(delegate);
            }
            filerClass = filerClass.getSuperclass();
        }
        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Can't get a JavacFiler from " + filer.getClass().getName() + ". Lombok won't work.");
        return null;
    }

    private Object tryGetDelegateField(Class<?> delegateClass, Object instance) {
        try {
            return Permit.getField(delegateClass, "delegate").get(instance);
        }
        catch (Exception exception) {
            return null;
        }
    }

    private Object tryGetProcessingEnvField(Class<?> delegateClass, Object instance) {
        try {
            return Permit.getField(delegateClass, "processingEnv").get(instance);
        }
        catch (Exception exception) {
            return null;
        }
    }

    private Object tryGetFilerField(Class<?> delegateClass, Object instance) {
        try {
            return Permit.getField(delegateClass, "filer").get(instance);
        }
        catch (Exception exception) {
            return null;
        }
    }

    private Object tryGetProxyDelegateToField(Class<?> delegateClass, Object instance) {
        try {
            InvocationHandler handler = Proxy.getInvocationHandler(instance);
            return Permit.getField(handler.getClass(), "val$delegateTo").get(handler);
        }
        catch (Exception exception) {
            return null;
        }
    }
}
