package lombok.core.configuration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ConfigurationFile {
    private static final Pattern VARIABLE = Pattern.compile("\\<(.+?)\\>");
    private static final String LOMBOK_CONFIG_FILENAME = "lombok.config";
    private static final Map<String, String> ENV = new HashMap<String, String>(System.getenv());
    private static final ThreadLocal<byte[]> buffers = new ThreadLocal<byte[]>(){

        @Override
        protected byte[] initialValue() {
            return new byte[65536];
        }
    };
    private final String identifier;

    static void setEnvironment(String key, String value) {
        ENV.put(key, value);
    }

    public static ConfigurationFile forFile(File file) {
        return new RegularConfigurationFile(file);
    }

    public static ConfigurationFile forDirectory(File directory) {
        return ConfigurationFile.forFile(new File(directory, LOMBOK_CONFIG_FILENAME));
    }

    public static ConfigurationFile fromCharSequence(String identifier, CharSequence contents, long lastModified) {
        return new CharSequenceConfigurationFile(identifier, contents, lastModified);
    }

    private ConfigurationFile(String identifier) {
        this.identifier = identifier;
    }

    abstract long getLastModifiedOrMissing();

    abstract boolean exists();

    abstract CharSequence contents() throws IOException;

    public abstract ConfigurationFile resolve(String var1);

    abstract ConfigurationFile parent();

    final String description() {
        return this.identifier;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof ConfigurationFile)) {
            return false;
        }
        return this.identifier.equals(((ConfigurationFile)obj).identifier);
    }

    public final int hashCode() {
        return this.identifier.hashCode();
    }

    public static long getLastModifiedOrMissing(File file) {
        if (!ConfigurationFile.fileExists(file)) {
            return -88L;
        }
        return file.lastModified();
    }

    private static boolean fileExists(File file) {
        return file.exists() && file.isFile();
    }

    private static String read(InputStream is) throws IOException {
        int r;
        byte[] b = buffers.get();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while ((r = is.read(b)) != -1) {
            out.write(b, 0, r);
        }
        return new String(out.toByteArray(), "UTF-8");
    }

    /* synthetic */ ConfigurationFile(String string, ConfigurationFile configurationFile) {
        this(string);
    }

    private static class ArchivedConfigurationFile
    extends ConfigurationFile {
        private static final URI ROOT1 = URI.create("http://x.y/a/");
        private static final URI ROOT2 = URI.create("ftp://y.x/b/");
        private static final ConcurrentMap<String, Object> locks = new ConcurrentHashMap<String, Object>();
        private final File archive;
        private final URI file;
        private final Object lock;
        private long lastModified = -2L;
        private String contents;

        public static ConfigurationFile create(File archive, URI file) {
            if (!ArchivedConfigurationFile.isRelative(file)) {
                return null;
            }
            return new ArchivedConfigurationFile(archive, file, String.valueOf(archive.getPath()) + "!" + file.getPath());
        }

        static boolean isRelative(URI path) {
            try {
                return ROOT1.resolve(path).toString().startsWith(ROOT1.toString()) && ROOT2.resolve(path).toString().startsWith(ROOT2.toString());
            }
            catch (Exception exception) {
                return false;
            }
        }

        ArchivedConfigurationFile(File archive, URI file, String description) {
            super(description, null);
            this.archive = archive;
            this.file = file;
            locks.putIfAbsent(archive.getPath(), new Object());
            this.lock = locks.get(archive.getPath());
        }

        @Override
        long getLastModifiedOrMissing() {
            return ArchivedConfigurationFile.getLastModifiedOrMissing(this.archive);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        boolean exists() {
            if (!ConfigurationFile.fileExists(this.archive)) {
                return false;
            }
            Object object = this.lock;
            synchronized (object) {
                try {
                    this.readIfNeccesary();
                    return this.contents != null;
                }
                catch (Exception exception) {
                    return false;
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        CharSequence contents() throws IOException {
            Object object = this.lock;
            synchronized (object) {
                this.readIfNeccesary();
                return this.contents;
            }
        }

        void readIfNeccesary() throws IOException {
            long archiveModified = this.getLastModifiedOrMissing();
            if (archiveModified == this.lastModified) {
                return;
            }
            this.contents = null;
            this.lastModified = archiveModified;
            if (archiveModified == -88L) {
                return;
            }
            this.contents = this.read();
        }

        /*
         * Exception decompiling
         */
        private String read() throws IOException {
            /*
             * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
             * 
             * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [8[DOLOOP]], but top level block is 3[TRYBLOCK]
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
             *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
             *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:923)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
             *     at org.benf.cfr.reader.Driver.doClass(Driver.java:84)
             *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:78)
             *     at software.coley.recaf.services.decompile.cfr.CfrDecompiler.decompileInternal(CfrDecompiler.java:61)
             *     at software.coley.recaf.services.decompile.AbstractJvmDecompiler.decompile(AbstractJvmDecompiler.java:49)
             *     at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:104)
             *     at java.base/java.lang.reflect.Method.invoke(Method.java:565)
             *     at org.jboss.weld.bean.proxy.AbstractBeanInstance.invoke(AbstractBeanInstance.java:39)
             *     at org.jboss.weld.bean.proxy.ProxyMethodHandler.invoke(ProxyMethodHandler.java:109)
             *     at software.coley.recaf.services.decompile.Decompiler$JvmDecompiler$1269202896$Proxy$_$$_WeldClientProxy.decompile(Unknown Source)
             *     at software.coley.recaf.services.decompile.DecompilerManager.lambda$decompile$2(DecompilerManager.java:156)
             *     at java.base/java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1789)
             *     at software.coley.recaf.util.threading.ThreadUtil.lambda$wrap$2(ThreadUtil.java:236)
             *     at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1090)
             *     at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:614)
             *     at java.base/java.lang.Thread.run(Thread.java:1474)
             */
            throw new IllegalStateException("Decompilation failed");
        }

        @Override
        public ConfigurationFile resolve(String path) {
            URI resolved;
            block3: {
                try {
                    resolved = this.file.resolve(path);
                    if (ArchivedConfigurationFile.isRelative(resolved)) break block3;
                    return null;
                }
                catch (Exception exception) {
                    return null;
                }
            }
            return ArchivedConfigurationFile.create(this.archive, resolved);
        }

        @Override
        ConfigurationFile parent() {
            return null;
        }
    }

    private static class CharSequenceConfigurationFile
    extends ConfigurationFile {
        private final CharSequence contents;
        private final long lastModified;

        private CharSequenceConfigurationFile(String identifier, CharSequence contents, long lastModified) {
            super(identifier, null);
            this.contents = contents;
            this.lastModified = lastModified;
        }

        @Override
        long getLastModifiedOrMissing() {
            return this.lastModified;
        }

        @Override
        CharSequence contents() throws IOException {
            return this.contents;
        }

        @Override
        boolean exists() {
            return true;
        }

        @Override
        public ConfigurationFile resolve(String path) {
            return null;
        }

        @Override
        ConfigurationFile parent() {
            return null;
        }
    }

    private static class RegularConfigurationFile
    extends ConfigurationFile {
        private final File file;
        private ConfigurationFile parent;

        private RegularConfigurationFile(File file) {
            super(file.getPath(), null);
            this.file = file;
        }

        @Override
        boolean exists() {
            return ConfigurationFile.fileExists(this.file);
        }

        @Override
        public ConfigurationFile resolve(String path) {
            if (path.endsWith("!")) {
                return null;
            }
            String[] parts = path.split("!");
            if (parts.length > 2) {
                return null;
            }
            String realFileName = parts[0];
            File file = this.resolveFile(RegularConfigurationFile.replaceEnvironmentVariables(realFileName));
            if (realFileName.endsWith(".zip") || realFileName.endsWith(".jar")) {
                try {
                    return ArchivedConfigurationFile.create(file, URI.create(parts.length == 1 ? ConfigurationFile.LOMBOK_CONFIG_FILENAME : parts[1]));
                }
                catch (Exception exception) {
                    return null;
                }
            }
            if (parts.length > 1) {
                return null;
            }
            return file == null ? null : RegularConfigurationFile.forFile(file);
        }

        private File resolveFile(String path) {
            boolean absolute = false;
            int colon = path.indexOf(58);
            if (colon != -1) {
                if (colon != 1 || path.indexOf(58, colon + 1) != -1) {
                    return null;
                }
                char firstCharacter = Character.toLowerCase(path.charAt(0));
                if (firstCharacter < 'a' || firstCharacter > 'z') {
                    return null;
                }
                absolute = true;
            }
            if (path.charAt(0) == '/') {
                absolute = true;
            }
            try {
                return absolute ? new File(path) : new File(this.file.toURI().resolve(path));
            }
            catch (Exception exception) {
                return null;
            }
        }

        @Override
        long getLastModifiedOrMissing() {
            return RegularConfigurationFile.getLastModifiedOrMissing(this.file);
        }

        @Override
        CharSequence contents() throws IOException {
            FileInputStream is = new FileInputStream(this.file);
            try {
                String string = ConfigurationFile.read(is);
                return string;
            }
            finally {
                is.close();
            }
        }

        @Override
        ConfigurationFile parent() {
            if (this.parent == null) {
                File parentFile = this.file.getParentFile().getParentFile();
                this.parent = parentFile == null ? null : RegularConfigurationFile.forDirectory(parentFile);
            }
            return this.parent;
        }

        private static String replaceEnvironmentVariables(String fileName) {
            int start = 0;
            StringBuffer result = new StringBuffer();
            if (fileName.startsWith("~")) {
                start = 1;
                result.append(System.getProperty("user.home", "~"));
            }
            Matcher matcher = VARIABLE.matcher(fileName.substring(start));
            while (matcher.find()) {
                String key = matcher.group(1);
                String value = (String)ENV.get(key);
                if (value == null) {
                    value = "<" + key + ">";
                }
                matcher.appendReplacement(result, value);
            }
            matcher.appendTail(result);
            return result.toString();
        }
    }
}
