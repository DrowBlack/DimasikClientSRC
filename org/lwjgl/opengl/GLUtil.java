package org.lwjgl.opengl;

import java.io.PrintStream;
import javax.annotation.Nullable;
import org.lwjgl.opengl.AMDDebugOutput;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL43C;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageAMDCallback;
import org.lwjgl.opengl.GLDebugMessageARBCallback;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.KHRDebug;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.Callback;

public final class GLUtil {
    private GLUtil() {
    }

    @Nullable
    public static Callback setupDebugMessageCallback() {
        return GLUtil.setupDebugMessageCallback(APIUtil.DEBUG_STREAM);
    }

    @Nullable
    public static Callback setupDebugMessageCallback(PrintStream stream) {
        GLCapabilities caps = GL.getCapabilities();
        if (caps.OpenGL43) {
            APIUtil.apiLog("[GL] Using OpenGL 4.3 for error logging.");
            GLDebugMessageCallback proc = GLDebugMessageCallback.create((source, type, id, severity, length, message, userParam) -> {
                StringBuilder sb = new StringBuilder(300);
                sb.append("[LWJGL] OpenGL debug message\n");
                GLUtil.printDetail(sb, "ID", "0x" + Integer.toHexString(id).toUpperCase());
                GLUtil.printDetail(sb, "Source", GLUtil.getDebugSource(source));
                GLUtil.printDetail(sb, "Type", GLUtil.getDebugType(type));
                GLUtil.printDetail(sb, "Severity", GLUtil.getDebugSeverity(severity));
                GLUtil.printDetail(sb, "Message", GLDebugMessageCallback.getMessage(length, message));
                stream.print(sb);
            });
            GL43C.glDebugMessageCallback(proc, 0L);
            if ((GL43C.glGetInteger(33310) & 2) == 0) {
                APIUtil.apiLog("[GL] Warning: A non-debug context may not produce any debug output.");
                GL43C.glEnable(37600);
            }
            return proc;
        }
        if (caps.GL_KHR_debug) {
            APIUtil.apiLog("[GL] Using KHR_debug for error logging.");
            GLDebugMessageCallback proc = GLDebugMessageCallback.create((source, type, id, severity, length, message, userParam) -> {
                StringBuilder sb = new StringBuilder(300);
                sb.append("[LWJGL] OpenGL debug message\n");
                GLUtil.printDetail(sb, "ID", "0x" + Integer.toHexString(id).toUpperCase());
                GLUtil.printDetail(sb, "Source", GLUtil.getDebugSource(source));
                GLUtil.printDetail(sb, "Type", GLUtil.getDebugType(type));
                GLUtil.printDetail(sb, "Severity", GLUtil.getDebugSeverity(severity));
                GLUtil.printDetail(sb, "Message", GLDebugMessageCallback.getMessage(length, message));
                stream.print(sb);
            });
            KHRDebug.glDebugMessageCallback(proc, 0L);
            if (caps.OpenGL30 && (GL43C.glGetInteger(33310) & 2) == 0) {
                APIUtil.apiLog("[GL] Warning: A non-debug context may not produce any debug output.");
                GL43C.glEnable(37600);
            }
            return proc;
        }
        if (caps.GL_ARB_debug_output) {
            APIUtil.apiLog("[GL] Using ARB_debug_output for error logging.");
            GLDebugMessageARBCallback proc = GLDebugMessageARBCallback.create((source, type, id, severity, length, message, userParam) -> {
                StringBuilder sb = new StringBuilder(300);
                sb.append("[LWJGL] ARB_debug_output message\n");
                GLUtil.printDetail(sb, "ID", "0x" + Integer.toHexString(id).toUpperCase());
                GLUtil.printDetail(sb, "Source", GLUtil.getSourceARB(source));
                GLUtil.printDetail(sb, "Type", GLUtil.getTypeARB(type));
                GLUtil.printDetail(sb, "Severity", GLUtil.getSeverityARB(severity));
                GLUtil.printDetail(sb, "Message", GLDebugMessageARBCallback.getMessage(length, message));
                stream.print(sb);
            });
            ARBDebugOutput.glDebugMessageCallbackARB(proc, 0L);
            return proc;
        }
        if (caps.GL_AMD_debug_output) {
            APIUtil.apiLog("[GL] Using AMD_debug_output for error logging.");
            GLDebugMessageAMDCallback proc = GLDebugMessageAMDCallback.create((id, category, severity, length, message, userParam) -> {
                StringBuilder sb = new StringBuilder(300);
                sb.append("[LWJGL] AMD_debug_output message\n");
                GLUtil.printDetail(sb, "ID", "0x" + Integer.toHexString(id).toUpperCase());
                GLUtil.printDetail(sb, "Category", GLUtil.getCategoryAMD(category));
                GLUtil.printDetail(sb, "Severity", GLUtil.getSeverityAMD(severity));
                GLUtil.printDetail(sb, "Message", GLDebugMessageAMDCallback.getMessage(length, message));
                stream.print(sb);
            });
            AMDDebugOutput.glDebugMessageCallbackAMD(proc, 0L);
            return proc;
        }
        APIUtil.apiLog("[GL] No debug output implementation is available.");
        return null;
    }

    private static void printDetail(StringBuilder sb, String type, String message) {
        sb.append("\t").append(type).append(": ").append(message).append("\n");
    }

    private static String getDebugSource(int source) {
        switch (source) {
            case 33350: {
                return "API";
            }
            case 33351: {
                return "WINDOW SYSTEM";
            }
            case 33352: {
                return "SHADER COMPILER";
            }
            case 33353: {
                return "THIRD PARTY";
            }
            case 33354: {
                return "APPLICATION";
            }
            case 33355: {
                return "OTHER";
            }
        }
        return APIUtil.apiUnknownToken(source);
    }

    private static String getDebugType(int type) {
        switch (type) {
            case 33356: {
                return "ERROR";
            }
            case 33357: {
                return "DEPRECATED BEHAVIOR";
            }
            case 33358: {
                return "UNDEFINED BEHAVIOR";
            }
            case 33359: {
                return "PORTABILITY";
            }
            case 33360: {
                return "PERFORMANCE";
            }
            case 33361: {
                return "OTHER";
            }
            case 33384: {
                return "MARKER";
            }
        }
        return APIUtil.apiUnknownToken(type);
    }

    private static String getDebugSeverity(int severity) {
        switch (severity) {
            case 37190: {
                return "HIGH";
            }
            case 37191: {
                return "MEDIUM";
            }
            case 37192: {
                return "LOW";
            }
            case 33387: {
                return "NOTIFICATION";
            }
        }
        return APIUtil.apiUnknownToken(severity);
    }

    private static String getSourceARB(int source) {
        switch (source) {
            case 33350: {
                return "API";
            }
            case 33351: {
                return "WINDOW SYSTEM";
            }
            case 33352: {
                return "SHADER COMPILER";
            }
            case 33353: {
                return "THIRD PARTY";
            }
            case 33354: {
                return "APPLICATION";
            }
            case 33355: {
                return "OTHER";
            }
        }
        return APIUtil.apiUnknownToken(source);
    }

    private static String getTypeARB(int type) {
        switch (type) {
            case 33356: {
                return "ERROR";
            }
            case 33357: {
                return "DEPRECATED BEHAVIOR";
            }
            case 33358: {
                return "UNDEFINED BEHAVIOR";
            }
            case 33359: {
                return "PORTABILITY";
            }
            case 33360: {
                return "PERFORMANCE";
            }
            case 33361: {
                return "OTHER";
            }
        }
        return APIUtil.apiUnknownToken(type);
    }

    private static String getSeverityARB(int severity) {
        switch (severity) {
            case 37190: {
                return "HIGH";
            }
            case 37191: {
                return "MEDIUM";
            }
            case 37192: {
                return "LOW";
            }
        }
        return APIUtil.apiUnknownToken(severity);
    }

    private static String getCategoryAMD(int category) {
        switch (category) {
            case 37193: {
                return "API ERROR";
            }
            case 37194: {
                return "WINDOW SYSTEM";
            }
            case 37195: {
                return "DEPRECATION";
            }
            case 37196: {
                return "UNDEFINED BEHAVIOR";
            }
            case 37197: {
                return "PERFORMANCE";
            }
            case 37198: {
                return "SHADER COMPILER";
            }
            case 37199: {
                return "APPLICATION";
            }
            case 37200: {
                return "OTHER";
            }
        }
        return APIUtil.apiUnknownToken(category);
    }

    private static String getSeverityAMD(int severity) {
        switch (severity) {
            case 37190: {
                return "HIGH";
            }
            case 37191: {
                return "MEDIUM";
            }
            case 37192: {
                return "LOW";
            }
        }
        return APIUtil.apiUnknownToken(severity);
    }
}
