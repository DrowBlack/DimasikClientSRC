package com.ibm.icu.impl;

import com.ibm.icu.impl.Utility;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.OutputInt;
import java.util.ArrayList;

public class UnicodeSetStringSpan {
    public static final int WITH_COUNT = 64;
    public static final int FWD = 32;
    public static final int BACK = 16;
    public static final int CONTAINED = 2;
    public static final int NOT_CONTAINED = 1;
    public static final int ALL = 127;
    public static final int FWD_UTF16_CONTAINED = 34;
    public static final int FWD_UTF16_NOT_CONTAINED = 33;
    public static final int BACK_UTF16_CONTAINED = 18;
    public static final int BACK_UTF16_NOT_CONTAINED = 17;
    static final short ALL_CP_CONTAINED = 255;
    static final short LONG_SPAN = 254;
    private UnicodeSet spanSet;
    private UnicodeSet spanNotSet;
    private ArrayList<String> strings;
    private short[] spanLengths;
    private final int maxLength16;
    private boolean someRelevant;
    private boolean all;
    private OffsetList offsets;

    public UnicodeSetStringSpan(UnicodeSet set, ArrayList<String> setStrings, int which) {
        int spanLength;
        int i;
        this.spanSet = new UnicodeSet(0, 0x10FFFF);
        this.strings = setStrings;
        this.all = which == 127;
        this.spanSet.retainAll(set);
        if (0 != (which & 1)) {
            this.spanNotSet = this.spanSet;
        }
        this.offsets = new OffsetList();
        int stringsLength = this.strings.size();
        int maxLength16 = 0;
        this.someRelevant = false;
        for (i = 0; i < stringsLength; ++i) {
            String string = this.strings.get(i);
            int length16 = string.length();
            spanLength = this.spanSet.span(string, UnicodeSet.SpanCondition.CONTAINED);
            if (spanLength < length16) {
                this.someRelevant = true;
            }
            if (length16 <= maxLength16) continue;
            maxLength16 = length16;
        }
        this.maxLength16 = maxLength16;
        if (!this.someRelevant && (which & 0x40) == 0) {
            return;
        }
        if (this.all) {
            this.spanSet.freeze();
        }
        int allocSize = this.all ? stringsLength * 2 : stringsLength;
        this.spanLengths = new short[allocSize];
        int spanBackLengthsOffset = this.all ? stringsLength : 0;
        for (i = 0; i < stringsLength; ++i) {
            String string = this.strings.get(i);
            int length16 = string.length();
            spanLength = this.spanSet.span(string, UnicodeSet.SpanCondition.CONTAINED);
            if (spanLength < length16) {
                int c;
                if (0 != (which & 2)) {
                    if (0 != (which & 0x20)) {
                        this.spanLengths[i] = UnicodeSetStringSpan.makeSpanLengthByte(spanLength);
                    }
                    if (0 != (which & 0x10)) {
                        spanLength = length16 - this.spanSet.spanBack(string, length16, UnicodeSet.SpanCondition.CONTAINED);
                        this.spanLengths[spanBackLengthsOffset + i] = UnicodeSetStringSpan.makeSpanLengthByte(spanLength);
                    }
                } else {
                    this.spanLengths[spanBackLengthsOffset + i] = 0;
                    this.spanLengths[i] = 0;
                }
                if (0 == (which & 1)) continue;
                if (0 != (which & 0x20)) {
                    c = string.codePointAt(0);
                    this.addToSpanNotSet(c);
                }
                if (0 == (which & 0x10)) continue;
                c = string.codePointBefore(length16);
                this.addToSpanNotSet(c);
                continue;
            }
            if (this.all) {
                this.spanLengths[spanBackLengthsOffset + i] = 255;
                this.spanLengths[i] = 255;
                continue;
            }
            this.spanLengths[i] = 255;
        }
        if (this.all) {
            this.spanNotSet.freeze();
        }
    }

    public UnicodeSetStringSpan(UnicodeSetStringSpan otherStringSpan, ArrayList<String> newParentSetStrings) {
        this.spanSet = otherStringSpan.spanSet;
        this.strings = newParentSetStrings;
        this.maxLength16 = otherStringSpan.maxLength16;
        this.someRelevant = otherStringSpan.someRelevant;
        this.all = true;
        this.spanNotSet = Utility.sameObjects(otherStringSpan.spanNotSet, otherStringSpan.spanSet) ? this.spanSet : (UnicodeSet)otherStringSpan.spanNotSet.clone();
        this.offsets = new OffsetList();
        this.spanLengths = (short[])otherStringSpan.spanLengths.clone();
    }

    public boolean needsStringSpanUTF16() {
        return this.someRelevant;
    }

    public boolean contains(int c) {
        return this.spanSet.contains(c);
    }

    private void addToSpanNotSet(int c) {
        if (Utility.sameObjects(this.spanNotSet, null) || Utility.sameObjects(this.spanNotSet, this.spanSet)) {
            if (this.spanSet.contains(c)) {
                return;
            }
            this.spanNotSet = this.spanSet.cloneAsThawed();
        }
        this.spanNotSet.add(c);
    }

    public int span(CharSequence s, int start, UnicodeSet.SpanCondition spanCondition) {
        if (spanCondition == UnicodeSet.SpanCondition.NOT_CONTAINED) {
            return this.spanNot(s, start, null);
        }
        int spanLimit = this.spanSet.span(s, start, UnicodeSet.SpanCondition.CONTAINED);
        if (spanLimit == s.length()) {
            return spanLimit;
        }
        return this.spanWithStrings(s, start, spanLimit, spanCondition);
    }

    private synchronized int spanWithStrings(CharSequence s, int start, int spanLimit, UnicodeSet.SpanCondition spanCondition) {
        int initSize = 0;
        if (spanCondition == UnicodeSet.SpanCondition.CONTAINED) {
            initSize = this.maxLength16;
        }
        this.offsets.setMaxLength(initSize);
        int length = s.length();
        int pos = spanLimit;
        int rest = length - spanLimit;
        int spanLength = spanLimit - start;
        int stringsLength = this.strings.size();
        while (true) {
            int i;
            if (spanCondition == UnicodeSet.SpanCondition.CONTAINED) {
                block1: for (i = 0; i < stringsLength; ++i) {
                    int overlap = this.spanLengths[i];
                    if (overlap == 255) continue;
                    String string = this.strings.get(i);
                    int length16 = string.length();
                    if (overlap >= 254) {
                        overlap = length16;
                        overlap = string.offsetByCodePoints(overlap, -1);
                    }
                    if (overlap > spanLength) {
                        overlap = spanLength;
                    }
                    for (int inc = length16 - overlap; inc <= rest; ++inc) {
                        if (!this.offsets.containsOffset(inc) && UnicodeSetStringSpan.matches16CPB(s, pos - overlap, length, string, length16)) {
                            if (inc == rest) {
                                return length;
                            }
                            this.offsets.addOffset(inc);
                        }
                        if (overlap == 0) continue block1;
                        --overlap;
                    }
                }
            } else {
                int maxInc = 0;
                int maxOverlap = 0;
                block3: for (i = 0; i < stringsLength; ++i) {
                    int overlap = this.spanLengths[i];
                    String string = this.strings.get(i);
                    int length16 = string.length();
                    if (overlap >= 254) {
                        overlap = length16;
                    }
                    if (overlap > spanLength) {
                        overlap = spanLength;
                    }
                    for (int inc = length16 - overlap; inc <= rest && overlap >= maxOverlap; --overlap, ++inc) {
                        if (overlap <= maxOverlap && inc <= maxInc || !UnicodeSetStringSpan.matches16CPB(s, pos - overlap, length, string, length16)) continue;
                        maxInc = inc;
                        maxOverlap = overlap;
                        continue block3;
                    }
                }
                if (maxInc != 0 || maxOverlap != 0) {
                    pos += maxInc;
                    if ((rest -= maxInc) == 0) {
                        return length;
                    }
                    spanLength = 0;
                    continue;
                }
            }
            if (spanLength != 0 || pos == 0) {
                if (this.offsets.isEmpty()) {
                    return pos;
                }
            } else {
                if (this.offsets.isEmpty()) {
                    spanLimit = this.spanSet.span(s, pos, UnicodeSet.SpanCondition.CONTAINED);
                    spanLength = spanLimit - pos;
                    if (spanLength == rest || spanLength == 0) {
                        return spanLimit;
                    }
                    pos += spanLength;
                    rest -= spanLength;
                    continue;
                }
                spanLength = UnicodeSetStringSpan.spanOne(this.spanSet, s, pos, rest);
                if (spanLength > 0) {
                    if (spanLength == rest) {
                        return length;
                    }
                    pos += spanLength;
                    rest -= spanLength;
                    this.offsets.shift(spanLength);
                    spanLength = 0;
                    continue;
                }
            }
            int minOffset = this.offsets.popMinimum(null);
            pos += minOffset;
            rest -= minOffset;
            spanLength = 0;
        }
    }

    public int spanAndCount(CharSequence s, int start, UnicodeSet.SpanCondition spanCondition, OutputInt outCount) {
        int maxInc;
        if (spanCondition == UnicodeSet.SpanCondition.NOT_CONTAINED) {
            return this.spanNot(s, start, outCount);
        }
        if (spanCondition == UnicodeSet.SpanCondition.CONTAINED) {
            return this.spanContainedAndCount(s, start, outCount);
        }
        int stringsLength = this.strings.size();
        int length = s.length();
        int pos = start;
        int count = 0;
        for (int rest = length - start; rest != 0; rest -= maxInc) {
            int cpLength = UnicodeSetStringSpan.spanOne(this.spanSet, s, pos, rest);
            maxInc = cpLength > 0 ? cpLength : 0;
            for (int i = 0; i < stringsLength; ++i) {
                String string = this.strings.get(i);
                int length16 = string.length();
                if (maxInc >= length16 || length16 > rest || !UnicodeSetStringSpan.matches16CPB(s, pos, length, string, length16)) continue;
                maxInc = length16;
            }
            if (maxInc == 0) {
                outCount.value = count;
                return pos;
            }
            ++count;
            pos += maxInc;
        }
        outCount.value = count;
        return pos;
    }

    private synchronized int spanContainedAndCount(CharSequence s, int start, OutputInt outCount) {
        int minOffset;
        this.offsets.setMaxLength(this.maxLength16);
        int stringsLength = this.strings.size();
        int length = s.length();
        int pos = start;
        int count = 0;
        for (int rest = length - start; rest != 0; rest -= minOffset) {
            int cpLength = UnicodeSetStringSpan.spanOne(this.spanSet, s, pos, rest);
            if (cpLength > 0) {
                this.offsets.addOffsetAndCount(cpLength, count + 1);
            }
            for (int i = 0; i < stringsLength; ++i) {
                String string = this.strings.get(i);
                int length16 = string.length();
                if (length16 > rest || this.offsets.hasCountAtOffset(length16, count + 1) || !UnicodeSetStringSpan.matches16CPB(s, pos, length, string, length16)) continue;
                this.offsets.addOffsetAndCount(length16, count + 1);
            }
            if (this.offsets.isEmpty()) {
                outCount.value = count;
                return pos;
            }
            minOffset = this.offsets.popMinimum(outCount);
            count = outCount.value;
            pos += minOffset;
        }
        outCount.value = count;
        return pos;
    }

    public synchronized int spanBack(CharSequence s, int length, UnicodeSet.SpanCondition spanCondition) {
        if (spanCondition == UnicodeSet.SpanCondition.NOT_CONTAINED) {
            return this.spanNotBack(s, length);
        }
        int pos = this.spanSet.spanBack(s, length, UnicodeSet.SpanCondition.CONTAINED);
        if (pos == 0) {
            return 0;
        }
        int spanLength = length - pos;
        int initSize = 0;
        if (spanCondition == UnicodeSet.SpanCondition.CONTAINED) {
            initSize = this.maxLength16;
        }
        this.offsets.setMaxLength(initSize);
        int stringsLength = this.strings.size();
        int spanBackLengthsOffset = 0;
        if (this.all) {
            spanBackLengthsOffset = stringsLength;
        }
        while (true) {
            int i;
            if (spanCondition == UnicodeSet.SpanCondition.CONTAINED) {
                block1: for (i = 0; i < stringsLength; ++i) {
                    int overlap = this.spanLengths[spanBackLengthsOffset + i];
                    if (overlap == 255) continue;
                    String string = this.strings.get(i);
                    int length16 = string.length();
                    if (overlap >= 254) {
                        overlap = length16;
                        int len1 = 0;
                        len1 = string.offsetByCodePoints(0, 1);
                        overlap -= len1;
                    }
                    if (overlap > spanLength) {
                        overlap = spanLength;
                    }
                    for (int dec = length16 - overlap; dec <= pos; ++dec) {
                        if (!this.offsets.containsOffset(dec) && UnicodeSetStringSpan.matches16CPB(s, pos - dec, length, string, length16)) {
                            if (dec == pos) {
                                return 0;
                            }
                            this.offsets.addOffset(dec);
                        }
                        if (overlap == 0) continue block1;
                        --overlap;
                    }
                }
            } else {
                int maxDec = 0;
                int maxOverlap = 0;
                block3: for (i = 0; i < stringsLength; ++i) {
                    int overlap = this.spanLengths[spanBackLengthsOffset + i];
                    String string = this.strings.get(i);
                    int length16 = string.length();
                    if (overlap >= 254) {
                        overlap = length16;
                    }
                    if (overlap > spanLength) {
                        overlap = spanLength;
                    }
                    for (int dec = length16 - overlap; dec <= pos && overlap >= maxOverlap; --overlap, ++dec) {
                        if (overlap <= maxOverlap && dec <= maxDec || !UnicodeSetStringSpan.matches16CPB(s, pos - dec, length, string, length16)) continue;
                        maxDec = dec;
                        maxOverlap = overlap;
                        continue block3;
                    }
                }
                if (maxDec != 0 || maxOverlap != 0) {
                    if ((pos -= maxDec) == 0) {
                        return 0;
                    }
                    spanLength = 0;
                    continue;
                }
            }
            if (spanLength != 0 || pos == length) {
                if (this.offsets.isEmpty()) {
                    return pos;
                }
            } else {
                if (this.offsets.isEmpty()) {
                    int oldPos = pos;
                    pos = this.spanSet.spanBack(s, oldPos, UnicodeSet.SpanCondition.CONTAINED);
                    spanLength = oldPos - pos;
                    if (pos != 0 && spanLength != 0) continue;
                    return pos;
                }
                spanLength = UnicodeSetStringSpan.spanOneBack(this.spanSet, s, pos);
                if (spanLength > 0) {
                    if (spanLength == pos) {
                        return 0;
                    }
                    pos -= spanLength;
                    this.offsets.shift(spanLength);
                    spanLength = 0;
                    continue;
                }
            }
            pos -= this.offsets.popMinimum(null);
            spanLength = 0;
        }
    }

    private int spanNot(CharSequence s, int start, OutputInt outCount) {
        int cpLength;
        int length = s.length();
        int pos = start;
        int rest = length - start;
        int stringsLength = this.strings.size();
        int count = 0;
        do {
            int spanLimit;
            if (outCount == null) {
                spanLimit = this.spanNotSet.span(s, pos, UnicodeSet.SpanCondition.NOT_CONTAINED);
            } else {
                spanLimit = this.spanNotSet.spanAndCount(s, pos, UnicodeSet.SpanCondition.NOT_CONTAINED, outCount);
                outCount.value = count += outCount.value;
            }
            if (spanLimit == length) {
                return length;
            }
            pos = spanLimit;
            rest = length - spanLimit;
            cpLength = UnicodeSetStringSpan.spanOne(this.spanSet, s, pos, rest);
            if (cpLength > 0) {
                return pos;
            }
            for (int i = 0; i < stringsLength; ++i) {
                String string;
                int length16;
                if (this.spanLengths[i] == 255 || (length16 = (string = this.strings.get(i)).length()) > rest || !UnicodeSetStringSpan.matches16CPB(s, pos, length, string, length16)) continue;
                return pos;
            }
            pos -= cpLength;
            ++count;
        } while ((rest += cpLength) != 0);
        if (outCount != null) {
            outCount.value = count;
        }
        return length;
    }

    private int spanNotBack(CharSequence s, int length) {
        int cpLength;
        int pos = length;
        int stringsLength = this.strings.size();
        do {
            if ((pos = this.spanNotSet.spanBack(s, pos, UnicodeSet.SpanCondition.NOT_CONTAINED)) == 0) {
                return 0;
            }
            cpLength = UnicodeSetStringSpan.spanOneBack(this.spanSet, s, pos);
            if (cpLength > 0) {
                return pos;
            }
            for (int i = 0; i < stringsLength; ++i) {
                String string;
                int length16;
                if (this.spanLengths[i] == 255 || (length16 = (string = this.strings.get(i)).length()) > pos || !UnicodeSetStringSpan.matches16CPB(s, pos - length16, length, string, length16)) continue;
                return pos;
            }
        } while ((pos += cpLength) != 0);
        return 0;
    }

    static short makeSpanLengthByte(int spanLength) {
        return (short)(spanLength < 254 ? (int)spanLength : 254);
    }

    private static boolean matches16(CharSequence s, int start, String t, int length) {
        int end = start + length;
        while (length-- > 0) {
            if (s.charAt(--end) == t.charAt(length)) continue;
            return false;
        }
        return true;
    }

    static boolean matches16CPB(CharSequence s, int start, int limit, String t, int tlength) {
        return !(!UnicodeSetStringSpan.matches16(s, start, t, tlength) || 0 < start && Character.isHighSurrogate(s.charAt(start - 1)) && Character.isLowSurrogate(s.charAt(start)) || start + tlength < limit && Character.isHighSurrogate(s.charAt(start + tlength - 1)) && Character.isLowSurrogate(s.charAt(start + tlength)));
    }

    static int spanOne(UnicodeSet set, CharSequence s, int start, int length) {
        char c2;
        char c = s.charAt(start);
        if (c >= '\ud800' && c <= '\udbff' && length >= 2 && UTF16.isTrailSurrogate(c2 = s.charAt(start + 1))) {
            int supplementary = Character.toCodePoint(c, c2);
            return set.contains(supplementary) ? 2 : -2;
        }
        return set.contains(c) ? 1 : -1;
    }

    static int spanOneBack(UnicodeSet set, CharSequence s, int length) {
        char c2;
        char c = s.charAt(length - 1);
        if (c >= '\udc00' && c <= '\udfff' && length >= 2 && UTF16.isLeadSurrogate(c2 = s.charAt(length - 2))) {
            int supplementary = Character.toCodePoint(c2, c);
            return set.contains(supplementary) ? 2 : -2;
        }
        return set.contains(c) ? 1 : -1;
    }

    private static final class OffsetList {
        private int[] list = new int[16];
        private int length;
        private int start;

        public void setMaxLength(int maxLength) {
            if (maxLength > this.list.length) {
                this.list = new int[maxLength];
            }
            this.clear();
        }

        public void clear() {
            int i = this.list.length;
            while (i-- > 0) {
                this.list[i] = 0;
            }
            this.length = 0;
            this.start = 0;
        }

        public boolean isEmpty() {
            return this.length == 0;
        }

        public void shift(int delta) {
            int i = this.start + delta;
            if (i >= this.list.length) {
                i -= this.list.length;
            }
            if (this.list[i] != 0) {
                this.list[i] = 0;
                --this.length;
            }
            this.start = i;
        }

        public void addOffset(int offset) {
            int i = this.start + offset;
            if (i >= this.list.length) {
                i -= this.list.length;
            }
            assert (this.list[i] == 0);
            this.list[i] = 1;
            ++this.length;
        }

        public void addOffsetAndCount(int offset, int count) {
            assert (count > 0);
            int i = this.start + offset;
            if (i >= this.list.length) {
                i -= this.list.length;
            }
            if (this.list[i] == 0) {
                this.list[i] = count;
                ++this.length;
            } else if (count < this.list[i]) {
                this.list[i] = count;
            }
        }

        public boolean containsOffset(int offset) {
            int i = this.start + offset;
            if (i >= this.list.length) {
                i -= this.list.length;
            }
            return this.list[i] != 0;
        }

        public boolean hasCountAtOffset(int offset, int count) {
            int oldCount;
            int i = this.start + offset;
            if (i >= this.list.length) {
                i -= this.list.length;
            }
            return (oldCount = this.list[i]) != 0 && oldCount <= count;
        }

        public int popMinimum(OutputInt outCount) {
            int count;
            int i = this.start;
            while (++i < this.list.length) {
                count = this.list[i];
                if (count == 0) continue;
                this.list[i] = 0;
                --this.length;
                int result = i - this.start;
                this.start = i;
                if (outCount != null) {
                    outCount.value = count;
                }
                return result;
            }
            int result = this.list.length - this.start;
            i = 0;
            while ((count = this.list[i]) == 0) {
                ++i;
            }
            this.list[i] = 0;
            --this.length;
            this.start = i;
            if (outCount != null) {
                outCount.value = count;
            }
            return result + i;
        }
    }
}
