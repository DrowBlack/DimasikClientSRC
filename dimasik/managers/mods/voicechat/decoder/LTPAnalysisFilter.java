package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Inlines;

class LTPAnalysisFilter {
    LTPAnalysisFilter() {
    }

    static void silk_LTP_analysis_filter(short[] LTP_res, short[] x, int x_ptr, short[] LTPCoef_Q14, int[] pitchL, int[] invGains_Q16, int subfr_length, int nb_subfr, int pre_length) {
        short[] Btmp_Q14 = new short[5];
        int x_ptr2 = x_ptr;
        int LTP_res_ptr = 0;
        for (int k = 0; k < nb_subfr; ++k) {
            int x_lag_ptr = x_ptr2 - pitchL[k];
            Btmp_Q14[0] = LTPCoef_Q14[k * 5];
            Btmp_Q14[1] = LTPCoef_Q14[k * 5 + 1];
            Btmp_Q14[2] = LTPCoef_Q14[k * 5 + 2];
            Btmp_Q14[3] = LTPCoef_Q14[k * 5 + 3];
            Btmp_Q14[4] = LTPCoef_Q14[k * 5 + 4];
            for (int i = 0; i < subfr_length + pre_length; ++i) {
                int LTP_res_ptri = LTP_res_ptr + i;
                LTP_res[LTP_res_ptri] = x[x_ptr2 + i];
                int LTP_est = Inlines.silk_SMULBB(x[x_lag_ptr + 2], Btmp_Q14[0]);
                LTP_est = Inlines.silk_SMLABB_ovflw(LTP_est, x[x_lag_ptr + 1], Btmp_Q14[1]);
                LTP_est = Inlines.silk_SMLABB_ovflw(LTP_est, x[x_lag_ptr], Btmp_Q14[2]);
                LTP_est = Inlines.silk_SMLABB_ovflw(LTP_est, x[x_lag_ptr - 1], Btmp_Q14[3]);
                LTP_est = Inlines.silk_SMLABB_ovflw(LTP_est, x[x_lag_ptr - 2], Btmp_Q14[4]);
                LTP_est = Inlines.silk_RSHIFT_ROUND(LTP_est, 14);
                LTP_res[LTP_res_ptri] = (short)Inlines.silk_SAT16(x[x_ptr2 + i] - LTP_est);
                LTP_res[LTP_res_ptri] = (short)Inlines.silk_SMULWB(invGains_Q16[k], LTP_res[LTP_res_ptri]);
                ++x_lag_ptr;
            }
            LTP_res_ptr += subfr_length + pre_length;
            x_ptr2 += subfr_length;
        }
    }
}
