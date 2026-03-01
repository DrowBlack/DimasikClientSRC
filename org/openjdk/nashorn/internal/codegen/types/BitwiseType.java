package org.openjdk.nashorn.internal.codegen.types;

import org.openjdk.nashorn.internal.codegen.types.BytecodeBitwiseOps;
import org.openjdk.nashorn.internal.codegen.types.NumericType;

public abstract class BitwiseType
extends NumericType
implements BytecodeBitwiseOps {
    private static final long serialVersionUID = 1L;

    protected BitwiseType(String name, Class<?> clazz, int weight, int slots) {
        super(name, clazz, weight, slots);
    }
}
