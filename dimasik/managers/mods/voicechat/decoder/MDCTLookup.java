package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.FFTState;

class MDCTLookup {
    int n = 0;
    int maxshift = 0;
    FFTState[] kfft = new FFTState[4];
    short[] trig = null;

    MDCTLookup() {
    }
}
