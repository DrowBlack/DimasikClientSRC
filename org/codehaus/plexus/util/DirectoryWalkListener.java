package org.codehaus.plexus.util;

import java.io.File;

public interface DirectoryWalkListener {
    public void directoryWalkStarting(File var1);

    public void directoryWalkStep(int var1, File var2);

    public void directoryWalkFinished();

    public void debug(String var1);
}
