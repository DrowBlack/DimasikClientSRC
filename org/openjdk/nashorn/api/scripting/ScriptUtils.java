package org.openjdk.nashorn.api.scripting;

import java.lang.invoke.MethodHandle;
import jdk.dynalink.beans.StaticClass;
import jdk.dynalink.linker.LinkerServices;
import org.openjdk.nashorn.api.scripting.Formatter;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.linker.Bootstrap;

public final class ScriptUtils {
    private ScriptUtils() {
    }

    public static String parse(String code, String name, boolean includeLoc) {
        return ScriptRuntime.parse(code, name, includeLoc);
    }

    public static String format(String format, Object[] args) {
        return Formatter.format(format, args);
    }

    public static Object makeSynchronizedFunction(Object func, Object sync) {
        Object unwrapped = ScriptUtils.unwrap(func);
        if (unwrapped instanceof ScriptFunction) {
            return ((ScriptFunction)unwrapped).createSynchronized(ScriptUtils.unwrap(sync));
        }
        throw new IllegalArgumentException();
    }

    public static ScriptObjectMirror wrap(Object obj) {
        if (obj instanceof ScriptObjectMirror) {
            return (ScriptObjectMirror)obj;
        }
        if (obj instanceof ScriptObject) {
            ScriptObject sobj = (ScriptObject)obj;
            return (ScriptObjectMirror)ScriptObjectMirror.wrap(sobj, Context.getGlobal());
        }
        throw new IllegalArgumentException();
    }

    public static Object unwrap(Object obj) {
        if (obj instanceof ScriptObjectMirror) {
            return ScriptObjectMirror.unwrap(obj, Context.getGlobal());
        }
        return obj;
    }

    public static Object[] wrapArray(Object[] args) {
        if (args == null || args.length == 0) {
            return args;
        }
        return ScriptObjectMirror.wrapArray(args, Context.getGlobal());
    }

    public static Object[] unwrapArray(Object[] args) {
        if (args == null || args.length == 0) {
            return args;
        }
        return ScriptObjectMirror.unwrapArray(args, Context.getGlobal());
    }

    public static Object convert(Object obj, Object type) {
        Class<?> clazz;
        if (obj == null) {
            return null;
        }
        if (type instanceof Class) {
            clazz = (Class<?>)type;
        } else if (type instanceof StaticClass) {
            clazz = ((StaticClass)type).getRepresentedClass();
        } else {
            throw new IllegalArgumentException("type expected");
        }
        LinkerServices linker = Bootstrap.getLinkerServices();
        Object objToConvert = ScriptUtils.unwrap(obj);
        MethodHandle converter = linker.getTypeConverter(objToConvert.getClass(), clazz);
        if (converter == null) {
            throw new UnsupportedOperationException("conversion not supported");
        }
        try {
            return converter.invoke(objToConvert);
        }
        catch (Error | RuntimeException e) {
            throw e;
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
