package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;
import dimasik.managers.mods.voicechat.decoder.Downmix;
import dimasik.managers.mods.voicechat.decoder.Filters;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.OpusBandwidth;
import dimasik.managers.mods.voicechat.decoder.OpusBandwidthHelpers;
import dimasik.managers.mods.voicechat.decoder.OpusFramesize;
import dimasik.managers.mods.voicechat.decoder.OpusFramesizeHelpers;
import dimasik.managers.mods.voicechat.decoder.OpusMode;
import dimasik.managers.mods.voicechat.decoder.StereoWidthState;

public class CodecHelpers {
    private static final int MAX_DYNAMIC_FRAMESIZE = 24;

    static byte gen_toc(OpusMode mode, int framerate, OpusBandwidth bandwidth, int channels) {
        int toc;
        int period = 0;
        while (framerate < 400) {
            framerate <<= 1;
            ++period;
        }
        if (mode == OpusMode.MODE_SILK_ONLY) {
            toc = (short)(OpusBandwidthHelpers.GetOrdinal(bandwidth) - OpusBandwidthHelpers.GetOrdinal(OpusBandwidth.OPUS_BANDWIDTH_NARROWBAND) << 5);
            toc = (short)(toc | (short)(period - 2 << 3));
        } else if (mode == OpusMode.MODE_CELT_ONLY) {
            int tmp = OpusBandwidthHelpers.GetOrdinal(bandwidth) - OpusBandwidthHelpers.GetOrdinal(OpusBandwidth.OPUS_BANDWIDTH_MEDIUMBAND);
            if (tmp < 0) {
                tmp = 0;
            }
            toc = 128;
            toc = (short)(toc | (short)(tmp << 5));
            toc = (short)(toc | (short)(period << 3));
        } else {
            toc = 96;
            toc = (short)(toc | (short)(OpusBandwidthHelpers.GetOrdinal(bandwidth) - OpusBandwidthHelpers.GetOrdinal(OpusBandwidth.OPUS_BANDWIDTH_SUPERWIDEBAND) << 4));
            toc = (short)(toc | (short)(period - 2 << 3));
        }
        toc = (short)(toc | (short)((channels == 2 ? 1 : 0) << 2));
        return (byte)(0xFF & toc);
    }

    static void hp_cutoff(short[] input, int input_ptr, int cutoff_Hz, short[] output, int output_ptr, int[] hp_mem, int len, int channels, int Fs) {
        int r_Q28;
        int[] B_Q28 = new int[3];
        int[] A_Q28 = new int[2];
        Inlines.OpusAssert(cutoff_Hz <= 869074);
        int Fc_Q19 = Inlines.silk_DIV32_16(Inlines.silk_SMULBB(2471, cutoff_Hz), Fs / 1000);
        Inlines.OpusAssert(Fc_Q19 > 0 && Fc_Q19 < 32768);
        B_Q28[0] = r_Q28 = 0x10000000 - Inlines.silk_MUL(471, Fc_Q19);
        B_Q28[1] = Inlines.silk_LSHIFT(-r_Q28, 1);
        B_Q28[2] = r_Q28;
        int r_Q22 = Inlines.silk_RSHIFT(r_Q28, 6);
        A_Q28[0] = Inlines.silk_SMULWW(r_Q22, Inlines.silk_SMULWW(Fc_Q19, Fc_Q19) - 0x800000);
        A_Q28[1] = Inlines.silk_SMULWW(r_Q22, r_Q22);
        Filters.silk_biquad_alt(input, input_ptr, B_Q28, A_Q28, hp_mem, 0, output, output_ptr, len, channels);
        if (channels == 2) {
            Filters.silk_biquad_alt(input, input_ptr + 1, B_Q28, A_Q28, hp_mem, 2, output, output_ptr + 1, len, channels);
        }
    }

    static void dc_reject(short[] input, int input_ptr, int cutoff_Hz, short[] output, int output_ptr, int[] hp_mem, int len, int channels, int Fs) {
        int shift = Inlines.celt_ilog2(Fs / (cutoff_Hz * 3));
        for (int c = 0; c < channels; ++c) {
            for (int i = 0; i < len; ++i) {
                int x = Inlines.SHL32(Inlines.EXTEND32(input[channels * i + c + input_ptr]), 15);
                int tmp = x - hp_mem[2 * c];
                hp_mem[2 * c] = hp_mem[2 * c] + Inlines.PSHR32(x - hp_mem[2 * c], shift);
                int y = tmp - hp_mem[2 * c + 1];
                hp_mem[2 * c + 1] = hp_mem[2 * c + 1] + Inlines.PSHR32(tmp - hp_mem[2 * c + 1], shift);
                output[channels * i + c + output_ptr] = Inlines.EXTRACT16(Inlines.SATURATE(Inlines.PSHR32(y, 15), Short.MAX_VALUE));
            }
        }
    }

    static void stereo_fade(short[] pcm_buf, int g1, int g2, int overlap48, int frame_size, int channels, int[] window, int Fs) {
        int diff;
        int i;
        int inc = 48000 / Fs;
        int overlap = overlap48 / inc;
        g1 = Short.MAX_VALUE - g1;
        g2 = Short.MAX_VALUE - g2;
        for (i = 0; i < overlap; ++i) {
            int w = Inlines.MULT16_16_Q15(window[i * inc], window[i * inc]);
            int g = Inlines.SHR32(Inlines.MAC16_16(Inlines.MULT16_16(w, g2), Short.MAX_VALUE - w, g1), 15);
            diff = Inlines.EXTRACT16(Inlines.HALF32(pcm_buf[i * channels] - pcm_buf[i * channels + 1]));
            diff = Inlines.MULT16_16_Q15(g, diff);
            pcm_buf[i * channels] = (short)(pcm_buf[i * channels] - diff);
            pcm_buf[i * channels + 1] = (short)(pcm_buf[i * channels + 1] + diff);
        }
        while (i < frame_size) {
            diff = Inlines.EXTRACT16(Inlines.HALF32(pcm_buf[i * channels] - pcm_buf[i * channels + 1]));
            diff = Inlines.MULT16_16_Q15(g2, diff);
            pcm_buf[i * channels] = (short)(pcm_buf[i * channels] - diff);
            pcm_buf[i * channels + 1] = (short)(pcm_buf[i * channels + 1] + diff);
            ++i;
        }
    }

    static void gain_fade(short[] buffer, int buf_ptr, int g1, int g2, int overlap48, int frame_size, int channels, int[] window, int Fs) {
        int i;
        int inc = 48000 / Fs;
        int overlap = overlap48 / inc;
        if (channels == 1) {
            for (i = 0; i < overlap; ++i) {
                w = Inlines.MULT16_16_Q15(window[i * inc], window[i * inc]);
                g = Inlines.SHR32(Inlines.MAC16_16(Inlines.MULT16_16(w, g2), Short.MAX_VALUE - w, g1), 15);
                buffer[buf_ptr + i] = (short)Inlines.MULT16_16_Q15(g, (int)buffer[buf_ptr + i]);
            }
        } else {
            for (i = 0; i < overlap; ++i) {
                w = Inlines.MULT16_16_Q15(window[i * inc], window[i * inc]);
                g = Inlines.SHR32(Inlines.MAC16_16(Inlines.MULT16_16(w, g2), Short.MAX_VALUE - w, g1), 15);
                buffer[buf_ptr + i * 2] = (short)Inlines.MULT16_16_Q15(g, (int)buffer[buf_ptr + i * 2]);
                buffer[buf_ptr + i * 2 + 1] = (short)Inlines.MULT16_16_Q15(g, (int)buffer[buf_ptr + i * 2 + 1]);
            }
        }
        int c = 0;
        do {
            for (i = overlap; i < frame_size; ++i) {
                buffer[buf_ptr + i * channels + c] = (short)Inlines.MULT16_16_Q15(g2, (int)buffer[buf_ptr + i * channels + c]);
            }
        } while (++c < channels);
    }

    static float transient_boost(float[] E, int E_ptr, float[] E_1, int LM, int maxM) {
        float sumE = 0.0f;
        float sumE_1 = 0.0f;
        int M = Inlines.IMIN(maxM, (1 << LM) + 1);
        for (int i = E_ptr; i < M + E_ptr; ++i) {
            sumE += E[i];
            sumE_1 += E_1[i];
        }
        float metric = sumE * sumE_1 / (float)(M * M);
        return Inlines.MIN16(1.0f, (float)Math.sqrt(Inlines.MAX16(0.0f, 0.05f * (metric - 2.0f))));
    }

    static int transient_viterbi(float[] E, float[] E_1, int N, int frame_cost, int rate) {
        int i;
        float[][] cost = Arrays.InitTwoDimensionalArrayFloat(24, 16);
        int[][] states = Arrays.InitTwoDimensionalArrayInt(24, 16);
        float factor = rate < 80 ? 0.0f : (rate > 160 ? 1.0f : ((float)rate - 80.0f) / 80.0f);
        for (i = 0; i < 16; ++i) {
            states[0][i] = -1;
            cost[0][i] = 1.0E10f;
        }
        for (i = 0; i < 4; ++i) {
            cost[0][1 << i] = (float)(frame_cost + rate * (1 << i)) * (1.0f + factor * CodecHelpers.transient_boost(E, 0, E_1, i, N + 1));
            states[0][1 << i] = i;
        }
        for (i = 1; i < N; ++i) {
            int j;
            for (j = 2; j < 16; ++j) {
                cost[i][j] = cost[i - 1][j - 1];
                states[i][j] = j - 1;
            }
            for (j = 0; j < 4; ++j) {
                states[i][1 << j] = 1;
                float min_cost = cost[i - 1][1];
                for (int k = 1; k < 4; ++k) {
                    float tmp = cost[i - 1][(1 << k + 1) - 1];
                    if (!(tmp < min_cost)) continue;
                    states[i][1 << j] = (1 << k + 1) - 1;
                    min_cost = tmp;
                }
                float curr_cost = (float)(frame_cost + rate * (1 << j)) * (1.0f + factor * CodecHelpers.transient_boost(E, i, E_1, j, N - i + 1));
                cost[i][1 << j] = min_cost;
                if (N - i < 1 << j) {
                    float[] fArray = cost[i];
                    int n = 1 << j;
                    fArray[n] = fArray[n] + curr_cost * (float)(N - i) / (float)(1 << j);
                    continue;
                }
                float[] fArray = cost[i];
                int n = 1 << j;
                fArray[n] = fArray[n] + curr_cost;
            }
        }
        int best_state = 1;
        float best_cost = cost[N - 1][1];
        for (i = 2; i < 16; ++i) {
            if (!(cost[N - 1][i] < best_cost)) continue;
            best_cost = cost[N - 1][i];
            best_state = i;
        }
        for (i = N - 1; i >= 0; --i) {
            best_state = states[i][best_state];
        }
        return best_state;
    }

    static int optimize_framesize(short[] x, int x_ptr, int len, int C, int Fs, int bitrate, int tonality, float[] mem, int buffering) {
        int i;
        int pos;
        int offset;
        float[] e = new float[28];
        float[] e_1 = new float[27];
        int bestLM = 0;
        int subframe = Fs / 400;
        int[] sub = new int[subframe];
        e[0] = mem[0];
        e_1[0] = 1.0f / (1.0f + mem[0]);
        if (buffering != 0) {
            offset = 2 * subframe - buffering;
            Inlines.OpusAssert(offset >= 0 && offset <= subframe);
            len -= offset;
            e[1] = mem[1];
            e_1[1] = 1.0f / (1.0f + mem[1]);
            e[2] = mem[2];
            e_1[2] = 1.0f / (1.0f + mem[2]);
            pos = 3;
        } else {
            pos = 1;
            offset = 0;
        }
        int N = Inlines.IMIN(len / subframe, 24);
        int memx = 0;
        for (i = 0; i < N; ++i) {
            float tmp = 1.0f;
            Downmix.downmix_int(x, x_ptr, sub, 0, subframe, i * subframe + offset, 0, -2, C);
            if (i == 0) {
                memx = sub[0];
            }
            for (int j = 0; j < subframe; ++j) {
                int tmpx = sub[j];
                tmp += (float)(tmpx - memx) * (float)(tmpx - memx);
                memx = tmpx;
            }
            e[i + pos] = tmp;
            e_1[i + pos] = 1.0f / tmp;
        }
        e[i + pos] = e[i + pos - 1];
        if (buffering != 0) {
            N = Inlines.IMIN(24, N + 2);
        }
        bestLM = CodecHelpers.transient_viterbi(e, e_1, N, (int)((1.0f + 0.5f * (float)tonality) * (float)(60 * C + 40)), bitrate / 400);
        mem[0] = e[1 << bestLM];
        if (buffering != 0) {
            mem[1] = e[(1 << bestLM) + 1];
            mem[2] = e[(1 << bestLM) + 2];
        }
        return bestLM;
    }

    static int frame_size_select(int frame_size, OpusFramesize variable_duration, int Fs) {
        int new_size;
        if (frame_size < Fs / 400) {
            return -1;
        }
        if (variable_duration == OpusFramesize.OPUS_FRAMESIZE_ARG) {
            new_size = frame_size;
        } else if (variable_duration == OpusFramesize.OPUS_FRAMESIZE_VARIABLE) {
            new_size = Fs / 50;
        } else if (OpusFramesizeHelpers.GetOrdinal(variable_duration) >= OpusFramesizeHelpers.GetOrdinal(OpusFramesize.OPUS_FRAMESIZE_2_5_MS) && OpusFramesizeHelpers.GetOrdinal(variable_duration) <= OpusFramesizeHelpers.GetOrdinal(OpusFramesize.OPUS_FRAMESIZE_60_MS)) {
            new_size = Inlines.IMIN(3 * Fs / 50, Fs / 400 << OpusFramesizeHelpers.GetOrdinal(variable_duration) - OpusFramesizeHelpers.GetOrdinal(OpusFramesize.OPUS_FRAMESIZE_2_5_MS));
        } else {
            return -1;
        }
        if (new_size > frame_size) {
            return -1;
        }
        if (400 * new_size != Fs && 200 * new_size != Fs && 100 * new_size != Fs && 50 * new_size != Fs && 25 * new_size != Fs && 50 * new_size != 3 * Fs) {
            return -1;
        }
        return new_size;
    }

    static int compute_frame_size(short[] analysis_pcm, int analysis_pcm_ptr, int frame_size, OpusFramesize variable_duration, int C, int Fs, int bitrate_bps, int delay_compensation, float[] subframe_mem, boolean analysis_enabled) {
        if (analysis_enabled && variable_duration == OpusFramesize.OPUS_FRAMESIZE_VARIABLE && frame_size >= Fs / 200) {
            int LM = 3;
            LM = CodecHelpers.optimize_framesize(analysis_pcm, analysis_pcm_ptr, frame_size, C, Fs, bitrate_bps, 0, subframe_mem, delay_compensation);
            while (Fs / 400 << LM > frame_size) {
                --LM;
            }
            frame_size = Fs / 400 << LM;
        } else {
            frame_size = CodecHelpers.frame_size_select(frame_size, variable_duration, Fs);
        }
        if (frame_size < 0) {
            return -1;
        }
        return frame_size;
    }

    static int compute_stereo_width(short[] pcm, int pcm_ptr, int frame_size, int Fs, StereoWidthState mem) {
        int frame_rate = Fs / frame_size;
        int short_alpha = Short.MAX_VALUE - 819175 / Inlines.IMAX(50, frame_rate);
        int yy = 0;
        int xy = 0;
        int xx = 0;
        for (int i = 0; i < frame_size - 3; i += 4) {
            int pxx = 0;
            int pxy = 0;
            int pyy = 0;
            int p2i = pcm_ptr + 2 * i;
            short x = pcm[p2i];
            short y = pcm[p2i + 1];
            pxx = Inlines.SHR32(Inlines.MULT16_16((int)x, (int)x), 2);
            pxy = Inlines.SHR32(Inlines.MULT16_16((int)x, (int)y), 2);
            pyy = Inlines.SHR32(Inlines.MULT16_16((int)y, (int)y), 2);
            x = pcm[p2i + 2];
            y = pcm[p2i + 3];
            pxx += Inlines.SHR32(Inlines.MULT16_16((int)x, (int)x), 2);
            pxy += Inlines.SHR32(Inlines.MULT16_16((int)x, (int)y), 2);
            pyy += Inlines.SHR32(Inlines.MULT16_16((int)y, (int)y), 2);
            x = pcm[p2i + 4];
            y = pcm[p2i + 5];
            pxx += Inlines.SHR32(Inlines.MULT16_16((int)x, (int)x), 2);
            pxy += Inlines.SHR32(Inlines.MULT16_16((int)x, (int)y), 2);
            pyy += Inlines.SHR32(Inlines.MULT16_16((int)y, (int)y), 2);
            x = pcm[p2i + 6];
            y = pcm[p2i + 7];
            xx += Inlines.SHR32(pxx += Inlines.SHR32(Inlines.MULT16_16((int)x, (int)x), 2), 10);
            xy += Inlines.SHR32(pxy += Inlines.SHR32(Inlines.MULT16_16((int)x, (int)y), 2), 10);
            yy += Inlines.SHR32(pyy += Inlines.SHR32(Inlines.MULT16_16((int)y, (int)y), 2), 10);
        }
        mem.XX += Inlines.MULT16_32_Q15(short_alpha, xx - mem.XX);
        mem.XY += Inlines.MULT16_32_Q15(short_alpha, xy - mem.XY);
        mem.YY += Inlines.MULT16_32_Q15(short_alpha, yy - mem.YY);
        mem.XX = Inlines.MAX32(0, mem.XX);
        mem.XY = Inlines.MAX32(0, mem.XY);
        mem.YY = Inlines.MAX32(0, mem.YY);
        if (Inlines.MAX32(mem.XX, mem.YY) > 210) {
            int sqrt_xx = Inlines.celt_sqrt(mem.XX);
            int sqrt_yy = Inlines.celt_sqrt(mem.YY);
            int qrrt_xx = Inlines.celt_sqrt(sqrt_xx);
            int qrrt_yy = Inlines.celt_sqrt(sqrt_yy);
            mem.XY = Inlines.MIN32(mem.XY, sqrt_xx * sqrt_yy);
            int corr = Inlines.SHR32(Inlines.frac_div32(mem.XY, 1 + Inlines.MULT16_16(sqrt_xx, sqrt_yy)), 16);
            int ldiff = Short.MAX_VALUE * Inlines.ABS16(qrrt_xx - qrrt_yy) / (1 + qrrt_xx + qrrt_yy);
            int width = Inlines.MULT16_16_Q15(Inlines.celt_sqrt(0x40000000 - Inlines.MULT16_16(corr, corr)), ldiff);
            mem.smoothed_width += (width - mem.smoothed_width) / frame_rate;
            mem.max_follower = Inlines.MAX16(mem.max_follower - 655 / frame_rate, mem.smoothed_width);
        } else {
            boolean width = false;
            int corr = Short.MAX_VALUE;
            boolean ldiff = false;
        }
        return Inlines.EXTRACT16(Inlines.MIN32(Short.MAX_VALUE, 20 * mem.max_follower));
    }

    static void smooth_fade(short[] in1, int in1_ptr, short[] in2, int in2_ptr, short[] output, int output_ptr, int overlap, int channels, int[] window, int Fs) {
        int inc = 48000 / Fs;
        for (int c = 0; c < channels; ++c) {
            for (int i = 0; i < overlap; ++i) {
                int w = Inlines.MULT16_16_Q15(window[i * inc], window[i * inc]);
                output[output_ptr + i * channels + c] = (short)Inlines.SHR32(Inlines.MAC16_16(Inlines.MULT16_16(w, (int)in2[in2_ptr + i * channels + c]), Short.MAX_VALUE - w, (int)in1[in1_ptr + i * channels + c]), 15);
            }
        }
    }

    public static String opus_strerror(int error) {
        String[] error_strings = new String[]{"success", "invalid argument", "buffer too small", "error", "corrupted stream", "request not implemented", "invalid state", "memory allocation failed"};
        if (error > 0 || error < -7) {
            return "unknown error";
        }
        return error_strings[-error];
    }

    public static String GetVersionString() {
        return "concentus 1.0a-java-fixed";
    }
}
