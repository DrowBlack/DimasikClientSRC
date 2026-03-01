package org.codehaus.plexus.util;

import java.io.File;

public interface Scanner {
    public void setIncludes(String[] var1);

    public void setExcludes(String[] var1);

    public void addDefaultExcludes();

    public void scan();

    public String[] getIncludedFiles();

    public String[] getIncludedDirectories();

    public File getBasedir();
}
