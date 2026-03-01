package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.BoxedValueByte;
import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.Inlines;

class VQ_WMat_EC {
    VQ_WMat_EC() {
    }

    static void silk_VQ_WMat_EC(BoxedValueByte ind, BoxedValueInt rate_dist_Q14, BoxedValueInt gain_Q7, short[] in_Q14, int in_Q14_ptr, int[] W_Q18, int W_Q18_ptr, byte[][] cb_Q7, short[] cb_gain_Q7, short[] cl_Q5, int mu_Q9, int max_gain_Q7, int L) {
        int cb_row_Q7_ptr = 0;
        short[] diff_Q14 = new short[5];
        rate_dist_Q14.Val = Integer.MAX_VALUE;
        for (int k = 0; k < L; ++k) {
            byte[] cb_row_Q7 = cb_Q7[cb_row_Q7_ptr++];
            int gain_tmp_Q7 = cb_gain_Q7[k];
            diff_Q14[0] = (short)(in_Q14[in_Q14_ptr] - Inlines.silk_LSHIFT(cb_row_Q7[0], 7));
            diff_Q14[1] = (short)(in_Q14[in_Q14_ptr + 1] - Inlines.silk_LSHIFT(cb_row_Q7[1], 7));
            diff_Q14[2] = (short)(in_Q14[in_Q14_ptr + 2] - Inlines.silk_LSHIFT(cb_row_Q7[2], 7));
            diff_Q14[3] = (short)(in_Q14[in_Q14_ptr + 3] - Inlines.silk_LSHIFT(cb_row_Q7[3], 7));
            diff_Q14[4] = (short)(in_Q14[in_Q14_ptr + 4] - Inlines.silk_LSHIFT(cb_row_Q7[4], 7));
            int sum1_Q14 = Inlines.silk_SMULBB(mu_Q9, cl_Q5[k]);
            Inlines.OpusAssert((sum1_Q14 = Inlines.silk_ADD_LSHIFT32(sum1_Q14, Inlines.silk_max(Inlines.silk_SUB32(gain_tmp_Q7, max_gain_Q7), 0), 10)) >= 0);
            int sum2_Q16 = Inlines.silk_SMULWB(W_Q18[W_Q18_ptr + 1], diff_Q14[1]);
            sum2_Q16 = Inlines.silk_SMLAWB(sum2_Q16, W_Q18[W_Q18_ptr + 2], diff_Q14[2]);
            sum2_Q16 = Inlines.silk_SMLAWB(sum2_Q16, W_Q18[W_Q18_ptr + 3], diff_Q14[3]);
            sum2_Q16 = Inlines.silk_SMLAWB(sum2_Q16, W_Q18[W_Q18_ptr + 4], diff_Q14[4]);
            sum2_Q16 = Inlines.silk_LSHIFT(sum2_Q16, 1);
            sum2_Q16 = Inlines.silk_SMLAWB(sum2_Q16, W_Q18[W_Q18_ptr], diff_Q14[0]);
            sum1_Q14 = Inlines.silk_SMLAWB(sum1_Q14, sum2_Q16, diff_Q14[0]);
            sum2_Q16 = Inlines.silk_SMULWB(W_Q18[W_Q18_ptr + 7], diff_Q14[2]);
            sum2_Q16 = Inlines.silk_SMLAWB(sum2_Q16, W_Q18[W_Q18_ptr + 8], diff_Q14[3]);
            sum2_Q16 = Inlines.silk_SMLAWB(sum2_Q16, W_Q18[W_Q18_ptr + 9], diff_Q14[4]);
            sum2_Q16 = Inlines.silk_LSHIFT(sum2_Q16, 1);
            sum2_Q16 = Inlines.silk_SMLAWB(sum2_Q16, W_Q18[W_Q18_ptr + 6], diff_Q14[1]);
            sum1_Q14 = Inlines.silk_SMLAWB(sum1_Q14, sum2_Q16, diff_Q14[1]);
            sum2_Q16 = Inlines.silk_SMULWB(W_Q18[W_Q18_ptr + 13], diff_Q14[3]);
            sum2_Q16 = Inlines.silk_SMLAWB(sum2_Q16, W_Q18[W_Q18_ptr + 14], diff_Q14[4]);
            sum2_Q16 = Inlines.silk_LSHIFT(sum2_Q16, 1);
            sum2_Q16 = Inlines.silk_SMLAWB(sum2_Q16, W_Q18[W_Q18_ptr + 12], diff_Q14[2]);
            sum1_Q14 = Inlines.silk_SMLAWB(sum1_Q14, sum2_Q16, diff_Q14[2]);
            sum2_Q16 = Inlines.silk_SMULWB(W_Q18[W_Q18_ptr + 19], diff_Q14[4]);
            sum2_Q16 = Inlines.silk_LSHIFT(sum2_Q16, 1);
            sum2_Q16 = Inlines.silk_SMLAWB(sum2_Q16, W_Q18[W_Q18_ptr + 18], diff_Q14[3]);
            sum1_Q14 = Inlines.silk_SMLAWB(sum1_Q14, sum2_Q16, diff_Q14[3]);
            sum2_Q16 = Inlines.silk_SMULWB(W_Q18[W_Q18_ptr + 24], diff_Q14[4]);
            sum1_Q14 = Inlines.silk_SMLAWB(sum1_Q14, sum2_Q16, diff_Q14[4]);
            Inlines.OpusAssert(sum1_Q14 >= 0);
            if (sum1_Q14 >= rate_dist_Q14.Val) continue;
            rate_dist_Q14.Val = sum1_Q14;
            ind.Val = (byte)k;
            gain_Q7.Val = gain_tmp_Q7;
        }
    }
}
