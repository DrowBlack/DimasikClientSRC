package lombok.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.core.DiagnosticsReceiver;
import lombok.core.PostCompilerTransformation;
import lombok.core.SpiLoadUtil;

public final class PostCompiler {
    private static List<PostCompilerTransformation> transformations;

    private PostCompiler() {
    }

    public static byte[] applyTransformations(byte[] original, String fileName, DiagnosticsReceiver diagnostics) {
        if (System.getProperty("lombok.disablePostCompiler", null) != null) {
            return original;
        }
        PostCompiler.init(diagnostics);
        byte[] previous = original;
        for (PostCompilerTransformation transformation : transformations) {
            try {
                byte[] next = transformation.applyTransformations(previous, fileName, diagnostics);
                if (next == null) continue;
                previous = next;
            }
            catch (Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter((Writer)sw, true));
                diagnostics.addError(String.format("Error during the transformation of '%s'; post-compiler '%s' caused an exception: %s", fileName, transformation.getClass().getName(), sw));
            }
        }
        return previous;
    }

    private static synchronized void init(DiagnosticsReceiver diagnostics) {
        if (transformations != null) {
            return;
        }
        try {
            transformations = SpiLoadUtil.readAllFromIterator(SpiLoadUtil.findServices(PostCompilerTransformation.class, PostCompilerTransformation.class.getClassLoader()));
        }
        catch (IOException e) {
            transformations = Collections.emptyList();
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter((Writer)sw, true));
            diagnostics.addError("Could not load post-compile transformers: " + e.getMessage() + "\n" + sw.toString());
        }
    }

    public static OutputStream wrapOutputStream(final OutputStream originalStream, final String fileName, final DiagnosticsReceiver diagnostics) throws IOException {
        if (System.getProperty("lombok.disablePostCompiler", null) != null) {
            return originalStream;
        }
        final AtomicBoolean closed = new AtomicBoolean();
        return new ByteArrayOutputStream(){

            @Override
            public void close() throws IOException {
                if (closed.getAndSet(true)) {
                    originalStream.close();
                    return;
                }
                byte[] original = this.toByteArray();
                byte[] copy = null;
                if (original.length > 0) {
                    try {
                        copy = PostCompiler.applyTransformations(original, fileName, diagnostics);
                    }
                    catch (Exception exception) {
                        diagnostics.addWarning(String.format("Error during the transformation of '%s'; no post-compilation has been applied", fileName));
                    }
                }
                if (copy == null) {
                    copy = original;
                }
                originalStream.write(copy);
                originalStream.close();
            }
        };
    }
}
