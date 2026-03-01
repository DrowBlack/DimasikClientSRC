package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;
import dimasik.managers.mods.voicechat.decoder.CodeSigns;
import dimasik.managers.mods.voicechat.decoder.EntropyCoder;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.ShellCoder;
import dimasik.managers.mods.voicechat.decoder.SilkTables;

class EncodePulses {
    EncodePulses() {
    }

    static int combine_and_check(int[] pulses_comb, int pulses_comb_ptr, int[] pulses_in, int pulses_in_ptr, int max_pulses, int len) {
        for (int k = 0; k < len; ++k) {
            int k2p = 2 * k + pulses_in_ptr;
            int sum = pulses_in[k2p] + pulses_in[k2p + 1];
            if (sum > max_pulses) {
                return 1;
            }
            pulses_comb[pulses_comb_ptr + k] = sum;
        }
        return 0;
    }

    static int combine_and_check(int[] pulses_comb, int[] pulses_in, int max_pulses, int len) {
        for (int k = 0; k < len; ++k) {
            int sum = pulses_in[2 * k] + pulses_in[2 * k + 1];
            if (sum > max_pulses) {
                return 1;
            }
            pulses_comb[k] = sum;
        }
        return 0;
    }

    static void silk_encode_pulses(EntropyCoder psRangeEnc, int signalType, int quantOffsetType, byte[] pulses, int frame_length) {
        int k;
        int i;
        int RateLevelIndex = 0;
        int[] pulses_comb = new int[8];
        Arrays.MemSet(pulses_comb, 0, 8);
        Inlines.OpusAssert(true);
        int iter = Inlines.silk_RSHIFT(frame_length, 4);
        if (iter * 16 < frame_length) {
            Inlines.OpusAssert(frame_length == 120);
            ++iter;
            Arrays.MemSetWithOffset(pulses, (byte)0, frame_length, 16);
        }
        int[] abs_pulses = new int[iter * 16];
        Inlines.OpusAssert(true);
        for (i = 0; i < iter * 16; i += 4) {
            abs_pulses[i + 0] = Inlines.silk_abs(pulses[i + 0]);
            abs_pulses[i + 1] = Inlines.silk_abs(pulses[i + 1]);
            abs_pulses[i + 2] = Inlines.silk_abs(pulses[i + 2]);
            abs_pulses[i + 3] = Inlines.silk_abs(pulses[i + 3]);
        }
        int[] sum_pulses = new int[iter];
        int[] nRshifts = new int[iter];
        int abs_pulses_ptr = 0;
        for (i = 0; i < iter; ++i) {
            nRshifts[i] = 0;
            block2: while (true) {
                int scale_down = EncodePulses.combine_and_check(pulses_comb, 0, abs_pulses, abs_pulses_ptr, SilkTables.silk_max_pulses_table[0], 8);
                scale_down += EncodePulses.combine_and_check(pulses_comb, pulses_comb, SilkTables.silk_max_pulses_table[1], 4);
                scale_down += EncodePulses.combine_and_check(pulses_comb, pulses_comb, SilkTables.silk_max_pulses_table[2], 2);
                if ((scale_down += EncodePulses.combine_and_check(sum_pulses, i, pulses_comb, 0, SilkTables.silk_max_pulses_table[3], 1)) == 0) break;
                int n = i;
                nRshifts[n] = nRshifts[n] + 1;
                k = abs_pulses_ptr;
                while (true) {
                    if (k >= abs_pulses_ptr + 16) continue block2;
                    abs_pulses[k] = Inlines.silk_RSHIFT(abs_pulses[k], 1);
                    ++k;
                }
                break;
            }
            abs_pulses_ptr += 16;
        }
        int minSumBits_Q5 = Integer.MAX_VALUE;
        for (k = 0; k < 9; ++k) {
            short[] nBits_ptr = SilkTables.silk_pulses_per_block_BITS_Q5[k];
            int sumBits_Q5 = SilkTables.silk_rate_levels_BITS_Q5[signalType >> 1][k];
            for (i = 0; i < iter; ++i) {
                if (nRshifts[i] > 0) {
                    sumBits_Q5 += nBits_ptr[17];
                    continue;
                }
                sumBits_Q5 += nBits_ptr[sum_pulses[i]];
            }
            if (sumBits_Q5 >= minSumBits_Q5) continue;
            minSumBits_Q5 = sumBits_Q5;
            RateLevelIndex = k;
        }
        psRangeEnc.enc_icdf(RateLevelIndex, SilkTables.silk_rate_levels_iCDF[signalType >> 1], 8);
        for (i = 0; i < iter; ++i) {
            if (nRshifts[i] == 0) {
                psRangeEnc.enc_icdf(sum_pulses[i], SilkTables.silk_pulses_per_block_iCDF[RateLevelIndex], 8);
                continue;
            }
            psRangeEnc.enc_icdf(17, SilkTables.silk_pulses_per_block_iCDF[RateLevelIndex], 8);
            for (k = 0; k < nRshifts[i] - 1; ++k) {
                psRangeEnc.enc_icdf(17, SilkTables.silk_pulses_per_block_iCDF[9], 8);
            }
            psRangeEnc.enc_icdf(sum_pulses[i], SilkTables.silk_pulses_per_block_iCDF[9], 8);
        }
        for (i = 0; i < iter; ++i) {
            if (sum_pulses[i] <= 0) continue;
            ShellCoder.silk_shell_encoder(psRangeEnc, abs_pulses, i * 16);
        }
        for (i = 0; i < iter; ++i) {
            if (nRshifts[i] <= 0) continue;
            int pulses_ptr = i * 16;
            int nLS = nRshifts[i] - 1;
            for (k = 0; k < 16; ++k) {
                int bit;
                byte abs_q = (byte)Inlines.silk_abs(pulses[pulses_ptr + k]);
                for (int j = nLS; j > 0; --j) {
                    bit = Inlines.silk_RSHIFT(abs_q, j) & 1;
                    psRangeEnc.enc_icdf(bit, SilkTables.silk_lsb_iCDF, 8);
                }
                bit = abs_q & 1;
                psRangeEnc.enc_icdf(bit, SilkTables.silk_lsb_iCDF, 8);
            }
        }
        CodeSigns.silk_encode_signs(psRangeEnc, pulses, frame_length, signalType, quantOffsetType, sum_pulses);
    }
}
