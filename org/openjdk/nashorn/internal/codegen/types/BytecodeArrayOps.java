package org.openjdk.nashorn.internal.codegen.types;

import org.objectweb.asm.MethodVisitor;
import org.openjdk.nashorn.internal.codegen.types.Type;

interface BytecodeArrayOps {
    public Type aload(MethodVisitor var1);

    public void astore(MethodVisitor var1);

    public Type arraylength(MethodVisitor var1);

    public Type newarray(MethodVisitor var1);

    public Type newarray(MethodVisitor var1, int var2);
}
