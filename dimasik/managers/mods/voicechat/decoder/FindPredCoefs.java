package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;
import dimasik.managers.mods.voicechat.decoder.BoxedValueByte;
import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.FindLPC;
import dimasik.managers.mods.voicechat.decoder.FindLTP;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.LTPAnalysisFilter;
import dimasik.managers.mods.voicechat.decoder.LTPScaleControl;
import dimasik.managers.mods.voicechat.decoder.NLSF;
import dimasik.managers.mods.voicechat.decoder.QuantizeLTPGains;
import dimasik.managers.mods.voicechat.decoder.ResidualEnergy;
import dimasik.managers.mods.voicechat.decoder.SilkChannelEncoder;
import dimasik.managers.mods.voicechat.decoder.SilkEncoderControl;

class FindPredCoefs {
    FindPredCoefs() {
    }

    static void silk_find_pred_coefs(SilkChannelEncoder psEnc, SilkEncoderControl psEncCtrl, short[] res_pitch, short[] x, int x_ptr, int condCoding) {
        int minInvGain_Q30;
        int i;
        int[] invGains_Q16 = new int[4];
        int[] local_gains = new int[4];
        int[] Wght_Q15 = new int[4];
        short[] NLSF_Q15 = new short[16];
        int[] LTP_corrs_rshift = new int[4];
        int min_gain_Q16 = 0x1FFFFFF;
        for (i = 0; i < psEnc.nb_subfr; ++i) {
            min_gain_Q16 = Inlines.silk_min(min_gain_Q16, psEncCtrl.Gains_Q16[i]);
        }
        for (i = 0; i < psEnc.nb_subfr; ++i) {
            Inlines.OpusAssert(psEncCtrl.Gains_Q16[i] > 0);
            invGains_Q16[i] = Inlines.silk_DIV32_varQ(min_gain_Q16, psEncCtrl.Gains_Q16[i], 14);
            invGains_Q16[i] = Inlines.silk_max(invGains_Q16[i], 363);
            Inlines.OpusAssert(invGains_Q16[i] == Inlines.silk_SAT16(invGains_Q16[i]));
            int tmp = Inlines.silk_SMULWB(invGains_Q16[i], invGains_Q16[i]);
            Wght_Q15[i] = Inlines.silk_RSHIFT(tmp, 1);
            local_gains[i] = Inlines.silk_DIV32(65536, invGains_Q16[i]);
        }
        short[] LPC_in_pre = new short[psEnc.nb_subfr * psEnc.predictLPCOrder + psEnc.frame_length];
        if (psEnc.indices.signalType == 2) {
            Inlines.OpusAssert(psEnc.ltp_mem_length - psEnc.predictLPCOrder >= psEncCtrl.pitchL[0] + 2);
            int[] WLTP = new int[psEnc.nb_subfr * 5 * 5];
            BoxedValueInt boxed_codgain = new BoxedValueInt(psEncCtrl.LTPredCodGain_Q7);
            FindLTP.silk_find_LTP(psEncCtrl.LTPCoef_Q14, WLTP, boxed_codgain, res_pitch, psEncCtrl.pitchL, Wght_Q15, psEnc.subfr_length, psEnc.nb_subfr, psEnc.ltp_mem_length, LTP_corrs_rshift);
            psEncCtrl.LTPredCodGain_Q7 = boxed_codgain.Val;
            BoxedValueByte boxed_periodicity = new BoxedValueByte(psEnc.indices.PERIndex);
            BoxedValueInt boxed_gain = new BoxedValueInt(psEnc.sum_log_gain_Q7);
            QuantizeLTPGains.silk_quant_LTP_gains(psEncCtrl.LTPCoef_Q14, psEnc.indices.LTPIndex, boxed_periodicity, boxed_gain, WLTP, psEnc.mu_LTP_Q9, psEnc.LTPQuantLowComplexity, psEnc.nb_subfr);
            psEnc.indices.PERIndex = boxed_periodicity.Val;
            psEnc.sum_log_gain_Q7 = boxed_gain.Val;
            LTPScaleControl.silk_LTP_scale_ctrl(psEnc, psEncCtrl, condCoding);
            LTPAnalysisFilter.silk_LTP_analysis_filter(LPC_in_pre, x, x_ptr - psEnc.predictLPCOrder, psEncCtrl.LTPCoef_Q14, psEncCtrl.pitchL, invGains_Q16, psEnc.subfr_length, psEnc.nb_subfr, psEnc.predictLPCOrder);
        } else {
            int x_ptr2 = x_ptr - psEnc.predictLPCOrder;
            int x_pre_ptr = 0;
            for (i = 0; i < psEnc.nb_subfr; ++i) {
                Inlines.silk_scale_copy_vector16(LPC_in_pre, x_pre_ptr, x, x_ptr2, invGains_Q16[i], psEnc.subfr_length + psEnc.predictLPCOrder);
                x_pre_ptr += psEnc.subfr_length + psEnc.predictLPCOrder;
                x_ptr2 += psEnc.subfr_length;
            }
            Arrays.MemSet(psEncCtrl.LTPCoef_Q14, (short)0, psEnc.nb_subfr * 5);
            psEncCtrl.LTPredCodGain_Q7 = 0;
            psEnc.sum_log_gain_Q7 = 0;
        }
        if (psEnc.first_frame_after_reset != 0) {
            minInvGain_Q30 = 10737418;
        } else {
            minInvGain_Q30 = Inlines.silk_log2lin(Inlines.silk_SMLAWB(2048, psEncCtrl.LTPredCodGain_Q7, 21845));
            minInvGain_Q30 = Inlines.silk_DIV32_varQ(minInvGain_Q30, Inlines.silk_SMULWW(10000, Inlines.silk_SMLAWB(65536, 196608, psEncCtrl.coding_quality_Q14)), 14);
        }
        FindLPC.silk_find_LPC(psEnc, NLSF_Q15, LPC_in_pre, minInvGain_Q30);
        NLSF.silk_process_NLSFs(psEnc, psEncCtrl.PredCoef_Q12, NLSF_Q15, psEnc.prev_NLSFq_Q15);
        ResidualEnergy.silk_residual_energy(psEncCtrl.ResNrg, psEncCtrl.ResNrgQ, LPC_in_pre, psEncCtrl.PredCoef_Q12, local_gains, psEnc.subfr_length, psEnc.nb_subfr, psEnc.predictLPCOrder);
        System.arraycopy(NLSF_Q15, 0, psEnc.prev_NLSFq_Q15, 0, 16);
    }
}
