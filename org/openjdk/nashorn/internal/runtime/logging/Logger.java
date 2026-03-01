package org.openjdk.nashorn.internal.runtime.logging;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.RUNTIME)
public @interface Logger {
    public String name() default "";
}
