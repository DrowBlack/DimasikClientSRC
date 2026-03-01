package lombok.javac;

import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import lombok.core.AlreadyHandledAnnotations;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.core.SpiLoadUtil;
import lombok.core.TypeLibrary;
import lombok.core.TypeResolver;
import lombok.core.configuration.ConfigurationKeysLoader;
import lombok.javac.JavacAST;
import lombok.javac.JavacASTVisitor;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacAugments;
import lombok.javac.JavacNode;
import lombok.javac.ResolutionResetNeeded;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandlerLibrary {
    private final TypeLibrary typeLibrary = new TypeLibrary();
    private final Map<String, List<AnnotationHandlerContainer<?>>> annotationHandlers = new HashMap();
    private final Collection<VisitorContainer> visitorHandlers = new ArrayList<VisitorContainer>();
    private final Messager messager;
    private SortedSet<Long> priorities;
    private SortedSet<Long> prioritiesRequiringResolutionReset;

    public HandlerLibrary(Messager messager) {
        ConfigurationKeysLoader.LoaderLoader.loadAllConfigurationKeys();
        this.messager = messager;
    }

    public SortedSet<Long> getPriorities() {
        return this.priorities;
    }

    public SortedSet<Long> getPrioritiesRequiringResolutionReset() {
        return this.prioritiesRequiringResolutionReset;
    }

    private void calculatePriorities() {
        TreeSet<Long> set = new TreeSet<Long>();
        TreeSet<Long> resetNeeded = new TreeSet<Long>();
        for (List<AnnotationHandlerContainer<?>> containers : this.annotationHandlers.values()) {
            for (AnnotationHandlerContainer<?> container : containers) {
                set.add(container.getPriority());
                if (!container.isResolutionResetNeeded()) continue;
                resetNeeded.add(container.getPriority());
            }
        }
        for (VisitorContainer container : this.visitorHandlers) {
            set.add(container.getPriority());
            if (!container.isResolutionResetNeeded()) continue;
            resetNeeded.add(container.getPriority());
        }
        this.priorities = Collections.unmodifiableSortedSet(set);
        this.prioritiesRequiringResolutionReset = Collections.unmodifiableSortedSet(resetNeeded);
    }

    public static HandlerLibrary load(Messager messager, Trees trees) {
        HandlerLibrary library = new HandlerLibrary(messager);
        try {
            HandlerLibrary.loadAnnotationHandlers(library, trees);
            HandlerLibrary.loadVisitorHandlers(library, trees);
        }
        catch (IOException e) {
            System.err.println("Lombok isn't running due to misconfigured SPI files: " + e);
        }
        library.calculatePriorities();
        return library;
    }

    private static void loadAnnotationHandlers(HandlerLibrary lib, Trees trees) throws IOException {
        for (JavacAnnotationHandler handler : SpiLoadUtil.findServices(JavacAnnotationHandler.class, JavacAnnotationHandler.class.getClassLoader())) {
            handler.setTrees(trees);
            Class annotationClass = handler.getAnnotationHandledByThisHandler();
            AnnotationHandlerContainer container = new AnnotationHandlerContainer(handler, annotationClass);
            String annotationClassName = container.annotationClass.getName().replace("$", ".");
            List<AnnotationHandlerContainer<?>> list = lib.annotationHandlers.get(annotationClassName);
            if (list == null) {
                list = new ArrayList(1);
                lib.annotationHandlers.put(annotationClassName, list);
            }
            list.add(container);
            lib.typeLibrary.addType(container.annotationClass.getName());
        }
    }

    private static void loadVisitorHandlers(HandlerLibrary lib, Trees trees) throws IOException {
        for (JavacASTVisitor visitor : SpiLoadUtil.findServices(JavacASTVisitor.class, JavacASTVisitor.class.getClassLoader())) {
            visitor.setTrees(trees);
            lib.visitorHandlers.add(new VisitorContainer(visitor));
        }
    }

    public void javacWarning(String message) {
        this.javacWarning(message, null);
    }

    public void javacWarning(String message, Throwable t) {
        this.messager.printMessage(Diagnostic.Kind.WARNING, String.valueOf(message) + (t == null ? "" : ": " + t));
    }

    public void javacError(String message) {
        this.javacError(message, null);
    }

    public void javacError(String message, Throwable t) {
        this.messager.printMessage(Diagnostic.Kind.ERROR, String.valueOf(message) + (t == null ? "" : ": " + t));
        if (t != null) {
            t.printStackTrace();
        }
    }

    private boolean checkAndSetHandled(JCTree node) {
        return JavacAugments.JCTree_handled.getAndSet(node, true) == false;
    }

    public void handleAnnotation(JCTree.JCCompilationUnit unit, JavacNode node, JCTree.JCAnnotation annotation, long priority) {
        String rawType;
        TypeResolver resolver = new TypeResolver(node.getImportList());
        String fqn = resolver.typeRefToFullyQualifiedName(node, this.typeLibrary, rawType = annotation.annotationType.toString());
        if (fqn == null) {
            return;
        }
        List<AnnotationHandlerContainer<?>> containers = this.annotationHandlers.get(fqn);
        if (containers == null) {
            return;
        }
        for (AnnotationHandlerContainer<?> container : containers) {
            try {
                if (container.getPriority() != priority) continue;
                if (this.checkAndSetHandled(annotation)) {
                    container.handle(node);
                    continue;
                }
                if (!container.isEvenIfAlreadyHandled()) continue;
                container.handle(node);
            }
            catch (AnnotationValues.AnnotationValueDecodeFail fail) {
                fail.owner.setError(fail.getMessage(), fail.idx);
            }
            catch (Throwable t) {
                String sourceName = "(unknown).java";
                if (unit != null && unit.sourcefile != null) {
                    sourceName = unit.sourcefile.getName();
                }
                this.javacError(String.format("Lombok annotation handler %s failed on " + sourceName, ((AnnotationHandlerContainer)container).handler.getClass()), t);
            }
        }
    }

    public void callASTVisitors(JavacAST ast, long priority) {
        for (VisitorContainer container : this.visitorHandlers) {
            try {
                if (container.getPriority() != priority) continue;
                ast.traverse(container.visitor);
            }
            catch (Throwable t) {
                this.javacError(String.format("Lombok visitor handler %s failed", container.visitor.getClass()), t);
            }
        }
    }

    private static class AnnotationHandlerContainer<T extends Annotation> {
        private final JavacAnnotationHandler<T> handler;
        private final Class<T> annotationClass;
        private final long priority;
        private final boolean resolutionResetNeeded;
        private final boolean evenIfAlreadyHandled;

        AnnotationHandlerContainer(JavacAnnotationHandler<T> handler, Class<T> annotationClass) {
            this.handler = handler;
            this.annotationClass = annotationClass;
            HandlerPriority hp = handler.getClass().getAnnotation(HandlerPriority.class);
            this.priority = hp == null ? 0L : ((long)hp.value() << 32) + (long)hp.subValue();
            this.resolutionResetNeeded = handler.getClass().isAnnotationPresent(ResolutionResetNeeded.class);
            this.evenIfAlreadyHandled = handler.getClass().isAnnotationPresent(AlreadyHandledAnnotations.class);
        }

        public void handle(JavacNode node) {
            this.handler.handle(JavacHandlerUtil.createAnnotation(this.annotationClass, node), (JCTree.JCAnnotation)node.get(), node);
        }

        public long getPriority() {
            return this.priority;
        }

        public boolean isResolutionResetNeeded() {
            return this.resolutionResetNeeded;
        }

        public boolean isEvenIfAlreadyHandled() {
            return this.evenIfAlreadyHandled;
        }
    }

    private static class VisitorContainer {
        private final JavacASTVisitor visitor;
        private final long priority;
        private final boolean resolutionResetNeeded;

        VisitorContainer(JavacASTVisitor visitor) {
            this.visitor = visitor;
            HandlerPriority hp = visitor.getClass().getAnnotation(HandlerPriority.class);
            this.priority = hp == null ? 0L : ((long)hp.value() << 32) + (long)hp.subValue();
            this.resolutionResetNeeded = visitor.getClass().isAnnotationPresent(ResolutionResetNeeded.class);
        }

        public long getPriority() {
            return this.priority;
        }

        public boolean isResolutionResetNeeded() {
            return this.resolutionResetNeeded;
        }
    }
}
