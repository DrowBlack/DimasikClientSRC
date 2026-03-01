package cpw.mods.modlauncher;

import cpw.mods.modlauncher.api.ITransformerVotingContext;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public class PredicateVisitor
extends ClassVisitor {
    private ITransformerVotingContext.MethodPredicate methodPredicate;
    private ITransformerVotingContext.FieldPredicate fieldPredicate;
    private ITransformerVotingContext.ClassPredicate classPredicate;
    private boolean result;

    PredicateVisitor(ITransformerVotingContext.FieldPredicate fieldPredicate) {
        super(458752);
        this.fieldPredicate = fieldPredicate;
    }

    PredicateVisitor(ITransformerVotingContext.MethodPredicate methodPredicate) {
        super(458752);
        this.methodPredicate = methodPredicate;
    }

    PredicateVisitor(ITransformerVotingContext.ClassPredicate classPredicate) {
        super(458752);
        this.classPredicate = classPredicate;
    }

    boolean getResult() {
        return this.result;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        this.result = this.fieldPredicate == null || this.fieldPredicate.test(access, name, descriptor, signature, value);
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        this.result = this.methodPredicate == null || this.methodPredicate.test(access, name, descriptor, signature, exceptions);
        return null;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.result = this.classPredicate == null || this.classPredicate.test(version, access, name, signature, superName, interfaces);
    }
}
