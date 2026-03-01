package dimasik.managers.mods.voicechat.decoder;

class CeltConstants {
    public static final int Q15ONE = Short.MAX_VALUE;
    public static final float CELT_SIG_SCALE = 32768.0f;
    public static final int SIG_SHIFT = 12;
    public static final int NORM_SCALING = 16384;
    public static final int DB_SHIFT = 10;
    public static final int EPSILON = 1;
    public static final int VERY_SMALL = 0;
    public static final short VERY_LARGE16 = Short.MAX_VALUE;
    public static final short Q15_ONE = Short.MAX_VALUE;
    public static final int COMBFILTER_MAXPERIOD = 1024;
    public static final int COMBFILTER_MINPERIOD = 15;
    public static final int DECODE_BUFFER_SIZE = 2048;
    public static final int BITALLOC_SIZE = 11;
    public static final int MAX_PERIOD = 1024;
    public static final int TOTAL_MODES = 1;
    public static final int MAX_PSEUDO = 40;
    public static final int LOG_MAX_PSEUDO = 6;
    public static final int CELT_MAX_PULSES = 128;
    public static final int MAX_FINE_BITS = 8;
    public static final int FINE_OFFSET = 21;
    public static final int QTHETA_OFFSET = 4;
    public static final int QTHETA_OFFSET_TWOPHASE = 16;
    public static final int PLC_PITCH_LAG_MAX = 720;
    public static final int PLC_PITCH_LAG_MIN = 100;
    public static final int LPC_ORDER = 24;

    CeltConstants() {
    }
}
