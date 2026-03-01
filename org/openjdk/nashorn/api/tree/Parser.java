package org.openjdk.nashorn.api.tree;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Path;
import org.openjdk.nashorn.api.scripting.NashornException;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import org.openjdk.nashorn.api.tree.CompilationUnitTree;
import org.openjdk.nashorn.api.tree.DiagnosticListener;
import org.openjdk.nashorn.api.tree.ParserImpl;

public interface Parser {
    public CompilationUnitTree parse(File var1, DiagnosticListener var2) throws IOException, NashornException;

    public CompilationUnitTree parse(Path var1, DiagnosticListener var2) throws IOException, NashornException;

    public CompilationUnitTree parse(URL var1, DiagnosticListener var2) throws IOException, NashornException;

    public CompilationUnitTree parse(String var1, Reader var2, DiagnosticListener var3) throws IOException, NashornException;

    public CompilationUnitTree parse(String var1, String var2, DiagnosticListener var3) throws NashornException;

    public CompilationUnitTree parse(ScriptObjectMirror var1, DiagnosticListener var2) throws NashornException;

    public static Parser create(String ... options) throws IllegalArgumentException {
        options.getClass();
        String[] stringArray = options;
        int n = stringArray.length;
        block15: for (int i = 0; i < n; ++i) {
            String opt;
            switch (opt = stringArray[i]) {
                case "--const-as-var": 
                case "-dump-on-error": 
                case "-doe": 
                case "--empty-statements": 
                case "--no-syntax-extensions": 
                case "-nse": 
                case "-scripting": 
                case "-strict": 
                case "--language=es6": 
                case "--es6-module": {
                    continue block15;
                }
                default: {
                    throw new IllegalArgumentException(opt);
                }
            }
        }
        return new ParserImpl(options);
    }
}
