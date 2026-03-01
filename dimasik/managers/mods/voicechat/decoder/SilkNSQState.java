package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;
import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.Filters;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.SideInfoIndices;
import dimasik.managers.mods.voicechat.decoder.SilkChannelEncoder;
import dimasik.managers.mods.voicechat.decoder.SilkConstants;
import dimasik.managers.mods.voicechat.decoder.SilkTables;

class SilkNSQState {
    final short[] xq = new short[640];
    final int[] sLTP_shp_Q14 = new int[640];
    final int[] sLPC_Q14 = new int[80 + SilkConstants.NSQ_LPC_BUF_LENGTH];
    final int[] sAR2_Q14 = new int[16];
    int sLF_AR_shp_Q14 = 0;
    int lagPrev = 0;
    int sLTP_buf_idx = 0;
    int sLTP_shp_buf_idx = 0;
    int rand_seed = 0;
    int prev_gain_Q16 = 0;
    int rewhite_flag = 0;

    SilkNSQState() {
    }

    void Reset() {
        Arrays.MemSet(this.xq, (short)0, 640);
        Arrays.MemSet(this.sLTP_shp_Q14, 0, 640);
        Arrays.MemSet(this.sLPC_Q14, 0, 80 + SilkConstants.NSQ_LPC_BUF_LENGTH);
        Arrays.MemSet(this.sAR2_Q14, 0, 16);
        this.sLF_AR_shp_Q14 = 0;
        this.lagPrev = 0;
        this.sLTP_buf_idx = 0;
        this.sLTP_shp_buf_idx = 0;
        this.rand_seed = 0;
        this.prev_gain_Q16 = 0;
        this.rewhite_flag = 0;
    }

    void Assign(SilkNSQState other) {
        this.sLF_AR_shp_Q14 = other.sLF_AR_shp_Q14;
        this.lagPrev = other.lagPrev;
        this.sLTP_buf_idx = other.sLTP_buf_idx;
        this.sLTP_shp_buf_idx = other.sLTP_shp_buf_idx;
        this.rand_seed = other.rand_seed;
        this.prev_gain_Q16 = other.prev_gain_Q16;
        this.rewhite_flag = other.rewhite_flag;
        System.arraycopy(other.xq, 0, this.xq, 0, 640);
        System.arraycopy(other.sLTP_shp_Q14, 0, this.sLTP_shp_Q14, 0, 640);
        System.arraycopy(other.sLPC_Q14, 0, this.sLPC_Q14, 0, 80 + SilkConstants.NSQ_LPC_BUF_LENGTH);
        System.arraycopy(other.sAR2_Q14, 0, this.sAR2_Q14, 0, 16);
    }

    void silk_NSQ(SilkChannelEncoder psEncC, SideInfoIndices psIndices, int[] x_Q3, byte[] pulses, short[][] PredCoef_Q12, short[] LTPCoef_Q14, short[] AR2_Q13, int[] HarmShapeGain_Q14, int[] Tilt_Q14, int[] LF_shp_Q14, int[] Gains_Q16, int[] pitchL, int Lambda_Q10, int LTP_scale_Q14) {
        int pulses_ptr = 0;
        int x_Q3_ptr = 0;
        this.rand_seed = psIndices.Seed;
        int lag = this.lagPrev;
        Inlines.OpusAssert(this.prev_gain_Q16 != 0);
        short offset_Q10 = SilkTables.silk_Quantization_Offsets_Q10[psIndices.signalType >> 1][psIndices.quantOffsetType];
        int LSF_interpolation_flag = psIndices.NLSFInterpCoef_Q2 == 4 ? 0 : 1;
        int[] sLTP_Q15 = new int[psEncC.ltp_mem_length + psEncC.frame_length];
        short[] sLTP = new short[psEncC.ltp_mem_length + psEncC.frame_length];
        int[] x_sc_Q10 = new int[psEncC.subfr_length];
        this.sLTP_shp_buf_idx = psEncC.ltp_mem_length;
        this.sLTP_buf_idx = psEncC.ltp_mem_length;
        int pxq = psEncC.ltp_mem_length;
        for (int k = 0; k < psEncC.nb_subfr; ++k) {
            int A_Q12 = k >> 1 | 1 - LSF_interpolation_flag;
            int B_Q14 = k * 5;
            int AR_shp_Q13 = k * 16;
            Inlines.OpusAssert(HarmShapeGain_Q14[k] >= 0);
            int HarmShapeFIRPacked_Q14 = Inlines.silk_RSHIFT(HarmShapeGain_Q14[k], 2);
            HarmShapeFIRPacked_Q14 |= Inlines.silk_LSHIFT(Inlines.silk_RSHIFT(HarmShapeGain_Q14[k], 1), 16);
            this.rewhite_flag = 0;
            if (psIndices.signalType == 2) {
                lag = pitchL[k];
                if ((k & 3 - Inlines.silk_LSHIFT(LSF_interpolation_flag, 1)) == 0) {
                    int start_idx = psEncC.ltp_mem_length - lag - psEncC.predictLPCOrder - 2;
                    Inlines.OpusAssert(start_idx > 0);
                    Filters.silk_LPC_analysis_filter(sLTP, start_idx, this.xq, start_idx + k * psEncC.subfr_length, PredCoef_Q12[A_Q12], 0, psEncC.ltp_mem_length - start_idx, psEncC.predictLPCOrder);
                    this.rewhite_flag = 1;
                    this.sLTP_buf_idx = psEncC.ltp_mem_length;
                }
            }
            this.silk_nsq_scale_states(psEncC, x_Q3, x_Q3_ptr, x_sc_Q10, sLTP, sLTP_Q15, k, LTP_scale_Q14, Gains_Q16, pitchL, psIndices.signalType);
            this.silk_noise_shape_quantizer(psIndices.signalType, x_sc_Q10, pulses, pulses_ptr, this.xq, pxq, sLTP_Q15, PredCoef_Q12[A_Q12], LTPCoef_Q14, B_Q14, AR2_Q13, AR_shp_Q13, lag, HarmShapeFIRPacked_Q14, Tilt_Q14[k], LF_shp_Q14[k], Gains_Q16[k], Lambda_Q10, offset_Q10, psEncC.subfr_length, psEncC.shapingLPCOrder, psEncC.predictLPCOrder);
            x_Q3_ptr += psEncC.subfr_length;
            pulses_ptr += psEncC.subfr_length;
            pxq += psEncC.subfr_length;
        }
        this.lagPrev = pitchL[psEncC.nb_subfr - 1];
        Arrays.MemMove(this.xq, psEncC.frame_length, 0, psEncC.ltp_mem_length);
        Arrays.MemMove(this.sLTP_shp_Q14, psEncC.frame_length, 0, psEncC.ltp_mem_length);
    }

    private void silk_noise_shape_quantizer(int signalType, int[] x_sc_Q10, byte[] pulses, int pulses_ptr, short[] xq, int xq_ptr, int[] sLTP_Q15, short[] a_Q12, short[] b_Q14, int b_Q14_ptr, short[] AR_shp_Q13, int AR_shp_Q13_ptr, int lag, int HarmShapeFIRPacked_Q14, int Tilt_Q14, int LF_shp_Q14, int Gain_Q16, int Lambda_Q10, int offset_Q10, int length, int shapingLPCOrder, int predictLPCOrder) {
        int shp_lag_ptr = this.sLTP_shp_buf_idx - lag + 1;
        int pred_lag_ptr = this.sLTP_buf_idx - lag + 2;
        int Gain_Q10 = Inlines.silk_RSHIFT(Gain_Q16, 6);
        int psLPC_Q14 = SilkConstants.NSQ_LPC_BUF_LENGTH - 1;
        for (int i = 0; i < length; ++i) {
            int sLF_AR_shp_Q14;
            int rd2_Q20;
            int rd1_Q20;
            int q2_Q10;
            int q1_Q10;
            int q1_Q0;
            int LTP_pred_Q13;
            this.rand_seed = Inlines.silk_RAND(this.rand_seed);
            Inlines.OpusAssert(predictLPCOrder == 10 || predictLPCOrder == 16);
            int LPC_pred_Q10 = Inlines.silk_RSHIFT(predictLPCOrder, 1);
            LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, this.sLPC_Q14[psLPC_Q14 - 0], a_Q12[0]);
            LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, this.sLPC_Q14[psLPC_Q14 - 1], a_Q12[1]);
            LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, this.sLPC_Q14[psLPC_Q14 - 2], a_Q12[2]);
            LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, this.sLPC_Q14[psLPC_Q14 - 3], a_Q12[3]);
            LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, this.sLPC_Q14[psLPC_Q14 - 4], a_Q12[4]);
            LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, this.sLPC_Q14[psLPC_Q14 - 5], a_Q12[5]);
            LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, this.sLPC_Q14[psLPC_Q14 - 6], a_Q12[6]);
            LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, this.sLPC_Q14[psLPC_Q14 - 7], a_Q12[7]);
            LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, this.sLPC_Q14[psLPC_Q14 - 8], a_Q12[8]);
            LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, this.sLPC_Q14[psLPC_Q14 - 9], a_Q12[9]);
            if (predictLPCOrder == 16) {
                LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, this.sLPC_Q14[psLPC_Q14 - 10], a_Q12[10]);
                LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, this.sLPC_Q14[psLPC_Q14 - 11], a_Q12[11]);
                LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, this.sLPC_Q14[psLPC_Q14 - 12], a_Q12[12]);
                LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, this.sLPC_Q14[psLPC_Q14 - 13], a_Q12[13]);
                LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, this.sLPC_Q14[psLPC_Q14 - 14], a_Q12[14]);
                LPC_pred_Q10 = Inlines.silk_SMLAWB(LPC_pred_Q10, this.sLPC_Q14[psLPC_Q14 - 15], a_Q12[15]);
            }
            if (signalType == 2) {
                LTP_pred_Q13 = 2;
                LTP_pred_Q13 = Inlines.silk_SMLAWB(LTP_pred_Q13, sLTP_Q15[pred_lag_ptr], b_Q14[b_Q14_ptr]);
                LTP_pred_Q13 = Inlines.silk_SMLAWB(LTP_pred_Q13, sLTP_Q15[pred_lag_ptr - 1], b_Q14[b_Q14_ptr + 1]);
                LTP_pred_Q13 = Inlines.silk_SMLAWB(LTP_pred_Q13, sLTP_Q15[pred_lag_ptr - 2], b_Q14[b_Q14_ptr + 2]);
                LTP_pred_Q13 = Inlines.silk_SMLAWB(LTP_pred_Q13, sLTP_Q15[pred_lag_ptr - 3], b_Q14[b_Q14_ptr + 3]);
                LTP_pred_Q13 = Inlines.silk_SMLAWB(LTP_pred_Q13, sLTP_Q15[pred_lag_ptr - 4], b_Q14[b_Q14_ptr + 4]);
                ++pred_lag_ptr;
            } else {
                LTP_pred_Q13 = 0;
            }
            Inlines.OpusAssert((shapingLPCOrder & 1) == 0);
            int tmp2 = this.sLPC_Q14[psLPC_Q14];
            int tmp1 = this.sAR2_Q14[0];
            this.sAR2_Q14[0] = tmp2;
            int n_AR_Q12 = Inlines.silk_RSHIFT(shapingLPCOrder, 1);
            n_AR_Q12 = Inlines.silk_SMLAWB(n_AR_Q12, tmp2, AR_shp_Q13[AR_shp_Q13_ptr]);
            for (int j = 2; j < shapingLPCOrder; j += 2) {
                tmp2 = this.sAR2_Q14[j - 1];
                this.sAR2_Q14[j - 1] = tmp1;
                n_AR_Q12 = Inlines.silk_SMLAWB(n_AR_Q12, tmp1, AR_shp_Q13[AR_shp_Q13_ptr + j - 1]);
                tmp1 = this.sAR2_Q14[j + 0];
                this.sAR2_Q14[j + 0] = tmp2;
                n_AR_Q12 = Inlines.silk_SMLAWB(n_AR_Q12, tmp2, AR_shp_Q13[AR_shp_Q13_ptr + j]);
            }
            this.sAR2_Q14[shapingLPCOrder - 1] = tmp1;
            n_AR_Q12 = Inlines.silk_SMLAWB(n_AR_Q12, tmp1, AR_shp_Q13[AR_shp_Q13_ptr + shapingLPCOrder - 1]);
            n_AR_Q12 = Inlines.silk_LSHIFT32(n_AR_Q12, 1);
            n_AR_Q12 = Inlines.silk_SMLAWB(n_AR_Q12, this.sLF_AR_shp_Q14, Tilt_Q14);
            int n_LF_Q12 = Inlines.silk_SMULWB(this.sLTP_shp_Q14[this.sLTP_shp_buf_idx - 1], LF_shp_Q14);
            n_LF_Q12 = Inlines.silk_SMLAWT(n_LF_Q12, this.sLF_AR_shp_Q14, LF_shp_Q14);
            Inlines.OpusAssert(lag > 0 || signalType != 2);
            tmp1 = Inlines.silk_SUB32(Inlines.silk_LSHIFT32(LPC_pred_Q10, 2), n_AR_Q12);
            tmp1 = Inlines.silk_SUB32(tmp1, n_LF_Q12);
            if (lag > 0) {
                int n_LTP_Q13 = Inlines.silk_SMULWB(Inlines.silk_ADD32(this.sLTP_shp_Q14[shp_lag_ptr], this.sLTP_shp_Q14[shp_lag_ptr - 2]), HarmShapeFIRPacked_Q14);
                n_LTP_Q13 = Inlines.silk_SMLAWT(n_LTP_Q13, this.sLTP_shp_Q14[shp_lag_ptr - 1], HarmShapeFIRPacked_Q14);
                n_LTP_Q13 = Inlines.silk_LSHIFT(n_LTP_Q13, 1);
                ++shp_lag_ptr;
                tmp2 = Inlines.silk_SUB32(LTP_pred_Q13, n_LTP_Q13);
                tmp1 = Inlines.silk_ADD_LSHIFT32(tmp2, tmp1, 1);
                tmp1 = Inlines.silk_RSHIFT_ROUND(tmp1, 3);
            } else {
                tmp1 = Inlines.silk_RSHIFT_ROUND(tmp1, 2);
            }
            int r_Q10 = Inlines.silk_SUB32(x_sc_Q10[i], tmp1);
            if (this.rand_seed < 0) {
                r_Q10 = -r_Q10;
            }
            if ((q1_Q0 = Inlines.silk_RSHIFT(q1_Q10 = Inlines.silk_SUB32(r_Q10 = Inlines.silk_LIMIT_32(r_Q10, -31744, 30720), offset_Q10), 10)) > 0) {
                q1_Q10 = Inlines.silk_SUB32(Inlines.silk_LSHIFT(q1_Q0, 10), 80);
                q1_Q10 = Inlines.silk_ADD32(q1_Q10, offset_Q10);
                q2_Q10 = Inlines.silk_ADD32(q1_Q10, 1024);
                rd1_Q20 = Inlines.silk_SMULBB(q1_Q10, Lambda_Q10);
                rd2_Q20 = Inlines.silk_SMULBB(q2_Q10, Lambda_Q10);
            } else if (q1_Q0 == 0) {
                q1_Q10 = offset_Q10;
                q2_Q10 = Inlines.silk_ADD32(q1_Q10, 944);
                rd1_Q20 = Inlines.silk_SMULBB(q1_Q10, Lambda_Q10);
                rd2_Q20 = Inlines.silk_SMULBB(q2_Q10, Lambda_Q10);
            } else if (q1_Q0 == -1) {
                q2_Q10 = offset_Q10;
                q1_Q10 = Inlines.silk_SUB32(q2_Q10, 944);
                rd1_Q20 = Inlines.silk_SMULBB(-q1_Q10, Lambda_Q10);
                rd2_Q20 = Inlines.silk_SMULBB(q2_Q10, Lambda_Q10);
            } else {
                q1_Q10 = Inlines.silk_ADD32(Inlines.silk_LSHIFT(q1_Q0, 10), 80);
                q1_Q10 = Inlines.silk_ADD32(q1_Q10, offset_Q10);
                q2_Q10 = Inlines.silk_ADD32(q1_Q10, 1024);
                rd1_Q20 = Inlines.silk_SMULBB(-q1_Q10, Lambda_Q10);
                rd2_Q20 = Inlines.silk_SMULBB(-q2_Q10, Lambda_Q10);
            }
            int rr_Q10 = Inlines.silk_SUB32(r_Q10, q1_Q10);
            rd1_Q20 = Inlines.silk_SMLABB(rd1_Q20, rr_Q10, rr_Q10);
            rr_Q10 = Inlines.silk_SUB32(r_Q10, q2_Q10);
            rd2_Q20 = Inlines.silk_SMLABB(rd2_Q20, rr_Q10, rr_Q10);
            if (rd2_Q20 < rd1_Q20) {
                q1_Q10 = q2_Q10;
            }
            pulses[pulses_ptr + i] = (byte)Inlines.silk_RSHIFT_ROUND(q1_Q10, 10);
            int exc_Q14 = Inlines.silk_LSHIFT(q1_Q10, 4);
            if (this.rand_seed < 0) {
                exc_Q14 = -exc_Q14;
            }
            int LPC_exc_Q14 = Inlines.silk_ADD_LSHIFT32(exc_Q14, LTP_pred_Q13, 1);
            int xq_Q14 = Inlines.silk_ADD_LSHIFT32(LPC_exc_Q14, LPC_pred_Q10, 4);
            xq[xq_ptr + i] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(Inlines.silk_SMULWW(xq_Q14, Gain_Q10), 8));
            this.sLPC_Q14[++psLPC_Q14] = xq_Q14;
            this.sLF_AR_shp_Q14 = sLF_AR_shp_Q14 = Inlines.silk_SUB_LSHIFT32(xq_Q14, n_AR_Q12, 2);
            this.sLTP_shp_Q14[this.sLTP_shp_buf_idx] = Inlines.silk_SUB_LSHIFT32(sLF_AR_shp_Q14, n_LF_Q12, 2);
            sLTP_Q15[this.sLTP_buf_idx] = Inlines.silk_LSHIFT(LPC_exc_Q14, 1);
            ++this.sLTP_shp_buf_idx;
            ++this.sLTP_buf_idx;
            this.rand_seed = Inlines.silk_ADD32_ovflw(this.rand_seed, pulses[pulses_ptr + i]);
        }
        System.arraycopy(this.sLPC_Q14, length, this.sLPC_Q14, 0, SilkConstants.NSQ_LPC_BUF_LENGTH);
    }

    private void silk_nsq_scale_states(SilkChannelEncoder psEncC, int[] x_Q3, int x_Q3_ptr, int[] x_sc_Q10, short[] sLTP, int[] sLTP_Q15, int subfr, int LTP_scale_Q14, int[] Gains_Q16, int[] pitchL, int signal_type) {
        int i;
        int lag = pitchL[subfr];
        int inv_gain_Q31 = Inlines.silk_INVERSE32_varQ(Inlines.silk_max(Gains_Q16[subfr], 1), 47);
        Inlines.OpusAssert(inv_gain_Q31 != 0);
        int gain_adj_Q16 = Gains_Q16[subfr] != this.prev_gain_Q16 ? Inlines.silk_DIV32_varQ(this.prev_gain_Q16, Gains_Q16[subfr], 16) : 65536;
        int inv_gain_Q23 = Inlines.silk_RSHIFT_ROUND(inv_gain_Q31, 8);
        for (i = 0; i < psEncC.subfr_length; ++i) {
            x_sc_Q10[i] = Inlines.silk_SMULWW(x_Q3[x_Q3_ptr + i], inv_gain_Q23);
        }
        this.prev_gain_Q16 = Gains_Q16[subfr];
        if (this.rewhite_flag != 0) {
            if (subfr == 0) {
                inv_gain_Q31 = Inlines.silk_LSHIFT(Inlines.silk_SMULWB(inv_gain_Q31, LTP_scale_Q14), 2);
            }
            for (i = this.sLTP_buf_idx - lag - 2; i < this.sLTP_buf_idx; ++i) {
                Inlines.OpusAssert(i < 320);
                sLTP_Q15[i] = Inlines.silk_SMULWB(inv_gain_Q31, sLTP[i]);
            }
        }
        if (gain_adj_Q16 != 65536) {
            for (i = this.sLTP_shp_buf_idx - psEncC.ltp_mem_length; i < this.sLTP_shp_buf_idx; ++i) {
                this.sLTP_shp_Q14[i] = Inlines.silk_SMULWW(gain_adj_Q16, this.sLTP_shp_Q14[i]);
            }
            if (signal_type == 2 && this.rewhite_flag == 0) {
                for (i = this.sLTP_buf_idx - lag - 2; i < this.sLTP_buf_idx; ++i) {
                    sLTP_Q15[i] = Inlines.silk_SMULWW(gain_adj_Q16, sLTP_Q15[i]);
                }
            }
            this.sLF_AR_shp_Q14 = Inlines.silk_SMULWW(gain_adj_Q16, this.sLF_AR_shp_Q14);
            for (i = 0; i < SilkConstants.NSQ_LPC_BUF_LENGTH; ++i) {
                this.sLPC_Q14[i] = Inlines.silk_SMULWW(gain_adj_Q16, this.sLPC_Q14[i]);
            }
            for (i = 0; i < 16; ++i) {
                this.sAR2_Q14[i] = Inlines.silk_SMULWW(gain_adj_Q16, this.sAR2_Q14[i]);
            }
        }
    }

    void silk_NSQ_del_dec(SilkChannelEncoder psEncC, SideInfoIndices psIndices, int[] x_Q3, byte[] pulses, short[][] PredCoef_Q12, short[] LTPCoef_Q14, short[] AR2_Q13, int[] HarmShapeGain_Q14, int[] Tilt_Q14, int[] LF_shp_Q14, int[] Gains_Q16, int[] pitchL, int Lambda_Q10, int LTP_scale_Q14) {
        int last_smple_idx;
        int i;
        int Winner_ind;
        int RDmin_Q10;
        NSQ_del_dec_struct psDD;
        int k;
        int pulses_ptr = 0;
        int x_Q3_ptr = 0;
        int lag = this.lagPrev;
        Inlines.OpusAssert(this.prev_gain_Q16 != 0);
        NSQ_del_dec_struct[] psDelDec = new NSQ_del_dec_struct[psEncC.nStatesDelayedDecision];
        for (int c = 0; c < psEncC.nStatesDelayedDecision; ++c) {
            psDelDec[c] = new NSQ_del_dec_struct(psEncC.shapingLPCOrder);
        }
        for (k = 0; k < psEncC.nStatesDelayedDecision; ++k) {
            psDD = psDelDec[k];
            psDD.SeedInit = psDD.Seed = k + psIndices.Seed & 3;
            psDD.RD_Q10 = 0;
            psDD.LF_AR_Q14 = this.sLF_AR_shp_Q14;
            psDD.Shape_Q14[0] = this.sLTP_shp_Q14[psEncC.ltp_mem_length - 1];
            System.arraycopy(this.sLPC_Q14, 0, psDD.sLPC_Q14, 0, SilkConstants.NSQ_LPC_BUF_LENGTH);
            System.arraycopy(this.sAR2_Q14, 0, psDD.sAR2_Q14, 0, psEncC.shapingLPCOrder);
        }
        short offset_Q10 = SilkTables.silk_Quantization_Offsets_Q10[psIndices.signalType >> 1][psIndices.quantOffsetType];
        int smpl_buf_idx = 0;
        int decisionDelay = Inlines.silk_min_int(32, psEncC.subfr_length);
        if (psIndices.signalType == 2) {
            for (k = 0; k < psEncC.nb_subfr; ++k) {
                decisionDelay = Inlines.silk_min_int(decisionDelay, pitchL[k] - 2 - 1);
            }
        } else if (lag > 0) {
            decisionDelay = Inlines.silk_min_int(decisionDelay, lag - 2 - 1);
        }
        int LSF_interpolation_flag = psIndices.NLSFInterpCoef_Q2 == 4 ? 0 : 1;
        int[] sLTP_Q15 = new int[psEncC.ltp_mem_length + psEncC.frame_length];
        short[] sLTP = new short[psEncC.ltp_mem_length + psEncC.frame_length];
        int[] x_sc_Q10 = new int[psEncC.subfr_length];
        int[] delayedGain_Q10 = new int[32];
        int pxq = psEncC.ltp_mem_length;
        this.sLTP_shp_buf_idx = psEncC.ltp_mem_length;
        this.sLTP_buf_idx = psEncC.ltp_mem_length;
        int subfr = 0;
        for (k = 0; k < psEncC.nb_subfr; ++k) {
            int A_Q12 = k >> 1 | 1 - LSF_interpolation_flag;
            Inlines.OpusAssert(HarmShapeGain_Q14[k] >= 0);
            int HarmShapeFIRPacked_Q14 = Inlines.silk_RSHIFT(HarmShapeGain_Q14[k], 2);
            HarmShapeFIRPacked_Q14 |= Inlines.silk_LSHIFT(Inlines.silk_RSHIFT(HarmShapeGain_Q14[k], 1), 16);
            this.rewhite_flag = 0;
            if (psIndices.signalType == 2) {
                lag = pitchL[k];
                if ((k & 3 - Inlines.silk_LSHIFT(LSF_interpolation_flag, 1)) == 0) {
                    int start_idx;
                    if (k == 2) {
                        RDmin_Q10 = psDelDec[0].RD_Q10;
                        Winner_ind = 0;
                        for (i = 1; i < psEncC.nStatesDelayedDecision; ++i) {
                            if (psDelDec[i].RD_Q10 >= RDmin_Q10) continue;
                            RDmin_Q10 = psDelDec[i].RD_Q10;
                            Winner_ind = i;
                        }
                        for (i = 0; i < psEncC.nStatesDelayedDecision; ++i) {
                            if (i == Winner_ind) continue;
                            psDelDec[i].RD_Q10 += 0x7FFFFFF;
                            Inlines.OpusAssert(psDelDec[i].RD_Q10 >= 0);
                        }
                        psDD = psDelDec[Winner_ind];
                        last_smple_idx = smpl_buf_idx + decisionDelay;
                        for (i = 0; i < decisionDelay; ++i) {
                            last_smple_idx = last_smple_idx - 1 & 0x1F;
                            pulses[pulses_ptr + i - decisionDelay] = (byte)Inlines.silk_RSHIFT_ROUND(psDD.Q_Q10[last_smple_idx], 10);
                            this.xq[pxq + i - decisionDelay] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(Inlines.silk_SMULWW(psDD.Xq_Q14[last_smple_idx], Gains_Q16[1]), 14));
                            this.sLTP_shp_Q14[this.sLTP_shp_buf_idx - decisionDelay + i] = psDD.Shape_Q14[last_smple_idx];
                        }
                        subfr = 0;
                    }
                    Inlines.OpusAssert((start_idx = psEncC.ltp_mem_length - lag - psEncC.predictLPCOrder - 2) > 0);
                    Filters.silk_LPC_analysis_filter(sLTP, start_idx, this.xq, start_idx + k * psEncC.subfr_length, PredCoef_Q12[A_Q12], 0, psEncC.ltp_mem_length - start_idx, psEncC.predictLPCOrder);
                    this.sLTP_buf_idx = psEncC.ltp_mem_length;
                    this.rewhite_flag = 1;
                }
            }
            this.silk_nsq_del_dec_scale_states(psEncC, psDelDec, x_Q3, x_Q3_ptr, x_sc_Q10, sLTP, sLTP_Q15, k, psEncC.nStatesDelayedDecision, LTP_scale_Q14, Gains_Q16, pitchL, psIndices.signalType, decisionDelay);
            BoxedValueInt smpl_buf_idx_boxed = new BoxedValueInt(smpl_buf_idx);
            this.silk_noise_shape_quantizer_del_dec(psDelDec, psIndices.signalType, x_sc_Q10, pulses, pulses_ptr, this.xq, pxq, sLTP_Q15, delayedGain_Q10, PredCoef_Q12[A_Q12], LTPCoef_Q14, k * 5, AR2_Q13, k * 16, lag, HarmShapeFIRPacked_Q14, Tilt_Q14[k], LF_shp_Q14[k], Gains_Q16[k], Lambda_Q10, offset_Q10, psEncC.subfr_length, subfr++, psEncC.shapingLPCOrder, psEncC.predictLPCOrder, psEncC.warping_Q16, psEncC.nStatesDelayedDecision, smpl_buf_idx_boxed, decisionDelay);
            smpl_buf_idx = smpl_buf_idx_boxed.Val;
            x_Q3_ptr += psEncC.subfr_length;
            pulses_ptr += psEncC.subfr_length;
            pxq += psEncC.subfr_length;
        }
        RDmin_Q10 = psDelDec[0].RD_Q10;
        Winner_ind = 0;
        for (k = 1; k < psEncC.nStatesDelayedDecision; ++k) {
            if (psDelDec[k].RD_Q10 >= RDmin_Q10) continue;
            RDmin_Q10 = psDelDec[k].RD_Q10;
            Winner_ind = k;
        }
        psDD = psDelDec[Winner_ind];
        psIndices.Seed = (byte)psDD.SeedInit;
        last_smple_idx = smpl_buf_idx + decisionDelay;
        int Gain_Q10 = Inlines.silk_RSHIFT32(Gains_Q16[psEncC.nb_subfr - 1], 6);
        for (i = 0; i < decisionDelay; ++i) {
            last_smple_idx = last_smple_idx - 1 & 0x1F;
            pulses[pulses_ptr + i - decisionDelay] = (byte)Inlines.silk_RSHIFT_ROUND(psDD.Q_Q10[last_smple_idx], 10);
            this.xq[pxq + i - decisionDelay] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(Inlines.silk_SMULWW(psDD.Xq_Q14[last_smple_idx], Gain_Q10), 8));
            this.sLTP_shp_Q14[this.sLTP_shp_buf_idx - decisionDelay + i] = psDD.Shape_Q14[last_smple_idx];
        }
        System.arraycopy(psDD.sLPC_Q14, psEncC.subfr_length, this.sLPC_Q14, 0, SilkConstants.NSQ_LPC_BUF_LENGTH);
        System.arraycopy(psDD.sAR2_Q14, 0, this.sAR2_Q14, 0, psEncC.shapingLPCOrder);
        this.sLF_AR_shp_Q14 = psDD.LF_AR_Q14;
        this.lagPrev = pitchL[psEncC.nb_subfr - 1];
        Arrays.MemMove(this.xq, psEncC.frame_length, 0, psEncC.ltp_mem_length);
        Arrays.MemMove(this.sLTP_shp_Q14, psEncC.frame_length, 0, psEncC.ltp_mem_length);
    }

    private void silk_noise_shape_quantizer_del_dec(NSQ_del_dec_struct[] psDelDec, int signalType, int[] x_Q10, byte[] pulses, int pulses_ptr, short[] xq, int xq_ptr, int[] sLTP_Q15, int[] delayedGain_Q10, short[] a_Q12, short[] b_Q14, int b_Q14_ptr, short[] AR_shp_Q13, int AR_shp_Q13_ptr, int lag, int HarmShapeFIRPacked_Q14, int Tilt_Q14, int LF_shp_Q14, int Gain_Q16, int Lambda_Q10, int offset_Q10, int length, int subfr, int shapingLPCOrder, int predictLPCOrder, int warping_Q16, int nStatesDelayedDecision, BoxedValueInt smpl_buf_idx, int decisionDelay) {
        NSQ_del_dec_struct psDD;
        int k;
        Inlines.OpusAssert(nStatesDelayedDecision > 0);
        NSQ_sample_struct[] sampleStates = new NSQ_sample_struct[2 * nStatesDelayedDecision];
        for (int c = 0; c < 2 * nStatesDelayedDecision; ++c) {
            sampleStates[c] = new NSQ_sample_struct();
        }
        int shp_lag_ptr = this.sLTP_shp_buf_idx - lag + 1;
        int pred_lag_ptr = this.sLTP_buf_idx - lag + 2;
        int Gain_Q10 = Inlines.silk_RSHIFT(Gain_Q16, 6);
        for (int i = 0; i < length; ++i) {
            int k2;
            int SS_left;
            int n_LTP_Q14;
            int LTP_pred_Q14;
            if (signalType == 2) {
                LTP_pred_Q14 = 2;
                LTP_pred_Q14 = Inlines.silk_SMLAWB(LTP_pred_Q14, sLTP_Q15[pred_lag_ptr], b_Q14[b_Q14_ptr + 0]);
                LTP_pred_Q14 = Inlines.silk_SMLAWB(LTP_pred_Q14, sLTP_Q15[pred_lag_ptr - 1], b_Q14[b_Q14_ptr + 1]);
                LTP_pred_Q14 = Inlines.silk_SMLAWB(LTP_pred_Q14, sLTP_Q15[pred_lag_ptr - 2], b_Q14[b_Q14_ptr + 2]);
                LTP_pred_Q14 = Inlines.silk_SMLAWB(LTP_pred_Q14, sLTP_Q15[pred_lag_ptr - 3], b_Q14[b_Q14_ptr + 3]);
                LTP_pred_Q14 = Inlines.silk_SMLAWB(LTP_pred_Q14, sLTP_Q15[pred_lag_ptr - 4], b_Q14[b_Q14_ptr + 4]);
                LTP_pred_Q14 = Inlines.silk_LSHIFT(LTP_pred_Q14, 1);
                ++pred_lag_ptr;
            } else {
                LTP_pred_Q14 = 0;
            }
            if (lag > 0) {
                n_LTP_Q14 = Inlines.silk_SMULWB(Inlines.silk_ADD32(this.sLTP_shp_Q14[shp_lag_ptr], this.sLTP_shp_Q14[shp_lag_ptr - 2]), HarmShapeFIRPacked_Q14);
                n_LTP_Q14 = Inlines.silk_SMLAWT(n_LTP_Q14, this.sLTP_shp_Q14[shp_lag_ptr - 1], HarmShapeFIRPacked_Q14);
                n_LTP_Q14 = Inlines.silk_SUB_LSHIFT32(LTP_pred_Q14, n_LTP_Q14, 2);
                ++shp_lag_ptr;
            } else {
                n_LTP_Q14 = 0;
            }
            for (k = 0; k < nStatesDelayedDecision; ++k) {
                int rd2_Q10;
                int rd1_Q10;
                int q2_Q10;
                int q1_Q10;
                int q1_Q0;
                psDD = psDelDec[k];
                int[] psDD_sAR2 = psDD.sAR2_Q14;
                SS_left = 2 * k;
                int SS_right = SS_left + 1;
                psDD.Seed = Inlines.silk_RAND(psDD.Seed);
                int psLPC_Q14 = SilkConstants.NSQ_LPC_BUF_LENGTH - 1 + i;
                Inlines.OpusAssert(predictLPCOrder == 10 || predictLPCOrder == 16);
                int LPC_pred_Q14 = Inlines.silk_RSHIFT(predictLPCOrder, 1);
                LPC_pred_Q14 = Inlines.silk_SMLAWB(LPC_pred_Q14, psDD.sLPC_Q14[psLPC_Q14], a_Q12[0]);
                LPC_pred_Q14 = Inlines.silk_SMLAWB(LPC_pred_Q14, psDD.sLPC_Q14[psLPC_Q14 - 1], a_Q12[1]);
                LPC_pred_Q14 = Inlines.silk_SMLAWB(LPC_pred_Q14, psDD.sLPC_Q14[psLPC_Q14 - 2], a_Q12[2]);
                LPC_pred_Q14 = Inlines.silk_SMLAWB(LPC_pred_Q14, psDD.sLPC_Q14[psLPC_Q14 - 3], a_Q12[3]);
                LPC_pred_Q14 = Inlines.silk_SMLAWB(LPC_pred_Q14, psDD.sLPC_Q14[psLPC_Q14 - 4], a_Q12[4]);
                LPC_pred_Q14 = Inlines.silk_SMLAWB(LPC_pred_Q14, psDD.sLPC_Q14[psLPC_Q14 - 5], a_Q12[5]);
                LPC_pred_Q14 = Inlines.silk_SMLAWB(LPC_pred_Q14, psDD.sLPC_Q14[psLPC_Q14 - 6], a_Q12[6]);
                LPC_pred_Q14 = Inlines.silk_SMLAWB(LPC_pred_Q14, psDD.sLPC_Q14[psLPC_Q14 - 7], a_Q12[7]);
                LPC_pred_Q14 = Inlines.silk_SMLAWB(LPC_pred_Q14, psDD.sLPC_Q14[psLPC_Q14 - 8], a_Q12[8]);
                LPC_pred_Q14 = Inlines.silk_SMLAWB(LPC_pred_Q14, psDD.sLPC_Q14[psLPC_Q14 - 9], a_Q12[9]);
                if (predictLPCOrder == 16) {
                    LPC_pred_Q14 = Inlines.silk_SMLAWB(LPC_pred_Q14, psDD.sLPC_Q14[psLPC_Q14 - 10], a_Q12[10]);
                    LPC_pred_Q14 = Inlines.silk_SMLAWB(LPC_pred_Q14, psDD.sLPC_Q14[psLPC_Q14 - 11], a_Q12[11]);
                    LPC_pred_Q14 = Inlines.silk_SMLAWB(LPC_pred_Q14, psDD.sLPC_Q14[psLPC_Q14 - 12], a_Q12[12]);
                    LPC_pred_Q14 = Inlines.silk_SMLAWB(LPC_pred_Q14, psDD.sLPC_Q14[psLPC_Q14 - 13], a_Q12[13]);
                    LPC_pred_Q14 = Inlines.silk_SMLAWB(LPC_pred_Q14, psDD.sLPC_Q14[psLPC_Q14 - 14], a_Q12[14]);
                    LPC_pred_Q14 = Inlines.silk_SMLAWB(LPC_pred_Q14, psDD.sLPC_Q14[psLPC_Q14 - 15], a_Q12[15]);
                }
                LPC_pred_Q14 = Inlines.silk_LSHIFT(LPC_pred_Q14, 4);
                Inlines.OpusAssert((shapingLPCOrder & 1) == 0);
                int tmp2 = Inlines.silk_SMLAWB(psDD.sLPC_Q14[psLPC_Q14], psDD_sAR2[0], warping_Q16);
                int tmp1 = Inlines.silk_SMLAWB(psDD_sAR2[0], psDD_sAR2[1] - tmp2, warping_Q16);
                psDD_sAR2[0] = tmp2;
                int n_AR_Q14 = Inlines.silk_RSHIFT(shapingLPCOrder, 1);
                n_AR_Q14 = Inlines.silk_SMLAWB(n_AR_Q14, tmp2, AR_shp_Q13[AR_shp_Q13_ptr]);
                for (int j = 2; j < shapingLPCOrder; j += 2) {
                    tmp2 = Inlines.silk_SMLAWB(psDD_sAR2[j - 1], psDD_sAR2[j + 0] - tmp1, warping_Q16);
                    psDD_sAR2[j - 1] = tmp1;
                    n_AR_Q14 = Inlines.silk_SMLAWB(n_AR_Q14, tmp1, AR_shp_Q13[AR_shp_Q13_ptr + j - 1]);
                    tmp1 = Inlines.silk_SMLAWB(psDD_sAR2[j + 0], psDD_sAR2[j + 1] - tmp2, warping_Q16);
                    psDD_sAR2[j + 0] = tmp2;
                    n_AR_Q14 = Inlines.silk_SMLAWB(n_AR_Q14, tmp2, AR_shp_Q13[AR_shp_Q13_ptr + j]);
                }
                psDD_sAR2[shapingLPCOrder - 1] = tmp1;
                n_AR_Q14 = Inlines.silk_SMLAWB(n_AR_Q14, tmp1, AR_shp_Q13[AR_shp_Q13_ptr + shapingLPCOrder - 1]);
                n_AR_Q14 = Inlines.silk_LSHIFT(n_AR_Q14, 1);
                n_AR_Q14 = Inlines.silk_SMLAWB(n_AR_Q14, psDD.LF_AR_Q14, Tilt_Q14);
                n_AR_Q14 = Inlines.silk_LSHIFT(n_AR_Q14, 2);
                int n_LF_Q14 = Inlines.silk_SMULWB(psDD.Shape_Q14[smpl_buf_idx.Val], LF_shp_Q14);
                n_LF_Q14 = Inlines.silk_SMLAWT(n_LF_Q14, psDD.LF_AR_Q14, LF_shp_Q14);
                n_LF_Q14 = Inlines.silk_LSHIFT(n_LF_Q14, 2);
                tmp1 = Inlines.silk_ADD32(n_AR_Q14, n_LF_Q14);
                tmp2 = Inlines.silk_ADD32(n_LTP_Q14, LPC_pred_Q14);
                tmp1 = Inlines.silk_SUB32(tmp2, tmp1);
                tmp1 = Inlines.silk_RSHIFT_ROUND(tmp1, 4);
                int r_Q10 = Inlines.silk_SUB32(x_Q10[i], tmp1);
                if (psDD.Seed < 0) {
                    r_Q10 = -r_Q10;
                }
                if ((q1_Q0 = Inlines.silk_RSHIFT(q1_Q10 = Inlines.silk_SUB32(r_Q10 = Inlines.silk_LIMIT_32(r_Q10, -31744, 30720), offset_Q10), 10)) > 0) {
                    q1_Q10 = Inlines.silk_SUB32(Inlines.silk_LSHIFT(q1_Q0, 10), 80);
                    q1_Q10 = Inlines.silk_ADD32(q1_Q10, offset_Q10);
                    q2_Q10 = Inlines.silk_ADD32(q1_Q10, 1024);
                    rd1_Q10 = Inlines.silk_SMULBB(q1_Q10, Lambda_Q10);
                    rd2_Q10 = Inlines.silk_SMULBB(q2_Q10, Lambda_Q10);
                } else if (q1_Q0 == 0) {
                    q1_Q10 = offset_Q10;
                    q2_Q10 = Inlines.silk_ADD32(q1_Q10, 944);
                    rd1_Q10 = Inlines.silk_SMULBB(q1_Q10, Lambda_Q10);
                    rd2_Q10 = Inlines.silk_SMULBB(q2_Q10, Lambda_Q10);
                } else if (q1_Q0 == -1) {
                    q2_Q10 = offset_Q10;
                    q1_Q10 = Inlines.silk_SUB32(q2_Q10, 944);
                    rd1_Q10 = Inlines.silk_SMULBB(-q1_Q10, Lambda_Q10);
                    rd2_Q10 = Inlines.silk_SMULBB(q2_Q10, Lambda_Q10);
                } else {
                    q1_Q10 = Inlines.silk_ADD32(Inlines.silk_LSHIFT(q1_Q0, 10), 80);
                    q1_Q10 = Inlines.silk_ADD32(q1_Q10, offset_Q10);
                    q2_Q10 = Inlines.silk_ADD32(q1_Q10, 1024);
                    rd1_Q10 = Inlines.silk_SMULBB(-q1_Q10, Lambda_Q10);
                    rd2_Q10 = Inlines.silk_SMULBB(-q2_Q10, Lambda_Q10);
                }
                int rr_Q10 = Inlines.silk_SUB32(r_Q10, q1_Q10);
                rd1_Q10 = Inlines.silk_RSHIFT(Inlines.silk_SMLABB(rd1_Q10, rr_Q10, rr_Q10), 10);
                rr_Q10 = Inlines.silk_SUB32(r_Q10, q2_Q10);
                rd2_Q10 = Inlines.silk_RSHIFT(Inlines.silk_SMLABB(rd2_Q10, rr_Q10, rr_Q10), 10);
                if (rd1_Q10 < rd2_Q10) {
                    sampleStates[SS_left].RD_Q10 = Inlines.silk_ADD32(psDD.RD_Q10, rd1_Q10);
                    sampleStates[SS_right].RD_Q10 = Inlines.silk_ADD32(psDD.RD_Q10, rd2_Q10);
                    sampleStates[SS_left].Q_Q10 = q1_Q10;
                    sampleStates[SS_right].Q_Q10 = q2_Q10;
                } else {
                    sampleStates[SS_left].RD_Q10 = Inlines.silk_ADD32(psDD.RD_Q10, rd2_Q10);
                    sampleStates[SS_right].RD_Q10 = Inlines.silk_ADD32(psDD.RD_Q10, rd1_Q10);
                    sampleStates[SS_left].Q_Q10 = q2_Q10;
                    sampleStates[SS_right].Q_Q10 = q1_Q10;
                }
                int exc_Q14 = Inlines.silk_LSHIFT32(sampleStates[SS_left].Q_Q10, 4);
                if (psDD.Seed < 0) {
                    exc_Q14 = -exc_Q14;
                }
                int LPC_exc_Q14 = Inlines.silk_ADD32(exc_Q14, LTP_pred_Q14);
                int xq_Q14 = Inlines.silk_ADD32(LPC_exc_Q14, LPC_pred_Q14);
                int sLF_AR_shp_Q14 = Inlines.silk_SUB32(xq_Q14, n_AR_Q14);
                sampleStates[SS_left].sLTP_shp_Q14 = Inlines.silk_SUB32(sLF_AR_shp_Q14, n_LF_Q14);
                sampleStates[SS_left].LF_AR_Q14 = sLF_AR_shp_Q14;
                sampleStates[SS_left].LPC_exc_Q14 = LPC_exc_Q14;
                sampleStates[SS_left].xq_Q14 = xq_Q14;
                exc_Q14 = Inlines.silk_LSHIFT32(sampleStates[SS_right].Q_Q10, 4);
                if (psDD.Seed < 0) {
                    exc_Q14 = -exc_Q14;
                }
                LPC_exc_Q14 = Inlines.silk_ADD32(exc_Q14, LTP_pred_Q14);
                xq_Q14 = Inlines.silk_ADD32(LPC_exc_Q14, LPC_pred_Q14);
                sLF_AR_shp_Q14 = Inlines.silk_SUB32(xq_Q14, n_AR_Q14);
                sampleStates[SS_right].sLTP_shp_Q14 = Inlines.silk_SUB32(sLF_AR_shp_Q14, n_LF_Q14);
                sampleStates[SS_right].LF_AR_Q14 = sLF_AR_shp_Q14;
                sampleStates[SS_right].LPC_exc_Q14 = LPC_exc_Q14;
                sampleStates[SS_right].xq_Q14 = xq_Q14;
            }
            smpl_buf_idx.Val = smpl_buf_idx.Val - 1 & 0x1F;
            int last_smple_idx = smpl_buf_idx.Val + decisionDelay & 0x1F;
            int RDmin_Q10 = sampleStates[0].RD_Q10;
            int Winner_ind = 0;
            for (k = 1; k < nStatesDelayedDecision; ++k) {
                if (sampleStates[k * 2].RD_Q10 >= RDmin_Q10) continue;
                RDmin_Q10 = sampleStates[k * 2].RD_Q10;
                Winner_ind = k;
            }
            int Winner_rand_state = psDelDec[Winner_ind].RandState[last_smple_idx];
            for (k = 0; k < nStatesDelayedDecision; ++k) {
                if (psDelDec[k].RandState[last_smple_idx] == Winner_rand_state) continue;
                k2 = k * 2;
                sampleStates[k2].RD_Q10 = Inlines.silk_ADD32(sampleStates[k2].RD_Q10, 0x7FFFFFF);
                sampleStates[k2 + 1].RD_Q10 = Inlines.silk_ADD32(sampleStates[k2 + 1].RD_Q10, 0x7FFFFFF);
                Inlines.OpusAssert(sampleStates[k2].RD_Q10 >= 0);
            }
            int RDmax_Q10 = sampleStates[0].RD_Q10;
            RDmin_Q10 = sampleStates[1].RD_Q10;
            int RDmax_ind = 0;
            int RDmin_ind = 0;
            for (k = 1; k < nStatesDelayedDecision; ++k) {
                k2 = k * 2;
                if (sampleStates[k2].RD_Q10 > RDmax_Q10) {
                    RDmax_Q10 = sampleStates[k2].RD_Q10;
                    RDmax_ind = k;
                }
                if (sampleStates[k2 + 1].RD_Q10 >= RDmin_Q10) continue;
                RDmin_Q10 = sampleStates[k2 + 1].RD_Q10;
                RDmin_ind = k;
            }
            if (RDmin_Q10 < RDmax_Q10) {
                psDelDec[RDmax_ind].PartialCopyFrom(psDelDec[RDmin_ind], i);
                sampleStates[RDmax_ind * 2].Assign(sampleStates[RDmin_ind * 2 + 1]);
            }
            psDD = psDelDec[Winner_ind];
            if (subfr > 0 || i >= decisionDelay) {
                pulses[pulses_ptr + i - decisionDelay] = (byte)Inlines.silk_RSHIFT_ROUND(psDD.Q_Q10[last_smple_idx], 10);
                xq[xq_ptr + i - decisionDelay] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(Inlines.silk_SMULWW(psDD.Xq_Q14[last_smple_idx], delayedGain_Q10[last_smple_idx]), 8));
                this.sLTP_shp_Q14[this.sLTP_shp_buf_idx - decisionDelay] = psDD.Shape_Q14[last_smple_idx];
                sLTP_Q15[this.sLTP_buf_idx - decisionDelay] = psDD.Pred_Q15[last_smple_idx];
            }
            ++this.sLTP_shp_buf_idx;
            ++this.sLTP_buf_idx;
            for (k = 0; k < nStatesDelayedDecision; ++k) {
                psDD = psDelDec[k];
                SS_left = k * 2;
                psDD.LF_AR_Q14 = sampleStates[SS_left].LF_AR_Q14;
                psDD.sLPC_Q14[SilkConstants.NSQ_LPC_BUF_LENGTH + i] = sampleStates[SS_left].xq_Q14;
                psDD.Xq_Q14[smpl_buf_idx.Val] = sampleStates[SS_left].xq_Q14;
                psDD.Q_Q10[smpl_buf_idx.Val] = sampleStates[SS_left].Q_Q10;
                psDD.Pred_Q15[smpl_buf_idx.Val] = Inlines.silk_LSHIFT32(sampleStates[SS_left].LPC_exc_Q14, 1);
                psDD.Shape_Q14[smpl_buf_idx.Val] = sampleStates[SS_left].sLTP_shp_Q14;
                psDD.RandState[smpl_buf_idx.Val] = psDD.Seed = Inlines.silk_ADD32_ovflw(psDD.Seed, Inlines.silk_RSHIFT_ROUND(sampleStates[SS_left].Q_Q10, 10));
                psDD.RD_Q10 = sampleStates[SS_left].RD_Q10;
            }
            delayedGain_Q10[smpl_buf_idx.Val] = Gain_Q10;
        }
        for (k = 0; k < nStatesDelayedDecision; ++k) {
            psDD = psDelDec[k];
            System.arraycopy(psDD.sLPC_Q14, length, psDD.sLPC_Q14, 0, SilkConstants.NSQ_LPC_BUF_LENGTH);
        }
    }

    private void silk_nsq_del_dec_scale_states(SilkChannelEncoder psEncC, NSQ_del_dec_struct[] psDelDec, int[] x_Q3, int x_Q3_ptr, int[] x_sc_Q10, short[] sLTP, int[] sLTP_Q15, int subfr, int nStatesDelayedDecision, int LTP_scale_Q14, int[] Gains_Q16, int[] pitchL, int signal_type, int decisionDelay) {
        int i;
        int lag = pitchL[subfr];
        int inv_gain_Q31 = Inlines.silk_INVERSE32_varQ(Inlines.silk_max(Gains_Q16[subfr], 1), 47);
        Inlines.OpusAssert(inv_gain_Q31 != 0);
        int gain_adj_Q16 = Gains_Q16[subfr] != this.prev_gain_Q16 ? Inlines.silk_DIV32_varQ(this.prev_gain_Q16, Gains_Q16[subfr], 16) : 65536;
        int inv_gain_Q23 = Inlines.silk_RSHIFT_ROUND(inv_gain_Q31, 8);
        for (i = 0; i < psEncC.subfr_length; ++i) {
            x_sc_Q10[i] = Inlines.silk_SMULWW(x_Q3[x_Q3_ptr + i], inv_gain_Q23);
        }
        this.prev_gain_Q16 = Gains_Q16[subfr];
        if (this.rewhite_flag != 0) {
            if (subfr == 0) {
                inv_gain_Q31 = Inlines.silk_LSHIFT(Inlines.silk_SMULWB(inv_gain_Q31, LTP_scale_Q14), 2);
            }
            for (i = this.sLTP_buf_idx - lag - 2; i < this.sLTP_buf_idx; ++i) {
                Inlines.OpusAssert(i < 320);
                sLTP_Q15[i] = Inlines.silk_SMULWB(inv_gain_Q31, sLTP[i]);
            }
        }
        if (gain_adj_Q16 != 65536) {
            for (i = this.sLTP_shp_buf_idx - psEncC.ltp_mem_length; i < this.sLTP_shp_buf_idx; ++i) {
                this.sLTP_shp_Q14[i] = Inlines.silk_SMULWW(gain_adj_Q16, this.sLTP_shp_Q14[i]);
            }
            if (signal_type == 2 && this.rewhite_flag == 0) {
                for (i = this.sLTP_buf_idx - lag - 2; i < this.sLTP_buf_idx - decisionDelay; ++i) {
                    sLTP_Q15[i] = Inlines.silk_SMULWW(gain_adj_Q16, sLTP_Q15[i]);
                }
            }
            for (int k = 0; k < nStatesDelayedDecision; ++k) {
                NSQ_del_dec_struct psDD = psDelDec[k];
                psDD.LF_AR_Q14 = Inlines.silk_SMULWW(gain_adj_Q16, psDD.LF_AR_Q14);
                for (i = 0; i < SilkConstants.NSQ_LPC_BUF_LENGTH; ++i) {
                    psDD.sLPC_Q14[i] = Inlines.silk_SMULWW(gain_adj_Q16, psDD.sLPC_Q14[i]);
                }
                for (i = 0; i < psEncC.shapingLPCOrder; ++i) {
                    psDD.sAR2_Q14[i] = Inlines.silk_SMULWW(gain_adj_Q16, psDD.sAR2_Q14[i]);
                }
                for (i = 0; i < 32; ++i) {
                    psDD.Pred_Q15[i] = Inlines.silk_SMULWW(gain_adj_Q16, psDD.Pred_Q15[i]);
                    psDD.Shape_Q14[i] = Inlines.silk_SMULWW(gain_adj_Q16, psDD.Shape_Q14[i]);
                }
            }
        }
    }

    private class NSQ_del_dec_struct {
        final int[] sLPC_Q14 = new int[80 + SilkConstants.NSQ_LPC_BUF_LENGTH];
        final int[] RandState = new int[32];
        final int[] Q_Q10 = new int[32];
        final int[] Xq_Q14 = new int[32];
        final int[] Pred_Q15 = new int[32];
        final int[] Shape_Q14 = new int[32];
        int[] sAR2_Q14;
        int LF_AR_Q14 = 0;
        int Seed = 0;
        int SeedInit = 0;
        int RD_Q10 = 0;

        NSQ_del_dec_struct(int shapingOrder) {
            this.sAR2_Q14 = new int[shapingOrder];
        }

        void PartialCopyFrom(NSQ_del_dec_struct other, int q14Offset) {
            System.arraycopy(other.sLPC_Q14, q14Offset, this.sLPC_Q14, q14Offset, 80 + SilkConstants.NSQ_LPC_BUF_LENGTH - q14Offset);
            System.arraycopy(other.RandState, 0, this.RandState, 0, 32);
            System.arraycopy(other.Q_Q10, 0, this.Q_Q10, 0, 32);
            System.arraycopy(other.Xq_Q14, 0, this.Xq_Q14, 0, 32);
            System.arraycopy(other.Pred_Q15, 0, this.Pred_Q15, 0, 32);
            System.arraycopy(other.Shape_Q14, 0, this.Shape_Q14, 0, 32);
            System.arraycopy(other.sAR2_Q14, 0, this.sAR2_Q14, 0, this.sAR2_Q14.length);
            this.LF_AR_Q14 = other.LF_AR_Q14;
            this.Seed = other.Seed;
            this.SeedInit = other.SeedInit;
            this.RD_Q10 = other.RD_Q10;
        }

        void Assign(NSQ_del_dec_struct other) {
            this.PartialCopyFrom(other, 0);
        }
    }

    private class NSQ_sample_struct {
        int Q_Q10;
        int RD_Q10;
        int xq_Q14;
        int LF_AR_Q14;
        int sLTP_shp_Q14;
        int LPC_exc_Q14;

        private NSQ_sample_struct() {
        }

        void Assign(NSQ_sample_struct other) {
            this.Q_Q10 = other.Q_Q10;
            this.RD_Q10 = other.RD_Q10;
            this.xq_Q14 = other.xq_Q14;
            this.LF_AR_Q14 = other.LF_AR_Q14;
            this.sLTP_shp_Q14 = other.sLTP_shp_Q14;
            this.LPC_exc_Q14 = other.LPC_exc_Q14;
        }
    }
}
