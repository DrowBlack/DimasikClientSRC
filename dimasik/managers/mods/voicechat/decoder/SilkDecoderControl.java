package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;

class SilkDecoderControl {
    final int[] pitchL = new int[4];
    final int[] Gains_Q16 = new int[4];
    final short[][] PredCoef_Q12 = Arrays.InitTwoDimensionalArrayShort(2, 16);
    final short[] LTPCoef_Q14 = new short[20];
    int LTP_scale_Q14 = 0;

    SilkDecoderControl() {
    }

    void Reset() {
        Arrays.MemSet(this.pitchL, 0, 4);
        Arrays.MemSet(this.Gains_Q16, 0, 4);
        Arrays.MemSet(this.PredCoef_Q12[0], (short)0, 16);
        Arrays.MemSet(this.PredCoef_Q12[1], (short)0, 16);
        Arrays.MemSet(this.LTPCoef_Q14, (short)0, 20);
        this.LTP_scale_Q14 = 0;
    }
}
