package org.lwjgl.glfw;

import javax.annotation.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.Checks;
import org.lwjgl.system.FunctionProvider;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.SharedLibrary;

public class GLFWNativeEGL {
    protected GLFWNativeEGL() {
        throw new UnsupportedOperationException();
    }

    @NativeType(value="EGLDisplay")
    public static long glfwGetEGLDisplay() {
        long __functionAddress = Functions.GetEGLDisplay;
        return JNI.invokeP(__functionAddress);
    }

    @NativeType(value="EGLContext")
    public static long glfwGetEGLContext(@NativeType(value="GLFWwindow *") long window) {
        long __functionAddress = Functions.GetEGLContext;
        if (Checks.CHECKS) {
            Checks.check(window);
        }
        return JNI.invokePP(window, __functionAddress);
    }

    @NativeType(value="EGLSurface")
    public static long glfwGetEGLSurface(@NativeType(value="GLFWwindow *") long window) {
        long __functionAddress = Functions.GetEGLSurface;
        if (Checks.CHECKS) {
            Checks.check(window);
        }
        return JNI.invokePP(window, __functionAddress);
    }

    @NativeType(value="EGLConfig")
    public static long glfwGetEGLConfig(@NativeType(value="GLFWwindow *") long window) {
        long __functionAddress = Functions.GetEGLConfig;
        if (Checks.CHECKS) {
            Checks.check(window);
        }
        return JNI.invokePP(window, __functionAddress);
    }

    public static void setEGLPath(FunctionProvider sharedLibrary) {
        if (!(sharedLibrary instanceof SharedLibrary)) {
            APIUtil.apiLog("GLFW EGL path override not set: Function provider is not a shared library.");
            return;
        }
        String path = ((SharedLibrary)sharedLibrary).getPath();
        if (path == null) {
            APIUtil.apiLog("GLFW EGL path override not set: Could not resolve the shared library path.");
            return;
        }
        GLFWNativeEGL.setEGLPath(path);
    }

    public static void setEGLPath(@Nullable String path) {
        if (!GLFWNativeEGL.override("_glfw_egl_library", path)) {
            APIUtil.apiLog("GLFW EGL path override not set: Could not resolve override symbol.");
        }
    }

    public static void setGLESPath(FunctionProvider sharedLibrary) {
        if (!(sharedLibrary instanceof SharedLibrary)) {
            APIUtil.apiLog("GLFW OpenGL ES path override not set: Function provider is not a shared library.");
            return;
        }
        String path = ((SharedLibrary)sharedLibrary).getPath();
        if (path == null) {
            APIUtil.apiLog("GLFW OpenGL ES path override not set: Could not resolve the shared library path.");
            return;
        }
        GLFWNativeEGL.setGLESPath(path);
    }

    public static void setGLESPath(@Nullable String path) {
        if (!GLFWNativeEGL.override("_glfw_opengles_library", path)) {
            APIUtil.apiLog("GLFW OpenGL ES path override not set: Could not resolve override symbol.");
        }
    }

    private static boolean override(String symbol, @Nullable String path) {
        long override = GLFW.getLibrary().getFunctionAddress(symbol);
        if (override == 0L) {
            return false;
        }
        long a = MemoryUtil.memGetAddress(override);
        if (a != 0L) {
            MemoryUtil.nmemFree(a);
        }
        MemoryUtil.memPutAddress(override, path == null ? 0L : MemoryUtil.memAddress(MemoryUtil.memUTF8(path)));
        return true;
    }

    public static final class Functions {
        public static final long GetEGLDisplay = APIUtil.apiGetFunctionAddress(GLFW.getLibrary(), "glfwGetEGLDisplay");
        public static final long GetEGLContext = APIUtil.apiGetFunctionAddress(GLFW.getLibrary(), "glfwGetEGLContext");
        public static final long GetEGLSurface = APIUtil.apiGetFunctionAddress(GLFW.getLibrary(), "glfwGetEGLSurface");
        public static final long GetEGLConfig = APIUtil.apiGetFunctionAddress(GLFW.getLibrary(), "glfwGetEGLConfig");

        private Functions() {
        }
    }
}
