package lombok.patcher.scripts;

import java.util.List;
import java.util.Set;
import lombok.patcher.Hook;
import lombok.patcher.MethodLogistics;
import lombok.patcher.PatchScript;
import lombok.patcher.StackRequest;
import lombok.patcher.TargetMatcher;
import lombok.patcher.TransplantMapper;
import lombok.patcher.scripts.MethodLevelPatchScript;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class WrapReturnValuesScript
extends MethodLevelPatchScript {
    private final Hook wrapper;
    private final Set<StackRequest> requests;
    private final boolean hijackReturnValue;
    private final boolean transplant;
    private final boolean insert;
    private final boolean cast;

    @Override
    public String getPatchScriptName() {
        return "wrap returns with " + this.wrapper.getMethodName() + " in " + this.describeMatchers();
    }

    WrapReturnValuesScript(List<TargetMatcher> matchers, Hook wrapper, boolean transplant, boolean insert, boolean cast, Set<StackRequest> requests) {
        super(matchers);
        if (wrapper == null) {
            throw new NullPointerException("wrapper");
        }
        this.wrapper = wrapper;
        this.hijackReturnValue = !wrapper.getMethodDescriptor().endsWith(")V");
        this.requests = requests;
        this.transplant = transplant;
        this.insert = insert;
        boolean bl = this.cast = cast && this.hijackReturnValue;
        assert (!insert || !transplant);
        assert (!cast || !insert);
    }

    @Override
    protected PatchScript.MethodPatcher createPatcher(ClassWriter writer, final String classSpec, TransplantMapper transplantMapper) {
        PatchScript.MethodPatcher patcher = new PatchScript.MethodPatcher(writer, transplantMapper, new PatchScript.MethodPatcherFactory(){

            public MethodVisitor createMethodVisitor(String name, String desc, MethodVisitor parent, MethodLogistics logistics) {
                return new WrapReturnValues(parent, logistics, classSpec, desc);
            }
        });
        if (this.transplant) {
            patcher.addTransplant(this.wrapper);
        }
        return patcher;
    }

    private static String extractReturnValueFromDesc(String desc) {
        int lastIdx;
        int n = lastIdx = desc == null ? -1 : desc.lastIndexOf(41);
        if (lastIdx == -1) {
            return null;
        }
        String rd = desc.substring(lastIdx + 1);
        if (rd.startsWith("L") && rd.endsWith(";")) {
            return rd.substring(1, rd.length() - 1);
        }
        return rd;
    }

    public String toString() {
        return "WrapReturnValues(wrapper: " + this.wrapper + ", hijackReturn: " + this.hijackReturnValue + ", transplant: " + this.transplant + ", insert: " + this.insert + ", requests: " + this.requests + ")";
    }

    private class WrapReturnValues
    extends MethodVisitor {
        private final MethodLogistics logistics;
        private final String ownClassSpec;
        private final String returnValueDesc;

        public WrapReturnValues(MethodVisitor mv, MethodLogistics logistics, String ownClassSpec, String desc) {
            super(589824, mv);
            this.logistics = logistics;
            this.ownClassSpec = ownClassSpec;
            this.returnValueDesc = WrapReturnValuesScript.extractReturnValueFromDesc(desc);
        }

        public void visitInsn(int opcode) {
            if (opcode != this.logistics.getReturnOpcode()) {
                super.visitInsn(opcode);
                return;
            }
            if (WrapReturnValuesScript.this.requests.contains((Object)StackRequest.RETURN_VALUE)) {
                if (!WrapReturnValuesScript.this.hijackReturnValue) {
                    this.logistics.generateDupForReturn(this.mv);
                }
            } else if (WrapReturnValuesScript.this.hijackReturnValue) {
                this.logistics.generatePopForReturn(this.mv);
            }
            if (WrapReturnValuesScript.this.requests.contains((Object)StackRequest.THIS)) {
                this.logistics.generateLoadOpcodeForThis(this.mv);
            }
            for (StackRequest param : StackRequest.PARAMS_IN_ORDER) {
                if (!WrapReturnValuesScript.this.requests.contains((Object)param)) continue;
                this.logistics.generateLoadOpcodeForParam(param.getParamPos(), this.mv);
            }
            if (WrapReturnValuesScript.this.insert) {
                WrapReturnValuesScript.insertMethod(WrapReturnValuesScript.this.wrapper, this.mv);
            } else {
                super.visitMethodInsn(184, WrapReturnValuesScript.this.transplant ? this.ownClassSpec : WrapReturnValuesScript.this.wrapper.getClassSpec(), WrapReturnValuesScript.this.wrapper.getMethodName(), WrapReturnValuesScript.this.wrapper.getMethodDescriptor(), false);
            }
            if (WrapReturnValuesScript.this.cast) {
                super.visitTypeInsn(192, this.returnValueDesc);
            }
            super.visitInsn(opcode);
        }
    }
}
