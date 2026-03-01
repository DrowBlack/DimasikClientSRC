package dimasik.managers.mods.voicechat.decoder;

class TuningParameters {
    static final int BITRESERVOIR_DECAY_TIME_MS = 500;
    static final float FIND_PITCH_WHITE_NOISE_FRACTION = 0.001f;
    static final float FIND_PITCH_BANDWIDTH_EXPANSION = 0.99f;
    static final float FIND_LPC_COND_FAC = 1.0E-5f;
    static final float FIND_LTP_COND_FAC = 1.0E-5f;
    static final float LTP_DAMPING = 0.05f;
    static final float LTP_SMOOTHING = 0.1f;
    static final float MU_LTP_QUANT_NB = 0.03f;
    static final float MU_LTP_QUANT_MB = 0.025f;
    static final float MU_LTP_QUANT_WB = 0.02f;
    static final float MAX_SUM_LOG_GAIN_DB = 250.0f;
    static final float VARIABLE_HP_SMTH_COEF1 = 0.1f;
    static final float VARIABLE_HP_SMTH_COEF2 = 0.015f;
    static final float VARIABLE_HP_MAX_DELTA_FREQ = 0.4f;
    static final int VARIABLE_HP_MIN_CUTOFF_HZ = 60;
    static final int VARIABLE_HP_MAX_CUTOFF_HZ = 100;
    static final float SPEECH_ACTIVITY_DTX_THRES = 0.05f;
    static final float LBRR_SPEECH_ACTIVITY_THRES = 0.3f;
    static final float BG_SNR_DECR_dB = 2.0f;
    static final float HARM_SNR_INCR_dB = 2.0f;
    static final float SPARSE_SNR_INCR_dB = 2.0f;
    static final float SPARSENESS_THRESHOLD_QNT_OFFSET = 0.75f;
    static final float WARPING_MULTIPLIER = 0.015f;
    static final float SHAPE_WHITE_NOISE_FRACTION = 5.0E-5f;
    static final float BANDWIDTH_EXPANSION = 0.95f;
    static final float LOW_RATE_BANDWIDTH_EXPANSION_DELTA = 0.01f;
    static final float LOW_RATE_HARMONIC_BOOST = 0.1f;
    static final float LOW_INPUT_QUALITY_HARMONIC_BOOST = 0.1f;
    static final float HARMONIC_SHAPING = 0.3f;
    static final float HIGH_RATE_OR_LOW_QUALITY_HARMONIC_SHAPING = 0.2f;
    static final float HP_NOISE_COEF = 0.25f;
    static final float HARM_HP_NOISE_COEF = 0.35f;
    static final float INPUT_TILT = 0.05f;
    static final float HIGH_RATE_INPUT_TILT = 0.1f;
    static final float LOW_FREQ_SHAPING = 4.0f;
    static final float LOW_QUALITY_LOW_FREQ_SHAPING_DECR = 0.5f;
    static final float SUBFR_SMTH_COEF = 0.4f;
    static final float LAMBDA_OFFSET = 1.2f;
    static final float LAMBDA_SPEECH_ACT = -0.2f;
    static final float LAMBDA_DELAYED_DECISIONS = -0.05f;
    static final float LAMBDA_INPUT_QUALITY = -0.1f;
    static final float LAMBDA_CODING_QUALITY = -0.2f;
    static final float LAMBDA_QUANT_OFFSET = 0.8f;
    static final int REDUCE_BITRATE_10_MS_BPS = 2200;
    static final int MAX_BANDWIDTH_SWITCH_DELAY_MS = 5000;

    TuningParameters() {
    }
}
