package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.SilkChannelEncoder;
import dimasik.managers.mods.voicechat.decoder.StereoEncodeState;
import dimasik.managers.mods.voicechat.decoder.VoiceActivityDetection;

class SilkEncoder {
    final SilkChannelEncoder[] state_Fxx = new SilkChannelEncoder[2];
    final StereoEncodeState sStereo = new StereoEncodeState();
    int nBitsUsedLBRR = 0;
    int nBitsExceeded = 0;
    int nChannelsAPI = 0;
    int nChannelsInternal = 0;
    int nPrevChannelsInternal = 0;
    int timeSinceSwitchAllowed_ms = 0;
    int allowBandwidthSwitch = 0;
    int prev_decode_only_middle = 0;

    SilkEncoder() {
        for (int c = 0; c < 2; ++c) {
            this.state_Fxx[c] = new SilkChannelEncoder();
        }
    }

    void Reset() {
        for (int c = 0; c < 2; ++c) {
            this.state_Fxx[c].Reset();
        }
        this.sStereo.Reset();
        this.nBitsUsedLBRR = 0;
        this.nBitsExceeded = 0;
        this.nChannelsAPI = 0;
        this.nChannelsInternal = 0;
        this.nPrevChannelsInternal = 0;
        this.timeSinceSwitchAllowed_ms = 0;
        this.allowBandwidthSwitch = 0;
        this.prev_decode_only_middle = 0;
    }

    static int silk_init_encoder(SilkChannelEncoder psEnc) {
        int ret = 0;
        psEnc.Reset();
        psEnc.variable_HP_smth2_Q15 = psEnc.variable_HP_smth1_Q15 = Inlines.silk_LSHIFT(Inlines.silk_lin2log(0x3C0000) - 2048, 8);
        psEnc.first_frame_after_reset = 1;
        return ret += VoiceActivityDetection.silk_VAD_Init(psEnc.sVAD);
    }
}
