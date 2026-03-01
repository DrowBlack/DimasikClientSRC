package lombok.bytecode;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.bytecode.AsmUtil;
import lombok.bytecode.ClassFileMetaData;
import lombok.bytecode.FixedClassWriter;
import lombok.core.DiagnosticsReceiver;
import lombok.core.PostCompilerTransformation;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class PreventNullAnalysisRemover
implements PostCompilerTransformation {
    @Override
    public byte[] applyTransformations(byte[] original, String fileName, DiagnosticsReceiver diagnostics) {
        if (!new ClassFileMetaData(original).usesMethod("lombok/Lombok", "preventNullAnalysis")) {
            return null;
        }
        byte[] fixedByteCode = AsmUtil.fixJSRInlining(original);
        ClassReader reader = new ClassReader(fixedByteCode);
        FixedClassWriter writer = new FixedClassWriter(0);
        final AtomicBoolean changesMade = new AtomicBoolean();
        reader.accept(new ClassVisitor(589824, writer){

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                class PreventNullAnalysisVisitor
                extends MethodVisitor {
                    private final /* synthetic */ AtomicBoolean val$changesMade;

                    PreventNullAnalysisVisitor(MethodVisitor mv, AtomicBoolean atomicBoolean) {
                        this.val$changesMade = atomicBoolean;
                        super(589824, mv);
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                        boolean hit = true;
                        if (hit && opcode != 184) {
                            hit = false;
                        }
                        if (hit && !"preventNullAnalysis".equals(name)) {
                            hit = false;
                        }
                        if (hit && !"lombok/Lombok".equals(owner)) {
                            hit = false;
                        }
                        if (hit && !"(Ljava/lang/Object;)Ljava/lang/Object;".equals(desc)) {
                            hit = false;
                        }
                        if (hit) {
                            this.val$changesMade.set(true);
                            if (System.getProperty("lombok.debugAsmOnly", null) != null) {
                                super.visitMethodInsn(opcode, owner, name, desc, itf);
                            }
                        } else {
                            super.visitMethodInsn(opcode, owner, name, desc, itf);
                        }
                    }
                }
                return new PreventNullAnalysisVisitor(super.visitMethod(access, name, desc, signature, exceptions), changesMade);
            }
        }, 0);
        return changesMade.get() ? writer.toByteArray() : null;
    }
}
