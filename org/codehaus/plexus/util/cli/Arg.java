package org.codehaus.plexus.util.cli;

import java.io.File;

public interface Arg {
    public void setValue(String var1);

    public void setLine(String var1);

    public void setFile(File var1);

    public String[] getParts();
}
