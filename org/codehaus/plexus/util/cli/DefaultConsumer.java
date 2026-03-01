package org.codehaus.plexus.util.cli;

import java.io.IOException;
import org.codehaus.plexus.util.cli.StreamConsumer;

public class DefaultConsumer
implements StreamConsumer {
    @Override
    public void consumeLine(String line) throws IOException {
        System.out.println(line);
        if (System.out.checkError()) {
            throw new IOException(String.format("Failure printing line '%s' to stdout.", line));
        }
    }
}
