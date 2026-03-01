package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;

class TOCStruct {
    int VADFlag = 0;
    final int[] VADFlags = new int[3];
    int inbandFECFlag = 0;

    TOCStruct() {
    }

    void Reset() {
        this.VADFlag = 0;
        Arrays.MemSet(this.VADFlags, 0, 3);
        this.inbandFECFlag = 0;
    }
}
