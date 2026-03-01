package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;

class SilkVADState {
    final int[] AnaState = new int[2];
    final int[] AnaState1 = new int[2];
    final int[] AnaState2 = new int[2];
    final int[] XnrgSubfr = new int[4];
    final int[] NrgRatioSmth_Q8 = new int[4];
    short HPstate = 0;
    final int[] NL = new int[4];
    final int[] inv_NL = new int[4];
    final int[] NoiseLevelBias = new int[4];
    int counter = 0;

    SilkVADState() {
    }

    void Reset() {
        Arrays.MemSet(this.AnaState, 0, 2);
        Arrays.MemSet(this.AnaState1, 0, 2);
        Arrays.MemSet(this.AnaState2, 0, 2);
        Arrays.MemSet(this.XnrgSubfr, 0, 4);
        Arrays.MemSet(this.NrgRatioSmth_Q8, 0, 4);
        this.HPstate = 0;
        Arrays.MemSet(this.NL, 0, 4);
        Arrays.MemSet(this.inv_NL, 0, 4);
        Arrays.MemSet(this.NoiseLevelBias, 0, 4);
        this.counter = 0;
    }
}
