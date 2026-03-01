package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.Filters;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.SumSqrShift;

class ResidualEnergy {
    ResidualEnergy() {
    }

    static void silk_residual_energy(int[] nrgs, int[] nrgsQ, short[] x, short[][] a_Q12, int[] gains, int subfr_length, int nb_subfr, int LPC_order) {
        int i;
        BoxedValueInt rshift = new BoxedValueInt(0);
        BoxedValueInt energy = new BoxedValueInt(0);
        int x_ptr = 0;
        int offset = LPC_order + subfr_length;
        short[] LPC_res = new short[2 * offset];
        Inlines.OpusAssert((nb_subfr >> 1) * 2 == nb_subfr);
        for (i = 0; i < nb_subfr >> 1; ++i) {
            Filters.silk_LPC_analysis_filter(LPC_res, 0, x, x_ptr, a_Q12[i], 0, 2 * offset, LPC_order);
            int LPC_res_ptr = LPC_order;
            for (int j = 0; j < 2; ++j) {
                SumSqrShift.silk_sum_sqr_shift(energy, rshift, LPC_res, LPC_res_ptr, subfr_length);
                nrgs[i * 2 + j] = energy.Val;
                nrgsQ[i * 2 + j] = 0 - rshift.Val;
                LPC_res_ptr += offset;
            }
            x_ptr += 2 * offset;
        }
        i = 0;
        while (i < nb_subfr) {
            int lz1 = Inlines.silk_CLZ32(nrgs[i]) - 1;
            int lz2 = Inlines.silk_CLZ32(gains[i]) - 1;
            int tmp32 = Inlines.silk_LSHIFT32(gains[i], lz2);
            tmp32 = Inlines.silk_SMMUL(tmp32, tmp32);
            nrgs[i] = Inlines.silk_SMMUL(tmp32, Inlines.silk_LSHIFT32(nrgs[i], lz1));
            int n = i++;
            nrgsQ[n] = nrgsQ[n] + (lz1 + 2 * lz2 - 32 - 32);
        }
    }

    static int silk_residual_energy16_covar(short[] c, int c_ptr, int[] wXX, int wXX_ptr, int[] wXx, int wxx, int D, int cQ) {
        int i;
        int lshifts;
        int[] cn = new int[D];
        Inlines.OpusAssert(D >= 0);
        Inlines.OpusAssert(D <= 16);
        Inlines.OpusAssert(cQ > 0);
        Inlines.OpusAssert(cQ < 16);
        int Qxtra = lshifts = 16 - cQ;
        int c_max = 0;
        for (i = c_ptr; i < c_ptr + D; ++i) {
            c_max = Inlines.silk_max_32(c_max, Inlines.silk_abs(c[i]));
        }
        Qxtra = Inlines.silk_min_int(Qxtra, Inlines.silk_CLZ32(c_max) - 17);
        int w_max = Inlines.silk_max_32(wXX[wXX_ptr], wXX[wXX_ptr + D * D - 1]);
        Qxtra = Inlines.silk_min_int(Qxtra, Inlines.silk_CLZ32(Inlines.silk_MUL(D, Inlines.silk_RSHIFT(Inlines.silk_SMULWB(w_max, c_max), 4))) - 5);
        Qxtra = Inlines.silk_max_int(Qxtra, 0);
        for (i = 0; i < D; ++i) {
            cn[i] = Inlines.silk_LSHIFT(c[c_ptr + i], Qxtra);
            Inlines.OpusAssert(Inlines.silk_abs(cn[i]) <= 32768);
        }
        lshifts -= Qxtra;
        int tmp = 0;
        for (i = 0; i < D; ++i) {
            tmp = Inlines.silk_SMLAWB(tmp, wXx[i], cn[i]);
        }
        int nrg = Inlines.silk_RSHIFT(wxx, 1 + lshifts) - tmp;
        int tmp2 = 0;
        for (i = 0; i < D; ++i) {
            tmp = 0;
            int pRow = wXX_ptr + i * D;
            for (int j = i + 1; j < D; ++j) {
                tmp = Inlines.silk_SMLAWB(tmp, wXX[pRow + j], cn[j]);
            }
            tmp = Inlines.silk_SMLAWB(tmp, Inlines.silk_RSHIFT(wXX[pRow + i], 1), cn[i]);
            tmp2 = Inlines.silk_SMLAWB(tmp2, tmp, cn[i]);
        }
        nrg = (nrg = Inlines.silk_ADD_LSHIFT32(nrg, tmp2, lshifts)) < 1 ? 1 : (nrg > Inlines.silk_RSHIFT(Integer.MAX_VALUE, lshifts + 2) ? 0x3FFFFFFF : Inlines.silk_LSHIFT(nrg, lshifts + 1));
        return nrg;
    }
}
