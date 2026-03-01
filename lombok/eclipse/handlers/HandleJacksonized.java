package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import lombok.Builder;
import lombok.ConfigurationKeys;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.core.handlers.HandlerUtil;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.HandleBuilder;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

@HandlerPriority(value=-512)
public class HandleJacksonized
extends EclipseAnnotationHandler<Jacksonized> {
    private static final char[][] JSON_POJO_BUILDER_ANNOTATION = Eclipse.fromQualifiedName("com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder");
    private static final char[][] JSON_DESERIALIZE_ANNOTATION = Eclipse.fromQualifiedName("com.fasterxml.jackson.databind.annotation.JsonDeserialize");
    private static final Annotation[] EMPTY_ANNOTATIONS_ARRAY = new Annotation[0];

    @Override
    public void handle(AnnotationValues<Jacksonized> annotation, Annotation ast, EclipseNode annotationNode) {
        boolean isAbstract;
        HandlerUtil.handleExperimentalFlagUsage(annotationNode, ConfigurationKeys.JACKSONIZED_FLAG_USAGE, "@Jacksonized");
        EclipseNode annotatedNode = (EclipseNode)annotationNode.up();
        EclipseNode tdNode = annotatedNode.getKind() != AST.Kind.TYPE ? (EclipseNode)annotatedNode.up() : annotatedNode;
        TypeDeclaration td = (TypeDeclaration)tdNode.get();
        EclipseNode builderAnnotationNode = EclipseHandlerUtil.findAnnotation(Builder.class, annotatedNode);
        EclipseNode superBuilderAnnotationNode = EclipseHandlerUtil.findAnnotation(SuperBuilder.class, annotatedNode);
        if (builderAnnotationNode == null && superBuilderAnnotationNode == null) {
            annotationNode.addWarning("@Jacksonized requires @Builder or @SuperBuilder for it to mean anything.");
            return;
        }
        if (builderAnnotationNode != null && superBuilderAnnotationNode != null) {
            annotationNode.addError("@Jacksonized cannot process both @Builder and @SuperBuilder on the same class.");
            return;
        }
        boolean bl = isAbstract = (td.modifiers & 0x400) != 0;
        if (isAbstract) {
            annotationNode.addError("Builders on abstract classes cannot be @Jacksonized (the builder would never be used).");
            return;
        }
        AnnotationValues<Builder> builderAnnotation = builderAnnotationNode != null ? EclipseHandlerUtil.createAnnotation(Builder.class, builderAnnotationNode) : null;
        AnnotationValues<SuperBuilder> superBuilderAnnotation = superBuilderAnnotationNode != null ? EclipseHandlerUtil.createAnnotation(SuperBuilder.class, superBuilderAnnotationNode) : null;
        String setPrefix = builderAnnotation != null ? builderAnnotation.getInstance().setterPrefix() : superBuilderAnnotation.getInstance().setterPrefix();
        String buildMethodName = builderAnnotation != null ? builderAnnotation.getInstance().buildMethodName() : superBuilderAnnotation.getInstance().buildMethodName();
        EclipseNode builderClassNode = null;
        TypeDeclaration builderClass = null;
        String builderClassName = this.getBuilderClassName(ast, annotationNode, annotatedNode, td, builderAnnotation);
        for (EclipseNode member : tdNode.down()) {
            ASTNode astNode = (ASTNode)member.get();
            if (!(astNode instanceof TypeDeclaration) || !Arrays.equals(((TypeDeclaration)astNode).name, builderClassName.toCharArray())) continue;
            builderClassNode = member;
            builderClass = (TypeDeclaration)astNode;
            break;
        }
        if (builderClass == null) {
            annotationNode.addError("Could not find @(Super)Builder's generated builder class for @Jacksonized processing. If there are other compiler errors, fix them first.");
            return;
        }
        if (EclipseHandlerUtil.hasAnnotation("com.fasterxml.jackson.databind.annotation.JsonDeserialize", tdNode)) {
            annotationNode.addError("@JsonDeserialize already exists on class. Either delete @JsonDeserialize, or remove @Jacksonized and manually configure Jackson.");
            return;
        }
        long p = (long)ast.sourceStart << 32 | (long)ast.sourceEnd;
        TypeReference builderClassExpression = EclipseHandlerUtil.namePlusTypeParamsToTypeReference(builderClassNode, null, p);
        ClassLiteralAccess builderClassLiteralAccess = new ClassLiteralAccess(td.sourceEnd, builderClassExpression);
        MemberValuePair builderMvp = new MemberValuePair("builder".toCharArray(), td.sourceStart, td.sourceEnd, (Expression)builderClassLiteralAccess);
        td.annotations = EclipseHandlerUtil.addAnnotation((ASTNode)td, td.annotations, JSON_DESERIALIZE_ANNOTATION, new ASTNode[]{builderMvp});
        Annotation[] copyableAnnotations = HandleJacksonized.findJacksonAnnotationsOnClass(td, tdNode);
        builderClass.annotations = EclipseHandlerUtil.copyAnnotations((ASTNode)builderClass, builderClass.annotations, copyableAnnotations);
        StringLiteral withPrefixLiteral = new StringLiteral(setPrefix.toCharArray(), builderClass.sourceStart, builderClass.sourceEnd, 0);
        MemberValuePair withPrefixMvp = new MemberValuePair("withPrefix".toCharArray(), builderClass.sourceStart, builderClass.sourceEnd, (Expression)withPrefixLiteral);
        StringLiteral buildMethodNameLiteral = new StringLiteral(buildMethodName.toCharArray(), builderClass.sourceStart, builderClass.sourceEnd, 0);
        MemberValuePair buildMethodNameMvp = new MemberValuePair("buildMethodName".toCharArray(), builderClass.sourceStart, builderClass.sourceEnd, (Expression)buildMethodNameLiteral);
        builderClass.annotations = EclipseHandlerUtil.addAnnotation((ASTNode)builderClass, builderClass.annotations, JSON_POJO_BUILDER_ANNOTATION, new ASTNode[]{withPrefixMvp, buildMethodNameMvp});
        if (superBuilderAnnotationNode != null) {
            builderClass.modifiers &= 0xFFFFFFFD;
        }
    }

    private String getBuilderClassName(Annotation ast, EclipseNode annotationNode, EclipseNode annotatedNode, TypeDeclaration td, AnnotationValues<Builder> builderAnnotation) {
        String builderClassName;
        String string = builderClassName = builderAnnotation != null ? builderAnnotation.getInstance().builderClassName() : null;
        if (builderClassName == null || builderClassName.isEmpty()) {
            builderClassName = annotationNode.getAst().readConfiguration(ConfigurationKeys.BUILDER_CLASS_NAME);
            if (builderClassName == null || builderClassName.isEmpty()) {
                builderClassName = "*Builder";
            }
            MethodDeclaration fillParametersFrom = annotatedNode.get() instanceof MethodDeclaration ? (MethodDeclaration)annotatedNode.get() : null;
            char[] replacement = fillParametersFrom != null ? HandleBuilder.returnTypeToBuilderClassName(annotationNode, fillParametersFrom, fillParametersFrom.typeParameters) : td.name;
            builderClassName = builderClassName.replace("*", new String(replacement));
        }
        if (builderAnnotation == null) {
            builderClassName = String.valueOf(builderClassName) + "Impl";
        }
        return builderClassName;
    }

    private static Annotation[] findJacksonAnnotationsOnClass(TypeDeclaration td, EclipseNode node) {
        if (td.annotations == null) {
            return EMPTY_ANNOTATIONS_ARRAY;
        }
        ArrayList<Annotation> result = new ArrayList<Annotation>();
        Annotation[] annotationArray = td.annotations;
        int n = td.annotations.length;
        int n2 = 0;
        while (n2 < n) {
            Annotation annotation = annotationArray[n2];
            TypeReference typeRef = annotation.type;
            if (typeRef != null && typeRef.getTypeName() != null) {
                for (String bn : HandlerUtil.JACKSON_COPY_TO_BUILDER_ANNOTATIONS) {
                    if (!EclipseHandlerUtil.typeMatches(bn, node, typeRef)) continue;
                    result.add(annotation);
                    break;
                }
            }
            ++n2;
        }
        return result.toArray(EMPTY_ANNOTATIONS_ARRAY);
    }
}
