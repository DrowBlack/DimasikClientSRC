package lombok.eclipse.handlers;

import java.util.Arrays;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.core.handlers.HandlerUtil;
import lombok.eclipse.Eclipse;
import lombok.eclipse.EclipseASTAdapter;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.experimental.PackagePrivate;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

@HandlerPriority(value=-2048)
public class HandleFieldDefaults
extends EclipseASTAdapter {
    private static final char[] FIELD_DEFAULTS = "FieldDefaults".toCharArray();

    public boolean generateFieldDefaultsForType(EclipseNode typeNode, EclipseNode pos, AccessLevel level, boolean makeFinal, boolean checkForTypeLevelFieldDefaults) {
        if (checkForTypeLevelFieldDefaults && EclipseHandlerUtil.hasAnnotation(FieldDefaults.class, typeNode)) {
            return true;
        }
        if (!EclipseHandlerUtil.isClassOrEnum(typeNode)) {
            pos.addError("@FieldDefaults is only supported on a class or an enum.");
            return false;
        }
        for (EclipseNode field : typeNode.down()) {
            Class<?> t;
            FieldDeclaration fieldDecl;
            if (field.getKind() != AST.Kind.FIELD || !EclipseHandlerUtil.filterField(fieldDecl = (FieldDeclaration)field.get(), false) || (t = ((ASTNode)field.get()).getClass()) != FieldDeclaration.class) continue;
            this.setFieldDefaultsForField(field, (ASTNode)pos.get(), level, makeFinal);
        }
        return true;
    }

    public void setFieldDefaultsForField(EclipseNode fieldNode, ASTNode pos, AccessLevel level, boolean makeFinal) {
        FieldDeclaration field = (FieldDeclaration)fieldNode.get();
        if (level != null && level != AccessLevel.NONE && (field.modifiers & 7) == 0 && !EclipseHandlerUtil.hasAnnotation(PackagePrivate.class, fieldNode) && (field.modifiers & 8) == 0) {
            field.modifiers |= EclipseHandlerUtil.toEclipseModifier(level);
        }
        if (makeFinal && (field.modifiers & 0x10) == 0 && !EclipseHandlerUtil.hasAnnotation(NonFinal.class, fieldNode) && (field.modifiers & 8) == 0) {
            field.modifiers |= 0x10;
        }
        fieldNode.rebuild();
    }

    @Override
    public void visitType(EclipseNode typeNode, TypeDeclaration type) {
        boolean defaultToFinal;
        AnnotationValues<FieldDefaults> fieldDefaults = null;
        EclipseNode source = typeNode;
        boolean levelIsExplicit = false;
        boolean makeFinalIsExplicit = false;
        FieldDefaults fd = null;
        for (EclipseNode jn : typeNode.down()) {
            Object t;
            if (jn.getKind() != AST.Kind.ANNOTATION) continue;
            Annotation ann = (Annotation)jn.get();
            TypeReference typeTree = ann.type;
            if (typeTree == null || (!(typeTree instanceof SingleTypeReference) ? !(typeTree instanceof QualifiedTypeReference) || !Eclipse.nameEquals((char[][])(t = (Object)((QualifiedTypeReference)typeTree).tokens), "lombok.experimental.FieldDefaults") : !Arrays.equals(t = ((SingleTypeReference)typeTree).token, FIELD_DEFAULTS))) continue;
            if (!EclipseHandlerUtil.typeMatches(FieldDefaults.class, jn, typeTree)) continue;
            source = jn;
            fieldDefaults = EclipseHandlerUtil.createAnnotation(FieldDefaults.class, jn);
            levelIsExplicit = fieldDefaults.isExplicit("level");
            makeFinalIsExplicit = fieldDefaults.isExplicit("makeFinal");
            HandlerUtil.handleExperimentalFlagUsage(jn, ConfigurationKeys.FIELD_DEFAULTS_FLAG_USAGE, "@FieldDefaults");
            fd = fieldDefaults.getInstance();
            if (!levelIsExplicit && !makeFinalIsExplicit) {
                jn.addError("This does nothing; provide either level or makeFinal or both.");
            }
            if (!levelIsExplicit || fd.level() != AccessLevel.NONE) break;
            jn.addError("AccessLevel.NONE doesn't mean anything here. Pick another value.");
            levelIsExplicit = false;
            break;
        }
        if (fd == null && (type.modifiers & 0x2200) != 0) {
            return;
        }
        boolean defaultToPrivate = levelIsExplicit ? false : Boolean.TRUE.equals(typeNode.getAst().readConfiguration(ConfigurationKeys.FIELD_DEFAULTS_PRIVATE_EVERYWHERE));
        boolean bl = defaultToFinal = makeFinalIsExplicit ? false : Boolean.TRUE.equals(typeNode.getAst().readConfiguration(ConfigurationKeys.FIELD_DEFAULTS_FINAL_EVERYWHERE));
        if (!defaultToPrivate && !defaultToFinal && fieldDefaults == null) {
            return;
        }
        if (fieldDefaults == null && !EclipseHandlerUtil.isClassOrEnum(typeNode)) {
            return;
        }
        AccessLevel fdAccessLevel = fieldDefaults != null && levelIsExplicit ? fd.level() : (defaultToPrivate ? AccessLevel.PRIVATE : null);
        boolean fdToFinal = fieldDefaults != null && makeFinalIsExplicit ? fd.makeFinal() : defaultToFinal;
        this.generateFieldDefaultsForType(typeNode, source, fdAccessLevel, fdToFinal, false);
    }
}
