package lombok.bytecode;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

class FixedClassWriter
extends ClassWriter {
    FixedClassWriter(ClassReader classReader, int flags) {
        super(classReader, flags);
    }

    FixedClassWriter(int flags) {
        super(flags);
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        try {
            return super.getCommonSuperClass(type1, type2);
        }
        catch (OutOfMemoryError e) {
            throw e;
        }
        catch (Throwable throwable) {
            return "java/lang/Object";
        }
    }
}
