package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;
import dimasik.managers.mods.voicechat.decoder.Inlines;

class Schur {
    Schur() {
    }

    static int silk_schur(short[] rc_Q15, int[] c, int order) {
        int k;
        int[][] C = Arrays.InitTwoDimensionalArrayInt(17, 2);
        Inlines.OpusAssert(order == 6 || order == 8 || order == 10 || order == 12 || order == 14 || order == 16);
        int lz = Inlines.silk_CLZ32(c[0]);
        if (lz < 2) {
            for (k = 0; k < order + 1; ++k) {
                int n = Inlines.silk_RSHIFT(c[k], 1);
                C[k][1] = n;
                C[k][0] = n;
            }
        } else if (lz > 2) {
            lz -= 2;
            for (k = 0; k < order + 1; ++k) {
                int n = Inlines.silk_LSHIFT(c[k], lz);
                C[k][1] = n;
                C[k][0] = n;
            }
        } else {
            for (k = 0; k < order + 1; ++k) {
                int n = c[k];
                C[k][1] = n;
                C[k][0] = n;
            }
        }
        for (k = 0; k < order; ++k) {
            if (Inlines.silk_abs_int32(C[k + 1][0]) >= C[0][1]) {
                rc_Q15[k] = C[k + 1][0] > 0 ? -32440 : 32440;
                ++k;
                break;
            }
            int rc_tmp_Q15 = 0 - Inlines.silk_DIV32_16(C[k + 1][0], Inlines.silk_max_32(Inlines.silk_RSHIFT(C[0][1], 15), 1));
            rc_tmp_Q15 = Inlines.silk_SAT16(rc_tmp_Q15);
            rc_Q15[k] = (short)rc_tmp_Q15;
            for (int n = 0; n < order - k; ++n) {
                int Ctmp1 = C[n + k + 1][0];
                int Ctmp2 = C[n][1];
                C[n + k + 1][0] = Inlines.silk_SMLAWB(Ctmp1, Inlines.silk_LSHIFT(Ctmp2, 1), rc_tmp_Q15);
                C[n][1] = Inlines.silk_SMLAWB(Ctmp2, Inlines.silk_LSHIFT(Ctmp1, 1), rc_tmp_Q15);
            }
        }
        while (k < order) {
            rc_Q15[k] = 0;
            ++k;
        }
        return Inlines.silk_max_32(1, C[0][1]);
    }

    static int silk_schur64(int[] rc_Q16, int[] c, int order) {
        int k;
        int[][] C = Arrays.InitTwoDimensionalArrayInt(17, 2);
        Inlines.OpusAssert(order == 6 || order == 8 || order == 10 || order == 12 || order == 14 || order == 16);
        if (c[0] <= 0) {
            Arrays.MemSet(rc_Q16, 0, order);
            return 0;
        }
        for (k = 0; k < order + 1; ++k) {
            int n = c[k];
            C[k][1] = n;
            C[k][0] = n;
        }
        for (k = 0; k < order; ++k) {
            if (Inlines.silk_abs_int32(C[k + 1][0]) >= C[0][1]) {
                rc_Q16[k] = C[k + 1][0] > 0 ? -64881 : 64881;
                ++k;
                break;
            }
            int rc_tmp_Q31 = Inlines.silk_DIV32_varQ(-C[k + 1][0], C[0][1], 31);
            rc_Q16[k] = Inlines.silk_RSHIFT_ROUND(rc_tmp_Q31, 15);
            for (int n = 0; n < order - k; ++n) {
                int Ctmp1_Q30 = C[n + k + 1][0];
                int Ctmp2_Q30 = C[n][1];
                C[n + k + 1][0] = Ctmp1_Q30 + Inlines.silk_SMMUL(Inlines.silk_LSHIFT(Ctmp2_Q30, 1), rc_tmp_Q31);
                C[n][1] = Ctmp2_Q30 + Inlines.silk_SMMUL(Inlines.silk_LSHIFT(Ctmp1_Q30, 1), rc_tmp_Q31);
            }
        }
        while (k < order) {
            rc_Q16[k] = 0;
            ++k;
        }
        return Inlines.silk_max_32(1, C[0][1]);
    }
}
