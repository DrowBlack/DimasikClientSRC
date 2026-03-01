package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.CorrelateMatrix;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.LinearAlgebra;
import dimasik.managers.mods.voicechat.decoder.RegularizeCorrelations;
import dimasik.managers.mods.voicechat.decoder.ResidualEnergy;
import dimasik.managers.mods.voicechat.decoder.SumSqrShift;

class FindLTP {
    private static final int LTP_CORRS_HEAD_ROOM = 2;

    FindLTP() {
    }

    static void silk_find_LTP(short[] b_Q14, int[] WLTP, BoxedValueInt LTPredCodGain_Q7, short[] r_lpc, int[] lag, int[] Wght_Q15, int subfr_length, int nb_subfr, int mem_offset, int[] corr_rshifts) {
        int i;
        int temp32;
        int extra_shifts;
        int k;
        int[] b_Q16 = new int[5];
        int[] delta_b_Q14 = new int[5];
        int[] d_Q14 = new int[4];
        int[] nrg = new int[4];
        int[] w = new int[4];
        int[] Rr = new int[5];
        int[] rr = new int[4];
        int b_Q14_ptr = 0;
        int WLTP_ptr = 0;
        int r_ptr = mem_offset;
        for (k = 0; k < nb_subfr; ++k) {
            int lag_ptr = r_ptr - (lag[k] + 2);
            BoxedValueInt boxed_rr = new BoxedValueInt(0);
            BoxedValueInt boxed_rr_shift = new BoxedValueInt(0);
            SumSqrShift.silk_sum_sqr_shift(boxed_rr, boxed_rr_shift, r_lpc, r_ptr, subfr_length);
            rr[k] = boxed_rr.Val;
            int rr_shifts = boxed_rr_shift.Val;
            int LZs = Inlines.silk_CLZ32(rr[k]);
            if (LZs < 2) {
                rr[k] = Inlines.silk_RSHIFT_ROUND(rr[k], 2 - LZs);
                rr_shifts += 2 - LZs;
            }
            corr_rshifts[k] = rr_shifts;
            BoxedValueInt boxed_shifts = new BoxedValueInt(corr_rshifts[k]);
            CorrelateMatrix.silk_corrMatrix(r_lpc, lag_ptr, subfr_length, 5, 2, WLTP, WLTP_ptr, boxed_shifts);
            corr_rshifts[k] = boxed_shifts.Val;
            CorrelateMatrix.silk_corrVector(r_lpc, lag_ptr, r_lpc, r_ptr, subfr_length, 5, Rr, corr_rshifts[k]);
            if (corr_rshifts[k] > rr_shifts) {
                rr[k] = Inlines.silk_RSHIFT(rr[k], corr_rshifts[k] - rr_shifts);
            }
            Inlines.OpusAssert(rr[k] >= 0);
            int regu = 1;
            regu = Inlines.silk_SMLAWB(regu, rr[k], 1092);
            regu = Inlines.silk_SMLAWB(regu, Inlines.MatrixGet(WLTP, WLTP_ptr, 0, 0, 5), 1092);
            regu = Inlines.silk_SMLAWB(regu, Inlines.MatrixGet(WLTP, WLTP_ptr, 4, 4, 5), 1092);
            RegularizeCorrelations.silk_regularize_correlations(WLTP, WLTP_ptr, rr, k, regu, 5);
            LinearAlgebra.silk_solve_LDL(WLTP, WLTP_ptr, 5, Rr, b_Q16);
            FindLTP.silk_fit_LTP(b_Q16, b_Q14, b_Q14_ptr);
            nrg[k] = ResidualEnergy.silk_residual_energy16_covar(b_Q14, b_Q14_ptr, WLTP, WLTP_ptr, Rr, rr[k], 5, 14);
            extra_shifts = Inlines.silk_min_int(corr_rshifts[k], 2);
            int denom32 = Inlines.silk_LSHIFT_SAT32(Inlines.silk_SMULWB(nrg[k], Wght_Q15[k]), 1 + extra_shifts) + Inlines.silk_RSHIFT(Inlines.silk_SMULWB(subfr_length, 655), corr_rshifts[k] - extra_shifts);
            denom32 = Inlines.silk_max(denom32, 1);
            Inlines.OpusAssert((long)Wght_Q15[k] << 16 < Integer.MAX_VALUE);
            temp32 = Inlines.silk_DIV32(Inlines.silk_LSHIFT(Wght_Q15[k], 16), denom32);
            temp32 = Inlines.silk_RSHIFT(temp32, 31 + corr_rshifts[k] - extra_shifts - 26);
            int WLTP_max = 0;
            for (i = WLTP_ptr; i < WLTP_ptr + 25; ++i) {
                WLTP_max = Inlines.silk_max(WLTP[i], WLTP_max);
            }
            int lshift = Inlines.silk_CLZ32(WLTP_max) - 1 - 3;
            Inlines.OpusAssert(8 + lshift >= 0);
            if (8 + lshift < 31) {
                temp32 = Inlines.silk_min_32(temp32, Inlines.silk_LSHIFT(1, 8 + lshift));
            }
            Inlines.silk_scale_vector32_Q26_lshift_18(WLTP, WLTP_ptr, temp32, 25);
            w[k] = Inlines.MatrixGet(WLTP, WLTP_ptr, 2, 2, 5);
            Inlines.OpusAssert(w[k] >= 0);
            r_ptr += subfr_length;
            b_Q14_ptr += 5;
            WLTP_ptr += 25;
        }
        int maxRshifts = 0;
        for (k = 0; k < nb_subfr; ++k) {
            maxRshifts = Inlines.silk_max_int(corr_rshifts[k], maxRshifts);
        }
        if (LTPredCodGain_Q7 != null) {
            int LPC_LTP_res_nrg = 0;
            int LPC_res_nrg = 0;
            Inlines.OpusAssert(true);
            for (k = 0; k < nb_subfr; ++k) {
                LPC_res_nrg = Inlines.silk_ADD32(LPC_res_nrg, Inlines.silk_RSHIFT(Inlines.silk_ADD32(Inlines.silk_SMULWB(rr[k], Wght_Q15[k]), 1), 1 + (maxRshifts - corr_rshifts[k])));
                LPC_LTP_res_nrg = Inlines.silk_ADD32(LPC_LTP_res_nrg, Inlines.silk_RSHIFT(Inlines.silk_ADD32(Inlines.silk_SMULWB(nrg[k], Wght_Q15[k]), 1), 1 + (maxRshifts - corr_rshifts[k])));
            }
            LPC_LTP_res_nrg = Inlines.silk_max(LPC_LTP_res_nrg, 1);
            int div_Q16 = Inlines.silk_DIV32_varQ(LPC_res_nrg, LPC_LTP_res_nrg, 16);
            LTPredCodGain_Q7.Val = Inlines.silk_SMULBB(3, Inlines.silk_lin2log(div_Q16) - 2048);
            Inlines.OpusAssert(LTPredCodGain_Q7.Val == Inlines.silk_SAT16(Inlines.silk_MUL(3, Inlines.silk_lin2log(div_Q16) - 2048)));
        }
        b_Q14_ptr = 0;
        for (k = 0; k < nb_subfr; ++k) {
            d_Q14[k] = 0;
            for (i = b_Q14_ptr; i < b_Q14_ptr + 5; ++i) {
                int n = k;
                d_Q14[n] = d_Q14[n] + b_Q14[i];
            }
            b_Q14_ptr += 5;
        }
        int max_abs_d_Q14 = 0;
        int max_w_bits = 0;
        for (k = 0; k < nb_subfr; ++k) {
            max_abs_d_Q14 = Inlines.silk_max_32(max_abs_d_Q14, Inlines.silk_abs(d_Q14[k]));
            max_w_bits = Inlines.silk_max_32(max_w_bits, 32 - Inlines.silk_CLZ32(w[k]) + corr_rshifts[k] - maxRshifts);
        }
        Inlines.OpusAssert(max_abs_d_Q14 <= 163840);
        extra_shifts = max_w_bits + 32 - Inlines.silk_CLZ32(max_abs_d_Q14) - 14;
        extra_shifts -= 29 + maxRshifts;
        extra_shifts = Inlines.silk_max_int(extra_shifts, 0);
        int maxRshifts_wxtra = maxRshifts + extra_shifts;
        temp32 = Inlines.silk_RSHIFT(262, maxRshifts + extra_shifts) + 1;
        int wd = 0;
        for (k = 0; k < nb_subfr; ++k) {
            temp32 = Inlines.silk_ADD32(temp32, Inlines.silk_RSHIFT(w[k], maxRshifts_wxtra - corr_rshifts[k]));
            wd = Inlines.silk_ADD32(wd, Inlines.silk_LSHIFT(Inlines.silk_SMULWW(Inlines.silk_RSHIFT(w[k], maxRshifts_wxtra - corr_rshifts[k]), d_Q14[k]), 2));
        }
        int m_Q12 = Inlines.silk_DIV32_varQ(wd, temp32, 12);
        b_Q14_ptr = 0;
        for (k = 0; k < nb_subfr; ++k) {
            temp32 = 2 - corr_rshifts[k] > 0 ? Inlines.silk_RSHIFT(w[k], 2 - corr_rshifts[k]) : Inlines.silk_LSHIFT_SAT32(w[k], corr_rshifts[k] - 2);
            int g_Q26 = Inlines.silk_MUL(Inlines.silk_DIV32(0x666667, Inlines.silk_RSHIFT(0x666667, 10) + temp32), Inlines.silk_LSHIFT_SAT32(Inlines.silk_SUB_SAT32(m_Q12, Inlines.silk_RSHIFT(d_Q14[k], 2)), 4));
            temp32 = 0;
            for (i = 0; i < 5; ++i) {
                delta_b_Q14[i] = Inlines.silk_max_16(b_Q14[b_Q14_ptr + i], (short)1638);
                temp32 += delta_b_Q14[i];
            }
            temp32 = Inlines.silk_DIV32(g_Q26, temp32);
            for (i = 0; i < 5; ++i) {
                b_Q14[b_Q14_ptr + i] = (short)Inlines.silk_LIMIT_32(b_Q14[b_Q14_ptr + i] + Inlines.silk_SMULWB(Inlines.silk_LSHIFT_SAT32(temp32, 4), delta_b_Q14[i]), -16000, 28000);
            }
            b_Q14_ptr += 5;
        }
    }

    static void silk_fit_LTP(int[] LTP_coefs_Q16, short[] LTP_coefs_Q14, int LTP_coefs_Q14_ptr) {
        for (int i = 0; i < 5; ++i) {
            LTP_coefs_Q14[LTP_coefs_Q14_ptr + i] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(LTP_coefs_Q16[i], 2));
        }
    }
}
