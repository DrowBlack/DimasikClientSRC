package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.BoxedValueByte;
import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.ChannelLayout;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.OpusBandwidth;
import dimasik.managers.mods.voicechat.decoder.OpusDecoder;
import dimasik.managers.mods.voicechat.decoder.OpusError;
import dimasik.managers.mods.voicechat.decoder.OpusException;
import dimasik.managers.mods.voicechat.decoder.OpusMultistream;
import dimasik.managers.mods.voicechat.decoder.OpusPacketInfo;

public class OpusMSDecoder {
    ChannelLayout layout = new ChannelLayout();
    OpusDecoder[] decoders = null;

    private OpusMSDecoder(int nb_streams, int nb_coupled_streams) {
        this.decoders = new OpusDecoder[nb_streams];
        for (int c = 0; c < nb_streams; ++c) {
            this.decoders[c] = new OpusDecoder();
        }
    }

    int opus_multistream_decoder_init(int Fs, int channels, int streams, int coupled_streams, short[] mapping) {
        int ret;
        int i;
        int decoder_ptr = 0;
        if (channels > 255 || channels < 1 || coupled_streams > streams || streams < 1 || coupled_streams < 0 || streams > 255 - coupled_streams) {
            throw new IllegalArgumentException("Invalid channel or coupled stream count");
        }
        this.layout.nb_channels = channels;
        this.layout.nb_streams = streams;
        this.layout.nb_coupled_streams = coupled_streams;
        for (i = 0; i < this.layout.nb_channels; ++i) {
            this.layout.mapping[i] = mapping[i];
        }
        if (OpusMultistream.validate_layout(this.layout) == 0) {
            throw new IllegalArgumentException("Invalid surround channel layout");
        }
        for (i = 0; i < this.layout.nb_coupled_streams; ++i) {
            ret = this.decoders[decoder_ptr].opus_decoder_init(Fs, 2);
            if (ret != OpusError.OPUS_OK) {
                return ret;
            }
            ++decoder_ptr;
        }
        while (i < this.layout.nb_streams) {
            ret = this.decoders[decoder_ptr].opus_decoder_init(Fs, 1);
            if (ret != OpusError.OPUS_OK) {
                return ret;
            }
            ++decoder_ptr;
            ++i;
        }
        return OpusError.OPUS_OK;
    }

    public static OpusMSDecoder create(int Fs, int channels, int streams, int coupled_streams, short[] mapping) throws OpusException {
        if (channels > 255 || channels < 1 || coupled_streams > streams || streams < 1 || coupled_streams < 0 || streams > 255 - coupled_streams) {
            throw new IllegalArgumentException("Invalid channel / stream configuration");
        }
        OpusMSDecoder st = new OpusMSDecoder(streams, coupled_streams);
        int ret = st.opus_multistream_decoder_init(Fs, channels, streams, coupled_streams, mapping);
        if (ret != OpusError.OPUS_OK) {
            if (ret == OpusError.OPUS_BAD_ARG) {
                throw new IllegalArgumentException("Bad argument while creating MS decoder");
            }
            throw new OpusException("Could not create MS decoder", ret);
        }
        return st;
    }

    static int opus_multistream_packet_validate(byte[] data, int data_ptr, int len, int nb_streams, int Fs) {
        BoxedValueByte toc = new BoxedValueByte(0);
        short[] size = new short[48];
        int samples = 0;
        BoxedValueInt packet_offset = new BoxedValueInt(0);
        BoxedValueInt dummy = new BoxedValueInt(0);
        for (int s = 0; s < nb_streams; ++s) {
            if (len <= 0) {
                return OpusError.OPUS_INVALID_PACKET;
            }
            int count = OpusPacketInfo.opus_packet_parse_impl(data, data_ptr, len, s != nb_streams - 1 ? 1 : 0, toc, null, 0, size, 0, dummy, packet_offset);
            if (count < 0) {
                return count;
            }
            int tmp_samples = OpusPacketInfo.getNumSamples(data, data_ptr, packet_offset.Val, Fs);
            if (s != 0 && samples != tmp_samples) {
                return OpusError.OPUS_INVALID_PACKET;
            }
            samples = tmp_samples;
            data_ptr += packet_offset.Val;
            len -= packet_offset.Val;
        }
        return samples;
    }

    int opus_multistream_decode_native(byte[] data, int data_ptr, int len, short[] pcm, int pcm_ptr, int frame_size, int decode_fec, int soft_clip) {
        boolean do_plc = false;
        int Fs = this.getSampleRate();
        frame_size = Inlines.IMIN(frame_size, Fs / 25 * 3);
        short[] buf = new short[2 * frame_size];
        int decoder_ptr = 0;
        if (len == 0) {
            do_plc = true;
        }
        if (len < 0) {
            return OpusError.OPUS_BAD_ARG;
        }
        if (!do_plc && len < 2 * this.layout.nb_streams - 1) {
            return OpusError.OPUS_INVALID_PACKET;
        }
        if (!do_plc) {
            int ret = OpusMSDecoder.opus_multistream_packet_validate(data, data_ptr, len, this.layout.nb_streams, Fs);
            if (ret < 0) {
                return ret;
            }
            if (ret > frame_size) {
                return OpusError.OPUS_BUFFER_TOO_SMALL;
            }
        }
        for (int s = 0; s < this.layout.nb_streams; ++s) {
            int chan;
            int prev;
            OpusDecoder dec = this.decoders[decoder_ptr++];
            if (!do_plc && len <= 0) {
                return OpusError.OPUS_INTERNAL_ERROR;
            }
            BoxedValueInt packet_offset = new BoxedValueInt(0);
            int ret = dec.opus_decode_native(data, data_ptr, len, buf, 0, frame_size, decode_fec, s != this.layout.nb_streams - 1 ? 1 : 0, packet_offset, soft_clip);
            data_ptr += packet_offset.Val;
            len -= packet_offset.Val;
            if (ret <= 0) {
                return ret;
            }
            frame_size = ret;
            if (s < this.layout.nb_coupled_streams) {
                prev = -1;
                while ((chan = OpusMultistream.get_left_channel(this.layout, s, prev)) != -1) {
                    OpusMSDecoder.opus_copy_channel_out_short(pcm, pcm_ptr, this.layout.nb_channels, chan, buf, 0, 2, frame_size);
                    prev = chan;
                }
                prev = -1;
                while ((chan = OpusMultistream.get_right_channel(this.layout, s, prev)) != -1) {
                    OpusMSDecoder.opus_copy_channel_out_short(pcm, pcm_ptr, this.layout.nb_channels, chan, buf, 1, 2, frame_size);
                    prev = chan;
                }
                continue;
            }
            prev = -1;
            while ((chan = OpusMultistream.get_mono_channel(this.layout, s, prev)) != -1) {
                OpusMSDecoder.opus_copy_channel_out_short(pcm, pcm_ptr, this.layout.nb_channels, chan, buf, 0, 1, frame_size);
                prev = chan;
            }
        }
        for (int c = 0; c < this.layout.nb_channels; ++c) {
            if (this.layout.mapping[c] != 255) continue;
            OpusMSDecoder.opus_copy_channel_out_short(pcm, pcm_ptr, this.layout.nb_channels, c, null, 0, 0, frame_size);
        }
        return frame_size;
    }

    static void opus_copy_channel_out_short(short[] dst, int dst_ptr, int dst_stride, int dst_channel, short[] src, int src_ptr, int src_stride, int frame_size) {
        if (src != null) {
            for (int i = 0; i < frame_size; ++i) {
                dst[i * dst_stride + dst_channel + dst_ptr] = src[i * src_stride + src_ptr];
            }
        } else {
            for (int i = 0; i < frame_size; ++i) {
                dst[i * dst_stride + dst_channel + dst_ptr] = 0;
            }
        }
    }

    public int decodeMultistream(byte[] data, int data_offset, int len, short[] out_pcm, int out_pcm_offset, int frame_size, int decode_fec) {
        return this.opus_multistream_decode_native(data, data_offset, len, out_pcm, out_pcm_offset, frame_size, decode_fec, 0);
    }

    public OpusBandwidth getBandwidth() {
        if (this.decoders == null || this.decoders.length == 0) {
            throw new IllegalStateException("Decoder not initialized");
        }
        return this.decoders[0].getBandwidth();
    }

    public int getSampleRate() {
        if (this.decoders == null || this.decoders.length == 0) {
            throw new IllegalStateException("Decoder not initialized");
        }
        return this.decoders[0].getSampleRate();
    }

    public int getGain() {
        if (this.decoders == null || this.decoders.length == 0) {
            throw new IllegalStateException("Decoder not initialized");
        }
        return this.decoders[0].getGain();
    }

    public void setGain(int value) {
        for (int s = 0; s < this.layout.nb_streams; ++s) {
            this.decoders[s].setGain(value);
        }
    }

    public int getLastPacketDuration() {
        if (this.decoders == null || this.decoders.length == 0) {
            return OpusError.OPUS_INVALID_STATE;
        }
        return this.decoders[0].getLastPacketDuration();
    }

    public int getFinalRange() {
        int value = 0;
        for (int s = 0; s < this.layout.nb_streams; ++s) {
            value ^= this.decoders[s].getFinalRange();
        }
        return value;
    }

    public void ResetState() {
        for (int s = 0; s < this.layout.nb_streams; ++s) {
            this.decoders[s].resetState();
        }
    }

    public OpusDecoder GetMultistreamDecoderState(int streamId) {
        return this.decoders[streamId];
    }
}
