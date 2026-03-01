package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ConfigurationKeys;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.handlers.HandlerUtil;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.SetGeneratedByVisitor;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.CharLiteral;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.DoubleLiteral;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FalseLiteral;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.FloatLiteral;
import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
import org.eclipse.jdt.internal.compiler.ast.LongLiteral;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

public class HandleConstructor {
    private static final char[][] JAVA_BEANS_CONSTRUCTORPROPERTIES = new char[][]{"java".toCharArray(), "beans".toCharArray(), "ConstructorProperties".toCharArray()};
    private static final char[] DEFAULT_PREFIX = new char[]{'$', 'd', 'e', 'f', 'a', 'u', 'l', 't', '$'};

    private static List<EclipseNode> findRequiredFields(EclipseNode typeNode) {
        return HandleConstructor.findFields(typeNode, true);
    }

    private static List<EclipseNode> findFields(EclipseNode typeNode, boolean nullMarked) {
        ArrayList<EclipseNode> fields = new ArrayList<EclipseNode>();
        for (EclipseNode child : typeNode.down()) {
            boolean isNonNull;
            FieldDeclaration fieldDecl;
            if (child.getKind() != AST.Kind.FIELD || !EclipseHandlerUtil.filterField(fieldDecl = (FieldDeclaration)child.get())) continue;
            boolean isFinal = (fieldDecl.modifiers & 0x10) != 0;
            boolean bl = isNonNull = nullMarked && EclipseHandlerUtil.hasNonNullAnnotations(child);
            if (!isFinal && !isNonNull || fieldDecl.initialization != null) continue;
            fields.add(child);
        }
        return fields;
    }

    static List<EclipseNode> findAllFields(EclipseNode typeNode) {
        return HandleConstructor.findAllFields(typeNode, false);
    }

    static List<EclipseNode> findAllFields(EclipseNode typeNode, boolean evenFinalInitialized) {
        ArrayList<EclipseNode> fields = new ArrayList<EclipseNode>();
        for (EclipseNode child : typeNode.down()) {
            FieldDeclaration fieldDecl;
            if (child.getKind() != AST.Kind.FIELD || !EclipseHandlerUtil.filterField(fieldDecl = (FieldDeclaration)child.get()) || !evenFinalInitialized && (fieldDecl.modifiers & 0x10) != 0 && fieldDecl.initialization != null) continue;
            fields.add(child);
        }
        return fields;
    }

    static boolean checkLegality(EclipseNode typeNode, EclipseNode errorNode, String name) {
        if (!EclipseHandlerUtil.isClassOrEnum(typeNode)) {
            errorNode.addError(String.valueOf(name) + " is only supported on a class or an enum.");
            return false;
        }
        return true;
    }

    public void generateExtraNoArgsConstructor(EclipseNode typeNode, EclipseNode sourceNode) {
        if (!EclipseHandlerUtil.isDirectDescendantOfObject(typeNode)) {
            return;
        }
        Boolean v = typeNode.getAst().readConfiguration(ConfigurationKeys.NO_ARGS_CONSTRUCTOR_EXTRA_PRIVATE);
        if (v == null || !v.booleanValue()) {
            return;
        }
        this.generate(typeNode, AccessLevel.PRIVATE, Collections.<EclipseNode>emptyList(), true, null, SkipIfConstructorExists.NO, Collections.<Annotation>emptyList(), sourceNode, true);
    }

    public void generateRequiredArgsConstructor(EclipseNode typeNode, AccessLevel level, String staticName, SkipIfConstructorExists skipIfConstructorExists, List<Annotation> onConstructor, EclipseNode sourceNode) {
        this.generateConstructor(typeNode, level, HandleConstructor.findRequiredFields(typeNode), false, staticName, skipIfConstructorExists, onConstructor, sourceNode);
    }

    public void generateAllArgsConstructor(EclipseNode typeNode, AccessLevel level, String staticName, SkipIfConstructorExists skipIfConstructorExists, List<Annotation> onConstructor, EclipseNode sourceNode) {
        this.generateConstructor(typeNode, level, HandleConstructor.findAllFields(typeNode), false, staticName, skipIfConstructorExists, onConstructor, sourceNode);
    }

    public void generateConstructor(EclipseNode typeNode, AccessLevel level, List<EclipseNode> fieldsToParam, boolean forceDefaults, String staticName, SkipIfConstructorExists skipIfConstructorExists, List<Annotation> onConstructor, EclipseNode sourceNode) {
        this.generate(typeNode, level, fieldsToParam, forceDefaults, staticName, skipIfConstructorExists, onConstructor, sourceNode, false);
    }

    public void generate(EclipseNode typeNode, AccessLevel level, List<EclipseNode> fieldsToParam, boolean forceDefaults, String staticName, SkipIfConstructorExists skipIfConstructorExists, List<Annotation> onConstructor, EclipseNode sourceNode, boolean noArgs) {
        boolean staticConstrRequired;
        ASTNode source = (ASTNode)sourceNode.get();
        boolean bl = staticConstrRequired = staticName != null && !staticName.equals("");
        if (skipIfConstructorExists != SkipIfConstructorExists.NO) {
            for (EclipseNode child : typeNode.down()) {
                boolean skipGeneration;
                if (child.getKind() != AST.Kind.ANNOTATION) continue;
                boolean bl2 = skipGeneration = EclipseHandlerUtil.annotationTypeMatches(NoArgsConstructor.class, child) || EclipseHandlerUtil.annotationTypeMatches(AllArgsConstructor.class, child) || EclipseHandlerUtil.annotationTypeMatches(RequiredArgsConstructor.class, child);
                if (!skipGeneration && skipIfConstructorExists == SkipIfConstructorExists.YES) {
                    skipGeneration = EclipseHandlerUtil.annotationTypeMatches(Builder.class, child);
                }
                if (!skipGeneration) continue;
                if (staticConstrRequired) {
                    typeNode.addWarning("Ignoring static constructor name: explicit @XxxArgsConstructor annotation present; its `staticName` parameter will be used.", source.sourceStart, source.sourceEnd);
                }
                return;
            }
        }
        if (noArgs && HandleConstructor.noArgsConstructorExists(typeNode)) {
            return;
        }
        if (skipIfConstructorExists == SkipIfConstructorExists.NO || EclipseHandlerUtil.constructorExists(typeNode) == EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS) {
            ConstructorDeclaration constr = HandleConstructor.createConstructor(staticConstrRequired ? AccessLevel.PRIVATE : level, typeNode, fieldsToParam, forceDefaults, sourceNode, onConstructor);
            EclipseNode constructorNode = EclipseHandlerUtil.injectMethod(typeNode, (AbstractMethodDeclaration)constr);
            this.generateConstructorJavadoc(typeNode, constructorNode, fieldsToParam);
        }
        this.generateStaticConstructor(staticConstrRequired, typeNode, staticName, level, fieldsToParam, source);
    }

    private void generateStaticConstructor(boolean staticConstrRequired, EclipseNode typeNode, String staticName, AccessLevel level, Collection<EclipseNode> fields, ASTNode source) {
        if (staticConstrRequired) {
            MethodDeclaration staticConstr = this.createStaticConstructor(level, staticName, typeNode, fields, source);
            EclipseNode constructorNode = EclipseHandlerUtil.injectMethod(typeNode, (AbstractMethodDeclaration)staticConstr);
            this.generateConstructorJavadoc(typeNode, constructorNode, fields);
        }
    }

    private static boolean noArgsConstructorExists(EclipseNode node) {
        if ((node = EclipseHandlerUtil.upToTypeNode(node)) != null && node.get() instanceof TypeDeclaration) {
            TypeDeclaration typeDecl = (TypeDeclaration)node.get();
            if (typeDecl.methods != null) {
                AbstractMethodDeclaration[] abstractMethodDeclarationArray = typeDecl.methods;
                int n = typeDecl.methods.length;
                int n2 = 0;
                while (n2 < n) {
                    Argument[] arguments;
                    AbstractMethodDeclaration def = abstractMethodDeclarationArray[n2];
                    if (def instanceof ConstructorDeclaration && ((arguments = ((ConstructorDeclaration)def).arguments) == null || arguments.length == 0)) {
                        return true;
                    }
                    ++n2;
                }
            }
        }
        for (EclipseNode child : node.down()) {
            if (EclipseHandlerUtil.annotationTypeMatches(NoArgsConstructor.class, child)) {
                return true;
            }
            if (EclipseHandlerUtil.annotationTypeMatches(RequiredArgsConstructor.class, child) && HandleConstructor.findRequiredFields(node).isEmpty()) {
                return true;
            }
            if (!EclipseHandlerUtil.annotationTypeMatches(AllArgsConstructor.class, child) || !HandleConstructor.findAllFields(node).isEmpty()) continue;
            return true;
        }
        return false;
    }

    public static Annotation[] createConstructorProperties(ASTNode source, Collection<EclipseNode> fields) {
        if (fields.isEmpty()) {
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
        fieldNames.expressions = new Expression[fields.size()];
        int ctr = 0;
        for (EclipseNode field : fields) {
            char[] fieldName = EclipseHandlerUtil.removePrefixFromField(field);
            fieldNames.expressions[ctr] = new StringLiteral(fieldName, pS, pE, 0);
            EclipseHandlerUtil.setGeneratedBy(fieldNames.expressions[ctr], source);
            ++ctr;
        }
        ann.memberValue = fieldNames;
        EclipseHandlerUtil.setGeneratedBy(ann, source);
        EclipseHandlerUtil.setGeneratedBy(ann.memberValue, source);
        return new Annotation[]{ann};
    }

    private static final char[] prefixWith(char[] prefix, char[] name) {
        char[] out = new char[prefix.length + name.length];
        System.arraycopy(prefix, 0, out, 0, prefix.length);
        System.arraycopy(name, 0, out, prefix.length, name.length);
        return out;
    }

    public static ConstructorDeclaration createConstructor(AccessLevel level, EclipseNode type, Collection<EclipseNode> fieldsToParam, boolean forceDefaults, EclipseNode sourceNode, List<Annotation> onConstructor) {
        Assignment assignment;
        int e;
        FieldReference thisX;
        char[] rawName;
        FieldDeclaration field;
        Boolean v;
        List<EclipseNode> fieldsToExplicit;
        boolean isEnum;
        ASTNode source = (ASTNode)sourceNode.get();
        TypeDeclaration typeDeclaration = (TypeDeclaration)type.get();
        long p = (long)source.sourceStart << 32 | (long)source.sourceEnd;
        boolean bl = isEnum = (((TypeDeclaration)type.get()).modifiers & 0x4000) != 0;
        if (isEnum) {
            level = AccessLevel.PRIVATE;
        }
        List<EclipseNode> fieldsToDefault = HandleConstructor.fieldsNeedingBuilderDefaults(type, fieldsToParam);
        List<EclipseNode> list = fieldsToExplicit = forceDefaults ? HandleConstructor.fieldsNeedingExplicitDefaults(type, fieldsToParam) : Collections.emptyList();
        boolean addConstructorProperties = fieldsToParam.isEmpty() ? false : ((v = type.getAst().readConfiguration(ConfigurationKeys.ANY_CONSTRUCTOR_ADD_CONSTRUCTOR_PROPERTIES)) != null ? v.booleanValue() : Boolean.FALSE.equals(type.getAst().readConfiguration(ConfigurationKeys.ANY_CONSTRUCTOR_SUPPRESS_CONSTRUCTOR_PROPERTIES)));
        ConstructorDeclaration constructor = new ConstructorDeclaration(((CompilationUnitDeclaration)((EclipseNode)type.top()).get()).compilationResult);
        constructor.modifiers = EclipseHandlerUtil.toEclipseModifier(level);
        constructor.selector = typeDeclaration.name;
        constructor.constructorCall = new ExplicitConstructorCall(1);
        constructor.constructorCall.sourceStart = source.sourceStart;
        constructor.constructorCall.sourceEnd = source.sourceEnd;
        constructor.thrownExceptions = null;
        constructor.typeParameters = null;
        constructor.bits |= 0x800000;
        constructor.declarationSourceStart = constructor.sourceStart = source.sourceStart;
        constructor.bodyStart = constructor.sourceStart;
        constructor.declarationSourceEnd = constructor.sourceEnd = source.sourceEnd;
        constructor.bodyEnd = constructor.sourceEnd;
        constructor.arguments = null;
        ArrayList<Argument> params = new ArrayList<Argument>();
        ArrayList<Assignment> assigns = new ArrayList<Assignment>();
        ArrayList<Object> nullChecks = new ArrayList<Object>();
        for (EclipseNode fieldNode : fieldsToParam) {
            Statement nullCheck;
            field = (FieldDeclaration)fieldNode.get();
            rawName = field.name;
            char[] fieldName = EclipseHandlerUtil.removePrefixFromField(fieldNode);
            FieldReference thisX2 = new FieldReference(rawName, p);
            int s = (int)(p >> 32);
            int e2 = (int)p;
            thisX2.receiver = new ThisReference(s, e2);
            SingleNameReference assignmentExpr = new SingleNameReference(fieldName, p);
            Assignment assignment2 = new Assignment((Expression)thisX2, (Expression)assignmentExpr, (int)p);
            assignment2.sourceStart = (int)(p >> 32);
            assignment2.sourceEnd = assignment2.statementEnd = (int)(p >> 32);
            assigns.add(assignment2);
            long fieldPos = (long)field.sourceStart << 32 | (long)field.sourceEnd;
            Argument parameter = new Argument(fieldName, fieldPos, EclipseHandlerUtil.copyType(field.type, source), 16);
            Annotation[] copyableAnnotations = EclipseHandlerUtil.findCopyableAnnotations(fieldNode);
            if (EclipseHandlerUtil.hasNonNullAnnotations(fieldNode) && (nullCheck = EclipseHandlerUtil.generateNullCheck((AbstractVariableDeclaration)parameter, sourceNode, null)) != null) {
                nullChecks.add(nullCheck);
            }
            parameter.annotations = EclipseHandlerUtil.copyAnnotations(source, new Annotation[][]{copyableAnnotations});
            if (parameter.annotations != null) {
                parameter.bits |= 0x100000;
                constructor.bits |= 0x100000;
            }
            params.add(parameter);
        }
        for (EclipseNode fieldNode : fieldsToExplicit) {
            field = (FieldDeclaration)fieldNode.get();
            rawName = field.name;
            thisX = new FieldReference(rawName, p);
            int s = (int)(p >> 32);
            e = (int)p;
            thisX.receiver = new ThisReference(s, e);
            Expression assignmentExpr = HandleConstructor.getDefaultExpr(field.type, s, e);
            assignment = new Assignment((Expression)thisX, assignmentExpr, (int)p);
            assignment.sourceStart = (int)(p >> 32);
            assignment.sourceEnd = assignment.statementEnd = (int)(p >> 32);
            assigns.add(assignment);
        }
        for (EclipseNode fieldNode : fieldsToDefault) {
            field = (FieldDeclaration)fieldNode.get();
            rawName = field.name;
            thisX = new FieldReference(rawName, p);
            int s = (int)(p >> 32);
            e = (int)p;
            thisX.receiver = new ThisReference(s, e);
            MessageSend inv = new MessageSend();
            inv.sourceStart = source.sourceStart;
            inv.sourceEnd = source.sourceEnd;
            inv.receiver = new SingleNameReference(((TypeDeclaration)type.get()).name, 0L);
            inv.selector = HandleConstructor.prefixWith(DEFAULT_PREFIX, EclipseHandlerUtil.removePrefixFromField(fieldNode));
            assignment = new Assignment((Expression)thisX, (Expression)inv, (int)p);
            assignment.sourceStart = (int)(p >> 32);
            assignment.sourceEnd = assignment.statementEnd = (int)(p >> 32);
            assigns.add(assignment);
        }
        nullChecks.addAll(assigns);
        constructor.statements = nullChecks.isEmpty() ? null : nullChecks.toArray(new Statement[0]);
        constructor.arguments = params.isEmpty() ? null : params.toArray(new Argument[0]);
        Annotation[] constructorProperties = null;
        if (addConstructorProperties && !HandleConstructor.isLocalType(type)) {
            constructorProperties = HandleConstructor.createConstructorProperties(source, fieldsToParam);
        }
        constructor.annotations = EclipseHandlerUtil.copyAnnotations(source, onConstructor.toArray(new Annotation[0]), constructorProperties);
        constructor.traverse((ASTVisitor)new SetGeneratedByVisitor(source), typeDeclaration.scope);
        return constructor;
    }

    private static List<EclipseNode> fieldsNeedingBuilderDefaults(EclipseNode type, Collection<EclipseNode> fieldsToParam) {
        ArrayList<EclipseNode> out = new ArrayList<EclipseNode>();
        block0: for (EclipseNode node : type.down()) {
            if (node.getKind() != AST.Kind.FIELD) continue;
            FieldDeclaration fd = (FieldDeclaration)node.get();
            if ((fd.modifiers & 8) != 0) continue;
            for (EclipseNode ftp : fieldsToParam) {
                if (node == ftp) continue block0;
            }
            if (!EclipseHandlerUtil.hasAnnotation(Builder.Default.class, node)) continue;
            out.add(node);
        }
        return out;
    }

    private static List<EclipseNode> fieldsNeedingExplicitDefaults(EclipseNode type, Collection<EclipseNode> fieldsToParam) {
        ArrayList<EclipseNode> out = new ArrayList<EclipseNode>();
        block0: for (EclipseNode node : type.down()) {
            if (node.getKind() != AST.Kind.FIELD) continue;
            FieldDeclaration fd = (FieldDeclaration)node.get();
            if (fd.initialization != null || (fd.modifiers & 0x10) == 0 || (fd.modifiers & 8) != 0) continue;
            for (EclipseNode ftp : fieldsToParam) {
                if (node == ftp) continue block0;
            }
            if (EclipseHandlerUtil.hasAnnotation(Builder.Default.class, node)) continue;
            out.add(node);
        }
        return out;
    }

    private static Expression getDefaultExpr(TypeReference type, int s, int e) {
        boolean array = type instanceof ArrayTypeReference;
        if (array) {
            return new NullLiteral(s, e);
        }
        char[] lastToken = type.getLastToken();
        if (Arrays.equals(TypeConstants.BOOLEAN, lastToken)) {
            return new FalseLiteral(s, e);
        }
        if (Arrays.equals(TypeConstants.CHAR, lastToken)) {
            return new CharLiteral(new char[]{'\'', '\\', '0', '\''}, s, e);
        }
        if (Arrays.equals(TypeConstants.BYTE, lastToken) || Arrays.equals(TypeConstants.SHORT, lastToken) || Arrays.equals(TypeConstants.INT, lastToken)) {
            return IntLiteral.buildIntLiteral((char[])new char[]{'0'}, (int)s, (int)e);
        }
        if (Arrays.equals(TypeConstants.LONG, lastToken)) {
            return LongLiteral.buildLongLiteral((char[])new char[]{'0', 'L'}, (int)s, (int)e);
        }
        if (Arrays.equals(TypeConstants.FLOAT, lastToken)) {
            return new FloatLiteral(new char[]{'0', 'F'}, s, e);
        }
        if (Arrays.equals(TypeConstants.DOUBLE, lastToken)) {
            return new DoubleLiteral(new char[]{'0', 'D'}, s, e);
        }
        return new NullLiteral(s, e);
    }

    public static boolean isLocalType(EclipseNode type) {
        AST.Kind kind = ((EclipseNode)type.up()).getKind();
        if (kind == AST.Kind.COMPILATION_UNIT) {
            return false;
        }
        if (kind == AST.Kind.TYPE) {
            return HandleConstructor.isLocalType((EclipseNode)type.up());
        }
        return true;
    }

    public MethodDeclaration createStaticConstructor(AccessLevel level, String name, EclipseNode type, Collection<EclipseNode> fields, ASTNode source) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        MethodDeclaration constructor = new MethodDeclaration(((CompilationUnitDeclaration)((EclipseNode)type.top()).get()).compilationResult);
        constructor.modifiers = EclipseHandlerUtil.toEclipseModifier(level) | 8;
        TypeDeclaration typeDecl = (TypeDeclaration)type.get();
        constructor.returnType = EclipseHandlerUtil.namePlusTypeParamsToTypeReference(type, typeDecl.typeParameters, p);
        constructor.annotations = null;
        if (EclipseHandlerUtil.getCheckerFrameworkVersion(type).generateUnique()) {
            int len = constructor.returnType.getTypeName().length;
            constructor.returnType.annotations = new Annotation[len][];
            constructor.returnType.annotations[len - 1] = new Annotation[]{EclipseHandlerUtil.generateNamedAnnotation(source, "org.checkerframework.common.aliasing.qual.Unique")};
        }
        constructor.selector = name.toCharArray();
        constructor.thrownExceptions = null;
        constructor.typeParameters = EclipseHandlerUtil.copyTypeParams(((TypeDeclaration)type.get()).typeParameters, source);
        constructor.bits |= 0x800000;
        constructor.declarationSourceStart = constructor.sourceStart = source.sourceStart;
        constructor.bodyStart = constructor.sourceStart;
        constructor.declarationSourceEnd = constructor.sourceEnd = source.sourceEnd;
        constructor.bodyEnd = constructor.sourceEnd;
        ArrayList<Argument> params = new ArrayList<Argument>();
        ArrayList<SingleNameReference> assigns = new ArrayList<SingleNameReference>();
        AllocationExpression statement = new AllocationExpression();
        statement.sourceStart = pS;
        statement.sourceEnd = pE;
        statement.type = EclipseHandlerUtil.copyType(constructor.returnType, source);
        for (EclipseNode fieldNode : fields) {
            FieldDeclaration field = (FieldDeclaration)fieldNode.get();
            long fieldPos = (long)field.sourceStart << 32 | (long)field.sourceEnd;
            SingleNameReference nameRef = new SingleNameReference(field.name, fieldPos);
            assigns.add(nameRef);
            Argument parameter = new Argument(field.name, fieldPos, EclipseHandlerUtil.copyType(field.type, source), 16);
            parameter.annotations = EclipseHandlerUtil.copyAnnotations(source, new Annotation[][]{EclipseHandlerUtil.findCopyableAnnotations(fieldNode)});
            if (parameter.annotations != null) {
                parameter.bits |= 0x100000;
                constructor.bits |= 0x100000;
            }
            params.add(parameter);
        }
        statement.arguments = assigns.isEmpty() ? null : assigns.toArray(new Expression[0]);
        constructor.arguments = params.isEmpty() ? null : params.toArray(new Argument[0]);
        constructor.statements = new Statement[]{new ReturnStatement((Expression)statement, (int)(p >> 32), (int)p)};
        EclipseHandlerUtil.createRelevantNonNullAnnotation(type, constructor);
        constructor.traverse((ASTVisitor)new SetGeneratedByVisitor(source), typeDecl.scope);
        return constructor;
    }

    private void generateConstructorJavadoc(EclipseNode typeNode, EclipseNode constructorNode, Collection<EclipseNode> fields) {
        if (fields.isEmpty()) {
            return;
        }
        String constructorJavadoc = HandlerUtil.getConstructorJavadocHeader(typeNode.getName());
        boolean fieldDescriptionAdded = false;
        for (EclipseNode fieldNode : fields) {
            String fieldJavadoc;
            String paramName = String.valueOf(EclipseHandlerUtil.removePrefixFromField(fieldNode));
            String paramJavadoc = HandlerUtil.getConstructorParameterJavadoc(paramName, fieldJavadoc = EclipseHandlerUtil.getDocComment(fieldNode));
            if (paramJavadoc == null) {
                paramJavadoc = "@param " + paramName;
            } else {
                fieldDescriptionAdded = true;
            }
            constructorJavadoc = HandlerUtil.addJavadocLine(constructorJavadoc, paramJavadoc);
        }
        if (fieldDescriptionAdded) {
            EclipseHandlerUtil.setDocComment(typeNode, constructorNode, constructorJavadoc);
        }
    }

    public static class HandleAllArgsConstructor
    extends EclipseAnnotationHandler<AllArgsConstructor> {
        private static final String NAME = AllArgsConstructor.class.getSimpleName();
        private HandleConstructor handleConstructor = new HandleConstructor();

        @Override
        public void handle(AnnotationValues<AllArgsConstructor> annotation, Annotation ast, EclipseNode annotationNode) {
            List<Annotation> onConstructor;
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.ALL_ARGS_CONSTRUCTOR_FLAG_USAGE, "@AllArgsConstructor", ConfigurationKeys.ANY_CONSTRUCTOR_FLAG_USAGE, "any @xArgsConstructor");
            EclipseNode typeNode = (EclipseNode)annotationNode.up();
            if (!HandleConstructor.checkLegality(typeNode, annotationNode, NAME)) {
                return;
            }
            AllArgsConstructor ann = annotation.getInstance();
            AccessLevel level = ann.access();
            if (level == AccessLevel.NONE) {
                return;
            }
            String staticName = ann.staticName();
            if (annotation.isExplicit("suppressConstructorProperties")) {
                annotationNode.addError("This deprecated feature is no longer supported. Remove it; you can create a lombok.config file with 'lombok.anyConstructor.suppressConstructorProperties = true'.");
            }
            if (!(onConstructor = EclipseHandlerUtil.unboxAndRemoveAnnotationParameter(ast, "onConstructor", "@AllArgsConstructor(onConstructor", annotationNode)).isEmpty()) {
                HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.ON_X_FLAG_USAGE, "@AllArgsConstructor(onConstructor=...)");
            }
            this.handleConstructor.generateConstructor(typeNode, level, HandleConstructor.findAllFields(typeNode), false, staticName, SkipIfConstructorExists.NO, onConstructor, annotationNode);
        }
    }

    public static class HandleNoArgsConstructor
    extends EclipseAnnotationHandler<NoArgsConstructor> {
        private static final String NAME = NoArgsConstructor.class.getSimpleName();
        private HandleConstructor handleConstructor = new HandleConstructor();

        @Override
        public void handle(AnnotationValues<NoArgsConstructor> annotation, Annotation ast, EclipseNode annotationNode) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.NO_ARGS_CONSTRUCTOR_FLAG_USAGE, "@NoArgsConstructor", ConfigurationKeys.ANY_CONSTRUCTOR_FLAG_USAGE, "any @xArgsConstructor");
            EclipseNode typeNode = (EclipseNode)annotationNode.up();
            if (!HandleConstructor.checkLegality(typeNode, annotationNode, NAME)) {
                return;
            }
            NoArgsConstructor ann = annotation.getInstance();
            AccessLevel level = ann.access();
            String staticName = ann.staticName();
            if (level == AccessLevel.NONE) {
                return;
            }
            boolean force = ann.force();
            List<Annotation> onConstructor = EclipseHandlerUtil.unboxAndRemoveAnnotationParameter(ast, "onConstructor", "@NoArgsConstructor(onConstructor", annotationNode);
            if (!onConstructor.isEmpty()) {
                HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.ON_X_FLAG_USAGE, "@NoArgsConstructor(onConstructor=...)");
            }
            this.handleConstructor.generateConstructor(typeNode, level, Collections.<EclipseNode>emptyList(), force, staticName, SkipIfConstructorExists.NO, onConstructor, annotationNode);
        }
    }

    public static class HandleRequiredArgsConstructor
    extends EclipseAnnotationHandler<RequiredArgsConstructor> {
        private static final String NAME = RequiredArgsConstructor.class.getSimpleName();
        private HandleConstructor handleConstructor = new HandleConstructor();

        @Override
        public void handle(AnnotationValues<RequiredArgsConstructor> annotation, Annotation ast, EclipseNode annotationNode) {
            List<Annotation> onConstructor;
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.REQUIRED_ARGS_CONSTRUCTOR_FLAG_USAGE, "@RequiredArgsConstructor", ConfigurationKeys.ANY_CONSTRUCTOR_FLAG_USAGE, "any @xArgsConstructor");
            EclipseNode typeNode = (EclipseNode)annotationNode.up();
            if (!HandleConstructor.checkLegality(typeNode, annotationNode, NAME)) {
                return;
            }
            RequiredArgsConstructor ann = annotation.getInstance();
            AccessLevel level = ann.access();
            if (level == AccessLevel.NONE) {
                return;
            }
            String staticName = ann.staticName();
            if (annotation.isExplicit("suppressConstructorProperties")) {
                annotationNode.addError("This deprecated feature is no longer supported. Remove it; you can create a lombok.config file with 'lombok.anyConstructor.suppressConstructorProperties = true'.");
            }
            if (!(onConstructor = EclipseHandlerUtil.unboxAndRemoveAnnotationParameter(ast, "onConstructor", "@RequiredArgsConstructor(onConstructor", annotationNode)).isEmpty()) {
                HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.ON_X_FLAG_USAGE, "@RequiredArgsConstructor(onConstructor=...)");
            }
            this.handleConstructor.generateConstructor(typeNode, level, HandleConstructor.findRequiredFields(typeNode), false, staticName, SkipIfConstructorExists.NO, onConstructor, annotationNode);
        }
    }

    public static enum SkipIfConstructorExists {
        YES,
        NO,
        I_AM_BUILDER;

    }
}
