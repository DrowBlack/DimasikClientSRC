package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;

class CNGState {
    final int[] CNG_exc_buf_Q14 = new int[320];
    final short[] CNG_smth_NLSF_Q15 = new short[16];
    final int[] CNG_synth_state = new int[16];
    int CNG_smth_Gain_Q16 = 0;
    int rand_seed = 0;
    int fs_kHz = 0;

    CNGState() {
    }

    void Reset() {
        Arrays.MemSet(this.CNG_exc_buf_Q14, 0, 320);
        Arrays.MemSet(this.CNG_smth_NLSF_Q15, (short)0, 16);
        Arrays.MemSet(this.CNG_synth_state, 0, 16);
        this.CNG_smth_Gain_Q16 = 0;
        this.rand_seed = 0;
        this.fs_kHz = 0;
    }
}
