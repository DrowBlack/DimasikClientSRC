package cpw.mods.modlauncher;

import cpw.mods.modlauncher.ClassTransformer;
import cpw.mods.modlauncher.Environment;
import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.LogMarkers;
import cpw.mods.modlauncher.TransformStore;
import cpw.mods.modlauncher.TransformerAuditTrail;
import cpw.mods.modlauncher.TransformingClassLoaderBuilder;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TransformingClassLoader
extends ClassLoader
implements ITransformingClassLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<String> SKIP_PACKAGE_PREFIXES;
    private final ClassTransformer classTransformer;
    private final DelegatedClassLoader delegatedClassLoader;
    private final URL[] specialJars;
    private final Function<URLConnection, Manifest> manifestFinder;
    private Function<String, URL> classBytesFinder;
    private Predicate<String> targetPackageFilter;

    public TransformingClassLoader(TransformStore transformStore, LaunchPluginHandler pluginHandler, Path ... paths) {
        this.classTransformer = new ClassTransformer(transformStore, pluginHandler, this);
        this.specialJars = (URL[])Arrays.stream(paths).map(LamdbaExceptionUtils.rethrowFunction(path -> path.toUri().toURL())).toArray(URL[]::new);
        this.delegatedClassLoader = new DelegatedClassLoader(this);
        this.targetPackageFilter = s -> SKIP_PACKAGE_PREFIXES.stream().noneMatch(s::startsWith);
        this.classBytesFinder = input -> this.locateResource((String)input).orElse(null);
        this.manifestFinder = input -> this.findManifest((URLConnection)input).orElse(null);
    }

    TransformingClassLoader(TransformStore transformStore, LaunchPluginHandler pluginHandler, TransformingClassLoaderBuilder builder, Environment environment) {
        TransformerAuditTrail tat = new TransformerAuditTrail();
        environment.computePropertyIfAbsent(IEnvironment.Keys.AUDITTRAIL.get(), v -> tat);
        this.classTransformer = new ClassTransformer(transformStore, pluginHandler, this, tat);
        this.specialJars = builder.getSpecialJarsAsURLs();
        this.delegatedClassLoader = new DelegatedClassLoader(this);
        this.targetPackageFilter = s -> SKIP_PACKAGE_PREFIXES.stream().noneMatch(s::startsWith);
        this.classBytesFinder = TransformingClassLoader.alternate(builder.getClassBytesLocator(), this::locateResource);
        this.manifestFinder = TransformingClassLoader.alternate(builder.getManifestLocator(), this::findManifest);
    }

    private static <I, R> Function<I, R> alternate(@Nullable Function<I, Optional<R>> first, @Nullable Function<I, Optional<R>> second) {
        if (second == null) {
            return input -> ((Optional)first.apply(input)).orElse(null);
        }
        if (first == null) {
            return input -> ((Optional)second.apply(input)).orElse(null);
        }
        return input -> ((Optional)first.apply(input)).orElseGet(() -> ((Optional)second.apply(input)).orElse(null));
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Object object = this.getClassLoadingLock(name);
        synchronized (object) {
            if (!this.targetPackageFilter.test(name)) {
                LOGGER.trace(LogMarkers.CLASSLOADING, "Delegating to parent {}", (Object)name);
                return super.loadClass(name, resolve);
            }
            try {
                LOGGER.trace(LogMarkers.CLASSLOADING, "Attempting to load {}", (Object)name);
                Class<?> loadedClass = this.loadClass(name, this.classBytesFinder);
                LOGGER.trace(LogMarkers.CLASSLOADING, "Class loaded for {}", (Object)name);
                if (resolve) {
                    this.resolveClass(loadedClass);
                }
                return loadedClass;
            }
            catch (ClassNotFoundException | SecurityException e) {
                LOGGER.trace(LogMarkers.CLASSLOADING, "Delegating to parent classloader {}", (Object)name);
                try {
                    return super.loadClass(name, resolve);
                }
                catch (ClassNotFoundException | SecurityException e1) {
                    e1.addSuppressed(e);
                    LOGGER.trace(LogMarkers.CLASSLOADING, "Parent classloader error on {}", (Object)name, (Object)e);
                    throw e1;
                }
            }
        }
    }

    public Class<?> getLoadedClass(String name) {
        return this.findLoadedClass(name);
    }

    @Override
    public void addTargetPackageFilter(Predicate<String> filter) {
        this.targetPackageFilter = this.targetPackageFilter.and(filter);
    }

    public <T> Class<T> getClass(String name, byte[] bytes) {
        return super.defineClass(name, bytes, 0, bytes.length);
    }

    public Class<?> loadClass(String name, Function<String, URL> classBytesFinder) throws ClassNotFoundException {
        Class<?> existingClass = this.getLoadedClass(name);
        if (existingClass != null) {
            LOGGER.trace(LogMarkers.CLASSLOADING, "Found existing class {}", (Object)name);
            return existingClass;
        }
        byte[] classBytes = this.delegatedClassLoader.findClass(name, classBytesFinder, "classloading");
        return this.defineClass(name, classBytes, 0, classBytes.length);
    }

    byte[] buildTransformedClassNodeFor(String className, String reason) throws ClassNotFoundException {
        return this.delegatedClassLoader.findClass(className, this.classBytesFinder, reason);
    }

    private Optional<Manifest> findManifest(URLConnection urlConnection) {
        try {
            if (urlConnection instanceof JarURLConnection) {
                return Optional.ofNullable(((JarURLConnection)urlConnection).getManifest());
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return Optional.empty();
    }

    @Override
    protected URL findResource(String name) {
        return this.delegatedClassLoader.findResource(name, this.classBytesFinder);
    }

    protected Optional<URL> locateResource(String path) {
        return Optional.ofNullable(this.delegatedClassLoader.findResource(path));
    }

    static {
        ClassLoader.registerAsParallelCapable();
        SKIP_PACKAGE_PREFIXES = Arrays.asList("java.", "javax.", "org.objectweb.asm.", "org.apache.logging.log4j.");
    }

    private static class DelegatedClassLoader
    extends URLClassLoader {
        private final TransformingClassLoader tcl;

        DelegatedClassLoader(TransformingClassLoader cl) {
            super(cl.specialJars, null);
            this.tcl = cl;
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            return this.tcl.loadClass(name, resolve);
        }

        Class<?> getLoadedClass(String name) {
            return this.findLoadedClass(name);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            return this.tcl.findClass(name);
        }

        public URL findResource(String name, Function<String, URL> byteFinder) {
            return byteFinder.apply(name);
        }

        protected byte[] findClass(String name, Function<String, URL> classBytesFinder, String reason) throws ClassNotFoundException {
            byte[] classBytes;
            Manifest jarManifest;
            URL classResource;
            block18: {
                String path = name.replace('.', '/').concat(".class");
                classResource = classBytesFinder.apply(path);
                jarManifest = null;
                if (classResource != null) {
                    try (AutoURLConnection urlConnection = new AutoURLConnection(classResource, this.tcl.manifestFinder);){
                        int read;
                        int length = urlConnection.getContentLength();
                        InputStream is = urlConnection.getInputStream();
                        classBytes = new byte[length];
                        int pos = 0;
                        for (int remain = length; (read = is.read(classBytes, pos, remain)) != -1 && remain > 0; remain -= read) {
                            pos += read;
                        }
                        jarManifest = urlConnection.getJarManifest();
                        break block18;
                    }
                    catch (IOException e) {
                        LOGGER.trace(LogMarkers.CLASSLOADING, "Failed to load bytes for class {} at {} reason {}", (Object)name, (Object)classResource, (Object)reason, (Object)e);
                        throw new ClassNotFoundException("Failed to find class bytes for " + name, e);
                    }
                }
                classBytes = new byte[]{};
            }
            if ((classBytes = this.tcl.classTransformer.transform(classBytes, name, reason)).length > 0) {
                LOGGER.trace(LogMarkers.CLASSLOADING, "Loaded transform target {} from {} reason {}", (Object)name, (Object)classResource, (Object)reason);
                if (reason.equals("classloading")) {
                    int i = name.lastIndexOf(46);
                    String pkgname = i > 0 ? name.substring(0, i) : "";
                    this.tryDefinePackage(pkgname, jarManifest);
                }
                return classBytes;
            }
            LOGGER.trace(LogMarkers.CLASSLOADING, "Failed to transform target {} from {}", (Object)name, (Object)classResource);
            throw new ClassNotFoundException();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        Package tryDefinePackage(String name, @Nullable Manifest man) throws IllegalArgumentException {
            if (this.tcl.getPackage(name) == null) {
                DelegatedClassLoader delegatedClassLoader = this;
                synchronized (delegatedClassLoader) {
                    if (this.tcl.getPackage(name) != null) {
                        return this.tcl.getPackage(name);
                    }
                    String path = name.replace('.', '/').concat("/");
                    String specTitle = null;
                    String specVersion = null;
                    String specVendor = null;
                    String implTitle = null;
                    String implVersion = null;
                    String implVendor = null;
                    if (man != null) {
                        Attributes attr = man.getAttributes(path);
                        if (attr != null) {
                            specTitle = attr.getValue(Attributes.Name.SPECIFICATION_TITLE);
                            specVersion = attr.getValue(Attributes.Name.SPECIFICATION_VERSION);
                            specVendor = attr.getValue(Attributes.Name.SPECIFICATION_VENDOR);
                            implTitle = attr.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
                            implVersion = attr.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
                            implVendor = attr.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
                        }
                        if ((attr = man.getMainAttributes()) != null) {
                            if (specTitle == null) {
                                specTitle = attr.getValue(Attributes.Name.SPECIFICATION_TITLE);
                            }
                            if (specVersion == null) {
                                specVersion = attr.getValue(Attributes.Name.SPECIFICATION_VERSION);
                            }
                            if (specVendor == null) {
                                specVendor = attr.getValue(Attributes.Name.SPECIFICATION_VENDOR);
                            }
                            if (implTitle == null) {
                                implTitle = attr.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
                            }
                            if (implVersion == null) {
                                implVersion = attr.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
                            }
                            if (implVendor == null) {
                                implVendor = attr.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
                            }
                        }
                    }
                    return this.tcl.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, null);
                }
            }
            return this.tcl.getPackage(name);
        }

        static {
            ClassLoader.registerAsParallelCapable();
        }
    }

    static class AutoURLConnection
    implements AutoCloseable {
        private final URLConnection urlConnection;
        private final InputStream inputStream;
        private final Function<URLConnection, Manifest> manifestFinder;

        AutoURLConnection(URL url, Function<URLConnection, Manifest> manifestFinder) throws IOException {
            this.urlConnection = url.openConnection();
            this.inputStream = this.urlConnection.getInputStream();
            this.manifestFinder = manifestFinder;
        }

        @Override
        public void close() throws IOException {
            this.inputStream.close();
        }

        int getContentLength() {
            return this.urlConnection.getContentLength();
        }

        InputStream getInputStream() {
            return this.inputStream;
        }

        Manifest getJarManifest() {
            return this.manifestFinder.apply(this.urlConnection);
        }
    }
}
