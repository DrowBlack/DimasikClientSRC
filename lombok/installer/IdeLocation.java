package lombok.installer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import lombok.installer.InstallException;
import lombok.installer.OsUtils;
import lombok.installer.UninstallException;
import lombok.patcher.ClassRootFinder;

public abstract class IdeLocation {
    boolean selected = true;

    public abstract String install() throws InstallException;

    public abstract void uninstall() throws UninstallException;

    public abstract String getName();

    public abstract boolean hasLombok();

    public abstract URL getIdeIcon();

    public static File findOurJar() {
        return new File(ClassRootFinder.findClassRootOfClass(OsUtils.class));
    }

    public String toString() {
        return this.getName();
    }

    public static String canonical(File p) {
        try {
            return p.getCanonicalPath();
        }
        catch (IOException iOException) {
            String x = p.getAbsolutePath();
            return x == null ? p.getPath() : x;
        }
    }
}
