package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;
import dimasik.managers.mods.voicechat.decoder.BWExpander;
import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.Filters;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.LPCInversePredGain;
import dimasik.managers.mods.voicechat.decoder.PLCStruct;
import dimasik.managers.mods.voicechat.decoder.SilkChannelDecoder;
import dimasik.managers.mods.voicechat.decoder.SilkDecoderControl;
import dimasik.managers.mods.voicechat.decoder.SumSqrShift;

class PLC {
    private static final int NB_ATT = 2;
    private static final short[] HARM_ATT_Q15 = new short[]{32440, 31130};
    private static final short[] PLC_RAND_ATTENUATE_V_Q15 = new short[]{31130, 26214};
    private static final short[] PLC_RAND_ATTENUATE_UV_Q15 = new short[]{32440, 29491};

    PLC() {
    }

    static void silk_PLC_Reset(SilkChannelDecoder psDec) {
        psDec.sPLC.pitchL_Q8 = Inlines.silk_LSHIFT(psDec.frame_length, 7);
        psDec.sPLC.prevGain_Q16[0] = 65536;
        psDec.sPLC.prevGain_Q16[1] = 65536;
        psDec.sPLC.subfr_length = 20;
        psDec.sPLC.nb_subfr = 2;
    }

    static void silk_PLC(SilkChannelDecoder psDec, SilkDecoderControl psDecCtrl, short[] frame, int frame_ptr, int lost) {
        if (psDec.fs_kHz != psDec.sPLC.fs_kHz) {
            PLC.silk_PLC_Reset(psDec);
            psDec.sPLC.fs_kHz = psDec.fs_kHz;
        }
        if (lost != 0) {
            PLC.silk_PLC_conceal(psDec, psDecCtrl, frame, frame_ptr);
            ++psDec.lossCnt;
        } else {
            PLC.silk_PLC_update(psDec, psDecCtrl);
        }
    }

    static void silk_PLC_update(SilkChannelDecoder psDec, SilkDecoderControl psDecCtrl) {
        PLCStruct psPLC = psDec.sPLC;
        psDec.prevSignalType = psDec.indices.signalType;
        int LTP_Gain_Q14 = 0;
        if (psDec.indices.signalType == 2) {
            int i;
            for (int j = 0; j * psDec.subfr_length < psDecCtrl.pitchL[psDec.nb_subfr - 1] && j != psDec.nb_subfr; ++j) {
                int temp_LTP_Gain_Q14 = 0;
                for (i = 0; i < 5; ++i) {
                    temp_LTP_Gain_Q14 += psDecCtrl.LTPCoef_Q14[(psDec.nb_subfr - 1 - j) * 5 + i];
                }
                if (temp_LTP_Gain_Q14 <= LTP_Gain_Q14) continue;
                LTP_Gain_Q14 = temp_LTP_Gain_Q14;
                System.arraycopy(psDecCtrl.LTPCoef_Q14, Inlines.silk_SMULBB(psDec.nb_subfr - 1 - j, 5), psPLC.LTPCoef_Q14, 0, 5);
                psPLC.pitchL_Q8 = Inlines.silk_LSHIFT(psDecCtrl.pitchL[psDec.nb_subfr - 1 - j], 8);
            }
            Arrays.MemSet(psPLC.LTPCoef_Q14, (short)0, 5);
            psPLC.LTPCoef_Q14[2] = (short)LTP_Gain_Q14;
            if (LTP_Gain_Q14 < 11469) {
                int tmp = Inlines.silk_LSHIFT(11469, 10);
                int scale_Q10 = Inlines.silk_DIV32(tmp, Inlines.silk_max(LTP_Gain_Q14, 1));
                for (i = 0; i < 5; ++i) {
                    psPLC.LTPCoef_Q14[i] = (short)Inlines.silk_RSHIFT(Inlines.silk_SMULBB(psPLC.LTPCoef_Q14[i], scale_Q10), 10);
                }
            } else if (LTP_Gain_Q14 > 15565) {
                int tmp = Inlines.silk_LSHIFT(15565, 14);
                int scale_Q14 = Inlines.silk_DIV32(tmp, Inlines.silk_max(LTP_Gain_Q14, 1));
                for (i = 0; i < 5; ++i) {
                    psPLC.LTPCoef_Q14[i] = (short)Inlines.silk_RSHIFT(Inlines.silk_SMULBB(psPLC.LTPCoef_Q14[i], scale_Q14), 14);
                }
            }
        } else {
            psPLC.pitchL_Q8 = Inlines.silk_LSHIFT(Inlines.silk_SMULBB(psDec.fs_kHz, 18), 8);
            Arrays.MemSet(psPLC.LTPCoef_Q14, (short)0, 5);
        }
        System.arraycopy(psDecCtrl.PredCoef_Q12[1], 0, psPLC.prevLPC_Q12, 0, psDec.LPC_order);
        psPLC.prevLTP_scale_Q14 = (short)psDecCtrl.LTP_scale_Q14;
        System.arraycopy(psDecCtrl.Gains_Q16, psDec.nb_subfr - 2, psPLC.prevGain_Q16, 0, 2);
        psPLC.subfr_length = psDec.subfr_length;
        psPLC.nb_subfr = psDec.nb_subfr;
    }

    static void silk_PLC_energy(BoxedValueInt energy1, BoxedValueInt shift1, BoxedValueInt energy2, BoxedValueInt shift2, int[] exc_Q14, int[] prevGain_Q10, int subfr_length, int nb_subfr) {
        int exc_buf_ptr = 0;
        short[] exc_buf = new short[2 * subfr_length];
        for (int k = 0; k < 2; ++k) {
            for (int i = 0; i < subfr_length; ++i) {
                exc_buf[exc_buf_ptr + i] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT(Inlines.silk_SMULWW(exc_Q14[i + (k + nb_subfr - 2) * subfr_length], prevGain_Q10[k]), 8));
            }
            exc_buf_ptr += subfr_length;
        }
        SumSqrShift.silk_sum_sqr_shift(energy1, shift1, exc_buf, subfr_length);
        SumSqrShift.silk_sum_sqr_shift(energy2, shift2, exc_buf, subfr_length, subfr_length);
    }

    static void silk_PLC_conceal(SilkChannelDecoder psDec, SilkDecoderControl psDecCtrl, short[] frame, int frame_ptr) {
        int j;
        int i;
        BoxedValueInt energy1 = new BoxedValueInt(0);
        BoxedValueInt energy2 = new BoxedValueInt(0);
        BoxedValueInt shift1 = new BoxedValueInt(0);
        BoxedValueInt shift2 = new BoxedValueInt(0);
        short[] sLTP = new short[psDec.ltp_mem_length];
        int[] sLTP_Q14 = new int[psDec.ltp_mem_length + psDec.frame_length];
        PLCStruct psPLC = psDec.sPLC;
        int[] prevGain_Q10 = new int[]{Inlines.silk_RSHIFT(psPLC.prevGain_Q16[0], 6), Inlines.silk_RSHIFT(psPLC.prevGain_Q16[1], 6)};
        if (psDec.first_frame_after_reset != 0) {
            Arrays.MemSet(psPLC.prevLPC_Q12, (short)0, 16);
        }
        PLC.silk_PLC_energy(energy1, shift1, energy2, shift2, psDec.exc_Q14, prevGain_Q10, psDec.subfr_length, psDec.nb_subfr);
        int rand_ptr = Inlines.silk_RSHIFT(energy1.Val, shift2.Val) < Inlines.silk_RSHIFT(energy2.Val, shift1.Val) ? Inlines.silk_max_int(0, (psPLC.nb_subfr - 1) * psPLC.subfr_length - 128) : Inlines.silk_max_int(0, psPLC.nb_subfr * psPLC.subfr_length - 128);
        short[] B_Q14 = psPLC.LTPCoef_Q14;
        short rand_scale_Q14 = psPLC.randScale_Q14;
        short harm_Gain_Q15 = HARM_ATT_Q15[Inlines.silk_min_int(1, psDec.lossCnt)];
        int rand_Gain_Q15 = psDec.prevSignalType == 2 ? PLC_RAND_ATTENUATE_V_Q15[Inlines.silk_min_int(1, psDec.lossCnt)] : PLC_RAND_ATTENUATE_UV_Q15[Inlines.silk_min_int(1, psDec.lossCnt)];
        BWExpander.silk_bwexpander(psPLC.prevLPC_Q12, psDec.LPC_order, 64881);
        if (psDec.lossCnt == 0) {
            rand_scale_Q14 = 16384;
            if (psDec.prevSignalType == 2) {
                for (i = 0; i < 5; ++i) {
                    rand_scale_Q14 = (short)(rand_scale_Q14 - B_Q14[i]);
                }
                rand_scale_Q14 = Inlines.silk_max_16((short)3277, rand_scale_Q14);
                rand_scale_Q14 = (short)Inlines.silk_RSHIFT(Inlines.silk_SMULBB(rand_scale_Q14, psPLC.prevLTP_scale_Q14), 14);
            } else {
                int invGain_Q30 = LPCInversePredGain.silk_LPC_inverse_pred_gain(psPLC.prevLPC_Q12, psDec.LPC_order);
                int down_scale_Q30 = Inlines.silk_min_32(Inlines.silk_RSHIFT(0x40000000, 3), invGain_Q30);
                down_scale_Q30 = Inlines.silk_max_32(Inlines.silk_RSHIFT(0x40000000, 8), down_scale_Q30);
                down_scale_Q30 = Inlines.silk_LSHIFT(down_scale_Q30, 3);
                rand_Gain_Q15 = Inlines.silk_RSHIFT(Inlines.silk_SMULWB(down_scale_Q30, rand_Gain_Q15), 14);
            }
        }
        int rand_seed = psPLC.rand_seed;
        int lag = Inlines.silk_RSHIFT_ROUND(psPLC.pitchL_Q8, 8);
        int sLTP_buf_idx = psDec.ltp_mem_length;
        int idx = psDec.ltp_mem_length - lag - psDec.LPC_order - 2;
        Inlines.OpusAssert(idx > 0);
        Filters.silk_LPC_analysis_filter(sLTP, idx, psDec.outBuf, idx, psPLC.prevLPC_Q12, 0, psDec.ltp_mem_length - idx, psDec.LPC_order);
        int inv_gain_Q30 = Inlines.silk_INVERSE32_varQ(psPLC.prevGain_Q16[1], 46);
        inv_gain_Q30 = Inlines.silk_min(inv_gain_Q30, 0x3FFFFFFF);
        for (i = idx + psDec.LPC_order; i < psDec.ltp_mem_length; ++i) {
            sLTP_Q14[i] = Inlines.silk_SMULWB(inv_gain_Q30, sLTP[i]);
        }
        for (int k = 0; k < psDec.nb_subfr; ++k) {
            int pred_lag_ptr = sLTP_buf_idx - lag + 2;
            for (i = 0; i < psDec.subfr_length; ++i) {
                int LTP_pred_Q12 = 2;
                LTP_pred_Q12 = Inlines.silk_SMLAWB(LTP_pred_Q12, sLTP_Q14[pred_lag_ptr], B_Q14[0]);
                LTP_pred_Q12 = Inlines.silk_SMLAWB(LTP_pred_Q12, sLTP_Q14[pred_lag_ptr - 1], B_Q14[1]);
                LTP_pred_Q12 = Inlines.silk_SMLAWB(LTP_pred_Q12, sLTP_Q14[pred_lag_ptr - 2], B_Q14[2]);
                LTP_pred_Q12 = Inlines.silk_SMLAWB(LTP_pred_Q12, sLTP_Q14[pred_lag_ptr - 3], B_Q14[3]);
                LTP_pred_Q12 = Inlines.silk_SMLAWB(LTP_pred_Q12, sLTP_Q14[pred_lag_ptr - 4], B_Q14[4]);
                ++pred_lag_ptr;
                rand_seed = Inlines.silk_RAND(rand_seed);
                idx = Inlines.silk_RSHIFT(rand_seed, 25) & 0x7F;
                sLTP_Q14[sLTP_buf_idx] = Inlines.silk_LSHIFT32(Inlines.silk_SMLAWB(LTP_pred_Q12, psDec.exc_Q14[rand_ptr + idx], rand_scale_Q14), 2);
                ++sLTP_buf_idx;
            }
            for (j = 0; j < 5; ++j) {
                B_Q14[j] = (short)Inlines.silk_RSHIFT(Inlines.silk_SMULBB(harm_Gain_Q15, B_Q14[j]), 15);
            }
            rand_scale_Q14 = (short)Inlines.silk_RSHIFT(Inlines.silk_SMULBB(rand_scale_Q14, rand_Gain_Q15), 15);
            psPLC.pitchL_Q8 = Inlines.silk_SMLAWB(psPLC.pitchL_Q8, psPLC.pitchL_Q8, 655);
            psPLC.pitchL_Q8 = Inlines.silk_min_32(psPLC.pitchL_Q8, Inlines.silk_LSHIFT(Inlines.silk_SMULBB(18, psDec.fs_kHz), 8));
            lag = Inlines.silk_RSHIFT_ROUND(psPLC.pitchL_Q8, 8);
        }
        int sLPC_Q14_ptr = psDec.ltp_mem_length - 16;
        System.arraycopy(psDec.sLPC_Q14_buf, 0, sLTP_Q14, sLPC_Q14_ptr, 16);
        Inlines.OpusAssert(psDec.LPC_order >= 10);
        for (i = 0; i < psDec.frame_length; ++i) {
            int sLPCmaxi = sLPC_Q14_ptr + 16 + i;
            int LPC_pred_Q10 = Inlines.silk_RSHIFT(psDec.LPC_order, 1);
            LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, sLTP_Q14[sLPCmaxi - 1], psPLC.prevLPC_Q12[0]);
            LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, sLTP_Q14[sLPCmaxi - 2], psPLC.prevLPC_Q12[1]);
            LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, sLTP_Q14[sLPCmaxi - 3], psPLC.prevLPC_Q12[2]);
            LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, sLTP_Q14[sLPCmaxi - 4], psPLC.prevLPC_Q12[3]);
            LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, sLTP_Q14[sLPCmaxi - 5], psPLC.prevLPC_Q12[4]);
            LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, sLTP_Q14[sLPCmaxi - 6], psPLC.prevLPC_Q12[5]);
            LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, sLTP_Q14[sLPCmaxi - 7], psPLC.prevLPC_Q12[6]);
            LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, sLTP_Q14[sLPCmaxi - 8], psPLC.prevLPC_Q12[7]);
            LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, sLTP_Q14[sLPCmaxi - 9], psPLC.prevLPC_Q12[8]);
            LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, sLTP_Q14[sLPCmaxi - 10], psPLC.prevLPC_Q12[9]);
            for (j = 10; j < psDec.LPC_order; ++j) {
                LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, sLTP_Q14[sLPCmaxi - j - 1], psPLC.prevLPC_Q12[j]);
            }
            sLTP_Q14[sLPCmaxi] = Inlines.silk_ADD_LSHIFT32(sLTP_Q14[sLPCmaxi], LPC_pred_Q10, 4);
            frame[frame_ptr + i] = (short)Inlines.silk_SAT16(Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(Inlines.silk_SMULWW(sLTP_Q14[sLPCmaxi], prevGain_Q10[1]), 8)));
        }
        System.arraycopy(sLTP_Q14, sLPC_Q14_ptr + psDec.frame_length, psDec.sLPC_Q14_buf, 0, 16);
        psPLC.rand_seed = rand_seed;
        psPLC.randScale_Q14 = rand_scale_Q14;
        for (i = 0; i < 4; ++i) {
            psDecCtrl.pitchL[i] = lag;
        }
    }

    static void silk_PLC_glue_frames(SilkChannelDecoder psDec, short[] frame, int frame_ptr, int length) {
        BoxedValueInt energy_shift = new BoxedValueInt(0);
        BoxedValueInt energy = new BoxedValueInt(0);
        PLCStruct psPLC = psDec.sPLC;
        if (psDec.lossCnt != 0) {
            BoxedValueInt boxed_conc_e = new BoxedValueInt(0);
            BoxedValueInt boxed_conc_shift = new BoxedValueInt(0);
            SumSqrShift.silk_sum_sqr_shift(boxed_conc_e, boxed_conc_shift, frame, frame_ptr, length);
            psPLC.conc_energy = boxed_conc_e.Val;
            psPLC.conc_energy_shift = boxed_conc_shift.Val;
            psPLC.last_frame_lost = 1;
        } else {
            if (psDec.sPLC.last_frame_lost != 0) {
                SumSqrShift.silk_sum_sqr_shift(energy, energy_shift, frame, frame_ptr, length);
                if (energy_shift.Val > psPLC.conc_energy_shift) {
                    psPLC.conc_energy = Inlines.silk_RSHIFT(psPLC.conc_energy, energy_shift.Val - psPLC.conc_energy_shift);
                } else if (energy_shift.Val < psPLC.conc_energy_shift) {
                    energy.Val = Inlines.silk_RSHIFT(energy.Val, psPLC.conc_energy_shift - energy_shift.Val);
                }
                if (energy.Val > psPLC.conc_energy) {
                    int LZ = Inlines.silk_CLZ32(psPLC.conc_energy);
                    psPLC.conc_energy = Inlines.silk_LSHIFT(psPLC.conc_energy, --LZ);
                    energy.Val = Inlines.silk_RSHIFT(energy.Val, Inlines.silk_max_32(24 - LZ, 0));
                    int frac_Q24 = Inlines.silk_DIV32(psPLC.conc_energy, Inlines.silk_max(energy.Val, 1));
                    int gain_Q16 = Inlines.silk_LSHIFT(Inlines.silk_SQRT_APPROX(frac_Q24), 4);
                    int slope_Q16 = Inlines.silk_DIV32_16(65536 - gain_Q16, length);
                    slope_Q16 = Inlines.silk_LSHIFT(slope_Q16, 2);
                    for (int i = frame_ptr; i < frame_ptr + length; ++i) {
                        frame[i] = (short)Inlines.silk_SMULWB(gain_Q16, frame[i]);
                        if ((gain_Q16 += slope_Q16) > 65536) break;
                    }
                }
            }
            psPLC.last_frame_lost = 0;
        }
    }
}
