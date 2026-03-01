package lombok.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import lombok.Lombok;
import lombok.core.LombokApp;
import lombok.patcher.ClassRootFinder;

public class PublicApiCreatorApp
extends LombokApp {
    @Override
    public String getAppName() {
        return "publicApi";
    }

    @Override
    public String getAppDescription() {
        return "Creates a small lombok-api.jar with the annotations and other public API\nclasses of all lombok features. This is primarily useful to include in your\nandroid projects.";
    }

    @Override
    public int runApp(List<String> rawArgs) throws Exception {
        String loc = ".";
        switch (rawArgs.size()) {
            case 0: {
                break;
            }
            case 1: {
                loc = rawArgs.get(0);
                break;
            }
            default: {
                System.err.println("Supply 1 path to specify the directory where lombok-api.jar will be created. No path means the current directory is used.");
                return 1;
            }
        }
        File out = new File(loc, "lombok-api.jar");
        int errCode = 0;
        try {
            errCode = this.writeApiJar(out);
        }
        catch (Exception e) {
            System.err.println("ERROR: Creating " + PublicApiCreatorApp.canonical(out) + " failed: ");
            e.printStackTrace();
            return 1;
        }
        return errCode;
    }

    private static File findOurJar() {
        return new File(ClassRootFinder.findClassRootOfClass(PublicApiCreatorApp.class));
    }

    private int writeApiJar(File outFile) throws Exception {
        File selfRaw = PublicApiCreatorApp.findOurJar();
        if (selfRaw == null) {
            System.err.println("The publicApi option only works if lombok is a jar.");
            return 2;
        }
        ArrayList<String> toCopy = new ArrayList<String>();
        JarFile self = new JarFile(selfRaw);
        try {
            Enumeration<JarEntry> entries = self.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.startsWith("lombok/") || name.endsWith("/package-info.class") || !name.endsWith(".class")) continue;
                String subName = name.substring(7, name.length() - 6);
                int firstSlash = subName.indexOf(47);
                if (firstSlash == -1) {
                    if (subName.startsWith("ConfigurationKeys")) continue;
                    toCopy.add(name);
                    continue;
                }
                String topPkg = subName.substring(0, firstSlash);
                if (!"extern".equals(topPkg) && !"experimental".equals(topPkg)) continue;
                toCopy.add(name);
            }
        }
        finally {
            self.close();
        }
        if (toCopy.isEmpty()) {
            System.out.println("Not generating lombok-api.jar: No lombok api classes required!");
            return 1;
        }
        FileOutputStream out = new FileOutputStream(outFile);
        boolean success = false;
        try {
            JarOutputStream jar = new JarOutputStream(out);
            for (String resourceName : toCopy) {
                InputStream in = Lombok.class.getResourceAsStream("/" + resourceName);
                try {
                    if (in == null) {
                        throw new Fail(String.format("api class %s cannot be found", resourceName));
                    }
                    this.writeIntoJar(jar, resourceName, in);
                }
                finally {
                    if (in != null) {
                        in.close();
                    }
                }
            }
            jar.close();
            ((OutputStream)out).close();
            System.out.println("Successfully created: " + PublicApiCreatorApp.canonical(outFile));
            return 0;
        }
        catch (Throwable t) {
            try {
                ((OutputStream)out).close();
            }
            catch (Throwable throwable) {}
            if (!success) {
                outFile.delete();
            }
            if (t instanceof Fail) {
                System.err.println(t.getMessage());
                return 1;
            }
            if (t instanceof Exception) {
                throw (Exception)t;
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            throw new Exception(t);
        }
    }

    private void writeIntoJar(JarOutputStream jar, String resourceName, InputStream in) throws IOException {
        int r;
        jar.putNextEntry(new ZipEntry(resourceName));
        byte[] b = new byte[65536];
        while ((r = in.read(b)) != -1) {
            jar.write(b, 0, r);
        }
        jar.closeEntry();
        in.close();
    }

    private static String canonical(File out) {
        try {
            return out.getCanonicalPath();
        }
        catch (Exception exception) {
            return out.getAbsolutePath();
        }
    }

    private static class Fail
    extends Exception {
        Fail(String message) {
            super(message);
        }
    }
}
