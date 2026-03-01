package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;
import dimasik.managers.mods.voicechat.decoder.CodeSigns;
import dimasik.managers.mods.voicechat.decoder.EntropyCoder;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.ShellCoder;
import dimasik.managers.mods.voicechat.decoder.SilkTables;

class DecodePulses {
    DecodePulses() {
    }

    static void silk_decode_pulses(EntropyCoder psRangeDec, short[] pulses, int signalType, int quantOffsetType, int frame_length) {
        int i;
        int[] sum_pulses = new int[20];
        int[] nLshifts = new int[20];
        int RateLevelIndex = psRangeDec.dec_icdf(SilkTables.silk_rate_levels_iCDF[signalType >> 1], 8);
        Inlines.OpusAssert(true);
        int iter = Inlines.silk_RSHIFT(frame_length, 4);
        if (iter * 16 < frame_length) {
            Inlines.OpusAssert(frame_length == 120);
            ++iter;
        }
        for (i = 0; i < iter; ++i) {
            nLshifts[i] = 0;
            sum_pulses[i] = psRangeDec.dec_icdf(SilkTables.silk_pulses_per_block_iCDF[RateLevelIndex], 8);
            while (sum_pulses[i] == 17) {
                int n = i;
                nLshifts[n] = nLshifts[n] + 1;
                sum_pulses[i] = psRangeDec.dec_icdf(SilkTables.silk_pulses_per_block_iCDF[9], nLshifts[i] == 10 ? 1 : 0, 8);
            }
        }
        for (i = 0; i < iter; ++i) {
            if (sum_pulses[i] > 0) {
                ShellCoder.silk_shell_decoder(pulses, Inlines.silk_SMULBB(i, 16), psRangeDec, sum_pulses[i]);
                continue;
            }
            Arrays.MemSetWithOffset(pulses, (short)0, Inlines.silk_SMULBB(i, 16), 16);
        }
        for (i = 0; i < iter; ++i) {
            if (nLshifts[i] <= 0) continue;
            int nLS = nLshifts[i];
            int pulses_ptr = Inlines.silk_SMULBB(i, 16);
            for (int k = 0; k < 16; ++k) {
                int abs_q = pulses[pulses_ptr + k];
                for (int j = 0; j < nLS; ++j) {
                    abs_q = Inlines.silk_LSHIFT(abs_q, 1);
                    abs_q += psRangeDec.dec_icdf(SilkTables.silk_lsb_iCDF, 8);
                }
                pulses[pulses_ptr + k] = (short)abs_q;
            }
            int n = i;
            sum_pulses[n] = sum_pulses[n] | nLS << 5;
        }
        CodeSigns.silk_decode_signs(psRangeDec, pulses, frame_length, signalType, quantOffsetType, sum_pulses);
    }
}
