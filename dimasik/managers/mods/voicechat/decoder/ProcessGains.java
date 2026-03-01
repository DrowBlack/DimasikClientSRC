package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.BoxedValueByte;
import dimasik.managers.mods.voicechat.decoder.GainQuantization;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.Sigmoid;
import dimasik.managers.mods.voicechat.decoder.SilkChannelEncoder;
import dimasik.managers.mods.voicechat.decoder.SilkEncoderControl;
import dimasik.managers.mods.voicechat.decoder.SilkShapeState;
import dimasik.managers.mods.voicechat.decoder.SilkTables;

class ProcessGains {
    ProcessGains() {
    }

    static void silk_process_gains(SilkChannelEncoder psEnc, SilkEncoderControl psEncCtrl, int condCoding) {
        int k;
        SilkShapeState psShapeSt = psEnc.sShape;
        if (psEnc.indices.signalType == 2) {
            int s_Q16 = 0 - Sigmoid.silk_sigm_Q15(Inlines.silk_RSHIFT_ROUND(psEncCtrl.LTPredCodGain_Q7 - 1536, 4));
            for (k = 0; k < psEnc.nb_subfr; ++k) {
                psEncCtrl.Gains_Q16[k] = Inlines.silk_SMLAWB(psEncCtrl.Gains_Q16[k], psEncCtrl.Gains_Q16[k], s_Q16);
            }
        }
        int InvMaxSqrVal_Q16 = Inlines.silk_DIV32_16(Inlines.silk_log2lin(Inlines.silk_SMULWB(8894 - psEnc.SNR_dB_Q7, 21627)), psEnc.subfr_length);
        for (k = 0; k < psEnc.nb_subfr; ++k) {
            int gain;
            int ResNrg = psEncCtrl.ResNrg[k];
            int ResNrgPart = Inlines.silk_SMULWW(ResNrg, InvMaxSqrVal_Q16);
            ResNrgPart = psEncCtrl.ResNrgQ[k] > 0 ? Inlines.silk_RSHIFT_ROUND(ResNrgPart, psEncCtrl.ResNrgQ[k]) : (ResNrgPart >= Inlines.silk_RSHIFT(Integer.MAX_VALUE, -psEncCtrl.ResNrgQ[k]) ? Integer.MAX_VALUE : Inlines.silk_LSHIFT(ResNrgPart, -psEncCtrl.ResNrgQ[k]));
            int gain_squared = Inlines.silk_ADD_SAT32(ResNrgPart, Inlines.silk_SMMUL(gain = psEncCtrl.Gains_Q16[k], gain));
            if (gain_squared < Short.MAX_VALUE) {
                gain_squared = Inlines.silk_SMLAWW(Inlines.silk_LSHIFT(ResNrgPart, 16), gain, gain);
                Inlines.OpusAssert(gain_squared > 0);
                gain = Inlines.silk_SQRT_APPROX(gain_squared);
                gain = Inlines.silk_min(gain, 0x7FFFFF);
                psEncCtrl.Gains_Q16[k] = Inlines.silk_LSHIFT_SAT32(gain, 8);
                continue;
            }
            gain = Inlines.silk_SQRT_APPROX(gain_squared);
            gain = Inlines.silk_min(gain, Short.MAX_VALUE);
            psEncCtrl.Gains_Q16[k] = Inlines.silk_LSHIFT_SAT32(gain, 16);
        }
        System.arraycopy(psEncCtrl.Gains_Q16, 0, psEncCtrl.GainsUnq_Q16, 0, psEnc.nb_subfr);
        psEncCtrl.lastGainIndexPrev = psShapeSt.LastGainIndex;
        BoxedValueByte boxed_lastGainIndex = new BoxedValueByte(psShapeSt.LastGainIndex);
        GainQuantization.silk_gains_quant(psEnc.indices.GainsIndices, psEncCtrl.Gains_Q16, boxed_lastGainIndex, condCoding == 2 ? 1 : 0, psEnc.nb_subfr);
        psShapeSt.LastGainIndex = boxed_lastGainIndex.Val;
        if (psEnc.indices.signalType == 2) {
            psEnc.indices.quantOffsetType = psEncCtrl.LTPredCodGain_Q7 + Inlines.silk_RSHIFT(psEnc.input_tilt_Q15, 8) > 128 ? (byte)0 : 1;
        }
        short quant_offset_Q10 = SilkTables.silk_Quantization_Offsets_Q10[psEnc.indices.signalType >> 1][psEnc.indices.quantOffsetType];
        psEncCtrl.Lambda_Q10 = 1229 + Inlines.silk_SMULBB(-50, psEnc.nStatesDelayedDecision) + Inlines.silk_SMULWB(-52428, psEnc.speech_activity_Q8) + Inlines.silk_SMULWB(-409, psEncCtrl.input_quality_Q14) + Inlines.silk_SMULWB(-818, psEncCtrl.coding_quality_Q14) + Inlines.silk_SMULWB(52429, quant_offset_Q10);
        Inlines.OpusAssert(psEncCtrl.Lambda_Q10 > 0);
        Inlines.OpusAssert(psEncCtrl.Lambda_Q10 < 2048);
    }
}
