package org.codehaus.plexus.util.cli.shell;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.cli.shell.Shell;

public class BourneShell
extends Shell {
    public BourneShell() {
        this(false);
    }

    public BourneShell(boolean isLoginShell) {
        this.setUnconditionalQuoting(true);
        this.setShellCommand("/bin/sh");
        this.setArgumentQuoteDelimiter('\'');
        this.setExecutableQuoteDelimiter('\'');
        this.setSingleQuotedArgumentEscaped(true);
        this.setSingleQuotedExecutableEscaped(false);
        this.setQuotedExecutableEnabled(true);
        this.setArgumentEscapePattern("'\\%s'");
        if (isLoginShell) {
            this.addShellArg("-l");
        }
    }

    @Override
    public String getExecutable() {
        if (Os.isFamily("windows")) {
            return super.getExecutable();
        }
        return this.quoteOneItem(super.getOriginalExecutable(), true);
    }

    @Override
    public List<String> getShellArgsList() {
        ArrayList<String> shellArgs = new ArrayList<String>();
        List<String> existingShellArgs = super.getShellArgsList();
        if (existingShellArgs != null && !existingShellArgs.isEmpty()) {
            shellArgs.addAll(existingShellArgs);
        }
        shellArgs.add("-c");
        return shellArgs;
    }

    @Override
    public String[] getShellArgs() {
        String[] shellArgs = super.getShellArgs();
        if (shellArgs == null) {
            shellArgs = new String[]{};
        }
        if (shellArgs.length > 0 && !shellArgs[shellArgs.length - 1].equals("-c")) {
            String[] newArgs = new String[shellArgs.length + 1];
            System.arraycopy(shellArgs, 0, newArgs, 0, shellArgs.length);
            newArgs[shellArgs.length] = "-c";
            shellArgs = newArgs;
        }
        return shellArgs;
    }

    @Override
    protected String getExecutionPreamble() {
        if (this.getWorkingDirectoryAsString() == null) {
            return null;
        }
        String dir = this.getWorkingDirectoryAsString();
        StringBuilder sb = new StringBuilder();
        sb.append("cd ");
        sb.append(this.quoteOneItem(dir, false));
        sb.append(" && ");
        return sb.toString();
    }

    @Override
    protected String quoteOneItem(String path, boolean isExecutable) {
        if (path == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("'");
        sb.append(path.replace("'", "'\"'\"'"));
        sb.append("'");
        return sb.toString();
    }
}
