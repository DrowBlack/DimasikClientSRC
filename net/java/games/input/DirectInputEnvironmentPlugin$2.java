package net.java.games.input;

import java.security.PrivilegedAction;

static final class DirectInputEnvironmentPlugin.2
implements PrivilegedAction {
    private final /* synthetic */ String val$property;

    DirectInputEnvironmentPlugin.2(String string) {
        this.val$property = string;
    }

    public Object run() {
        return System.getProperty(this.val$property);
    }
}
