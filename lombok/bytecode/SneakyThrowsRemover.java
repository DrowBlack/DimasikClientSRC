package lombok.bytecode;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.bytecode.AsmUtil;
import lombok.bytecode.ClassFileMetaData;
import lombok.core.DiagnosticsReceiver;
import lombok.core.PostCompilerTransformation;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class SneakyThrowsRemover
implements PostCompilerTransformation {
    @Override
    public byte[] applyTransformations(byte[] original, String fileName, final DiagnosticsReceiver diagnostics) {
        if (!new ClassFileMetaData(original).usesMethod("lombok/Lombok", "sneakyThrow")) {
            return null;
        }
        byte[] fixedByteCode = AsmUtil.fixJSRInlining(original);
        ClassReader reader = new ClassReader(fixedByteCode);
        ClassWriter writer = new ClassWriter(0);
        final AtomicBoolean changesMade = new AtomicBoolean();
        reader.accept(new ClassVisitor(589824, writer){

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                class SneakyThrowsRemoverVisitor
                extends MethodVisitor {
                    private boolean methodInsnQueued;
                    private final /* synthetic */ DiagnosticsReceiver val$diagnostics;
                    private final /* synthetic */ AtomicBoolean val$changesMade;

                    SneakyThrowsRemoverVisitor(MethodVisitor mv, DiagnosticsReceiver diagnosticsReceiver, AtomicBoolean atomicBoolean) {
                        this.val$diagnostics = diagnosticsReceiver;
                        this.val$changesMade = atomicBoolean;
                        super(589824, mv);
                        this.methodInsnQueued = false;
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                        if (opcode == 184 && "sneakyThrow".equals(name) && "lombok/Lombok".equals(owner) && "(Ljava/lang/Throwable;)Ljava/lang/RuntimeException;".equals(desc)) {
                            if (System.getProperty("lombok.debugAsmOnly", null) != null) {
                                super.visitMethodInsn(opcode, owner, name, desc, itf);
                            } else {
                                this.methodInsnQueued = true;
                            }
                        } else {
                            super.visitMethodInsn(opcode, owner, name, desc, itf);
                        }
                    }

                    private void handleQueue() {
                        if (!this.methodInsnQueued) {
                            return;
                        }
                        super.visitMethodInsn(184, "lombok/Lombok", "sneakyThrow", "(Ljava/lang/Throwable;)Ljava/lang/RuntimeException;", false);
                        this.methodInsnQueued = false;
                        this.val$diagnostics.addWarning("Proper usage is: throw lombok.Lombok.sneakyThrow(someException);. You did not 'throw' it. Because of this, the call to sneakyThrow remains in your classfile and you will need lombok.jar on the classpath at runtime.");
                    }

                    @Override
                    public void visitInsn(int arg0) {
                        if (this.methodInsnQueued && arg0 == 191) {
                            this.val$changesMade.set(true);
                            this.methodInsnQueued = false;
                        }
                        this.handleQueue();
                        super.visitInsn(arg0);
                    }

                    @Override
                    public void visitFrame(int arg0, int arg1, Object[] arg2, int arg3, Object[] arg4) {
                        this.handleQueue();
                        super.visitFrame(arg0, arg1, arg2, arg3, arg4);
                    }

                    @Override
                    public void visitIincInsn(int arg0, int arg1) {
                        this.handleQueue();
                        super.visitIincInsn(arg0, arg1);
                    }

                    @Override
                    public void visitFieldInsn(int arg0, String arg1, String arg2, String arg3) {
                        this.handleQueue();
                        super.visitFieldInsn(arg0, arg1, arg2, arg3);
                    }

                    @Override
                    public void visitIntInsn(int arg0, int arg1) {
                        this.handleQueue();
                        super.visitIntInsn(arg0, arg1);
                    }

                    @Override
                    public void visitEnd() {
                        this.handleQueue();
                        super.visitEnd();
                    }

                    @Override
                    public void visitInvokeDynamicInsn(String arg0, String arg1, Handle arg2, Object ... arg3) {
                        this.handleQueue();
                        super.visitInvokeDynamicInsn(arg0, arg1, arg2, arg3);
                    }

                    @Override
                    public void visitLabel(Label arg0) {
                        this.handleQueue();
                        super.visitLabel(arg0);
                    }

                    @Override
                    public void visitJumpInsn(int arg0, Label arg1) {
                        this.handleQueue();
                        super.visitJumpInsn(arg0, arg1);
                    }

                    @Override
                    public void visitLdcInsn(Object arg0) {
                        this.handleQueue();
                        super.visitLdcInsn(arg0);
                    }

                    @Override
                    public void visitLocalVariable(String arg0, String arg1, String arg2, Label arg3, Label arg4, int arg5) {
                        this.handleQueue();
                        super.visitLocalVariable(arg0, arg1, arg2, arg3, arg4, arg5);
                    }

                    @Override
                    public void visitMaxs(int arg0, int arg1) {
                        this.handleQueue();
                        super.visitMaxs(arg0, arg1);
                    }

                    @Override
                    public void visitLookupSwitchInsn(Label arg0, int[] arg1, Label[] arg2) {
                        this.handleQueue();
                        super.visitLookupSwitchInsn(arg0, arg1, arg2);
                    }

                    @Override
                    public void visitMultiANewArrayInsn(String arg0, int arg1) {
                        this.handleQueue();
                        super.visitMultiANewArrayInsn(arg0, arg1);
                    }

                    @Override
                    public void visitVarInsn(int arg0, int arg1) {
                        this.handleQueue();
                        super.visitVarInsn(arg0, arg1);
                    }

                    @Override
                    public void visitTryCatchBlock(Label arg0, Label arg1, Label arg2, String arg3) {
                        this.handleQueue();
                        super.visitTryCatchBlock(arg0, arg1, arg2, arg3);
                    }

                    @Override
                    public void visitTableSwitchInsn(int arg0, int arg1, Label arg2, Label ... arg3) {
                        this.handleQueue();
                        super.visitTableSwitchInsn(arg0, arg1, arg2, arg3);
                    }

                    @Override
                    public void visitTypeInsn(int arg0, String arg1) {
                        this.handleQueue();
                        super.visitTypeInsn(arg0, arg1);
                    }
                }
                return new SneakyThrowsRemoverVisitor(super.visitMethod(access, name, desc, signature, exceptions), diagnostics, changesMade);
            }
        }, 0);
        return changesMade.get() ? writer.toByteArray() : null;
    }
}
