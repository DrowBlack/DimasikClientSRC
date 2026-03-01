package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.EqualsAndHashCode;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.configuration.CallSuperType;
import lombok.core.configuration.CheckerFrameworkVersion;
import lombok.core.configuration.NullAnnotationLibrary;
import lombok.core.handlers.HandlerUtil;
import lombok.core.handlers.InclusionExclusionUtils;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.SetGeneratedByVisitor;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FalseLiteral;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

public class HandleEqualsAndHashCode
extends EclipseAnnotationHandler<EqualsAndHashCode> {
    private static final String HASH_CODE_CACHE_NAME = "$hashCodeCache";
    private final char[] HASH_CODE_CACHE_NAME_ARR = "$hashCodeCache".toCharArray();
    private final char[] PRIME = "PRIME".toCharArray();
    private final char[] RESULT = "result".toCharArray();
    public static final Set<String> BUILT_IN_TYPES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("byte", "short", "int", "long", "char", "boolean", "double", "float")));
    private static final char[] HASH_CODE = "hashCode".toCharArray();
    private static final char[] FLOAT_TO_INT_BITS = "floatToIntBits".toCharArray();
    private static final char[] DOUBLE_TO_LONG_BITS = "doubleToLongBits".toCharArray();
    private static final char[][] JAVAX_ANNOTATION_NULLABLE = Eclipse.fromQualifiedName("javax.annotation.Nullable");
    private static final char[][] ORG_ECLIPSE_JDT_ANNOTATION_NULLABLE = Eclipse.fromQualifiedName("org.eclipse.jdt.annotation.Nullable");

    @Override
    public void handle(AnnotationValues<EqualsAndHashCode> annotation, Annotation ast, EclipseNode annotationNode) {
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.EQUALS_AND_HASH_CODE_FLAG_USAGE, "@EqualsAndHashCode");
        EqualsAndHashCode ann = annotation.getInstance();
        List<InclusionExclusionUtils.Included<EclipseNode, EqualsAndHashCode.Include>> members = InclusionExclusionUtils.handleEqualsAndHashCodeMarking(annotationNode.up(), annotation, annotationNode);
        if (members == null) {
            return;
        }
        List<Annotation> onParam = EclipseHandlerUtil.unboxAndRemoveAnnotationParameter(ast, "onParam", "@EqualsAndHashCode(onParam", annotationNode);
        Boolean callSuper = ann.callSuper();
        if (!annotation.isExplicit("callSuper")) {
            callSuper = null;
        }
        Boolean doNotUseGettersConfiguration = annotationNode.getAst().readConfiguration(ConfigurationKeys.EQUALS_AND_HASH_CODE_DO_NOT_USE_GETTERS);
        boolean doNotUseGetters = annotation.isExplicit("doNotUseGetters") || doNotUseGettersConfiguration == null ? ann.doNotUseGetters() : doNotUseGettersConfiguration.booleanValue();
        HandlerUtil.FieldAccess fieldAccess = doNotUseGetters ? HandlerUtil.FieldAccess.PREFER_FIELD : HandlerUtil.FieldAccess.GETTER;
        boolean cacheHashCode = ann.cacheStrategy() == EqualsAndHashCode.CacheStrategy.LAZY;
        this.generateMethods((EclipseNode)annotationNode.up(), annotationNode, members, callSuper, true, cacheHashCode, fieldAccess, onParam);
    }

    public void generateEqualsAndHashCodeForType(EclipseNode typeNode, EclipseNode errorNode) {
        if (EclipseHandlerUtil.hasAnnotation(EqualsAndHashCode.class, typeNode)) {
            return;
        }
        List<InclusionExclusionUtils.Included<EclipseNode, EqualsAndHashCode.Include>> members = InclusionExclusionUtils.handleEqualsAndHashCodeMarking(typeNode, null, null);
        Boolean doNotUseGettersConfiguration = typeNode.getAst().readConfiguration(ConfigurationKeys.EQUALS_AND_HASH_CODE_DO_NOT_USE_GETTERS);
        HandlerUtil.FieldAccess access = doNotUseGettersConfiguration == null || doNotUseGettersConfiguration == false ? HandlerUtil.FieldAccess.GETTER : HandlerUtil.FieldAccess.PREFER_FIELD;
        this.generateMethods(typeNode, errorNode, members, null, false, false, access, new ArrayList<Annotation>());
    }

    public void generateMethods(EclipseNode typeNode, EclipseNode errorNode, List<InclusionExclusionUtils.Included<EclipseNode, EqualsAndHashCode.Include>> members, Boolean callSuper, boolean whineIfExists, boolean cacheHashCode, HandlerUtil.FieldAccess fieldAccess, List<Annotation> onParam) {
        boolean implicitCallSuper;
        if (!EclipseHandlerUtil.isClass(typeNode)) {
            errorNode.addError("@EqualsAndHashCode is only supported on a class.");
            return;
        }
        TypeDeclaration typeDecl = (TypeDeclaration)typeNode.get();
        boolean bl = implicitCallSuper = callSuper == null;
        if (callSuper == null) {
            try {
                callSuper = (boolean)((Boolean)EqualsAndHashCode.class.getMethod("callSuper", new Class[0]).getDefaultValue());
            }
            catch (Exception exception) {
                throw new InternalError("Lombok bug - this cannot happen - can't find callSuper field in EqualsAndHashCode annotation.");
            }
        }
        boolean isDirectDescendantOfObject = EclipseHandlerUtil.isDirectDescendantOfObject(typeNode);
        boolean isFinal = (typeDecl.modifiers & 0x10) != 0;
        boolean needsCanEqual = !isFinal || !isDirectDescendantOfObject;
        EclipseHandlerUtil.MemberExistsResult equalsExists = EclipseHandlerUtil.methodExists("equals", typeNode, 1);
        EclipseHandlerUtil.MemberExistsResult hashCodeExists = EclipseHandlerUtil.methodExists("hashCode", typeNode, 0);
        EclipseHandlerUtil.MemberExistsResult canEqualExists = EclipseHandlerUtil.methodExists("canEqual", typeNode, 1);
        switch (Collections.max(Arrays.asList(equalsExists, hashCodeExists))) {
            case EXISTS_BY_LOMBOK: {
                return;
            }
            case EXISTS_BY_USER: {
                if (whineIfExists) {
                    String msg = "Not generating equals and hashCode: A method with one of those names already exists. (Either both or none of these methods will be generated).";
                    errorNode.addWarning(msg);
                } else if (equalsExists == EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS || hashCodeExists == EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS) {
                    String msg = String.format("Not generating %s: One of equals or hashCode exists. You should either write both of these or none of these (in the latter case, lombok generates them).", equalsExists == EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS ? "equals" : "hashCode");
                    errorNode.addWarning(msg);
                }
                return;
            }
        }
        if (isDirectDescendantOfObject && callSuper.booleanValue()) {
            errorNode.addError("Generating equals/hashCode with a supercall to java.lang.Object is pointless.");
            return;
        }
        if (implicitCallSuper && !isDirectDescendantOfObject) {
            CallSuperType cst = typeNode.getAst().readConfiguration(ConfigurationKeys.EQUALS_AND_HASH_CODE_CALL_SUPER);
            if (cst == null) {
                cst = CallSuperType.WARN;
            }
            switch (cst) {
                default: {
                    errorNode.addWarning("Generating equals/hashCode implementation but without a call to superclass, even though this class does not extend java.lang.Object. If this is intentional, add '@EqualsAndHashCode(callSuper=false)' to your type.");
                    callSuper = false;
                    break;
                }
                case SKIP: {
                    callSuper = false;
                    break;
                }
                case CALL: {
                    callSuper = true;
                }
            }
        }
        MethodDeclaration equalsMethod = this.createEquals(typeNode, members, callSuper, (ASTNode)errorNode.get(), fieldAccess, needsCanEqual, onParam);
        equalsMethod.traverse((ASTVisitor)new SetGeneratedByVisitor((ASTNode)errorNode.get()), ((TypeDeclaration)typeNode.get()).scope);
        EclipseHandlerUtil.injectMethod(typeNode, (AbstractMethodDeclaration)equalsMethod);
        if (needsCanEqual && canEqualExists == EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS) {
            MethodDeclaration canEqualMethod = this.createCanEqual(typeNode, (ASTNode)errorNode.get(), onParam);
            canEqualMethod.traverse((ASTVisitor)new SetGeneratedByVisitor((ASTNode)errorNode.get()), ((TypeDeclaration)typeNode.get()).scope);
            EclipseHandlerUtil.injectMethod(typeNode, (AbstractMethodDeclaration)canEqualMethod);
        }
        if (cacheHashCode) {
            if (EclipseHandlerUtil.fieldExists(HASH_CODE_CACHE_NAME, typeNode) != EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS) {
                String msg = String.format("Not caching the result of hashCode: A field named %s already exists.", HASH_CODE_CACHE_NAME);
                errorNode.addWarning(msg);
                cacheHashCode = false;
            } else {
                this.createHashCodeCacheField(typeNode, (ASTNode)errorNode.get());
            }
        }
        MethodDeclaration hashCodeMethod = this.createHashCode(typeNode, members, callSuper, cacheHashCode, (ASTNode)errorNode.get(), fieldAccess);
        hashCodeMethod.traverse((ASTVisitor)new SetGeneratedByVisitor((ASTNode)errorNode.get()), ((TypeDeclaration)typeNode.get()).scope);
        EclipseHandlerUtil.injectMethod(typeNode, (AbstractMethodDeclaration)hashCodeMethod);
    }

    private void createHashCodeCacheField(EclipseNode typeNode, ASTNode source) {
        FieldDeclaration hashCodeCacheDecl = new FieldDeclaration(this.HASH_CODE_CACHE_NAME_ARR, 0, 0);
        hashCodeCacheDecl.modifiers = 130;
        hashCodeCacheDecl.bits |= 0x800000;
        hashCodeCacheDecl.type = TypeReference.baseTypeReference((int)10, (int)0);
        hashCodeCacheDecl.declarationSourceEnd = -1;
        EclipseHandlerUtil.injectFieldAndMarkGenerated(typeNode, hashCodeCacheDecl);
        EclipseHandlerUtil.setGeneratedBy(hashCodeCacheDecl, source);
        EclipseHandlerUtil.setGeneratedBy(hashCodeCacheDecl.type, source);
    }

    public MethodDeclaration createHashCode(EclipseNode type, Collection<InclusionExclusionUtils.Included<EclipseNode, EqualsAndHashCode.Include>> members, boolean callSuper, boolean cacheHashCode, ASTNode source, HandlerUtil.FieldAccess fieldAccess) {
        Object init;
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        MethodDeclaration method = new MethodDeclaration(((CompilationUnitDeclaration)((EclipseNode)type.top()).get()).compilationResult);
        EclipseHandlerUtil.setGeneratedBy(method, source);
        method.modifiers = EclipseHandlerUtil.toEclipseModifier(AccessLevel.PUBLIC);
        method.returnType = TypeReference.baseTypeReference((int)10, (int)0);
        EclipseHandlerUtil.setGeneratedBy(method.returnType, source);
        MarkerAnnotation overrideAnnotation = EclipseHandlerUtil.makeMarkerAnnotation(TypeConstants.JAVA_LANG_OVERRIDE, source);
        CheckerFrameworkVersion checkerFramework = EclipseHandlerUtil.getCheckerFrameworkVersion(type);
        method.annotations = cacheHashCode && checkerFramework.generatePure() ? new Annotation[]{overrideAnnotation, EclipseHandlerUtil.generateNamedAnnotation(source, "org.checkerframework.dataflow.qual.Pure")} : (checkerFramework.generateSideEffectFree() ? new Annotation[]{overrideAnnotation, EclipseHandlerUtil.generateNamedAnnotation(source, "org.checkerframework.dataflow.qual.SideEffectFree")} : new Annotation[]{overrideAnnotation});
        method.selector = "hashCode".toCharArray();
        method.thrownExceptions = null;
        method.typeParameters = null;
        method.bits |= 0x800000;
        method.declarationSourceStart = method.sourceStart = source.sourceStart;
        method.bodyStart = method.sourceStart;
        method.declarationSourceEnd = method.sourceEnd = source.sourceEnd;
        method.bodyEnd = method.sourceEnd;
        method.arguments = null;
        ArrayList<Object> statements = new ArrayList<Object>();
        boolean isEmpty = true;
        for (InclusionExclusionUtils.Included<EclipseNode, EqualsAndHashCode.Include> included : members) {
            TypeReference fType = EclipseHandlerUtil.getFieldType(included.getNode(), fieldAccess);
            if (fType.getLastToken() == null) continue;
            isEmpty = false;
            break;
        }
        if (cacheHashCode) {
            FieldReference fieldReference = new FieldReference(this.HASH_CODE_CACHE_NAME_ARR, p);
            fieldReference.receiver = new ThisReference(pS, pE);
            EclipseHandlerUtil.setGeneratedBy(fieldReference, source);
            EclipseHandlerUtil.setGeneratedBy(fieldReference.receiver, source);
            EqualExpression cacheNotZero = new EqualExpression((Expression)fieldReference, (Expression)EclipseHandlerUtil.makeIntLiteral("0".toCharArray(), source), OperatorIds.NOT_EQUAL);
            EclipseHandlerUtil.setGeneratedBy(cacheNotZero, source);
            ReturnStatement returnCache = new ReturnStatement((Expression)fieldReference, pS, pE);
            EclipseHandlerUtil.setGeneratedBy(returnCache, source);
            IfStatement ifStatement = new IfStatement((Expression)cacheNotZero, (Statement)returnCache, pS, pE);
            EclipseHandlerUtil.setGeneratedBy(ifStatement, source);
            statements.add(ifStatement);
        }
        if (!isEmpty) {
            LocalDeclaration localDeclaration = new LocalDeclaration(this.PRIME, pS, pE);
            EclipseHandlerUtil.setGeneratedBy(localDeclaration, source);
            localDeclaration.modifiers |= 0x10;
            localDeclaration.type = TypeReference.baseTypeReference((int)10, (int)0);
            localDeclaration.type.sourceStart = pS;
            localDeclaration.type.sourceEnd = pE;
            EclipseHandlerUtil.setGeneratedBy(localDeclaration.type, source);
            localDeclaration.initialization = EclipseHandlerUtil.makeIntLiteral(String.valueOf(HandlerUtil.primeForHashcode()).toCharArray(), source);
            statements.add(localDeclaration);
        }
        LocalDeclaration localDeclaration = new LocalDeclaration(this.RESULT, pS, pE);
        EclipseHandlerUtil.setGeneratedBy(localDeclaration, source);
        if (callSuper) {
            MessageSend callToSuper = new MessageSend();
            EclipseHandlerUtil.setGeneratedBy(callToSuper, source);
            callToSuper.sourceStart = pS;
            callToSuper.sourceEnd = pE;
            callToSuper.receiver = new SuperReference(pS, pE);
            EclipseHandlerUtil.setGeneratedBy(callToSuper.receiver, source);
            callToSuper.selector = "hashCode".toCharArray();
            init = callToSuper;
        } else {
            init = EclipseHandlerUtil.makeIntLiteral("1".toCharArray(), source);
        }
        localDeclaration.initialization = init;
        localDeclaration.type = TypeReference.baseTypeReference((int)10, (int)0);
        localDeclaration.type.sourceStart = pS;
        localDeclaration.type.sourceEnd = pE;
        if (isEmpty && !cacheHashCode) {
            localDeclaration.modifiers |= 0x10;
        }
        EclipseHandlerUtil.setGeneratedBy(localDeclaration.type, source);
        statements.add(localDeclaration);
        for (InclusionExclusionUtils.Included included : members) {
            Expression fieldAccessor;
            EclipseNode memberNode = (EclipseNode)included.getNode();
            boolean isMethod = memberNode.getKind() == AST.Kind.METHOD;
            TypeReference fType = EclipseHandlerUtil.getFieldType(memberNode, fieldAccess);
            char[] dollarFieldName = (String.valueOf(isMethod ? "$$" : "$") + memberNode.getName()).toCharArray();
            char[] token = fType.getLastToken();
            Expression expression = fieldAccessor = isMethod ? EclipseHandlerUtil.createMethodAccessor(memberNode, source) : EclipseHandlerUtil.createFieldAccessor(memberNode, fieldAccess, source);
            if (fType.dimensions() == 0 && token != null) {
                SingleNameReference copy2;
                SingleNameReference copy1;
                if (Arrays.equals(TypeConstants.BOOLEAN, token)) {
                    IntLiteral intTrue = EclipseHandlerUtil.makeIntLiteral(String.valueOf(HandlerUtil.primeForTrue()).toCharArray(), source);
                    IntLiteral intFalse = EclipseHandlerUtil.makeIntLiteral(String.valueOf(HandlerUtil.primeForFalse()).toCharArray(), source);
                    ConditionalExpression intForBool = new ConditionalExpression(fieldAccessor, (Expression)intTrue, (Expression)intFalse);
                    EclipseHandlerUtil.setGeneratedBy(intForBool, source);
                    statements.add(this.createResultCalculation(source, (Expression)intForBool));
                    continue;
                }
                if (Arrays.equals(TypeConstants.LONG, token)) {
                    statements.add(this.createLocalDeclaration(source, dollarFieldName, TypeReference.baseTypeReference((int)7, (int)0), fieldAccessor));
                    copy1 = new SingleNameReference(dollarFieldName, p);
                    EclipseHandlerUtil.setGeneratedBy(copy1, source);
                    copy2 = new SingleNameReference(dollarFieldName, p);
                    EclipseHandlerUtil.setGeneratedBy(copy2, source);
                    statements.add(this.createResultCalculation(source, this.longToIntForHashCode((Expression)copy1, (Expression)copy2, source)));
                    continue;
                }
                if (Arrays.equals(TypeConstants.FLOAT, token)) {
                    MessageSend floatToIntBits = new MessageSend();
                    floatToIntBits.sourceStart = pS;
                    floatToIntBits.sourceEnd = pE;
                    EclipseHandlerUtil.setGeneratedBy(floatToIntBits, source);
                    floatToIntBits.receiver = EclipseHandlerUtil.generateQualifiedNameRef(source, TypeConstants.JAVA_LANG_FLOAT);
                    floatToIntBits.selector = FLOAT_TO_INT_BITS;
                    floatToIntBits.arguments = new Expression[]{fieldAccessor};
                    statements.add(this.createResultCalculation(source, (Expression)floatToIntBits));
                    continue;
                }
                if (Arrays.equals(TypeConstants.DOUBLE, token)) {
                    MessageSend doubleToLongBits = new MessageSend();
                    doubleToLongBits.sourceStart = pS;
                    doubleToLongBits.sourceEnd = pE;
                    EclipseHandlerUtil.setGeneratedBy(doubleToLongBits, source);
                    doubleToLongBits.receiver = EclipseHandlerUtil.generateQualifiedNameRef(source, TypeConstants.JAVA_LANG_DOUBLE);
                    doubleToLongBits.selector = DOUBLE_TO_LONG_BITS;
                    doubleToLongBits.arguments = new Expression[]{fieldAccessor};
                    statements.add(this.createLocalDeclaration(source, dollarFieldName, TypeReference.baseTypeReference((int)7, (int)0), (Expression)doubleToLongBits));
                    SingleNameReference copy12 = new SingleNameReference(dollarFieldName, p);
                    EclipseHandlerUtil.setGeneratedBy(copy12, source);
                    SingleNameReference copy22 = new SingleNameReference(dollarFieldName, p);
                    EclipseHandlerUtil.setGeneratedBy(copy22, source);
                    statements.add(this.createResultCalculation(source, this.longToIntForHashCode((Expression)copy12, (Expression)copy22, source)));
                    continue;
                }
                if (BUILT_IN_TYPES.contains(new String(token))) {
                    statements.add(this.createResultCalculation(source, fieldAccessor));
                    continue;
                }
                statements.add(this.createLocalDeclaration(source, dollarFieldName, EclipseHandlerUtil.generateQualifiedTypeRef(source, TypeConstants.JAVA_LANG_OBJECT), fieldAccessor));
                copy1 = new SingleNameReference(dollarFieldName, p);
                EclipseHandlerUtil.setGeneratedBy(copy1, source);
                copy2 = new SingleNameReference(dollarFieldName, p);
                EclipseHandlerUtil.setGeneratedBy(copy2, source);
                MessageSend hashCodeCall = new MessageSend();
                hashCodeCall.sourceStart = pS;
                hashCodeCall.sourceEnd = pE;
                EclipseHandlerUtil.setGeneratedBy(hashCodeCall, source);
                hashCodeCall.receiver = copy1;
                hashCodeCall.selector = HASH_CODE;
                NullLiteral nullLiteral = new NullLiteral(pS, pE);
                EclipseHandlerUtil.setGeneratedBy(nullLiteral, source);
                EqualExpression objIsNull = new EqualExpression((Expression)copy2, (Expression)nullLiteral, OperatorIds.EQUAL_EQUAL);
                EclipseHandlerUtil.setGeneratedBy(objIsNull, source);
                IntLiteral intMagic = EclipseHandlerUtil.makeIntLiteral(String.valueOf(HandlerUtil.primeForNull()).toCharArray(), source);
                ConditionalExpression nullOrHashCode = new ConditionalExpression((Expression)objIsNull, (Expression)intMagic, (Expression)hashCodeCall);
                nullOrHashCode.sourceStart = pS;
                nullOrHashCode.sourceEnd = pE;
                EclipseHandlerUtil.setGeneratedBy(nullOrHashCode, source);
                statements.add(this.createResultCalculation(source, (Expression)nullOrHashCode));
                continue;
            }
            if (fType.dimensions() <= 0 || token == null) continue;
            MessageSend arraysHashCodeCall = new MessageSend();
            arraysHashCodeCall.sourceStart = pS;
            arraysHashCodeCall.sourceEnd = pE;
            EclipseHandlerUtil.setGeneratedBy(arraysHashCodeCall, source);
            arraysHashCodeCall.receiver = EclipseHandlerUtil.generateQualifiedNameRef(source, TypeConstants.JAVA, TypeConstants.UTIL, "Arrays".toCharArray());
            arraysHashCodeCall.selector = fType.dimensions() > 1 || !BUILT_IN_TYPES.contains(new String(token)) ? "deepHashCode".toCharArray() : "hashCode".toCharArray();
            arraysHashCodeCall.arguments = new Expression[]{fieldAccessor};
            statements.add(this.createResultCalculation(source, (Expression)arraysHashCodeCall));
        }
        if (cacheHashCode) {
            SingleNameReference singleNameReference = new SingleNameReference(this.RESULT, p);
            EclipseHandlerUtil.setGeneratedBy(singleNameReference, source);
            EqualExpression resultIsZero = new EqualExpression((Expression)singleNameReference, (Expression)EclipseHandlerUtil.makeIntLiteral("0".toCharArray(), source), OperatorIds.EQUAL_EQUAL);
            EclipseHandlerUtil.setGeneratedBy(resultIsZero, source);
            SingleNameReference singleNameReference2 = new SingleNameReference(this.RESULT, p);
            EclipseHandlerUtil.setGeneratedBy(singleNameReference2, source);
            FieldReference integerMinValue = new FieldReference("MIN_VALUE".toCharArray(), p);
            integerMinValue.receiver = EclipseHandlerUtil.generateQualifiedNameRef(source, TypeConstants.JAVA_LANG_INTEGER);
            EclipseHandlerUtil.setGeneratedBy(integerMinValue, source);
            Assignment newResult = new Assignment((Expression)singleNameReference2, (Expression)integerMinValue, pE);
            newResult.sourceStart = pS;
            newResult.statementEnd = newResult.sourceEnd = pE;
            EclipseHandlerUtil.setGeneratedBy(newResult, source);
            IfStatement ifStatement = new IfStatement((Expression)resultIsZero, (Statement)newResult, pS, pE);
            EclipseHandlerUtil.setGeneratedBy(ifStatement, source);
            statements.add(ifStatement);
            FieldReference hashCodeCacheRef = new FieldReference(this.HASH_CODE_CACHE_NAME_ARR, p);
            hashCodeCacheRef.receiver = new ThisReference(pS, pE);
            EclipseHandlerUtil.setGeneratedBy(hashCodeCacheRef, source);
            EclipseHandlerUtil.setGeneratedBy(hashCodeCacheRef.receiver, source);
            SingleNameReference singleNameReference3 = new SingleNameReference(this.RESULT, p);
            EclipseHandlerUtil.setGeneratedBy(singleNameReference3, source);
            Assignment cacheResult = new Assignment((Expression)hashCodeCacheRef, (Expression)singleNameReference3, pE);
            cacheResult.sourceStart = pS;
            cacheResult.statementEnd = cacheResult.sourceEnd = pE;
            EclipseHandlerUtil.setGeneratedBy(cacheResult, source);
            statements.add(cacheResult);
        }
        SingleNameReference singleNameReference = new SingleNameReference(this.RESULT, p);
        EclipseHandlerUtil.setGeneratedBy(singleNameReference, source);
        ReturnStatement returnStatement = new ReturnStatement((Expression)singleNameReference, pS, pE);
        EclipseHandlerUtil.setGeneratedBy(returnStatement, source);
        statements.add(returnStatement);
        method.statements = statements.toArray(new Statement[0]);
        return method;
    }

    public LocalDeclaration createLocalDeclaration(ASTNode source, char[] dollarFieldName, TypeReference type, Expression initializer) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        LocalDeclaration tempVar = new LocalDeclaration(dollarFieldName, pS, pE);
        EclipseHandlerUtil.setGeneratedBy(tempVar, source);
        tempVar.initialization = initializer;
        tempVar.type = type;
        tempVar.type.sourceStart = pS;
        tempVar.type.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy(tempVar.type, source);
        tempVar.modifiers = 16;
        return tempVar;
    }

    public Expression createResultCalculation(ASTNode source, Expression ex) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        SingleNameReference resultRef = new SingleNameReference(this.RESULT, p);
        EclipseHandlerUtil.setGeneratedBy(resultRef, source);
        SingleNameReference primeRef = new SingleNameReference(this.PRIME, p);
        EclipseHandlerUtil.setGeneratedBy(primeRef, source);
        BinaryExpression multiplyByPrime = new BinaryExpression((Expression)resultRef, (Expression)primeRef, OperatorIds.MULTIPLY);
        multiplyByPrime.sourceStart = pS;
        multiplyByPrime.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy(multiplyByPrime, source);
        BinaryExpression addItem = new BinaryExpression((Expression)multiplyByPrime, ex, OperatorIds.PLUS);
        addItem.sourceStart = pS;
        addItem.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy(addItem, source);
        resultRef = new SingleNameReference(this.RESULT, p);
        EclipseHandlerUtil.setGeneratedBy(resultRef, source);
        Assignment assignment = new Assignment((Expression)resultRef, (Expression)addItem, pE);
        assignment.sourceStart = pS;
        assignment.sourceEnd = assignment.statementEnd = pE;
        EclipseHandlerUtil.setGeneratedBy(assignment, source);
        return assignment;
    }

    public TypeReference createTypeReference(EclipseNode type, long p, ASTNode source, boolean addWildcards) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<Integer> genericsCount = addWildcards ? new ArrayList<Integer>() : null;
        list.add(type.getName());
        if (addWildcards) {
            genericsCount.add(this.arraySizeOf(((TypeDeclaration)type.get()).typeParameters));
        }
        boolean staticContext = (((TypeDeclaration)type.get()).modifiers & 8) != 0;
        EclipseNode tNode = (EclipseNode)type.up();
        while (tNode != null && tNode.getKind() == AST.Kind.TYPE) {
            TypeDeclaration td = (TypeDeclaration)tNode.get();
            if (td.name == null || td.name.length == 0) break;
            list.add(tNode.getName());
            if (!staticContext && tNode.getKind() == AST.Kind.TYPE && (td.modifiers & 0x200) != 0) {
                staticContext = true;
            }
            if (addWildcards) {
                genericsCount.add(staticContext ? 0 : this.arraySizeOf(td.typeParameters));
            }
            if (!staticContext) {
                staticContext = (td.modifiers & 8) != 0;
            }
            tNode = (EclipseNode)tNode.up();
        }
        Collections.reverse(list);
        if (addWildcards) {
            Collections.reverse(genericsCount);
        }
        if (list.size() == 1) {
            if (!addWildcards || (Integer)genericsCount.get(0) == 0) {
                return new SingleTypeReference(((String)list.get(0)).toCharArray(), p);
            }
            return new ParameterizedSingleTypeReference(((String)list.get(0)).toCharArray(), this.wildcardify(pS, pE, source, (Integer)genericsCount.get(0)), 0, p);
        }
        if (addWildcards) {
            addWildcards = false;
            Iterator iterator = genericsCount.iterator();
            while (iterator.hasNext()) {
                int i = (Integer)iterator.next();
                if (i <= 0) continue;
                addWildcards = true;
            }
        }
        long[] ps = new long[list.size()];
        char[][] tokens = new char[list.size()][];
        int i = 0;
        while (i < list.size()) {
            ps[i] = p;
            tokens[i] = ((String)list.get(i)).toCharArray();
            ++i;
        }
        if (!addWildcards) {
            return new QualifiedTypeReference((char[][])tokens, ps);
        }
        TypeReference[][] typeArgs2 = new TypeReference[tokens.length][];
        int i2 = 0;
        while (i2 < tokens.length) {
            typeArgs2[i2] = this.wildcardify(pS, pE, source, (Integer)genericsCount.get(i2));
            ++i2;
        }
        return new ParameterizedQualifiedTypeReference((char[][])tokens, (TypeReference[][])typeArgs2, 0, ps);
    }

    private TypeReference[] wildcardify(int pS, int pE, ASTNode source, int count) {
        if (count == 0) {
            return null;
        }
        TypeReference[] typeArgs = new TypeReference[count];
        int i = 0;
        while (i < count) {
            typeArgs[i] = new Wildcard(0);
            typeArgs[i].sourceStart = pS;
            typeArgs[i].sourceEnd = pE;
            EclipseHandlerUtil.setGeneratedBy(typeArgs[i], source);
            ++i;
        }
        return typeArgs;
    }

    private int arraySizeOf(Object[] arr) {
        return arr == null ? 0 : arr.length;
    }

    public MethodDeclaration createEquals(EclipseNode type, Collection<InclusionExclusionUtils.Included<EclipseNode, EqualsAndHashCode.Include>> members, boolean callSuper, ASTNode source, HandlerUtil.FieldAccess fieldAccess, boolean needsCanEqual, List<Annotation> onParam) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        ArrayList<NullAnnotationLibrary> applied = new ArrayList<NullAnnotationLibrary>();
        Annotation[] onParamNullable = null;
        String nearest = EclipseHandlerUtil.scanForNearestAnnotation(type, "javax.annotation.ParametersAreNullableByDefault", "javax.annotation.ParametersAreNonnullByDefault");
        if ("javax.annotation.ParametersAreNonnullByDefault".equals(nearest)) {
            onParamNullable = new Annotation[]{new MarkerAnnotation(EclipseHandlerUtil.generateQualifiedTypeRef(source, JAVAX_ANNOTATION_NULLABLE), 0)};
            applied.add(NullAnnotationLibrary.JAVAX);
        }
        Annotation[] onParamTypeNullable = null;
        nearest = EclipseHandlerUtil.scanForNearestAnnotation(type, "org.eclipse.jdt.annotation.NonNullByDefault");
        if (nearest != null) {
            onParamTypeNullable = new Annotation[]{new MarkerAnnotation(EclipseHandlerUtil.generateQualifiedTypeRef(source, ORG_ECLIPSE_JDT_ANNOTATION_NULLABLE), 0)};
            applied.add(NullAnnotationLibrary.ECLIPSE);
        }
        MethodDeclaration method = new MethodDeclaration(((CompilationUnitDeclaration)((EclipseNode)type.top()).get()).compilationResult);
        EclipseHandlerUtil.setGeneratedBy(method, source);
        method.modifiers = EclipseHandlerUtil.toEclipseModifier(AccessLevel.PUBLIC);
        method.returnType = TypeReference.baseTypeReference((int)5, (int)0);
        method.returnType.sourceStart = pS;
        method.returnType.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy(method.returnType, source);
        MarkerAnnotation overrideAnnotation = EclipseHandlerUtil.makeMarkerAnnotation(TypeConstants.JAVA_LANG_OVERRIDE, source);
        method.annotations = EclipseHandlerUtil.getCheckerFrameworkVersion(type).generateSideEffectFree() ? new Annotation[]{overrideAnnotation, EclipseHandlerUtil.generateNamedAnnotation(source, "org.checkerframework.dataflow.qual.SideEffectFree")} : new Annotation[]{overrideAnnotation};
        method.selector = "equals".toCharArray();
        method.thrownExceptions = null;
        method.typeParameters = null;
        method.bits |= 0x800000;
        method.declarationSourceStart = method.sourceStart = source.sourceStart;
        method.bodyStart = method.sourceStart;
        method.declarationSourceEnd = method.sourceEnd = source.sourceEnd;
        method.bodyEnd = method.sourceEnd;
        QualifiedTypeReference objectRef = new QualifiedTypeReference(TypeConstants.JAVA_LANG_OBJECT, new long[]{p, p, p});
        if (onParamTypeNullable != null) {
            Annotation[][] annotationArrayArray = new Annotation[3][];
            annotationArrayArray[2] = onParamTypeNullable;
            objectRef.annotations = annotationArrayArray;
            objectRef.bits |= 0x100000;
            method.bits |= 0x100000;
        }
        EclipseHandlerUtil.setGeneratedBy(objectRef, source);
        method.arguments = new Argument[]{new Argument(new char[]{'o'}, 0L, (TypeReference)objectRef, 16)};
        method.arguments[0].sourceStart = pS;
        method.arguments[0].sourceEnd = pE;
        if (!onParam.isEmpty() || onParamNullable != null) {
            method.arguments[0].annotations = EclipseHandlerUtil.copyAnnotations(source, onParam.toArray(new Annotation[0]), onParamNullable);
        }
        EclipseHandlerUtil.createRelevantNullableAnnotation(type, method.arguments[0], method, applied);
        EclipseHandlerUtil.setGeneratedBy(method.arguments[0], source);
        ArrayList<Object> statements = new ArrayList<Object>();
        SingleNameReference oRef = new SingleNameReference(new char[]{'o'}, p);
        EclipseHandlerUtil.setGeneratedBy(oRef, source);
        ThisReference thisRef = new ThisReference(pS, pE);
        EclipseHandlerUtil.setGeneratedBy(thisRef, source);
        EqualExpression otherEqualsThis = new EqualExpression((Expression)oRef, (Expression)thisRef, OperatorIds.EQUAL_EQUAL);
        EclipseHandlerUtil.setGeneratedBy(otherEqualsThis, source);
        TrueLiteral trueLiteral = new TrueLiteral(pS, pE);
        EclipseHandlerUtil.setGeneratedBy(trueLiteral, source);
        ReturnStatement returnTrue = new ReturnStatement((Expression)trueLiteral, pS, pE);
        EclipseHandlerUtil.setGeneratedBy(returnTrue, source);
        IfStatement ifOtherEqualsThis = new IfStatement((Expression)otherEqualsThis, (Statement)returnTrue, pS, pE);
        EclipseHandlerUtil.setGeneratedBy(ifOtherEqualsThis, source);
        statements.add(ifOtherEqualsThis);
        oRef = new SingleNameReference(new char[]{'o'}, p);
        EclipseHandlerUtil.setGeneratedBy(oRef, source);
        TypeReference typeReference = this.createTypeReference(type, p, source, false);
        EclipseHandlerUtil.setGeneratedBy(typeReference, source);
        InstanceOfExpression instanceOf = new InstanceOfExpression((Expression)oRef, typeReference);
        instanceOf.sourceStart = pS;
        instanceOf.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy(instanceOf, source);
        UnaryExpression notInstanceOf = new UnaryExpression((Expression)instanceOf, OperatorIds.NOT);
        EclipseHandlerUtil.setGeneratedBy(notInstanceOf, source);
        FalseLiteral falseLiteral = new FalseLiteral(pS, pE);
        EclipseHandlerUtil.setGeneratedBy(falseLiteral, source);
        ReturnStatement returnFalse = new ReturnStatement((Expression)falseLiteral, pS, pE);
        EclipseHandlerUtil.setGeneratedBy(returnFalse, source);
        IfStatement ifNotInstanceOf = new IfStatement((Expression)notInstanceOf, (Statement)returnFalse, pS, pE);
        EclipseHandlerUtil.setGeneratedBy(ifNotInstanceOf, source);
        statements.add(ifNotInstanceOf);
        char[] otherName = "other".toCharArray();
        if (!members.isEmpty() || needsCanEqual) {
            LocalDeclaration other = new LocalDeclaration(otherName, pS, pE);
            other.modifiers |= 0x10;
            EclipseHandlerUtil.setGeneratedBy(other, source);
            TypeReference targetType = this.createTypeReference(type, p, source, true);
            EclipseHandlerUtil.setGeneratedBy(targetType, source);
            other.type = this.createTypeReference(type, p, source, true);
            EclipseHandlerUtil.setGeneratedBy(other.type, source);
            SingleNameReference oRef2 = new SingleNameReference(new char[]{'o'}, p);
            EclipseHandlerUtil.setGeneratedBy(oRef2, source);
            other.initialization = EclipseHandlerUtil.makeCastExpression((Expression)oRef2, targetType, source);
            statements.add(other);
        }
        if (needsCanEqual) {
            MessageSend otherCanEqual = new MessageSend();
            otherCanEqual.sourceStart = pS;
            otherCanEqual.sourceEnd = pE;
            EclipseHandlerUtil.setGeneratedBy(otherCanEqual, source);
            otherCanEqual.receiver = new SingleNameReference(otherName, p);
            EclipseHandlerUtil.setGeneratedBy(otherCanEqual.receiver, source);
            otherCanEqual.selector = "canEqual".toCharArray();
            ThisReference thisReference = new ThisReference(pS, pE);
            EclipseHandlerUtil.setGeneratedBy(thisReference, source);
            CastExpression castThisRef = EclipseHandlerUtil.makeCastExpression((Expression)thisReference, EclipseHandlerUtil.generateQualifiedTypeRef(source, TypeConstants.JAVA_LANG_OBJECT), source);
            castThisRef.sourceStart = pS;
            castThisRef.sourceEnd = pE;
            otherCanEqual.arguments = new Expression[]{castThisRef};
            UnaryExpression notOtherCanEqual = new UnaryExpression((Expression)otherCanEqual, OperatorIds.NOT);
            EclipseHandlerUtil.setGeneratedBy(notOtherCanEqual, source);
            FalseLiteral falseLiteral2 = new FalseLiteral(pS, pE);
            EclipseHandlerUtil.setGeneratedBy(falseLiteral2, source);
            ReturnStatement returnFalse2 = new ReturnStatement((Expression)falseLiteral2, pS, pE);
            EclipseHandlerUtil.setGeneratedBy(returnFalse2, source);
            IfStatement ifNotCanEqual = new IfStatement((Expression)notOtherCanEqual, (Statement)returnFalse2, pS, pE);
            EclipseHandlerUtil.setGeneratedBy(ifNotCanEqual, source);
            statements.add(ifNotCanEqual);
        }
        if (callSuper) {
            MessageSend callToSuper = new MessageSend();
            callToSuper.sourceStart = pS;
            callToSuper.sourceEnd = pE;
            EclipseHandlerUtil.setGeneratedBy(callToSuper, source);
            callToSuper.receiver = new SuperReference(pS, pE);
            EclipseHandlerUtil.setGeneratedBy(callToSuper.receiver, source);
            callToSuper.selector = "equals".toCharArray();
            SingleNameReference oRef3 = new SingleNameReference(new char[]{'o'}, p);
            EclipseHandlerUtil.setGeneratedBy(oRef3, source);
            callToSuper.arguments = new Expression[]{oRef3};
            UnaryExpression superNotEqual = new UnaryExpression((Expression)callToSuper, OperatorIds.NOT);
            EclipseHandlerUtil.setGeneratedBy(superNotEqual, source);
            falseLiteral = new FalseLiteral(pS, pE);
            EclipseHandlerUtil.setGeneratedBy(falseLiteral, source);
            returnFalse = new ReturnStatement((Expression)falseLiteral, pS, pE);
            EclipseHandlerUtil.setGeneratedBy(returnFalse, source);
            IfStatement ifSuperEquals = new IfStatement((Expression)superNotEqual, (Statement)returnFalse, pS, pE);
            EclipseHandlerUtil.setGeneratedBy(ifSuperEquals, source);
            statements.add(ifSuperEquals);
        }
        for (InclusionExclusionUtils.Included<EclipseNode, EqualsAndHashCode.Include> member : members) {
            Expression otherFieldAccessor;
            EclipseNode memberNode = member.getNode();
            boolean isMethod = memberNode.getKind() == AST.Kind.METHOD;
            TypeReference fType = EclipseHandlerUtil.getFieldType(memberNode, fieldAccess);
            char[] token = fType.getLastToken();
            Expression thisFieldAccessor = isMethod ? EclipseHandlerUtil.createMethodAccessor(memberNode, source) : EclipseHandlerUtil.createFieldAccessor(memberNode, fieldAccess, source);
            Expression expression = otherFieldAccessor = isMethod ? EclipseHandlerUtil.createMethodAccessor(memberNode, source, otherName) : EclipseHandlerUtil.createFieldAccessor(memberNode, fieldAccess, source, otherName);
            if (fType.dimensions() == 0 && token != null) {
                if (Arrays.equals(TypeConstants.FLOAT, token)) {
                    statements.add(this.generateCompareFloatOrDouble(thisFieldAccessor, otherFieldAccessor, "Float".toCharArray(), source));
                    continue;
                }
                if (Arrays.equals(TypeConstants.DOUBLE, token)) {
                    statements.add(this.generateCompareFloatOrDouble(thisFieldAccessor, otherFieldAccessor, "Double".toCharArray(), source));
                    continue;
                }
                if (BUILT_IN_TYPES.contains(new String(token))) {
                    EqualExpression fieldsNotEqual = new EqualExpression(thisFieldAccessor, otherFieldAccessor, OperatorIds.NOT_EQUAL);
                    EclipseHandlerUtil.setGeneratedBy(fieldsNotEqual, source);
                    FalseLiteral falseLiteral3 = new FalseLiteral(pS, pE);
                    EclipseHandlerUtil.setGeneratedBy(falseLiteral3, source);
                    ReturnStatement returnStatement = new ReturnStatement((Expression)falseLiteral3, pS, pE);
                    EclipseHandlerUtil.setGeneratedBy(returnStatement, source);
                    IfStatement ifStatement = new IfStatement((Expression)fieldsNotEqual, (Statement)returnStatement, pS, pE);
                    EclipseHandlerUtil.setGeneratedBy(ifStatement, source);
                    statements.add(ifStatement);
                    continue;
                }
                char[] thisDollarFieldName = ("this" + (isMethod ? "$$" : "$") + memberNode.getName()).toCharArray();
                char[] otherDollarFieldName = ("other" + (isMethod ? "$$" : "$") + memberNode.getName()).toCharArray();
                statements.add(this.createLocalDeclaration(source, thisDollarFieldName, EclipseHandlerUtil.generateQualifiedTypeRef(source, TypeConstants.JAVA_LANG_OBJECT), thisFieldAccessor));
                statements.add(this.createLocalDeclaration(source, otherDollarFieldName, EclipseHandlerUtil.generateQualifiedTypeRef(source, TypeConstants.JAVA_LANG_OBJECT), otherFieldAccessor));
                SingleNameReference this1 = new SingleNameReference(thisDollarFieldName, p);
                EclipseHandlerUtil.setGeneratedBy(this1, source);
                SingleNameReference this2 = new SingleNameReference(thisDollarFieldName, p);
                EclipseHandlerUtil.setGeneratedBy(this2, source);
                SingleNameReference other1 = new SingleNameReference(otherDollarFieldName, p);
                EclipseHandlerUtil.setGeneratedBy(other1, source);
                SingleNameReference other2 = new SingleNameReference(otherDollarFieldName, p);
                EclipseHandlerUtil.setGeneratedBy(other2, source);
                NullLiteral nullLiteral = new NullLiteral(pS, pE);
                EclipseHandlerUtil.setGeneratedBy(nullLiteral, source);
                EqualExpression fieldIsNull = new EqualExpression((Expression)this1, (Expression)nullLiteral, OperatorIds.EQUAL_EQUAL);
                nullLiteral = new NullLiteral(pS, pE);
                EclipseHandlerUtil.setGeneratedBy(nullLiteral, source);
                EqualExpression otherFieldIsntNull = new EqualExpression((Expression)other1, (Expression)nullLiteral, OperatorIds.NOT_EQUAL);
                MessageSend equalsCall = new MessageSend();
                equalsCall.sourceStart = pS;
                equalsCall.sourceEnd = pE;
                EclipseHandlerUtil.setGeneratedBy(equalsCall, source);
                equalsCall.receiver = this2;
                equalsCall.selector = "equals".toCharArray();
                equalsCall.arguments = new Expression[]{other2};
                UnaryExpression fieldsNotEqual = new UnaryExpression((Expression)equalsCall, OperatorIds.NOT);
                fieldsNotEqual.sourceStart = pS;
                fieldsNotEqual.sourceEnd = pE;
                EclipseHandlerUtil.setGeneratedBy(fieldsNotEqual, source);
                ConditionalExpression fullEquals = new ConditionalExpression((Expression)fieldIsNull, (Expression)otherFieldIsntNull, (Expression)fieldsNotEqual);
                fullEquals.sourceStart = pS;
                fullEquals.sourceEnd = pE;
                EclipseHandlerUtil.setGeneratedBy(fullEquals, source);
                FalseLiteral falseLiteral4 = new FalseLiteral(pS, pE);
                EclipseHandlerUtil.setGeneratedBy(falseLiteral4, source);
                ReturnStatement returnStatement = new ReturnStatement((Expression)falseLiteral4, pS, pE);
                EclipseHandlerUtil.setGeneratedBy(returnStatement, source);
                IfStatement ifStatement = new IfStatement((Expression)fullEquals, (Statement)returnStatement, pS, pE);
                EclipseHandlerUtil.setGeneratedBy(ifStatement, source);
                statements.add(ifStatement);
                continue;
            }
            if (fType.dimensions() <= 0 || token == null) continue;
            MessageSend arraysEqualCall = new MessageSend();
            arraysEqualCall.sourceStart = pS;
            arraysEqualCall.sourceEnd = pE;
            EclipseHandlerUtil.setGeneratedBy(arraysEqualCall, source);
            arraysEqualCall.receiver = EclipseHandlerUtil.generateQualifiedNameRef(source, TypeConstants.JAVA, TypeConstants.UTIL, "Arrays".toCharArray());
            arraysEqualCall.selector = fType.dimensions() > 1 || !BUILT_IN_TYPES.contains(new String(token)) ? "deepEquals".toCharArray() : "equals".toCharArray();
            arraysEqualCall.arguments = new Expression[]{thisFieldAccessor, otherFieldAccessor};
            UnaryExpression arraysNotEqual = new UnaryExpression((Expression)arraysEqualCall, OperatorIds.NOT);
            arraysNotEqual.sourceStart = pS;
            arraysNotEqual.sourceEnd = pE;
            EclipseHandlerUtil.setGeneratedBy(arraysNotEqual, source);
            FalseLiteral falseLiteral5 = new FalseLiteral(pS, pE);
            EclipseHandlerUtil.setGeneratedBy(falseLiteral5, source);
            ReturnStatement returnStatement = new ReturnStatement((Expression)falseLiteral5, pS, pE);
            EclipseHandlerUtil.setGeneratedBy(returnStatement, source);
            IfStatement ifStatement = new IfStatement((Expression)arraysNotEqual, (Statement)returnStatement, pS, pE);
            EclipseHandlerUtil.setGeneratedBy(ifStatement, source);
            statements.add(ifStatement);
        }
        TrueLiteral trueLiteral2 = new TrueLiteral(pS, pE);
        EclipseHandlerUtil.setGeneratedBy(trueLiteral2, source);
        ReturnStatement returnStatement = new ReturnStatement((Expression)trueLiteral2, pS, pE);
        EclipseHandlerUtil.setGeneratedBy(returnStatement, source);
        statements.add(returnStatement);
        method.statements = statements.toArray(new Statement[0]);
        return method;
    }

    public MethodDeclaration createCanEqual(EclipseNode type, ASTNode source, List<Annotation> onParam) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        char[] otherName = "other".toCharArray();
        MethodDeclaration method = new MethodDeclaration(((CompilationUnitDeclaration)((EclipseNode)type.top()).get()).compilationResult);
        EclipseHandlerUtil.setGeneratedBy(method, source);
        method.modifiers = EclipseHandlerUtil.toEclipseModifier(AccessLevel.PROTECTED);
        method.returnType = TypeReference.baseTypeReference((int)5, (int)0);
        method.returnType.sourceStart = pS;
        method.returnType.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy(method.returnType, source);
        method.selector = "canEqual".toCharArray();
        method.thrownExceptions = null;
        method.typeParameters = null;
        method.bits |= 0x800000;
        method.declarationSourceStart = method.sourceStart = source.sourceStart;
        method.bodyStart = method.sourceStart;
        method.declarationSourceEnd = method.sourceEnd = source.sourceEnd;
        method.bodyEnd = method.sourceEnd;
        QualifiedTypeReference objectRef = new QualifiedTypeReference(TypeConstants.JAVA_LANG_OBJECT, new long[]{p, p, p});
        EclipseHandlerUtil.setGeneratedBy(objectRef, source);
        method.arguments = new Argument[]{new Argument(otherName, 0L, (TypeReference)objectRef, 16)};
        method.arguments[0].sourceStart = pS;
        method.arguments[0].sourceEnd = pE;
        if (!onParam.isEmpty()) {
            method.arguments[0].annotations = onParam.toArray(new Annotation[0]);
        }
        EclipseHandlerUtil.createRelevantNullableAnnotation(type, method.arguments[0], method);
        EclipseHandlerUtil.setGeneratedBy(method.arguments[0], source);
        SingleNameReference otherRef = new SingleNameReference(otherName, p);
        EclipseHandlerUtil.setGeneratedBy(otherRef, source);
        TypeReference typeReference = this.createTypeReference(type, p, source, false);
        EclipseHandlerUtil.setGeneratedBy(typeReference, source);
        InstanceOfExpression instanceOf = new InstanceOfExpression((Expression)otherRef, typeReference);
        instanceOf.sourceStart = pS;
        instanceOf.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy(instanceOf, source);
        ReturnStatement returnStatement = new ReturnStatement((Expression)instanceOf, pS, pE);
        EclipseHandlerUtil.setGeneratedBy(returnStatement, source);
        method.statements = new Statement[]{returnStatement};
        if (EclipseHandlerUtil.getCheckerFrameworkVersion(type).generatePure()) {
            method.annotations = new Annotation[]{EclipseHandlerUtil.generateNamedAnnotation(source, "org.checkerframework.dataflow.qual.Pure")};
        }
        return method;
    }

    public IfStatement generateCompareFloatOrDouble(Expression thisRef, Expression otherRef, char[] floatOrDouble, ASTNode source) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        MessageSend floatCompare = new MessageSend();
        floatCompare.sourceStart = pS;
        floatCompare.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy(floatCompare, source);
        floatCompare.receiver = EclipseHandlerUtil.generateQualifiedNameRef(source, TypeConstants.JAVA, TypeConstants.LANG, floatOrDouble);
        floatCompare.selector = "compare".toCharArray();
        floatCompare.arguments = new Expression[]{thisRef, otherRef};
        IntLiteral int0 = EclipseHandlerUtil.makeIntLiteral("0".toCharArray(), source);
        EqualExpression ifFloatCompareIsNot0 = new EqualExpression((Expression)floatCompare, (Expression)int0, OperatorIds.NOT_EQUAL);
        ifFloatCompareIsNot0.sourceStart = pS;
        ifFloatCompareIsNot0.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy(ifFloatCompareIsNot0, source);
        FalseLiteral falseLiteral = new FalseLiteral(pS, pE);
        EclipseHandlerUtil.setGeneratedBy(falseLiteral, source);
        ReturnStatement returnFalse = new ReturnStatement((Expression)falseLiteral, pS, pE);
        EclipseHandlerUtil.setGeneratedBy(returnFalse, source);
        IfStatement ifStatement = new IfStatement((Expression)ifFloatCompareIsNot0, (Statement)returnFalse, pS, pE);
        EclipseHandlerUtil.setGeneratedBy(ifStatement, source);
        return ifStatement;
    }

    public Expression longToIntForHashCode(Expression ref1, Expression ref2, ASTNode source) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        IntLiteral int32 = EclipseHandlerUtil.makeIntLiteral("32".toCharArray(), source);
        BinaryExpression higherBits = new BinaryExpression(ref1, (Expression)int32, OperatorIds.UNSIGNED_RIGHT_SHIFT);
        EclipseHandlerUtil.setGeneratedBy(higherBits, source);
        BinaryExpression xorParts = new BinaryExpression(ref2, (Expression)higherBits, OperatorIds.XOR);
        EclipseHandlerUtil.setGeneratedBy(xorParts, source);
        TypeReference intRef = TypeReference.baseTypeReference((int)10, (int)0);
        intRef.sourceStart = pS;
        intRef.sourceEnd = pE;
        EclipseHandlerUtil.setGeneratedBy(intRef, source);
        CastExpression expr = EclipseHandlerUtil.makeCastExpression((Expression)xorParts, intRef, source);
        expr.sourceStart = pS;
        expr.sourceEnd = pE;
        return expr;
    }
}
