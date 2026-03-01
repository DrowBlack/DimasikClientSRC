package com.ibm.icu.impl.coll;

import com.ibm.icu.impl.coll.Collation;
import com.ibm.icu.impl.coll.CollationData;
import com.ibm.icu.impl.coll.CollationFastLatin;
import com.ibm.icu.impl.coll.UVector64;
import com.ibm.icu.util.CharsTrie;

final class CollationFastLatinBuilder {
    private static final int NUM_SPECIAL_GROUPS = 4;
    private static final long CONTRACTION_FLAG = 0x80000000L;
    private long ce0 = 0L;
    private long ce1 = 0L;
    private long[][] charCEs = new long[448][2];
    private UVector64 contractionCEs;
    private UVector64 uniqueCEs;
    private char[] miniCEs = null;
    long[] lastSpecialPrimaries = new long[4];
    private long firstDigitPrimary = 0L;
    private long firstLatinPrimary = 0L;
    private long lastLatinPrimary = 0L;
    private long firstShortPrimary = 0L;
    private boolean shortPrimaryOverflow = false;
    private StringBuilder result = new StringBuilder();
    private int headerLength = 0;

    private static final int compareInt64AsUnsigned(long a, long b) {
        if ((a += Long.MIN_VALUE) < (b += Long.MIN_VALUE)) {
            return -1;
        }
        if (a > b) {
            return 1;
        }
        return 0;
    }

    private static final int binarySearch(long[] list, int limit, long ce) {
        if (limit == 0) {
            return -1;
        }
        int start = 0;
        int i;
        int cmp;
        while ((cmp = CollationFastLatinBuilder.compareInt64AsUnsigned(ce, list[i = (int)(((long)start + (long)limit) / 2L)])) != 0) {
            if (cmp < 0) {
                if (i == start) {
                    return ~start;
                }
                limit = i;
                continue;
            }
            if (i == start) {
                return ~(start + 1);
            }
            start = i;
        }
        return i;
    }

    CollationFastLatinBuilder() {
        this.contractionCEs = new UVector64();
        this.uniqueCEs = new UVector64();
    }

    boolean forData(CollationData data) {
        boolean ok;
        if (this.result.length() != 0) {
            throw new IllegalStateException("attempt to reuse a CollationFastLatinBuilder");
        }
        if (!this.loadGroups(data)) {
            return false;
        }
        this.firstShortPrimary = this.firstDigitPrimary;
        this.getCEs(data);
        this.encodeUniqueCEs();
        if (this.shortPrimaryOverflow) {
            this.firstShortPrimary = this.firstLatinPrimary;
            this.resetCEs();
            this.getCEs(data);
            this.encodeUniqueCEs();
        }
        boolean bl = ok = !this.shortPrimaryOverflow;
        if (ok) {
            this.encodeCharCEs();
            this.encodeContractions();
        }
        this.contractionCEs.removeAllElements();
        this.uniqueCEs.removeAllElements();
        return ok;
    }

    char[] getHeader() {
        char[] resultArray = new char[this.headerLength];
        this.result.getChars(0, this.headerLength, resultArray, 0);
        return resultArray;
    }

    char[] getTable() {
        char[] resultArray = new char[this.result.length() - this.headerLength];
        this.result.getChars(this.headerLength, this.result.length(), resultArray, 0);
        return resultArray;
    }

    private boolean loadGroups(CollationData data) {
        this.headerLength = 5;
        int r0 = 0x200 | this.headerLength;
        this.result.append((char)r0);
        for (int i = 0; i < 4; ++i) {
            this.lastSpecialPrimaries[i] = data.getLastPrimaryForGroup(4096 + i);
            if (this.lastSpecialPrimaries[i] == 0L) {
                return false;
            }
            this.result.append(0);
        }
        this.firstDigitPrimary = data.getFirstPrimaryForGroup(4100);
        this.firstLatinPrimary = data.getFirstPrimaryForGroup(25);
        this.lastLatinPrimary = data.getLastPrimaryForGroup(25);
        return this.firstDigitPrimary != 0L && this.firstLatinPrimary != 0L;
    }

    private boolean inSameGroup(long p, long q) {
        if (p >= this.firstShortPrimary) {
            return q >= this.firstShortPrimary;
        }
        if (q >= this.firstShortPrimary) {
            return false;
        }
        long lastVariablePrimary = this.lastSpecialPrimaries[3];
        if (p > lastVariablePrimary) {
            return q > lastVariablePrimary;
        }
        if (q > lastVariablePrimary) {
            return false;
        }
        assert (p != 0L && q != 0L);
        int i = 0;
        long lastPrimary;
        while (p > (lastPrimary = this.lastSpecialPrimaries[i])) {
            if (q <= lastPrimary) {
                return false;
            }
            ++i;
        }
        return q <= lastPrimary;
    }

    private void resetCEs() {
        this.contractionCEs.removeAllElements();
        this.uniqueCEs.removeAllElements();
        this.shortPrimaryOverflow = false;
        this.result.setLength(this.headerLength);
    }

    private void getCEs(CollationData data) {
        int i = 0;
        int c = 0;
        while (true) {
            CollationData d;
            if (c == 384) {
                c = 8192;
            } else if (c == 8256) break;
            int ce32 = data.getCE32(c);
            if (ce32 == 192) {
                d = data.base;
                ce32 = d.getCE32(c);
            } else {
                d = data;
            }
            if (this.getCEsFromCE32(d, c, ce32)) {
                this.charCEs[i][0] = this.ce0;
                this.charCEs[i][1] = this.ce1;
                this.addUniqueCE(this.ce0);
                this.addUniqueCE(this.ce1);
            } else {
                this.ce0 = 0x101000100L;
                this.charCEs[i][0] = 0x101000100L;
                this.ce1 = 0L;
                this.charCEs[i][1] = 0L;
            }
            if (c == 0 && !CollationFastLatinBuilder.isContractionCharCE(this.ce0)) {
                assert (this.contractionCEs.isEmpty());
                this.addContractionEntry(511, this.ce0, this.ce1);
                this.charCEs[0][0] = 0x180000000L;
                this.charCEs[0][1] = 0L;
            }
            ++i;
            c = (char)(c + 1);
        }
        this.contractionCEs.addElement(511L);
    }

    private boolean getCEsFromCE32(CollationData data, int c, int ce32) {
        int sc0;
        ce32 = data.getFinalCE32(ce32);
        this.ce1 = 0L;
        if (Collation.isSimpleOrLongCE32(ce32)) {
            this.ce0 = Collation.ceFromCE32(ce32);
        } else {
            switch (Collation.tagFromCE32(ce32)) {
                case 4: {
                    this.ce0 = Collation.latinCE0FromCE32(ce32);
                    this.ce1 = Collation.latinCE1FromCE32(ce32);
                    break;
                }
                case 5: {
                    int index = Collation.indexFromCE32(ce32);
                    int length = Collation.lengthFromCE32(ce32);
                    if (length <= 2) {
                        this.ce0 = Collation.ceFromCE32(data.ce32s[index]);
                        if (length != 2) break;
                        this.ce1 = Collation.ceFromCE32(data.ce32s[index + 1]);
                        break;
                    }
                    return false;
                }
                case 6: {
                    int index = Collation.indexFromCE32(ce32);
                    int length = Collation.lengthFromCE32(ce32);
                    if (length <= 2) {
                        this.ce0 = data.ces[index];
                        if (length != 2) break;
                        this.ce1 = data.ces[index + 1];
                        break;
                    }
                    return false;
                }
                case 9: {
                    assert (c >= 0);
                    return this.getCEsFromContractionCE32(data, ce32);
                }
                case 14: {
                    assert (c >= 0);
                    this.ce0 = data.getCEFromOffsetCE32(c, ce32);
                    break;
                }
                default: {
                    return false;
                }
            }
        }
        if (this.ce0 == 0L) {
            return this.ce1 == 0L;
        }
        long p0 = this.ce0 >>> 32;
        if (p0 == 0L) {
            return false;
        }
        if (p0 > this.lastLatinPrimary) {
            return false;
        }
        int lower32_0 = (int)this.ce0;
        if (p0 < this.firstShortPrimary && (sc0 = lower32_0 & 0xFFFFC000) != 0x5000000) {
            return false;
        }
        if ((lower32_0 & 0x3F3F) < 1280) {
            return false;
        }
        if (this.ce1 != 0L) {
            int sc1;
            long p1 = this.ce1 >>> 32;
            if (p1 == 0L ? p0 < this.firstShortPrimary : !this.inSameGroup(p0, p1)) {
                return false;
            }
            int lower32_1 = (int)this.ce1;
            if (lower32_1 >>> 16 == 0) {
                return false;
            }
            if (p1 != 0L && p1 < this.firstShortPrimary && (sc1 = lower32_1 & 0xFFFFC000) != 0x5000000) {
                return false;
            }
            if ((lower32_0 & 0x3F3F) < 1280) {
                return false;
            }
        }
        return ((this.ce0 | this.ce1) & 0xC0L) == 0L;
    }

    private boolean getCEsFromContractionCE32(CollationData data, int ce32) {
        int trieIndex = Collation.indexFromCE32(ce32);
        ce32 = data.getCE32FromContexts(trieIndex);
        assert (!Collation.isContractionCE32(ce32));
        int contractionIndex = this.contractionCEs.size();
        if (this.getCEsFromCE32(data, -1, ce32)) {
            this.addContractionEntry(511, this.ce0, this.ce1);
        } else {
            this.addContractionEntry(511, 0x101000100L, 0L);
        }
        int prevX = -1;
        boolean addContraction = false;
        CharsTrie.Iterator suffixes = CharsTrie.iterator(data.contexts, trieIndex + 2, 0);
        while (suffixes.hasNext()) {
            CharsTrie.Entry entry = suffixes.next();
            CharSequence suffix = entry.chars;
            int x = CollationFastLatin.getCharIndex(suffix.charAt(0));
            if (x < 0) continue;
            if (x == prevX) {
                if (!addContraction) continue;
                this.addContractionEntry(x, 0x101000100L, 0L);
                addContraction = false;
                continue;
            }
            if (addContraction) {
                this.addContractionEntry(prevX, this.ce0, this.ce1);
            }
            ce32 = entry.value;
            if (suffix.length() == 1 && this.getCEsFromCE32(data, -1, ce32)) {
                addContraction = true;
            } else {
                this.addContractionEntry(x, 0x101000100L, 0L);
                addContraction = false;
            }
            prevX = x;
        }
        if (addContraction) {
            this.addContractionEntry(prevX, this.ce0, this.ce1);
        }
        this.ce0 = 0x180000000L | (long)contractionIndex;
        this.ce1 = 0L;
        return true;
    }

    private void addContractionEntry(int x, long cce0, long cce1) {
        this.contractionCEs.addElement(x);
        this.contractionCEs.addElement(cce0);
        this.contractionCEs.addElement(cce1);
        this.addUniqueCE(cce0);
        this.addUniqueCE(cce1);
    }

    private void addUniqueCE(long ce) {
        if (ce == 0L || ce >>> 32 == 1L) {
            return;
        }
        int i = CollationFastLatinBuilder.binarySearch(this.uniqueCEs.getBuffer(), this.uniqueCEs.size(), ce &= 0xFFFFFFFFFFFF3FFFL);
        if (i < 0) {
            this.uniqueCEs.insertElementAt(ce, ~i);
        }
    }

    private int getMiniCE(long ce) {
        int index = CollationFastLatinBuilder.binarySearch(this.uniqueCEs.getBuffer(), this.uniqueCEs.size(), ce &= 0xFFFFFFFFFFFF3FFFL);
        assert (index >= 0);
        return this.miniCEs[index];
    }

    /*
     * Unable to fully structure code
     */
    private void encodeUniqueCEs() {
        this.miniCEs = new char[this.uniqueCEs.size()];
        group = 0;
        lastGroupPrimary = this.lastSpecialPrimaries[group];
        if (!CollationFastLatinBuilder.$assertionsDisabled && (int)this.uniqueCEs.elementAti(0) >>> 16 == 0) {
            throw new AssertionError();
        }
        prevPrimary = 0L;
        prevSecondary = 0;
        pri = 0;
        sec = 0;
        ter = 0;
        for (i = 0; i < this.uniqueCEs.size(); ++i) {
            block34: {
                block36: {
                    block35: {
                        block33: {
                            ce = this.uniqueCEs.elementAti(i);
                            p = ce >>> 32;
                            if (p == prevPrimary) break block33;
                            while (p > lastGroupPrimary) {
                                if (!CollationFastLatinBuilder.$assertionsDisabled && pri > 4088) {
                                    throw new AssertionError();
                                }
                                this.result.setCharAt(1 + group, (char)pri);
                                if (++group < 4) {
                                    lastGroupPrimary = this.lastSpecialPrimaries[group];
                                    continue;
                                }
                                lastGroupPrimary = 0xFFFFFFFFL;
                                break;
                            }
                            if (p >= this.firstShortPrimary) ** GOTO lbl33
                            if (pri == 0) {
                                pri = 3072;
                            } else if (pri < 4088) {
                                pri += 8;
                            } else {
                                this.miniCEs[i] = '\u0001';
                                continue;
lbl33:
                                // 1 sources

                                if (pri < 4096) {
                                    pri = 4096;
                                } else if (pri < 63488) {
                                    pri += 1024;
                                } else {
                                    this.shortPrimaryOverflow = true;
                                    this.miniCEs[i] = '\u0001';
                                    continue;
                                }
                            }
                            prevPrimary = p;
                            prevSecondary = 1280;
                            sec = 160;
                            ter = 0;
                        }
                        if ((s = (lower32 = (int)ce) >>> 16) == prevSecondary) break block34;
                        if (pri != 0) break block35;
                        if (sec == 0) {
                            sec = 384;
                        } else if (sec < 992) {
                            sec += 32;
                        } else {
                            this.miniCEs[i] = '\u0001';
                            continue;
                        }
                        prevSecondary = s;
                        ter = 0;
                        break block36;
                    }
                    if (s >= 1280) ** GOTO lbl70
                    if (sec == 160) {
                        sec = 0;
                    } else if (sec < 128) {
                        sec += 32;
                    } else {
                        this.miniCEs[i] = '\u0001';
                        continue;
lbl70:
                        // 1 sources

                        if (s == 1280) {
                            sec = 160;
                        } else if (sec < 192) {
                            sec = 192;
                        } else if (sec < 352) {
                            sec += 32;
                        } else {
                            this.miniCEs[i] = '\u0001';
                            continue;
                        }
                    }
                }
                prevSecondary = s;
                ter = 0;
            }
            if (!CollationFastLatinBuilder.$assertionsDisabled && (lower32 & 49152) != 0) {
                throw new AssertionError();
            }
            t = lower32 & 16191;
            if (t > 1280) {
                if (ter < 7) {
                    ++ter;
                } else {
                    this.miniCEs[i] = '\u0001';
                    continue;
                }
            }
            if (3072 <= pri && pri <= 4088) {
                if (!CollationFastLatinBuilder.$assertionsDisabled && sec != 160) {
                    throw new AssertionError();
                }
                this.miniCEs[i] = (char)(pri | ter);
                continue;
            }
            this.miniCEs[i] = (char)(pri | sec | ter);
        }
    }

    private void encodeCharCEs() {
        int miniCEsStart = this.result.length();
        for (int i = 0; i < 448; ++i) {
            this.result.append(0);
        }
        int indexBase = this.result.length();
        for (int i = 0; i < 448; ++i) {
            long ce = this.charCEs[i][0];
            if (CollationFastLatinBuilder.isContractionCharCE(ce)) continue;
            int miniCE = this.encodeTwoCEs(ce, this.charCEs[i][1]);
            if (miniCE >>> 16 > 0) {
                int expansionIndex = this.result.length() - indexBase;
                if (expansionIndex > 1023) {
                    miniCE = 1;
                } else {
                    this.result.append((char)(miniCE >> 16)).append((char)miniCE);
                    miniCE = 0x800 | expansionIndex;
                }
            }
            this.result.setCharAt(miniCEsStart + i, (char)miniCE);
        }
    }

    private void encodeContractions() {
        int indexBase = this.headerLength + 448;
        int firstContractionIndex = this.result.length();
        for (int i = 0; i < 448; ++i) {
            long x;
            long ce = this.charCEs[i][0];
            if (!CollationFastLatinBuilder.isContractionCharCE(ce)) continue;
            int contractionIndex = this.result.length() - indexBase;
            if (contractionIndex > 1023) {
                this.result.setCharAt(this.headerLength + i, '\u0001');
                continue;
            }
            boolean firstTriple = true;
            int index = (int)ce & Integer.MAX_VALUE;
            while ((x = this.contractionCEs.elementAti(index)) != 511L || firstTriple) {
                long cce1;
                long cce0 = this.contractionCEs.elementAti(index + 1);
                int miniCE = this.encodeTwoCEs(cce0, cce1 = this.contractionCEs.elementAti(index + 2));
                if (miniCE == 1) {
                    this.result.append((char)(x | 0x200L));
                } else if (miniCE >>> 16 == 0) {
                    this.result.append((char)(x | 0x400L));
                    this.result.append((char)miniCE);
                } else {
                    this.result.append((char)(x | 0x600L));
                    this.result.append((char)(miniCE >> 16)).append((char)miniCE);
                }
                firstTriple = false;
                index += 3;
            }
            this.result.setCharAt(this.headerLength + i, (char)(0x400 | contractionIndex));
        }
        if (this.result.length() > firstContractionIndex) {
            this.result.append('\u01ff');
        }
    }

    private int encodeTwoCEs(long first, long second) {
        if (first == 0L) {
            return 0;
        }
        if (first == 0x101000100L) {
            return 1;
        }
        assert (first >>> 32 != 1L);
        int miniCE = this.getMiniCE(first);
        if (miniCE == 1) {
            return miniCE;
        }
        if (miniCE >= 4096) {
            int c = ((int)first & 0xC000) >> 11;
            miniCE |= (c += 8);
        }
        if (second == 0L) {
            return miniCE;
        }
        int miniCE1 = this.getMiniCE(second);
        if (miniCE1 == 1) {
            return miniCE1;
        }
        int case1 = (int)second & 0xC000;
        if (miniCE >= 4096 && (miniCE & 0x3E0) == 160) {
            int sec1 = miniCE1 & 0x3E0;
            int ter1 = miniCE1 & 7;
            if (sec1 >= 384 && case1 == 0 && ter1 == 0) {
                return miniCE & 0xFFFFFC1F | sec1;
            }
        }
        if (miniCE1 <= 992 || 4096 <= miniCE1) {
            case1 = (case1 >> 11) + 8;
            miniCE1 |= case1;
        }
        return miniCE << 16 | miniCE1;
    }

    private static boolean isContractionCharCE(long ce) {
        return ce >>> 32 == 1L && ce != 0x101000100L;
    }
}
