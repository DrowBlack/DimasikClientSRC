package org.openjdk.nashorn.internal.runtime.doubleconv;

import org.openjdk.nashorn.internal.runtime.doubleconv.DtoaMode;

public class DtoaBuffer {
    final char[] chars;
    int length = 0;
    int decimalPoint = 0;
    boolean isNegative = false;
    public static final int kFastDtoaMaximalLength = 17;

    public DtoaBuffer(int capacity) {
        this.chars = new char[capacity];
    }

    void append(char c) {
        this.chars[this.length++] = c;
    }

    public void reset() {
        this.length = 0;
        this.decimalPoint = 0;
    }

    public String getRawDigits() {
        return new String(this.chars, 0, this.length);
    }

    public int getDecimalPoint() {
        return this.decimalPoint;
    }

    public int getLength() {
        return this.length;
    }

    public String format(DtoaMode mode, int digitsAfterPoint) {
        StringBuilder buffer = new StringBuilder();
        if (this.isNegative) {
            buffer.append('-');
        }
        switch (mode) {
            case SHORTEST: {
                if (this.decimalPoint < -5 || this.decimalPoint > 21) {
                    this.toExponentialFormat(buffer);
                    break;
                }
                this.toFixedFormat(buffer, digitsAfterPoint);
                break;
            }
            case FIXED: {
                this.toFixedFormat(buffer, digitsAfterPoint);
                break;
            }
            case PRECISION: {
                if (this.decimalPoint < -5 || this.decimalPoint > this.length) {
                    this.toExponentialFormat(buffer);
                    break;
                }
                this.toFixedFormat(buffer, digitsAfterPoint);
            }
        }
        return buffer.toString();
    }

    private void toFixedFormat(StringBuilder buffer, int digitsAfterPoint) {
        int i;
        if (this.decimalPoint <= 0) {
            buffer.append('0');
            if (this.length > 0) {
                buffer.append('.');
                int padding = -this.decimalPoint;
                for (int i2 = 0; i2 < padding; ++i2) {
                    buffer.append('0');
                }
                buffer.append(this.chars, 0, this.length);
            } else {
                this.decimalPoint = 1;
            }
        } else if (this.decimalPoint >= this.length) {
            buffer.append(this.chars, 0, this.length);
            for (i = this.length; i < this.decimalPoint; ++i) {
                buffer.append('0');
            }
        } else if (this.decimalPoint < this.length) {
            buffer.append(this.chars, 0, this.decimalPoint);
            buffer.append('.');
            buffer.append(this.chars, this.decimalPoint, this.length - this.decimalPoint);
        }
        if (digitsAfterPoint > 0) {
            if (this.decimalPoint >= this.length) {
                buffer.append('.');
            }
            for (i = Math.max(0, this.length - this.decimalPoint); i < digitsAfterPoint; ++i) {
                buffer.append('0');
            }
        }
    }

    private void toExponentialFormat(StringBuilder buffer) {
        buffer.append(this.chars[0]);
        if (this.length > 1) {
            buffer.append('.');
            buffer.append(this.chars, 1, this.length - 1);
        }
        buffer.append('e');
        int exponent = this.decimalPoint - 1;
        if (exponent > 0) {
            buffer.append('+');
        }
        buffer.append(exponent);
    }

    public String toString() {
        return "[chars:" + new String(this.chars, 0, this.length) + ", decimalPoint:" + this.decimalPoint + "]";
    }
}
