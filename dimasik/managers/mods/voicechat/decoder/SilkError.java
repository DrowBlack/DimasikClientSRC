package dimasik.managers.mods.voicechat.decoder;

class SilkError {
    static int SILK_NO_ERROR = 0;
    static int SILK_ENC_INPUT_INVALID_NO_OF_SAMPLES = -101;
    static int SILK_ENC_FS_NOT_SUPPORTED = -102;
    static int SILK_ENC_PACKET_SIZE_NOT_SUPPORTED = -103;
    static int SILK_ENC_PAYLOAD_BUF_TOO_SHORT = -104;
    static int SILK_ENC_INVALID_LOSS_RATE = -105;
    static int SILK_ENC_INVALID_COMPLEXITY_SETTING = -106;
    static int SILK_ENC_INVALID_INBAND_FEC_SETTING = -107;
    static int SILK_ENC_INVALID_DTX_SETTING = -108;
    static int SILK_ENC_INVALID_CBR_SETTING = -109;
    static int SILK_ENC_INTERNAL_ERROR = -110;
    static int SILK_ENC_INVALID_NUMBER_OF_CHANNELS_ERROR = -111;
    static int SILK_DEC_INVALID_SAMPLING_FREQUENCY = -200;
    static int SILK_DEC_PAYLOAD_TOO_LARGE = -201;
    static int SILK_DEC_PAYLOAD_ERROR = -202;
    static int SILK_DEC_INVALID_FRAME_SIZE = -203;

    SilkError() {
    }
}
