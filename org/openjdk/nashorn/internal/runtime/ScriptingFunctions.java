package org.openjdk.nashorn.internal.runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;
import org.openjdk.nashorn.internal.lookup.Lookup;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.objects.NativeArray;
import org.openjdk.nashorn.internal.runtime.CommandExecutor;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.Source;
import org.openjdk.nashorn.internal.runtime.Undefined;

public final class ScriptingFunctions {
    public static final MethodHandle READLINE = ScriptingFunctions.findOwnMH("readLine", Object.class, Object.class, Object.class);
    public static final MethodHandle READFULLY = ScriptingFunctions.findOwnMH("readFully", Object.class, Object.class, Object.class);
    public static final MethodHandle EXEC = ScriptingFunctions.findOwnMH("exec", Object.class, Object.class, Object[].class);
    public static final String EXEC_NAME = "$EXEC";
    public static final String OUT_NAME = "$OUT";
    public static final String ERR_NAME = "$ERR";
    public static final String EXIT_NAME = "$EXIT";
    public static final String ENV_NAME = "$ENV";
    public static final String PWD_NAME = "PWD";
    private static Function<String, String> readLineHelper;

    private ScriptingFunctions() {
    }

    public static Object readLine(Object self, Object prompt) throws IOException {
        return ScriptingFunctions.readLine(prompt);
    }

    public static Object readFully(Object self, Object file) throws IOException {
        File f = null;
        if (file instanceof File) {
            f = (File)file;
        } else if (JSType.isString(file)) {
            f = new File(((CharSequence)file).toString());
        }
        if (f == null || !f.isFile()) {
            throw ECMAErrors.typeError("not.a.file", ScriptRuntime.safeToString(file));
        }
        return new String(Source.readFully(f));
    }

    public static Object exec(Object self, Object ... args) {
        Undefined arg0 = args.length > 0 ? args[0] : ScriptRuntime.UNDEFINED;
        Undefined arg1 = args.length > 1 ? args[1] : ScriptRuntime.UNDEFINED;
        Undefined arg2 = args.length > 2 ? args[2] : ScriptRuntime.UNDEFINED;
        Undefined arg3 = args.length > 3 ? args[3] : ScriptRuntime.UNDEFINED;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        OutputStream errorStream = null;
        String script = null;
        ArrayList<String> tokens = null;
        String inputString = null;
        if (arg0 instanceof NativeArray) {
            String[] array = (String[])JSType.toJavaArray(arg0, String.class);
            tokens = new ArrayList<String>(Arrays.asList(array));
        } else {
            script = JSType.toString(arg0);
        }
        if (arg1 instanceof InputStream) {
            inputStream = (InputStream)((Object)arg1);
        } else {
            inputString = JSType.toString(arg1);
        }
        if (arg2 instanceof OutputStream) {
            outputStream = (OutputStream)((Object)arg2);
        }
        if (arg3 instanceof OutputStream) {
            errorStream = (OutputStream)((Object)arg3);
        }
        Global global = Context.getGlobal();
        HashMap<String, String> environment = new HashMap<String, String>();
        Object env = global.get(ENV_NAME);
        if (env instanceof ScriptObject) {
            ScriptObject envProperties = (ScriptObject)env;
            envProperties.entrySet().forEach(entry -> environment.put(JSType.toString(entry.getKey()), JSType.toString(entry.getValue())));
        }
        Object exec = global.get(EXEC_NAME);
        assert (exec instanceof ScriptObject) : "$EXEC is not a script object!";
        CommandExecutor executor = new CommandExecutor();
        executor.setInputString(inputString);
        executor.setInputStream(inputStream);
        executor.setOutputStream(outputStream);
        executor.setErrorStream(errorStream);
        executor.setEnvironment(environment);
        if (tokens != null) {
            executor.process(tokens);
        } else {
            executor.process(script);
        }
        String outString = executor.getOutputString();
        String errString = executor.getErrorString();
        int exitCode = executor.getExitCode();
        global.set((Object)OUT_NAME, (Object)outString, 0);
        global.set((Object)ERR_NAME, (Object)errString, 0);
        global.set((Object)EXIT_NAME, exitCode, 0);
        return outString;
    }

    public static void setReadLineHelper(Function<String, String> func) {
        readLineHelper = Objects.requireNonNull(func);
    }

    public static Function<String, String> getReadLineHelper() {
        return readLineHelper;
    }

    public static String readLine(Object prompt) throws IOException {
        String p;
        String string = p = prompt != ScriptRuntime.UNDEFINED ? JSType.toString(prompt) : "";
        if (readLineHelper != null) {
            return readLineHelper.apply(p);
        }
        System.out.print(p);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }

    private static MethodHandle findOwnMH(String name, Class<?> rtype, Class<?> ... types) {
        return Lookup.MH.findStatic(MethodHandles.lookup(), ScriptingFunctions.class, name, Lookup.MH.type(rtype, types));
    }
}
