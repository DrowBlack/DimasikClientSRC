package lombok.patcher.scripts;

import java.util.ArrayList;
import java.util.List;
import lombok.patcher.Hook;
import lombok.patcher.MethodLogistics;
import lombok.patcher.PatchScript;
import lombok.patcher.TargetMatcher;
import lombok.patcher.TransplantMapper;
import lombok.patcher.scripts.MethodLevelPatchScript;
import lombok.patcher.scripts.WrapperMethodDescriptor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SetSymbolDuringMethodCallScript
extends MethodLevelPatchScript {
    private final Hook callToWrap;
    private final String symbol;
    private final boolean report;

    @Override
    public String getPatchScriptName() {
        return "set symbol " + this.symbol + " if " + this.callToWrap.getMethodName() + " is invoked in " + this.describeMatchers();
    }

    SetSymbolDuringMethodCallScript(List<TargetMatcher> matchers, Hook callToWrap, String symbol, boolean report) {
        super(matchers);
        if (callToWrap == null) {
            throw new NullPointerException("callToWrap");
        }
        if (symbol == null) {
            throw new NullPointerException("symbol");
        }
        this.callToWrap = callToWrap;
        this.symbol = symbol;
        this.report = report;
    }

    @Override
    protected PatchScript.MethodPatcher createPatcher(ClassWriter writer, final String classSpec, TransplantMapper transplantMapper) {
        final ArrayList descriptors = new ArrayList();
        PatchScript.MethodPatcher patcher = new PatchScript.MethodPatcher(writer, transplantMapper, new PatchScript.MethodPatcherFactory(){

            public MethodVisitor createMethodVisitor(String name, String desc, MethodVisitor parent, MethodLogistics logistics) {
                return new WrapWithSymbol(name, parent, classSpec, descriptors);
            }
        }){

            public void visitEnd() {
                for (WrapperMethodDescriptor wmd : descriptors) {
                    SetSymbolDuringMethodCallScript.this.makeWrapperMethod(this, wmd);
                }
                super.visitEnd();
            }
        };
        return patcher;
    }

    private void makeWrapperMethod(ClassVisitor cv, WrapperMethodDescriptor wmd) {
        MethodVisitor mv = cv.visitMethod(4106, wmd.getWrapperName(), wmd.getWrapperDescriptor(), null, null);
        MethodLogistics logistics = new MethodLogistics(8, wmd.getWrapperDescriptor());
        mv.visitCode();
        Label start = new Label();
        Label end = new Label();
        Label handler = new Label();
        mv.visitTryCatchBlock(start, end, handler, null);
        mv.visitLabel(start);
        mv.visitLdcInsn(this.symbol);
        mv.visitMethodInsn(184, "lombok/patcher/Symbols", "push", "(Ljava/lang/String;)V", false);
        int i = 0;
        while (i < logistics.getParamCount()) {
            logistics.generateLoadOpcodeForParam(i, mv);
            ++i;
        }
        mv.visitMethodInsn(wmd.getOpcode(), wmd.getOwner(), wmd.getName(), wmd.getTargetDescriptor(), wmd.isItf());
        mv.visitLabel(end);
        mv.visitMethodInsn(184, "lombok/patcher/Symbols", "pop", "()V", false);
        logistics.generateReturnOpcode(mv);
        mv.visitLabel(handler);
        mv.visitFrame(0, 0, null, 1, new Object[]{"java/lang/Throwable"});
        mv.visitMethodInsn(184, "lombok/patcher/Symbols", "pop", "()V", false);
        mv.visitInsn(191);
        mv.visitMaxs(Math.max(1, logistics.getParamCount()), logistics.getParamCount());
        mv.visitEnd();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class WrapWithSymbol
    extends MethodVisitor {
        private final String selfMethodName;
        private final String selfTypeName;
        private final List<WrapperMethodDescriptor> descriptors;

        public WrapWithSymbol(String selfMethodName, MethodVisitor mv, String selfTypeName, List<WrapperMethodDescriptor> descriptors) {
            super(589824, mv);
            this.selfMethodName = selfMethodName;
            this.selfTypeName = selfTypeName;
            this.descriptors = descriptors;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            boolean addOwner;
            if (opcode == 185 || opcode == 182) {
                addOwner = true;
            } else if (opcode == 184) {
                addOwner = false;
            } else {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
            if (!(SetSymbolDuringMethodCallScript.this.callToWrap.getClassSpec().equals(owner) && SetSymbolDuringMethodCallScript.this.callToWrap.getMethodName().equals(name) && SetSymbolDuringMethodCallScript.this.callToWrap.getMethodDescriptor().equals(desc))) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
            String fixedDesc = addOwner ? "(L" + SetSymbolDuringMethodCallScript.this.callToWrap.getClassSpec() + ";" + desc.substring(1) : desc;
            WrapperMethodDescriptor wmd = new WrapperMethodDescriptor(this.descriptors.size(), opcode, owner, name, fixedDesc, desc, itf);
            if (SetSymbolDuringMethodCallScript.this.report) {
                System.out.println("Changing method " + this.selfTypeName + "::" + this.selfMethodName + " wrapping call to " + owner + "::" + name + " to set symbol " + SetSymbolDuringMethodCallScript.this.symbol);
            }
            super.visitMethodInsn(184, this.selfTypeName, wmd.getWrapperName(), fixedDesc, false);
            this.descriptors.add(wmd);
        }
    }
}
