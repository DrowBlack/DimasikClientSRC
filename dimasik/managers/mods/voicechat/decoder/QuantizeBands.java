package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;
import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.CeltMode;
import dimasik.managers.mods.voicechat.decoder.CeltTables;
import dimasik.managers.mods.voicechat.decoder.EntropyCoder;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.Laplace;

class QuantizeBands {
    private static final int[] pred_coef = new int[]{29440, 26112, 21248, 16384};
    private static final int[] beta_coef = new int[]{30147, 22282, 12124, 6554};
    private static final int beta_intra = 4915;
    private static short[] small_energy_icdf = new short[]{2, 1, 0};

    QuantizeBands() {
    }

    static int loss_distortion(int[][] eBands, int[][] oldEBands, int start, int end, int len, int C) {
        int dist = 0;
        int c = 0;
        do {
            for (int i = start; i < end; ++i) {
                int d = Inlines.SUB16(Inlines.SHR16(eBands[c][i], 3), Inlines.SHR16(oldEBands[c][i], 3));
                dist = Inlines.MAC16_16(dist, d, d);
            }
        } while (++c < C);
        return Inlines.MIN32(200, Inlines.SHR32(dist, 14));
    }

    static int quant_coarse_energy_impl(CeltMode m, int start, int end, int[][] eBands, int[][] oldEBands, int budget, int tell, short[] prob_model, int[][] error, EntropyCoder enc, int C, int LM, int intra, int max_decay, int lfe) {
        int beta;
        int coef;
        int badness = 0;
        int[] prev = new int[]{0, 0};
        if (tell + 3 <= budget) {
            enc.enc_bit_logp(intra, 3);
        }
        if (intra != 0) {
            coef = 0;
            beta = 4915;
        } else {
            beta = beta_coef[LM];
            coef = pred_coef[LM];
        }
        for (int i = start; i < end; ++i) {
            int c = 0;
            do {
                int x = eBands[c][i];
                int oldE = Inlines.MAX16(-9216, oldEBands[c][i]);
                int f = Inlines.SHL32(Inlines.EXTEND32(x), 7) - Inlines.PSHR32(Inlines.MULT16_16(coef, oldE), 8) - prev[c];
                int qi = f + 65536 >> 17;
                short decay_bound = Inlines.EXTRACT16(Inlines.MAX32(-28672, Inlines.SUB32(oldEBands[c][i], max_decay)));
                if (qi < 0 && x < decay_bound && (qi += Inlines.SHR16(Inlines.SUB16((int)decay_bound, x), 10)) > 0) {
                    qi = 0;
                }
                int qi0 = qi;
                tell = enc.tell();
                int bits_left = budget - tell - 3 * C * (end - i);
                if (i != start && bits_left < 30) {
                    if (bits_left < 24) {
                        qi = Inlines.IMIN(1, qi);
                    }
                    if (bits_left < 16) {
                        qi = Inlines.IMAX(-1, qi);
                    }
                }
                if (lfe != 0 && i >= 2) {
                    qi = Inlines.IMIN(qi, 0);
                }
                if (budget - tell >= 15) {
                    int pi = 2 * Inlines.IMIN(i, 20);
                    BoxedValueInt boxed_qi = new BoxedValueInt(qi);
                    Laplace.ec_laplace_encode(enc, boxed_qi, prob_model[pi] << 7, prob_model[pi + 1] << 6);
                    qi = boxed_qi.Val;
                } else if (budget - tell >= 2) {
                    enc.enc_icdf(2 * qi ^ 0 - ((qi = Inlines.IMAX(-1, Inlines.IMIN(qi, 1))) < 0 ? 1 : 0), small_energy_icdf, 2);
                } else if (budget - tell >= 1) {
                    qi = Inlines.IMIN(0, qi);
                    enc.enc_bit_logp(-qi, 1);
                } else {
                    qi = -1;
                }
                error[c][i] = Inlines.PSHR32(f, 7) - Inlines.SHL16(qi, 10);
                badness += Inlines.abs(qi0 - qi);
                int q = Inlines.SHL32(qi, 10);
                int tmp = Inlines.PSHR32(Inlines.MULT16_16(coef, oldE), 8) + prev[c] + Inlines.SHL32(q, 7);
                tmp = Inlines.MAX32(-3670016, tmp);
                oldEBands[c][i] = Inlines.PSHR32(tmp, 7);
                prev[c] = prev[c] + Inlines.SHL32(q, 7) - Inlines.MULT16_16(beta, Inlines.PSHR32(q, 8));
            } while (++c < C);
        }
        return lfe != 0 ? 0 : badness;
    }

    static void quant_coarse_energy(CeltMode m, int start, int end, int effEnd, int[][] eBands, int[][] oldEBands, int budget, int[][] error, EntropyCoder enc, int C, int LM, int nbAvailableBytes, int force_intra, BoxedValueInt delayedIntra, int two_pass, int loss_rate, int lfe) {
        EntropyCoder enc_start_state = new EntropyCoder();
        int badness1 = 0;
        int intra = force_intra != 0 || two_pass == 0 && delayedIntra.Val > 2 * C * (end - start) && nbAvailableBytes > (end - start) * C ? 1 : 0;
        int intra_bias = budget * delayedIntra.Val * loss_rate / (C * 512);
        int new_distortion = QuantizeBands.loss_distortion(eBands, oldEBands, start, effEnd, m.nbEBands, C);
        int tell = enc.tell();
        if (tell + 3 > budget) {
            intra = 0;
            two_pass = 0;
        }
        int max_decay = 16384;
        if (end - start > 10) {
            max_decay = Inlines.MIN32(max_decay, Inlines.SHL32(nbAvailableBytes, 7));
        }
        if (lfe != 0) {
            max_decay = 3072;
        }
        enc_start_state.Assign(enc);
        int[][] oldEBands_intra = Arrays.InitTwoDimensionalArrayInt(C, m.nbEBands);
        int[][] error_intra = Arrays.InitTwoDimensionalArrayInt(C, m.nbEBands);
        System.arraycopy(oldEBands[0], 0, oldEBands_intra[0], 0, m.nbEBands);
        if (C == 2) {
            System.arraycopy(oldEBands[1], 0, oldEBands_intra[1], 0, m.nbEBands);
        }
        if (two_pass != 0 || intra != 0) {
            badness1 = QuantizeBands.quant_coarse_energy_impl(m, start, end, eBands, oldEBands_intra, budget, tell, CeltTables.e_prob_model[LM][1], error_intra, enc, C, LM, 1, max_decay, lfe);
        }
        if (intra == 0) {
            EntropyCoder enc_intra_state = new EntropyCoder();
            byte[] intra_bits = null;
            int tell_intra = enc.tell_frac();
            enc_intra_state.Assign(enc);
            int nstart_bytes = enc_start_state.range_bytes();
            int nintra_bytes = enc_intra_state.range_bytes();
            int intra_buf = nstart_bytes;
            int save_bytes = nintra_bytes - nstart_bytes;
            if (save_bytes != 0) {
                intra_bits = new byte[save_bytes];
                System.arraycopy(enc_intra_state.get_buffer(), intra_buf, intra_bits, 0, save_bytes);
            }
            enc.Assign(enc_start_state);
            int badness2 = QuantizeBands.quant_coarse_energy_impl(m, start, end, eBands, oldEBands, budget, tell, CeltTables.e_prob_model[LM][intra], error, enc, C, LM, 0, max_decay, lfe);
            if (two_pass != 0 && (badness1 < badness2 || badness1 == badness2 && enc.tell_frac() + intra_bias > tell_intra)) {
                enc.Assign(enc_intra_state);
                if (intra_bits != null) {
                    enc_intra_state.write_buffer(intra_bits, 0, intra_buf, nintra_bytes - nstart_bytes);
                }
                System.arraycopy(oldEBands_intra[0], 0, oldEBands[0], 0, m.nbEBands);
                System.arraycopy(error_intra[0], 0, error[0], 0, m.nbEBands);
                if (C == 2) {
                    System.arraycopy(oldEBands_intra[1], 0, oldEBands[1], 0, m.nbEBands);
                    System.arraycopy(error_intra[1], 0, error[1], 0, m.nbEBands);
                }
                intra = 1;
            }
        } else {
            System.arraycopy(oldEBands_intra[0], 0, oldEBands[0], 0, m.nbEBands);
            System.arraycopy(error_intra[0], 0, error[0], 0, m.nbEBands);
            if (C == 2) {
                System.arraycopy(oldEBands_intra[1], 0, oldEBands[1], 0, m.nbEBands);
                System.arraycopy(error_intra[1], 0, error[1], 0, m.nbEBands);
            }
        }
        delayedIntra.Val = intra != 0 ? new_distortion : Inlines.ADD32(Inlines.MULT16_32_Q15(Inlines.MULT16_16_Q15(pred_coef[LM], pred_coef[LM]), delayedIntra.Val), new_distortion);
    }

    static void quant_fine_energy(CeltMode m, int start, int end, int[][] oldEBands, int[][] error, int[] fine_quant, EntropyCoder enc, int C) {
        for (int i = start; i < end; ++i) {
            int frac = 1 << fine_quant[i];
            if (fine_quant[i] <= 0) continue;
            int c = 0;
            do {
                int q2;
                if ((q2 = error[c][i] + 512 >> 10 - fine_quant[i]) > frac - 1) {
                    q2 = frac - 1;
                }
                if (q2 < 0) {
                    q2 = 0;
                }
                enc.enc_bits(q2, fine_quant[i]);
                int offset = Inlines.SUB16(Inlines.SHR32(Inlines.SHL32(q2, 10) + 512, fine_quant[i]), 512);
                int[] nArray = oldEBands[c];
                int n = i;
                nArray[n] = nArray[n] + offset;
                int[] nArray2 = error[c];
                int n2 = i;
                nArray2[n2] = nArray2[n2] - offset;
            } while (++c < C);
        }
    }

    static void quant_energy_finalise(CeltMode m, int start, int end, int[][] oldEBands, int[][] error, int[] fine_quant, int[] fine_priority, int bits_left, EntropyCoder enc, int C) {
        for (int prio = 0; prio < 2; ++prio) {
            for (int i = start; i < end && bits_left >= C; ++i) {
                if (fine_quant[i] >= 8 || fine_priority[i] != prio) continue;
                int c = 0;
                do {
                    int q2 = error[c][i] < 0 ? 0 : 1;
                    enc.enc_bits(q2, 1);
                    int offset = Inlines.SHR16(Inlines.SHL16(q2, 10) - 512, fine_quant[i] + 1);
                    int[] nArray = oldEBands[c];
                    int n = i;
                    nArray[n] = nArray[n] + offset;
                    --bits_left;
                } while (++c < C);
            }
        }
    }

    static void unquant_coarse_energy(CeltMode m, int start, int end, int[] oldEBands, int intra, EntropyCoder dec, int C, int LM) {
        int beta;
        int coef;
        short[] prob_model = CeltTables.e_prob_model[LM][intra];
        int[] prev = new int[]{0, 0};
        if (intra != 0) {
            coef = 0;
            beta = 4915;
        } else {
            beta = beta_coef[LM];
            coef = pred_coef[LM];
        }
        int budget = dec.storage * 8;
        for (int i = start; i < end; ++i) {
            int c = 0;
            do {
                int qi;
                Inlines.OpusAssert(c < 2);
                int tell = dec.tell();
                if (budget - tell >= 15) {
                    int pi = 2 * Inlines.IMIN(i, 20);
                    qi = Laplace.ec_laplace_decode(dec, prob_model[pi] << 7, prob_model[pi + 1] << 6);
                } else if (budget - tell >= 2) {
                    qi = dec.dec_icdf(small_energy_icdf, 2);
                    qi = qi >> 1 ^ -(qi & 1);
                } else {
                    qi = budget - tell >= 1 ? 0 - dec.dec_bit_logp(1L) : -1;
                }
                int q = Inlines.SHL32(qi, 10);
                oldEBands[i + c * m.nbEBands] = Inlines.MAX16(-9216, oldEBands[i + c * m.nbEBands]);
                int tmp = Inlines.PSHR32(Inlines.MULT16_16(coef, oldEBands[i + c * m.nbEBands]), 8) + prev[c] + Inlines.SHL32(q, 7);
                tmp = Inlines.MAX32(-3670016, tmp);
                oldEBands[i + c * m.nbEBands] = Inlines.PSHR32(tmp, 7);
                prev[c] = prev[c] + Inlines.SHL32(q, 7) - Inlines.MULT16_16(beta, Inlines.PSHR32(q, 8));
            } while (++c < C);
        }
    }

    static void unquant_fine_energy(CeltMode m, int start, int end, int[] oldEBands, int[] fine_quant, EntropyCoder dec, int C) {
        for (int i = start; i < end; ++i) {
            if (fine_quant[i] <= 0) continue;
            int c = 0;
            do {
                int q2 = dec.dec_bits(fine_quant[i]);
                int offset = Inlines.SUB16(Inlines.SHR32(Inlines.SHL32(q2, 10) + 512, fine_quant[i]), 512);
                int n = i + c * m.nbEBands;
                oldEBands[n] = oldEBands[n] + offset;
            } while (++c < C);
        }
    }

    static void unquant_energy_finalise(CeltMode m, int start, int end, int[] oldEBands, int[] fine_quant, int[] fine_priority, int bits_left, EntropyCoder dec, int C) {
        for (int prio = 0; prio < 2; ++prio) {
            for (int i = start; i < end && bits_left >= C; ++i) {
                if (fine_quant[i] >= 8 || fine_priority[i] != prio) continue;
                int c = 0;
                do {
                    int q2 = dec.dec_bits(1);
                    int offset = Inlines.SHR16(Inlines.SHL16(q2, 10) - 512, fine_quant[i] + 1);
                    int n = i + c * m.nbEBands;
                    oldEBands[n] = oldEBands[n] + offset;
                    --bits_left;
                } while (++c < C);
            }
        }
    }

    static void amp2Log2(CeltMode m, int effEnd, int end, int[][] bandE, int[][] bandLogE, int C) {
        int c = 0;
        do {
            int i;
            for (i = 0; i < effEnd; ++i) {
                bandLogE[c][i] = Inlines.celt_log2(Inlines.SHL32(bandE[c][i], 2)) - Inlines.SHL16((int)CeltTables.eMeans[i], 6);
            }
            for (i = effEnd; i < end; ++i) {
                bandLogE[c][i] = -14336;
            }
        } while (++c < C);
    }

    static void amp2Log2(CeltMode m, int effEnd, int end, int[] bandE, int[] bandLogE, int bandLogE_ptr, int C) {
        int c = 0;
        do {
            int i;
            for (i = 0; i < effEnd; ++i) {
                bandLogE[bandLogE_ptr + c * m.nbEBands + i] = Inlines.celt_log2(Inlines.SHL32(bandE[i + c * m.nbEBands], 2)) - Inlines.SHL16((int)CeltTables.eMeans[i], 6);
            }
            for (i = effEnd; i < end; ++i) {
                bandLogE[bandLogE_ptr + c * m.nbEBands + i] = -14336;
            }
        } while (++c < C);
    }
}
