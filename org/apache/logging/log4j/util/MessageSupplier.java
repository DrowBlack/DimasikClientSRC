package org.apache.logging.log4j.util;

import org.apache.logging.log4j.message.Message;

public interface MessageSupplier {
    public Message get();
}
