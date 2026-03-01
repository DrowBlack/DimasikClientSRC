package com.mojang.realmsclient.exception;

import org.apache.logging.log4j.Logger;

public class RealmsDefaultUncaughtExceptionHandler
implements Thread.UncaughtExceptionHandler {
    private final Logger field_224980_a;

    public RealmsDefaultUncaughtExceptionHandler(Logger p_i51787_1_) {
        this.field_224980_a = p_i51787_1_;
    }

    @Override
    public void uncaughtException(Thread p_uncaughtException_1_, Throwable p_uncaughtException_2_) {
        this.field_224980_a.error("Caught previously unhandled exception :");
        this.field_224980_a.error(p_uncaughtException_2_);
    }
}
