package com.ibm.icu.impl.coll;

import com.ibm.icu.impl.Normalizer2Impl;
import com.ibm.icu.impl.coll.CollationData;
import com.ibm.icu.impl.coll.CollationFCD;
import com.ibm.icu.impl.coll.CollationIterator;
import com.ibm.icu.impl.coll.UTF16CollationIterator;

public final class FCDUTF16CollationIterator
extends UTF16CollationIterator {
    private CharSequence rawSeq;
    private static final int rawStart = 0;
    private int segmentStart;
    private int segmentLimit;
    private int rawLimit;
    private final Normalizer2Impl nfcImpl;
    private StringBuilder normalized;
    private int checkDir;

    public FCDUTF16CollationIterator(CollationData d) {
        super(d);
        this.nfcImpl = d.nfcImpl;
    }

    public FCDUTF16CollationIterator(CollationData data, boolean numeric, CharSequence s, int p) {
        super(data, numeric, s, p);
        this.rawSeq = s;
        this.segmentStart = p;
        this.rawLimit = s.length();
        this.nfcImpl = data.nfcImpl;
        this.checkDir = 1;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof CollationIterator && ((CollationIterator)this).equals(other) && other instanceof FCDUTF16CollationIterator)) {
            return false;
        }
        FCDUTF16CollationIterator o = (FCDUTF16CollationIterator)other;
        if (this.checkDir != o.checkDir) {
            return false;
        }
        if (this.checkDir == 0 && this.seq == this.rawSeq != (o.seq == o.rawSeq)) {
            return false;
        }
        if (this.checkDir != 0 || this.seq == this.rawSeq) {
            return this.pos - 0 == o.pos - 0;
        }
        return this.segmentStart - 0 == o.segmentStart - 0 && this.pos - this.start == o.pos - o.start;
    }

    @Override
    public int hashCode() {
        assert (false) : "hashCode not designed";
        return 42;
    }

    @Override
    public void resetToOffset(int newOffset) {
        this.reset();
        this.seq = this.rawSeq;
        this.segmentStart = this.pos = 0 + newOffset;
        this.start = this.pos;
        this.limit = this.rawLimit;
        this.checkDir = 1;
    }

    @Override
    public int getOffset() {
        if (this.checkDir != 0 || this.seq == this.rawSeq) {
            return this.pos - 0;
        }
        if (this.pos == this.start) {
            return this.segmentStart - 0;
        }
        return this.segmentLimit - 0;
    }

    @Override
    public void setText(boolean numeric, CharSequence s, int p) {
        super.setText(numeric, s, p);
        this.rawSeq = s;
        this.segmentStart = p;
        this.rawLimit = this.limit = s.length();
        this.checkDir = 1;
    }

    @Override
    public int nextCodePoint() {
        char trail;
        char c;
        while (true) {
            if (this.checkDir > 0) {
                if (this.pos == this.limit) {
                    return -1;
                }
                if (!CollationFCD.hasTccc(c = this.seq.charAt(this.pos++)) || !CollationFCD.maybeTibetanCompositeVowel(c) && (this.pos == this.limit || !CollationFCD.hasLccc(this.seq.charAt(this.pos)))) break;
                --this.pos;
                this.nextSegment();
                c = this.seq.charAt(this.pos++);
                break;
            }
            if (this.checkDir == 0 && this.pos != this.limit) {
                c = this.seq.charAt(this.pos++);
                break;
            }
            this.switchToForward();
        }
        if (Character.isHighSurrogate(c) && this.pos != this.limit && Character.isLowSurrogate(trail = this.seq.charAt(this.pos))) {
            ++this.pos;
            return Character.toCodePoint(c, trail);
        }
        return c;
    }

    @Override
    public int previousCodePoint() {
        char lead;
        char c;
        while (true) {
            if (this.checkDir < 0) {
                if (this.pos == this.start) {
                    return -1;
                }
                if (!CollationFCD.hasLccc(c = this.seq.charAt(--this.pos)) || !CollationFCD.maybeTibetanCompositeVowel(c) && (this.pos == this.start || !CollationFCD.hasTccc(this.seq.charAt(this.pos - 1)))) break;
                ++this.pos;
                this.previousSegment();
                c = this.seq.charAt(--this.pos);
                break;
            }
            if (this.checkDir == 0 && this.pos != this.start) {
                c = this.seq.charAt(--this.pos);
                break;
            }
            this.switchToBackward();
        }
        if (Character.isLowSurrogate(c) && this.pos != this.start && Character.isHighSurrogate(lead = this.seq.charAt(this.pos - 1))) {
            --this.pos;
            return Character.toCodePoint(lead, c);
        }
        return c;
    }

    @Override
    protected long handleNextCE32() {
        char c;
        while (true) {
            if (this.checkDir > 0) {
                if (this.pos == this.limit) {
                    return -4294967104L;
                }
                if (!CollationFCD.hasTccc(c = this.seq.charAt(this.pos++)) || !CollationFCD.maybeTibetanCompositeVowel(c) && (this.pos == this.limit || !CollationFCD.hasLccc(this.seq.charAt(this.pos)))) break;
                --this.pos;
                this.nextSegment();
                c = this.seq.charAt(this.pos++);
                break;
            }
            if (this.checkDir == 0 && this.pos != this.limit) {
                c = this.seq.charAt(this.pos++);
                break;
            }
            this.switchToForward();
        }
        return this.makeCodePointAndCE32Pair(c, this.trie.getFromU16SingleLead(c));
    }

    @Override
    protected void forwardNumCodePoints(int num) {
        while (num > 0 && this.nextCodePoint() >= 0) {
            --num;
        }
    }

    @Override
    protected void backwardNumCodePoints(int num) {
        while (num > 0 && this.previousCodePoint() >= 0) {
            --num;
        }
    }

    private void switchToForward() {
        assert (this.checkDir < 0 && this.seq == this.rawSeq || this.checkDir == 0 && this.pos == this.limit);
        if (this.checkDir < 0) {
            this.start = this.segmentStart = this.pos;
            if (this.pos == this.segmentLimit) {
                this.limit = this.rawLimit;
                this.checkDir = 1;
            } else {
                this.checkDir = 0;
            }
        } else {
            if (this.seq != this.rawSeq) {
                this.seq = this.rawSeq;
                this.start = this.segmentStart = this.segmentLimit;
                this.pos = this.segmentStart;
            }
            this.limit = this.rawLimit;
            this.checkDir = 1;
        }
    }

    private void nextSegment() {
        block6: {
            assert (this.checkDir > 0 && this.seq == this.rawSeq && this.pos != this.limit);
            int p = this.pos;
            int prevCC = 0;
            do {
                int q = p;
                int c = Character.codePointAt(this.seq, p);
                p += Character.charCount(c);
                int fcd16 = this.nfcImpl.getFCD16(c);
                int leadCC = fcd16 >> 8;
                if (leadCC == 0 && q != this.pos) {
                    this.limit = this.segmentLimit = q;
                    break block6;
                }
                if (leadCC != 0 && (prevCC > leadCC || CollationFCD.isFCD16OfTibetanCompositeVowel(fcd16))) {
                    do {
                        q = p;
                        if (p == this.rawLimit) break;
                        c = Character.codePointAt(this.seq, p);
                        p += Character.charCount(c);
                    } while (this.nfcImpl.getFCD16(c) > 255);
                    this.normalize(this.pos, q);
                    this.pos = this.start;
                    break block6;
                }
                prevCC = fcd16 & 0xFF;
            } while (p != this.rawLimit && prevCC != 0);
            this.limit = this.segmentLimit = p;
        }
        assert (this.pos != this.limit);
        this.checkDir = 0;
    }

    private void switchToBackward() {
        assert (this.checkDir > 0 && this.seq == this.rawSeq || this.checkDir == 0 && this.pos == this.start);
        if (this.checkDir > 0) {
            this.limit = this.segmentLimit = this.pos;
            if (this.pos == this.segmentStart) {
                this.start = 0;
                this.checkDir = -1;
            } else {
                this.checkDir = 0;
            }
        } else {
            if (this.seq != this.rawSeq) {
                this.seq = this.rawSeq;
                this.limit = this.segmentLimit = this.segmentStart;
                this.pos = this.segmentLimit;
            }
            this.start = 0;
            this.checkDir = -1;
        }
    }

    private void previousSegment() {
        block6: {
            assert (this.checkDir < 0 && this.seq == this.rawSeq && this.pos != this.start);
            int p = this.pos;
            int nextCC = 0;
            do {
                int q = p;
                int c = Character.codePointBefore(this.seq, p);
                p -= Character.charCount(c);
                int fcd16 = this.nfcImpl.getFCD16(c);
                int trailCC = fcd16 & 0xFF;
                if (trailCC == 0 && q != this.pos) {
                    this.start = this.segmentStart = q;
                    break block6;
                }
                if (trailCC != 0 && (nextCC != 0 && trailCC > nextCC || CollationFCD.isFCD16OfTibetanCompositeVowel(fcd16))) {
                    do {
                        q = p;
                        if (fcd16 <= 255 || p == 0) break;
                        c = Character.codePointBefore(this.seq, p);
                        p -= Character.charCount(c);
                    } while ((fcd16 = this.nfcImpl.getFCD16(c)) != 0);
                    this.normalize(q, this.pos);
                    this.pos = this.limit;
                    break block6;
                }
                nextCC = fcd16 >> 8;
            } while (p != 0 && nextCC != 0);
            this.start = this.segmentStart = p;
        }
        assert (this.pos != this.start);
        this.checkDir = 0;
    }

    private void normalize(int from, int to) {
        if (this.normalized == null) {
            this.normalized = new StringBuilder();
        }
        this.nfcImpl.decompose(this.rawSeq, from, to, this.normalized, to - from);
        this.segmentStart = from;
        this.segmentLimit = to;
        this.seq = this.normalized;
        this.start = 0;
        this.limit = this.start + this.normalized.length();
    }
}
