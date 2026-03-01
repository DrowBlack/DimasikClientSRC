package net.java.games.input;

import java.security.PrivilegedAction;

static final class LinuxEnvironmentPlugin.2
implements PrivilegedAction {
    private final /* synthetic */ String val$property;

    LinuxEnvironmentPlugin.2(String string) {
        this.val$property = string;
    }

    public Object run() {
        return System.getProperty(this.val$property);
    }
}
