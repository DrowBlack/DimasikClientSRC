package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.CeltPitchXCorr;
import dimasik.managers.mods.voicechat.decoder.Inlines;

class Autocorrelation {
    private static final int QC = 10;
    private static final int QS = 14;

    Autocorrelation() {
    }

    static void silk_autocorr(int[] results, BoxedValueInt scale, short[] inputData, int inputDataSize, int correlationCount) {
        int corrCount = Inlines.silk_min_int(inputDataSize, correlationCount);
        scale.Val = Autocorrelation._celt_autocorr(inputData, results, corrCount - 1, inputDataSize);
    }

    static int _celt_autocorr(short[] x, int[] ac, int lag, int n) {
        int i;
        int fastN = n - lag;
        short[] xx = new short[n];
        Inlines.OpusAssert(n > 0);
        short[] xptr = x;
        int shift = 0;
        int ac0 = 1 + (n << 7);
        if ((n & 1) != 0) {
            ac0 += Inlines.SHR32(Inlines.MULT16_16(xptr[0], xptr[0]), 9);
        }
        for (i = n & 1; i < n; i += 2) {
            ac0 += Inlines.SHR32(Inlines.MULT16_16(xptr[i], xptr[i]), 9);
            ac0 += Inlines.SHR32(Inlines.MULT16_16(xptr[i + 1], xptr[i + 1]), 9);
        }
        shift = Inlines.celt_ilog2(ac0) - 30 + 10;
        if ((shift /= 2) > 0) {
            for (i = 0; i < n; ++i) {
                xx[i] = (short)Inlines.PSHR32(xptr[i], shift);
            }
            xptr = xx;
        } else {
            shift = 0;
        }
        CeltPitchXCorr.pitch_xcorr(xptr, xptr, ac, fastN, lag + 1);
        int k = 0;
        while (k <= lag) {
            int d = 0;
            for (i = k + fastN; i < n; ++i) {
                d = Inlines.MAC16_16(d, xptr[i], xptr[i - k]);
            }
            int n2 = k++;
            ac[n2] = ac[n2] + d;
        }
        if ((shift = 2 * shift) <= 0) {
            ac[0] = ac[0] + Inlines.SHL32(1, -shift);
        }
        if (ac[0] < 0x10000000) {
            shift2 = 29 - Inlines.EC_ILOG(ac[0]);
            for (i = 0; i <= lag; ++i) {
                ac[i] = Inlines.SHL32(ac[i], shift2);
            }
            shift -= shift2;
        } else if (ac[0] >= 0x20000000) {
            shift2 = 1;
            if (ac[0] >= 0x40000000) {
                ++shift2;
            }
            for (i = 0; i <= lag; ++i) {
                ac[i] = Inlines.SHR32(ac[i], shift2);
            }
            shift += shift2;
        }
        return shift;
    }

    static int _celt_autocorr(int[] x, int[] ac, int[] window, int overlap, int lag, int n) {
        int i;
        int[] xptr;
        int fastN = n - lag;
        int[] xx = new int[n];
        Inlines.OpusAssert(n > 0);
        Inlines.OpusAssert(overlap >= 0);
        if (overlap == 0) {
            xptr = x;
        } else {
            for (i = 0; i < n; ++i) {
                xx[i] = x[i];
            }
            for (i = 0; i < overlap; ++i) {
                xx[i] = Inlines.MULT16_16_Q15(x[i], window[i]);
                xx[n - i - 1] = Inlines.MULT16_16_Q15(x[n - i - 1], window[i]);
            }
            xptr = xx;
        }
        int shift = 0;
        int ac0 = 1 + (n << 7);
        if ((n & 1) != 0) {
            ac0 += Inlines.SHR32(Inlines.MULT16_16(xptr[0], xptr[0]), 9);
        }
        for (i = n & 1; i < n; i += 2) {
            ac0 += Inlines.SHR32(Inlines.MULT16_16(xptr[i], xptr[i]), 9);
            ac0 += Inlines.SHR32(Inlines.MULT16_16(xptr[i + 1], xptr[i + 1]), 9);
        }
        shift = Inlines.celt_ilog2(ac0) - 30 + 10;
        if ((shift /= 2) > 0) {
            for (i = 0; i < n; ++i) {
                xx[i] = Inlines.PSHR32(xptr[i], shift);
            }
            xptr = xx;
        } else {
            shift = 0;
        }
        CeltPitchXCorr.pitch_xcorr(xptr, xptr, ac, fastN, lag + 1);
        int k = 0;
        while (k <= lag) {
            int d = 0;
            for (i = k + fastN; i < n; ++i) {
                d = Inlines.MAC16_16(d, xptr[i], xptr[i - k]);
            }
            int n2 = k++;
            ac[n2] = ac[n2] + d;
        }
        if ((shift = 2 * shift) <= 0) {
            ac[0] = ac[0] + Inlines.SHL32(1, -shift);
        }
        if (ac[0] < 0x10000000) {
            int shift2 = 29 - Inlines.EC_ILOG(ac[0]);
            for (i = 0; i <= lag; ++i) {
                ac[i] = Inlines.SHL32(ac[i], shift2);
            }
            shift -= shift2;
        } else if (ac[0] >= 0x20000000) {
            int shift2 = 1;
            if (ac[0] >= 0x40000000) {
                ++shift2;
            }
            for (i = 0; i <= lag; ++i) {
                ac[i] = Inlines.SHR32(ac[i], shift2);
            }
            shift += shift2;
        }
        return shift;
    }

    static void silk_warped_autocorrelation(int[] corr, BoxedValueInt scale, short[] input, int warping_Q16, int length, int order) {
        int i;
        int[] state_QS = new int[17];
        long[] corr_QC = new long[17];
        Inlines.OpusAssert((order & 1) == 0);
        Inlines.OpusAssert(true);
        for (int n = 0; n < length; ++n) {
            int tmp1_QS = Inlines.silk_LSHIFT32(input[n], 14);
            for (i = 0; i < order; i += 2) {
                int tmp2_QS = Inlines.silk_SMLAWB(state_QS[i], state_QS[i + 1] - tmp1_QS, warping_Q16);
                state_QS[i] = tmp1_QS;
                int n2 = i;
                corr_QC[n2] = corr_QC[n2] + Inlines.silk_RSHIFT64(Inlines.silk_SMULL(tmp1_QS, state_QS[0]), 18);
                tmp1_QS = Inlines.silk_SMLAWB(state_QS[i + 1], state_QS[i + 2] - tmp2_QS, warping_Q16);
                state_QS[i + 1] = tmp2_QS;
                int n3 = i + 1;
                corr_QC[n3] = corr_QC[n3] + Inlines.silk_RSHIFT64(Inlines.silk_SMULL(tmp2_QS, state_QS[0]), 18);
            }
            state_QS[order] = tmp1_QS;
            int n4 = order;
            corr_QC[n4] = corr_QC[n4] + Inlines.silk_RSHIFT64(Inlines.silk_SMULL(tmp1_QS, state_QS[0]), 18);
        }
        int lsh = Inlines.silk_CLZ64(corr_QC[0]) - 35;
        lsh = Inlines.silk_LIMIT(lsh, -22, 20);
        scale.Val = -(10 + lsh);
        Inlines.OpusAssert(scale.Val >= -30 && scale.Val <= 12);
        if (lsh >= 0) {
            for (i = 0; i < order + 1; ++i) {
                corr[i] = (int)Inlines.silk_LSHIFT64(corr_QC[i], lsh);
            }
        } else {
            for (i = 0; i < order + 1; ++i) {
                corr[i] = (int)Inlines.silk_RSHIFT64(corr_QC[i], -lsh);
            }
        }
        Inlines.OpusAssert(corr_QC[0] >= 0L);
    }
}
