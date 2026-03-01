package lombok.eclipse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.Lombok;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.core.SpiLoadUtil;
import lombok.core.TypeLibrary;
import lombok.core.TypeResolver;
import lombok.core.configuration.ConfigurationKeysLoader;
import lombok.eclipse.DeferUntilPostDiet;
import lombok.eclipse.EcjAugments;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseAST;
import lombok.eclipse.EclipseASTVisitor;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

public class HandlerLibrary {
    private TypeLibrary typeLibrary = new TypeLibrary();
    private Map<String, AnnotationHandlerContainer<?>> annotationHandlers = new HashMap();
    private Collection<VisitorContainer> visitorHandlers = new ArrayList<VisitorContainer>();
    private SortedSet<Long> priorities;

    public HandlerLibrary() {
        ConfigurationKeysLoader.LoaderLoader.loadAllConfigurationKeys();
    }

    public static HandlerLibrary load() {
        HandlerLibrary lib = new HandlerLibrary();
        HandlerLibrary.loadAnnotationHandlers(lib);
        HandlerLibrary.loadVisitorHandlers(lib);
        lib.calculatePriorities();
        return lib;
    }

    public SortedSet<Long> getPriorities() {
        return this.priorities;
    }

    private void calculatePriorities() {
        TreeSet<Long> set = new TreeSet<Long>();
        for (AnnotationHandlerContainer<?> annotationHandlerContainer : this.annotationHandlers.values()) {
            set.add(annotationHandlerContainer.getPriority());
        }
        for (VisitorContainer visitorContainer : this.visitorHandlers) {
            set.add(visitorContainer.getPriority());
        }
        this.priorities = Collections.unmodifiableSortedSet(set);
    }

    private static void loadAnnotationHandlers(HandlerLibrary lib) {
        try {
            for (EclipseAnnotationHandler handler : SpiLoadUtil.findServices(EclipseAnnotationHandler.class, EclipseAnnotationHandler.class.getClassLoader())) {
                try {
                    Class annotationClass = handler.getAnnotationHandledByThisHandler();
                    AnnotationHandlerContainer container = new AnnotationHandlerContainer(handler, annotationClass);
                    String annotationClassName = container.annotationClass.getName().replace("$", ".");
                    if (lib.annotationHandlers.put(annotationClassName, container) != null) {
                        EclipseHandlerUtil.error(null, "Duplicate handlers for annotation type: " + annotationClassName, null);
                    }
                    lib.typeLibrary.addType(container.annotationClass.getName());
                }
                catch (Throwable t) {
                    EclipseHandlerUtil.error(null, "Can't load Lombok annotation handler for Eclipse: ", t);
                }
            }
        }
        catch (IOException e) {
            throw Lombok.sneakyThrow(e);
        }
    }

    private static void loadVisitorHandlers(HandlerLibrary lib) {
        try {
            for (EclipseASTVisitor visitor : SpiLoadUtil.findServices(EclipseASTVisitor.class, EclipseASTVisitor.class.getClassLoader())) {
                lib.visitorHandlers.add(new VisitorContainer(visitor));
            }
        }
        catch (Throwable t) {
            throw Lombok.sneakyThrow(t);
        }
    }

    private boolean checkAndSetHandled(ASTNode node) {
        return EcjAugments.ASTNode_handled.getAndSet(node, true) == false;
    }

    private boolean needsHandling(ASTNode node) {
        return EcjAugments.ASTNode_handled.get(node) == false;
    }

    public long handleAnnotation(CompilationUnitDeclaration ast, EclipseNode annotationNode, Annotation annotation, long priority) {
        TypeResolver resolver = new TypeResolver(annotationNode.getImportList());
        TypeReference rawType = annotation.type;
        if (rawType == null) {
            return Long.MAX_VALUE;
        }
        String fqn = resolver.typeRefToFullyQualifiedName(annotationNode, this.typeLibrary, Eclipse.toQualifiedName(annotation.type.getTypeName()));
        if (fqn == null) {
            return Long.MAX_VALUE;
        }
        AnnotationHandlerContainer<?> container = this.annotationHandlers.get(fqn);
        if (container == null) {
            return Long.MAX_VALUE;
        }
        if (priority < container.getPriority()) {
            return container.getPriority();
        }
        if (priority > container.getPriority()) {
            return Long.MAX_VALUE;
        }
        if (!annotationNode.isCompleteParse() && container.deferUntilPostDiet()) {
            if (this.needsHandling((ASTNode)annotation)) {
                container.preHandle(annotation, annotationNode);
            }
            return Long.MAX_VALUE;
        }
        try {
            if (this.checkAndSetHandled((ASTNode)annotation)) {
                container.handle(annotation, annotationNode);
            }
        }
        catch (AnnotationValues.AnnotationValueDecodeFail fail) {
            fail.owner.setError(fail.getMessage(), fail.idx);
        }
        catch (Throwable t) {
            EclipseHandlerUtil.error(ast, String.format("Lombok annotation handler %s failed", ((AnnotationHandlerContainer)container).handler.getClass()), t);
        }
        return Long.MAX_VALUE;
    }

    public long callASTVisitors(EclipseAST ast, long priority, boolean isCompleteParse) {
        long nearestPriority = Long.MAX_VALUE;
        for (VisitorContainer container : this.visitorHandlers) {
            if (priority < container.getPriority()) {
                nearestPriority = Math.min(container.getPriority(), nearestPriority);
            }
            if (!isCompleteParse && container.deferUntilPostDiet() || priority != container.getPriority()) continue;
            try {
                ast.traverse(container.visitor);
            }
            catch (Throwable t) {
                EclipseHandlerUtil.error((CompilationUnitDeclaration)((EclipseNode)ast.top()).get(), String.format("Lombok visitor handler %s failed", container.visitor.getClass()), t);
            }
        }
        return nearestPriority;
    }

    private static class AnnotationHandlerContainer<T extends java.lang.annotation.Annotation> {
        private final EclipseAnnotationHandler<T> handler;
        private final Class<T> annotationClass;
        private final long priority;
        private final boolean deferUntilPostDiet;

        AnnotationHandlerContainer(EclipseAnnotationHandler<T> handler, Class<T> annotationClass) {
            this.handler = handler;
            this.annotationClass = annotationClass;
            this.deferUntilPostDiet = handler.getClass().isAnnotationPresent(DeferUntilPostDiet.class);
            HandlerPriority hp = handler.getClass().getAnnotation(HandlerPriority.class);
            this.priority = hp == null ? 0L : ((long)hp.value() << 32) + (long)hp.subValue();
        }

        public void handle(Annotation annotation, EclipseNode annotationNode) {
            AnnotationValues<T> annValues = EclipseHandlerUtil.createAnnotation(this.annotationClass, annotationNode);
            this.handler.handle(annValues, annotation, annotationNode);
        }

        public void preHandle(Annotation annotation, EclipseNode annotationNode) {
            AnnotationValues<T> annValues = EclipseHandlerUtil.createAnnotation(this.annotationClass, annotationNode);
            this.handler.preHandle(annValues, annotation, annotationNode);
        }

        public boolean deferUntilPostDiet() {
            return this.deferUntilPostDiet;
        }

        public long getPriority() {
            return this.priority;
        }
    }

    private static class VisitorContainer {
        private final EclipseASTVisitor visitor;
        private final long priority;
        private final boolean deferUntilPostDiet;

        VisitorContainer(EclipseASTVisitor visitor) {
            this.visitor = visitor;
            this.deferUntilPostDiet = visitor.getClass().isAnnotationPresent(DeferUntilPostDiet.class);
            HandlerPriority hp = visitor.getClass().getAnnotation(HandlerPriority.class);
            this.priority = hp == null ? 0L : ((long)hp.value() << 32) + (long)hp.subValue();
        }

        public boolean deferUntilPostDiet() {
            return this.deferUntilPostDiet;
        }

        public long getPriority() {
            return this.priority;
        }
    }
}
