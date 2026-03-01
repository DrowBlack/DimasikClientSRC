package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nullable;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL41C;
import org.lwjgl.system.NativeType;

public class ARBGetProgramBinary {
    public static final int GL_PROGRAM_BINARY_RETRIEVABLE_HINT = 33367;
    public static final int GL_PROGRAM_BINARY_LENGTH = 34625;
    public static final int GL_NUM_PROGRAM_BINARY_FORMATS = 34814;
    public static final int GL_PROGRAM_BINARY_FORMATS = 34815;

    protected ARBGetProgramBinary() {
        throw new UnsupportedOperationException();
    }

    public static void nglGetProgramBinary(int program, int bufSize, long length, long binaryFormat, long binary) {
        GL41C.nglGetProgramBinary(program, bufSize, length, binaryFormat, binary);
    }

    public static void glGetProgramBinary(@NativeType(value="GLuint") int program, @Nullable @NativeType(value="GLsizei *") IntBuffer length, @NativeType(value="GLenum *") IntBuffer binaryFormat, @NativeType(value="void *") ByteBuffer binary) {
        GL41C.glGetProgramBinary(program, length, binaryFormat, binary);
    }

    public static void nglProgramBinary(int program, int binaryFormat, long binary, int length) {
        GL41C.nglProgramBinary(program, binaryFormat, binary, length);
    }

    public static void glProgramBinary(@NativeType(value="GLuint") int program, @NativeType(value="GLenum") int binaryFormat, @NativeType(value="void const *") ByteBuffer binary) {
        GL41C.glProgramBinary(program, binaryFormat, binary);
    }

    public static void glProgramParameteri(@NativeType(value="GLuint") int program, @NativeType(value="GLenum") int pname, @NativeType(value="GLint") int value) {
        GL41C.glProgramParameteri(program, pname, value);
    }

    public static void glGetProgramBinary(@NativeType(value="GLuint") int program, @Nullable @NativeType(value="GLsizei *") int[] length, @NativeType(value="GLenum *") int[] binaryFormat, @NativeType(value="void *") ByteBuffer binary) {
        GL41C.glGetProgramBinary(program, length, binaryFormat, binary);
    }

    static {
        GL.initialize();
    }
}
