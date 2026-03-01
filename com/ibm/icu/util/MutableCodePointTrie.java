package com.ibm.icu.util;

import com.ibm.icu.util.CodePointMap;
import com.ibm.icu.util.CodePointTrie;
import java.util.Arrays;

public final class MutableCodePointTrie
extends CodePointMap
implements Cloneable {
    private static final int MAX_UNICODE = 0x10FFFF;
    private static final int UNICODE_LIMIT = 0x110000;
    private static final int BMP_LIMIT = 65536;
    private static final int ASCII_LIMIT = 128;
    private static final int I_LIMIT = 69632;
    private static final int BMP_I_LIMIT = 4096;
    private static final int ASCII_I_LIMIT = 8;
    private static final int SMALL_DATA_BLOCKS_PER_BMP_BLOCK = 4;
    private static final byte ALL_SAME = 0;
    private static final byte MIXED = 1;
    private static final byte SAME_AS = 2;
    private static final int INITIAL_DATA_LENGTH = 16384;
    private static final int MEDIUM_DATA_LENGTH = 131072;
    private static final int MAX_DATA_LENGTH = 0x110000;
    private static final byte I3_NULL = 0;
    private static final byte I3_BMP = 1;
    private static final byte I3_16 = 2;
    private static final byte I3_18 = 3;
    private static final int INDEX_3_18BIT_BLOCK_LENGTH = 36;
    private int[] index;
    private int index3NullOffset = -1;
    private int[] data;
    private int dataLength;
    private int dataNullOffset = -1;
    private int origInitialValue;
    private int initialValue;
    private int errorValue;
    private int highStart;
    private int highValue;
    private char[] index16;
    private byte[] flags = new byte[69632];

    public MutableCodePointTrie(int initialValue, int errorValue) {
        this.index = new int[4096];
        this.data = new int[16384];
        this.origInitialValue = initialValue;
        this.initialValue = initialValue;
        this.errorValue = errorValue;
        this.highValue = initialValue;
    }

    public MutableCodePointTrie clone() {
        try {
            MutableCodePointTrie builder = (MutableCodePointTrie)super.clone();
            int iCapacity = this.highStart <= 65536 ? 4096 : 69632;
            builder.index = new int[iCapacity];
            builder.flags = new byte[69632];
            int iLimit = this.highStart >> 4;
            for (int i = 0; i < iLimit; ++i) {
                builder.index[i] = this.index[i];
                builder.flags[i] = this.flags[i];
            }
            builder.index3NullOffset = this.index3NullOffset;
            builder.data = (int[])this.data.clone();
            builder.dataLength = this.dataLength;
            builder.dataNullOffset = this.dataNullOffset;
            builder.origInitialValue = this.origInitialValue;
            builder.initialValue = this.initialValue;
            builder.errorValue = this.errorValue;
            builder.highStart = this.highStart;
            builder.highValue = this.highValue;
            assert (this.index16 == null);
            return builder;
        }
        catch (CloneNotSupportedException ignored) {
            return null;
        }
    }

    public static MutableCodePointTrie fromCodePointMap(CodePointMap map) {
        int errorValue = map.get(-1);
        int initialValue = map.get(0x10FFFF);
        MutableCodePointTrie mutableTrie = new MutableCodePointTrie(initialValue, errorValue);
        CodePointMap.Range range = new CodePointMap.Range();
        int start = 0;
        while (map.getRange(start, null, range)) {
            int end = range.getEnd();
            int value = range.getValue();
            if (value != initialValue) {
                if (start == end) {
                    mutableTrie.set(start, value);
                } else {
                    mutableTrie.setRange(start, end, value);
                }
            }
            start = end + 1;
        }
        return mutableTrie;
    }

    private void clear() {
        this.dataNullOffset = -1;
        this.index3NullOffset = -1;
        this.dataLength = 0;
        this.highValue = this.initialValue = this.origInitialValue;
        this.highStart = 0;
        this.index16 = null;
    }

    @Override
    public int get(int c) {
        if (c < 0 || 0x10FFFF < c) {
            return this.errorValue;
        }
        if (c >= this.highStart) {
            return this.highValue;
        }
        int i = c >> 4;
        if (this.flags[i] == 0) {
            return this.index[i];
        }
        return this.data[this.index[i] + (c & 0xF)];
    }

    private static final int maybeFilterValue(int value, int initialValue, int nullValue, CodePointMap.ValueFilter filter) {
        if (value == initialValue) {
            value = nullValue;
        } else if (filter != null) {
            value = filter.apply(value);
        }
        return value;
    }

    @Override
    public boolean getRange(int start, CodePointMap.ValueFilter filter, CodePointMap.Range range) {
        if (start < 0 || 0x10FFFF < start) {
            return false;
        }
        if (start >= this.highStart) {
            int value = this.highValue;
            if (filter != null) {
                value = filter.apply(value);
            }
            range.set(start, 0x10FFFF, value);
            return true;
        }
        int nullValue = this.initialValue;
        if (filter != null) {
            nullValue = filter.apply(nullValue);
        }
        int c = start;
        int trieValue = 0;
        int value = 0;
        boolean haveValue = false;
        int i = c >> 4;
        do {
            if (this.flags[i] == 0) {
                int trieValue2 = this.index[i];
                if (haveValue) {
                    if (trieValue2 != trieValue) {
                        if (filter == null || MutableCodePointTrie.maybeFilterValue(trieValue2, this.initialValue, nullValue, filter) != value) {
                            range.set(start, c - 1, value);
                            return true;
                        }
                        trieValue = trieValue2;
                    }
                } else {
                    trieValue = trieValue2;
                    value = MutableCodePointTrie.maybeFilterValue(trieValue2, this.initialValue, nullValue, filter);
                    haveValue = true;
                }
                c = c + 16 & 0xFFFFFFF0;
            } else {
                int di = this.index[i] + (c & 0xF);
                int trieValue2 = this.data[di];
                if (haveValue) {
                    if (trieValue2 != trieValue) {
                        if (filter == null || MutableCodePointTrie.maybeFilterValue(trieValue2, this.initialValue, nullValue, filter) != value) {
                            range.set(start, c - 1, value);
                            return true;
                        }
                        trieValue = trieValue2;
                    }
                } else {
                    trieValue = trieValue2;
                    value = MutableCodePointTrie.maybeFilterValue(trieValue2, this.initialValue, nullValue, filter);
                    haveValue = true;
                }
                while ((++c & 0xF) != 0) {
                    if ((trieValue2 = this.data[++di]) == trieValue) continue;
                    if (filter == null || MutableCodePointTrie.maybeFilterValue(trieValue2, this.initialValue, nullValue, filter) != value) {
                        range.set(start, c - 1, value);
                        return true;
                    }
                    trieValue = trieValue2;
                }
            }
            ++i;
        } while (c < this.highStart);
        assert (haveValue);
        if (MutableCodePointTrie.maybeFilterValue(this.highValue, this.initialValue, nullValue, filter) != value) {
            range.set(start, c - 1, value);
        } else {
            range.set(start, 0x10FFFF, value);
        }
        return true;
    }

    private void writeBlock(int block, int value) {
        int limit = block + 16;
        Arrays.fill(this.data, block, limit, value);
    }

    public void set(int c, int value) {
        if (c < 0 || 0x10FFFF < c) {
            throw new IllegalArgumentException("invalid code point");
        }
        this.ensureHighStart(c);
        int block = this.getDataBlock(c >> 4);
        this.data[block + (c & 0xF)] = value;
    }

    private void fillBlock(int block, int start, int limit, int value) {
        Arrays.fill(this.data, block + start, block + limit, value);
    }

    public void setRange(int start, int end, int value) {
        if (start < 0 || 0x10FFFF < start || end < 0 || 0x10FFFF < end || start > end) {
            throw new IllegalArgumentException("invalid code point range");
        }
        this.ensureHighStart(end);
        int limit = end + 1;
        if ((start & 0xF) != 0) {
            int block = this.getDataBlock(start >> 4);
            int nextStart = start + 15 & 0xFFFFFFF0;
            if (nextStart <= limit) {
                this.fillBlock(block, start & 0xF, 16, value);
                start = nextStart;
            } else {
                this.fillBlock(block, start & 0xF, limit & 0xF, value);
                return;
            }
        }
        int rest = limit & 0xF;
        limit &= 0xFFFFFFF0;
        while (start < limit) {
            int i = start >> 4;
            if (this.flags[i] == 0) {
                this.index[i] = value;
            } else {
                this.fillBlock(this.index[i], 0, 16, value);
            }
            start += 16;
        }
        if (rest > 0) {
            int block = this.getDataBlock(start >> 4);
            this.fillBlock(block, 0, rest, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CodePointTrie buildImmutable(CodePointTrie.Type type, CodePointTrie.ValueWidth valueWidth) {
        if (type == null || valueWidth == null) {
            throw new IllegalArgumentException("The type and valueWidth must be specified.");
        }
        try {
            CodePointTrie codePointTrie = this.build(type, valueWidth);
            return codePointTrie;
        }
        finally {
            this.clear();
        }
    }

    private void ensureHighStart(int c) {
        if (c >= this.highStart) {
            c = c + 512 & 0xFFFFFE00;
            int i = this.highStart >> 4;
            int iLimit = c >> 4;
            if (iLimit > this.index.length) {
                int[] newIndex = new int[69632];
                for (int j = 0; j < i; ++j) {
                    newIndex[j] = this.index[j];
                }
                this.index = newIndex;
            }
            do {
                this.flags[i] = 0;
                this.index[i] = this.initialValue;
            } while (++i < iLimit);
            this.highStart = c;
        }
    }

    private int allocDataBlock(int blockLength) {
        int newBlock = this.dataLength;
        int newTop = newBlock + blockLength;
        if (newTop > this.data.length) {
            int capacity;
            if (this.data.length < 131072) {
                capacity = 131072;
            } else if (this.data.length < 0x110000) {
                capacity = 0x110000;
            } else {
                throw new AssertionError();
            }
            int[] newData = new int[capacity];
            for (int j = 0; j < this.dataLength; ++j) {
                newData[j] = this.data[j];
            }
            this.data = newData;
        }
        this.dataLength = newTop;
        return newBlock;
    }

    private int getDataBlock(int i) {
        if (this.flags[i] == 1) {
            return this.index[i];
        }
        if (i < 4096) {
            int newBlock = this.allocDataBlock(64);
            int iStart = i & 0xFFFFFFFC;
            int iLimit = iStart + 4;
            do {
                assert (this.flags[iStart] == 0);
                this.writeBlock(newBlock, this.index[iStart]);
                this.flags[iStart] = 1;
                this.index[iStart++] = newBlock;
                newBlock += 16;
            } while (iStart < iLimit);
            return this.index[i];
        }
        int newBlock = this.allocDataBlock(16);
        if (newBlock < 0) {
            return newBlock;
        }
        this.writeBlock(newBlock, this.index[i]);
        this.flags[i] = 1;
        this.index[i] = newBlock;
        return newBlock;
    }

    private void maskValues(int mask) {
        int i;
        this.initialValue &= mask;
        this.errorValue &= mask;
        this.highValue &= mask;
        int iLimit = this.highStart >> 4;
        for (i = 0; i < iLimit; ++i) {
            if (this.flags[i] != 0) continue;
            int n = i;
            this.index[n] = this.index[n] & mask;
        }
        i = 0;
        while (i < this.dataLength) {
            int n = i++;
            this.data[n] = this.data[n] & mask;
        }
    }

    private static boolean equalBlocks(int[] s, int si, int[] t, int ti, int length) {
        while (length > 0 && s[si] == t[ti]) {
            ++si;
            ++ti;
            --length;
        }
        return length == 0;
    }

    private static boolean equalBlocks(char[] s, int si, int[] t, int ti, int length) {
        while (length > 0 && s[si] == t[ti]) {
            ++si;
            ++ti;
            --length;
        }
        return length == 0;
    }

    private static boolean equalBlocks(char[] s, int si, char[] t, int ti, int length) {
        while (length > 0 && s[si] == t[ti]) {
            ++si;
            ++ti;
            --length;
        }
        return length == 0;
    }

    private static boolean allValuesSameAs(int[] p, int pi, int length, int value) {
        int pLimit = pi + length;
        while (pi < pLimit && p[pi] == value) {
            ++pi;
        }
        return pi == pLimit;
    }

    private static int findSameBlock(char[] p, int pStart, int length, char[] q, int qStart, int blockLength) {
        length -= blockLength;
        while (pStart <= length) {
            if (MutableCodePointTrie.equalBlocks(p, pStart, q, qStart, blockLength)) {
                return pStart;
            }
            ++pStart;
        }
        return -1;
    }

    private static int findAllSameBlock(int[] p, int start, int limit, int value, int blockLength) {
        limit -= blockLength;
        block0: for (int block = start; block <= limit; ++block) {
            if (p[block] != value) continue;
            int i = 1;
            while (i != blockLength) {
                if (p[block + i] != value) {
                    block += i;
                    continue block0;
                }
                ++i;
            }
            return block;
        }
        return -1;
    }

    private static int getOverlap(int[] p, int length, int[] q, int qStart, int blockLength) {
        int overlap;
        assert (overlap <= length);
        for (overlap = blockLength - 1; overlap > 0 && !MutableCodePointTrie.equalBlocks(p, length - overlap, q, qStart, overlap); --overlap) {
        }
        return overlap;
    }

    private static int getOverlap(char[] p, int length, int[] q, int qStart, int blockLength) {
        int overlap;
        assert (overlap <= length);
        for (overlap = blockLength - 1; overlap > 0 && !MutableCodePointTrie.equalBlocks(p, length - overlap, q, qStart, overlap); --overlap) {
        }
        return overlap;
    }

    private static int getOverlap(char[] p, int length, char[] q, int qStart, int blockLength) {
        int overlap;
        assert (overlap <= length);
        for (overlap = blockLength - 1; overlap > 0 && !MutableCodePointTrie.equalBlocks(p, length - overlap, q, qStart, overlap); --overlap) {
        }
        return overlap;
    }

    private static int getAllSameOverlap(int[] p, int length, int value, int blockLength) {
        int i;
        int min = length - (blockLength - 1);
        for (i = length; min < i && p[i - 1] == value; --i) {
        }
        return length - i;
    }

    private static boolean isStartOfSomeFastBlock(int dataOffset, int[] index, int fastILimit) {
        for (int i = 0; i < fastILimit; i += 4) {
            if (index[i] != dataOffset) continue;
            return true;
        }
        return false;
    }

    private int findHighStart() {
        int i = this.highStart >> 4;
        while (i > 0) {
            boolean match;
            if (this.flags[--i] == 0) {
                match = this.index[i] == this.highValue;
            } else {
                int p = this.index[i];
                int j = 0;
                while (true) {
                    if (j == 16) {
                        match = true;
                        break;
                    }
                    if (this.data[p + j] != this.highValue) {
                        match = false;
                        break;
                    }
                    ++j;
                }
            }
            if (match) continue;
            return i + 1 << 4;
        }
        return 0;
    }

    /*
     * Unable to fully structure code
     */
    private int compactWholeDataBlocks(int fastILimit, AllSameBlocks allSameBlocks) {
        newDataCapacity = 128;
        newDataCapacity += 16;
        newDataCapacity += 4;
        iLimit = this.highStart >> 4;
        blockLength = 64;
        inc = 4;
        for (i = 0; i < iLimit; i += inc) {
            if (i == fastILimit) {
                blockLength = 16;
                inc = 1;
            }
            value = this.index[i];
            if (this.flags[i] != 1) ** GOTO lbl20
            p = value;
            if (MutableCodePointTrie.allValuesSameAs(this.data, p + 1, blockLength - 1, value = this.data[p])) {
                this.flags[i] = 0;
                this.index[i] = value;
            } else {
                newDataCapacity += blockLength;
                continue;
lbl20:
                // 1 sources

                if (!MutableCodePointTrie.$assertionsDisabled && this.flags[i] != 0) {
                    throw new AssertionError();
                }
                if (inc > 1) {
                    allSame = true;
                    next_i = i + inc;
                    for (j = i + 1; j < next_i; ++j) {
                        if (!MutableCodePointTrie.$assertionsDisabled && this.flags[j] != 0) {
                            throw new AssertionError();
                        }
                        if (this.index[j] == value) continue;
                        allSame = false;
                        break;
                    }
                    if (!allSame) {
                        if (this.getDataBlock(i) < 0) {
                            return -1;
                        }
                        newDataCapacity += blockLength;
                        continue;
                    }
                }
            }
            if ((other = allSameBlocks.findOrAdd(i, inc, value)) == -2) {
                jInc = 4;
                j = 0;
                while (true) {
                    if (j == i) {
                        allSameBlocks.add(i, inc, value);
                        break;
                    }
                    if (j == fastILimit) {
                        jInc = 1;
                    }
                    if (this.flags[j] == 0 && this.index[j] == value) {
                        allSameBlocks.add(j, jInc + inc, value);
                        other = j;
                        break;
                    }
                    j += jInc;
                }
            }
            if (other >= 0) {
                this.flags[i] = 2;
                this.index[i] = other;
                continue;
            }
            newDataCapacity += blockLength;
        }
        return newDataCapacity;
    }

    private int compactData(int fastILimit, int[] newData, int dataNullIndex, MixedBlocks mixedBlocks) {
        int newDataLength = 0;
        int i = 0;
        while (newDataLength < 128) {
            this.index[i] = newDataLength;
            newDataLength += 64;
            i += 4;
        }
        int blockLength = 64;
        mixedBlocks.init(newData.length, blockLength);
        mixedBlocks.extend(newData, 0, 0, newDataLength);
        int iLimit = this.highStart >> 4;
        int inc = 4;
        int fastLength = 0;
        for (int i2 = 8; i2 < iLimit; i2 += inc) {
            int prevDataLength;
            int n;
            if (i2 == fastILimit) {
                blockLength = 16;
                inc = 1;
                fastLength = newDataLength;
                mixedBlocks.init(newData.length, blockLength);
                mixedBlocks.extend(newData, 0, 0, newDataLength);
            }
            if (this.flags[i2] == 0) {
                int value = this.index[i2];
                n = mixedBlocks.findAllSameBlock(newData, value);
                while (n >= 0 && i2 == dataNullIndex && i2 >= fastILimit && n < fastLength && MutableCodePointTrie.isStartOfSomeFastBlock(n, this.index, fastILimit)) {
                    n = MutableCodePointTrie.findAllSameBlock(newData, n + 1, newDataLength, value, blockLength);
                }
                if (n >= 0) {
                    this.index[i2] = n;
                    continue;
                }
                this.index[i2] = newDataLength - n;
                prevDataLength = newDataLength;
                for (n = MutableCodePointTrie.getAllSameOverlap(newData, newDataLength, value, blockLength); n < blockLength; ++n) {
                    newData[newDataLength++] = value;
                }
                mixedBlocks.extend(newData, 0, prevDataLength, newDataLength);
                continue;
            }
            if (this.flags[i2] == 1) {
                int block = this.index[i2];
                n = mixedBlocks.findBlock(newData, this.data, block);
                if (n >= 0) {
                    this.index[i2] = n;
                    continue;
                }
                n = MutableCodePointTrie.getOverlap(newData, newDataLength, this.data, block, blockLength);
                this.index[i2] = newDataLength - n;
                prevDataLength = newDataLength;
                while (n < blockLength) {
                    newData[newDataLength++] = this.data[block + n++];
                }
                mixedBlocks.extend(newData, 0, prevDataLength, newDataLength);
                continue;
            }
            int j = this.index[i2];
            this.index[i2] = this.index[j];
        }
        return newDataLength;
    }

    private int compactIndex(int fastILimit, MixedBlocks mixedBlocks) {
        int n;
        int index3Start;
        int fastIndexLength = fastILimit >> 2;
        if (this.highStart >> 6 <= fastIndexLength) {
            this.index3NullOffset = Short.MAX_VALUE;
            return fastIndexLength;
        }
        char[] fastIndex = new char[fastIndexLength];
        int i3FirstNull = -1;
        int i = 0;
        int j = 0;
        while (i < fastILimit) {
            int i3 = this.index[i];
            fastIndex[j] = (char)i3;
            if (i3 == this.dataNullOffset) {
                if (i3FirstNull < 0) {
                    i3FirstNull = j;
                } else if (this.index3NullOffset < 0 && j - i3FirstNull + 1 == 32) {
                    this.index3NullOffset = i3FirstNull;
                }
            } else {
                i3FirstNull = -1;
            }
            int iNext = i + 4;
            while (++i < iNext) {
                this.index[i] = i3 += 16;
            }
            ++j;
        }
        mixedBlocks.init(fastIndexLength, 32);
        mixedBlocks.extend(fastIndex, 0, 0, fastIndexLength);
        int index3Capacity = 0;
        i3FirstNull = this.index3NullOffset;
        boolean hasLongI3Blocks = false;
        int iStart = fastILimit < 4096 ? 0 : 4096;
        int iLimit = this.highStart >> 4;
        int i2 = iStart;
        while (i2 < iLimit) {
            int j2 = i2;
            int jLimit = i2 + 32;
            int oredI3 = 0;
            boolean isNull = true;
            do {
                int i3 = this.index[j2];
                oredI3 |= i3;
                if (i3 == this.dataNullOffset) continue;
                isNull = false;
            } while (++j2 < jLimit);
            if (isNull) {
                this.flags[i2] = 0;
                if (i3FirstNull < 0) {
                    if (oredI3 <= 65535) {
                        index3Capacity += 32;
                    } else {
                        index3Capacity += 36;
                        hasLongI3Blocks = true;
                    }
                    i3FirstNull = 0;
                }
            } else if (oredI3 <= 65535) {
                int n2 = mixedBlocks.findBlock(fastIndex, this.index, i2);
                if (n2 >= 0) {
                    this.flags[i2] = 1;
                    this.index[i2] = n2;
                } else {
                    this.flags[i2] = 2;
                    index3Capacity += 32;
                }
            } else {
                this.flags[i2] = 3;
                index3Capacity += 36;
                hasLongI3Blocks = true;
            }
            i2 = j2;
        }
        int index2Capacity = iLimit - iStart >> 5;
        int index1Length = index2Capacity + 31 >> 5;
        int index16Capacity = fastIndexLength + index1Length + index3Capacity + index2Capacity + 1;
        this.index16 = Arrays.copyOf(fastIndex, index16Capacity);
        mixedBlocks.init(index16Capacity, 32);
        MixedBlocks longI3Blocks = null;
        if (hasLongI3Blocks) {
            longI3Blocks = new MixedBlocks();
            longI3Blocks.init(index16Capacity, 36);
        }
        char[] index2 = new char[index2Capacity];
        int i2Length = 0;
        i3FirstNull = this.index3NullOffset;
        int indexLength = index3Start = fastIndexLength + index1Length;
        for (int i3 = iStart; i3 < iLimit; i3 += 32) {
            int i32;
            int f = this.flags[i3];
            if (f == 0 && i3FirstNull < 0) {
                f = this.dataNullOffset <= 65535 ? 2 : 3;
                i3FirstNull = 0;
            }
            if (f == 0) {
                i32 = this.index3NullOffset;
            } else if (f == 1) {
                i32 = this.index[i3];
            } else if (f == 2) {
                n = mixedBlocks.findBlock(this.index16, this.index, i3);
                if (n >= 0) {
                    i32 = n;
                } else {
                    n = indexLength == index3Start ? 0 : MutableCodePointTrie.getOverlap(this.index16, indexLength, this.index, i3, 32);
                    i32 = indexLength - n;
                    int prevIndexLength = indexLength;
                    while (n < 32) {
                        this.index16[indexLength++] = (char)this.index[i3 + n++];
                    }
                    mixedBlocks.extend(this.index16, index3Start, prevIndexLength, indexLength);
                    if (hasLongI3Blocks) {
                        longI3Blocks.extend(this.index16, index3Start, prevIndexLength, indexLength);
                    }
                }
            } else {
                assert (f == 3);
                assert (hasLongI3Blocks);
                int j3 = i3;
                int jLimit = i3 + 32;
                int k = indexLength;
                do {
                    int v = this.index[j3++];
                    int upperBits = (v & 0x30000) >> 2;
                    int n3 = ++k;
                    this.index16[n3] = (char)v;
                    v = this.index[j3++];
                    upperBits |= (v & 0x30000) >> 4;
                    int n4 = ++k;
                    this.index16[n4] = (char)v;
                    v = this.index[j3++];
                    upperBits |= (v & 0x30000) >> 6;
                    int n5 = ++k;
                    this.index16[n5] = (char)v;
                    v = this.index[j3++];
                    upperBits |= (v & 0x30000) >> 8;
                    int n6 = ++k;
                    this.index16[n6] = (char)v;
                    v = this.index[j3++];
                    upperBits |= (v & 0x30000) >> 10;
                    int n7 = ++k;
                    this.index16[n7] = (char)v;
                    v = this.index[j3++];
                    upperBits |= (v & 0x30000) >> 12;
                    int n8 = ++k;
                    this.index16[n8] = (char)v;
                    v = this.index[j3++];
                    upperBits |= (v & 0x30000) >> 14;
                    int n9 = ++k;
                    this.index16[n9] = (char)v;
                    v = this.index[j3++];
                    int n10 = ++k;
                    this.index16[n10] = (char)v;
                    this.index16[++k - 9] = (char)(upperBits |= (v & 0x30000) >> 16);
                } while (j3 < jLimit);
                int n11 = longI3Blocks.findBlock(this.index16, this.index16, indexLength);
                if (n11 >= 0) {
                    i32 = n11 | 0x8000;
                } else {
                    n11 = indexLength == index3Start ? 0 : MutableCodePointTrie.getOverlap(this.index16, indexLength, this.index16, indexLength, 36);
                    i32 = indexLength - n11 | 0x8000;
                    int prevIndexLength = indexLength;
                    if (n11 > 0) {
                        int start = indexLength;
                        while (n11 < 36) {
                            this.index16[indexLength++] = this.index16[start + n11++];
                        }
                    } else {
                        indexLength += 36;
                    }
                    mixedBlocks.extend(this.index16, index3Start, prevIndexLength, indexLength);
                    if (hasLongI3Blocks) {
                        longI3Blocks.extend(this.index16, index3Start, prevIndexLength, indexLength);
                    }
                }
            }
            if (this.index3NullOffset < 0 && i3FirstNull >= 0) {
                this.index3NullOffset = i32;
            }
            index2[i2Length++] = (char)i32;
        }
        assert (i2Length == index2Capacity);
        assert (indexLength <= index3Start + index3Capacity);
        if (this.index3NullOffset < 0) {
            this.index3NullOffset = Short.MAX_VALUE;
        }
        if (indexLength >= 32799) {
            throw new IndexOutOfBoundsException("The trie data exceeds limitations of the data structure.");
        }
        int blockLength = 32;
        int i1 = fastIndexLength;
        for (int i4 = 0; i4 < i2Length; i4 += blockLength) {
            int i22;
            if (i2Length - i4 >= blockLength) {
                assert (blockLength == 32);
                n = mixedBlocks.findBlock(this.index16, index2, i4);
            } else {
                blockLength = i2Length - i4;
                n = MutableCodePointTrie.findSameBlock(this.index16, index3Start, indexLength, index2, i4, blockLength);
            }
            if (n >= 0) {
                i22 = n;
            } else {
                n = indexLength == index3Start ? 0 : MutableCodePointTrie.getOverlap(this.index16, indexLength, index2, i4, blockLength);
                i22 = indexLength - n;
                int prevIndexLength = indexLength;
                while (n < blockLength) {
                    this.index16[indexLength++] = index2[i4 + n++];
                }
                mixedBlocks.extend(this.index16, index3Start, prevIndexLength, indexLength);
            }
            this.index16[i1++] = (char)i22;
        }
        assert (i1 == index3Start);
        assert (indexLength <= index16Capacity);
        return indexLength;
    }

    private int compactTrie(int fastILimit) {
        int fastLimit;
        assert ((this.highStart & 0x1FF) == 0);
        this.highValue = this.get(0x10FFFF);
        int realHighStart = this.findHighStart();
        if ((realHighStart = realHighStart + 511 & 0xFFFFFE00) == 0x110000) {
            this.highValue = this.initialValue;
        }
        if (realHighStart < (fastLimit = fastILimit << 4)) {
            for (int i = realHighStart >> 4; i < fastILimit; ++i) {
                this.flags[i] = 0;
                this.index[i] = this.highValue;
            }
            this.highStart = fastLimit;
        } else {
            this.highStart = realHighStart;
        }
        int[] asciiData = new int[128];
        for (int i = 0; i < 128; ++i) {
            asciiData[i] = this.get(i);
        }
        AllSameBlocks allSameBlocks = new AllSameBlocks();
        int newDataCapacity = this.compactWholeDataBlocks(fastILimit, allSameBlocks);
        int[] newData = Arrays.copyOf(asciiData, newDataCapacity);
        int dataNullIndex = allSameBlocks.findMostUsed();
        MixedBlocks mixedBlocks = new MixedBlocks();
        int newDataLength = this.compactData(fastILimit, newData, dataNullIndex, mixedBlocks);
        assert (newDataLength <= newDataCapacity);
        this.data = newData;
        this.dataLength = newDataLength;
        if (this.dataLength > 262159) {
            throw new IndexOutOfBoundsException("The trie data exceeds limitations of the data structure.");
        }
        if (dataNullIndex >= 0) {
            this.dataNullOffset = this.index[dataNullIndex];
            this.initialValue = this.data[this.dataNullOffset];
        } else {
            this.dataNullOffset = 1048575;
        }
        int indexLength = this.compactIndex(fastILimit, mixedBlocks);
        this.highStart = realHighStart;
        return indexLength;
    }

    private CodePointTrie build(CodePointTrie.Type type, CodePointTrie.ValueWidth valueWidth) {
        char[] trieIndex;
        switch (valueWidth) {
            case BITS_32: {
                break;
            }
            case BITS_16: {
                this.maskValues(65535);
                break;
            }
            case BITS_8: {
                this.maskValues(255);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        int fastLimit = type == CodePointTrie.Type.FAST ? 65536 : 4096;
        int indexLength = this.compactTrie(fastLimit >> 4);
        if (valueWidth == CodePointTrie.ValueWidth.BITS_32 && (indexLength & 1) != 0) {
            this.index16[indexLength++] = 65518;
        }
        int length = indexLength * 2;
        if (valueWidth == CodePointTrie.ValueWidth.BITS_16) {
            if (((indexLength ^ this.dataLength) & 1) != 0) {
                this.data[this.dataLength++] = this.errorValue;
            }
            if (this.data[this.dataLength - 1] != this.errorValue || this.data[this.dataLength - 2] != this.highValue) {
                this.data[this.dataLength++] = this.highValue;
                this.data[this.dataLength++] = this.errorValue;
            }
            length += this.dataLength * 2;
        } else if (valueWidth == CodePointTrie.ValueWidth.BITS_32) {
            if (this.data[this.dataLength - 1] != this.errorValue || this.data[this.dataLength - 2] != this.highValue) {
                if (this.data[this.dataLength - 1] != this.highValue) {
                    this.data[this.dataLength++] = this.highValue;
                }
                this.data[this.dataLength++] = this.errorValue;
            }
            length += this.dataLength * 4;
        } else {
            int and3 = length + this.dataLength & 3;
            if (and3 != 0 || this.data[this.dataLength - 1] != this.errorValue || this.data[this.dataLength - 2] != this.highValue) {
                if (and3 == 3 && this.data[this.dataLength - 1] == this.highValue) {
                    this.data[this.dataLength++] = this.errorValue;
                } else {
                    while (and3 != 2) {
                        this.data[this.dataLength++] = this.highValue;
                        and3 = and3 + 1 & 3;
                    }
                    this.data[this.dataLength++] = this.highValue;
                    this.data[this.dataLength++] = this.errorValue;
                }
            }
            length += this.dataLength;
        }
        assert ((length & 3) == 0);
        if (this.highStart <= fastLimit) {
            trieIndex = new char[indexLength];
            int i = 0;
            for (int j = 0; j < indexLength; ++j) {
                trieIndex[j] = (char)this.index[i];
                i += 4;
            }
        } else if (indexLength == this.index16.length) {
            trieIndex = this.index16;
            this.index16 = null;
        } else {
            trieIndex = Arrays.copyOf(this.index16, indexLength);
        }
        switch (valueWidth) {
            case BITS_16: {
                int i;
                char[] data16 = new char[this.dataLength];
                for (i = 0; i < this.dataLength; ++i) {
                    data16[i] = (char)this.data[i];
                }
                return type == CodePointTrie.Type.FAST ? new CodePointTrie.Fast16(trieIndex, data16, this.highStart, this.index3NullOffset, this.dataNullOffset) : new CodePointTrie.Small16(trieIndex, data16, this.highStart, this.index3NullOffset, this.dataNullOffset);
            }
            case BITS_32: {
                int[] data32 = Arrays.copyOf(this.data, this.dataLength);
                return type == CodePointTrie.Type.FAST ? new CodePointTrie.Fast32(trieIndex, data32, this.highStart, this.index3NullOffset, this.dataNullOffset) : new CodePointTrie.Small32(trieIndex, data32, this.highStart, this.index3NullOffset, this.dataNullOffset);
            }
            case BITS_8: {
                int i;
                byte[] data8 = new byte[this.dataLength];
                for (i = 0; i < this.dataLength; ++i) {
                    data8[i] = (byte)this.data[i];
                }
                return type == CodePointTrie.Type.FAST ? new CodePointTrie.Fast8(trieIndex, data8, this.highStart, this.index3NullOffset, this.dataNullOffset) : new CodePointTrie.Small8(trieIndex, data8, this.highStart, this.index3NullOffset, this.dataNullOffset);
            }
        }
        throw new IllegalArgumentException();
    }

    private static final class MixedBlocks {
        private int[] table;
        private int length;
        private int shift;
        private int mask;
        private int blockLength;

        private MixedBlocks() {
        }

        void init(int maxLength, int newBlockLength) {
            int newLength;
            int maxDataIndex = maxLength - newBlockLength + 1;
            if (maxDataIndex <= 4095) {
                newLength = 6007;
                this.shift = 12;
                this.mask = 4095;
            } else if (maxDataIndex <= Short.MAX_VALUE) {
                newLength = 50021;
                this.shift = 15;
                this.mask = Short.MAX_VALUE;
            } else if (maxDataIndex <= 131071) {
                newLength = 200003;
                this.shift = 17;
                this.mask = 131071;
            } else {
                newLength = 1500007;
                this.shift = 21;
                this.mask = 0x1FFFFF;
            }
            if (this.table == null || newLength > this.table.length) {
                this.table = new int[newLength];
            } else {
                Arrays.fill(this.table, 0, newLength, 0);
            }
            this.length = newLength;
            this.blockLength = newBlockLength;
        }

        void extend(int[] data, int minStart, int prevDataLength, int newDataLength) {
            int start = prevDataLength - this.blockLength;
            start = start >= minStart ? ++start : minStart;
            int end = newDataLength - this.blockLength;
            while (start <= end) {
                int hashCode = this.makeHashCode(data, start);
                this.addEntry(data, null, start, hashCode, start);
                ++start;
            }
        }

        void extend(char[] data, int minStart, int prevDataLength, int newDataLength) {
            int start = prevDataLength - this.blockLength;
            start = start >= minStart ? ++start : minStart;
            int end = newDataLength - this.blockLength;
            while (start <= end) {
                int hashCode = this.makeHashCode(data, start);
                this.addEntry(null, data, start, hashCode, start);
                ++start;
            }
        }

        int findBlock(int[] data, int[] blockData, int blockStart) {
            int hashCode = this.makeHashCode(blockData, blockStart);
            int entryIndex = this.findEntry(data, null, blockData, null, blockStart, hashCode);
            if (entryIndex >= 0) {
                return (this.table[entryIndex] & this.mask) - 1;
            }
            return -1;
        }

        int findBlock(char[] data, int[] blockData, int blockStart) {
            int hashCode = this.makeHashCode(blockData, blockStart);
            int entryIndex = this.findEntry(null, data, blockData, null, blockStart, hashCode);
            if (entryIndex >= 0) {
                return (this.table[entryIndex] & this.mask) - 1;
            }
            return -1;
        }

        int findBlock(char[] data, char[] blockData, int blockStart) {
            int hashCode = this.makeHashCode(blockData, blockStart);
            int entryIndex = this.findEntry(null, data, null, blockData, blockStart, hashCode);
            if (entryIndex >= 0) {
                return (this.table[entryIndex] & this.mask) - 1;
            }
            return -1;
        }

        int findAllSameBlock(int[] data, int blockValue) {
            int hashCode = this.makeHashCode(blockValue);
            int entryIndex = this.findEntry(data, blockValue, hashCode);
            if (entryIndex >= 0) {
                return (this.table[entryIndex] & this.mask) - 1;
            }
            return -1;
        }

        private int makeHashCode(int[] blockData, int blockStart) {
            int blockLimit = blockStart + this.blockLength;
            int hashCode = blockData[blockStart++];
            do {
                hashCode = 37 * hashCode + blockData[blockStart++];
            } while (blockStart < blockLimit);
            return hashCode;
        }

        private int makeHashCode(char[] blockData, int blockStart) {
            int blockLimit = blockStart + this.blockLength;
            int hashCode = blockData[blockStart++];
            do {
                hashCode = 37 * hashCode + blockData[blockStart++];
            } while (blockStart < blockLimit);
            return hashCode;
        }

        private int makeHashCode(int blockValue) {
            int hashCode = blockValue;
            for (int i = 1; i < this.blockLength; ++i) {
                hashCode = 37 * hashCode + blockValue;
            }
            return hashCode;
        }

        private void addEntry(int[] data32, char[] data16, int blockStart, int hashCode, int dataIndex) {
            assert (0 <= dataIndex && dataIndex < this.mask);
            int entryIndex = this.findEntry(data32, data16, data32, data16, blockStart, hashCode);
            if (entryIndex < 0) {
                this.table[entryIndex ^ 0xFFFFFFFF] = hashCode << this.shift | dataIndex + 1;
            }
        }

        private int findEntry(int[] data32, char[] data16, int[] blockData32, char[] blockData16, int blockStart, int hashCode) {
            int initialEntryIndex;
            int shiftedHashCode = hashCode << this.shift;
            int entryIndex = initialEntryIndex = this.modulo(hashCode, this.length - 1) + 1;
            int entry;
            while ((entry = this.table[entryIndex]) != 0) {
                if ((entry & ~this.mask) == shiftedHashCode) {
                    int dataIndex = (entry & this.mask) - 1;
                    if (data32 != null ? MutableCodePointTrie.equalBlocks(data32, dataIndex, blockData32, blockStart, this.blockLength) : (blockData32 != null ? MutableCodePointTrie.equalBlocks(data16, dataIndex, blockData32, blockStart, this.blockLength) : MutableCodePointTrie.equalBlocks(data16, dataIndex, blockData16, blockStart, this.blockLength))) {
                        return entryIndex;
                    }
                }
                entryIndex = this.nextIndex(initialEntryIndex, entryIndex);
            }
            return ~entryIndex;
        }

        private int findEntry(int[] data, int blockValue, int hashCode) {
            int initialEntryIndex;
            int shiftedHashCode = hashCode << this.shift;
            int entryIndex = initialEntryIndex = this.modulo(hashCode, this.length - 1) + 1;
            int entry;
            while ((entry = this.table[entryIndex]) != 0) {
                int dataIndex;
                if ((entry & ~this.mask) == shiftedHashCode && MutableCodePointTrie.allValuesSameAs(data, dataIndex = (entry & this.mask) - 1, this.blockLength, blockValue)) {
                    return entryIndex;
                }
                entryIndex = this.nextIndex(initialEntryIndex, entryIndex);
            }
            return ~entryIndex;
        }

        private int nextIndex(int initialEntryIndex, int entryIndex) {
            return (entryIndex + initialEntryIndex) % this.length;
        }

        private int modulo(int n, int m) {
            int i = n % m;
            if (i < 0) {
                i += m;
            }
            return i;
        }
    }

    private static final class AllSameBlocks {
        static final int NEW_UNIQUE = -1;
        static final int OVERFLOW = -2;
        private static final int CAPACITY = 32;
        private int length;
        private int mostRecent = -1;
        private int[] indexes = new int[32];
        private int[] values = new int[32];
        private int[] refCounts = new int[32];

        AllSameBlocks() {
        }

        int findOrAdd(int index, int count, int value) {
            if (this.mostRecent >= 0 && this.values[this.mostRecent] == value) {
                int n = this.mostRecent;
                this.refCounts[n] = this.refCounts[n] + count;
                return this.indexes[this.mostRecent];
            }
            for (int i = 0; i < this.length; ++i) {
                if (this.values[i] != value) continue;
                this.mostRecent = i;
                int n = i;
                this.refCounts[n] = this.refCounts[n] + count;
                return this.indexes[i];
            }
            if (this.length == 32) {
                return -2;
            }
            this.mostRecent = this.length;
            this.indexes[this.length] = index;
            this.values[this.length] = value;
            this.refCounts[this.length++] = count;
            return -1;
        }

        void add(int index, int count, int value) {
            assert (this.length == 32);
            int least = -1;
            int leastCount = 69632;
            for (int i = 0; i < this.length; ++i) {
                assert (this.values[i] != value);
                if (this.refCounts[i] >= leastCount) continue;
                least = i;
                leastCount = this.refCounts[i];
            }
            assert (least >= 0);
            this.mostRecent = least;
            this.indexes[least] = index;
            this.values[least] = value;
            this.refCounts[least] = count;
        }

        int findMostUsed() {
            if (this.length == 0) {
                return -1;
            }
            int max = -1;
            int maxCount = 0;
            for (int i = 0; i < this.length; ++i) {
                if (this.refCounts[i] <= maxCount) continue;
                max = i;
                maxCount = this.refCounts[i];
            }
            return this.indexes[max];
        }
    }
}
