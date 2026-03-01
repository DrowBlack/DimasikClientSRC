package net.java.games.input;

import java.io.File;
import java.io.FilenameFilter;

class LinuxEnvironmentPlugin.8
implements FilenameFilter {
    LinuxEnvironmentPlugin.8() {
    }

    public final boolean accept(File dir, String name) {
        return name.startsWith("event");
    }
}
