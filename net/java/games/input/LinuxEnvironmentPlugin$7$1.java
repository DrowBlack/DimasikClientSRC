package net.java.games.input;

import java.io.File;
import java.util.Comparator;

class LinuxEnvironmentPlugin.1
implements Comparator {
    LinuxEnvironmentPlugin.1() {
    }

    public int compare(Object f1, Object f2) {
        return ((File)f1).getName().compareTo(((File)f2).getName());
    }
}
