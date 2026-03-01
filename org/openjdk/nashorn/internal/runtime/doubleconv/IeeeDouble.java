package org.openjdk.nashorn.internal.runtime.doubleconv;

import org.openjdk.nashorn.internal.runtime.doubleconv.DiyFp;

class IeeeDouble {
    static final long kSignMask = Long.MIN_VALUE;
    static final long kExponentMask = 0x7FF0000000000000L;
    static final long kSignificandMask = 0xFFFFFFFFFFFFFL;
    static final long kHiddenBit = 0x10000000000000L;
    static final int kPhysicalSignificandSize = 52;
    static final int kSignificandSize = 53;
    private static final int kExponentBias = 1075;
    private static final int kDenormalExponent = -1074;
    private static final int kMaxExponent = 972;
    private static final long kInfinity = 0x7FF0000000000000L;
    private static final long kNaN = 9221120237041090560L;

    IeeeDouble() {
    }

    static long doubleToLong(double d) {
        return Double.doubleToRawLongBits(d);
    }

    static double longToDouble(long d64) {
        return Double.longBitsToDouble(d64);
    }

    static DiyFp asDiyFp(long d64) {
        assert (!IeeeDouble.isSpecial(d64));
        return new DiyFp(IeeeDouble.significand(d64), IeeeDouble.exponent(d64));
    }

    static DiyFp asNormalizedDiyFp(long d64) {
        assert (IeeeDouble.value(d64) > 0.0);
        long f = IeeeDouble.significand(d64);
        int e = IeeeDouble.exponent(d64);
        while ((f & 0x10000000000000L) == 0L) {
            f <<= 1;
            --e;
        }
        return new DiyFp(f <<= 11, e -= 11);
    }

    static double nextDouble(long d64) {
        if (d64 == 0x7FF0000000000000L) {
            return IeeeDouble.longToDouble(0x7FF0000000000000L);
        }
        if (IeeeDouble.sign(d64) < 0 && IeeeDouble.significand(d64) == 0L) {
            return 0.0;
        }
        if (IeeeDouble.sign(d64) < 0) {
            return IeeeDouble.longToDouble(d64 - 1L);
        }
        return IeeeDouble.longToDouble(d64 + 1L);
    }

    static double previousDouble(long d64) {
        if (d64 == -4503599627370496L) {
            return -IeeeDouble.Infinity();
        }
        if (IeeeDouble.sign(d64) < 0) {
            return IeeeDouble.longToDouble(d64 + 1L);
        }
        if (IeeeDouble.significand(d64) == 0L) {
            return -0.0;
        }
        return IeeeDouble.longToDouble(d64 - 1L);
    }

    static int exponent(long d64) {
        if (IeeeDouble.isDenormal(d64)) {
            return -1074;
        }
        int biased_e = (int)((d64 & 0x7FF0000000000000L) >>> 52);
        return biased_e - 1075;
    }

    static long significand(long d64) {
        long significand = d64 & 0xFFFFFFFFFFFFFL;
        if (!IeeeDouble.isDenormal(d64)) {
            return significand + 0x10000000000000L;
        }
        return significand;
    }

    static boolean isDenormal(long d64) {
        return (d64 & 0x7FF0000000000000L) == 0L;
    }

    static boolean isSpecial(long d64) {
        return (d64 & 0x7FF0000000000000L) == 0x7FF0000000000000L;
    }

    static boolean isNaN(long d64) {
        return (d64 & 0x7FF0000000000000L) == 0x7FF0000000000000L && (d64 & 0xFFFFFFFFFFFFFL) != 0L;
    }

    static boolean isInfinite(long d64) {
        return (d64 & 0x7FF0000000000000L) == 0x7FF0000000000000L && (d64 & 0xFFFFFFFFFFFFFL) == 0L;
    }

    static int sign(long d64) {
        return (d64 & Long.MIN_VALUE) == 0L ? 1 : -1;
    }

    static void normalizedBoundaries(long d64, DiyFp m_minus, DiyFp m_plus) {
        assert (IeeeDouble.value(d64) > 0.0);
        DiyFp v = IeeeDouble.asDiyFp(d64);
        m_plus.setF((v.f() << 1) + 1L);
        m_plus.setE(v.e() - 1);
        m_plus.normalize();
        if (IeeeDouble.lowerBoundaryIsCloser(d64)) {
            m_minus.setF((v.f() << 2) - 1L);
            m_minus.setE(v.e() - 2);
        } else {
            m_minus.setF((v.f() << 1) - 1L);
            m_minus.setE(v.e() - 1);
        }
        m_minus.setF(m_minus.f() << m_minus.e() - m_plus.e());
        m_minus.setE(m_plus.e());
    }

    static boolean lowerBoundaryIsCloser(long d64) {
        boolean physical_significand_is_zero = (d64 & 0xFFFFFFFFFFFFFL) == 0L;
        return physical_significand_is_zero && IeeeDouble.exponent(d64) != -1074;
    }

    static double value(long d64) {
        return IeeeDouble.longToDouble(d64);
    }

    static int significandSizeForOrderOfMagnitude(int order) {
        if (order >= -1021) {
            return 53;
        }
        if (order <= -1074) {
            return 0;
        }
        return order - -1074;
    }

    static double Infinity() {
        return IeeeDouble.longToDouble(0x7FF0000000000000L);
    }

    static double NaN() {
        return IeeeDouble.longToDouble(9221120237041090560L);
    }
}
