package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;

class StereoDecodeState {
    final short[] pred_prev_Q13 = new short[2];
    final short[] sMid = new short[2];
    final short[] sSide = new short[2];

    StereoDecodeState() {
    }

    void Reset() {
        Arrays.MemSet(this.pred_prev_Q13, (short)0, 2);
        Arrays.MemSet(this.sMid, (short)0, 2);
        Arrays.MemSet(this.sSide, (short)0, 2);
    }
}
