package net.java.games.input;

import java.io.File;
import java.io.FilenameFilter;

static final class LinuxEnvironmentPlugin.5
implements FilenameFilter {
    LinuxEnvironmentPlugin.5() {
    }

    public final boolean accept(File dir, String name) {
        return name.startsWith("js");
    }
}
