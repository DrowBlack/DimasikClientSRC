package com.ibm.icu.impl;

import java.nio.ByteBuffer;

public final class UResource {

    public static abstract class Sink {
        public abstract void put(Key var1, Value var2, boolean var3);
    }

    public static abstract class Value {
        protected Value() {
        }

        public abstract int getType();

        public abstract String getString();

        public abstract String getAliasString();

        public abstract int getInt();

        public abstract int getUInt();

        public abstract int[] getIntVector();

        public abstract ByteBuffer getBinary();

        public abstract Array getArray();

        public abstract Table getTable();

        public abstract boolean isNoInheritanceMarker();

        public abstract String[] getStringArray();

        public abstract String[] getStringArrayOrStringAsArray();

        public abstract String getStringOrFirstOfArray();

        public String toString() {
            switch (this.getType()) {
                case 0: {
                    return this.getString();
                }
                case 7: {
                    return Integer.toString(this.getInt());
                }
                case 14: {
                    int[] iv = this.getIntVector();
                    StringBuilder sb = new StringBuilder("[");
                    sb.append(iv.length).append("]{");
                    if (iv.length != 0) {
                        sb.append(iv[0]);
                        for (int i = 1; i < iv.length; ++i) {
                            sb.append(", ").append(iv[i]);
                        }
                    }
                    return sb.append('}').toString();
                }
                case 1: {
                    return "(binary blob)";
                }
                case 8: {
                    return "(array)";
                }
                case 2: {
                    return "(table)";
                }
            }
            return "???";
        }
    }

    public static interface Table {
        public int getSize();

        public boolean getKeyAndValue(int var1, Key var2, Value var3);

        public boolean findValue(CharSequence var1, Value var2);
    }

    public static interface Array {
        public int getSize();

        public boolean getValue(int var1, Value var2);
    }

    public static final class Key
    implements CharSequence,
    Cloneable,
    Comparable<Key> {
        private byte[] bytes;
        private int offset;
        private int length;
        private String s;

        public Key() {
            this.s = "";
        }

        public Key(String s) {
            this.setString(s);
        }

        private Key(byte[] keyBytes, int keyOffset, int keyLength) {
            this.bytes = keyBytes;
            this.offset = keyOffset;
            this.length = keyLength;
        }

        public Key setBytes(byte[] keyBytes, int keyOffset) {
            this.bytes = keyBytes;
            this.offset = keyOffset;
            this.length = 0;
            while (keyBytes[keyOffset + this.length] != 0) {
                ++this.length;
            }
            this.s = null;
            return this;
        }

        public Key setToEmpty() {
            this.bytes = null;
            this.length = 0;
            this.offset = 0;
            this.s = "";
            return this;
        }

        public Key setString(String s) {
            if (s.isEmpty()) {
                this.setToEmpty();
            } else {
                this.bytes = new byte[s.length()];
                this.offset = 0;
                this.length = s.length();
                for (int i = 0; i < this.length; ++i) {
                    char c = s.charAt(i);
                    if (c > '\u007f') {
                        throw new IllegalArgumentException('\"' + s + "\" is not an ASCII string");
                    }
                    this.bytes[i] = (byte)c;
                }
                this.s = s;
            }
            return this;
        }

        public Key clone() {
            try {
                return (Key)super.clone();
            }
            catch (CloneNotSupportedException cannotOccur) {
                return null;
            }
        }

        @Override
        public char charAt(int i) {
            assert (0 <= i && i < this.length);
            return (char)this.bytes[this.offset + i];
        }

        @Override
        public int length() {
            return this.length;
        }

        @Override
        public Key subSequence(int start, int end) {
            assert (0 <= start && start < this.length);
            assert (start <= end && end <= this.length);
            return new Key(this.bytes, this.offset + start, end - start);
        }

        @Override
        public String toString() {
            if (this.s == null) {
                this.s = this.internalSubString(0, this.length);
            }
            return this.s;
        }

        private String internalSubString(int start, int end) {
            StringBuilder sb = new StringBuilder(end - start);
            for (int i = start; i < end; ++i) {
                sb.append((char)this.bytes[this.offset + i]);
            }
            return sb.toString();
        }

        public String substring(int start) {
            assert (0 <= start && start < this.length);
            return this.internalSubString(start, this.length);
        }

        public String substring(int start, int end) {
            assert (0 <= start && start < this.length);
            assert (start <= end && end <= this.length);
            return this.internalSubString(start, end);
        }

        private boolean regionMatches(byte[] otherBytes, int otherOffset, int n) {
            for (int i = 0; i < n; ++i) {
                if (this.bytes[this.offset + i] == otherBytes[otherOffset + i]) continue;
                return false;
            }
            return true;
        }

        private boolean regionMatches(int start, CharSequence cs, int n) {
            for (int i = 0; i < n; ++i) {
                if (this.bytes[this.offset + start + i] == cs.charAt(i)) continue;
                return false;
            }
            return true;
        }

        public boolean equals(Object other) {
            if (other == null) {
                return false;
            }
            if (this == other) {
                return true;
            }
            if (other instanceof Key) {
                Key otherKey = (Key)other;
                return this.length == otherKey.length && this.regionMatches(otherKey.bytes, otherKey.offset, this.length);
            }
            return false;
        }

        public boolean contentEquals(CharSequence cs) {
            if (cs == null) {
                return false;
            }
            return this == cs || cs.length() == this.length && this.regionMatches(0, cs, this.length);
        }

        public boolean startsWith(CharSequence cs) {
            int csLength = cs.length();
            return csLength <= this.length && this.regionMatches(0, cs, csLength);
        }

        public boolean endsWith(CharSequence cs) {
            int csLength = cs.length();
            return csLength <= this.length && this.regionMatches(this.length - csLength, cs, csLength);
        }

        public boolean regionMatches(int start, CharSequence cs) {
            int csLength = cs.length();
            return csLength == this.length - start && this.regionMatches(start, cs, csLength);
        }

        public int hashCode() {
            if (this.length == 0) {
                return 0;
            }
            int h = this.bytes[this.offset];
            for (int i = 1; i < this.length; ++i) {
                h = 37 * h + this.bytes[this.offset];
            }
            return h;
        }

        @Override
        public int compareTo(Key other) {
            return this.compareTo((CharSequence)other);
        }

        @Override
        public int compareTo(CharSequence cs) {
            int csLength = cs.length();
            int minLength = this.length <= csLength ? this.length : csLength;
            for (int i = 0; i < minLength; ++i) {
                int diff = this.charAt(i) - cs.charAt(i);
                if (diff == 0) continue;
                return diff;
            }
            return this.length - csLength;
        }
    }
}
