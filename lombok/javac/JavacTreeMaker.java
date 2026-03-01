package lombok.javac;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.javac.Javac;
import lombok.permit.Permit;

public class JavacTreeMaker {
    private final TreeMaker tm;
    private static final ConcurrentHashMap<FieldId<?>, Object> FIELD_CACHE = new ConcurrentHashMap();
    private static final Object REFLECTIVE_ITEM_NOT_FOUND = new Object[0];
    private static final Object REFLECTIVE_ITEM_MULTIPLE_FOUND = new Object[0];
    private static final ConcurrentHashMap<MethodId<?>, Object> METHOD_CACHE = new ConcurrentHashMap();
    private static final MethodId<JCTree.JCCompilationUnit> TopLevel = JavacTreeMaker.MethodId("TopLevel");
    private static final MethodId<JCTree.JCImport> Import = JavacTreeMaker.MethodId("Import");
    private static final MethodId<JCTree.JCClassDecl> ClassDef = JavacTreeMaker.MethodId("ClassDef");
    private static final MethodId<JCTree.JCMethodDecl> MethodDef = JavacTreeMaker.MethodId("MethodDef", JCTree.JCMethodDecl.class, JCTree.JCModifiers.class, Name.class, JCTree.JCExpression.class, List.class, List.class, List.class, JCTree.JCBlock.class, JCTree.JCExpression.class);
    private static final MethodId<JCTree.JCMethodDecl> MethodDefWithRecvParam = JavacTreeMaker.MethodId("MethodDef", JCTree.JCMethodDecl.class, JCTree.JCModifiers.class, Name.class, JCTree.JCExpression.class, List.class, JCTree.JCVariableDecl.class, List.class, List.class, JCTree.JCBlock.class, JCTree.JCExpression.class);
    private static final MethodId<JCTree.JCVariableDecl> VarDef = JavacTreeMaker.MethodId("VarDef");
    private static final MethodId<JCTree.JCVariableDecl> ReceiverVarDef = JavacTreeMaker.MethodId("ReceiverVarDef");
    private static final MethodId<JCTree.JCSkip> Skip = JavacTreeMaker.MethodId("Skip");
    private static final MethodId<JCTree.JCBlock> Block = JavacTreeMaker.MethodId("Block");
    private static final MethodId<JCTree.JCDoWhileLoop> DoLoop = JavacTreeMaker.MethodId("DoLoop");
    private static final MethodId<JCTree.JCWhileLoop> WhileLoop = JavacTreeMaker.MethodId("WhileLoop");
    private static final MethodId<JCTree.JCForLoop> ForLoop = JavacTreeMaker.MethodId("ForLoop");
    private static final MethodId<JCTree.JCEnhancedForLoop> ForeachLoop = JavacTreeMaker.MethodId("ForeachLoop");
    private static final MethodId<JCTree.JCLabeledStatement> Labelled = JavacTreeMaker.MethodId("Labelled");
    private static final MethodId<JCTree.JCSwitch> Switch = JavacTreeMaker.MethodId("Switch");
    private static final MethodId<JCTree.JCCase> Case11 = JavacTreeMaker.MethodId("Case", JCTree.JCCase.class, JCTree.JCExpression.class, List.class);
    private static final MethodId<JCTree> DefaultCaseLabel = JavacTreeMaker.MethodId("DefaultCaseLabel", JCTree.class, new Class[0]);
    private static final MethodId<JCTree> ConstantCaseLabel = JavacTreeMaker.MethodId("ConstantCaseLabel", JCTree.class, JCTree.JCExpression.class);
    private static final MethodId<JCTree.JCSynchronized> Synchronized = JavacTreeMaker.MethodId("Synchronized");
    private static final MethodId<JCTree.JCTry> Try = JavacTreeMaker.MethodId("Try", JCTree.JCTry.class, JCTree.JCBlock.class, List.class, JCTree.JCBlock.class);
    private static final MethodId<JCTree.JCTry> TryWithResources = JavacTreeMaker.MethodId("Try", JCTree.JCTry.class, List.class, JCTree.JCBlock.class, List.class, JCTree.JCBlock.class);
    private static final MethodId<JCTree.JCCatch> Catch = JavacTreeMaker.MethodId("Catch");
    private static final MethodId<JCTree.JCConditional> Conditional = JavacTreeMaker.MethodId("Conditional");
    private static final MethodId<JCTree.JCIf> If = JavacTreeMaker.MethodId("If");
    private static final MethodId<JCTree.JCExpressionStatement> Exec = JavacTreeMaker.MethodId("Exec");
    private static final MethodId<JCTree.JCBreak> Break11 = JavacTreeMaker.MethodId("Break", JCTree.JCBreak.class, Name.class);
    private static final MethodId<JCTree.JCBreak> Break12 = JavacTreeMaker.MethodId("Break", JCTree.JCBreak.class, JCTree.JCExpression.class);
    private static final MethodId<JCTree.JCContinue> Continue = JavacTreeMaker.MethodId("Continue");
    private static final MethodId<JCTree.JCReturn> Return = JavacTreeMaker.MethodId("Return");
    private static final MethodId<JCTree.JCThrow> Throw = JavacTreeMaker.MethodId("Throw");
    private static final MethodId<JCTree.JCAssert> Assert = JavacTreeMaker.MethodId("Assert");
    private static final MethodId<JCTree.JCMethodInvocation> Apply = JavacTreeMaker.MethodId("Apply");
    private static final MethodId<JCTree.JCNewClass> NewClass = JavacTreeMaker.MethodId("NewClass");
    private static final MethodId<JCTree.JCNewArray> NewArray = JavacTreeMaker.MethodId("NewArray");
    private static final MethodId<JCTree.JCParens> Parens = JavacTreeMaker.MethodId("Parens");
    private static final MethodId<JCTree.JCAssign> Assign = JavacTreeMaker.MethodId("Assign");
    private static final MethodId<JCTree.JCAssignOp> Assignop = JavacTreeMaker.MethodId("Assignop");
    private static final MethodId<JCTree.JCUnary> Unary = JavacTreeMaker.MethodId("Unary");
    private static final MethodId<JCTree.JCBinary> Binary = JavacTreeMaker.MethodId("Binary");
    private static final MethodId<JCTree.JCTypeCast> TypeCast = JavacTreeMaker.MethodId("TypeCast");
    private static final MethodId<JCTree.JCInstanceOf> TypeTest = JavacTreeMaker.MethodId("TypeTest");
    private static final MethodId<JCTree.JCArrayAccess> Indexed = JavacTreeMaker.MethodId("Indexed");
    private static final MethodId<JCTree.JCFieldAccess> Select = JavacTreeMaker.MethodId("Select");
    private static final MethodId<JCTree.JCIdent> Ident = JavacTreeMaker.MethodId("Ident", JCTree.JCIdent.class, Name.class);
    private static final MethodId<JCTree.JCLiteral> Literal = JavacTreeMaker.MethodId("Literal", JCTree.JCLiteral.class, TypeTag.class, Object.class);
    private static final MethodId<JCTree.JCPrimitiveTypeTree> TypeIdent = JavacTreeMaker.MethodId("TypeIdent");
    private static final MethodId<JCTree.JCArrayTypeTree> TypeArray = JavacTreeMaker.MethodId("TypeArray");
    private static final MethodId<JCTree.JCTypeApply> TypeApply = JavacTreeMaker.MethodId("TypeApply");
    private static final MethodId<JCTree.JCTypeParameter> TypeParameter = JavacTreeMaker.MethodId("TypeParameter", JCTree.JCTypeParameter.class, Name.class, List.class);
    private static final MethodId<JCTree.JCTypeParameter> TypeParameterWithAnnos = JavacTreeMaker.MethodId("TypeParameter", JCTree.JCTypeParameter.class, Name.class, List.class, List.class);
    private static final MethodId<JCTree.JCWildcard> Wildcard = JavacTreeMaker.MethodId("Wildcard");
    private static final MethodId<JCTree.TypeBoundKind> TypeBoundKind = JavacTreeMaker.MethodId("TypeBoundKind");
    private static final MethodId<JCTree.JCAnnotation> Annotation = JavacTreeMaker.MethodId("Annotation", JCTree.JCAnnotation.class, JCTree.class, List.class);
    private static final MethodId<JCTree.JCAnnotation> TypeAnnotation = JavacTreeMaker.MethodId("TypeAnnotation", JCTree.JCAnnotation.class, JCTree.class, List.class);
    private static final MethodId<JCTree.JCModifiers> ModifiersWithAnnotations = JavacTreeMaker.MethodId("Modifiers", JCTree.JCModifiers.class, Long.TYPE, List.class);
    private static final MethodId<JCTree.JCModifiers> Modifiers = JavacTreeMaker.MethodId("Modifiers", JCTree.JCModifiers.class, Long.TYPE);
    private static final MethodId<JCTree.JCErroneous> Erroneous = JavacTreeMaker.MethodId("Erroneous", JCTree.JCErroneous.class, new Class[0]);
    private static final MethodId<JCTree.JCErroneous> ErroneousWithErrs = JavacTreeMaker.MethodId("Erroneous", JCTree.JCErroneous.class, List.class);
    private static final MethodId<JCTree.LetExpr> LetExpr = JavacTreeMaker.MethodId("LetExpr", JCTree.LetExpr.class, List.class, JCTree.class);
    private static final MethodId<JCTree.JCClassDecl> AnonymousClassDef = JavacTreeMaker.MethodId("AnonymousClassDef");
    private static final MethodId<JCTree.LetExpr> LetExprSingle = JavacTreeMaker.MethodId("LetExpr", JCTree.LetExpr.class, JCTree.JCVariableDecl.class, JCTree.class);
    private static final MethodId<JCTree.JCIdent> IdentVarDecl = JavacTreeMaker.MethodId("Ident", JCTree.JCIdent.class, JCTree.JCVariableDecl.class);
    private static final MethodId<List<JCTree.JCExpression>> Idents = JavacTreeMaker.MethodId("Idents");
    private static final MethodId<JCTree.JCMethodInvocation> App2 = JavacTreeMaker.MethodId("App", JCTree.JCMethodInvocation.class, JCTree.JCExpression.class, List.class);
    private static final MethodId<JCTree.JCMethodInvocation> App1 = JavacTreeMaker.MethodId("App", JCTree.JCMethodInvocation.class, JCTree.JCExpression.class);
    private static final MethodId<List<JCTree.JCAnnotation>> Annotations = JavacTreeMaker.MethodId("Annotations");
    private static final MethodId<JCTree.JCLiteral> LiteralWithValue = JavacTreeMaker.MethodId("Literal", JCTree.JCLiteral.class, Object.class);
    private static final MethodId<JCTree.JCAnnotation> AnnotationWithAttributeOnly = JavacTreeMaker.MethodId("Annotation", JCTree.JCAnnotation.class, Attribute.class);
    private static final MethodId<JCTree.JCAnnotation> TypeAnnotationWithAttributeOnly = JavacTreeMaker.MethodId("TypeAnnotation", JCTree.JCAnnotation.class, Attribute.class);
    private static final MethodId<JCTree.JCExpression> AnnotatedType = JavacTreeMaker.MethodId("AnnotatedType", JCTree.JCExpression.class, List.class, JCTree.JCExpression.class);
    private static final MethodId<JCTree.JCStatement> Call = JavacTreeMaker.MethodId("Call");
    private static final MethodId<JCTree.JCExpression> Type = JavacTreeMaker.MethodId("Type");
    private static final FieldId<JCTree.JCVariableDecl> MethodDecl_recvParam = JavacTreeMaker.FieldId(JCTree.JCMethodDecl.class, "recvparam", JCTree.JCVariableDecl.class);

    public JavacTreeMaker(TreeMaker tm) {
        this.tm = tm;
    }

    public TreeMaker getUnderlyingTreeMaker() {
        return this.tm;
    }

    public JavacTreeMaker at(int pos) {
        this.tm.at(pos);
        return this;
    }

    static <J> MethodId<J> MethodId(Class<?> owner, String name, Class<J> returnType, Class<?> ... types) {
        return new MethodId<J>(owner, name, returnType, types);
    }

    static <J> MethodId<J> MethodId(String name, Class<J> returnType, Class<?> ... types) {
        return new MethodId<J>(TreeMaker.class, name, returnType, types);
    }

    static <J> MethodId<J> MethodId(String name) {
        Method[] methodArray = JavacTreeMaker.class.getDeclaredMethods();
        int n = methodArray.length;
        int n2 = 0;
        while (n2 < n) {
            Method m = methodArray[n2];
            if (m.getName().equals(name)) {
                Class<?> r = m.getReturnType();
                Class<?>[] p = m.getParameterTypes();
                return new MethodId(TreeMaker.class, name, r, p);
            }
            ++n2;
        }
        throw new InternalError("Not found: " + name);
    }

    static <J> FieldId<J> FieldId(Class<?> owner, String name, Class<J> fieldType) {
        return new FieldId<J>(owner, name, fieldType);
    }

    private static boolean has(FieldId<?> f) {
        Object field = FIELD_CACHE.get(f);
        if (field == REFLECTIVE_ITEM_NOT_FOUND) {
            return false;
        }
        if (field instanceof Field) {
            return true;
        }
        try {
            return JavacTreeMaker.getFromCache(f) != REFLECTIVE_ITEM_NOT_FOUND;
        }
        catch (IllegalStateException illegalStateException) {
            return false;
        }
    }

    private static <J> J get(Object owner, FieldId<J> f) {
        Field field = JavacTreeMaker.getFromCache(f);
        try {
            return (J)((FieldId)f).fieldType.cast(field.get(owner));
        }
        catch (IllegalAccessException e) {
            throw Javac.sneakyThrow(e);
        }
    }

    private static <J> void set(Object owner, FieldId<J> f, J val) {
        Field field = JavacTreeMaker.getFromCache(f);
        try {
            field.set(owner, val);
        }
        catch (IllegalAccessException e) {
            throw Javac.sneakyThrow(e);
        }
        catch (IllegalArgumentException e) {
            System.err.println("Type mismatch for: " + field);
            throw e;
        }
    }

    private static Field getFromCache(FieldId<?> f) {
        Object s = FIELD_CACHE.get(f);
        if (s == null) {
            s = JavacTreeMaker.addToCache(f);
        }
        if (s == REFLECTIVE_ITEM_NOT_FOUND) {
            throw new IllegalStateException("Lombok TreeMaker frontend issue: no match when looking for field: " + f);
        }
        return (Field)s;
    }

    private static Object addToCache(FieldId<?> f) {
        Field[] fieldArray = ((FieldId)f).owner.getDeclaredFields();
        int n = fieldArray.length;
        int n2 = 0;
        while (n2 < n) {
            Field field = fieldArray[n2];
            if (((FieldId)f).name.equals(field.getName())) {
                if (!Modifier.isPublic(field.getModifiers())) {
                    field.setAccessible(true);
                }
                return FIELD_CACHE.putIfAbsent(f, field);
            }
            ++n2;
        }
        return FIELD_CACHE.putIfAbsent(f, REFLECTIVE_ITEM_NOT_FOUND);
    }

    private boolean has(MethodId<?> m) {
        Object method = METHOD_CACHE.get(m);
        if (method == REFLECTIVE_ITEM_NOT_FOUND) {
            return false;
        }
        if (method instanceof Method) {
            return true;
        }
        try {
            return JavacTreeMaker.getFromCache(m) != REFLECTIVE_ITEM_NOT_FOUND;
        }
        catch (IllegalStateException illegalStateException) {
            return false;
        }
    }

    private <J> J invoke(MethodId<J> m, Object ... args) {
        return JavacTreeMaker.invokeAny(this.tm, m, args);
    }

    private static <J> J invokeAny(Object owner, MethodId<J> m, Object ... args) {
        Method method = JavacTreeMaker.getFromCache(m);
        try {
            if (((MethodId)m).returnType.isPrimitive()) {
                return (J)method.invoke(owner, args);
            }
            return (J)((MethodId)m).returnType.cast(method.invoke(owner, args));
        }
        catch (InvocationTargetException e) {
            throw Javac.sneakyThrow(e.getCause());
        }
        catch (IllegalAccessException e) {
            throw Javac.sneakyThrow(e);
        }
        catch (IllegalArgumentException e) {
            System.err.println("Type mismatch for: " + method);
            throw e;
        }
    }

    private static boolean tryResolve(MethodId<?> m) {
        Object s = METHOD_CACHE.get(m);
        if (s == null) {
            s = JavacTreeMaker.addToCache(m);
        }
        return s instanceof Method;
    }

    private static Method getFromCache(MethodId<?> m) {
        Object s = METHOD_CACHE.get(m);
        if (s == null) {
            s = JavacTreeMaker.addToCache(m);
        }
        if (s == REFLECTIVE_ITEM_MULTIPLE_FOUND) {
            throw new IllegalStateException("Lombok TreeMaker frontend issue: multiple matches when looking for method: " + m);
        }
        if (s == REFLECTIVE_ITEM_NOT_FOUND) {
            throw new IllegalStateException("Lombok TreeMaker frontend issue: no match when looking for method: " + m);
        }
        return (Method)s;
    }

    private static Object addToCache(MethodId<?> m) {
        Method found = null;
        Method[] methodArray = ((MethodId)m).owner.getDeclaredMethods();
        int n = methodArray.length;
        int n2 = 0;
        while (n2 < n) {
            block8: {
                Class<?>[] t;
                Method method = methodArray[n2];
                if (((MethodId)m).name.equals(method.getName()) && (t = method.getParameterTypes()).length == ((MethodId)m).paramTypes.length) {
                    int i = 0;
                    while (i < t.length) {
                        if (!(Symbol.class.isAssignableFrom(t[i]) || !SchroedingerType.class.isAssignableFrom(((MethodId)m).paramTypes[i]) && (t[i].isPrimitive() ? t[i] != ((MethodId)m).paramTypes[i] : !t[i].isAssignableFrom(((MethodId)m).paramTypes[i])))) {
                            ++i;
                            continue;
                        }
                        break block8;
                    }
                    if (found == null) {
                        found = method;
                    } else {
                        METHOD_CACHE.putIfAbsent(m, REFLECTIVE_ITEM_MULTIPLE_FOUND);
                        return REFLECTIVE_ITEM_MULTIPLE_FOUND;
                    }
                }
            }
            ++n2;
        }
        if (found == null) {
            METHOD_CACHE.putIfAbsent(m, REFLECTIVE_ITEM_NOT_FOUND);
            return REFLECTIVE_ITEM_NOT_FOUND;
        }
        Permit.setAccessible(found);
        Object marker = METHOD_CACHE.putIfAbsent(m, found);
        if (marker == null) {
            return found;
        }
        return marker;
    }

    public JCTree.JCCompilationUnit TopLevel(List<JCTree.JCAnnotation> packageAnnotations, JCTree.JCExpression pid, List<JCTree> defs) {
        return this.invoke(TopLevel, packageAnnotations, pid, defs);
    }

    public JCTree.JCImport Import(JCTree qualid, boolean staticImport) {
        return this.invoke(Import, qualid, staticImport);
    }

    public JCTree.JCClassDecl ClassDef(JCTree.JCModifiers mods, Name name, List<JCTree.JCTypeParameter> typarams, JCTree.JCExpression extending, List<JCTree.JCExpression> implementing, List<JCTree> defs) {
        return this.invoke(ClassDef, mods, name, typarams, extending, implementing, defs);
    }

    public JCTree.JCMethodDecl MethodDef(JCTree.JCModifiers mods, Name name, JCTree.JCExpression resType, List<JCTree.JCTypeParameter> typarams, List<JCTree.JCVariableDecl> params, List<JCTree.JCExpression> thrown, JCTree.JCBlock body, JCTree.JCExpression defaultValue) {
        return this.invoke(MethodDef, mods, name, resType, typarams, params, thrown, body, defaultValue);
    }

    public boolean hasMethodDefWithRecvParam() {
        return this.has(MethodDefWithRecvParam);
    }

    public JCTree.JCMethodDecl MethodDefWithRecvParam(JCTree.JCModifiers mods, Name name, JCTree.JCExpression resType, List<JCTree.JCTypeParameter> typarams, JCTree.JCVariableDecl recvparam, List<JCTree.JCVariableDecl> params, List<JCTree.JCExpression> thrown, JCTree.JCBlock body, JCTree.JCExpression defaultValue) {
        return this.invoke(MethodDefWithRecvParam, mods, name, resType, typarams, recvparam, params, thrown, body, defaultValue);
    }

    public JCTree.JCVariableDecl VarDef(JCTree.JCModifiers mods, Name name, JCTree.JCExpression vartype, JCTree.JCExpression init) {
        JCTree.JCVariableDecl varDef = this.invoke(VarDef, mods, name, vartype, init);
        if (varDef.vartype != null && varDef.vartype.pos == -1) {
            varDef.vartype.pos = 0;
        }
        return varDef;
    }

    public JCTree.JCVariableDecl ReceiverVarDef(JCTree.JCModifiers mods, JCTree.JCExpression name, JCTree.JCExpression vartype) {
        return this.invoke(ReceiverVarDef, mods, name, vartype);
    }

    public JCTree.JCSkip Skip() {
        return this.invoke(Skip, new Object[0]);
    }

    public JCTree.JCBlock Block(long flags, List<JCTree.JCStatement> stats) {
        return this.invoke(Block, flags, stats);
    }

    public JCTree.JCDoWhileLoop DoLoop(JCTree.JCStatement body, JCTree.JCExpression cond) {
        return this.invoke(DoLoop, body, cond);
    }

    public JCTree.JCWhileLoop WhileLoop(JCTree.JCExpression cond, JCTree.JCStatement body) {
        return this.invoke(WhileLoop, cond, body);
    }

    public JCTree.JCForLoop ForLoop(List<JCTree.JCStatement> init, JCTree.JCExpression cond, List<JCTree.JCExpressionStatement> step, JCTree.JCStatement body) {
        return this.invoke(ForLoop, init, cond, step, body);
    }

    public JCTree.JCEnhancedForLoop ForeachLoop(JCTree.JCVariableDecl var, JCTree.JCExpression expr, JCTree.JCStatement body) {
        return this.invoke(ForeachLoop, var, expr, body);
    }

    public JCTree.JCLabeledStatement Labelled(Name label, JCTree.JCStatement body) {
        return this.invoke(Labelled, label, body);
    }

    public JCTree.JCSwitch Switch(JCTree.JCExpression selector, List<JCTree.JCCase> cases) {
        return this.invoke(Switch, selector, cases);
    }

    static Class<?> classForName(Class<?> context, String name) {
        try {
            return context.getClassLoader().loadClass(name);
        }
        catch (ClassNotFoundException e) {
            NoClassDefFoundError x = new NoClassDefFoundError(e.getMessage());
            x.setStackTrace(e.getStackTrace());
            throw x;
        }
    }

    public JCTree.JCCase Case(JCTree.JCExpression pat, List<JCTree.JCStatement> stats) {
        if (JavacTreeMaker.tryResolve(Case11)) {
            return this.invoke(Case11, pat, stats);
        }
        List<Object> labels = pat == null ? (JavacTreeMaker.tryResolve(DefaultCaseLabel) ? List.of(this.DefaultCaseLabel()) : List.nil()) : (JavacTreeMaker.tryResolve(ConstantCaseLabel) ? List.of(this.ConstantCaseLabel(pat)) : List.of(pat));
        if (JavacTreeMaker.tryResolve(Case.Case12)) {
            return this.invoke(Case.Case12, Case.CASE_KIND_STATEMENT, labels, stats, null);
        }
        return this.invoke(Case.Case21, Case.CASE_KIND_STATEMENT, labels, null, stats, null);
    }

    public JCTree DefaultCaseLabel() {
        return this.invoke(DefaultCaseLabel, new Object[0]);
    }

    public JCTree ConstantCaseLabel(JCTree.JCExpression expr) {
        return this.invoke(ConstantCaseLabel, expr);
    }

    public JCTree.JCSynchronized Synchronized(JCTree.JCExpression lock, JCTree.JCBlock body) {
        return this.invoke(Synchronized, lock, body);
    }

    public JCTree.JCTry Try(JCTree.JCBlock body, List<JCTree.JCCatch> catchers, JCTree.JCBlock finalizer) {
        return this.invoke(Try, body, catchers, finalizer);
    }

    public JCTree.JCTry Try(List<JCTree> resources, JCTree.JCBlock body, List<JCTree.JCCatch> catchers, JCTree.JCBlock finalizer) {
        return this.invoke(TryWithResources, resources, body, catchers, finalizer);
    }

    public JCTree.JCCatch Catch(JCTree.JCVariableDecl param, JCTree.JCBlock body) {
        return this.invoke(Catch, param, body);
    }

    public JCTree.JCConditional Conditional(JCTree.JCExpression cond, JCTree.JCExpression thenpart, JCTree.JCExpression elsepart) {
        return this.invoke(Conditional, cond, thenpart, elsepart);
    }

    public JCTree.JCIf If(JCTree.JCExpression cond, JCTree.JCStatement thenpart, JCTree.JCStatement elsepart) {
        return this.invoke(If, cond, thenpart, elsepart);
    }

    public JCTree.JCExpressionStatement Exec(JCTree.JCExpression expr) {
        return this.invoke(Exec, expr);
    }

    public JCTree.JCBreak Break(Name label) {
        if (JavacTreeMaker.tryResolve(Break11)) {
            return this.invoke(Break11, label);
        }
        return this.invoke(Break12, label != null ? this.Ident(label) : null);
    }

    public JCTree.JCContinue Continue(Name label) {
        return this.invoke(Continue, label);
    }

    public JCTree.JCReturn Return(JCTree.JCExpression expr) {
        return this.invoke(Return, expr);
    }

    public JCTree.JCThrow Throw(JCTree.JCExpression expr) {
        return this.invoke(Throw, expr);
    }

    public JCTree.JCAssert Assert(JCTree.JCExpression cond, JCTree.JCExpression detail) {
        return this.invoke(Assert, cond, detail);
    }

    public JCTree.JCMethodInvocation Apply(List<JCTree.JCExpression> typeargs, JCTree.JCExpression fn, List<JCTree.JCExpression> args) {
        return this.invoke(Apply, typeargs, fn, args);
    }

    public JCTree.JCNewClass NewClass(JCTree.JCExpression encl, List<JCTree.JCExpression> typeargs, JCTree.JCExpression clazz, List<JCTree.JCExpression> args, JCTree.JCClassDecl def) {
        return this.invoke(NewClass, encl, typeargs, clazz, args, def);
    }

    public JCTree.JCNewArray NewArray(JCTree.JCExpression elemtype, List<JCTree.JCExpression> dims, List<JCTree.JCExpression> elems) {
        return this.invoke(NewArray, elemtype, dims, elems);
    }

    public JCTree.JCParens Parens(JCTree.JCExpression expr) {
        return this.invoke(Parens, expr);
    }

    public JCTree.JCAssign Assign(JCTree.JCExpression lhs, JCTree.JCExpression rhs) {
        return this.invoke(Assign, lhs, rhs);
    }

    public JCTree.JCAssignOp Assignop(TreeTag opcode, JCTree lhs, JCTree rhs) {
        return this.invoke(Assignop, opcode.value, lhs, rhs);
    }

    public JCTree.JCUnary Unary(TreeTag opcode, JCTree.JCExpression arg) {
        return this.invoke(Unary, opcode.value, arg);
    }

    public JCTree.JCBinary Binary(TreeTag opcode, JCTree.JCExpression lhs, JCTree.JCExpression rhs) {
        return this.invoke(Binary, opcode.value, lhs, rhs);
    }

    public JCTree.JCTypeCast TypeCast(JCTree expr, JCTree.JCExpression type) {
        return this.invoke(TypeCast, expr, type);
    }

    public JCTree.JCInstanceOf TypeTest(JCTree.JCExpression expr, JCTree clazz) {
        return this.invoke(TypeTest, expr, clazz);
    }

    public JCTree.JCArrayAccess Indexed(JCTree.JCExpression indexed, JCTree.JCExpression index) {
        return this.invoke(Indexed, indexed, index);
    }

    public JCTree.JCFieldAccess Select(JCTree.JCExpression selected, Name selector) {
        return this.invoke(Select, selected, selector);
    }

    public JCTree.JCIdent Ident(Name idname) {
        return this.invoke(Ident, idname);
    }

    public JCTree.JCLiteral Literal(TypeTag tag, Object value) {
        return this.invoke(Literal, tag.value, value);
    }

    public JCTree.JCPrimitiveTypeTree TypeIdent(TypeTag typetag) {
        return this.invoke(TypeIdent, typetag.value);
    }

    public JCTree.JCArrayTypeTree TypeArray(JCTree.JCExpression elemtype) {
        return this.invoke(TypeArray, elemtype);
    }

    public JCTree.JCTypeApply TypeApply(JCTree.JCExpression clazz, List<JCTree.JCExpression> arguments) {
        return this.invoke(TypeApply, clazz, arguments);
    }

    public JCTree.JCTypeParameter TypeParameter(Name name, List<JCTree.JCExpression> bounds) {
        return this.invoke(TypeParameter, name, bounds);
    }

    public JCTree.JCTypeParameter TypeParameter(Name name, List<JCTree.JCExpression> bounds, List<JCTree.JCAnnotation> annos) {
        return this.invoke(TypeParameterWithAnnos, name, bounds, annos);
    }

    public JCTree.JCWildcard Wildcard(JCTree.TypeBoundKind kind, JCTree type) {
        return this.invoke(Wildcard, kind, type);
    }

    public JCTree.TypeBoundKind TypeBoundKind(BoundKind kind) {
        return this.invoke(TypeBoundKind, new Object[]{kind});
    }

    public JCTree.JCAnnotation Annotation(JCTree annotationType, List<JCTree.JCExpression> args) {
        return this.invoke(Annotation, annotationType, args);
    }

    public JCTree.JCAnnotation TypeAnnotation(JCTree annotationType, List<JCTree.JCExpression> args) {
        return this.invoke(TypeAnnotation, annotationType, args);
    }

    public JCTree.JCModifiers Modifiers(long flags, List<JCTree.JCAnnotation> annotations) {
        return this.invoke(ModifiersWithAnnotations, flags, annotations);
    }

    public JCTree.JCModifiers Modifiers(long flags) {
        return this.invoke(Modifiers, flags);
    }

    public JCTree.JCErroneous Erroneous() {
        return this.invoke(Erroneous, new Object[0]);
    }

    public JCTree.JCErroneous Erroneous(List<? extends JCTree> errs) {
        return this.invoke(ErroneousWithErrs, errs);
    }

    public JCTree.LetExpr LetExpr(List<JCTree.JCVariableDecl> defs, JCTree expr) {
        return this.invoke(LetExpr, defs, expr);
    }

    public JCTree.JCClassDecl AnonymousClassDef(JCTree.JCModifiers mods, List<JCTree> defs) {
        return this.invoke(AnonymousClassDef, mods, defs);
    }

    public JCTree.LetExpr LetExpr(JCTree.JCVariableDecl def, JCTree expr) {
        return this.invoke(LetExprSingle, def, expr);
    }

    public JCTree.JCExpression Ident(JCTree.JCVariableDecl param) {
        return this.invoke(IdentVarDecl, param);
    }

    public List<JCTree.JCExpression> Idents(List<JCTree.JCVariableDecl> params) {
        return this.invoke(Idents, params);
    }

    public JCTree.JCMethodInvocation App(JCTree.JCExpression meth, List<JCTree.JCExpression> args) {
        return this.invoke(App2, meth, args);
    }

    public JCTree.JCMethodInvocation App(JCTree.JCExpression meth) {
        return this.invoke(App1, meth);
    }

    public List<JCTree.JCAnnotation> Annotations(List<Attribute.Compound> attributes) {
        return this.invoke(Annotations, attributes);
    }

    public JCTree.JCLiteral Literal(Object value) {
        return this.invoke(LiteralWithValue, value);
    }

    public JCTree.JCAnnotation Annotation(Attribute a) {
        return this.invoke(AnnotationWithAttributeOnly, a);
    }

    public JCTree.JCAnnotation TypeAnnotation(Attribute a) {
        return this.invoke(TypeAnnotationWithAttributeOnly, a);
    }

    public JCTree.JCExpression AnnotatedType(List<JCTree.JCAnnotation> annotations, JCTree.JCExpression underlyingType) {
        return this.invoke(AnnotatedType, annotations, underlyingType);
    }

    public JCTree.JCStatement Call(JCTree.JCExpression apply) {
        return this.invoke(Call, apply);
    }

    public JCTree.JCExpression Type(Type type) {
        return this.invoke(Type, type);
    }

    public boolean hasReceiverParameter() {
        return JavacTreeMaker.has(MethodDecl_recvParam);
    }

    public JCTree.JCVariableDecl getReceiverParameter(JCTree.JCMethodDecl method) {
        return JavacTreeMaker.get(method, MethodDecl_recvParam);
    }

    public void setReceiverParameter(JCTree.JCMethodDecl method, JCTree.JCVariableDecl param) {
        JavacTreeMaker.set(method, MethodDecl_recvParam, param);
    }

    public static class Case {
        private static final Class<?> CASE_KIND_CLASS = JavacTreeMaker.classForName(TreeMaker.class, "com.sun.source.tree.CaseTree$CaseKind");
        static final MethodId<JCTree.JCCase> Case12 = JavacTreeMaker.MethodId("Case", JCTree.JCCase.class, CASE_KIND_CLASS, List.class, List.class, JCTree.class);
        static final MethodId<JCTree.JCCase> Case21 = JavacTreeMaker.MethodId("Case", JCTree.JCCase.class, CASE_KIND_CLASS, List.class, JCTree.JCExpression.class, List.class, JCTree.class);
        static final Object CASE_KIND_STATEMENT = CASE_KIND_CLASS.getEnumConstants()[0];
    }

    private static final class FieldId<J> {
        private final Class<?> owner;
        private final String name;
        private final Class<J> fieldType;

        FieldId(Class<?> owner, String name, Class<J> fieldType) {
            this.owner = owner;
            this.name = name;
            this.fieldType = fieldType;
        }
    }

    private static final class MethodId<J> {
        private final Class<?> owner;
        private final String name;
        private final Class<J> returnType;
        private final Class<?>[] paramTypes;

        MethodId(Class<?> owner, String name, Class<J> returnType, Class<?> ... types) {
            this.owner = owner;
            this.name = name;
            this.paramTypes = types;
            this.returnType = returnType;
        }

        public String toString() {
            StringBuilder out = new StringBuilder();
            out.append(this.returnType.getName()).append(" ").append(this.owner.getName()).append(".").append(this.name).append("(");
            boolean f = true;
            Class<?>[] classArray = this.paramTypes;
            int n = this.paramTypes.length;
            int n2 = 0;
            while (n2 < n) {
                Class<?> p = classArray[n2];
                if (f) {
                    f = false;
                } else {
                    out.append(", ");
                }
                out.append(p.getName());
                ++n2;
            }
            return out.append(")").toString();
        }
    }

    private static class SchroedingerType {
        final Object value;
        private static Field NOSUCHFIELDEX_MARKER;

        static {
            try {
                NOSUCHFIELDEX_MARKER = Permit.getField(SchroedingerType.class, "NOSUCHFIELDEX_MARKER");
            }
            catch (NoSuchFieldException e) {
                throw Javac.sneakyThrow(e);
            }
        }

        private SchroedingerType(Object value) {
            this.value = value;
        }

        public int hashCode() {
            return this.value == null ? -1 : this.value.hashCode();
        }

        public boolean equals(Object obj) {
            if (obj instanceof SchroedingerType) {
                Object other = ((SchroedingerType)obj).value;
                return this.value == null ? other == null : this.value.equals(other);
            }
            return false;
        }

        static Object getFieldCached(ConcurrentMap<String, Object> cache, String className, String fieldName) {
            Object value = cache.get(fieldName);
            if (value != null) {
                return value;
            }
            try {
                value = Permit.getField(Class.forName(className), fieldName).get(null);
            }
            catch (NoSuchFieldException e) {
                throw Javac.sneakyThrow(e);
            }
            catch (IllegalAccessException e) {
                throw Javac.sneakyThrow(e);
            }
            catch (ClassNotFoundException e) {
                throw Javac.sneakyThrow(e);
            }
            cache.putIfAbsent(fieldName, value);
            return value;
        }

        static Object getFieldCached(ConcurrentMap<Class<?>, Field> cache, Object ref, String fieldName) throws NoSuchFieldException {
            Class<?> c = ref.getClass();
            Field field = (Field)cache.get(c);
            if (field == null) {
                try {
                    field = Permit.getField(c, fieldName);
                }
                catch (NoSuchFieldException e) {
                    cache.putIfAbsent(c, NOSUCHFIELDEX_MARKER);
                    throw Javac.sneakyThrow(e);
                }
                Permit.setAccessible(field);
                Field old = cache.putIfAbsent(c, field);
                if (old != null) {
                    field = old;
                }
            }
            if (field == NOSUCHFIELDEX_MARKER) {
                throw new NoSuchFieldException(fieldName);
            }
            try {
                return field.get(ref);
            }
            catch (IllegalAccessException e) {
                throw Javac.sneakyThrow(e);
            }
        }
    }

    public static class TreeTag
    extends SchroedingerType {
        private static final ConcurrentMap<String, Object> TREE_TAG_CACHE = new ConcurrentHashMap<String, Object>();
        private static final Field TAG_FIELD;
        private static final Method TAG_METHOD;
        private static final MethodId<Integer> OP_PREC;

        static {
            OP_PREC = JavacTreeMaker.MethodId(TreeInfo.class, "opPrec", Integer.TYPE, TreeTag.class);
            Method m = null;
            try {
                m = Permit.getMethod(JCTree.class, "getTag", new Class[0]);
            }
            catch (NoSuchMethodException noSuchMethodException) {}
            if (m != null) {
                TAG_FIELD = null;
                TAG_METHOD = m;
            } else {
                Field f = null;
                try {
                    f = Permit.getField(JCTree.class, "tag");
                }
                catch (NoSuchFieldException noSuchFieldException) {}
                TAG_FIELD = f;
                TAG_METHOD = null;
            }
        }

        private TreeTag(Object value) {
            super(value);
        }

        public static TreeTag treeTag(JCTree o) {
            try {
                if (TAG_METHOD != null) {
                    return new TreeTag(TAG_METHOD.invoke((Object)o, new Object[0]));
                }
                return new TreeTag(TAG_FIELD.get(o));
            }
            catch (InvocationTargetException e) {
                throw Javac.sneakyThrow(e.getCause());
            }
            catch (IllegalAccessException e) {
                throw Javac.sneakyThrow(e);
            }
        }

        public static TreeTag treeTag(String identifier) {
            return new TreeTag(TreeTag.getFieldCached(TREE_TAG_CACHE, Javac.getJavaCompilerVersion() < 8 ? "com.sun.tools.javac.tree.JCTree" : "com.sun.tools.javac.tree.JCTree$Tag", identifier));
        }

        public int getOperatorPrecedenceLevel() {
            return (Integer)JavacTreeMaker.invokeAny(null, TreeTag.OP_PREC, new Object[]{this.value});
        }

        public boolean isPrefixUnaryOp() {
            return Javac.CTC_NEG.equals(this) || Javac.CTC_POS.equals(this) || Javac.CTC_NOT.equals(this) || Javac.CTC_COMPL.equals(this) || Javac.CTC_PREDEC.equals(this) || Javac.CTC_PREINC.equals(this);
        }
    }

    public static class TypeTag
    extends SchroedingerType {
        private static final ConcurrentMap<String, Object> TYPE_TAG_CACHE = new ConcurrentHashMap<String, Object>();
        private static final ConcurrentMap<Class<?>, Field> FIELD_CACHE = new ConcurrentHashMap();
        private static final Method TYPE_TYPETAG_METHOD;

        static {
            Method m = null;
            try {
                m = Permit.getMethod(Type.class, "getTag", new Class[0]);
            }
            catch (NoSuchMethodException noSuchMethodException) {}
            TYPE_TYPETAG_METHOD = m;
        }

        private TypeTag(Object value) {
            super(value);
        }

        public static TypeTag typeTag(JCTree o) {
            try {
                return new TypeTag(TypeTag.getFieldCached(FIELD_CACHE, o, "typetag"));
            }
            catch (NoSuchFieldException e) {
                throw Javac.sneakyThrow(e);
            }
        }

        public static TypeTag typeTag(Type t) {
            if (t == null) {
                return Javac.CTC_VOID;
            }
            try {
                return new TypeTag(TypeTag.getFieldCached(FIELD_CACHE, t, "tag"));
            }
            catch (NoSuchFieldException noSuchFieldException) {
                if (TYPE_TYPETAG_METHOD == null) {
                    throw new IllegalStateException("Type " + t.getClass() + " has neither 'tag' nor getTag()");
                }
                try {
                    return new TypeTag(TYPE_TYPETAG_METHOD.invoke((Object)t, new Object[0]));
                }
                catch (IllegalAccessException ex) {
                    throw Javac.sneakyThrow(ex);
                }
                catch (InvocationTargetException ex) {
                    throw Javac.sneakyThrow(ex.getCause());
                }
            }
        }

        public static TypeTag typeTag(String identifier) {
            return new TypeTag(TypeTag.getFieldCached(TYPE_TAG_CACHE, Javac.getJavaCompilerVersion() < 8 ? "com.sun.tools.javac.code.TypeTags" : "com.sun.tools.javac.code.TypeTag", identifier));
        }
    }
}
