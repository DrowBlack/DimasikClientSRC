package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.EntropyCoder;
import dimasik.managers.mods.voicechat.decoder.Inlines;

class Laplace {
    private static final int LAPLACE_LOG_MINP = 0;
    private static final long LAPLACE_MINP = 1L;
    private static final int LAPLACE_NMIN = 16;

    Laplace() {
    }

    static long ec_laplace_get_freq1(long fs0, int decay) {
        long ft = Inlines.CapToUInt32(32736L - fs0);
        return Inlines.CapToUInt32(ft * (long)(16384 - decay)) >> 15;
    }

    static void ec_laplace_encode(EntropyCoder enc, BoxedValueInt value, long fs, int decay) {
        int val = value.Val;
        long fl = 0L;
        if (val != 0) {
            int i;
            int s = 0 - (val < 0 ? 1 : 0);
            val = val + s ^ s;
            fl = fs;
            fs = Laplace.ec_laplace_get_freq1(fs, decay);
            for (i = 1; fs > 0L && i < val; ++i) {
                fl = Inlines.CapToUInt32(fl + (fs *= 2L) + 2L);
                fs = Inlines.CapToUInt32(fs * (long)decay >> 15);
            }
            if (fs == 0L) {
                int ndi_max = (int)(32768L - fl + 1L - 1L) >> 0;
                ndi_max = ndi_max - s >> 1;
                int di = Inlines.IMIN(val - i, ndi_max - 1);
                fl = Inlines.CapToUInt32(fl + (long)(2 * di + 1 + s) * 1L);
                fs = Inlines.IMIN(1L, 32768L - fl);
                value.Val = i + di + s ^ s;
            } else {
                fl += Inlines.CapToUInt32(++fs & (long)(~s));
            }
            Inlines.OpusAssert(fl + fs <= 32768L);
            Inlines.OpusAssert(fs > 0L);
        }
        enc.encode_bin(fl, fl + fs, 15);
    }

    static int ec_laplace_decode(EntropyCoder dec, long fs, int decay) {
        int val = 0;
        long fm = dec.decode_bin(15);
        long fl = 0L;
        if (fm >= fs) {
            ++val;
            fl = fs;
            fs = Laplace.ec_laplace_get_freq1(fs, decay) + 1L;
            while (fs > 1L && fm >= fl + 2L * fs) {
                fl = Inlines.CapToUInt32(fl + (fs *= 2L));
                fs = Inlines.CapToUInt32((fs - 2L) * (long)decay >> 15);
                ++fs;
                ++val;
            }
            if (fs <= 1L) {
                int di = (int)(fm - fl) >> 1;
                val += di;
                fl = Inlines.CapToUInt32(fl + Inlines.CapToUInt32((long)(2 * di) * 1L));
            }
            if (fm < fl + fs) {
                val = -val;
            } else {
                fl = Inlines.CapToUInt32(fl + fs);
            }
        }
        Inlines.OpusAssert(fl < 32768L);
        Inlines.OpusAssert(fs > 0L);
        Inlines.OpusAssert(fl <= fm);
        Inlines.OpusAssert(fm < Inlines.IMIN(fl + fs, 32768L));
        dec.dec_update(fl, Inlines.IMIN(fl + fs, 32768L), 32768L);
        return val;
    }
}
