package lombok.installer.eclipse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.installer.CorruptedIdeLocationException;
import lombok.installer.IdeLocation;
import lombok.installer.InstallException;
import lombok.installer.Installer;
import lombok.installer.OsUtils;
import lombok.installer.UninstallException;
import lombok.installer.eclipse.EclipseProductDescriptor;

public final class EclipseProductLocation
extends IdeLocation {
    private static final String OS_NEWLINE = OsUtils.getOS().getLineEnding();
    private final EclipseProductDescriptor descriptor;
    private final String[] name;
    private final File[] eclipseIniPath;
    private final String[] pathToLombokJarPrefix;
    private final boolean[] hasLombok;
    private static final Pattern JAVA_AGENT_LINE_MATCHER = Pattern.compile("^\\-javaagent\\:.*lombok.*\\.jar$", 2);
    private static final Pattern BOOTCLASSPATH_LINE_MATCHER = Pattern.compile("^\\-Xbootclasspath\\/a\\:(.*lombok.*\\.jar.*)$", 2);

    EclipseProductLocation(EclipseProductDescriptor descriptor, String[] nameOfLocation, File[] pathToEclipseIni) throws CorruptedIdeLocationException {
        this.descriptor = descriptor;
        this.name = nameOfLocation;
        this.eclipseIniPath = pathToEclipseIni;
        this.pathToLombokJarPrefix = new String[pathToEclipseIni.length];
        this.hasLombok = new boolean[pathToEclipseIni.length];
        int i = 0;
        while (i < pathToEclipseIni.length) {
            File p1 = pathToEclipseIni[i].getParentFile();
            File p2 = p1 == null ? null : p1.getParentFile();
            File p3 = p2 == null ? null : p2.getParentFile();
            this.pathToLombokJarPrefix[i] = p1 != null && p1.getName().equals("Eclipse") && p2 != null && p2.getName().equals("Contents") && p3 != null && p3.getName().endsWith(".app") ? "../Eclipse/" : "";
            try {
                this.hasLombok[i] = EclipseProductLocation.checkForLombok(this.eclipseIniPath[i]);
            }
            catch (IOException e) {
                throw new CorruptedIdeLocationException("I can't read the configuration file of the " + descriptor.getProductName() + " installed at " + this.name + "\n" + "You may need to run this installer with root privileges if you want to modify that " + descriptor.getProductName() + ".", descriptor.getProductName(), e);
            }
            ++i;
        }
    }

    public int hashCode() {
        return Arrays.hashCode(this.eclipseIniPath);
    }

    public boolean equals(Object o) {
        if (!(o instanceof EclipseProductLocation)) {
            return false;
        }
        return Arrays.deepEquals(((EclipseProductLocation)o).eclipseIniPath, this.eclipseIniPath);
    }

    @Override
    public String getName() {
        return this.name[0];
    }

    @Override
    public boolean hasLombok() {
        int i = 0;
        while (i < this.hasLombok.length) {
            if (!this.hasLombok[i]) {
                return false;
            }
            ++i;
        }
        return true;
    }

    private static boolean checkForLombok(File iniFile) throws IOException {
        if (!iniFile.exists()) {
            return false;
        }
        FileInputStream fis = new FileInputStream(iniFile);
        try {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            while ((line = br.readLine()) != null) {
                if (!JAVA_AGENT_LINE_MATCHER.matcher(line.trim()).matches()) continue;
                br.close();
                return true;
            }
            br.close();
            return false;
        }
        finally {
            fis.close();
        }
    }

    private List<File> getUninstallDirs() {
        HashSet<String> result = new HashSet<String>();
        int i = 0;
        while (i < this.name.length) {
            File x = new File(this.name[i]);
            if (!x.isDirectory()) {
                x = x.getParentFile();
            }
            if (x.isDirectory()) {
                result.add(x.getAbsolutePath());
            }
            result.add(this.eclipseIniPath[i].getAbsolutePath());
            ++i;
        }
        ArrayList<File> out = new ArrayList<File>();
        for (String r : result) {
            out.add(new File(r));
        }
        return out;
    }

    @Override
    public void uninstall() throws UninstallException {
        ArrayList<File> lombokJarsForWhichCantDeleteSelf = new ArrayList<File>();
        StringBuilder newContents = new StringBuilder();
        int i = 0;
        while (i < this.eclipseIniPath.length) {
            if (this.eclipseIniPath[i].exists()) {
                try {
                    FileInputStream fis = new FileInputStream(this.eclipseIniPath[i]);
                    try {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                        while ((line = br.readLine()) != null) {
                            if (JAVA_AGENT_LINE_MATCHER.matcher(line).matches()) continue;
                            Matcher m = BOOTCLASSPATH_LINE_MATCHER.matcher(line);
                            if (m.matches()) {
                                StringBuilder elemBuilder = new StringBuilder();
                                elemBuilder.append("-Xbootclasspath/a:");
                                boolean first = true;
                                String[] stringArray = m.group(1).split(Pattern.quote(File.pathSeparator));
                                int n = stringArray.length;
                                int n2 = 0;
                                while (n2 < n) {
                                    String elem = stringArray[n2];
                                    if (!elem.toLowerCase().endsWith("lombok.jar") && !elem.toLowerCase().endsWith("lombok.eclipse.agent.jar")) {
                                        if (first) {
                                            first = false;
                                        } else {
                                            elemBuilder.append(File.pathSeparator);
                                        }
                                        elemBuilder.append(elem);
                                    }
                                    ++n2;
                                }
                                if (first) continue;
                                newContents.append(elemBuilder.toString()).append(OS_NEWLINE);
                                continue;
                            }
                            newContents.append(line).append(OS_NEWLINE);
                        }
                        br.close();
                    }
                    finally {
                        fis.close();
                    }
                    FileOutputStream fos = new FileOutputStream(this.eclipseIniPath[i]);
                    try {
                        fos.write(newContents.toString().getBytes());
                    }
                    finally {
                        fos.close();
                    }
                }
                catch (IOException e) {
                    throw new UninstallException("Cannot uninstall lombok from " + this.name + EclipseProductLocation.generateWriteErrorMessage(), e);
                }
            }
            for (File dir : this.getUninstallDirs()) {
                File agentJar;
                File lombokJar = new File(dir, "lombok.jar");
                if (lombokJar.exists() && !lombokJar.delete()) {
                    if (OsUtils.getOS() == OsUtils.OS.WINDOWS && Installer.isSelf(lombokJar.getAbsolutePath())) {
                        lombokJarsForWhichCantDeleteSelf.add(lombokJar);
                    } else {
                        throw new UninstallException("Can't delete " + lombokJar.getAbsolutePath() + EclipseProductLocation.generateWriteErrorMessage(), null);
                    }
                }
                if (!(agentJar = new File(dir, "lombok.eclipse.agent.jar")).exists()) continue;
                agentJar.delete();
            }
            if (!lombokJarsForWhichCantDeleteSelf.isEmpty()) {
                throw new UninstallException(true, String.format("lombok.jar cannot delete itself on windows.\nHowever, lombok has been uncoupled from your %s.\nYou can safely delete this jar file. You can find it at:\n%s", this.descriptor.getProductName(), ((File)lombokJarsForWhichCantDeleteSelf.get(0)).getAbsolutePath()), null);
            }
            ++i;
        }
    }

    private static String generateWriteErrorMessage() {
        String osSpecificError;
        switch (OsUtils.getOS()) {
            default: {
                osSpecificError = ":\nStart terminal, go to the directory with lombok.jar, and run: sudo java -jar lombok.jar";
                break;
            }
            case WINDOWS: {
                osSpecificError = ":\nStart a new cmd (dos box) with admin privileges, go to the directory with lombok.jar, and run: java -jar lombok.jar";
            }
        }
        return ", probably because this installer does not have the access rights.\nTry re-running the installer with administrative privileges" + osSpecificError;
    }

    @Override
    public String install() throws InstallException {
        boolean fullPathRequired = !"false".equals(System.getProperty("lombok.installer.fullpath", "true"));
        boolean installSucceeded = false;
        StringBuilder newContents = new StringBuilder();
        int i = 0;
        while (i < this.eclipseIniPath.length) {
            installSucceeded = false;
            File lombokJar = new File(this.eclipseIniPath[i].getParentFile(), "lombok.jar");
            if (!Installer.isSelf(lombokJar.getAbsolutePath())) {
                File ourJar = EclipseProductLocation.findOurJar();
                byte[] b = new byte[524288];
                boolean readSucceeded = true;
                try {
                    FileOutputStream out = new FileOutputStream(lombokJar);
                    try {
                        readSucceeded = false;
                        FileInputStream in = new FileInputStream(ourJar);
                        try {
                            int r;
                            while ((r = ((InputStream)in).read(b)) != -1) {
                                if (r > 0) {
                                    readSucceeded = true;
                                }
                                out.write(b, 0, r);
                            }
                        }
                        finally {
                            ((InputStream)in).close();
                        }
                    }
                    finally {
                        out.close();
                    }
                }
                catch (IOException e) {
                    try {
                        lombokJar.delete();
                    }
                    catch (Throwable throwable) {}
                    if (!readSucceeded) {
                        throw new InstallException("I can't read my own jar file (trying: " + ourJar.toString() + "). I think you've found a bug in this installer!\nI suggest you restart it " + "and use the 'what do I do' link, to manually install lombok. Also, tell us about this at:\n" + "http://groups.google.com/group/project-lombok - Thanks!\n\n[DEBUG INFO] " + e.getClass() + ": " + e.getMessage() + "\nBase: " + OsUtils.class.getResource("OsUtils.class"), e);
                    }
                    throw new InstallException("I can't write to your " + this.descriptor.getProductName() + " directory at " + this.name + EclipseProductLocation.generateWriteErrorMessage(), e);
                }
            }
            new File(lombokJar.getParentFile(), "lombok.eclipse.agent.jar").delete();
            try {
                try {
                    FileInputStream fis = new FileInputStream(this.eclipseIniPath[i]);
                    try {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                        while ((line = br.readLine()) != null) {
                            if (JAVA_AGENT_LINE_MATCHER.matcher(line).matches()) continue;
                            Matcher m = BOOTCLASSPATH_LINE_MATCHER.matcher(line);
                            if (m.matches()) {
                                StringBuilder elemBuilder = new StringBuilder();
                                elemBuilder.append("-Xbootclasspath/a:");
                                boolean first = true;
                                String[] stringArray = m.group(1).split(Pattern.quote(File.pathSeparator));
                                int n = stringArray.length;
                                int n2 = 0;
                                while (n2 < n) {
                                    String elem = stringArray[n2];
                                    if (!elem.toLowerCase().endsWith("lombok.jar") && !elem.toLowerCase().endsWith("lombok.eclipse.agent.jar")) {
                                        if (first) {
                                            first = false;
                                        } else {
                                            elemBuilder.append(File.pathSeparator);
                                        }
                                        elemBuilder.append(elem);
                                    }
                                    ++n2;
                                }
                                if (first) continue;
                                newContents.append(elemBuilder.toString()).append(OS_NEWLINE);
                                continue;
                            }
                            newContents.append(line).append(OS_NEWLINE);
                        }
                        br.close();
                    }
                    finally {
                        fis.close();
                    }
                    String pathPrefix = fullPathRequired ? String.valueOf(lombokJar.getParentFile().getCanonicalPath()) + File.separator : this.pathToLombokJarPrefix[i];
                    newContents.append(String.format("-javaagent:%s", String.valueOf(pathPrefix) + "lombok.jar")).append(OS_NEWLINE);
                    FileOutputStream fos = new FileOutputStream(this.eclipseIniPath[i]);
                    try {
                        fos.write(newContents.toString().getBytes());
                    }
                    finally {
                        fos.close();
                    }
                    installSucceeded = true;
                }
                catch (IOException e) {
                    throw new InstallException("Cannot install lombok at " + this.name + EclipseProductLocation.generateWriteErrorMessage(), e);
                }
            }
            catch (Throwable throwable) {
                if (!installSucceeded) {
                    try {
                        lombokJar.delete();
                    }
                    catch (Throwable throwable2) {}
                }
                throw throwable;
            }
            if (!installSucceeded) {
                try {
                    lombokJar.delete();
                }
                catch (Throwable throwable) {}
            }
            if (!installSucceeded) {
                throw new InstallException("I can't find the " + this.descriptor.getIniFileName() + " file. Is this a real " + this.descriptor.getProductName() + " installation?", null);
            }
            ++i;
        }
        return "If you start " + this.descriptor.getProductName() + " with a custom -vm parameter, you'll need to add:<br>" + "<code>-vmargs -javaagent:lombok.jar</code><br>as parameter as well.";
    }

    @Override
    public URL getIdeIcon() {
        return this.descriptor.getIdeIcon();
    }
}
