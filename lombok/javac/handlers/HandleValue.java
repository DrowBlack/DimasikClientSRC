package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import lombok.AccessLevel;
import lombok.ConfigurationKeys;
import lombok.Value;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.core.handlers.HandlerUtil;
import lombok.experimental.NonFinal;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.HandleConstructor;
import lombok.javac.handlers.HandleEqualsAndHashCode;
import lombok.javac.handlers.HandleFieldDefaults;
import lombok.javac.handlers.HandleGetter;
import lombok.javac.handlers.HandleToString;
import lombok.javac.handlers.JavacHandlerUtil;

@HandlerPriority(value=-512)
public class HandleValue
extends JavacAnnotationHandler<Value> {
    private HandleFieldDefaults handleFieldDefaults = new HandleFieldDefaults();
    private HandleConstructor handleConstructor = new HandleConstructor();
    private HandleGetter handleGetter = new HandleGetter();
    private HandleEqualsAndHashCode handleEqualsAndHashCode = new HandleEqualsAndHashCode();
    private HandleToString handleToString = new HandleToString();

    @Override
    public void handle(AnnotationValues<Value> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        boolean notAClass;
        HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.VALUE_FLAG_USAGE, "@Value");
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, Value.class, "lombok.experimental.Value");
        JavacNode typeNode = (JavacNode)annotationNode.up();
        boolean bl = notAClass = !JavacHandlerUtil.isClass(typeNode);
        if (notAClass) {
            annotationNode.addError("@Value is only supported on a class.");
            return;
        }
        String staticConstructorName = annotation.getInstance().staticConstructor();
        if (!JavacHandlerUtil.hasAnnotationAndDeleteIfNeccessary(NonFinal.class, typeNode)) {
            JCTree.JCModifiers jcm = ((JCTree.JCClassDecl)typeNode.get()).mods;
            if ((jcm.flags & 0x10L) == 0L) {
                jcm.flags |= 0x10L;
            }
        }
        this.handleFieldDefaults.generateFieldDefaultsForType(typeNode, annotationNode, AccessLevel.PRIVATE, true, true);
        this.handleConstructor.generateAllArgsConstructor(typeNode, AccessLevel.PUBLIC, staticConstructorName, HandleConstructor.SkipIfConstructorExists.YES, annotationNode);
        this.handleConstructor.generateExtraNoArgsConstructor(typeNode, annotationNode);
        this.handleGetter.generateGetterForType(typeNode, annotationNode, AccessLevel.PUBLIC, true, List.<JCTree.JCAnnotation>nil());
        this.handleEqualsAndHashCode.generateEqualsAndHashCodeForType(typeNode, annotationNode);
        this.handleToString.generateToStringForType(typeNode, annotationNode);
    }
}
