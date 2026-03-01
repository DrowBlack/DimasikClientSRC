package lombok.eclipse.handlers;

import java.util.Collections;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.Value;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.core.handlers.HandlerUtil;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.HandleConstructor;
import lombok.eclipse.handlers.HandleEqualsAndHashCode;
import lombok.eclipse.handlers.HandleFieldDefaults;
import lombok.eclipse.handlers.HandleGetter;
import lombok.eclipse.handlers.HandleToString;
import lombok.experimental.NonFinal;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;

@HandlerPriority(value=-512)
public class HandleValue
extends EclipseAnnotationHandler<Value> {
    private HandleFieldDefaults handleFieldDefaults = new HandleFieldDefaults();
    private HandleGetter handleGetter = new HandleGetter();
    private HandleEqualsAndHashCode handleEqualsAndHashCode = new HandleEqualsAndHashCode();
    private HandleToString handleToString = new HandleToString();
    private HandleConstructor handleConstructor = new HandleConstructor();

    @Override
    public void handle(AnnotationValues<Value> annotation, Annotation ast, EclipseNode annotationNode) {
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.VALUE_FLAG_USAGE, "@Value");
        Value ann = annotation.getInstance();
        EclipseNode typeNode = (EclipseNode)annotationNode.up();
        if (!EclipseHandlerUtil.isClass(typeNode)) {
            annotationNode.addError("@Value is only supported on a class.");
            return;
        }
        TypeDeclaration typeDecl = (TypeDeclaration)typeNode.get();
        if (!EclipseHandlerUtil.hasAnnotation(NonFinal.class, typeNode) && (typeDecl.modifiers & 0x10) == 0) {
            typeDecl.modifiers |= 0x10;
            typeNode.rebuild();
        }
        this.handleFieldDefaults.generateFieldDefaultsForType(typeNode, annotationNode, AccessLevel.PRIVATE, true, true);
        this.handleGetter.generateGetterForType(typeNode, annotationNode, AccessLevel.PUBLIC, true, Collections.<Annotation>emptyList());
        this.handleEqualsAndHashCode.generateEqualsAndHashCodeForType(typeNode, annotationNode);
        this.handleToString.generateToStringForType(typeNode, annotationNode);
        this.handleConstructor.generateAllArgsConstructor(typeNode, AccessLevel.PUBLIC, ann.staticConstructor(), HandleConstructor.SkipIfConstructorExists.YES, Collections.<Annotation>emptyList(), annotationNode);
        this.handleConstructor.generateExtraNoArgsConstructor(typeNode, annotationNode);
    }
}
