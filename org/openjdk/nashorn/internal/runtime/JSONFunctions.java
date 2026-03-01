package org.openjdk.nashorn.internal.runtime;

import java.lang.invoke.MethodHandle;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.parser.JSONParser;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.ParserException;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.arrays.ArrayIndex;
import org.openjdk.nashorn.internal.runtime.linker.Bootstrap;

public final class JSONFunctions {
    private static final Object REVIVER_INVOKER = new Object();

    private JSONFunctions() {
    }

    private static MethodHandle getREVIVER_INVOKER() {
        return Context.getGlobal().getDynamicInvoker(REVIVER_INVOKER, () -> Bootstrap.createDynamicCallInvoker(Object.class, Object.class, Object.class, String.class, Object.class));
    }

    public static String quote(String str) {
        return JSONParser.quote(str);
    }

    public static Object parse(Object text, Object reviver) {
        Object value;
        String str = JSType.toString(text);
        Global global = Context.getGlobal();
        boolean dualFields = ((ScriptObject)global).useDualFields();
        JSONParser parser = new JSONParser(str, global, dualFields);
        try {
            value = parser.parse();
        }
        catch (ParserException e) {
            throw ECMAErrors.syntaxError(e, "invalid.json", e.getMessage());
        }
        return JSONFunctions.applyReviver(global, value, reviver);
    }

    private static Object applyReviver(Global global, Object unfiltered, Object reviver) {
        if (Bootstrap.isCallable(reviver)) {
            ScriptObject root = global.newObject();
            root.addOwnProperty("", 0, unfiltered);
            return JSONFunctions.walk(root, "", reviver);
        }
        return unfiltered;
    }

    private static Object walk(ScriptObject holder, Object name, Object reviver) {
        Object val = holder.get(name);
        if (val instanceof ScriptObject) {
            ScriptObject valueObj = (ScriptObject)val;
            if (valueObj.isArray()) {
                int length = JSType.toInteger(valueObj.getLength());
                for (int i = 0; i < length; ++i) {
                    String key = Integer.toString(i);
                    Object newElement = JSONFunctions.walk(valueObj, key, reviver);
                    if (newElement == ScriptRuntime.UNDEFINED) {
                        valueObj.delete(i, false);
                        continue;
                    }
                    JSONFunctions.setPropertyValue(valueObj, key, newElement);
                }
            } else {
                String[] keys;
                for (String key : keys = valueObj.getOwnKeys(false)) {
                    Object newElement = JSONFunctions.walk(valueObj, key, reviver);
                    if (newElement == ScriptRuntime.UNDEFINED) {
                        valueObj.delete(key, false);
                        continue;
                    }
                    JSONFunctions.setPropertyValue(valueObj, key, newElement);
                }
            }
        }
        try {
            return JSONFunctions.getREVIVER_INVOKER().invokeExact(reviver, holder, JSType.toString(name), val);
        }
        catch (Error | RuntimeException t) {
            throw t;
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static void setPropertyValue(ScriptObject sobj, String name, Object value) {
        int index = ArrayIndex.getArrayIndex(name);
        if (ArrayIndex.isValidArrayIndex(index)) {
            sobj.defineOwnProperty(index, value);
        } else if (sobj.getMap().findProperty(name) != null) {
            sobj.set((Object)name, value, 0);
        } else {
            sobj.addOwnProperty(name, 0, value);
        }
    }
}
