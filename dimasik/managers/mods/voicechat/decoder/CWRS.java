package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.CeltTables;
import dimasik.managers.mods.voicechat.decoder.EntropyCoder;
import dimasik.managers.mods.voicechat.decoder.Inlines;

class CWRS {
    static final int[] CELT_PVQ_U_ROW = new int[]{0, 176, 351, 525, 698, 870, 1041, 1131, 1178, 1207, 1226, 1240, 1248, 1254, 1257};

    CWRS() {
    }

    private static long CELT_PVQ_U(int _n, int _k) {
        return CeltTables.CELT_PVQ_U_DATA[CELT_PVQ_U_ROW[Inlines.IMIN(_n, _k)] + Inlines.IMAX(_n, _k)];
    }

    private static long CELT_PVQ_V(int _n, int _k) {
        return CWRS.CELT_PVQ_U(_n, _k) + CWRS.CELT_PVQ_U(_n, _k + 1);
    }

    static long icwrs(int _n, int[] _y) {
        Inlines.OpusAssert(_n >= 2);
        int j = _n - 1;
        long i = _y[j] < 0 ? 1L : 0L;
        int k = Inlines.abs(_y[j]);
        do {
            i += CWRS.CELT_PVQ_U(_n - --j, k);
            k += Inlines.abs(_y[j]);
            if (_y[j] >= 0) continue;
            i += CWRS.CELT_PVQ_U(_n - j, k + 1);
        } while (j > 0);
        return i;
    }

    static void encode_pulses(int[] _y, int _n, int _k, EntropyCoder _enc) {
        Inlines.OpusAssert(_k > 0);
        _enc.enc_uint(CWRS.icwrs(_n, _y), CWRS.CELT_PVQ_V(_n, _k));
    }

    static int cwrsi(int _n, int _k, long _i, int[] _y) {
        short val;
        int k0;
        int s;
        long p;
        int yy = 0;
        int y_ptr = 0;
        Inlines.OpusAssert(_k > 0);
        Inlines.OpusAssert(_n > 1);
        while (_n > 2) {
            if (_k >= _n) {
                int row = CELT_PVQ_U_ROW[_n];
                p = CeltTables.CELT_PVQ_U_DATA[row + _k + 1];
                s = 0 - (_i >= p ? 1 : 0);
                k0 = _k;
                q = CeltTables.CELT_PVQ_U_DATA[row + _n];
                if (q > (_i -= Inlines.CapToUInt32(p & (long)s))) {
                    Inlines.OpusAssert(p > q);
                    _k = _n;
                    while ((p = CeltTables.CELT_PVQ_U_DATA[CELT_PVQ_U_ROW[--_k] + _n]) > _i) {
                    }
                } else {
                    p = CeltTables.CELT_PVQ_U_DATA[row + _k];
                    while (p > _i) {
                        p = CeltTables.CELT_PVQ_U_DATA[row + --_k];
                    }
                }
                _i -= p;
                val = (short)(k0 - _k + s ^ s);
                _y[y_ptr++] = val;
                yy = Inlines.MAC16_16(yy, val, val);
            } else {
                p = CeltTables.CELT_PVQ_U_DATA[CELT_PVQ_U_ROW[_k] + _n];
                q = CeltTables.CELT_PVQ_U_DATA[CELT_PVQ_U_ROW[_k + 1] + _n];
                if (p <= _i && _i < q) {
                    _i -= p;
                    _y[y_ptr++] = 0;
                } else {
                    s = 0 - (_i >= q ? 1 : 0);
                    _i -= Inlines.CapToUInt32(q & (long)s);
                    k0 = _k;
                    while ((p = CeltTables.CELT_PVQ_U_DATA[CELT_PVQ_U_ROW[--_k] + _n]) > _i) {
                    }
                    _i -= p;
                    val = (short)(k0 - _k + s ^ s);
                    _y[y_ptr++] = val;
                    yy = Inlines.MAC16_16(yy, val, val);
                }
            }
            --_n;
        }
        p = 2L * (long)_k + 1L;
        s = 0 - (_i >= p ? 1 : 0);
        k0 = _k;
        _k = (int)((_i -= Inlines.CapToUInt32(p & (long)s)) + 1L >> 1);
        if (_k != 0) {
            _i -= 2L * (long)_k - 1L;
        }
        val = (short)(k0 - _k + s ^ s);
        _y[y_ptr++] = val;
        yy = Inlines.MAC16_16(yy, val, val);
        s = -((int)_i);
        val = (short)(_k + s ^ s);
        _y[y_ptr] = val;
        yy = Inlines.MAC16_16(yy, val, val);
        return yy;
    }

    static int decode_pulses(int[] _y, int _n, int _k, EntropyCoder _dec) {
        return CWRS.cwrsi(_n, _k, _dec.dec_uint(CWRS.CELT_PVQ_V(_n, _k)), _y);
    }
}
