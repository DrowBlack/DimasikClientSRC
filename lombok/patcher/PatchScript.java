package lombok.patcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import lombok.patcher.Hook;
import lombok.patcher.MethodLogistics;
import lombok.patcher.MethodTarget;
import lombok.patcher.TargetMatcher;
import lombok.patcher.TransplantMapper;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class PatchScript {
    public String getPatchScriptName() {
        return this.getClass().getSimpleName();
    }

    public abstract Collection<String> getClassesToReload();

    public static boolean classMatches(String className, Collection<String> classSpecs) {
        for (String classSpec : classSpecs) {
            if (!MethodTarget.typeMatches(className, classSpec)) continue;
            return true;
        }
        return false;
    }

    public abstract boolean wouldPatch(String var1);

    public abstract byte[] patch(String var1, byte[] var2, TransplantMapper var3);

    protected byte[] runASM(byte[] byteCode, boolean computeStacks, TransplantMapper transplantMapper) {
        int flags;
        ClassReader reader = new ClassReader(byteCode);
        int classFileFormatVersion = 48;
        if (byteCode.length > 7) {
            classFileFormatVersion = byteCode[7] & 0xFF;
        }
        int n = flags = classFileFormatVersion < 50 ? 2 : 0;
        if (computeStacks) {
            flags |= 1;
        }
        FixedClassWriter writer = new FixedClassWriter(reader, flags);
        ClassVisitor visitor = this.createClassVisitor(writer, reader.getClassName(), transplantMapper);
        reader.accept(visitor, 0);
        return writer.toByteArray();
    }

    protected ClassVisitor createClassVisitor(ClassWriter writer, String classSpec, TransplantMapper transplantMapper) {
        throw new IllegalStateException("If you're going to call runASM, then you need to implement createClassVisitor");
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static byte[] readStream(String resourceName) {
        byte[] byArray;
        InputStream wrapStream = null;
        try {
            wrapStream = PatchScript.class.getResourceAsStream(resourceName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] b = new byte[65536];
            while (true) {
                int r;
                if ((r = wrapStream.read(b)) == -1) {
                    byArray = baos.toByteArray();
                    if (wrapStream == null) return byArray;
                    break;
                }
                baos.write(b, 0, r);
            }
        }
        catch (Exception e) {
            try {
                throw new IllegalArgumentException("resource " + resourceName + " does not exist.", e);
            }
            catch (Throwable throwable) {
                if (wrapStream == null) throw throwable;
                try {
                    wrapStream.close();
                    throw throwable;
                }
                catch (IOException iOException) {}
                throw throwable;
            }
        }
        try {
            wrapStream.close();
            return byArray;
        }
        catch (IOException iOException) {}
        return byArray;
    }

    protected static void insertMethod(final Hook methodToInsert, final MethodVisitor target) {
        byte[] classData = PatchScript.readStream("/" + methodToInsert.getClassSpec() + ".class");
        ClassReader reader = new ClassReader(classData);
        NoopClassVisitor methodFinder = new NoopClassVisitor(){

            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if (name.equals(methodToInsert.getMethodName()) && desc.equals(methodToInsert.getMethodDescriptor())) {
                    return new InsertBodyOfMethodIntoAnotherVisitor(target);
                }
                return null;
            }
        };
        reader.accept(methodFinder, 0);
    }

    protected static void transplantMethod(String resourceName, final Hook methodToTransplant, final ClassVisitor target) {
        byte[] classData = PatchScript.readStream(resourceName);
        ClassReader reader = new ClassReader(classData);
        NoopClassVisitor methodFinder = new NoopClassVisitor(){

            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if (name.equals(methodToTransplant.getMethodName()) && desc.equals(methodToTransplant.getMethodDescriptor())) {
                    return target.visitMethod(access, name, desc, signature, exceptions);
                }
                return null;
            }
        };
        reader.accept(methodFinder, 0);
    }

    private static class FixedClassWriter
    extends ClassWriter {
        FixedClassWriter(ClassReader classReader, int flags) {
            super(classReader, flags);
        }

        protected String getCommonSuperClass(String type1, String type2) {
            try {
                return super.getCommonSuperClass(type1, type2);
            }
            catch (Throwable throwable) {
                return "java/lang/Object";
            }
        }
    }

    private static final class InsertBodyOfMethodIntoAnotherVisitor
    extends MethodVisitor {
        private InsertBodyOfMethodIntoAnotherVisitor(MethodVisitor mv) {
            super(589824, mv);
        }

        public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
            return null;
        }

        public void visitMaxs(int maxStack, int maxLocals) {
        }

        public void visitLineNumber(int line, Label start) {
        }

        public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        }

        public void visitEnd() {
        }

        public void visitCode() {
        }

        public void visitInsn(int opcode) {
            if (opcode == 177 || opcode == 176 || opcode == 172 || opcode == 175 || opcode == 174 || opcode == 173) {
                return;
            }
            super.visitInsn(opcode);
        }

        public void visitAttribute(Attribute attr) {
        }

        public AnnotationVisitor visitAnnotationDefault() {
            return null;
        }

        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return null;
        }
    }

    protected static class MethodPatcher
    extends ClassVisitor {
        private List<TargetMatcher> targets = new ArrayList<TargetMatcher>();
        private String ownClassSpec;
        private final MethodPatcherFactory factory;
        private List<Hook> transplants = new ArrayList<Hook>();
        private final TransplantMapper transplantMapper;
        private int classFileFormatVersion;

        public MethodPatcher(ClassVisitor cv, TransplantMapper transplantMapper, MethodPatcherFactory factory) {
            super(589824, cv);
            this.factory = factory;
            this.transplantMapper = transplantMapper;
        }

        public String getOwnClassSpec() {
            return this.ownClassSpec;
        }

        public void addTargetMatcher(TargetMatcher t) {
            this.targets.add(t);
        }

        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.ownClassSpec = name;
            this.classFileFormatVersion = version;
            super.visit(version, access, name, signature, superName, interfaces);
        }

        public void addTransplant(Hook transplant) {
            if (transplant == null) {
                throw new NullPointerException("transplant");
            }
            this.transplants.add(transplant);
        }

        public void visitEnd() {
            for (Hook transplant : this.transplants) {
                String resourceName = "/" + this.transplantMapper.mapResourceName(this.classFileFormatVersion, String.valueOf(transplant.getClassSpec()) + ".class");
                PatchScript.transplantMethod(resourceName, transplant, this.cv);
            }
        }

        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor visitor = super.visitMethod(access, name, desc, signature, exceptions);
            Iterator<Hook> it = this.transplants.iterator();
            while (it.hasNext()) {
                Hook h = it.next();
                if (!h.getMethodName().equals(name) || !h.getMethodDescriptor().equals(desc)) continue;
                it.remove();
            }
            for (TargetMatcher t : this.targets) {
                if (!t.matches(this.ownClassSpec, name, desc)) continue;
                return this.factory.createMethodVisitor(name, desc, visitor, new MethodLogistics(access, desc));
            }
            return visitor;
        }
    }

    public static interface MethodPatcherFactory {
        public MethodVisitor createMethodVisitor(String var1, String var2, MethodVisitor var3, MethodLogistics var4);
    }

    private static abstract class NoopClassVisitor
    extends ClassVisitor {
        public NoopClassVisitor() {
            super(589824);
        }

        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        }

        public void visitAttribute(Attribute attr) {
        }

        public void visitEnd() {
        }

        public void visitOuterClass(String owner, String name, String desc) {
        }

        public void visitSource(String source, String debug) {
        }

        public void visitInnerClass(String name, String outerName, String innerName, int access) {
        }

        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return null;
        }

        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            return null;
        }

        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            return null;
        }
    }
}
