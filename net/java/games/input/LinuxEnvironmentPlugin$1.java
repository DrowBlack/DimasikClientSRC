package net.java.games.input;

import java.io.File;
import java.security.PrivilegedAction;
import net.java.games.input.ControllerEnvironment;

static final class LinuxEnvironmentPlugin.1
implements PrivilegedAction {
    private final /* synthetic */ String val$lib_name;

    LinuxEnvironmentPlugin.1(String string) {
        this.val$lib_name = string;
    }

    public final Object run() {
        String lib_path = System.getProperty("net.java.games.input.librarypath");
        try {
            if (lib_path != null) {
                System.load(lib_path + File.separator + System.mapLibraryName(this.val$lib_name));
            } else {
                System.loadLibrary(this.val$lib_name);
            }
        }
        catch (UnsatisfiedLinkError e) {
            ControllerEnvironment.logln("Failed to load library: " + e.getMessage());
            e.printStackTrace();
            supported = false;
        }
        return null;
    }
}
