package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;
import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.CeltMode;
import dimasik.managers.mods.voicechat.decoder.CeltTables;
import dimasik.managers.mods.voicechat.decoder.EntropyCoder;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.Kernels;
import dimasik.managers.mods.voicechat.decoder.Rate;
import dimasik.managers.mods.voicechat.decoder.VQ;

class Bands {
    private static final byte[] bit_interleave_table = new byte[]{0, 1, 1, 1, 2, 3, 3, 3, 2, 3, 3, 3, 2, 3, 3, 3};
    private static final short[] bit_deinterleave_table = new short[]{0, 3, 12, 15, 48, 51, 60, 63, 192, 195, 204, 207, 240, 243, 252, 255};

    Bands() {
    }

    static int hysteresis_decision(int val, int[] thresholds, int[] hysteresis, int N, int prev) {
        int i;
        for (i = 0; i < N && val >= thresholds[i]; ++i) {
        }
        if (i > prev && val < thresholds[prev] + hysteresis[prev]) {
            i = prev;
        }
        if (i < prev && val > thresholds[prev - 1] - hysteresis[prev - 1]) {
            i = prev;
        }
        return i;
    }

    static int celt_lcg_rand(int seed) {
        return 1664525 * seed + 1013904223;
    }

    static int bitexact_cos(int x) {
        int tmp = 4096 + x * x >> 13;
        Inlines.OpusAssert(tmp <= Short.MAX_VALUE);
        int x2 = tmp;
        x2 = Short.MAX_VALUE - x2 + Inlines.FRAC_MUL16(x2, -7651 + Inlines.FRAC_MUL16(x2, 8277 + Inlines.FRAC_MUL16(-626, x2)));
        Inlines.OpusAssert(x2 <= 32766);
        return 1 + x2;
    }

    static int bitexact_log2tan(int isin, int icos) {
        int lc = Inlines.EC_ILOG(icos);
        int ls = Inlines.EC_ILOG(isin);
        return (ls - lc) * 2048 + Inlines.FRAC_MUL16(isin <<= 15 - ls, Inlines.FRAC_MUL16(isin, -2597) + 7932) - Inlines.FRAC_MUL16(icos <<= 15 - lc, Inlines.FRAC_MUL16(icos, -2597) + 7932);
    }

    static void compute_band_energies(CeltMode m, int[][] X, int[][] bandE, int end, int C, int LM) {
        short[] eBands = m.eBands;
        int N = m.shortMdctSize << LM;
        int c = 0;
        do {
            for (int i = 0; i < end; ++i) {
                int maxval = 0;
                int sum = 0;
                maxval = Inlines.celt_maxabs32(X[c], eBands[i] << LM, eBands[i + 1] - eBands[i] << LM);
                if (maxval > 0) {
                    int shift = Inlines.celt_ilog2(maxval) - 14 + ((m.logN[i] >> 3) + LM + 1 >> 1);
                    int j = eBands[i] << LM;
                    if (shift > 0) {
                        do {
                            sum = Inlines.MAC16_16(sum, Inlines.EXTRACT16(Inlines.SHR32(X[c][j], shift)), Inlines.EXTRACT16(Inlines.SHR32(X[c][j], shift)));
                        } while (++j < eBands[i + 1] << LM);
                    } else {
                        do {
                            sum = Inlines.MAC16_16(sum, Inlines.EXTRACT16(Inlines.SHL32(X[c][j], -shift)), Inlines.EXTRACT16(Inlines.SHL32(X[c][j], -shift)));
                        } while (++j < eBands[i + 1] << LM);
                    }
                    bandE[c][i] = 1 + Inlines.VSHR32(Inlines.celt_sqrt(sum), -shift);
                    continue;
                }
                bandE[c][i] = 1;
            }
        } while (++c < C);
    }

    static void normalise_bands(CeltMode m, int[][] freq, int[][] X, int[][] bandE, int end, int C, int M) {
        short[] eBands = m.eBands;
        int c = 0;
        do {
            int i = 0;
            do {
                int shift = Inlines.celt_zlog2(bandE[c][i]) - 13;
                int E = Inlines.VSHR32(bandE[c][i], shift);
                short g = Inlines.EXTRACT16(Inlines.celt_rcp(Inlines.SHL32(E, 3)));
                int j = M * eBands[i];
                do {
                    X[c][j] = Inlines.MULT16_16_Q15(Inlines.VSHR32(freq[c][j], shift - 1), (int)g);
                } while (++j < M * eBands[i + 1]);
            } while (++i < end);
        } while (++c < C);
    }

    static void denormalise_bands(CeltMode m, int[] X, int[] freq, int freq_ptr, int[] bandLogE, int bandLogE_ptr, int start, int end, int M, int downsample, int silence) {
        int i;
        short[] eBands = m.eBands;
        int N = M * m.shortMdctSize;
        int bound = M * eBands[end];
        if (downsample != 1) {
            bound = Inlines.IMIN(bound, N / downsample);
        }
        if (silence != 0) {
            bound = 0;
            end = 0;
            start = 0;
        }
        int f = freq_ptr;
        int x = M * eBands[start];
        for (i = 0; i < M * eBands[start]; ++i) {
            freq[f++] = 0;
        }
        for (i = start; i < end; ++i) {
            int g;
            int j = M * eBands[i];
            int band_end = M * eBands[i + 1];
            int lg = Inlines.ADD16(bandLogE[bandLogE_ptr + i], (int)Inlines.SHL16(CeltTables.eMeans[i], 6));
            int shift = 16 - (lg >> 10);
            if (shift > 31) {
                shift = 0;
                g = 0;
            } else {
                g = Inlines.celt_exp2_frac(lg & 0x3FF);
            }
            if (shift < 0) {
                if (shift < -2) {
                    g = Short.MAX_VALUE;
                    shift = -2;
                }
                do {
                    freq[f] = Inlines.SHR32(Inlines.MULT16_16(X[x], g), -shift);
                } while (++j < band_end);
                continue;
            }
            do {
                freq[f++] = Inlines.SHR32(Inlines.MULT16_16(X[x++], g), shift);
            } while (++j < band_end);
        }
        Inlines.OpusAssert(start <= end);
        Arrays.MemSetWithOffset(freq, 0, freq_ptr + bound, N - bound);
    }

    static void anti_collapse(CeltMode m, int[][] X_, short[] collapse_masks, int LM, int C, int size, int start, int end, int[] logE, int[] prev1logE, int[] prev2logE, int[] pulses, int seed) {
        for (int i = start; i < end; ++i) {
            int N0 = m.eBands[i + 1] - m.eBands[i];
            Inlines.OpusAssert(pulses[i] >= 0);
            int depth = Inlines.celt_udiv(1 + pulses[i], m.eBands[i + 1] - m.eBands[i]) >> LM;
            int thresh32 = Inlines.SHR32(Inlines.celt_exp2(0 - Inlines.SHL16(depth, 7)), 1);
            int thresh = Inlines.MULT16_32_Q15((short)16384, Inlines.MIN32(Short.MAX_VALUE, thresh32));
            int t = N0 << LM;
            int shift = Inlines.celt_ilog2(t) >> 1;
            t = Inlines.SHL32(t, 7 - shift << 1);
            int sqrt_1 = Inlines.celt_rsqrt_norm(t);
            int c = 0;
            do {
                int r;
                boolean renormalize = false;
                int prev1 = prev1logE[c * m.nbEBands + i];
                int prev2 = prev2logE[c * m.nbEBands + i];
                if (C == 1) {
                    prev1 = Inlines.MAX16(prev1, prev1logE[m.nbEBands + i]);
                    prev2 = Inlines.MAX16(prev2, prev2logE[m.nbEBands + i]);
                }
                int Ediff = Inlines.EXTEND32(logE[c * m.nbEBands + i]) - Inlines.EXTEND32(Inlines.MIN16(prev1, prev2));
                if ((Ediff = Inlines.MAX32(0, Ediff)) < 16384) {
                    int r32 = Inlines.SHR32(Inlines.celt_exp2((short)(0 - Inlines.EXTRACT16(Ediff))), 1);
                    r = 2 * Inlines.MIN16(16383, r32);
                } else {
                    r = 0;
                }
                if (LM == 3) {
                    r = Inlines.MULT16_16_Q14(23170, Inlines.MIN32(23169, r));
                }
                r = Inlines.SHR16(Inlines.MIN16(thresh, r), 1);
                r = Inlines.SHR32(Inlines.MULT16_16_Q15(sqrt_1, r), shift);
                int X = m.eBands[i] << LM;
                for (int k = 0; k < 1 << LM; ++k) {
                    if ((collapse_masks[i * C + c] & 1 << k) != 0) continue;
                    int Xk = X + k;
                    for (int j = 0; j < N0; ++j) {
                        seed = Bands.celt_lcg_rand(seed);
                        X_[c][Xk + (j << LM)] = (seed & 0x8000) != 0 ? r : 0 - r;
                    }
                    renormalize = true;
                }
                if (!renormalize) continue;
                VQ.renormalise_vector(X_[c], X, N0 << LM, Short.MAX_VALUE);
            } while (++c < C);
        }
    }

    static void intensity_stereo(CeltMode m, int[] X, int X_ptr, int[] Y, int Y_ptr, int[][] bandE, int bandID, int N) {
        int i = bandID;
        int shift = Inlines.celt_zlog2(Inlines.MAX32(bandE[0][i], bandE[1][i])) - 13;
        int left = Inlines.VSHR32(bandE[0][i], shift);
        int right = Inlines.VSHR32(bandE[1][i], shift);
        int norm = 1 + Inlines.celt_sqrt(1 + Inlines.MULT16_16(left, left) + Inlines.MULT16_16(right, right));
        int a1 = Inlines.DIV32_16(Inlines.SHL32(left, 14), norm);
        int a2 = Inlines.DIV32_16(Inlines.SHL32(right, 14), norm);
        for (int j = 0; j < N; ++j) {
            int l = X[X_ptr + j];
            int r = Y[Y_ptr + j];
            X[X_ptr + j] = Inlines.EXTRACT16(Inlines.SHR32(Inlines.MAC16_16(Inlines.MULT16_16(a1, l), a2, r), 14));
        }
    }

    static void stereo_split(int[] X, int X_ptr, int[] Y, int Y_ptr, int N) {
        for (int j = 0; j < N; ++j) {
            int l = Inlines.MULT16_16(23170, X[X_ptr + j]);
            int r = Inlines.MULT16_16(23170, Y[Y_ptr + j]);
            X[X_ptr + j] = Inlines.EXTRACT16(Inlines.SHR32(Inlines.ADD32(l, r), 15));
            Y[Y_ptr + j] = Inlines.EXTRACT16(Inlines.SHR32(Inlines.SUB32(r, l), 15));
        }
    }

    static void stereo_merge(int[] X, int X_ptr, int[] Y, int Y_ptr, int mid, int N) {
        BoxedValueInt xp = new BoxedValueInt(0);
        BoxedValueInt side = new BoxedValueInt(0);
        Kernels.dual_inner_prod(Y, Y_ptr, X, X_ptr, Y, Y_ptr, N, xp, side);
        xp.Val = Inlines.MULT16_32_Q15(mid, xp.Val);
        int mid2 = Inlines.SHR16(mid, 1);
        int El = Inlines.MULT16_16(mid2, mid2) + side.Val - 2 * xp.Val;
        int Er = Inlines.MULT16_16(mid2, mid2) + side.Val + 2 * xp.Val;
        if (Er < 161061 || El < 161061) {
            System.arraycopy(X, X_ptr, Y, Y_ptr, N);
            return;
        }
        int kl = Inlines.celt_ilog2(El) >> 1;
        int kr = Inlines.celt_ilog2(Er) >> 1;
        int t = Inlines.VSHR32(El, kl - 7 << 1);
        int lgain = Inlines.celt_rsqrt_norm(t);
        t = Inlines.VSHR32(Er, kr - 7 << 1);
        int rgain = Inlines.celt_rsqrt_norm(t);
        if (kl < 7) {
            kl = 7;
        }
        if (kr < 7) {
            kr = 7;
        }
        for (int j = 0; j < N; ++j) {
            int l = Inlines.MULT16_16_P15(mid, X[X_ptr + j]);
            int r = Y[Y_ptr + j];
            X[X_ptr + j] = Inlines.EXTRACT16(Inlines.PSHR32(Inlines.MULT16_16(lgain, Inlines.SUB16(l, r)), kl + 1));
            Y[Y_ptr + j] = Inlines.EXTRACT16(Inlines.PSHR32(Inlines.MULT16_16(rgain, Inlines.ADD16(l, r)), kr + 1));
        }
    }

    static int spreading_decision(CeltMode m, int[][] X, BoxedValueInt average, int last_decision, BoxedValueInt hf_average, BoxedValueInt tapset_decision, int update_hf, int end, int C, int M) {
        int sum = 0;
        int nbBands = 0;
        short[] eBands = m.eBands;
        int hf_sum = 0;
        Inlines.OpusAssert(end > 0);
        if (M * (eBands[end] - eBands[end - 1]) <= 8) {
            return 0;
        }
        int c = 0;
        do {
            for (int i = 0; i < end; ++i) {
                int tmp = 0;
                int[] tcount = new int[]{0, 0, 0};
                int[] x = X[c];
                int x_ptr = M * eBands[i];
                int N = M * (eBands[i + 1] - eBands[i]);
                if (N <= 8) continue;
                for (int j = x_ptr; j < N + x_ptr; ++j) {
                    int x2N = Inlines.MULT16_16(Inlines.MULT16_16_Q15(x[j], x[j]), N);
                    if (x2N < 2048) {
                        tcount[0] = tcount[0] + 1;
                    }
                    if (x2N < 512) {
                        tcount[1] = tcount[1] + 1;
                    }
                    if (x2N >= 128) continue;
                    tcount[2] = tcount[2] + 1;
                }
                if (i > m.nbEBands - 4) {
                    hf_sum += Inlines.celt_udiv(32 * (tcount[1] + tcount[0]), N);
                }
                tmp = (2 * tcount[2] >= N ? 1 : 0) + (2 * tcount[1] >= N ? 1 : 0) + (2 * tcount[0] >= N ? 1 : 0);
                sum += tmp * 256;
                ++nbBands;
            }
        } while (++c < C);
        if (update_hf != 0) {
            if (hf_sum != 0) {
                hf_sum = Inlines.celt_udiv(hf_sum, C * (4 - m.nbEBands + end));
            }
            hf_sum = hf_average.Val = hf_average.Val + hf_sum >> 1;
            if (tapset_decision.Val == 2) {
                hf_sum += 4;
            } else if (tapset_decision.Val == 0) {
                hf_sum -= 4;
            }
            tapset_decision.Val = hf_sum > 22 ? 2 : (hf_sum > 18 ? 1 : 0);
        }
        Inlines.OpusAssert(nbBands > 0);
        Inlines.OpusAssert(sum >= 0);
        sum = Inlines.celt_udiv(sum, nbBands);
        average.Val = sum = sum + average.Val >> 1;
        sum = 3 * sum + ((3 - last_decision << 7) + 64) + 2 >> 2;
        int decision = sum < 80 ? 3 : (sum < 256 ? 2 : (sum < 384 ? 1 : 0));
        return decision;
    }

    static void deinterleave_hadamard(int[] X, int X_ptr, int N0, int stride, int hadamard) {
        int N = N0 * stride;
        int[] tmp = new int[N];
        Inlines.OpusAssert(stride > 0);
        if (hadamard != 0) {
            int ordery = stride - 2;
            for (int i = 0; i < stride; ++i) {
                for (int j = 0; j < N0; ++j) {
                    tmp[CeltTables.ordery_table[ordery + i] * N0 + j] = X[j * stride + i + X_ptr];
                }
            }
        } else {
            for (int i = 0; i < stride; ++i) {
                for (int j = 0; j < N0; ++j) {
                    tmp[i * N0 + j] = X[j * stride + i + X_ptr];
                }
            }
        }
        System.arraycopy(tmp, 0, X, X_ptr, N);
    }

    static void interleave_hadamard(int[] X, int X_ptr, int N0, int stride, int hadamard) {
        int N = N0 * stride;
        int[] tmp = new int[N];
        if (hadamard != 0) {
            int ordery = stride - 2;
            for (int i = 0; i < stride; ++i) {
                for (int j = 0; j < N0; ++j) {
                    tmp[j * stride + i] = X[CeltTables.ordery_table[ordery + i] * N0 + j + X_ptr];
                }
            }
        } else {
            for (int i = 0; i < stride; ++i) {
                for (int j = 0; j < N0; ++j) {
                    tmp[j * stride + i] = X[i * N0 + j + X_ptr];
                }
            }
        }
        System.arraycopy(tmp, 0, X, X_ptr, N);
    }

    static void haar1(int[] X, int X_ptr, int N0, int stride) {
        N0 >>= 1;
        for (int i = 0; i < stride; ++i) {
            for (int j = 0; j < N0; ++j) {
                int tmpidx = X_ptr + i + stride * 2 * j;
                int tmp1 = Inlines.MULT16_16(23170, X[tmpidx]);
                int tmp2 = Inlines.MULT16_16(23170, X[tmpidx + stride]);
                X[tmpidx] = Inlines.EXTRACT16(Inlines.PSHR32(Inlines.ADD32(tmp1, tmp2), 15));
                X[tmpidx + stride] = Inlines.EXTRACT16(Inlines.PSHR32(Inlines.SUB32(tmp1, tmp2), 15));
            }
        }
    }

    static void haar1ZeroOffset(int[] X, int N0, int stride) {
        N0 >>= 1;
        for (int i = 0; i < stride; ++i) {
            for (int j = 0; j < N0; ++j) {
                int tmpidx = i + stride * 2 * j;
                int tmp1 = Inlines.MULT16_16(23170, X[tmpidx]);
                int tmp2 = Inlines.MULT16_16(23170, X[tmpidx + stride]);
                X[tmpidx] = Inlines.EXTRACT16(Inlines.PSHR32(Inlines.ADD32(tmp1, tmp2), 15));
                X[tmpidx + stride] = Inlines.EXTRACT16(Inlines.PSHR32(Inlines.SUB32(tmp1, tmp2), 15));
            }
        }
    }

    static int compute_qn(int N, int b, int offset, int pulse_cap, int stereo) {
        int qn;
        short[] exp2_table8 = new short[]{16384, 17866, 19483, 21247, 23170, 25267, 27554, 30048};
        int N2 = 2 * N - 1;
        if (stereo != 0 && N == 2) {
            --N2;
        }
        int qb = Inlines.celt_sudiv(b + N2 * offset, N2);
        qb = Inlines.IMIN(b - pulse_cap - 32, qb);
        if ((qb = Inlines.IMIN(64, qb)) < 4) {
            qn = 1;
        } else {
            qn = exp2_table8[qb & 7] >> 14 - (qb >> 3);
            qn = qn + 1 >> 1 << 1;
        }
        Inlines.OpusAssert(qn <= 256);
        return qn;
    }

    static void compute_theta(band_ctx ctx, split_ctx sctx, int[] X, int X_ptr, int[] Y, int Y_ptr, int N, BoxedValueInt b, int B, int B0, int LM, int stereo, BoxedValueInt fill) {
        int delta;
        int iside;
        int imid;
        int itheta = 0;
        int inv = 0;
        int encode = ctx.encode;
        CeltMode m = ctx.m;
        int i = ctx.i;
        int intensity = ctx.intensity;
        EntropyCoder ec = ctx.ec;
        int[][] bandE = ctx.bandE;
        int pulse_cap = m.logN[i] + LM * 8;
        int offset = (pulse_cap >> 1) - (stereo != 0 && N == 2 ? 16 : 4);
        int qn = Bands.compute_qn(N, b.Val, offset, pulse_cap, stereo);
        if (stereo != 0 && i >= intensity) {
            qn = 1;
        }
        if (encode != 0) {
            itheta = VQ.stereo_itheta(X, X_ptr, Y, Y_ptr, stereo, N);
        }
        int tell = ec.tell_frac();
        if (qn != 1) {
            if (encode != 0) {
                itheta = itheta * qn + 8192 >> 14;
            }
            if (stereo != 0 && N > 2) {
                int p0 = 3;
                int x = itheta;
                int x0 = qn / 2;
                long ft = Inlines.CapToUInt32(p0 * (x0 + 1) + x0);
                if (encode != 0) {
                    ec.encode(x <= x0 ? p0 * x : x - 1 - x0 + (x0 + 1) * p0, x <= x0 ? p0 * (x + 1) : x - x0 + (x0 + 1) * p0, ft);
                } else {
                    int fs = (int)ec.decode(ft);
                    x = fs < (x0 + 1) * p0 ? fs / p0 : x0 + 1 + (fs - (x0 + 1) * p0);
                    ec.dec_update(x <= x0 ? p0 * x : x - 1 - x0 + (x0 + 1) * p0, x <= x0 ? p0 * (x + 1) : x - x0 + (x0 + 1) * p0, ft);
                    itheta = x;
                }
            } else if (B0 > 1 || stereo != 0) {
                if (encode != 0) {
                    ec.enc_uint(itheta, qn + 1);
                } else {
                    itheta = (int)ec.dec_uint(qn + 1);
                }
            } else {
                int fs = 1;
                int ft = ((qn >> 1) + 1) * ((qn >> 1) + 1);
                if (encode != 0) {
                    fs = itheta <= qn >> 1 ? itheta + 1 : qn + 1 - itheta;
                    int fl = itheta <= qn >> 1 ? itheta * (itheta + 1) >> 1 : ft - ((qn + 1 - itheta) * (qn + 2 - itheta) >> 1);
                    ec.encode(fl, fl + fs, ft);
                } else {
                    int fl = 0;
                    int fm = (int)ec.decode(ft);
                    if (fm < (qn >> 1) * ((qn >> 1) + 1) >> 1) {
                        itheta = Inlines.isqrt32(8 * fm + 1) - 1 >> 1;
                        fs = itheta + 1;
                        fl = itheta * (itheta + 1) >> 1;
                    } else {
                        itheta = 2 * (qn + 1) - Inlines.isqrt32(8 * (ft - fm - 1) + 1) >> 1;
                        fs = qn + 1 - itheta;
                        fl = ft - ((qn + 1 - itheta) * (qn + 2 - itheta) >> 1);
                    }
                    ec.dec_update(fl, fl + fs, ft);
                }
            }
            Inlines.OpusAssert(itheta >= 0);
            itheta = Inlines.celt_udiv(itheta * 16384, qn);
            if (encode != 0 && stereo != 0) {
                if (itheta == 0) {
                    Bands.intensity_stereo(m, X, X_ptr, Y, Y_ptr, bandE, i, N);
                } else {
                    Bands.stereo_split(X, X_ptr, Y, Y_ptr, N);
                }
            }
        } else if (stereo != 0) {
            if (encode != 0) {
                int n = inv = itheta > 8192 ? 1 : 0;
                if (inv != 0) {
                    for (int j = 0; j < N; ++j) {
                        Y[Y_ptr + j] = 0 - Y[Y_ptr + j];
                    }
                }
                Bands.intensity_stereo(m, X, X_ptr, Y, Y_ptr, bandE, i, N);
            }
            if (b.Val > 16 && ctx.remaining_bits > 16) {
                if (encode != 0) {
                    ec.enc_bit_logp(inv, 2);
                } else {
                    inv = ec.dec_bit_logp(2L);
                }
            } else {
                inv = 0;
            }
            itheta = 0;
        }
        int qalloc = ec.tell_frac() - tell;
        b.Val -= qalloc;
        if (itheta == 0) {
            imid = Short.MAX_VALUE;
            iside = 0;
            fill.Val &= (1 << B) - 1;
            delta = -16384;
        } else if (itheta == 16384) {
            imid = 0;
            iside = Short.MAX_VALUE;
            fill.Val &= (1 << B) - 1 << B;
            delta = 16384;
        } else {
            imid = Bands.bitexact_cos((short)itheta);
            iside = Bands.bitexact_cos((short)(16384 - itheta));
            delta = Inlines.FRAC_MUL16(N - 1 << 7, Bands.bitexact_log2tan(iside, imid));
        }
        sctx.inv = inv;
        sctx.imid = imid;
        sctx.iside = iside;
        sctx.delta = delta;
        sctx.itheta = itheta;
        sctx.qalloc = qalloc;
    }

    static int quant_band_n1(band_ctx ctx, int[] X, int X_ptr, int[] Y, int Y_ptr, int b, int[] lowband_out, int lowband_out_ptr) {
        boolean resynth = ctx.encode == 0;
        int[] x = X;
        int x_ptr = X_ptr;
        int encode = ctx.encode;
        EntropyCoder ec = ctx.ec;
        int stereo = Y != null ? 1 : 0;
        int c = 0;
        do {
            int sign = 0;
            if (ctx.remaining_bits >= 8) {
                if (encode != 0) {
                    sign = x[x_ptr] < 0 ? 1 : 0;
                    ec.enc_bits(sign, 1);
                } else {
                    sign = ec.dec_bits(1);
                }
                ctx.remaining_bits -= 8;
                b -= 8;
            }
            if (resynth) {
                x[x_ptr] = sign != 0 ? -16384 : 16384;
            }
            x = Y;
            x_ptr = Y_ptr;
        } while (++c < 1 + stereo);
        if (lowband_out != null) {
            lowband_out[lowband_out_ptr] = Inlines.SHR16(X[X_ptr], 4);
        }
        return 1;
    }

    static int quant_partition(band_ctx ctx, int[] X, int X_ptr, int N, int b, int B, int[] lowband, int lowband_ptr, int LM, int gain, int fill) {
        int imid = 0;
        int iside = 0;
        int B0 = B;
        int mid = 0;
        int side = 0;
        int cm = 0;
        boolean resynth = ctx.encode == 0;
        int Y = 0;
        int encode = ctx.encode;
        CeltMode m = ctx.m;
        int i = ctx.i;
        int spread = ctx.spread;
        EntropyCoder ec = ctx.ec;
        short[] cache = m.cache.bits;
        short cache_ptr = m.cache.index[(LM + 1) * m.nbEBands + i];
        if (LM != -1 && b > cache[cache_ptr + cache[cache_ptr]] + 12 && N > 2) {
            split_ctx sctx = new split_ctx();
            int next_lowband2 = 0;
            Y = X_ptr + (N >>= 1);
            --LM;
            if (B == 1) {
                fill = fill & 1 | fill << 1;
            }
            B = B + 1 >> 1;
            BoxedValueInt boxed_b = new BoxedValueInt(b);
            BoxedValueInt boxed_fill = new BoxedValueInt(fill);
            Bands.compute_theta(ctx, sctx, X, X_ptr, X, Y, N, boxed_b, B, B0, LM, 0, boxed_fill);
            b = boxed_b.Val;
            fill = boxed_fill.Val;
            imid = sctx.imid;
            iside = sctx.iside;
            int delta = sctx.delta;
            int itheta = sctx.itheta;
            int qalloc = sctx.qalloc;
            mid = imid;
            side = iside;
            if (B0 > 1 && (itheta & 0x3FFF) != 0) {
                delta = itheta > 8192 ? (delta -= delta >> 4 - LM) : Inlines.IMIN(0, delta + (N << 3 >> 5 - LM));
            }
            int mbits = Inlines.IMAX(0, Inlines.IMIN(b, (b - delta) / 2));
            int sbits = b - mbits;
            ctx.remaining_bits -= qalloc;
            if (lowband != null) {
                next_lowband2 = lowband_ptr + N;
            }
            int rebalance = ctx.remaining_bits;
            if (mbits >= sbits) {
                cm = Bands.quant_partition(ctx, X, X_ptr, N, mbits, B, lowband, lowband_ptr, LM, Inlines.MULT16_16_P15(gain, mid), fill);
                if ((rebalance = mbits - (rebalance - ctx.remaining_bits)) > 24 && itheta != 0) {
                    sbits += rebalance - 24;
                }
                cm |= Bands.quant_partition(ctx, X, Y, N, sbits, B, lowband, next_lowband2, LM, Inlines.MULT16_16_P15(gain, side), fill >> B) << (B0 >> 1);
            } else {
                cm = Bands.quant_partition(ctx, X, Y, N, sbits, B, lowband, next_lowband2, LM, Inlines.MULT16_16_P15(gain, side), fill >> B) << (B0 >> 1);
                if ((rebalance = sbits - (rebalance - ctx.remaining_bits)) > 24 && itheta != 16384) {
                    mbits += rebalance - 24;
                }
                cm |= Bands.quant_partition(ctx, X, X_ptr, N, mbits, B, lowband, lowband_ptr, LM, Inlines.MULT16_16_P15(gain, mid), fill);
            }
        } else {
            int q = Rate.bits2pulses(m, i, LM, b);
            int curr_bits = Rate.pulses2bits(m, i, LM, q);
            ctx.remaining_bits -= curr_bits;
            while (ctx.remaining_bits < 0 && q > 0) {
                ctx.remaining_bits += curr_bits;
                curr_bits = Rate.pulses2bits(m, i, LM, --q);
                ctx.remaining_bits -= curr_bits;
            }
            if (q != 0) {
                int K = Rate.get_pulses(q);
                cm = encode != 0 ? VQ.alg_quant(X, X_ptr, N, K, spread, B, ec) : VQ.alg_unquant(X, X_ptr, N, K, spread, B, ec, gain);
            } else if (resynth) {
                int cm_mask = (1 << B) - 1;
                if ((fill &= cm_mask) == 0) {
                    Arrays.MemSetWithOffset(X, 0, X_ptr, N);
                } else {
                    if (lowband == null) {
                        for (int j = 0; j < N; ++j) {
                            ctx.seed = Bands.celt_lcg_rand(ctx.seed);
                            X[X_ptr + j] = ctx.seed >> 20;
                        }
                        cm = cm_mask;
                    } else {
                        for (int j = 0; j < N; ++j) {
                            ctx.seed = Bands.celt_lcg_rand(ctx.seed);
                            int tmp = 4;
                            tmp = (ctx.seed & 0x8000) != 0 ? tmp : 0 - tmp;
                            X[X_ptr + j] = lowband[lowband_ptr + j] + tmp;
                        }
                        cm = fill;
                    }
                    VQ.renormalise_vector(X, X_ptr, N, gain);
                }
            }
        }
        return cm;
    }

    static int quant_band(band_ctx ctx, int[] X, int X_ptr, int N, int b, int B, int[] lowband, int lowband_ptr, int LM, int[] lowband_out, int lowband_out_ptr, int gain, int[] lowband_scratch, int lowband_scratch_ptr, int fill) {
        int k;
        int N0 = N;
        int N_B = N;
        int B0 = B;
        int time_divide = 0;
        int recombine = 0;
        int cm = 0;
        boolean resynth = ctx.encode == 0;
        int encode = ctx.encode;
        int tf_change = ctx.tf_change;
        int longBlocks = B0 == 1 ? 1 : 0;
        N_B = Inlines.celt_udiv(N_B, B);
        if (N == 1) {
            return Bands.quant_band_n1(ctx, X, X_ptr, null, 0, b, lowband_out, lowband_out_ptr);
        }
        if (tf_change > 0) {
            recombine = tf_change;
        }
        if (lowband_scratch != null && lowband != null && (recombine != 0 || (N_B & 1) == 0 && tf_change < 0 || B0 > 1)) {
            System.arraycopy(lowband, lowband_ptr, lowband_scratch, lowband_scratch_ptr, N);
            lowband = lowband_scratch;
            lowband_ptr = lowband_scratch_ptr;
        }
        for (k = 0; k < recombine; ++k) {
            if (encode != 0) {
                Bands.haar1(X, X_ptr, N >> k, 1 << k);
            }
            if (lowband != null) {
                Bands.haar1(lowband, lowband_ptr, N >> k, 1 << k);
            }
            int idx1 = fill & 0xF;
            int idx2 = fill >> 4;
            if (idx1 < 0) {
                System.out.println("e");
            }
            if (idx2 < 0) {
                System.out.println("e");
            }
            fill = bit_interleave_table[fill & 0xF] | bit_interleave_table[fill >> 4] << 2;
        }
        B >>= recombine;
        N_B <<= recombine;
        while ((N_B & 1) == 0 && tf_change < 0) {
            if (encode != 0) {
                Bands.haar1(X, X_ptr, N_B, B);
            }
            if (lowband != null) {
                Bands.haar1(lowband, lowband_ptr, N_B, B);
            }
            fill |= fill << B;
            B <<= 1;
            N_B >>= 1;
            ++time_divide;
            ++tf_change;
        }
        B0 = B;
        int N_B0 = N_B;
        if (B0 > 1) {
            if (encode != 0) {
                Bands.deinterleave_hadamard(X, X_ptr, N_B >> recombine, B0 << recombine, longBlocks);
            }
            if (lowband != null) {
                Bands.deinterleave_hadamard(lowband, lowband_ptr, N_B >> recombine, B0 << recombine, longBlocks);
            }
        }
        cm = Bands.quant_partition(ctx, X, X_ptr, N, b, B, lowband, lowband_ptr, LM, gain, fill);
        if (resynth) {
            if (B0 > 1) {
                Bands.interleave_hadamard(X, X_ptr, N_B >> recombine, B0 << recombine, longBlocks);
            }
            N_B = N_B0;
            B = B0;
            for (k = 0; k < time_divide; ++k) {
                cm |= cm >> (B >>= 1);
                Bands.haar1(X, X_ptr, N_B <<= 1, B);
            }
            for (k = 0; k < recombine; ++k) {
                cm = bit_deinterleave_table[cm];
                Bands.haar1(X, X_ptr, N0 >> k, 1 << k);
            }
            B <<= recombine;
            if (lowband_out != null) {
                int n = Inlines.celt_sqrt(Inlines.SHL32(N0, 22));
                for (int j = 0; j < N0; ++j) {
                    lowband_out[lowband_out_ptr + j] = Inlines.MULT16_16_Q15(n, X[X_ptr + j]);
                }
            }
            cm &= (1 << B) - 1;
        }
        return cm;
    }

    static int quant_band_stereo(band_ctx ctx, int[] X, int X_ptr, int[] Y, int Y_ptr, int N, int b, int B, int[] lowband, int lowband_ptr, int LM, int[] lowband_out, int lowband_out_ptr, int[] lowband_scratch, int lowband_scratch_ptr, int fill) {
        int imid = 0;
        int iside = 0;
        int inv = 0;
        int mid = 0;
        int side = 0;
        int cm = 0;
        boolean resynth = ctx.encode == 0;
        split_ctx sctx = new split_ctx();
        int encode = ctx.encode;
        EntropyCoder ec = ctx.ec;
        if (N == 1) {
            return Bands.quant_band_n1(ctx, X, X_ptr, Y, Y_ptr, b, lowband_out, lowband_out_ptr);
        }
        int orig_fill = fill;
        BoxedValueInt boxed_b = new BoxedValueInt(b);
        BoxedValueInt boxed_fill = new BoxedValueInt(fill);
        Bands.compute_theta(ctx, sctx, X, X_ptr, Y, Y_ptr, N, boxed_b, B, B, LM, 1, boxed_fill);
        b = boxed_b.Val;
        fill = boxed_fill.Val;
        inv = sctx.inv;
        imid = sctx.imid;
        iside = sctx.iside;
        int delta = sctx.delta;
        int itheta = sctx.itheta;
        int qalloc = sctx.qalloc;
        mid = imid;
        side = iside;
        if (N == 2) {
            int[] y2;
            int x2_ptr;
            int[] x2;
            int sign = 0;
            mbits = b;
            sbits = 0;
            if (itheta != 0 && itheta != 16384) {
                sbits = 8;
            }
            mbits -= sbits;
            boolean c = itheta > 8192;
            ctx.remaining_bits -= qalloc + sbits;
            if (c) {
                x2 = Y;
                x2_ptr = Y_ptr;
                y2 = X;
                y2_ptr = X_ptr;
            } else {
                x2 = X;
                x2_ptr = X_ptr;
                y2 = Y;
                y2_ptr = Y_ptr;
            }
            if (sbits != 0) {
                if (encode != 0) {
                    sign = x2[x2_ptr] * y2[Y_ptr + 1] - x2[x2_ptr + 1] * y2[Y_ptr] < 0 ? 1 : 0;
                    ec.enc_bits(sign, 1);
                } else {
                    sign = ec.dec_bits(1);
                }
            }
            sign = 1 - 2 * sign;
            cm = Bands.quant_band(ctx, x2, x2_ptr, N, mbits, B, lowband, lowband_ptr, LM, lowband_out, lowband_out_ptr, Short.MAX_VALUE, lowband_scratch, lowband_scratch_ptr, orig_fill);
            y2[Y_ptr] = (0 - sign) * x2[x2_ptr + 1];
            y2[Y_ptr + 1] = sign * x2[x2_ptr];
            if (resynth) {
                X[X_ptr] = Inlines.MULT16_16_Q15(mid, X[X_ptr]);
                X[X_ptr + 1] = Inlines.MULT16_16_Q15(mid, X[X_ptr + 1]);
                Y[Y_ptr] = Inlines.MULT16_16_Q15(side, Y[Y_ptr]);
                Y[Y_ptr + 1] = Inlines.MULT16_16_Q15(side, Y[Y_ptr + 1]);
                int tmp = X[X_ptr];
                X[X_ptr] = Inlines.SUB16(tmp, Y[Y_ptr]);
                Y[Y_ptr] = Inlines.ADD16(tmp, Y[Y_ptr]);
                tmp = X[X_ptr + 1];
                X[X_ptr + 1] = Inlines.SUB16(tmp, Y[Y_ptr + 1]);
                Y[Y_ptr + 1] = Inlines.ADD16(tmp, Y[Y_ptr + 1]);
            }
        } else {
            mbits = Inlines.IMAX(0, Inlines.IMIN(b, (b - delta) / 2));
            sbits = b - mbits;
            ctx.remaining_bits -= qalloc;
            int rebalance = ctx.remaining_bits;
            if (mbits >= sbits) {
                cm = Bands.quant_band(ctx, X, X_ptr, N, mbits, B, lowband, lowband_ptr, LM, lowband_out, lowband_out_ptr, Short.MAX_VALUE, lowband_scratch, lowband_scratch_ptr, fill);
                if ((rebalance = mbits - (rebalance - ctx.remaining_bits)) > 24 && itheta != 0) {
                    sbits += rebalance - 24;
                }
                cm |= Bands.quant_band(ctx, Y, Y_ptr, N, sbits, B, null, 0, LM, null, 0, side, null, 0, fill >> B);
            } else {
                cm = Bands.quant_band(ctx, Y, Y_ptr, N, sbits, B, null, 0, LM, null, 0, side, null, 0, fill >> B);
                if ((rebalance = sbits - (rebalance - ctx.remaining_bits)) > 24 && itheta != 16384) {
                    mbits += rebalance - 24;
                }
                cm |= Bands.quant_band(ctx, X, X_ptr, N, mbits, B, lowband, lowband_ptr, LM, lowband_out, lowband_out_ptr, Short.MAX_VALUE, lowband_scratch, lowband_scratch_ptr, fill);
            }
        }
        if (resynth) {
            if (N != 2) {
                Bands.stereo_merge(X, X_ptr, Y, Y_ptr, mid, N);
            }
            if (inv != 0) {
                for (int j = Y_ptr; j < N + Y_ptr; ++j) {
                    Y[j] = (short)(0 - Y[j]);
                }
            }
        }
        return cm;
    }

    static void quant_all_bands(int encode, CeltMode m, int start, int end, int[] X_, int[] Y_, short[] collapse_masks, int[][] bandE, int[] pulses, int shortBlocks, int spread, int dual_stereo, int intensity, int[] tf_res, int total_bits, int balance, EntropyCoder ec, int LM, int codedBands, BoxedValueInt seed) {
        short[] eBands = m.eBands;
        boolean update_lowband = true;
        int C = Y_ != null ? 2 : 1;
        boolean resynth = encode == 0;
        band_ctx ctx = new band_ctx();
        int M = 1 << LM;
        int B = shortBlocks != 0 ? M : 1;
        int norm_offset = M * eBands[start];
        int[] norm = new int[C * (M * eBands[m.nbEBands - 1] - norm_offset)];
        int norm2 = M * eBands[m.nbEBands - 1] - norm_offset;
        int[] lowband_scratch = X_;
        int lowband_scratch_ptr = M * eBands[m.nbEBands - 1];
        int lowband_offset = 0;
        ctx.bandE = bandE;
        ctx.ec = ec;
        ctx.encode = encode;
        ctx.intensity = intensity;
        ctx.m = m;
        ctx.seed = seed.Val;
        ctx.spread = spread;
        for (int i = start; i < end; ++i) {
            long x_cm;
            long y_cm;
            int b;
            int remaining_bits;
            int[] Y;
            int effective_lowband = -1;
            int Y_ptr = 0;
            int tf_change = 0;
            ctx.i = i;
            boolean last = i == end - 1;
            int[] X = X_;
            int X_ptr = M * eBands[i];
            if (Y_ != null) {
                Y = Y_;
                Y_ptr = M * eBands[i];
            } else {
                Y = null;
            }
            int N = M * eBands[i + 1] - M * eBands[i];
            int tell = ec.tell_frac();
            if (i != start) {
                balance -= tell;
            }
            ctx.remaining_bits = remaining_bits = total_bits - tell - 1;
            if (i <= codedBands - 1) {
                int curr_balance = Inlines.celt_sudiv(balance, Inlines.IMIN(3, codedBands - i));
                b = Inlines.IMAX(0, Inlines.IMIN(16383, Inlines.IMIN(remaining_bits + 1, pulses[i] + curr_balance)));
            } else {
                b = 0;
            }
            if (resynth && M * eBands[i] - N >= M * eBands[start] && (update_lowband || lowband_offset == 0)) {
                lowband_offset = i;
            }
            ctx.tf_change = tf_change = tf_res[i];
            if (i >= m.effEBands) {
                X = norm;
                X_ptr = 0;
                if (Y_ != null) {
                    Y = norm;
                    Y_ptr = 0;
                }
                lowband_scratch = null;
            }
            if (i == end - 1) {
                lowband_scratch = null;
            }
            if (lowband_offset != 0 && (spread != 3 || B > 1 || tf_change < 0)) {
                effective_lowband = Inlines.IMAX(0, M * eBands[lowband_offset] - norm_offset - N);
                int fold_start = lowband_offset;
                while (M * eBands[--fold_start] > effective_lowband + norm_offset) {
                }
                int fold_end = lowband_offset - 1;
                while (M * eBands[++fold_end] < effective_lowband + norm_offset + N) {
                }
                y_cm = 0L;
                x_cm = 0L;
                int fold_i = fold_start;
                do {
                    x_cm |= (long)collapse_masks[fold_i * C + 0];
                    y_cm |= (long)collapse_masks[fold_i * C + C - 1];
                } while (++fold_i < fold_end);
            } else {
                x_cm = y_cm = (long)((1 << B) - 1);
            }
            if (dual_stereo != 0 && i == intensity) {
                dual_stereo = 0;
                if (resynth) {
                    for (int j = 0; j < M * eBands[i] - norm_offset; ++j) {
                        norm[j] = Inlines.HALF32(norm[j] + norm[norm2 + j]);
                    }
                }
            }
            if (dual_stereo != 0) {
                x_cm = Bands.quant_band(ctx, X, X_ptr, N, b / 2, B, (int[])(effective_lowband != -1 ? norm : null), effective_lowband, LM, last ? null : norm, M * eBands[i] - norm_offset, Short.MAX_VALUE, lowband_scratch, lowband_scratch_ptr, (int)x_cm);
                y_cm = Bands.quant_band(ctx, Y, Y_ptr, N, b / 2, B, (int[])(effective_lowband != -1 ? norm : null), norm2 + effective_lowband, LM, last ? null : norm, norm2 + (M * eBands[i] - norm_offset), Short.MAX_VALUE, lowband_scratch, lowband_scratch_ptr, (int)y_cm);
            } else {
                x_cm = Y != null ? (long)Bands.quant_band_stereo(ctx, X, X_ptr, Y, Y_ptr, N, b, B, (int[])(effective_lowband != -1 ? norm : null), effective_lowband, LM, last ? null : norm, M * eBands[i] - norm_offset, lowband_scratch, lowband_scratch_ptr, (int)(x_cm | y_cm)) : (long)Bands.quant_band(ctx, X, X_ptr, N, b, B, (int[])(effective_lowband != -1 ? norm : null), effective_lowband, LM, last ? null : norm, M * eBands[i] - norm_offset, Short.MAX_VALUE, lowband_scratch, lowband_scratch_ptr, (int)(x_cm | y_cm));
                y_cm = x_cm;
            }
            collapse_masks[i * C + 0] = (short)(x_cm & 0xFFL);
            collapse_masks[i * C + C - 1] = (short)(y_cm & 0xFFL);
            balance += pulses[i] + tell;
            update_lowband = b > N << 3;
        }
        seed.Val = ctx.seed;
    }

    public static class band_ctx {
        public int encode;
        public CeltMode m;
        public int i;
        public int intensity;
        public int spread;
        public int tf_change;
        public EntropyCoder ec;
        public int remaining_bits;
        public int[][] bandE;
        public int seed;
    }

    public static class split_ctx {
        public int inv;
        public int imid;
        public int iside;
        public int delta;
        public int itheta;
        public int qalloc;
    }
}
