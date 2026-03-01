package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.CeltTables;
import dimasik.managers.mods.voicechat.decoder.FFTState;
import dimasik.managers.mods.voicechat.decoder.MDCTLookup;
import dimasik.managers.mods.voicechat.decoder.PulseCache;

class CeltMode {
    int Fs = 0;
    int overlap = 0;
    int nbEBands = 0;
    int effEBands = 0;
    int[] preemph = new int[]{0, 0, 0, 0};
    short[] eBands = null;
    int maxLM = 0;
    int nbShortMdcts = 0;
    int shortMdctSize = 0;
    int nbAllocVectors = 0;
    short[] allocVectors = null;
    short[] logN = null;
    int[] window = null;
    MDCTLookup mdct = new MDCTLookup();
    PulseCache cache = new PulseCache();
    static final CeltMode mode48000_960_120 = new CeltMode();

    private CeltMode() {
    }

    static {
        CeltMode.mode48000_960_120.Fs = 48000;
        CeltMode.mode48000_960_120.overlap = 120;
        CeltMode.mode48000_960_120.nbEBands = 21;
        CeltMode.mode48000_960_120.effEBands = 21;
        CeltMode.mode48000_960_120.preemph = new int[]{27853, 0, 4096, 8192};
        CeltMode.mode48000_960_120.eBands = CeltTables.eband5ms;
        CeltMode.mode48000_960_120.maxLM = 3;
        CeltMode.mode48000_960_120.nbShortMdcts = 8;
        CeltMode.mode48000_960_120.shortMdctSize = 120;
        CeltMode.mode48000_960_120.nbAllocVectors = 11;
        CeltMode.mode48000_960_120.allocVectors = CeltTables.band_allocation;
        CeltMode.mode48000_960_120.logN = CeltTables.logN400;
        CeltMode.mode48000_960_120.window = CeltTables.window120;
        CeltMode.mode48000_960_120.mdct = new MDCTLookup();
        CeltMode.mode48000_960_120.mdct.n = 1920;
        CeltMode.mode48000_960_120.mdct.maxshift = 3;
        CeltMode.mode48000_960_120.mdct.kfft = new FFTState[]{CeltTables.fft_state48000_960_0, CeltTables.fft_state48000_960_1, CeltTables.fft_state48000_960_2, CeltTables.fft_state48000_960_3};
        CeltMode.mode48000_960_120.mdct.trig = CeltTables.mdct_twiddles960;
        CeltMode.mode48000_960_120.cache = new PulseCache();
        CeltMode.mode48000_960_120.cache.size = 392;
        CeltMode.mode48000_960_120.cache.index = CeltTables.cache_index50;
        CeltMode.mode48000_960_120.cache.bits = CeltTables.cache_bits50;
        CeltMode.mode48000_960_120.cache.caps = CeltTables.cache_caps50;
    }
}
