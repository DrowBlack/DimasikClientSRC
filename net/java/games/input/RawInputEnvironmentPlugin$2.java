package net.java.games.input;

import java.security.PrivilegedAction;

static final class RawInputEnvironmentPlugin.2
implements PrivilegedAction {
    private final /* synthetic */ String val$property;

    RawInputEnvironmentPlugin.2(String string) {
        this.val$property = string;
    }

    public Object run() {
        return System.getProperty(this.val$property);
    }
}
