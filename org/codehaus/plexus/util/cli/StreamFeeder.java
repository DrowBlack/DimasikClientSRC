package org.codehaus.plexus.util.cli;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.codehaus.plexus.util.cli.AbstractStreamHandler;

public class StreamFeeder
extends AbstractStreamHandler {
    private InputStream input;
    private OutputStream output;
    private volatile Throwable exception = null;

    public StreamFeeder(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        try {
            this.feed();
        }
        catch (Throwable ex) {
            if (this.exception == null) {
                this.exception = ex;
            }
        }
        finally {
            this.close();
            StreamFeeder streamFeeder = this;
            synchronized (streamFeeder) {
                this.setDone();
                this.notifyAll();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() {
        Closeable closeable;
        if (this.input != null) {
            closeable = this.input;
            synchronized (closeable) {
                block12: {
                    try {
                        this.input.close();
                    }
                    catch (IOException ex) {
                        if (this.exception != null) break block12;
                        this.exception = ex;
                    }
                }
                this.input = null;
            }
        }
        if (this.output != null) {
            closeable = this.output;
            synchronized (closeable) {
                block13: {
                    try {
                        this.output.close();
                    }
                    catch (IOException ex) {
                        if (this.exception != null) break block13;
                        this.exception = ex;
                    }
                }
                this.output = null;
            }
        }
    }

    public Throwable getException() {
        return this.exception;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void feed() throws IOException {
        boolean flush = false;
        int data = this.input.read();
        while (!this.isDone() && data != -1) {
            OutputStream outputStream = this.output;
            synchronized (outputStream) {
                if (!this.isDisabled()) {
                    this.output.write(data);
                    flush = true;
                }
                data = this.input.read();
            }
        }
        if (flush) {
            this.output.flush();
        }
    }
}
