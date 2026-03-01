package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.core.handlers.HandlerUtil;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.experimental.PackagePrivate;
import lombok.javac.JavacASTAdapter;
import lombok.javac.JavacNode;
import lombok.javac.handlers.JavacHandlerUtil;

@HandlerPriority(value=-2048)
public class HandleFieldDefaults
extends JavacASTAdapter {
    public boolean generateFieldDefaultsForType(JavacNode typeNode, JavacNode errorNode, AccessLevel level, boolean makeFinal, boolean checkForTypeLevelFieldDefaults) {
        if (checkForTypeLevelFieldDefaults && JavacHandlerUtil.hasAnnotation(FieldDefaults.class, typeNode)) {
            return true;
        }
        if (!JavacHandlerUtil.isClassOrEnum(typeNode)) {
            errorNode.addError("@FieldDefaults is only supported on a class or an enum.");
            return false;
        }
        for (JavacNode field : typeNode.down()) {
            if (field.getKind() != AST.Kind.FIELD) continue;
            JCTree.JCVariableDecl fieldDecl = (JCTree.JCVariableDecl)field.get();
            if (fieldDecl.name.toString().startsWith("$")) continue;
            this.setFieldDefaultsForField(field, level, makeFinal);
        }
        return true;
    }

    public void setFieldDefaultsForField(JavacNode fieldNode, AccessLevel level, boolean makeFinal) {
        JCTree.JCVariableDecl field = (JCTree.JCVariableDecl)fieldNode.get();
        if (level != null && level != AccessLevel.NONE && (field.mods.flags & 7L) == 0L && !JavacHandlerUtil.hasAnnotationAndDeleteIfNeccessary(PackagePrivate.class, fieldNode) && (field.mods.flags & 8L) == 0L) {
            field.mods.flags |= (long)JavacHandlerUtil.toJavacModifier(level);
        }
        if (makeFinal && (field.mods.flags & 0x10L) == 0L && !JavacHandlerUtil.hasAnnotationAndDeleteIfNeccessary(NonFinal.class, fieldNode) && (field.mods.flags & 8L) == 0L) {
            field.mods.flags |= 0x10L;
        }
        fieldNode.rebuild();
    }

    @Override
    public void visitType(JavacNode typeNode, JCTree.JCClassDecl type) {
        boolean defaultToFinal;
        AnnotationValues<FieldDefaults> fieldDefaults = null;
        JavacNode source = typeNode;
        boolean levelIsExplicit = false;
        boolean makeFinalIsExplicit = false;
        FieldDefaults fd = null;
        for (JavacNode jn : typeNode.down()) {
            String typeTreeToString;
            if (jn.getKind() != AST.Kind.ANNOTATION) continue;
            JCTree.JCAnnotation ann = (JCTree.JCAnnotation)jn.get();
            JCTree typeTree = ann.annotationType;
            if (typeTree == null || !(typeTreeToString = typeTree.toString()).equals("FieldDefaults") && !typeTreeToString.equals("lombok.experimental.FieldDefaults") || !JavacHandlerUtil.typeMatches(FieldDefaults.class, jn, typeTree)) continue;
            source = jn;
            fieldDefaults = JavacHandlerUtil.createAnnotation(FieldDefaults.class, jn);
            levelIsExplicit = fieldDefaults.isExplicit("level");
            makeFinalIsExplicit = fieldDefaults.isExplicit("makeFinal");
            HandlerUtil.handleExperimentalFlagUsage(jn, ConfigurationKeys.FIELD_DEFAULTS_FLAG_USAGE, "@FieldDefaults");
            fd = fieldDefaults.getInstance();
            if (!levelIsExplicit && !makeFinalIsExplicit) {
                jn.addError("This does nothing; provide either level or makeFinal or both.");
            }
            if (levelIsExplicit && fd.level() == AccessLevel.NONE) {
                jn.addError("AccessLevel.NONE doesn't mean anything here. Pick another value.");
                levelIsExplicit = false;
            }
            JavacHandlerUtil.deleteAnnotationIfNeccessary(jn, FieldDefaults.class);
            JavacHandlerUtil.deleteImportFromCompilationUnit(jn, "lombok.AccessLevel");
            break;
        }
        if (fd == null && (type.mods.flags & 0x2200L) != 0L) {
            return;
        }
        boolean defaultToPrivate = levelIsExplicit ? false : Boolean.TRUE.equals(typeNode.getAst().readConfiguration(ConfigurationKeys.FIELD_DEFAULTS_PRIVATE_EVERYWHERE));
        boolean bl = defaultToFinal = makeFinalIsExplicit ? false : Boolean.TRUE.equals(typeNode.getAst().readConfiguration(ConfigurationKeys.FIELD_DEFAULTS_FINAL_EVERYWHERE));
        if (!defaultToPrivate && !defaultToFinal && fieldDefaults == null) {
            return;
        }
        if (fieldDefaults == null && !JavacHandlerUtil.isClassOrEnum(typeNode)) {
            return;
        }
        AccessLevel fdAccessLevel = fieldDefaults != null && levelIsExplicit ? fd.level() : (defaultToPrivate ? AccessLevel.PRIVATE : null);
        boolean fdToFinal = fieldDefaults != null && makeFinalIsExplicit ? fd.makeFinal() : defaultToFinal;
        this.generateFieldDefaultsForType(typeNode, source, fdAccessLevel, fdToFinal, false);
    }
}
