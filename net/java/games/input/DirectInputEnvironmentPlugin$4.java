package net.java.games.input;

import java.security.PrivilegedAction;
import net.java.games.input.DirectInputEnvironmentPlugin;

class DirectInputEnvironmentPlugin.4
implements PrivilegedAction {
    DirectInputEnvironmentPlugin.4() {
    }

    public final Object run() {
        Runtime.getRuntime().addShutdownHook(new DirectInputEnvironmentPlugin.ShutdownHook(DirectInputEnvironmentPlugin.this, null));
        return null;
    }
}
