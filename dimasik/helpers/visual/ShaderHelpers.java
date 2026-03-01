package dimasik.helpers.visual;

import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.helpers.interfaces.IFastAccess;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL20;

public class ShaderHelpers
implements IFastAccess {
    int shaderProgram;

    public String readInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String line;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public ShaderHelpers(String fragmentSource, String vertexSource, boolean isFile) {
        int shaderProgram = GL20.glCreateProgram();
        int fragmentShader = GL20.glCreateShader(35632);
        try {
            GL20.glShaderSource(fragmentShader, (CharSequence)(isFile ? this.readInputStream(Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(fragmentSource)).getInputStream()) : fragmentSource));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        GL20.glAttachShader(shaderProgram, fragmentShader);
        GL20.glCompileShader(fragmentShader);
        ShaderHelpers.checkCompileErrors(fragmentShader, "FRAGMENT");
        int vertexShader = GL20.glCreateShader(35633);
        try {
            GL20.glShaderSource(vertexShader, (CharSequence)(isFile ? this.readInputStream(Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(vertexSource)).getInputStream()) : vertexSource));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        GL20.glCompileShader(vertexShader);
        ShaderHelpers.checkCompileErrors(vertexShader, "VERTEX");
        GL20.glAttachShader(shaderProgram, vertexShader);
        GL20.glDeleteShader(fragmentShader);
        GL20.glDeleteShader(vertexShader);
        GL20.glLinkProgram(shaderProgram);
        this.shaderProgram = shaderProgram;
    }

    public void useProgram() {
        GL20.glUseProgram(this.shaderProgram);
    }

    public void unloadProgram() {
        GL20.glUseProgram(0);
    }

    public void setupUniformf(String name, float ... args) {
        int loc = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram, name);
        switch (args.length) {
            case 1: {
                GL20.glUniform1f(loc, args[0]);
                break;
            }
            case 2: {
                GL20.glUniform2f(loc, args[0], args[1]);
                break;
            }
            case 3: {
                GL20.glUniform3f(loc, args[0], args[1], args[2]);
                break;
            }
            case 4: {
                GL20.glUniform4f(loc, args[0], args[1], args[2], args[3]);
                break;
            }
            default: {
                throw new IllegalArgumentException("\u041d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u043e\u0435 \u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442\u043e\u0432 \u0434\u043b\u044f uniform '" + name + "'");
            }
        }
    }

    public void setupUniformi(String name, int ... args) {
        int loc = GL20.glGetUniformLocation(this.shaderProgram, name);
        if (args.length > 1) {
            GL20.glUniform2i(loc, args[0], args[1]);
        } else {
            GL20.glUniform1i(loc, args[0]);
        }
    }

    public void setupUniform1f(String uniform, float x) {
        int vertexColorLocation = GL20.glGetUniformLocation(this.shaderProgram, uniform);
        GL20.glUniform1f(vertexColorLocation, x);
    }

    public void setupUniform2f(String uniform, float x, float y) {
        int vertexColorLocation = GL20.glGetUniformLocation(this.shaderProgram, uniform);
        GL20.glUniform2f(vertexColorLocation, x, y);
    }

    public void setupUniform3f(String uniform, float x, float y, float z) {
        int vertexColorLocation = GL20.glGetUniformLocation(this.shaderProgram, uniform);
        GL20.glUniform3f(vertexColorLocation, x, y, z);
    }

    public void setupUniform4f(String uniform, float x, float y, float z, float w) {
        int vertexColorLocation = GL20.glGetUniformLocation(this.shaderProgram, uniform);
        GL20.glUniform4f(vertexColorLocation, x, y, z, w);
    }

    public void setupUniformFM(String uniform, float[] fm) {
        int vertexColorLocation = GL20.glGetUniformLocation(this.shaderProgram, uniform);
        GL20.glUniform4f(vertexColorLocation, fm[0], fm[1], fm[2], fm[3]);
    }

    public void setupUniform1i(String uniform, int x) {
        int vertexColorLocation = GL20.glGetUniformLocation(this.shaderProgram, uniform);
        GL20.glUniform1i(vertexColorLocation, x);
    }

    public void setupUniform2i(String uniform, int x, int y) {
        int vertexColorLocation = GL20.glGetUniformLocation(this.shaderProgram, uniform);
        GL20.glUniform2i(vertexColorLocation, x, y);
    }

    public void setupUniform3i(String uniform, int x, int y, int z) {
        int vertexColorLocation = GL20.glGetUniformLocation(this.shaderProgram, uniform);
        GL20.glUniform3i(vertexColorLocation, x, y, z);
    }

    public void setupUniform4i(String uniform, int x, int y, int z, int w) {
        int vertexColorLocation = GL20.glGetUniformLocation(this.shaderProgram, uniform);
        GL20.glUniform4i(vertexColorLocation, x, y, z, w);
    }

    public void setupUniformBF(String uniform, FloatBuffer floatBuffer) {
        int vertexColorLocation = GL20.glGetUniformLocation(this.shaderProgram, uniform);
        RenderSystem.glUniform1(vertexColorLocation, floatBuffer);
    }

    public static void drawQuads() {
        MainWindow mainWindow = Minecraft.getInstance().getMainWindow();
        float width = mainWindow.getScaledWidth();
        float height = mainWindow.getScaledHeight();
        GL20.glBegin(7);
        GL20.glTexCoord2f(0.0f, 1.0f);
        GL20.glVertex2f(0.0f, 0.0f);
        GL20.glTexCoord2f(0.0f, 0.0f);
        GL20.glVertex2f(0.0f, height);
        GL20.glTexCoord2f(1.0f, 0.0f);
        GL20.glVertex2f(width, height);
        GL20.glTexCoord2f(1.0f, 1.0f);
        GL20.glVertex2f(width, 0.0f);
        GL20.glEnd();
    }

    public int getUniform(String name) {
        return GL20.glGetUniformLocation(this.shaderProgram, name);
    }

    private static void checkCompileErrors(int shader, String type) {
        int success;
        if ((type.equals("VERTEX") || type.equals("FRAGMENT")) && (success = GL20.glGetShaderi(shader, 35713)) == 0) {
            String infoLog = GL20.glGetShaderInfoLog(shader);
            System.out.println(type + " SHADER COMPILATION ERROR: " + infoLog);
        }
    }

    private static void checkLinkErrors(int program) {
        int success = GL20.glGetProgrami(program, 35714);
        if (success == 0) {
            String infoLog = GL20.glGetProgramInfoLog(program);
            System.out.println("SHADER PROGRAM LINKING ERROR: " + infoLog);
        }
    }
}
