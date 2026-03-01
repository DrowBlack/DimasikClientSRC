package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Inlines;

class BWExpander {
    BWExpander() {
    }

    static void silk_bwexpander_32(int[] ar, int d, int chirp_Q16) {
        int chirp_minus_one_Q16 = chirp_Q16 - 65536;
        for (int i = 0; i < d - 1; ++i) {
            ar[i] = Inlines.silk_SMULWW(chirp_Q16, ar[i]);
            chirp_Q16 += Inlines.silk_RSHIFT_ROUND(Inlines.silk_MUL(chirp_Q16, chirp_minus_one_Q16), 16);
        }
        ar[d - 1] = Inlines.silk_SMULWW(chirp_Q16, ar[d - 1]);
    }

    static void silk_bwexpander(short[] ar, int d, int chirp_Q16) {
        int chirp_minus_one_Q16 = chirp_Q16 - 65536;
        for (int i = 0; i < d - 1; ++i) {
            ar[i] = (short)Inlines.silk_RSHIFT_ROUND(Inlines.silk_MUL(chirp_Q16, ar[i]), 16);
            chirp_Q16 += Inlines.silk_RSHIFT_ROUND(Inlines.silk_MUL(chirp_Q16, chirp_minus_one_Q16), 16);
        }
        ar[d - 1] = (short)Inlines.silk_RSHIFT_ROUND(Inlines.silk_MUL(chirp_Q16, ar[d - 1]), 16);
    }
}
