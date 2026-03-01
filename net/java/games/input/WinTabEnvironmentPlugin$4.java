package net.java.games.input;

import java.security.PrivilegedAction;
import net.java.games.input.WinTabEnvironmentPlugin;

class WinTabEnvironmentPlugin.4
implements PrivilegedAction {
    WinTabEnvironmentPlugin.4() {
    }

    public final Object run() {
        Runtime.getRuntime().addShutdownHook(new WinTabEnvironmentPlugin.ShutdownHook(WinTabEnvironmentPlugin.this, null));
        return null;
    }
}
