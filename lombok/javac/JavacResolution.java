package lombok.javac;

import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.comp.ArgumentAttr;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.comp.MemberEnter;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.lang.model.type.TypeKind;
import javax.tools.JavaFileObject;
import lombok.core.debug.AssertionLogger;
import lombok.javac.CompilerMessageSuppressor;
import lombok.javac.Javac;
import lombok.javac.JavacAST;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.TreeMirrorMaker;
import lombok.permit.Permit;

public class JavacResolution {
    private final Context context;
    private final Attr attr;
    private final CompilerMessageSuppressor messageSuppressor;
    private static final Method isLocal;
    private static Field memberEnterDotEnv;

    static {
        Method local = Permit.permissiveGetMethod(Symbol.TypeSymbol.class, "isLocal", new Class[0]);
        if (local == null) {
            local = Permit.permissiveGetMethod(Symbol.TypeSymbol.class, "isDirectlyOrIndirectlyLocal", new Class[0]);
        }
        isLocal = local;
    }

    public JavacResolution(Context context) {
        this.context = context;
        this.attr = Attr.instance(context);
        this.messageSuppressor = new CompilerMessageSuppressor(context);
    }

    public Map<JCTree, JCTree> resolveMethodMember(JavacNode node) {
        ArrayDeque<JCTree> stack = new ArrayDeque<JCTree>();
        JavacNode n = node;
        while (n != null) {
            stack.push((JCTree)n.get());
            n = (JavacNode)n.up();
        }
        this.messageSuppressor.disableLoggers();
        try {
            Map<JCTree, JCTree> map;
            EnvFinder finder = new EnvFinder(node.getContext());
            while (!stack.isEmpty()) {
                ((JCTree)stack.pop()).accept(finder);
            }
            TreeMirrorMaker mirrorMaker = new TreeMirrorMaker(node.getTreeMaker(), node.getContext());
            JCTree copy = mirrorMaker.copy(finder.copyAt());
            Log log = Log.instance(node.getContext());
            JavaFileObject oldFileObject = log.useSource(((JCTree.JCCompilationUnit)((JavacNode)node.top()).get()).getSourceFile());
            try {
                this.memberEnterAndAttribute(copy, finder.get(), node.getContext());
                map = mirrorMaker.getOriginalToCopyMap();
            }
            catch (Throwable throwable) {
                log.useSource(oldFileObject);
                throw throwable;
            }
            log.useSource(oldFileObject);
            return map;
        }
        finally {
            this.messageSuppressor.enableLoggers();
        }
    }

    private static Field getMemberEnterDotEnv() {
        if (memberEnterDotEnv != null) {
            return memberEnterDotEnv;
        }
        try {
            memberEnterDotEnv = Permit.getField(MemberEnter.class, "env");
            return memberEnterDotEnv;
        }
        catch (NoSuchFieldException noSuchFieldException) {
            return null;
        }
    }

    private static Env<AttrContext> getEnvOfMemberEnter(MemberEnter memberEnter) {
        Field f = JavacResolution.getMemberEnterDotEnv();
        try {
            return (Env)f.get(memberEnter);
        }
        catch (Exception exception) {
            return null;
        }
    }

    private static void setEnvOfMemberEnter(MemberEnter memberEnter, Env<AttrContext> env) {
        Field f = JavacResolution.getMemberEnterDotEnv();
        try {
            f.set(memberEnter, env);
        }
        catch (Exception exception) {
            return;
        }
    }

    private void memberEnterAndAttribute(JCTree copy, Env<AttrContext> env, Context context) {
        block5: {
            MemberEnter memberEnter = MemberEnter.instance(context);
            Env<AttrContext> oldEnv = JavacResolution.getEnvOfMemberEnter(memberEnter);
            JavacResolution.setEnvOfMemberEnter(memberEnter, env);
            try {
                try {
                    copy.accept(memberEnter);
                }
                catch (Exception ignore) {
                    AssertionLogger.assertLog("member enter failed.", ignore);
                    JavacResolution.setEnvOfMemberEnter(memberEnter, oldEnv);
                    break block5;
                }
            }
            catch (Throwable throwable) {
                JavacResolution.setEnvOfMemberEnter(memberEnter, oldEnv);
                throw throwable;
            }
            JavacResolution.setEnvOfMemberEnter(memberEnter, oldEnv);
        }
        this.attrib(copy, env);
    }

    public void resolveClassMember(JavacNode node) {
        ArrayDeque<JCTree> stack = new ArrayDeque<JCTree>();
        JavacNode n = node;
        while (n != null) {
            stack.push((JCTree)n.get());
            n = (JavacNode)n.up();
        }
        this.messageSuppressor.disableLoggers();
        try {
            EnvFinder finder = new EnvFinder(node.getContext());
            while (!stack.isEmpty()) {
                ((JCTree)stack.pop()).accept(finder);
            }
            this.attrib((JCTree)node.get(), finder.get());
        }
        finally {
            this.messageSuppressor.enableLoggers();
        }
    }

    private void attrib(JCTree tree, Env<AttrContext> env) {
        block10: {
            try {
                if (env.enclClass.type == null && env.enclClass.sym != null) {
                    env.enclClass.type = env.enclClass.sym.type;
                }
                if (env.enclClass.type == null) {
                    env.enclClass.type = Type.noType;
                }
            }
            catch (Throwable throwable) {}
            Map<?, ?> cache = null;
            try {
                cache = ArgumentAttrReflect.enableTempCache(this.context);
                if (tree instanceof JCTree.JCBlock) {
                    this.attr.attribStat(tree, env);
                    break block10;
                }
                if (tree instanceof JCTree.JCMethodDecl) {
                    this.attr.attribStat(((JCTree.JCMethodDecl)tree).body, env);
                    break block10;
                }
                if (tree instanceof JCTree.JCVariableDecl) {
                    this.attr.attribStat(tree, env);
                    break block10;
                }
                throw new IllegalStateException("Called with something that isn't a block, method decl, or variable decl");
            }
            finally {
                ArgumentAttrReflect.restoreCache(cache, this.context);
            }
        }
    }

    public static Type ifTypeIsIterableToComponent(Type type, JavacAST ast) {
        if (type == null) {
            return null;
        }
        Types types = Types.instance(ast.getContext());
        Symtab syms = Symtab.instance(ast.getContext());
        Type boundType = ReflectiveAccess.Types_upperBound(types, type);
        Type elemTypeIfArray = types.elemtype(boundType);
        if (elemTypeIfArray != null) {
            return elemTypeIfArray;
        }
        Type base = types.asSuper(boundType, syms.iterableType.tsym);
        if (base == null) {
            return syms.objectType;
        }
        List<Type> iterableParams = base.allparams();
        return iterableParams.isEmpty() ? syms.objectType : ReflectiveAccess.Types_upperBound(types, (Type)iterableParams.head);
    }

    public static JCTree.JCExpression typeToJCTree(Type type, JavacAST ast, boolean allowVoid) throws TypeNotConvertibleException {
        return JavacResolution.typeToJCTree(type, ast, false, allowVoid, false);
    }

    public static JCTree.JCExpression createJavaLangObject(JavacAST ast) {
        JavacTreeMaker maker = ast.getTreeMaker();
        JCTree.JCExpression out = maker.Ident(ast.toName("java"));
        out = maker.Select(out, ast.toName("lang"));
        out = maker.Select(out, ast.toName("Object"));
        return out;
    }

    private static JCTree.JCExpression typeToJCTree(Type type, JavacAST ast, boolean allowCompound, boolean allowVoid, boolean allowCapture) throws TypeNotConvertibleException {
        int dims = 0;
        Type type0 = type;
        while (type0 instanceof Type.ArrayType) {
            ++dims;
            type0 = ((Type.ArrayType)type0).elemtype;
        }
        JCTree.JCExpression result = JavacResolution.typeToJCTree0(type0, ast, allowCompound, allowVoid, allowCapture);
        while (dims > 0) {
            result = ast.getTreeMaker().TypeArray(result);
            --dims;
        }
        return result;
    }

    private static Iterable<? extends Type> concat(final Type t, final Collection<? extends Type> ts) {
        if (t == null) {
            return ts;
        }
        return new Iterable<Type>(){

            @Override
            public Iterator<Type> iterator() {
                return new Iterator<Type>(ts){
                    private boolean first = true;
                    private Iterator<? extends Type> wrap;
                    {
                        this.wrap = collection == null ? null : collection.iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        if (this.first) {
                            return true;
                        }
                        if (this.wrap == null) {
                            return false;
                        }
                        return this.wrap.hasNext();
                    }

                    @Override
                    public Type next() {
                        if (this.first) {
                            this.first = false;
                            return t;
                        }
                        if (this.wrap == null) {
                            throw new NoSuchElementException();
                        }
                        return this.wrap.next();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    private static int compare(Name a, Name b) {
        return a.compareTo(b);
    }

    private static boolean isLocalType(Symbol.TypeSymbol symbol) {
        try {
            return (Boolean)Permit.invoke(isLocal, symbol, new Object[0]);
        }
        catch (Exception exception) {
            return false;
        }
    }

    private static JCTree.JCExpression typeToJCTree0(Type type, JavacAST ast, boolean allowCompound, boolean allowVoid, boolean allowCapture) throws TypeNotConvertibleException {
        String qName;
        JavacTreeMaker maker = ast.getTreeMaker();
        if (Javac.CTC_BOT.equals(JavacTreeMaker.TypeTag.typeTag(type))) {
            return JavacResolution.createJavaLangObject(ast);
        }
        if (Javac.CTC_VOID.equals(JavacTreeMaker.TypeTag.typeTag(type))) {
            return allowVoid ? JavacResolution.primitiveToJCTree(type.getKind(), maker) : JavacResolution.createJavaLangObject(ast);
        }
        if (type.isPrimitive()) {
            return JavacResolution.primitiveToJCTree(type.getKind(), maker);
        }
        if (type.isErroneous()) {
            throw new TypeNotConvertibleException("Type cannot be resolved");
        }
        Symbol.TypeSymbol symbol = type.asElement();
        List<Type> generics = type.getTypeArguments();
        JCTree.JCExpression replacement = null;
        if (symbol == null) {
            throw new TypeNotConvertibleException("Null or compound type");
        }
        if (symbol.name.length() == 0) {
            if (type instanceof Type.ClassType) {
                Type winner = null;
                int winLevel = 0;
                Type supertype = ((Type.ClassType)type).supertype_field;
                List<Type> ifaces = ((Type.ClassType)type).interfaces_field;
                for (Type type2 : JavacResolution.concat(supertype, ifaces)) {
                    int level = 0;
                    level = type2 instanceof Type.ArrayType ? 100 : (type2 instanceof Type.TypeVar ? 20 : (type2 instanceof Type.WildcardType ? 15 : (type2.isInterface() ? 10 : (JavacResolution.isObject(type2) ? 1 : (type2 instanceof Type.ClassType ? 50 : 5)))));
                    if (winLevel > level) continue;
                    if (winLevel < level) {
                        winner = type2;
                        winLevel = level;
                        continue;
                    }
                    if (JavacResolution.compare(winner.tsym.getQualifiedName(), type2.tsym.getQualifiedName()) >= 0) continue;
                    winner = type2;
                }
                if (winner == null) {
                    return JavacResolution.createJavaLangObject(ast);
                }
                return JavacResolution.typeToJCTree(winner, ast, allowCompound, allowVoid, allowCapture);
            }
            throw new TypeNotConvertibleException("Anonymous inner class");
        }
        if (type instanceof Type.WildcardType || type instanceof Type.CapturedType) {
            Type lower;
            Type upper;
            if (type instanceof Type.WildcardType) {
                upper = ((Type.WildcardType)type).getExtendsBound();
                lower = ((Type.WildcardType)type).getSuperBound();
            } else {
                lower = type.getLowerBound();
                upper = type.getUpperBound();
                if (allowCapture) {
                    BoundKind bk = ((Type.CapturedType)type).wildcard.kind;
                    if (bk == BoundKind.UNBOUND) {
                        return maker.Wildcard(maker.TypeBoundKind(BoundKind.UNBOUND), null);
                    }
                    if (bk == BoundKind.EXTENDS) {
                        lower = null;
                        upper = ((Type.CapturedType)type).wildcard.type;
                    } else if (bk == BoundKind.SUPER) {
                        lower = ((Type.CapturedType)type).wildcard.type;
                        upper = null;
                    }
                }
            }
            if (allowCompound) {
                JCTree.JCExpression bound;
                if (lower == null || Javac.CTC_BOT.equals(JavacTreeMaker.TypeTag.typeTag(lower))) {
                    if (upper == null || upper.toString().equals("java.lang.Object")) {
                        return maker.Wildcard(maker.TypeBoundKind(BoundKind.UNBOUND), null);
                    }
                    if (upper.getTypeArguments().contains(type)) {
                        return maker.Wildcard(maker.TypeBoundKind(BoundKind.UNBOUND), null);
                    }
                    bound = JavacResolution.typeToJCTree(upper, ast, false, false, true);
                    if (bound instanceof JCTree.JCWildcard) {
                        return maker.Wildcard(maker.TypeBoundKind(BoundKind.UNBOUND), null);
                    }
                    return maker.Wildcard(maker.TypeBoundKind(BoundKind.EXTENDS), bound);
                }
                bound = JavacResolution.typeToJCTree(lower, ast, false, false, true);
                if (bound instanceof JCTree.JCWildcard) {
                    return maker.Wildcard(maker.TypeBoundKind(BoundKind.UNBOUND), null);
                }
                return maker.Wildcard(maker.TypeBoundKind(BoundKind.SUPER), bound);
            }
            if (upper != null) {
                if (upper.getTypeArguments().contains(type)) {
                    return maker.Wildcard(maker.TypeBoundKind(BoundKind.UNBOUND), null);
                }
                return JavacResolution.typeToJCTree(upper, ast, allowCompound, allowVoid, true);
            }
            return JavacResolution.createJavaLangObject(ast);
        }
        if (JavacResolution.isLocalType(symbol)) {
            qName = ((Name)symbol.getSimpleName()).toString();
        } else if (symbol.type != null && symbol.type.getEnclosingType() != null && JavacTreeMaker.TypeTag.typeTag(symbol.type.getEnclosingType()).equals(JavacTreeMaker.TypeTag.typeTag("CLASS"))) {
            replacement = JavacResolution.typeToJCTree0(type.getEnclosingType(), ast, false, false, false);
            qName = ((Name)symbol.getSimpleName()).toString();
        } else {
            qName = symbol.getQualifiedName().toString();
        }
        if (qName.isEmpty()) {
            throw new TypeNotConvertibleException("unknown type");
        }
        if (qName.startsWith("<")) {
            throw new TypeNotConvertibleException(qName);
        }
        String[] baseNames = qName.split("\\.");
        int i = 0;
        if (replacement == null) {
            replacement = maker.Ident(ast.toName(baseNames[0]));
            i = 1;
        }
        while (i < baseNames.length) {
            replacement = maker.Select(replacement, ast.toName(baseNames[i]));
            ++i;
        }
        return JavacResolution.genericsToJCTreeNodes(generics, ast, replacement);
    }

    private static boolean isObject(Type supertype) {
        return ((Object)supertype.tsym).toString().equals("java.lang.Object");
    }

    private static JCTree.JCExpression genericsToJCTreeNodes(List<Type> generics, JavacAST ast, JCTree.JCExpression rawTypeNode) throws TypeNotConvertibleException {
        if (generics != null && !generics.isEmpty()) {
            ListBuffer<JCTree.JCExpression> args = new ListBuffer<JCTree.JCExpression>();
            for (Type t : generics) {
                args.append(JavacResolution.typeToJCTree(t, ast, true, false, true));
            }
            return ast.getTreeMaker().TypeApply(rawTypeNode, args.toList());
        }
        return rawTypeNode;
    }

    private static JCTree.JCExpression primitiveToJCTree(TypeKind kind, JavacTreeMaker maker) throws TypeNotConvertibleException {
        switch (kind) {
            case BYTE: {
                return maker.TypeIdent(Javac.CTC_BYTE);
            }
            case CHAR: {
                return maker.TypeIdent(Javac.CTC_CHAR);
            }
            case SHORT: {
                return maker.TypeIdent(Javac.CTC_SHORT);
            }
            case INT: {
                return maker.TypeIdent(Javac.CTC_INT);
            }
            case LONG: {
                return maker.TypeIdent(Javac.CTC_LONG);
            }
            case FLOAT: {
                return maker.TypeIdent(Javac.CTC_FLOAT);
            }
            case DOUBLE: {
                return maker.TypeIdent(Javac.CTC_DOUBLE);
            }
            case BOOLEAN: {
                return maker.TypeIdent(Javac.CTC_BOOLEAN);
            }
            case VOID: {
                return maker.TypeIdent(Javac.CTC_VOID);
            }
        }
        throw new TypeNotConvertibleException("Nulltype");
    }

    public static boolean platformHasTargetTyping() {
        return Javac.getJavaCompilerVersion() >= 8;
    }

    private static class ArgumentAttrReflect {
        private static Field ARGUMENT_TYPE_CACHE;

        static {
            if (Javac.getJavaCompilerVersion() >= 9) {
                try {
                    ARGUMENT_TYPE_CACHE = Permit.getField(ArgumentAttr.class, "argumentTypeCache");
                }
                catch (Exception exception) {}
            }
        }

        private ArgumentAttrReflect() {
        }

        public static Map<?, ?> enableTempCache(Context context) {
            if (ARGUMENT_TYPE_CACHE == null) {
                return null;
            }
            ArgumentAttr argumentAttr = ArgumentAttr.instance(context);
            try {
                Map cache = (Map)Permit.get(ARGUMENT_TYPE_CACHE, argumentAttr);
                Permit.set(ARGUMENT_TYPE_CACHE, argumentAttr, new LinkedHashMap(cache));
                return cache;
            }
            catch (Exception exception) {
                return null;
            }
        }

        public static void restoreCache(Map<?, ?> cache, Context context) {
            if (ARGUMENT_TYPE_CACHE == null) {
                return;
            }
            ArgumentAttr argumentAttr = ArgumentAttr.instance(context);
            try {
                Permit.set(ARGUMENT_TYPE_CACHE, argumentAttr, cache);
            }
            catch (Exception exception) {}
        }
    }

    private static final class EnvFinder
    extends JCTree.Visitor {
        private Env<AttrContext> env = null;
        private Enter enter;
        private MemberEnter memberEnter;
        private JCTree copyAt = null;

        EnvFinder(Context context) {
            this.enter = Enter.instance(context);
            this.memberEnter = MemberEnter.instance(context);
        }

        Env<AttrContext> get() {
            return this.env;
        }

        JCTree copyAt() {
            return this.copyAt;
        }

        @Override
        public void visitTopLevel(JCTree.JCCompilationUnit tree) {
            if (this.copyAt != null) {
                return;
            }
            this.env = this.enter.getTopLevelEnv(tree);
        }

        @Override
        public void visitClassDef(JCTree.JCClassDecl tree) {
            if (this.copyAt != null) {
                return;
            }
            if (tree.sym != null) {
                this.env = this.enter.getClassEnv(tree.sym);
            }
        }

        @Override
        public void visitMethodDef(JCTree.JCMethodDecl tree) {
            if (this.copyAt != null) {
                return;
            }
            this.env = this.memberEnter.getMethodEnv(tree, this.env);
            this.copyAt = tree;
        }

        @Override
        public void visitVarDef(JCTree.JCVariableDecl tree) {
            if (this.copyAt != null) {
                return;
            }
            this.env = this.memberEnter.getInitEnv(tree, this.env);
            this.copyAt = tree;
        }

        @Override
        public void visitBlock(JCTree.JCBlock tree) {
            if (this.copyAt != null) {
                return;
            }
            this.copyAt = tree;
        }

        @Override
        public void visitTree(JCTree that) {
        }
    }

    private static class ReflectiveAccess {
        private static Method UPPER_BOUND;
        private static Throwable initError;

        static {
            Method upperBound = null;
            try {
                upperBound = Permit.getMethod(Types.class, "upperBound", Type.class);
            }
            catch (Throwable e) {
                initError = e;
            }
            if (upperBound == null) {
                try {
                    upperBound = Permit.getMethod(Types.class, "wildUpperBound", Type.class);
                }
                catch (Throwable e) {
                    initError = e;
                }
            }
            UPPER_BOUND = upperBound;
        }

        private ReflectiveAccess() {
        }

        public static Type Types_upperBound(Types types, Type type) {
            return (Type)Permit.invokeSneaky(initError, UPPER_BOUND, (Object)types, type);
        }
    }

    public static class TypeNotConvertibleException
    extends Exception {
        public TypeNotConvertibleException(String msg) {
            super(msg);
        }
    }
}
