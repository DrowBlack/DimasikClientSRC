package lombok.bytecode;

import lombok.bytecode.FixedClassWriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.JSRInlinerAdapter;

class AsmUtil {
    private AsmUtil() {
        throw new UnsupportedOperationException();
    }

    static byte[] fixJSRInlining(byte[] byteCode) {
        ClassReader reader = new ClassReader(byteCode);
        FixedClassWriter writer = new FixedClassWriter(reader, 0);
        ClassVisitor visitor = new ClassVisitor(589824, writer){

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                return new JSRInlinerAdapter(super.visitMethod(access, name, desc, signature, exceptions), access, name, desc, signature, exceptions);
            }
        };
        reader.accept(visitor, 0);
        return writer.toByteArray();
    }
}
