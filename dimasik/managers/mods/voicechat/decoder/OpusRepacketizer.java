package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;
import dimasik.managers.mods.voicechat.decoder.BoxedValueByte;
import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.OpusError;
import dimasik.managers.mods.voicechat.decoder.OpusPacketInfo;

public class OpusRepacketizer {
    byte toc = 0;
    int nb_frames = 0;
    final byte[][] frames = new byte[48][];
    final short[] len = new short[48];
    int framesize = 0;

    public void Reset() {
        this.nb_frames = 0;
    }

    public OpusRepacketizer() {
        this.Reset();
    }

    int opus_repacketizer_cat_impl(byte[] data, int data_ptr, int len, int self_delimited) {
        BoxedValueByte dummy_toc = new BoxedValueByte(0);
        BoxedValueInt dummy_offset = new BoxedValueInt(0);
        if (len < 1) {
            return OpusError.OPUS_INVALID_PACKET;
        }
        if (this.nb_frames == 0) {
            this.toc = data[data_ptr];
            this.framesize = OpusPacketInfo.getNumSamplesPerFrame(data, data_ptr, 8000);
        } else if ((this.toc & 0xFC) != (data[data_ptr] & 0xFC)) {
            return OpusError.OPUS_INVALID_PACKET;
        }
        int curr_nb_frames = OpusPacketInfo.getNumFrames(data, data_ptr, len);
        if (curr_nb_frames < 1) {
            return OpusError.OPUS_INVALID_PACKET;
        }
        if ((curr_nb_frames + this.nb_frames) * this.framesize > 960) {
            return OpusError.OPUS_INVALID_PACKET;
        }
        int ret = OpusPacketInfo.opus_packet_parse_impl(data, data_ptr, len, self_delimited, dummy_toc, this.frames, this.nb_frames, this.len, this.nb_frames, dummy_offset, dummy_offset);
        if (ret < 1) {
            return ret;
        }
        this.nb_frames += curr_nb_frames;
        return OpusError.OPUS_OK;
    }

    public int addPacket(byte[] data, int data_offset, int len) {
        return this.opus_repacketizer_cat_impl(data, data_offset, len, 0);
    }

    public int getNumFrames() {
        return this.nb_frames;
    }

    int opus_repacketizer_out_range_impl(int begin, int end, byte[] data, int data_ptr, int maxlen, int self_delimited, int pad) {
        int i;
        if (begin < 0 || begin >= end || end > this.nb_frames) {
            return OpusError.OPUS_BAD_ARG;
        }
        int count = end - begin;
        int tot_size = self_delimited != 0 ? 1 + (this.len[count - 1] >= 252 ? 1 : 0) : 0;
        int ptr = data_ptr;
        if (count == 1) {
            if ((tot_size += this.len[0] + 1) > maxlen) {
                return OpusError.OPUS_BUFFER_TOO_SMALL;
            }
            data[ptr++] = (byte)(this.toc & 0xFC);
        } else if (count == 2) {
            if (this.len[1] == this.len[0]) {
                if ((tot_size += 2 * this.len[0] + 1) > maxlen) {
                    return OpusError.OPUS_BUFFER_TOO_SMALL;
                }
                data[ptr++] = (byte)(this.toc & 0xFC | 1);
            } else {
                if ((tot_size += this.len[0] + this.len[1] + 2 + (this.len[0] >= 252 ? 1 : 0)) > maxlen) {
                    return OpusError.OPUS_BUFFER_TOO_SMALL;
                }
                data[ptr++] = (byte)(this.toc & 0xFC | 2);
                ptr += OpusPacketInfo.encode_size(this.len[0], data, ptr);
            }
        }
        if (count > 2 || pad != 0 && tot_size < maxlen) {
            int pad_amount = 0;
            ptr = data_ptr;
            tot_size = self_delimited != 0 ? 1 + (this.len[count - 1] >= 252 ? 1 : 0) : 0;
            boolean vbr = false;
            for (i = 1; i < count; ++i) {
                if (this.len[i] == this.len[0]) continue;
                vbr = true;
                break;
            }
            if (vbr) {
                tot_size += 2;
                for (i = 0; i < count - 1; ++i) {
                    tot_size += 1 + (this.len[i] >= 252 ? 1 : 0) + this.len[i];
                }
                if ((tot_size += this.len[count - 1]) > maxlen) {
                    return OpusError.OPUS_BUFFER_TOO_SMALL;
                }
                data[ptr++] = (byte)(this.toc & 0xFC | 3);
                data[ptr++] = (byte)(count | 0x80);
            } else {
                if ((tot_size += count * this.len[0] + 2) > maxlen) {
                    return OpusError.OPUS_BUFFER_TOO_SMALL;
                }
                data[ptr++] = (byte)(this.toc & 0xFC | 3);
                data[ptr++] = (byte)count;
            }
            int n = pad_amount = pad != 0 ? maxlen - tot_size : 0;
            if (pad_amount != 0) {
                data[data_ptr + 1] = (byte)(data[data_ptr + 1] | 0x40);
                int nb_255s = (pad_amount - 1) / 255;
                for (i = 0; i < nb_255s; ++i) {
                    data[ptr++] = -1;
                }
                data[ptr++] = (byte)(pad_amount - 255 * nb_255s - 1);
                tot_size += pad_amount;
            }
            if (vbr) {
                for (i = 0; i < count - 1; ++i) {
                    ptr += OpusPacketInfo.encode_size(this.len[i], data, ptr);
                }
            }
        }
        if (self_delimited != 0) {
            int sdlen = OpusPacketInfo.encode_size(this.len[count - 1], data, ptr);
            ptr += sdlen;
        }
        for (i = begin; i < count + begin; ++i) {
            if (data == this.frames[i]) {
                Arrays.MemMove(data, 0, ptr, (int)this.len[i]);
            } else {
                System.arraycopy(this.frames[i], 0, data, ptr, this.len[i]);
            }
            ptr += this.len[i];
        }
        if (pad != 0) {
            Arrays.MemSetWithOffset(data, (byte)0, ptr, data_ptr + maxlen - ptr);
        }
        return tot_size;
    }

    public int createPacket(int begin, int end, byte[] data, int data_offset, int maxlen) {
        return this.opus_repacketizer_out_range_impl(begin, end, data, data_offset, maxlen, 0, 0);
    }

    public int createPacket(byte[] data, int data_offset, int maxlen) {
        return this.opus_repacketizer_out_range_impl(0, this.nb_frames, data, data_offset, maxlen, 0, 0);
    }

    public static int padPacket(byte[] data, int data_offset, int len, int new_len) {
        OpusRepacketizer rp = new OpusRepacketizer();
        if (len < 1) {
            return OpusError.OPUS_BAD_ARG;
        }
        if (len == new_len) {
            return OpusError.OPUS_OK;
        }
        if (len > new_len) {
            return OpusError.OPUS_BAD_ARG;
        }
        rp.Reset();
        Arrays.MemMove(data, data_offset, data_offset + new_len - len, len);
        rp.addPacket(data, data_offset + new_len - len, len);
        int ret = rp.opus_repacketizer_out_range_impl(0, rp.nb_frames, data, data_offset, new_len, 0, 1);
        if (ret > 0) {
            return OpusError.OPUS_OK;
        }
        return ret;
    }

    public static int unpadPacket(byte[] data, int data_offset, int len) {
        if (len < 1) {
            return OpusError.OPUS_BAD_ARG;
        }
        OpusRepacketizer rp = new OpusRepacketizer();
        rp.Reset();
        int ret = rp.addPacket(data, data_offset, len);
        if (ret < 0) {
            return ret;
        }
        ret = rp.opus_repacketizer_out_range_impl(0, rp.nb_frames, data, data_offset, len, 0, 0);
        Inlines.OpusAssert(ret > 0 && ret <= len);
        return ret;
    }

    public static int padMultistreamPacket(byte[] data, int data_offset, int len, int new_len, int nb_streams) {
        BoxedValueByte dummy_toc = new BoxedValueByte(0);
        short[] size = new short[48];
        BoxedValueInt packet_offset = new BoxedValueInt(0);
        BoxedValueInt dummy_offset = new BoxedValueInt(0);
        if (len < 1) {
            return OpusError.OPUS_BAD_ARG;
        }
        if (len == new_len) {
            return OpusError.OPUS_OK;
        }
        if (len > new_len) {
            return OpusError.OPUS_BAD_ARG;
        }
        int amount = new_len - len;
        for (int s = 0; s < nb_streams - 1; ++s) {
            if (len <= 0) {
                return OpusError.OPUS_INVALID_PACKET;
            }
            int count = OpusPacketInfo.opus_packet_parse_impl(data, data_offset, len, 1, dummy_toc, null, 0, size, 0, dummy_offset, packet_offset);
            if (count < 0) {
                return count;
            }
            data_offset += packet_offset.Val;
            len -= packet_offset.Val;
        }
        return OpusRepacketizer.padPacket(data, data_offset, len, len + amount);
    }

    public static int unpadMultistreamPacket(byte[] data, int data_offset, int len, int nb_streams) {
        BoxedValueByte dummy_toc = new BoxedValueByte(0);
        short[] size = new short[48];
        BoxedValueInt packet_offset = new BoxedValueInt(0);
        BoxedValueInt dummy_offset = new BoxedValueInt(0);
        OpusRepacketizer rp = new OpusRepacketizer();
        if (len < 1) {
            return OpusError.OPUS_BAD_ARG;
        }
        int dst = data_offset;
        int dst_len = 0;
        for (int s = 0; s < nb_streams; ++s) {
            int self_delimited = (s != nb_streams ? 1 : 0) - 1;
            if (len <= 0) {
                return OpusError.OPUS_INVALID_PACKET;
            }
            rp.Reset();
            int ret = OpusPacketInfo.opus_packet_parse_impl(data, data_offset, len, self_delimited, dummy_toc, null, 0, size, 0, dummy_offset, packet_offset);
            if (ret < 0) {
                return ret;
            }
            ret = rp.opus_repacketizer_cat_impl(data, data_offset, packet_offset.Val, self_delimited);
            if (ret < 0) {
                return ret;
            }
            ret = rp.opus_repacketizer_out_range_impl(0, rp.nb_frames, data, dst, len, self_delimited, 0);
            if (ret < 0) {
                return ret;
            }
            dst_len += ret;
            dst += ret;
            data_offset += packet_offset.Val;
            len -= packet_offset.Val;
        }
        return dst_len;
    }
}
