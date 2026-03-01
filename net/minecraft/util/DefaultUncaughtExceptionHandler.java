package net.minecraft.util;

import org.apache.logging.log4j.Logger;

public class DefaultUncaughtExceptionHandler
implements Thread.UncaughtExceptionHandler {
    private final Logger logger;

    public DefaultUncaughtExceptionHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void uncaughtException(Thread p_uncaughtException_1_, Throwable p_uncaughtException_2_) {
        this.logger.error("Caught previously unhandled exception :", p_uncaughtException_2_);
    }
}
