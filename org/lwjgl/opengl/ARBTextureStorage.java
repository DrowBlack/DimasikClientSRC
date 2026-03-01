package org.lwjgl.opengl;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL42C;
import org.lwjgl.system.NativeType;

public class ARBTextureStorage {
    public static final int GL_TEXTURE_IMMUTABLE_FORMAT = 37167;

    protected ARBTextureStorage() {
        throw new UnsupportedOperationException();
    }

    public static void glTexStorage1D(@NativeType(value="GLenum") int target, @NativeType(value="GLsizei") int levels, @NativeType(value="GLenum") int internalformat, @NativeType(value="GLsizei") int width) {
        GL42C.glTexStorage1D(target, levels, internalformat, width);
    }

    public static void glTexStorage2D(@NativeType(value="GLenum") int target, @NativeType(value="GLsizei") int levels, @NativeType(value="GLenum") int internalformat, @NativeType(value="GLsizei") int width, @NativeType(value="GLsizei") int height) {
        GL42C.glTexStorage2D(target, levels, internalformat, width, height);
    }

    public static void glTexStorage3D(@NativeType(value="GLenum") int target, @NativeType(value="GLsizei") int levels, @NativeType(value="GLenum") int internalformat, @NativeType(value="GLsizei") int width, @NativeType(value="GLsizei") int height, @NativeType(value="GLsizei") int depth) {
        GL42C.glTexStorage3D(target, levels, internalformat, width, height, depth);
    }

    public static native void glTextureStorage1DEXT(@NativeType(value="GLuint") int var0, @NativeType(value="GLenum") int var1, @NativeType(value="GLsizei") int var2, @NativeType(value="GLenum") int var3, @NativeType(value="GLsizei") int var4);

    public static native void glTextureStorage2DEXT(@NativeType(value="GLuint") int var0, @NativeType(value="GLenum") int var1, @NativeType(value="GLsizei") int var2, @NativeType(value="GLenum") int var3, @NativeType(value="GLsizei") int var4, @NativeType(value="GLsizei") int var5);

    public static native void glTextureStorage3DEXT(@NativeType(value="GLuint") int var0, @NativeType(value="GLenum") int var1, @NativeType(value="GLsizei") int var2, @NativeType(value="GLenum") int var3, @NativeType(value="GLsizei") int var4, @NativeType(value="GLsizei") int var5, @NativeType(value="GLsizei") int var6);

    static {
        GL.initialize();
    }
}
