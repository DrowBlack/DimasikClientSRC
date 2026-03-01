package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.Data;
import lombok.core.AnnotationValues;
import lombok.core.handlers.HandlerUtil;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.HandleConstructor;
import lombok.javac.handlers.HandleEqualsAndHashCode;
import lombok.javac.handlers.HandleGetter;
import lombok.javac.handlers.HandleSetter;
import lombok.javac.handlers.HandleToString;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleData
extends JavacAnnotationHandler<Data> {
    private HandleConstructor handleConstructor = new HandleConstructor();
    private HandleGetter handleGetter = new HandleGetter();
    private HandleSetter handleSetter = new HandleSetter();
    private HandleEqualsAndHashCode handleEqualsAndHashCode = new HandleEqualsAndHashCode();
    private HandleToString handleToString = new HandleToString();

    @Override
    public void handle(AnnotationValues<Data> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.DATA_FLAG_USAGE, "@Data");
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Data.class);
        JavacNode typeNode = (JavacNode)annotationNode.up();
        if (!JavacHandlerUtil.isClass(typeNode)) {
            annotationNode.addError("@Data is only supported on a class.");
            return;
        }
        String staticConstructorName = annotation.getInstance().staticConstructor();
        this.handleConstructor.generateRequiredArgsConstructor(typeNode, AccessLevel.PUBLIC, staticConstructorName, HandleConstructor.SkipIfConstructorExists.YES, annotationNode);
        this.handleConstructor.generateExtraNoArgsConstructor(typeNode, annotationNode);
        this.handleGetter.generateGetterForType(typeNode, annotationNode, AccessLevel.PUBLIC, true, List.<JCTree.JCAnnotation>nil());
        this.handleSetter.generateSetterForType(typeNode, annotationNode, AccessLevel.PUBLIC, true, List.<JCTree.JCAnnotation>nil(), List.<JCTree.JCAnnotation>nil());
        this.handleEqualsAndHashCode.generateEqualsAndHashCodeForType(typeNode, annotationNode);
        this.handleToString.generateToStringForType(typeNode, annotationNode);
    }
}
