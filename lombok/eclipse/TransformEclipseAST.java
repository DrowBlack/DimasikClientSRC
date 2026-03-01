package lombok.eclipse;

import java.lang.reflect.Field;
import lombok.ConfigurationKeys;
import lombok.core.LombokConfiguration;
import lombok.core.debug.DebugSnapshotStore;
import lombok.core.debug.HistogramTracker;
import lombok.eclipse.EcjAugments;
import lombok.eclipse.EclipseAST;
import lombok.eclipse.EclipseASTAdapter;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.HandlerLibrary;
import lombok.eclipse.TransformationState;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.patcher.Symbols;
import lombok.permit.Permit;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.parser.Parser;

public class TransformEclipseAST {
    private final EclipseAST ast;
    private static final Field astCacheField;
    private static final HandlerLibrary handlers;
    public static boolean disableLombok;
    private static final HistogramTracker lombokTracker;

    static {
        disableLombok = false;
        String v = System.getProperty("lombok.histogram");
        lombokTracker = v == null ? null : (v.toLowerCase().equals("sysout") ? new HistogramTracker("lombok.histogram", System.out) : new HistogramTracker("lombok.histogram"));
        Field f = null;
        HandlerLibrary h = null;
        if (System.getProperty("lombok.disable") != null) {
            disableLombok = true;
            astCacheField = null;
            handlers = null;
        } else {
            try {
                h = HandlerLibrary.load();
            }
            catch (Throwable t) {
                try {
                    EclipseHandlerUtil.error(null, "Problem initializing lombok", t);
                }
                catch (Throwable throwable) {
                    System.err.println("Problem initializing lombok");
                    t.printStackTrace();
                }
                disableLombok = true;
            }
            try {
                f = Permit.getField(CompilationUnitDeclaration.class, "$lombokAST");
            }
            catch (Throwable throwable) {}
            astCacheField = f;
            handlers = h;
        }
    }

    public static void transform_swapped(CompilationUnitDeclaration ast, Parser parser) {
        TransformEclipseAST.transform(parser, ast);
    }

    public static EclipseAST getAST(CompilationUnitDeclaration ast, boolean forceRebuild) {
        EclipseAST existing = null;
        if (astCacheField != null) {
            try {
                existing = (EclipseAST)astCacheField.get(ast);
            }
            catch (Exception exception) {}
        }
        if (existing == null) {
            existing = new EclipseAST(ast);
            if (astCacheField != null) {
                try {
                    astCacheField.set(ast, existing);
                }
                catch (Exception exception) {}
            }
        } else {
            existing.rebuild(forceRebuild);
        }
        return existing;
    }

    public static boolean alreadyTransformed(CompilationUnitDeclaration ast) {
        TransformationState state = EcjAugments.CompilationUnitDeclaration_transformationState.get(ast);
        if (state == TransformationState.FULL) {
            return true;
        }
        if (state == TransformationState.DIET) {
            if (!EclipseAST.isComplete(ast)) {
                return true;
            }
            EcjAugments.CompilationUnitDeclaration_transformationState.set(ast, TransformationState.FULL);
        } else {
            EcjAugments.CompilationUnitDeclaration_transformationState.set(ast, TransformationState.DIET);
        }
        return false;
    }

    public static void transform(Parser parser, CompilationUnitDeclaration ast) {
        if (disableLombok) {
            return;
        }
        char[] fileName = ast.getFileName();
        if (fileName != null && String.valueOf(fileName).endsWith("module-info.java")) {
            return;
        }
        if (Symbols.hasSymbol("lombok.disable")) {
            return;
        }
        if ("org.eclipse.jdt.internal.core.search.indexing.IndexingParser".equals(parser.getClass().getName())) {
            return;
        }
        if (TransformEclipseAST.alreadyTransformed(ast)) {
            return;
        }
        if (Boolean.TRUE.equals(LombokConfiguration.read(ConfigurationKeys.LOMBOK_DISABLE, EclipseAST.getAbsoluteFileLocation(ast)))) {
            return;
        }
        try {
            DebugSnapshotStore.INSTANCE.snapshot(ast, "transform entry", new Object[0]);
            long histoToken = lombokTracker == null ? 0L : lombokTracker.start();
            EclipseAST existing = TransformEclipseAST.getAST(ast, false);
            existing.setSource(parser.scanner.getSource());
            new TransformEclipseAST(existing).go();
            if (lombokTracker != null) {
                lombokTracker.end(histoToken);
            }
            DebugSnapshotStore.INSTANCE.snapshot(ast, "transform exit", new Object[0]);
        }
        catch (Throwable t) {
            DebugSnapshotStore.INSTANCE.snapshot(ast, "transform error: %s", t.getClass().getSimpleName());
            try {
                String message = "Lombok can't parse this source: " + t.toString();
                EclipseAST.addProblemToCompilationResult(ast.getFileName(), ast.compilationResult, false, message, 0, 0);
                t.printStackTrace();
            }
            catch (Throwable t2) {
                try {
                    EclipseHandlerUtil.error(ast, "Can't create an error in the problems dialog while adding: " + t.toString(), t2);
                }
                catch (Throwable throwable) {
                    disableLombok = true;
                }
            }
        }
    }

    public TransformEclipseAST(EclipseAST ast) {
        this.ast = ast;
    }

    public void go() {
        long nextPriority = Long.MIN_VALUE;
        for (Long d : handlers.getPriorities()) {
            if (nextPriority > d) continue;
            AnnotationVisitor visitor = new AnnotationVisitor(d);
            this.ast.traverse(visitor);
            nextPriority = visitor.getNextPriority();
            nextPriority = Math.min(nextPriority, handlers.callASTVisitors(this.ast, d, this.ast.isCompleteParse()));
        }
    }

    private static class AnnotationVisitor
    extends EclipseASTAdapter {
        private final long priority;
        private long nextPriority = Long.MAX_VALUE;

        public AnnotationVisitor(long priority) {
            this.priority = priority;
        }

        public long getNextPriority() {
            return this.nextPriority;
        }

        @Override
        public void visitAnnotationOnField(FieldDeclaration field, EclipseNode annotationNode, Annotation annotation) {
            CompilationUnitDeclaration top = (CompilationUnitDeclaration)((EclipseNode)annotationNode.top()).get();
            this.nextPriority = Math.min(this.nextPriority, handlers.handleAnnotation(top, annotationNode, annotation, this.priority));
        }

        @Override
        public void visitAnnotationOnMethodArgument(Argument arg, AbstractMethodDeclaration method, EclipseNode annotationNode, Annotation annotation) {
            CompilationUnitDeclaration top = (CompilationUnitDeclaration)((EclipseNode)annotationNode.top()).get();
            this.nextPriority = Math.min(this.nextPriority, handlers.handleAnnotation(top, annotationNode, annotation, this.priority));
        }

        @Override
        public void visitAnnotationOnLocal(LocalDeclaration local, EclipseNode annotationNode, Annotation annotation) {
            CompilationUnitDeclaration top = (CompilationUnitDeclaration)((EclipseNode)annotationNode.top()).get();
            this.nextPriority = Math.min(this.nextPriority, handlers.handleAnnotation(top, annotationNode, annotation, this.priority));
        }

        @Override
        public void visitAnnotationOnMethod(AbstractMethodDeclaration method, EclipseNode annotationNode, Annotation annotation) {
            CompilationUnitDeclaration top = (CompilationUnitDeclaration)((EclipseNode)annotationNode.top()).get();
            this.nextPriority = Math.min(this.nextPriority, handlers.handleAnnotation(top, annotationNode, annotation, this.priority));
        }

        @Override
        public void visitAnnotationOnType(TypeDeclaration type, EclipseNode annotationNode, Annotation annotation) {
            CompilationUnitDeclaration top = (CompilationUnitDeclaration)((EclipseNode)annotationNode.top()).get();
            this.nextPriority = Math.min(this.nextPriority, handlers.handleAnnotation(top, annotationNode, annotation, this.priority));
        }

        @Override
        public void visitAnnotationOnTypeUse(TypeReference typeUse, EclipseNode annotationNode, Annotation annotation) {
            CompilationUnitDeclaration top = (CompilationUnitDeclaration)((EclipseNode)annotationNode.top()).get();
            this.nextPriority = Math.min(this.nextPriority, handlers.handleAnnotation(top, annotationNode, annotation, this.priority));
        }
    }
}
