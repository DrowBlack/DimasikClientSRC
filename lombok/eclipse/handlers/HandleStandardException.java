package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.handlers.HandlerUtil;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.SetGeneratedByVisitor;
import lombok.experimental.StandardException;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

public class HandleStandardException
extends EclipseAnnotationHandler<StandardException> {
    private static final char[][] JAVA_BEANS_CONSTRUCTORPROPERTIES = new char[][]{"java".toCharArray(), "beans".toCharArray(), "ConstructorProperties".toCharArray()};
    private static final char[] MESSAGE = "message".toCharArray();
    private static final char[] CAUSE = "cause".toCharArray();
    private static final char[] GET_MESSAGE = "getMessage".toCharArray();
    private static final char[] INIT_CAUSE = "initCause".toCharArray();

    @Override
    public void handle(AnnotationValues<StandardException> annotation, Annotation ast, EclipseNode annotationNode) {
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.STANDARD_EXCEPTION_FLAG_USAGE, "@StandardException");
        EclipseNode typeNode = (EclipseNode)annotationNode.up();
        if (!EclipseHandlerUtil.isClass(typeNode)) {
            annotationNode.addError("@StandardException is only supported on a class");
            return;
        }
        TypeDeclaration classDef = (TypeDeclaration)typeNode.get();
        if (classDef.superclass == null) {
            annotationNode.addError("@StandardException requires that you extend a Throwable type");
            return;
        }
        AccessLevel access = annotation.getInstance().access();
        this.generateNoArgsConstructor(typeNode, access, annotationNode);
        this.generateMsgOnlyConstructor(typeNode, access, annotationNode);
        this.generateCauseOnlyConstructor(typeNode, access, annotationNode);
        this.generateFullConstructor(typeNode, access, annotationNode);
    }

    private void generateNoArgsConstructor(EclipseNode typeNode, AccessLevel level, EclipseNode source) {
        if (HandleStandardException.hasConstructor(typeNode, new Class[0]) != EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS) {
            return;
        }
        int pS = ((ASTNode)source.get()).sourceStart;
        int pE = ((ASTNode)source.get()).sourceEnd;
        CastExpression messageArgument = new CastExpression((Expression)new NullLiteral(pS, pE), EclipseHandlerUtil.generateQualifiedTypeRef((ASTNode)source.get(), TypeConstants.JAVA_LANG_STRING));
        CastExpression causeArgument = new CastExpression((Expression)new NullLiteral(pS, pE), EclipseHandlerUtil.generateQualifiedTypeRef((ASTNode)source.get(), TypeConstants.JAVA_LANG_THROWABLE));
        ExplicitConstructorCall explicitCall = new ExplicitConstructorCall(3);
        explicitCall.arguments = new Expression[]{messageArgument, causeArgument};
        ConstructorDeclaration constructor = HandleStandardException.createConstructor(level, typeNode, false, false, source, explicitCall, null);
        EclipseHandlerUtil.injectMethod(typeNode, (AbstractMethodDeclaration)constructor);
    }

    private void generateMsgOnlyConstructor(EclipseNode typeNode, AccessLevel level, EclipseNode source) {
        if (HandleStandardException.hasConstructor(typeNode, String.class) != EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS) {
            return;
        }
        int pS = ((ASTNode)source.get()).sourceStart;
        int pE = ((ASTNode)source.get()).sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        SingleNameReference messageArgument = new SingleNameReference(MESSAGE, p);
        CastExpression causeArgument = new CastExpression((Expression)new NullLiteral(pS, pE), EclipseHandlerUtil.generateQualifiedTypeRef((ASTNode)source.get(), TypeConstants.JAVA_LANG_THROWABLE));
        ExplicitConstructorCall explicitCall = new ExplicitConstructorCall(3);
        explicitCall.arguments = new Expression[]{messageArgument, causeArgument};
        ConstructorDeclaration constructor = HandleStandardException.createConstructor(level, typeNode, true, false, source, explicitCall, null);
        EclipseHandlerUtil.injectMethod(typeNode, (AbstractMethodDeclaration)constructor);
    }

    private void generateCauseOnlyConstructor(EclipseNode typeNode, AccessLevel level, EclipseNode source) {
        if (HandleStandardException.hasConstructor(typeNode, Throwable.class) != EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS) {
            return;
        }
        int pS = ((ASTNode)source.get()).sourceStart;
        int pE = ((ASTNode)source.get()).sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        ExplicitConstructorCall explicitCall = new ExplicitConstructorCall(3);
        EqualExpression causeNotNull = new EqualExpression((Expression)new SingleNameReference(CAUSE, p), (Expression)new NullLiteral(pS, pE), OperatorIds.NOT_EQUAL);
        MessageSend causeDotGetMessage = new MessageSend();
        causeDotGetMessage.sourceStart = pS;
        causeDotGetMessage.sourceEnd = pE;
        causeDotGetMessage.receiver = new SingleNameReference(CAUSE, p);
        causeDotGetMessage.selector = GET_MESSAGE;
        ConditionalExpression messageExpr = new ConditionalExpression((Expression)causeNotNull, (Expression)causeDotGetMessage, (Expression)new NullLiteral(pS, pE));
        explicitCall.arguments = new Expression[]{messageExpr, new SingleNameReference(CAUSE, p)};
        ConstructorDeclaration constructor = HandleStandardException.createConstructor(level, typeNode, false, true, source, explicitCall, null);
        EclipseHandlerUtil.injectMethod(typeNode, (AbstractMethodDeclaration)constructor);
    }

    private void generateFullConstructor(EclipseNode typeNode, AccessLevel level, EclipseNode source) {
        if (HandleStandardException.hasConstructor(typeNode, String.class, Throwable.class) != EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS) {
            return;
        }
        int pS = ((ASTNode)source.get()).sourceStart;
        int pE = ((ASTNode)source.get()).sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        ExplicitConstructorCall explicitCall = new ExplicitConstructorCall(2);
        explicitCall.arguments = new Expression[]{new SingleNameReference(MESSAGE, p)};
        EqualExpression causeNotNull = new EqualExpression((Expression)new SingleNameReference(CAUSE, p), (Expression)new NullLiteral(pS, pE), OperatorIds.NOT_EQUAL);
        MessageSend causeDotInitCause = new MessageSend();
        causeDotInitCause.sourceStart = pS;
        causeDotInitCause.sourceEnd = pE;
        causeDotInitCause.receiver = new SuperReference(pS, pE);
        causeDotInitCause.selector = INIT_CAUSE;
        causeDotInitCause.arguments = new Expression[]{new SingleNameReference(CAUSE, p)};
        IfStatement ifs = new IfStatement((Expression)causeNotNull, (Statement)causeDotInitCause, pS, pE);
        ConstructorDeclaration constructor = HandleStandardException.createConstructor(level, typeNode, true, true, source, explicitCall, (Statement)ifs);
        EclipseHandlerUtil.injectMethod(typeNode, (AbstractMethodDeclaration)constructor);
    }

    public static EclipseHandlerUtil.MemberExistsResult hasConstructor(EclipseNode node, Class<?> ... paramTypes) {
        if ((node = EclipseHandlerUtil.upToTypeNode(node)) != null && node.get() instanceof TypeDeclaration) {
            TypeDeclaration typeDecl = (TypeDeclaration)node.get();
            if (typeDecl.methods != null) {
                AbstractMethodDeclaration[] abstractMethodDeclarationArray = typeDecl.methods;
                int n = typeDecl.methods.length;
                int n2 = 0;
                while (n2 < n) {
                    AbstractMethodDeclaration def = abstractMethodDeclarationArray[n2];
                    if (def instanceof ConstructorDeclaration && (def.bits & 0x80) == 0 && HandleStandardException.paramsMatch(node, def.arguments, paramTypes)) {
                        return EclipseHandlerUtil.getGeneratedBy((ASTNode)def) == null ? EclipseHandlerUtil.MemberExistsResult.EXISTS_BY_USER : EclipseHandlerUtil.MemberExistsResult.EXISTS_BY_LOMBOK;
                    }
                    ++n2;
                }
            }
        }
        return EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS;
    }

    private static boolean paramsMatch(EclipseNode node, Argument[] a, Class<?>[] b) {
        if (a == null) {
            return b == null || b.length == 0;
        }
        if (b == null) {
            return a.length == 0;
        }
        if (a.length != b.length) {
            return false;
        }
        int i = 0;
        while (i < a.length) {
            if (!EclipseHandlerUtil.typeMatches(b[i], node, a[i].type)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static Annotation[] createConstructorProperties(ASTNode source, boolean msgParam, boolean causeParam) {
        if (!msgParam && !causeParam) {
            return null;
        }
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        long[] poss = new long[3];
        Arrays.fill(poss, p);
        QualifiedTypeReference constructorPropertiesType = new QualifiedTypeReference(JAVA_BEANS_CONSTRUCTORPROPERTIES, poss);
        EclipseHandlerUtil.setGeneratedBy(constructorPropertiesType, source);
        SingleMemberAnnotation ann = new SingleMemberAnnotation((TypeReference)constructorPropertiesType, pS);
        ann.declarationSourceEnd = pE;
        ArrayInitializer fieldNames = new ArrayInitializer();
        fieldNames.sourceStart = pS;
        fieldNames.sourceEnd = pE;
        fieldNames.expressions = new Expression[msgParam && causeParam ? 2 : 1];
        int ctr = 0;
        if (msgParam) {
            fieldNames.expressions[ctr] = new StringLiteral(MESSAGE, pS, pE, 0);
            EclipseHandlerUtil.setGeneratedBy(fieldNames.expressions[ctr], source);
            ++ctr;
        }
        if (causeParam) {
            fieldNames.expressions[ctr] = new StringLiteral(CAUSE, pS, pE, 0);
            EclipseHandlerUtil.setGeneratedBy(fieldNames.expressions[ctr], source);
            ++ctr;
        }
        ann.memberValue = fieldNames;
        EclipseHandlerUtil.setGeneratedBy(ann, source);
        EclipseHandlerUtil.setGeneratedBy(ann.memberValue, source);
        return new Annotation[]{ann};
    }

    public static ConstructorDeclaration createConstructor(AccessLevel level, EclipseNode typeNode, boolean msgParam, boolean causeParam, EclipseNode sourceNode, ExplicitConstructorCall explicitCall, Statement extra) {
        Statement[] statementArray;
        Argument parameter;
        QualifiedTypeReference typeRef;
        Boolean v;
        ASTNode source = (ASTNode)sourceNode.get();
        TypeDeclaration typeDeclaration = (TypeDeclaration)typeNode.get();
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        boolean addConstructorProperties = !msgParam && !causeParam || HandleStandardException.isLocalType(typeNode) ? false : ((v = typeNode.getAst().readConfiguration(ConfigurationKeys.ANY_CONSTRUCTOR_ADD_CONSTRUCTOR_PROPERTIES)) != null ? v.booleanValue() : Boolean.FALSE.equals(typeNode.getAst().readConfiguration(ConfigurationKeys.ANY_CONSTRUCTOR_SUPPRESS_CONSTRUCTOR_PROPERTIES)));
        ConstructorDeclaration constructor = new ConstructorDeclaration(((CompilationUnitDeclaration)((EclipseNode)typeNode.top()).get()).compilationResult);
        constructor.modifiers = EclipseHandlerUtil.toEclipseModifier(level);
        constructor.selector = typeDeclaration.name;
        constructor.thrownExceptions = null;
        constructor.typeParameters = null;
        constructor.bits |= 0x800000;
        constructor.declarationSourceStart = constructor.sourceStart = pS;
        constructor.bodyStart = constructor.sourceStart;
        constructor.declarationSourceEnd = constructor.sourceEnd = pE;
        constructor.bodyEnd = constructor.sourceEnd;
        constructor.arguments = null;
        ArrayList<Argument> params = new ArrayList<Argument>();
        if (msgParam) {
            typeRef = new QualifiedTypeReference(TypeConstants.JAVA_LANG_STRING, new long[]{p, p, p});
            parameter = new Argument(MESSAGE, p, (TypeReference)typeRef, 16);
            params.add(parameter);
        }
        if (causeParam) {
            typeRef = new QualifiedTypeReference(TypeConstants.JAVA_LANG_THROWABLE, new long[]{p, p, p});
            parameter = new Argument(CAUSE, p, (TypeReference)typeRef, 16);
            params.add(parameter);
        }
        explicitCall.sourceStart = pS;
        explicitCall.sourceEnd = pE;
        constructor.constructorCall = explicitCall;
        if (extra != null) {
            Statement[] statementArray2 = new Statement[1];
            statementArray = statementArray2;
            statementArray2[0] = extra;
        } else {
            statementArray = null;
        }
        constructor.statements = statementArray;
        constructor.arguments = params.isEmpty() ? null : params.toArray(new Argument[0]);
        Annotation[] constructorProperties = null;
        if (addConstructorProperties) {
            constructorProperties = HandleStandardException.createConstructorProperties(source, msgParam, causeParam);
        }
        constructor.annotations = EclipseHandlerUtil.copyAnnotations(source, new Annotation[][]{constructorProperties});
        constructor.traverse((ASTVisitor)new SetGeneratedByVisitor(source), typeDeclaration.scope);
        return constructor;
    }

    public static boolean isLocalType(EclipseNode type) {
        AST.Kind kind = ((EclipseNode)type.up()).getKind();
        if (kind == AST.Kind.COMPILATION_UNIT) {
            return false;
        }
        if (kind == AST.Kind.TYPE) {
            return HandleStandardException.isLocalType((EclipseNode)type.up());
        }
        return true;
    }
}
