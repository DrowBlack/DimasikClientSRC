package lombok.javac.apt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.CharsetDecoder;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import lombok.core.DiagnosticsReceiver;
import lombok.core.PostCompiler;
import lombok.javac.apt.LombokFileObject;
import lombok.permit.Permit;

final class InterceptingJavaFileObject
implements LombokFileObject {
    private final JavaFileObject delegate;
    private final String fileName;
    private final DiagnosticsReceiver diagnostics;
    private final Method decoderMethod;

    public InterceptingJavaFileObject(JavaFileObject original, String fileName, DiagnosticsReceiver diagnostics, Method decoderMethod) {
        this.delegate = original;
        this.fileName = fileName;
        this.diagnostics = diagnostics;
        this.decoderMethod = decoderMethod;
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return PostCompiler.wrapOutputStream(this.delegate.openOutputStream(), this.fileName, this.diagnostics);
    }

    @Override
    public Writer openWriter() throws IOException {
        throw new UnsupportedOperationException("Can't use a write for class files");
    }

    @Override
    public CharsetDecoder getDecoder(boolean ignoreEncodingErrors) {
        if (this.decoderMethod == null) {
            throw new UnsupportedOperationException();
        }
        return (CharsetDecoder)Permit.invokeSneaky(this.decoderMethod, this.delegate, ignoreEncodingErrors);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof InterceptingJavaFileObject)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        InterceptingJavaFileObject other = (InterceptingJavaFileObject)obj;
        return this.fileName.equals(other.fileName) && this.delegate.equals(other.delegate);
    }

    public int hashCode() {
        return this.fileName.hashCode() ^ this.delegate.hashCode();
    }

    @Override
    public boolean delete() {
        return this.delegate.delete();
    }

    @Override
    public Modifier getAccessLevel() {
        return this.delegate.getAccessLevel();
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return this.delegate.getCharContent(ignoreEncodingErrors);
    }

    @Override
    public JavaFileObject.Kind getKind() {
        return this.delegate.getKind();
    }

    @Override
    public long getLastModified() {
        return this.delegate.getLastModified();
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    @Override
    public NestingKind getNestingKind() {
        return this.delegate.getNestingKind();
    }

    @Override
    public boolean isNameCompatible(String simpleName, JavaFileObject.Kind kind) {
        return this.delegate.isNameCompatible(simpleName, kind);
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
    public URI toUri() {
        return this.delegate.toUri();
    }

    public String toString() {
        return this.delegate.toString();
    }
}
