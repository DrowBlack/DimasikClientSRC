package net.java.games.input;

import java.io.File;
import java.security.PrivilegedAction;

static final class LinuxEnvironmentPlugin.6
implements PrivilegedAction {
    private final /* synthetic */ File val$file;

    LinuxEnvironmentPlugin.6(File file) {
        this.val$file = file;
    }

    public Object run() {
        return this.val$file.getAbsolutePath();
    }
}
