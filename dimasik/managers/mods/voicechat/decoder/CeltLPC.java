package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.Kernels;

class CeltLPC {
    CeltLPC() {
    }

    static void celt_lpc(int[] _lpc, int[] ac, int p) {
        int i;
        int error = ac[0];
        int[] lpc = new int[p];
        if (ac[0] != 0) {
            for (i = 0; i < p; ++i) {
                int j;
                int rr = 0;
                for (j = 0; j < i; ++j) {
                    rr += Inlines.MULT32_32_Q31(lpc[j], ac[i - j]);
                }
                int r = 0 - Inlines.frac_div32(Inlines.SHL32(rr += Inlines.SHR32(ac[i + 1], 3), 3), error);
                lpc[i] = Inlines.SHR32(r, 3);
                for (j = 0; j < i + 1 >> 1; ++j) {
                    int tmp1 = lpc[j];
                    int tmp2 = lpc[i - 1 - j];
                    lpc[j] = tmp1 + Inlines.MULT32_32_Q31(r, tmp2);
                    lpc[i - 1 - j] = tmp2 + Inlines.MULT32_32_Q31(r, tmp1);
                }
                if ((error -= Inlines.MULT32_32_Q31(Inlines.MULT32_32_Q31(r, r), error)) < Inlines.SHR32(ac[0], 10)) break;
            }
        }
        for (i = 0; i < p; ++i) {
            _lpc[i] = Inlines.ROUND16(lpc[i], 16);
        }
    }

    static void celt_iir(int[] _x, int _x_ptr, int[] den, int[] _y, int _y_ptr, int N, int ord, int[] mem) {
        int i;
        int[] rden = new int[ord];
        int[] y = new int[N + ord];
        Inlines.OpusAssert((ord & 3) == 0);
        BoxedValueInt _sum0 = new BoxedValueInt(0);
        BoxedValueInt _sum1 = new BoxedValueInt(0);
        BoxedValueInt _sum2 = new BoxedValueInt(0);
        BoxedValueInt _sum3 = new BoxedValueInt(0);
        for (i = 0; i < ord; ++i) {
            rden[i] = den[ord - i - 1];
        }
        for (i = 0; i < ord; ++i) {
            y[i] = 0 - mem[ord - i - 1];
        }
        while (i < N + ord) {
            y[i] = 0;
            ++i;
        }
        for (i = 0; i < N - 3; i += 4) {
            _sum0.Val = _x[_x_ptr + i];
            _sum1.Val = _x[_x_ptr + i + 1];
            _sum2.Val = _x[_x_ptr + i + 2];
            _sum3.Val = _x[_x_ptr + i + 3];
            Kernels.xcorr_kernel(rden, y, i, _sum0, _sum1, _sum2, _sum3, ord);
            int sum0 = _sum0.Val;
            int sum1 = _sum1.Val;
            int sum2 = _sum2.Val;
            int sum3 = _sum3.Val;
            y[i + ord] = 0 - Inlines.ROUND16(sum0, 12);
            _y[_y_ptr + i] = sum0;
            sum1 = Inlines.MAC16_16(sum1, y[i + ord], den[0]);
            y[i + ord + 1] = 0 - Inlines.ROUND16(sum1, 12);
            _y[_y_ptr + i + 1] = sum1;
            sum2 = Inlines.MAC16_16(sum2, y[i + ord + 1], den[0]);
            sum2 = Inlines.MAC16_16(sum2, y[i + ord], den[1]);
            y[i + ord + 2] = 0 - Inlines.ROUND16(sum2, 12);
            _y[_y_ptr + i + 2] = sum2;
            sum3 = Inlines.MAC16_16(sum3, y[i + ord + 2], den[0]);
            sum3 = Inlines.MAC16_16(sum3, y[i + ord + 1], den[1]);
            sum3 = Inlines.MAC16_16(sum3, y[i + ord], den[2]);
            y[i + ord + 3] = 0 - Inlines.ROUND16(sum3, 12);
            _y[_y_ptr + i + 3] = sum3;
        }
        while (i < N) {
            int sum = _x[_x_ptr + i];
            for (int j = 0; j < ord; ++j) {
                sum -= Inlines.MULT16_16(rden[j], y[i + j]);
            }
            y[i + ord] = Inlines.ROUND16(sum, 12);
            _y[_y_ptr + i] = sum;
            ++i;
        }
        for (i = 0; i < ord; ++i) {
            mem[i] = _y[_y_ptr + N - i - 1];
        }
    }
}
