package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;
import dimasik.managers.mods.voicechat.decoder.Autocorrelation;
import dimasik.managers.mods.voicechat.decoder.Bands;
import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.CeltCommon;
import dimasik.managers.mods.voicechat.decoder.CeltLPC;
import dimasik.managers.mods.voicechat.decoder.CeltMode;
import dimasik.managers.mods.voicechat.decoder.CeltTables;
import dimasik.managers.mods.voicechat.decoder.EntropyCoder;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.Kernels;
import dimasik.managers.mods.voicechat.decoder.OpusError;
import dimasik.managers.mods.voicechat.decoder.QuantizeBands;
import dimasik.managers.mods.voicechat.decoder.Rate;
import dimasik.managers.mods.voicechat.decoder.VQ;

class CeltDecoder {
    CeltMode mode = null;
    int overlap = 0;
    int channels = 0;
    int stream_channels = 0;
    int downsample = 0;
    int start = 0;
    int end = 0;
    int signalling = 0;
    int rng = 0;
    int error = 0;
    int last_pitch_index = 0;
    int loss_count = 0;
    int postfilter_period = 0;
    int postfilter_period_old = 0;
    int postfilter_gain = 0;
    int postfilter_gain_old = 0;
    int postfilter_tapset = 0;
    int postfilter_tapset_old = 0;
    final int[] preemph_memD = new int[2];
    int[][] decode_mem = null;
    int[][] lpc = null;
    int[] oldEBands = null;
    int[] oldLogE = null;
    int[] oldLogE2 = null;
    int[] backgroundLogE = null;

    CeltDecoder() {
    }

    private void Reset() {
        this.mode = null;
        this.overlap = 0;
        this.channels = 0;
        this.stream_channels = 0;
        this.downsample = 0;
        this.start = 0;
        this.end = 0;
        this.signalling = 0;
        this.PartialReset();
    }

    private void PartialReset() {
        this.rng = 0;
        this.error = 0;
        this.last_pitch_index = 0;
        this.loss_count = 0;
        this.postfilter_period = 0;
        this.postfilter_period_old = 0;
        this.postfilter_gain = 0;
        this.postfilter_gain_old = 0;
        this.postfilter_tapset = 0;
        this.postfilter_tapset_old = 0;
        Arrays.MemSet(this.preemph_memD, 0, 2);
        this.decode_mem = null;
        this.lpc = null;
        this.oldEBands = null;
        this.oldLogE = null;
        this.oldLogE2 = null;
        this.backgroundLogE = null;
    }

    void ResetState() {
        this.PartialReset();
        this.decode_mem = new int[this.channels][];
        this.lpc = new int[this.channels][];
        for (int c = 0; c < this.channels; ++c) {
            this.decode_mem[c] = new int[2048 + this.mode.overlap];
            this.lpc[c] = new int[24];
        }
        this.oldEBands = new int[2 * this.mode.nbEBands];
        this.oldLogE = new int[2 * this.mode.nbEBands];
        this.oldLogE2 = new int[2 * this.mode.nbEBands];
        this.backgroundLogE = new int[2 * this.mode.nbEBands];
        for (int i = 0; i < 2 * this.mode.nbEBands; ++i) {
            this.oldLogE2[i] = -28672;
            this.oldLogE[i] = -28672;
        }
    }

    int celt_decoder_init(int sampling_rate, int channels) {
        int ret = this.opus_custom_decoder_init(CeltMode.mode48000_960_120, channels);
        if (ret != OpusError.OPUS_OK) {
            return ret;
        }
        this.downsample = CeltCommon.resampling_factor(sampling_rate);
        if (this.downsample == 0) {
            return OpusError.OPUS_BAD_ARG;
        }
        return OpusError.OPUS_OK;
    }

    private int opus_custom_decoder_init(CeltMode mode, int channels) {
        if (channels < 0 || channels > 2) {
            return OpusError.OPUS_BAD_ARG;
        }
        if (this == null) {
            return OpusError.OPUS_ALLOC_FAIL;
        }
        this.Reset();
        this.mode = mode;
        this.overlap = mode.overlap;
        this.stream_channels = this.channels = channels;
        this.downsample = 1;
        this.start = 0;
        this.end = this.mode.effEBands;
        this.signalling = 1;
        this.loss_count = 0;
        this.ResetState();
        return OpusError.OPUS_OK;
    }

    void celt_decode_lost(int N, int LM) {
        boolean noise_based;
        int C = this.channels;
        int[][] out_syn = new int[2][];
        int[] out_syn_ptrs = new int[2];
        CeltMode mode = this.mode;
        int nbEBands = mode.nbEBands;
        int overlap = mode.overlap;
        short[] eBands = mode.eBands;
        int c = 0;
        do {
            out_syn[c] = this.decode_mem[c];
            out_syn_ptrs[c] = 2048 - N;
        } while (++c < C);
        boolean bl = noise_based = this.loss_count >= 5 || this.start != 0;
        if (noise_based) {
            int i;
            int end = this.end;
            int effEnd = Inlines.IMAX(this.start, Inlines.IMIN(end, mode.effEBands));
            int[][] X = Arrays.InitTwoDimensionalArrayInt(C, N);
            int decay = this.loss_count == 0 ? 1536 : 512;
            c = 0;
            do {
                for (i = this.start; i < end; ++i) {
                    this.oldEBands[c * nbEBands + i] = Inlines.MAX16(this.backgroundLogE[c * nbEBands + i], this.oldEBands[c * nbEBands + i] - decay);
                }
            } while (++c < C);
            int seed = this.rng;
            for (c = 0; c < C; ++c) {
                for (i = this.start; i < effEnd; ++i) {
                    int boffs = eBands[i] << LM;
                    int blen = eBands[i + 1] - eBands[i] << LM;
                    for (int j = 0; j < blen; ++j) {
                        seed = Bands.celt_lcg_rand(seed);
                        X[c][boffs + j] = seed >> 20;
                    }
                    VQ.renormalise_vector(X[c], 0, blen, Short.MAX_VALUE);
                }
            }
            this.rng = seed;
            c = 0;
            do {
                Arrays.MemMove(this.decode_mem[c], N, 0, 2048 - N + (overlap >> 1));
            } while (++c < C);
            CeltCommon.celt_synthesis(mode, X, out_syn, out_syn_ptrs, this.oldEBands, this.start, effEnd, C, C, 0, LM, this.downsample, 0);
        } else {
            int pitch_index;
            int fade = Short.MAX_VALUE;
            if (this.loss_count == 0) {
                this.last_pitch_index = pitch_index = CeltCommon.celt_plc_pitch_search(this.decode_mem, C);
            } else {
                pitch_index = this.last_pitch_index;
                fade = 26214;
            }
            int[] etmp = new int[overlap];
            int[] exc = new int[1024];
            int[] window = mode.window;
            c = 0;
            do {
                int i;
                int S1 = 0;
                int[] buf = this.decode_mem[c];
                for (i = 0; i < 1024; ++i) {
                    exc[i] = Inlines.ROUND16(buf[1024 + i], 12);
                }
                if (this.loss_count == 0) {
                    int[] ac = new int[25];
                    Autocorrelation._celt_autocorr(exc, ac, window, overlap, 24, 1024);
                    ac[0] = ac[0] + Inlines.SHR32(ac[0], 13);
                    for (i = 1; i <= 24; ++i) {
                        int n = i;
                        ac[n] = ac[n] - Inlines.MULT16_32_Q15(2 * i * i, ac[i]);
                    }
                    CeltLPC.celt_lpc(this.lpc[c], ac, 24);
                }
                int exc_length = Inlines.IMIN(2 * pitch_index, 1024);
                int[] lpc_mem = new int[24];
                for (i = 0; i < 24; ++i) {
                    lpc_mem[i] = Inlines.ROUND16(buf[2048 - exc_length - 1 - i], 12);
                }
                Kernels.celt_fir(exc, 1024 - exc_length, this.lpc[c], 0, exc, 1024 - exc_length, exc_length, 24, lpc_mem);
                int E1 = 1;
                int E2 = 1;
                int shift = Inlines.IMAX(0, 2 * Inlines.celt_zlog2(Inlines.celt_maxabs16(exc, 1024 - exc_length, exc_length)) - 20);
                int decay_length = exc_length >> 1;
                for (i = 0; i < decay_length; ++i) {
                    int e = exc[1024 - decay_length + i];
                    E1 += Inlines.SHR32(Inlines.MULT16_16(e, e), shift);
                    e = exc[1024 - 2 * decay_length + i];
                    E2 += Inlines.SHR32(Inlines.MULT16_16(e, e), shift);
                }
                E1 = Inlines.MIN32(E1, E2);
                int decay = Inlines.celt_sqrt(Inlines.frac_div32(Inlines.SHR32(E1, 1), E2));
                Arrays.MemMove(buf, N, 0, 2048 - N);
                int extrapolation_offset = 1024 - pitch_index;
                int extrapolation_len = N + overlap;
                int attenuation = Inlines.MULT16_16_Q15(fade, decay);
                int j = 0;
                i = 0;
                while (i < extrapolation_len) {
                    if (j >= pitch_index) {
                        j -= pitch_index;
                        attenuation = Inlines.MULT16_16_Q15(attenuation, decay);
                    }
                    buf[2048 - N + i] = Inlines.SHL32(Inlines.MULT16_16_Q15(attenuation, exc[extrapolation_offset + j]), 12);
                    int tmp = Inlines.ROUND16(buf[1024 - N + extrapolation_offset + j], 12);
                    S1 += Inlines.SHR32(Inlines.MULT16_16(tmp, tmp), 8);
                    ++i;
                    ++j;
                }
                lpc_mem = new int[24];
                for (i = 0; i < 24; ++i) {
                    lpc_mem[i] = Inlines.ROUND16(buf[2048 - N - 1 - i], 12);
                }
                CeltLPC.celt_iir(buf, 2048 - N, this.lpc[c], buf, 2048 - N, extrapolation_len, 24, lpc_mem);
                int S2 = 0;
                for (i = 0; i < extrapolation_len; ++i) {
                    int tmp = Inlines.ROUND16(buf[2048 - N + i], 12);
                    S2 += Inlines.SHR32(Inlines.MULT16_16(tmp, tmp), 8);
                }
                if (S1 <= Inlines.SHR32(S2, 2)) {
                    for (i = 0; i < extrapolation_len; ++i) {
                        buf[2048 - N + i] = 0;
                    }
                } else if (S1 < S2) {
                    int ratio = Inlines.celt_sqrt(Inlines.frac_div32(Inlines.SHR32(S1, 1) + 1, S2 + 1));
                    for (i = 0; i < overlap; ++i) {
                        int tmp_g = Short.MAX_VALUE - Inlines.MULT16_16_Q15(window[i], Short.MAX_VALUE - ratio);
                        buf[2048 - N + i] = Inlines.MULT16_32_Q15(tmp_g, buf[2048 - N + i]);
                    }
                    for (i = overlap; i < extrapolation_len; ++i) {
                        buf[2048 - N + i] = Inlines.MULT16_32_Q15(ratio, buf[2048 - N + i]);
                    }
                }
                CeltCommon.comb_filter(etmp, 0, buf, 2048, this.postfilter_period, this.postfilter_period, overlap, -this.postfilter_gain, -this.postfilter_gain, this.postfilter_tapset, this.postfilter_tapset, null, 0);
                for (i = 0; i < overlap / 2; ++i) {
                    buf[2048 + i] = Inlines.MULT16_32_Q15(window[i], etmp[overlap - 1 - i]) + Inlines.MULT16_32_Q15(window[overlap - i - 1], etmp[i]);
                }
            } while (++c < C);
        }
        ++this.loss_count;
    }

    int celt_decode_with_ec(byte[] data, int data_ptr, int len, short[] pcm, int pcm_ptr, int frame_size, EntropyCoder dec, int accum) {
        int isTransient;
        int i;
        int LM;
        int[][] out_syn = new int[2][];
        int[] out_syn_ptrs = new int[2];
        int CC = this.channels;
        int intensity = 0;
        int dual_stereo = 0;
        int anti_collapse_on = 0;
        int C = this.stream_channels;
        CeltMode mode = this.mode;
        int nbEBands = mode.nbEBands;
        int overlap = mode.overlap;
        short[] eBands = mode.eBands;
        int start = this.start;
        int end = this.end;
        frame_size *= this.downsample;
        int[] oldBandE = this.oldEBands;
        int[] oldLogE = this.oldLogE;
        int[] oldLogE2 = this.oldLogE2;
        int[] backgroundLogE = this.backgroundLogE;
        for (LM = 0; LM <= mode.maxLM && mode.shortMdctSize << LM != frame_size; ++LM) {
        }
        if (LM > mode.maxLM) {
            return OpusError.OPUS_BAD_ARG;
        }
        int M = 1 << LM;
        if (len < 0 || len > 1275 || pcm == null) {
            return OpusError.OPUS_BAD_ARG;
        }
        int N = M * mode.shortMdctSize;
        int c = 0;
        do {
            out_syn[c] = this.decode_mem[c];
            out_syn_ptrs[c] = 2048 - N;
        } while (++c < CC);
        int effEnd = end;
        if (effEnd > mode.effEBands) {
            effEnd = mode.effEBands;
        }
        if (data == null || len <= 1) {
            this.celt_decode_lost(N, LM);
            CeltCommon.deemphasis(out_syn, out_syn_ptrs, pcm, pcm_ptr, N, CC, this.downsample, mode.preemph, this.preemph_memD, accum);
            return frame_size / this.downsample;
        }
        if (dec == null) {
            dec = new EntropyCoder();
            dec.dec_init(data, data_ptr, len);
        }
        if (C == 1) {
            for (i = 0; i < nbEBands; ++i) {
                oldBandE[i] = Inlines.MAX16(oldBandE[i], oldBandE[nbEBands + i]);
            }
        }
        int total_bits = len * 8;
        int tell = dec.tell();
        int silence = tell >= total_bits ? 1 : (tell == 1 ? dec.dec_bit_logp(15L) : 0);
        if (silence != 0) {
            tell = len * 8;
            dec.nbits_total += tell - dec.tell();
        }
        int postfilter_gain = 0;
        int postfilter_pitch = 0;
        int postfilter_tapset = 0;
        if (start == 0 && tell + 16 <= total_bits) {
            if (dec.dec_bit_logp(1L) != 0) {
                int octave = (int)dec.dec_uint(6L);
                postfilter_pitch = (16 << octave) + dec.dec_bits(4 + octave) - 1;
                int qg = dec.dec_bits(3);
                if (dec.tell() + 2 <= total_bits) {
                    postfilter_tapset = dec.dec_icdf(CeltTables.tapset_icdf, 2);
                }
                postfilter_gain = 3072 * (qg + 1);
            }
            tell = dec.tell();
        }
        if (LM > 0 && tell + 3 <= total_bits) {
            isTransient = dec.dec_bit_logp(3L);
            tell = dec.tell();
        } else {
            isTransient = 0;
        }
        int shortBlocks = isTransient != 0 ? M : 0;
        int intra_ener = tell + 3 <= total_bits ? dec.dec_bit_logp(3L) : 0;
        QuantizeBands.unquant_coarse_energy(mode, start, end, oldBandE, intra_ener, dec, C, LM);
        int[] tf_res = new int[nbEBands];
        CeltCommon.tf_decode(start, end, isTransient, tf_res, LM, dec);
        tell = dec.tell();
        int spread_decision = 2;
        if (tell + 4 <= total_bits) {
            spread_decision = dec.dec_icdf(CeltTables.spread_icdf, 5);
        }
        int[] cap = new int[nbEBands];
        CeltCommon.init_caps(mode, cap, LM, C);
        int[] offsets = new int[nbEBands];
        int dynalloc_logp = 6;
        total_bits <<= 3;
        tell = dec.tell_frac();
        for (i = start; i < end; ++i) {
            int boost;
            int width = C * (eBands[i + 1] - eBands[i]) << LM;
            int quanta = Inlines.IMIN(width << 3, Inlines.IMAX(48, width));
            int dynalloc_loop_logp = dynalloc_logp;
            for (boost = 0; tell + (dynalloc_loop_logp << 3) < total_bits && boost < cap[i]; boost += quanta, total_bits -= quanta) {
                int flag = dec.dec_bit_logp(dynalloc_loop_logp);
                tell = dec.tell_frac();
                if (flag == 0) break;
                dynalloc_loop_logp = 1;
            }
            offsets[i] = boost;
            if (boost <= 0) continue;
            dynalloc_logp = Inlines.IMAX(2, dynalloc_logp - 1);
        }
        int[] fine_quant = new int[nbEBands];
        int alloc_trim = tell + 48 <= total_bits ? dec.dec_icdf(CeltTables.trim_icdf, 7) : 5;
        int bits = (len * 8 << 3) - dec.tell_frac() - 1;
        int anti_collapse_rsv = isTransient != 0 && LM >= 2 && bits >= LM + 2 << 3 ? 8 : 0;
        bits -= anti_collapse_rsv;
        int[] pulses = new int[nbEBands];
        int[] fine_priority = new int[nbEBands];
        BoxedValueInt boxed_intensity = new BoxedValueInt(intensity);
        BoxedValueInt boxed_dual_stereo = new BoxedValueInt(dual_stereo);
        BoxedValueInt boxed_balance = new BoxedValueInt(0);
        int codedBands = Rate.compute_allocation(mode, start, end, offsets, cap, alloc_trim, boxed_intensity, boxed_dual_stereo, bits, boxed_balance, pulses, fine_quant, fine_priority, C, LM, dec, 0, 0, 0);
        intensity = boxed_intensity.Val;
        dual_stereo = boxed_dual_stereo.Val;
        int balance = boxed_balance.Val;
        QuantizeBands.unquant_fine_energy(mode, start, end, oldBandE, fine_quant, dec, C);
        c = 0;
        do {
            Arrays.MemMove(this.decode_mem[c], N, 0, 2048 - N + overlap / 2);
        } while (++c < CC);
        short[] collapse_masks = new short[C * nbEBands];
        int[][] X = Arrays.InitTwoDimensionalArrayInt(C, N);
        BoxedValueInt boxed_rng = new BoxedValueInt(this.rng);
        Bands.quant_all_bands(0, mode, start, end, X[0], C == 2 ? X[1] : null, collapse_masks, null, pulses, shortBlocks, spread_decision, dual_stereo, intensity, tf_res, len * 64 - anti_collapse_rsv, balance, dec, LM, codedBands, boxed_rng);
        this.rng = boxed_rng.Val;
        if (anti_collapse_rsv > 0) {
            anti_collapse_on = dec.dec_bits(1);
        }
        QuantizeBands.unquant_energy_finalise(mode, start, end, oldBandE, fine_quant, fine_priority, len * 8 - dec.tell(), dec, C);
        if (anti_collapse_on != 0) {
            Bands.anti_collapse(mode, X, collapse_masks, LM, C, N, start, end, oldBandE, oldLogE, oldLogE2, pulses, this.rng);
        }
        if (silence != 0) {
            for (i = 0; i < C * nbEBands; ++i) {
                oldBandE[i] = -28672;
            }
        }
        CeltCommon.celt_synthesis(mode, X, out_syn, out_syn_ptrs, oldBandE, start, effEnd, C, CC, isTransient, LM, this.downsample, silence);
        c = 0;
        do {
            this.postfilter_period = Inlines.IMAX(this.postfilter_period, 15);
            this.postfilter_period_old = Inlines.IMAX(this.postfilter_period_old, 15);
            CeltCommon.comb_filter(out_syn[c], out_syn_ptrs[c], out_syn[c], out_syn_ptrs[c], this.postfilter_period_old, this.postfilter_period, mode.shortMdctSize, this.postfilter_gain_old, this.postfilter_gain, this.postfilter_tapset_old, this.postfilter_tapset, mode.window, overlap);
            if (LM == 0) continue;
            CeltCommon.comb_filter(out_syn[c], out_syn_ptrs[c] + mode.shortMdctSize, out_syn[c], out_syn_ptrs[c] + mode.shortMdctSize, this.postfilter_period, postfilter_pitch, N - mode.shortMdctSize, this.postfilter_gain, postfilter_gain, this.postfilter_tapset, postfilter_tapset, mode.window, overlap);
        } while (++c < CC);
        this.postfilter_period_old = this.postfilter_period;
        this.postfilter_gain_old = this.postfilter_gain;
        this.postfilter_tapset_old = this.postfilter_tapset;
        this.postfilter_period = postfilter_pitch;
        this.postfilter_gain = postfilter_gain;
        this.postfilter_tapset = postfilter_tapset;
        if (LM != 0) {
            this.postfilter_period_old = this.postfilter_period;
            this.postfilter_gain_old = this.postfilter_gain;
            this.postfilter_tapset_old = this.postfilter_tapset;
        }
        if (C == 1) {
            System.arraycopy(oldBandE, 0, oldBandE, nbEBands, nbEBands);
        }
        if (isTransient == 0) {
            System.arraycopy(oldLogE, 0, oldLogE2, 0, 2 * nbEBands);
            System.arraycopy(oldBandE, 0, oldLogE, 0, 2 * nbEBands);
            int max_background_increase = this.loss_count < 10 ? M * 1 : 1024;
            for (i = 0; i < 2 * nbEBands; ++i) {
                backgroundLogE[i] = Inlines.MIN16(backgroundLogE[i] + max_background_increase, oldBandE[i]);
            }
        } else {
            for (i = 0; i < 2 * nbEBands; ++i) {
                oldLogE[i] = Inlines.MIN16(oldLogE[i], oldBandE[i]);
            }
        }
        c = 0;
        do {
            for (i = 0; i < start; ++i) {
                oldBandE[c * nbEBands + i] = 0;
                oldLogE2[c * nbEBands + i] = -28672;
                oldLogE[c * nbEBands + i] = -28672;
            }
            for (i = end; i < nbEBands; ++i) {
                oldBandE[c * nbEBands + i] = 0;
                oldLogE2[c * nbEBands + i] = -28672;
                oldLogE[c * nbEBands + i] = -28672;
            }
        } while (++c < 2);
        this.rng = (int)dec.rng;
        CeltCommon.deemphasis(out_syn, out_syn_ptrs, pcm, pcm_ptr, N, CC, this.downsample, mode.preemph, this.preemph_memD, accum);
        this.loss_count = 0;
        if (dec.tell() > 8 * len) {
            return OpusError.OPUS_INTERNAL_ERROR;
        }
        if (dec.get_error() != 0) {
            this.error = 1;
        }
        return frame_size / this.downsample;
    }

    void SetStartBand(int value) {
        if (value < 0 || value >= this.mode.nbEBands) {
            throw new IllegalArgumentException("Start band above max number of ebands (or negative)");
        }
        this.start = value;
    }

    void SetEndBand(int value) {
        if (value < 1 || value > this.mode.nbEBands) {
            throw new IllegalArgumentException("End band above max number of ebands (or less than 1)");
        }
        this.end = value;
    }

    void SetChannels(int value) {
        if (value < 1 || value > 2) {
            throw new IllegalArgumentException("Channel count must be 1 or 2");
        }
        this.stream_channels = value;
    }

    int GetAndClearError() {
        int returnVal = this.error;
        this.error = 0;
        return returnVal;
    }

    public int GetLookahead() {
        return this.overlap / this.downsample;
    }

    public int GetPitch() {
        return this.postfilter_period;
    }

    public CeltMode GetMode() {
        return this.mode;
    }

    public void SetSignalling(int value) {
        this.signalling = value;
    }

    public int GetFinalRange() {
        return this.rng;
    }
}
