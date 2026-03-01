package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.configuration.IdentifierName;
import lombok.core.handlers.HandlerUtil;
import lombok.experimental.FieldNameConstants;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleFieldNameConstants
extends JavacAnnotationHandler<FieldNameConstants> {
    private static final IdentifierName FIELDS = IdentifierName.valueOf("Fields");

    public void generateFieldNameConstantsForType(JavacNode typeNode, JavacNode errorNode, AccessLevel level, boolean asEnum, IdentifierName innerTypeName, boolean onlyExplicit, boolean uppercase) {
        if (!JavacHandlerUtil.isClassEnumOrRecord(typeNode)) {
            errorNode.addError("@FieldNameConstants is only supported on a class, an enum or a record.");
            return;
        }
        if (!JavacHandlerUtil.isStaticAllowed(typeNode)) {
            errorNode.addError("@FieldNameConstants is not supported on non-static nested classes.");
            return;
        }
        ArrayList<JavacNode> qualified = new ArrayList<JavacNode>();
        for (JavacNode field : typeNode.down()) {
            if (!this.fieldQualifiesForFieldNameConstantsGeneration(field, onlyExplicit)) continue;
            qualified.add(field);
        }
        if (qualified.isEmpty()) {
            errorNode.addWarning("No fields qualify for @FieldNameConstants, therefore this annotation does nothing");
        } else {
            this.createInnerTypeFieldNameConstants(typeNode, errorNode, level, qualified, asEnum, innerTypeName, uppercase);
        }
    }

    private boolean fieldQualifiesForFieldNameConstantsGeneration(JavacNode field, boolean onlyExplicit) {
        if (field.getKind() != AST.Kind.FIELD) {
            return false;
        }
        boolean exclAnn = JavacHandlerUtil.hasAnnotationAndDeleteIfNeccessary(FieldNameConstants.Exclude.class, field);
        boolean inclAnn = JavacHandlerUtil.hasAnnotationAndDeleteIfNeccessary(FieldNameConstants.Include.class, field);
        if (exclAnn) {
            return false;
        }
        if (inclAnn) {
            return true;
        }
        if (onlyExplicit) {
            return false;
        }
        JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)field.get();
        if (fieldDecl.name.toString().startsWith("$")) {
            return false;
        }
        return (fieldDecl.mods.flags & 8L) == 0L;
    }

    @Override
    public void handle(AnnotationValues<FieldNameConstants> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        Boolean uppercase;
        IdentifierName innerTypeName;
        boolean usingLombokv1_18_2;
        HandlerUtil.handleExperimentalFlagUsage(annotationNode, ConfigurationKeys.FIELD_NAME_CONSTANTS_FLAG_USAGE, "@FieldNameConstants");
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, FieldNameConstants.class);
        JavacHandlerUtil.deleteImportFromCompilationUnit(annotationNode, "lombok.AccessLevel");
        JavacNode node = (JavacNode)annotationNode.up();
        FieldNameConstants annotationInstance = annotation.getInstance();
        AccessLevel level = annotationInstance.level();
        boolean asEnum = annotationInstance.asEnum();
        boolean bl = usingLombokv1_18_2 = annotation.isExplicit("prefix") || annotation.isExplicit("suffix") || node.getKind() == AST.Kind.FIELD;
        if (usingLombokv1_18_2) {
            annotationNode.addError("@FieldNameConstants has been redesigned in lombok v1.18.4; please upgrade your project dependency on lombok. See https://projectlombok.org/features/experimental/FieldNameConstants for more information.");
            return;
        }
        if (level == AccessLevel.NONE) {
            annotationNode.addWarning("AccessLevel.NONE is not compatible with @FieldNameConstants. If you don't want the inner type, simply remove @FieldNameConstants.");
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

    private void createInnerTypeFieldNameConstants(JavacNode typeNode, JavacNode errorNode, AccessLevel level, java.util.List<JavacNode> fields, boolean asEnum, IdentifierName innerTypeName, boolean uppercase) {
        if (fields.isEmpty()) {
            return;
        }
        JavacTreeMaker maker = typeNode.getTreeMaker();
        JCTree.JCModifiers mods = maker.Modifiers(JavacHandlerUtil.toJavacModifier(level) | (asEnum ? 16384 : 24));
        Name fieldsName = typeNode.toName(innerTypeName.getName());
        JavacNode fieldsType = JavacHandlerUtil.findInnerClass(typeNode, innerTypeName.getName());
        boolean genConstr = false;
        if (fieldsType == null) {
            JCTree.JCClassDecl innerType = maker.ClassDef(mods, fieldsName, List.<JCTree.JCTypeParameter>nil(), null, List.<JCTree.JCExpression>nil(), List.<JCTree>nil());
            fieldsType = JavacHandlerUtil.injectType(typeNode, innerType);
            JavacHandlerUtil.recursiveSetGeneratedBy(innerType, errorNode);
            genConstr = true;
        } else {
            JCTree.JCClassDecl builderTypeDeclaration = (JCTree.JCClassDecl)fieldsType.get();
            long f = builderTypeDeclaration.getModifiers().flags;
            if (asEnum && (f & 0x4000L) == 0L) {
                errorNode.addError("Existing " + innerTypeName + " must be declared as an 'enum'.");
                return;
            }
            if (!asEnum && (f & 8L) == 0L) {
                errorNode.addError("Existing " + innerTypeName + " must be declared as a 'static class'.");
                return;
            }
            boolean bl = genConstr = JavacHandlerUtil.constructorExists(fieldsType) == JavacHandlerUtil.MemberExistsResult.NOT_EXISTS;
        }
        if (genConstr) {
            JCTree.JCModifiers genConstrMods = maker.Modifiers(0x1000000000L | (asEnum ? 0L : 2L));
            JCTree.JCBlock genConstrBody = maker.Block(0L, List.of(maker.Exec(maker.Apply(List.<JCTree.JCExpression>nil(), maker.Ident(typeNode.toName("super")), List.<JCTree.JCExpression>nil()))));
            JCTree.JCMethodDecl c = maker.MethodDef(genConstrMods, typeNode.toName("<init>"), null, List.<JCTree.JCTypeParameter>nil(), List.<JCTree.JCVariableDecl>nil(), List.<JCTree.JCExpression>nil(), genConstrBody, null);
            JavacHandlerUtil.recursiveSetGeneratedBy(c, errorNode);
            JavacHandlerUtil.injectMethod(fieldsType, c);
        }
        ArrayList<JCTree.JCVariableDecl> generated = new ArrayList<JCTree.JCVariableDecl>();
        for (JavacNode field : fields) {
            JCTree.JCExpression init;
            JCTree.JCExpression returnType;
            Name fName = ((JCTree.JCVariableDecl)field.get()).name;
            if (uppercase) {
                fName = typeNode.toName(HandlerUtil.camelCaseToConstant(fName.toString()));
            }
            if (JavacHandlerUtil.fieldExists(fName.toString(), fieldsType) != JavacHandlerUtil.MemberExistsResult.NOT_EXISTS) continue;
            JCTree.JCModifiers constantValueMods = maker.Modifiers(0x19L | (asEnum ? 16384L : 0L));
            if (asEnum) {
                returnType = maker.Ident(fieldsName);
                init = maker.NewClass(null, List.<JCTree.JCExpression>nil(), maker.Ident(fieldsName), List.<JCTree.JCExpression>nil(), null);
            } else {
                returnType = JavacHandlerUtil.chainDots(field, "java", "lang", "String");
                init = maker.Literal(field.getName());
            }
            JCTree.JCVariableDecl constantField = maker.VarDef(constantValueMods, fName, returnType, init);
            JavacHandlerUtil.injectField(fieldsType, constantField, false, true);
            JavacHandlerUtil.setGeneratedBy(constantField, errorNode);
            generated.add(constantField);
        }
        for (JCTree.JCVariableDecl cf : generated) {
            JavacHandlerUtil.recursiveSetGeneratedBy(cf, errorNode);
        }
    }
}
