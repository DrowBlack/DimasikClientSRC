package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;
import dimasik.managers.mods.voicechat.decoder.BoxedValueByte;
import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.EntropyCoder;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.SilkTables;
import dimasik.managers.mods.voicechat.decoder.StereoDecodeState;
import dimasik.managers.mods.voicechat.decoder.StereoEncodeState;
import dimasik.managers.mods.voicechat.decoder.SumSqrShift;

class Stereo {
    Stereo() {
    }

    static void silk_stereo_decode_pred(EntropyCoder psRangeDec, int[] pred_Q13) {
        int[][] ix = Arrays.InitTwoDimensionalArrayInt(2, 3);
        int n = psRangeDec.dec_icdf(SilkTables.silk_stereo_pred_joint_iCDF, 8);
        ix[0][2] = Inlines.silk_DIV32_16(n, 5);
        ix[1][2] = n - 5 * ix[0][2];
        for (n = 0; n < 2; ++n) {
            ix[n][0] = psRangeDec.dec_icdf(SilkTables.silk_uniform3_iCDF, 8);
            ix[n][1] = psRangeDec.dec_icdf(SilkTables.silk_uniform5_iCDF, 8);
        }
        for (n = 0; n < 2; ++n) {
            int[] nArray = ix[n];
            nArray[0] = nArray[0] + 3 * ix[n][2];
            short low_Q13 = SilkTables.silk_stereo_pred_quant_Q13[ix[n][0]];
            int step_Q13 = Inlines.silk_SMULWB(SilkTables.silk_stereo_pred_quant_Q13[ix[n][0] + 1] - low_Q13, 6554);
            pred_Q13[n] = Inlines.silk_SMLABB(low_Q13, step_Q13, 2 * ix[n][1] + 1);
        }
        pred_Q13[0] = pred_Q13[0] - pred_Q13[1];
    }

    static void silk_stereo_decode_mid_only(EntropyCoder psRangeDec, BoxedValueInt decode_only_mid) {
        decode_only_mid.Val = psRangeDec.dec_icdf(SilkTables.silk_stereo_only_code_mid_iCDF, 8);
    }

    static void silk_stereo_encode_pred(EntropyCoder psRangeEnc, byte[][] ix) {
        int n = 5 * ix[0][2] + ix[1][2];
        Inlines.OpusAssert(n < 25);
        psRangeEnc.enc_icdf(n, SilkTables.silk_stereo_pred_joint_iCDF, 8);
        for (n = 0; n < 2; ++n) {
            Inlines.OpusAssert(ix[n][0] < 3);
            Inlines.OpusAssert(ix[n][1] < 5);
            psRangeEnc.enc_icdf(ix[n][0], SilkTables.silk_uniform3_iCDF, 8);
            psRangeEnc.enc_icdf(ix[n][1], SilkTables.silk_uniform5_iCDF, 8);
        }
    }

    static void silk_stereo_encode_mid_only(EntropyCoder psRangeEnc, byte mid_only_flag) {
        psRangeEnc.enc_icdf(mid_only_flag, SilkTables.silk_stereo_only_code_mid_iCDF, 8);
    }

    static int silk_stereo_find_predictor(BoxedValueInt ratio_Q14, short[] x, short[] y, int[] mid_res_amp_Q0, int mid_res_amp_Q0_ptr, int length, int smooth_coef_Q16) {
        BoxedValueInt nrgx = new BoxedValueInt(0);
        BoxedValueInt nrgy = new BoxedValueInt(0);
        BoxedValueInt scale1 = new BoxedValueInt(0);
        BoxedValueInt scale2 = new BoxedValueInt(0);
        SumSqrShift.silk_sum_sqr_shift(nrgx, scale1, x, length);
        SumSqrShift.silk_sum_sqr_shift(nrgy, scale2, y, length);
        int scale = Inlines.silk_max_int(scale1.Val, scale2.Val);
        scale += scale & 1;
        nrgy.Val = Inlines.silk_RSHIFT32(nrgy.Val, scale - scale2.Val);
        nrgx.Val = Inlines.silk_RSHIFT32(nrgx.Val, scale - scale1.Val);
        nrgx.Val = Inlines.silk_max_int(nrgx.Val, 1);
        int corr = Inlines.silk_inner_prod_aligned_scale(x, y, scale, length);
        int pred_Q13 = Inlines.silk_DIV32_varQ(corr, nrgx.Val, 13);
        pred_Q13 = Inlines.silk_LIMIT(pred_Q13, -16384, 16384);
        int pred2_Q10 = Inlines.silk_SMULWB(pred_Q13, pred_Q13);
        smooth_coef_Q16 = Inlines.silk_max_int(smooth_coef_Q16, Inlines.silk_abs(pred2_Q10));
        Inlines.OpusAssert(smooth_coef_Q16 < 32768);
        scale = Inlines.silk_RSHIFT(scale, 1);
        mid_res_amp_Q0[mid_res_amp_Q0_ptr] = Inlines.silk_SMLAWB(mid_res_amp_Q0[mid_res_amp_Q0_ptr], Inlines.silk_LSHIFT(Inlines.silk_SQRT_APPROX(nrgx.Val), scale) - mid_res_amp_Q0[mid_res_amp_Q0_ptr], smooth_coef_Q16);
        nrgy.Val = Inlines.silk_SUB_LSHIFT32(nrgy.Val, Inlines.silk_SMULWB(corr, pred_Q13), 4);
        nrgy.Val = Inlines.silk_ADD_LSHIFT32(nrgy.Val, Inlines.silk_SMULWB(nrgx.Val, pred2_Q10), 6);
        mid_res_amp_Q0[mid_res_amp_Q0_ptr + 1] = Inlines.silk_SMLAWB(mid_res_amp_Q0[mid_res_amp_Q0_ptr + 1], Inlines.silk_LSHIFT(Inlines.silk_SQRT_APPROX(nrgy.Val), scale) - mid_res_amp_Q0[mid_res_amp_Q0_ptr + 1], smooth_coef_Q16);
        ratio_Q14.Val = Inlines.silk_DIV32_varQ(mid_res_amp_Q0[mid_res_amp_Q0_ptr + 1], Inlines.silk_max(mid_res_amp_Q0[mid_res_amp_Q0_ptr], 1), 14);
        ratio_Q14.Val = Inlines.silk_LIMIT(ratio_Q14.Val, 0, Short.MAX_VALUE);
        return pred_Q13;
    }

    static void silk_stereo_LR_to_MS(StereoEncodeState state, short[] x1, int x1_ptr, short[] x2, int x2_ptr, byte[][] ix, BoxedValueByte mid_only_flag, int[] mid_side_rates_bps, int total_rate_bps, int prev_speech_act_Q8, int toMono, int fs_kHz, int frame_length) {
        int width_Q14;
        int min_mid_rate_bps;
        int sum;
        int n;
        int[] pred_Q13 = new int[2];
        BoxedValueInt LP_ratio_Q14 = new BoxedValueInt(0);
        BoxedValueInt HP_ratio_Q14 = new BoxedValueInt(0);
        int mid = x1_ptr - 2;
        short[] side = new short[frame_length + 2];
        for (n = 0; n < frame_length + 2; ++n) {
            sum = x1[x1_ptr + n - 2] + x2[x2_ptr + n - 2];
            int diff = x1[x1_ptr + n - 2] - x2[x2_ptr + n - 2];
            x1[mid + n] = (short)Inlines.silk_RSHIFT_ROUND(sum, 1);
            side[n] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(diff, 1));
        }
        System.arraycopy(state.sMid, 0, x1, mid, 2);
        System.arraycopy(state.sSide, 0, side, 0, 2);
        System.arraycopy(x1, mid + frame_length, state.sMid, 0, 2);
        System.arraycopy(side, frame_length, state.sSide, 0, 2);
        short[] LP_mid = new short[frame_length];
        short[] HP_mid = new short[frame_length];
        for (n = 0; n < frame_length; ++n) {
            sum = Inlines.silk_RSHIFT_ROUND(Inlines.silk_ADD_LSHIFT32(x1[mid + n] + x1[mid + n + 2], x1[mid + n + 1], 1), 2);
            LP_mid[n] = (short)sum;
            HP_mid[n] = (short)(x1[mid + n + 1] - sum);
        }
        short[] LP_side = new short[frame_length];
        short[] HP_side = new short[frame_length];
        for (n = 0; n < frame_length; ++n) {
            sum = Inlines.silk_RSHIFT_ROUND(Inlines.silk_ADD_LSHIFT32(side[n] + side[n + 2], side[n + 1], 1), 2);
            LP_side[n] = (short)sum;
            HP_side[n] = (short)(side[n + 1] - sum);
        }
        boolean is10msFrame = frame_length == 10 * fs_kHz;
        int smooth_coef_Q16 = is10msFrame ? 328 : 655;
        smooth_coef_Q16 = Inlines.silk_SMULWB(Inlines.silk_SMULBB(prev_speech_act_Q8, prev_speech_act_Q8), smooth_coef_Q16);
        pred_Q13[0] = Stereo.silk_stereo_find_predictor(LP_ratio_Q14, LP_mid, LP_side, state.mid_side_amp_Q0, 0, frame_length, smooth_coef_Q16);
        pred_Q13[1] = Stereo.silk_stereo_find_predictor(HP_ratio_Q14, HP_mid, HP_side, state.mid_side_amp_Q0, 2, frame_length, smooth_coef_Q16);
        int frac_Q16 = Inlines.silk_SMLABB(HP_ratio_Q14.Val, LP_ratio_Q14.Val, 3);
        frac_Q16 = Inlines.silk_min(frac_Q16, 65536);
        if ((total_rate_bps -= is10msFrame ? 1200 : 600) < 1) {
            total_rate_bps = 1;
        }
        Inlines.OpusAssert((min_mid_rate_bps = Inlines.silk_SMLABB(2000, fs_kHz, 900)) < Short.MAX_VALUE);
        int frac_3_Q16 = Inlines.silk_MUL(3, frac_Q16);
        mid_side_rates_bps[0] = Inlines.silk_DIV32_varQ(total_rate_bps, 851968 + frac_3_Q16, 19);
        if (mid_side_rates_bps[0] < min_mid_rate_bps) {
            mid_side_rates_bps[0] = min_mid_rate_bps;
            mid_side_rates_bps[1] = total_rate_bps - mid_side_rates_bps[0];
            width_Q14 = Inlines.silk_DIV32_varQ(Inlines.silk_LSHIFT(mid_side_rates_bps[1], 1) - min_mid_rate_bps, Inlines.silk_SMULWB(65536 + frac_3_Q16, min_mid_rate_bps), 16);
            width_Q14 = Inlines.silk_LIMIT(width_Q14, 0, 16384);
        } else {
            mid_side_rates_bps[1] = total_rate_bps - mid_side_rates_bps[0];
            width_Q14 = 16384;
        }
        state.smth_width_Q14 = (short)Inlines.silk_SMLAWB(state.smth_width_Q14, width_Q14 - state.smth_width_Q14, smooth_coef_Q16);
        mid_only_flag.Val = 0;
        if (toMono != 0) {
            width_Q14 = 0;
            pred_Q13[0] = 0;
            pred_Q13[1] = 0;
            Stereo.silk_stereo_quant_pred(pred_Q13, ix);
        } else if (state.width_prev_Q14 == 0 && (8 * total_rate_bps < 13 * min_mid_rate_bps || Inlines.silk_SMULWB(frac_Q16, state.smth_width_Q14) < 819)) {
            pred_Q13[0] = Inlines.silk_RSHIFT(Inlines.silk_SMULBB(state.smth_width_Q14, pred_Q13[0]), 14);
            pred_Q13[1] = Inlines.silk_RSHIFT(Inlines.silk_SMULBB(state.smth_width_Q14, pred_Q13[1]), 14);
            Stereo.silk_stereo_quant_pred(pred_Q13, ix);
            width_Q14 = 0;
            pred_Q13[0] = 0;
            pred_Q13[1] = 0;
            mid_side_rates_bps[0] = total_rate_bps;
            mid_side_rates_bps[1] = 0;
            mid_only_flag.Val = 1;
        } else if (state.width_prev_Q14 != 0 && (8 * total_rate_bps < 11 * min_mid_rate_bps || Inlines.silk_SMULWB(frac_Q16, state.smth_width_Q14) < 328)) {
            pred_Q13[0] = Inlines.silk_RSHIFT(Inlines.silk_SMULBB(state.smth_width_Q14, pred_Q13[0]), 14);
            pred_Q13[1] = Inlines.silk_RSHIFT(Inlines.silk_SMULBB(state.smth_width_Q14, pred_Q13[1]), 14);
            Stereo.silk_stereo_quant_pred(pred_Q13, ix);
            width_Q14 = 0;
            pred_Q13[0] = 0;
            pred_Q13[1] = 0;
        } else if (state.smth_width_Q14 > 15565) {
            Stereo.silk_stereo_quant_pred(pred_Q13, ix);
            width_Q14 = 16384;
        } else {
            pred_Q13[0] = Inlines.silk_RSHIFT(Inlines.silk_SMULBB(state.smth_width_Q14, pred_Q13[0]), 14);
            pred_Q13[1] = Inlines.silk_RSHIFT(Inlines.silk_SMULBB(state.smth_width_Q14, pred_Q13[1]), 14);
            Stereo.silk_stereo_quant_pred(pred_Q13, ix);
            width_Q14 = state.smth_width_Q14;
        }
        if (mid_only_flag.Val == 1) {
            state.silent_side_len = (short)(state.silent_side_len + (short)(frame_length - 8 * fs_kHz));
            if (state.silent_side_len < 5 * fs_kHz) {
                mid_only_flag.Val = 0;
            } else {
                state.silent_side_len = (short)10000;
            }
        } else {
            state.silent_side_len = 0;
        }
        if (mid_only_flag.Val == 0 && mid_side_rates_bps[1] < 1) {
            mid_side_rates_bps[1] = 1;
            mid_side_rates_bps[0] = Inlines.silk_max_int(1, total_rate_bps - mid_side_rates_bps[1]);
        }
        int pred0_Q13 = -state.pred_prev_Q13[0];
        int pred1_Q13 = -state.pred_prev_Q13[1];
        int w_Q24 = Inlines.silk_LSHIFT(state.width_prev_Q14, 10);
        int denom_Q16 = Inlines.silk_DIV32_16(65536, 8 * fs_kHz);
        int delta0_Q13 = 0 - Inlines.silk_RSHIFT_ROUND(Inlines.silk_SMULBB(pred_Q13[0] - state.pred_prev_Q13[0], denom_Q16), 16);
        int delta1_Q13 = 0 - Inlines.silk_RSHIFT_ROUND(Inlines.silk_SMULBB(pred_Q13[1] - state.pred_prev_Q13[1], denom_Q16), 16);
        int deltaw_Q24 = Inlines.silk_LSHIFT(Inlines.silk_SMULWB(width_Q14 - state.width_prev_Q14, denom_Q16), 10);
        for (n = 0; n < 8 * fs_kHz; ++n) {
            sum = Inlines.silk_LSHIFT(Inlines.silk_ADD_LSHIFT(x1[mid + n] + x1[mid + n + 2], x1[mid + n + 1], 1), 9);
            sum = Inlines.silk_SMLAWB(Inlines.silk_SMULWB(w_Q24 += deltaw_Q24, side[n + 1]), sum, pred0_Q13 += delta0_Q13);
            sum = Inlines.silk_SMLAWB(sum, Inlines.silk_LSHIFT(x1[mid + n + 1], 11), pred1_Q13 += delta1_Q13);
            x2[x2_ptr + n - 1] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(sum, 8));
        }
        pred0_Q13 = 0 - pred_Q13[0];
        pred1_Q13 = 0 - pred_Q13[1];
        w_Q24 = Inlines.silk_LSHIFT(width_Q14, 10);
        for (n = 8 * fs_kHz; n < frame_length; ++n) {
            sum = Inlines.silk_LSHIFT(Inlines.silk_ADD_LSHIFT(x1[mid + n] + x1[mid + n + 2], x1[mid + n + 1], 1), 9);
            sum = Inlines.silk_SMLAWB(Inlines.silk_SMULWB(w_Q24, side[n + 1]), sum, pred0_Q13);
            sum = Inlines.silk_SMLAWB(sum, Inlines.silk_LSHIFT(x1[mid + n + 1], 11), pred1_Q13);
            x2[x2_ptr + n - 1] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(sum, 8));
        }
        state.pred_prev_Q13[0] = (short)pred_Q13[0];
        state.pred_prev_Q13[1] = (short)pred_Q13[1];
        state.width_prev_Q14 = (short)width_Q14;
    }

    static void silk_stereo_MS_to_LR(StereoDecodeState state, short[] x1, int x1_ptr, short[] x2, int x2_ptr, int[] pred_Q13, int fs_kHz, int frame_length) {
        int sum;
        int n;
        System.arraycopy(state.sMid, 0, x1, x1_ptr, 2);
        System.arraycopy(state.sSide, 0, x2, x2_ptr, 2);
        System.arraycopy(x1, x1_ptr + frame_length, state.sMid, 0, 2);
        System.arraycopy(x2, x2_ptr + frame_length, state.sSide, 0, 2);
        int pred0_Q13 = state.pred_prev_Q13[0];
        int pred1_Q13 = state.pred_prev_Q13[1];
        int denom_Q16 = Inlines.silk_DIV32_16(65536, 8 * fs_kHz);
        int delta0_Q13 = Inlines.silk_RSHIFT_ROUND(Inlines.silk_SMULBB(pred_Q13[0] - state.pred_prev_Q13[0], denom_Q16), 16);
        int delta1_Q13 = Inlines.silk_RSHIFT_ROUND(Inlines.silk_SMULBB(pred_Q13[1] - state.pred_prev_Q13[1], denom_Q16), 16);
        for (n = 0; n < 8 * fs_kHz; ++n) {
            sum = Inlines.silk_LSHIFT(Inlines.silk_ADD_LSHIFT(x1[x1_ptr + n] + x1[x1_ptr + n + 2], x1[x1_ptr + n + 1], 1), 9);
            sum = Inlines.silk_SMLAWB(Inlines.silk_LSHIFT(x2[x2_ptr + n + 1], 8), sum, pred0_Q13 += delta0_Q13);
            sum = Inlines.silk_SMLAWB(sum, Inlines.silk_LSHIFT(x1[x1_ptr + n + 1], 11), pred1_Q13 += delta1_Q13);
            x2[x2_ptr + n + 1] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(sum, 8));
        }
        pred0_Q13 = pred_Q13[0];
        pred1_Q13 = pred_Q13[1];
        for (n = 8 * fs_kHz; n < frame_length; ++n) {
            sum = Inlines.silk_LSHIFT(Inlines.silk_ADD_LSHIFT(x1[x1_ptr + n] + x1[x1_ptr + n + 2], x1[x1_ptr + n + 1], 1), 9);
            sum = Inlines.silk_SMLAWB(Inlines.silk_LSHIFT(x2[x2_ptr + n + 1], 8), sum, pred0_Q13);
            sum = Inlines.silk_SMLAWB(sum, Inlines.silk_LSHIFT(x1[x1_ptr + n + 1], 11), pred1_Q13);
            x2[x2_ptr + n + 1] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(sum, 8));
        }
        state.pred_prev_Q13[0] = (short)pred_Q13[0];
        state.pred_prev_Q13[1] = (short)pred_Q13[1];
        for (n = 0; n < frame_length; ++n) {
            sum = x1[x1_ptr + n + 1] + x2[x2_ptr + n + 1];
            int diff = x1[x1_ptr + n + 1] - x2[x2_ptr + n + 1];
            x1[x1_ptr + n + 1] = (short)Inlines.silk_SAT16(sum);
            x2[x2_ptr + n + 1] = (short)Inlines.silk_SAT16(diff);
        }
    }

    static void silk_stereo_quant_pred(int[] pred_Q13, byte[][] ix) {
        int quant_pred_Q13 = 0;
        Arrays.MemSet(ix[0], (byte)0, 3);
        Arrays.MemSet(ix[1], (byte)0, 3);
        for (int n = 0; n < 2; ++n) {
            boolean done = false;
            int err_min_Q13 = Integer.MAX_VALUE;
            for (int i = 0; !done && i < 15; i = (int)((byte)(i + 1))) {
                short low_Q13 = SilkTables.silk_stereo_pred_quant_Q13[i];
                int step_Q13 = Inlines.silk_SMULWB(SilkTables.silk_stereo_pred_quant_Q13[i + 1] - low_Q13, 6554);
                for (int j = 0; !done && j < 5; j = (int)((byte)(j + 1))) {
                    int lvl_Q13 = Inlines.silk_SMLABB(low_Q13, step_Q13, 2 * j + 1);
                    int err_Q13 = Inlines.silk_abs(pred_Q13[n] - lvl_Q13);
                    if (err_Q13 < err_min_Q13) {
                        err_min_Q13 = err_Q13;
                        quant_pred_Q13 = lvl_Q13;
                        ix[n][0] = i;
                        ix[n][1] = j;
                        continue;
                    }
                    done = true;
                }
            }
            ix[n][2] = (byte)Inlines.silk_DIV32_16(ix[n][0], 3);
            ix[n][0] = (byte)(ix[n][0] - (byte)(ix[n][2] * 3));
            pred_Q13[n] = quant_pred_Q13;
        }
        pred_Q13[0] = pred_Q13[0] - pred_Q13[1];
    }
}
