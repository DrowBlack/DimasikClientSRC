package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.EntropyCoder;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.SilkTables;

class CodeSigns {
    CodeSigns() {
    }

    private static int silk_enc_map(int a) {
        return Inlines.silk_RSHIFT(a, 15) + 1;
    }

    private static int silk_dec_map(int a) {
        return Inlines.silk_LSHIFT(a, 1) - 1;
    }

    static void silk_encode_signs(EntropyCoder psRangeEnc, byte[] pulses, int length, int signalType, int quantOffsetType, int[] sum_pulses) {
        int i;
        short[] icdf = new short[2];
        short[] sign_icdf = SilkTables.silk_sign_iCDF;
        icdf[1] = 0;
        int q_ptr = 0;
        int icdf_ptr = i = Inlines.silk_SMULBB(7, Inlines.silk_ADD_LSHIFT(quantOffsetType, signalType, 1));
        length = Inlines.silk_RSHIFT(length + 8, 4);
        for (i = 0; i < length; ++i) {
            int p = sum_pulses[i];
            if (p > 0) {
                icdf[0] = sign_icdf[icdf_ptr + Inlines.silk_min(p & 0x1F, 6)];
                for (int j = q_ptr; j < q_ptr + 16; ++j) {
                    if (pulses[j] == 0) continue;
                    psRangeEnc.enc_icdf(CodeSigns.silk_enc_map(pulses[j]), icdf, 8);
                }
            }
            q_ptr += 16;
        }
    }

    static void silk_decode_signs(EntropyCoder psRangeDec, short[] pulses, int length, int signalType, int quantOffsetType, int[] sum_pulses) {
        int i;
        short[] icdf = new short[2];
        short[] icdf_table = SilkTables.silk_sign_iCDF;
        icdf[1] = 0;
        int q_ptr = 0;
        int icdf_ptr = i = Inlines.silk_SMULBB(7, Inlines.silk_ADD_LSHIFT(quantOffsetType, signalType, 1));
        length = Inlines.silk_RSHIFT(length + 8, 4);
        for (i = 0; i < length; ++i) {
            int p = sum_pulses[i];
            if (p > 0) {
                icdf[0] = icdf_table[icdf_ptr + Inlines.silk_min(p & 0x1F, 6)];
                for (int j = 0; j < 16; ++j) {
                    if (pulses[q_ptr + j] <= 0) continue;
                    int n = q_ptr + j;
                    pulses[n] = (short)(pulses[n] * (short)CodeSigns.silk_dec_map(psRangeDec.dec_icdf(icdf, 8)));
                }
            }
            q_ptr += 16;
        }
    }
}
