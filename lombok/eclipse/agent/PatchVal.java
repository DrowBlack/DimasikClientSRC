package lombok.eclipse.agent;

import java.lang.reflect.Field;
import lombok.Lombok;
import lombok.eclipse.Eclipse;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.permit.Permit;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.FunctionalExpression;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.ImportBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;

public class PatchVal {
    public static boolean matches(String key, char[] array) {
        if (array == null || key.length() != array.length) {
            return false;
        }
        int i = 0;
        while (i < array.length) {
            if (key.charAt(i) != array[i]) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static boolean couldBe(ImportBinding[] imports, String key, TypeReference ref) {
        String[] keyParts = key.split("\\.");
        if (ref instanceof SingleTypeReference) {
            char[] token = ((SingleTypeReference)ref).token;
            if (!PatchVal.matches(keyParts[keyParts.length - 1], token)) {
                return false;
            }
            if (imports == null) {
                return true;
            }
            ImportBinding[] importBindingArray = imports;
            int n = imports.length;
            int n2 = 0;
            while (n2 < n) {
                block12: {
                    char[][] t;
                    boolean star;
                    int len;
                    ImportBinding ib = importBindingArray[n2];
                    ImportReference ir = ib.reference;
                    if (ir != null && !ir.isStatic() && (len = keyParts.length - ((star = (ir.bits & 0x20000) != 0) ? 1 : 0)) == (t = ir.tokens).length) {
                        int i = 0;
                        while (i < len) {
                            if (keyParts[i].length() != t[i].length) break block12;
                            int j = 0;
                            while (j < t[i].length) {
                                if (keyParts[i].charAt(j) == t[i][j]) {
                                    ++j;
                                    continue;
                                }
                                break block12;
                            }
                            ++i;
                        }
                        return true;
                    }
                }
                ++n2;
            }
            return false;
        }
        if (ref instanceof QualifiedTypeReference) {
            char[][] tokens = ((QualifiedTypeReference)ref).tokens;
            if (keyParts.length != tokens.length) {
                return false;
            }
            int i = 0;
            while (i < tokens.length) {
                String part = keyParts[i];
                char[] token = tokens[i];
                if (!PatchVal.matches(part, token)) {
                    return false;
                }
                ++i;
            }
            return true;
        }
        return false;
    }

    public static boolean couldBe(ImportReference[] imports, String key, TypeReference ref) {
        String[] keyParts = key.split("\\.");
        if (ref instanceof SingleTypeReference) {
            char[] token = ((SingleTypeReference)ref).token;
            if (!PatchVal.matches(keyParts[keyParts.length - 1], token)) {
                return false;
            }
            if (imports == null) {
                return true;
            }
            ImportReference[] importReferenceArray = imports;
            int n = imports.length;
            int n2 = 0;
            while (n2 < n) {
                block12: {
                    char[][] t;
                    boolean star;
                    int len;
                    ImportReference ir = importReferenceArray[n2];
                    if (!ir.isStatic() && (len = keyParts.length - ((star = (ir.bits & 0x20000) != 0) ? 1 : 0)) == (t = ir.tokens).length) {
                        int i = 0;
                        while (i < len) {
                            if (keyParts[i].length() != t[i].length) break block12;
                            int j = 0;
                            while (j < t[i].length) {
                                if (keyParts[i].charAt(j) == t[i][j]) {
                                    ++j;
                                    continue;
                                }
                                break block12;
                            }
                            ++i;
                        }
                        return true;
                    }
                }
                ++n2;
            }
            return false;
        }
        if (ref instanceof QualifiedTypeReference) {
            char[][] tokens = ((QualifiedTypeReference)ref).tokens;
            if (keyParts.length != tokens.length) {
                return false;
            }
            int i = 0;
            while (i < tokens.length) {
                String part = keyParts[i];
                char[] token = tokens[i];
                if (!PatchVal.matches(part, token)) {
                    return false;
                }
                ++i;
            }
            return true;
        }
        return false;
    }

    private static boolean is(TypeReference ref, BlockScope scope, String key) {
        Scope s = scope.parent;
        while (s != null && !(s instanceof CompilationUnitScope)) {
            Scope ns = s.parent;
            Scope scope2 = s = ns == s ? null : ns;
        }
        ImportBinding[] imports = null;
        if (s instanceof CompilationUnitScope) {
            imports = ((CompilationUnitScope)s).imports;
        }
        if (!PatchVal.couldBe(imports, key, ref)) {
            return false;
        }
        TypeBinding resolvedType = ref.resolvedType;
        if (resolvedType == null) {
            resolvedType = ref.resolveType(scope, false);
        }
        if (resolvedType == null) {
            return false;
        }
        char[] pkg = resolvedType.qualifiedPackageName();
        char[] nm = resolvedType.qualifiedSourceName();
        int pkgFullLength = pkg.length > 0 ? pkg.length + 1 : 0;
        char[] fullName = new char[pkgFullLength + nm.length];
        if (pkg.length > 0) {
            System.arraycopy(pkg, 0, fullName, 0, pkg.length);
            fullName[pkg.length] = 46;
        }
        System.arraycopy(nm, 0, fullName, pkgFullLength, nm.length);
        return PatchVal.matches(key, fullName);
    }

    public static boolean handleValForLocalDeclaration(LocalDeclaration local, BlockScope scope) {
        Expression init;
        if (local == null || !LocalDeclaration.class.equals(local.getClass())) {
            return false;
        }
        boolean decomponent = false;
        boolean val = PatchVal.isVal(local, scope);
        boolean var = PatchVal.isVar(local, scope);
        if (!val && !var) {
            return false;
        }
        if (val) {
            StackTraceElement[] st = new Throwable().getStackTrace();
            int i = 0;
            while (i < st.length - 2 && i < 10) {
                if (st[i].getClassName().equals("lombok.launch.PatchFixesHider$Val")) {
                    boolean valInForStatement;
                    boolean bl = valInForStatement = st[i + 1].getClassName().equals("org.eclipse.jdt.internal.compiler.ast.LocalDeclaration") && st[i + 2].getClassName().equals("org.eclipse.jdt.internal.compiler.ast.ForStatement");
                    if (!valInForStatement) break;
                    return false;
                }
                ++i;
            }
        }
        if ((init = local.initialization) == null && Reflection.initCopyField != null) {
            try {
                init = (Expression)Reflection.initCopyField.get(local);
            }
            catch (Exception exception) {}
        }
        if (init == null && Reflection.iterableCopyField != null) {
            try {
                init = (Expression)Reflection.iterableCopyField.get(local);
                decomponent = true;
            }
            catch (Exception exception) {}
        }
        SingleTypeReference replacement = null;
        if (PatchVal.hasNativeVarSupport((Scope)scope) && val) {
            replacement = new SingleTypeReference("var".toCharArray(), Eclipse.pos((ASTNode)local.type));
            local.initialization = init;
            init = null;
        }
        if (init != null) {
            if (init.getClass().getName().equals("org.eclipse.jdt.internal.compiler.ast.LambdaExpression")) {
                return false;
            }
            TypeBinding resolved = null;
            try {
                resolved = decomponent ? PatchVal.getForEachComponentType(init, scope) : PatchVal.resolveForExpression(init, scope);
            }
            catch (NullPointerException nullPointerException) {
                resolved = null;
            }
            if (resolved == null && init instanceof ConditionalExpression) {
                ConditionalExpression cexp = (ConditionalExpression)init;
                Expression ifTrue = cexp.valueIfTrue;
                Expression ifFalse = cexp.valueIfFalse;
                TypeBinding ifTrueResolvedType = ifTrue.resolvedType;
                CompilationResult compilationResult = scope.referenceCompilationUnit().compilationResult;
                CategorizedProblem[] problems = compilationResult.problems;
                CategorizedProblem lastProblem = problems[compilationResult.problemCount - 1];
                if (ifTrueResolvedType != null && ifFalse.resolvedType == null && lastProblem.getCategoryID() == 40) {
                    int problemCount = compilationResult.problemCount;
                    int i = 0;
                    while (i < problemCount) {
                        if (problems[i] == lastProblem) {
                            problems[i] = null;
                            if (i + 1 >= problemCount) break;
                            System.arraycopy(problems, i + 1, problems, i, problemCount - i + 1);
                            break;
                        }
                        ++i;
                    }
                    compilationResult.removeProblem(lastProblem);
                    if (!compilationResult.hasErrors()) {
                        PatchVal.clearIgnoreFurtherInvestigationField(scope.referenceContext());
                        PatchVal.setValue(PatchVal.getField(CompilationResult.class, "hasMandatoryErrors"), compilationResult, false);
                    }
                    if (ifFalse instanceof FunctionalExpression) {
                        FunctionalExpression functionalExpression = (FunctionalExpression)ifFalse;
                        functionalExpression.setExpectedType(ifTrueResolvedType);
                    }
                    if (ifFalse.resolvedType == null) {
                        PatchVal.resolveForExpression(ifFalse, scope);
                    }
                    resolved = ifTrueResolvedType;
                }
            }
            if (resolved != null) {
                try {
                    replacement = EclipseHandlerUtil.makeType(resolved, (ASTNode)local.type, false);
                    if (!decomponent) {
                        init.resolvedType = replacement.resolveType(scope);
                    }
                }
                catch (Exception exception) {}
            }
        }
        if (val) {
            local.modifiers |= 0x10;
        }
        local.annotations = PatchVal.addValAnnotation(local.annotations, local.type, scope);
        local.type = replacement != null ? replacement : new QualifiedTypeReference(TypeConstants.JAVA_LANG_OBJECT, Eclipse.poss((ASTNode)local.type, 3));
        return false;
    }

    private static boolean isVar(LocalDeclaration local, BlockScope scope) {
        return PatchVal.is(local.type, scope, "lombok.experimental.var") || PatchVal.is(local.type, scope, "lombok.var");
    }

    private static boolean isVal(LocalDeclaration local, BlockScope scope) {
        return PatchVal.is(local.type, scope, "lombok.val");
    }

    private static boolean hasNativeVarSupport(Scope scope) {
        long sl = scope.problemReporter().options.sourceLevel >> 16;
        long cl = scope.problemReporter().options.complianceLevel >> 16;
        if (sl == 0L) {
            sl = cl;
        }
        if (cl == 0L) {
            cl = sl;
        }
        return Math.min((int)(sl - 44L), (int)(cl - 44L)) >= 10;
    }

    public static boolean handleValForForEach(ForeachStatement forEach, BlockScope scope) {
        if (forEach.elementVariable == null) {
            return false;
        }
        boolean val = PatchVal.isVal(forEach.elementVariable, scope);
        boolean var = PatchVal.isVar(forEach.elementVariable, scope);
        if (!val && !var) {
            return false;
        }
        if (PatchVal.hasNativeVarSupport((Scope)scope)) {
            return false;
        }
        TypeBinding component = PatchVal.getForEachComponentType(forEach.collection, scope);
        if (component == null) {
            return false;
        }
        TypeReference replacement = EclipseHandlerUtil.makeType(component, (ASTNode)forEach.elementVariable.type, false);
        if (val) {
            forEach.elementVariable.modifiers |= 0x10;
        }
        forEach.elementVariable.annotations = PatchVal.addValAnnotation(forEach.elementVariable.annotations, forEach.elementVariable.type, scope);
        forEach.elementVariable.type = replacement != null ? replacement : new QualifiedTypeReference(TypeConstants.JAVA_LANG_OBJECT, Eclipse.poss((ASTNode)forEach.elementVariable.type, 3));
        return false;
    }

    private static Annotation[] addValAnnotation(Annotation[] originals, TypeReference originalRef, BlockScope scope) {
        Annotation[] newAnn;
        if (originals != null) {
            newAnn = new Annotation[1 + originals.length];
            System.arraycopy(originals, 0, newAnn, 0, originals.length);
        } else {
            newAnn = new Annotation[1];
        }
        TypeReference qualifiedTypeRef = EclipseHandlerUtil.generateQualifiedTypeRef((ASTNode)originalRef, originalRef.getTypeName());
        newAnn[newAnn.length - 1] = new MarkerAnnotation(qualifiedTypeRef, qualifiedTypeRef.sourceStart);
        return newAnn;
    }

    private static TypeBinding getForEachComponentType(Expression collection, BlockScope scope) {
        if (collection != null) {
            TypeBinding resolved = collection.resolvedType;
            if (resolved == null) {
                resolved = PatchVal.resolveForExpression(collection, scope);
            }
            if (resolved == null) {
                return null;
            }
            if (resolved.isArrayType()) {
                resolved = ((ArrayBinding)resolved).elementsType();
                return resolved;
            }
            if (resolved instanceof ReferenceBinding) {
                ReferenceBinding iterableType = ((ReferenceBinding)resolved).findSuperTypeOriginatingFrom(38, false);
                TypeVariableBinding[] arguments = null;
                if (iterableType != null) {
                    switch (iterableType.kind()) {
                        case 2052: {
                            arguments = iterableType.typeVariables();
                            break;
                        }
                        case 260: {
                            arguments = ((ParameterizedTypeBinding)iterableType).arguments;
                            break;
                        }
                        case 1028: {
                            return null;
                        }
                    }
                }
                if (arguments != null && arguments.length == 1) {
                    return arguments[0];
                }
            }
        }
        return null;
    }

    private static TypeBinding resolveForExpression(Expression collection, BlockScope scope) {
        try {
            return collection.resolveType(scope);
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            return null;
        }
        catch (AbortCompilation abortCompilation) {
            return null;
        }
    }

    private static void clearIgnoreFurtherInvestigationField(ReferenceContext currentContext) {
        if (currentContext instanceof AbstractMethodDeclaration) {
            AbstractMethodDeclaration methodDeclaration = (AbstractMethodDeclaration)currentContext;
            methodDeclaration.ignoreFurtherInvestigation = false;
        } else if (currentContext instanceof LambdaExpression) {
            LambdaExpression lambdaExpression = (LambdaExpression)currentContext;
            PatchVal.setValue(PatchVal.getField(LambdaExpression.class, "ignoreFurtherInvestigation"), lambdaExpression, false);
            Scope parent = lambdaExpression.enclosingScope.parent;
            while (parent != null) {
                switch (parent.kind) {
                    case 2: 
                    case 3: {
                        ReferenceContext parentAST = parent.referenceContext();
                        if (parentAST == lambdaExpression) break;
                        PatchVal.clearIgnoreFurtherInvestigationField(parentAST);
                        return;
                    }
                }
                parent = parent.parent;
            }
        } else if (currentContext instanceof TypeDeclaration) {
            TypeDeclaration typeDeclaration = (TypeDeclaration)currentContext;
            typeDeclaration.ignoreFurtherInvestigation = false;
        } else if (currentContext instanceof CompilationUnitDeclaration) {
            CompilationUnitDeclaration typeDeclaration = (CompilationUnitDeclaration)currentContext;
            typeDeclaration.ignoreFurtherInvestigation = false;
        } else {
            throw new UnsupportedOperationException("clearIgnoreFurtherInvestigationField for " + currentContext.getClass());
        }
    }

    private static void setValue(Field field, Object object, Object value) {
        try {
            field.set(object, value);
        }
        catch (IllegalAccessException e) {
            throw Lombok.sneakyThrow(e);
        }
    }

    private static Field getField(Class<?> clazz, String name) {
        try {
            return Permit.getField(clazz, name);
        }
        catch (NoSuchFieldException e) {
            throw Lombok.sneakyThrow(e);
        }
    }

    public static final class Reflection {
        private static final Field initCopyField;
        private static final Field iterableCopyField;

        static {
            Field a = null;
            Field b = null;
            try {
                a = Permit.getField(LocalDeclaration.class, "$initCopy");
                b = Permit.getField(LocalDeclaration.class, "$iterableCopy");
            }
            catch (Throwable throwable) {}
            initCopyField = a;
            iterableCopyField = b;
        }
    }
}
