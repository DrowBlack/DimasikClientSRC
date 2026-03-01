package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;
import dimasik.managers.mods.voicechat.decoder.BoxedValueByte;
import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.BoxedValueShort;
import dimasik.managers.mods.voicechat.decoder.CeltPitchXCorr;
import dimasik.managers.mods.voicechat.decoder.Inlines;
import dimasik.managers.mods.voicechat.decoder.Resampler;
import dimasik.managers.mods.voicechat.decoder.SilkTables;
import dimasik.managers.mods.voicechat.decoder.Sort;
import dimasik.managers.mods.voicechat.decoder.SumSqrShift;

class PitchAnalysisCore {
    private static final int SCRATCH_SIZE = 22;
    private static final int SF_LENGTH_4KHZ = 20;
    private static final int SF_LENGTH_8KHZ = 40;
    private static final int MIN_LAG_4KHZ = 8;
    private static final int MIN_LAG_8KHZ = 16;
    private static final int MAX_LAG_4KHZ = 72;
    private static final int MAX_LAG_8KHZ = 143;
    private static final int CSTRIDE_4KHZ = 65;
    private static final int CSTRIDE_8KHZ = 132;
    private static final int D_COMP_MIN = 13;
    private static final int D_COMP_MAX = 147;
    private static final int D_COMP_STRIDE = 134;

    PitchAnalysisCore() {
    }

    static int silk_pitch_analysis_core(short[] frame, int[] pitch_out, BoxedValueShort lagIndex, BoxedValueByte contourIndex, BoxedValueInt LTPCorr_Q15, int prevLag, int search_thres1_Q16, int search_thres2_Q13, int Fs_kHz, int complexity, int nb_subfr) {
        int CCmax_new;
        int nb_cbk_search;
        byte[][] Lag_CB_ptr;
        int prevLag_log2_Q7;
        int j;
        int energy_target;
        int length_d_srch;
        int d;
        int cross_corr;
        int basis_ptr;
        short[] basis;
        int k;
        int i;
        int[] filt_state = new int[6];
        int[] d_srch = new int[24];
        int[] CC = new int[11];
        Inlines.OpusAssert(Fs_kHz == 8 || Fs_kHz == 12 || Fs_kHz == 16);
        Inlines.OpusAssert(complexity >= 0);
        Inlines.OpusAssert(complexity <= 2);
        Inlines.OpusAssert(search_thres1_Q16 >= 0 && search_thres1_Q16 <= 65536);
        Inlines.OpusAssert(search_thres2_Q13 >= 0 && search_thres2_Q13 <= 8192);
        int frame_length = (20 + nb_subfr * 5) * Fs_kHz;
        int frame_length_4kHz = (20 + nb_subfr * 5) * 4;
        int frame_length_8kHz = (20 + nb_subfr * 5) * 8;
        int sf_length = 5 * Fs_kHz;
        int min_lag = 2 * Fs_kHz;
        int max_lag = 18 * Fs_kHz - 1;
        short[] frame_8kHz = new short[frame_length_8kHz];
        if (Fs_kHz == 16) {
            Arrays.MemSet(filt_state, 0, 2);
            Resampler.silk_resampler_down2(filt_state, frame_8kHz, frame, frame_length);
        } else if (Fs_kHz == 12) {
            Arrays.MemSet(filt_state, 0, 6);
            Resampler.silk_resampler_down2_3(filt_state, frame_8kHz, frame, frame_length);
        } else {
            Inlines.OpusAssert(Fs_kHz == 8);
            System.arraycopy(frame, 0, frame_8kHz, 0, frame_length_8kHz);
        }
        Arrays.MemSet(filt_state, 0, 2);
        short[] frame_4kHz = new short[frame_length_4kHz];
        Resampler.silk_resampler_down2(filt_state, frame_4kHz, frame_8kHz, frame_length_8kHz);
        for (i = frame_length_4kHz - 1; i > 0; --i) {
            frame_4kHz[i] = Inlines.silk_ADD_SAT16(frame_4kHz[i], frame_4kHz[i - 1]);
        }
        BoxedValueInt boxed_energy = new BoxedValueInt(0);
        BoxedValueInt boxed_shift = new BoxedValueInt(0);
        SumSqrShift.silk_sum_sqr_shift(boxed_energy, boxed_shift, frame_4kHz, frame_length_4kHz);
        int energy = boxed_energy.Val;
        int shift = boxed_shift.Val;
        if (shift > 0) {
            shift = Inlines.silk_RSHIFT(shift, 1);
            for (i = 0; i < frame_length_4kHz; ++i) {
                frame_4kHz[i] = Inlines.silk_RSHIFT16(frame_4kHz[i], shift);
            }
        }
        short[] C = new short[nb_subfr * 132];
        int[] xcorr32 = new int[65];
        Arrays.MemSet(C, (short)0, (nb_subfr >> 1) * 65);
        short[] target = frame_4kHz;
        int target_ptr = Inlines.silk_LSHIFT(20, 2);
        for (k = 0; k < nb_subfr >> 1; ++k) {
            basis = target;
            basis_ptr = target_ptr - 8;
            CeltPitchXCorr.pitch_xcorr(target, target_ptr, target, target_ptr - 72, xcorr32, 40, 65);
            cross_corr = xcorr32[64];
            int normalizer = Inlines.silk_inner_prod_self(target, target_ptr, 40);
            normalizer = Inlines.silk_ADD32(normalizer, Inlines.silk_inner_prod_self(basis, basis_ptr, 40));
            normalizer = Inlines.silk_ADD32(normalizer, Inlines.silk_SMULBB(40, 4000));
            Inlines.MatrixSet(C, k, 0, 65, (short)Inlines.silk_DIV32_varQ(cross_corr, normalizer, 14));
            for (d = 9; d <= 72; ++d) {
                cross_corr = xcorr32[72 - d];
                normalizer = Inlines.silk_ADD32(normalizer, Inlines.silk_SMULBB(basis[--basis_ptr], basis[basis_ptr]) - Inlines.silk_SMULBB(basis[basis_ptr + 40], basis[basis_ptr + 40]));
                Inlines.MatrixSet(C, k, d - 8, 65, (short)Inlines.silk_DIV32_varQ(cross_corr, normalizer, 14));
            }
            target_ptr += 40;
        }
        if (nb_subfr == 4) {
            for (i = 72; i >= 8; --i) {
                sum = Inlines.MatrixGet(C, 0, i - 8, 65) + Inlines.MatrixGet(C, 1, i - 8, 65);
                sum = Inlines.silk_SMLAWB(sum, sum, Inlines.silk_LSHIFT(-i, 4));
                C[i - 8] = (short)sum;
            }
        } else {
            for (i = 72; i >= 8; --i) {
                sum = Inlines.silk_LSHIFT(C[i - 8], 1);
                sum = Inlines.silk_SMLAWB(sum, sum, Inlines.silk_LSHIFT(-i, 4));
                C[i - 8] = (short)sum;
            }
        }
        Inlines.OpusAssert(3 * (length_d_srch = Inlines.silk_ADD_LSHIFT32(4, complexity, 1)) <= 24);
        Sort.silk_insertion_sort_decreasing_int16(C, d_srch, 65, length_d_srch);
        short Cmax = C[0];
        if (Cmax < 3277) {
            Arrays.MemSet(pitch_out, 0, nb_subfr);
            LTPCorr_Q15.Val = 0;
            lagIndex.Val = 0;
            contourIndex.Val = 0;
            return 1;
        }
        int threshold = Inlines.silk_SMULWB(search_thres1_Q16, Cmax);
        for (i = 0; i < length_d_srch; ++i) {
            if (C[i] <= threshold) {
                length_d_srch = i;
                break;
            }
            d_srch[i] = Inlines.silk_LSHIFT(d_srch[i] + 8, 1);
        }
        Inlines.OpusAssert(length_d_srch > 0);
        short[] d_comp = new short[134];
        for (i = 13; i < 147; ++i) {
            d_comp[i - 13] = 0;
        }
        for (i = 0; i < length_d_srch; ++i) {
            d_comp[d_srch[i] - 13] = 1;
        }
        for (i = 146; i >= 16; --i) {
            int n = i - 13;
            d_comp[n] = (short)(d_comp[n] + (short)(d_comp[i - 1 - 13] + d_comp[i - 2 - 13]));
        }
        length_d_srch = 0;
        for (i = 16; i < 144; ++i) {
            if (d_comp[i + 1 - 13] <= 0) continue;
            d_srch[length_d_srch] = i;
            ++length_d_srch;
        }
        for (i = 146; i >= 16; --i) {
            int n = i - 13;
            d_comp[n] = (short)(d_comp[n] + (short)(d_comp[i - 1 - 13] + d_comp[i - 2 - 13] + d_comp[i - 3 - 13]));
        }
        int length_d_comp = 0;
        for (i = 16; i < 147; ++i) {
            if (d_comp[i - 13] <= 0) continue;
            d_comp[length_d_comp] = (short)(i - 2);
            ++length_d_comp;
        }
        boxed_energy.Val = 0;
        boxed_shift.Val = 0;
        SumSqrShift.silk_sum_sqr_shift(boxed_energy, boxed_shift, frame_8kHz, frame_length_8kHz);
        energy = boxed_energy.Val;
        shift = boxed_shift.Val;
        if (shift > 0) {
            shift = Inlines.silk_RSHIFT(shift, 1);
            for (i = 0; i < frame_length_8kHz; ++i) {
                frame_8kHz[i] = Inlines.silk_RSHIFT16(frame_8kHz[i], shift);
            }
        }
        Arrays.MemSet(C, (short)0, nb_subfr * 132);
        target = frame_8kHz;
        target_ptr = 160;
        for (k = 0; k < nb_subfr; ++k) {
            energy_target = Inlines.silk_ADD32(Inlines.silk_inner_prod(target, target_ptr, target, target_ptr, 40), 1);
            for (j = 0; j < length_d_comp; ++j) {
                basis = target;
                d = d_comp[j];
                basis_ptr = target_ptr - d;
                cross_corr = Inlines.silk_inner_prod(target, target_ptr, basis, basis_ptr, 40);
                if (cross_corr > 0) {
                    int energy_basis = Inlines.silk_inner_prod_self(basis, basis_ptr, 40);
                    Inlines.MatrixSet(C, k, d - 14, 132, (short)Inlines.silk_DIV32_varQ(cross_corr, Inlines.silk_ADD32(energy_target, energy_basis), 14));
                    continue;
                }
                Inlines.MatrixSet(C, k, d - 14, 132, (short)0);
            }
            target_ptr += 40;
        }
        int CCmax = Integer.MIN_VALUE;
        int CCmax_b = Integer.MIN_VALUE;
        int CBimax = 0;
        int lag = -1;
        if (prevLag > 0) {
            if (Fs_kHz == 12) {
                prevLag = Inlines.silk_DIV32_16(Inlines.silk_LSHIFT(prevLag, 1), 3);
            } else if (Fs_kHz == 16) {
                prevLag = Inlines.silk_RSHIFT(prevLag, 1);
            }
            prevLag_log2_Q7 = Inlines.silk_lin2log(prevLag);
        } else {
            prevLag_log2_Q7 = 0;
        }
        Inlines.OpusAssert(search_thres2_Q13 == Inlines.silk_SAT16(search_thres2_Q13));
        if (nb_subfr == 4) {
            Lag_CB_ptr = SilkTables.silk_CB_lags_stage2;
            nb_cbk_search = Fs_kHz == 8 && complexity > 0 ? 11 : 3;
        } else {
            Lag_CB_ptr = SilkTables.silk_CB_lags_stage2_10_ms;
            nb_cbk_search = 3;
        }
        for (k = 0; k < length_d_srch; ++k) {
            d = d_srch[k];
            for (j = 0; j < nb_cbk_search; ++j) {
                CC[j] = 0;
                for (i = 0; i < nb_subfr; ++i) {
                    int d_subfr = d + Lag_CB_ptr[i][j];
                    CC[j] = CC[j] + Inlines.MatrixGet(C, i, d_subfr - 14, 132);
                }
            }
            CCmax_new = Integer.MIN_VALUE;
            int CBimax_new = 0;
            for (i = 0; i < nb_cbk_search; ++i) {
                if (CC[i] <= CCmax_new) continue;
                CCmax_new = CC[i];
                CBimax_new = i;
            }
            int lag_log2_Q7 = Inlines.silk_lin2log(d);
            Inlines.OpusAssert(lag_log2_Q7 == Inlines.silk_SAT16(lag_log2_Q7));
            Inlines.OpusAssert(nb_subfr * 1638 == Inlines.silk_SAT16(nb_subfr * 1638));
            int CCmax_new_b = CCmax_new - Inlines.silk_RSHIFT(Inlines.silk_SMULBB(nb_subfr * 1638, lag_log2_Q7), 7);
            Inlines.OpusAssert(nb_subfr * 1638 == Inlines.silk_SAT16(nb_subfr * 1638));
            if (prevLag > 0) {
                int delta_lag_log2_sqr_Q7 = lag_log2_Q7 - prevLag_log2_Q7;
                Inlines.OpusAssert(delta_lag_log2_sqr_Q7 == Inlines.silk_SAT16(delta_lag_log2_sqr_Q7));
                delta_lag_log2_sqr_Q7 = Inlines.silk_RSHIFT(Inlines.silk_SMULBB(delta_lag_log2_sqr_Q7, delta_lag_log2_sqr_Q7), 7);
                int prev_lag_bias_Q13 = Inlines.silk_RSHIFT(Inlines.silk_SMULBB(nb_subfr * 1638, LTPCorr_Q15.Val), 15);
                prev_lag_bias_Q13 = Inlines.silk_DIV32(Inlines.silk_MUL(prev_lag_bias_Q13, delta_lag_log2_sqr_Q7), delta_lag_log2_sqr_Q7 + 64);
                CCmax_new_b -= prev_lag_bias_Q13;
            }
            if (CCmax_new_b <= CCmax_b || CCmax_new <= Inlines.silk_SMULBB(nb_subfr, search_thres2_Q13) || SilkTables.silk_CB_lags_stage2[0][CBimax_new] > 16) continue;
            CCmax_b = CCmax_new_b;
            CCmax = CCmax_new;
            lag = d;
            CBimax = CBimax_new;
        }
        if (lag == -1) {
            Arrays.MemSet(pitch_out, 0, nb_subfr);
            LTPCorr_Q15.Val = 0;
            lagIndex.Val = 0;
            contourIndex.Val = 0;
            return 1;
        }
        LTPCorr_Q15.Val = Inlines.silk_LSHIFT(Inlines.silk_DIV32_16(CCmax, nb_subfr), 2);
        Inlines.OpusAssert(LTPCorr_Q15.Val >= 0);
        if (Fs_kHz > 8) {
            short[] input_frame_ptr;
            boxed_energy.Val = 0;
            boxed_shift.Val = 0;
            SumSqrShift.silk_sum_sqr_shift(boxed_energy, boxed_shift, frame, frame_length);
            energy = boxed_energy.Val;
            shift = boxed_shift.Val;
            if (shift > 0) {
                short[] scratch_mem = new short[frame_length];
                shift = Inlines.silk_RSHIFT(shift, 1);
                for (i = 0; i < frame_length; ++i) {
                    scratch_mem[i] = Inlines.silk_RSHIFT16(frame[i], shift);
                }
                input_frame_ptr = scratch_mem;
            } else {
                input_frame_ptr = frame;
            }
            int CBimax_old = CBimax;
            Inlines.OpusAssert(lag == Inlines.silk_SAT16(lag));
            lag = Fs_kHz == 12 ? Inlines.silk_RSHIFT(Inlines.silk_SMULBB(lag, 3), 1) : (Fs_kHz == 16 ? Inlines.silk_LSHIFT(lag, 1) : Inlines.silk_SMULBB(lag, 3));
            lag = Inlines.silk_LIMIT_int(lag, min_lag, max_lag);
            int start_lag = Inlines.silk_max_int(lag - 2, min_lag);
            int end_lag = Inlines.silk_min_int(lag + 2, max_lag);
            int lag_new = lag;
            CBimax = 0;
            CCmax = Integer.MIN_VALUE;
            for (k = 0; k < nb_subfr; ++k) {
                pitch_out[k] = lag + 2 * SilkTables.silk_CB_lags_stage2[k][CBimax_old];
            }
            if (nb_subfr == 4) {
                nb_cbk_search = SilkTables.silk_nb_cbk_searchs_stage3[complexity];
                Lag_CB_ptr = SilkTables.silk_CB_lags_stage3;
            } else {
                nb_cbk_search = 12;
                Lag_CB_ptr = SilkTables.silk_CB_lags_stage3_10_ms;
            }
            silk_pe_stage3_vals[] energies_st3 = new silk_pe_stage3_vals[nb_subfr * nb_cbk_search];
            silk_pe_stage3_vals[] cross_corr_st3 = new silk_pe_stage3_vals[nb_subfr * nb_cbk_search];
            for (int c = 0; c < nb_subfr * nb_cbk_search; ++c) {
                energies_st3[c] = new silk_pe_stage3_vals();
                cross_corr_st3[c] = new silk_pe_stage3_vals();
            }
            PitchAnalysisCore.silk_P_Ana_calc_corr_st3(cross_corr_st3, input_frame_ptr, start_lag, sf_length, nb_subfr, complexity);
            PitchAnalysisCore.silk_P_Ana_calc_energy_st3(energies_st3, input_frame_ptr, start_lag, sf_length, nb_subfr, complexity);
            int lag_counter = 0;
            Inlines.OpusAssert(lag == Inlines.silk_SAT16(lag));
            int contour_bias_Q15 = Inlines.silk_DIV32_16(1638, lag);
            target = input_frame_ptr;
            target_ptr = 20 * Fs_kHz;
            energy_target = Inlines.silk_ADD32(Inlines.silk_inner_prod_self(target, target_ptr, nb_subfr * sf_length), 1);
            for (d = start_lag; d <= end_lag; ++d) {
                for (j = 0; j < nb_cbk_search; ++j) {
                    cross_corr = 0;
                    energy = energy_target;
                    for (k = 0; k < nb_subfr; ++k) {
                        cross_corr = Inlines.silk_ADD32(cross_corr, Inlines.MatrixGet((silk_pe_stage3_vals[])cross_corr_st3, (int)k, (int)j, (int)nb_cbk_search).Values[lag_counter]);
                        Inlines.OpusAssert((energy = Inlines.silk_ADD32(energy, Inlines.MatrixGet((silk_pe_stage3_vals[])energies_st3, (int)k, (int)j, (int)nb_cbk_search).Values[lag_counter])) >= 0);
                    }
                    if (cross_corr > 0) {
                        CCmax_new = Inlines.silk_DIV32_varQ(cross_corr, energy, 14);
                        int diff = Short.MAX_VALUE - Inlines.silk_MUL(contour_bias_Q15, j);
                        Inlines.OpusAssert(diff == Inlines.silk_SAT16(diff));
                        CCmax_new = Inlines.silk_SMULWB(CCmax_new, diff);
                    } else {
                        CCmax_new = 0;
                    }
                    if (CCmax_new <= CCmax || d + SilkTables.silk_CB_lags_stage3[0][j] > max_lag) continue;
                    CCmax = CCmax_new;
                    lag_new = d;
                    CBimax = j;
                }
                ++lag_counter;
            }
            for (k = 0; k < nb_subfr; ++k) {
                pitch_out[k] = lag_new + Lag_CB_ptr[k][CBimax];
                pitch_out[k] = Inlines.silk_LIMIT(pitch_out[k], min_lag, 18 * Fs_kHz);
            }
            lagIndex.Val = (short)(lag_new - min_lag);
            contourIndex.Val = (byte)CBimax;
        } else {
            for (k = 0; k < nb_subfr; ++k) {
                pitch_out[k] = lag + Lag_CB_ptr[k][CBimax];
                pitch_out[k] = Inlines.silk_LIMIT(pitch_out[k], 16, 144);
            }
            lagIndex.Val = (short)(lag - 16);
            contourIndex.Val = (byte)CBimax;
        }
        Inlines.OpusAssert(lagIndex.Val >= 0);
        return 0;
    }

    private static void silk_P_Ana_calc_corr_st3(silk_pe_stage3_vals[] cross_corr_st3, short[] frame, int start_lag, int sf_length, int nb_subfr, int complexity) {
        int nb_cbk_search;
        byte[][] Lag_CB_ptr;
        byte[][] Lag_range_ptr;
        Inlines.OpusAssert(complexity >= 0);
        Inlines.OpusAssert(complexity <= 2);
        if (nb_subfr == 4) {
            Lag_range_ptr = SilkTables.silk_Lag_range_stage3[complexity];
            Lag_CB_ptr = SilkTables.silk_CB_lags_stage3;
            nb_cbk_search = SilkTables.silk_nb_cbk_searchs_stage3[complexity];
        } else {
            Inlines.OpusAssert(nb_subfr == 2);
            Lag_range_ptr = SilkTables.silk_Lag_range_stage3_10_ms;
            Lag_CB_ptr = SilkTables.silk_CB_lags_stage3_10_ms;
            nb_cbk_search = 12;
        }
        int[] scratch_mem = new int[22];
        int[] xcorr32 = new int[22];
        int target_ptr = Inlines.silk_LSHIFT(sf_length, 2);
        for (int k = 0; k < nb_subfr; ++k) {
            int j;
            int lag_counter = 0;
            byte lag_high = Lag_range_ptr[k][1];
            int lag_low = Lag_range_ptr[k][0];
            Inlines.OpusAssert(lag_high - lag_low + 1 <= 22);
            CeltPitchXCorr.pitch_xcorr(frame, target_ptr, frame, target_ptr - start_lag - lag_high, xcorr32, sf_length, lag_high - lag_low + 1);
            for (j = lag_low; j <= lag_high; ++j) {
                Inlines.OpusAssert(lag_counter < 22);
                scratch_mem[lag_counter] = xcorr32[lag_high - j];
                ++lag_counter;
            }
            byte delta = Lag_range_ptr[k][0];
            for (int i = 0; i < nb_cbk_search; ++i) {
                int idx = Lag_CB_ptr[k][i] - delta;
                for (j = 0; j < 5; ++j) {
                    Inlines.OpusAssert(idx + j < 22);
                    Inlines.OpusAssert(idx + j < lag_counter);
                    Inlines.MatrixGet((silk_pe_stage3_vals[])cross_corr_st3, (int)k, (int)i, (int)nb_cbk_search).Values[j] = scratch_mem[idx + j];
                }
            }
            target_ptr += sf_length;
        }
    }

    static void silk_P_Ana_calc_energy_st3(silk_pe_stage3_vals[] energies_st3, short[] frame, int start_lag, int sf_length, int nb_subfr, int complexity) {
        int nb_cbk_search;
        byte[][] Lag_CB_ptr;
        byte[][] Lag_range_ptr;
        Inlines.OpusAssert(complexity >= 0);
        Inlines.OpusAssert(complexity <= 2);
        if (nb_subfr == 4) {
            Lag_range_ptr = SilkTables.silk_Lag_range_stage3[complexity];
            Lag_CB_ptr = SilkTables.silk_CB_lags_stage3;
            nb_cbk_search = SilkTables.silk_nb_cbk_searchs_stage3[complexity];
        } else {
            Inlines.OpusAssert(nb_subfr == 2);
            Lag_range_ptr = SilkTables.silk_Lag_range_stage3_10_ms;
            Lag_CB_ptr = SilkTables.silk_CB_lags_stage3_10_ms;
            nb_cbk_search = 12;
        }
        int[] scratch_mem = new int[22];
        int target_ptr = Inlines.silk_LSHIFT(sf_length, 2);
        for (int k = 0; k < nb_subfr; ++k) {
            int i;
            int lag_counter = 0;
            int basis_ptr = target_ptr - (start_lag + Lag_range_ptr[k][0]);
            int energy = Inlines.silk_inner_prod_self(frame, basis_ptr, sf_length);
            Inlines.OpusAssert(energy >= 0);
            scratch_mem[lag_counter] = energy;
            ++lag_counter;
            int lag_diff = Lag_range_ptr[k][1] - Lag_range_ptr[k][0] + 1;
            for (i = 1; i < lag_diff; ++i) {
                Inlines.OpusAssert((energy -= Inlines.silk_SMULBB(frame[basis_ptr + sf_length - i], frame[basis_ptr + sf_length - i])) >= 0);
                energy = Inlines.silk_ADD_SAT32(energy, Inlines.silk_SMULBB(frame[basis_ptr - i], frame[basis_ptr - i]));
                Inlines.OpusAssert(energy >= 0);
                Inlines.OpusAssert(lag_counter < 22);
                scratch_mem[lag_counter] = energy;
                ++lag_counter;
            }
            byte delta = Lag_range_ptr[k][0];
            for (i = 0; i < nb_cbk_search; ++i) {
                int idx = Lag_CB_ptr[k][i] - delta;
                for (int j = 0; j < 5; ++j) {
                    Inlines.OpusAssert(idx + j < 22);
                    Inlines.OpusAssert(idx + j < lag_counter);
                    Inlines.MatrixGet((silk_pe_stage3_vals[])energies_st3, (int)k, (int)i, (int)nb_cbk_search).Values[j] = scratch_mem[idx + j];
                    Inlines.OpusAssert(Inlines.MatrixGet((silk_pe_stage3_vals[])energies_st3, (int)k, (int)i, (int)nb_cbk_search).Values[j] >= 0);
                }
            }
            target_ptr += sf_length;
        }
    }

    static class silk_pe_stage3_vals {
        public final int[] Values = new int[5];

        silk_pe_stage3_vals() {
        }
    }
}
