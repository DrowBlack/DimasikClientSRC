package org.openjdk.nashorn.internal.runtime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.SecureClassLoader;
import java.util.Arrays;
import org.openjdk.nashorn.internal.runtime.Context;

abstract class NashornLoader
extends SecureClassLoader {
    protected static final String OBJECTS_PKG = "org.openjdk.nashorn.internal.objects";
    protected static final String RUNTIME_PKG = "org.openjdk.nashorn.internal.runtime";
    protected static final String RUNTIME_ARRAYS_PKG = "org.openjdk.nashorn.internal.runtime.arrays";
    protected static final String RUNTIME_LINKER_PKG = "org.openjdk.nashorn.internal.runtime.linker";
    protected static final String SCRIPTS_PKG = "org.openjdk.nashorn.internal.scripts";
    static final Module NASHORN_MODULE = Context.class.getModule();
    private static final Permission[] SCRIPT_PERMISSIONS;
    private static final String MODULE_MANIPULATOR_NAME = "org.openjdk.nashorn.internal.scripts.ModuleGraphManipulator";
    private static final byte[] MODULE_MANIPULATOR_BYTES;
    private Method addModuleExport;

    NashornLoader(ClassLoader parent) {
        super(parent);
    }

    void loadModuleManipulator() {
        Class<?> clazz = this.defineClass(MODULE_MANIPULATOR_NAME, MODULE_MANIPULATOR_BYTES, 0, MODULE_MANIPULATOR_BYTES.length);
        try {
            Class.forName(MODULE_MANIPULATOR_NAME, true, this);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        PrivilegedAction<Void> pa = () -> {
            try {
                this.addModuleExport = clazz.getDeclaredMethod("addExport", Module.class);
                this.addModuleExport.setAccessible(true);
            }
            catch (NoSuchMethodException | SecurityException ex) {
                throw new RuntimeException(ex);
            }
            return null;
        };
        AccessController.doPrivileged(pa);
    }

    final void addModuleExport(Module to) {
        try {
            this.addModuleExport.invoke(null, to);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    static boolean isInNamedModule() {
        return NASHORN_MODULE.isNamed();
    }

    protected static void checkPackageAccess(String name) {
        SecurityManager sm;
        int i = name.lastIndexOf(46);
        if (i != -1 && (sm = System.getSecurityManager()) != null) {
            String pkgName;
            switch (pkgName = name.substring(0, i)) {
                case "org.openjdk.nashorn.internal.runtime": 
                case "org.openjdk.nashorn.internal.runtime.arrays": 
                case "org.openjdk.nashorn.internal.runtime.linker": 
                case "org.openjdk.nashorn.internal.objects": 
                case "org.openjdk.nashorn.internal.scripts": {
                    break;
                }
                default: {
                    sm.checkPackageAccess(pkgName);
                }
            }
        }
    }

    @Override
    protected PermissionCollection getPermissions(CodeSource codesource) {
        Permissions permCollection = new Permissions();
        for (Permission perm : SCRIPT_PERMISSIONS) {
            permCollection.add(perm);
        }
        return permCollection;
    }

    static ClassLoader createClassLoader(String classPath, ClassLoader parent) {
        URL[] urls = NashornLoader.pathToURLs(classPath);
        return URLClassLoader.newInstance(urls, parent);
    }

    private static URL[] pathToURLs(String path) {
        String[] components = path.split(File.pathSeparator);
        return (URL[])Arrays.stream(components).map(File::new).map(NashornLoader::fileToURL).toArray(URL[]::new);
    }

    private static URL fileToURL(File file) {
        Object name;
        try {
            name = file.getCanonicalPath();
        }
        catch (IOException e) {
            name = file.getAbsolutePath();
        }
        name = ((String)name).replace(File.separatorChar, '/');
        if (!((String)name).startsWith("/")) {
            name = "/" + (String)name;
        }
        if (!file.isFile()) {
            name = (String)name + "/";
        }
        try {
            return new URL("file", "", (String)name);
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("file");
        }
    }

    private static byte[] readModuleManipulatorBytes() {
        PrivilegedAction<byte[]> pa = () -> {
            byte[] byArray;
            block8: {
                String res = "/" + MODULE_MANIPULATOR_NAME.replace('.', '/') + ".class";
                InputStream in = NashornLoader.class.getResourceAsStream(res);
                try {
                    byArray = in.readAllBytes();
                    if (in == null) break block8;
                }
                catch (Throwable throwable) {
                    try {
                        if (in != null) {
                            try {
                                in.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException exp) {
                        throw new UncheckedIOException(exp);
                    }
                }
                in.close();
            }
            return byArray;
        };
        return AccessController.doPrivileged(pa);
    }

    static {
        MODULE_MANIPULATOR_BYTES = NashornLoader.readModuleManipulatorBytes();
        SCRIPT_PERMISSIONS = new Permission[]{new RuntimePermission("accessClassInPackage.org.openjdk.nashorn.internal.runtime"), new RuntimePermission("accessClassInPackage.org.openjdk.nashorn.internal.runtime.linker"), new RuntimePermission("accessClassInPackage.org.openjdk.nashorn.internal.objects"), new RuntimePermission("accessClassInPackage.org.openjdk.nashorn.internal.scripts"), new RuntimePermission("accessClassInPackage.org.openjdk.nashorn.internal.runtime.arrays")};
    }
}
