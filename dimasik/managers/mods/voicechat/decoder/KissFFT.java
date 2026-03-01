package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.FFTState;
import dimasik.managers.mods.voicechat.decoder.Inlines;

class KissFFT {
    static final int MAXFACTORS = 8;

    KissFFT() {
    }

    static int S_MUL(int a, int b) {
        return Inlines.MULT16_32_Q15(b, a);
    }

    static int S_MUL(int a, short b) {
        return Inlines.MULT16_32_Q15(b, a);
    }

    static int HALF_OF(int x) {
        return x >> 1;
    }

    static void kf_bfly2(int[] Fout, int fout_ptr, int m, int N) {
        short tw = 23170;
        Inlines.OpusAssert(m == 4);
        for (int i = 0; i < N; ++i) {
            int Fout2 = fout_ptr + 8;
            int t_r = Fout[Fout2 + 0];
            int t_i = Fout[Fout2 + 1];
            Fout[Fout2 + 0] = Fout[fout_ptr + 0] - t_r;
            Fout[Fout2 + 1] = Fout[fout_ptr + 1] - t_i;
            int n = fout_ptr + 0;
            Fout[n] = Fout[n] + t_r;
            int n2 = fout_ptr + 1;
            Fout[n2] = Fout[n2] + t_i;
            t_r = KissFFT.S_MUL(Fout[Fout2 + 2] + Fout[Fout2 + 3], tw);
            t_i = KissFFT.S_MUL(Fout[Fout2 + 3] - Fout[Fout2 + 2], tw);
            Fout[Fout2 + 2] = Fout[fout_ptr + 2] - t_r;
            Fout[Fout2 + 3] = Fout[fout_ptr + 3] - t_i;
            int n3 = fout_ptr + 2;
            Fout[n3] = Fout[n3] + t_r;
            int n4 = fout_ptr + 3;
            Fout[n4] = Fout[n4] + t_i;
            t_r = Fout[Fout2 + 5];
            t_i = 0 - Fout[Fout2 + 4];
            Fout[Fout2 + 4] = Fout[fout_ptr + 4] - t_r;
            Fout[Fout2 + 5] = Fout[fout_ptr + 5] - t_i;
            int n5 = fout_ptr + 4;
            Fout[n5] = Fout[n5] + t_r;
            int n6 = fout_ptr + 5;
            Fout[n6] = Fout[n6] + t_i;
            t_r = KissFFT.S_MUL(Fout[Fout2 + 7] - Fout[Fout2 + 6], tw);
            t_i = KissFFT.S_MUL(0 - Fout[Fout2 + 7] - Fout[Fout2 + 6], tw);
            Fout[Fout2 + 6] = Fout[fout_ptr + 6] - t_r;
            Fout[Fout2 + 7] = Fout[fout_ptr + 7] - t_i;
            int n7 = fout_ptr + 6;
            Fout[n7] = Fout[n7] + t_r;
            int n8 = fout_ptr + 7;
            Fout[n8] = Fout[n8] + t_i;
            fout_ptr += 16;
        }
    }

    static void kf_bfly4(int[] Fout, int fout_ptr, int fstride, FFTState st, int m, int N, int mm) {
        if (m == 1) {
            for (int i = 0; i < N; ++i) {
                int scratch0 = Fout[fout_ptr + 0] - Fout[fout_ptr + 4];
                int scratch1 = Fout[fout_ptr + 1] - Fout[fout_ptr + 5];
                int n = fout_ptr + 0;
                Fout[n] = Fout[n] + Fout[fout_ptr + 4];
                int n2 = fout_ptr + 1;
                Fout[n2] = Fout[n2] + Fout[fout_ptr + 5];
                int scratch2 = Fout[fout_ptr + 2] + Fout[fout_ptr + 6];
                int scratch3 = Fout[fout_ptr + 3] + Fout[fout_ptr + 7];
                Fout[fout_ptr + 4] = Fout[fout_ptr + 0] - scratch2;
                Fout[fout_ptr + 5] = Fout[fout_ptr + 1] - scratch3;
                int n3 = fout_ptr + 0;
                Fout[n3] = Fout[n3] + scratch2;
                int n4 = fout_ptr + 1;
                Fout[n4] = Fout[n4] + scratch3;
                scratch2 = Fout[fout_ptr + 2] - Fout[fout_ptr + 6];
                scratch3 = Fout[fout_ptr + 3] - Fout[fout_ptr + 7];
                Fout[fout_ptr + 2] = scratch0 + scratch3;
                Fout[fout_ptr + 3] = scratch1 - scratch2;
                Fout[fout_ptr + 6] = scratch0 - scratch3;
                Fout[fout_ptr + 7] = scratch1 + scratch2;
                fout_ptr += 8;
            }
        } else {
            int Fout_beg = fout_ptr;
            for (int i = 0; i < N; ++i) {
                fout_ptr = Fout_beg + 2 * i * mm;
                int m1 = fout_ptr + 2 * m;
                int m2 = fout_ptr + 4 * m;
                int m3 = fout_ptr + 6 * m;
                int tw1 = 0;
                int tw2 = 0;
                int tw3 = 0;
                for (int j = 0; j < m; ++j) {
                    int scratch0 = KissFFT.S_MUL(Fout[m1], st.twiddles[tw1]) - KissFFT.S_MUL(Fout[m1 + 1], st.twiddles[tw1 + 1]);
                    int scratch1 = KissFFT.S_MUL(Fout[m1], st.twiddles[tw1 + 1]) + KissFFT.S_MUL(Fout[m1 + 1], st.twiddles[tw1]);
                    int scratch2 = KissFFT.S_MUL(Fout[m2], st.twiddles[tw2]) - KissFFT.S_MUL(Fout[m2 + 1], st.twiddles[tw2 + 1]);
                    int scratch3 = KissFFT.S_MUL(Fout[m2], st.twiddles[tw2 + 1]) + KissFFT.S_MUL(Fout[m2 + 1], st.twiddles[tw2]);
                    int scratch4 = KissFFT.S_MUL(Fout[m3], st.twiddles[tw3]) - KissFFT.S_MUL(Fout[m3 + 1], st.twiddles[tw3 + 1]);
                    int scratch5 = KissFFT.S_MUL(Fout[m3], st.twiddles[tw3 + 1]) + KissFFT.S_MUL(Fout[m3 + 1], st.twiddles[tw3]);
                    int scratch10 = Fout[fout_ptr] - scratch2;
                    int scratch11 = Fout[fout_ptr + 1] - scratch3;
                    int n = fout_ptr;
                    Fout[n] = Fout[n] + scratch2;
                    int n5 = fout_ptr + 1;
                    Fout[n5] = Fout[n5] + scratch3;
                    int scratch6 = scratch0 + scratch4;
                    int scratch7 = scratch1 + scratch5;
                    int scratch8 = scratch0 - scratch4;
                    int scratch9 = scratch1 - scratch5;
                    Fout[m2] = Fout[fout_ptr] - scratch6;
                    Fout[m2 + 1] = Fout[fout_ptr + 1] - scratch7;
                    tw1 += fstride * 2;
                    tw2 += fstride * 4;
                    tw3 += fstride * 6;
                    int n6 = fout_ptr;
                    Fout[n6] = Fout[n6] + scratch6;
                    int n7 = fout_ptr + 1;
                    Fout[n7] = Fout[n7] + scratch7;
                    Fout[m1] = scratch10 + scratch9;
                    Fout[m1 + 1] = scratch11 - scratch8;
                    Fout[m3] = scratch10 - scratch9;
                    Fout[m3 + 1] = scratch11 + scratch8;
                    fout_ptr += 2;
                    m1 += 2;
                    m2 += 2;
                    m3 += 2;
                }
            }
        }
    }

    static void kf_bfly3(int[] Fout, int fout_ptr, int fstride, FFTState st, int m, int N, int mm) {
        int m1 = 2 * m;
        int m2 = 4 * m;
        int Fout_beg = fout_ptr;
        for (int i = 0; i < N; ++i) {
            fout_ptr = Fout_beg + 2 * i * mm;
            int tw2 = 0;
            int tw1 = 0;
            int k = m;
            do {
                int scratch2 = KissFFT.S_MUL(Fout[fout_ptr + m1], st.twiddles[tw1]) - KissFFT.S_MUL(Fout[fout_ptr + m1 + 1], st.twiddles[tw1 + 1]);
                int scratch3 = KissFFT.S_MUL(Fout[fout_ptr + m1], st.twiddles[tw1 + 1]) + KissFFT.S_MUL(Fout[fout_ptr + m1 + 1], st.twiddles[tw1]);
                int scratch4 = KissFFT.S_MUL(Fout[fout_ptr + m2], st.twiddles[tw2]) - KissFFT.S_MUL(Fout[fout_ptr + m2 + 1], st.twiddles[tw2 + 1]);
                int scratch5 = KissFFT.S_MUL(Fout[fout_ptr + m2], st.twiddles[tw2 + 1]) + KissFFT.S_MUL(Fout[fout_ptr + m2 + 1], st.twiddles[tw2]);
                int scratch6 = scratch2 + scratch4;
                int scratch7 = scratch3 + scratch5;
                int scratch0 = scratch2 - scratch4;
                int scratch1 = scratch3 - scratch5;
                tw1 += fstride * 2;
                tw2 += fstride * 4;
                Fout[fout_ptr + m1] = Fout[fout_ptr + 0] - KissFFT.HALF_OF(scratch6);
                Fout[fout_ptr + m1 + 1] = Fout[fout_ptr + 1] - KissFFT.HALF_OF(scratch7);
                scratch0 = KissFFT.S_MUL(scratch0, -28378);
                scratch1 = KissFFT.S_MUL(scratch1, -28378);
                int n = fout_ptr + 0;
                Fout[n] = Fout[n] + scratch6;
                int n2 = fout_ptr + 1;
                Fout[n2] = Fout[n2] + scratch7;
                Fout[fout_ptr + m2] = Fout[fout_ptr + m1] + scratch1;
                Fout[fout_ptr + m2 + 1] = Fout[fout_ptr + m1 + 1] - scratch0;
                int n3 = fout_ptr + m1;
                Fout[n3] = Fout[n3] - scratch1;
                int n4 = fout_ptr + m1 + 1;
                Fout[n4] = Fout[n4] + scratch0;
                fout_ptr += 2;
            } while (--k != 0);
        }
    }

    static void kf_bfly5(int[] Fout, int fout_ptr, int fstride, FFTState st, int m, int N, int mm) {
        int Fout_beg = fout_ptr;
        short ya_r = 10126;
        short ya_i = -31164;
        short yb_r = -26510;
        short yb_i = -19261;
        for (int i = 0; i < N; ++i) {
            int tw4 = 0;
            int tw3 = 0;
            int tw2 = 0;
            int tw1 = 0;
            int Fout0 = fout_ptr = Fout_beg + 2 * i * mm;
            int Fout1 = fout_ptr + 2 * m;
            int Fout2 = fout_ptr + 4 * m;
            int Fout3 = fout_ptr + 6 * m;
            int Fout4 = fout_ptr + 8 * m;
            for (int u = 0; u < m; ++u) {
                int scratch0 = Fout[Fout0 + 0];
                int scratch1 = Fout[Fout0 + 1];
                int scratch2 = KissFFT.S_MUL(Fout[Fout1 + 0], st.twiddles[tw1]) - KissFFT.S_MUL(Fout[Fout1 + 1], st.twiddles[tw1 + 1]);
                int scratch3 = KissFFT.S_MUL(Fout[Fout1 + 0], st.twiddles[tw1 + 1]) + KissFFT.S_MUL(Fout[Fout1 + 1], st.twiddles[tw1]);
                int scratch4 = KissFFT.S_MUL(Fout[Fout2 + 0], st.twiddles[tw2]) - KissFFT.S_MUL(Fout[Fout2 + 1], st.twiddles[tw2 + 1]);
                int scratch5 = KissFFT.S_MUL(Fout[Fout2 + 0], st.twiddles[tw2 + 1]) + KissFFT.S_MUL(Fout[Fout2 + 1], st.twiddles[tw2]);
                int scratch6 = KissFFT.S_MUL(Fout[Fout3 + 0], st.twiddles[tw3]) - KissFFT.S_MUL(Fout[Fout3 + 1], st.twiddles[tw3 + 1]);
                int scratch7 = KissFFT.S_MUL(Fout[Fout3 + 0], st.twiddles[tw3 + 1]) + KissFFT.S_MUL(Fout[Fout3 + 1], st.twiddles[tw3]);
                int scratch8 = KissFFT.S_MUL(Fout[Fout4 + 0], st.twiddles[tw4]) - KissFFT.S_MUL(Fout[Fout4 + 1], st.twiddles[tw4 + 1]);
                int scratch9 = KissFFT.S_MUL(Fout[Fout4 + 0], st.twiddles[tw4 + 1]) + KissFFT.S_MUL(Fout[Fout4 + 1], st.twiddles[tw4]);
                tw1 += 2 * fstride;
                tw2 += 4 * fstride;
                tw3 += 6 * fstride;
                tw4 += 8 * fstride;
                int scratch14 = scratch2 + scratch8;
                int scratch15 = scratch3 + scratch9;
                int scratch20 = scratch2 - scratch8;
                int scratch21 = scratch3 - scratch9;
                int scratch16 = scratch4 + scratch6;
                int scratch17 = scratch5 + scratch7;
                int scratch18 = scratch4 - scratch6;
                int scratch19 = scratch5 - scratch7;
                int n = Fout0 + 0;
                Fout[n] = Fout[n] + (scratch14 + scratch16);
                int n2 = Fout0 + 1;
                Fout[n2] = Fout[n2] + (scratch15 + scratch17);
                int scratch10 = scratch0 + KissFFT.S_MUL(scratch14, ya_r) + KissFFT.S_MUL(scratch16, yb_r);
                int scratch11 = scratch1 + KissFFT.S_MUL(scratch15, ya_r) + KissFFT.S_MUL(scratch17, yb_r);
                int scratch12 = KissFFT.S_MUL(scratch21, ya_i) + KissFFT.S_MUL(scratch19, yb_i);
                int scratch13 = 0 - KissFFT.S_MUL(scratch20, ya_i) - KissFFT.S_MUL(scratch18, yb_i);
                Fout[Fout1 + 0] = scratch10 - scratch12;
                Fout[Fout1 + 1] = scratch11 - scratch13;
                Fout[Fout4 + 0] = scratch10 + scratch12;
                Fout[Fout4 + 1] = scratch11 + scratch13;
                int scratch22 = scratch0 + KissFFT.S_MUL(scratch14, yb_r) + KissFFT.S_MUL(scratch16, ya_r);
                int scratch23 = scratch1 + KissFFT.S_MUL(scratch15, yb_r) + KissFFT.S_MUL(scratch17, ya_r);
                int scratch24 = 0 - KissFFT.S_MUL(scratch21, yb_i) + KissFFT.S_MUL(scratch19, ya_i);
                int scratch25 = KissFFT.S_MUL(scratch20, yb_i) - KissFFT.S_MUL(scratch18, ya_i);
                Fout[Fout2 + 0] = scratch22 + scratch24;
                Fout[Fout2 + 1] = scratch23 + scratch25;
                Fout[Fout3 + 0] = scratch22 - scratch24;
                Fout[Fout3 + 1] = scratch23 - scratch25;
                Fout0 += 2;
                Fout1 += 2;
                Fout2 += 2;
                Fout3 += 2;
                Fout4 += 2;
            }
        }
    }

    static void opus_fft_impl(FFTState st, int[] fout, int fout_ptr) {
        short m;
        int[] fstride = new int[8];
        int shift = st.shift > 0 ? st.shift : 0;
        fstride[0] = 1;
        int L = 0;
        do {
            short p = st.factors[2 * L];
            m = st.factors[2 * L + 1];
            fstride[L + 1] = fstride[L] * p;
            ++L;
        } while (m != 1);
        m = st.factors[2 * L - 1];
        for (int i = L - 1; i >= 0; --i) {
            short m2 = i != 0 ? st.factors[2 * i - 1] : (short)1;
            switch (st.factors[2 * i]) {
                case 2: {
                    KissFFT.kf_bfly2(fout, fout_ptr, m, fstride[i]);
                    break;
                }
                case 4: {
                    KissFFT.kf_bfly4(fout, fout_ptr, fstride[i] << shift, st, m, fstride[i], m2);
                    break;
                }
                case 3: {
                    KissFFT.kf_bfly3(fout, fout_ptr, fstride[i] << shift, st, m, fstride[i], m2);
                    break;
                }
                case 5: {
                    KissFFT.kf_bfly5(fout, fout_ptr, fstride[i] << shift, st, m, fstride[i], m2);
                }
            }
            m = m2;
        }
    }

    static void opus_fft(FFTState st, int[] fin, int[] fout) {
        int scale_shift = st.scale_shift - 1;
        short scale = st.scale;
        Inlines.OpusAssert(fin != fout, "In-place FFT not supported");
        for (int i = 0; i < st.nfft; ++i) {
            fout[2 * st.bitrev[i]] = Inlines.SHR32(Inlines.MULT16_32_Q16(scale, fin[2 * i]), scale_shift);
            fout[2 * st.bitrev[i] + 1] = Inlines.SHR32(Inlines.MULT16_32_Q16(scale, fin[2 * i + 1]), scale_shift);
        }
        KissFFT.opus_fft_impl(st, fout, 0);
    }
}
