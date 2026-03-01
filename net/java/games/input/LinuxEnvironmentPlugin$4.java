package net.java.games.input;

import java.security.PrivilegedAction;
import net.java.games.input.LinuxEnvironmentPlugin;

class LinuxEnvironmentPlugin.4
implements PrivilegedAction {
    LinuxEnvironmentPlugin.4() {
    }

    public final Object run() {
        Runtime.getRuntime().addShutdownHook(new LinuxEnvironmentPlugin.ShutdownHook(LinuxEnvironmentPlugin.this, null));
        return null;
    }
}
