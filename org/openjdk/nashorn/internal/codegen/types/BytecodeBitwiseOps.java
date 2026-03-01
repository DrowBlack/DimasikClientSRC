package org.openjdk.nashorn.internal.codegen.types;

import org.objectweb.asm.MethodVisitor;
import org.openjdk.nashorn.internal.codegen.types.Type;

interface BytecodeBitwiseOps {
    public Type shr(MethodVisitor var1);

    public Type sar(MethodVisitor var1);

    public Type shl(MethodVisitor var1);

    public Type and(MethodVisitor var1);

    public Type or(MethodVisitor var1);

    public Type xor(MethodVisitor var1);

    public Type cmp(MethodVisitor var1);
}
