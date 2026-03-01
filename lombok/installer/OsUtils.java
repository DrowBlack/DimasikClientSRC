package lombok.installer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.core.Version;
import lombok.installer.WindowsDriveInfo;

public final class OsUtils {
    private static final AtomicBoolean windowsDriveInfoLibLoaded = new AtomicBoolean(false);

    private OsUtils() {
    }

    private static void loadWindowsDriveInfoLib() throws IOException {
        if (!windowsDriveInfoLibLoaded.compareAndSet(false, true)) {
            return;
        }
        String prefix = "lombok-" + Version.getVersion() + "-";
        File temp = File.createTempFile("lombok", ".mark");
        File dll1 = new File(temp.getParentFile(), String.valueOf(prefix) + "WindowsDriveInfo-i386.dll");
        File dll2 = new File(temp.getParentFile(), String.valueOf(prefix) + "WindowsDriveInfo-x86_64.dll");
        temp.delete();
        dll1.deleteOnExit();
        dll2.deleteOnExit();
        try {
            if (OsUtils.unpackDLL("WindowsDriveInfo-i386.binary", dll1)) {
                System.load(dll1.getAbsolutePath());
                return;
            }
        }
        catch (Throwable throwable) {}
        try {
            if (OsUtils.unpackDLL("WindowsDriveInfo-x86_64.binary", dll2)) {
                System.load(dll2.getAbsolutePath());
            }
        }
        catch (Throwable throwable) {}
    }

    private static boolean unpackDLL(String dllName, File target) throws IOException {
        InputStream in = OsUtils.class.getResourceAsStream(dllName);
        try {
            try {
                FileOutputStream out = new FileOutputStream(target);
                try {
                    int r;
                    byte[] b = new byte[32000];
                    while ((r = in.read(b)) != -1) {
                        out.write(b, 0, r);
                    }
                }
                finally {
                    out.close();
                }
            }
            catch (IOException iOException) {
                boolean bl = target.exists() && target.canRead();
                in.close();
                return bl;
            }
        }
        finally {
            in.close();
        }
        return true;
    }

    public static List<String> getDrivesOnWindows() throws Throwable {
        OsUtils.loadWindowsDriveInfoLib();
        ArrayList<String> drives = new ArrayList<String>();
        WindowsDriveInfo info = new WindowsDriveInfo();
        for (String drive : info.getLogicalDrives()) {
            if (!info.isFixedDisk(drive)) continue;
            drives.add(drive);
        }
        return drives;
    }

    public static OS getOS() {
        String prop = System.getProperty("os.name", "").toLowerCase();
        if (prop.matches("^.*\\bmac\\b.*$")) {
            return OS.MAC_OS_X;
        }
        if (prop.matches("^.*\\bdarwin\\b.*$")) {
            return OS.MAC_OS_X;
        }
        if (prop.matches("^.*\\bwin(dows|32|64)?\\b.*$")) {
            return OS.WINDOWS;
        }
        return OS.UNIX;
    }

    public static enum OS {
        MAC_OS_X("\n"),
        WINDOWS("\r\n"),
        UNIX("\n");

        private final String lineEnding;

        private OS(String lineEnding) {
            this.lineEnding = lineEnding;
        }

        public String getLineEnding() {
            return this.lineEnding;
        }
    }
}
