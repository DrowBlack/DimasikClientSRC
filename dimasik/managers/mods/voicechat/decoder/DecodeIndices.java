package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.EntropyCoder;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.NLSF;
import dimasik.managers.mods.voicechat.decoder.SilkChannelDecoder;
import dimasik.managers.mods.voicechat.decoder.SilkTables;

class DecodeIndices {
    DecodeIndices() {
    }

    static void silk_decode_indices(SilkChannelDecoder psDec, EntropyCoder psRangeDec, int FrameIndex, int decode_LBRR, int condCoding) {
        int i;
        short[] ec_ix = new short[psDec.LPC_order];
        short[] pred_Q8 = new short[psDec.LPC_order];
        int Ix = decode_LBRR != 0 || psDec.VAD_flags[FrameIndex] != 0 ? psRangeDec.dec_icdf(SilkTables.silk_type_offset_VAD_iCDF, 8) + 2 : psRangeDec.dec_icdf(SilkTables.silk_type_offset_no_VAD_iCDF, 8);
        psDec.indices.signalType = (byte)Inlines.silk_RSHIFT(Ix, 1);
        psDec.indices.quantOffsetType = (byte)(Ix & 1);
        if (condCoding == 2) {
            psDec.indices.GainsIndices[0] = (byte)psRangeDec.dec_icdf(SilkTables.silk_delta_gain_iCDF, 8);
        } else {
            psDec.indices.GainsIndices[0] = (byte)Inlines.silk_LSHIFT(psRangeDec.dec_icdf(SilkTables.silk_gain_iCDF[psDec.indices.signalType], 8), 3);
            psDec.indices.GainsIndices[0] = (byte)(psDec.indices.GainsIndices[0] + (byte)psRangeDec.dec_icdf(SilkTables.silk_uniform8_iCDF, 8));
        }
        for (i = 1; i < psDec.nb_subfr; ++i) {
            psDec.indices.GainsIndices[i] = (byte)psRangeDec.dec_icdf(SilkTables.silk_delta_gain_iCDF, 8);
        }
        psDec.indices.NLSFIndices[0] = (byte)psRangeDec.dec_icdf(psDec.psNLSF_CB.CB1_iCDF, (psDec.indices.signalType >> 1) * psDec.psNLSF_CB.nVectors, 8);
        NLSF.silk_NLSF_unpack(ec_ix, pred_Q8, psDec.psNLSF_CB, psDec.indices.NLSFIndices[0]);
        Inlines.OpusAssert(psDec.psNLSF_CB.order == psDec.LPC_order);
        for (i = 0; i < psDec.psNLSF_CB.order; ++i) {
            Ix = psRangeDec.dec_icdf(psDec.psNLSF_CB.ec_iCDF, ec_ix[i], 8);
            if (Ix == 0) {
                Ix -= psRangeDec.dec_icdf(SilkTables.silk_NLSF_EXT_iCDF, 8);
            } else if (Ix == 8) {
                Ix += psRangeDec.dec_icdf(SilkTables.silk_NLSF_EXT_iCDF, 8);
            }
            psDec.indices.NLSFIndices[i + 1] = (byte)(Ix - 4);
        }
        psDec.indices.NLSFInterpCoef_Q2 = psDec.nb_subfr == 4 ? (byte)psRangeDec.dec_icdf(SilkTables.silk_NLSF_interpolation_factor_iCDF, 8) : (byte)4;
        if (psDec.indices.signalType == 2) {
            int delta_lagIndex;
            boolean decode_absolute_lagIndex = true;
            if (condCoding == 2 && psDec.ec_prevSignalType == 2 && (delta_lagIndex = (int)psRangeDec.dec_icdf(SilkTables.silk_pitch_delta_iCDF, 8)) > 0) {
                psDec.indices.lagIndex = (short)(psDec.ec_prevLagIndex + (delta_lagIndex -= 9));
                decode_absolute_lagIndex = false;
            }
            if (decode_absolute_lagIndex) {
                psDec.indices.lagIndex = (short)(psRangeDec.dec_icdf(SilkTables.silk_pitch_lag_iCDF, 8) * Inlines.silk_RSHIFT(psDec.fs_kHz, 1));
                psDec.indices.lagIndex = (short)(psDec.indices.lagIndex + (short)psRangeDec.dec_icdf(psDec.pitch_lag_low_bits_iCDF, 8));
            }
            psDec.ec_prevLagIndex = psDec.indices.lagIndex;
            psDec.indices.contourIndex = (byte)psRangeDec.dec_icdf(psDec.pitch_contour_iCDF, 8);
            psDec.indices.PERIndex = (byte)psRangeDec.dec_icdf(SilkTables.silk_LTP_per_index_iCDF, 8);
            for (int k = 0; k < psDec.nb_subfr; ++k) {
                psDec.indices.LTPIndex[k] = (byte)psRangeDec.dec_icdf(SilkTables.silk_LTP_gain_iCDF_ptrs[psDec.indices.PERIndex], 8);
            }
            psDec.indices.LTP_scaleIndex = condCoding == 0 ? (byte)psRangeDec.dec_icdf(SilkTables.silk_LTPscale_iCDF, 8) : (byte)0;
        }
        psDec.ec_prevSignalType = psDec.indices.signalType;
        psDec.indices.Seed = (byte)psRangeDec.dec_icdf(SilkTables.silk_uniform4_iCDF, 8);
    }
}
