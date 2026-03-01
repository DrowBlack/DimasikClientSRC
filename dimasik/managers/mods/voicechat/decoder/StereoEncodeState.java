package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;

class StereoEncodeState {
    final short[] pred_prev_Q13 = new short[2];
    final short[] sMid = new short[2];
    final short[] sSide = new short[2];
    final int[] mid_side_amp_Q0 = new int[4];
    short smth_width_Q14 = 0;
    short width_prev_Q14 = 0;
    short silent_side_len = 0;
    final byte[][][] predIx = Arrays.InitThreeDimensionalArrayByte(3, 2, 3);
    final byte[] mid_only_flags = new byte[3];

    StereoEncodeState() {
    }

    void Reset() {
        Arrays.MemSet(this.pred_prev_Q13, (short)0, 2);
        Arrays.MemSet(this.sMid, (short)0, 2);
        Arrays.MemSet(this.sSide, (short)0, 2);
        Arrays.MemSet(this.mid_side_amp_Q0, 0, 4);
        this.smth_width_Q14 = 0;
        this.width_prev_Q14 = 0;
        this.silent_side_len = 0;
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 2; ++y) {
                Arrays.MemSet(this.predIx[x][y], (byte)0, 3);
            }
        }
        Arrays.MemSet(this.mid_only_flags, (byte)0, 3);
    }
}
