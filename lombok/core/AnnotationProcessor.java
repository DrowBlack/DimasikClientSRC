package lombok.core;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import lombok.core.Augments;
import lombok.patcher.ClassRootFinder;
import lombok.permit.Permit;

@SupportedAnnotationTypes(value={"*"})
public class AnnotationProcessor
extends AbstractProcessor {
    private final List<ProcessorDescriptor> registered = Arrays.asList(new JavacDescriptor(), new EcjDescriptor());
    private final List<ProcessorDescriptor> active = new ArrayList<ProcessorDescriptor>();
    private final List<String> delayedWarnings = new ArrayList<String>();

    private static String trace(Throwable t) {
        StringWriter w = new StringWriter();
        t.printStackTrace(new PrintWriter((Writer)w, true));
        return w.toString();
    }

    public static ProcessingEnvironment getJavacProcessingEnvironment(ProcessingEnvironment procEnv, List<String> delayedWarnings) {
        return AnnotationProcessor.tryRecursivelyObtainJavacProcessingEnvironment(procEnv);
    }

    private static ProcessingEnvironment tryRecursivelyObtainJavacProcessingEnvironment(ProcessingEnvironment procEnv) {
        if (procEnv.getClass().getName().equals("com.sun.tools.javac.processing.JavacProcessingEnvironment")) {
            return procEnv;
        }
        Class<?> procEnvClass = procEnv.getClass();
        while (procEnvClass != null) {
            try {
                Object delegate = AnnotationProcessor.tryGetDelegateField(procEnvClass, procEnv);
                if (delegate == null) {
                    delegate = AnnotationProcessor.tryGetProcessingEnvField(procEnvClass, procEnv);
                }
                if (delegate == null) {
                    delegate = AnnotationProcessor.tryGetProxyDelegateToField(procEnvClass, procEnv);
                }
                if (delegate != null) {
                    return AnnotationProcessor.tryRecursivelyObtainJavacProcessingEnvironment((ProcessingEnvironment)delegate);
                }
            }
            catch (Exception exception) {}
            procEnvClass = procEnvClass.getSuperclass();
        }
        return null;
    }

    private static Object tryGetDelegateField(Class<?> delegateClass, Object instance) {
        try {
            return Permit.getField(delegateClass, "delegate").get(instance);
        }
        catch (Exception exception) {
            return null;
        }
    }

    private static Object tryGetProcessingEnvField(Class<?> delegateClass, Object instance) {
        try {
            return Permit.getField(delegateClass, "processingEnv").get(instance);
        }
        catch (Exception exception) {
            return null;
        }
    }

    private static Object tryGetProxyDelegateToField(Class<?> delegateClass, Object instance) {
        try {
            InvocationHandler handler = Proxy.getInvocationHandler(instance);
            return Permit.getField(handler.getClass(), "val$delegateTo").get(handler);
        }
        catch (Exception exception) {
            return null;
        }
    }

    @Override
    public void init(ProcessingEnvironment procEnv) {
        super.init(procEnv);
        for (ProcessorDescriptor proc : this.registered) {
            if (!proc.want(procEnv, this.delayedWarnings)) continue;
            this.active.add(proc);
        }
        if (this.active.isEmpty() && this.delayedWarnings.isEmpty()) {
            StringBuilder supported = new StringBuilder();
            for (ProcessorDescriptor proc : this.registered) {
                if (supported.length() > 0) {
                    supported.append(", ");
                }
                supported.append(proc.getName());
            }
            if (procEnv.getClass().getName().equals("com.google.turbine.processing.TurbineProcessingEnvironment")) {
                procEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("Turbine is not currently supported by lombok.", new Object[0]));
            } else {
                procEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, String.format("You aren't using a compiler supported by lombok, so lombok will not work and has been disabled.\nYour processor is: %s\nLombok supports: %s", procEnv.getClass().getName(), supported));
            }
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> rootElements;
        if (!this.delayedWarnings.isEmpty() && !(rootElements = roundEnv.getRootElements()).isEmpty()) {
            Element firstRoot = rootElements.iterator().next();
            for (String string : this.delayedWarnings) {
                this.processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, string, firstRoot);
            }
            this.delayedWarnings.clear();
        }
        for (ProcessorDescriptor proc : this.active) {
            proc.process(annotations, roundEnv);
        }
        boolean onlyLombok = true;
        boolean zeroElems = true;
        for (TypeElement typeElement : annotations) {
            zeroElems = false;
            Name n = typeElement.getQualifiedName();
            if (n.toString().startsWith("lombok.")) continue;
            onlyLombok = false;
        }
        return onlyLombok && !zeroElems;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    static class EcjDescriptor
    extends ProcessorDescriptor {
        EcjDescriptor() {
        }

        @Override
        String getName() {
            return "ECJ";
        }

        @Override
        boolean want(ProcessingEnvironment procEnv, List<String> delayedWarnings) {
            return procEnv.getClass().getName().startsWith("org.eclipse.jdt.");
        }

        @Override
        boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            return false;
        }
    }

    static class JavacDescriptor
    extends ProcessorDescriptor {
        private Processor processor;

        JavacDescriptor() {
        }

        @Override
        String getName() {
            return "OpenJDK javac";
        }

        @Override
        boolean want(ProcessingEnvironment procEnv, List<String> delayedWarnings) {
            if (procEnv.getClass().getName().startsWith("org.eclipse.jdt.")) {
                return false;
            }
            ProcessingEnvironment javacProcEnv = AnnotationProcessor.getJavacProcessingEnvironment(procEnv, delayedWarnings);
            if (javacProcEnv == null) {
                return false;
            }
            try {
                ClassLoader classLoader = this.findAndPatchClassLoader(javacProcEnv);
                this.processor = (Processor)Class.forName("lombok.javac.apt.LombokProcessor", false, classLoader).getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (Exception e) {
                delayedWarnings.add("You found a bug in lombok; lombok.javac.apt.LombokProcessor is not available. Lombok will not run during this compilation: " + AnnotationProcessor.trace(e));
                return false;
            }
            catch (NoClassDefFoundError e) {
                delayedWarnings.add("Can't load javac processor due to (most likely) a class loader problem: " + AnnotationProcessor.trace(e));
                return false;
            }
            try {
                this.processor.init(procEnv);
            }
            catch (Exception e) {
                delayedWarnings.add("lombok.javac.apt.LombokProcessor could not be initialized. Lombok will not run during this compilation: " + AnnotationProcessor.trace(e));
                return false;
            }
            catch (NoClassDefFoundError e) {
                delayedWarnings.add("Can't initialize javac processor due to (most likely) a class loader problem: " + AnnotationProcessor.trace(e));
                return false;
            }
            return true;
        }

        private ClassLoader findAndPatchClassLoader(ProcessingEnvironment procEnv) throws Exception {
            ClassLoader ourClassLoader;
            ClassLoader environmentClassLoader = procEnv.getClass().getClassLoader();
            if (environmentClassLoader != null && environmentClassLoader.getClass().getCanonicalName().equals("org.codehaus.plexus.compiler.javac.IsolatedClassLoader") && !Augments.ClassLoader_lombokAlreadyAddedTo.getAndSet(environmentClassLoader, true).booleanValue()) {
                Method m = Permit.getMethod(environmentClassLoader.getClass(), "addURL", URL.class);
                URL selfUrl = new File(ClassRootFinder.findClassRootOfClass(AnnotationProcessor.class)).toURI().toURL();
                Permit.invoke(m, environmentClassLoader, selfUrl);
            }
            if ((ourClassLoader = JavacDescriptor.class.getClassLoader()) == null) {
                return ClassLoader.getSystemClassLoader();
            }
            return ourClassLoader;
        }

        @Override
        boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            return this.processor.process(annotations, roundEnv);
        }
    }

    static abstract class ProcessorDescriptor {
        ProcessorDescriptor() {
        }

        abstract boolean want(ProcessingEnvironment var1, List<String> var2);

        abstract String getName();

        abstract boolean process(Set<? extends TypeElement> var1, RoundEnvironment var2);
    }
}
