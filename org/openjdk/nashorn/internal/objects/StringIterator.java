package org.openjdk.nashorn.internal.objects;

import java.util.Collections;
import org.openjdk.nashorn.internal.objects.AbstractIterator;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.objects.IteratorResult;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.Undefined;

public class StringIterator
extends AbstractIterator {
    private static PropertyMap $nasgenmap$;
    private String iteratedString;
    private int nextIndex = 0;
    private final Global global;

    StringIterator(String iteratedString, Global global) {
        super(global.getStringIteratorPrototype(), $nasgenmap$);
        this.iteratedString = iteratedString;
        this.global = global;
    }

    public static Object next(Object self, Object arg) {
        if (!(self instanceof StringIterator)) {
            throw ECMAErrors.typeError("not.a.string.iterator", ScriptRuntime.safeToString(self));
        }
        return ((StringIterator)self).next(arg);
    }

    @Override
    public String getClassName() {
        return "String Iterator";
    }

    @Override
    protected IteratorResult next(Object arg) {
        char second;
        int index = this.nextIndex;
        String string = this.iteratedString;
        if (string == null || index >= string.length()) {
            this.iteratedString = null;
            return this.makeResult(Undefined.getUndefined(), Boolean.TRUE, this.global);
        }
        char first = string.charAt(index);
        if (first >= '\ud800' && first <= '\udbff' && index < string.length() - 1 && (second = string.charAt(index + 1)) >= '\udc00' && second <= '\udfff') {
            this.nextIndex += 2;
            return this.makeResult(String.valueOf(new char[]{first, second}), Boolean.FALSE, this.global);
        }
        ++this.nextIndex;
        return this.makeResult(String.valueOf(first), Boolean.FALSE, this.global);
    }

    static {
        StringIterator.$clinit$();
    }

    public static void $clinit$() {
        $nasgenmap$ = PropertyMap.newMap(Collections.EMPTY_LIST);
    }
}
