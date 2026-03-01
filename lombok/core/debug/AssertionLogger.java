package lombok.core.debug;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.core.Version;

public class AssertionLogger {
    private static final String LOG_PATH;
    private static final AtomicBoolean loggedIntro;
    private static final String PROCESS_ID;
    private static final String ID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    static {
        String log = System.getProperty("lombok.assertion.log", null);
        if (log != null) {
            LOG_PATH = log.isEmpty() ? null : log;
        } else {
            try {
                log = System.getenv("LOMBOK_ASSERTION_LOG");
            }
            catch (Exception exception) {
                log = null;
            }
            LOG_PATH = log == null || log.isEmpty() ? null : log;
        }
        loggedIntro = new AtomicBoolean(false);
        PROCESS_ID = AssertionLogger.generateProcessId();
    }

    private static String generateProcessId() {
        char[] ID = new char[4];
        Random r = new Random();
        int i = 0;
        while (i < ID.length) {
            ID[i] = ID_CHARS.charAt(r.nextInt(ID_CHARS.length()));
            ++i;
        }
        return new String(ID);
    }

    private static synchronized void logToFile(String msg) {
        if (msg == null) {
            return;
        }
        try {
            FileOutputStream out = new FileOutputStream(LOG_PATH, true);
            ((OutputStream)out).write(msg.getBytes("UTF-8"));
            ((OutputStream)out).close();
        }
        catch (Exception e) {
            throw new RuntimeException("assertion logging can't write to log file", e);
        }
    }

    private static void logIntro() {
        String version;
        if (loggedIntro.getAndSet(true)) {
            return;
        }
        try {
            version = Version.getFullVersion();
        }
        catch (Exception exception) {
            version = Version.getVersion();
        }
        AssertionLogger.logToFile(String.format("{%s} [%s -- START %s]\n", PROCESS_ID, new Date(), version));
    }

    public static <T extends Throwable> T assertLog(String message, T throwable) {
        if (LOG_PATH == null) {
            return throwable;
        }
        AssertionLogger.logIntro();
        if (message == null) {
            message = "(No message)";
        }
        String stackMsg = "";
        if (throwable != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            pw.close();
            stackMsg = "\n  " + sw.toString().replace("\r", "").replace("\n", "\n  ").trim();
        }
        AssertionLogger.logToFile(String.format("{%s} [%ty%<tm%<tdT%<tH%<tM%<tS.%<tL] %s%s\n", PROCESS_ID, new Date(), message, stackMsg));
        return throwable;
    }

    public static void assertLog(String message) {
        if (LOG_PATH == null) {
            return;
        }
        AssertionLogger.assertLog(message, null);
    }
}
