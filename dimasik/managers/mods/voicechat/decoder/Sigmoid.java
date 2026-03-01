package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Inlines;

class Sigmoid {
    private static final int[] sigm_LUT_slope_Q10 = new int[]{237, 153, 73, 30, 12, 7};
    private static final int[] sigm_LUT_pos_Q15 = new int[]{16384, 23955, 28861, 31213, 32178, 32548};
    private static final int[] sigm_LUT_neg_Q15 = new int[]{16384, 8812, 3906, 1554, 589, 219};

    Sigmoid() {
    }

    static int silk_sigm_Q15(int in_Q5) {
        if (in_Q5 < 0) {
            if ((in_Q5 = -in_Q5) >= 192) {
                return 0;
            }
            int ind = Inlines.silk_RSHIFT(in_Q5, 5);
            return sigm_LUT_neg_Q15[ind] - Inlines.silk_SMULBB(sigm_LUT_slope_Q10[ind], in_Q5 & 0x1F);
        }
        if (in_Q5 >= 192) {
            return Short.MAX_VALUE;
        }
        int ind = Inlines.silk_RSHIFT(in_Q5, 5);
        return sigm_LUT_pos_Q15[ind] + Inlines.silk_SMULBB(sigm_LUT_slope_Q10[ind], in_Q5 & 0x1F);
    }
}
