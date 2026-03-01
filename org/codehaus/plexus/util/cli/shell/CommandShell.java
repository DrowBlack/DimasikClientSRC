package org.codehaus.plexus.util.cli.shell;

import org.codehaus.plexus.util.cli.shell.Shell;

public class CommandShell
extends Shell {
    public CommandShell() {
        this.setShellCommand("command.com");
        this.setShellArgs(new String[]{"/C"});
    }
}
