package com.ibm.icu.impl.coll;

import com.ibm.icu.impl.coll.Collation;

public final class CollationRootElements {
    public static final long PRIMARY_SENTINEL = 0xFFFFFF00L;
    public static final int SEC_TER_DELTA_FLAG = 128;
    public static final int PRIMARY_STEP_MASK = 127;
    public static final int IX_FIRST_TERTIARY_INDEX = 0;
    static final int IX_FIRST_SECONDARY_INDEX = 1;
    static final int IX_FIRST_PRIMARY_INDEX = 2;
    static final int IX_COMMON_SEC_AND_TER_CE = 3;
    static final int IX_SEC_TER_BOUNDARIES = 4;
    static final int IX_COUNT = 5;
    private long[] elements;

    public CollationRootElements(long[] rootElements) {
        this.elements = rootElements;
    }

    public int getTertiaryBoundary() {
        return (int)this.elements[4] << 8 & 0xFF00;
    }

    long getFirstTertiaryCE() {
        return this.elements[(int)this.elements[0]] & 0xFFFFFFFFFFFFFF7FL;
    }

    long getLastTertiaryCE() {
        return this.elements[(int)this.elements[1] - 1] & 0xFFFFFFFFFFFFFF7FL;
    }

    public int getLastCommonSecondary() {
        return (int)this.elements[4] >> 16 & 0xFF00;
    }

    public int getSecondaryBoundary() {
        return (int)this.elements[4] >> 8 & 0xFF00;
    }

    long getFirstSecondaryCE() {
        return this.elements[(int)this.elements[1]] & 0xFFFFFFFFFFFFFF7FL;
    }

    long getLastSecondaryCE() {
        return this.elements[(int)this.elements[2] - 1] & 0xFFFFFFFFFFFFFF7FL;
    }

    long getFirstPrimary() {
        return this.elements[(int)this.elements[2]];
    }

    long getFirstPrimaryCE() {
        return Collation.makeCE(this.getFirstPrimary());
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    long lastCEWithPrimaryBefore(long p) {
        long secTer;
        if (p == 0L) {
            return 0L;
        }
        assert (p > this.elements[(int)this.elements[2]]);
        int index = this.findP(p);
        long q = this.elements[index];
        if (p == (q & 0xFFFFFF00L)) {
            assert ((q & 0x7FL) == 0L);
            secTer = this.elements[index - 1];
            if ((secTer & 0x80L) == 0L) {
                p = secTer & 0xFFFFFF00L;
                secTer = 0x5000500L;
                return p << 32 | secTer & 0xFFFFFFFFFFFFFF7FL;
            }
            index -= 2;
            while (true) {
                if (((p = this.elements[index]) & 0x80L) == 0L) {
                    p &= 0xFFFFFF00L;
                    return p << 32 | secTer & 0xFFFFFFFFFFFFFF7FL;
                }
                --index;
            }
        }
        p = q & 0xFFFFFF00L;
        secTer = 0x5000500L;
        while (true) {
            if (((q = this.elements[++index]) & 0x80L) == 0L) {
                assert ((q & 0x7FL) == 0L);
                return p << 32 | secTer & 0xFFFFFFFFFFFFFF7FL;
            }
            secTer = q;
        }
    }

    long firstCEWithPrimaryAtLeast(long p) {
        if (p == 0L) {
            return 0L;
        }
        int index = this.findP(p);
        if (p != (this.elements[index] & 0xFFFFFF00L)) {
            while (((p = this.elements[++index]) & 0x80L) != 0L) {
            }
            assert ((p & 0x7FL) == 0L);
        }
        return p << 32 | 0x5000500L;
    }

    long getPrimaryBefore(long p, boolean isCompressible) {
        int step;
        int index = this.findPrimary(p);
        long q = this.elements[index];
        if (p == (q & 0xFFFFFF00L)) {
            step = (int)q & 0x7F;
            if (step == 0) {
                while (((p = this.elements[--index]) & 0x80L) != 0L) {
                }
                return p & 0xFFFFFF00L;
            }
        } else {
            long nextElement = this.elements[index + 1];
            assert (CollationRootElements.isEndOfPrimaryRange(nextElement));
            step = (int)nextElement & 0x7F;
        }
        if ((p & 0xFFFFL) == 0L) {
            return Collation.decTwoBytePrimaryByOneStep(p, isCompressible, step);
        }
        return Collation.decThreeBytePrimaryByOneStep(p, isCompressible, step);
    }

    int getSecondaryBefore(long p, int s) {
        int sec;
        int previousSec;
        int index;
        if (p == 0L) {
            index = (int)this.elements[1];
            previousSec = 0;
            sec = (int)(this.elements[index] >> 16);
        } else {
            index = this.findPrimary(p) + 1;
            previousSec = 256;
            sec = (int)this.getFirstSecTerForPrimary(index) >>> 16;
        }
        assert (s >= sec);
        while (s > sec) {
            previousSec = sec;
            assert ((this.elements[index] & 0x80L) != 0L);
            sec = (int)(this.elements[index++] >> 16);
        }
        assert (sec == s);
        return previousSec;
    }

    int getTertiaryBefore(long p, int s, int t) {
        long secTer;
        int previousTer;
        int index;
        assert ((t & 0xFFFFC0C0) == 0);
        if (p == 0L) {
            if (s == 0) {
                index = (int)this.elements[0];
                previousTer = 0;
            } else {
                index = (int)this.elements[1];
                previousTer = 256;
            }
            secTer = this.elements[index] & 0xFFFFFFFFFFFFFF7FL;
        } else {
            index = this.findPrimary(p) + 1;
            previousTer = 256;
            secTer = this.getFirstSecTerForPrimary(index);
        }
        long st = (long)s << 16 | (long)t;
        while (st > secTer) {
            if ((int)(secTer >> 16) == s) {
                previousTer = (int)secTer;
            }
            assert ((this.elements[index] & 0x80L) != 0L);
            secTer = this.elements[index++] & 0xFFFFFFFFFFFFFF7FL;
        }
        assert (secTer == st);
        return previousTer & 0xFFFF;
    }

    int findPrimary(long p) {
        assert ((p & 0xFFL) == 0L);
        int index = this.findP(p);
        assert (CollationRootElements.isEndOfPrimaryRange(this.elements[index + 1]) || p == (this.elements[index] & 0xFFFFFF00L));
        return index;
    }

    long getPrimaryAfter(long p, int index, boolean isCompressible) {
        int step;
        long q;
        assert (p == (this.elements[index] & 0xFFFFFF00L) || CollationRootElements.isEndOfPrimaryRange(this.elements[index + 1]));
        if (((q = this.elements[++index]) & 0x80L) == 0L && (step = (int)q & 0x7F) != 0) {
            if ((p & 0xFFFFL) == 0L) {
                return Collation.incTwoBytePrimaryByOffset(p, isCompressible, step);
            }
            return Collation.incThreeBytePrimaryByOffset(p, isCompressible, step);
        }
        while ((q & 0x80L) != 0L) {
            q = this.elements[++index];
        }
        assert ((q & 0x7FL) == 0L);
        return q;
    }

    int getSecondaryAfter(int index, int s) {
        int secLimit;
        long secTer;
        if (index == 0) {
            assert (s != 0);
            index = (int)this.elements[1];
            secTer = this.elements[index];
            secLimit = 65536;
        } else {
            assert (index >= (int)this.elements[2]);
            secTer = this.getFirstSecTerForPrimary(index + 1);
            secLimit = this.getSecondaryBoundary();
        }
        do {
            int sec;
            if ((sec = (int)(secTer >> 16)) <= s) continue;
            return sec;
        } while (((secTer = this.elements[++index]) & 0x80L) != 0L);
        return secLimit;
    }

    int getTertiaryAfter(int index, int s, int t) {
        long secTer;
        int terLimit;
        if (index == 0) {
            if (s == 0) {
                assert (t != 0);
                index = (int)this.elements[0];
                terLimit = 16384;
            } else {
                index = (int)this.elements[1];
                terLimit = this.getTertiaryBoundary();
            }
            secTer = this.elements[index] & 0xFFFFFFFFFFFFFF7FL;
        } else {
            assert (index >= (int)this.elements[2]);
            secTer = this.getFirstSecTerForPrimary(index + 1);
            terLimit = this.getTertiaryBoundary();
        }
        long st = ((long)s & 0xFFFFFFFFL) << 16 | (long)t;
        while (true) {
            if (secTer > st) {
                assert (secTer >> 16 == (long)s);
                return (int)secTer & 0xFFFF;
            }
            if (((secTer = this.elements[++index]) & 0x80L) == 0L || secTer >> 16 > (long)s) {
                return terLimit;
            }
            secTer &= 0xFFFFFFFFFFFFFF7FL;
        }
    }

    private long getFirstSecTerForPrimary(int index) {
        long secTer = this.elements[index];
        if ((secTer & 0x80L) == 0L) {
            return 0x5000500L;
        }
        if ((secTer &= 0xFFFFFFFFFFFFFF7FL) > 0x5000500L) {
            return 0x5000500L;
        }
        return secTer;
    }

    private int findP(long p) {
        assert (p >> 24 != 254L);
        int start = (int)this.elements[2];
        assert (p >= this.elements[start]);
        int limit = this.elements.length - 1;
        assert (this.elements[limit] >= 0xFFFFFF00L);
        assert (p < this.elements[limit]);
        while (start + 1 < limit) {
            int i = (int)(((long)start + (long)limit) / 2L);
            long q = this.elements[i];
            if ((q & 0x80L) != 0L) {
                int j;
                for (j = i + 1; j != limit; ++j) {
                    q = this.elements[j];
                    if ((q & 0x80L) != 0L) continue;
                    i = j;
                    break;
                }
                if ((q & 0x80L) != 0L) {
                    for (j = i - 1; j != start; --j) {
                        q = this.elements[j];
                        if ((q & 0x80L) != 0L) continue;
                        i = j;
                        break;
                    }
                    if ((q & 0x80L) != 0L) break;
                }
            }
            if (p < (q & 0xFFFFFF00L)) {
                limit = i;
                continue;
            }
            start = i;
        }
        return start;
    }

    private static boolean isEndOfPrimaryRange(long q) {
        return (q & 0x80L) == 0L && (q & 0x7FL) != 0L;
    }
}
