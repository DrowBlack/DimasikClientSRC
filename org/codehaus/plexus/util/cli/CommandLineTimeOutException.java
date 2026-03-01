package org.codehaus.plexus.util.cli;

import org.codehaus.plexus.util.cli.CommandLineException;

public class CommandLineTimeOutException
extends CommandLineException {
    public CommandLineTimeOutException(String message) {
        super(message);
    }

    public CommandLineTimeOutException(String message, Throwable cause) {
        super(message, cause);
    }
}
