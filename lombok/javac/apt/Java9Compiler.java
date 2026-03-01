package lombok.javac.apt;

import com.sun.tools.javac.file.BaseFileManager;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import lombok.javac.apt.Javac9BaseFileObjectWrapper;
import lombok.javac.apt.Javac9JavaFileObjectWrapper;
import lombok.javac.apt.LombokFileObject;
import lombok.javac.apt.LombokFileObjects;

class Java9Compiler
implements LombokFileObjects.Compiler {
    private final BaseFileManager fileManager;

    public Java9Compiler(JavaFileManager jfm) {
        this.fileManager = Java9Compiler.asBaseFileManager(jfm);
    }

    @Override
    public JavaFileObject wrap(LombokFileObject fileObject) {
        Path p;
        try {
            p = Java9Compiler.toPath(fileObject);
        }
        catch (Exception exception) {
            p = null;
        }
        if (p != null) {
            return new Javac9BaseFileObjectWrapper(this.fileManager, p, fileObject);
        }
        return new Javac9JavaFileObjectWrapper(fileObject);
    }

    @Override
    public Method getDecoderMethod() {
        return null;
    }

    private static Path toPath(LombokFileObject fileObject) {
        URI uri = fileObject.toUri();
        if (uri.getScheme() == null) {
            uri = URI.create("file:///" + uri);
        }
        try {
            return Paths.get(uri);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Problems in URI '" + uri + "' (" + fileObject.toUri() + ")", e);
        }
    }

    private static BaseFileManager asBaseFileManager(JavaFileManager jfm) {
        if (jfm instanceof BaseFileManager) {
            return (BaseFileManager)jfm;
        }
        return new FileManagerWrapper(jfm);
    }

    static class FileManagerWrapper
    extends BaseFileManager {
        JavaFileManager manager;

        public FileManagerWrapper(JavaFileManager manager) {
            super(null);
            this.manager = manager;
        }

        @Override
        public int isSupportedOption(String option) {
            return this.manager.isSupportedOption(option);
        }

        @Override
        public ClassLoader getClassLoader(JavaFileManager.Location location) {
            return this.manager.getClassLoader(location);
        }

        @Override
        public Iterable<JavaFileObject> list(JavaFileManager.Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
            return this.manager.list(location, packageName, kinds, recurse);
        }

        @Override
        public String inferBinaryName(JavaFileManager.Location location, JavaFileObject file) {
            return this.manager.inferBinaryName(location, file);
        }

        @Override
        public boolean isSameFile(FileObject a, FileObject b) {
            return this.manager.isSameFile(a, b);
        }

        @Override
        public boolean handleOption(String current, Iterator<String> remaining) {
            return this.manager.handleOption(current, remaining);
        }

        @Override
        public boolean hasLocation(JavaFileManager.Location location) {
            return this.manager.hasLocation(location);
        }

        @Override
        public JavaFileObject getJavaFileForInput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind) throws IOException {
            return this.manager.getJavaFileForInput(location, className, kind);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            return this.manager.getJavaFileForOutput(location, className, kind, sibling);
        }

        @Override
        public FileObject getFileForInput(JavaFileManager.Location location, String packageName, String relativeName) throws IOException {
            return this.manager.getFileForInput(location, packageName, relativeName);
        }

        @Override
        public FileObject getFileForOutput(JavaFileManager.Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
            return this.manager.getFileForOutput(location, packageName, relativeName, sibling);
        }

        @Override
        public void flush() throws IOException {
            this.manager.flush();
        }

        @Override
        public void close() throws IOException {
            this.manager.close();
        }
    }
}
