package lombok.javac.apt;

import com.sun.tools.javac.file.BaseFileManager;
import com.sun.tools.javac.file.PathFileObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Path;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import lombok.javac.apt.LombokFileObject;

class Javac9BaseFileObjectWrapper
extends PathFileObject {
    private final LombokFileObject delegate;

    public Javac9BaseFileObjectWrapper(BaseFileManager fileManager, Path path, LombokFileObject delegate) {
        super(fileManager, path);
        this.delegate = delegate;
    }

    @Override
    public boolean isNameCompatible(String simpleName, JavaFileObject.Kind kind) {
        return this.delegate.isNameCompatible(simpleName, kind);
    }

    @Override
    public URI toUri() {
        return this.delegate.toUri();
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return this.delegate.getCharContent(ignoreEncodingErrors);
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return this.delegate.openInputStream();
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        return this.delegate.openReader(ignoreEncodingErrors);
    }

    @Override
    public Writer openWriter() throws IOException {
        return this.delegate.openWriter();
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return this.delegate.openOutputStream();
    }

    @Override
    public long getLastModified() {
        return this.delegate.getLastModified();
    }

    @Override
    public boolean delete() {
        return this.delegate.delete();
    }

    @Override
    public JavaFileObject.Kind getKind() {
        return this.delegate.getKind();
    }

    @Override
    public NestingKind getNestingKind() {
        return this.delegate.getNestingKind();
    }

    @Override
    public Modifier getAccessLevel() {
        return this.delegate.getAccessLevel();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Javac9BaseFileObjectWrapper)) {
            return false;
        }
        return this.delegate.equals(((Javac9BaseFileObjectWrapper)obj).delegate);
    }

    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }
}
