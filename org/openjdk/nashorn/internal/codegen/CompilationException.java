package org.openjdk.nashorn.internal.codegen;

public class CompilationException
extends RuntimeException {
    CompilationException(String description) {
        super(description);
    }
}
