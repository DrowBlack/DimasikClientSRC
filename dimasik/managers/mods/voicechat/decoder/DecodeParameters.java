package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;
import dimasik.managers.mods.voicechat.decoder.BWExpander;
import dimasik.managers.mods.voicechat.decoder.BoxedValueByte;
import dimasik.managers.mods.voicechat.decoder.DecodePitch;
import dimasik.managers.mods.voicechat.decoder.GainQuantization;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.NLSF;
import dimasik.managers.mods.voicechat.decoder.SilkChannelDecoder;
import dimasik.managers.mods.voicechat.decoder.SilkDecoderControl;
import dimasik.managers.mods.voicechat.decoder.SilkTables;

class DecodeParameters {
    DecodeParameters() {
    }

    static void silk_decode_parameters(SilkChannelDecoder psDec, SilkDecoderControl psDecCtrl, int condCoding) {
        int i;
        short[] pNLSF_Q15 = new short[psDec.LPC_order];
        short[] pNLSF0_Q15 = new short[psDec.LPC_order];
        BoxedValueByte boxedLastGainIndex = new BoxedValueByte(psDec.LastGainIndex);
        GainQuantization.silk_gains_dequant(psDecCtrl.Gains_Q16, psDec.indices.GainsIndices, boxedLastGainIndex, condCoding == 2 ? 1 : 0, psDec.nb_subfr);
        psDec.LastGainIndex = boxedLastGainIndex.Val;
        NLSF.silk_NLSF_decode(pNLSF_Q15, psDec.indices.NLSFIndices, psDec.psNLSF_CB);
        NLSF.silk_NLSF2A(psDecCtrl.PredCoef_Q12[1], pNLSF_Q15, psDec.LPC_order);
        if (psDec.first_frame_after_reset == 1) {
            psDec.indices.NLSFInterpCoef_Q2 = (byte)4;
        }
        if (psDec.indices.NLSFInterpCoef_Q2 < 4) {
            for (i = 0; i < psDec.LPC_order; ++i) {
                pNLSF0_Q15[i] = (short)(psDec.prevNLSF_Q15[i] + Inlines.silk_RSHIFT(Inlines.silk_MUL(psDec.indices.NLSFInterpCoef_Q2, pNLSF_Q15[i] - psDec.prevNLSF_Q15[i]), 2));
            }
            NLSF.silk_NLSF2A(psDecCtrl.PredCoef_Q12[0], pNLSF0_Q15, psDec.LPC_order);
        } else {
            System.arraycopy(psDecCtrl.PredCoef_Q12[1], 0, psDecCtrl.PredCoef_Q12[0], 0, psDec.LPC_order);
        }
        System.arraycopy(pNLSF_Q15, 0, psDec.prevNLSF_Q15, 0, psDec.LPC_order);
        if (psDec.lossCnt != 0) {
            BWExpander.silk_bwexpander(psDecCtrl.PredCoef_Q12[0], psDec.LPC_order, 63570);
            BWExpander.silk_bwexpander(psDecCtrl.PredCoef_Q12[1], psDec.LPC_order, 63570);
        }
        if (psDec.indices.signalType == 2) {
            byte Ix;
            DecodePitch.silk_decode_pitch(psDec.indices.lagIndex, psDec.indices.contourIndex, psDecCtrl.pitchL, psDec.fs_kHz, psDec.nb_subfr);
            byte[][] cbk_ptr_Q7 = SilkTables.silk_LTP_vq_ptrs_Q7[psDec.indices.PERIndex];
            for (int k = 0; k < psDec.nb_subfr; ++k) {
                Ix = psDec.indices.LTPIndex[k];
                for (i = 0; i < 5; ++i) {
                    psDecCtrl.LTPCoef_Q14[k * 5 + i] = (short)Inlines.silk_LSHIFT(cbk_ptr_Q7[Ix][i], 7);
                }
            }
            Ix = psDec.indices.LTP_scaleIndex;
            psDecCtrl.LTP_scale_Q14 = SilkTables.silk_LTPScales_table_Q14[Ix];
        } else {
            Arrays.MemSet(psDecCtrl.pitchL, 0, psDec.nb_subfr);
            Arrays.MemSet(psDecCtrl.LTPCoef_Q14, (short)0, 5 * psDec.nb_subfr);
            psDec.indices.PERIndex = 0;
            psDecCtrl.LTP_scale_Q14 = 0;
        }
    }
}
