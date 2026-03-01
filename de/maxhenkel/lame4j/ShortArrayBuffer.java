package de.maxhenkel.lame4j;

import java.util.Arrays;

public class ShortArrayBuffer {
    protected short[] buf;
    protected int count;
    public static final int SOFT_MAX_ARRAY_LENGTH = 0x7FFFFFF7;

    public ShortArrayBuffer() {
        this(32);
    }

    public ShortArrayBuffer(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: " + size);
        }
        this.buf = new short[size];
    }

    private void ensureCapacity(int minCapacity) {
        int oldCapacity = this.buf.length;
        int minGrowth = minCapacity - oldCapacity;
        if (minGrowth > 0) {
            this.buf = Arrays.copyOf(this.buf, ShortArrayBuffer.newLength(oldCapacity, minGrowth, oldCapacity));
        }
    }

    private static int newLength(int oldLength, int minGrowth, int prefGrowth) {
        int prefLength = oldLength + Math.max(minGrowth, prefGrowth);
        if (0 < prefLength && prefLength <= 0x7FFFFFF7) {
            return prefLength;
        }
        return ShortArrayBuffer.hugeLength(oldLength, minGrowth);
    }

    private static int hugeLength(int oldLength, int minGrowth) {
        int minLength = oldLength + minGrowth;
        if (minLength < 0) {
            throw new OutOfMemoryError("Required array length " + oldLength + " + " + minGrowth + " is too large");
        }
        if (minLength <= 0x7FFFFFF7) {
            return 0x7FFFFFF7;
        }
        return minLength;
    }

    public synchronized void write(short s) {
        this.ensureCapacity(this.count + 1);
        this.buf[this.count] = s;
        ++this.count;
    }

    public synchronized void write(short[] b, int off, int len) {
        assert (off + len <= b.length);
        this.ensureCapacity(this.count + len);
        System.arraycopy(b, off, this.buf, this.count, len);
        this.count += len;
    }

    public void writeShorts(short[] b) {
        this.write(b, 0, b.length);
    }

    public synchronized void reset() {
        this.count = 0;
    }

    public synchronized short[] toShortArray() {
        return Arrays.copyOf(this.buf, this.count);
    }

    public synchronized int size() {
        return this.count;
    }
}
