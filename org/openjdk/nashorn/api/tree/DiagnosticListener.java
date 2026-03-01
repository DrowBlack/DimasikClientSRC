package org.openjdk.nashorn.api.tree;

import org.openjdk.nashorn.api.tree.Diagnostic;

@FunctionalInterface
public interface DiagnosticListener {
    public void report(Diagnostic var1);
}
