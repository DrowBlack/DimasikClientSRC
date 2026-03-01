package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.SilkChannelDecoder;
import dimasik.managers.mods.voicechat.decoder.StereoDecodeState;

class SilkDecoder {
    final SilkChannelDecoder[] channel_state = new SilkChannelDecoder[2];
    final StereoDecodeState sStereo = new StereoDecodeState();
    int nChannelsAPI = 0;
    int nChannelsInternal = 0;
    int prev_decode_only_middle = 0;

    SilkDecoder() {
        for (int c = 0; c < 2; ++c) {
            this.channel_state[c] = new SilkChannelDecoder();
        }
    }

    void Reset() {
        for (int c = 0; c < 2; ++c) {
            this.channel_state[c].Reset();
        }
        this.sStereo.Reset();
        this.nChannelsAPI = 0;
        this.nChannelsInternal = 0;
        this.prev_decode_only_middle = 0;
    }
}
