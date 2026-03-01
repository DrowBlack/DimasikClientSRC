package cpw.mods.modlauncher;

import cpw.mods.modlauncher.PredicateVisitor;
import cpw.mods.modlauncher.api.ITransformerActivity;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import java.util.List;
import java.util.function.Supplier;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

class VotingContext
implements ITransformerVotingContext {
    private static final Object[] EMPTY = new Object[0];
    private final String className;
    private final boolean classExists;
    private final Supplier<byte[]> sha256;
    private final List<ITransformerActivity> auditActivities;
    private final String reason;
    private Object node;

    VotingContext(String className, boolean classExists, Supplier<byte[]> sha256sum, List<ITransformerActivity> activities, String reason) {
        this.className = className;
        this.classExists = classExists;
        this.sha256 = sha256sum;
        this.auditActivities = activities;
        this.reason = reason;
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    public boolean doesClassExist() {
        return this.classExists;
    }

    @Override
    public byte[] getInitialClassSha256() {
        return this.sha256.get();
    }

    @Override
    public List<ITransformerActivity> getAuditActivities() {
        return this.auditActivities;
    }

    @Override
    public String getReason() {
        return this.reason;
    }

    <T> void setNode(T node) {
        this.node = node;
    }

    @Override
    public boolean applyFieldPredicate(ITransformerVotingContext.FieldPredicate fieldPredicate) {
        FieldNode fn = (FieldNode)this.node;
        PredicateVisitor predicateVisitor = new PredicateVisitor(fieldPredicate);
        fn.accept(predicateVisitor);
        return predicateVisitor.getResult();
    }

    @Override
    public boolean applyMethodPredicate(ITransformerVotingContext.MethodPredicate methodPredicate) {
        MethodNode mn = (MethodNode)this.node;
        PredicateVisitor predicateVisitor = new PredicateVisitor(methodPredicate);
        mn.accept(predicateVisitor);
        return predicateVisitor.getResult();
    }

    @Override
    public boolean applyClassPredicate(ITransformerVotingContext.ClassPredicate classPredicate) {
        ClassNode cn = (ClassNode)this.node;
        PredicateVisitor predicateVisitor = new PredicateVisitor(classPredicate);
        cn.accept(predicateVisitor);
        return predicateVisitor.getResult();
    }

    @Override
    public boolean applyInstructionPredicate(ITransformerVotingContext.InsnPredicate insnPredicate) {
        MethodNode mn = (MethodNode)this.node;
        boolean result = false;
        AbstractInsnNode[] insnNodes = mn.instructions.toArray();
        for (int i = 0; i < insnNodes.length; ++i) {
            result |= insnPredicate.test(i, insnNodes[i].getOpcode(), this.toObjectArray(insnNodes[0]));
        }
        return result;
    }

    private Object[] toObjectArray(AbstractInsnNode insnNode) {
        if (insnNode instanceof MethodInsnNode) {
            MethodInsnNode methodInsnNode = (MethodInsnNode)insnNode;
            return new Object[]{methodInsnNode.name, methodInsnNode.desc, methodInsnNode.owner, methodInsnNode.itf};
        }
        if (insnNode instanceof FieldInsnNode) {
            FieldInsnNode fieldInsnNode = (FieldInsnNode)insnNode;
            return new Object[]{fieldInsnNode.name, fieldInsnNode.desc, fieldInsnNode.owner};
        }
        return EMPTY;
    }
}
