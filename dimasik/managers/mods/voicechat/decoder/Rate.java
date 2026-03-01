package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.CeltMode;
import dimasik.managers.mods.voicechat.decoder.EntropyCoder;
import dimasik.managers.mods.voicechat.decoder.Inlines;

class Rate {
    private static final byte[] LOG2_FRAC_TABLE = new byte[]{0, 8, 13, 16, 19, 21, 23, 24, 26, 27, 28, 29, 30, 31, 32, 32, 33, 34, 34, 35, 36, 36, 37, 37};
    private static final int ALLOC_STEPS = 6;

    Rate() {
    }

    static int get_pulses(int i) {
        return i < 8 ? i : 8 + (i & 7) << (i >> 3) - 1;
    }

    static int bits2pulses(CeltMode m, int band, int LM, int bits) {
        short[] cache = m.cache.bits;
        short cache_ptr = m.cache.index[++LM * m.nbEBands + band];
        int lo = 0;
        int hi = cache[cache_ptr];
        --bits;
        for (int i = 0; i < 6; ++i) {
            int mid = lo + hi + 1 >> 1;
            if (cache[cache_ptr + mid] >= bits) {
                hi = mid;
                continue;
            }
            lo = mid;
        }
        if (bits - (lo == 0 ? -1 : cache[cache_ptr + lo]) <= cache[cache_ptr + hi] - bits) {
            return lo;
        }
        return hi;
    }

    static int pulses2bits(CeltMode m, int band, int LM, int pulses) {
        return pulses == 0 ? 0 : m.cache.bits[m.cache.index[++LM * m.nbEBands + band] + pulses] + 1;
    }

    static int interp_bits2pulses(CeltMode m, int start, int end, int skip_start, int[] bits1, int[] bits2, int[] thresh, int[] cap, int total, BoxedValueInt _balance, int skip_rsv, BoxedValueInt intensity, int intensity_rsv, BoxedValueInt dual_stereo, int dual_stereo_rsv, int[] bits, int[] ebits, int[] fine_priority, int C, int LM, EntropyCoder ec, int encode, int prev, int signalBandwidth) {
        int percoeff;
        int left;
        int tmp;
        int j;
        boolean done;
        int psum;
        int codedBands = -1;
        int alloc_floor = C << 3;
        int stereo = C > 1 ? 1 : 0;
        int logM = LM << 3;
        int lo = 0;
        int hi = 64;
        for (int i = 0; i < 6; ++i) {
            int mid = lo + hi >> 1;
            psum = 0;
            done = false;
            j = end;
            while (j-- > start) {
                int tmp2 = bits1[j] + (mid * bits2[j] >> 6);
                if (tmp2 >= thresh[j] || done) {
                    done = true;
                    psum += Inlines.IMIN(tmp2, cap[j]);
                    continue;
                }
                if (tmp2 < alloc_floor) continue;
                psum += alloc_floor;
            }
            if (psum > total) {
                hi = mid;
                continue;
            }
            lo = mid;
        }
        psum = 0;
        done = false;
        j = end;
        while (j-- > start) {
            tmp = bits1[j] + (lo * bits2[j] >> 6);
            if (tmp < thresh[j] && !done) {
                tmp = tmp >= alloc_floor ? alloc_floor : 0;
            } else {
                done = true;
            }
            bits[j] = tmp = Inlines.IMIN(tmp, cap[j]);
            psum += tmp;
        }
        codedBands = end;
        while (true) {
            int rem;
            int band_width;
            if ((j = codedBands - 1) <= skip_start) {
                total += skip_rsv;
                break;
            }
            left = total - psum;
            percoeff = Inlines.celt_udiv(left, m.eBands[codedBands] - m.eBands[start]);
            int band_bits = bits[j] + percoeff * (band_width = m.eBands[codedBands] - m.eBands[j]) + (rem = Inlines.IMAX((left -= (m.eBands[codedBands] - m.eBands[start]) * percoeff) - (m.eBands[j] - m.eBands[start]), 0));
            if (band_bits >= Inlines.IMAX(thresh[j], alloc_floor + 8)) {
                if (encode != 0) {
                    if (codedBands <= start + 2 || band_bits > (j < prev ? 7 : 9) * band_width << LM << 3 >> 4 && j <= signalBandwidth) {
                        ec.enc_bit_logp(1, 1);
                        break;
                    }
                    ec.enc_bit_logp(0, 1);
                } else if (ec.dec_bit_logp(1L) != 0) break;
                psum += 8;
                band_bits -= 8;
            }
            psum -= bits[j] + intensity_rsv;
            if (intensity_rsv > 0) {
                intensity_rsv = LOG2_FRAC_TABLE[j - start];
            }
            psum += intensity_rsv;
            if (band_bits >= alloc_floor) {
                psum += alloc_floor;
                bits[j] = alloc_floor;
            } else {
                bits[j] = 0;
            }
            --codedBands;
        }
        Inlines.OpusAssert(codedBands > start);
        if (intensity_rsv > 0) {
            if (encode != 0) {
                intensity.Val = Inlines.IMIN(intensity.Val, codedBands);
                ec.enc_uint(intensity.Val - start, codedBands + 1 - start);
            } else {
                intensity.Val = start + (int)ec.dec_uint(codedBands + 1 - start);
            }
        } else {
            intensity.Val = 0;
        }
        if (intensity.Val <= start) {
            total += dual_stereo_rsv;
            dual_stereo_rsv = 0;
        }
        if (dual_stereo_rsv > 0) {
            if (encode != 0) {
                ec.enc_bit_logp(dual_stereo.Val, 1);
            } else {
                dual_stereo.Val = ec.dec_bit_logp(1L);
            }
        } else {
            dual_stereo.Val = 0;
        }
        left = total - psum;
        percoeff = Inlines.celt_udiv(left, m.eBands[codedBands] - m.eBands[start]);
        left -= (m.eBands[codedBands] - m.eBands[start]) * percoeff;
        for (j = start; j < codedBands; ++j) {
            int n = j;
            bits[n] = bits[n] + percoeff * (m.eBands[j + 1] - m.eBands[j]);
        }
        j = start;
        while (j < codedBands) {
            tmp = Inlines.IMIN(left, m.eBands[j + 1] - m.eBands[j]);
            int n = j++;
            bits[n] = bits[n] + tmp;
            left -= tmp;
        }
        int balance = 0;
        for (j = start; j < codedBands; ++j) {
            int excess;
            Inlines.OpusAssert(bits[j] >= 0);
            int N0 = m.eBands[j + 1] - m.eBands[j];
            int N = N0 << LM;
            int bit = bits[j] + balance;
            if (N > 1) {
                excess = Inlines.MAX32(bit - cap[j], 0);
                bits[j] = bit - excess;
                int den = C * N + (C == 2 && N > 2 && dual_stereo.Val == 0 && j < intensity.Val ? 1 : 0);
                int NClogN = den * (m.logN[j] + logM);
                int offset = (NClogN >> 1) - den * 21;
                if (N == 2) {
                    offset += den << 3 >> 2;
                }
                if (bits[j] + offset < den * 2 << 3) {
                    offset += NClogN >> 2;
                } else if (bits[j] + offset < den * 3 << 3) {
                    offset += NClogN >> 3;
                }
                ebits[j] = Inlines.IMAX(0, bits[j] + offset + (den << 2));
                ebits[j] = Inlines.celt_udiv(ebits[j], den) >> 3;
                if (C * ebits[j] > bits[j] >> 3) {
                    ebits[j] = bits[j] >> stereo >> 3;
                }
                ebits[j] = Inlines.IMIN(ebits[j], 8);
                fine_priority[j] = ebits[j] * (den << 3) >= bits[j] + offset ? 1 : 0;
                int n = j;
                bits[n] = bits[n] - (C * ebits[j] << 3);
            } else {
                excess = Inlines.MAX32(0, bit - (C << 3));
                bits[j] = bit - excess;
                ebits[j] = 0;
                fine_priority[j] = 1;
            }
            if (excess > 0) {
                int extra_fine = Inlines.IMIN(excess >> stereo + 3, 8 - ebits[j]);
                int n = j;
                ebits[n] = ebits[n] + extra_fine;
                int extra_bits = extra_fine * C << 3;
                fine_priority[j] = extra_bits >= excess - balance ? 1 : 0;
                excess -= extra_bits;
            }
            balance = excess;
            Inlines.OpusAssert(bits[j] >= 0);
            Inlines.OpusAssert(ebits[j] >= 0);
        }
        _balance.Val = balance;
        while (j < end) {
            ebits[j] = bits[j] >> stereo >> 3;
            Inlines.OpusAssert(C * ebits[j] << 3 == bits[j]);
            bits[j] = 0;
            fine_priority[j] = ebits[j] < 1 ? 1 : 0;
            ++j;
        }
        return codedBands;
    }

    static int compute_allocation(CeltMode m, int start, int end, int[] offsets, int[] cap, int alloc_trim, BoxedValueInt intensity, BoxedValueInt dual_stereo, int total, BoxedValueInt balance, int[] pulses, int[] ebits, int[] fine_priority, int C, int LM, EntropyCoder ec, int encode, int prev, int signalBandwidth) {
        int j;
        total = Inlines.IMAX(total, 0);
        int len = m.nbEBands;
        int skip_start = start;
        int skip_rsv = total >= 8 ? 8 : 0;
        total -= skip_rsv;
        int dual_stereo_rsv = 0;
        int intensity_rsv = 0;
        if (C == 2) {
            intensity_rsv = LOG2_FRAC_TABLE[end - start];
            if (intensity_rsv > total) {
                intensity_rsv = 0;
            } else {
                dual_stereo_rsv = (total -= intensity_rsv) >= 8 ? 8 : 0;
                total -= dual_stereo_rsv;
            }
        }
        int[] bits1 = new int[len];
        int[] bits2 = new int[len];
        int[] thresh = new int[len];
        int[] trim_offset = new int[len];
        for (j = start; j < end; ++j) {
            thresh[j] = Inlines.IMAX(C << 3, 3 * (m.eBands[j + 1] - m.eBands[j]) << LM << 3 >> 4);
            trim_offset[j] = C * (m.eBands[j + 1] - m.eBands[j]) * (alloc_trim - 5 - LM) * (end - j - 1) * (1 << LM + 3) >> 6;
            if (m.eBands[j + 1] - m.eBands[j] << LM != 1) continue;
            int n = j;
            trim_offset[n] = trim_offset[n] - (C << 3);
        }
        int lo = 1;
        int hi = m.nbAllocVectors - 1;
        do {
            boolean done = false;
            int psum = 0;
            int mid = lo + hi >> 1;
            j = end;
            while (j-- > start) {
                int N = m.eBands[j + 1] - m.eBands[j];
                int bitsj = C * N * m.allocVectors[mid * len + j] << LM >> 2;
                if (bitsj > 0) {
                    bitsj = Inlines.IMAX(0, bitsj + trim_offset[j]);
                }
                if ((bitsj += offsets[j]) >= thresh[j] || done) {
                    done = true;
                    psum += Inlines.IMIN(bitsj, cap[j]);
                    continue;
                }
                if (bitsj < C << 3) continue;
                psum += C << 3;
            }
            if (psum > total) {
                hi = mid - 1;
                continue;
            }
            lo = mid + 1;
        } while (lo <= hi);
        hi = lo--;
        for (j = start; j < end; ++j) {
            int bits2j;
            int N = m.eBands[j + 1] - m.eBands[j];
            int bits1j = C * N * m.allocVectors[lo * len + j] << LM >> 2;
            int n = bits2j = hi >= m.nbAllocVectors ? cap[j] : C * N * m.allocVectors[hi * len + j] << LM >> 2;
            if (bits1j > 0) {
                bits1j = Inlines.IMAX(0, bits1j + trim_offset[j]);
            }
            if (bits2j > 0) {
                bits2j = Inlines.IMAX(0, bits2j + trim_offset[j]);
            }
            if (lo > 0) {
                bits1j += offsets[j];
            }
            bits2j += offsets[j];
            if (offsets[j] > 0) {
                skip_start = j;
            }
            bits2j = Inlines.IMAX(0, bits2j - bits1j);
            bits1[j] = bits1j;
            bits2[j] = bits2j;
        }
        int codedBands = Rate.interp_bits2pulses(m, start, end, skip_start, bits1, bits2, thresh, cap, total, balance, skip_rsv, intensity, intensity_rsv, dual_stereo, dual_stereo_rsv, pulses, ebits, fine_priority, C, LM, ec, encode, prev, signalBandwidth);
        return codedBands;
    }
}
