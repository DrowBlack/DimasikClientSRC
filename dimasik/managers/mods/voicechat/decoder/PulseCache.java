package dimasik.managers.mods.voicechat.decoder;

class PulseCache {
    int size = 0;
    short[] index = null;
    short[] bits = null;
    short[] caps = null;

    PulseCache() {
    }

    void Reset() {
        this.size = 0;
        this.index = null;
        this.bits = null;
        this.caps = null;
    }
}
