package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.SilkError;

class EncControlState {
    int nChannelsAPI = 0;
    int nChannelsInternal = 0;
    int API_sampleRate = 0;
    int maxInternalSampleRate = 0;
    int minInternalSampleRate = 0;
    int desiredInternalSampleRate = 0;
    int payloadSize_ms = 0;
    int bitRate = 0;
    int packetLossPercentage = 0;
    int complexity = 0;
    int useInBandFEC = 0;
    int useDTX = 0;
    int useCBR = 0;
    int maxBits = 0;
    int toMono = 0;
    int opusCanSwitch = 0;
    int reducedDependency = 0;
    int internalSampleRate = 0;
    int allowBandwidthSwitch = 0;
    int inWBmodeWithoutVariableLP = 0;
    int stereoWidth_Q14 = 0;
    int switchReady = 0;

    EncControlState() {
    }

    void Reset() {
        this.nChannelsAPI = 0;
        this.nChannelsInternal = 0;
        this.API_sampleRate = 0;
        this.maxInternalSampleRate = 0;
        this.minInternalSampleRate = 0;
        this.desiredInternalSampleRate = 0;
        this.payloadSize_ms = 0;
        this.bitRate = 0;
        this.packetLossPercentage = 0;
        this.complexity = 0;
        this.useInBandFEC = 0;
        this.useDTX = 0;
        this.useCBR = 0;
        this.maxBits = 0;
        this.toMono = 0;
        this.opusCanSwitch = 0;
        this.reducedDependency = 0;
        this.internalSampleRate = 0;
        this.allowBandwidthSwitch = 0;
        this.inWBmodeWithoutVariableLP = 0;
        this.stereoWidth_Q14 = 0;
        this.switchReady = 0;
    }

    int check_control_input() {
        if (this.API_sampleRate != 8000 && this.API_sampleRate != 12000 && this.API_sampleRate != 16000 && this.API_sampleRate != 24000 && this.API_sampleRate != 32000 && this.API_sampleRate != 44100 && this.API_sampleRate != 48000 || this.desiredInternalSampleRate != 8000 && this.desiredInternalSampleRate != 12000 && this.desiredInternalSampleRate != 16000 || this.maxInternalSampleRate != 8000 && this.maxInternalSampleRate != 12000 && this.maxInternalSampleRate != 16000 || this.minInternalSampleRate != 8000 && this.minInternalSampleRate != 12000 && this.minInternalSampleRate != 16000 || this.minInternalSampleRate > this.desiredInternalSampleRate || this.maxInternalSampleRate < this.desiredInternalSampleRate || this.minInternalSampleRate > this.maxInternalSampleRate) {
            Inlines.OpusAssert(false);
            return SilkError.SILK_ENC_FS_NOT_SUPPORTED;
        }
        if (this.payloadSize_ms != 10 && this.payloadSize_ms != 20 && this.payloadSize_ms != 40 && this.payloadSize_ms != 60) {
            Inlines.OpusAssert(false);
            return SilkError.SILK_ENC_PACKET_SIZE_NOT_SUPPORTED;
        }
        if (this.packetLossPercentage < 0 || this.packetLossPercentage > 100) {
            Inlines.OpusAssert(false);
            return SilkError.SILK_ENC_INVALID_LOSS_RATE;
        }
        if (this.useDTX < 0 || this.useDTX > 1) {
            Inlines.OpusAssert(false);
            return SilkError.SILK_ENC_INVALID_DTX_SETTING;
        }
        if (this.useCBR < 0 || this.useCBR > 1) {
            Inlines.OpusAssert(false);
            return SilkError.SILK_ENC_INVALID_CBR_SETTING;
        }
        if (this.useInBandFEC < 0 || this.useInBandFEC > 1) {
            Inlines.OpusAssert(false);
            return SilkError.SILK_ENC_INVALID_INBAND_FEC_SETTING;
        }
        if (this.nChannelsAPI < 1 || this.nChannelsAPI > 2) {
            Inlines.OpusAssert(false);
            return SilkError.SILK_ENC_INVALID_NUMBER_OF_CHANNELS_ERROR;
        }
        if (this.nChannelsInternal < 1 || this.nChannelsInternal > 2) {
            Inlines.OpusAssert(false);
            return SilkError.SILK_ENC_INVALID_NUMBER_OF_CHANNELS_ERROR;
        }
        if (this.nChannelsInternal > this.nChannelsAPI) {
            Inlines.OpusAssert(false);
            return SilkError.SILK_ENC_INVALID_NUMBER_OF_CHANNELS_ERROR;
        }
        if (this.complexity < 0 || this.complexity > 10) {
            Inlines.OpusAssert(false);
            return SilkError.SILK_ENC_INVALID_COMPLEXITY_SETTING;
        }
        return SilkError.SILK_NO_ERROR;
    }
}
