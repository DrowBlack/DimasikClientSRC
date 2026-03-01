package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.Inlines;

class Kernels {
    Kernels() {
    }

    static void celt_fir(short[] x, int x_ptr, short[] num, short[] y, int y_ptr, int N, int ord, short[] mem) {
        int i;
        short[] rnum = new short[ord];
        short[] local_x = new short[N + ord];
        for (i = 0; i < ord; ++i) {
            rnum[i] = num[ord - i - 1];
        }
        for (i = 0; i < ord; ++i) {
            local_x[i] = mem[ord - i - 1];
        }
        for (i = 0; i < N; ++i) {
            local_x[i + ord] = x[x_ptr + i];
        }
        for (i = 0; i < ord; ++i) {
            mem[i] = x[x_ptr + N - i - 1];
        }
        BoxedValueInt sum0 = new BoxedValueInt(0);
        BoxedValueInt sum1 = new BoxedValueInt(0);
        BoxedValueInt sum2 = new BoxedValueInt(0);
        BoxedValueInt sum3 = new BoxedValueInt(0);
        for (i = 0; i < N - 3; i += 4) {
            sum0.Val = 0;
            sum1.Val = 0;
            sum2.Val = 0;
            sum3.Val = 0;
            Kernels.xcorr_kernel(rnum, 0, local_x, i, sum0, sum1, sum2, sum3, ord);
            y[y_ptr + i] = Inlines.SATURATE16(Inlines.ADD32(Inlines.EXTEND32(x[x_ptr + i]), Inlines.PSHR32(sum0.Val, 12)));
            y[y_ptr + i + 1] = Inlines.SATURATE16(Inlines.ADD32(Inlines.EXTEND32(x[x_ptr + i + 1]), Inlines.PSHR32(sum1.Val, 12)));
            y[y_ptr + i + 2] = Inlines.SATURATE16(Inlines.ADD32(Inlines.EXTEND32(x[x_ptr + i + 2]), Inlines.PSHR32(sum2.Val, 12)));
            y[y_ptr + i + 3] = Inlines.SATURATE16(Inlines.ADD32(Inlines.EXTEND32(x[x_ptr + i + 3]), Inlines.PSHR32(sum3.Val, 12)));
        }
        while (i < N) {
            int sum = 0;
            for (int j = 0; j < ord; ++j) {
                sum = Inlines.MAC16_16(sum, rnum[j], local_x[i + j]);
            }
            y[y_ptr + i] = Inlines.SATURATE16(Inlines.ADD32(Inlines.EXTEND32(x[x_ptr + i]), Inlines.PSHR32(sum, 12)));
            ++i;
        }
    }

    static void celt_fir(int[] x, int x_ptr, int[] num, int num_ptr, int[] y, int y_ptr, int N, int ord, int[] mem) {
        int i;
        int[] rnum = new int[ord];
        int[] local_x = new int[N + ord];
        for (i = 0; i < ord; ++i) {
            rnum[i] = num[num_ptr + ord - i - 1];
        }
        for (i = 0; i < ord; ++i) {
            local_x[i] = mem[ord - i - 1];
        }
        for (i = 0; i < N; ++i) {
            local_x[i + ord] = x[x_ptr + i];
        }
        for (i = 0; i < ord; ++i) {
            mem[i] = x[x_ptr + N - i - 1];
        }
        BoxedValueInt sum0 = new BoxedValueInt(0);
        BoxedValueInt sum1 = new BoxedValueInt(0);
        BoxedValueInt sum2 = new BoxedValueInt(0);
        BoxedValueInt sum3 = new BoxedValueInt(0);
        for (i = 0; i < N - 3; i += 4) {
            sum0.Val = 0;
            sum1.Val = 0;
            sum2.Val = 0;
            sum3.Val = 0;
            Kernels.xcorr_kernel(rnum, local_x, i, sum0, sum1, sum2, sum3, ord);
            y[y_ptr + i] = Inlines.SATURATE16(Inlines.ADD32(Inlines.EXTEND32(x[x_ptr + i]), Inlines.PSHR32(sum0.Val, 12)));
            y[y_ptr + i + 1] = Inlines.SATURATE16(Inlines.ADD32(Inlines.EXTEND32(x[x_ptr + i + 1]), Inlines.PSHR32(sum1.Val, 12)));
            y[y_ptr + i + 2] = Inlines.SATURATE16(Inlines.ADD32(Inlines.EXTEND32(x[x_ptr + i + 2]), Inlines.PSHR32(sum2.Val, 12)));
            y[y_ptr + i + 3] = Inlines.SATURATE16(Inlines.ADD32(Inlines.EXTEND32(x[x_ptr + i + 3]), Inlines.PSHR32(sum3.Val, 12)));
        }
        while (i < N) {
            int sum = 0;
            for (int j = 0; j < ord; ++j) {
                sum = Inlines.MAC16_16(sum, rnum[j], local_x[i + j]);
            }
            y[y_ptr + i] = Inlines.SATURATE16(Inlines.ADD32(Inlines.EXTEND32(x[x_ptr + i]), Inlines.PSHR32(sum, 12)));
            ++i;
        }
    }

    static void xcorr_kernel(short[] x, int x_ptr, short[] y, int y_ptr, BoxedValueInt _sum0, BoxedValueInt _sum1, BoxedValueInt _sum2, BoxedValueInt _sum3, int len) {
        short tmp;
        int j;
        int sum0 = _sum0.Val;
        int sum1 = _sum1.Val;
        int sum2 = _sum2.Val;
        int sum3 = _sum3.Val;
        Inlines.OpusAssert(len >= 3);
        short y_3 = 0;
        short y_0 = y[y_ptr++];
        short y_1 = y[y_ptr++];
        short y_2 = y[y_ptr++];
        for (j = 0; j < len - 3; j += 4) {
            tmp = x[x_ptr++];
            y_3 = y[y_ptr++];
            sum0 = Inlines.MAC16_16(sum0, tmp, y_0);
            sum1 = Inlines.MAC16_16(sum1, tmp, y_1);
            sum2 = Inlines.MAC16_16(sum2, tmp, y_2);
            sum3 = Inlines.MAC16_16(sum3, tmp, y_3);
            tmp = x[x_ptr++];
            y_0 = y[y_ptr++];
            sum0 = Inlines.MAC16_16(sum0, tmp, y_1);
            sum1 = Inlines.MAC16_16(sum1, tmp, y_2);
            sum2 = Inlines.MAC16_16(sum2, tmp, y_3);
            sum3 = Inlines.MAC16_16(sum3, tmp, y_0);
            tmp = x[x_ptr++];
            y_1 = y[y_ptr++];
            sum0 = Inlines.MAC16_16(sum0, tmp, y_2);
            sum1 = Inlines.MAC16_16(sum1, tmp, y_3);
            sum2 = Inlines.MAC16_16(sum2, tmp, y_0);
            sum3 = Inlines.MAC16_16(sum3, tmp, y_1);
            tmp = x[x_ptr++];
            y_2 = y[y_ptr++];
            sum0 = Inlines.MAC16_16(sum0, tmp, y_3);
            sum1 = Inlines.MAC16_16(sum1, tmp, y_0);
            sum2 = Inlines.MAC16_16(sum2, tmp, y_1);
            sum3 = Inlines.MAC16_16(sum3, tmp, y_2);
        }
        if (j++ < len) {
            tmp = x[x_ptr++];
            y_3 = y[y_ptr++];
            sum0 = Inlines.MAC16_16(sum0, tmp, y_0);
            sum1 = Inlines.MAC16_16(sum1, tmp, y_1);
            sum2 = Inlines.MAC16_16(sum2, tmp, y_2);
            sum3 = Inlines.MAC16_16(sum3, tmp, y_3);
        }
        if (j++ < len) {
            tmp = x[x_ptr++];
            y_0 = y[y_ptr++];
            sum0 = Inlines.MAC16_16(sum0, tmp, y_1);
            sum1 = Inlines.MAC16_16(sum1, tmp, y_2);
            sum2 = Inlines.MAC16_16(sum2, tmp, y_3);
            sum3 = Inlines.MAC16_16(sum3, tmp, y_0);
        }
        if (j < len) {
            tmp = x[x_ptr++];
            y_1 = y[y_ptr++];
            sum0 = Inlines.MAC16_16(sum0, tmp, y_2);
            sum1 = Inlines.MAC16_16(sum1, tmp, y_3);
            sum2 = Inlines.MAC16_16(sum2, tmp, y_0);
            sum3 = Inlines.MAC16_16(sum3, tmp, y_1);
        }
        _sum0.Val = sum0;
        _sum1.Val = sum1;
        _sum2.Val = sum2;
        _sum3.Val = sum3;
    }

    static void xcorr_kernel(int[] x, int[] y, int y_ptr, BoxedValueInt _sum0, BoxedValueInt _sum1, BoxedValueInt _sum2, BoxedValueInt _sum3, int len) {
        int tmp;
        int j;
        int sum0 = _sum0.Val;
        int sum1 = _sum1.Val;
        int sum2 = _sum2.Val;
        int sum3 = _sum3.Val;
        int x_ptr = 0;
        Inlines.OpusAssert(len >= 3);
        int y_3 = 0;
        int y_0 = y[y_ptr++];
        int y_1 = y[y_ptr++];
        int y_2 = y[y_ptr++];
        for (j = 0; j < len - 3; j += 4) {
            tmp = x[x_ptr++];
            y_3 = y[y_ptr++];
            sum0 = Inlines.MAC16_16(sum0, tmp, y_0);
            sum1 = Inlines.MAC16_16(sum1, tmp, y_1);
            sum2 = Inlines.MAC16_16(sum2, tmp, y_2);
            sum3 = Inlines.MAC16_16(sum3, tmp, y_3);
            tmp = x[x_ptr++];
            y_0 = y[y_ptr++];
            sum0 = Inlines.MAC16_16(sum0, tmp, y_1);
            sum1 = Inlines.MAC16_16(sum1, tmp, y_2);
            sum2 = Inlines.MAC16_16(sum2, tmp, y_3);
            sum3 = Inlines.MAC16_16(sum3, tmp, y_0);
            tmp = x[x_ptr++];
            y_1 = y[y_ptr++];
            sum0 = Inlines.MAC16_16(sum0, tmp, y_2);
            sum1 = Inlines.MAC16_16(sum1, tmp, y_3);
            sum2 = Inlines.MAC16_16(sum2, tmp, y_0);
            sum3 = Inlines.MAC16_16(sum3, tmp, y_1);
            tmp = x[x_ptr++];
            y_2 = y[y_ptr++];
            sum0 = Inlines.MAC16_16(sum0, tmp, y_3);
            sum1 = Inlines.MAC16_16(sum1, tmp, y_0);
            sum2 = Inlines.MAC16_16(sum2, tmp, y_1);
            sum3 = Inlines.MAC16_16(sum3, tmp, y_2);
        }
        if (j++ < len) {
            tmp = x[x_ptr++];
            y_3 = y[y_ptr++];
            sum0 = Inlines.MAC16_16(sum0, tmp, y_0);
            sum1 = Inlines.MAC16_16(sum1, tmp, y_1);
            sum2 = Inlines.MAC16_16(sum2, tmp, y_2);
            sum3 = Inlines.MAC16_16(sum3, tmp, y_3);
        }
        if (j++ < len) {
            tmp = x[x_ptr++];
            y_0 = y[y_ptr++];
            sum0 = Inlines.MAC16_16(sum0, tmp, y_1);
            sum1 = Inlines.MAC16_16(sum1, tmp, y_2);
            sum2 = Inlines.MAC16_16(sum2, tmp, y_3);
            sum3 = Inlines.MAC16_16(sum3, tmp, y_0);
        }
        if (j < len) {
            tmp = x[x_ptr++];
            y_1 = y[y_ptr++];
            sum0 = Inlines.MAC16_16(sum0, tmp, y_2);
            sum1 = Inlines.MAC16_16(sum1, tmp, y_3);
            sum2 = Inlines.MAC16_16(sum2, tmp, y_0);
            sum3 = Inlines.MAC16_16(sum3, tmp, y_1);
        }
        _sum0.Val = sum0;
        _sum1.Val = sum1;
        _sum2.Val = sum2;
        _sum3.Val = sum3;
    }

    static int celt_inner_prod(short[] x, int x_ptr, short[] y, int y_ptr, int N) {
        int xy = 0;
        for (int i = 0; i < N; ++i) {
            xy = Inlines.MAC16_16(xy, x[x_ptr + i], y[y_ptr + i]);
        }
        return xy;
    }

    static int celt_inner_prod(short[] x, short[] y, int y_ptr, int N) {
        int xy = 0;
        for (int i = 0; i < N; ++i) {
            xy = Inlines.MAC16_16(xy, x[i], y[y_ptr + i]);
        }
        return xy;
    }

    static int celt_inner_prod(int[] x, int x_ptr, int[] y, int y_ptr, int N) {
        int xy = 0;
        for (int i = 0; i < N; ++i) {
            xy = Inlines.MAC16_16(xy, x[x_ptr + i], y[y_ptr + i]);
        }
        return xy;
    }

    static void dual_inner_prod(int[] x, int x_ptr, int[] y01, int y01_ptr, int[] y02, int y02_ptr, int N, BoxedValueInt xy1, BoxedValueInt xy2) {
        int xy01 = 0;
        int xy02 = 0;
        for (int i = 0; i < N; ++i) {
            xy01 = Inlines.MAC16_16(xy01, x[x_ptr + i], y01[y01_ptr + i]);
            xy02 = Inlines.MAC16_16(xy02, x[x_ptr + i], y02[y02_ptr + i]);
        }
        xy1.Val = xy01;
        xy2.Val = xy02;
    }
}
