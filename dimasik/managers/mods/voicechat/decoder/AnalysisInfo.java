package dimasik.managers.mods.voicechat.decoder;

class AnalysisInfo {
    boolean enabled = false;
    int valid = 0;
    float tonality = 0.0f;
    float tonality_slope = 0.0f;
    float noisiness = 0.0f;
    float activity = 0.0f;
    float music_prob = 0.0f;
    int bandwidth = 0;

    AnalysisInfo() {
    }

    void Assign(AnalysisInfo other) {
        this.valid = other.valid;
        this.tonality = other.tonality;
        this.tonality_slope = other.tonality_slope;
        this.noisiness = other.noisiness;
        this.activity = other.activity;
        this.music_prob = other.music_prob;
        this.bandwidth = other.bandwidth;
    }

    void Reset() {
        this.valid = 0;
        this.tonality = 0.0f;
        this.tonality_slope = 0.0f;
        this.noisiness = 0.0f;
        this.activity = 0.0f;
        this.music_prob = 0.0f;
        this.bandwidth = 0;
    }
}
