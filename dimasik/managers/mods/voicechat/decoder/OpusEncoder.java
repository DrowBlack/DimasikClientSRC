package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Analysis;
import dimasik.managers.mods.voicechat.decoder.AnalysisInfo;
import dimasik.managers.mods.voicechat.decoder.Arrays;
import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.CeltEncoder;
import dimasik.managers.mods.voicechat.decoder.CeltMode;
import dimasik.managers.mods.voicechat.decoder.CodecHelpers;
import dimasik.managers.mods.voicechat.decoder.EncControlState;
import dimasik.managers.mods.voicechat.decoder.EncodeAPI;
import dimasik.managers.mods.voicechat.decoder.EntropyCoder;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.OpusApplication;
import dimasik.managers.mods.voicechat.decoder.OpusBandwidth;
import dimasik.managers.mods.voicechat.decoder.OpusBandwidthHelpers;
import dimasik.managers.mods.voicechat.decoder.OpusError;
import dimasik.managers.mods.voicechat.decoder.OpusException;
import dimasik.managers.mods.voicechat.decoder.OpusFramesize;
import dimasik.managers.mods.voicechat.decoder.OpusMode;
import dimasik.managers.mods.voicechat.decoder.OpusRepacketizer;
import dimasik.managers.mods.voicechat.decoder.OpusSignal;
import dimasik.managers.mods.voicechat.decoder.OpusTables;
import dimasik.managers.mods.voicechat.decoder.SilkEncoder;
import dimasik.managers.mods.voicechat.decoder.StereoWidthState;
import dimasik.managers.mods.voicechat.decoder.TonalityAnalysisState;

public class OpusEncoder {
    final EncControlState silk_mode = new EncControlState();
    OpusApplication application;
    int channels;
    int delay_compensation;
    int force_channels;
    OpusSignal signal_type;
    OpusBandwidth user_bandwidth;
    OpusBandwidth max_bandwidth;
    OpusMode user_forced_mode;
    int voice_ratio;
    int Fs;
    int use_vbr;
    int vbr_constraint;
    OpusFramesize variable_duration;
    int bitrate_bps;
    int user_bitrate_bps;
    int lsb_depth;
    int encoder_buffer;
    int lfe;
    final TonalityAnalysisState analysis = new TonalityAnalysisState();
    int stream_channels;
    short hybrid_stereo_width_Q14;
    int variable_HP_smth2_Q15;
    int prev_HB_gain;
    final int[] hp_mem = new int[4];
    OpusMode mode;
    OpusMode prev_mode;
    int prev_channels;
    int prev_framesize;
    OpusBandwidth bandwidth;
    int silk_bw_switch;
    int first;
    int[] energy_masking;
    final StereoWidthState width_mem = new StereoWidthState();
    final short[] delay_buffer = new short[960];
    OpusBandwidth detected_bandwidth;
    int rangeFinal;
    final SilkEncoder SilkEncoder = new SilkEncoder();
    final CeltEncoder Celt_Encoder = new CeltEncoder();

    OpusEncoder() {
    }

    void reset() {
        this.silk_mode.Reset();
        this.application = OpusApplication.OPUS_APPLICATION_UNIMPLEMENTED;
        this.channels = 0;
        this.delay_compensation = 0;
        this.force_channels = 0;
        this.signal_type = OpusSignal.OPUS_SIGNAL_UNKNOWN;
        this.user_bandwidth = OpusBandwidth.OPUS_BANDWIDTH_UNKNOWN;
        this.max_bandwidth = OpusBandwidth.OPUS_BANDWIDTH_UNKNOWN;
        this.user_forced_mode = OpusMode.MODE_UNKNOWN;
        this.voice_ratio = 0;
        this.Fs = 0;
        this.use_vbr = 0;
        this.vbr_constraint = 0;
        this.variable_duration = OpusFramesize.OPUS_FRAMESIZE_UNKNOWN;
        this.bitrate_bps = 0;
        this.user_bitrate_bps = 0;
        this.lsb_depth = 0;
        this.encoder_buffer = 0;
        this.lfe = 0;
        this.analysis.Reset();
        this.PartialReset();
    }

    void PartialReset() {
        this.stream_channels = 0;
        this.hybrid_stereo_width_Q14 = 0;
        this.variable_HP_smth2_Q15 = 0;
        this.prev_HB_gain = 0;
        Arrays.MemSet(this.hp_mem, 0, 4);
        this.mode = OpusMode.MODE_UNKNOWN;
        this.prev_mode = OpusMode.MODE_UNKNOWN;
        this.prev_channels = 0;
        this.prev_framesize = 0;
        this.bandwidth = OpusBandwidth.OPUS_BANDWIDTH_UNKNOWN;
        this.silk_bw_switch = 0;
        this.first = 0;
        this.energy_masking = null;
        this.width_mem.Reset();
        Arrays.MemSet(this.delay_buffer, (short)0, 960);
        this.detected_bandwidth = OpusBandwidth.OPUS_BANDWIDTH_UNKNOWN;
        this.rangeFinal = 0;
    }

    public void resetState() {
        EncControlState dummy = new EncControlState();
        this.analysis.Reset();
        this.PartialReset();
        this.Celt_Encoder.ResetState();
        EncodeAPI.silk_InitEncoder(this.SilkEncoder, dummy);
        this.stream_channels = this.channels;
        this.hybrid_stereo_width_Q14 = (short)16384;
        this.prev_HB_gain = Short.MAX_VALUE;
        this.first = 1;
        this.mode = OpusMode.MODE_HYBRID;
        this.bandwidth = OpusBandwidth.OPUS_BANDWIDTH_FULLBAND;
        this.variable_HP_smth2_Q15 = Inlines.silk_LSHIFT(Inlines.silk_lin2log(60), 8);
    }

    public OpusEncoder(int Fs, int channels, OpusApplication application) throws OpusException {
        if (Fs != 48000 && Fs != 24000 && Fs != 16000 && Fs != 12000 && Fs != 8000) {
            throw new IllegalArgumentException("Sample rate is invalid (must be 8/12/16/24/48 Khz)");
        }
        if (channels != 1 && channels != 2) {
            throw new IllegalArgumentException("Number of channels must be 1 or 2");
        }
        int ret = this.opus_init_encoder(Fs, channels, application);
        if (ret != OpusError.OPUS_OK) {
            if (ret == OpusError.OPUS_BAD_ARG) {
                throw new IllegalArgumentException("OPUS_BAD_ARG when creating encoder");
            }
            throw new OpusException("Error while initializing encoder", ret);
        }
    }

    int opus_init_encoder(int Fs, int channels, OpusApplication application) {
        if (Fs != 48000 && Fs != 24000 && Fs != 16000 && Fs != 12000 && Fs != 8000 || channels != 1 && channels != 2 || application == OpusApplication.OPUS_APPLICATION_UNIMPLEMENTED) {
            return OpusError.OPUS_BAD_ARG;
        }
        this.reset();
        SilkEncoder silk_enc = this.SilkEncoder;
        CeltEncoder celt_enc = this.Celt_Encoder;
        this.stream_channels = this.channels = channels;
        this.Fs = Fs;
        int ret = EncodeAPI.silk_InitEncoder(silk_enc, this.silk_mode);
        if (ret != 0) {
            return OpusError.OPUS_INTERNAL_ERROR;
        }
        this.silk_mode.nChannelsAPI = channels;
        this.silk_mode.nChannelsInternal = channels;
        this.silk_mode.API_sampleRate = this.Fs;
        this.silk_mode.maxInternalSampleRate = 16000;
        this.silk_mode.minInternalSampleRate = 8000;
        this.silk_mode.desiredInternalSampleRate = 16000;
        this.silk_mode.payloadSize_ms = 20;
        this.silk_mode.bitRate = 25000;
        this.silk_mode.packetLossPercentage = 0;
        this.silk_mode.complexity = 9;
        this.silk_mode.useInBandFEC = 0;
        this.silk_mode.useDTX = 0;
        this.silk_mode.useCBR = 0;
        this.silk_mode.reducedDependency = 0;
        int err = celt_enc.celt_encoder_init(Fs, channels);
        if (err != OpusError.OPUS_OK) {
            return OpusError.OPUS_INTERNAL_ERROR;
        }
        celt_enc.SetSignalling(0);
        celt_enc.SetComplexity(this.silk_mode.complexity);
        this.use_vbr = 1;
        this.vbr_constraint = 1;
        this.user_bitrate_bps = -1000;
        this.bitrate_bps = 3000 + Fs * channels;
        this.application = application;
        this.signal_type = OpusSignal.OPUS_SIGNAL_AUTO;
        this.user_bandwidth = OpusBandwidth.OPUS_BANDWIDTH_AUTO;
        this.max_bandwidth = OpusBandwidth.OPUS_BANDWIDTH_FULLBAND;
        this.force_channels = -1000;
        this.user_forced_mode = OpusMode.MODE_AUTO;
        this.voice_ratio = -1;
        this.encoder_buffer = this.Fs / 100;
        this.lsb_depth = 24;
        this.variable_duration = OpusFramesize.OPUS_FRAMESIZE_ARG;
        this.delay_compensation = this.Fs / 250;
        this.hybrid_stereo_width_Q14 = (short)16384;
        this.prev_HB_gain = Short.MAX_VALUE;
        this.variable_HP_smth2_Q15 = Inlines.silk_LSHIFT(Inlines.silk_lin2log(60), 8);
        this.first = 1;
        this.mode = OpusMode.MODE_HYBRID;
        this.bandwidth = OpusBandwidth.OPUS_BANDWIDTH_FULLBAND;
        Analysis.tonality_analysis_init(this.analysis);
        return OpusError.OPUS_OK;
    }

    int user_bitrate_to_bitrate(int frame_size, int max_data_bytes) {
        if (frame_size == 0) {
            frame_size = this.Fs / 400;
        }
        if (this.user_bitrate_bps == -1000) {
            return 60 * this.Fs / frame_size + this.Fs * this.channels;
        }
        if (this.user_bitrate_bps == -1) {
            return max_data_bytes * 8 * this.Fs / frame_size;
        }
        return this.user_bitrate_bps;
    }

    int opus_encode_native(short[] pcm, int pcm_ptr, int frame_size, byte[] data, int data_ptr, int out_data_bytes, int lsb_depth, short[] analysis_pcm, int analysis_pcm_ptr, int analysis_size, int c1, int c2, int analysis_channels, int float_api) {
        int nb_compr_bytes;
        int i;
        int voice_est;
        int ret = 0;
        EntropyCoder enc = new EntropyCoder();
        boolean prefill = false;
        int start_band = 0;
        int redundancy = 0;
        int redundancy_bytes = 0;
        int celt_to_silk = 0;
        boolean to_celt = false;
        int redundant_rng = 0;
        AnalysisInfo analysis_info = new AnalysisInfo();
        int analysis_read_pos_bak = -1;
        int analysis_read_subframe_bak = -1;
        int max_data_bytes = Inlines.IMIN(1276, out_data_bytes);
        this.rangeFinal = 0;
        if (this.variable_duration == OpusFramesize.OPUS_FRAMESIZE_UNKNOWN && 400 * frame_size != this.Fs && 200 * frame_size != this.Fs && 100 * frame_size != this.Fs && 50 * frame_size != this.Fs && 25 * frame_size != this.Fs && 50 * frame_size != 3 * this.Fs || 400 * frame_size < this.Fs || max_data_bytes <= 0) {
            return OpusError.OPUS_BAD_ARG;
        }
        SilkEncoder silk_enc = this.SilkEncoder;
        CeltEncoder celt_enc = this.Celt_Encoder;
        int delay_compensation = this.application == OpusApplication.OPUS_APPLICATION_RESTRICTED_LOWDELAY ? 0 : this.delay_compensation;
        lsb_depth = Inlines.IMIN(lsb_depth, this.lsb_depth);
        CeltMode celt_mode = celt_enc.GetMode();
        this.voice_ratio = -1;
        if (this.analysis.enabled) {
            analysis_info.valid = 0;
            if (this.silk_mode.complexity >= 7 && this.Fs == 48000) {
                analysis_read_pos_bak = this.analysis.read_pos;
                analysis_read_subframe_bak = this.analysis.read_subframe;
                Analysis.run_analysis(this.analysis, celt_mode, (short[])(analysis_pcm != null ? analysis_pcm : null), analysis_pcm_ptr, analysis_size, frame_size, c1, c2, analysis_channels, this.Fs, lsb_depth, analysis_info);
            }
            this.detected_bandwidth = OpusBandwidth.OPUS_BANDWIDTH_UNKNOWN;
            if (analysis_info.valid != 0) {
                int analysis_bandwidth;
                if (this.signal_type == OpusSignal.OPUS_SIGNAL_AUTO) {
                    this.voice_ratio = (int)Math.floor(0.5f + 100.0f * (1.0f - analysis_info.music_prob));
                }
                this.detected_bandwidth = (analysis_bandwidth = analysis_info.bandwidth) <= 12 ? OpusBandwidth.OPUS_BANDWIDTH_NARROWBAND : (analysis_bandwidth <= 14 ? OpusBandwidth.OPUS_BANDWIDTH_MEDIUMBAND : (analysis_bandwidth <= 16 ? OpusBandwidth.OPUS_BANDWIDTH_WIDEBAND : (analysis_bandwidth <= 18 ? OpusBandwidth.OPUS_BANDWIDTH_SUPERWIDEBAND : OpusBandwidth.OPUS_BANDWIDTH_FULLBAND)));
            }
        }
        int stereo_width = this.channels == 2 && this.force_channels != 1 ? CodecHelpers.compute_stereo_width(pcm, pcm_ptr, frame_size, this.Fs, this.width_mem) : 0;
        int total_buffer = delay_compensation;
        this.bitrate_bps = this.user_bitrate_to_bitrate(frame_size, max_data_bytes);
        int frame_rate = this.Fs / frame_size;
        if (this.use_vbr == 0) {
            int frame_rate3 = 3 * this.Fs / frame_size;
            int cbrBytes = Inlines.IMIN((3 * this.bitrate_bps / 8 + frame_rate3 / 2) / frame_rate3, max_data_bytes);
            this.bitrate_bps = cbrBytes * frame_rate3 * 8 / 3;
            max_data_bytes = cbrBytes;
        }
        if (max_data_bytes < 3 || this.bitrate_bps < 3 * frame_rate * 8 || frame_rate < 50 && (max_data_bytes * frame_rate < 300 || this.bitrate_bps < 2400)) {
            OpusBandwidth bw;
            OpusMode tocmode = this.mode;
            OpusBandwidth opusBandwidth = bw = this.bandwidth == OpusBandwidth.OPUS_BANDWIDTH_UNKNOWN ? OpusBandwidth.OPUS_BANDWIDTH_NARROWBAND : this.bandwidth;
            if (tocmode == OpusMode.MODE_UNKNOWN) {
                tocmode = OpusMode.MODE_SILK_ONLY;
            }
            if (frame_rate > 100) {
                tocmode = OpusMode.MODE_CELT_ONLY;
            }
            if (frame_rate < 50) {
                tocmode = OpusMode.MODE_SILK_ONLY;
            }
            if (tocmode == OpusMode.MODE_SILK_ONLY && OpusBandwidthHelpers.GetOrdinal(bw) > OpusBandwidthHelpers.GetOrdinal(OpusBandwidth.OPUS_BANDWIDTH_WIDEBAND)) {
                bw = OpusBandwidth.OPUS_BANDWIDTH_WIDEBAND;
            } else if (tocmode == OpusMode.MODE_CELT_ONLY && OpusBandwidthHelpers.GetOrdinal(bw) == OpusBandwidthHelpers.GetOrdinal(OpusBandwidth.OPUS_BANDWIDTH_MEDIUMBAND)) {
                bw = OpusBandwidth.OPUS_BANDWIDTH_NARROWBAND;
            } else if (tocmode == OpusMode.MODE_HYBRID && OpusBandwidthHelpers.GetOrdinal(bw) <= OpusBandwidthHelpers.GetOrdinal(OpusBandwidth.OPUS_BANDWIDTH_SUPERWIDEBAND)) {
                bw = OpusBandwidth.OPUS_BANDWIDTH_SUPERWIDEBAND;
            }
            data[data_ptr] = CodecHelpers.gen_toc(tocmode, frame_rate, bw, this.stream_channels);
            ret = 1;
            if (this.use_vbr == 0 && (ret = OpusRepacketizer.padPacket(data, data_ptr, ret, max_data_bytes)) == OpusError.OPUS_OK) {
                ret = max_data_bytes;
            }
            return ret;
        }
        int max_rate = frame_rate * max_data_bytes * 8;
        int equiv_rate = this.bitrate_bps - (40 * this.channels + 20) * (this.Fs / frame_size - 50);
        if (this.signal_type == OpusSignal.OPUS_SIGNAL_VOICE) {
            voice_est = 127;
        } else if (this.signal_type == OpusSignal.OPUS_SIGNAL_MUSIC) {
            voice_est = 0;
        } else if (this.voice_ratio >= 0) {
            voice_est = this.voice_ratio * 327 >> 8;
            if (this.application == OpusApplication.OPUS_APPLICATION_AUDIO) {
                voice_est = Inlines.IMIN(voice_est, 115);
            }
        } else {
            voice_est = this.application == OpusApplication.OPUS_APPLICATION_VOIP ? 115 : 48;
        }
        if (this.force_channels != -1000 && this.channels == 2) {
            this.stream_channels = this.force_channels;
        } else if (this.channels == 2) {
            int stereo_threshold = 30000 + (voice_est * voice_est * 0 >> 14);
            stereo_threshold = this.stream_channels == 2 ? (stereo_threshold -= 1000) : (stereo_threshold += 1000);
            this.stream_channels = equiv_rate > stereo_threshold ? 2 : 1;
        } else {
            this.stream_channels = this.channels;
        }
        equiv_rate = this.bitrate_bps - (40 * this.stream_channels + 20) * (this.Fs / frame_size - 50);
        if (this.application == OpusApplication.OPUS_APPLICATION_RESTRICTED_LOWDELAY) {
            this.mode = OpusMode.MODE_CELT_ONLY;
        } else if (this.user_forced_mode == OpusMode.MODE_AUTO) {
            int mode_voice = Inlines.MULT16_32_Q15(Short.MAX_VALUE - stereo_width, OpusTables.mode_thresholds[0][0]) + Inlines.MULT16_32_Q15(stereo_width, OpusTables.mode_thresholds[1][0]);
            int mode_music = Inlines.MULT16_32_Q15(Short.MAX_VALUE - stereo_width, OpusTables.mode_thresholds[1][1]) + Inlines.MULT16_32_Q15(stereo_width, OpusTables.mode_thresholds[1][1]);
            int threshold = mode_music + (voice_est * voice_est * (mode_voice - mode_music) >> 14);
            if (this.application == OpusApplication.OPUS_APPLICATION_VOIP) {
                threshold += 8000;
            }
            if (this.prev_mode == OpusMode.MODE_CELT_ONLY) {
                threshold -= 4000;
            } else if (this.prev_mode != OpusMode.MODE_AUTO && this.prev_mode != OpusMode.MODE_UNKNOWN) {
                threshold += 4000;
            }
            OpusMode opusMode = this.mode = equiv_rate >= threshold ? OpusMode.MODE_CELT_ONLY : OpusMode.MODE_SILK_ONLY;
            if (this.silk_mode.useInBandFEC != 0 && this.silk_mode.packetLossPercentage > 128 - voice_est >> 4) {
                this.mode = OpusMode.MODE_SILK_ONLY;
            }
            if (this.silk_mode.useDTX != 0 && voice_est > 100) {
                this.mode = OpusMode.MODE_SILK_ONLY;
            }
        } else {
            this.mode = this.user_forced_mode;
        }
        if (this.mode != OpusMode.MODE_CELT_ONLY && frame_size < this.Fs / 100) {
            this.mode = OpusMode.MODE_CELT_ONLY;
        }
        if (this.lfe != 0) {
            this.mode = OpusMode.MODE_CELT_ONLY;
        }
        if (max_data_bytes < (frame_rate > 50 ? 12000 : 8000) * frame_size / (this.Fs * 8)) {
            this.mode = OpusMode.MODE_CELT_ONLY;
        }
        if (this.stream_channels == 1 && this.prev_channels == 2 && this.silk_mode.toMono == 0 && this.mode != OpusMode.MODE_CELT_ONLY && this.prev_mode != OpusMode.MODE_CELT_ONLY) {
            this.silk_mode.toMono = 1;
            this.stream_channels = 2;
        } else {
            this.silk_mode.toMono = 0;
        }
        if (this.prev_mode != OpusMode.MODE_AUTO && this.prev_mode != OpusMode.MODE_UNKNOWN && (this.mode != OpusMode.MODE_CELT_ONLY && this.prev_mode == OpusMode.MODE_CELT_ONLY || this.mode == OpusMode.MODE_CELT_ONLY && this.prev_mode != OpusMode.MODE_CELT_ONLY)) {
            redundancy = 1;
            int n = celt_to_silk = this.mode != OpusMode.MODE_CELT_ONLY ? 1 : 0;
            if (celt_to_silk == 0) {
                if (frame_size >= this.Fs / 100) {
                    this.mode = this.prev_mode;
                    to_celt = true;
                } else {
                    redundancy = 0;
                }
            }
        }
        if (this.silk_bw_switch != 0) {
            redundancy = 1;
            celt_to_silk = 1;
            this.silk_bw_switch = 0;
            prefill = true;
        }
        if (redundancy != 0) {
            redundancy_bytes = Inlines.IMIN(257, max_data_bytes * (this.Fs / 200) / (frame_size + this.Fs / 200));
            if (this.use_vbr != 0) {
                redundancy_bytes = Inlines.IMIN(redundancy_bytes, this.bitrate_bps / 1600);
            }
        }
        if (this.mode != OpusMode.MODE_CELT_ONLY && this.prev_mode == OpusMode.MODE_CELT_ONLY) {
            EncControlState dummy = new EncControlState();
            EncodeAPI.silk_InitEncoder(silk_enc, dummy);
            prefill = true;
        }
        if (this.mode == OpusMode.MODE_CELT_ONLY || this.first != 0 || this.silk_mode.allowBandwidthSwitch != 0) {
            int threshold;
            int[] music_bandwidth_thresholds;
            int[] voice_bandwidth_thresholds;
            int[] bandwidth_thresholds = new int[8];
            OpusBandwidth bandwidth = OpusBandwidth.OPUS_BANDWIDTH_FULLBAND;
            int equiv_rate2 = equiv_rate;
            if (this.mode != OpusMode.MODE_CELT_ONLY) {
                equiv_rate2 = equiv_rate2 * (45 + this.silk_mode.complexity) / 50;
                if (this.use_vbr == 0) {
                    equiv_rate2 -= 1000;
                }
            }
            if (this.channels == 2 && this.force_channels != 1) {
                voice_bandwidth_thresholds = OpusTables.stereo_voice_bandwidth_thresholds;
                music_bandwidth_thresholds = OpusTables.stereo_music_bandwidth_thresholds;
            } else {
                voice_bandwidth_thresholds = OpusTables.mono_voice_bandwidth_thresholds;
                music_bandwidth_thresholds = OpusTables.mono_music_bandwidth_thresholds;
            }
            for (i = 0; i < 8; ++i) {
                bandwidth_thresholds[i] = music_bandwidth_thresholds[i] + (voice_est * voice_est * (voice_bandwidth_thresholds[i] - music_bandwidth_thresholds[i]) >> 14);
            }
            do {
                threshold = bandwidth_thresholds[2 * (OpusBandwidthHelpers.GetOrdinal(bandwidth) - OpusBandwidthHelpers.GetOrdinal(OpusBandwidth.OPUS_BANDWIDTH_MEDIUMBAND))];
                int hysteresis = bandwidth_thresholds[2 * (OpusBandwidthHelpers.GetOrdinal(bandwidth) - OpusBandwidthHelpers.GetOrdinal(OpusBandwidth.OPUS_BANDWIDTH_MEDIUMBAND)) + 1];
                if (this.first != 0) continue;
                if (OpusBandwidthHelpers.GetOrdinal(this.bandwidth) >= OpusBandwidthHelpers.GetOrdinal(bandwidth)) {
                    threshold -= hysteresis;
                    continue;
                }
                threshold += hysteresis;
            } while (equiv_rate2 < threshold && OpusBandwidthHelpers.GetOrdinal(bandwidth = OpusBandwidthHelpers.SUBTRACT(bandwidth, 1)) > OpusBandwidthHelpers.GetOrdinal(OpusBandwidth.OPUS_BANDWIDTH_NARROWBAND));
            this.bandwidth = bandwidth;
            if (this.first == 0 && this.mode != OpusMode.MODE_CELT_ONLY && this.silk_mode.inWBmodeWithoutVariableLP == 0 && OpusBandwidthHelpers.GetOrdinal(this.bandwidth) > OpusBandwidthHelpers.GetOrdinal(OpusBandwidth.OPUS_BANDWIDTH_WIDEBAND)) {
                this.bandwidth = OpusBandwidth.OPUS_BANDWIDTH_WIDEBAND;
            }
        }
        if (OpusBandwidthHelpers.GetOrdinal(this.bandwidth) > OpusBandwidthHelpers.GetOrdinal(this.max_bandwidth)) {
            this.bandwidth = this.max_bandwidth;
        }
        if (this.user_bandwidth != OpusBandwidth.OPUS_BANDWIDTH_AUTO) {
            this.bandwidth = this.user_bandwidth;
        }
        if (this.mode != OpusMode.MODE_CELT_ONLY && max_rate < 15000) {
            this.bandwidth = OpusBandwidthHelpers.MIN(this.bandwidth, OpusBandwidth.OPUS_BANDWIDTH_WIDEBAND);
        }
        if (this.Fs <= 24000 && OpusBandwidthHelpers.GetOrdinal(this.bandwidth) > OpusBandwidthHelpers.GetOrdinal(OpusBandwidth.OPUS_BANDWIDTH_SUPERWIDEBAND)) {
            this.bandwidth = OpusBandwidth.OPUS_BANDWIDTH_SUPERWIDEBAND;
        }
        if (this.Fs <= 16000 && OpusBandwidthHelpers.GetOrdinal(this.bandwidth) > OpusBandwidthHelpers.GetOrdinal(OpusBandwidth.OPUS_BANDWIDTH_WIDEBAND)) {
            this.bandwidth = OpusBandwidth.OPUS_BANDWIDTH_WIDEBAND;
        }
        if (this.Fs <= 12000 && OpusBandwidthHelpers.GetOrdinal(this.bandwidth) > OpusBandwidthHelpers.GetOrdinal(OpusBandwidth.OPUS_BANDWIDTH_MEDIUMBAND)) {
            this.bandwidth = OpusBandwidth.OPUS_BANDWIDTH_MEDIUMBAND;
        }
        if (this.Fs <= 8000 && OpusBandwidthHelpers.GetOrdinal(this.bandwidth) > OpusBandwidthHelpers.GetOrdinal(OpusBandwidth.OPUS_BANDWIDTH_NARROWBAND)) {
            this.bandwidth = OpusBandwidth.OPUS_BANDWIDTH_NARROWBAND;
        }
        if (this.detected_bandwidth != OpusBandwidth.OPUS_BANDWIDTH_UNKNOWN && this.user_bandwidth == OpusBandwidth.OPUS_BANDWIDTH_AUTO) {
            OpusBandwidth min_detected_bandwidth = equiv_rate <= 18000 * this.stream_channels && this.mode == OpusMode.MODE_CELT_ONLY ? OpusBandwidth.OPUS_BANDWIDTH_NARROWBAND : (equiv_rate <= 24000 * this.stream_channels && this.mode == OpusMode.MODE_CELT_ONLY ? OpusBandwidth.OPUS_BANDWIDTH_MEDIUMBAND : (equiv_rate <= 30000 * this.stream_channels ? OpusBandwidth.OPUS_BANDWIDTH_WIDEBAND : (equiv_rate <= 44000 * this.stream_channels ? OpusBandwidth.OPUS_BANDWIDTH_SUPERWIDEBAND : OpusBandwidth.OPUS_BANDWIDTH_FULLBAND)));
            this.detected_bandwidth = OpusBandwidthHelpers.MAX(this.detected_bandwidth, min_detected_bandwidth);
            this.bandwidth = OpusBandwidthHelpers.MIN(this.bandwidth, this.detected_bandwidth);
        }
        celt_enc.SetLSBDepth(lsb_depth);
        if (this.mode == OpusMode.MODE_CELT_ONLY && this.bandwidth == OpusBandwidth.OPUS_BANDWIDTH_MEDIUMBAND) {
            this.bandwidth = OpusBandwidth.OPUS_BANDWIDTH_WIDEBAND;
        }
        if (this.lfe != 0) {
            this.bandwidth = OpusBandwidth.OPUS_BANDWIDTH_NARROWBAND;
        }
        if (frame_size > this.Fs / 50 && (this.mode == OpusMode.MODE_CELT_ONLY || OpusBandwidthHelpers.GetOrdinal(this.bandwidth) > OpusBandwidthHelpers.GetOrdinal(OpusBandwidth.OPUS_BANDWIDTH_WIDEBAND))) {
            if (this.analysis.enabled && analysis_read_pos_bak != -1) {
                this.analysis.read_pos = analysis_read_pos_bak;
                this.analysis.read_subframe = analysis_read_subframe_bak;
            }
            int nb_frames = frame_size > this.Fs / 25 ? 3 : 2;
            int bytes_per_frame = Inlines.IMIN(1276, (out_data_bytes - 3) / nb_frames);
            byte[] tmp_data = new byte[nb_frames * bytes_per_frame];
            OpusRepacketizer rp = new OpusRepacketizer();
            OpusMode bak_mode = this.user_forced_mode;
            OpusBandwidth bak_bandwidth = this.user_bandwidth;
            int bak_channels = this.force_channels;
            this.user_forced_mode = this.mode;
            this.user_bandwidth = this.bandwidth;
            this.force_channels = this.stream_channels;
            int bak_to_mono = this.silk_mode.toMono;
            if (bak_to_mono != 0) {
                this.force_channels = 1;
            } else {
                this.prev_channels = this.stream_channels;
            }
            for (i = 0; i < nb_frames; ++i) {
                int tmp_len;
                this.silk_mode.toMono = 0;
                if (to_celt && i == nb_frames - 1) {
                    this.user_forced_mode = OpusMode.MODE_CELT_ONLY;
                }
                if ((tmp_len = this.opus_encode_native(pcm, pcm_ptr + i * (this.channels * this.Fs / 50), this.Fs / 50, tmp_data, i * bytes_per_frame, bytes_per_frame, lsb_depth, null, 0, 0, c1, c2, analysis_channels, float_api)) < 0) {
                    return OpusError.OPUS_INTERNAL_ERROR;
                }
                ret = rp.addPacket(tmp_data, i * bytes_per_frame, tmp_len);
                if (ret >= 0) continue;
                return OpusError.OPUS_INTERNAL_ERROR;
            }
            int repacketize_len = this.use_vbr != 0 ? out_data_bytes : Inlines.IMIN(3 * this.bitrate_bps / (1200 / nb_frames), out_data_bytes);
            ret = rp.opus_repacketizer_out_range_impl(0, nb_frames, data, data_ptr, repacketize_len, 0, this.use_vbr == 0 ? 1 : 0);
            if (ret < 0) {
                return OpusError.OPUS_INTERNAL_ERROR;
            }
            this.user_forced_mode = bak_mode;
            this.user_bandwidth = bak_bandwidth;
            this.force_channels = bak_channels;
            this.silk_mode.toMono = bak_to_mono;
            return ret;
        }
        OpusBandwidth curr_bandwidth = this.bandwidth;
        if (this.mode == OpusMode.MODE_SILK_ONLY && OpusBandwidthHelpers.GetOrdinal(curr_bandwidth) > OpusBandwidthHelpers.GetOrdinal(OpusBandwidth.OPUS_BANDWIDTH_WIDEBAND)) {
            this.mode = OpusMode.MODE_HYBRID;
        }
        if (this.mode == OpusMode.MODE_HYBRID && OpusBandwidthHelpers.GetOrdinal(curr_bandwidth) <= OpusBandwidthHelpers.GetOrdinal(OpusBandwidth.OPUS_BANDWIDTH_WIDEBAND)) {
            this.mode = OpusMode.MODE_SILK_ONLY;
        }
        int bytes_target = Inlines.IMIN(max_data_bytes - redundancy_bytes, this.bitrate_bps * frame_size / (this.Fs * 8)) - 1;
        enc.enc_init(data, ++data_ptr, max_data_bytes - 1);
        short[] pcm_buf = new short[(total_buffer + frame_size) * this.channels];
        System.arraycopy(this.delay_buffer, (this.encoder_buffer - total_buffer) * this.channels, pcm_buf, 0, total_buffer * this.channels);
        int hp_freq_smth1 = this.mode == OpusMode.MODE_CELT_ONLY ? Inlines.silk_LSHIFT(Inlines.silk_lin2log(60), 8) : silk_enc.state_Fxx[0].variable_HP_smth1_Q15;
        this.variable_HP_smth2_Q15 = Inlines.silk_SMLAWB(this.variable_HP_smth2_Q15, hp_freq_smth1 - this.variable_HP_smth2_Q15, 983);
        int cutoff_Hz = Inlines.silk_log2lin(Inlines.silk_RSHIFT(this.variable_HP_smth2_Q15, 8));
        if (this.application == OpusApplication.OPUS_APPLICATION_VOIP) {
            CodecHelpers.hp_cutoff(pcm, pcm_ptr, cutoff_Hz, pcm_buf, total_buffer * this.channels, this.hp_mem, frame_size, this.channels, this.Fs);
        } else {
            CodecHelpers.dc_reject(pcm, pcm_ptr, 3, pcm_buf, total_buffer * this.channels, this.hp_mem, frame_size, this.channels, this.Fs);
        }
        int HB_gain = Short.MAX_VALUE;
        if (this.mode != OpusMode.MODE_CELT_ONLY) {
            short[] pcm_silk = new short[this.channels * frame_size];
            int total_bitRate = 8 * bytes_target * frame_rate;
            if (this.mode == OpusMode.MODE_HYBRID) {
                this.silk_mode.bitRate = this.stream_channels * (5000 + (this.Fs == 100 * frame_size ? 1000 : 0));
                this.silk_mode.bitRate = curr_bandwidth == OpusBandwidth.OPUS_BANDWIDTH_SUPERWIDEBAND ? (this.silk_mode.bitRate += (total_bitRate - this.silk_mode.bitRate) * 2 / 3) : (this.silk_mode.bitRate += (total_bitRate - this.silk_mode.bitRate) * 3 / 5);
                if (this.silk_mode.bitRate > total_bitRate * 4 / 5) {
                    this.silk_mode.bitRate = total_bitRate * 4 / 5;
                }
                if (this.energy_masking == null) {
                    int celt_rate = total_bitRate - this.silk_mode.bitRate;
                    int HB_gain_ref = curr_bandwidth == OpusBandwidth.OPUS_BANDWIDTH_SUPERWIDEBAND ? 3000 : 3600;
                    HB_gain = Inlines.SHL32(celt_rate, 9) / Inlines.SHR32(celt_rate + this.stream_channels * HB_gain_ref, 6);
                    HB_gain = HB_gain < 28086 ? HB_gain + 4681 : Short.MAX_VALUE;
                }
            } else {
                this.silk_mode.bitRate = total_bitRate;
            }
            if (this.energy_masking != null && this.use_vbr != 0 && this.lfe == 0) {
                int mask_sum = 0;
                int end = 17;
                int srate = 16000;
                if (this.bandwidth == OpusBandwidth.OPUS_BANDWIDTH_NARROWBAND) {
                    end = 13;
                    srate = 8000;
                } else if (this.bandwidth == OpusBandwidth.OPUS_BANDWIDTH_MEDIUMBAND) {
                    end = 15;
                    srate = 12000;
                }
                for (int c = 0; c < this.channels; ++c) {
                    for (i = 0; i < end; ++i) {
                        int mask = Inlines.MAX16(Inlines.MIN16(this.energy_masking[21 * c + i], 512), -2048);
                        if (mask > 0) {
                            mask = Inlines.HALF16(mask);
                        }
                        mask_sum += mask;
                    }
                }
                int masking_depth = mask_sum / end * this.channels;
                int rate_offset = Inlines.PSHR32(Inlines.MULT16_16(srate, masking_depth += 205), 10);
                rate_offset = Inlines.MAX32(rate_offset, -2 * this.silk_mode.bitRate / 3);
                this.silk_mode.bitRate = this.bandwidth == OpusBandwidth.OPUS_BANDWIDTH_SUPERWIDEBAND || this.bandwidth == OpusBandwidth.OPUS_BANDWIDTH_FULLBAND ? (this.silk_mode.bitRate += 3 * rate_offset / 5) : (this.silk_mode.bitRate += rate_offset);
                bytes_target += rate_offset * frame_size / (8 * this.Fs);
            }
            this.silk_mode.payloadSize_ms = 1000 * frame_size / this.Fs;
            this.silk_mode.nChannelsAPI = this.channels;
            this.silk_mode.nChannelsInternal = this.stream_channels;
            if (curr_bandwidth == OpusBandwidth.OPUS_BANDWIDTH_NARROWBAND) {
                this.silk_mode.desiredInternalSampleRate = 8000;
            } else if (curr_bandwidth == OpusBandwidth.OPUS_BANDWIDTH_MEDIUMBAND) {
                this.silk_mode.desiredInternalSampleRate = 12000;
            } else {
                Inlines.OpusAssert(this.mode == OpusMode.MODE_HYBRID || curr_bandwidth == OpusBandwidth.OPUS_BANDWIDTH_WIDEBAND);
                this.silk_mode.desiredInternalSampleRate = 16000;
            }
            this.silk_mode.minInternalSampleRate = this.mode == OpusMode.MODE_HYBRID ? 16000 : 8000;
            if (this.mode == OpusMode.MODE_SILK_ONLY) {
                int effective_max_rate = max_rate;
                this.silk_mode.maxInternalSampleRate = 16000;
                if (frame_rate > 50) {
                    effective_max_rate = effective_max_rate * 2 / 3;
                }
                if (effective_max_rate < 13000) {
                    this.silk_mode.maxInternalSampleRate = 12000;
                    this.silk_mode.desiredInternalSampleRate = Inlines.IMIN(12000, this.silk_mode.desiredInternalSampleRate);
                }
                if (effective_max_rate < 9600) {
                    this.silk_mode.maxInternalSampleRate = 8000;
                    this.silk_mode.desiredInternalSampleRate = Inlines.IMIN(8000, this.silk_mode.desiredInternalSampleRate);
                }
            } else {
                this.silk_mode.maxInternalSampleRate = 16000;
            }
            this.silk_mode.useCBR = this.use_vbr == 0 ? 1 : 0;
            int nBytes = Inlines.IMIN(1275, max_data_bytes - 1 - redundancy_bytes);
            this.silk_mode.maxBits = nBytes * 8;
            if (this.mode == OpusMode.MODE_HYBRID) {
                this.silk_mode.maxBits = this.silk_mode.maxBits * 9 / 10;
            }
            if (this.silk_mode.useCBR != 0) {
                this.silk_mode.maxBits = this.silk_mode.bitRate * frame_size / (this.Fs * 8) * 8;
                this.silk_mode.bitRate = Inlines.IMAX(1, this.silk_mode.bitRate - 2000);
            }
            if (prefill) {
                BoxedValueInt zero = new BoxedValueInt(0);
                int prefill_offset = this.channels * (this.encoder_buffer - this.delay_compensation - this.Fs / 400);
                CodecHelpers.gain_fade(this.delay_buffer, prefill_offset, 0, Short.MAX_VALUE, celt_mode.overlap, this.Fs / 400, this.channels, celt_mode.window, this.Fs);
                Arrays.MemSet(this.delay_buffer, (short)0, prefill_offset);
                System.arraycopy(this.delay_buffer, 0, pcm_silk, 0, this.encoder_buffer * this.channels);
                EncodeAPI.silk_Encode(silk_enc, this.silk_mode, pcm_silk, this.encoder_buffer, null, zero, 1);
            }
            System.arraycopy(pcm_buf, total_buffer * this.channels, pcm_silk, 0, frame_size * this.channels);
            BoxedValueInt boxed_silkBytes = new BoxedValueInt(nBytes);
            ret = EncodeAPI.silk_Encode(silk_enc, this.silk_mode, pcm_silk, frame_size, enc, boxed_silkBytes, 0);
            nBytes = boxed_silkBytes.Val;
            if (ret != 0) {
                return OpusError.OPUS_INTERNAL_ERROR;
            }
            if (nBytes == 0) {
                this.rangeFinal = 0;
                data[data_ptr - 1] = CodecHelpers.gen_toc(this.mode, this.Fs / frame_size, curr_bandwidth, this.stream_channels);
                return 1;
            }
            if (this.mode == OpusMode.MODE_SILK_ONLY) {
                if (this.silk_mode.internalSampleRate == 8000) {
                    curr_bandwidth = OpusBandwidth.OPUS_BANDWIDTH_NARROWBAND;
                } else if (this.silk_mode.internalSampleRate == 12000) {
                    curr_bandwidth = OpusBandwidth.OPUS_BANDWIDTH_MEDIUMBAND;
                } else if (this.silk_mode.internalSampleRate == 16000) {
                    curr_bandwidth = OpusBandwidth.OPUS_BANDWIDTH_WIDEBAND;
                }
            } else {
                Inlines.OpusAssert(this.silk_mode.internalSampleRate == 16000);
            }
            this.silk_mode.opusCanSwitch = this.silk_mode.switchReady;
            if (this.silk_mode.opusCanSwitch != 0) {
                redundancy = 1;
                celt_to_silk = 0;
                this.silk_bw_switch = 1;
            }
        }
        int endband = 21;
        switch (curr_bandwidth) {
            case OPUS_BANDWIDTH_NARROWBAND: {
                endband = 13;
                break;
            }
            case OPUS_BANDWIDTH_MEDIUMBAND: 
            case OPUS_BANDWIDTH_WIDEBAND: {
                endband = 17;
                break;
            }
            case OPUS_BANDWIDTH_SUPERWIDEBAND: {
                endband = 19;
                break;
            }
            case OPUS_BANDWIDTH_FULLBAND: {
                endband = 21;
            }
        }
        celt_enc.SetEndBand(endband);
        celt_enc.SetChannels(this.stream_channels);
        celt_enc.SetBitrate(-1);
        if (this.mode != OpusMode.MODE_SILK_ONLY) {
            int celt_pred = 2;
            celt_enc.SetVBR(false);
            if (this.silk_mode.reducedDependency != 0) {
                celt_pred = 0;
            }
            celt_enc.SetPrediction(celt_pred);
            if (this.mode == OpusMode.MODE_HYBRID) {
                int len = enc.tell() + 7 >> 3;
                if (redundancy != 0) {
                    len += this.mode == OpusMode.MODE_HYBRID ? 3 : 1;
                }
                nb_compr_bytes = this.use_vbr != 0 ? len + bytes_target - this.silk_mode.bitRate * frame_size / (8 * this.Fs) : (len > bytes_target ? len : bytes_target);
            } else if (this.use_vbr != 0) {
                int bonus = 0;
                if (this.analysis.enabled && this.variable_duration == OpusFramesize.OPUS_FRAMESIZE_VARIABLE && frame_size != this.Fs / 50) {
                    bonus = (60 * this.stream_channels + 40) * (this.Fs / frame_size - 50);
                    if (analysis_info.valid != 0) {
                        bonus = (int)((float)bonus * (1.0f + 0.5f * analysis_info.tonality));
                    }
                }
                celt_enc.SetVBR(true);
                celt_enc.SetVBRConstraint(this.vbr_constraint != 0);
                celt_enc.SetBitrate(this.bitrate_bps + bonus);
                nb_compr_bytes = max_data_bytes - 1 - redundancy_bytes;
            } else {
                nb_compr_bytes = bytes_target;
            }
        } else {
            nb_compr_bytes = 0;
        }
        short[] tmp_prefill = new short[this.channels * this.Fs / 400];
        if (this.mode != OpusMode.MODE_SILK_ONLY && this.mode != this.prev_mode && this.prev_mode != OpusMode.MODE_AUTO && this.prev_mode != OpusMode.MODE_UNKNOWN) {
            System.arraycopy(this.delay_buffer, (this.encoder_buffer - total_buffer - this.Fs / 400) * this.channels, tmp_prefill, 0, this.channels * this.Fs / 400);
        }
        if (this.channels * (this.encoder_buffer - (frame_size + total_buffer)) > 0) {
            Arrays.MemMove(this.delay_buffer, this.channels * frame_size, 0, this.channels * (this.encoder_buffer - frame_size - total_buffer));
            System.arraycopy(pcm_buf, 0, this.delay_buffer, this.channels * (this.encoder_buffer - frame_size - total_buffer), (frame_size + total_buffer) * this.channels);
        } else {
            System.arraycopy(pcm_buf, (frame_size + total_buffer - this.encoder_buffer) * this.channels, this.delay_buffer, 0, this.encoder_buffer * this.channels);
        }
        if (this.prev_HB_gain < Short.MAX_VALUE || HB_gain < Short.MAX_VALUE) {
            CodecHelpers.gain_fade(pcm_buf, 0, this.prev_HB_gain, HB_gain, celt_mode.overlap, frame_size, this.channels, celt_mode.window, this.Fs);
        }
        this.prev_HB_gain = HB_gain;
        if (this.mode != OpusMode.MODE_HYBRID || this.stream_channels == 1) {
            this.silk_mode.stereoWidth_Q14 = Inlines.IMIN(16384, 2 * Inlines.IMAX(0, equiv_rate - 30000));
        }
        if (this.energy_masking == null && this.channels == 2 && (this.hybrid_stereo_width_Q14 < 16384 || this.silk_mode.stereoWidth_Q14 < 16384)) {
            int g1 = this.hybrid_stereo_width_Q14;
            int g2 = this.silk_mode.stereoWidth_Q14;
            g1 = g1 == 16384 ? Short.MAX_VALUE : Inlines.SHL16(g1, 1);
            g2 = g2 == 16384 ? Short.MAX_VALUE : Inlines.SHL16(g2, 1);
            CodecHelpers.stereo_fade(pcm_buf, g1, g2, celt_mode.overlap, frame_size, this.channels, celt_mode.window, this.Fs);
            this.hybrid_stereo_width_Q14 = (short)this.silk_mode.stereoWidth_Q14;
        }
        if (this.mode != OpusMode.MODE_CELT_ONLY && enc.tell() + 17 + 20 * (this.mode == OpusMode.MODE_HYBRID ? 1 : 0) <= 8 * (max_data_bytes - 1)) {
            if (this.mode == OpusMode.MODE_HYBRID && (redundancy != 0 || enc.tell() + 37 <= 8 * nb_compr_bytes)) {
                enc.enc_bit_logp(redundancy, 12);
            }
            if (redundancy != 0) {
                enc.enc_bit_logp(celt_to_silk, 1);
                int max_redundancy = this.mode == OpusMode.MODE_HYBRID ? max_data_bytes - 1 - nb_compr_bytes : max_data_bytes - 1 - (enc.tell() + 7 >> 3);
                redundancy_bytes = Inlines.IMIN(max_redundancy, this.bitrate_bps / 1600);
                redundancy_bytes = Inlines.IMIN(257, Inlines.IMAX(2, redundancy_bytes));
                if (this.mode == OpusMode.MODE_HYBRID) {
                    enc.enc_uint(redundancy_bytes - 2, 256L);
                }
            }
        } else {
            redundancy = 0;
        }
        if (redundancy == 0) {
            this.silk_bw_switch = 0;
            redundancy_bytes = 0;
        }
        if (this.mode != OpusMode.MODE_CELT_ONLY) {
            start_band = 17;
        }
        if (this.mode == OpusMode.MODE_SILK_ONLY) {
            ret = enc.tell() + 7 >> 3;
            enc.enc_done();
            nb_compr_bytes = ret;
        } else {
            nb_compr_bytes = Inlines.IMIN(max_data_bytes - 1 - redundancy_bytes, nb_compr_bytes);
            enc.enc_shrink(nb_compr_bytes);
        }
        if (this.analysis.enabled && redundancy != 0 || this.mode != OpusMode.MODE_SILK_ONLY) {
            analysis_info.enabled = this.analysis.enabled;
            celt_enc.SetAnalysis(analysis_info);
        }
        if (redundancy != 0 && celt_to_silk != 0) {
            celt_enc.SetStartBand(0);
            celt_enc.SetVBR(false);
            int err = celt_enc.celt_encode_with_ec(pcm_buf, 0, this.Fs / 200, data, data_ptr + nb_compr_bytes, redundancy_bytes, null);
            if (err < 0) {
                return OpusError.OPUS_INTERNAL_ERROR;
            }
            redundant_rng = celt_enc.GetFinalRange();
            celt_enc.ResetState();
        }
        celt_enc.SetStartBand(start_band);
        if (this.mode != OpusMode.MODE_SILK_ONLY) {
            if (this.mode != this.prev_mode && this.prev_mode != OpusMode.MODE_AUTO && this.prev_mode != OpusMode.MODE_UNKNOWN) {
                byte[] dummy = new byte[2];
                celt_enc.ResetState();
                celt_enc.celt_encode_with_ec(tmp_prefill, 0, this.Fs / 400, dummy, 0, 2, null);
                celt_enc.SetPrediction(0);
            }
            if (enc.tell() <= 8 * nb_compr_bytes && (ret = celt_enc.celt_encode_with_ec(pcm_buf, 0, frame_size, null, 0, nb_compr_bytes, enc)) < 0) {
                return OpusError.OPUS_INTERNAL_ERROR;
            }
        }
        if (redundancy != 0 && celt_to_silk == 0) {
            byte[] dummy = new byte[2];
            int N2 = this.Fs / 200;
            int N4 = this.Fs / 400;
            celt_enc.ResetState();
            celt_enc.SetStartBand(0);
            celt_enc.SetPrediction(0);
            celt_enc.celt_encode_with_ec(pcm_buf, this.channels * (frame_size - N2 - N4), N4, dummy, 0, 2, null);
            int err = celt_enc.celt_encode_with_ec(pcm_buf, this.channels * (frame_size - N2), N2, data, data_ptr + nb_compr_bytes, redundancy_bytes, null);
            if (err < 0) {
                return OpusError.OPUS_INTERNAL_ERROR;
            }
            redundant_rng = celt_enc.GetFinalRange();
        }
        data[--data_ptr] = CodecHelpers.gen_toc(this.mode, this.Fs / frame_size, curr_bandwidth, this.stream_channels);
        this.rangeFinal = (int)enc.rng ^ redundant_rng;
        this.prev_mode = to_celt ? OpusMode.MODE_CELT_ONLY : this.mode;
        this.prev_channels = this.stream_channels;
        this.prev_framesize = frame_size;
        this.first = 0;
        if (enc.tell() > (max_data_bytes - 1) * 8) {
            if (max_data_bytes < 2) {
                return OpusError.OPUS_BUFFER_TOO_SMALL;
            }
            data[data_ptr + 1] = 0;
            ret = 1;
            this.rangeFinal = 0;
        } else if (this.mode == OpusMode.MODE_SILK_ONLY && redundancy == 0) {
            while (ret > 2 && data[data_ptr + ret] == 0) {
                --ret;
            }
        }
        ret += 1 + redundancy_bytes;
        if (this.use_vbr == 0) {
            if (OpusRepacketizer.padPacket(data, data_ptr, ret, max_data_bytes) != OpusError.OPUS_OK) {
                return OpusError.OPUS_INTERNAL_ERROR;
            }
            ret = max_data_bytes;
        }
        return ret;
    }

    public int encode(short[] in_pcm, int pcm_offset, int frame_size, byte[] out_data, int out_data_offset, int max_data_bytes) throws OpusException {
        if (out_data_offset + max_data_bytes > out_data.length) {
            throw new IllegalArgumentException("Output buffer is too small: Stated size is " + max_data_bytes + " bytes, actual size is " + (out_data.length - out_data_offset) + " bytes");
        }
        int delay_compensation = this.application == OpusApplication.OPUS_APPLICATION_RESTRICTED_LOWDELAY ? 0 : this.delay_compensation;
        int internal_frame_size = CodecHelpers.compute_frame_size(in_pcm, pcm_offset, frame_size, this.variable_duration, this.channels, this.Fs, this.bitrate_bps, delay_compensation, this.analysis.subframe_mem, this.analysis.enabled);
        if (pcm_offset + internal_frame_size > in_pcm.length) {
            throw new IllegalArgumentException("Not enough samples provided in input signal: Expected " + internal_frame_size + " samples, found " + (in_pcm.length - pcm_offset));
        }
        try {
            int ret = this.opus_encode_native(in_pcm, pcm_offset, internal_frame_size, out_data, out_data_offset, max_data_bytes, 16, in_pcm, pcm_offset, frame_size, 0, -2, this.channels, 0);
            if (ret < 0) {
                if (ret == OpusError.OPUS_BAD_ARG) {
                    throw new IllegalArgumentException("OPUS_BAD_ARG while encoding");
                }
                throw new OpusException("An error occurred during encoding", ret);
            }
            return ret;
        }
        catch (ArithmeticException e) {
            throw new OpusException("Internal error during encoding: " + e.getMessage());
        }
    }

    public int encode(byte[] in_pcm, int pcm_offset, int frame_size, byte[] out_data, int out_data_offset, int max_data_bytes) throws OpusException {
        short[] spcm = new short[frame_size * this.channels];
        int idx = pcm_offset;
        for (int c = 0; c < spcm.length; ++c) {
            spcm[c] = (short)((in_pcm[idx] & 0xFF | in_pcm[idx + 1] << 8) & 0xFFFF);
            idx += 2;
        }
        return this.encode(spcm, 0, frame_size, out_data, out_data_offset, max_data_bytes);
    }

    public OpusApplication getApplication() {
        return this.application;
    }

    public void setApplication(OpusApplication value) {
        if (this.first == 0 && this.application != value) {
            throw new IllegalArgumentException("Application cannot be changed after encoding has started");
        }
        this.application = value;
    }

    public int getBitrate() {
        return this.user_bitrate_to_bitrate(this.prev_framesize, 1276);
    }

    public void setBitrate(int value) {
        if (value != -1000 && value != -1) {
            if (value <= 0) {
                throw new IllegalArgumentException("Bitrate must be positive");
            }
            if (value <= 500) {
                value = 500;
            } else if (value > 300000 * this.channels) {
                value = 300000 * this.channels;
            }
        }
        this.user_bitrate_bps = value;
    }

    public int getForceChannels() {
        return this.force_channels;
    }

    public void setForceChannels(int value) {
        if ((value < 1 || value > this.channels) && value != -1000) {
            throw new IllegalArgumentException("Force channels must be <= num. of channels");
        }
        this.force_channels = value;
    }

    public OpusBandwidth getMaxBandwidth() {
        return this.max_bandwidth;
    }

    public void setMaxBandwidth(OpusBandwidth value) {
        this.max_bandwidth = value;
        this.silk_mode.maxInternalSampleRate = this.max_bandwidth == OpusBandwidth.OPUS_BANDWIDTH_NARROWBAND ? 8000 : (this.max_bandwidth == OpusBandwidth.OPUS_BANDWIDTH_MEDIUMBAND ? 12000 : 16000);
    }

    public OpusBandwidth getBandwidth() {
        return this.bandwidth;
    }

    public void setBandwidth(OpusBandwidth value) {
        this.user_bandwidth = value;
        this.silk_mode.maxInternalSampleRate = this.user_bandwidth == OpusBandwidth.OPUS_BANDWIDTH_NARROWBAND ? 8000 : (this.user_bandwidth == OpusBandwidth.OPUS_BANDWIDTH_MEDIUMBAND ? 12000 : 16000);
    }

    public boolean getUseDTX() {
        return this.silk_mode.useDTX != 0;
    }

    public void setUseDTX(boolean value) {
        this.silk_mode.useDTX = value ? 1 : 0;
    }

    public int getComplexity() {
        return this.silk_mode.complexity;
    }

    public void setComplexity(int value) {
        if (value < 0 || value > 10) {
            throw new IllegalArgumentException("Complexity must be between 0 and 10");
        }
        this.silk_mode.complexity = value;
        this.Celt_Encoder.SetComplexity(value);
    }

    public boolean getUseInbandFEC() {
        return this.silk_mode.useInBandFEC != 0;
    }

    public void setUseInbandFEC(boolean value) {
        this.silk_mode.useInBandFEC = value ? 1 : 0;
    }

    public int getPacketLossPercent() {
        return this.silk_mode.packetLossPercentage;
    }

    public void setPacketLossPercent(int value) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Packet loss must be between 0 and 100");
        }
        this.silk_mode.packetLossPercentage = value;
        this.Celt_Encoder.SetPacketLossPercent(value);
    }

    public boolean getUseVBR() {
        return this.use_vbr != 0;
    }

    public void setUseVBR(boolean value) {
        this.use_vbr = value ? 1 : 0;
        this.silk_mode.useCBR = value ? 0 : 1;
    }

    public boolean getUseConstrainedVBR() {
        return this.vbr_constraint != 0;
    }

    public void setUseConstrainedVBR(boolean value) {
        this.vbr_constraint = value ? 1 : 0;
    }

    public OpusSignal getSignalType() {
        return this.signal_type;
    }

    public void setSignalType(OpusSignal value) {
        this.signal_type = value;
    }

    public int getLookahead() {
        int returnVal = this.Fs / 400;
        if (this.application != OpusApplication.OPUS_APPLICATION_RESTRICTED_LOWDELAY) {
            returnVal += this.delay_compensation;
        }
        return returnVal;
    }

    public int getSampleRate() {
        return this.Fs;
    }

    public int getFinalRange() {
        return this.rangeFinal;
    }

    public int getLSBDepth() {
        return this.lsb_depth;
    }

    public void setLSBDepth(int value) {
        if (value < 8 || value > 24) {
            throw new IllegalArgumentException("LSB depth must be between 8 and 24");
        }
        this.lsb_depth = value;
    }

    public OpusFramesize getExpertFrameDuration() {
        return this.variable_duration;
    }

    public void setExpertFrameDuration(OpusFramesize value) {
        this.variable_duration = value;
        this.Celt_Encoder.SetExpertFrameDuration(value);
    }

    public OpusMode getForceMode() {
        return this.user_forced_mode;
    }

    public void setForceMode(OpusMode value) {
        this.user_forced_mode = value;
    }

    public boolean getIsLFE() {
        return this.lfe != 0;
    }

    public void setIsLFE(boolean value) {
        this.lfe = value ? 1 : 0;
        this.Celt_Encoder.SetLFE(value ? 1 : 0);
    }

    public boolean getPredictionDisabled() {
        return this.silk_mode.reducedDependency != 0;
    }

    public void setPredictionDisabled(boolean value) {
        this.silk_mode.reducedDependency = value ? 1 : 0;
    }

    public boolean getEnableAnalysis() {
        return this.analysis.enabled;
    }

    public void setEnableAnalysis(boolean value) {
        this.analysis.enabled = value;
    }

    void SetEnergyMask(int[] value) {
        this.energy_masking = value;
        this.Celt_Encoder.SetEnergyMask(value);
    }

    CeltMode GetCeltMode() {
        return this.Celt_Encoder.GetMode();
    }
}
