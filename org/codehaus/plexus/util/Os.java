package org.codehaus.plexus.util;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Os {
    public static final String FAMILY_DOS = "dos";
    public static final String FAMILY_MAC = "mac";
    public static final String FAMILY_NETWARE = "netware";
    public static final String FAMILY_OS2 = "os/2";
    public static final String FAMILY_TANDEM = "tandem";
    public static final String FAMILY_UNIX = "unix";
    public static final String FAMILY_WINDOWS = "windows";
    public static final String FAMILY_WIN9X = "win9x";
    public static final String FAMILY_ZOS = "z/os";
    public static final String FAMILY_OS400 = "os/400";
    public static final String FAMILY_OPENVMS = "openvms";
    private static final Set<String> validFamilies = Os.setValidFamilies();
    private static final String PATH_SEP = System.getProperty("path.separator");
    public static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.US);
    public static final String OS_ARCH = System.getProperty("os.arch").toLowerCase(Locale.US);
    public static final String OS_VERSION = System.getProperty("os.version").toLowerCase(Locale.US);
    public static final String OS_FAMILY = Os.getOsFamily();
    private String family;
    private String name;
    private String version;
    private String arch;

    public Os() {
    }

    public Os(String family) {
        this.setFamily(family);
    }

    private static Set<String> setValidFamilies() {
        HashSet<String> valid = new HashSet<String>();
        valid.add(FAMILY_DOS);
        valid.add(FAMILY_MAC);
        valid.add(FAMILY_NETWARE);
        valid.add(FAMILY_OS2);
        valid.add(FAMILY_TANDEM);
        valid.add(FAMILY_UNIX);
        valid.add(FAMILY_WINDOWS);
        valid.add(FAMILY_WIN9X);
        valid.add(FAMILY_ZOS);
        valid.add(FAMILY_OS400);
        valid.add(FAMILY_OPENVMS);
        return valid;
    }

    public void setFamily(String f) {
        this.family = f.toLowerCase(Locale.US);
    }

    public void setName(String name) {
        this.name = name.toLowerCase(Locale.US);
    }

    public void setArch(String arch) {
        this.arch = arch.toLowerCase(Locale.US);
    }

    public void setVersion(String version) {
        this.version = version.toLowerCase(Locale.US);
    }

    public boolean eval() throws Exception {
        return Os.isOs(this.family, this.name, this.arch, this.version);
    }

    public static boolean isFamily(String family) {
        return Os.isOs(family, null, null, null);
    }

    public static boolean isName(String name) {
        return Os.isOs(null, name, null, null);
    }

    public static boolean isArch(String arch) {
        return Os.isOs(null, null, arch, null);
    }

    public static boolean isVersion(String version) {
        return Os.isOs(null, null, null, version);
    }

    public static boolean isOs(String family, String name, String arch, String version) {
        boolean retValue = false;
        if (family != null || name != null || arch != null || version != null) {
            boolean isFamily = true;
            boolean isName = true;
            boolean isArch = true;
            boolean isVersion = true;
            if (family != null) {
                isFamily = family.equalsIgnoreCase(FAMILY_WINDOWS) ? OS_NAME.contains(FAMILY_WINDOWS) : (family.equalsIgnoreCase(FAMILY_OS2) ? OS_NAME.contains(FAMILY_OS2) : (family.equalsIgnoreCase(FAMILY_NETWARE) ? OS_NAME.contains(FAMILY_NETWARE) : (family.equalsIgnoreCase(FAMILY_DOS) ? PATH_SEP.equals(";") && !Os.isFamily(FAMILY_NETWARE) && !Os.isFamily(FAMILY_WINDOWS) && !Os.isFamily(FAMILY_WIN9X) : (family.equalsIgnoreCase(FAMILY_MAC) ? OS_NAME.contains(FAMILY_MAC) : (family.equalsIgnoreCase(FAMILY_TANDEM) ? OS_NAME.contains("nonstop_kernel") : (family.equalsIgnoreCase(FAMILY_UNIX) ? PATH_SEP.equals(":") && !Os.isFamily(FAMILY_OPENVMS) && (!Os.isFamily(FAMILY_MAC) || OS_NAME.endsWith("x")) : (family.equalsIgnoreCase(FAMILY_WIN9X) ? Os.isFamily(FAMILY_WINDOWS) && (OS_NAME.contains("95") || OS_NAME.contains("98") || OS_NAME.contains("me") || OS_NAME.contains("ce")) : (family.equalsIgnoreCase(FAMILY_ZOS) ? OS_NAME.contains(FAMILY_ZOS) || OS_NAME.contains("os/390") : (family.equalsIgnoreCase(FAMILY_OS400) ? OS_NAME.contains(FAMILY_OS400) : (family.equalsIgnoreCase(FAMILY_OPENVMS) ? OS_NAME.contains(FAMILY_OPENVMS) : OS_NAME.contains(family.toLowerCase(Locale.US))))))))))));
            }
            if (name != null) {
                isName = name.toLowerCase(Locale.US).equals(OS_NAME);
            }
            if (arch != null) {
                isArch = arch.toLowerCase(Locale.US).equals(OS_ARCH);
            }
            if (version != null) {
                isVersion = version.toLowerCase(Locale.US).equals(OS_VERSION);
            }
            retValue = isFamily && isName && isArch && isVersion;
        }
        return retValue;
    }

    private static String getOsFamily() {
        Set<String> families = null;
        families = !validFamilies.isEmpty() ? validFamilies : Os.setValidFamilies();
        for (String fam : families) {
            if (!Os.isFamily(fam)) continue;
            return fam;
        }
        return null;
    }

    public static boolean isValidFamily(String theFamily) {
        return validFamilies.contains(theFamily);
    }

    public static Set<String> getValidFamilies() {
        return new HashSet<String>(validFamilies);
    }
}
