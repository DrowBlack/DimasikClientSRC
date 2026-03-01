package org.lwjgl.system;

public interface NativeResource
extends AutoCloseable {
    public void free();

    @Override
    default public void close() {
        this.free();
    }
}
