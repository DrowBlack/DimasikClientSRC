package dimasik.managers.mods.voicechat.decoder;

class SilkShapeState {
    byte LastGainIndex = 0;
    int HarmBoost_smth_Q16 = 0;
    int HarmShapeGain_smth_Q16 = 0;
    int Tilt_smth_Q16 = 0;

    SilkShapeState() {
    }

    void Reset() {
        this.LastGainIndex = 0;
        this.HarmBoost_smth_Q16 = 0;
        this.HarmShapeGain_smth_Q16 = 0;
        this.Tilt_smth_Q16 = 0;
    }
}
