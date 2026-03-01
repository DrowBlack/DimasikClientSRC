package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.ConfigurationKeys;
import lombok.Singular;
import lombok.ToString;
import lombok.Value;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.core.handlers.HandlerUtil;
import lombok.core.handlers.InclusionExclusionUtils;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.EclipseSingularsRecipes;
import lombok.eclipse.handlers.HandleBuilder;
import lombok.eclipse.handlers.HandleConstructor;
import lombok.eclipse.handlers.HandleSetter;
import lombok.eclipse.handlers.HandleToString;
import lombok.eclipse.handlers.SetGeneratedByVisitor;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FalseLiteral;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

@HandlerPriority(value=-1024)
public class HandleSuperBuilder
extends EclipseAnnotationHandler<SuperBuilder> {
    private static final char[] SELF_METHOD_NAME = "self".toCharArray();
    private static final char[] FILL_VALUES_METHOD_NAME = "$fillValuesFrom".toCharArray();
    private static final char[] FILL_VALUES_STATIC_METHOD_NAME = "$fillValuesFromInstanceIntoBuilder".toCharArray();
    private static final char[] INSTANCE_VARIABLE_NAME = "instance".toCharArray();
    private static final String BUILDER_VARIABLE_NAME_STRING = "b";
    private static final char[] BUILDER_VARIABLE_NAME = "b".toCharArray();

    @Override
    public void handle(AnnotationValues<SuperBuilder> annotation, Annotation ast, EclipseNode annotationNode) {
        boolean isAbstract;
        MethodDeclaration md;
        boolean generateBuilderMethod;
        HandlerUtil.handleExperimentalFlagUsage(annotationNode, ConfigurationKeys.SUPERBUILDER_FLAG_USAGE, "@SuperBuilder");
        SuperBuilderJob job = new SuperBuilderJob();
        job.sourceNode = annotationNode;
        job.source = ast;
        job.checkerFramework = EclipseHandlerUtil.getCheckerFrameworkVersion(annotationNode);
        job.isStatic = true;
        SuperBuilder annInstance = annotation.getInstance();
        job.init(annotation, annInstance, annotationNode);
        if (job.builderMethodName.isEmpty()) {
            generateBuilderMethod = false;
        } else {
            if (!HandlerUtil.checkName("builderMethodName", job.builderMethodName, annotationNode)) {
                return;
            }
            generateBuilderMethod = true;
        }
        if (!HandlerUtil.checkName("buildMethodName", job.buildMethodName, annotationNode)) {
            return;
        }
        EclipseNode parent = (EclipseNode)annotationNode.up();
        job.builderFields = new ArrayList();
        boolean addCleaning = false;
        ArrayList<EclipseNode> nonFinalNonDefaultedFields = null;
        if (!EclipseHandlerUtil.isClass(parent)) {
            annotationNode.addError("@SuperBuilder is only supported on classes.");
            return;
        }
        if (!EclipseHandlerUtil.isStaticAllowed(parent)) {
            annotationNode.addError("@SuperBuilder is not supported on non-static nested classes.");
            return;
        }
        job.parentType = parent;
        TypeDeclaration td = (TypeDeclaration)parent.get();
        ArrayList<EclipseNode> allFields = new ArrayList<EclipseNode>();
        boolean valuePresent = EclipseHandlerUtil.hasAnnotation(Value.class, parent) || EclipseHandlerUtil.hasAnnotation("lombok.experimental.Value", parent);
        for (EclipseNode fieldNode : HandleConstructor.findAllFields(parent, true)) {
            FieldDeclaration fd = (FieldDeclaration)fieldNode.get();
            EclipseNode isDefault = EclipseHandlerUtil.findAnnotation(Builder.Default.class, fieldNode);
            boolean isFinal = (fd.modifiers & 0x10) != 0 || valuePresent && !EclipseHandlerUtil.hasAnnotation(NonFinal.class, fieldNode);
            Annotation[] copyableAnnotations = EclipseHandlerUtil.findCopyableAnnotations(fieldNode);
            HandleBuilder.BuilderFieldData bfd = new HandleBuilder.BuilderFieldData();
            bfd.rawName = fieldNode.getName().toCharArray();
            bfd.name = EclipseHandlerUtil.removePrefixFromField(fieldNode);
            bfd.builderFieldName = bfd.name;
            bfd.annotations = EclipseHandlerUtil.copyAnnotations((ASTNode)fd, new Annotation[][]{copyableAnnotations});
            bfd.type = fd.type;
            bfd.singularData = this.getSingularData(fieldNode, (ASTNode)ast, annInstance.setterPrefix());
            bfd.originalFieldNode = fieldNode;
            if (bfd.singularData != null && isDefault != null) {
                isDefault.addError("@Builder.Default and @Singular cannot be mixed.");
                isDefault = null;
            }
            if (fd.initialization == null && isDefault != null) {
                isDefault.addWarning("@Builder.Default requires an initializing expression (' = something;').");
                isDefault = null;
            }
            if (fd.initialization != null && isDefault == null) {
                if (isFinal) continue;
                if (nonFinalNonDefaultedFields == null) {
                    nonFinalNonDefaultedFields = new ArrayList<EclipseNode>();
                }
                nonFinalNonDefaultedFields.add(fieldNode);
            }
            if (isDefault != null) {
                bfd.nameOfDefaultProvider = HandleSuperBuilder.prefixWith(HandleBuilder.DEFAULT_PREFIX, bfd.name);
                bfd.nameOfSetFlag = HandleSuperBuilder.prefixWith(bfd.name, HandleBuilder.SET_PREFIX);
                bfd.builderFieldName = HandleSuperBuilder.prefixWith(bfd.name, HandleBuilder.VALUE_PREFIX);
                MethodDeclaration md2 = HandleBuilder.generateDefaultProvider(bfd.nameOfDefaultProvider, td.typeParameters, fieldNode, (ASTNode)ast);
                if (md2 != null) {
                    EclipseHandlerUtil.injectMethod(parent, (AbstractMethodDeclaration)md2);
                }
            }
            this.addObtainVia(bfd, fieldNode);
            job.builderFields.add(bfd);
            allFields.add(fieldNode);
        }
        job.typeParams = td.typeParameters != null ? td.typeParameters : new TypeParameter[]{};
        TypeReference buildMethodReturnType = job.createBuilderParentTypeReference();
        String classGenericName = "C";
        String builderGenericName = "B";
        Set<String> usedNames = this.gatherUsedTypeNames(job.typeParams, td);
        classGenericName = this.generateNonclashingNameFor(classGenericName, usedNames);
        builderGenericName = this.generateNonclashingNameFor(builderGenericName, usedNames);
        TypeParameter[] paddedTypeParameters = new TypeParameter[job.typeParams.length + 2];
        System.arraycopy(job.typeParams, 0, paddedTypeParameters, 0, job.typeParams.length);
        TypeParameter c = new TypeParameter();
        c.name = classGenericName.toCharArray();
        c.type = EclipseHandlerUtil.cloneSelfType(job.parentType, job.source);
        paddedTypeParameters[paddedTypeParameters.length - 2] = c;
        TypeParameter b = new TypeParameter();
        b.name = builderGenericName.toCharArray();
        b.type = EclipseHandlerUtil.cloneSelfType(job.parentType, job.source);
        paddedTypeParameters[paddedTypeParameters.length - 1] = b;
        job.builderTypeParams_ = paddedTypeParameters;
        job.builderTypeParams = paddedTypeParameters;
        TypeReference extendsClause = td.superclass;
        ParameterizedQualifiedTypeReference superclassBuilderClass = null;
        TypeReference[] typeArguments = new TypeReference[]{new SingleTypeReference(classGenericName.toCharArray(), 0L), new SingleTypeReference(builderGenericName.toCharArray(), 0L)};
        if (extendsClause instanceof QualifiedTypeReference) {
            QualifiedTypeReference qualifiedTypeReference = (QualifiedTypeReference)extendsClause;
            char[] superclassClassName = qualifiedTypeReference.getLastToken();
            String builderClassNameTemplate = HandleBuilder.BuilderJob.getBuilderClassNameTemplate(annotationNode, null);
            String superclassBuilderClassName = job.replaceBuilderClassName(superclassClassName, builderClassNameTemplate);
            char[][] tokens = (char[][])Arrays.copyOf(qualifiedTypeReference.tokens, qualifiedTypeReference.tokens.length + 1);
            tokens[tokens.length - 1] = superclassBuilderClassName.toCharArray();
            long[] poss = new long[tokens.length];
            Arrays.fill(poss, job.getPos());
            TypeReference[] superclassTypeArgs = this.getTypeParametersFrom(extendsClause);
            TypeReference[][] typeArgsForTokens = new TypeReference[tokens.length][];
            typeArgsForTokens[typeArgsForTokens.length - 1] = this.mergeTypeReferences(superclassTypeArgs, typeArguments);
            superclassBuilderClass = new ParameterizedQualifiedTypeReference(tokens, (TypeReference[][])typeArgsForTokens, 0, poss);
        } else if (extendsClause != null) {
            char[] superclassClassName = extendsClause.getTypeName()[0];
            String builderClassNameTemplate = HandleBuilder.BuilderJob.getBuilderClassNameTemplate(annotationNode, null);
            String superclassBuilderClassName = job.replaceBuilderClassName(superclassClassName, builderClassNameTemplate);
            char[][] tokens = new char[][]{superclassClassName, superclassBuilderClassName.toCharArray()};
            long[] poss = new long[tokens.length];
            Arrays.fill(poss, job.getPos());
            TypeReference[] superclassTypeArgs = this.getTypeParametersFrom(extendsClause);
            TypeReference[][] typeArgsForTokens = new TypeReference[tokens.length][];
            typeArgsForTokens[typeArgsForTokens.length - 1] = this.mergeTypeReferences(superclassTypeArgs, typeArguments);
            superclassBuilderClass = new ParameterizedQualifiedTypeReference((char[][])tokens, (TypeReference[][])typeArgsForTokens, 0, poss);
        }
        job.builderAbstractClassName = job.builderClassName = job.replaceBuilderClassName(td.name);
        job.builderClassNameArr = job.builderAbstractClassName.toCharArray();
        job.builderAbstractClassNameArr = job.builderClassNameArr;
        job.builderImplClassName = String.valueOf(job.builderAbstractClassName) + "Impl";
        job.builderImplClassNameArr = job.builderImplClassName.toCharArray();
        if (!this.constructorExists(parent, job.builderClassName)) {
            this.generateBuilderBasedConstructor(job, superclassBuilderClass != null);
        }
        job.builderAbstractType = this.findInnerClass(parent, job.builderClassName);
        if (job.builderAbstractType == null) {
            job.builderAbstractType = this.generateBuilderAbstractClass(job, (TypeReference)superclassBuilderClass, classGenericName, builderGenericName);
        } else {
            TypeDeclaration builderTypeDeclaration = (TypeDeclaration)job.builderAbstractType.get();
            if ((builderTypeDeclaration.modifiers & 0x408) == 0) {
                annotationNode.addError("Existing Builder must be an abstract static inner class.");
                return;
            }
            EclipseHandlerUtil.sanityCheckForMethodGeneratingAnnotationsOnBuilderClass(job.builderAbstractType, annotationNode);
            for (Object bfd : job.builderFields) {
                EclipseSingularsRecipes.EclipseSingularizer singularizer;
                EclipseSingularsRecipes.SingularData sd = ((HandleBuilder.BuilderFieldData)bfd).singularData;
                if (sd == null || (singularizer = sd.getSingularizer()) == null || !singularizer.checkForAlreadyExistingNodesAndGenerateError(job.builderAbstractType, sd)) continue;
                ((HandleBuilder.BuilderFieldData)bfd).singularData = null;
            }
        }
        for (HandleBuilder.BuilderFieldData bfd : job.builderFields) {
            if (bfd.singularData != null && bfd.singularData.getSingularizer() != null && bfd.singularData.getSingularizer().requiresCleaning()) {
                addCleaning = true;
                break;
            }
            if (bfd.obtainVia == null) continue;
            if (bfd.obtainVia.field().isEmpty() == bfd.obtainVia.method().isEmpty()) {
                bfd.obtainViaNode.addError("The syntax is either @ObtainVia(field = \"fieldName\") or @ObtainVia(method = \"methodName\").");
                return;
            }
            if (!bfd.obtainVia.method().isEmpty() || !bfd.obtainVia.isStatic()) continue;
            bfd.obtainViaNode.addError("@ObtainVia(isStatic = true) is not valid unless 'method' has been set.");
            return;
        }
        job.setBuilderToAbstract();
        this.generateBuilderFields(job);
        if (addCleaning) {
            FieldDeclaration cleanDecl = new FieldDeclaration(HandleBuilder.CLEAN_FIELD_NAME, 0, -1);
            cleanDecl.declarationSourceEnd = -1;
            cleanDecl.modifiers = 2;
            cleanDecl.type = TypeReference.baseTypeReference((int)5, (int)0);
            EclipseHandlerUtil.injectFieldAndMarkGenerated(job.builderType, cleanDecl);
        }
        if (job.toBuilder) {
            EclipseHandlerUtil.injectMethod(job.builderType, (AbstractMethodDeclaration)this.generateFillValuesMethod(job, superclassBuilderClass != null, builderGenericName, classGenericName));
            EclipseHandlerUtil.injectMethod(job.builderType, (AbstractMethodDeclaration)this.generateStaticFillValuesMethod(job, annInstance.setterPrefix()));
        }
        for (HandleBuilder.BuilderFieldData bfd : job.builderFields) {
            this.generateSetterMethodsForBuilder(job, bfd, builderGenericName, annInstance.setterPrefix());
        }
        EclipseHandlerUtil.injectMethod(job.builderType, (AbstractMethodDeclaration)this.generateAbstractSelfMethod(job, superclassBuilderClass != null, builderGenericName));
        job.setBuilderToAbstract();
        EclipseHandlerUtil.injectMethod(job.builderType, (AbstractMethodDeclaration)this.generateAbstractBuildMethod(job, superclassBuilderClass != null, classGenericName));
        if (EclipseHandlerUtil.methodExists("toString", job.builderType, 0) == EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS) {
            ArrayList<InclusionExclusionUtils.Included<EclipseNode, ToString.Include>> fieldNodes = new ArrayList<InclusionExclusionUtils.Included<EclipseNode, ToString.Include>>();
            for (Object bfd : job.builderFields) {
                for (EclipseNode f : ((HandleBuilder.BuilderFieldData)bfd).createdFields) {
                    fieldNodes.add(new InclusionExclusionUtils.Included<EclipseNode, Object>(f, null, true, false));
                }
            }
            md = HandleToString.createToString(job.builderType, fieldNodes, true, superclassBuilderClass != null, (ASTNode)ast, HandlerUtil.FieldAccess.ALWAYS_FIELD);
            if (md != null) {
                EclipseHandlerUtil.injectMethod(job.builderType, (AbstractMethodDeclaration)md);
            }
        }
        if (addCleaning) {
            job.setBuilderToAbstract();
            EclipseHandlerUtil.injectMethod(job.builderType, (AbstractMethodDeclaration)this.generateCleanMethod(job));
        }
        boolean bl = isAbstract = (td.modifiers & 0x400) != 0;
        if (isAbstract) {
            return;
        }
        job.builderImplType = this.findInnerClass(parent, job.builderImplClassName);
        if (job.builderImplType == null) {
            job.builderImplType = this.generateBuilderImplClass(job, job.builderImplClassName);
        } else {
            TypeDeclaration builderImplTypeDeclaration = (TypeDeclaration)job.builderImplType.get();
            if ((builderImplTypeDeclaration.modifiers & 0x400) != 0 || (builderImplTypeDeclaration.modifiers & 8) == 0) {
                annotationNode.addError("Existing BuilderImpl must be a non-abstract static inner class.");
                return;
            }
            EclipseHandlerUtil.sanityCheckForMethodGeneratingAnnotationsOnBuilderClass(job.builderImplType, annotationNode);
        }
        job.setBuilderToImpl();
        if (job.toBuilder) {
            switch (EclipseHandlerUtil.methodExists("toBuilder", job.parentType, 0)) {
                case EXISTS_BY_USER: {
                    break;
                }
                case NOT_EXISTS: {
                    EclipseHandlerUtil.injectMethod(parent, (AbstractMethodDeclaration)this.generateToBuilderMethod(job));
                }
            }
        }
        job.setBuilderToImpl();
        EclipseHandlerUtil.injectMethod(job.builderImplType, (AbstractMethodDeclaration)this.generateSelfMethod(job));
        if (EclipseHandlerUtil.methodExists(job.buildMethodName, job.builderImplType, -1) == EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS) {
            job.setBuilderToImpl();
            EclipseHandlerUtil.injectMethod(job.builderImplType, (AbstractMethodDeclaration)this.generateBuildMethod(job, buildMethodReturnType));
        }
        if (generateBuilderMethod && EclipseHandlerUtil.methodExists(job.builderMethodName, parent, -1) != EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS) {
            generateBuilderMethod = false;
        }
        if (generateBuilderMethod && (md = this.generateBuilderMethod(job)) != null) {
            EclipseHandlerUtil.injectMethod(parent, (AbstractMethodDeclaration)md);
        }
        if (nonFinalNonDefaultedFields != null && generateBuilderMethod) {
            for (EclipseNode fieldNode : nonFinalNonDefaultedFields) {
                fieldNode.addWarning("@SuperBuilder will ignore the initializing expression entirely. If you want the initializing expression to serve as default, add @Builder.Default. If it is not supposed to be settable during building, make the field final.");
            }
        }
    }

    private EclipseNode generateBuilderAbstractClass(HandleBuilder.BuilderJob job, TypeReference superclassBuilderClass, String classGenericName, String builderGenericName) {
        TypeDeclaration parent = (TypeDeclaration)job.parentType.get();
        TypeDeclaration builder = new TypeDeclaration(parent.compilationResult);
        builder.bits |= 0x800000;
        builder.modifiers |= 0x409;
        builder.name = job.builderClassNameArr;
        builder.typeParameters = Arrays.copyOf(EclipseHandlerUtil.copyTypeParams(job.typeParams, job.source), job.typeParams.length + 2);
        TypeParameter o = new TypeParameter();
        o.name = classGenericName.toCharArray();
        o.type = EclipseHandlerUtil.cloneSelfType(job.parentType, job.source);
        builder.typeParameters[builder.typeParameters.length - 2] = o;
        o = new TypeParameter();
        o.name = builderGenericName.toCharArray();
        TypeReference[] typerefs = this.appendBuilderTypeReferences(job.typeParams, classGenericName, builderGenericName);
        o.type = EclipseHandlerUtil.generateParameterizedTypeReference(job.parentType, job.builderClassNameArr, false, typerefs, 0L);
        builder.typeParameters[builder.typeParameters.length - 1] = o;
        if (superclassBuilderClass != null) {
            builder.superclass = EclipseHandlerUtil.copyType(superclassBuilderClass, job.source);
        }
        builder.createDefaultConstructor(false, true);
        builder.traverse((ASTVisitor)new SetGeneratedByVisitor(job.source), null);
        return EclipseHandlerUtil.injectType(job.parentType, builder);
    }

    private EclipseNode generateBuilderImplClass(HandleBuilder.BuilderJob job, String builderImplClass) {
        TypeDeclaration parent = (TypeDeclaration)job.parentType.get();
        TypeDeclaration builder = new TypeDeclaration(parent.compilationResult);
        builder.bits |= 0x800000;
        builder.modifiers |= 0x1A;
        builder.name = builderImplClass.toCharArray();
        if (job.typeParams != null && job.typeParams.length > 0) {
            builder.typeParameters = EclipseHandlerUtil.copyTypeParams(job.typeParams, job.source);
        }
        if (job.builderClassName != null) {
            TypeReference[] typeArgs = new TypeReference[job.typeParams.length + 2];
            int i = 0;
            while (i < job.typeParams.length) {
                typeArgs[i] = new SingleTypeReference(job.typeParams[i].name, 0L);
                ++i;
            }
            typeArgs[typeArgs.length - 2] = EclipseHandlerUtil.cloneSelfType(job.parentType, job.source);
            typeArgs[typeArgs.length - 1] = HandleSuperBuilder.createTypeReferenceWithTypeParameters(job.parentType, builderImplClass, job.typeParams);
            builder.superclass = EclipseHandlerUtil.generateParameterizedTypeReference(job.parentType, job.builderClassNameArr, false, typeArgs, 0L);
        }
        builder.createDefaultConstructor(false, true);
        builder.traverse((ASTVisitor)new SetGeneratedByVisitor(job.source), null);
        return EclipseHandlerUtil.injectType(job.parentType, builder);
    }

    private void generateBuilderBasedConstructor(HandleBuilder.BuilderJob job, boolean callBuilderBasedSuperConstructor) {
        TypeDeclaration typeDeclaration = (TypeDeclaration)job.parentType.get();
        long p = job.getPos();
        ConstructorDeclaration constructor = new ConstructorDeclaration(((CompilationUnitDeclaration)((EclipseNode)job.parentType.top()).get()).compilationResult);
        constructor.modifiers = EclipseHandlerUtil.toEclipseModifier(AccessLevel.PROTECTED);
        constructor.selector = typeDeclaration.name;
        if (callBuilderBasedSuperConstructor) {
            constructor.constructorCall = new ExplicitConstructorCall(2);
            constructor.constructorCall.arguments = new Expression[]{new SingleNameReference(BUILDER_VARIABLE_NAME, p)};
        } else {
            constructor.constructorCall = new ExplicitConstructorCall(1);
        }
        constructor.constructorCall.sourceStart = job.source.sourceStart;
        constructor.constructorCall.sourceEnd = job.source.sourceEnd;
        constructor.thrownExceptions = null;
        constructor.typeParameters = null;
        constructor.bits |= 0x800000;
        constructor.declarationSourceStart = constructor.sourceStart = job.source.sourceStart;
        constructor.bodyStart = constructor.sourceStart;
        constructor.declarationSourceEnd = constructor.sourceEnd = job.source.sourceEnd;
        constructor.bodyEnd = constructor.sourceEnd;
        TypeReference[] wildcards = new TypeReference[]{new Wildcard(0), new Wildcard(0)};
        TypeReference builderType = EclipseHandlerUtil.generateParameterizedTypeReference(job.parentType, job.builderClassNameArr, false, this.mergeToTypeReferences(job.typeParams, wildcards), p);
        constructor.arguments = new Argument[]{new Argument(BUILDER_VARIABLE_NAME, p, builderType, 16)};
        ArrayList<Statement> statements = new ArrayList<Statement>();
        for (HandleBuilder.BuilderFieldData fieldNode : job.builderFields) {
            Statement nullCheck;
            SingleNameReference assignmentExpr;
            FieldReference fieldInThis = new FieldReference(fieldNode.rawName, p);
            int s = (int)(p >> 32);
            int e = (int)p;
            fieldInThis.receiver = new ThisReference(s, e);
            if (fieldNode.singularData != null && fieldNode.singularData.getSingularizer() != null) {
                fieldNode.singularData.getSingularizer().appendBuildCode(fieldNode.singularData, job.parentType, statements, fieldNode.builderFieldName, BUILDER_VARIABLE_NAME_STRING);
                assignmentExpr = new SingleNameReference(fieldNode.builderFieldName, p);
            } else {
                char[][] variableInBuilder = new char[][]{BUILDER_VARIABLE_NAME, fieldNode.builderFieldName};
                long[] positions = new long[]{p, p};
                assignmentExpr = new QualifiedNameReference((char[][])variableInBuilder, positions, s, e);
            }
            Assignment assignment = new Assignment((Expression)fieldInThis, (Expression)assignmentExpr, (int)p);
            if (fieldNode.nameOfSetFlag != null) {
                char[][] setVariableInBuilder = new char[][]{BUILDER_VARIABLE_NAME, fieldNode.nameOfSetFlag};
                long[] positions = new long[]{p, p};
                QualifiedNameReference setVariableInBuilderRef = new QualifiedNameReference((char[][])setVariableInBuilder, positions, s, e);
                MessageSend defaultMethodCall = new MessageSend();
                defaultMethodCall.sourceStart = job.source.sourceStart;
                defaultMethodCall.sourceEnd = job.source.sourceEnd;
                defaultMethodCall.receiver = EclipseHandlerUtil.generateNameReference(job.parentType, 0L);
                defaultMethodCall.selector = fieldNode.nameOfDefaultProvider;
                defaultMethodCall.typeArguments = this.typeParameterNames(((TypeDeclaration)job.parentType.get()).typeParameters);
                Assignment defaultAssignment = new Assignment((Expression)fieldInThis, (Expression)defaultMethodCall, (int)p);
                IfStatement ifBlockForDefault = new IfStatement((Expression)setVariableInBuilderRef, (Statement)assignment, (Statement)defaultAssignment, s, e);
                statements.add((Statement)ifBlockForDefault);
            } else {
                statements.add((Statement)assignment);
            }
            if (!EclipseHandlerUtil.hasNonNullAnnotations(fieldNode.originalFieldNode) || (nullCheck = EclipseHandlerUtil.generateNullCheck((AbstractVariableDeclaration)((FieldDeclaration)fieldNode.originalFieldNode.get()), job.sourceNode, null)) == null) continue;
            statements.add(nullCheck);
        }
        Statement[] statementArray = constructor.statements = statements.isEmpty() ? null : statements.toArray(new Statement[0]);
        if (job.checkerFramework.generateSideEffectFree()) {
            constructor.annotations = new Annotation[]{EclipseHandlerUtil.generateNamedAnnotation(job.source, "org.checkerframework.dataflow.qual.SideEffectFree")};
        }
        constructor.traverse((ASTVisitor)new SetGeneratedByVisitor(job.source), typeDeclaration.scope);
        EclipseHandlerUtil.injectMethod(job.parentType, (AbstractMethodDeclaration)constructor);
    }

    private MethodDeclaration generateBuilderMethod(SuperBuilderJob job) {
        int pS = job.source.sourceStart;
        int pE = job.source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        MethodDeclaration out = job.createNewMethodDeclaration();
        out.selector = job.builderMethodName.toCharArray();
        out.modifiers = 9;
        out.bits |= 0x800000;
        if (job.typeParams != null && job.typeParams.length > 0) {
            out.typeParameters = EclipseHandlerUtil.copyTypeParams(job.typeParams, job.source);
        }
        TypeReference[] wildcards = new TypeReference[]{new Wildcard(0), new Wildcard(0)};
        out.returnType = EclipseHandlerUtil.generateParameterizedTypeReference(job.parentType, job.builderAbstractClassNameArr, false, this.mergeToTypeReferences(job.typeParams, wildcards), p);
        if (job.checkerFramework.generateUnique()) {
            int len = out.returnType.getTypeName().length;
            out.returnType.annotations = new Annotation[len][];
            out.returnType.annotations[len - 1] = new Annotation[]{EclipseHandlerUtil.generateNamedAnnotation(job.source, "org.checkerframework.common.aliasing.qual.Unique")};
        }
        AllocationExpression invoke = new AllocationExpression();
        invoke.type = EclipseHandlerUtil.namePlusTypeParamsToTypeReference(job.parentType, job.builderImplClassNameArr, false, job.typeParams, p);
        out.statements = new Statement[]{new ReturnStatement((Expression)invoke, pS, pE)};
        if (job.checkerFramework.generateSideEffectFree()) {
            out.annotations = new Annotation[]{EclipseHandlerUtil.generateNamedAnnotation(job.source, "org.checkerframework.dataflow.qual.SideEffectFree")};
        }
        EclipseHandlerUtil.createRelevantNonNullAnnotation(job.parentType, out);
        out.traverse((ASTVisitor)new SetGeneratedByVisitor(job.source), ((TypeDeclaration)job.parentType.get()).scope);
        return out;
    }

    private MethodDeclaration generateToBuilderMethod(SuperBuilderJob job) {
        int pS = job.source.sourceStart;
        int pE = job.source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        MethodDeclaration out = job.createNewMethodDeclaration();
        out.selector = HandleBuilder.TO_BUILDER_METHOD_NAME;
        out.modifiers = 1;
        out.bits |= 0x800000;
        TypeReference[] wildcards = new TypeReference[]{new Wildcard(0), new Wildcard(0)};
        out.returnType = EclipseHandlerUtil.generateParameterizedTypeReference(job.parentType, job.builderAbstractClassNameArr, false, this.mergeToTypeReferences(job.typeParams, wildcards), p);
        if (job.checkerFramework.generateUnique()) {
            int len = out.returnType.getTypeName().length;
            out.returnType.annotations = new Annotation[len][];
            out.returnType.annotations[len - 1] = new Annotation[]{EclipseHandlerUtil.generateNamedAnnotation(job.source, "org.checkerframework.common.aliasing.qual.Unique")};
        }
        AllocationExpression newClass = new AllocationExpression();
        newClass.type = EclipseHandlerUtil.namePlusTypeParamsToTypeReference(job.parentType, job.builderImplClassNameArr, false, job.typeParams, p);
        MessageSend invokeFillMethod = new MessageSend();
        invokeFillMethod.receiver = newClass;
        invokeFillMethod.selector = FILL_VALUES_METHOD_NAME;
        invokeFillMethod.arguments = new Expression[]{new ThisReference(0, 0)};
        out.statements = new Statement[]{new ReturnStatement((Expression)invokeFillMethod, pS, pE)};
        if (job.checkerFramework.generateSideEffectFree()) {
            out.annotations = new Annotation[]{EclipseHandlerUtil.generateNamedAnnotation(job.source, "org.checkerframework.dataflow.qual.SideEffectFree")};
        }
        EclipseHandlerUtil.createRelevantNonNullAnnotation(job.parentType, out);
        out.traverse((ASTVisitor)new SetGeneratedByVisitor(job.source), ((TypeDeclaration)job.parentType.get()).scope);
        return out;
    }

    private MethodDeclaration generateFillValuesMethod(SuperBuilderJob job, boolean inherited, String builderGenericName, String classGenericName) {
        MethodDeclaration out = job.createNewMethodDeclaration();
        out.selector = FILL_VALUES_METHOD_NAME;
        out.bits |= 0x800000;
        out.modifiers = 4;
        if (inherited) {
            out.annotations = new Annotation[]{EclipseHandlerUtil.makeMarkerAnnotation(TypeConstants.JAVA_LANG_OVERRIDE, (ASTNode)job.parentType.get())};
        }
        out.returnType = new SingleTypeReference(builderGenericName.toCharArray(), 0L);
        SingleTypeReference builderType = new SingleTypeReference(classGenericName.toCharArray(), 0L);
        out.arguments = new Argument[]{new Argument(INSTANCE_VARIABLE_NAME, 0L, (TypeReference)builderType, 16)};
        ArrayList<Object> body = new ArrayList<Object>();
        if (inherited) {
            MessageSend callToSuper = new MessageSend();
            callToSuper.receiver = new SuperReference(0, 0);
            callToSuper.selector = FILL_VALUES_METHOD_NAME;
            callToSuper.arguments = new Expression[]{new SingleNameReference(INSTANCE_VARIABLE_NAME, 0L)};
            body.add(callToSuper);
        }
        MessageSend callStaticFillValuesMethod = new MessageSend();
        callStaticFillValuesMethod.receiver = EclipseHandlerUtil.generateNameReference(job.parentType, job.builderAbstractClassNameArr, 0L);
        callStaticFillValuesMethod.selector = FILL_VALUES_STATIC_METHOD_NAME;
        callStaticFillValuesMethod.arguments = new Expression[]{new SingleNameReference(INSTANCE_VARIABLE_NAME, 0L), new ThisReference(0, 0)};
        body.add(callStaticFillValuesMethod);
        MessageSend returnCall = new MessageSend();
        returnCall.receiver = ThisReference.implicitThis();
        returnCall.selector = SELF_METHOD_NAME;
        body.add(new ReturnStatement((Expression)returnCall, 0, 0));
        out.statements = body.isEmpty() ? null : body.toArray(new Statement[0]);
        return out;
    }

    private MethodDeclaration generateStaticFillValuesMethod(HandleBuilder.BuilderJob job, String setterPrefix) {
        MethodDeclaration out = job.createNewMethodDeclaration();
        out.selector = FILL_VALUES_STATIC_METHOD_NAME;
        out.bits |= 0x800000;
        out.modifiers = 10;
        out.returnType = TypeReference.baseTypeReference((int)6, (int)0);
        TypeReference[] wildcards = new TypeReference[]{new Wildcard(0), new Wildcard(0)};
        TypeReference builderType = EclipseHandlerUtil.generateParameterizedTypeReference(job.parentType, job.builderClassNameArr, false, this.mergeToTypeReferences(job.typeParams, wildcards), 0L);
        Argument builderArgument = new Argument(BUILDER_VARIABLE_NAME, 0L, builderType, 16);
        TypeReference[] typerefs = null;
        if (job.typeParams.length > 0) {
            typerefs = new TypeReference[job.typeParams.length];
            int i = 0;
            while (i < job.typeParams.length) {
                typerefs[i] = new SingleTypeReference(job.typeParams[i].name, 0L);
                ++i;
            }
        }
        long p = job.getPos();
        TypeReference parentArgument = typerefs == null ? EclipseHandlerUtil.generateTypeReference(job.parentType, p) : EclipseHandlerUtil.generateParameterizedTypeReference(job.parentType, typerefs, p);
        out.arguments = new Argument[]{new Argument(INSTANCE_VARIABLE_NAME, 0L, parentArgument, 16), builderArgument};
        if (job.typeParams.length > 0) {
            out.typeParameters = EclipseHandlerUtil.copyTypeParams(job.typeParams, job.source);
        }
        ArrayList<MessageSend> body = new ArrayList<MessageSend>();
        for (HandleBuilder.BuilderFieldData bfd : job.builderFields) {
            MessageSend exec = this.createSetterCallWithInstanceValue(bfd, job.parentType, job.source, setterPrefix);
            body.add(exec);
        }
        out.statements = body.isEmpty() ? null : body.toArray(new Statement[0]);
        out.traverse((ASTVisitor)new SetGeneratedByVisitor(job.source), null);
        return out;
    }

    private MessageSend createSetterCallWithInstanceValue(HandleBuilder.BuilderFieldData bfd, EclipseNode type, ASTNode source, String setterPrefix) {
        char[] setterName = HandlerUtil.buildAccessorName(type, setterPrefix, String.valueOf(bfd.name)).toCharArray();
        MessageSend ms = new MessageSend();
        Expression[] tgt = new Expression[bfd.singularData == null ? 1 : 2];
        if (bfd.obtainVia == null || !bfd.obtainVia.field().isEmpty()) {
            char[] fieldName = bfd.obtainVia == null ? bfd.rawName : bfd.obtainVia.field().toCharArray();
            int i = 0;
            while (i < tgt.length) {
                FieldReference fr = new FieldReference(fieldName, 0L);
                fr.receiver = new SingleNameReference(INSTANCE_VARIABLE_NAME, 0L);
                tgt[i] = fr;
                ++i;
            }
        } else {
            String obtainName = bfd.obtainVia.method();
            boolean obtainIsStatic = bfd.obtainVia.isStatic();
            int i = 0;
            while (i < tgt.length) {
                MessageSend obtainExpr = new MessageSend();
                obtainExpr.receiver = obtainIsStatic ? EclipseHandlerUtil.generateNameReference(type, 0L) : new SingleNameReference(INSTANCE_VARIABLE_NAME, 0L);
                obtainExpr.selector = obtainName.toCharArray();
                if (obtainIsStatic) {
                    obtainExpr.arguments = new Expression[]{new SingleNameReference(INSTANCE_VARIABLE_NAME, 0L)};
                }
                tgt[i] = obtainExpr;
                ++i;
            }
        }
        if (bfd.singularData == null) {
            ms.arguments = tgt;
        } else {
            EqualExpression ifNull = new EqualExpression(tgt[0], (Expression)new NullLiteral(0, 0), OperatorIds.EQUAL_EQUAL);
            MessageSend emptyCollection = bfd.singularData.getSingularizer().getEmptyExpression(bfd.singularData.getTargetFqn(), bfd.singularData, type, source);
            ms.arguments = new Expression[]{new ConditionalExpression((Expression)ifNull, (Expression)emptyCollection, tgt[1])};
        }
        ms.receiver = new SingleNameReference(BUILDER_VARIABLE_NAME, 0L);
        ms.selector = setterName;
        return ms;
    }

    private MethodDeclaration generateAbstractSelfMethod(HandleBuilder.BuilderJob job, boolean override, String builderGenericName) {
        MarkerAnnotation sefAnn;
        MethodDeclaration out = job.createNewMethodDeclaration();
        out.selector = SELF_METHOD_NAME;
        out.bits |= 0x800000;
        out.modifiers = 0x1000404;
        MarkerAnnotation overrideAnn = override ? EclipseHandlerUtil.makeMarkerAnnotation(TypeConstants.JAVA_LANG_OVERRIDE, (ASTNode)job.parentType.get()) : null;
        MarkerAnnotation markerAnnotation = sefAnn = job.checkerFramework.generatePure() ? EclipseHandlerUtil.generateNamedAnnotation((ASTNode)job.parentType.get(), "org.checkerframework.dataflow.qual.Pure") : null;
        if (overrideAnn != null && sefAnn != null) {
            out.annotations = new Annotation[]{overrideAnn, sefAnn};
        } else if (overrideAnn != null) {
            out.annotations = new Annotation[]{overrideAnn};
        } else if (sefAnn != null) {
            out.annotations = new Annotation[]{sefAnn};
        }
        out.returnType = new SingleTypeReference(builderGenericName.toCharArray(), 0L);
        EclipseHandlerUtil.addCheckerFrameworkReturnsReceiver(out.returnType, (ASTNode)job.parentType.get(), job.checkerFramework);
        return out;
    }

    private MethodDeclaration generateSelfMethod(HandleBuilder.BuilderJob job) {
        MethodDeclaration out = job.createNewMethodDeclaration();
        out.selector = SELF_METHOD_NAME;
        out.bits |= 0x800000;
        out.modifiers = 4;
        MarkerAnnotation overrideAnn = EclipseHandlerUtil.makeMarkerAnnotation(TypeConstants.JAVA_LANG_OVERRIDE, (ASTNode)job.builderType.get());
        MarkerAnnotation sefAnn = job.checkerFramework.generatePure() ? EclipseHandlerUtil.generateNamedAnnotation((ASTNode)job.builderType.get(), "org.checkerframework.dataflow.qual.Pure") : null;
        out.annotations = sefAnn != null ? new Annotation[]{overrideAnn, sefAnn} : new Annotation[]{overrideAnn};
        out.returnType = EclipseHandlerUtil.namePlusTypeParamsToTypeReference(job.builderType, job.typeParams, job.getPos());
        EclipseHandlerUtil.addCheckerFrameworkReturnsReceiver(out.returnType, (ASTNode)job.parentType.get(), job.checkerFramework);
        out.statements = new Statement[]{new ReturnStatement((Expression)new ThisReference(0, 0), 0, 0)};
        return out;
    }

    private MethodDeclaration generateAbstractBuildMethod(HandleBuilder.BuilderJob job, boolean override, String classGenericName) {
        MarkerAnnotation sefAnn;
        MethodDeclaration out = job.createNewMethodDeclaration();
        out.bits |= 0x800000;
        out.modifiers = 0x1000401;
        out.selector = job.buildMethodName.toCharArray();
        out.bits |= 0x800000;
        out.returnType = new SingleTypeReference(classGenericName.toCharArray(), 0L);
        MarkerAnnotation overrideAnn = override ? EclipseHandlerUtil.makeMarkerAnnotation(TypeConstants.JAVA_LANG_OVERRIDE, job.source) : null;
        MarkerAnnotation markerAnnotation = sefAnn = job.checkerFramework.generateSideEffectFree() ? EclipseHandlerUtil.generateNamedAnnotation(job.source, "org.checkerframework.dataflow.qual.SideEffectFree") : null;
        if (overrideAnn != null && sefAnn != null) {
            out.annotations = new Annotation[]{overrideAnn, sefAnn};
        } else if (overrideAnn != null) {
            out.annotations = new Annotation[]{overrideAnn};
        } else if (sefAnn != null) {
            out.annotations = new Annotation[]{sefAnn};
        }
        out.receiver = HandleBuilder.generateBuildReceiver(job);
        out.traverse((ASTVisitor)new SetGeneratedByVisitor(job.source), null);
        return out;
    }

    private MethodDeclaration generateBuildMethod(HandleBuilder.BuilderJob job, TypeReference returnType) {
        MethodDeclaration out = job.createNewMethodDeclaration();
        out.bits |= 0x800000;
        ArrayList<ReturnStatement> statements = new ArrayList<ReturnStatement>();
        out.modifiers = 1;
        out.selector = job.buildMethodName.toCharArray();
        out.bits |= 0x800000;
        out.returnType = returnType;
        MarkerAnnotation overrideAnn = EclipseHandlerUtil.makeMarkerAnnotation(TypeConstants.JAVA_LANG_OVERRIDE, job.source);
        MarkerAnnotation sefAnn = job.checkerFramework.generateSideEffectFree() ? EclipseHandlerUtil.generateNamedAnnotation(job.source, "org.checkerframework.dataflow.qual.SideEffectFree") : null;
        out.annotations = sefAnn != null ? new Annotation[]{overrideAnn, sefAnn} : new Annotation[]{overrideAnn};
        AllocationExpression allocationStatement = new AllocationExpression();
        allocationStatement.type = EclipseHandlerUtil.copyType(out.returnType);
        allocationStatement.arguments = new Expression[]{new ThisReference(0, 0)};
        statements.add(new ReturnStatement((Expression)allocationStatement, 0, 0));
        out.statements = statements.isEmpty() ? null : statements.toArray(new Statement[0]);
        out.receiver = HandleBuilder.generateBuildReceiver(job);
        EclipseHandlerUtil.createRelevantNonNullAnnotation(job.builderType, out);
        out.traverse((ASTVisitor)new SetGeneratedByVisitor(job.source), null);
        return out;
    }

    private MethodDeclaration generateCleanMethod(HandleBuilder.BuilderJob job) {
        ArrayList<Statement> statements = new ArrayList<Statement>();
        for (HandleBuilder.BuilderFieldData bfd : job.builderFields) {
            if (bfd.singularData == null || bfd.singularData.getSingularizer() == null) continue;
            bfd.singularData.getSingularizer().appendCleaningCode(bfd.singularData, job.builderType, statements);
        }
        FieldReference thisUnclean = new FieldReference(HandleBuilder.CLEAN_FIELD_NAME, 0L);
        thisUnclean.receiver = new ThisReference(0, 0);
        statements.add((Statement)new Assignment((Expression)thisUnclean, (Expression)new FalseLiteral(0, 0), 0));
        MethodDeclaration decl = job.createNewMethodDeclaration();
        decl.selector = HandleBuilder.CLEAN_METHOD_NAME;
        decl.modifiers = 2;
        decl.bits |= 0x800000;
        decl.returnType = TypeReference.baseTypeReference((int)6, (int)0);
        decl.statements = statements.toArray(new Statement[0]);
        decl.traverse((ASTVisitor)new SetGeneratedByVisitor(job.source), null);
        return decl;
    }

    private void generateBuilderFields(HandleBuilder.BuilderJob job) {
        ArrayList<EclipseNode> existing = new ArrayList<EclipseNode>();
        for (EclipseNode child : job.builderType.down()) {
            if (child.getKind() != AST.Kind.FIELD) continue;
            existing.add(child);
        }
        for (HandleBuilder.BuilderFieldData bfd : job.builderFields) {
            FieldDeclaration fd;
            if (bfd.singularData != null && bfd.singularData.getSingularizer() != null) {
                bfd.createdFields.addAll(bfd.singularData.getSingularizer().generateFields(bfd.singularData, job.builderType));
                continue;
            }
            EclipseNode field = null;
            EclipseNode setFlag = null;
            for (EclipseNode exists : existing) {
                char[] n = ((FieldDeclaration)exists.get()).name;
                if (Arrays.equals(n, bfd.builderFieldName)) {
                    field = exists;
                }
                if (bfd.nameOfSetFlag == null || !Arrays.equals(n, bfd.nameOfSetFlag)) continue;
                setFlag = exists;
            }
            if (field == null) {
                fd = new FieldDeclaration((char[])bfd.builderFieldName.clone(), 0, 0);
                fd.bits |= 0x800000;
                fd.modifiers = 2;
                fd.type = EclipseHandlerUtil.copyType(bfd.type);
                fd.traverse((ASTVisitor)new SetGeneratedByVisitor(job.source), null);
                field = EclipseHandlerUtil.injectFieldAndMarkGenerated(job.builderType, fd);
            }
            if (setFlag == null && bfd.nameOfSetFlag != null) {
                fd = new FieldDeclaration(bfd.nameOfSetFlag, 0, 0);
                fd.bits |= 0x800000;
                fd.modifiers = 2;
                fd.type = TypeReference.baseTypeReference((int)5, (int)0);
                fd.traverse((ASTVisitor)new SetGeneratedByVisitor(job.source), null);
                EclipseHandlerUtil.injectFieldAndMarkGenerated(job.builderType, fd);
            }
            bfd.createdFields.add(field);
        }
    }

    private void generateSetterMethodsForBuilder(HandleBuilder.BuilderJob job, HandleBuilder.BuilderFieldData bfd, final String builderGenericName, String setterPrefix) {
        boolean deprecate = EclipseHandlerUtil.isFieldDeprecated(bfd.originalFieldNode);
        EclipseSingularsRecipes.TypeReferenceMaker returnTypeMaker = new EclipseSingularsRecipes.TypeReferenceMaker(){

            @Override
            public TypeReference make() {
                return new SingleTypeReference(builderGenericName.toCharArray(), 0L);
            }
        };
        EclipseSingularsRecipes.StatementMaker returnStatementMaker = new EclipseSingularsRecipes.StatementMaker(){

            public ReturnStatement make() {
                MessageSend returnCall = new MessageSend();
                returnCall.receiver = ThisReference.implicitThis();
                returnCall.selector = SELF_METHOD_NAME;
                return new ReturnStatement((Expression)returnCall, 0, 0);
            }
        };
        if (bfd.singularData == null || bfd.singularData.getSingularizer() == null) {
            this.generateSimpleSetterMethodForBuilder(job, deprecate, bfd.createdFields.get(0), bfd.name, bfd.nameOfSetFlag, returnTypeMaker.make(), returnStatementMaker.make(), bfd.annotations, bfd.originalFieldNode, setterPrefix);
        } else {
            bfd.singularData.getSingularizer().generateMethods(job.checkerFramework, bfd.singularData, deprecate, job.builderType, true, returnTypeMaker, returnStatementMaker, AccessLevel.PUBLIC);
        }
    }

    private void generateSimpleSetterMethodForBuilder(HandleBuilder.BuilderJob job, boolean deprecate, EclipseNode fieldNode, char[] paramName, char[] nameOfSetFlag, TypeReference returnType, Statement returnStatement, Annotation[] annosOnParam, EclipseNode originalFieldNode, String setterPrefix) {
        TypeDeclaration td = (TypeDeclaration)job.builderType.get();
        AbstractMethodDeclaration[] existing = td.methods;
        if (existing == null) {
            existing = HandleBuilder.EMPTY_METHODS;
        }
        int len = existing.length;
        String setterName = HandlerUtil.buildAccessorName(job.sourceNode, setterPrefix, new String(paramName));
        int i = 0;
        while (i < len) {
            if (existing[i] instanceof MethodDeclaration) {
                char[] existingName = existing[i].selector;
                if (Arrays.equals(setterName.toCharArray(), existingName) && !EclipseHandlerUtil.isTolerate(fieldNode, existing[i])) {
                    return;
                }
            }
            ++i;
        }
        List<Annotation> methodAnnsList = Arrays.asList(EclipseHandlerUtil.findCopyableToSetterAnnotations(originalFieldNode));
        EclipseHandlerUtil.addCheckerFrameworkReturnsReceiver(returnType, job.source, job.checkerFramework);
        MethodDeclaration setter = HandleSetter.createSetter(td, deprecate, fieldNode, setterName, paramName, nameOfSetFlag, returnType, returnStatement, 1, job.sourceNode, methodAnnsList, annosOnParam != null ? Arrays.asList(EclipseHandlerUtil.copyAnnotations(job.source, new Annotation[][]{annosOnParam})) : Collections.emptyList());
        if (((EclipseNode)job.sourceNode.up()).getKind() == AST.Kind.METHOD) {
            EclipseHandlerUtil.copyJavadocFromParam((EclipseNode)originalFieldNode.up(), setter, td, paramName.toString());
        } else {
            EclipseHandlerUtil.copyJavadoc(originalFieldNode, (ASTNode)setter, td, EclipseHandlerUtil.CopyJavadoc.SETTER, true);
        }
        EclipseHandlerUtil.injectMethod(job.builderType, (AbstractMethodDeclaration)setter);
    }

    private void addObtainVia(HandleBuilder.BuilderFieldData bfd, EclipseNode node) {
        for (EclipseNode child : node.down()) {
            if (!EclipseHandlerUtil.annotationTypeMatches(Builder.ObtainVia.class, child)) continue;
            AnnotationValues<Builder.ObtainVia> ann = EclipseHandlerUtil.createAnnotation(Builder.ObtainVia.class, child);
            bfd.obtainVia = ann.getInstance();
            bfd.obtainViaNode = child;
            return;
        }
    }

    private EclipseSingularsRecipes.SingularData getSingularData(EclipseNode node, ASTNode source, String setterPrefix) {
        for (EclipseNode child : node.down()) {
            String typeName;
            if (!EclipseHandlerUtil.annotationTypeMatches(Singular.class, child)) continue;
            char[] pluralName = node.getKind() == AST.Kind.FIELD ? EclipseHandlerUtil.removePrefixFromField(node) : ((AbstractVariableDeclaration)node.get()).name;
            AnnotationValues<Singular> ann = EclipseHandlerUtil.createAnnotation(Singular.class, child);
            Singular singularInstance = ann.getInstance();
            String explicitSingular = singularInstance.value();
            if (explicitSingular.isEmpty()) {
                if (Boolean.FALSE.equals(node.getAst().readConfiguration(ConfigurationKeys.SINGULAR_AUTO))) {
                    node.addError("The singular must be specified explicitly (e.g. @Singular(\"task\")) because auto singularization is disabled.");
                    explicitSingular = new String(pluralName);
                } else {
                    explicitSingular = HandlerUtil.autoSingularize(new String(pluralName));
                    if (explicitSingular == null) {
                        node.addError("Can't singularize this name; please specify the singular explicitly (i.e. @Singular(\"sheep\"))");
                        explicitSingular = new String(pluralName);
                    }
                }
            }
            char[] singularName = explicitSingular.toCharArray();
            TypeReference type = ((AbstractVariableDeclaration)node.get()).type;
            TypeReference[] typeArgs = null;
            if (type instanceof ParameterizedSingleTypeReference) {
                typeArgs = ((ParameterizedSingleTypeReference)type).typeArguments;
                typeName = new String(((ParameterizedSingleTypeReference)type).token);
            } else if (type instanceof ParameterizedQualifiedTypeReference) {
                TypeReference[][] tr = ((ParameterizedQualifiedTypeReference)type).typeArguments;
                if (tr != null) {
                    typeArgs = tr[tr.length - 1];
                }
                char[][] tokens = ((ParameterizedQualifiedTypeReference)type).tokens;
                StringBuilder sb = new StringBuilder();
                int i = 0;
                while (i < tokens.length) {
                    if (i > 0) {
                        sb.append(".");
                    }
                    sb.append(tokens[i]);
                    ++i;
                }
                typeName = sb.toString();
            } else {
                typeName = type.toString();
            }
            String targetFqn = EclipseSingularsRecipes.get().toQualified(typeName);
            EclipseSingularsRecipes.EclipseSingularizer singularizer = EclipseSingularsRecipes.get().getSingularizer(targetFqn);
            if (singularizer == null) {
                node.addError("Lombok does not know how to create the singular-form builder methods for type '" + typeName + "'; they won't be generated.");
                return null;
            }
            return new EclipseSingularsRecipes.SingularData(child, singularName, pluralName, typeArgs == null ? Collections.emptyList() : Arrays.asList(typeArgs), targetFqn, singularizer, source, singularInstance.ignoreNullCollections(), setterPrefix.toCharArray());
        }
        return null;
    }

    private Set<String> gatherUsedTypeNames(TypeParameter[] typeParams, TypeDeclaration td) {
        HashSet<String> usedNames = new HashSet<String>();
        TypeParameter[] typeParameterArray = typeParams;
        int n = typeParams.length;
        int n2 = 0;
        while (n2 < n) {
            TypeParameter typeParam = typeParameterArray[n2];
            usedNames.add(typeParam.toString());
            ++n2;
        }
        usedNames.add(String.valueOf(td.name));
        if (td.fields != null) {
            typeParameterArray = td.fields;
            n = td.fields.length;
            n2 = 0;
            while (n2 < n) {
                TypeParameter field = typeParameterArray[n2];
                if (!(field instanceof Initializer)) {
                    this.addFirstToken(usedNames, field.type);
                }
                ++n2;
            }
        }
        this.addFirstToken(usedNames, td.superclass);
        if (td.superInterfaces != null) {
            typeParameterArray = td.superInterfaces;
            n = td.superInterfaces.length;
            n2 = 0;
            while (n2 < n) {
                TypeParameter typeReference = typeParameterArray[n2];
                this.addFirstToken(usedNames, (TypeReference)typeReference);
                ++n2;
            }
        }
        return usedNames;
    }

    private void addFirstToken(Set<String> usedNames, TypeReference type) {
        if (type == null) {
            return;
        }
        char[][] typeName = type.getTypeName();
        if (typeName != null && typeName.length >= 1) {
            usedNames.add(String.valueOf(typeName[0]));
        }
    }

    private String generateNonclashingNameFor(String classGenericName, Set<String> typeParamStrings) {
        if (!typeParamStrings.contains(classGenericName)) {
            return classGenericName;
        }
        int counter = 2;
        while (typeParamStrings.contains(String.valueOf(classGenericName) + counter)) {
            ++counter;
        }
        return String.valueOf(classGenericName) + counter;
    }

    private TypeReference[] appendBuilderTypeReferences(TypeParameter[] typeParams, String classGenericName, String builderGenericName) {
        TypeReference[] typeReferencesToAppend = new TypeReference[2];
        typeReferencesToAppend[typeReferencesToAppend.length - 2] = new SingleTypeReference(classGenericName.toCharArray(), 0L);
        typeReferencesToAppend[typeReferencesToAppend.length - 1] = new SingleTypeReference(builderGenericName.toCharArray(), 0L);
        return this.mergeToTypeReferences(typeParams, typeReferencesToAppend);
    }

    private TypeReference[] getTypeParametersFrom(TypeReference typeRef) {
        TypeReference[][] typeArgss = null;
        if (typeRef instanceof ParameterizedQualifiedTypeReference) {
            typeArgss = ((ParameterizedQualifiedTypeReference)typeRef).typeArguments;
        } else if (typeRef instanceof ParameterizedSingleTypeReference) {
            typeArgss = new TypeReference[][]{((ParameterizedSingleTypeReference)typeRef).typeArguments};
        }
        TypeReference[] typeArgs = new TypeReference[]{};
        if (typeArgss != null && typeArgss.length > 0) {
            typeArgs = typeArgss[typeArgss.length - 1];
        }
        return typeArgs;
    }

    private static TypeReference createTypeReferenceWithTypeParameters(EclipseNode parent, String referenceName, TypeParameter[] typeParams) {
        if (typeParams.length > 0) {
            TypeReference[] typerefs = new TypeReference[typeParams.length];
            int i = 0;
            while (i < typeParams.length) {
                typerefs[i] = new SingleTypeReference(typeParams[i].name, 0L);
                ++i;
            }
            return EclipseHandlerUtil.generateParameterizedTypeReference(parent, referenceName.toCharArray(), false, typerefs, 0L);
        }
        return EclipseHandlerUtil.generateTypeReference(parent, referenceName.toCharArray(), false, 0L);
    }

    private TypeReference[] mergeToTypeReferences(TypeParameter[] typeParams, TypeReference[] typeReferencesToAppend) {
        TypeReference[] typerefs = new TypeReference[typeParams.length + typeReferencesToAppend.length];
        int i = 0;
        while (i < typeParams.length) {
            typerefs[i] = new SingleTypeReference(typeParams[i].name, 0L);
            ++i;
        }
        i = 0;
        while (i < typeReferencesToAppend.length) {
            typerefs[typeParams.length + i] = typeReferencesToAppend[i];
            ++i;
        }
        return typerefs;
    }

    private TypeReference[] mergeTypeReferences(TypeReference[] refs1, TypeReference[] refs2) {
        TypeReference[] result = new TypeReference[refs1.length + refs2.length];
        int i = 0;
        while (i < refs1.length) {
            result[i] = refs1[i];
            ++i;
        }
        i = 0;
        while (i < refs2.length) {
            result[refs1.length + i] = refs2[i];
            ++i;
        }
        return result;
    }

    private TypeReference[] typeParameterNames(TypeParameter[] typeParameters) {
        if (typeParameters == null) {
            return null;
        }
        TypeReference[] trs = new TypeReference[typeParameters.length];
        int i = 0;
        while (i < trs.length) {
            trs[i] = new SingleTypeReference(typeParameters[i].name, 0L);
            ++i;
        }
        return trs;
    }

    private EclipseNode findInnerClass(EclipseNode parent, String name) {
        char[] c = name.toCharArray();
        for (EclipseNode child : parent.down()) {
            if (child.getKind() != AST.Kind.TYPE) continue;
            TypeDeclaration td = (TypeDeclaration)child.get();
            if (!Arrays.equals(td.name, c)) continue;
            return child;
        }
        return null;
    }

    private static final char[] prefixWith(char[] prefix, char[] name) {
        char[] out = new char[prefix.length + name.length];
        System.arraycopy(prefix, 0, out, 0, prefix.length);
        System.arraycopy(name, 0, out, prefix.length, name.length);
        return out;
    }

    private boolean constructorExists(EclipseNode type, String builderClassName) {
        if (type != null && type.get() instanceof TypeDeclaration) {
            TypeDeclaration typeDecl = (TypeDeclaration)type.get();
            if (typeDecl.methods != null) {
                AbstractMethodDeclaration[] abstractMethodDeclarationArray = typeDecl.methods;
                int n = typeDecl.methods.length;
                int n2 = 0;
                while (n2 < n) {
                    char[] typeName;
                    AbstractMethodDeclaration def = abstractMethodDeclarationArray[n2];
                    if (def instanceof ConstructorDeclaration && (def.bits & 0x80) == 0 && def.isConstructor() && !EclipseHandlerUtil.isTolerate(type, def) && def.arguments != null && def.arguments.length == 1 && builderClassName.equals(String.valueOf(typeName = def.arguments[0].type.getLastToken()))) {
                        return true;
                    }
                    ++n2;
                }
            }
        }
        return false;
    }

    class SuperBuilderJob
    extends HandleBuilder.BuilderJob {
        EclipseNode builderAbstractType;
        String builderAbstractClassName;
        char[] builderAbstractClassNameArr;
        EclipseNode builderImplType;
        String builderImplClassName;
        char[] builderImplClassNameArr;
        private TypeParameter[] builderTypeParams_;

        SuperBuilderJob() {
        }

        void init(AnnotationValues<SuperBuilder> annValues, SuperBuilder ann, EclipseNode node) {
            this.accessOuters = this.accessInners = AccessLevel.PUBLIC;
            this.oldFluent = true;
            this.oldChain = true;
            this.builderMethodName = ann.builderMethodName();
            this.buildMethodName = ann.buildMethodName();
            this.toBuilder = ann.toBuilder();
            if (this.builderMethodName == null) {
                this.builderMethodName = "builder";
            }
            if (this.buildMethodName == null) {
                this.buildMethodName = "build";
            }
            this.builderClassName = SuperBuilderJob.getBuilderClassNameTemplate(node, null);
        }

        void setBuilderToImpl() {
            this.builderType = this.builderImplType;
            this.builderClassName = this.builderImplClassName;
            this.builderClassNameArr = this.builderImplClassNameArr;
            this.builderTypeParams = this.typeParams;
        }

        void setBuilderToAbstract() {
            this.builderType = this.builderAbstractType;
            this.builderClassName = this.builderAbstractClassName;
            this.builderClassNameArr = this.builderAbstractClassNameArr;
            this.builderTypeParams = this.builderTypeParams_;
        }
    }
}
