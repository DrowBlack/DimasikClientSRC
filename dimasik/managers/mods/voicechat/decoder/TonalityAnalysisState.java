package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.AnalysisInfo;
import dimasik.managers.mods.voicechat.decoder.Arrays;

class TonalityAnalysisState {
    boolean enabled = false;
    final float[] angle = new float[240];
    final float[] d_angle = new float[240];
    final float[] d2_angle = new float[240];
    final int[] inmem = new int[720];
    int mem_fill;
    final float[] prev_band_tonality = new float[18];
    float prev_tonality;
    final float[][] E = Arrays.InitTwoDimensionalArrayFloat(8, 18);
    final float[] lowE = new float[18];
    final float[] highE = new float[18];
    final float[] meanE = new float[21];
    final float[] mem = new float[32];
    final float[] cmean = new float[8];
    final float[] std = new float[9];
    float music_prob;
    float Etracker;
    float lowECount;
    int E_count;
    int last_music;
    int last_transition;
    int count;
    final float[] subframe_mem = new float[3];
    int analysis_offset;
    final float[] pspeech = new float[200];
    final float[] pmusic = new float[200];
    float speech_confidence;
    float music_confidence;
    int speech_confidence_count;
    int music_confidence_count;
    int write_pos;
    int read_pos;
    int read_subframe;
    final AnalysisInfo[] info = new AnalysisInfo[200];

    TonalityAnalysisState() {
        for (int c = 0; c < 200; ++c) {
            this.info[c] = new AnalysisInfo();
        }
    }

    void Reset() {
        int c;
        Arrays.MemSet(this.angle, 0.0f, 240);
        Arrays.MemSet(this.d_angle, 0.0f, 240);
        Arrays.MemSet(this.d2_angle, 0.0f, 240);
        Arrays.MemSet(this.inmem, 0, 720);
        this.mem_fill = 0;
        Arrays.MemSet(this.prev_band_tonality, 0.0f, 18);
        this.prev_tonality = 0.0f;
        for (c = 0; c < 8; ++c) {
            Arrays.MemSet(this.E[c], 0.0f, 18);
        }
        Arrays.MemSet(this.lowE, 0.0f, 18);
        Arrays.MemSet(this.highE, 0.0f, 18);
        Arrays.MemSet(this.meanE, 0.0f, 21);
        Arrays.MemSet(this.mem, 0.0f, 32);
        Arrays.MemSet(this.cmean, 0.0f, 8);
        Arrays.MemSet(this.std, 0.0f, 9);
        this.music_prob = 0.0f;
        this.Etracker = 0.0f;
        this.lowECount = 0.0f;
        this.E_count = 0;
        this.last_music = 0;
        this.last_transition = 0;
        this.count = 0;
        Arrays.MemSet(this.subframe_mem, 0.0f, 3);
        this.analysis_offset = 0;
        Arrays.MemSet(this.pspeech, 0.0f, 200);
        Arrays.MemSet(this.pmusic, 0.0f, 200);
        this.speech_confidence = 0.0f;
        this.music_confidence = 0.0f;
        this.speech_confidence_count = 0;
        this.music_confidence_count = 0;
        this.write_pos = 0;
        this.read_pos = 0;
        this.read_subframe = 0;
        for (c = 0; c < 200; ++c) {
            this.info[c].Reset();
        }
    }
}
