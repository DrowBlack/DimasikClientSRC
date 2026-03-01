package lombok.patcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import lombok.patcher.MethodTarget;
import org.objectweb.asm.MethodVisitor;

public class MethodLogistics {
    private final int staticOffset;
    private final int returnOpcode;
    private final int returnSize;
    private final List<Integer> loadOpcodes;
    private final List<Integer> paramSizes;
    private final List<Integer> paramIndices;

    public MethodLogistics(int accessFlags, String descriptor) {
        this.staticOffset = (accessFlags & 8) != 0 ? 0 : 1;
        List<String> specs = MethodTarget.decomposeFullDesc(descriptor);
        Iterator<String> it = specs.iterator();
        String returnSpec = it.next();
        this.returnSize = MethodLogistics.sizeOf(returnSpec);
        this.returnOpcode = MethodLogistics.returnOpcodeFor(returnSpec);
        int index = this.staticOffset;
        ArrayList<Integer> paramSizes = new ArrayList<Integer>();
        ArrayList<Integer> paramIndices = new ArrayList<Integer>();
        ArrayList<Integer> loadOpcodes = new ArrayList<Integer>();
        while (it.hasNext()) {
            String spec = it.next();
            int size = MethodLogistics.sizeOf(spec);
            paramSizes.add(size);
            paramIndices.add(index);
            loadOpcodes.add(MethodLogistics.loadOpcodeFor(spec));
            index += size;
        }
        this.paramSizes = Collections.unmodifiableList(paramSizes);
        this.paramIndices = Collections.unmodifiableList(paramIndices);
        this.loadOpcodes = Collections.unmodifiableList(loadOpcodes);
    }

    public boolean isStatic() {
        return this.staticOffset == 0;
    }

    public int getParamCount() {
        return this.paramSizes.size();
    }

    public int getReturnOpcode() {
        return this.returnOpcode;
    }

    public void generateLoadOpcodeForParam(int index, MethodVisitor mv) {
        mv.visitVarInsn(this.loadOpcodes.get(index), this.paramIndices.get(index));
    }

    public void generateLoadOpcodeForThis(MethodVisitor mv) {
        if (this.isStatic()) {
            mv.visitInsn(1);
        } else {
            mv.visitVarInsn(25, 0);
        }
    }

    public void generateReturnOpcode(MethodVisitor mv) {
        mv.visitInsn(this.returnOpcode);
    }

    public void generatePopForReturn(MethodVisitor mv) {
        mv.visitInsn(this.returnSize == 2 ? 88 : 87);
    }

    public void generateDupForReturn(MethodVisitor mv) {
        mv.visitInsn(this.returnSize == 2 ? 92 : 89);
    }

    public static void generateDupForType(String type, MethodVisitor mv) {
        switch (MethodLogistics.sizeOf(type)) {
            default: {
                mv.visitInsn(89);
                break;
            }
            case 2: {
                mv.visitInsn(92);
            }
            case 0: 
        }
    }

    private static int loadOpcodeFor(String spec) {
        switch (spec.charAt(0)) {
            case 'D': {
                return 24;
            }
            case 'J': {
                return 22;
            }
            case 'F': {
                return 23;
            }
            case 'B': 
            case 'I': 
            case 'S': 
            case 'Z': {
                return 21;
            }
            case 'V': {
                throw new IllegalArgumentException("There's no load opcode for 'void'");
            }
            case 'L': 
            case '[': {
                return 25;
            }
        }
        throw new IllegalStateException("Uhoh - bug - unrecognized JVM type: " + spec);
    }

    private static int returnOpcodeFor(String returnSpec) {
        switch (returnSpec.charAt(0)) {
            case 'D': {
                return 175;
            }
            case 'J': {
                return 173;
            }
            case 'F': {
                return 174;
            }
            case 'B': 
            case 'I': 
            case 'S': 
            case 'Z': {
                return 172;
            }
            case 'V': {
                return 177;
            }
            case 'L': 
            case '[': {
                return 176;
            }
        }
        throw new IllegalStateException("Uhoh - bug - unrecognized JVM type: " + returnSpec);
    }

    private static int sizeOf(String spec) {
        switch (spec.charAt(0)) {
            case 'D': 
            case 'J': {
                return 2;
            }
            case 'V': {
                return 0;
            }
        }
        return 1;
    }
}
