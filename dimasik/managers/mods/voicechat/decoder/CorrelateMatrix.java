package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.SumSqrShift;

class CorrelateMatrix {
    CorrelateMatrix() {
    }

    static void silk_corrVector(short[] x, int x_ptr, short[] t, int t_ptr, int L, int order, int[] Xt, int rshifts) {
        int ptr1 = x_ptr + order - 1;
        int ptr2 = t_ptr;
        if (rshifts > 0) {
            for (int lag = 0; lag < order; ++lag) {
                int inner_prod = 0;
                for (int i = 0; i < L; ++i) {
                    inner_prod += Inlines.silk_RSHIFT32(Inlines.silk_SMULBB(x[ptr1 + i], t[ptr2 + i]), rshifts);
                }
                Xt[lag] = inner_prod;
                --ptr1;
            }
        } else {
            Inlines.OpusAssert(rshifts == 0);
            for (int lag = 0; lag < order; ++lag) {
                Xt[lag] = Inlines.silk_inner_prod(x, ptr1, t, ptr2, L);
                --ptr1;
            }
        }
    }

    static void silk_corrMatrix(short[] x, int x_ptr, int L, int order, int head_room, int[] XX, int XX_ptr, BoxedValueInt rshifts) {
        int j;
        int i;
        BoxedValueInt boxed_energy = new BoxedValueInt(0);
        BoxedValueInt boxed_rshifts_local = new BoxedValueInt(0);
        SumSqrShift.silk_sum_sqr_shift(boxed_energy, boxed_rshifts_local, x, x_ptr, L + order - 1);
        int energy = boxed_energy.Val;
        int rshifts_local = boxed_rshifts_local.Val;
        int head_room_rshifts = Inlines.silk_max(head_room - Inlines.silk_CLZ32(energy), 0);
        energy = Inlines.silk_RSHIFT32(energy, head_room_rshifts);
        rshifts_local += head_room_rshifts;
        for (i = x_ptr; i < x_ptr + order - 1; ++i) {
            energy -= Inlines.silk_RSHIFT32(Inlines.silk_SMULBB(x[i], x[i]), rshifts_local);
        }
        if (rshifts_local < rshifts.Val) {
            energy = Inlines.silk_RSHIFT32(energy, rshifts.Val - rshifts_local);
            rshifts_local = rshifts.Val;
        }
        Inlines.MatrixSet(XX, XX_ptr, 0, 0, order, energy);
        int ptr1 = x_ptr + order - 1;
        for (j = 1; j < order; ++j) {
            energy = Inlines.silk_SUB32(energy, Inlines.silk_RSHIFT32(Inlines.silk_SMULBB(x[ptr1 + L - j], x[ptr1 + L - j]), rshifts_local));
            energy = Inlines.silk_ADD32(energy, Inlines.silk_RSHIFT32(Inlines.silk_SMULBB(x[ptr1 - j], x[ptr1 - j]), rshifts_local));
            Inlines.MatrixSet(XX, XX_ptr, j, j, order, energy);
        }
        int ptr2 = x_ptr + order - 2;
        if (rshifts_local > 0) {
            for (int lag = 1; lag < order; ++lag) {
                energy = 0;
                for (i = 0; i < L; ++i) {
                    energy += Inlines.silk_RSHIFT32(Inlines.silk_SMULBB(x[ptr1 + i], x[ptr2 + i]), rshifts_local);
                }
                Inlines.MatrixSet(XX, XX_ptr, lag, 0, order, energy);
                Inlines.MatrixSet(XX, XX_ptr, 0, lag, order, energy);
                for (j = 1; j < order - lag; ++j) {
                    energy = Inlines.silk_SUB32(energy, Inlines.silk_RSHIFT32(Inlines.silk_SMULBB(x[ptr1 + L - j], x[ptr2 + L - j]), rshifts_local));
                    energy = Inlines.silk_ADD32(energy, Inlines.silk_RSHIFT32(Inlines.silk_SMULBB(x[ptr1 - j], x[ptr2 - j]), rshifts_local));
                    Inlines.MatrixSet(XX, XX_ptr, lag + j, j, order, energy);
                    Inlines.MatrixSet(XX, XX_ptr, j, lag + j, order, energy);
                }
                --ptr2;
            }
        } else {
            for (int lag = 1; lag < order; ++lag) {
                energy = Inlines.silk_inner_prod(x, ptr1, x, ptr2, L);
                Inlines.MatrixSet(XX, XX_ptr, lag, 0, order, energy);
                Inlines.MatrixSet(XX, XX_ptr, 0, lag, order, energy);
                for (j = 1; j < order - lag; ++j) {
                    energy = Inlines.silk_SUB32(energy, Inlines.silk_SMULBB(x[ptr1 + L - j], x[ptr2 + L - j]));
                    energy = Inlines.silk_SMLABB(energy, x[ptr1 - j], x[ptr2 - j]);
                    Inlines.MatrixSet(XX, XX_ptr, lag + j, j, order, energy);
                    Inlines.MatrixSet(XX, XX_ptr, j, lag + j, order, energy);
                }
                --ptr2;
            }
        }
        rshifts.Val = rshifts_local;
    }
}
