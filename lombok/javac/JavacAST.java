package lombok.javac;

import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import lombok.core.AST;
import lombok.core.CleanupRegistry;
import lombok.core.CleanupTask;
import lombok.javac.CompilerMessageSuppressor;
import lombok.javac.Javac;
import lombok.javac.JavacASTVisitor;
import lombok.javac.JavacImportList;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.PackageName;
import lombok.permit.Permit;

public class JavacAST
extends AST<JavacAST, JavacNode, JCTree> {
    private final CleanupRegistry cleanup;
    private final JavacElements elements;
    private final JavacTreeMaker treeMaker;
    private final Symtab symtab;
    private final JavacTypes javacTypes;
    private final ErrorLog errorLogger;
    private final Context context;
    private static final URI NOT_CALCULATED_MARKER = URI.create("https://projectlombok.org/not/calculated");
    private URI memoizedAbsoluteFileLocation = NOT_CALCULATED_MARKER;
    private static Class<?> wrappedFileObjectClass;
    private static Class<?> sbtJavaFileObjectClass;
    private static Class<?> sbtMappedVirtualFileClass;
    private static Class<?> sbtOptionClass;
    private static Field wrappedFileObjectField;
    private static Field sbtJavaFileObjectField;
    private static Field sbtMappedVirtualFilePathField;
    private static Field sbtMappedVirtualFileRootsField;
    private static Field sbtOptionField;
    private static Method sbtMapGetMethod;
    private static boolean JCTRY_RESOURCES_FIELD_INITIALIZED;
    private static Field JCTRY_RESOURCES_FIELD;
    private static boolean JCANNOTATEDTYPE_FIELDS_INITIALIZED;
    private static Field JCANNOTATEDTYPE_ANNOTATIONS;
    private static Field JCANNOTATEDTYPE_UNDERLYINGTYPE;
    private static Field JCENHANCEDFORLOOP_VARORRECORDPATTERN_FIELD;
    private static final ConcurrentMap<Class<?>, Method> getBodyMethods;

    static {
        JCTRY_RESOURCES_FIELD_INITIALIZED = false;
        JCANNOTATEDTYPE_FIELDS_INITIALIZED = false;
        JCENHANCEDFORLOOP_VARORRECORDPATTERN_FIELD = Permit.permissiveGetField(JCTree.JCEnhancedForLoop.class, "varOrRecordPattern");
        getBodyMethods = new ConcurrentHashMap();
    }

    public JavacAST(ErrorLog errorLog, Context context, JCTree.JCCompilationUnit top, CleanupRegistry cleanup) {
        super(JavacAST.sourceName(top), PackageName.getPackageName(top), new JavacImportList(top), JavacAST.statementTypes());
        this.setTop(this.buildCompilationUnit(top));
        this.context = context;
        this.errorLogger = errorLog;
        this.elements = JavacElements.instance(context);
        this.treeMaker = new JavacTreeMaker(TreeMaker.instance(context));
        this.symtab = Symtab.instance(context);
        this.javacTypes = JavacTypes.instance(context);
        this.cleanup = cleanup;
        this.clearChanged();
    }

    @Override
    public URI getAbsoluteFileLocation() {
        if (this.memoizedAbsoluteFileLocation == NOT_CALCULATED_MARKER) {
            this.memoizedAbsoluteFileLocation = JavacAST.getAbsoluteFileLocation((JCTree.JCCompilationUnit)((JavacNode)this.top()).get());
        }
        return this.memoizedAbsoluteFileLocation;
    }

    public static URI getAbsoluteFileLocation(JCTree.JCCompilationUnit cu) {
        try {
            URI uri = cu.sourcefile.toUri();
            String fn = uri.toString();
            if (fn.startsWith("file:")) {
                return uri;
            }
            URI sbtUri = JavacAST.tryGetSbtFile(cu.sourcefile);
            if (sbtUri != null) {
                return sbtUri;
            }
            return uri;
        }
        catch (Exception exception) {
            return null;
        }
    }

    private static URI tryGetSbtFile(JavaFileObject sourcefile) {
        try {
            return JavacAST.tryGetSbtFile_(sourcefile);
        }
        catch (Exception exception) {
            return null;
        }
    }

    private static URI tryGetSbtFile_(JavaFileObject sourcefile) throws Exception {
        String cn;
        Class<?> c = sourcefile.getClass();
        if (wrappedFileObjectClass == null) {
            if (!c.getName().equals("com.sun.tools.javac.api.ClientCodeWrapper$WrappedJavaFileObject")) {
                return null;
            }
            wrappedFileObjectClass = c;
        }
        if (c != wrappedFileObjectClass) {
            return null;
        }
        if (wrappedFileObjectField == null) {
            wrappedFileObjectField = Permit.permissiveGetField(wrappedFileObjectClass.getSuperclass(), "clientFileObject");
        }
        if (wrappedFileObjectField == null) {
            return null;
        }
        Object fileObject = wrappedFileObjectField.get(sourcefile);
        c = fileObject.getClass();
        if (sbtJavaFileObjectClass == null) {
            cn = c.getName();
            if (!cn.startsWith("sbt.") || !cn.endsWith("JavaFileObject")) {
                return null;
            }
            sbtJavaFileObjectClass = c;
        }
        if (sbtJavaFileObjectClass != c) {
            return null;
        }
        if (sbtJavaFileObjectField == null) {
            sbtJavaFileObjectField = Permit.permissiveGetField(sbtJavaFileObjectClass, "underlying");
        }
        if (sbtJavaFileObjectField == null) {
            return null;
        }
        Object mappedVirtualFile = sbtJavaFileObjectField.get(fileObject);
        c = mappedVirtualFile.getClass();
        if (sbtMappedVirtualFileClass == null) {
            cn = c.getName();
            if (!cn.startsWith("sbt.") || !cn.endsWith("MappedVirtualFile")) {
                return null;
            }
            sbtMappedVirtualFileClass = c;
        }
        if (sbtMappedVirtualFilePathField == null) {
            sbtMappedVirtualFilePathField = Permit.permissiveGetField(sbtMappedVirtualFileClass, "encodedPath");
        }
        if (sbtMappedVirtualFilePathField == null) {
            return null;
        }
        if (sbtMappedVirtualFileRootsField == null) {
            sbtMappedVirtualFileRootsField = Permit.permissiveGetField(sbtMappedVirtualFileClass, "rootPathsMap");
        }
        if (sbtMappedVirtualFileRootsField == null) {
            return null;
        }
        String encodedPath = (String)sbtMappedVirtualFilePathField.get(mappedVirtualFile);
        if (!encodedPath.startsWith("${")) {
            File maybeAbsoluteFile = new File(encodedPath);
            if (maybeAbsoluteFile.exists()) {
                return maybeAbsoluteFile.toURI();
            }
            return null;
        }
        int idx = encodedPath.indexOf(125);
        if (idx == -1) {
            return null;
        }
        String base = encodedPath.substring(2, idx);
        Object roots = sbtMappedVirtualFileRootsField.get(mappedVirtualFile);
        if (sbtMapGetMethod == null) {
            sbtMapGetMethod = Permit.getMethod(roots.getClass(), "get", Object.class);
        }
        if (sbtMapGetMethod == null) {
            return null;
        }
        Object option = sbtMapGetMethod.invoke(roots, base);
        c = option.getClass();
        if (sbtOptionClass == null && c.getName().equals("scala.Some")) {
            sbtOptionClass = c;
        }
        if (c != sbtOptionClass) {
            return null;
        }
        if (sbtOptionField == null) {
            sbtOptionField = Permit.permissiveGetField(sbtOptionClass, "value");
        }
        if (sbtOptionField == null) {
            return null;
        }
        Object path = sbtOptionField.get(option);
        return new File(String.valueOf(path.toString()) + encodedPath.substring(idx + 1)).toURI();
    }

    private static String sourceName(JCTree.JCCompilationUnit cu) {
        return cu.sourcefile == null ? null : cu.sourcefile.toString();
    }

    public Context getContext() {
        return this.context;
    }

    public void traverse(JavacASTVisitor visitor) {
        ((JavacNode)this.top()).traverse(visitor);
    }

    void traverseChildren(JavacASTVisitor visitor, JavacNode node) {
        for (JavacNode child : node.down()) {
            child.traverse(visitor);
        }
    }

    @Override
    public int getSourceVersion() {
        try {
            String nm = Source.instance(this.context).name();
            int underscoreIdx = nm.indexOf(95);
            if (underscoreIdx > -1) {
                return Integer.parseInt(nm.substring(underscoreIdx + 1));
            }
            return Integer.parseInt(nm.substring(3));
        }
        catch (Exception exception) {
            return 6;
        }
    }

    @Override
    public int getLatestJavaSpecSupported() {
        return Javac.getJavaCompilerVersion();
    }

    public void cleanupTask(String key, JCTree target, CleanupTask task) {
        this.cleanup.registerTask(key, target, task);
    }

    public Name toName(String name) {
        return this.elements.getName(name);
    }

    public JavacTreeMaker getTreeMaker() {
        this.treeMaker.at(-1);
        return this.treeMaker;
    }

    public Symtab getSymbolTable() {
        return this.symtab;
    }

    public JavacTypes getTypesUtil() {
        return this.javacTypes;
    }

    @Override
    protected JavacNode buildTree(JCTree node, AST.Kind kind) {
        switch (kind) {
            case COMPILATION_UNIT: {
                return this.buildCompilationUnit((JCTree.JCCompilationUnit)node);
            }
            case TYPE: {
                return this.buildType((JCTree.JCClassDecl)node);
            }
            case FIELD: {
                return this.buildField((JCTree.JCVariableDecl)node);
            }
            case INITIALIZER: {
                return this.buildInitializer((JCTree.JCBlock)node);
            }
            case METHOD: {
                return this.buildMethod((JCTree.JCMethodDecl)node);
            }
            case ARGUMENT: {
                return this.buildLocalVar((JCTree.JCVariableDecl)node, kind);
            }
            case LOCAL: {
                return this.buildLocalVar((JCTree.JCVariableDecl)node, kind);
            }
            case STATEMENT: {
                return this.buildStatementOrExpression(node);
            }
            case ANNOTATION: {
                return this.buildAnnotation((JCTree.JCAnnotation)node, false);
            }
            case TYPE_USE: {
                return this.buildTypeUse(node);
            }
        }
        throw new AssertionError((Object)("Did not expect: " + (Object)((Object)kind)));
    }

    private JavacNode buildCompilationUnit(JCTree.JCCompilationUnit top) {
        ArrayList<JavacNode> childNodes = new ArrayList<JavacNode>();
        for (JCTree s : top.defs) {
            if (!(s instanceof JCTree.JCClassDecl)) continue;
            JavacAST.addIfNotNull(childNodes, this.buildType((JCTree.JCClassDecl)s));
        }
        return new JavacNode(this, top, childNodes, AST.Kind.COMPILATION_UNIT);
    }

    private JavacNode buildType(JCTree.JCClassDecl type) {
        if (this.setAndGetAsHandled(type)) {
            return null;
        }
        ArrayList<JavacNode> childNodes = new ArrayList<JavacNode>();
        for (JCTree.JCAnnotation annotation : type.mods.annotations) {
            JavacAST.addIfNotNull(childNodes, this.buildAnnotation(annotation, false));
        }
        for (JCTree def : type.defs) {
            if (def instanceof JCTree.JCMethodDecl) {
                JavacAST.addIfNotNull(childNodes, this.buildMethod((JCTree.JCMethodDecl)def));
                continue;
            }
            if (def instanceof JCTree.JCClassDecl) {
                JavacAST.addIfNotNull(childNodes, this.buildType((JCTree.JCClassDecl)def));
                continue;
            }
            if (def instanceof JCTree.JCVariableDecl) {
                JavacAST.addIfNotNull(childNodes, this.buildField((JCTree.JCVariableDecl)def));
                continue;
            }
            if (!(def instanceof JCTree.JCBlock)) continue;
            JavacAST.addIfNotNull(childNodes, this.buildInitializer((JCTree.JCBlock)def));
        }
        return this.putInMap(new JavacNode(this, type, childNodes, AST.Kind.TYPE));
    }

    private JavacNode buildField(JCTree.JCVariableDecl field) {
        if (this.setAndGetAsHandled(field)) {
            return null;
        }
        ArrayList<JavacNode> childNodes = new ArrayList<JavacNode>();
        for (JCTree.JCAnnotation annotation : field.mods.annotations) {
            JavacAST.addIfNotNull(childNodes, this.buildAnnotation(annotation, true));
        }
        JavacAST.addIfNotNull(childNodes, this.buildTypeUse(field.vartype));
        JavacAST.addIfNotNull(childNodes, this.buildExpression(field.init));
        return this.putInMap(new JavacNode(this, field, childNodes, AST.Kind.FIELD));
    }

    private JavacNode buildLocalVar(JCTree.JCVariableDecl local, AST.Kind kind) {
        if (this.setAndGetAsHandled(local)) {
            return null;
        }
        ArrayList<JavacNode> childNodes = new ArrayList<JavacNode>();
        for (JCTree.JCAnnotation annotation : local.mods.annotations) {
            JavacAST.addIfNotNull(childNodes, this.buildAnnotation(annotation, true));
        }
        JavacAST.addIfNotNull(childNodes, this.buildTypeUse(local.vartype));
        JavacAST.addIfNotNull(childNodes, this.buildExpression(local.init));
        return this.putInMap(new JavacNode(this, local, childNodes, kind));
    }

    private JavacNode buildTypeUse(JCTree typeUse) {
        if (this.setAndGetAsHandled(typeUse)) {
            return null;
        }
        if (typeUse == null) {
            return null;
        }
        if (typeUse.getClass().getName().equals("com.sun.tools.javac.tree.JCTree$JCAnnotatedType")) {
            JavacAST.initJcAnnotatedType(typeUse.getClass());
            Collection anns = Permit.permissiveReadField(Collection.class, JCANNOTATEDTYPE_ANNOTATIONS, typeUse);
            JCTree.JCExpression underlying = Permit.permissiveReadField(JCTree.JCExpression.class, JCANNOTATEDTYPE_UNDERLYINGTYPE, typeUse);
            ArrayList<JavacNode> childNodes = new ArrayList<JavacNode>();
            if (anns != null) {
                for (Object annotation : anns) {
                    if (!(annotation instanceof JCTree.JCAnnotation)) continue;
                    JavacAST.addIfNotNull(childNodes, this.buildAnnotation((JCTree.JCAnnotation)annotation, true));
                }
            }
            JavacAST.addIfNotNull(childNodes, this.buildTypeUse(underlying));
            return this.putInMap(new JavacNode(this, typeUse, childNodes, AST.Kind.TYPE_USE));
        }
        if (typeUse instanceof JCTree.JCWildcard) {
            ArrayList<JavacNode> childNodes;
            JCTree inner = ((JCTree.JCWildcard)typeUse).inner;
            ArrayList<JavacNode> arrayList = childNodes = inner == null ? Collections.emptyList() : new ArrayList<JavacNode>();
            if (inner != null) {
                JavacAST.addIfNotNull(childNodes, this.buildTypeUse(inner));
            }
            return this.putInMap(new JavacNode(this, typeUse, childNodes, AST.Kind.TYPE_USE));
        }
        if (typeUse instanceof JCTree.JCArrayTypeTree) {
            ArrayList<JavacNode> childNodes;
            JCTree.JCExpression inner = ((JCTree.JCArrayTypeTree)typeUse).elemtype;
            ArrayList<JavacNode> arrayList = childNodes = inner == null ? Collections.emptyList() : new ArrayList<JavacNode>();
            if (inner != null) {
                JavacAST.addIfNotNull(childNodes, this.buildTypeUse(inner));
            }
            return this.putInMap(new JavacNode(this, typeUse, childNodes, AST.Kind.TYPE_USE));
        }
        if (typeUse instanceof JCTree.JCFieldAccess) {
            ArrayList<JavacNode> childNodes;
            JCTree.JCExpression inner = ((JCTree.JCFieldAccess)typeUse).selected;
            ArrayList<JavacNode> arrayList = childNodes = inner == null ? Collections.emptyList() : new ArrayList<JavacNode>();
            if (inner != null) {
                JavacAST.addIfNotNull(childNodes, this.buildTypeUse(inner));
            }
            return this.putInMap(new JavacNode(this, typeUse, childNodes, AST.Kind.TYPE_USE));
        }
        if (typeUse instanceof JCTree.JCIdent) {
            return this.putInMap(new JavacNode(this, typeUse, Collections.<JavacNode>emptyList(), AST.Kind.TYPE_USE));
        }
        return null;
    }

    private static java.util.List<JCTree> getResourcesForTryNode(JCTree.JCTry tryNode) {
        if (!JCTRY_RESOURCES_FIELD_INITIALIZED) {
            JCTRY_RESOURCES_FIELD = Permit.permissiveGetField(JCTree.JCTry.class, "resources");
            JCTRY_RESOURCES_FIELD_INITIALIZED = true;
        }
        if (JCTRY_RESOURCES_FIELD == null) {
            return Collections.emptyList();
        }
        Object rv = null;
        try {
            rv = JCTRY_RESOURCES_FIELD.get(tryNode);
        }
        catch (Exception exception) {}
        if (rv instanceof java.util.List) {
            return (java.util.List)rv;
        }
        return Collections.emptyList();
    }

    private static void initJcAnnotatedType(Class<?> context) {
        if (JCANNOTATEDTYPE_FIELDS_INITIALIZED) {
            return;
        }
        JCANNOTATEDTYPE_ANNOTATIONS = Permit.permissiveGetField(context, "annotations");
        JCANNOTATEDTYPE_UNDERLYINGTYPE = Permit.permissiveGetField(context, "underlyingType");
        JCANNOTATEDTYPE_FIELDS_INITIALIZED = true;
    }

    private static JCTree getVarOrRecordPattern(JCTree.JCEnhancedForLoop loop) {
        if (JCENHANCEDFORLOOP_VARORRECORDPATTERN_FIELD == null) {
            return loop.var;
        }
        try {
            return (JCTree)JCENHANCEDFORLOOP_VARORRECORDPATTERN_FIELD.get(loop);
        }
        catch (Exception exception) {
            return null;
        }
    }

    private JavacNode buildTry(JCTree.JCTry tryNode) {
        if (this.setAndGetAsHandled(tryNode)) {
            return null;
        }
        ArrayList<JavacNode> childNodes = new ArrayList<JavacNode>();
        for (JCTree varDecl : JavacAST.getResourcesForTryNode(tryNode)) {
            if (!(varDecl instanceof JCTree.JCVariableDecl)) continue;
            JavacAST.addIfNotNull(childNodes, this.buildLocalVar((JCTree.JCVariableDecl)varDecl, AST.Kind.LOCAL));
        }
        JavacAST.addIfNotNull(childNodes, this.buildStatement(tryNode.body));
        for (JCTree.JCCatch jcc : tryNode.catchers) {
            JavacAST.addIfNotNull(childNodes, this.buildTree(jcc, AST.Kind.STATEMENT));
        }
        JavacAST.addIfNotNull(childNodes, this.buildStatement(tryNode.finalizer));
        return this.putInMap(new JavacNode(this, tryNode, childNodes, AST.Kind.STATEMENT));
    }

    private JavacNode buildInitializer(JCTree.JCBlock initializer) {
        if (this.setAndGetAsHandled(initializer)) {
            return null;
        }
        ArrayList<JavacNode> childNodes = new ArrayList<JavacNode>();
        for (JCTree.JCStatement statement : initializer.stats) {
            JavacAST.addIfNotNull(childNodes, this.buildStatement(statement));
        }
        return this.putInMap(new JavacNode(this, initializer, childNodes, AST.Kind.INITIALIZER));
    }

    private JavacNode buildMethod(JCTree.JCMethodDecl method) {
        if (this.setAndGetAsHandled(method)) {
            return null;
        }
        ArrayList<JavacNode> childNodes = new ArrayList<JavacNode>();
        for (JCTree.JCAnnotation annotation : method.mods.annotations) {
            JavacAST.addIfNotNull(childNodes, this.buildAnnotation(annotation, false));
        }
        for (JCTree.JCVariableDecl param : method.params) {
            JavacAST.addIfNotNull(childNodes, this.buildLocalVar(param, AST.Kind.ARGUMENT));
        }
        if (method.body != null && method.body.stats != null) {
            for (JCTree.JCStatement statement : method.body.stats) {
                JavacAST.addIfNotNull(childNodes, this.buildStatement(statement));
            }
        }
        return this.putInMap(new JavacNode(this, method, childNodes, AST.Kind.METHOD));
    }

    private JavacNode buildAnnotation(JCTree.JCAnnotation annotation, boolean varDecl) {
        boolean handled = this.setAndGetAsHandled(annotation);
        if (!varDecl && handled) {
            return null;
        }
        return this.putInMap(new JavacNode(this, annotation, null, AST.Kind.ANNOTATION));
    }

    private JavacNode buildExpression(JCTree.JCExpression expression) {
        return this.buildStatementOrExpression(expression);
    }

    private JavacNode buildStatement(JCTree.JCStatement statement) {
        return this.buildStatementOrExpression(statement);
    }

    private JavacNode buildStatementOrExpression(JCTree statement) {
        if (statement == null) {
            return null;
        }
        if (statement instanceof JCTree.JCAnnotation) {
            return null;
        }
        if (statement instanceof JCTree.JCClassDecl) {
            return this.buildType((JCTree.JCClassDecl)statement);
        }
        if (statement instanceof JCTree.JCVariableDecl) {
            return this.buildLocalVar((JCTree.JCVariableDecl)statement, AST.Kind.LOCAL);
        }
        if (statement instanceof JCTree.JCTry) {
            return this.buildTry((JCTree.JCTry)statement);
        }
        if (statement.getClass().getName().equals("com.sun.tools.javac.tree.JCTree$JCLambda")) {
            return this.buildLambda(statement);
        }
        if (statement instanceof JCTree.JCEnhancedForLoop) {
            return this.buildEnhancedForLoop((JCTree.JCEnhancedForLoop)statement);
        }
        if (this.setAndGetAsHandled(statement)) {
            return null;
        }
        return this.drill(statement);
    }

    private JavacNode buildLambda(JCTree jcTree) {
        return this.buildStatementOrExpression(this.getBody(jcTree));
    }

    private JCTree getBody(JCTree jcTree) {
        return (JCTree)Permit.invokeSneaky(this.getBodyMethod(jcTree.getClass()), jcTree, new Object[0]);
    }

    private Method getBodyMethod(Class<?> c) {
        Method m = (Method)getBodyMethods.get(c);
        if (m != null) {
            return m;
        }
        try {
            m = Permit.getMethod(c, "getBody", new Class[0]);
        }
        catch (NoSuchMethodException e) {
            throw Javac.sneakyThrow(e);
        }
        getBodyMethods.putIfAbsent(c, m);
        return (Method)getBodyMethods.get(c);
    }

    private JavacNode buildEnhancedForLoop(JCTree.JCEnhancedForLoop loop) {
        if (this.setAndGetAsHandled(loop)) {
            return null;
        }
        ArrayList<JavacNode> childNodes = new ArrayList<JavacNode>();
        JavacAST.addIfNotNull(childNodes, this.buildTree(JavacAST.getVarOrRecordPattern(loop), AST.Kind.STATEMENT));
        JavacAST.addIfNotNull(childNodes, this.buildTree(loop.expr, AST.Kind.STATEMENT));
        JavacAST.addIfNotNull(childNodes, this.buildStatement(loop.body));
        return this.putInMap(new JavacNode(this, loop, childNodes, AST.Kind.STATEMENT));
    }

    private JavacNode drill(JCTree statement) {
        try {
            ArrayList<JavacNode> childNodes = new ArrayList<JavacNode>();
            AST.FieldAccess[] fieldAccessArray = this.fieldsOf(statement.getClass());
            int n = fieldAccessArray.length;
            int n2 = 0;
            while (n2 < n) {
                AST.FieldAccess fa = fieldAccessArray[n2];
                childNodes.addAll(this.buildWithField(JavacNode.class, statement, fa));
                ++n2;
            }
            return this.putInMap(new JavacNode(this, statement, childNodes, AST.Kind.STATEMENT));
        }
        catch (OutOfMemoryError oome) {
            String msg = oome.getMessage();
            if (msg == null) {
                msg = "(no original message)";
            }
            OutOfMemoryError newError = new OutOfMemoryError(String.valueOf(this.getFileName()) + "@pos" + statement.getPreferredPosition() + ": " + msg);
            throw newError;
        }
    }

    private static Collection<Class<? extends JCTree>> statementTypes() {
        ArrayList<Class<? extends JCTree>> collection = new ArrayList<Class<? extends JCTree>>(3);
        collection.add(JCTree.JCStatement.class);
        collection.add(JCTree.JCExpression.class);
        collection.add(JCTree.JCCatch.class);
        return collection;
    }

    private static void addIfNotNull(Collection<JavacNode> nodes, JavacNode node) {
        if (node != null) {
            nodes.add(node);
        }
    }

    void removeDeferredErrors(JavacNode node) {
        JCDiagnostic.DiagnosticPosition pos = ((JCTree)node.get()).pos();
        JCTree.JCCompilationUnit top = (JCTree.JCCompilationUnit)((JavacNode)this.top()).get();
        this.removeFromDeferredDiagnostics(pos.getStartPosition(), Javac.getEndPosition(pos, top));
    }

    void printMessage(Diagnostic.Kind kind, String message, JavacNode node, JCDiagnostic.DiagnosticPosition pos, boolean attemptToRemoveErrorsInRange) {
        JavaFileObject oldSource = null;
        JavaFileObject newSource = null;
        JCTree astObject = node == null ? null : (JCTree)node.get();
        JCTree.JCCompilationUnit top = (JCTree.JCCompilationUnit)((JavacNode)this.top()).get();
        newSource = top.sourcefile;
        if (newSource != null) {
            oldSource = this.errorLogger.useSource(newSource);
            if (pos == null) {
                pos = astObject.pos();
            }
        }
        if (pos != null && node != null && attemptToRemoveErrorsInRange) {
            this.removeFromDeferredDiagnostics(pos.getStartPosition(), node.getEndPosition(pos));
        }
        try {
            switch (kind) {
                case ERROR: {
                    this.errorLogger.error(pos, message);
                    break;
                }
                case MANDATORY_WARNING: {
                    this.errorLogger.mandatoryWarning(pos, message);
                    break;
                }
                case WARNING: {
                    this.errorLogger.warning(pos, message);
                    break;
                }
                default: {
                    this.errorLogger.note(pos, message);
                    break;
                }
            }
        }
        finally {
            if (newSource != null) {
                this.errorLogger.useSource(oldSource);
            }
        }
    }

    public void removeFromDeferredDiagnostics(int startPos, int endPos) {
        JCTree.JCCompilationUnit self = (JCTree.JCCompilationUnit)((JavacNode)this.top()).get();
        new CompilerMessageSuppressor(this.getContext()).removeAllBetween(self.sourcefile, startPos, endPos);
    }

    @Override
    protected void setElementInASTCollection(Field field, Object refField, java.util.List<Collection<?>> chain, Collection<?> collection, int idx, JCTree newN) throws IllegalAccessException {
        List<?> list = this.setElementInConsList(chain, collection, ((java.util.List)collection).get(idx), newN);
        field.set(refField, list);
    }

    private List<?> setElementInConsList(java.util.List<Collection<?>> chain, Collection<?> current, Object oldO, Object newO) {
        List oldL = (List)current;
        List<?> newL = this.replaceInConsList(oldL, oldO, newO);
        if (chain.isEmpty()) {
            return newL;
        }
        ArrayList reducedChain = new ArrayList(chain);
        Collection newCurrent = (Collection)reducedChain.remove(reducedChain.size() - 1);
        return this.setElementInConsList(reducedChain, newCurrent, oldL, newL);
    }

    private List<?> replaceInConsList(List<?> oldL, Object oldO, Object newO) {
        boolean repl = false;
        Object[] a = oldL.toArray();
        int i = 0;
        while (i < a.length) {
            if (a[i] == oldO) {
                a[i] = newO;
                repl = true;
            }
            ++i;
        }
        if (repl) {
            return List.from(a);
        }
        return oldL;
    }

    static abstract class ErrorLog {
        final Log log;
        private final Messager messager;
        private final Field errorCount;
        private final Field warningCount;

        private ErrorLog(Log log, Messager messager, Field errorCount, Field warningCount) {
            this.log = log;
            this.messager = messager;
            this.errorCount = errorCount;
            this.warningCount = warningCount;
        }

        final JavaFileObject useSource(JavaFileObject file) {
            return this.log.useSource(file);
        }

        final void error(JCDiagnostic.DiagnosticPosition pos, String message) {
            this.increment(this.errorCount);
            this.error1(pos, message);
        }

        final void warning(JCDiagnostic.DiagnosticPosition pos, String message) {
            this.increment(this.warningCount);
            this.warning1(pos, message);
        }

        final void mandatoryWarning(JCDiagnostic.DiagnosticPosition pos, String message) {
            this.increment(this.warningCount);
            this.mandatoryWarning1(pos, message);
        }

        abstract void error1(JCDiagnostic.DiagnosticPosition var1, String var2);

        abstract void warning1(JCDiagnostic.DiagnosticPosition var1, String var2);

        abstract void mandatoryWarning1(JCDiagnostic.DiagnosticPosition var1, String var2);

        abstract void note(JCDiagnostic.DiagnosticPosition var1, String var2);

        private void increment(Field field) {
            if (field == null) {
                return;
            }
            try {
                int val = ((Number)field.get(this.messager)).intValue();
                field.set(this.messager, val + 1);
            }
            catch (Throwable throwable) {}
        }

        static ErrorLog create(Messager messager, Context context) {
            Field warningCount;
            Field errorCount;
            try {
                errorCount = Permit.getField(messager.getClass(), "errorCount");
            }
            catch (Throwable throwable) {
                errorCount = null;
            }
            Log log = Log.instance(context);
            boolean hasMultipleErrors = false;
            Field[] fieldArray = log.getClass().getFields();
            int n = fieldArray.length;
            int n2 = 0;
            while (n2 < n) {
                Field field = fieldArray[n2];
                if (field.getName().equals("multipleErrors")) {
                    hasMultipleErrors = true;
                    break;
                }
                ++n2;
            }
            if (hasMultipleErrors) {
                return new JdkBefore9(log, messager, errorCount);
            }
            try {
                warningCount = Permit.getField(messager.getClass(), "warningCount");
            }
            catch (Throwable throwable) {
                warningCount = null;
            }
            return new Jdk9Plus(log, messager, errorCount, warningCount);
        }
    }

    static class Jdk9Plus
    extends ErrorLog {
        private static final String PROC_MESSAGER = "proc.messager";
        private Object multiple;
        private Method errorMethod;
        private Method warningMethod;
        private Method mandatoryWarningMethod;
        private Method noteMethod;
        private Method errorKey;
        private Method warningKey;
        private Method noteKey;
        private JCDiagnostic.Factory diags;

        private Jdk9Plus(Log log, Messager messager, Field errorCount, Field warningCount) {
            super(log, messager, errorCount, warningCount);
            try {
                Class<?> df = Class.forName("com.sun.tools.javac.util.JCDiagnostic$DiagnosticFlag");
                ?[] objArray = df.getEnumConstants();
                int n = objArray.length;
                int n2 = 0;
                while (n2 < n) {
                    Object constant = objArray[n2];
                    if (constant.toString().equals("MULTIPLE")) {
                        this.multiple = constant;
                    }
                    ++n2;
                }
                Class<?> errorCls = Class.forName("com.sun.tools.javac.util.JCDiagnostic$Error");
                Class<?> warningCls = Class.forName("com.sun.tools.javac.util.JCDiagnostic$Warning");
                Class<?> noteCls = Class.forName("com.sun.tools.javac.util.JCDiagnostic$Note");
                Class<?> lc = log.getClass();
                this.errorMethod = Permit.getMethod(lc, "error", df, JCDiagnostic.DiagnosticPosition.class, errorCls);
                this.warningMethod = Permit.getMethod(lc, "warning", JCDiagnostic.DiagnosticPosition.class, warningCls);
                this.mandatoryWarningMethod = Permit.getMethod(lc, "mandatoryWarning", JCDiagnostic.DiagnosticPosition.class, warningCls);
                this.noteMethod = Permit.getMethod(lc, "note", JCDiagnostic.DiagnosticPosition.class, noteCls);
                Field diagsField = Permit.getField(lc.getSuperclass(), "diags");
                this.diags = (JCDiagnostic.Factory)diagsField.get(log);
                Class<?> dc = this.diags.getClass();
                this.errorKey = Permit.getMethod(dc, "errorKey", String.class, Object[].class);
                this.warningKey = Permit.getMethod(dc, "warningKey", String.class, Object[].class);
                this.noteKey = Permit.getMethod(dc, "noteKey", String.class, Object[].class);
            }
            catch (Throwable throwable) {}
        }

        @Override
        void error1(JCDiagnostic.DiagnosticPosition pos, String message) {
            Object error = Permit.invokeSneaky(this.errorKey, this.diags, PROC_MESSAGER, new Object[]{message});
            if (error != null) {
                Permit.invokeSneaky(this.errorMethod, this.log, this.multiple, pos, error);
            }
        }

        @Override
        void warning1(JCDiagnostic.DiagnosticPosition pos, String message) {
            Object warning = Permit.invokeSneaky(this.warningKey, this.diags, PROC_MESSAGER, new Object[]{message});
            if (warning != null) {
                Permit.invokeSneaky(this.warningMethod, this.log, pos, warning);
            }
        }

        @Override
        void mandatoryWarning1(JCDiagnostic.DiagnosticPosition pos, String message) {
            Object warning = Permit.invokeSneaky(this.warningKey, this.diags, PROC_MESSAGER, new Object[]{message});
            if (warning != null) {
                Permit.invokeSneaky(this.mandatoryWarningMethod, this.log, pos, warning);
            }
        }

        @Override
        void note(JCDiagnostic.DiagnosticPosition pos, String message) {
            Object note = Permit.invokeSneaky(this.noteKey, this.diags, PROC_MESSAGER, new Object[]{message});
            if (note != null) {
                Permit.invokeSneaky(this.noteMethod, this.log, pos, note);
            }
        }
    }

    static class JdkBefore9
    extends ErrorLog {
        private JdkBefore9(Log log, Messager messager, Field errorCount) {
            super(log, messager, errorCount, null);
        }

        @Override
        void error1(JCDiagnostic.DiagnosticPosition pos, String message) {
            boolean prev = this.log.multipleErrors;
            this.log.multipleErrors = true;
            try {
                this.log.error(pos, "proc.messager", new Object[]{message});
            }
            finally {
                this.log.multipleErrors = prev;
            }
        }

        @Override
        void warning1(JCDiagnostic.DiagnosticPosition pos, String message) {
            this.log.warning(pos, "proc.messager", new Object[]{message});
        }

        @Override
        void mandatoryWarning1(JCDiagnostic.DiagnosticPosition pos, String message) {
            this.log.mandatoryWarning(pos, "proc.messager", new Object[]{message});
        }

        @Override
        void note(JCDiagnostic.DiagnosticPosition pos, String message) {
            this.log.note(pos, "proc.messager", new Object[]{message});
        }
    }
}
