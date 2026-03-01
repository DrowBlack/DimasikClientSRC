package lombok.eclipse.handlers;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.configuration.IdentifierName;
import lombok.core.handlers.HandlerUtil;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.SetGeneratedByVisitor;
import lombok.experimental.FieldNameConstants;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Clinit;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

public class HandleFieldNameConstants
extends EclipseAnnotationHandler<FieldNameConstants> {
    private static final IdentifierName FIELDS = IdentifierName.valueOf("Fields");

    public void generateFieldNameConstantsForType(EclipseNode typeNode, EclipseNode errorNode, AccessLevel level, boolean asEnum, IdentifierName innerTypeName, boolean onlyExplicit, boolean uppercase) {
        if (!EclipseHandlerUtil.isClassEnumOrRecord(typeNode)) {
            errorNode.addError("@FieldNameConstants is only supported on a class, an enum or a record.");
            return;
        }
        if (!EclipseHandlerUtil.isStaticAllowed(typeNode)) {
            errorNode.addError("@FieldNameConstants is not supported on non-static nested classes.");
            return;
        }
        ArrayList<EclipseNode> qualified = new ArrayList<EclipseNode>();
        for (EclipseNode field : typeNode.down()) {
            if (!this.fieldQualifiesForFieldNameConstantsGeneration(field, onlyExplicit)) continue;
            qualified.add(field);
        }
        if (qualified.isEmpty()) {
            errorNode.addWarning("No fields qualify for @FieldNameConstants, therefore this annotation does nothing");
        } else {
            this.createInnerTypeFieldNameConstants(typeNode, errorNode, (ASTNode)errorNode.get(), level, qualified, asEnum, innerTypeName, uppercase);
        }
    }

    private boolean fieldQualifiesForFieldNameConstantsGeneration(EclipseNode field, boolean onlyExplicit) {
        if (field.getKind() != AST.Kind.FIELD) {
            return false;
        }
        if (EclipseHandlerUtil.hasAnnotation(FieldNameConstants.Exclude.class, field)) {
            return false;
        }
        if (EclipseHandlerUtil.hasAnnotation(FieldNameConstants.Include.class, field)) {
            return true;
        }
        if (onlyExplicit) {
            return false;
        }
        FieldDeclaration fieldDecl = (FieldDeclaration)field.get();
        return EclipseHandlerUtil.filterField(fieldDecl);
    }

    @Override
    public void handle(AnnotationValues<FieldNameConstants> annotation, Annotation ast, EclipseNode annotationNode) {
        Boolean uppercase;
        IdentifierName innerTypeName;
        boolean usingLombokv1_18_2;
        HandlerUtil.handleExperimentalFlagUsage(annotationNode, ConfigurationKeys.FIELD_NAME_CONSTANTS_FLAG_USAGE, "@FieldNameConstants");
        EclipseNode node = (EclipseNode)annotationNode.up();
        FieldNameConstants annotationInstance = annotation.getInstance();
        AccessLevel level = annotationInstance.level();
        boolean asEnum = annotationInstance.asEnum();
        boolean bl = usingLombokv1_18_2 = annotation.isExplicit("prefix") || annotation.isExplicit("suffix") || node.getKind() == AST.Kind.FIELD;
        if (usingLombokv1_18_2) {
            annotationNode.addError("@FieldNameConstants has been redesigned in lombok v1.18.4; please upgrade your project dependency on lombok. See https://projectlombok.org/features/experimental/FieldNameConstants for more information.");
            return;
        }
        if (level == AccessLevel.NONE) {
            annotationNode.addWarning("AccessLevel.NONE is not compatible with @FieldNameConstants. If you don't want the inner type, simply remove FieldNameConstants.");
            return;
        }
        try {
            innerTypeName = IdentifierName.valueOf(annotationInstance.innerTypeName());
        }
        catch (IllegalArgumentException illegalArgumentException) {
            annotationNode.addError("InnerTypeName " + annotationInstance.innerTypeName() + " is not a valid Java identifier.");
            return;
        }
        if (innerTypeName == null) {
            innerTypeName = annotationNode.getAst().readConfiguration(ConfigurationKeys.FIELD_NAME_CONSTANTS_INNER_TYPE_NAME);
        }
        if (innerTypeName == null) {
            innerTypeName = FIELDS;
        }
        if ((uppercase = annotationNode.getAst().readConfiguration(ConfigurationKeys.FIELD_NAME_CONSTANTS_UPPERCASE)) == null) {
            uppercase = false;
        }
        this.generateFieldNameConstantsForType(node, annotationNode, level, asEnum, innerTypeName, annotationInstance.onlyExplicitlyIncluded(), uppercase);
    }

    private void createInnerTypeFieldNameConstants(EclipseNode typeNode, EclipseNode errorNode, ASTNode source, AccessLevel level, List<EclipseNode> fields, boolean asEnum, IdentifierName innerTypeName, boolean uppercase) {
        if (fields.isEmpty()) {
            return;
        }
        SetGeneratedByVisitor generatedByVisitor = new SetGeneratedByVisitor(source);
        TypeDeclaration parent = (TypeDeclaration)typeNode.get();
        EclipseNode fieldsType = EclipseHandlerUtil.findInnerClass(typeNode, innerTypeName.getName());
        boolean genConstr = false;
        boolean genClinit = false;
        char[] name = innerTypeName.getCharArray();
        if (fieldsType == null) {
            TypeDeclaration generatedInnerType = new TypeDeclaration(parent.compilationResult);
            generatedInnerType.bits |= 0x800000;
            generatedInnerType.modifiers = EclipseHandlerUtil.toEclipseModifier(level) | (asEnum ? 16384 : 24);
            generatedInnerType.name = name;
            fieldsType = EclipseHandlerUtil.injectType(typeNode, generatedInnerType);
            genConstr = true;
            genClinit = asEnum;
            generatedInnerType.traverse((ASTVisitor)generatedByVisitor, ((TypeDeclaration)typeNode.get()).scope);
        } else {
            TypeDeclaration builderTypeDeclaration = (TypeDeclaration)fieldsType.get();
            if (asEnum && (builderTypeDeclaration.modifiers & 0x4000) == 0) {
                errorNode.addError("Existing " + innerTypeName + " must be declared as an 'enum'.");
                return;
            }
            if (!asEnum && (builderTypeDeclaration.modifiers & 8) == 0) {
                errorNode.addError("Existing " + innerTypeName + " must be declared as a 'static class'.");
                return;
            }
            boolean bl = genConstr = EclipseHandlerUtil.constructorExists(fieldsType) == EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS;
        }
        if (genConstr) {
            ConstructorDeclaration constructor = new ConstructorDeclaration(parent.compilationResult);
            constructor.selector = name;
            constructor.modifiers = 2;
            ExplicitConstructorCall superCall = new ExplicitConstructorCall(0);
            superCall.sourceStart = source.sourceStart;
            superCall.sourceEnd = source.sourceEnd;
            superCall.bits |= 0x800000;
            constructor.constructorCall = superCall;
            if (!asEnum) {
                constructor.statements = new Statement[0];
            }
            EclipseHandlerUtil.injectMethod(fieldsType, (AbstractMethodDeclaration)constructor);
        }
        Clinit cli = null;
        if (genClinit) {
            cli = new Clinit(parent.compilationResult);
            EclipseHandlerUtil.injectMethod(fieldsType, (AbstractMethodDeclaration)cli);
        }
        for (EclipseNode fieldNode : fields) {
            FieldDeclaration field = (FieldDeclaration)fieldNode.get();
            char[] fName = field.name;
            if (uppercase) {
                fName = HandlerUtil.camelCaseToConstant(new String(fName)).toCharArray();
            }
            if (EclipseHandlerUtil.fieldExists(new String(fName), fieldsType) != EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS) continue;
            int pS = source.sourceStart;
            int pE = source.sourceEnd;
            long p = (long)pS << 32 | (long)pE;
            FieldDeclaration constantField = new FieldDeclaration(fName, pS, pE);
            constantField.bits |= 0x800000;
            if (asEnum) {
                AllocationExpression ac = new AllocationExpression();
                ac.enumConstant = constantField;
                ac.sourceStart = source.sourceStart;
                ac.sourceEnd = source.sourceEnd;
                constantField.initialization = ac;
                constantField.modifiers = 0;
                ++((TypeDeclaration)fieldsType.get()).enumConstantsCounter;
            } else {
                constantField.type = new QualifiedTypeReference(TypeConstants.JAVA_LANG_STRING, new long[]{p, p, p});
                constantField.initialization = new StringLiteral(field.name, pS, pE, 0);
                constantField.modifiers = 25;
            }
            EclipseHandlerUtil.injectField(fieldsType, constantField);
            constantField.traverse((ASTVisitor)generatedByVisitor, ((TypeDeclaration)fieldsType.get()).initializerScope);
        }
        if (genClinit) {
            cli.traverse((ASTVisitor)generatedByVisitor, ((TypeDeclaration)fieldsType.get()).scope);
        }
    }
}
