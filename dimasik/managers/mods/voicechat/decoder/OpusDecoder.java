package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;
import dimasik.managers.mods.voicechat.decoder.BoxedValueByte;
import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.CeltDecoder;
import dimasik.managers.mods.voicechat.decoder.CodecHelpers;
import dimasik.managers.mods.voicechat.decoder.DecControlState;
import dimasik.managers.mods.voicechat.decoder.DecodeAPI;
import dimasik.managers.mods.voicechat.decoder.EntropyCoder;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.OpusBandwidth;
import dimasik.managers.mods.voicechat.decoder.OpusError;
import dimasik.managers.mods.voicechat.decoder.OpusException;
import dimasik.managers.mods.voicechat.decoder.OpusMode;
import dimasik.managers.mods.voicechat.decoder.OpusPacketInfo;
import dimasik.managers.mods.voicechat.decoder.SilkDecoder;

public class OpusDecoder {
    int channels;
    int Fs;
    final DecControlState DecControl = new DecControlState();
    int decode_gain;
    int stream_channels;
    OpusBandwidth bandwidth;
    OpusMode mode;
    OpusMode prev_mode;
    int frame_size;
    int prev_redundancy;
    int last_packet_duration;
    int rangeFinal;
    SilkDecoder SilkDecoder = new SilkDecoder();
    CeltDecoder Celt_Decoder = new CeltDecoder();
    private static final byte[] SILENCE = new byte[]{-1, -1};

    OpusDecoder() {
    }

    void reset() {
        this.channels = 0;
        this.Fs = 0;
        this.DecControl.Reset();
        this.decode_gain = 0;
        this.partialReset();
    }

    void partialReset() {
        this.stream_channels = 0;
        this.bandwidth = OpusBandwidth.OPUS_BANDWIDTH_UNKNOWN;
        this.mode = OpusMode.MODE_UNKNOWN;
        this.prev_mode = OpusMode.MODE_UNKNOWN;
        this.frame_size = 0;
        this.prev_redundancy = 0;
        this.last_packet_duration = 0;
        this.rangeFinal = 0;
    }

    int opus_decoder_init(int Fs, int channels) {
        if (Fs != 48000 && Fs != 24000 && Fs != 16000 && Fs != 12000 && Fs != 8000 || channels != 1 && channels != 2) {
            return OpusError.OPUS_BAD_ARG;
        }
        this.reset();
        SilkDecoder silk_dec = this.SilkDecoder;
        CeltDecoder celt_dec = this.Celt_Decoder;
        this.stream_channels = this.channels = channels;
        this.DecControl.API_sampleRate = this.Fs = Fs;
        this.DecControl.nChannelsAPI = this.channels;
        int ret = DecodeAPI.silk_InitDecoder(silk_dec);
        if (ret != 0) {
            return OpusError.OPUS_INTERNAL_ERROR;
        }
        ret = celt_dec.celt_decoder_init(Fs, channels);
        if (ret != OpusError.OPUS_OK) {
            return OpusError.OPUS_INTERNAL_ERROR;
        }
        celt_dec.SetSignalling(0);
        this.prev_mode = OpusMode.MODE_UNKNOWN;
        this.frame_size = Fs / 400;
        return OpusError.OPUS_OK;
    }

    public OpusDecoder(int Fs, int channels) throws OpusException {
        if (Fs != 48000 && Fs != 24000 && Fs != 16000 && Fs != 12000 && Fs != 8000) {
            throw new IllegalArgumentException("Sample rate is invalid (must be 8/12/16/24/48 Khz)");
        }
        if (channels != 1 && channels != 2) {
            throw new IllegalArgumentException("Number of channels must be 1 or 2");
        }
        int ret = this.opus_decoder_init(Fs, channels);
        if (ret != OpusError.OPUS_OK) {
            if (ret == OpusError.OPUS_BAD_ARG) {
                throw new IllegalArgumentException("OPUS_BAD_ARG when creating decoder");
            }
            throw new OpusException("Error while initializing decoder", ret);
        }
    }

    int opus_decode_frame(byte[] data, int data_ptr, int len, short[] pcm, int pcm_ptr, int frame_size, int decode_fec) {
        int i;
        OpusMode mode;
        int audiosize;
        int silk_ret = 0;
        int celt_ret = 0;
        EntropyCoder dec = new EntropyCoder();
        short[] pcm_transition = null;
        boolean transition = false;
        int redundancy = 0;
        int redundancy_bytes = 0;
        int celt_to_silk = 0;
        int redundant_rng = 0;
        SilkDecoder silk_dec = this.SilkDecoder;
        CeltDecoder celt_dec = this.Celt_Decoder;
        int F20 = this.Fs / 50;
        int F10 = F20 >> 1;
        int F5 = F10 >> 1;
        int F2_5 = F5 >> 1;
        if (frame_size < F2_5) {
            return OpusError.OPUS_BUFFER_TOO_SMALL;
        }
        frame_size = Inlines.IMIN(frame_size, this.Fs / 25 * 3);
        if (len <= 1) {
            data = null;
            frame_size = Inlines.IMIN(frame_size, this.frame_size);
        }
        if (data != null) {
            audiosize = this.frame_size;
            mode = this.mode;
            dec.dec_init(data, data_ptr, len);
        } else {
            audiosize = frame_size;
            mode = this.prev_mode;
            if (mode == OpusMode.MODE_UNKNOWN) {
                for (int i2 = pcm_ptr; i2 < pcm_ptr + audiosize * this.channels; ++i2) {
                    pcm[i2] = 0;
                }
                return audiosize;
            }
            if (audiosize > F20) {
                int ret;
                do {
                    if ((ret = this.opus_decode_frame(null, 0, 0, pcm, pcm_ptr, Inlines.IMIN(audiosize, F20), 0)) < 0) {
                        return ret;
                    }
                    pcm_ptr += ret * this.channels;
                } while ((audiosize -= ret) > 0);
                return frame_size;
            }
            if (audiosize < F20) {
                if (audiosize > F10) {
                    audiosize = F10;
                } else if (mode != OpusMode.MODE_SILK_ONLY && audiosize > F5 && audiosize < F10) {
                    audiosize = F5;
                }
            }
        }
        int celt_accum = mode != OpusMode.MODE_CELT_ONLY && frame_size >= F10 ? 1 : 0;
        int pcm_transition_silk_size = 0;
        int pcm_transition_celt_size = 0;
        if (data != null && this.prev_mode != OpusMode.MODE_UNKNOWN && this.prev_mode != OpusMode.MODE_AUTO && (mode == OpusMode.MODE_CELT_ONLY && this.prev_mode != OpusMode.MODE_CELT_ONLY && this.prev_redundancy == 0 || mode != OpusMode.MODE_CELT_ONLY && this.prev_mode == OpusMode.MODE_CELT_ONLY)) {
            transition = true;
            if (mode == OpusMode.MODE_CELT_ONLY) {
                pcm_transition_celt_size = F5 * this.channels;
            } else {
                pcm_transition_silk_size = F5 * this.channels;
            }
        }
        short[] pcm_transition_celt = new short[pcm_transition_celt_size];
        if (transition && mode == OpusMode.MODE_CELT_ONLY) {
            pcm_transition = pcm_transition_celt;
            this.opus_decode_frame(null, 0, 0, pcm_transition, 0, Inlines.IMIN(F5, audiosize), 0);
        }
        if (audiosize > frame_size) {
            return OpusError.OPUS_BAD_ARG;
        }
        frame_size = audiosize;
        int pcm_silk_size = mode != OpusMode.MODE_CELT_ONLY && celt_accum == 0 ? Inlines.IMAX(F10, frame_size) * this.channels : 0;
        short[] pcm_silk = new short[pcm_silk_size];
        if (mode != OpusMode.MODE_CELT_ONLY) {
            int silk_frame_size;
            short[] pcm_ptr2;
            int pcm_ptr2_ptr = 0;
            if (celt_accum != 0) {
                pcm_ptr2 = pcm;
                pcm_ptr2_ptr = pcm_ptr;
            } else {
                pcm_ptr2 = pcm_silk;
                pcm_ptr2_ptr = 0;
            }
            if (this.prev_mode == OpusMode.MODE_CELT_ONLY) {
                DecodeAPI.silk_InitDecoder(silk_dec);
            }
            this.DecControl.payloadSize_ms = Inlines.IMAX(10, 1000 * audiosize / this.Fs);
            if (data != null) {
                this.DecControl.nChannelsInternal = this.stream_channels;
                if (mode == OpusMode.MODE_SILK_ONLY) {
                    if (this.bandwidth == OpusBandwidth.OPUS_BANDWIDTH_NARROWBAND) {
                        this.DecControl.internalSampleRate = 8000;
                    } else if (this.bandwidth == OpusBandwidth.OPUS_BANDWIDTH_MEDIUMBAND) {
                        this.DecControl.internalSampleRate = 12000;
                    } else if (this.bandwidth == OpusBandwidth.OPUS_BANDWIDTH_WIDEBAND) {
                        this.DecControl.internalSampleRate = 16000;
                    } else {
                        this.DecControl.internalSampleRate = 16000;
                        Inlines.OpusAssert(false);
                    }
                } else {
                    this.DecControl.internalSampleRate = 16000;
                }
            }
            int lost_flag = data == null ? 1 : 2 * decode_fec;
            int decoded_samples = 0;
            do {
                int first_frame = decoded_samples == 0 ? 1 : 0;
                BoxedValueInt boxed_silk_frame_size = new BoxedValueInt(0);
                silk_ret = DecodeAPI.silk_Decode(silk_dec, this.DecControl, lost_flag, first_frame, dec, pcm_ptr2, pcm_ptr2_ptr, boxed_silk_frame_size);
                silk_frame_size = boxed_silk_frame_size.Val;
                if (silk_ret != 0) {
                    if (lost_flag != 0) {
                        silk_frame_size = frame_size;
                        Arrays.MemSetWithOffset(pcm_ptr2, (short)0, pcm_ptr2_ptr, frame_size * this.channels);
                    } else {
                        return OpusError.OPUS_INTERNAL_ERROR;
                    }
                }
                pcm_ptr2_ptr += silk_frame_size * this.channels;
            } while ((decoded_samples += silk_frame_size) < frame_size);
        }
        int start_band = 0;
        if (decode_fec == 0 && mode != OpusMode.MODE_CELT_ONLY && data != null && dec.tell() + 17 + 20 * (this.mode == OpusMode.MODE_HYBRID ? 1 : 0) <= 8 * len && (redundancy = mode == OpusMode.MODE_HYBRID ? dec.dec_bit_logp(12L) : 1) != 0) {
            celt_to_silk = dec.dec_bit_logp(1L);
            int n = redundancy_bytes = mode == OpusMode.MODE_HYBRID ? (int)dec.dec_uint(256L) + 2 : len - (dec.tell() + 7 >> 3);
            if ((len -= redundancy_bytes) * 8 < dec.tell()) {
                len = 0;
                redundancy_bytes = 0;
                redundancy = 0;
            }
            dec.storage -= redundancy_bytes;
        }
        if (mode != OpusMode.MODE_CELT_ONLY) {
            start_band = 17;
        }
        int endband = 21;
        switch (this.bandwidth) {
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
        celt_dec.SetEndBand(endband);
        celt_dec.SetChannels(this.stream_channels);
        if (redundancy != 0) {
            transition = false;
            pcm_transition_silk_size = 0;
        }
        short[] pcm_transition_silk = new short[pcm_transition_silk_size];
        if (transition && mode != OpusMode.MODE_CELT_ONLY) {
            pcm_transition = pcm_transition_silk;
            this.opus_decode_frame(null, 0, 0, pcm_transition, 0, Inlines.IMIN(F5, audiosize), 0);
        }
        int redundant_audio_size = redundancy != 0 ? F5 * this.channels : 0;
        short[] redundant_audio = new short[redundant_audio_size];
        if (redundancy != 0 && celt_to_silk != 0) {
            celt_dec.SetStartBand(0);
            celt_dec.celt_decode_with_ec(data, data_ptr + len, redundancy_bytes, redundant_audio, 0, F5, null, 0);
            redundant_rng = celt_dec.GetFinalRange();
        }
        celt_dec.SetStartBand(start_band);
        if (mode != OpusMode.MODE_SILK_ONLY) {
            int celt_frame_size = Inlines.IMIN(F20, frame_size);
            if (mode != this.prev_mode && this.prev_mode != OpusMode.MODE_AUTO && this.prev_mode != OpusMode.MODE_UNKNOWN && this.prev_redundancy == 0) {
                celt_dec.ResetState();
            }
            celt_ret = celt_dec.celt_decode_with_ec(decode_fec != 0 ? null : data, data_ptr, len, pcm, pcm_ptr, celt_frame_size, dec, celt_accum);
        } else {
            if (celt_accum == 0) {
                for (i = pcm_ptr; i < frame_size * this.channels + pcm_ptr; ++i) {
                    pcm[i] = 0;
                }
            }
            if (this.prev_mode == OpusMode.MODE_HYBRID && (redundancy == 0 || celt_to_silk == 0 || this.prev_redundancy == 0)) {
                celt_dec.SetStartBand(0);
                celt_dec.celt_decode_with_ec(SILENCE, 0, 2, pcm, pcm_ptr, F2_5, null, celt_accum);
            }
        }
        if (mode != OpusMode.MODE_CELT_ONLY && celt_accum == 0) {
            for (i = 0; i < frame_size * this.channels; ++i) {
                pcm[pcm_ptr + i] = Inlines.SAT16(Inlines.ADD32(pcm[pcm_ptr + i], pcm_silk[i]));
            }
        }
        int[] window = celt_dec.GetMode().window;
        if (redundancy != 0 && celt_to_silk == 0) {
            celt_dec.ResetState();
            celt_dec.SetStartBand(0);
            celt_dec.celt_decode_with_ec(data, data_ptr + len, redundancy_bytes, redundant_audio, 0, F5, null, 0);
            redundant_rng = celt_dec.GetFinalRange();
            CodecHelpers.smooth_fade(pcm, pcm_ptr + this.channels * (frame_size - F2_5), redundant_audio, this.channels * F2_5, pcm, pcm_ptr + this.channels * (frame_size - F2_5), F2_5, this.channels, window, this.Fs);
        }
        if (redundancy != 0 && celt_to_silk != 0) {
            for (int c = 0; c < this.channels; ++c) {
                for (i = 0; i < F2_5; ++i) {
                    pcm[this.channels * i + c + pcm_ptr] = redundant_audio[this.channels * i + c];
                }
            }
            CodecHelpers.smooth_fade(redundant_audio, this.channels * F2_5, pcm, pcm_ptr + this.channels * F2_5, pcm, pcm_ptr + this.channels * F2_5, F2_5, this.channels, window, this.Fs);
        }
        if (transition) {
            if (audiosize >= F5) {
                for (i = 0; i < this.channels * F2_5; ++i) {
                    pcm[i] = pcm_transition[i];
                }
                CodecHelpers.smooth_fade(pcm_transition, this.channels * F2_5, pcm, pcm_ptr + this.channels * F2_5, pcm, pcm_ptr + this.channels * F2_5, F2_5, this.channels, window, this.Fs);
            } else {
                CodecHelpers.smooth_fade(pcm_transition, 0, pcm, pcm_ptr, pcm, pcm_ptr, F2_5, this.channels, window, this.Fs);
            }
        }
        if (this.decode_gain != 0) {
            int gain = Inlines.celt_exp2(Inlines.MULT16_16_P15(21771, this.decode_gain));
            for (i = pcm_ptr; i < pcm_ptr + frame_size * this.channels; ++i) {
                int x = Inlines.MULT16_32_P16(pcm[i], gain);
                pcm[i] = (short)Inlines.SATURATE(x, Short.MAX_VALUE);
            }
        }
        this.rangeFinal = len <= 1 ? 0 : (int)dec.rng ^ redundant_rng;
        this.prev_mode = mode;
        this.prev_redundancy = redundancy != 0 && celt_to_silk == 0 ? 1 : 0;
        return celt_ret < 0 ? celt_ret : audiosize;
    }

    int opus_decode_native(byte[] data, int data_ptr, int len, short[] pcm_out, int pcm_out_ptr, int frame_size, int decode_fec, int self_delimited, BoxedValueInt packet_offset, int soft_clip) {
        packet_offset.Val = 0;
        short[] size = new short[48];
        if (decode_fec < 0 || decode_fec > 1) {
            return OpusError.OPUS_BAD_ARG;
        }
        if ((decode_fec != 0 || len == 0 || data == null) && frame_size % (this.Fs / 400) != 0) {
            return OpusError.OPUS_BAD_ARG;
        }
        if (len == 0 || data == null) {
            int ret;
            int pcm_count = 0;
            do {
                if ((ret = this.opus_decode_frame(null, 0, 0, pcm_out, pcm_out_ptr + pcm_count * this.channels, frame_size - pcm_count, 0)) >= 0) continue;
                return ret;
            } while ((pcm_count += ret) < frame_size);
            Inlines.OpusAssert(pcm_count == frame_size);
            this.last_packet_duration = pcm_count;
            return pcm_count;
        }
        if (len < 0) {
            return OpusError.OPUS_BAD_ARG;
        }
        OpusMode packet_mode = OpusPacketInfo.getEncoderMode(data, data_ptr);
        OpusBandwidth packet_bandwidth = OpusPacketInfo.getBandwidth(data, data_ptr);
        int packet_frame_size = OpusPacketInfo.getNumSamplesPerFrame(data, data_ptr, this.Fs);
        int packet_stream_channels = OpusPacketInfo.getNumEncodedChannels(data, data_ptr);
        BoxedValueByte boxed_toc = new BoxedValueByte(0);
        BoxedValueInt boxed_offset = new BoxedValueInt(0);
        int count = OpusPacketInfo.opus_packet_parse_impl(data, data_ptr, len, self_delimited, boxed_toc, null, 0, size, 0, boxed_offset, packet_offset);
        int offset = boxed_offset.Val;
        if (count < 0) {
            return count;
        }
        data_ptr += offset;
        if (decode_fec != 0) {
            int ret;
            BoxedValueInt dummy = new BoxedValueInt(0);
            if (frame_size < packet_frame_size || packet_mode == OpusMode.MODE_CELT_ONLY || this.mode == OpusMode.MODE_CELT_ONLY) {
                return this.opus_decode_native(null, 0, 0, pcm_out, pcm_out_ptr, frame_size, 0, 0, dummy, soft_clip);
            }
            int duration_copy = this.last_packet_duration;
            if (frame_size - packet_frame_size != 0) {
                ret = this.opus_decode_native(null, 0, 0, pcm_out, pcm_out_ptr, frame_size - packet_frame_size, 0, 0, dummy, soft_clip);
                if (ret < 0) {
                    this.last_packet_duration = duration_copy;
                    return ret;
                }
                Inlines.OpusAssert(ret == frame_size - packet_frame_size);
            }
            this.mode = packet_mode;
            this.bandwidth = packet_bandwidth;
            this.frame_size = packet_frame_size;
            this.stream_channels = packet_stream_channels;
            ret = this.opus_decode_frame(data, data_ptr, size[0], pcm_out, pcm_out_ptr + this.channels * (frame_size - packet_frame_size), packet_frame_size, 1);
            if (ret < 0) {
                return ret;
            }
            this.last_packet_duration = frame_size;
            return frame_size;
        }
        if (count * packet_frame_size > frame_size) {
            return OpusError.OPUS_BUFFER_TOO_SMALL;
        }
        this.mode = packet_mode;
        this.bandwidth = packet_bandwidth;
        this.frame_size = packet_frame_size;
        this.stream_channels = packet_stream_channels;
        int nb_samples = 0;
        for (int i = 0; i < count; ++i) {
            int ret = this.opus_decode_frame(data, data_ptr, size[i], pcm_out, pcm_out_ptr + nb_samples * this.channels, frame_size - nb_samples, 0);
            if (ret < 0) {
                return ret;
            }
            Inlines.OpusAssert(ret == packet_frame_size);
            data_ptr += size[i];
            nb_samples += ret;
        }
        this.last_packet_duration = nb_samples;
        return nb_samples;
    }

    public int decode(byte[] in_data, int in_data_offset, int len, short[] out_pcm, int out_pcm_offset, int frame_size, boolean decode_fec) throws OpusException {
        if (frame_size <= 0) {
            throw new IllegalArgumentException("Frame size must be > 0");
        }
        try {
            BoxedValueInt dummy = new BoxedValueInt(0);
            int ret = this.opus_decode_native(in_data, in_data_offset, len, out_pcm, out_pcm_offset, frame_size, decode_fec ? 1 : 0, 0, dummy, 0);
            if (ret < 0) {
                if (ret == OpusError.OPUS_BAD_ARG) {
                    throw new IllegalArgumentException("OPUS_BAD_ARG while decoding");
                }
                throw new OpusException("An error occurred during decoding", ret);
            }
            return ret;
        }
        catch (ArithmeticException e) {
            throw new OpusException("Internal error during decoding: " + e.getMessage());
        }
    }

    public int decode(byte[] in_data, int in_data_offset, int len, byte[] out_pcm, int out_pcm_offset, int frame_size, boolean decode_fec) throws OpusException {
        short[] spcm = new short[Math.min(frame_size, 5760) * this.channels];
        int decSamples = this.decode(in_data, in_data_offset, len, spcm, 0, frame_size, decode_fec);
        int idx = out_pcm_offset;
        for (int c = 0; c < spcm.length; ++c) {
            out_pcm[idx++] = (byte)(spcm[c] & 0xFF);
            out_pcm[idx++] = (byte)(spcm[c] >> 8 & 0xFF);
        }
        return decSamples;
    }

    public OpusBandwidth getBandwidth() {
        return this.bandwidth;
    }

    public int getFinalRange() {
        return this.rangeFinal;
    }

    public int getSampleRate() {
        return this.Fs;
    }

    public int getPitch() {
        if (this.prev_mode == OpusMode.MODE_CELT_ONLY) {
            return this.Celt_Decoder.GetPitch();
        }
        return this.DecControl.prevPitchLag;
    }

    public int getGain() {
        return this.decode_gain;
    }

    public void setGain(int value) {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Gain must be within the range of a signed int16");
        }
        this.decode_gain = value;
    }

    public int getLastPacketDuration() {
        return this.last_packet_duration;
    }

    public void resetState() {
        this.partialReset();
        this.Celt_Decoder.ResetState();
        DecodeAPI.silk_InitDecoder(this.SilkDecoder);
        this.stream_channels = this.channels;
        this.frame_size = this.Fs / 400;
    }
}
