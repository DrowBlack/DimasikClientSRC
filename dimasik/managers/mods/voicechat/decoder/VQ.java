package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.CWRS;
import dimasik.managers.mods.voicechat.decoder.EntropyCoder;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.Kernels;

class VQ {
    private static int[] SPREAD_FACTOR = new int[]{15, 10, 5};

    VQ() {
    }

    static void exp_rotation1(int[] X, int X_ptr, int len, int stride, int c, int s) {
        int x2;
        int x1;
        int i;
        int Xptr = X_ptr;
        int ms = Inlines.NEG16(s);
        for (i = 0; i < len - stride; ++i) {
            x1 = X[Xptr];
            x2 = X[Xptr + stride];
            X[Xptr + stride] = Inlines.EXTRACT16(Inlines.PSHR32(Inlines.MAC16_16(Inlines.MULT16_16(c, x2), s, x1), 15));
            X[Xptr] = Inlines.EXTRACT16(Inlines.PSHR32(Inlines.MAC16_16(Inlines.MULT16_16(c, x1), ms, x2), 15));
            ++Xptr;
        }
        Xptr = X_ptr + (len - 2 * stride - 1);
        for (i = len - 2 * stride - 1; i >= 0; --i) {
            x1 = X[Xptr];
            x2 = X[Xptr + stride];
            X[Xptr + stride] = Inlines.EXTRACT16(Inlines.PSHR32(Inlines.MAC16_16(Inlines.MULT16_16(c, x2), s, x1), 15));
            X[Xptr] = Inlines.EXTRACT16(Inlines.PSHR32(Inlines.MAC16_16(Inlines.MULT16_16(c, x1), ms, x2), 15));
            --Xptr;
        }
    }

    static void exp_rotation(int[] X, int X_ptr, int len, int dir, int stride, int K, int spread) {
        int stride2 = 0;
        if (2 * K >= len || spread == 0) {
            return;
        }
        int factor = SPREAD_FACTOR[spread - 1];
        int gain = Inlines.celt_div(Inlines.MULT16_16(Short.MAX_VALUE, len), len + factor * K);
        int theta = Inlines.HALF16(Inlines.MULT16_16_Q15(gain, gain));
        int c = Inlines.celt_cos_norm(Inlines.EXTEND32(theta));
        int s = Inlines.celt_cos_norm(Inlines.EXTEND32(Inlines.SUB16(Short.MAX_VALUE, theta)));
        if (len >= 8 * stride) {
            stride2 = 1;
            while ((stride2 * stride2 + stride2) * stride + (stride >> 2) < len) {
                ++stride2;
            }
        }
        len = Inlines.celt_udiv(len, stride);
        for (int i = 0; i < stride; ++i) {
            if (dir < 0) {
                if (stride2 != 0) {
                    VQ.exp_rotation1(X, X_ptr + i * len, len, stride2, s, c);
                }
                VQ.exp_rotation1(X, X_ptr + i * len, len, 1, c, s);
                continue;
            }
            VQ.exp_rotation1(X, X_ptr + i * len, len, 1, c, (short)(0 - s));
            if (stride2 == 0) continue;
            VQ.exp_rotation1(X, X_ptr + i * len, len, stride2, s, (short)(0 - c));
        }
    }

    static void normalise_residual(int[] iy, int[] X, int X_ptr, int N, int Ryy, int gain) {
        int k = Inlines.celt_ilog2(Ryy) >> 1;
        int t = Inlines.VSHR32(Ryy, 2 * (k - 7));
        int g = Inlines.MULT16_16_P15(Inlines.celt_rsqrt_norm(t), gain);
        int i = 0;
        do {
            X[X_ptr + i] = Inlines.EXTRACT16(Inlines.PSHR32(Inlines.MULT16_16(g, iy[i]), k + 1));
        } while (++i < N);
    }

    static int extract_collapse_mask(int[] iy, int N, int B) {
        if (B <= 1) {
            return 1;
        }
        int N0 = Inlines.celt_udiv(N, B);
        int collapse_mask = 0;
        int i = 0;
        do {
            int tmp = 0;
            int j = 0;
            do {
                tmp |= iy[i * N0 + j];
            } while (++j < N0);
            collapse_mask |= (tmp != 0 ? 1 : 0) << i;
        } while (++i < B);
        return collapse_mask;
    }

    static int alg_quant(int[] X, int X_ptr, int N, int K, int spread, int B, EntropyCoder enc) {
        int[] y = new int[N];
        int[] iy = new int[N];
        int[] signx = new int[N];
        Inlines.OpusAssert(K > 0, "alg_quant() needs at least one pulse");
        Inlines.OpusAssert(N > 1, "alg_quant() needs at least two dimensions");
        VQ.exp_rotation(X, X_ptr, N, 1, B, K, spread);
        int sum = 0;
        int j = 0;
        do {
            if (X[X_ptr + j] > 0) {
                signx[j] = 1;
            } else {
                signx[j] = -1;
                X[X_ptr + j] = 0 - X[X_ptr + j];
            }
            iy[j] = 0;
            y[j] = 0;
        } while (++j < N);
        int yy = 0;
        int xy = 0;
        int pulsesLeft = K;
        if (K > N >> 1) {
            j = 0;
            do {
                sum += X[X_ptr + j];
            } while (++j < N);
            if (sum <= K) {
                X[X_ptr] = 16384;
                j = X_ptr + 1;
                do {
                    X[j] = 0;
                } while (++j < N + X_ptr);
                sum = 16384;
            }
            short rcp = Inlines.EXTRACT16(Inlines.MULT16_32_Q16(K - 1, Inlines.celt_rcp(sum)));
            j = 0;
            do {
                iy[j] = Inlines.MULT16_16_Q15(X[X_ptr + j], (int)rcp);
                y[j] = iy[j];
                yy = Inlines.MAC16_16(yy, y[j], y[j]);
                xy = Inlines.MAC16_16(xy, X[X_ptr + j], y[j]);
                int n = j;
                y[n] = y[n] * 2;
                pulsesLeft -= iy[j];
            } while (++j < N);
        }
        Inlines.OpusAssert(pulsesLeft >= 1, "Allocated too many pulses in the quick pass");
        if (pulsesLeft > N + 3) {
            int tmp = pulsesLeft;
            yy = Inlines.MAC16_16(yy, tmp, tmp);
            yy = Inlines.MAC16_16(yy, tmp, y[0]);
            iy[0] = iy[0] + pulsesLeft;
            pulsesLeft = 0;
        }
        int s = 1;
        for (int i = 0; i < pulsesLeft; ++i) {
            int best_num = -32767;
            int best_den = 0;
            int rshift = 1 + Inlines.celt_ilog2(K - pulsesLeft + i + 1);
            int best_id = 0;
            yy = Inlines.ADD16(yy, 1);
            j = 0;
            do {
                int Rxy = Inlines.EXTRACT16(Inlines.SHR32(Inlines.ADD32(xy, Inlines.EXTEND32(X[X_ptr + j])), rshift));
                int Ryy = Inlines.ADD16(yy, y[j]);
                if (Inlines.MULT16_16(best_den, Rxy = Inlines.MULT16_16_Q15(Rxy, Rxy)) <= Inlines.MULT16_16(Ryy, best_num)) continue;
                best_den = Ryy;
                best_num = Rxy;
                best_id = j;
            } while (++j < N);
            xy = Inlines.ADD32(xy, Inlines.EXTEND32(X[X_ptr + best_id]));
            yy = Inlines.ADD16(yy, y[best_id]);
            y[best_id] = y[best_id] + 2 * s;
            int n = best_id;
            iy[n] = iy[n] + 1;
        }
        j = 0;
        do {
            X[X_ptr + j] = Inlines.MULT16_16(signx[j], X[X_ptr + j]);
            if (signx[j] >= 0) continue;
            iy[j] = -iy[j];
        } while (++j < N);
        CWRS.encode_pulses(iy, N, K, enc);
        int collapse_mask = VQ.extract_collapse_mask(iy, N, B);
        return collapse_mask;
    }

    static int alg_unquant(int[] X, int X_ptr, int N, int K, int spread, int B, EntropyCoder dec, int gain) {
        int[] iy = new int[N];
        Inlines.OpusAssert(K > 0, "alg_unquant() needs at least one pulse");
        Inlines.OpusAssert(N > 1, "alg_unquant() needs at least two dimensions");
        int Ryy = CWRS.decode_pulses(iy, N, K, dec);
        VQ.normalise_residual(iy, X, X_ptr, N, Ryy, gain);
        VQ.exp_rotation(X, X_ptr, N, -1, B, K, spread);
        int collapse_mask = VQ.extract_collapse_mask(iy, N, B);
        return collapse_mask;
    }

    static void renormalise_vector(int[] X, int X_ptr, int N, int gain) {
        int E = 1 + Kernels.celt_inner_prod(X, X_ptr, X, X_ptr, N);
        int k = Inlines.celt_ilog2(E) >> 1;
        int t = Inlines.VSHR32(E, 2 * (k - 7));
        int g = Inlines.MULT16_16_P15(Inlines.celt_rsqrt_norm(t), gain);
        int xptr = X_ptr;
        for (int i = 0; i < N; ++i) {
            X[xptr] = Inlines.EXTRACT16(Inlines.PSHR32(Inlines.MULT16_16(g, X[xptr]), k + 1));
            ++xptr;
        }
    }

    static int stereo_itheta(int[] X, int X_ptr, int[] Y, int Y_ptr, int stereo, int N) {
        int Eside = 1;
        int Emid = 1;
        if (stereo != 0) {
            for (int i = 0; i < N; ++i) {
                int m = Inlines.ADD16(Inlines.SHR16(X[X_ptr + i], 1), Inlines.SHR16(Y[Y_ptr + i], 1));
                int s = Inlines.SUB16(Inlines.SHR16(X[X_ptr + i], 1), Inlines.SHR16(Y[Y_ptr + i], 1));
                Emid = Inlines.MAC16_16(Emid, m, m);
                Eside = Inlines.MAC16_16(Eside, s, s);
            }
        } else {
            Emid += Kernels.celt_inner_prod(X, X_ptr, X, X_ptr, N);
            Eside += Kernels.celt_inner_prod(Y, Y_ptr, Y, Y_ptr, N);
        }
        int mid = Inlines.celt_sqrt(Emid);
        int side = Inlines.celt_sqrt(Eside);
        int itheta = Inlines.MULT16_16_Q15(20861, Inlines.celt_atan2p(side, mid));
        return itheta;
    }
}
