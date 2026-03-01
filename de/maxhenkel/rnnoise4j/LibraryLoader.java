package de.maxhenkel.rnnoise4j;

import de.maxhenkel.rnnoise4j.UnknownPlatformException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.annotation.Nullable;

class LibraryLoader {
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    private static final String OS_ARCH = System.getProperty("os.arch").toLowerCase();

    LibraryLoader() {
    }

    private static boolean isWindows() {
        return OS_NAME.contains("win");
    }

    private static boolean isMac() {
        return OS_NAME.contains("mac");
    }

    private static boolean isLinux() {
        return OS_NAME.contains("nux");
    }

    private static String getPlatform() throws UnknownPlatformException {
        if (LibraryLoader.isWindows()) {
            return "windows";
        }
        if (LibraryLoader.isMac()) {
            return "mac";
        }
        if (LibraryLoader.isLinux()) {
            return "linux";
        }
        throw new UnknownPlatformException(String.format("Unknown operating system: %s", OS_NAME));
    }

    private static String getArchitecture() {
        switch (OS_ARCH) {
            case "i386": 
            case "i486": 
            case "i586": 
            case "i686": 
            case "x86": 
            case "x86_32": {
                return "x86";
            }
            case "amd64": 
            case "x86_64": 
            case "x86-64": {
                return "x64";
            }
            case "aarch64": {
                return "aarch64";
            }
        }
        return OS_ARCH;
    }

    private static String getLibraryExtension() throws UnknownPlatformException {
        if (LibraryLoader.isWindows()) {
            return "dll";
        }
        if (LibraryLoader.isMac()) {
            return "dylib";
        }
        if (LibraryLoader.isLinux()) {
            return "so";
        }
        throw new UnknownPlatformException(String.format("Unknown operating system: %s", OS_NAME));
    }

    private static String getLibraryName(String name) throws UnknownPlatformException {
        if (LibraryLoader.isWindows()) {
            return String.format("%s.%s", name, LibraryLoader.getLibraryExtension());
        }
        return String.format("lib%s.%s", name, LibraryLoader.getLibraryExtension());
    }

    private static String getNativeFolderName() throws UnknownPlatformException {
        return String.format("%s-%s", LibraryLoader.getPlatform(), LibraryLoader.getArchitecture());
    }

    private static String getResourcePath(String libName) throws UnknownPlatformException {
        return String.format("natives/%s/%s", LibraryLoader.getNativeFolderName(), LibraryLoader.getLibraryName(libName));
    }

    private static File getTempDir() {
        return new File(System.getProperty("java.io.tmpdir"));
    }

    public static void load(String libraryName) throws UnknownPlatformException, IOException {
        String resourcePath = LibraryLoader.getResourcePath(libraryName);
        String md5 = null;
        try (InputStream in2 = LibraryLoader.getResource(resourcePath);){
            if (in2 == null) {
                throw new UnknownPlatformException(String.format("Could not find %s natives for platform %s", libraryName, LibraryLoader.getNativeFolderName()));
            }
            md5 = LibraryLoader.checksum(in2);
        }
        catch (Exception in2) {
            // empty catch block
        }
        File tempDir = new File(LibraryLoader.getTempDir(), md5 == null ? libraryName : String.format("%s-%s", libraryName, md5));
        tempDir.mkdirs();
        File tempFile = new File(tempDir, LibraryLoader.getLibraryName(libraryName));
        if (!tempFile.exists()) {
            try (InputStream in = LibraryLoader.getResource(resourcePath);){
                if (in == null) {
                    throw new UnknownPlatformException(String.format("Could not find %s natives for platform %s", libraryName, LibraryLoader.getNativeFolderName()));
                }
                Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        try {
            System.load(tempFile.getAbsolutePath());
        }
        catch (UnsatisfiedLinkError e) {
            throw new UnknownPlatformException(String.format("Could not load %s natives for %s", libraryName, LibraryLoader.getNativeFolderName()), e);
        }
    }

    @Nullable
    private static InputStream getResource(String path) {
        return LibraryLoader.class.getClassLoader().getResourceAsStream(path);
    }

    private static String checksum(InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        int numRead;
        byte[] buffer = new byte[1024];
        MessageDigest digest = MessageDigest.getInstance("MD5");
        do {
            if ((numRead = inputStream.read(buffer)) <= 0) continue;
            digest.update(buffer, 0, numRead);
        } while (numRead != -1);
        inputStream.close();
        byte[] bytes = digest.digest();
        StringBuilder result = new StringBuilder();
        for (byte value : bytes) {
            result.append(String.format("%02x", value));
        }
        return result.toString();
    }
}
