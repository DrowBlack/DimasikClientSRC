package net.minecraft.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VanillaPack
implements IResourcePack {
    public static Path basePath;
    private static final Logger LOGGER;
    public static Class<?> baseClass;
    private static final Map<ResourcePackType, FileSystem> FILE_SYSTEMS_BY_PACK_TYPE;
    public final Set<String> resourceNamespaces;
    private static final boolean ON_WINDOWS;
    private static final boolean FORGE;

    public VanillaPack(String ... resourceNamespacesIn) {
        this.resourceNamespaces = ImmutableSet.copyOf(resourceNamespacesIn);
    }

    @Override
    public InputStream getRootResourceStream(String fileName) throws IOException {
        if (!fileName.contains("/") && !fileName.contains("\\")) {
            Path path;
            if (basePath != null && Files.exists(path = basePath.resolve(fileName), new LinkOption[0])) {
                return Files.newInputStream(path, new OpenOption[0]);
            }
            return this.getInputStreamVanilla(fileName);
        }
        throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
    }

    @Override
    public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException {
        InputStream inputstream = this.getInputStreamVanilla(type, location);
        if (inputstream != null) {
            return inputstream;
        }
        throw new FileNotFoundException(location.getPath());
    }

    @Override
    public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespaceIn, String pathIn, int maxDepthIn, Predicate<String> filterIn) {
        HashSet<ResourceLocation> set = Sets.newHashSet();
        if (basePath != null) {
            try {
                VanillaPack.collectResources(set, maxDepthIn, namespaceIn, basePath.resolve(type.getDirectoryName()), pathIn, filterIn);
            }
            catch (IOException iOException) {
                // empty catch block
            }
            if (type == ResourcePackType.CLIENT_RESOURCES) {
                Enumeration<URL> enumeration = null;
                try {
                    enumeration = baseClass.getClassLoader().getResources(type.getDirectoryName() + "/");
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                while (enumeration != null && enumeration.hasMoreElements()) {
                    try {
                        URI uri = enumeration.nextElement().toURI();
                        if (!"file".equals(uri.getScheme())) continue;
                        VanillaPack.collectResources(set, maxDepthIn, namespaceIn, Paths.get(uri), pathIn, filterIn);
                    }
                    catch (IOException | URISyntaxException uri) {}
                }
            }
        }
        try {
            URL url1 = VanillaPack.class.getResource("/" + type.getDirectoryName() + "/.mcassetsroot");
            if (url1 == null) {
                LOGGER.error("Couldn't find .mcassetsroot, cannot load vanilla resources");
                return set;
            }
            URI uri1 = url1.toURI();
            if ("file".equals(uri1.getScheme())) {
                URL url = new URL(url1.toString().substring(0, url1.toString().length() - ".mcassetsroot".length()));
                Path path = Paths.get(url.toURI());
                VanillaPack.collectResources(set, maxDepthIn, namespaceIn, path, pathIn, filterIn);
            } else if ("jar".equals(uri1.getScheme())) {
                Path path1 = FILE_SYSTEMS_BY_PACK_TYPE.get((Object)type).getPath("/" + type.getDirectoryName(), new String[0]);
                VanillaPack.collectResources(set, maxDepthIn, "minecraft", path1, pathIn, filterIn);
            } else {
                LOGGER.error("Unsupported scheme {} trying to list vanilla resources (NYI?)", (Object)uri1);
            }
        }
        catch (FileNotFoundException | NoSuchFileException url1) {
        }
        catch (IOException | URISyntaxException ioexception) {
            LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)ioexception);
        }
        return set;
    }

    private static void collectResources(Collection<ResourceLocation> resourceLocationsIn, int maxDepthIn, String namespaceIn, Path pathIn, String pathNameIn, Predicate<String> filterIn) throws IOException {
        Path path = pathIn.resolve(namespaceIn);
        try (Stream<Path> stream = Files.walk(path.resolve(pathNameIn), maxDepthIn, new FileVisitOption[0]);){
            stream.filter(p_lambda$collectResources$1_1_ -> !p_lambda$collectResources$1_1_.endsWith(".mcmeta") && Files.isRegularFile(p_lambda$collectResources$1_1_, new LinkOption[0]) && filterIn.test(p_lambda$collectResources$1_1_.getFileName().toString())).map(p_lambda$collectResources$2_2_ -> new ResourceLocation(namespaceIn, path.relativize((Path)p_lambda$collectResources$2_2_).toString().replaceAll("\\\\", "/"))).forEach(resourceLocationsIn::add);
        }
    }

    @Nullable
    protected InputStream getInputStreamVanilla(ResourcePackType type, ResourceLocation location) {
        Path path;
        String s = VanillaPack.getPath(type, location);
        InputStream inputstream = ReflectorForge.getOptiFineResourceStream(s);
        if (inputstream != null) {
            return inputstream;
        }
        if (basePath != null && Files.exists(path = basePath.resolve(type.getDirectoryName() + "/" + location.getNamespace() + "/" + location.getPath()), new LinkOption[0])) {
            try {
                return Files.newInputStream(path, new OpenOption[0]);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        try {
            URL url = VanillaPack.class.getResource(s);
            return VanillaPack.isValid(s, url) ? (FORGE ? this.getExtraInputStream(type, s) : url.openStream()) : null;
        }
        catch (IOException ioexception1) {
            return VanillaPack.class.getResourceAsStream(s);
        }
    }

    private static String getPath(ResourcePackType packTypeIn, ResourceLocation locationIn) {
        return "/" + packTypeIn.getDirectoryName() + "/" + locationIn.getNamespace() + "/" + locationIn.getPath();
    }

    private static boolean isValid(String pathIn, @Nullable URL urlIn) throws IOException {
        return urlIn != null && (urlIn.getProtocol().equals("jar") || VanillaPack.validatePath(new File(urlIn.getFile()), pathIn));
    }

    @Nullable
    protected InputStream getInputStreamVanilla(String pathIn) {
        return FORGE ? this.getExtraInputStream(ResourcePackType.SERVER_DATA, "/" + pathIn) : VanillaPack.class.getResourceAsStream("/" + pathIn);
    }

    @Override
    public boolean resourceExists(ResourcePackType type, ResourceLocation location) {
        Path path;
        String s = VanillaPack.getPath(type, location);
        InputStream inputstream = ReflectorForge.getOptiFineResourceStream(s);
        if (inputstream != null) {
            return true;
        }
        if (basePath != null && Files.exists(path = basePath.resolve(type.getDirectoryName() + "/" + location.getNamespace() + "/" + location.getPath()), new LinkOption[0])) {
            return true;
        }
        try {
            URL url = VanillaPack.class.getResource(s);
            return VanillaPack.isValid(s, url);
        }
        catch (IOException ioexception1) {
            return false;
        }
    }

    @Override
    public Set<String> getResourceNamespaces(ResourcePackType type) {
        return this.resourceNamespaces;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    @Nullable
    public <T> T getMetadata(IMetadataSectionSerializer<T> deserializer) throws IOException {
        try (InputStream inputstream = this.getRootResourceStream("pack.mcmeta");){
            T t = ResourcePack.getResourceMetadata(deserializer, inputstream);
            return t;
        }
        catch (FileNotFoundException | RuntimeException filenotfoundexception) {
            return null;
        }
    }

    @Override
    public String getName() {
        return "Default";
    }

    @Override
    public void close() {
    }

    private static boolean validatePath(File p_validatePath_0_, String p_validatePath_1_) throws IOException {
        String s = p_validatePath_0_.getPath();
        if (s.startsWith("file:")) {
            if (ON_WINDOWS) {
                s = s.replace("\\", "/");
            }
            return s.endsWith(p_validatePath_1_);
        }
        return FolderPack.validatePath(p_validatePath_0_, p_validatePath_1_);
    }

    private InputStream getExtraInputStream(ResourcePackType p_getExtraInputStream_1_, String p_getExtraInputStream_2_) {
        try {
            FileSystem filesystem = FILE_SYSTEMS_BY_PACK_TYPE.get((Object)p_getExtraInputStream_1_);
            return filesystem != null ? Files.newInputStream(filesystem.getPath(p_getExtraInputStream_2_, new String[0]), new OpenOption[0]) : VanillaPack.class.getResourceAsStream(p_getExtraInputStream_2_);
        }
        catch (IOException ioexception) {
            return VanillaPack.class.getResourceAsStream(p_getExtraInputStream_2_);
        }
    }

    static {
        LOGGER = LogManager.getLogger();
        FILE_SYSTEMS_BY_PACK_TYPE = Util.make(Maps.newHashMap(), p_lambda$static$0_0_ -> {
            Class<VanillaPack> clazz = VanillaPack.class;
            synchronized (VanillaPack.class) {
                for (ResourcePackType resourcepacktype : ResourcePackType.values()) {
                    URL url = VanillaPack.class.getResource("/" + resourcepacktype.getDirectoryName() + "/.mcassetsroot");
                    try {
                        FileSystem filesystem;
                        URI uri = url.toURI();
                        if (!"jar".equals(uri.getScheme())) continue;
                        try {
                            filesystem = FileSystems.getFileSystem(uri);
                        }
                        catch (FileSystemNotFoundException filesystemnotfoundexception) {
                            filesystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                        }
                        p_lambda$static$0_0_.put(resourcepacktype, filesystem);
                    }
                    catch (IOException | URISyntaxException ioexception) {
                        LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)ioexception);
                    }
                }
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return;
            }
        });
        ON_WINDOWS = Util.getOSType() == Util.OS.WINDOWS;
        FORGE = Reflector.ForgeHooksClient.exists();
    }
}
