package org.codehaus.plexus.util.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.codehaus.plexus.util.cli.AbstractStreamHandler;
import org.codehaus.plexus.util.cli.StreamConsumer;

public class StreamPumper
extends AbstractStreamHandler {
    private final BufferedReader in;
    private final StreamConsumer consumer;
    private final PrintWriter out;
    private volatile Exception exception = null;
    private static final int SIZE = 1024;

    public StreamPumper(InputStream in) {
        this(in, (StreamConsumer)null);
    }

    public StreamPumper(InputStream in, StreamConsumer consumer) {
        this(in, null, consumer);
    }

    public StreamPumper(InputStream in, PrintWriter writer) {
        this(in, writer, null);
    }

    public StreamPumper(InputStream in, PrintWriter writer, StreamConsumer consumer) {
        this.in = new BufferedReader(new InputStreamReader(in), 1024);
        this.out = writer;
        this.consumer = consumer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        boolean outError = this.out != null ? this.out.checkError() : false;
        try {
            String line = this.in.readLine();
            while (line != null) {
                try {
                    if (this.exception == null && this.consumer != null && !this.isDisabled()) {
                        this.consumer.consumeLine(line);
                    }
                }
                catch (Exception t) {
                    this.exception = t;
                }
                if (this.out != null && !outError) {
                    this.out.println(line);
                    this.out.flush();
                    if (this.out.checkError()) {
                        outError = true;
                        try {
                            throw new IOException(String.format("Failure printing line '%s'.", line));
                        }
                        catch (IOException e) {
                            this.exception = e;
                        }
                    }
                }
                line = this.in.readLine();
            }
        }
        catch (IOException e) {
            this.exception = e;
        }
        finally {
            block29: {
                try {
                    this.in.close();
                }
                catch (IOException e2) {
                    if (this.exception != null) break block29;
                    this.exception = e2;
                }
            }
            StreamPumper streamPumper = this;
            synchronized (streamPumper) {
                this.setDone();
                this.notifyAll();
            }
        }
    }

    public void flush() {
        if (this.out != null) {
            this.out.flush();
            if (this.out.checkError() && this.exception == null) {
                try {
                    throw new IOException("Failure flushing output.");
                }
                catch (IOException e) {
                    this.exception = e;
                }
            }
        }
    }

    public void close() {
        if (this.out != null) {
            this.out.close();
            if (this.out.checkError() && this.exception == null) {
                try {
                    throw new IOException("Failure closing output.");
                }
                catch (IOException e) {
                    this.exception = e;
                }
            }
        }
    }

    public Exception getException() {
        return this.exception;
    }
}
