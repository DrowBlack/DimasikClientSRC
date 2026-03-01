package lombok.installer.eclipse;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import lombok.installer.OsUtils;
import lombok.installer.eclipse.EclipseProductDescriptor;

public class StandardProductDescriptor
implements EclipseProductDescriptor {
    private static final String USER_HOME = System.getProperty("user.home", ".");
    private static final String[] BASE_WINDOWS_ROOTS = new String[]{"\\", "\\Program Files", "\\Program Files (x86)", "\\ProgramData\\Chocolatey\\lib"};
    private static final String[] WINDOWS_ROOTS = StandardProductDescriptor.windowsRoots();
    private static final String[] MAC_ROOTS = new String[]{"/Applications", USER_HOME};
    private static final String[] UNIX_ROOTS = new String[]{USER_HOME};
    private final String productName;
    private final String windowsName;
    private final String unixName;
    private final String macAppName;
    private final List<String> executableNames;
    private final List<String> sourceDirsOnWindows;
    private final List<String> sourceDirsOnMac;
    private final List<String> sourceDirsOnUnix;
    private final String iniFileName;
    private final Pattern locationSelectors;
    private final String directoryName;
    private final URL ideIcon;

    public StandardProductDescriptor(String productName, String baseName, String directoryName, URL ideIcon, Collection<String> alternativeDirectoryNames) {
        this.productName = productName;
        this.windowsName = String.valueOf(baseName) + ".exe";
        this.unixName = baseName;
        this.macAppName = String.valueOf(baseName) + ".app";
        this.executableNames = StandardProductDescriptor.executableNames(baseName);
        this.sourceDirsOnWindows = StandardProductDescriptor.generateAlternatives(WINDOWS_ROOTS, "\\", alternativeDirectoryNames);
        this.sourceDirsOnMac = StandardProductDescriptor.generateAlternatives(MAC_ROOTS, "/", alternativeDirectoryNames);
        this.sourceDirsOnUnix = StandardProductDescriptor.generateAlternatives(UNIX_ROOTS, "/", alternativeDirectoryNames);
        this.iniFileName = String.valueOf(baseName) + ".ini";
        this.locationSelectors = StandardProductDescriptor.getLocationSelectors(baseName);
        this.directoryName = directoryName.toLowerCase();
        this.ideIcon = ideIcon;
    }

    @Override
    public String getProductName() {
        return this.productName;
    }

    @Override
    public String getWindowsExecutableName() {
        return this.windowsName;
    }

    @Override
    public String getUnixAppName() {
        return this.unixName;
    }

    @Override
    public String getMacAppName() {
        return this.macAppName;
    }

    @Override
    public String getDirectoryName() {
        return this.directoryName;
    }

    @Override
    public List<String> getExecutableNames() {
        return this.executableNames;
    }

    @Override
    public List<String> getSourceDirsOnWindows() {
        return this.sourceDirsOnWindows;
    }

    @Override
    public List<String> getSourceDirsOnMac() {
        return this.sourceDirsOnMac;
    }

    @Override
    public List<String> getSourceDirsOnUnix() {
        return this.sourceDirsOnUnix;
    }

    @Override
    public String getIniFileName() {
        return this.iniFileName;
    }

    @Override
    public Pattern getLocationSelectors() {
        return this.locationSelectors;
    }

    @Override
    public URL getIdeIcon() {
        return this.ideIcon;
    }

    private static Pattern getLocationSelectors(String baseName) {
        return Pattern.compile(String.format(StandardProductDescriptor.platformPattern(), baseName.toLowerCase()), 2);
    }

    private static String platformPattern() {
        switch (OsUtils.getOS()) {
            case MAC_OS_X: {
                return "^(%s|%<s\\.ini|%<s\\.app)$";
            }
            case WINDOWS: {
                return "^(%sc?\\.exe|%<s\\.ini)$";
            }
        }
        return "^(%s|%<s\\.ini)$";
    }

    private static List<String> executableNames(String baseName) {
        String base = baseName.toLowerCase();
        return Collections.unmodifiableList(Arrays.asList(base, String.valueOf(base) + ".app", String.valueOf(base) + ".exe", String.valueOf(base) + "c.exe"));
    }

    private static List<String> generateAlternatives(String[] roots, String pathSeparator, Collection<String> alternatives) {
        ArrayList<String> result = new ArrayList<String>();
        String[] stringArray = roots;
        int n = roots.length;
        int n2 = 0;
        while (n2 < n) {
            String root = stringArray[n2];
            result.add(StandardProductDescriptor.concat(root, pathSeparator, ""));
            for (String alternative : alternatives) {
                result.add(StandardProductDescriptor.concat(root, pathSeparator, alternative));
            }
            ++n2;
        }
        return Collections.unmodifiableList(result);
    }

    private static String concat(String base, String pathSeparator, String alternative) {
        if (alternative.isEmpty()) {
            return base;
        }
        if (base.endsWith(pathSeparator)) {
            return String.valueOf(base) + alternative.replaceAll("[\\/]", "\\" + pathSeparator);
        }
        return String.valueOf(base) + pathSeparator + alternative.replaceAll("[\\/]", "\\" + pathSeparator);
    }

    private static String[] windowsRoots() {
        String localAppData = StandardProductDescriptor.windowsLocalAppData();
        String[] out = new String[BASE_WINDOWS_ROOTS.length + (localAppData == null ? 1 : 2)];
        System.arraycopy(BASE_WINDOWS_ROOTS, 0, out, 0, BASE_WINDOWS_ROOTS.length);
        out[StandardProductDescriptor.BASE_WINDOWS_ROOTS.length] = USER_HOME;
        if (localAppData != null) {
            out[StandardProductDescriptor.BASE_WINDOWS_ROOTS.length + 1] = localAppData;
        }
        return out;
    }

    private static String windowsLocalAppData() {
        String localAppData = System.getenv("LOCALAPPDATA");
        File file = localAppData == null ? null : new File(localAppData);
        return file != null && file.exists() && file.canRead() && file.isDirectory() ? localAppData : null;
    }
}
