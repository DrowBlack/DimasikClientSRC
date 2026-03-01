package org.openjdk.nashorn.internal.runtime.arrays;

import java.util.Iterator;
import java.util.List;
import org.openjdk.nashorn.api.scripting.JSObject;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.arrays.EmptyArrayLikeIterator;
import org.openjdk.nashorn.internal.runtime.arrays.JSObjectIterator;
import org.openjdk.nashorn.internal.runtime.arrays.JavaArrayIterator;
import org.openjdk.nashorn.internal.runtime.arrays.JavaListIterator;
import org.openjdk.nashorn.internal.runtime.arrays.ReverseJSObjectIterator;
import org.openjdk.nashorn.internal.runtime.arrays.ReverseJavaArrayIterator;
import org.openjdk.nashorn.internal.runtime.arrays.ReverseJavaListIterator;
import org.openjdk.nashorn.internal.runtime.arrays.ReverseScriptArrayIterator;
import org.openjdk.nashorn.internal.runtime.arrays.ReverseScriptObjectIterator;
import org.openjdk.nashorn.internal.runtime.arrays.ScriptArrayIterator;
import org.openjdk.nashorn.internal.runtime.arrays.ScriptObjectIterator;

public abstract class ArrayLikeIterator<T>
implements Iterator<T> {
    protected long index;
    protected final boolean includeUndefined;

    ArrayLikeIterator(boolean includeUndefined) {
        this.includeUndefined = includeUndefined;
        this.index = 0L;
    }

    public boolean isReverse() {
        return false;
    }

    protected long bumpIndex() {
        return this.index++;
    }

    public long nextIndex() {
        return this.index;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    public abstract long getLength();

    public static ArrayLikeIterator<Object> arrayLikeIterator(Object object) {
        return ArrayLikeIterator.arrayLikeIterator(object, false);
    }

    public static ArrayLikeIterator<Object> reverseArrayLikeIterator(Object object) {
        return ArrayLikeIterator.reverseArrayLikeIterator(object, false);
    }

    public static ArrayLikeIterator<Object> arrayLikeIterator(Object object, boolean includeUndefined) {
        Object obj = object;
        if (ScriptObject.isArray(obj)) {
            return new ScriptArrayIterator((ScriptObject)obj, includeUndefined);
        }
        if ((obj = JSType.toScriptObject(obj)) instanceof ScriptObject) {
            return new ScriptObjectIterator((ScriptObject)obj, includeUndefined);
        }
        if (obj instanceof JSObject) {
            return new JSObjectIterator((JSObject)obj, includeUndefined);
        }
        if (obj instanceof List) {
            return new JavaListIterator((List)obj, includeUndefined);
        }
        if (obj != null && obj.getClass().isArray()) {
            return new JavaArrayIterator(obj, includeUndefined);
        }
        return new EmptyArrayLikeIterator();
    }

    public static ArrayLikeIterator<Object> reverseArrayLikeIterator(Object object, boolean includeUndefined) {
        Object obj = object;
        if (ScriptObject.isArray(obj)) {
            return new ReverseScriptArrayIterator((ScriptObject)obj, includeUndefined);
        }
        if ((obj = JSType.toScriptObject(obj)) instanceof ScriptObject) {
            return new ReverseScriptObjectIterator((ScriptObject)obj, includeUndefined);
        }
        if (obj instanceof JSObject) {
            return new ReverseJSObjectIterator((JSObject)obj, includeUndefined);
        }
        if (obj instanceof List) {
            return new ReverseJavaListIterator((List)obj, includeUndefined);
        }
        if (obj != null && obj.getClass().isArray()) {
            return new ReverseJavaArrayIterator(obj, includeUndefined);
        }
        return new EmptyArrayLikeIterator();
    }
}
