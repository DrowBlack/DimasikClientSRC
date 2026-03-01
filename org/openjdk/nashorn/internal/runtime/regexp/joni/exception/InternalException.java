package org.openjdk.nashorn.internal.runtime.regexp.joni.exception;

import org.openjdk.nashorn.internal.runtime.regexp.joni.exception.JOniException;

public class InternalException
extends JOniException {
    private static final long serialVersionUID = -3871816465397927992L;

    public InternalException(String message) {
        super(message);
    }
}
