package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;

class SilkEncoderControl {
    final int[] Gains_Q16 = new int[4];
    final short[][] PredCoef_Q12 = Arrays.InitTwoDimensionalArrayShort(2, 16);
    final short[] LTPCoef_Q14 = new short[20];
    int LTP_scale_Q14 = 0;
    final int[] pitchL = new int[4];
    final short[] AR1_Q13 = new short[64];
    final short[] AR2_Q13 = new short[64];
    final int[] LF_shp_Q14 = new int[4];
    final int[] GainsPre_Q14 = new int[4];
    final int[] HarmBoost_Q14 = new int[4];
    final int[] Tilt_Q14 = new int[4];
    final int[] HarmShapeGain_Q14 = new int[4];
    int Lambda_Q10 = 0;
    int input_quality_Q14 = 0;
    int coding_quality_Q14 = 0;
    int sparseness_Q8 = 0;
    int predGain_Q16 = 0;
    int LTPredCodGain_Q7 = 0;
    final int[] ResNrg = new int[4];
    final int[] ResNrgQ = new int[4];
    final int[] GainsUnq_Q16 = new int[4];
    byte lastGainIndexPrev = 0;

    SilkEncoderControl() {
    }

    void Reset() {
        Arrays.MemSet(this.Gains_Q16, 0, 4);
        Arrays.MemSet(this.PredCoef_Q12[0], (short)0, 16);
        Arrays.MemSet(this.PredCoef_Q12[1], (short)0, 16);
        Arrays.MemSet(this.LTPCoef_Q14, (short)0, 20);
        this.LTP_scale_Q14 = 0;
        Arrays.MemSet(this.pitchL, 0, 4);
        Arrays.MemSet(this.AR1_Q13, (short)0, 64);
        Arrays.MemSet(this.AR2_Q13, (short)0, 64);
        Arrays.MemSet(this.LF_shp_Q14, 0, 4);
        Arrays.MemSet(this.GainsPre_Q14, 0, 4);
        Arrays.MemSet(this.HarmBoost_Q14, 0, 4);
        Arrays.MemSet(this.Tilt_Q14, 0, 4);
        Arrays.MemSet(this.HarmShapeGain_Q14, 0, 4);
        this.Lambda_Q10 = 0;
        this.input_quality_Q14 = 0;
        this.coding_quality_Q14 = 0;
        this.sparseness_Q8 = 0;
        this.predGain_Q16 = 0;
        this.LTPredCodGain_Q7 = 0;
        Arrays.MemSet(this.ResNrg, 0, 4);
        Arrays.MemSet(this.ResNrgQ, 0, 4);
        Arrays.MemSet(this.GainsUnq_Q16, 0, 4);
        this.lastGainIndexPrev = 0;
    }
}
