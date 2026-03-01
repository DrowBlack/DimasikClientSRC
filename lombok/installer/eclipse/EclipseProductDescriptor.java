package lombok.installer.eclipse;

import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

public interface EclipseProductDescriptor {
    public String getProductName();

    public String getWindowsExecutableName();

    public String getUnixAppName();

    public String getMacAppName();

    public String getDirectoryName();

    public List<String> getExecutableNames();

    public List<String> getSourceDirsOnWindows();

    public List<String> getSourceDirsOnMac();

    public List<String> getSourceDirsOnUnix();

    public String getIniFileName();

    public Pattern getLocationSelectors();

    public URL getIdeIcon();
}
