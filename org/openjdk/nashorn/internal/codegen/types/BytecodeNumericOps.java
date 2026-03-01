package org.openjdk.nashorn.internal.codegen.types;

import org.objectweb.asm.MethodVisitor;
import org.openjdk.nashorn.internal.codegen.types.Type;

interface BytecodeNumericOps {
    public Type neg(MethodVisitor var1, int var2);

    public Type sub(MethodVisitor var1, int var2);

    public Type mul(MethodVisitor var1, int var2);

    public Type div(MethodVisitor var1, int var2);

    public Type rem(MethodVisitor var1, int var2);

    public Type cmp(MethodVisitor var1, boolean var2);
}
