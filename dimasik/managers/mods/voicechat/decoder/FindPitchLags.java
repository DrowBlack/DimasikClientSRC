package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.ApplySineWindow;
import dimasik.managers.mods.voicechat.decoder.Arrays;
import dimasik.managers.mods.voicechat.decoder.Autocorrelation;
import dimasik.managers.mods.voicechat.decoder.BWExpander;
import dimasik.managers.mods.voicechat.decoder.BoxedValueByte;
import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.BoxedValueShort;
import dimasik.managers.mods.voicechat.decoder.Filters;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.K2A;
import dimasik.managers.mods.voicechat.decoder.PitchAnalysisCore;
import dimasik.managers.mods.voicechat.decoder.Schur;
import dimasik.managers.mods.voicechat.decoder.SilkChannelEncoder;
import dimasik.managers.mods.voicechat.decoder.SilkEncoderControl;

class FindPitchLags {
    FindPitchLags() {
    }

    static void silk_find_pitch_lags(SilkChannelEncoder psEnc, SilkEncoderControl psEncCtrl, short[] res, short[] x, int x_ptr) {
        int[] auto_corr = new int[17];
        short[] rc_Q15 = new short[16];
        int[] A_Q24 = new int[16];
        short[] A_Q12 = new short[16];
        int buf_len = psEnc.la_pitch + psEnc.frame_length + psEnc.ltp_mem_length;
        Inlines.OpusAssert(buf_len >= psEnc.pitch_LPC_win_length);
        int x_buf = x_ptr - psEnc.ltp_mem_length;
        short[] Wsig = new short[psEnc.pitch_LPC_win_length];
        int x_buf_ptr = x_buf + buf_len - psEnc.pitch_LPC_win_length;
        int Wsig_ptr = 0;
        ApplySineWindow.silk_apply_sine_window(Wsig, Wsig_ptr, x, x_buf_ptr, 1, psEnc.la_pitch);
        System.arraycopy(x, x_buf_ptr += psEnc.la_pitch, Wsig, Wsig_ptr += psEnc.la_pitch, psEnc.pitch_LPC_win_length - Inlines.silk_LSHIFT(psEnc.la_pitch, 1));
        ApplySineWindow.silk_apply_sine_window(Wsig, Wsig_ptr += psEnc.pitch_LPC_win_length - Inlines.silk_LSHIFT(psEnc.la_pitch, 1), x, x_buf_ptr += psEnc.pitch_LPC_win_length - Inlines.silk_LSHIFT(psEnc.la_pitch, 1), 2, psEnc.la_pitch);
        BoxedValueInt boxed_scale = new BoxedValueInt(0);
        Autocorrelation.silk_autocorr(auto_corr, boxed_scale, Wsig, psEnc.pitch_LPC_win_length, psEnc.pitchEstimationLPCOrder + 1);
        int scale = boxed_scale.Val;
        auto_corr[0] = Inlines.silk_SMLAWB(auto_corr[0], auto_corr[0], 66) + 1;
        int res_nrg = Schur.silk_schur(rc_Q15, auto_corr, psEnc.pitchEstimationLPCOrder);
        psEncCtrl.predGain_Q16 = Inlines.silk_DIV32_varQ(auto_corr[0], Inlines.silk_max_int(res_nrg, 1), 16);
        K2A.silk_k2a(A_Q24, rc_Q15, psEnc.pitchEstimationLPCOrder);
        for (int i = 0; i < psEnc.pitchEstimationLPCOrder; ++i) {
            A_Q12[i] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT(A_Q24[i], 12));
        }
        BWExpander.silk_bwexpander(A_Q12, psEnc.pitchEstimationLPCOrder, 64881);
        Filters.silk_LPC_analysis_filter(res, 0, x, x_buf, A_Q12, 0, buf_len, psEnc.pitchEstimationLPCOrder);
        if (psEnc.indices.signalType != 0 && psEnc.first_frame_after_reset == 0) {
            int thrhld_Q13 = 4915;
            thrhld_Q13 = Inlines.silk_SMLABB(thrhld_Q13, -32, psEnc.pitchEstimationLPCOrder);
            thrhld_Q13 = Inlines.silk_SMLAWB(thrhld_Q13, -209714, psEnc.speech_activity_Q8);
            thrhld_Q13 = Inlines.silk_SMLABB(thrhld_Q13, -1228, Inlines.silk_RSHIFT(psEnc.prevSignalType, 1));
            thrhld_Q13 = Inlines.silk_SMLAWB(thrhld_Q13, -1637, psEnc.input_tilt_Q15);
            BoxedValueShort boxed_lagIndex = new BoxedValueShort(psEnc.indices.lagIndex);
            BoxedValueByte boxed_contourIndex = new BoxedValueByte(psEnc.indices.contourIndex);
            BoxedValueInt boxed_LTPcorr = new BoxedValueInt(psEnc.LTPCorr_Q15);
            psEnc.indices.signalType = PitchAnalysisCore.silk_pitch_analysis_core(res, psEncCtrl.pitchL, boxed_lagIndex, boxed_contourIndex, boxed_LTPcorr, psEnc.prevLag, psEnc.pitchEstimationThreshold_Q16, thrhld_Q13 = Inlines.silk_SAT16(thrhld_Q13), psEnc.fs_kHz, psEnc.pitchEstimationComplexity, psEnc.nb_subfr) == 0 ? (byte)2 : (byte)1;
            psEnc.indices.lagIndex = boxed_lagIndex.Val;
            psEnc.indices.contourIndex = boxed_contourIndex.Val;
            psEnc.LTPCorr_Q15 = boxed_LTPcorr.Val;
        } else {
            Arrays.MemSet(psEncCtrl.pitchL, 0, 4);
            psEnc.indices.lagIndex = 0;
            psEnc.indices.contourIndex = 0;
            psEnc.LTPCorr_Q15 = 0;
        }
    }
}
