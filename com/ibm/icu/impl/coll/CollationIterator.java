package com.ibm.icu.impl.coll;

import com.ibm.icu.impl.Trie2_32;
import com.ibm.icu.impl.coll.Collation;
import com.ibm.icu.impl.coll.CollationData;
import com.ibm.icu.impl.coll.CollationFCD;
import com.ibm.icu.impl.coll.UVector32;
import com.ibm.icu.util.BytesTrie;
import com.ibm.icu.util.CharsTrie;
import com.ibm.icu.util.ICUException;

public abstract class CollationIterator {
    protected static final long NO_CP_AND_CE32 = -4294967104L;
    protected final Trie2_32 trie;
    protected final CollationData data;
    private CEBuffer ceBuffer;
    private int cesIndex;
    private SkippedState skipped;
    private int numCpFwd;
    private boolean isNumeric;

    public CollationIterator(CollationData d) {
        this.trie = d.trie;
        this.data = d;
        this.numCpFwd = -1;
        this.isNumeric = false;
        this.ceBuffer = null;
    }

    public CollationIterator(CollationData d, boolean numeric) {
        this.trie = d.trie;
        this.data = d;
        this.numCpFwd = -1;
        this.isNumeric = numeric;
        this.ceBuffer = new CEBuffer();
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!this.getClass().equals(other.getClass())) {
            return false;
        }
        CollationIterator o = (CollationIterator)other;
        if (this.ceBuffer.length != o.ceBuffer.length || this.cesIndex != o.cesIndex || this.numCpFwd != o.numCpFwd || this.isNumeric != o.isNumeric) {
            return false;
        }
        for (int i = 0; i < this.ceBuffer.length; ++i) {
            if (this.ceBuffer.get(i) == o.ceBuffer.get(i)) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        return 0;
    }

    public abstract void resetToOffset(int var1);

    public abstract int getOffset();

    public final long nextCE() {
        CollationData d;
        if (this.cesIndex < this.ceBuffer.length) {
            return this.ceBuffer.get(this.cesIndex++);
        }
        assert (this.cesIndex == this.ceBuffer.length);
        this.ceBuffer.incLength();
        long cAndCE32 = this.handleNextCE32();
        int c = (int)(cAndCE32 >> 32);
        int ce32 = (int)cAndCE32;
        int t = ce32 & 0xFF;
        if (t < 192) {
            return this.ceBuffer.set(this.cesIndex++, (long)(ce32 & 0xFFFF0000) << 32 | (long)(ce32 & 0xFF00) << 16 | (long)(t << 8));
        }
        if (t == 192) {
            if (c < 0) {
                return this.ceBuffer.set(this.cesIndex++, 0x101000100L);
            }
            d = this.data.base;
            ce32 = d.getCE32(c);
            t = ce32 & 0xFF;
            if (t < 192) {
                return this.ceBuffer.set(this.cesIndex++, (long)(ce32 & 0xFFFF0000) << 32 | (long)(ce32 & 0xFF00) << 16 | (long)(t << 8));
            }
        } else {
            d = this.data;
        }
        if (t == 193) {
            return this.ceBuffer.set(this.cesIndex++, (long)(ce32 - t) << 32 | 0x5000500L);
        }
        return this.nextCEFromCE32(d, c, ce32);
    }

    public final int fetchCEs() {
        while (this.nextCE() != 0x101000100L) {
            this.cesIndex = this.ceBuffer.length;
        }
        return this.ceBuffer.length;
    }

    final void setCurrentCE(long ce) {
        assert (this.cesIndex > 0);
        this.ceBuffer.set(this.cesIndex - 1, ce);
    }

    public final long previousCE(UVector32 offsets) {
        CollationData d;
        if (this.ceBuffer.length > 0) {
            return this.ceBuffer.get(--this.ceBuffer.length);
        }
        offsets.removeAllElements();
        int limitOffset = this.getOffset();
        int c = this.previousCodePoint();
        if (c < 0) {
            return 0x101000100L;
        }
        if (this.data.isUnsafeBackward(c, this.isNumeric)) {
            return this.previousCEUnsafe(c, offsets);
        }
        int ce32 = this.data.getCE32(c);
        if (ce32 == 192) {
            d = this.data.base;
            ce32 = d.getCE32(c);
        } else {
            d = this.data;
        }
        if (Collation.isSimpleOrLongCE32(ce32)) {
            return Collation.ceFromCE32(ce32);
        }
        this.appendCEsFromCE32(d, c, ce32, false);
        if (this.ceBuffer.length > 1) {
            offsets.addElement(this.getOffset());
            while (offsets.size() <= this.ceBuffer.length) {
                offsets.addElement(limitOffset);
            }
        }
        return this.ceBuffer.get(--this.ceBuffer.length);
    }

    public final int getCEsLength() {
        return this.ceBuffer.length;
    }

    public final long getCE(int i) {
        return this.ceBuffer.get(i);
    }

    public final long[] getCEs() {
        return this.ceBuffer.getCEs();
    }

    final void clearCEs() {
        this.ceBuffer.length = 0;
        this.cesIndex = 0;
    }

    public final void clearCEsIfNoneRemaining() {
        if (this.cesIndex == this.ceBuffer.length) {
            this.clearCEs();
        }
    }

    public abstract int nextCodePoint();

    public abstract int previousCodePoint();

    protected final void reset() {
        this.ceBuffer.length = 0;
        this.cesIndex = 0;
        if (this.skipped != null) {
            this.skipped.clear();
        }
    }

    protected final void reset(boolean numeric) {
        if (this.ceBuffer == null) {
            this.ceBuffer = new CEBuffer();
        }
        this.reset();
        this.isNumeric = numeric;
    }

    protected long handleNextCE32() {
        int c = this.nextCodePoint();
        if (c < 0) {
            return -4294967104L;
        }
        return this.makeCodePointAndCE32Pair(c, this.data.getCE32(c));
    }

    protected long makeCodePointAndCE32Pair(int c, int ce32) {
        return (long)c << 32 | (long)ce32 & 0xFFFFFFFFL;
    }

    protected char handleGetTrailSurrogate() {
        return '\u0000';
    }

    protected boolean forbidSurrogateCodePoints() {
        return false;
    }

    protected abstract void forwardNumCodePoints(int var1);

    protected abstract void backwardNumCodePoints(int var1);

    protected int getDataCE32(int c) {
        return this.data.getCE32(c);
    }

    protected int getCE32FromBuilderData(int ce32) {
        throw new ICUException("internal program error: should be unreachable");
    }

    protected final void appendCEsFromCE32(CollationData d, int c, int ce32, boolean forward) {
        while (Collation.isSpecialCE32(ce32)) {
            switch (Collation.tagFromCE32(ce32)) {
                case 0: 
                case 3: {
                    throw new ICUException("internal program error: should be unreachable");
                }
                case 1: {
                    this.ceBuffer.append(Collation.ceFromLongPrimaryCE32(ce32));
                    return;
                }
                case 2: {
                    this.ceBuffer.append(Collation.ceFromLongSecondaryCE32(ce32));
                    return;
                }
                case 4: {
                    this.ceBuffer.ensureAppendCapacity(2);
                    this.ceBuffer.set(this.ceBuffer.length, Collation.latinCE0FromCE32(ce32));
                    this.ceBuffer.set(this.ceBuffer.length + 1, Collation.latinCE1FromCE32(ce32));
                    this.ceBuffer.length += 2;
                    return;
                }
                case 5: {
                    int index = Collation.indexFromCE32(ce32);
                    int length = Collation.lengthFromCE32(ce32);
                    this.ceBuffer.ensureAppendCapacity(length);
                    do {
                        this.ceBuffer.appendUnsafe(Collation.ceFromCE32(d.ce32s[index++]));
                    } while (--length > 0);
                    return;
                }
                case 6: {
                    int index = Collation.indexFromCE32(ce32);
                    int length = Collation.lengthFromCE32(ce32);
                    this.ceBuffer.ensureAppendCapacity(length);
                    do {
                        this.ceBuffer.appendUnsafe(d.ces[index++]);
                    } while (--length > 0);
                    return;
                }
                case 7: {
                    ce32 = this.getCE32FromBuilderData(ce32);
                    if (ce32 != 192) break;
                    d = this.data.base;
                    ce32 = d.getCE32(c);
                    break;
                }
                case 8: {
                    if (forward) {
                        this.backwardNumCodePoints(1);
                    }
                    ce32 = this.getCE32FromPrefix(d, ce32);
                    if (!forward) break;
                    this.forwardNumCodePoints(1);
                    break;
                }
                case 9: {
                    int nextCp;
                    int index = Collation.indexFromCE32(ce32);
                    int defaultCE32 = d.getCE32FromContexts(index);
                    if (!forward) {
                        ce32 = defaultCE32;
                        break;
                    }
                    if (this.skipped == null && this.numCpFwd < 0) {
                        nextCp = this.nextCodePoint();
                        if (nextCp < 0) {
                            ce32 = defaultCE32;
                            break;
                        }
                        if ((ce32 & 0x200) != 0 && !CollationFCD.mayHaveLccc(nextCp)) {
                            this.backwardNumCodePoints(1);
                            ce32 = defaultCE32;
                            break;
                        }
                    } else {
                        nextCp = this.nextSkippedCodePoint();
                        if (nextCp < 0) {
                            ce32 = defaultCE32;
                            break;
                        }
                        if ((ce32 & 0x200) != 0 && !CollationFCD.mayHaveLccc(nextCp)) {
                            this.backwardNumSkipped(1);
                            ce32 = defaultCE32;
                            break;
                        }
                    }
                    if ((ce32 = this.nextCE32FromContraction(d, ce32, d.contexts, index + 2, defaultCE32, nextCp)) != 1) break;
                    return;
                }
                case 10: {
                    if (this.isNumeric) {
                        this.appendNumericCEs(ce32, forward);
                        return;
                    }
                    ce32 = d.ce32s[Collation.indexFromCE32(ce32)];
                    break;
                }
                case 11: {
                    assert (c == 0);
                    ce32 = d.ce32s[0];
                    break;
                }
                case 12: {
                    int[] jamoCE32s = d.jamoCE32s;
                    int t = (c -= 44032) % 28;
                    int v = (c /= 28) % 21;
                    c /= 21;
                    if ((ce32 & 0x100) != 0) {
                        this.ceBuffer.ensureAppendCapacity(t == 0 ? 2 : 3);
                        this.ceBuffer.set(this.ceBuffer.length, Collation.ceFromCE32(jamoCE32s[c]));
                        this.ceBuffer.set(this.ceBuffer.length + 1, Collation.ceFromCE32(jamoCE32s[19 + v]));
                        this.ceBuffer.length += 2;
                        if (t != 0) {
                            this.ceBuffer.appendUnsafe(Collation.ceFromCE32(jamoCE32s[39 + t]));
                        }
                        return;
                    }
                    this.appendCEsFromCE32(d, -1, jamoCE32s[c], forward);
                    this.appendCEsFromCE32(d, -1, jamoCE32s[19 + v], forward);
                    if (t == 0) {
                        return;
                    }
                    ce32 = jamoCE32s[39 + t];
                    c = -1;
                    break;
                }
                case 13: {
                    assert (forward);
                    assert (CollationIterator.isLeadSurrogate(c));
                    char trail = this.handleGetTrailSurrogate();
                    if (Character.isLowSurrogate(trail)) {
                        c = Character.toCodePoint((char)c, trail);
                        if ((ce32 &= 0x300) == 0) {
                            ce32 = -1;
                            break;
                        }
                        if (ce32 != 256 && (ce32 = d.getCE32FromSupplementary(c)) != 192) break;
                        d = d.base;
                        ce32 = d.getCE32FromSupplementary(c);
                        break;
                    }
                    ce32 = -1;
                    break;
                }
                case 14: {
                    assert (c >= 0);
                    this.ceBuffer.append(d.getCEFromOffsetCE32(c, ce32));
                    return;
                }
                case 15: {
                    assert (c >= 0);
                    if (CollationIterator.isSurrogate(c) && this.forbidSurrogateCodePoints()) {
                        ce32 = -195323;
                        break;
                    }
                    this.ceBuffer.append(Collation.unassignedCEFromCodePoint(c));
                    return;
                }
            }
        }
        this.ceBuffer.append(Collation.ceFromSimpleCE32(ce32));
    }

    private static final boolean isSurrogate(int c) {
        return (c & 0xFFFFF800) == 55296;
    }

    protected static final boolean isLeadSurrogate(int c) {
        return (c & 0xFFFFFC00) == 55296;
    }

    protected static final boolean isTrailSurrogate(int c) {
        return (c & 0xFFFFFC00) == 56320;
    }

    private final long nextCEFromCE32(CollationData d, int c, int ce32) {
        --this.ceBuffer.length;
        this.appendCEsFromCE32(d, c, ce32, true);
        return this.ceBuffer.get(this.cesIndex++);
    }

    private final int getCE32FromPrefix(CollationData d, int ce32) {
        int c;
        int index = Collation.indexFromCE32(ce32);
        ce32 = d.getCE32FromContexts(index);
        int lookBehind = 0;
        CharsTrie prefixes = new CharsTrie(d.contexts, index += 2);
        while ((c = this.previousCodePoint()) >= 0) {
            ++lookBehind;
            BytesTrie.Result match = prefixes.nextForCodePoint(c);
            if (match.hasValue()) {
                ce32 = prefixes.getValue();
            }
            if (match.hasNext()) continue;
            break;
        }
        this.forwardNumCodePoints(lookBehind);
        return ce32;
    }

    private final int nextSkippedCodePoint() {
        if (this.skipped != null && this.skipped.hasNext()) {
            return this.skipped.next();
        }
        if (this.numCpFwd == 0) {
            return -1;
        }
        int c = this.nextCodePoint();
        if (this.skipped != null && !this.skipped.isEmpty() && c >= 0) {
            this.skipped.incBeyond();
        }
        if (this.numCpFwd > 0 && c >= 0) {
            --this.numCpFwd;
        }
        return c;
    }

    private final void backwardNumSkipped(int n) {
        if (this.skipped != null && !this.skipped.isEmpty()) {
            n = this.skipped.backwardNumCodePoints(n);
        }
        this.backwardNumCodePoints(n);
        if (this.numCpFwd >= 0) {
            this.numCpFwd += n;
        }
    }

    private final int nextCE32FromContraction(CollationData d, int contractionCE32, CharSequence trieChars, int trieOffset, int ce32, int c) {
        int lookAhead = 1;
        int sinceMatch = 1;
        CharsTrie suffixes = new CharsTrie(trieChars, trieOffset);
        if (this.skipped != null && !this.skipped.isEmpty()) {
            this.skipped.saveTrieState(suffixes);
        }
        BytesTrie.Result match = suffixes.firstForCodePoint(c);
        while (true) {
            if (match.hasValue()) {
                ce32 = suffixes.getValue();
                if (!match.hasNext() || (c = this.nextSkippedCodePoint()) < 0) {
                    return ce32;
                }
                if (this.skipped != null && !this.skipped.isEmpty()) {
                    this.skipped.saveTrieState(suffixes);
                }
                sinceMatch = 1;
            } else {
                int nextCp;
                if (match == BytesTrie.Result.NO_MATCH || (nextCp = this.nextSkippedCodePoint()) < 0) {
                    if ((contractionCE32 & 0x400) == 0 || (contractionCE32 & 0x100) != 0 && sinceMatch >= lookAhead) break;
                    if (sinceMatch > 1) {
                        this.backwardNumSkipped(sinceMatch);
                        c = this.nextSkippedCodePoint();
                        lookAhead -= sinceMatch - 1;
                        sinceMatch = 1;
                    }
                    if (d.getFCD16(c) <= 255) break;
                    return this.nextCE32FromDiscontiguousContraction(d, suffixes, ce32, lookAhead, c);
                }
                c = nextCp;
                ++sinceMatch;
            }
            ++lookAhead;
            match = suffixes.nextForCodePoint(c);
        }
        this.backwardNumSkipped(sinceMatch);
        return ce32;
    }

    private final int nextCE32FromDiscontiguousContraction(CollationData d, CharsTrie suffixes, int ce32, int lookAhead, int c) {
        int fcd16 = d.getFCD16(c);
        assert (fcd16 > 255);
        int nextCp = this.nextSkippedCodePoint();
        if (nextCp < 0) {
            this.backwardNumSkipped(1);
            return ce32;
        }
        ++lookAhead;
        int prevCC = fcd16 & 0xFF;
        fcd16 = d.getFCD16(nextCp);
        if (fcd16 <= 255) {
            this.backwardNumSkipped(2);
            return ce32;
        }
        if (this.skipped == null || this.skipped.isEmpty()) {
            if (this.skipped == null) {
                this.skipped = new SkippedState();
            }
            suffixes.reset();
            if (lookAhead > 2) {
                this.backwardNumCodePoints(lookAhead);
                suffixes.firstForCodePoint(this.nextCodePoint());
                for (int i = 3; i < lookAhead; ++i) {
                    suffixes.nextForCodePoint(this.nextCodePoint());
                }
                this.forwardNumCodePoints(2);
            }
            this.skipped.saveTrieState(suffixes);
        } else {
            this.skipped.resetToTrieState(suffixes);
        }
        this.skipped.setFirstSkipped(c);
        int sinceMatch = 2;
        c = nextCp;
        do {
            BytesTrie.Result match;
            if (prevCC < fcd16 >> 8 && (match = suffixes.nextForCodePoint(c)).hasValue()) {
                ce32 = suffixes.getValue();
                sinceMatch = 0;
                this.skipped.recordMatch();
                if (!match.hasNext()) break;
                this.skipped.saveTrieState(suffixes);
            } else {
                this.skipped.skip(c);
                this.skipped.resetToTrieState(suffixes);
                prevCC = fcd16 & 0xFF;
            }
            c = this.nextSkippedCodePoint();
            if (c < 0) break;
            ++sinceMatch;
        } while ((fcd16 = d.getFCD16(c)) > 255);
        this.backwardNumSkipped(sinceMatch);
        boolean isTopDiscontiguous = this.skipped.isEmpty();
        this.skipped.replaceMatch();
        if (isTopDiscontiguous && !this.skipped.isEmpty()) {
            c = -1;
            while (true) {
                this.appendCEsFromCE32(d, c, ce32, true);
                if (!this.skipped.hasNext()) break;
                c = this.skipped.next();
                ce32 = this.getDataCE32(c);
                if (ce32 == 192) {
                    d = this.data.base;
                    ce32 = d.getCE32(c);
                    continue;
                }
                d = this.data;
            }
            this.skipped.clear();
            ce32 = 1;
        }
        return ce32;
    }

    private final long previousCEUnsafe(int c, UVector32 offsets) {
        int numBackward = 1;
        while ((c = this.previousCodePoint()) >= 0) {
            ++numBackward;
            if (this.data.isUnsafeBackward(c, this.isNumeric)) continue;
        }
        this.numCpFwd = numBackward;
        this.cesIndex = 0;
        assert (this.ceBuffer.length == 0);
        int offset = this.getOffset();
        while (this.numCpFwd > 0) {
            --this.numCpFwd;
            this.nextCE();
            assert (this.ceBuffer.get(this.ceBuffer.length - 1) != 0x101000100L);
            this.cesIndex = this.ceBuffer.length;
            assert (offsets.size() < this.ceBuffer.length);
            offsets.addElement(offset);
            offset = this.getOffset();
            while (offsets.size() < this.ceBuffer.length) {
                offsets.addElement(offset);
            }
        }
        assert (offsets.size() == this.ceBuffer.length);
        offsets.addElement(offset);
        this.numCpFwd = -1;
        this.backwardNumCodePoints(numBackward);
        this.cesIndex = 0;
        return this.ceBuffer.get(--this.ceBuffer.length);
    }

    private final void appendNumericCEs(int ce32, boolean forward) {
        StringBuilder digits;
        block8: {
            block9: {
                int c;
                char digit;
                digits = new StringBuilder();
                if (forward) {
                    while (true) {
                        digit = Collation.digitFromCE32(ce32);
                        digits.append(digit);
                        if (this.numCpFwd == 0 || (c = this.nextCodePoint()) < 0) break block8;
                        ce32 = this.data.getCE32(c);
                        if (ce32 == 192) {
                            ce32 = this.data.base.getCE32(c);
                        }
                        if (!Collation.hasCE32Tag(ce32, 10)) {
                            this.backwardNumCodePoints(1);
                            break block8;
                        }
                        if (this.numCpFwd <= 0) continue;
                        --this.numCpFwd;
                    }
                }
                do {
                    digit = Collation.digitFromCE32(ce32);
                    digits.append(digit);
                    c = this.previousCodePoint();
                    if (c < 0) break block9;
                    ce32 = this.data.getCE32(c);
                    if (ce32 != 192) continue;
                    ce32 = this.data.base.getCE32(c);
                } while (Collation.hasCE32Tag(ce32, 10));
                this.forwardNumCodePoints(1);
            }
            digits.reverse();
        }
        int pos = 0;
        while (true) {
            if (pos < digits.length() - 1 && digits.charAt(pos) == '\u0000') {
                ++pos;
                continue;
            }
            int segmentLength = digits.length() - pos;
            if (segmentLength > 254) {
                segmentLength = 254;
            }
            this.appendNumericSegmentCEs(digits.subSequence(pos, pos + segmentLength));
            if ((pos += segmentLength) >= digits.length()) break;
        }
    }

    private final void appendNumericSegmentCEs(CharSequence digits) {
        int pos;
        int pair;
        int length = digits.length();
        assert (1 <= length && length <= 254);
        assert (length == 1 || digits.charAt(0) != '\u0000');
        long numericPrimary = this.data.numericPrimary;
        if (length <= 7) {
            int value = digits.charAt(0);
            for (int i = 1; i < length; ++i) {
                value = value * 10 + digits.charAt(i);
            }
            int firstByte = 2;
            int numBytes = 74;
            if (value < numBytes) {
                long primary = numericPrimary | (long)(firstByte + value << 16);
                this.ceBuffer.append(Collation.makeCE(primary));
                return;
            }
            value -= numBytes;
            firstByte += numBytes;
            numBytes = 40;
            if (value < numBytes * 254) {
                long primary = numericPrimary | (long)(firstByte + value / 254 << 16) | (long)(2 + value % 254 << 8);
                this.ceBuffer.append(Collation.makeCE(primary));
                return;
            }
            value -= numBytes * 254;
            firstByte += numBytes;
            numBytes = 16;
            if (value < numBytes * 254 * 254) {
                long primary = numericPrimary | (long)(2 + value % 254);
                primary |= (long)(2 + (value /= 254) % 254 << 8);
                this.ceBuffer.append(Collation.makeCE(primary |= (long)(firstByte + (value /= 254) % 254 << 16)));
                return;
            }
        }
        assert (length >= 7);
        int numPairs = (length + 1) / 2;
        long primary = numericPrimary | (long)(128 + numPairs << 16);
        while (digits.charAt(length - 1) == '\u0000' && digits.charAt(length - 2) == '\u0000') {
            length -= 2;
        }
        if ((length & 1) != 0) {
            pair = digits.charAt(0);
            pos = 1;
        } else {
            pair = digits.charAt(0) * 10 + digits.charAt(1);
            pos = 2;
        }
        pair = 11 + 2 * pair;
        int shift = 8;
        while (pos < length) {
            if (shift == 0) {
                this.ceBuffer.append(Collation.makeCE(primary |= (long)pair));
                primary = numericPrimary;
                shift = 16;
            } else {
                primary |= (long)(pair << shift);
                shift -= 8;
            }
            pair = 11 + 2 * (digits.charAt(pos) * 10 + digits.charAt(pos + 1));
            pos += 2;
        }
        this.ceBuffer.append(Collation.makeCE(primary |= (long)(pair - 1 << shift)));
    }

    private static final class SkippedState {
        private final StringBuilder oldBuffer = new StringBuilder();
        private final StringBuilder newBuffer = new StringBuilder();
        private int pos;
        private int skipLengthAtMatch;
        private CharsTrie.State state = new CharsTrie.State();

        SkippedState() {
        }

        void clear() {
            this.oldBuffer.setLength(0);
            this.pos = 0;
        }

        boolean isEmpty() {
            return this.oldBuffer.length() == 0;
        }

        boolean hasNext() {
            return this.pos < this.oldBuffer.length();
        }

        int next() {
            int c = this.oldBuffer.codePointAt(this.pos);
            this.pos += Character.charCount(c);
            return c;
        }

        void incBeyond() {
            assert (!this.hasNext());
            ++this.pos;
        }

        int backwardNumCodePoints(int n) {
            int length = this.oldBuffer.length();
            int beyond = this.pos - length;
            if (beyond > 0) {
                if (beyond >= n) {
                    this.pos -= n;
                    return n;
                }
                this.pos = this.oldBuffer.offsetByCodePoints(length, beyond - n);
                return beyond;
            }
            this.pos = this.oldBuffer.offsetByCodePoints(this.pos, -n);
            return 0;
        }

        void setFirstSkipped(int c) {
            this.skipLengthAtMatch = 0;
            this.newBuffer.setLength(0);
            this.newBuffer.appendCodePoint(c);
        }

        void skip(int c) {
            this.newBuffer.appendCodePoint(c);
        }

        void recordMatch() {
            this.skipLengthAtMatch = this.newBuffer.length();
        }

        void replaceMatch() {
            int oldLength = this.oldBuffer.length();
            if (this.pos > oldLength) {
                this.pos = oldLength;
            }
            this.oldBuffer.delete(0, this.pos).insert(0, this.newBuffer, 0, this.skipLengthAtMatch);
            this.pos = 0;
        }

        void saveTrieState(CharsTrie trie) {
            trie.saveState(this.state);
        }

        void resetToTrieState(CharsTrie trie) {
            trie.resetToState(this.state);
        }
    }

    private static final class CEBuffer {
        private static final int INITIAL_CAPACITY = 40;
        int length = 0;
        private long[] buffer = new long[40];

        CEBuffer() {
        }

        void append(long ce) {
            if (this.length >= 40) {
                this.ensureAppendCapacity(1);
            }
            this.buffer[this.length++] = ce;
        }

        void appendUnsafe(long ce) {
            this.buffer[this.length++] = ce;
        }

        void ensureAppendCapacity(int appCap) {
            int capacity = this.buffer.length;
            if (this.length + appCap <= capacity) {
                return;
            }
            do {
                if (capacity < 1000) {
                    capacity *= 4;
                    continue;
                }
                capacity *= 2;
            } while (capacity < this.length + appCap);
            long[] newBuffer = new long[capacity];
            System.arraycopy(this.buffer, 0, newBuffer, 0, this.length);
            this.buffer = newBuffer;
        }

        void incLength() {
            if (this.length >= 40) {
                this.ensureAppendCapacity(1);
            }
            ++this.length;
        }

        long set(int i, long ce) {
            this.buffer[i] = ce;
            return this.buffer[i];
        }

        long get(int i) {
            return this.buffer[i];
        }

        long[] getCEs() {
            return this.buffer;
        }
    }
}
