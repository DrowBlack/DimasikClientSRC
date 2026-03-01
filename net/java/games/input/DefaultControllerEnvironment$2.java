package net.java.games.input;

import java.security.PrivilegedAction;

static final class DefaultControllerEnvironment.2
implements PrivilegedAction {
    private final /* synthetic */ String val$property;

    DefaultControllerEnvironment.2(String string) {
        this.val$property = string;
    }

    public Object run() {
        return System.getProperty(this.val$property);
    }
}
