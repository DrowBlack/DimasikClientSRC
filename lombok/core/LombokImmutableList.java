package lombok.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class LombokImmutableList<T>
implements Iterable<T> {
    private Object[] content;
    private static final LombokImmutableList<?> EMPTY = new LombokImmutableList(new Object[0]);

    public static <T> LombokImmutableList<T> of() {
        return EMPTY;
    }

    public static <T> LombokImmutableList<T> of(T a) {
        return new LombokImmutableList<T>(new Object[]{a});
    }

    public static <T> LombokImmutableList<T> of(T a, T b) {
        return new LombokImmutableList<T>(new Object[]{a, b});
    }

    public static <T> LombokImmutableList<T> of(T a, T b, T c) {
        return new LombokImmutableList<T>(new Object[]{a, b, c});
    }

    public static <T> LombokImmutableList<T> of(T a, T b, T c, T d) {
        return new LombokImmutableList<T>(new Object[]{a, b, c, d});
    }

    public static <T> LombokImmutableList<T> of(T a, T b, T c, T d, T e) {
        return new LombokImmutableList<T>(new Object[]{a, b, c, d, e});
    }

    public static <T> LombokImmutableList<T> of(T a, T b, T c, T d, T e, T f, T ... g) {
        Object[] rest = g == null ? new Object[1] : g;
        Object[] val = new Object[rest.length + 6];
        System.arraycopy(rest, 0, val, 6, rest.length);
        val[0] = a;
        val[1] = b;
        val[2] = c;
        val[3] = d;
        val[4] = e;
        val[5] = f;
        return new LombokImmutableList<T>(val);
    }

    public static <T> LombokImmutableList<T> copyOf(Collection<? extends T> list) {
        return new LombokImmutableList<T>(list.toArray());
    }

    public static <T> LombokImmutableList<T> copyOf(Iterable<? extends T> iterable) {
        ArrayList<T> list = new ArrayList<T>();
        for (T o : iterable) {
            list.add(o);
        }
        return LombokImmutableList.copyOf(list);
    }

    public static <T> LombokImmutableList<T> copyOf(T[] array) {
        Object[] content = new Object[array.length];
        System.arraycopy(array, 0, content, 0, array.length);
        return new LombokImmutableList<T>(content);
    }

    private LombokImmutableList(Object[] content) {
        this.content = content;
    }

    public LombokImmutableList<T> replaceElementAt(int idx, T newValue) {
        Object[] newContent = (Object[])this.content.clone();
        newContent[idx] = newValue;
        return new LombokImmutableList<T>(newContent);
    }

    public LombokImmutableList<T> append(T newValue) {
        int len = this.content.length;
        Object[] newContent = new Object[len + 1];
        System.arraycopy(this.content, 0, newContent, 0, len);
        newContent[len] = newValue;
        return new LombokImmutableList<T>(newContent);
    }

    public LombokImmutableList<T> prepend(T newValue) {
        int len = this.content.length;
        Object[] newContent = new Object[len + 1];
        System.arraycopy(this.content, 0, newContent, 1, len);
        newContent[0] = newValue;
        return new LombokImmutableList<T>(newContent);
    }

    public int indexOf(T val) {
        int len = this.content.length;
        if (val == null) {
            int i = 0;
            while (i < len) {
                if (this.content[i] == null) {
                    return i;
                }
                ++i;
            }
            return -1;
        }
        int i = 0;
        while (i < len) {
            if (val.equals(this.content[i])) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public LombokImmutableList<T> removeElement(T val) {
        int idx = this.indexOf(val);
        return idx == -1 ? this : this.removeElementAt(idx);
    }

    public LombokImmutableList<T> removeElementAt(int idx) {
        int len = this.content.length;
        Object[] newContent = new Object[len - 1];
        if (idx > 0) {
            System.arraycopy(this.content, 0, newContent, 0, idx);
        }
        if (idx < len - 1) {
            System.arraycopy(this.content, idx + 1, newContent, idx, len - idx - 1);
        }
        return new LombokImmutableList<T>(newContent);
    }

    public boolean isEmpty() {
        return this.content.length == 0;
    }

    public int size() {
        return this.content.length;
    }

    public T get(int idx) {
        return (T)this.content[idx];
    }

    public boolean contains(T in) {
        if (in == null) {
            Object[] objectArray = this.content;
            int n = this.content.length;
            int n2 = 0;
            while (n2 < n) {
                Object e = objectArray[n2];
                if (e == null) {
                    return true;
                }
                ++n2;
            }
            return false;
        }
        Object[] objectArray = this.content;
        int n = this.content.length;
        int n3 = 0;
        while (n3 < n) {
            Object e = objectArray[n3];
            if (in.equals(e)) {
                return true;
            }
            ++n3;
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>(){
            private int idx = 0;

            @Override
            public boolean hasNext() {
                return this.idx < LombokImmutableList.this.content.length;
            }

            @Override
            public T next() {
                if (this.idx < LombokImmutableList.this.content.length) {
                    return LombokImmutableList.this.content[this.idx++];
                }
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("List is immutable");
            }
        };
    }

    public String toString() {
        return Arrays.toString(this.content);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof LombokImmutableList)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        return Arrays.equals(this.content, ((LombokImmutableList)obj).content);
    }

    public int hashCode() {
        return Arrays.hashCode(this.content);
    }
}
