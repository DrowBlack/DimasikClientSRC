package org.lwjgl.opengl;

import java.lang.invoke.LambdaMetafactory;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLX11;
import org.lwjgl.opengl.GLXCapabilities;
import org.lwjgl.opengl.WGL;
import org.lwjgl.opengl.WGLCapabilities;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.Checks;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.FunctionProvider;
import org.lwjgl.system.JNI;
import org.lwjgl.system.Library;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.Platform;
import org.lwjgl.system.SharedLibrary;
import org.lwjgl.system.ThreadLocalUtil;
import org.lwjgl.system.linux.X11;
import org.lwjgl.system.windows.GDI32;
import org.lwjgl.system.windows.PIXELFORMATDESCRIPTOR;
import org.lwjgl.system.windows.User32;
import org.lwjgl.system.windows.WNDCLASSEX;
import org.lwjgl.system.windows.WindowsLibrary;
import org.lwjgl.system.windows.WindowsUtil;

public final class GL {
    @Nullable
    private static final APIUtil.APIVersion MAX_VERSION;
    @Nullable
    private static FunctionProvider functionProvider;
    private static final ThreadLocal<GLCapabilities> capabilitiesTLS;
    private static ICD icd;
    @Nullable
    private static WGLCapabilities capabilitiesWGL;
    @Nullable
    private static GLXCapabilities capabilitiesGLXClient;
    @Nullable
    private static GLXCapabilities capabilitiesGLX;

    private GL() {
    }

    static void initialize() {
    }

    public static void create() {
        SharedLibrary GL2;
        switch (Platform.get()) {
            case FREEBSD: 
            case LINUX: {
                GL2 = Library.loadNative(GL.class, "org.lwjgl.opengl", Configuration.OPENGL_LIBRARY_NAME, "libGLX.so.0", "libGL.so.1", "libGL.so");
                break;
            }
            case MACOSX: {
                GL2 = Library.loadNative(GL.class, "org.lwjgl.opengl", Configuration.OPENGL_LIBRARY_NAME, "/System/Library/Frameworks/OpenGL.framework/Versions/Current/OpenGL");
                break;
            }
            case WINDOWS: {
                GL2 = Library.loadNative(GL.class, "org.lwjgl.opengl", Configuration.OPENGL_LIBRARY_NAME, "opengl32");
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        GL.create(GL2);
    }

    public static void create(String libName) {
        GL.create(Library.loadNative(GL.class, "org.lwjgl.opengl", libName));
    }

    private static void create(SharedLibrary OPENGL) {
        try {
            GL.create((FunctionProvider)new SharedLibrary.Delegate(OPENGL){
                private final long GetProcAddress;
                {
                    long GetProcAddress = 0L;
                    switch (Platform.get()) {
                        case FREEBSD: 
                        case LINUX: {
                            GetProcAddress = this.library.getFunctionAddress("glXGetProcAddress");
                            if (GetProcAddress != 0L) break;
                            GetProcAddress = this.library.getFunctionAddress("glXGetProcAddressARB");
                            break;
                        }
                        case WINDOWS: {
                            GetProcAddress = this.library.getFunctionAddress("wglGetProcAddress");
                        }
                    }
                    if (GetProcAddress == 0L) {
                        GetProcAddress = this.library.getFunctionAddress("OSMesaGetProcAddress");
                    }
                    this.GetProcAddress = GetProcAddress;
                }

                @Override
                public long getFunctionAddress(ByteBuffer functionName) {
                    long address;
                    long l = this.GetProcAddress == 0L ? 0L : (address = Platform.get() == Platform.WINDOWS ? WGL.nwglGetProcAddress(MemoryUtil.memAddress(functionName), this.GetProcAddress) : JNI.callPP(MemoryUtil.memAddress(functionName), this.GetProcAddress));
                    if (address == 0L && (address = this.library.getFunctionAddress(functionName)) == 0L && Checks.DEBUG_FUNCTIONS) {
                        APIUtil.apiLogMissing("GL", functionName);
                    }
                    return address;
                }
            });
        }
        catch (RuntimeException e) {
            OPENGL.free();
            throw e;
        }
    }

    public static void create(FunctionProvider functionProvider) {
        if (GL.functionProvider != null) {
            throw new IllegalStateException("OpenGL library has already been loaded.");
        }
        GL.functionProvider = functionProvider;
        ThreadLocalUtil.setFunctionMissingAddresses(2228);
    }

    public static void destroy() {
        if (functionProvider == null) {
            return;
        }
        ThreadLocalUtil.setFunctionMissingAddresses(0);
        capabilitiesWGL = null;
        capabilitiesGLX = null;
        if (functionProvider instanceof NativeResource) {
            ((NativeResource)((Object)functionProvider)).free();
        }
        functionProvider = null;
    }

    @Nullable
    public static FunctionProvider getFunctionProvider() {
        return functionProvider;
    }

    public static void setCapabilities(@Nullable GLCapabilities caps) {
        capabilitiesTLS.set(caps);
        ThreadLocalUtil.setCapabilities(caps == null ? 0L : MemoryUtil.memAddress(caps.addresses));
        icd.set(caps);
    }

    public static GLCapabilities getCapabilities() {
        return GL.checkCapabilities(capabilitiesTLS.get());
    }

    private static GLCapabilities checkCapabilities(@Nullable GLCapabilities caps) {
        if (Checks.CHECKS && caps == null) {
            throw new IllegalStateException("No GLCapabilities instance set for the current thread. Possible solutions:\n\ta) Call GL.createCapabilities() after making a context current in the current thread.\n\tb) Call GL.setCapabilities() if a GLCapabilities instance already exists for the current context.");
        }
        return caps;
    }

    public static WGLCapabilities getCapabilitiesWGL() {
        if (capabilitiesWGL == null) {
            capabilitiesWGL = GL.createCapabilitiesWGLDummy();
        }
        return capabilitiesWGL;
    }

    static GLXCapabilities getCapabilitiesGLXClient() {
        if (capabilitiesGLXClient == null) {
            capabilitiesGLXClient = GL.initCapabilitiesGLX(true);
        }
        return capabilitiesGLXClient;
    }

    public static GLXCapabilities getCapabilitiesGLX() {
        if (capabilitiesGLX == null) {
            capabilitiesGLX = GL.initCapabilitiesGLX(false);
        }
        return capabilitiesGLX;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static GLXCapabilities initCapabilitiesGLX(boolean client) {
        long display = X11.nXOpenDisplay(0L);
        try {
            GLXCapabilities gLXCapabilities = GL.createCapabilitiesGLX(display, client ? -1 : X11.XDefaultScreen(display));
            return gLXCapabilities;
        }
        finally {
            X11.XCloseDisplay(display);
        }
    }

    public static GLCapabilities createCapabilities() {
        return GL.createCapabilities(null);
    }

    public static GLCapabilities createCapabilities(@Nullable IntFunction<PointerBuffer> bufferFactory) {
        return GL.createCapabilities(false, bufferFactory);
    }

    public static GLCapabilities createCapabilities(boolean forwardCompatible) {
        return GL.createCapabilities(forwardCompatible, null);
    }

    /*
     * Unable to fully structure code
     */
    public static GLCapabilities createCapabilities(boolean forwardCompatible, @Nullable IntFunction<PointerBuffer> bufferFactory) {
        functionProvider = GL.functionProvider;
        if (functionProvider == null) {
            throw new IllegalStateException("OpenGL library has not been loaded.");
        }
        GetError = functionProvider.getFunctionAddress("glGetError");
        GetString = functionProvider.getFunctionAddress("glGetString");
        GetIntegerv = functionProvider.getFunctionAddress("glGetIntegerv");
        if (GetError == 0L || GetString == 0L || GetIntegerv == 0L) {
            throw new IllegalStateException("Core OpenGL functions could not be found. Make sure that the OpenGL library has been loaded correctly.");
        }
        errorCode = JNI.callI(GetError);
        if (errorCode != 0) {
            APIUtil.apiLog(String.format("An OpenGL context was in an error state before the creation of its capabilities instance. Error: 0x%X", new Object[]{errorCode}));
        }
        stack = MemoryStack.stackPush();
        var13_8 = null;
        try {
            version = stack.ints(0);
            JNI.callPV(33307, MemoryUtil.memAddress(version), GetIntegerv);
            if (JNI.callI(GetError) == 0 && 3 <= (majorVersion = version.get(0))) {
                JNI.callPV(33308, MemoryUtil.memAddress(version), GetIntegerv);
                minorVersion = version.get(0);
            } else {
                versionString = MemoryUtil.memUTF8Safe(JNI.callP(7938, GetString));
                if (versionString == null || JNI.callI(GetError) != 0) {
                    throw new IllegalStateException("There is no OpenGL context current in the current thread.");
                }
                apiVersion = APIUtil.apiParseVersion(versionString);
                majorVersion = apiVersion.major;
                minorVersion = apiVersion.minor;
            }
        }
        catch (Throwable version) {
            var13_8 = version;
            throw version;
        }
        finally {
            if (stack != null) {
                if (var13_8 != null) {
                    try {
                        stack.close();
                    }
                    catch (Throwable version) {
                        var13_8.addSuppressed(version);
                    }
                } else {
                    stack.close();
                }
            }
        }
        if (majorVersion < 1 || majorVersion == 1 && minorVersion < 1) {
            throw new IllegalStateException("OpenGL 1.1 is required.");
        }
        GL_VERSIONS = new int[]{5, 1, 3, 6};
        supportedExtensions = new HashSet<String>(512);
        maxMajor = Math.min(majorVersion, GL_VERSIONS.length);
        if (GL.MAX_VERSION != null) {
            maxMajor = Math.min(GL.MAX_VERSION.major, maxMajor);
        }
        for (M = 1; M <= maxMajor; ++M) {
            maxMinor = GL_VERSIONS[M - 1];
            if (M == majorVersion) {
                maxMinor = Math.min(minorVersion, maxMinor);
            }
            if (GL.MAX_VERSION != null && M == GL.MAX_VERSION.major) {
                maxMinor = Math.min(GL.MAX_VERSION.minor, maxMinor);
            }
            v0 = m = M == 1 ? 1 : 0;
            while (m <= maxMinor) {
                supportedExtensions.add("OpenGL" + M + m);
                ++m;
            }
        }
        if (majorVersion < 3) {
            extensionsString = MemoryUtil.memASCIISafe(JNI.callP(7939, GetString));
            if (extensionsString != null) {
                tokenizer = new StringTokenizer(extensionsString);
                while (tokenizer.hasMoreTokens()) {
                    supportedExtensions.add(tokenizer.nextToken());
                }
            }
        } else {
            stack = MemoryStack.stackPush();
            var16_18 = null;
            try {
                pi = stack.ints(0);
                JNI.callPV(33309, MemoryUtil.memAddress(pi), GetIntegerv);
                extensionCount = pi.get(0);
                GetStringi = APIUtil.apiGetFunctionAddress(functionProvider, "glGetStringi");
                for (i = 0; i < extensionCount; ++i) {
                    supportedExtensions.add(MemoryUtil.memASCII(JNI.callP(7939, i, GetStringi)));
                }
                JNI.callPV(33310, MemoryUtil.memAddress(pi), GetIntegerv);
                if ((pi.get(0) & 1) != 0) {
                    forwardCompatible = true;
                }
                if (3 >= majorVersion && 1 > minorVersion) ** GOTO lbl110
                if (3 < majorVersion || 2 <= minorVersion) {
                    JNI.callPV(37158, MemoryUtil.memAddress(pi), GetIntegerv);
                    if ((pi.get(0) & 1) == 0) ** GOTO lbl110
                    forwardCompatible = true;
                }
                forwardCompatible = supportedExtensions.contains("GL_ARB_compatibility") == false;
            }
            catch (Throwable var17_24) {
                var16_18 = var17_24;
                throw var17_24;
            }
            finally {
                if (stack != null) {
                    if (var16_18 != null) {
                        try {
                            stack.close();
                        }
                        catch (Throwable var17_23) {
                            var16_18.addSuppressed(var17_23);
                        }
                    } else {
                        stack.close();
                    }
                }
            }
        }
lbl110:
        // 6 sources

        APIUtil.apiFilterExtensions(supportedExtensions, Configuration.OPENGL_EXTENSION_FILTER);
        caps = new GLCapabilities(functionProvider, supportedExtensions, forwardCompatible, bufferFactory == null ? (IntFunction<PointerBuffer>)LambdaMetafactory.metafactory(null, null, null, (I)Ljava/lang/Object;, createPointerBuffer(int ), (I)Lorg/lwjgl/PointerBuffer;)() : bufferFactory);
        GL.setCapabilities(caps);
        return caps;
    }

    /*
     * Loose catch block
     */
    private static WGLCapabilities createCapabilitiesWGLDummy() {
        long hdc = WGL.wglGetCurrentDC();
        if (hdc != 0L) {
            return GL.createCapabilitiesWGL(hdc);
        }
        int classAtom = 0;
        long hwnd = 0L;
        long hglrc = 0L;
        try {
            try (MemoryStack stack = MemoryStack.stackPush();){
                PIXELFORMATDESCRIPTOR pfd;
                WNDCLASSEX wc = WNDCLASSEX.calloc(stack).cbSize(WNDCLASSEX.SIZEOF).style(3).hInstance(WindowsLibrary.HINSTANCE).lpszClassName(stack.UTF16("WGL"));
                MemoryUtil.memPutAddress(wc.address() + (long)WNDCLASSEX.LPFNWNDPROC, User32.Functions.DefWindowProc);
                classAtom = User32.RegisterClassEx(wc);
                if (classAtom == 0) {
                    throw new IllegalStateException("Failed to register WGL window class");
                }
                hwnd = Checks.check(User32.nCreateWindowEx(0, classAtom & 0xFFFF, 0L, 114229248, 0, 0, 1, 1, 0L, 0L, 0L, 0L));
                hdc = Checks.check(User32.GetDC(hwnd));
                int pixelFormat = GDI32.ChoosePixelFormat(hdc, pfd = PIXELFORMATDESCRIPTOR.calloc(stack).nSize((short)PIXELFORMATDESCRIPTOR.SIZEOF).nVersion((short)1).dwFlags(32));
                if (pixelFormat == 0) {
                    WindowsUtil.windowsThrowException("Failed to choose an OpenGL-compatible pixel format");
                }
                if (GDI32.DescribePixelFormat(hdc, pixelFormat, pfd) == 0) {
                    WindowsUtil.windowsThrowException("Failed to obtain pixel format information");
                }
                if (!GDI32.SetPixelFormat(hdc, pixelFormat, pfd)) {
                    WindowsUtil.windowsThrowException("Failed to set the pixel format");
                }
                hglrc = Checks.check(WGL.wglCreateContext(hdc));
                WGL.wglMakeCurrent(hdc, hglrc);
                WGLCapabilities wGLCapabilities = GL.createCapabilitiesWGL(hdc);
                return wGLCapabilities;
            }
            {
                catch (Throwable throwable) {
                    throw throwable;
                }
            }
        }
        finally {
            if (hglrc != 0L) {
                WGL.wglMakeCurrent(0L, 0L);
                WGL.wglDeleteContext(hglrc);
            }
            if (hwnd != 0L) {
                User32.DestroyWindow(hwnd);
            }
            if (classAtom != 0) {
                User32.nUnregisterClass(classAtom & 0xFFFF, WindowsLibrary.HINSTANCE);
            }
        }
    }

    public static WGLCapabilities createCapabilitiesWGL() {
        long hdc = WGL.wglGetCurrentDC();
        if (hdc == 0L) {
            throw new IllegalStateException("Failed to retrieve the device context of the current OpenGL context");
        }
        return GL.createCapabilitiesWGL(hdc);
    }

    private static WGLCapabilities createCapabilitiesWGL(long hdc) {
        FunctionProvider functionProvider = GL.functionProvider;
        if (functionProvider == null) {
            throw new IllegalStateException("OpenGL library has not been loaded.");
        }
        String extensionsString = null;
        long wglGetExtensionsString = functionProvider.getFunctionAddress("wglGetExtensionsStringARB");
        if (wglGetExtensionsString != 0L) {
            extensionsString = MemoryUtil.memASCII(JNI.callPP(hdc, wglGetExtensionsString));
        } else {
            wglGetExtensionsString = functionProvider.getFunctionAddress("wglGetExtensionsStringEXT");
            if (wglGetExtensionsString != 0L) {
                extensionsString = MemoryUtil.memASCII(JNI.callP(wglGetExtensionsString));
            }
        }
        HashSet<String> supportedExtensions = new HashSet<String>(32);
        if (extensionsString != null) {
            StringTokenizer tokenizer = new StringTokenizer(extensionsString);
            while (tokenizer.hasMoreTokens()) {
                supportedExtensions.add(tokenizer.nextToken());
            }
        }
        APIUtil.apiFilterExtensions(supportedExtensions, Configuration.OPENGL_EXTENSION_FILTER);
        return new WGLCapabilities(functionProvider, supportedExtensions);
    }

    public static GLXCapabilities createCapabilitiesGLX(long display) {
        return GL.createCapabilitiesGLX(display, X11.XDefaultScreen(display));
    }

    public static GLXCapabilities createCapabilitiesGLX(long display, int screen) {
        int minorVersion;
        int majorVersion;
        FunctionProvider functionProvider = GL.functionProvider;
        if (functionProvider == null) {
            throw new IllegalStateException("OpenGL library has not been loaded.");
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            IntBuffer piMajor = stack.ints(0);
            IntBuffer piMinor = stack.ints(0);
            if (!GLX11.glXQueryVersion(display, piMajor, piMinor)) {
                throw new IllegalStateException("Failed to query GLX version");
            }
            majorVersion = piMajor.get(0);
            minorVersion = piMinor.get(0);
            if (majorVersion != 1) {
                throw new IllegalStateException("Invalid GLX major version: " + majorVersion);
            }
        }
        HashSet<String> supportedExtensions = new HashSet<String>(32);
        int[][] GLX_VERSIONS = new int[][]{{1, 2, 3, 4}};
        for (int major = 1; major <= GLX_VERSIONS.length; ++major) {
            int[] minors;
            for (int minor : minors = GLX_VERSIONS[major - 1]) {
                if (major >= majorVersion && (major != majorVersion || minor > minorVersion)) continue;
                supportedExtensions.add("GLX" + major + minor);
            }
        }
        if (1 <= minorVersion) {
            String extensionsString;
            if (screen == -1) {
                long glXGetClientString = functionProvider.getFunctionAddress("glXGetClientString");
                extensionsString = MemoryUtil.memASCIISafe(JNI.callPP(display, 3, glXGetClientString));
            } else {
                long glXQueryExtensionsString = functionProvider.getFunctionAddress("glXQueryExtensionsString");
                extensionsString = MemoryUtil.memASCIISafe(JNI.callPP(display, screen, glXQueryExtensionsString));
            }
            if (extensionsString != null) {
                StringTokenizer tokenizer = new StringTokenizer(extensionsString);
                while (tokenizer.hasMoreTokens()) {
                    supportedExtensions.add(tokenizer.nextToken());
                }
            }
        }
        APIUtil.apiFilterExtensions(supportedExtensions, Configuration.OPENGL_EXTENSION_FILTER);
        return new GLXCapabilities(functionProvider, supportedExtensions);
    }

    static GLCapabilities getICD() {
        return GL.checkCapabilities(icd.get());
    }

    static {
        capabilitiesTLS = new ThreadLocal();
        icd = new ICDStatic();
        Library.loadSystem(System::load, System::loadLibrary, GL.class, "org.lwjgl.opengl", Platform.mapLibraryNameBundled("lwjgl_opengl"));
        MAX_VERSION = APIUtil.apiParseVersion(Configuration.OPENGL_MAXVERSION);
        if (!Configuration.OPENGL_EXPLICIT_INIT.get(false).booleanValue()) {
            GL.create();
        }
    }

    private static class ICDStatic
    implements ICD {
        @Nullable
        private static GLCapabilities tempCaps;

        private ICDStatic() {
        }

        @Override
        public void set(@Nullable GLCapabilities caps) {
            if (tempCaps == null) {
                tempCaps = caps;
            } else if (caps != null && caps != tempCaps && ThreadLocalUtil.areCapabilitiesDifferent(ICDStatic.tempCaps.addresses, caps.addresses)) {
                APIUtil.apiLog("[WARNING] Incompatible context detected. Falling back to thread-local lookup for GL contexts.");
                icd = GL::getCapabilities;
            }
        }

        @Override
        public GLCapabilities get() {
            return WriteOnce.caps;
        }

        private static final class WriteOnce {
            static final GLCapabilities caps;

            private WriteOnce() {
            }

            static {
                GLCapabilities tempCaps = tempCaps;
                if (tempCaps == null) {
                    throw new IllegalStateException("No GLCapabilities instance has been set");
                }
                caps = tempCaps;
            }
        }
    }

    private static interface ICD {
        default public void set(@Nullable GLCapabilities caps) {
        }

        @Nullable
        public GLCapabilities get();
    }
}
