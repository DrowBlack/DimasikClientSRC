package dimasik.managers.mods.voicechat.decoder;

class DecControlState {
    int nChannelsAPI = 0;
    int nChannelsInternal = 0;
    int API_sampleRate = 0;
    int internalSampleRate = 0;
    int payloadSize_ms = 0;
    int prevPitchLag = 0;

    DecControlState() {
    }

    void Reset() {
        this.nChannelsAPI = 0;
        this.nChannelsInternal = 0;
        this.API_sampleRate = 0;
        this.internalSampleRate = 0;
        this.payloadSize_ms = 0;
        this.prevPitchLag = 0;
    }
}
