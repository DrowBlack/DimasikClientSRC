package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.Kernels;
import dimasik.managers.mods.voicechat.decoder.SilkChannelEncoder;
import dimasik.managers.mods.voicechat.decoder.SilkEncoderControl;
import dimasik.managers.mods.voicechat.decoder.SilkPrefilterState;
import dimasik.managers.mods.voicechat.decoder.SilkTables;

class Filters {
    private static final short A_fb1_20 = 10788;
    private static final short A_fb1_21 = -24290;
    private static final int QA = 24;
    private static final int A_LIMIT = 0xFFEF9E;

    Filters() {
    }

    static void silk_warped_LPC_analysis_filter(int[] state, int[] res_Q2, short[] coef_Q13, int coef_Q13_ptr, short[] input, int input_ptr, short lambda_Q16, int length, int order) {
        Inlines.OpusAssert((order & 1) == 0);
        for (int n = 0; n < length; ++n) {
            int tmp2 = Inlines.silk_SMLAWB(state[0], state[1], lambda_Q16);
            state[0] = Inlines.silk_LSHIFT(input[input_ptr + n], 14);
            int tmp1 = Inlines.silk_SMLAWB(state[1], state[2] - tmp2, lambda_Q16);
            state[1] = tmp2;
            int acc_Q11 = Inlines.silk_RSHIFT(order, 1);
            acc_Q11 = Inlines.silk_SMLAWB(acc_Q11, tmp2, coef_Q13[coef_Q13_ptr]);
            for (int i = 2; i < order; i += 2) {
                tmp2 = Inlines.silk_SMLAWB(state[i], state[i + 1] - tmp1, lambda_Q16);
                state[i] = tmp1;
                acc_Q11 = Inlines.silk_SMLAWB(acc_Q11, tmp1, coef_Q13[coef_Q13_ptr + i - 1]);
                tmp1 = Inlines.silk_SMLAWB(state[i + 1], state[i + 2] - tmp2, lambda_Q16);
                state[i + 1] = tmp2;
                acc_Q11 = Inlines.silk_SMLAWB(acc_Q11, tmp2, coef_Q13[coef_Q13_ptr + i]);
            }
            state[order] = tmp1;
            acc_Q11 = Inlines.silk_SMLAWB(acc_Q11, tmp1, coef_Q13[coef_Q13_ptr + order - 1]);
            res_Q2[n] = Inlines.silk_LSHIFT(input[input_ptr + n], 2) - Inlines.silk_RSHIFT_ROUND(acc_Q11, 9);
        }
    }

    static void silk_prefilter(SilkChannelEncoder psEnc, SilkEncoderControl psEncCtrl, int[] xw_Q3, short[] x, int x_ptr) {
        SilkPrefilterState P = psEnc.sPrefilt;
        short[] B_Q10 = new short[2];
        int px = x_ptr;
        int pxw_Q3 = 0;
        int lag = P.lagPrev;
        int[] x_filt_Q12 = new int[psEnc.subfr_length];
        int[] st_res_Q2 = new int[psEnc.subfr_length];
        for (int k = 0; k < psEnc.nb_subfr; ++k) {
            int HarmShapeGain_Q12;
            if (psEnc.indices.signalType == 2) {
                lag = psEncCtrl.pitchL[k];
            }
            Inlines.OpusAssert((HarmShapeGain_Q12 = Inlines.silk_SMULWB(psEncCtrl.HarmShapeGain_Q14[k], 16384 - psEncCtrl.HarmBoost_Q14[k])) >= 0);
            int HarmShapeFIRPacked_Q12 = Inlines.silk_RSHIFT(HarmShapeGain_Q12, 2);
            HarmShapeFIRPacked_Q12 |= Inlines.silk_LSHIFT(Inlines.silk_RSHIFT(HarmShapeGain_Q12, 1), 16);
            int Tilt_Q14 = psEncCtrl.Tilt_Q14[k];
            int LF_shp_Q14 = psEncCtrl.LF_shp_Q14[k];
            int AR1_shp_Q13 = k * 16;
            Filters.silk_warped_LPC_analysis_filter(P.sAR_shp, st_res_Q2, psEncCtrl.AR1_Q13, AR1_shp_Q13, x, px, (short)psEnc.warping_Q16, psEnc.subfr_length, psEnc.shapingLPCOrder);
            B_Q10[0] = (short)Inlines.silk_RSHIFT_ROUND(psEncCtrl.GainsPre_Q14[k], 4);
            int tmp_32 = Inlines.silk_SMLABB(0x333333, psEncCtrl.HarmBoost_Q14[k], HarmShapeGain_Q12);
            tmp_32 = Inlines.silk_SMLABB(tmp_32, psEncCtrl.coding_quality_Q14, 410);
            tmp_32 = Inlines.silk_SMULWB(tmp_32, -psEncCtrl.GainsPre_Q14[k]);
            tmp_32 = Inlines.silk_RSHIFT_ROUND(tmp_32, 14);
            B_Q10[1] = (short)Inlines.silk_SAT16(tmp_32);
            x_filt_Q12[0] = Inlines.silk_MLA(Inlines.silk_MUL(st_res_Q2[0], B_Q10[0]), P.sHarmHP_Q2, B_Q10[1]);
            for (int j = 1; j < psEnc.subfr_length; ++j) {
                x_filt_Q12[j] = Inlines.silk_MLA(Inlines.silk_MUL(st_res_Q2[j], B_Q10[0]), st_res_Q2[j - 1], B_Q10[1]);
            }
            P.sHarmHP_Q2 = st_res_Q2[psEnc.subfr_length - 1];
            Filters.silk_prefilt(P, x_filt_Q12, xw_Q3, pxw_Q3, HarmShapeFIRPacked_Q12, Tilt_Q14, LF_shp_Q14, lag, psEnc.subfr_length);
            px += psEnc.subfr_length;
            pxw_Q3 += psEnc.subfr_length;
        }
        P.lagPrev = psEncCtrl.pitchL[psEnc.nb_subfr - 1];
    }

    static void silk_prefilt(SilkPrefilterState P, int[] st_res_Q12, int[] xw_Q3, int xw_Q3_ptr, int HarmShapeFIRPacked_Q12, int Tilt_Q14, int LF_shp_Q14, int lag, int length) {
        short[] LTP_shp_buf = P.sLTP_shp;
        int LTP_shp_buf_idx = P.sLTP_shp_buf_idx;
        int sLF_AR_shp_Q12 = P.sLF_AR_shp_Q12;
        int sLF_MA_shp_Q12 = P.sLF_MA_shp_Q12;
        for (int i = 0; i < length; ++i) {
            int n_LTP_Q12;
            if (lag > 0) {
                Inlines.OpusAssert(true);
                int idx = lag + LTP_shp_buf_idx;
                n_LTP_Q12 = Inlines.silk_SMULBB(LTP_shp_buf[idx - 1 - 1 & 0x1FF], HarmShapeFIRPacked_Q12);
                n_LTP_Q12 = Inlines.silk_SMLABT(n_LTP_Q12, LTP_shp_buf[idx - 1 & 0x1FF], HarmShapeFIRPacked_Q12);
                n_LTP_Q12 = Inlines.silk_SMLABB(n_LTP_Q12, LTP_shp_buf[idx - 1 + 1 & 0x1FF], HarmShapeFIRPacked_Q12);
            } else {
                n_LTP_Q12 = 0;
            }
            int n_Tilt_Q10 = Inlines.silk_SMULWB(sLF_AR_shp_Q12, Tilt_Q14);
            int n_LF_Q10 = Inlines.silk_SMLAWB(Inlines.silk_SMULWT(sLF_AR_shp_Q12, LF_shp_Q14), sLF_MA_shp_Q12, LF_shp_Q14);
            sLF_AR_shp_Q12 = Inlines.silk_SUB32(st_res_Q12[i], Inlines.silk_LSHIFT(n_Tilt_Q10, 2));
            sLF_MA_shp_Q12 = Inlines.silk_SUB32(sLF_AR_shp_Q12, Inlines.silk_LSHIFT(n_LF_Q10, 2));
            LTP_shp_buf_idx = LTP_shp_buf_idx - 1 & 0x1FF;
            LTP_shp_buf[LTP_shp_buf_idx] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(sLF_MA_shp_Q12, 12));
            xw_Q3[xw_Q3_ptr + i] = Inlines.silk_RSHIFT_ROUND(Inlines.silk_SUB32(sLF_MA_shp_Q12, n_LTP_Q12), 9);
        }
        P.sLF_AR_shp_Q12 = sLF_AR_shp_Q12;
        P.sLF_MA_shp_Q12 = sLF_MA_shp_Q12;
        P.sLTP_shp_buf_idx = LTP_shp_buf_idx;
    }

    static void silk_biquad_alt(short[] input, int input_ptr, int[] B_Q28, int[] A_Q28, int[] S, short[] output, int output_ptr, int len, int stride) {
        int A0_L_Q28 = -A_Q28[0] & 0x3FFF;
        int A0_U_Q28 = Inlines.silk_RSHIFT(-A_Q28[0], 14);
        int A1_L_Q28 = -A_Q28[1] & 0x3FFF;
        int A1_U_Q28 = Inlines.silk_RSHIFT(-A_Q28[1], 14);
        for (int k = 0; k < len; ++k) {
            short inval = input[input_ptr + k * stride];
            int out32_Q14 = Inlines.silk_LSHIFT(Inlines.silk_SMLAWB(S[0], B_Q28[0], inval), 2);
            S[0] = S[1] + Inlines.silk_RSHIFT_ROUND(Inlines.silk_SMULWB(out32_Q14, A0_L_Q28), 14);
            S[0] = Inlines.silk_SMLAWB(S[0], out32_Q14, A0_U_Q28);
            S[0] = Inlines.silk_SMLAWB(S[0], B_Q28[1], inval);
            S[1] = Inlines.silk_RSHIFT_ROUND(Inlines.silk_SMULWB(out32_Q14, A1_L_Q28), 14);
            S[1] = Inlines.silk_SMLAWB(S[1], out32_Q14, A1_U_Q28);
            S[1] = Inlines.silk_SMLAWB(S[1], B_Q28[2], inval);
            output[output_ptr + k * stride] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT(out32_Q14 + 16384 - 1, 14));
        }
    }

    static void silk_biquad_alt(short[] input, int input_ptr, int[] B_Q28, int[] A_Q28, int[] S, int S_ptr, short[] output, int output_ptr, int len, int stride) {
        int A0_L_Q28 = -A_Q28[0] & 0x3FFF;
        int A0_U_Q28 = Inlines.silk_RSHIFT(-A_Q28[0], 14);
        int A1_L_Q28 = -A_Q28[1] & 0x3FFF;
        int A1_U_Q28 = Inlines.silk_RSHIFT(-A_Q28[1], 14);
        for (int k = 0; k < len; ++k) {
            int s1 = S_ptr + 1;
            short inval = input[input_ptr + k * stride];
            int out32_Q14 = Inlines.silk_LSHIFT(Inlines.silk_SMLAWB(S[S_ptr], B_Q28[0], inval), 2);
            S[S_ptr] = S[s1] + Inlines.silk_RSHIFT_ROUND(Inlines.silk_SMULWB(out32_Q14, A0_L_Q28), 14);
            S[S_ptr] = Inlines.silk_SMLAWB(S[S_ptr], out32_Q14, A0_U_Q28);
            S[S_ptr] = Inlines.silk_SMLAWB(S[S_ptr], B_Q28[1], inval);
            S[s1] = Inlines.silk_RSHIFT_ROUND(Inlines.silk_SMULWB(out32_Q14, A1_L_Q28), 14);
            S[s1] = Inlines.silk_SMLAWB(S[s1], out32_Q14, A1_U_Q28);
            S[s1] = Inlines.silk_SMLAWB(S[s1], B_Q28[2], inval);
            output[output_ptr + k * stride] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT(out32_Q14 + 16384 - 1, 14));
        }
    }

    static void silk_ana_filt_bank_1(short[] input, int input_ptr, int[] S, short[] outL, short[] outH, int outH_ptr, int N) {
        int N2 = Inlines.silk_RSHIFT(N, 1);
        for (int k = 0; k < N2; ++k) {
            int in32 = Inlines.silk_LSHIFT(input[input_ptr + 2 * k], 10);
            int Y = Inlines.silk_SUB32(in32, S[0]);
            int X = Inlines.silk_SMLAWB(Y, Y, -24290);
            int out_1 = Inlines.silk_ADD32(S[0], X);
            S[0] = Inlines.silk_ADD32(in32, X);
            in32 = Inlines.silk_LSHIFT(input[input_ptr + 2 * k + 1], 10);
            Y = Inlines.silk_SUB32(in32, S[1]);
            X = Inlines.silk_SMULWB(Y, 10788);
            int out_2 = Inlines.silk_ADD32(S[1], X);
            S[1] = Inlines.silk_ADD32(in32, X);
            outL[k] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(Inlines.silk_ADD32(out_2, out_1), 11));
            outH[outH_ptr + k] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(Inlines.silk_SUB32(out_2, out_1), 11));
        }
    }

    static void silk_bwexpander_32(int[] ar, int d, int chirp_Q16) {
        int chirp_minus_one_Q16 = chirp_Q16 - 65536;
        for (int i = 0; i < d - 1; ++i) {
            ar[i] = Inlines.silk_SMULWW(chirp_Q16, ar[i]);
            chirp_Q16 += Inlines.silk_RSHIFT_ROUND(Inlines.silk_MUL(chirp_Q16, chirp_minus_one_Q16), 16);
        }
        ar[d - 1] = Inlines.silk_SMULWW(chirp_Q16, ar[d - 1]);
    }

    static void silk_LP_interpolate_filter_taps(int[] B_Q28, int[] A_Q28, int ind, int fac_Q16) {
        if (ind < 4) {
            if (fac_Q16 > 0) {
                if (fac_Q16 < 32768) {
                    for (int nb = 0; nb < 3; ++nb) {
                        B_Q28[nb] = Inlines.silk_SMLAWB(SilkTables.silk_Transition_LP_B_Q28[ind][nb], SilkTables.silk_Transition_LP_B_Q28[ind + 1][nb] - SilkTables.silk_Transition_LP_B_Q28[ind][nb], fac_Q16);
                    }
                    for (int na = 0; na < 2; ++na) {
                        A_Q28[na] = Inlines.silk_SMLAWB(SilkTables.silk_Transition_LP_A_Q28[ind][na], SilkTables.silk_Transition_LP_A_Q28[ind + 1][na] - SilkTables.silk_Transition_LP_A_Q28[ind][na], fac_Q16);
                    }
                } else {
                    Inlines.OpusAssert(fac_Q16 - 65536 == Inlines.silk_SAT16(fac_Q16 - 65536));
                    for (int nb = 0; nb < 3; ++nb) {
                        B_Q28[nb] = Inlines.silk_SMLAWB(SilkTables.silk_Transition_LP_B_Q28[ind + 1][nb], SilkTables.silk_Transition_LP_B_Q28[ind + 1][nb] - SilkTables.silk_Transition_LP_B_Q28[ind][nb], fac_Q16 - 65536);
                    }
                    for (int na = 0; na < 2; ++na) {
                        A_Q28[na] = Inlines.silk_SMLAWB(SilkTables.silk_Transition_LP_A_Q28[ind + 1][na], SilkTables.silk_Transition_LP_A_Q28[ind + 1][na] - SilkTables.silk_Transition_LP_A_Q28[ind][na], fac_Q16 - 65536);
                    }
                }
            } else {
                System.arraycopy(SilkTables.silk_Transition_LP_B_Q28[ind], 0, B_Q28, 0, 3);
                System.arraycopy(SilkTables.silk_Transition_LP_A_Q28[ind], 0, A_Q28, 0, 2);
            }
        } else {
            System.arraycopy(SilkTables.silk_Transition_LP_B_Q28[4], 0, B_Q28, 0, 3);
            System.arraycopy(SilkTables.silk_Transition_LP_A_Q28[4], 0, A_Q28, 0, 2);
        }
    }

    static void silk_LPC_analysis_filter(short[] output, int output_ptr, short[] input, int input_ptr, short[] B, int B_ptr, int len, int d) {
        int j;
        short[] mem = new short[16];
        short[] num = new short[16];
        Inlines.OpusAssert(d >= 6);
        Inlines.OpusAssert((d & 1) == 0);
        Inlines.OpusAssert(d <= len);
        Inlines.OpusAssert(d <= 16);
        for (j = 0; j < d; ++j) {
            num[j] = (short)(0 - B[B_ptr + j]);
        }
        for (j = 0; j < d; ++j) {
            mem[j] = input[input_ptr + d - j - 1];
        }
        Kernels.celt_fir(input, input_ptr + d, num, output, output_ptr + d, len - d, d, mem);
        for (j = output_ptr; j < output_ptr + d; ++j) {
            output[j] = 0;
        }
    }

    static int LPC_inverse_pred_gain_QA(int[][] A_QA, int order) {
        int rc_mult1_Q30;
        int rc_Q31;
        int[] Anew_QA = A_QA[order & 1];
        int invGain_Q30 = 0x40000000;
        for (int k = order - 1; k > 0; --k) {
            if (Anew_QA[k] > 0xFFEF9E || Anew_QA[k] < -16773022) {
                return 0;
            }
            rc_Q31 = 0 - Inlines.silk_LSHIFT(Anew_QA[k], 7);
            rc_mult1_Q30 = 0x40000000 - Inlines.silk_SMMUL(rc_Q31, rc_Q31);
            Inlines.OpusAssert(rc_mult1_Q30 > 32768);
            Inlines.OpusAssert(rc_mult1_Q30 <= 0x40000000);
            int mult2Q = 32 - Inlines.silk_CLZ32(Inlines.silk_abs(rc_mult1_Q30));
            int rc_mult2 = Inlines.silk_INVERSE32_varQ(rc_mult1_Q30, mult2Q + 30);
            invGain_Q30 = Inlines.silk_LSHIFT(Inlines.silk_SMMUL(invGain_Q30, rc_mult1_Q30), 2);
            Inlines.OpusAssert(invGain_Q30 >= 0);
            Inlines.OpusAssert(invGain_Q30 <= 0x40000000);
            int[] Aold_QA = Anew_QA;
            Anew_QA = A_QA[k & 1];
            for (int n = 0; n < k; ++n) {
                int tmp_QA = Aold_QA[n] - Inlines.MUL32_FRAC_Q(Aold_QA[k - n - 1], rc_Q31, 31);
                Anew_QA[n] = Inlines.MUL32_FRAC_Q(tmp_QA, rc_mult2, mult2Q);
            }
        }
        if (Anew_QA[0] > 0xFFEF9E || Anew_QA[0] < -16773022) {
            return 0;
        }
        rc_Q31 = 0 - Inlines.silk_LSHIFT(Anew_QA[0], 7);
        rc_mult1_Q30 = 0x40000000 - Inlines.silk_SMMUL(rc_Q31, rc_Q31);
        Inlines.OpusAssert((invGain_Q30 = Inlines.silk_LSHIFT(Inlines.silk_SMMUL(invGain_Q30, rc_mult1_Q30), 2)) >= 0);
        Inlines.OpusAssert(invGain_Q30 <= 0x40000000);
        return invGain_Q30;
    }

    static int silk_LPC_inverse_pred_gain(short[] A_Q12, int order) {
        int[][] Atmp_QA = new int[][]{new int[order], new int[order]};
        int DC_resp = 0;
        int[] Anew_QA = Atmp_QA[order & 1];
        for (int k = 0; k < order; ++k) {
            DC_resp += A_Q12[k];
            Anew_QA[k] = Inlines.silk_LSHIFT32(A_Q12[k], 12);
        }
        if (DC_resp >= 4096) {
            return 0;
        }
        return Filters.LPC_inverse_pred_gain_QA(Atmp_QA, order);
    }
}
