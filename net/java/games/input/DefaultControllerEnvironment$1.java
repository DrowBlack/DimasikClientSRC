package net.java.games.input;

import java.io.File;
import java.security.PrivilegedAction;

static final class DefaultControllerEnvironment.1
implements PrivilegedAction {
    private final /* synthetic */ String val$lib_name;

    DefaultControllerEnvironment.1(String string) {
        this.val$lib_name = string;
    }

    public final Object run() {
        String lib_path = System.getProperty("net.java.games.input.librarypath");
        if (lib_path != null) {
            System.load(lib_path + File.separator + System.mapLibraryName(this.val$lib_name));
        } else {
            System.loadLibrary(this.val$lib_name);
        }
        return null;
    }
}
