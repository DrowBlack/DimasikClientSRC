package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.ApplySineWindow;
import dimasik.managers.mods.voicechat.decoder.Autocorrelation;
import dimasik.managers.mods.voicechat.decoder.BWExpander;
import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.K2A;
import dimasik.managers.mods.voicechat.decoder.LPCInversePredGain;
import dimasik.managers.mods.voicechat.decoder.Schur;
import dimasik.managers.mods.voicechat.decoder.Sigmoid;
import dimasik.managers.mods.voicechat.decoder.SilkChannelEncoder;
import dimasik.managers.mods.voicechat.decoder.SilkEncoderControl;
import dimasik.managers.mods.voicechat.decoder.SilkShapeState;
import dimasik.managers.mods.voicechat.decoder.SumSqrShift;

class NoiseShapeAnalysis {
    NoiseShapeAnalysis() {
    }

    static int warped_gain(int[] coefs_Q24, int lambda_Q16, int order) {
        lambda_Q16 = -lambda_Q16;
        int gain_Q24 = coefs_Q24[order - 1];
        for (int i = order - 2; i >= 0; --i) {
            gain_Q24 = Inlines.silk_SMLAWB(coefs_Q24[i], gain_Q24, lambda_Q16);
        }
        gain_Q24 = Inlines.silk_SMLAWB(0x1000000, gain_Q24, -lambda_Q16);
        return Inlines.silk_INVERSE32_varQ(gain_Q24, 40);
    }

    static void limit_warped_coefs(int[] coefs_syn_Q24, int[] coefs_ana_Q24, int lambda_Q16, int limit_Q24, int order) {
        int i;
        int ind = 0;
        lambda_Q16 = -lambda_Q16;
        for (i = order - 1; i > 0; --i) {
            coefs_syn_Q24[i - 1] = Inlines.silk_SMLAWB(coefs_syn_Q24[i - 1], coefs_syn_Q24[i], lambda_Q16);
            coefs_ana_Q24[i - 1] = Inlines.silk_SMLAWB(coefs_ana_Q24[i - 1], coefs_ana_Q24[i], lambda_Q16);
        }
        lambda_Q16 = -lambda_Q16;
        int nom_Q16 = Inlines.silk_SMLAWB(65536, -lambda_Q16, lambda_Q16);
        int den_Q24 = Inlines.silk_SMLAWB(0x1000000, coefs_syn_Q24[0], lambda_Q16);
        int gain_syn_Q16 = Inlines.silk_DIV32_varQ(nom_Q16, den_Q24, 24);
        den_Q24 = Inlines.silk_SMLAWB(0x1000000, coefs_ana_Q24[0], lambda_Q16);
        int gain_ana_Q16 = Inlines.silk_DIV32_varQ(nom_Q16, den_Q24, 24);
        for (i = 0; i < order; ++i) {
            coefs_syn_Q24[i] = Inlines.silk_SMULWW(gain_syn_Q16, coefs_syn_Q24[i]);
            coefs_ana_Q24[i] = Inlines.silk_SMULWW(gain_ana_Q16, coefs_ana_Q24[i]);
        }
        for (int iter = 0; iter < 10; ++iter) {
            int maxabs_Q24 = -1;
            for (i = 0; i < order; ++i) {
                int tmp = Inlines.silk_max(Inlines.silk_abs_int32(coefs_syn_Q24[i]), Inlines.silk_abs_int32(coefs_ana_Q24[i]));
                if (tmp <= maxabs_Q24) continue;
                maxabs_Q24 = tmp;
                ind = i;
            }
            if (maxabs_Q24 <= limit_Q24) {
                return;
            }
            for (i = 1; i < order; ++i) {
                coefs_syn_Q24[i - 1] = Inlines.silk_SMLAWB(coefs_syn_Q24[i - 1], coefs_syn_Q24[i], lambda_Q16);
                coefs_ana_Q24[i - 1] = Inlines.silk_SMLAWB(coefs_ana_Q24[i - 1], coefs_ana_Q24[i], lambda_Q16);
            }
            gain_syn_Q16 = Inlines.silk_INVERSE32_varQ(gain_syn_Q16, 32);
            gain_ana_Q16 = Inlines.silk_INVERSE32_varQ(gain_ana_Q16, 32);
            for (i = 0; i < order; ++i) {
                coefs_syn_Q24[i] = Inlines.silk_SMULWW(gain_syn_Q16, coefs_syn_Q24[i]);
                coefs_ana_Q24[i] = Inlines.silk_SMULWW(gain_ana_Q16, coefs_ana_Q24[i]);
            }
            int chirp_Q16 = 64881 - Inlines.silk_DIV32_varQ(Inlines.silk_SMULWB(maxabs_Q24 - limit_Q24, Inlines.silk_SMLABB(819, 102, iter)), Inlines.silk_MUL(maxabs_Q24, ind + 1), 22);
            BWExpander.silk_bwexpander_32(coefs_syn_Q24, order, chirp_Q16);
            BWExpander.silk_bwexpander_32(coefs_ana_Q24, order, chirp_Q16);
            lambda_Q16 = -lambda_Q16;
            for (i = order - 1; i > 0; --i) {
                coefs_syn_Q24[i - 1] = Inlines.silk_SMLAWB(coefs_syn_Q24[i - 1], coefs_syn_Q24[i], lambda_Q16);
                coefs_ana_Q24[i - 1] = Inlines.silk_SMLAWB(coefs_ana_Q24[i - 1], coefs_ana_Q24[i], lambda_Q16);
            }
            lambda_Q16 = -lambda_Q16;
            nom_Q16 = Inlines.silk_SMLAWB(65536, -lambda_Q16, lambda_Q16);
            den_Q24 = Inlines.silk_SMLAWB(0x1000000, coefs_syn_Q24[0], lambda_Q16);
            gain_syn_Q16 = Inlines.silk_DIV32_varQ(nom_Q16, den_Q24, 24);
            den_Q24 = Inlines.silk_SMLAWB(0x1000000, coefs_ana_Q24[0], lambda_Q16);
            gain_ana_Q16 = Inlines.silk_DIV32_varQ(nom_Q16, den_Q24, 24);
            for (i = 0; i < order; ++i) {
                coefs_syn_Q24[i] = Inlines.silk_SMULWW(gain_syn_Q16, coefs_syn_Q24[i]);
                coefs_ana_Q24[i] = Inlines.silk_SMULWW(gain_ana_Q16, coefs_ana_Q24[i]);
            }
        }
        Inlines.OpusAssert(false);
    }

    static void silk_noise_shape_analysis(SilkChannelEncoder psEnc, SilkEncoderControl psEncCtrl, short[] pitch_res, int pitch_res_ptr, short[] x, int x_ptr) {
        int HarmShapeGain_Q16;
        int Tilt_Q16;
        int gain_mult_Q16;
        int BWExp2_Q16;
        int nrg;
        int k;
        SilkShapeState psShapeSt = psEnc.sShape;
        int scale = 0;
        int[] auto_corr = new int[17];
        int[] refl_coef_Q16 = new int[16];
        int[] AR1_Q24 = new int[16];
        int[] AR2_Q24 = new int[16];
        int x_ptr2 = x_ptr - psEnc.la_shape;
        int SNR_adj_dB_Q7 = psEnc.SNR_dB_Q7;
        psEncCtrl.input_quality_Q14 = Inlines.silk_RSHIFT(psEnc.input_quality_bands_Q15[0] + psEnc.input_quality_bands_Q15[1], 2);
        psEncCtrl.coding_quality_Q14 = Inlines.silk_RSHIFT(Sigmoid.silk_sigm_Q15(Inlines.silk_RSHIFT_ROUND(SNR_adj_dB_Q7 - 2560, 4)), 1);
        if (psEnc.useCBR == 0) {
            int b_Q8 = 256 - psEnc.speech_activity_Q8;
            b_Q8 = Inlines.silk_SMULWB(Inlines.silk_LSHIFT(b_Q8, 8), b_Q8);
            SNR_adj_dB_Q7 = Inlines.silk_SMLAWB(SNR_adj_dB_Q7, Inlines.silk_SMULBB(-8, b_Q8), Inlines.silk_SMULWB(16384 + psEncCtrl.input_quality_Q14, psEncCtrl.coding_quality_Q14));
        }
        SNR_adj_dB_Q7 = psEnc.indices.signalType == 2 ? Inlines.silk_SMLAWB(SNR_adj_dB_Q7, 512, psEnc.LTPCorr_Q15) : Inlines.silk_SMLAWB(SNR_adj_dB_Q7, Inlines.silk_SMLAWB(3072, -104858, psEnc.SNR_dB_Q7), 16384 - psEncCtrl.input_quality_Q14);
        if (psEnc.indices.signalType == 2) {
            psEnc.indices.quantOffsetType = 0;
            psEncCtrl.sparseness_Q8 = 0;
        } else {
            int nSamples = Inlines.silk_LSHIFT(psEnc.fs_kHz, 1);
            int energy_variation_Q7 = 0;
            int log_energy_prev_Q7 = 0;
            int pitch_res_ptr2 = pitch_res_ptr;
            BoxedValueInt boxed_nrg = new BoxedValueInt(0);
            BoxedValueInt boxed_scale = new BoxedValueInt(0);
            for (k = 0; k < Inlines.silk_SMULBB(5, psEnc.nb_subfr) / 2; ++k) {
                SumSqrShift.silk_sum_sqr_shift(boxed_nrg, boxed_scale, pitch_res, pitch_res_ptr2, nSamples);
                nrg = boxed_nrg.Val;
                scale = boxed_scale.Val;
                int log_energy_Q7 = Inlines.silk_lin2log(nrg += Inlines.silk_RSHIFT(nSamples, scale));
                if (k > 0) {
                    energy_variation_Q7 += Inlines.silk_abs(log_energy_Q7 - log_energy_prev_Q7);
                }
                log_energy_prev_Q7 = log_energy_Q7;
                pitch_res_ptr2 += nSamples;
            }
            psEncCtrl.sparseness_Q8 = Inlines.silk_RSHIFT(Sigmoid.silk_sigm_Q15(Inlines.silk_SMULWB(energy_variation_Q7 - 640, 6554)), 7);
            psEnc.indices.quantOffsetType = psEncCtrl.sparseness_Q8 > 192 ? (byte)0 : 1;
            SNR_adj_dB_Q7 = Inlines.silk_SMLAWB(SNR_adj_dB_Q7, 65536, psEncCtrl.sparseness_Q8 - 128);
        }
        int strength_Q16 = Inlines.silk_SMULWB(psEncCtrl.predGain_Q16, 66);
        int BWExp1_Q16 = BWExp2_Q16 = Inlines.silk_DIV32_varQ(62259, Inlines.silk_SMLAWW(65536, strength_Q16, strength_Q16), 16);
        int delta_Q16 = Inlines.silk_SMULWB(65536 - Inlines.silk_SMULBB(3, psEncCtrl.coding_quality_Q14), 655);
        BWExp1_Q16 = Inlines.silk_SUB32(BWExp1_Q16, delta_Q16);
        BWExp2_Q16 = Inlines.silk_ADD32(BWExp2_Q16, delta_Q16);
        BWExp1_Q16 = Inlines.silk_DIV32_16(Inlines.silk_LSHIFT(BWExp1_Q16, 14), Inlines.silk_RSHIFT(BWExp2_Q16, 2));
        int warping_Q16 = psEnc.warping_Q16 > 0 ? Inlines.silk_SMLAWB(psEnc.warping_Q16, psEncCtrl.coding_quality_Q14, 2621) : 0;
        short[] x_windowed = new short[psEnc.shapeWinLength];
        for (k = 0; k < psEnc.nb_subfr; ++k) {
            int flat_part = psEnc.fs_kHz * 3;
            int slope_part = Inlines.silk_RSHIFT(psEnc.shapeWinLength - flat_part, 1);
            ApplySineWindow.silk_apply_sine_window(x_windowed, 0, x, x_ptr2, 1, slope_part);
            int shift = slope_part;
            System.arraycopy(x, x_ptr2 + shift, x_windowed, shift, flat_part);
            ApplySineWindow.silk_apply_sine_window(x_windowed, shift += flat_part, x, x_ptr2 + shift, 2, slope_part);
            x_ptr2 += psEnc.subfr_length;
            BoxedValueInt scale_boxed = new BoxedValueInt(scale);
            if (psEnc.warping_Q16 > 0) {
                Autocorrelation.silk_warped_autocorrelation(auto_corr, scale_boxed, x_windowed, warping_Q16, psEnc.shapeWinLength, psEnc.shapingLPCOrder);
            } else {
                Autocorrelation.silk_autocorr(auto_corr, scale_boxed, x_windowed, psEnc.shapeWinLength, psEnc.shapingLPCOrder + 1);
            }
            scale = scale_boxed.Val;
            auto_corr[0] = Inlines.silk_ADD32(auto_corr[0], Inlines.silk_max_32(Inlines.silk_SMULWB(Inlines.silk_RSHIFT(auto_corr[0], 4), 52), 1));
            nrg = Schur.silk_schur64(refl_coef_Q16, auto_corr, psEnc.shapingLPCOrder);
            Inlines.OpusAssert(nrg >= 0);
            K2A.silk_k2a_Q16(AR2_Q24, refl_coef_Q16, psEnc.shapingLPCOrder);
            int Qnrg = -scale;
            Inlines.OpusAssert(Qnrg >= -12);
            Inlines.OpusAssert(Qnrg <= 30);
            if ((Qnrg & 1) != 0) {
                --Qnrg;
                nrg >>= 1;
            }
            int tmp32 = Inlines.silk_SQRT_APPROX(nrg);
            psEncCtrl.Gains_Q16[k] = Inlines.silk_LSHIFT_SAT32(tmp32, 16 - (Qnrg >>= 1));
            if (psEnc.warping_Q16 > 0) {
                gain_mult_Q16 = NoiseShapeAnalysis.warped_gain(AR2_Q24, warping_Q16, psEnc.shapingLPCOrder);
                Inlines.OpusAssert(psEncCtrl.Gains_Q16[k] >= 0);
                psEncCtrl.Gains_Q16[k] = Inlines.silk_SMULWW(Inlines.silk_RSHIFT_ROUND(psEncCtrl.Gains_Q16[k], 1), gain_mult_Q16) >= 0x3FFFFFFF ? Integer.MAX_VALUE : Inlines.silk_SMULWW(psEncCtrl.Gains_Q16[k], gain_mult_Q16);
            }
            BWExpander.silk_bwexpander_32(AR2_Q24, psEnc.shapingLPCOrder, BWExp2_Q16);
            System.arraycopy(AR2_Q24, 0, AR1_Q24, 0, psEnc.shapingLPCOrder);
            Inlines.OpusAssert(BWExp1_Q16 <= 65536);
            BWExpander.silk_bwexpander_32(AR1_Q24, psEnc.shapingLPCOrder, BWExp1_Q16);
            int pre_nrg_Q30 = LPCInversePredGain.silk_LPC_inverse_pred_gain_Q24(AR2_Q24, psEnc.shapingLPCOrder);
            nrg = LPCInversePredGain.silk_LPC_inverse_pred_gain_Q24(AR1_Q24, psEnc.shapingLPCOrder);
            pre_nrg_Q30 = Inlines.silk_LSHIFT32(Inlines.silk_SMULWB(pre_nrg_Q30, 22938), 1);
            psEncCtrl.GainsPre_Q14[k] = 4915 + Inlines.silk_DIV32_varQ(pre_nrg_Q30, nrg, 14);
            NoiseShapeAnalysis.limit_warped_coefs(AR2_Q24, AR1_Q24, warping_Q16, 67092088, psEnc.shapingLPCOrder);
            for (int i = 0; i < psEnc.shapingLPCOrder; ++i) {
                psEncCtrl.AR1_Q13[k * 16 + i] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(AR1_Q24[i], 11));
                psEncCtrl.AR2_Q13[k * 16 + i] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(AR2_Q24[i], 11));
            }
        }
        gain_mult_Q16 = Inlines.silk_log2lin(-Inlines.silk_SMLAWB(-2048, SNR_adj_dB_Q7, 10486));
        int gain_add_Q16 = Inlines.silk_log2lin(Inlines.silk_SMLAWB(2048, 256, 10486));
        Inlines.OpusAssert(gain_mult_Q16 > 0);
        for (k = 0; k < psEnc.nb_subfr; ++k) {
            psEncCtrl.Gains_Q16[k] = Inlines.silk_SMULWW(psEncCtrl.Gains_Q16[k], gain_mult_Q16);
            Inlines.OpusAssert(psEncCtrl.Gains_Q16[k] >= 0);
            psEncCtrl.Gains_Q16[k] = Inlines.silk_ADD_POS_SAT32(psEncCtrl.Gains_Q16[k], gain_add_Q16);
        }
        gain_mult_Q16 = 65536 + Inlines.silk_RSHIFT_ROUND(Inlines.silk_MLA(0x333333, psEncCtrl.coding_quality_Q14, 410), 10);
        for (k = 0; k < psEnc.nb_subfr; ++k) {
            psEncCtrl.GainsPre_Q14[k] = Inlines.silk_SMULWB(gain_mult_Q16, psEncCtrl.GainsPre_Q14[k]);
        }
        strength_Q16 = Inlines.silk_MUL(64, Inlines.silk_SMLAWB(4096, 4096, psEnc.input_quality_bands_Q15[0] - 32768));
        strength_Q16 = Inlines.silk_RSHIFT(Inlines.silk_MUL(strength_Q16, psEnc.speech_activity_Q8), 8);
        if (psEnc.indices.signalType == 2) {
            int fs_kHz_inv = Inlines.silk_DIV32_16(3277, psEnc.fs_kHz);
            k = 0;
            while (k < psEnc.nb_subfr) {
                b_Q14 = fs_kHz_inv + Inlines.silk_DIV32_16(49152, psEncCtrl.pitchL[k]);
                psEncCtrl.LF_shp_Q14[k] = Inlines.silk_LSHIFT(16384 - b_Q14 - Inlines.silk_SMULWB(strength_Q16, b_Q14), 16);
                int n = k++;
                psEncCtrl.LF_shp_Q14[n] = psEncCtrl.LF_shp_Q14[n] | b_Q14 - 16384 & 0xFFFF;
            }
            Inlines.OpusAssert(true);
            Tilt_Q16 = -16384 - Inlines.silk_SMULWB(49152, Inlines.silk_SMULWB(0x59999A, psEnc.speech_activity_Q8));
        } else {
            b_Q14 = Inlines.silk_DIV32_16(21299, psEnc.fs_kHz);
            psEncCtrl.LF_shp_Q14[0] = Inlines.silk_LSHIFT(16384 - b_Q14 - Inlines.silk_SMULWB(strength_Q16, Inlines.silk_SMULWB(39322, b_Q14)), 16);
            psEncCtrl.LF_shp_Q14[0] = psEncCtrl.LF_shp_Q14[0] | b_Q14 - 16384 & 0xFFFF;
            for (k = 1; k < psEnc.nb_subfr; ++k) {
                psEncCtrl.LF_shp_Q14[k] = psEncCtrl.LF_shp_Q14[0];
            }
            Tilt_Q16 = -16384;
        }
        int HarmBoost_Q16 = Inlines.silk_SMULWB(Inlines.silk_SMULWB(131072 - Inlines.silk_LSHIFT(psEncCtrl.coding_quality_Q14, 3), psEnc.LTPCorr_Q15), 6554);
        HarmBoost_Q16 = Inlines.silk_SMLAWB(HarmBoost_Q16, 65536 - Inlines.silk_LSHIFT(psEncCtrl.input_quality_Q14, 2), 6554);
        if (psEnc.indices.signalType == 2) {
            HarmShapeGain_Q16 = Inlines.silk_SMLAWB(19661, 65536 - Inlines.silk_SMULWB(262144 - Inlines.silk_LSHIFT(psEncCtrl.coding_quality_Q14, 4), psEncCtrl.input_quality_Q14), 13107);
            HarmShapeGain_Q16 = Inlines.silk_SMULWB(Inlines.silk_LSHIFT(HarmShapeGain_Q16, 1), Inlines.silk_SQRT_APPROX(Inlines.silk_LSHIFT(psEnc.LTPCorr_Q15, 15)));
        } else {
            HarmShapeGain_Q16 = 0;
        }
        for (k = 0; k < 4; ++k) {
            psShapeSt.HarmBoost_smth_Q16 = Inlines.silk_SMLAWB(psShapeSt.HarmBoost_smth_Q16, HarmBoost_Q16 - psShapeSt.HarmBoost_smth_Q16, 26214);
            psShapeSt.HarmShapeGain_smth_Q16 = Inlines.silk_SMLAWB(psShapeSt.HarmShapeGain_smth_Q16, HarmShapeGain_Q16 - psShapeSt.HarmShapeGain_smth_Q16, 26214);
            psShapeSt.Tilt_smth_Q16 = Inlines.silk_SMLAWB(psShapeSt.Tilt_smth_Q16, Tilt_Q16 - psShapeSt.Tilt_smth_Q16, 26214);
            psEncCtrl.HarmBoost_Q14[k] = Inlines.silk_RSHIFT_ROUND(psShapeSt.HarmBoost_smth_Q16, 2);
            psEncCtrl.HarmShapeGain_Q14[k] = Inlines.silk_RSHIFT_ROUND(psShapeSt.HarmShapeGain_smth_Q16, 2);
            psEncCtrl.Tilt_Q14[k] = Inlines.silk_RSHIFT_ROUND(psShapeSt.Tilt_smth_Q16, 2);
        }
    }
}
