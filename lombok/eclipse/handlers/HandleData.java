package lombok.eclipse.handlers;

import java.util.Collections;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.Data;
import lombok.core.AnnotationValues;
import lombok.core.handlers.HandlerUtil;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.HandleConstructor;
import lombok.eclipse.handlers.HandleEqualsAndHashCode;
import lombok.eclipse.handlers.HandleGetter;
import lombok.eclipse.handlers.HandleSetter;
import lombok.eclipse.handlers.HandleToString;
import org.eclipse.jdt.internal.compiler.ast.Annotation;

public class HandleData
extends EclipseAnnotationHandler<Data> {
    private HandleGetter handleGetter = new HandleGetter();
    private HandleSetter handleSetter = new HandleSetter();
    private HandleEqualsAndHashCode handleEqualsAndHashCode = new HandleEqualsAndHashCode();
    private HandleToString handleToString = new HandleToString();
    private HandleConstructor handleConstructor = new HandleConstructor();

    @Override
    public void handle(AnnotationValues<Data> annotation, Annotation ast, EclipseNode annotationNode) {
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.DATA_FLAG_USAGE, "@Data");
        Data ann = annotation.getInstance();
        EclipseNode typeNode = (EclipseNode)annotationNode.up();
        if (!EclipseHandlerUtil.isClass(typeNode)) {
            annotationNode.addError("@Data is only supported on a class.");
            return;
        }
        this.handleGetter.generateGetterForType(typeNode, annotationNode, AccessLevel.PUBLIC, true, Collections.<Annotation>emptyList());
        this.handleSetter.generateSetterForType(typeNode, annotationNode, AccessLevel.PUBLIC, true, Collections.<Annotation>emptyList(), Collections.<Annotation>emptyList());
        this.handleEqualsAndHashCode.generateEqualsAndHashCodeForType(typeNode, annotationNode);
        this.handleToString.generateToStringForType(typeNode, annotationNode);
        this.handleConstructor.generateRequiredArgsConstructor(typeNode, AccessLevel.PUBLIC, ann.staticConstructor(), HandleConstructor.SkipIfConstructorExists.YES, Collections.<Annotation>emptyList(), annotationNode);
        this.handleConstructor.generateExtraNoArgsConstructor(typeNode, annotationNode);
    }
}
