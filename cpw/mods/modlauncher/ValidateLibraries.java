package cpw.mods.modlauncher;

import cpw.mods.modlauncher.InvalidLauncherSetupException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ValidateLibraries {
    ValidateLibraries() {
    }

    static void validate() {
        if (System.getProperty("java.vendor").contains("OpenJ9")) {
            System.err.println("You are attempting to run with an unsupported Java Virtual Machine : " + System.getProperty("java.vendor"));
            System.err.println("Please visit https://adoptopenjdk.net and install the HotSpot variant.");
            System.err.println("OpenJ9 is incompatible with several of the transformation behaviours that we rely on to work.");
            throw new IllegalStateException("Open J9 is not supported");
        }
        List<Map.Entry> toCheck = Arrays.asList(ValidateLibraries.pair("log4j", "org.apache.logging.log4j.LogManager"), ValidateLibraries.pair("asm", "org.objectweb.asm.ClassVisitor"), ValidateLibraries.pair("joptsimple", "joptsimple.OptionParser"));
        List<Map.Entry> brokenLibs = toCheck.stream().filter(ValidateLibraries::tryLoad).collect(Collectors.toList());
        brokenLibs.forEach(e -> System.err.println("Failed to find class associated with library " + (String)e.getKey()));
        if (!brokenLibs.isEmpty()) {
            throw new InvalidLauncherSetupException("Missing classes, cannot continue");
        }
    }

    private static boolean tryLoad(Map.Entry<String, String> nameClazz) {
        try {
            Class.forName(nameClazz.getValue(), false, ClassLoader.getSystemClassLoader());
            return false;
        }
        catch (ClassNotFoundException e) {
            return true;
        }
    }

    private static Map.Entry<String, String> pair(String name, String clazzName) {
        return new AbstractMap.SimpleEntry<String, String>(name, clazzName);
    }
}
