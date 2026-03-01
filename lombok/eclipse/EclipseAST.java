package lombok.eclipse;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.core.AST;
import lombok.core.LombokImmutableList;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseASTVisitor;
import lombok.eclipse.EclipseAstProblemView;
import lombok.eclipse.EclipseImportList;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.permit.Permit;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;

public class EclipseAST
extends AST<EclipseAST, EclipseNode, ASTNode> {
    private static volatile boolean skipEclipseWorkspaceBasedFileResolver = false;
    private static final URI NOT_CALCULATED_MARKER = URI.create("https://projectlombok.org/not/calculated");
    private URI memoizedAbsoluteFileLocation = NOT_CALCULATED_MARKER;
    private final List<ParseProblem> queuedProblems = new ArrayList<ParseProblem>();
    private final CompilationUnitDeclaration compilationUnitDeclaration;
    private char[] source;
    private boolean completeParse;

    public EclipseAST(CompilationUnitDeclaration ast) {
        super(EclipseAST.toFileName(ast), EclipseAST.packageDeclaration(ast), new EclipseImportList(ast), EclipseAST.statementTypes());
        this.compilationUnitDeclaration = ast;
        this.setTop(this.buildCompilationUnit(ast));
        this.completeParse = EclipseAST.isComplete(ast);
        this.clearChanged();
    }

    public static URI getAbsoluteFileLocation(CompilationUnitDeclaration ast) {
        return EclipseAST.getAbsoluteFileLocation0(ast);
    }

    @Override
    public URI getAbsoluteFileLocation() {
        if (this.memoizedAbsoluteFileLocation != NOT_CALCULATED_MARKER) {
            return this.memoizedAbsoluteFileLocation;
        }
        this.memoizedAbsoluteFileLocation = EclipseAST.getAbsoluteFileLocation0(this.compilationUnitDeclaration);
        return this.memoizedAbsoluteFileLocation;
    }

    private static URI getAbsoluteFileLocation0(CompilationUnitDeclaration ast) {
        String fileName = EclipseAST.toFileName(ast);
        if (fileName != null && (fileName.startsWith("file:") || fileName.startsWith("sourcecontrol:"))) {
            return URI.create(fileName);
        }
        if (!skipEclipseWorkspaceBasedFileResolver) {
            try {
                try {
                    return EclipseWorkspaceBasedFileResolver.resolve(fileName);
                }
                catch (IllegalArgumentException e) {
                    EclipseHandlerUtil.warning("Finding 'lombok.config' file failed for '" + fileName + "'", e);
                }
            }
            catch (NoClassDefFoundError noClassDefFoundError) {
                skipEclipseWorkspaceBasedFileResolver = true;
            }
        }
        try {
            return new File(fileName).getAbsoluteFile().toURI();
        }
        catch (Exception exception) {
            return null;
        }
    }

    private static String packageDeclaration(CompilationUnitDeclaration cud) {
        ImportReference pkg = cud.currentPackage;
        return pkg == null ? null : Eclipse.toQualifiedName(pkg.getImportName());
    }

    @Override
    public int getSourceVersion() {
        long sl = this.compilationUnitDeclaration.problemReporter.options.sourceLevel;
        long cl = this.compilationUnitDeclaration.problemReporter.options.complianceLevel;
        cl >>= 16;
        if ((sl >>= 16) == 0L) {
            sl = cl;
        }
        if (cl == 0L) {
            cl = sl;
        }
        return Math.min((int)(sl - 44L), (int)(cl - 44L));
    }

    @Override
    public int getLatestJavaSpecSupported() {
        return Eclipse.getEcjCompilerVersion();
    }

    public void traverse(EclipseASTVisitor visitor) {
        ((EclipseNode)this.top()).traverse(visitor);
    }

    void traverseChildren(EclipseASTVisitor visitor, EclipseNode node) {
        LombokImmutableList children = node.down();
        int len = children.size();
        int i = 0;
        while (i < len) {
            ((EclipseNode)children.get(i)).traverse(visitor);
            ++i;
        }
    }

    public void setSource(char[] source) {
        this.source = source;
    }

    public char[] getSource() {
        return this.source;
    }

    public boolean isCompleteParse() {
        return this.completeParse;
    }

    private void propagateProblems() {
        if (this.queuedProblems.isEmpty()) {
            return;
        }
        CompilationUnitDeclaration cud = (CompilationUnitDeclaration)((EclipseNode)this.top()).get();
        if (cud.compilationResult == null) {
            return;
        }
        for (ParseProblem problem : this.queuedProblems) {
            problem.addToCompilationResult();
        }
        this.queuedProblems.clear();
    }

    void addProblem(ParseProblem problem) {
        this.queuedProblems.add(problem);
        this.propagateProblems();
    }

    public static void addProblemToCompilationResult(char[] fileNameArray, CompilationResult result, boolean isWarning, String message, int sourceStart, int sourceEnd) {
        Permit.invokeSneaky(EcjReflectionCheck.problemAddProblemToCompilationResult, EcjReflectionCheck.addProblemToCompilationResult, null, fileNameArray, result, isWarning, message, sourceStart, sourceEnd);
    }

    public static Annotation[] getTopLevelTypeReferenceAnnotations(TypeReference tr) {
        Annotation[][] annss;
        block9: {
            Field f;
            block8: {
                Method m = EcjReflectionCheck.typeReferenceGetAnnotationsOnDimensions;
                if (m == null) {
                    return null;
                }
                annss = null;
                try {
                    annss = (Annotation[][])Permit.invoke(m, tr, new Object[0]);
                    if (annss != null) {
                        return annss[0];
                    }
                }
                catch (Throwable throwable) {}
                try {
                    f = EcjReflectionCheck.typeReferenceAnnotations;
                    if (f != null) break block8;
                    return null;
                }
                catch (Throwable throwable) {
                    return null;
                }
            }
            annss = (Annotation[][])Permit.get(f, tr);
            if (annss != null) break block9;
            return null;
        }
        return annss[annss.length - 1];
    }

    private static String toFileName(CompilationUnitDeclaration ast) {
        return ast.compilationResult.fileName == null ? null : new String(ast.compilationResult.fileName);
    }

    public void rebuild(boolean force) {
        this.propagateProblems();
        if (this.completeParse && !force) {
            return;
        }
        boolean changed = this.isChanged();
        boolean newCompleteParse = EclipseAST.isComplete(this.compilationUnitDeclaration);
        if (!newCompleteParse && !force) {
            return;
        }
        ((EclipseNode)this.top()).rebuild();
        this.completeParse = newCompleteParse;
        if (!changed) {
            this.clearChanged();
        }
    }

    public static boolean isComplete(CompilationUnitDeclaration unit) {
        return (unit.bits & 0x10) != 0;
    }

    @Override
    protected EclipseNode buildTree(ASTNode node, AST.Kind kind) {
        switch (kind) {
            case COMPILATION_UNIT: {
                return this.buildCompilationUnit((CompilationUnitDeclaration)node);
            }
            case TYPE: {
                return this.buildType((TypeDeclaration)node);
            }
            case FIELD: {
                return this.buildField((FieldDeclaration)node, null);
            }
            case INITIALIZER: {
                return this.buildInitializer((Initializer)node);
            }
            case METHOD: {
                return this.buildMethod((AbstractMethodDeclaration)node);
            }
            case ARGUMENT: {
                return this.buildLocal((LocalDeclaration)((Argument)node), kind);
            }
            case LOCAL: {
                return this.buildLocal((LocalDeclaration)node, kind);
            }
            case STATEMENT: {
                return this.buildStatement((Statement)node);
            }
            case ANNOTATION: {
                return this.buildAnnotation((Annotation)node, false);
            }
            case TYPE_USE: {
                return this.buildTypeUse((TypeReference)node);
            }
        }
        throw new AssertionError((Object)("Did not expect to arrive here: " + (Object)((Object)kind)));
    }

    private EclipseNode buildCompilationUnit(CompilationUnitDeclaration top) {
        if (this.setAndGetAsHandled(top)) {
            return null;
        }
        List<EclipseNode> children = this.buildTypes(top.types);
        return this.putInMap(new EclipseNode(this, (ASTNode)top, children, AST.Kind.COMPILATION_UNIT));
    }

    private void addIfNotNull(Collection<EclipseNode> collection, EclipseNode n) {
        if (n != null) {
            collection.add(n);
        }
    }

    private List<EclipseNode> buildTypes(TypeDeclaration[] children) {
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        if (children != null) {
            TypeDeclaration[] typeDeclarationArray = children;
            int n = children.length;
            int n2 = 0;
            while (n2 < n) {
                TypeDeclaration type = typeDeclarationArray[n2];
                this.addIfNotNull(childNodes, this.buildType(type));
                ++n2;
            }
        }
        return childNodes;
    }

    private EclipseNode buildType(TypeDeclaration type) {
        if (this.setAndGetAsHandled(type)) {
            return null;
        }
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        childNodes.addAll(this.buildFields(type.fields, EclipseHandlerUtil.getRecordFieldAnnotations(type)));
        childNodes.addAll(this.buildTypes(type.memberTypes));
        childNodes.addAll(this.buildMethods(type.methods));
        childNodes.addAll(this.buildAnnotations(type.annotations, false));
        return this.putInMap(new EclipseNode(this, (ASTNode)type, childNodes, AST.Kind.TYPE));
    }

    private Collection<EclipseNode> buildFields(FieldDeclaration[] children, Annotation[][] annotations) {
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        if (children != null) {
            int i = 0;
            while (i < children.length) {
                this.addIfNotNull(childNodes, this.buildField(children[i], annotations[i]));
                ++i;
            }
        }
        return childNodes;
    }

    private static <T> List<T> singleton(T item) {
        ArrayList<T> list = new ArrayList<T>();
        if (item != null) {
            list.add(item);
        }
        return list;
    }

    private EclipseNode buildField(FieldDeclaration field, Annotation[] annotations) {
        if (field instanceof Initializer) {
            return this.buildInitializer((Initializer)field);
        }
        if (this.setAndGetAsHandled(field)) {
            return null;
        }
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        this.addIfNotNull(childNodes, this.buildTypeUse(field.type));
        this.addIfNotNull(childNodes, this.buildStatement((Statement)field.initialization));
        childNodes.addAll(this.buildAnnotations(annotations != null ? annotations : field.annotations, true));
        return this.putInMap(new EclipseNode(this, (ASTNode)field, childNodes, AST.Kind.FIELD));
    }

    private EclipseNode buildInitializer(Initializer initializer) {
        if (this.setAndGetAsHandled(initializer)) {
            return null;
        }
        return this.putInMap(new EclipseNode(this, (ASTNode)initializer, EclipseAST.singleton(this.buildStatement((Statement)initializer.block)), AST.Kind.INITIALIZER));
    }

    private Collection<EclipseNode> buildMethods(AbstractMethodDeclaration[] children) {
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        if (children != null) {
            AbstractMethodDeclaration[] abstractMethodDeclarationArray = children;
            int n = children.length;
            int n2 = 0;
            while (n2 < n) {
                AbstractMethodDeclaration method = abstractMethodDeclarationArray[n2];
                this.addIfNotNull(childNodes, this.buildMethod(method));
                ++n2;
            }
        }
        return childNodes;
    }

    private EclipseNode buildMethod(AbstractMethodDeclaration method) {
        if (this.setAndGetAsHandled(method)) {
            return null;
        }
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        childNodes.addAll(this.buildArguments(method.arguments));
        if (method instanceof ConstructorDeclaration) {
            ConstructorDeclaration constructor = (ConstructorDeclaration)method;
            this.addIfNotNull(childNodes, this.buildStatement((Statement)constructor.constructorCall));
        }
        childNodes.addAll(this.buildStatements(method.statements));
        childNodes.addAll(this.buildAnnotations(method.annotations, false));
        return this.putInMap(new EclipseNode(this, (ASTNode)method, childNodes, AST.Kind.METHOD));
    }

    private Collection<EclipseNode> buildArguments(Argument[] children) {
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        if (children != null) {
            Argument[] argumentArray = children;
            int n = children.length;
            int n2 = 0;
            while (n2 < n) {
                Argument local = argumentArray[n2];
                this.addIfNotNull(childNodes, this.buildLocal((LocalDeclaration)local, AST.Kind.ARGUMENT));
                ++n2;
            }
        }
        return childNodes;
    }

    private EclipseNode buildLocal(LocalDeclaration local, AST.Kind kind) {
        if (this.setAndGetAsHandled(local)) {
            return null;
        }
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        this.addIfNotNull(childNodes, this.buildTypeUse(local.type));
        this.addIfNotNull(childNodes, this.buildStatement((Statement)local.initialization));
        childNodes.addAll(this.buildAnnotations(local.annotations, true));
        return this.putInMap(new EclipseNode(this, (ASTNode)local, childNodes, kind));
    }

    private EclipseNode buildTypeUse(TypeReference tr) {
        TypeReference bound;
        int n;
        if (this.setAndGetAsHandled(tr)) {
            return null;
        }
        if (tr == null) {
            return null;
        }
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        Annotation[] anns = EclipseAST.getTopLevelTypeReferenceAnnotations(tr);
        if (anns != null) {
            Annotation[] annotationArray = anns;
            n = anns.length;
            int n2 = 0;
            while (n2 < n) {
                Annotation ann = annotationArray[n2];
                this.addIfNotNull(childNodes, this.buildAnnotation(ann, false));
                ++n2;
            }
        }
        if (tr instanceof ParameterizedQualifiedTypeReference) {
            ParameterizedQualifiedTypeReference pqtr = (ParameterizedQualifiedTypeReference)tr;
            int len = pqtr.tokens.length;
            int i = 0;
            while (i < len) {
                TypeReference[] typeArgs = pqtr.typeArguments[i];
                if (typeArgs != null) {
                    TypeReference[] typeReferenceArray = typeArgs;
                    int n3 = typeArgs.length;
                    int n4 = 0;
                    while (n4 < n3) {
                        TypeReference tArg = typeReferenceArray[n4];
                        this.addIfNotNull(childNodes, this.buildTypeUse(tArg));
                        ++n4;
                    }
                }
                ++i;
            }
        } else if (tr instanceof ParameterizedSingleTypeReference) {
            ParameterizedSingleTypeReference pstr = (ParameterizedSingleTypeReference)tr;
            if (pstr.typeArguments != null) {
                TypeReference[] typeReferenceArray = pstr.typeArguments;
                int n5 = pstr.typeArguments.length;
                n = 0;
                while (n < n5) {
                    TypeReference tArg = typeReferenceArray[n];
                    this.addIfNotNull(childNodes, this.buildTypeUse(tArg));
                    ++n;
                }
            }
        } else if (tr instanceof Wildcard && (bound = ((Wildcard)tr).bound) != null) {
            this.addIfNotNull(childNodes, this.buildTypeUse(bound));
        }
        return this.putInMap(new EclipseNode(this, (ASTNode)tr, childNodes, AST.Kind.TYPE_USE));
    }

    private Collection<EclipseNode> buildAnnotations(Annotation[] annotations, boolean varDecl) {
        ArrayList<EclipseNode> elements = new ArrayList<EclipseNode>();
        if (annotations != null) {
            Annotation[] annotationArray = annotations;
            int n = annotations.length;
            int n2 = 0;
            while (n2 < n) {
                Annotation an = annotationArray[n2];
                this.addIfNotNull(elements, this.buildAnnotation(an, varDecl));
                ++n2;
            }
        }
        return elements;
    }

    private EclipseNode buildAnnotation(Annotation annotation, boolean field) {
        if (annotation == null) {
            return null;
        }
        boolean handled = this.setAndGetAsHandled(annotation);
        if (!field && handled) {
            return null;
        }
        return this.putInMap(new EclipseNode(this, (ASTNode)annotation, null, AST.Kind.ANNOTATION));
    }

    private Collection<EclipseNode> buildStatements(Statement[] children) {
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        if (children != null) {
            Statement[] statementArray = children;
            int n = children.length;
            int n2 = 0;
            while (n2 < n) {
                Statement child = statementArray[n2];
                this.addIfNotNull(childNodes, this.buildStatement(child));
                ++n2;
            }
        }
        return childNodes;
    }

    private EclipseNode buildStatement(Statement child) {
        if (child == null) {
            return null;
        }
        if (child instanceof TypeDeclaration) {
            return this.buildType((TypeDeclaration)child);
        }
        if (child instanceof LocalDeclaration) {
            return this.buildLocal((LocalDeclaration)child, AST.Kind.LOCAL);
        }
        if (this.setAndGetAsHandled(child)) {
            return null;
        }
        return this.drill(child);
    }

    private EclipseNode drill(Statement statement) {
        ArrayList<EclipseNode> childNodes = new ArrayList<EclipseNode>();
        AST.FieldAccess[] fieldAccessArray = this.fieldsOf(statement.getClass());
        int n = fieldAccessArray.length;
        int n2 = 0;
        while (n2 < n) {
            AST.FieldAccess fa = fieldAccessArray[n2];
            childNodes.addAll(this.buildWithField(EclipseNode.class, statement, fa));
            ++n2;
        }
        return this.putInMap(new EclipseNode(this, (ASTNode)statement, childNodes, AST.Kind.STATEMENT));
    }

    private static Collection<Class<? extends ASTNode>> statementTypes() {
        return Collections.singleton(Statement.class);
    }

    private static class EcjReflectionCheck {
        private static final String COMPILATIONRESULT_TYPE = "org.eclipse.jdt.internal.compiler.CompilationResult";
        public static final Method addProblemToCompilationResult;
        public static final Throwable problemAddProblemToCompilationResult;
        public static final Method typeReferenceGetAnnotationsOnDimensions;
        public static final Field typeReferenceAnnotations;

        static {
            Field f;
            Method m2;
            Throwable problem_ = null;
            Method m1 = null;
            try {
                m1 = Permit.getMethod(EclipseAstProblemView.class, "addProblemToCompilationResult", char[].class, Class.forName(COMPILATIONRESULT_TYPE), Boolean.TYPE, String.class, Integer.TYPE, Integer.TYPE);
            }
            catch (Throwable t) {
                problem_ = t;
            }
            try {
                m2 = Permit.getMethod(TypeReference.class, "getAnnotationsOnDimensions", new Class[0]);
            }
            catch (Throwable throwable) {
                m2 = null;
            }
            try {
                f = Permit.getField(TypeReference.class, "annotations");
            }
            catch (Throwable throwable) {
                f = null;
            }
            addProblemToCompilationResult = m1;
            problemAddProblemToCompilationResult = problem_;
            typeReferenceGetAnnotationsOnDimensions = m2;
            typeReferenceAnnotations = f;
        }

        private EcjReflectionCheck() {
        }
    }

    private static class EclipseWorkspaceBasedFileResolver {
        private EclipseWorkspaceBasedFileResolver() {
        }

        public static URI resolve(String path) {
            if (path == null || path.indexOf(47, 1) == -1) {
                return null;
            }
            try {
                return ResourcesPlugin.getWorkspace().getRoot().getFile((IPath)new Path(path)).getLocationURI();
            }
            catch (Exception exception) {
                return null;
            }
        }
    }

    class ParseProblem {
        final boolean isWarning;
        final String message;
        final int sourceStart;
        final int sourceEnd;

        ParseProblem(boolean isWarning, String message, int sourceStart, int sourceEnd) {
            this.isWarning = isWarning;
            this.message = message;
            this.sourceStart = sourceStart;
            this.sourceEnd = sourceEnd;
        }

        void addToCompilationResult() {
            CompilationUnitDeclaration cud = (CompilationUnitDeclaration)((EclipseNode)EclipseAST.this.top()).get();
            EclipseAST.addProblemToCompilationResult(cud.getFileName(), cud.compilationResult, this.isWarning, this.message, this.sourceStart, this.sourceEnd);
        }
    }
}
