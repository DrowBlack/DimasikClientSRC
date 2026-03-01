package lombok.delombok;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.comp.Todo;
import com.sun.tools.javac.file.BaseFileManager;
import com.sun.tools.javac.main.Arguments;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.zwitserloot.cmdreader.CmdReader;
import com.zwitserloot.cmdreader.Description;
import com.zwitserloot.cmdreader.Excludes;
import com.zwitserloot.cmdreader.FullName;
import com.zwitserloot.cmdreader.InvalidCommandLineException;
import com.zwitserloot.cmdreader.Mandatory;
import com.zwitserloot.cmdreader.Sequential;
import com.zwitserloot.cmdreader.Shorthand;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Pattern;
import javax.annotation.processing.AbstractProcessor;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import lombok.Lombok;
import lombok.delombok.DelombokResult;
import lombok.delombok.DocCommentIntegrator;
import lombok.delombok.FormatPreferences;
import lombok.delombok.LombokOptionsFactory;
import lombok.delombok.UnicodeEscapeWriter;
import lombok.javac.CommentCatcher;
import lombok.javac.Javac;
import lombok.javac.JavacAugments;
import lombok.javac.LombokOptions;
import lombok.javac.apt.LombokProcessor;
import lombok.permit.Permit;

public class Delombok {
    private Charset charset = Charset.defaultCharset();
    private Context context = new Context();
    private Writer presetWriter;
    private PrintStream feedback = System.err;
    private boolean verbose;
    private boolean noCopy;
    private boolean onlyChanged;
    private boolean force = false;
    private boolean disablePreview;
    private String classpath;
    private String sourcepath;
    private String bootclasspath;
    private String modulepath;
    private LinkedHashMap<File, File> fileToBase = new LinkedHashMap();
    private java.util.List<File> filesToParse = new ArrayList<File>();
    private Map<String, String> formatPrefs = new HashMap<String, String>();
    private java.util.List<AbstractProcessor> additionalAnnotationProcessors = new ArrayList<AbstractProcessor>();
    private File output = null;
    private static final Field MODULE_FIELD;
    private static Method attributeMethod;
    private static Method flowMethod;

    static {
        LombokProcessor.addOpensForLombok();
        MODULE_FIELD = Delombok.getModuleField();
    }

    public void setWriter(Writer writer) {
        this.presetWriter = writer;
    }

    private static String indentAndWordbreak(String in, int indent, int maxLen) {
        StringBuilder out = new StringBuilder();
        StringBuilder line = new StringBuilder();
        StringBuilder word = new StringBuilder();
        int len = in.length();
        int i = 0;
        while (i < len + 1) {
            char c;
            char c2 = c = i == len ? (char)' ' : (char)in.charAt(i);
            if (c == ' ') {
                if (line.length() + word.length() < maxLen) {
                    line.append((CharSequence)word);
                } else {
                    if (out.length() > 0) {
                        out.append("\n");
                    }
                    int j = 0;
                    while (j < indent) {
                        out.append(" ");
                        ++j;
                    }
                    out.append((CharSequence)line);
                    line.setLength(0);
                    line.append(word.toString().trim());
                }
                word.setLength(0);
            }
            if (i < len) {
                word.append(c);
            }
            ++i;
        }
        if (line.length() > 0) {
            if (out.length() > 0) {
                out.append("\n");
            }
            int j = 0;
            while (j < indent) {
                out.append(" ");
                ++j;
            }
            out.append((CharSequence)line);
        }
        return out.toString();
    }

    static String getPathOfSelf() {
        String url = Delombok.class.getResource("Delombok.class").toString();
        if (url.endsWith("lombok/delombok/Delombok.class")) {
            url = Delombok.urlDecode(url.substring(0, url.length() - "lombok/delombok/Delombok.class".length()));
        } else if (url.endsWith("lombok/delombok/Delombok.SCL.lombok")) {
            url = Delombok.urlDecode(url.substring(0, url.length() - "lombok/delombok/Delombok.SCL.lombok".length()));
        } else {
            return null;
        }
        if (url.startsWith("jar:file:") && url.endsWith("!/")) {
            return url.substring(9, url.length() - 2);
        }
        if (url.startsWith("file:")) {
            return url.substring(5);
        }
        return null;
    }

    private static String urlDecode(String in) {
        try {
            return URLDecoder.decode(in, "UTF-8");
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new InternalError("UTF-8 not supported");
        }
    }

    public static void main(String[] rawArgs) {
        block32: {
            CmdArgs args;
            try {
                rawArgs = Delombok.fileExpand(rawArgs);
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
            CmdReader<CmdArgs> reader = CmdReader.of(CmdArgs.class);
            try {
                args = reader.make(rawArgs);
            }
            catch (InvalidCommandLineException e) {
                System.err.println("ERROR: " + e.getMessage());
                System.err.println(Delombok.cmdHelp(reader));
                System.exit(1);
                return;
            }
            if (args.help || args.input.isEmpty() && !args.formatHelp) {
                if (!args.help) {
                    System.err.println("ERROR: no files or directories to delombok specified.");
                }
                System.err.println(Delombok.cmdHelp(reader));
                System.exit(args.help ? 0 : 1);
                return;
            }
            Delombok delombok = new Delombok();
            if (args.quiet) {
                delombok.setFeedback(new PrintStream(new OutputStream(){

                    @Override
                    public void write(int b) throws IOException {
                    }
                }));
            }
            if (args.formatHelp) {
                System.out.println("Available format keys (to use, -f key:value -f key2:value2 -f ... ):");
                for (Map.Entry<String, String> e : FormatPreferences.getKeysAndDescriptions().entrySet()) {
                    System.out.print("  ");
                    System.out.print(e.getKey());
                    System.out.println(":");
                    System.out.println(Delombok.indentAndWordbreak(e.getValue(), 4, 70));
                }
                System.out.println("Example: -f indent:4 -f emptyLines:indent");
                System.out.println("The '-f pretty' option is shorthand for '-f suppressWarnings:skip -f generated:skip -f danceAroundIdeChecks:skip -f generateDelombokComment:skip -f javaLangAsFQN:skip'");
                System.exit(0);
                return;
            }
            try {
                delombok.setFormatPreferences(Delombok.formatOptionsToMap(args.format));
            }
            catch (InvalidFormatOptionException e) {
                System.out.println(String.valueOf(e.getMessage()) + " Try --format-help.");
                System.exit(1);
                return;
            }
            if (args.encoding != null) {
                try {
                    delombok.setCharset(args.encoding);
                }
                catch (UnsupportedCharsetException unsupportedCharsetException) {
                    System.err.println("ERROR: Not a known charset: " + args.encoding);
                    System.exit(1);
                    return;
                }
            }
            if (args.verbose) {
                delombok.setVerbose(true);
            }
            if (args.nocopy || args.onlyChanged) {
                delombok.setNoCopy(true);
            }
            if (args.disablePreview) {
                delombok.setDisablePreview(true);
            }
            if (args.onlyChanged) {
                delombok.setOnlyChanged(true);
            }
            if (args.print) {
                delombok.setOutputToStandardOut();
            } else {
                delombok.setOutput(new File(args.target));
            }
            if (args.classpath != null) {
                delombok.setClasspath(args.classpath);
            }
            if (args.sourcepath != null) {
                delombok.setSourcepath(args.sourcepath);
            }
            if (args.bootclasspath != null) {
                delombok.setBootclasspath(args.bootclasspath);
            }
            if (args.modulepath != null) {
                delombok.setModulepath(args.modulepath);
            }
            try {
                for (String in : args.input) {
                    File f = new File(in).getAbsoluteFile();
                    if (f.isFile()) {
                        delombok.addFile(f.getParentFile(), f.getName());
                        continue;
                    }
                    if (f.isDirectory()) {
                        delombok.addDirectory(f);
                        continue;
                    }
                    if (!f.exists()) {
                        if (args.quiet) continue;
                        System.err.println("WARNING: does not exist - skipping: " + f);
                        continue;
                    }
                    if (args.quiet) continue;
                    System.err.println("WARNING: not a standard file or directory - skipping: " + f);
                }
                delombok.delombok();
            }
            catch (Exception e) {
                if (args.quiet) break block32;
                String msg = e.getMessage();
                if (msg != null && msg.startsWith("DELOMBOK: ")) {
                    System.err.println(msg.substring("DELOMBOK: ".length()));
                } else {
                    e.printStackTrace();
                }
                System.exit(1);
                return;
            }
        }
    }

    private static String cmdHelp(CmdReader<CmdArgs> reader) {
        String x = reader.generateCommandLineHelp("delombok");
        int idx = x.indexOf(10);
        return String.valueOf(x.substring(0, idx)) + "\n You can use @filename.args to read arguments from the file 'filename.args'.\n" + x.substring(idx);
    }

    private static String[] fileExpand(String[] rawArgs) throws IOException {
        String[] out = rawArgs;
        int offset = 0;
        int i = 0;
        while (i < rawArgs.length) {
            if (rawArgs[i].length() > 0 && rawArgs[i].charAt(0) == '@') {
                String[] parts = Delombok.readArgsFromFile(rawArgs[i].substring(1));
                String[] newOut = new String[out.length + parts.length - 1];
                System.arraycopy(out, 0, newOut, 0, i + offset);
                System.arraycopy(parts, 0, newOut, i + offset, parts.length);
                System.arraycopy(out, i + offset + 1, newOut, i + offset + parts.length, out.length - (i + offset + 1));
                offset += parts.length - 1;
                out = newOut;
            }
            ++i;
        }
        return out;
    }

    private static String[] readArgsFromFile(String file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        StringBuilder s = new StringBuilder();
        try {
            InputStreamReader isr = new InputStreamReader((InputStream)in, "UTF-8");
            try {
                int r;
                char[] c = new char[4096];
                while ((r = isr.read(c)) != -1) {
                    s.append(c, 0, r);
                }
            }
            finally {
                isr.close();
            }
        }
        finally {
            ((InputStream)in).close();
        }
        ArrayList<String> x = new ArrayList<String>();
        StringBuilder a = new StringBuilder();
        int state = 1;
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (state < 0) {
                state = -state;
                if (c != '\n') {
                    a.append(c);
                }
            } else if (state == 1) {
                if (c == '\\') {
                    state = -1;
                } else if (c == '\"') {
                    state = 2;
                } else if (c == '\'') {
                    state = 3;
                } else if (Character.isWhitespace(c)) {
                    String aa = a.toString();
                    if (!aa.isEmpty()) {
                        x.add(aa);
                    }
                    a.setLength(0);
                } else {
                    a.append(c);
                }
            } else if (state == 2) {
                if (c == '\\') {
                    state = -2;
                } else if (c == '\"') {
                    state = 1;
                    x.add(a.toString());
                    a.setLength(0);
                } else {
                    a.append(c);
                }
            } else if (state == 3) {
                if (c == '\'') {
                    state = 1;
                    x.add(a.toString());
                    a.setLength(0);
                } else {
                    a.append(c);
                }
            }
            ++i;
        }
        if (state == 1) {
            String aa = a.toString();
            if (!aa.isEmpty()) {
                x.add(aa);
            }
        } else {
            if (state < 0) {
                throw new IOException("Unclosed backslash escape in @ file");
            }
            if (state == 2) {
                throw new IOException("Unclosed \" in @ file");
            }
            if (state == 3) {
                throw new IOException("Unclosed ' in @ file");
            }
        }
        return x.toArray(new String[0]);
    }

    public static Map<String, String> formatOptionsToMap(java.util.List<String> formatOptions) throws InvalidFormatOptionException {
        boolean prettyEnabled = false;
        HashMap<String, String> formatPrefs = new HashMap<String, String>();
        for (String format : formatOptions) {
            int idx = format.indexOf(58);
            if (idx == -1) {
                if (format.equalsIgnoreCase("pretty")) {
                    prettyEnabled = true;
                    continue;
                }
                throw new InvalidFormatOptionException("Format keys need to be 2 values separated with a colon.");
            }
            String key = format.substring(0, idx);
            String value = format.substring(idx + 1);
            boolean valid = false;
            for (String k : FormatPreferences.getKeysAndDescriptions().keySet()) {
                if (!k.equalsIgnoreCase(key)) continue;
                valid = true;
                break;
            }
            if (!valid) {
                throw new InvalidFormatOptionException("Unknown format key: '" + key + "'.");
            }
            formatPrefs.put(key.toLowerCase(), value);
        }
        if (prettyEnabled) {
            if (!formatPrefs.containsKey("suppresswarnings")) {
                formatPrefs.put("suppresswarnings", "skip");
            }
            if (!formatPrefs.containsKey("generated")) {
                formatPrefs.put("generated", "skip");
            }
            if (!formatPrefs.containsKey("dancearoundidechecks")) {
                formatPrefs.put("dancearoundidechecks", "skip");
            }
            if (!formatPrefs.containsKey("generatedelombokcomment")) {
                formatPrefs.put("generatedelombokcomment", "skip");
            }
            if (!formatPrefs.containsKey("javalangasfqn")) {
                formatPrefs.put("javalangasfqn", "skip");
            }
        }
        return formatPrefs;
    }

    public void setFormatPreferences(Map<String, String> prefs) {
        this.formatPrefs = prefs;
    }

    public void setCharset(String charsetName) throws UnsupportedCharsetException {
        if (charsetName == null) {
            this.charset = Charset.defaultCharset();
            return;
        }
        this.charset = Charset.forName(charsetName);
    }

    public void setDiagnosticsListener(DiagnosticListener<JavaFileObject> diagnostics) {
        if (diagnostics != null) {
            this.context.put(DiagnosticListener.class, diagnostics);
        }
    }

    public void setForceProcess(boolean force) {
        this.force = force;
    }

    public void setFeedback(PrintStream feedback) {
        this.feedback = feedback;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    public void setSourcepath(String sourcepath) {
        this.sourcepath = sourcepath;
    }

    public void setBootclasspath(String bootclasspath) {
        this.bootclasspath = bootclasspath;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setNoCopy(boolean noCopy) {
        this.noCopy = noCopy;
    }

    public void setDisablePreview(boolean disablePreview) {
        this.disablePreview = disablePreview;
    }

    public void setOnlyChanged(boolean onlyChanged) {
        this.onlyChanged = onlyChanged;
    }

    public void setOutput(File dir) {
        if (dir.isFile() || !dir.isDirectory() && dir.getName().endsWith(".java")) {
            throw new IllegalArgumentException("DELOMBOK: delombok will only write to a directory. If you want to delombok a single file, use -p to output to standard output, then redirect this to a file:\ndelombok MyJavaFile.java -p >MyJavaFileDelombok.java");
        }
        this.output = dir;
    }

    public void setOutputToStandardOut() {
        this.output = null;
    }

    public void setModulepath(String modulepath) {
        this.modulepath = modulepath;
    }

    public void addDirectory(File base) throws IOException {
        this.addDirectory0(false, base, "", 0);
    }

    public void addDirectory1(boolean copy, File base, String name) throws IOException {
        File f = new File(base, name);
        if (f.isFile()) {
            String extension = Delombok.getExtension(f);
            if (extension.equals("java")) {
                this.addFile(base, name);
            } else if (extension.equals("class")) {
                this.skipClass(name);
            } else {
                this.copy(copy, base, name);
            }
        } else if (!f.exists()) {
            this.feedback.printf("Skipping %s because it does not exist.\n", Delombok.canonical(f));
        } else if (!f.isDirectory()) {
            this.feedback.printf("Skipping %s because it is a special file type.\n", Delombok.canonical(f));
        }
    }

    private void addDirectory0(boolean inHiddenDir, File base, String suffix, int loop) throws IOException {
        File dir;
        File file = dir = suffix.isEmpty() ? base : new File(base, suffix);
        if (dir.isDirectory()) {
            boolean thisDirIsHidden;
            boolean bl = thisDirIsHidden = !inHiddenDir && new File(Delombok.canonical(dir)).getName().startsWith(".");
            if (loop >= 100) {
                this.feedback.printf("Over 100 subdirectories? I'm guessing there's a loop in your directory structure. Skipping: %s\n", suffix);
            } else {
                File[] list = dir.listFiles();
                if (list.length > 0) {
                    if (thisDirIsHidden && !this.noCopy && this.output != null) {
                        this.feedback.printf("Only processing java files (not copying non-java files) in %s because it's a hidden directory.\n", Delombok.canonical(dir));
                    }
                    File[] fileArray = list;
                    int n = list.length;
                    int n2 = 0;
                    while (n2 < n) {
                        File f = fileArray[n2];
                        this.addDirectory0(inHiddenDir || thisDirIsHidden, base, String.valueOf(suffix) + (suffix.isEmpty() ? "" : File.separator) + f.getName(), loop + 1);
                        ++n2;
                    }
                } else if (!(thisDirIsHidden || this.noCopy || inHiddenDir || this.output == null || suffix.isEmpty())) {
                    File emptyDir = new File(this.output, suffix);
                    emptyDir.mkdirs();
                    if (this.verbose) {
                        this.feedback.printf("Creating empty directory: %s\n", Delombok.canonical(emptyDir));
                    }
                }
            }
        } else {
            this.addDirectory1(!inHiddenDir && !this.noCopy, base, suffix);
        }
    }

    private void skipClass(String fileName) {
        if (this.verbose) {
            this.feedback.printf("Skipping class file: %s\n", fileName);
        }
    }

    private void copy(boolean copy, File base, String fileName) throws IOException {
        if (this.output == null) {
            this.feedback.printf("Skipping resource file: %s\n", fileName);
            return;
        }
        if (!copy) {
            if (this.verbose) {
                this.feedback.printf("Skipping resource file: %s\n", fileName);
            }
            return;
        }
        if (this.verbose) {
            this.feedback.printf("Copying resource file: %s\n", fileName);
        }
        byte[] b = new byte[65536];
        File inFile = new File(base, fileName);
        FileInputStream in = new FileInputStream(inFile);
        try {
            File outFile = new File(this.output, fileName);
            outFile.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(outFile);
            try {
                int r;
                while ((r = in.read(b)) != -1) {
                    out.write(b, 0, r);
                }
            }
            finally {
                out.close();
            }
        }
        finally {
            in.close();
        }
    }

    public void addFile(File base, String fileName) throws IOException {
        if (this.output != null && Delombok.canonical(base).equals(Delombok.canonical(this.output))) {
            throw new IOException("DELOMBOK: Output file and input file refer to the same filesystem location. Specify a separate path for output.");
        }
        File f = new File(base, fileName);
        this.filesToParse.add(f);
        this.fileToBase.put(f, base);
    }

    public void addAdditionalAnnotationProcessor(AbstractProcessor processor) {
        this.additionalAnnotationProcessors.add(processor);
    }

    private static <T> List<T> toJavacList(java.util.List<T> list) {
        List<T> out = List.nil();
        ListIterator<T> li = list.listIterator(list.size());
        while (li.hasPrevious()) {
            out = out.prepend(li.previous());
        }
        return out;
    }

    private static Field getModuleField() {
        try {
            return Permit.getField(JCTree.JCCompilationUnit.class, "modle");
        }
        catch (NoSuchFieldException noSuchFieldException) {
            return null;
        }
        catch (SecurityException securityException) {
            return null;
        }
    }

    public boolean delombok() throws IOException {
        JavaCompiler delegate;
        JavaFileManager jfm_;
        LombokOptions options = LombokOptionsFactory.getDelombokOptions(this.context);
        options.deleteLombokAnnotations();
        options.putJavacOption("ENCODING", this.charset.name());
        if (this.classpath != null) {
            options.putJavacOption("CLASSPATH", this.unpackClasspath(this.classpath));
        }
        if (this.sourcepath != null) {
            options.putJavacOption("SOURCEPATH", this.sourcepath);
        }
        if (this.bootclasspath != null) {
            options.putJavacOption("BOOTCLASSPATH", this.unpackClasspath(this.bootclasspath));
        }
        options.setFormatPreferences(new FormatPreferences(this.formatPrefs));
        options.put("compilePolicy", "check");
        if (Javac.getJavaCompilerVersion() >= 9) {
            String pathToSelfJar;
            Arguments args = Arguments.instance(this.context);
            ArrayList<String> argsList = new ArrayList<String>();
            if (this.classpath != null) {
                argsList.add("--class-path");
                argsList.add(options.get("--class-path"));
            }
            if (this.sourcepath != null) {
                argsList.add("--source-path");
                argsList.add(options.get("--source-path"));
            }
            if (this.bootclasspath != null) {
                argsList.add("--boot-class-path");
                argsList.add(options.get("--boot-class-path"));
            }
            if (this.charset != null) {
                argsList.add("-encoding");
                argsList.add(this.charset.name());
            }
            if ((pathToSelfJar = Delombok.getPathOfSelf()) != null) {
                argsList.add("--module-path");
                argsList.add(this.modulepath == null || this.modulepath.isEmpty() ? pathToSelfJar : String.valueOf(pathToSelfJar) + File.pathSeparator + this.modulepath);
            } else if (this.modulepath != null && !this.modulepath.isEmpty()) {
                argsList.add("--module-path");
                argsList.add(this.modulepath);
            }
            if (!this.disablePreview && Javac.getJavaCompilerVersion() >= 11) {
                argsList.add("--enable-preview");
            }
            if (Javac.getJavaCompilerVersion() >= 21) {
                argsList.add("-proc:full");
            }
            if (Javac.getJavaCompilerVersion() < 15) {
                String[] argv = argsList.toArray(new String[0]);
                args.init("javac", argv);
            } else {
                args.init("javac", argsList);
            }
            options.put("diags.legacy", "TRUE");
            options.put("allowStringFolding", "FALSE");
        } else if (this.modulepath != null && !this.modulepath.isEmpty()) {
            throw new IllegalStateException("DELOMBOK: Option --module-path requires usage of JDK9 or higher.");
        }
        CommentCatcher catcher = CommentCatcher.create(this.context, Javac.getJavaCompilerVersion() >= 13);
        JavaCompiler compiler = catcher.getCompiler();
        ArrayList<JCTree.JCCompilationUnit> roots = new ArrayList<JCTree.JCCompilationUnit>();
        IdentityHashMap<JCTree.JCCompilationUnit, File> baseMap = new IdentityHashMap<JCTree.JCCompilationUnit, File>();
        LinkedHashSet<AbstractProcessor> processors = new LinkedHashSet<AbstractProcessor>();
        processors.add(new LombokProcessor());
        processors.addAll(this.additionalAnnotationProcessors);
        if (Javac.getJavaCompilerVersion() >= 9 && (jfm_ = this.context.get(JavaFileManager.class)) instanceof BaseFileManager) {
            Arguments args = Arguments.instance(this.context);
            ((BaseFileManager)jfm_).setContext(this.context);
            ((BaseFileManager)jfm_).handleOptions(args.getDeferredFileManagerOptions());
        }
        if (Javac.getJavaCompilerVersion() < 9) {
            compiler.initProcessAnnotations(processors);
        } else {
            compiler.initProcessAnnotations(processors, Collections.emptySet(), Collections.<String>emptySet());
        }
        Symbol.ModuleSymbol unnamedModule = null;
        if (Javac.getJavaCompilerVersion() >= 9) {
            unnamedModule = Symtab.instance((Context)this.context).unnamedModule;
        }
        for (File fileToParse : this.filesToParse) {
            JCTree.JCCompilationUnit unit = compiler.parse(fileToParse.getAbsolutePath());
            if (Javac.getJavaCompilerVersion() >= 9) {
                try {
                    MODULE_FIELD.set(unit, unnamedModule);
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            baseMap.put(unit, this.fileToBase.get(fileToParse));
            roots.add(unit);
        }
        if (compiler.errorCount() > 0) {
            return false;
        }
        for (JCTree.JCCompilationUnit unit : roots) {
            catcher.setComments(unit, new DocCommentIntegrator().integrate(catcher.getComments(unit), unit));
        }
        if (Javac.getJavaCompilerVersion() >= 9) {
            compiler.initModules(List.from(roots.toArray(new JCTree.JCCompilationUnit[0])));
        }
        List<JCTree.JCCompilationUnit> trees = compiler.enterTrees(Delombok.toJavacList(roots));
        if (Javac.getJavaCompilerVersion() < 9) {
            delegate = compiler.processAnnotations(trees, List.nil());
        } else {
            delegate = compiler;
            List<String> c = List.nil();
            compiler.processAnnotations(trees, c);
        }
        Object care = Delombok.callAttributeMethodOnJavaCompiler(delegate, delegate.todo);
        Delombok.callFlowMethodOnJavaCompiler(delegate, care);
        FormatPreferences fps = new FormatPreferences(this.formatPrefs);
        for (JCTree.JCCompilationUnit unit : roots) {
            DelombokResult result = new DelombokResult(catcher.getComments(unit), catcher.getTextBlockStarts(unit), unit, this.force || options.isChanged(unit), fps);
            if (this.onlyChanged && !result.isChanged() && !options.isChanged(unit)) {
                if (!this.verbose) continue;
                this.feedback.printf("File: %s [%s]\n", unit.sourcefile.getName(), "unchanged (skipped)");
                continue;
            }
            ListBuffer<JCTree> newDefs = new ListBuffer<JCTree>();
            for (JCTree def : unit.defs) {
                if (def instanceof JCTree.JCImport) {
                    Boolean b = JavacAugments.JCImport_deletable.get((JCTree.JCImport)def);
                    if (b != null && b.booleanValue()) continue;
                    newDefs.append(def);
                    continue;
                }
                newDefs.append(def);
            }
            unit.defs = newDefs.toList();
            if (this.verbose) {
                this.feedback.printf("File: %s [%s%s]\n", unit.sourcefile.getName(), result.isChanged() ? "delomboked" : "unchanged", this.force && !options.isChanged(unit) ? " (forced)" : "");
            }
            Writer rawWriter = this.presetWriter != null ? this.createUnicodeEscapeWriter(this.presetWriter) : (this.output == null ? this.createStandardOutWriter() : this.createFileWriter(this.output, (File)baseMap.get(unit), unit.sourcefile.toUri()));
            BufferedWriter writer = new BufferedWriter(rawWriter);
            try {
                result.print(writer);
            }
            finally {
                if (this.output != null) {
                    writer.close();
                } else {
                    writer.flush();
                }
            }
        }
        delegate.close();
        return true;
    }

    private String unpackClasspath(String cp) {
        String[] parts = cp.split(Pattern.quote(File.pathSeparator));
        StringBuilder out = new StringBuilder();
        String[] stringArray = parts;
        int n = parts.length;
        int n2 = 0;
        while (n2 < n) {
            String p = stringArray[n2];
            if (!p.endsWith("*")) {
                if (out.length() > 0) {
                    out.append(File.pathSeparator);
                }
                out.append(p);
            } else {
                File f = new File(p.substring(0, p.length() - 2));
                File[] files = f.listFiles();
                if (files != null) {
                    File[] fileArray = files;
                    int n3 = files.length;
                    int n4 = 0;
                    while (n4 < n3) {
                        File file = fileArray[n4];
                        if (file.isFile()) {
                            if (out.length() > 0) {
                                out.append(File.pathSeparator);
                            }
                            out.append(p, 0, p.length() - 1);
                            out.append(file.getName());
                        }
                        ++n4;
                    }
                }
            }
            ++n2;
        }
        return out.toString();
    }

    private static Object callAttributeMethodOnJavaCompiler(JavaCompiler compiler, Todo arg) {
        if (attributeMethod == null) {
            try {
                attributeMethod = Permit.getMethod(JavaCompiler.class, "attribute", Queue.class);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                try {
                    attributeMethod = Permit.getMethod(JavaCompiler.class, "attribute", ListBuffer.class);
                }
                catch (NoSuchMethodException e2) {
                    throw Lombok.sneakyThrow(e2);
                }
            }
        }
        return Permit.invokeSneaky(attributeMethod, compiler, arg);
    }

    private static void callFlowMethodOnJavaCompiler(JavaCompiler compiler, Object arg) {
        if (flowMethod == null) {
            try {
                flowMethod = Permit.getMethod(JavaCompiler.class, "flow", Queue.class);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                try {
                    flowMethod = Permit.getMethod(JavaCompiler.class, "flow", List.class);
                }
                catch (NoSuchMethodException e2) {
                    throw Lombok.sneakyThrow(e2);
                }
            }
        }
        Permit.invokeSneaky(flowMethod, compiler, arg);
    }

    private static String canonical(File dir) {
        try {
            return dir.getCanonicalPath();
        }
        catch (Exception exception) {
            return dir.getAbsolutePath();
        }
    }

    private static String getExtension(File dir) {
        String name = dir.getName();
        int idx = name.lastIndexOf(46);
        return idx == -1 ? "" : name.substring(idx + 1);
    }

    private Writer createFileWriter(File outBase, File inBase, URI file) throws IOException {
        URI base = inBase.toURI();
        URI relative = base.relativize(base.resolve(file));
        File outFile = relative.isAbsolute() ? new File(outBase, new File(relative).getName()) : new File(outBase, relative.getPath());
        outFile.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(outFile);
        return this.createUnicodeEscapeWriter(out);
    }

    private Writer createStandardOutWriter() {
        return this.createUnicodeEscapeWriter(System.out);
    }

    private Writer createUnicodeEscapeWriter(Writer writer) {
        return new UnicodeEscapeWriter(writer, this.charset);
    }

    private Writer createUnicodeEscapeWriter(OutputStream out) {
        return new UnicodeEscapeWriter(new OutputStreamWriter(out, this.charset), this.charset);
    }

    private static class CmdArgs {
        @Shorthand(value={"v"})
        @Description(value="Print the name of each file as it is being delombok-ed.")
        @Excludes(value={"quiet"})
        private boolean verbose;
        @Shorthand(value={"f"})
        @Description(value="Sets formatting rules. Use --format-help to list all available rules. Unset format rules are inferred by scanning the source for usages.")
        private java.util.List<String> format = new ArrayList<String>();
        @FullName(value="format-help")
        private boolean formatHelp;
        @Shorthand(value={"q"})
        @Description(value="No warnings or errors will be emitted to standard error")
        @Excludes(value={"verbose"})
        private boolean quiet;
        @Shorthand(value={"e"})
        @Description(value="Sets the encoding of your source files. Defaults to the system default charset. Example: \"UTF-8\"")
        private String encoding;
        @Shorthand(value={"p"})
        @Description(value="Print delombok-ed code to standard output instead of saving it in target directory")
        private boolean print;
        @Shorthand(value={"d"})
        @Description(value="Directory to save delomboked files to")
        @Mandatory(onlyIfNot={"print", "help", "format-help"})
        private String target;
        @Shorthand(value={"c"})
        @Description(value="Classpath (analogous to javac -cp option)")
        private String classpath;
        @Shorthand(value={"s"})
        @Description(value="Sourcepath (analogous to javac -sourcepath option)")
        private String sourcepath;
        @Description(value="override Bootclasspath (analogous to javac -bootclasspath option)")
        private String bootclasspath;
        @Description(value="Module path (analogous to javac --module-path option)")
        @FullName(value="module-path")
        private String modulepath;
        @Description(value="Files to delombok. Provide either a file, or a directory. If you use a directory, all files in it (recursive) are delombok-ed")
        @Sequential
        private java.util.List<String> input = new ArrayList<String>();
        @Description(value="Lombok will only delombok source files. Without this option, non-java, non-class files are copied to the target directory.")
        @Shorthand(value={"n"})
        private boolean nocopy;
        @Description(value="Output only changed files (implies -n)")
        private boolean onlyChanged;
        @Description(value="By default lombok enables preview features if available (introduced in JDK 12). With this option, lombok won't do that.")
        @FullName(value="disable-preview")
        private boolean disablePreview;
        private boolean help;

        private CmdArgs() {
        }
    }

    public static class InvalidFormatOptionException
    extends Exception {
        public InvalidFormatOptionException(String msg) {
            super(msg);
        }
    }
}
