package dimasik.helpers.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Project {
    private static final float[] identity = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    private static final FloatBuffer matrix = BufferUtils.createFloatBuffer(16);

    public static boolean gluProject(float objx, float objy, float objz, FloatBuffer modelMatrix, FloatBuffer projMatrix, IntBuffer viewport, FloatBuffer win_pos) {
        float[] in = new float[4];
        float[] out = new float[4];
        in[0] = objx;
        in[1] = objy;
        in[2] = objz;
        in[3] = 1.0f;
        Project.__gluMultMatrixVecf(modelMatrix, in, out);
        Project.__gluMultMatrixVecf(projMatrix, out, in);
        if ((double)in[3] == 0.0) {
            return false;
        }
        in[3] = 1.0f / in[3] * 0.5f;
        in[0] = in[0] * in[3] + 0.5f;
        in[1] = in[1] * in[3] + 0.5f;
        in[2] = in[2] * in[3] + 0.5f;
        win_pos.put(0, in[0] * (float)viewport.get(viewport.position() + 2) + (float)viewport.get(viewport.position()));
        win_pos.put(1, in[1] * (float)viewport.get(viewport.position() + 3) + (float)viewport.get(viewport.position() + 1));
        win_pos.put(2, in[2]);
        return true;
    }

    public static void gluPerspective(float fovy, float aspect, float zNear, float zFar) {
        float radians = fovy / 2.0f * (float)Math.PI / 180.0f;
        float deltaZ = zFar - zNear;
        float sine = (float)Math.sin(radians);
        if (deltaZ != 0.0f && sine != 0.0f && aspect != 0.0f) {
            float cotangent = (float)Math.cos(radians) / sine;
            Project.__gluMakeIdentityf(matrix);
            matrix.put(0, cotangent / aspect);
            matrix.put(5, cotangent);
            matrix.put(10, -(zFar + zNear) / deltaZ);
            matrix.put(11, -1.0f);
            matrix.put(14, -2.0f * zNear * zFar / deltaZ);
            matrix.put(15, 0.0f);
            GL11.glMultMatrixf(matrix);
        }
    }

    private static void __gluMakeIdentityf(FloatBuffer m) {
        int oldPos = m.position();
        m.put(identity);
        m.position(oldPos);
    }

    public static void __gluMultMatrixVecf(FloatBuffer m, float[] in, float[] out) {
        for (int i = 0; i < 4; ++i) {
            out[i] = in[0] * m.get(m.position() + i) + in[1] * m.get(m.position() + 4 + i) + in[2] * m.get(m.position() + 8 + i) + in[3] * m.get(m.position() + 12 + i);
        }
    }
}
