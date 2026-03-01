package dimasik.managers.mods.voicechat.resourcepacks;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import dimasik.managers.mods.voicechat.Voicechat;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class VoiceChatResourcePack
extends ResourcePack {
    protected String path;
    protected ITextComponent name;

    public VoiceChatResourcePack(String path, ITextComponent name) {
        super(null);
        this.path = path;
        this.name = name;
    }

    @Override
    protected InputStream getInputStream(String resourcePath) throws IOException {
        return this.get(resourcePath);
    }

    @Override
    protected boolean resourceExists(String resourcePath) {
        return this.get(resourcePath) != null;
    }

    @Override
    public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespaceIn, String pathIn, int maxDepthIn, Predicate<String> filterIn) {
        return this.getResources(type, namespaceIn, pathIn, maxDepthIn, filterIn);
    }

    @Override
    public Set<String> getResourceNamespaces(ResourcePackType type) {
        return this.getNamespaces(type);
    }

    @Override
    public String getName() {
        return this.path;
    }

    private String getPath() {
        return "/packs/" + this.path + "/";
    }

    @Nullable
    private InputStream get(String name) {
        return Voicechat.class.getResourceAsStream(this.getPath() + name);
    }

    protected InputStream getResource(String name) throws IOException {
        InputStream resourceAsStream = this.get(name);
        if (resourceAsStream == null) {
            throw new FileNotFoundException("Resource " + name + " does not exist");
        }
        return resourceAsStream;
    }

    protected boolean hasResource(String name) {
        return this.get(name) != null;
    }

    public Collection<ResourceLocation> getResources(ResourcePackType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
        ArrayList<ResourceLocation> list = Lists.newArrayList();
        try {
            URL url = Voicechat.class.getResource(this.getPath());
            if (url == null) {
                return list;
            }
            Path namespacePath = Paths.get(url.toURI()).resolve(type.getDirectoryName()).resolve(namespace);
            Path resPath = namespacePath.resolve(prefix);
            if (!Files.exists(resPath, new LinkOption[0])) {
                return list;
            }
            try (Stream<Path> files = Files.walk(resPath, new FileVisitOption[0]);){
                files.filter(path -> !Files.isDirectory(path, new LinkOption[0])).forEach(path -> {
                    String relative = VoiceChatResourcePack.convertPath(path).substring(VoiceChatResourcePack.convertPath(namespacePath).length() + 1);
                    ResourceLocation resourceLocation = new ResourceLocation(namespace, relative);
                    list.add(resourceLocation);
                });
            }
        }
        catch (Exception e) {
            Voicechat.LOGGER.error("Failed to list builtin pack resources", e);
        }
        return list.stream().filter(resourceLocation -> pathFilter.test(resourceLocation.getPath())).collect(Collectors.toList());
    }

    private static String convertPath(Path path) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < path.getNameCount(); ++i) {
            stringBuilder.append(path.getName(i));
            if (i >= path.getNameCount() - 1) continue;
            stringBuilder.append("/");
        }
        return stringBuilder.toString();
    }

    public Set<String> getNamespaces(ResourcePackType packType) {
        if (packType == ResourcePackType.CLIENT_RESOURCES) {
            return ImmutableSet.of("voicechat");
        }
        return ImmutableSet.of();
    }

    @Override
    public void close() {
    }
}
