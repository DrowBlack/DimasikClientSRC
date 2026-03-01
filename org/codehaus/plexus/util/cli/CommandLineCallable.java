package org.codehaus.plexus.util.cli;

import java.util.concurrent.Callable;
import org.codehaus.plexus.util.cli.CommandLineException;

public interface CommandLineCallable
extends Callable<Integer> {
    @Override
    public Integer call() throws CommandLineException;
}
