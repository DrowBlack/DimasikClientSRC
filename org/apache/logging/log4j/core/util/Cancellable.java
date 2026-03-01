package org.apache.logging.log4j.core.util;

public interface Cancellable
extends Runnable {
    public void cancel();
}
