package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;

class PLCStruct {
    int pitchL_Q8 = 0;
    final short[] LTPCoef_Q14 = new short[5];
    final short[] prevLPC_Q12 = new short[16];
    int last_frame_lost = 0;
    int rand_seed = 0;
    short randScale_Q14 = 0;
    int conc_energy = 0;
    int conc_energy_shift = 0;
    short prevLTP_scale_Q14 = 0;
    final int[] prevGain_Q16 = new int[2];
    int fs_kHz = 0;
    int nb_subfr = 0;
    int subfr_length = 0;

    PLCStruct() {
    }

    void Reset() {
        this.pitchL_Q8 = 0;
        Arrays.MemSet(this.LTPCoef_Q14, (short)0, 5);
        Arrays.MemSet(this.prevLPC_Q12, (short)0, 16);
        this.last_frame_lost = 0;
        this.rand_seed = 0;
        this.randScale_Q14 = 0;
        this.conc_energy = 0;
        this.conc_energy_shift = 0;
        this.prevLTP_scale_Q14 = 0;
        Arrays.MemSet(this.prevGain_Q16, 0, 2);
        this.fs_kHz = 0;
        this.nb_subfr = 0;
        this.subfr_length = 0;
    }
}
