package org.openjdk.nashorn.internal.runtime.regexp.joni.exception;

import org.openjdk.nashorn.internal.runtime.regexp.joni.exception.JOniException;

public class SyntaxException
extends JOniException {
    private static final long serialVersionUID = 7862720128961874288L;

    public SyntaxException(String message) {
        super(message);
    }
}
