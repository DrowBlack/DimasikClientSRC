package lombok.javac;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

public class CapturingDiagnosticListener
implements DiagnosticListener<JavaFileObject> {
    private final File file;
    private final Collection<CompilerMessage> messages;

    public CapturingDiagnosticListener(File file, Collection<CompilerMessage> messages) {
        this.file = file;
        this.messages = messages;
    }

    @Override
    public void report(Diagnostic<? extends JavaFileObject> d) {
        String msg = d.getMessage(Locale.ENGLISH);
        Matcher m = Pattern.compile("^" + Pattern.quote(this.file.getAbsolutePath()) + "\\s*:\\s*\\d+\\s*:\\s*(?:warning:\\s*)?(.*)$", 32).matcher(msg);
        if (m.matches()) {
            msg = m.group(1);
        }
        if (msg.equals("deprecated item is not annotated with @Deprecated")) {
            return;
        }
        this.messages.add(new CompilerMessage(d.getLineNumber(), d.getStartPosition(), d.getKind() == Diagnostic.Kind.ERROR, msg));
    }

    public void suppress(int start, int end) {
        Iterator<CompilerMessage> it = this.messages.iterator();
        while (it.hasNext()) {
            long pos = it.next().getPosition();
            if (pos < (long)start || pos >= (long)end) continue;
            it.remove();
        }
    }

    public static final class CompilerMessage {
        private final long line;
        private final long position;
        private final boolean isError;
        private final String message;

        public CompilerMessage(long line, long position, boolean isError, String message) {
            this.line = line;
            this.position = position;
            this.isError = isError;
            this.message = message;
        }

        public long getLine() {
            return this.line;
        }

        public long getPosition() {
            return this.position;
        }

        public boolean isError() {
            return this.isError;
        }

        public String getMessage() {
            return this.message;
        }

        public int hashCode() {
            int result = 1;
            result = 31 * result + (this.isError ? 1231 : 1237);
            result = 31 * result + (int)(this.line ^ this.line >>> 32);
            result = 31 * result + (this.message == null ? 0 : this.message.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            CompilerMessage other = (CompilerMessage)obj;
            if (this.isError != other.isError) {
                return false;
            }
            if (this.line != other.line) {
                return false;
            }
            return !(this.message == null ? other.message != null : !this.message.equals(other.message));
        }

        public String toString() {
            return String.format("%d %s %s", this.line, this.isError ? "ERROR" : "WARNING", this.message);
        }
    }
}
