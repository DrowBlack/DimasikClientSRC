package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.FFTState;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.KissFFT;
import dimasik.managers.mods.voicechat.decoder.MDCTLookup;

class MDCT {
    MDCT() {
    }

    static void clt_mdct_forward(MDCTLookup l, int[] input, int input_ptr, int[] output, int output_ptr, int[] window, int overlap, int shift, int stride) {
        int i;
        FFTState st = l.kfft[shift];
        int trig_ptr = 0;
        int scale_shift = st.scale_shift - 1;
        short scale = st.scale;
        int N = l.n;
        short[] trig = l.trig;
        for (i = 0; i < shift; ++i) {
            trig_ptr += (N >>= 1);
        }
        int N2 = N >> 1;
        int N4 = N >> 2;
        int[] f = new int[N2];
        int[] f2 = new int[N4 * 2];
        int xp1 = input_ptr + (overlap >> 1);
        int xp2 = input_ptr + N2 - 1 + (overlap >> 1);
        int yp = 0;
        int wp1 = overlap >> 1;
        int wp2 = (overlap >> 1) - 1;
        for (i = 0; i < overlap + 3 >> 2; ++i) {
            f[yp++] = Inlines.MULT16_32_Q15(window[wp2], input[xp1 + N2]) + Inlines.MULT16_32_Q15(window[wp1], input[xp2]);
            f[yp++] = Inlines.MULT16_32_Q15(window[wp1], input[xp1]) - Inlines.MULT16_32_Q15(window[wp2], input[xp2 - N2]);
            xp1 += 2;
            xp2 -= 2;
            wp1 += 2;
            wp2 -= 2;
        }
        wp1 = 0;
        wp2 = overlap - 1;
        while (i < N4 - (overlap + 3 >> 2)) {
            f[yp++] = input[xp2];
            f[yp++] = input[xp1];
            xp1 += 2;
            xp2 -= 2;
            ++i;
        }
        while (i < N4) {
            f[yp++] = Inlines.MULT16_32_Q15(window[wp2], input[xp2]) - Inlines.MULT16_32_Q15(window[wp1], input[xp1 - N2]);
            f[yp++] = Inlines.MULT16_32_Q15(window[wp2], input[xp1]) + Inlines.MULT16_32_Q15(window[wp1], input[xp2 + N2]);
            xp1 += 2;
            xp2 -= 2;
            wp1 += 2;
            wp2 -= 2;
            ++i;
        }
        int yp2 = 0;
        int t = trig_ptr;
        for (i = 0; i < N4; ++i) {
            short t0 = trig[t + i];
            short t1 = trig[t + N4 + i];
            int re = f[yp2++];
            int im = f[yp2++];
            int yr = KissFFT.S_MUL(re, t0) - KissFFT.S_MUL(im, t1);
            int yi = KissFFT.S_MUL(im, t0) + KissFFT.S_MUL(re, t1);
            f2[2 * st.bitrev[i]] = Inlines.PSHR32(Inlines.MULT16_32_Q16((int)scale, yr), scale_shift);
            f2[2 * st.bitrev[i] + 1] = Inlines.PSHR32(Inlines.MULT16_32_Q16((int)scale, yi), scale_shift);
        }
        KissFFT.opus_fft_impl(st, f2, 0);
        int fp = 0;
        int yp1 = output_ptr;
        int yp22 = output_ptr + stride * (N2 - 1);
        int t2 = trig_ptr;
        for (i = 0; i < N4; ++i) {
            int yr = KissFFT.S_MUL(f2[fp + 1], trig[t2 + N4 + i]) - KissFFT.S_MUL(f2[fp], trig[t2 + i]);
            int yi = KissFFT.S_MUL(f2[fp], trig[t2 + N4 + i]) + KissFFT.S_MUL(f2[fp + 1], trig[t2 + i]);
            output[yp1] = yr;
            output[yp22] = yi;
            fp += 2;
            yp1 += 2 * stride;
            yp22 -= 2 * stride;
        }
    }

    static void clt_mdct_backward(MDCTLookup l, int[] input, int input_ptr, int[] output, int output_ptr, int[] window, int overlap, int shift, int stride) {
        int i;
        int trig = 0;
        int N = l.n;
        for (i = 0; i < shift; ++i) {
            trig += (N >>= 1);
        }
        int N2 = N >> 1;
        int N4 = N >> 2;
        int xp2 = input_ptr + stride * (N2 - 1);
        int yp = output_ptr + (overlap >> 1);
        short[] bitrev = l.kfft[shift].bitrev;
        int bitrav_ptr = 0;
        for (i = 0; i < N4; ++i) {
            short rev = bitrev[bitrav_ptr++];
            int ypr = yp + 2 * rev;
            output[ypr + 1] = KissFFT.S_MUL(input[xp2], l.trig[trig + i]) + KissFFT.S_MUL(input[input_ptr], l.trig[trig + N4 + i]);
            output[ypr] = KissFFT.S_MUL(input[input_ptr], l.trig[trig + i]) - KissFFT.S_MUL(input[xp2], l.trig[trig + N4 + i]);
            input_ptr += 2 * stride;
            xp2 -= 2 * stride;
        }
        KissFFT.opus_fft_impl(l.kfft[shift], output, output_ptr + (overlap >> 1));
        int yp0 = output_ptr + (overlap >> 1);
        int yp1 = output_ptr + (overlap >> 1) + N2 - 2;
        int t = trig;
        int tN4m1 = t + N4 - 1;
        int tN2m1 = t + N2 - 1;
        for (i = 0; i < N4 + 1 >> 1; ++i) {
            int re = output[yp0 + 1];
            int im = output[yp0];
            short t0 = l.trig[t + i];
            short t1 = l.trig[t + N4 + i];
            int yr = KissFFT.S_MUL(re, t0) + KissFFT.S_MUL(im, t1);
            int yi = KissFFT.S_MUL(re, t1) - KissFFT.S_MUL(im, t0);
            re = output[yp1 + 1];
            im = output[yp1];
            output[yp0] = yr;
            output[yp1 + 1] = yi;
            t0 = l.trig[tN4m1 - i];
            t1 = l.trig[tN2m1 - i];
            yr = KissFFT.S_MUL(re, t0) + KissFFT.S_MUL(im, t1);
            yi = KissFFT.S_MUL(re, t1) - KissFFT.S_MUL(im, t0);
            output[yp1] = yr;
            output[yp0 + 1] = yi;
            yp0 += 2;
            yp1 -= 2;
        }
        int xp1 = output_ptr + overlap - 1;
        yp1 = output_ptr;
        int wp1 = 0;
        int wp2 = overlap - 1;
        for (i = 0; i < overlap / 2; ++i) {
            int x1 = output[xp1];
            int x2 = output[yp1];
            output[yp1++] = Inlines.MULT16_32_Q15(window[wp2], x2) - Inlines.MULT16_32_Q15(window[wp1], x1);
            output[xp1--] = Inlines.MULT16_32_Q15(window[wp1], x2) + Inlines.MULT16_32_Q15(window[wp2], x1);
            ++wp1;
            --wp2;
        }
    }
}
