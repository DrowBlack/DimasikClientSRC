package lombok.installer;

import com.zwitserloot.cmdreader.CmdReader;
import com.zwitserloot.cmdreader.Description;
import com.zwitserloot.cmdreader.InvalidCommandLineException;
import com.zwitserloot.cmdreader.Sequential;
import com.zwitserloot.cmdreader.Shorthand;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import lombok.Lombok;
import lombok.core.LombokApp;
import lombok.core.SpiLoadUtil;
import lombok.core.Version;
import lombok.installer.CorruptedIdeLocationException;
import lombok.installer.IdeLocation;
import lombok.installer.IdeLocationProvider;
import lombok.installer.InstallException;
import lombok.installer.InstallerGUI;
import lombok.installer.OsUtils;
import lombok.installer.UninstallException;
import lombok.patcher.ClassRootFinder;

public class Installer {
    static final URI ABOUT_LOMBOK_URL = URI.create("https://projectlombok.org");
    static final List<IdeLocationProvider> locationProviders;

    static {
        ArrayList<IdeLocationProvider> list = new ArrayList<IdeLocationProvider>();
        try {
            for (IdeLocationProvider provider : SpiLoadUtil.findServices(IdeLocationProvider.class)) {
                list.add(provider);
            }
        }
        catch (IOException e) {
            throw Lombok.sneakyThrow(e);
        }
        locationProviders = Collections.unmodifiableList(list);
    }

    static List<Pattern> getIdeExecutableNames() {
        ArrayList<Pattern> list = new ArrayList<Pattern>();
        for (IdeLocationProvider provider : locationProviders) {
            Pattern p = provider.getLocationSelectors();
            if (p == null) continue;
            list.add(p);
        }
        return list;
    }

    static IdeLocation tryAllProviders(String location) throws CorruptedIdeLocationException {
        for (IdeLocationProvider provider : locationProviders) {
            IdeLocation loc = provider.create(location);
            if (loc == null) continue;
            return loc;
        }
        return null;
    }

    static void autoDiscover(List<IdeLocation> locations, List<CorruptedIdeLocationException> problems) {
        for (IdeLocationProvider provider : locationProviders) {
            provider.findIdes(locations, problems);
        }
    }

    public static boolean isSelf(String jar) {
        String self = ClassRootFinder.findClassRootOfClass(Installer.class);
        if (self == null) {
            return false;
        }
        File a = new File(jar).getAbsoluteFile();
        File b = new File(self).getAbsoluteFile();
        try {
            a = a.getCanonicalFile();
        }
        catch (IOException iOException) {}
        try {
            b = b.getCanonicalFile();
        }
        catch (IOException iOException) {}
        return a.equals(b);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static int guiInstaller() {
        if (OsUtils.getOS() == OsUtils.OS.MAC_OS_X) {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Lombok Installer");
            System.setProperty("com.apple.macos.use-file-dialog-packages", "true");
        }
        try {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    try {
                        try {
                            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                        }
                        catch (Exception exception) {}
                        new InstallerGUI().show();
                    }
                    catch (HeadlessException headlessException) {
                        Installer.printHeadlessInfo();
                    }
                }
            });
            AtomicReference<Integer> atomicReference = InstallerGUI.exitMarker;
            synchronized (atomicReference) {
                Integer errCode;
                while (true) {
                    if (Thread.interrupted() || InstallerGUI.exitMarker.get() != null) {
                        errCode = InstallerGUI.exitMarker.get();
                        if (errCode != null) break;
                        return 1;
                    }
                    try {
                        InstallerGUI.exitMarker.wait();
                    }
                    catch (InterruptedException interruptedException) {
                        return 1;
                    }
                }
                int n = errCode;
                return n;
            }
        }
        catch (HeadlessException headlessException) {
            Installer.printHeadlessInfo();
            return 1;
        }
    }

    public static int cliInstaller(boolean uninstall, List<String> rawArgs) {
        CmdArgs args;
        CmdReader<CmdArgs> reader = CmdReader.of(CmdArgs.class);
        try {
            args = reader.make(rawArgs.toArray(new String[0]));
        }
        catch (InvalidCommandLineException e) {
            System.err.println(e.getMessage());
            System.err.println("--------------------------");
            System.err.println(Installer.generateCliHelp(uninstall, reader));
            return 1;
        }
        if (args.help) {
            System.out.println(Installer.generateCliHelp(uninstall, reader));
            return 0;
        }
        if (args.path.isEmpty()) {
            System.err.println("ERROR: Nothing to do!");
            System.err.println("--------------------------");
            System.err.println(Installer.generateCliHelp(uninstall, reader));
            return 1;
        }
        ArrayList<IdeLocation> locations = new ArrayList<IdeLocation>();
        ArrayList<CorruptedIdeLocationException> problems = new ArrayList<CorruptedIdeLocationException>();
        if (args.path.contains("auto")) {
            Installer.autoDiscover(locations, problems);
        }
        for (String rawPath : args.path) {
            if (rawPath.equals("auto")) continue;
            try {
                IdeLocation loc = Installer.tryAllProviders(rawPath);
                if (loc != null) {
                    locations.add(loc);
                    continue;
                }
                problems.add(new CorruptedIdeLocationException("Can't find any IDE at: " + rawPath, null, null));
            }
            catch (CorruptedIdeLocationException e) {
                problems.add(e);
            }
        }
        int validLocations = locations.size();
        for (IdeLocation loc : locations) {
            try {
                if (uninstall) {
                    loc.uninstall();
                } else {
                    loc.install();
                }
                System.out.printf("Lombok %s %s: %s\n", uninstall ? "uninstalled" : "installed", uninstall ? "from" : "to", loc.getName());
            }
            catch (InstallException e) {
                if (e.isWarning()) {
                    System.err.printf("Warning while installing at %s:\n", loc.getName());
                } else {
                    System.err.printf("Installation at %s failed:\n", loc.getName());
                    --validLocations;
                }
                System.err.println(e.getMessage());
            }
            catch (UninstallException e) {
                if (e.isWarning()) {
                    System.err.printf("Warning while uninstalling at %s:\n", loc.getName());
                } else {
                    System.err.printf("Uninstall at %s failed:\n", loc.getName());
                    --validLocations;
                }
                System.err.println(e.getMessage());
            }
        }
        for (CorruptedIdeLocationException problem : problems) {
            System.err.println("WARNING: " + problem.getMessage());
        }
        if (validLocations == 0) {
            System.err.println("WARNING: Zero valid locations found; so nothing was done!");
        }
        return 0;
    }

    private static String generateCliHelp(boolean uninstall, CmdReader<CmdArgs> reader) {
        return reader.generateCommandLineHelp("java -jar lombok.jar " + (uninstall ? "uninstall" : "install"));
    }

    private static void printHeadlessInfo() {
        System.out.printf("About lombok v%s\nLombok makes java better by providing very spicy additions to the Java programming language,such as using @Getter to automatically generate a getter method for any field.\n\nBrowse to %s for more information. To install lombok on Eclipse, re-run this jar file on a graphical computer system - this message is being shown because your terminal is not graphics capable.\nAlternatively, use the command line installer (java -jar lombok.jar install --help).\nIf you are just using 'javac' or a tool that calls on javac, no installation is neccessary; just make sure lombok.jar is in the classpath when you compile. Example:\n\n   java -cp lombok.jar MyCode.java\n", Version.getVersion(), ABOUT_LOMBOK_URL);
    }

    private static class CmdArgs {
        @Description(value="Specify paths to a location to install/uninstall. Use 'auto' to apply to all automatically discoverable installations.")
        @Sequential
        List<String> path = new ArrayList<String>();
        @Shorthand(value={"h", "?"})
        @Description(value="Shows this help text")
        boolean help;

        private CmdArgs() {
        }
    }

    public static class CommandLineInstallerApp
    extends LombokApp {
        @Override
        public String getAppName() {
            return "install";
        }

        @Override
        public String getAppDescription() {
            return "Runs the 'handsfree' command line scriptable installer.";
        }

        @Override
        public int runApp(List<String> args) throws Exception {
            return Installer.cliInstaller(false, args);
        }
    }

    public static class CommandLineUninstallerApp
    extends LombokApp {
        @Override
        public String getAppName() {
            return "uninstall";
        }

        @Override
        public String getAppDescription() {
            return "Runs the 'handsfree' command line scriptable uninstaller.";
        }

        @Override
        public int runApp(List<String> args) throws Exception {
            return Installer.cliInstaller(true, args);
        }
    }

    public static class GraphicalInstallerApp
    extends LombokApp {
        @Override
        public String getAppName() {
            return "installer";
        }

        @Override
        public String getAppDescription() {
            return "Runs the graphical installer tool (default).";
        }

        @Override
        public List<String> getAppAliases() {
            return Arrays.asList("");
        }

        @Override
        public int runApp(List<String> args) throws Exception {
            return Installer.guiInstaller();
        }
    }
}
