package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Filters;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.Sigmoid;
import dimasik.managers.mods.voicechat.decoder.SilkChannelEncoder;
import dimasik.managers.mods.voicechat.decoder.SilkVADState;

class VoiceActivityDetection {
    private static final int[] tiltWeights = new int[]{30000, 6000, -12000, -12000};

    VoiceActivityDetection() {
    }

    static int silk_VAD_Init(SilkVADState psSilk_VAD) {
        int b;
        int ret = 0;
        psSilk_VAD.Reset();
        for (b = 0; b < 4; ++b) {
            psSilk_VAD.NoiseLevelBias[b] = Inlines.silk_max_32(Inlines.silk_DIV32_16(50, (short)(b + 1)), 1);
        }
        for (b = 0; b < 4; ++b) {
            psSilk_VAD.NL[b] = Inlines.silk_MUL(100, psSilk_VAD.NoiseLevelBias[b]);
            psSilk_VAD.inv_NL[b] = Inlines.silk_DIV32(Integer.MAX_VALUE, psSilk_VAD.NL[b]);
        }
        psSilk_VAD.counter = 15;
        for (b = 0; b < 4; ++b) {
            psSilk_VAD.NrgRatioSmth_Q8[b] = 25600;
        }
        return ret;
    }

    static int silk_VAD_GetSA_Q8(SilkChannelEncoder psEncC, short[] pIn, int pIn_ptr) {
        int SNR_Q7;
        int speech_nrg;
        int b;
        int i;
        int sumSquared = 0;
        int[] Xnrg = new int[4];
        int[] NrgToNoiseRatio_Q8 = new int[4];
        int[] X_offset = new int[4];
        int ret = 0;
        SilkVADState psSilk_VAD = psEncC.sVAD;
        Inlines.OpusAssert(true);
        Inlines.OpusAssert(320 >= psEncC.frame_length);
        Inlines.OpusAssert(psEncC.frame_length <= 512);
        Inlines.OpusAssert(psEncC.frame_length == 8 * Inlines.silk_RSHIFT(psEncC.frame_length, 3));
        int decimated_framelength1 = Inlines.silk_RSHIFT(psEncC.frame_length, 1);
        int decimated_framelength2 = Inlines.silk_RSHIFT(psEncC.frame_length, 2);
        int decimated_framelength = Inlines.silk_RSHIFT(psEncC.frame_length, 3);
        X_offset[0] = 0;
        X_offset[1] = decimated_framelength + decimated_framelength2;
        X_offset[2] = X_offset[1] + decimated_framelength;
        X_offset[3] = X_offset[2] + decimated_framelength2;
        short[] X = new short[X_offset[3] + decimated_framelength1];
        Filters.silk_ana_filt_bank_1(pIn, pIn_ptr, psSilk_VAD.AnaState, X, X, X_offset[3], psEncC.frame_length);
        Filters.silk_ana_filt_bank_1(X, 0, psSilk_VAD.AnaState1, X, X, X_offset[2], decimated_framelength1);
        Filters.silk_ana_filt_bank_1(X, 0, psSilk_VAD.AnaState2, X, X, X_offset[1], decimated_framelength2);
        X[decimated_framelength - 1] = (short)Inlines.silk_RSHIFT(X[decimated_framelength - 1], 1);
        short HPstateTmp = X[decimated_framelength - 1];
        for (i = decimated_framelength - 1; i > 0; --i) {
            X[i - 1] = (short)Inlines.silk_RSHIFT(X[i - 1], 1);
            int n = i;
            X[n] = (short)(X[n] - X[i - 1]);
        }
        X[0] = (short)(X[0] - psSilk_VAD.HPstate);
        psSilk_VAD.HPstate = HPstateTmp;
        for (b = 0; b < 4; ++b) {
            decimated_framelength = Inlines.silk_RSHIFT(psEncC.frame_length, Inlines.silk_min_int(4 - b, 3));
            int dec_subframe_length = Inlines.silk_RSHIFT(decimated_framelength, 2);
            int dec_subframe_offset = 0;
            Xnrg[b] = psSilk_VAD.XnrgSubfr[b];
            for (int s = 0; s < 4; ++s) {
                sumSquared = 0;
                for (i = 0; i < dec_subframe_length; ++i) {
                    int x_tmp = Inlines.silk_RSHIFT(X[X_offset[b] + i + dec_subframe_offset], 3);
                    Inlines.OpusAssert((sumSquared = Inlines.silk_SMLABB(sumSquared, x_tmp, x_tmp)) >= 0);
                }
                Xnrg[b] = s < 3 ? Inlines.silk_ADD_POS_SAT32(Xnrg[b], sumSquared) : Inlines.silk_ADD_POS_SAT32(Xnrg[b], Inlines.silk_RSHIFT(sumSquared, 1));
                dec_subframe_offset += dec_subframe_length;
            }
            psSilk_VAD.XnrgSubfr[b] = sumSquared;
        }
        VoiceActivityDetection.silk_VAD_GetNoiseLevels(Xnrg, psSilk_VAD);
        sumSquared = 0;
        int input_tilt = 0;
        for (b = 0; b < 4; ++b) {
            speech_nrg = Xnrg[b] - psSilk_VAD.NL[b];
            if (speech_nrg > 0) {
                NrgToNoiseRatio_Q8[b] = (Xnrg[b] & 0xFF800000) == 0 ? Inlines.silk_DIV32(Inlines.silk_LSHIFT(Xnrg[b], 8), psSilk_VAD.NL[b] + 1) : Inlines.silk_DIV32(Xnrg[b], Inlines.silk_RSHIFT(psSilk_VAD.NL[b], 8) + 1);
                SNR_Q7 = Inlines.silk_lin2log(NrgToNoiseRatio_Q8[b]) - 1024;
                sumSquared = Inlines.silk_SMLABB(sumSquared, SNR_Q7, SNR_Q7);
                if (speech_nrg < 0x100000) {
                    SNR_Q7 = Inlines.silk_SMULWB(Inlines.silk_LSHIFT(Inlines.silk_SQRT_APPROX(speech_nrg), 6), SNR_Q7);
                }
                input_tilt = Inlines.silk_SMLAWB(input_tilt, tiltWeights[b], SNR_Q7);
                continue;
            }
            NrgToNoiseRatio_Q8[b] = 256;
        }
        sumSquared = Inlines.silk_DIV32_16(sumSquared, 4);
        short pSNR_dB_Q7 = (short)(3 * Inlines.silk_SQRT_APPROX(sumSquared));
        int SA_Q15 = Sigmoid.silk_sigm_Q15(Inlines.silk_SMULWB(45000, pSNR_dB_Q7) - 128);
        psEncC.input_tilt_Q15 = Inlines.silk_LSHIFT(Sigmoid.silk_sigm_Q15(input_tilt) - 16384, 1);
        speech_nrg = 0;
        for (b = 0; b < 4; ++b) {
            speech_nrg += (b + 1) * Inlines.silk_RSHIFT(Xnrg[b] - psSilk_VAD.NL[b], 4);
        }
        if (speech_nrg <= 0) {
            SA_Q15 = Inlines.silk_RSHIFT(SA_Q15, 1);
        } else if (speech_nrg < 32768) {
            speech_nrg = psEncC.frame_length == 10 * psEncC.fs_kHz ? Inlines.silk_LSHIFT_SAT32(speech_nrg, 16) : Inlines.silk_LSHIFT_SAT32(speech_nrg, 15);
            speech_nrg = Inlines.silk_SQRT_APPROX(speech_nrg);
            SA_Q15 = Inlines.silk_SMULWB(32768 + speech_nrg, SA_Q15);
        }
        psEncC.speech_activity_Q8 = Inlines.silk_min_int(Inlines.silk_RSHIFT(SA_Q15, 7), 255);
        int smooth_coef_Q16 = Inlines.silk_SMULWB(4096, Inlines.silk_SMULWB(SA_Q15, SA_Q15));
        if (psEncC.frame_length == 10 * psEncC.fs_kHz) {
            smooth_coef_Q16 >>= 1;
        }
        for (b = 0; b < 4; ++b) {
            psSilk_VAD.NrgRatioSmth_Q8[b] = Inlines.silk_SMLAWB(psSilk_VAD.NrgRatioSmth_Q8[b], NrgToNoiseRatio_Q8[b] - psSilk_VAD.NrgRatioSmth_Q8[b], smooth_coef_Q16);
            SNR_Q7 = 3 * (Inlines.silk_lin2log(psSilk_VAD.NrgRatioSmth_Q8[b]) - 1024);
            psEncC.input_quality_bands_Q15[b] = Sigmoid.silk_sigm_Q15(Inlines.silk_RSHIFT(SNR_Q7 - 2048, 4));
        }
        return ret;
    }

    static void silk_VAD_GetNoiseLevels(int[] pX, SilkVADState psSilk_VAD) {
        int min_coef = psSilk_VAD.counter < 1000 ? Inlines.silk_DIV32_16(Short.MAX_VALUE, (short)(Inlines.silk_RSHIFT(psSilk_VAD.counter, 4) + 1)) : 0;
        for (int k = 0; k < 4; ++k) {
            int nl = psSilk_VAD.NL[k];
            Inlines.OpusAssert(nl >= 0);
            int nrg = Inlines.silk_ADD_POS_SAT32(pX[k], psSilk_VAD.NoiseLevelBias[k]);
            Inlines.OpusAssert(nrg > 0);
            int inv_nrg = Inlines.silk_DIV32(Integer.MAX_VALUE, nrg);
            Inlines.OpusAssert(inv_nrg >= 0);
            int coef = nrg > Inlines.silk_LSHIFT(nl, 3) ? 128 : (nrg < nl ? 1024 : Inlines.silk_SMULWB(Inlines.silk_SMULWW(inv_nrg, nl), 2048));
            coef = Inlines.silk_max_int(coef, min_coef);
            psSilk_VAD.inv_NL[k] = Inlines.silk_SMLAWB(psSilk_VAD.inv_NL[k], inv_nrg - psSilk_VAD.inv_NL[k], coef);
            Inlines.OpusAssert(psSilk_VAD.inv_NL[k] >= 0);
            nl = Inlines.silk_DIV32(Integer.MAX_VALUE, psSilk_VAD.inv_NL[k]);
            Inlines.OpusAssert(nl >= 0);
            psSilk_VAD.NL[k] = nl = Inlines.silk_min(nl, 0xFFFFFF);
        }
        ++psSilk_VAD.counter;
    }
}
