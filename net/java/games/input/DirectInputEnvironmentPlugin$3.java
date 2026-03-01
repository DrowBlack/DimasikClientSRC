package net.java.games.input;

import java.security.PrivilegedAction;

static final class DirectInputEnvironmentPlugin.3
implements PrivilegedAction {
    private final /* synthetic */ String val$property;
    private final /* synthetic */ String val$default_value;

    DirectInputEnvironmentPlugin.3(String string, String string2) {
        this.val$property = string;
        this.val$default_value = string2;
    }

    public Object run() {
        return System.getProperty(this.val$property, this.val$default_value);
    }
}
