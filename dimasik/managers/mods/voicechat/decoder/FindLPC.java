package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.BurgModified;
import dimasik.managers.mods.voicechat.decoder.Filters;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.NLSF;
import dimasik.managers.mods.voicechat.decoder.SilkChannelEncoder;
import dimasik.managers.mods.voicechat.decoder.SumSqrShift;

class FindLPC {
    FindLPC() {
    }

    static void silk_find_LPC(SilkChannelEncoder psEncC, short[] NLSF_Q15, short[] x, int minInvGain_Q30) {
        int[] a_Q16 = new int[16];
        BoxedValueInt res_nrg0 = new BoxedValueInt(0);
        BoxedValueInt res_nrg1 = new BoxedValueInt(0);
        BoxedValueInt rshift0 = new BoxedValueInt(0);
        BoxedValueInt rshift1 = new BoxedValueInt(0);
        BoxedValueInt scratch_box1 = new BoxedValueInt(0);
        BoxedValueInt scratch_box2 = new BoxedValueInt(0);
        int[] a_tmp_Q16 = new int[16];
        short[] a_tmp_Q12 = new short[16];
        short[] NLSF0_Q15 = new short[16];
        int subfr_length = psEncC.subfr_length + psEncC.predictLPCOrder;
        psEncC.indices.NLSFInterpCoef_Q2 = (byte)4;
        BurgModified.silk_burg_modified(scratch_box1, scratch_box2, a_Q16, x, 0, minInvGain_Q30, subfr_length, psEncC.nb_subfr, psEncC.predictLPCOrder);
        int res_nrg = scratch_box1.Val;
        int res_nrg_Q = scratch_box2.Val;
        if (psEncC.useInterpolatedNLSFs != 0 && psEncC.first_frame_after_reset == 0 && psEncC.nb_subfr == 4) {
            BurgModified.silk_burg_modified(scratch_box1, scratch_box2, a_tmp_Q16, x, 2 * subfr_length, minInvGain_Q30, subfr_length, 2, psEncC.predictLPCOrder);
            int res_tmp_nrg = scratch_box1.Val;
            int res_tmp_nrg_Q = scratch_box2.Val;
            int shift = res_tmp_nrg_Q - res_nrg_Q;
            if (shift >= 0) {
                if (shift < 32) {
                    res_nrg -= Inlines.silk_RSHIFT(res_tmp_nrg, shift);
                }
            } else {
                Inlines.OpusAssert(shift > -32);
                res_nrg = Inlines.silk_RSHIFT(res_nrg, -shift) - res_tmp_nrg;
                res_nrg_Q = res_tmp_nrg_Q;
            }
            NLSF.silk_A2NLSF(NLSF_Q15, a_tmp_Q16, psEncC.predictLPCOrder);
            short[] LPC_res = new short[2 * subfr_length];
            for (int k = 3; k >= 0; --k) {
                int res_nrg_interp_Q;
                Inlines.silk_interpolate(NLSF0_Q15, psEncC.prev_NLSFq_Q15, NLSF_Q15, k, psEncC.predictLPCOrder);
                NLSF.silk_NLSF2A(a_tmp_Q12, NLSF0_Q15, psEncC.predictLPCOrder);
                Filters.silk_LPC_analysis_filter(LPC_res, 0, x, 0, a_tmp_Q12, 0, 2 * subfr_length, psEncC.predictLPCOrder);
                SumSqrShift.silk_sum_sqr_shift(res_nrg0, rshift0, LPC_res, psEncC.predictLPCOrder, subfr_length - psEncC.predictLPCOrder);
                SumSqrShift.silk_sum_sqr_shift(res_nrg1, rshift1, LPC_res, psEncC.predictLPCOrder + subfr_length, subfr_length - psEncC.predictLPCOrder);
                shift = rshift0.Val - rshift1.Val;
                if (shift >= 0) {
                    res_nrg1.Val = Inlines.silk_RSHIFT(res_nrg1.Val, shift);
                    res_nrg_interp_Q = 0 - rshift0.Val;
                } else {
                    res_nrg0.Val = Inlines.silk_RSHIFT(res_nrg0.Val, 0 - shift);
                    res_nrg_interp_Q = 0 - rshift1.Val;
                }
                int res_nrg_interp = Inlines.silk_ADD32(res_nrg0.Val, res_nrg1.Val);
                shift = res_nrg_interp_Q - res_nrg_Q;
                boolean isInterpLower = shift >= 0 ? Inlines.silk_RSHIFT(res_nrg_interp, shift) < res_nrg : (-shift < 32 ? res_nrg_interp < Inlines.silk_RSHIFT(res_nrg, -shift) : false);
                if (!isInterpLower) continue;
                res_nrg = res_nrg_interp;
                res_nrg_Q = res_nrg_interp_Q;
                psEncC.indices.NLSFInterpCoef_Q2 = (byte)k;
            }
        }
        if (psEncC.indices.NLSFInterpCoef_Q2 == 4) {
            NLSF.silk_A2NLSF(NLSF_Q15, a_Q16, psEncC.predictLPCOrder);
        }
        Inlines.OpusAssert(psEncC.indices.NLSFInterpCoef_Q2 == 4 || psEncC.useInterpolatedNLSFs != 0 && psEncC.first_frame_after_reset == 0 && psEncC.nb_subfr == 4);
    }
}
