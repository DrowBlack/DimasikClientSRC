package net.java.games.input;

import java.io.File;
import java.io.FilenameFilter;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Comparator;

static final class LinuxEnvironmentPlugin.7
implements PrivilegedAction {
    private final /* synthetic */ File val$dir;
    private final /* synthetic */ FilenameFilter val$filter;

    LinuxEnvironmentPlugin.7(File file, FilenameFilter filenameFilter) {
        this.val$dir = file;
        this.val$filter = filenameFilter;
    }

    public Object run() {
        File[] files = this.val$dir.listFiles(this.val$filter);
        Arrays.sort(files, new Comparator(){

            public int compare(Object f1, Object f2) {
                return ((File)f1).getName().compareTo(((File)f2).getName());
            }
        });
        return files;
    }
}
