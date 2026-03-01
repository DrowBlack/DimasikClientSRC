package org.openjdk.nashorn.api.scripting;

import java.util.List;
import java.util.Objects;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import org.openjdk.nashorn.api.scripting.ClassFilter;
import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
import org.openjdk.nashorn.internal.parser.JSONParser;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.Version;

public final class NashornScriptEngineFactory
implements ScriptEngineFactory {
    private static final String[] DEFAULT_OPTIONS = new String[]{"-doe"};
    private static final List<String> names = List.of("nashorn", "Nashorn", "js", "JS", "JavaScript", "javascript", "ECMAScript", "ecmascript");
    private static final List<String> mimeTypes = List.of("application/javascript", "application/ecmascript", "text/javascript", "text/ecmascript");
    private static final List<String> extensions = List.of("js");

    @Override
    public String getEngineName() {
        return "OpenJDK Nashorn";
    }

    @Override
    public String getEngineVersion() {
        return Version.version();
    }

    @Override
    public List<String> getExtensions() {
        return extensions;
    }

    @Override
    public String getLanguageName() {
        return "ECMAScript";
    }

    @Override
    public String getLanguageVersion() {
        return "ECMA - 262 Edition 5.1";
    }

    @Override
    public String getMethodCallSyntax(String obj, String method, String ... args) {
        StringBuilder sb = new StringBuilder().append(Objects.requireNonNull(obj)).append('.').append(Objects.requireNonNull(method)).append('(');
        int len = args.length;
        if (len > 0) {
            sb.append(Objects.requireNonNull(args[0]));
        }
        for (int i = 1; i < len; ++i) {
            sb.append(',').append(Objects.requireNonNull(args[i]));
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public List<String> getMimeTypes() {
        return mimeTypes;
    }

    @Override
    public List<String> getNames() {
        return names;
    }

    @Override
    public String getOutputStatement(String toDisplay) {
        return "print(" + JSONParser.quote(toDisplay) + ")";
    }

    @Override
    public Object getParameter(String key) {
        switch (key) {
            case "javax.script.name": {
                return "javascript";
            }
            case "javax.script.engine": {
                return this.getEngineName();
            }
            case "javax.script.engine_version": {
                return this.getEngineVersion();
            }
            case "javax.script.language": {
                return this.getLanguageName();
            }
            case "javax.script.language_version": {
                return this.getLanguageVersion();
            }
            case "THREADING": {
                return null;
            }
        }
        return null;
    }

    @Override
    public String getProgram(String ... statements) {
        Objects.requireNonNull(statements);
        StringBuilder sb = new StringBuilder();
        for (String statement : statements) {
            sb.append(Objects.requireNonNull(statement)).append(';');
        }
        return sb.toString();
    }

    @Override
    public ScriptEngine getScriptEngine() {
        try {
            return new NashornScriptEngine(this, DEFAULT_OPTIONS, NashornScriptEngineFactory.getAppClassLoader(), null);
        }
        catch (RuntimeException e) {
            if (Context.DEBUG) {
                e.printStackTrace();
            }
            throw e;
        }
    }

    public ScriptEngine getScriptEngine(ClassLoader appLoader) {
        return this.newEngine(DEFAULT_OPTIONS, appLoader, null);
    }

    public ScriptEngine getScriptEngine(ClassFilter classFilter) {
        return this.newEngine(DEFAULT_OPTIONS, NashornScriptEngineFactory.getAppClassLoader(), Objects.requireNonNull(classFilter));
    }

    public ScriptEngine getScriptEngine(String ... args) {
        return this.newEngine(Objects.requireNonNull(args), NashornScriptEngineFactory.getAppClassLoader(), null);
    }

    public ScriptEngine getScriptEngine(String[] args, ClassLoader appLoader) {
        return this.newEngine(Objects.requireNonNull(args), appLoader, null);
    }

    public ScriptEngine getScriptEngine(String[] args, ClassLoader appLoader, ClassFilter classFilter) {
        return this.newEngine(Objects.requireNonNull(args), appLoader, Objects.requireNonNull(classFilter));
    }

    private ScriptEngine newEngine(String[] args, ClassLoader appLoader, ClassFilter classFilter) {
        NashornScriptEngineFactory.checkConfigPermission();
        try {
            return new NashornScriptEngine(this, args, appLoader, classFilter);
        }
        catch (RuntimeException e) {
            if (Context.DEBUG) {
                e.printStackTrace();
            }
            throw e;
        }
    }

    private static void checkConfigPermission() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("nashorn.setConfig"));
        }
    }

    private static ClassLoader getAppClassLoader() {
        return Objects.requireNonNullElseGet(Thread.currentThread().getContextClassLoader(), NashornScriptEngineFactory.class::getClassLoader);
    }
}
