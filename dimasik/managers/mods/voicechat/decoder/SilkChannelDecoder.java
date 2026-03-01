package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;
import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.CNG;
import dimasik.managers.mods.voicechat.decoder.CNGState;
import dimasik.managers.mods.voicechat.decoder.DecodeCore;
import dimasik.managers.mods.voicechat.decoder.DecodeIndices;
import dimasik.managers.mods.voicechat.decoder.DecodeParameters;
import dimasik.managers.mods.voicechat.decoder.DecodePulses;
import dimasik.managers.mods.voicechat.decoder.EntropyCoder;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.NLSFCodebook;
import dimasik.managers.mods.voicechat.decoder.PLC;
import dimasik.managers.mods.voicechat.decoder.PLCStruct;
import dimasik.managers.mods.voicechat.decoder.Resampler;
import dimasik.managers.mods.voicechat.decoder.SideInfoIndices;
import dimasik.managers.mods.voicechat.decoder.SilkDecoderControl;
import dimasik.managers.mods.voicechat.decoder.SilkResamplerState;
import dimasik.managers.mods.voicechat.decoder.SilkTables;

class SilkChannelDecoder {
    int prev_gain_Q16 = 0;
    final int[] exc_Q14 = new int[320];
    final int[] sLPC_Q14_buf = new int[16];
    final short[] outBuf = new short[480];
    int lagPrev = 0;
    byte LastGainIndex = 0;
    int fs_kHz = 0;
    int fs_API_hz = 0;
    int nb_subfr = 0;
    int frame_length = 0;
    int subfr_length = 0;
    int ltp_mem_length = 0;
    int LPC_order = 0;
    final short[] prevNLSF_Q15 = new short[16];
    int first_frame_after_reset = 0;
    short[] pitch_lag_low_bits_iCDF;
    short[] pitch_contour_iCDF;
    int nFramesDecoded = 0;
    int nFramesPerPacket = 0;
    int ec_prevSignalType = 0;
    short ec_prevLagIndex = 0;
    final int[] VAD_flags = new int[3];
    int LBRR_flag = 0;
    final int[] LBRR_flags = new int[3];
    final SilkResamplerState resampler_state = new SilkResamplerState();
    NLSFCodebook psNLSF_CB = null;
    final SideInfoIndices indices = new SideInfoIndices();
    final CNGState sCNG = new CNGState();
    int lossCnt = 0;
    int prevSignalType = 0;
    final PLCStruct sPLC = new PLCStruct();

    SilkChannelDecoder() {
    }

    void Reset() {
        this.prev_gain_Q16 = 0;
        Arrays.MemSet(this.exc_Q14, 0, 320);
        Arrays.MemSet(this.sLPC_Q14_buf, 0, 16);
        Arrays.MemSet(this.outBuf, (short)0, 480);
        this.lagPrev = 0;
        this.LastGainIndex = 0;
        this.fs_kHz = 0;
        this.fs_API_hz = 0;
        this.nb_subfr = 0;
        this.frame_length = 0;
        this.subfr_length = 0;
        this.ltp_mem_length = 0;
        this.LPC_order = 0;
        Arrays.MemSet(this.prevNLSF_Q15, (short)0, 16);
        this.first_frame_after_reset = 0;
        this.pitch_lag_low_bits_iCDF = null;
        this.pitch_contour_iCDF = null;
        this.nFramesDecoded = 0;
        this.nFramesPerPacket = 0;
        this.ec_prevSignalType = 0;
        this.ec_prevLagIndex = 0;
        Arrays.MemSet(this.VAD_flags, 0, 3);
        this.LBRR_flag = 0;
        Arrays.MemSet(this.LBRR_flags, 0, 3);
        this.resampler_state.Reset();
        this.psNLSF_CB = null;
        this.indices.Reset();
        this.sCNG.Reset();
        this.lossCnt = 0;
        this.prevSignalType = 0;
        this.sPLC.Reset();
    }

    int silk_init_decoder() {
        this.Reset();
        this.first_frame_after_reset = 1;
        this.prev_gain_Q16 = 65536;
        this.silk_CNG_Reset();
        this.silk_PLC_Reset();
        return 0;
    }

    private void silk_CNG_Reset() {
        int NLSF_step_Q15 = Inlines.silk_DIV32_16(Short.MAX_VALUE, this.LPC_order + 1);
        int NLSF_acc_Q15 = 0;
        for (int i = 0; i < this.LPC_order; ++i) {
            this.sCNG.CNG_smth_NLSF_Q15[i] = (short)(NLSF_acc_Q15 += NLSF_step_Q15);
        }
        this.sCNG.CNG_smth_Gain_Q16 = 0;
        this.sCNG.rand_seed = 3176576;
    }

    private void silk_PLC_Reset() {
        this.sPLC.pitchL_Q8 = Inlines.silk_LSHIFT(this.frame_length, 7);
        this.sPLC.prevGain_Q16[0] = 65536;
        this.sPLC.prevGain_Q16[1] = 65536;
        this.sPLC.subfr_length = 20;
        this.sPLC.nb_subfr = 2;
    }

    int silk_decoder_set_fs(int fs_kHz, int fs_API_Hz) {
        int ret = 0;
        Inlines.OpusAssert(fs_kHz == 8 || fs_kHz == 12 || fs_kHz == 16);
        Inlines.OpusAssert(this.nb_subfr == 4 || this.nb_subfr == 2);
        this.subfr_length = Inlines.silk_SMULBB(5, fs_kHz);
        int frame_length = Inlines.silk_SMULBB(this.nb_subfr, this.subfr_length);
        if (this.fs_kHz != fs_kHz || this.fs_API_hz != fs_API_Hz) {
            ret += Resampler.silk_resampler_init(this.resampler_state, Inlines.silk_SMULBB(fs_kHz, 1000), fs_API_Hz, 0);
            this.fs_API_hz = fs_API_Hz;
        }
        if (this.fs_kHz != fs_kHz || frame_length != this.frame_length) {
            this.pitch_contour_iCDF = fs_kHz == 8 ? (this.nb_subfr == 4 ? SilkTables.silk_pitch_contour_NB_iCDF : SilkTables.silk_pitch_contour_10_ms_NB_iCDF) : (this.nb_subfr == 4 ? SilkTables.silk_pitch_contour_iCDF : SilkTables.silk_pitch_contour_10_ms_iCDF);
            if (this.fs_kHz != fs_kHz) {
                this.ltp_mem_length = Inlines.silk_SMULBB(20, fs_kHz);
                if (fs_kHz == 8 || fs_kHz == 12) {
                    this.LPC_order = 10;
                    this.psNLSF_CB = SilkTables.silk_NLSF_CB_NB_MB;
                } else {
                    this.LPC_order = 16;
                    this.psNLSF_CB = SilkTables.silk_NLSF_CB_WB;
                }
                if (fs_kHz == 16) {
                    this.pitch_lag_low_bits_iCDF = SilkTables.silk_uniform8_iCDF;
                } else if (fs_kHz == 12) {
                    this.pitch_lag_low_bits_iCDF = SilkTables.silk_uniform6_iCDF;
                } else if (fs_kHz == 8) {
                    this.pitch_lag_low_bits_iCDF = SilkTables.silk_uniform4_iCDF;
                } else {
                    Inlines.OpusAssert(false);
                }
                this.first_frame_after_reset = 1;
                this.lagPrev = 100;
                this.LastGainIndex = (byte)10;
                this.prevSignalType = 0;
                Arrays.MemSet(this.outBuf, (short)0, 480);
                Arrays.MemSet(this.sLPC_Q14_buf, 0, 16);
            }
            this.fs_kHz = fs_kHz;
            this.frame_length = frame_length;
        }
        Inlines.OpusAssert(this.frame_length > 0 && this.frame_length <= 320);
        return ret;
    }

    int silk_decode_frame(EntropyCoder psRangeDec, short[] pOut, int pOut_ptr, BoxedValueInt pN, int lostFlag, int condCoding) {
        SilkDecoderControl thisCtrl = new SilkDecoderControl();
        int ret = 0;
        int L = this.frame_length;
        thisCtrl.LTP_scale_Q14 = 0;
        Inlines.OpusAssert(L > 0 && L <= 320);
        if (lostFlag == 0 || lostFlag == 2 && this.LBRR_flags[this.nFramesDecoded] == 1) {
            short[] pulses = new short[L + 16 - 1 & 0xFFFFFFF0];
            DecodeIndices.silk_decode_indices(this, psRangeDec, this.nFramesDecoded, lostFlag, condCoding);
            DecodePulses.silk_decode_pulses(psRangeDec, pulses, this.indices.signalType, this.indices.quantOffsetType, this.frame_length);
            DecodeParameters.silk_decode_parameters(this, thisCtrl, condCoding);
            DecodeCore.silk_decode_core(this, thisCtrl, pOut, pOut_ptr, pulses);
            PLC.silk_PLC(this, thisCtrl, pOut, pOut_ptr, 0);
            this.lossCnt = 0;
            this.prevSignalType = this.indices.signalType;
            Inlines.OpusAssert(this.prevSignalType >= 0 && this.prevSignalType <= 2);
            this.first_frame_after_reset = 0;
        } else {
            PLC.silk_PLC(this, thisCtrl, pOut, pOut_ptr, 1);
        }
        Inlines.OpusAssert(this.ltp_mem_length >= this.frame_length);
        int mv_len = this.ltp_mem_length - this.frame_length;
        Arrays.MemMove(this.outBuf, this.frame_length, 0, mv_len);
        System.arraycopy(pOut, pOut_ptr, this.outBuf, mv_len, this.frame_length);
        CNG.silk_CNG(this, thisCtrl, pOut, pOut_ptr, L);
        PLC.silk_PLC_glue_frames(this, pOut, pOut_ptr, L);
        this.lagPrev = thisCtrl.pitchL[this.nb_subfr - 1];
        pN.Val = L;
        return ret;
    }
}
