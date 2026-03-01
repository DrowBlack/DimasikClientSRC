package org.codehaus.plexus.util.cli.shell;

import java.util.Arrays;
import java.util.List;
import org.codehaus.plexus.util.cli.shell.Shell;

public class CmdShell
extends Shell {
    public CmdShell() {
        this.setShellCommand("cmd.exe");
        this.setQuotedExecutableEnabled(true);
        this.setShellArgs(new String[]{"/X", "/C"});
    }

    @Override
    public List<String> getCommandLine(String executable, String[] arguments) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        sb.append(super.getCommandLine(executable, arguments).get(0));
        sb.append("\"");
        return Arrays.asList(sb.toString());
    }
}
