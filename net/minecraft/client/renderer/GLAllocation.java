package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GLAllocation {
    public static synchronized ByteBuffer createDirectByteBuffer(int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }

    public static FloatBuffer createDirectFloatBuffer(int capacity) {
        return GLAllocation.createDirectByteBuffer(capacity << 2).asFloatBuffer();
    }

    public static IntBuffer createDirectIntBuffer(int capacity) {
        return GLAllocation.createDirectByteBuffer(capacity << 2).asIntBuffer();
    }
}
