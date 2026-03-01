package lombok.patcher;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ClassRootFinder {
    private static String urlDecode(String in, boolean forceUtf8) {
        try {
            return URLDecoder.decode(in, forceUtf8 ? "UTF-8" : Charset.defaultCharset().name());
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            try {
                return URLDecoder.decode(in, "UTF-8");
            }
            catch (UnsupportedEncodingException unsupportedEncodingException2) {
                return in;
            }
        }
    }

    public static String findClassRootOfSelf() {
        return ClassRootFinder.findClassRootOfClass(ClassRootFinder.class);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static String findClassRootOfClass(Class<?> context) {
        String packageBase;
        String name = context.getName();
        int idx = name.lastIndexOf(46);
        if (idx > -1) {
            packageBase = name.substring(0, idx);
            name = name.substring(idx + 1);
        } else {
            packageBase = "";
        }
        URL selfURL = context.getResource(String.valueOf(name) + ".class");
        String self = selfURL.toString();
        if (self.startsWith("file:/")) {
            String suffix;
            String path = ClassRootFinder.urlDecode(self.substring(5), false);
            if (!new File(path).exists()) {
                path = ClassRootFinder.urlDecode(self.substring(5), true);
            }
            if (!path.endsWith(suffix = "/" + packageBase.replace('.', '/') + "/" + name + ".class")) {
                throw new IllegalArgumentException("Unknown path structure: " + path);
            }
            self = path.substring(0, path.length() - suffix.length());
        } else {
            if (!self.startsWith("jar:")) throw new IllegalArgumentException("Unknown protocol: " + self);
            int sep = self.indexOf(33);
            if (sep == -1) {
                throw new IllegalArgumentException("No separator in jar protocol: " + self);
            }
            String jarLoc = self.substring(4, sep);
            if (!jarLoc.startsWith("file:/")) throw new IllegalArgumentException("Unknown path structure: " + self);
            String path = ClassRootFinder.urlDecode(jarLoc.substring(5), false);
            if (!new File(path).exists()) {
                path = ClassRootFinder.urlDecode(jarLoc.substring(5), true);
            }
            self = path;
        }
        if (!self.isEmpty()) return self;
        return "/";
    }

    public static void main(String[] args) {
        System.out.println(ClassRootFinder.findClassRootOfSelf());
    }
}
