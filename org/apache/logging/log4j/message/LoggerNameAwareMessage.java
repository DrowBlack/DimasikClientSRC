package org.apache.logging.log4j.message;

public interface LoggerNameAwareMessage {
    public void setLoggerName(String var1);

    public String getLoggerName();
}
