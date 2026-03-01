package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.BoxedValueByte;
import dimasik.managers.mods.voicechat.decoder.Inlines;

class GainQuantization {
    private static final int OFFSET = 2090;
    private static final int SCALE_Q16 = 2251;
    private static final int INV_SCALE_Q16 = 1907825;

    GainQuantization() {
    }

    static void silk_gains_quant(byte[] ind, int[] gain_Q16, BoxedValueByte prev_ind, int conditional, int nb_subfr) {
        for (int k = 0; k < nb_subfr; ++k) {
            ind[k] = (byte)Inlines.silk_SMULWB(2251, Inlines.silk_lin2log(gain_Q16[k]) - 2090);
            if (ind[k] < prev_ind.Val) {
                int n = k;
                ind[n] = (byte)(ind[n] + 1);
            }
            ind[k] = (byte)Inlines.silk_LIMIT_int(ind[k], 0, 63);
            if (k == 0 && conditional == 0) {
                ind[k] = (byte)Inlines.silk_LIMIT_int(ind[k], prev_ind.Val + -4, 63);
                prev_ind.Val = ind[k];
            } else {
                ind[k] = (byte)(ind[k] - prev_ind.Val);
                int double_step_size_threshold = 8 + prev_ind.Val;
                if (ind[k] > double_step_size_threshold) {
                    ind[k] = (byte)(double_step_size_threshold + Inlines.silk_RSHIFT(ind[k] - double_step_size_threshold + 1, 1));
                }
                ind[k] = (byte)Inlines.silk_LIMIT_int(ind[k], -4, 36);
                prev_ind.Val = ind[k] > double_step_size_threshold ? (byte)(prev_ind.Val + (byte)(Inlines.silk_LSHIFT(ind[k], 1) - double_step_size_threshold)) : (byte)(prev_ind.Val + ind[k]);
                int n = k;
                ind[n] = (byte)(ind[n] - -4);
            }
            gain_Q16[k] = Inlines.silk_log2lin(Inlines.silk_min_32(Inlines.silk_SMULWB(1907825, prev_ind.Val) + 2090, 3967));
        }
    }

    static void silk_gains_dequant(int[] gain_Q16, byte[] ind, BoxedValueByte prev_ind, int conditional, int nb_subfr) {
        for (int k = 0; k < nb_subfr; ++k) {
            int double_step_size_threshold;
            int ind_tmp;
            prev_ind.Val = k == 0 && conditional == 0 ? (byte)Inlines.silk_max_int(ind[k], prev_ind.Val - 16) : ((ind_tmp = ind[k] + -4) > (double_step_size_threshold = 8 + prev_ind.Val) ? (byte)(prev_ind.Val + (byte)(Inlines.silk_LSHIFT(ind_tmp, 1) - double_step_size_threshold)) : (byte)(prev_ind.Val + (byte)ind_tmp));
            prev_ind.Val = (byte)Inlines.silk_LIMIT_int(prev_ind.Val, 0, 63);
            gain_Q16[k] = Inlines.silk_log2lin(Inlines.silk_min_32(Inlines.silk_SMULWB(1907825, prev_ind.Val) + 2090, 3967));
        }
    }

    static int silk_gains_ID(byte[] ind, int nb_subfr) {
        int gainsID = 0;
        for (int k = 0; k < nb_subfr; ++k) {
            gainsID = Inlines.silk_ADD_LSHIFT32(ind[k], gainsID, 8);
        }
        return gainsID;
    }
}
