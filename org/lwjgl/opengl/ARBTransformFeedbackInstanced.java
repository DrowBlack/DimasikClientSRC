package org.lwjgl.opengl;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL42C;
import org.lwjgl.system.NativeType;

public class ARBTransformFeedbackInstanced {
    protected ARBTransformFeedbackInstanced() {
        throw new UnsupportedOperationException();
    }

    public static void glDrawTransformFeedbackInstanced(@NativeType(value="GLenum") int mode, @NativeType(value="GLuint") int id, @NativeType(value="GLsizei") int primcount) {
        GL42C.glDrawTransformFeedbackInstanced(mode, id, primcount);
    }

    public static void glDrawTransformFeedbackStreamInstanced(@NativeType(value="GLenum") int mode, @NativeType(value="GLuint") int id, @NativeType(value="GLuint") int stream, @NativeType(value="GLsizei") int primcount) {
        GL42C.glDrawTransformFeedbackStreamInstanced(mode, id, stream, primcount);
    }

    static {
        GL.initialize();
    }
}
