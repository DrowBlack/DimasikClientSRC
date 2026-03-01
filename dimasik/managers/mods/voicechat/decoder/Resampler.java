package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.SilkError;
import dimasik.managers.mods.voicechat.decoder.SilkResamplerState;
import dimasik.managers.mods.voicechat.decoder.SilkTables;

class Resampler {
    private static final int USE_silk_resampler_copy = 0;
    private static final int USE_silk_resampler_private_up2_HQ_wrapper = 1;
    private static final int USE_silk_resampler_private_IIR_FIR = 2;
    private static final int USE_silk_resampler_private_down_FIR = 3;
    private static final int ORDER_FIR = 4;

    Resampler() {
    }

    private static int rateID(int R) {
        return ((R >> 12) - (R > 16000 ? 1 : 0) >> (R > 24000 ? 1 : 0)) - 1;
    }

    /*
     * Enabled aggressive block sorting
     */
    static int silk_resampler_init(SilkResamplerState S, int Fs_Hz_in, int Fs_Hz_out, int forEnc) {
        int up2x;
        block13: {
            block15: {
                block18: {
                    block17: {
                        block16: {
                            block14: {
                                S.Reset();
                                if (forEnc != 0) {
                                    if (Fs_Hz_in != 8000 && Fs_Hz_in != 12000 && Fs_Hz_in != 16000 && Fs_Hz_in != 24000 && Fs_Hz_in != 48000 || Fs_Hz_out != 8000 && Fs_Hz_out != 12000 && Fs_Hz_out != 16000) {
                                        Inlines.OpusAssert(false);
                                        return -1;
                                    }
                                    S.inputDelay = SilkTables.delay_matrix_enc[Resampler.rateID(Fs_Hz_in)][Resampler.rateID(Fs_Hz_out)];
                                } else {
                                    if (Fs_Hz_in != 8000 && Fs_Hz_in != 12000 && Fs_Hz_in != 16000 || Fs_Hz_out != 8000 && Fs_Hz_out != 12000 && Fs_Hz_out != 16000 && Fs_Hz_out != 24000 && Fs_Hz_out != 48000) {
                                        Inlines.OpusAssert(false);
                                        return -1;
                                    }
                                    S.inputDelay = SilkTables.delay_matrix_dec[Resampler.rateID(Fs_Hz_in)][Resampler.rateID(Fs_Hz_out)];
                                }
                                S.Fs_in_kHz = Inlines.silk_DIV32_16(Fs_Hz_in, 1000);
                                S.Fs_out_kHz = Inlines.silk_DIV32_16(Fs_Hz_out, 1000);
                                S.batchSize = S.Fs_in_kHz * 10;
                                up2x = 0;
                                if (Fs_Hz_out <= Fs_Hz_in) break block14;
                                if (Fs_Hz_out == Inlines.silk_MUL(Fs_Hz_in, 2)) {
                                    S.resampler_function = 1;
                                    break block13;
                                } else {
                                    S.resampler_function = 2;
                                    up2x = 1;
                                }
                                break block13;
                            }
                            if (Fs_Hz_out >= Fs_Hz_in) break block15;
                            S.resampler_function = 3;
                            if (Inlines.silk_MUL(Fs_Hz_out, 4) != Inlines.silk_MUL(Fs_Hz_in, 3)) break block16;
                            S.FIR_Fracs = 3;
                            S.FIR_Order = 18;
                            S.Coefs = SilkTables.silk_Resampler_3_4_COEFS;
                            break block13;
                        }
                        if (Inlines.silk_MUL(Fs_Hz_out, 3) != Inlines.silk_MUL(Fs_Hz_in, 2)) break block17;
                        S.FIR_Fracs = 2;
                        S.FIR_Order = 18;
                        S.Coefs = SilkTables.silk_Resampler_2_3_COEFS;
                        break block13;
                    }
                    if (Inlines.silk_MUL(Fs_Hz_out, 2) != Fs_Hz_in) break block18;
                    S.FIR_Fracs = 1;
                    S.FIR_Order = 24;
                    S.Coefs = SilkTables.silk_Resampler_1_2_COEFS;
                    break block13;
                }
                if (Inlines.silk_MUL(Fs_Hz_out, 3) == Fs_Hz_in) {
                    S.FIR_Fracs = 1;
                    S.FIR_Order = 36;
                    S.Coefs = SilkTables.silk_Resampler_1_3_COEFS;
                    break block13;
                } else if (Inlines.silk_MUL(Fs_Hz_out, 4) == Fs_Hz_in) {
                    S.FIR_Fracs = 1;
                    S.FIR_Order = 36;
                    S.Coefs = SilkTables.silk_Resampler_1_4_COEFS;
                    break block13;
                } else {
                    if (Inlines.silk_MUL(Fs_Hz_out, 6) != Fs_Hz_in) {
                        Inlines.OpusAssert(false);
                        return -1;
                    }
                    S.FIR_Fracs = 1;
                    S.FIR_Order = 36;
                    S.Coefs = SilkTables.silk_Resampler_1_6_COEFS;
                }
                break block13;
            }
            S.resampler_function = 0;
        }
        S.invRatio_Q16 = Inlines.silk_LSHIFT32(Inlines.silk_DIV32(Inlines.silk_LSHIFT32(Fs_Hz_in, 14 + up2x), Fs_Hz_out), 2);
        while (Inlines.silk_SMULWW(S.invRatio_Q16, Fs_Hz_out) < Inlines.silk_LSHIFT32(Fs_Hz_in, up2x)) {
            ++S.invRatio_Q16;
        }
        return 0;
    }

    static int silk_resampler(SilkResamplerState S, short[] output, int output_ptr, short[] input, int input_ptr, int inLen) {
        Inlines.OpusAssert(inLen >= S.Fs_in_kHz);
        Inlines.OpusAssert(S.inputDelay <= S.Fs_in_kHz);
        int nSamples = S.Fs_in_kHz - S.inputDelay;
        short[] delayBufPtr = S.delayBuf;
        System.arraycopy(input, input_ptr, delayBufPtr, S.inputDelay, nSamples);
        switch (S.resampler_function) {
            case 1: {
                Resampler.silk_resampler_private_up2_HQ(S.sIIR, output, output_ptr, delayBufPtr, 0, S.Fs_in_kHz);
                Resampler.silk_resampler_private_up2_HQ(S.sIIR, output, output_ptr + S.Fs_out_kHz, input, input_ptr + nSamples, inLen - S.Fs_in_kHz);
                break;
            }
            case 2: {
                Resampler.silk_resampler_private_IIR_FIR(S, output, output_ptr, delayBufPtr, 0, S.Fs_in_kHz);
                Resampler.silk_resampler_private_IIR_FIR(S, output, output_ptr + S.Fs_out_kHz, input, input_ptr + nSamples, inLen - S.Fs_in_kHz);
                break;
            }
            case 3: {
                Resampler.silk_resampler_private_down_FIR(S, output, output_ptr, delayBufPtr, 0, S.Fs_in_kHz);
                Resampler.silk_resampler_private_down_FIR(S, output, output_ptr + S.Fs_out_kHz, input, input_ptr + nSamples, inLen - S.Fs_in_kHz);
                break;
            }
            default: {
                System.arraycopy(delayBufPtr, 0, output, output_ptr, S.Fs_in_kHz);
                System.arraycopy(input, input_ptr + nSamples, output, output_ptr + S.Fs_out_kHz, inLen - S.Fs_in_kHz);
            }
        }
        System.arraycopy(input, input_ptr + inLen - S.inputDelay, delayBufPtr, 0, S.inputDelay);
        return SilkError.SILK_NO_ERROR;
    }

    static void silk_resampler_down2(int[] S, short[] output, short[] input, int inLen) {
        int len2 = Inlines.silk_RSHIFT32(inLen, 1);
        Inlines.OpusAssert(true);
        Inlines.OpusAssert(true);
        for (int k = 0; k < len2; ++k) {
            int in32 = Inlines.silk_LSHIFT(input[2 * k], 10);
            int Y = Inlines.silk_SUB32(in32, S[0]);
            int X = Inlines.silk_SMLAWB(Y, Y, -25727);
            int out32 = Inlines.silk_ADD32(S[0], X);
            S[0] = Inlines.silk_ADD32(in32, X);
            in32 = Inlines.silk_LSHIFT(input[2 * k + 1], 10);
            Y = Inlines.silk_SUB32(in32, S[1]);
            X = Inlines.silk_SMULWB(Y, 9872);
            out32 = Inlines.silk_ADD32(out32, S[1]);
            out32 = Inlines.silk_ADD32(out32, X);
            S[1] = Inlines.silk_ADD32(in32, X);
            output[k] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(out32, 11));
        }
    }

    static void silk_resampler_down2_3(int[] S, short[] output, short[] input, int inLen) {
        int nSamplesIn;
        int[] buf = new int[484];
        int input_ptr = 0;
        int output_ptr = 0;
        System.arraycopy(S, 0, buf, 0, 4);
        while (true) {
            nSamplesIn = Inlines.silk_min(inLen, 480);
            Resampler.silk_resampler_private_AR2(S, 4, buf, 4, input, input_ptr, SilkTables.silk_Resampler_2_3_COEFS_LQ, nSamplesIn);
            int buf_ptr = 0;
            for (int counter = nSamplesIn; counter > 2; counter -= 3) {
                int res_Q6 = Inlines.silk_SMULWB(buf[buf_ptr], SilkTables.silk_Resampler_2_3_COEFS_LQ[2]);
                res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 1], SilkTables.silk_Resampler_2_3_COEFS_LQ[3]);
                res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 2], SilkTables.silk_Resampler_2_3_COEFS_LQ[5]);
                res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 3], SilkTables.silk_Resampler_2_3_COEFS_LQ[4]);
                output[output_ptr++] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(res_Q6, 6));
                res_Q6 = Inlines.silk_SMULWB(buf[buf_ptr + 1], SilkTables.silk_Resampler_2_3_COEFS_LQ[4]);
                res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 2], SilkTables.silk_Resampler_2_3_COEFS_LQ[5]);
                res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 3], SilkTables.silk_Resampler_2_3_COEFS_LQ[3]);
                res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 4], SilkTables.silk_Resampler_2_3_COEFS_LQ[2]);
                output[output_ptr++] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(res_Q6, 6));
                buf_ptr += 3;
            }
            input_ptr += nSamplesIn;
            if ((inLen -= nSamplesIn) <= 0) break;
            System.arraycopy(buf, nSamplesIn, buf, 0, 4);
        }
        System.arraycopy(buf, nSamplesIn, S, 0, 4);
    }

    static void silk_resampler_private_AR2(int[] S, int S_ptr, int[] out_Q8, int out_Q8_ptr, short[] input, int input_ptr, short[] A_Q14, int len) {
        for (int k = 0; k < len; ++k) {
            int out32;
            out_Q8[out_Q8_ptr + k] = out32 = Inlines.silk_ADD_LSHIFT32(S[S_ptr], input[input_ptr + k], 8);
            out32 = Inlines.silk_LSHIFT(out32, 2);
            S[S_ptr] = Inlines.silk_SMLAWB(S[S_ptr + 1], out32, A_Q14[0]);
            S[S_ptr + 1] = Inlines.silk_SMULWB(out32, A_Q14[1]);
        }
    }

    static int silk_resampler_private_down_FIR_INTERPOL(short[] output, int output_ptr, int[] buf, short[] FIR_Coefs, int FIR_Coefs_ptr, int FIR_Order, int FIR_Fracs, int max_index_Q16, int index_increment_Q16) {
        switch (FIR_Order) {
            case 18: {
                for (int index_Q16 = 0; index_Q16 < max_index_Q16; index_Q16 += index_increment_Q16) {
                    int buf_ptr = Inlines.silk_RSHIFT(index_Q16, 16);
                    int interpol_ind = Inlines.silk_SMULWB(index_Q16 & 0xFFFF, FIR_Fracs);
                    int interpol_ptr = FIR_Coefs_ptr + 9 * interpol_ind;
                    int res_Q6 = Inlines.silk_SMULWB(buf[buf_ptr + 0], FIR_Coefs[interpol_ptr + 0]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 1], FIR_Coefs[interpol_ptr + 1]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 2], FIR_Coefs[interpol_ptr + 2]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 3], FIR_Coefs[interpol_ptr + 3]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 4], FIR_Coefs[interpol_ptr + 4]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 5], FIR_Coefs[interpol_ptr + 5]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 6], FIR_Coefs[interpol_ptr + 6]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 7], FIR_Coefs[interpol_ptr + 7]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 8], FIR_Coefs[interpol_ptr + 8]);
                    interpol_ptr = FIR_Coefs_ptr + 9 * (FIR_Fracs - 1 - interpol_ind);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 17], FIR_Coefs[interpol_ptr + 0]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 16], FIR_Coefs[interpol_ptr + 1]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 15], FIR_Coefs[interpol_ptr + 2]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 14], FIR_Coefs[interpol_ptr + 3]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 13], FIR_Coefs[interpol_ptr + 4]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 12], FIR_Coefs[interpol_ptr + 5]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 11], FIR_Coefs[interpol_ptr + 6]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 10], FIR_Coefs[interpol_ptr + 7]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, buf[buf_ptr + 9], FIR_Coefs[interpol_ptr + 8]);
                    output[output_ptr++] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(res_Q6, 6));
                }
                break;
            }
            case 24: {
                for (int index_Q16 = 0; index_Q16 < max_index_Q16; index_Q16 += index_increment_Q16) {
                    int buf_ptr = Inlines.silk_RSHIFT(index_Q16, 16);
                    int res_Q6 = Inlines.silk_SMULWB(Inlines.silk_ADD32(buf[buf_ptr + 0], buf[buf_ptr + 23]), FIR_Coefs[FIR_Coefs_ptr + 0]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 1], buf[buf_ptr + 22]), FIR_Coefs[FIR_Coefs_ptr + 1]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 2], buf[buf_ptr + 21]), FIR_Coefs[FIR_Coefs_ptr + 2]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 3], buf[buf_ptr + 20]), FIR_Coefs[FIR_Coefs_ptr + 3]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 4], buf[buf_ptr + 19]), FIR_Coefs[FIR_Coefs_ptr + 4]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 5], buf[buf_ptr + 18]), FIR_Coefs[FIR_Coefs_ptr + 5]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 6], buf[buf_ptr + 17]), FIR_Coefs[FIR_Coefs_ptr + 6]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 7], buf[buf_ptr + 16]), FIR_Coefs[FIR_Coefs_ptr + 7]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 8], buf[buf_ptr + 15]), FIR_Coefs[FIR_Coefs_ptr + 8]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 9], buf[buf_ptr + 14]), FIR_Coefs[FIR_Coefs_ptr + 9]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 10], buf[buf_ptr + 13]), FIR_Coefs[FIR_Coefs_ptr + 10]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 11], buf[buf_ptr + 12]), FIR_Coefs[FIR_Coefs_ptr + 11]);
                    output[output_ptr++] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(res_Q6, 6));
                }
                break;
            }
            case 36: {
                for (int index_Q16 = 0; index_Q16 < max_index_Q16; index_Q16 += index_increment_Q16) {
                    int buf_ptr = Inlines.silk_RSHIFT(index_Q16, 16);
                    int res_Q6 = Inlines.silk_SMULWB(Inlines.silk_ADD32(buf[buf_ptr + 0], buf[buf_ptr + 35]), FIR_Coefs[FIR_Coefs_ptr + 0]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 1], buf[buf_ptr + 34]), FIR_Coefs[FIR_Coefs_ptr + 1]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 2], buf[buf_ptr + 33]), FIR_Coefs[FIR_Coefs_ptr + 2]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 3], buf[buf_ptr + 32]), FIR_Coefs[FIR_Coefs_ptr + 3]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 4], buf[buf_ptr + 31]), FIR_Coefs[FIR_Coefs_ptr + 4]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 5], buf[buf_ptr + 30]), FIR_Coefs[FIR_Coefs_ptr + 5]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 6], buf[buf_ptr + 29]), FIR_Coefs[FIR_Coefs_ptr + 6]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 7], buf[buf_ptr + 28]), FIR_Coefs[FIR_Coefs_ptr + 7]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 8], buf[buf_ptr + 27]), FIR_Coefs[FIR_Coefs_ptr + 8]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 9], buf[buf_ptr + 26]), FIR_Coefs[FIR_Coefs_ptr + 9]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 10], buf[buf_ptr + 25]), FIR_Coefs[FIR_Coefs_ptr + 10]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 11], buf[buf_ptr + 24]), FIR_Coefs[FIR_Coefs_ptr + 11]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 12], buf[buf_ptr + 23]), FIR_Coefs[FIR_Coefs_ptr + 12]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 13], buf[buf_ptr + 22]), FIR_Coefs[FIR_Coefs_ptr + 13]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 14], buf[buf_ptr + 21]), FIR_Coefs[FIR_Coefs_ptr + 14]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 15], buf[buf_ptr + 20]), FIR_Coefs[FIR_Coefs_ptr + 15]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 16], buf[buf_ptr + 19]), FIR_Coefs[FIR_Coefs_ptr + 16]);
                    res_Q6 = Inlines.silk_SMLAWB(res_Q6, Inlines.silk_ADD32(buf[buf_ptr + 17], buf[buf_ptr + 18]), FIR_Coefs[FIR_Coefs_ptr + 17]);
                    output[output_ptr++] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(res_Q6, 6));
                }
                break;
            }
            default: {
                Inlines.OpusAssert(false);
            }
        }
        return output_ptr;
    }

    static void silk_resampler_private_down_FIR(SilkResamplerState S, short[] output, int output_ptr, short[] input, int input_ptr, int inLen) {
        int nSamplesIn;
        int[] buf = new int[S.batchSize + S.FIR_Order];
        System.arraycopy(S.sFIR_i32, 0, buf, 0, S.FIR_Order);
        int index_increment_Q16 = S.invRatio_Q16;
        while (true) {
            nSamplesIn = Inlines.silk_min(inLen, S.batchSize);
            Resampler.silk_resampler_private_AR2(S.sIIR, 0, buf, S.FIR_Order, input, input_ptr, S.Coefs, nSamplesIn);
            int max_index_Q16 = Inlines.silk_LSHIFT32(nSamplesIn, 16);
            output_ptr = Resampler.silk_resampler_private_down_FIR_INTERPOL(output, output_ptr, buf, S.Coefs, 2, S.FIR_Order, S.FIR_Fracs, max_index_Q16, index_increment_Q16);
            input_ptr += nSamplesIn;
            if ((inLen -= nSamplesIn) <= 1) break;
            System.arraycopy(buf, nSamplesIn, buf, 0, S.FIR_Order);
        }
        System.arraycopy(buf, nSamplesIn, S.sFIR_i32, 0, S.FIR_Order);
    }

    static int silk_resampler_private_IIR_FIR_INTERPOL(short[] output, int output_ptr, short[] buf, int max_index_Q16, int index_increment_Q16) {
        for (int index_Q16 = 0; index_Q16 < max_index_Q16; index_Q16 += index_increment_Q16) {
            int table_index = Inlines.silk_SMULWB(index_Q16 & 0xFFFF, 12);
            int buf_ptr = index_Q16 >> 16;
            int res_Q15 = Inlines.silk_SMULBB(buf[buf_ptr], SilkTables.silk_resampler_frac_FIR_12[table_index][0]);
            res_Q15 = Inlines.silk_SMLABB(res_Q15, buf[buf_ptr + 1], SilkTables.silk_resampler_frac_FIR_12[table_index][1]);
            res_Q15 = Inlines.silk_SMLABB(res_Q15, buf[buf_ptr + 2], SilkTables.silk_resampler_frac_FIR_12[table_index][2]);
            res_Q15 = Inlines.silk_SMLABB(res_Q15, buf[buf_ptr + 3], SilkTables.silk_resampler_frac_FIR_12[table_index][3]);
            res_Q15 = Inlines.silk_SMLABB(res_Q15, buf[buf_ptr + 4], SilkTables.silk_resampler_frac_FIR_12[11 - table_index][3]);
            res_Q15 = Inlines.silk_SMLABB(res_Q15, buf[buf_ptr + 5], SilkTables.silk_resampler_frac_FIR_12[11 - table_index][2]);
            res_Q15 = Inlines.silk_SMLABB(res_Q15, buf[buf_ptr + 6], SilkTables.silk_resampler_frac_FIR_12[11 - table_index][1]);
            res_Q15 = Inlines.silk_SMLABB(res_Q15, buf[buf_ptr + 7], SilkTables.silk_resampler_frac_FIR_12[11 - table_index][0]);
            output[output_ptr++] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(res_Q15, 15));
        }
        return output_ptr;
    }

    static void silk_resampler_private_IIR_FIR(SilkResamplerState S, short[] output, int output_ptr, short[] input, int input_ptr, int inLen) {
        int nSamplesIn;
        short[] buf = new short[2 * S.batchSize + 8];
        System.arraycopy(S.sFIR_i16, 0, buf, 0, 8);
        int index_increment_Q16 = S.invRatio_Q16;
        while (true) {
            nSamplesIn = Inlines.silk_min(inLen, S.batchSize);
            Resampler.silk_resampler_private_up2_HQ(S.sIIR, buf, 8, input, input_ptr, nSamplesIn);
            int max_index_Q16 = Inlines.silk_LSHIFT32(nSamplesIn, 17);
            output_ptr = Resampler.silk_resampler_private_IIR_FIR_INTERPOL(output, output_ptr, buf, max_index_Q16, index_increment_Q16);
            input_ptr += nSamplesIn;
            if ((inLen -= nSamplesIn) <= 0) break;
            System.arraycopy(buf, nSamplesIn << 1, buf, 0, 8);
        }
        System.arraycopy(buf, nSamplesIn << 1, S.sFIR_i16, 0, 8);
    }

    static void silk_resampler_private_up2_HQ(int[] S, short[] output, int output_ptr, short[] input, int input_ptr, int len) {
        Inlines.OpusAssert(SilkTables.silk_resampler_up2_hq_0[0] > 0);
        Inlines.OpusAssert(SilkTables.silk_resampler_up2_hq_0[1] > 0);
        Inlines.OpusAssert(SilkTables.silk_resampler_up2_hq_0[2] < 0);
        Inlines.OpusAssert(SilkTables.silk_resampler_up2_hq_1[0] > 0);
        Inlines.OpusAssert(SilkTables.silk_resampler_up2_hq_1[1] > 0);
        Inlines.OpusAssert(SilkTables.silk_resampler_up2_hq_1[2] < 0);
        for (int k = 0; k < len; ++k) {
            int in32 = Inlines.silk_LSHIFT(input[input_ptr + k], 10);
            int Y = Inlines.silk_SUB32(in32, S[0]);
            int X = Inlines.silk_SMULWB(Y, SilkTables.silk_resampler_up2_hq_0[0]);
            int out32_1 = Inlines.silk_ADD32(S[0], X);
            S[0] = Inlines.silk_ADD32(in32, X);
            Y = Inlines.silk_SUB32(out32_1, S[1]);
            X = Inlines.silk_SMULWB(Y, SilkTables.silk_resampler_up2_hq_0[1]);
            int out32_2 = Inlines.silk_ADD32(S[1], X);
            S[1] = Inlines.silk_ADD32(out32_1, X);
            Y = Inlines.silk_SUB32(out32_2, S[2]);
            X = Inlines.silk_SMLAWB(Y, Y, SilkTables.silk_resampler_up2_hq_0[2]);
            out32_1 = Inlines.silk_ADD32(S[2], X);
            S[2] = Inlines.silk_ADD32(out32_2, X);
            output[output_ptr + 2 * k] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(out32_1, 10));
            Y = Inlines.silk_SUB32(in32, S[3]);
            X = Inlines.silk_SMULWB(Y, SilkTables.silk_resampler_up2_hq_1[0]);
            out32_1 = Inlines.silk_ADD32(S[3], X);
            S[3] = Inlines.silk_ADD32(in32, X);
            Y = Inlines.silk_SUB32(out32_1, S[4]);
            X = Inlines.silk_SMULWB(Y, SilkTables.silk_resampler_up2_hq_1[1]);
            out32_2 = Inlines.silk_ADD32(S[4], X);
            S[4] = Inlines.silk_ADD32(out32_1, X);
            Y = Inlines.silk_SUB32(out32_2, S[5]);
            X = Inlines.silk_SMLAWB(Y, Y, SilkTables.silk_resampler_up2_hq_1[2]);
            out32_1 = Inlines.silk_ADD32(S[5], X);
            S[5] = Inlines.silk_ADD32(out32_2, X);
            output[output_ptr + 2 * k + 1] = (short)Inlines.silk_SAT16(Inlines.silk_RSHIFT_ROUND(out32_1, 10));
        }
    }
}
