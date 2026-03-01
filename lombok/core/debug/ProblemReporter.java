package lombok.core.debug;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

public class ProblemReporter {
    private static ErrorLogger logger;

    public static void info(String msg, Throwable ex) {
        ProblemReporter.init();
        try {
            logger.info(msg, ex);
        }
        catch (Throwable throwable) {
            logger = new TerminalLogger();
            logger.info(msg, ex);
        }
    }

    public static void warning(String msg, Throwable ex) {
        ProblemReporter.init();
        try {
            logger.warning(msg, ex);
        }
        catch (Throwable throwable) {
            logger = new TerminalLogger();
            logger.warning(msg, ex);
        }
    }

    public static void error(String msg, Throwable ex) {
        ProblemReporter.init();
        try {
            logger.error(msg, ex);
        }
        catch (Throwable throwable) {
            logger = new TerminalLogger();
            logger.error(msg, ex);
        }
    }

    private static void init() {
        if (logger != null) {
            return;
        }
        try {
            logger = new EclipseWorkspaceLogger();
        }
        catch (Throwable throwable) {
            logger = new TerminalLogger();
        }
    }

    private static class EclipseWorkspaceLogger
    implements ErrorLogger {
        private static final String DEFAULT_BUNDLE_NAME = "org.eclipse.jdt.core";
        private static final Bundle bundle;
        private static final int MAX_LOG = 200;
        private static final long SQUELCH_TIMEOUT;
        private static final AtomicInteger counter;
        private static volatile long squelchTimeout;

        static {
            SQUELCH_TIMEOUT = TimeUnit.HOURS.toMillis(1L);
            counter = new AtomicInteger();
            squelchTimeout = 0L;
            bundle = Platform.getBundle((String)DEFAULT_BUNDLE_NAME);
            if (bundle == null) {
                throw new NoClassDefFoundError();
            }
        }

        private EclipseWorkspaceLogger() {
        }

        @Override
        public void info(String message, Throwable error) {
            this.msg(1, message, error);
        }

        @Override
        public void warning(String message, Throwable error) {
            this.msg(2, message, error);
        }

        @Override
        public void error(String message, Throwable error) {
            this.msg(4, message, error);
        }

        private void msg(int msgType, String message, Throwable error) {
            int ct = squelchTimeout != 0L ? 0 : counter.incrementAndGet();
            boolean printSquelchWarning = false;
            if (squelchTimeout != 0L) {
                long now = System.currentTimeMillis();
                if (squelchTimeout > now) {
                    return;
                }
                squelchTimeout = now + SQUELCH_TIMEOUT;
                printSquelchWarning = true;
            } else if (ct >= 200) {
                squelchTimeout = System.currentTimeMillis() + SQUELCH_TIMEOUT;
                printSquelchWarning = true;
            }
            ILog log = Platform.getLog((Bundle)bundle);
            log.log((IStatus)new Status(msgType, DEFAULT_BUNDLE_NAME, message, error));
            if (printSquelchWarning) {
                log.log((IStatus)new Status(2, DEFAULT_BUNDLE_NAME, "Lombok has logged too many messages; to avoid memory issues, further lombok logs will be squelched for a while. Restart eclipse to start over."));
            }
        }
    }

    private static interface ErrorLogger {
        public void info(String var1, Throwable var2);

        public void warning(String var1, Throwable var2);

        public void error(String var1, Throwable var2);
    }

    private static class TerminalLogger
    implements ErrorLogger {
        private TerminalLogger() {
        }

        @Override
        public void info(String message, Throwable ex) {
            System.err.println(message);
            if (ex != null) {
                ex.printStackTrace();
            }
        }

        @Override
        public void warning(String message, Throwable ex) {
            System.err.println(message);
            if (ex != null) {
                ex.printStackTrace();
            }
        }

        @Override
        public void error(String message, Throwable ex) {
            System.err.println(message);
            if (ex != null) {
                ex.printStackTrace();
            }
        }
    }
}
