package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Inlines;

class K2A {
    K2A() {
    }

    static void silk_k2a(int[] A_Q24, short[] rc_Q15, int order) {
        int[] Atmp = new int[16];
        for (int k = 0; k < order; ++k) {
            int n;
            for (n = 0; n < k; ++n) {
                Atmp[n] = A_Q24[n];
            }
            for (n = 0; n < k; ++n) {
                A_Q24[n] = Inlines.silk_SMLAWB(A_Q24[n], Inlines.silk_LSHIFT(Atmp[k - n - 1], 1), rc_Q15[k]);
            }
            A_Q24[k] = 0 - Inlines.silk_LSHIFT(rc_Q15[k], 9);
        }
    }

    static void silk_k2a_Q16(int[] A_Q24, int[] rc_Q16, int order) {
        int[] Atmp = new int[16];
        for (int k = 0; k < order; ++k) {
            int n;
            for (n = 0; n < k; ++n) {
                Atmp[n] = A_Q24[n];
            }
            for (n = 0; n < k; ++n) {
                A_Q24[n] = Inlines.silk_SMLAWW(A_Q24[n], Atmp[k - n - 1], rc_Q16[k]);
            }
            A_Q24[k] = 0 - Inlines.silk_LSHIFT(rc_Q16[k], 8);
        }
    }
}
