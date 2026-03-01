package net.minecraft.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.ResourcePackFileNotFoundException;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class FilePack
extends ResourcePack {
    public static final Splitter PATH_SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);
    private ZipFile zipFile;

    public FilePack(File fileIn) {
        super(fileIn);
    }

    private ZipFile getResourcePackZipFile() throws IOException {
        if (this.zipFile == null) {
            this.zipFile = new ZipFile(this.file);
        }
        return this.zipFile;
    }

    @Override
    protected InputStream getInputStream(String resourcePath) throws IOException {
        ZipFile zipfile = this.getResourcePackZipFile();
        ZipEntry zipentry = zipfile.getEntry(resourcePath);
        if (zipentry == null) {
            throw new ResourcePackFileNotFoundException(this.file, resourcePath);
        }
        return zipfile.getInputStream(zipentry);
    }

    @Override
    public boolean resourceExists(String resourcePath) {
        try {
            return this.getResourcePackZipFile().getEntry(resourcePath) != null;
        }
        catch (IOException ioexception) {
            return false;
        }
    }

    @Override
    public Set<String> getResourceNamespaces(ResourcePackType type) {
        ZipFile zipfile;
        try {
            zipfile = this.getResourcePackZipFile();
        }
        catch (IOException ioexception) {
            return Collections.emptySet();
        }
        Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
        HashSet<String> set = Sets.newHashSet();
        while (enumeration.hasMoreElements()) {
            ArrayList<String> list;
            ZipEntry zipentry = enumeration.nextElement();
            String s = zipentry.getName();
            if (!s.startsWith(type.getDirectoryName() + "/") || (list = Lists.newArrayList(PATH_SPLITTER.split(s))).size() <= 1) continue;
            String s1 = (String)list.get(1);
            if (s1.equals(s1.toLowerCase(Locale.ROOT))) {
                set.add(s1);
                continue;
            }
            this.onIgnoreNonLowercaseNamespace(s1);
        }
        return set;
    }

    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    @Override
    public void close() {
        if (this.zipFile != null) {
            IOUtils.closeQuietly((Closeable)this.zipFile);
            this.zipFile = null;
        }
    }

    @Override
    public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespaceIn, String pathIn, int maxDepthIn, Predicate<String> filterIn) {
        ZipFile zipfile;
        try {
            zipfile = this.getResourcePackZipFile();
        }
        catch (IOException ioexception) {
            return Collections.emptySet();
        }
        Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
        ArrayList<ResourceLocation> list = Lists.newArrayList();
        String s = type.getDirectoryName() + "/" + namespaceIn + "/";
        String s1 = s + pathIn + "/";
        while (enumeration.hasMoreElements()) {
            String s3;
            String[] astring;
            String s2;
            ZipEntry zipentry = enumeration.nextElement();
            if (zipentry.isDirectory() || (s2 = zipentry.getName()).endsWith(".mcmeta") || !s2.startsWith(s1) || (astring = (s3 = s2.substring(s.length())).split("/")).length < maxDepthIn + 1 || !filterIn.test(astring[astring.length - 1])) continue;
            list.add(new ResourceLocation(namespaceIn, s3));
        }
        return list;
    }
}
