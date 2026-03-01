package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.Inlines;

class SumSqrShift {
    SumSqrShift() {
    }

    static void silk_sum_sqr_shift(BoxedValueInt energy, BoxedValueInt shift, short[] x, int x_ptr, int len) {
        int nrg_tmp;
        int i;
        int nrg = 0;
        int shft = 0;
        --len;
        for (i = 0; i < len; i += 2) {
            nrg = Inlines.silk_SMLABB_ovflw(nrg, x[x_ptr + i], x[x_ptr + i]);
            if ((nrg = Inlines.silk_SMLABB_ovflw(nrg, x[x_ptr + i + 1], x[x_ptr + i + 1])) >= 0) continue;
            nrg = (int)Inlines.silk_RSHIFT_uint(nrg, 2);
            shft = 2;
            i += 2;
            break;
        }
        while (i < len) {
            nrg_tmp = Inlines.silk_SMULBB(x[x_ptr + i], x[x_ptr + i]);
            if ((nrg = (int)Inlines.silk_ADD_RSHIFT_uint(nrg, nrg_tmp = Inlines.silk_SMLABB_ovflw(nrg_tmp, x[x_ptr + i + 1], x[x_ptr + i + 1]), shft)) < 0) {
                nrg = (int)Inlines.silk_RSHIFT_uint(nrg, 2);
                shft += 2;
            }
            i += 2;
        }
        if (i == len) {
            nrg_tmp = Inlines.silk_SMULBB(x[x_ptr + i], x[x_ptr + i]);
            nrg = (int)Inlines.silk_ADD_RSHIFT_uint(nrg, nrg_tmp, shft);
        }
        if ((nrg & 0xC0000000) != 0) {
            nrg = (int)Inlines.silk_RSHIFT_uint(nrg, 2);
            shft += 2;
        }
        shift.Val = shft;
        energy.Val = nrg;
    }

    static void silk_sum_sqr_shift(BoxedValueInt energy, BoxedValueInt shift, short[] x, int len) {
        int nrg_tmp;
        int i;
        int nrg = 0;
        int shft = 0;
        --len;
        for (i = 0; i < len; i += 2) {
            nrg = Inlines.silk_SMLABB_ovflw(nrg, x[i], x[i]);
            if ((nrg = Inlines.silk_SMLABB_ovflw(nrg, x[i + 1], x[i + 1])) >= 0) continue;
            nrg = (int)Inlines.silk_RSHIFT_uint(nrg, 2);
            shft = 2;
            i += 2;
            break;
        }
        while (i < len) {
            nrg_tmp = Inlines.silk_SMULBB(x[i], x[i]);
            if ((nrg = (int)Inlines.silk_ADD_RSHIFT_uint(nrg, nrg_tmp = Inlines.silk_SMLABB_ovflw(nrg_tmp, x[i + 1], x[i + 1]), shft)) < 0) {
                nrg = (int)Inlines.silk_RSHIFT_uint(nrg, 2);
                shft += 2;
            }
            i += 2;
        }
        if (i == len) {
            nrg_tmp = Inlines.silk_SMULBB(x[i], x[i]);
            nrg = (int)Inlines.silk_ADD_RSHIFT_uint(nrg, nrg_tmp, shft);
        }
        if ((nrg & 0xC0000000) != 0) {
            nrg = (int)Inlines.silk_RSHIFT_uint(nrg, 2);
            shft += 2;
        }
        shift.Val = shft;
        energy.Val = nrg;
    }
}
