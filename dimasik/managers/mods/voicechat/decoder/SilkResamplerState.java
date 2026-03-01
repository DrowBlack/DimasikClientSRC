package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;

class SilkResamplerState {
    final int[] sIIR = new int[6];
    final int[] sFIR_i32 = new int[36];
    final short[] sFIR_i16 = new short[36];
    final short[] delayBuf = new short[48];
    int resampler_function = 0;
    int batchSize = 0;
    int invRatio_Q16 = 0;
    int FIR_Order = 0;
    int FIR_Fracs = 0;
    int Fs_in_kHz = 0;
    int Fs_out_kHz = 0;
    int inputDelay = 0;
    short[] Coefs = null;

    SilkResamplerState() {
    }

    void Reset() {
        Arrays.MemSet(this.sIIR, 0, 6);
        Arrays.MemSet(this.sFIR_i32, 0, 36);
        Arrays.MemSet(this.sFIR_i16, (short)0, 36);
        Arrays.MemSet(this.delayBuf, (short)0, 48);
        this.resampler_function = 0;
        this.batchSize = 0;
        this.invRatio_Q16 = 0;
        this.FIR_Order = 0;
        this.FIR_Fracs = 0;
        this.Fs_in_kHz = 0;
        this.Fs_out_kHz = 0;
        this.inputDelay = 0;
        this.Coefs = null;
    }

    void Assign(SilkResamplerState other) {
        this.resampler_function = other.resampler_function;
        this.batchSize = other.batchSize;
        this.invRatio_Q16 = other.invRatio_Q16;
        this.FIR_Order = other.FIR_Order;
        this.FIR_Fracs = other.FIR_Fracs;
        this.Fs_in_kHz = other.Fs_in_kHz;
        this.Fs_out_kHz = other.Fs_out_kHz;
        this.inputDelay = other.inputDelay;
        this.Coefs = other.Coefs;
        System.arraycopy(other.sIIR, 0, this.sIIR, 0, 6);
        System.arraycopy(other.sFIR_i32, 0, this.sFIR_i32, 0, 36);
        System.arraycopy(other.sFIR_i16, 0, this.sFIR_i16, 0, 36);
        System.arraycopy(other.delayBuf, 0, this.delayBuf, 0, 48);
    }
}
