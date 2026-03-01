package lombok.javac;

import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import lombok.javac.CapturingDiagnosticListener;
import lombok.javac.JavacResolution;
import lombok.permit.Permit;

public final class CompilerMessageSuppressor {
    private final Log log;
    private static final WriterField errWriterField;
    private static final WriterField warnWriterField;
    private static final WriterField noticeWriterField;
    private static final Field dumpOnErrorField;
    private static final Field promptOnErrorField;
    private static final Field diagnosticListenerField;
    private static final Field deferDiagnosticsField;
    private static final Field deferredDiagnosticsField;
    private static final Field diagnosticHandlerField;
    private static final ConcurrentMap<Class<?>, Field> handlerDeferredFields;
    private static final Field NULL_FIELD;
    private Boolean dumpOnError;
    private Boolean promptOnError;
    private DiagnosticListener<?> contextDiagnosticListener;
    private DiagnosticListener<?> logDiagnosticListener;
    private final Context context;
    private static final ThreadLocal<Queue<?>> queueCache;

    static {
        handlerDeferredFields = new ConcurrentHashMap();
        queueCache = new ThreadLocal();
        errWriterField = CompilerMessageSuppressor.createWriterField(Writers.ERROR);
        warnWriterField = CompilerMessageSuppressor.createWriterField(Writers.WARNING);
        noticeWriterField = CompilerMessageSuppressor.createWriterField(Writers.NOTICE);
        dumpOnErrorField = CompilerMessageSuppressor.getDeclaredField(Log.class, "dumpOnError");
        promptOnErrorField = CompilerMessageSuppressor.getDeclaredField(Log.class, "promptOnError");
        diagnosticListenerField = CompilerMessageSuppressor.getDeclaredField(Log.class, "diagListener");
        deferDiagnosticsField = CompilerMessageSuppressor.getDeclaredField(Log.class, "deferDiagnostics");
        deferredDiagnosticsField = CompilerMessageSuppressor.getDeclaredField(Log.class, "deferredDiagnostics");
        diagnosticHandlerField = CompilerMessageSuppressor.getDeclaredField(Log.class, "diagnosticHandler");
        NULL_FIELD = CompilerMessageSuppressor.getDeclaredField(JavacResolution.class, "NULL_FIELD");
    }

    static Field getDeclaredField(Class<?> c, String fieldName) {
        try {
            return Permit.getField(c, fieldName);
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    public CompilerMessageSuppressor(Context context) {
        this.log = Log.instance(context);
        this.context = context;
    }

    public void disableLoggers() {
        this.contextDiagnosticListener = this.context.get(DiagnosticListener.class);
        this.context.put(DiagnosticListener.class, null);
        errWriterField.pauze(this.log);
        warnWriterField.pauze(this.log);
        noticeWriterField.pauze(this.log);
        if (deferDiagnosticsField != null) {
            try {
                if (Boolean.TRUE.equals(deferDiagnosticsField.get(this.log))) {
                    queueCache.set((Queue)deferredDiagnosticsField.get(this.log));
                    LinkedList empty = new LinkedList();
                    deferredDiagnosticsField.set(this.log, empty);
                }
            }
            catch (Exception exception) {}
        }
        if (diagnosticHandlerField != null) {
            try {
                Object handler = diagnosticHandlerField.get(this.log);
                Field field = CompilerMessageSuppressor.getDeferredField(handler);
                if (field != null) {
                    queueCache.set((Queue)field.get(handler));
                    LinkedList empty = new LinkedList();
                    field.set(handler, empty);
                }
            }
            catch (Exception exception) {}
        }
        if (dumpOnErrorField != null) {
            try {
                this.dumpOnError = (Boolean)dumpOnErrorField.get(this.log);
                dumpOnErrorField.set(this.log, false);
            }
            catch (Exception exception) {}
        }
        if (promptOnErrorField != null) {
            try {
                this.promptOnError = (Boolean)promptOnErrorField.get(this.log);
                promptOnErrorField.set(this.log, false);
            }
            catch (Exception exception) {}
        }
        if (diagnosticListenerField != null) {
            try {
                this.logDiagnosticListener = (DiagnosticListener)diagnosticListenerField.get(this.log);
                diagnosticListenerField.set(this.log, null);
            }
            catch (Exception exception) {}
        }
    }

    private static Field getDeferredField(Object handler) {
        Class<?> key = handler.getClass();
        Field field = (Field)handlerDeferredFields.get(key);
        if (field != null) {
            return field == NULL_FIELD ? null : field;
        }
        Field value = CompilerMessageSuppressor.getDeclaredField(key, "deferred");
        handlerDeferredFields.put(key, value == null ? NULL_FIELD : value);
        return CompilerMessageSuppressor.getDeferredField(handler);
    }

    public void enableLoggers() {
        if (this.contextDiagnosticListener != null) {
            this.context.put(DiagnosticListener.class, this.contextDiagnosticListener);
            this.contextDiagnosticListener = null;
        }
        errWriterField.resume(this.log);
        warnWriterField.resume(this.log);
        noticeWriterField.resume(this.log);
        if (this.dumpOnError != null) {
            try {
                dumpOnErrorField.set(this.log, this.dumpOnError);
                this.dumpOnError = null;
            }
            catch (Exception exception) {}
        }
        if (this.promptOnError != null) {
            try {
                promptOnErrorField.set(this.log, this.promptOnError);
                this.promptOnError = null;
            }
            catch (Exception exception) {}
        }
        if (this.logDiagnosticListener != null) {
            try {
                diagnosticListenerField.set(this.log, this.logDiagnosticListener);
                this.logDiagnosticListener = null;
            }
            catch (Exception exception) {}
        }
        if (diagnosticHandlerField != null && queueCache.get() != null) {
            try {
                Object handler = diagnosticHandlerField.get(this.log);
                Field field = CompilerMessageSuppressor.getDeferredField(handler);
                if (field != null) {
                    field.set(handler, queueCache.get());
                    queueCache.set(null);
                }
            }
            catch (Exception exception) {}
        }
        if (deferDiagnosticsField != null && queueCache.get() != null) {
            try {
                deferredDiagnosticsField.set(this.log, queueCache.get());
                queueCache.set(null);
            }
            catch (Exception exception) {}
        }
    }

    public void removeAllBetween(JavaFileObject sourcefile, int startPos, int endPos) {
        DiagnosticListener listener = this.context.get(DiagnosticListener.class);
        if (listener instanceof CapturingDiagnosticListener) {
            ((CapturingDiagnosticListener)listener).suppress(startPos, endPos);
        }
        Field field = null;
        Object receiver = null;
        if (deferDiagnosticsField != null) {
            try {
                if (Boolean.TRUE.equals(deferDiagnosticsField.get(this.log))) {
                    field = deferredDiagnosticsField;
                    receiver = this.log;
                }
            }
            catch (Exception exception) {}
        }
        if (diagnosticHandlerField != null) {
            try {
                Object handler = diagnosticHandlerField.get(this.log);
                field = CompilerMessageSuppressor.getDeferredField(handler);
                receiver = handler;
            }
            catch (Exception exception) {}
        }
        if (field == null || receiver == null) {
            return;
        }
        try {
            ListBuffer deferredDiagnostics = (ListBuffer)field.get(receiver);
            ListBuffer newDeferredDiagnostics = new ListBuffer();
            for (Object diag_ : deferredDiagnostics) {
                if (!(diag_ instanceof JCDiagnostic)) {
                    newDeferredDiagnostics.append(diag_);
                    continue;
                }
                JCDiagnostic diag = (JCDiagnostic)diag_;
                long here = diag.getStartPosition();
                if (here >= (long)startPos && here < (long)endPos && diag.getSource() == sourcefile) continue;
                newDeferredDiagnostics.append(diag);
            }
            field.set(receiver, newDeferredDiagnostics);
        }
        catch (Exception exception) {}
    }

    private static WriterField createWriterField(Writers w) {
        try {
            Field writers = CompilerMessageSuppressor.getDeclaredField(Log.class, "writer");
            if (writers != null) {
                Class<?> kindsClass = Class.forName("com.sun.tools.javac.util.Log$WriterKind");
                ?[] objArray = kindsClass.getEnumConstants();
                int n = objArray.length;
                int n2 = 0;
                while (n2 < n) {
                    Object enumConstant = objArray[n2];
                    if (enumConstant.toString().equals(w.keyName)) {
                        return new Java9WriterField(writers, enumConstant);
                    }
                    ++n2;
                }
                return WriterField.NONE;
            }
        }
        catch (Exception exception) {}
        Field writerField = CompilerMessageSuppressor.getDeclaredField(Log.class, w.fieldName);
        if (writerField != null) {
            return new Java8WriterField(writerField);
        }
        return WriterField.NONE;
    }

    static class Java8WriterField
    implements WriterField {
        private final Field field;
        private PrintWriter writer;

        public Java8WriterField(Field field) {
            this.field = field;
        }

        @Override
        public void pauze(Log log) {
            try {
                this.writer = (PrintWriter)this.field.get(log);
                this.field.set(log, NO_WRITER);
            }
            catch (Exception exception) {}
        }

        @Override
        public void resume(Log log) {
            if (this.writer != null) {
                try {
                    this.field.set(log, this.writer);
                }
                catch (Exception exception) {}
            }
            this.writer = null;
        }
    }

    static class Java9WriterField
    implements WriterField {
        private final Field field;
        private final Object key;
        private PrintWriter writer;

        public Java9WriterField(Field field, Object key) {
            this.field = field;
            this.key = key;
        }

        @Override
        public void pauze(Log log) {
            try {
                Map map = (Map)this.field.get(log);
                this.writer = (PrintWriter)map.get(this.key);
                map.put(this.key, NO_WRITER);
            }
            catch (Exception exception) {}
        }

        @Override
        public void resume(Log log) {
            if (this.writer != null) {
                try {
                    Map map = (Map)this.field.get(log);
                    map.put(this.key, this.writer);
                }
                catch (Exception exception) {}
            }
            this.writer = null;
        }
    }

    static interface WriterField {
        public static final PrintWriter NO_WRITER = new PrintWriter(new OutputStream(){

            @Override
            public void write(int b) throws IOException {
            }
        });
        public static final WriterField NONE = new WriterField(){

            @Override
            public void pauze(Log log) {
            }

            @Override
            public void resume(Log log) {
            }
        };

        public void pauze(Log var1);

        public void resume(Log var1);
    }

    static enum Writers {
        ERROR("errWriter", "ERROR"),
        WARNING("warnWriter", "WARNING"),
        NOTICE("noticeWriter", "NOTICE");

        final String fieldName;
        final String keyName;

        private Writers(String fieldName, String keyName) {
            this.fieldName = fieldName;
            this.keyName = keyName;
        }
    }
}
