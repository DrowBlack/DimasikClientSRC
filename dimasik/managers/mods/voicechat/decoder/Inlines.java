package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.BoxedValueInt;
import dimasik.managers.mods.voicechat.decoder.PitchAnalysisCore;

class Inlines {
    private static short[] sqrt_C = new short[]{23175, 11561, -3011, 1699, -664};
    private static short log2_C0 = (short)-6793;

    Inlines() {
    }

    static void OpusAssert(boolean condition) {
        if (!condition) {
            throw new AssertionError();
        }
    }

    static void OpusAssert(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError((Object)message);
        }
    }

    static long CapToUInt32(long val) {
        return 0xFFFFFFFFL & (long)((int)val);
    }

    static long CapToUInt32(int val) {
        return val;
    }

    static int MULT16_16SU(int a, int b) {
        return (short)a * (b & 0xFFFF);
    }

    static int MULT16_32_Q16(short a, int b) {
        return Inlines.ADD32(Inlines.MULT16_16((int)a, Inlines.SHR(b, 16)), Inlines.SHR(Inlines.MULT16_16SU(a, b & 0xFFFF), 16));
    }

    static int MULT16_32_Q16(int a, int b) {
        return Inlines.ADD32(Inlines.MULT16_16(a, Inlines.SHR(b, 16)), Inlines.SHR(Inlines.MULT16_16SU(a, b & 0xFFFF), 16));
    }

    static int MULT16_32_P16(short a, int b) {
        return Inlines.ADD32(Inlines.MULT16_16((int)a, Inlines.SHR(b, 16)), Inlines.PSHR(Inlines.MULT16_16SU(a, b & 0xFFFF), 16));
    }

    static int MULT16_32_P16(int a, int b) {
        return Inlines.ADD32(Inlines.MULT16_16(a, Inlines.SHR(b, 16)), Inlines.PSHR(Inlines.MULT16_16SU(a, b & 0xFFFF), 16));
    }

    static int MULT16_32_Q15(short a, int b) {
        return (a * (b >> 16) << 1) + (a * (b & 0xFFFF) >> 15);
    }

    static int MULT16_32_Q15(int a, int b) {
        return (a * (b >> 16) << 1) + (a * (b & 0xFFFF) >> 15);
    }

    static int MULT32_32_Q31(int a, int b) {
        return Inlines.ADD32(Inlines.ADD32(Inlines.SHL(Inlines.MULT16_16(Inlines.SHR(a, 16), Inlines.SHR(b, 16)), 1), Inlines.SHR(Inlines.MULT16_16SU(Inlines.SHR(a, 16), b & 0xFFFF), 15)), Inlines.SHR(Inlines.MULT16_16SU(Inlines.SHR(b, 16), a & 0xFFFF), 15));
    }

    static short QCONST16(float x, int bits) {
        return (short)(0.5 + (double)(x * (float)(1 << bits)));
    }

    static int QCONST32(float x, int bits) {
        return (int)(0.5 + (double)(x * (float)(1 << bits)));
    }

    static short NEG16(short x) {
        return (short)(0 - x);
    }

    static int NEG16(int x) {
        return 0 - x;
    }

    static int NEG32(int x) {
        return 0 - x;
    }

    static short EXTRACT16(int x) {
        return (short)x;
    }

    static int EXTEND32(short x) {
        return x;
    }

    static int EXTEND32(int x) {
        return x;
    }

    static short SHR16(short a, int shift) {
        return (short)(a >> shift);
    }

    static int SHR16(int a, int shift) {
        return a >> shift;
    }

    static short SHL16(short a, int shift) {
        return (short)((a & 0xFFFF) << shift);
    }

    static int SHL16(int a, int shift) {
        return (int)(0xFFFFFFFFFFFFFFFFL & (long)a << shift);
    }

    static int SHR32(int a, int shift) {
        return a >> shift;
    }

    static int SHL32(int a, int shift) {
        return (int)(0xFFFFFFFFFFFFFFFFL & (long)a << shift);
    }

    static int PSHR32(int a, int shift) {
        return Inlines.SHR32(a + (Inlines.EXTEND32(1) << shift >> 1), shift);
    }

    static short PSHR16(short a, int shift) {
        return Inlines.SHR16((short)(a + (1 << shift >> 1)), shift);
    }

    static int PSHR16(int a, int shift) {
        return Inlines.SHR32(a + (1 << shift >> 1), shift);
    }

    static int VSHR32(int a, int shift) {
        return shift > 0 ? Inlines.SHR32(a, shift) : Inlines.SHL32(a, -shift);
    }

    private static int SHR(int a, int shift) {
        return a >> shift;
    }

    private static int SHL(int a, int shift) {
        return Inlines.SHL32(a, shift);
    }

    private static int SHR(short a, int shift) {
        return a >> shift;
    }

    private static int SHL(short a, int shift) {
        return Inlines.SHL32(a, shift);
    }

    private static int PSHR(int a, int shift) {
        return Inlines.SHR(a + (Inlines.EXTEND32(1) << shift >> 1), shift);
    }

    static int SATURATE(int x, int a) {
        return x > a ? a : (x < -a ? -a : x);
    }

    static short SATURATE16(int x) {
        return Inlines.EXTRACT16(x > Short.MAX_VALUE ? Short.MAX_VALUE : (x < Short.MIN_VALUE ? Short.MIN_VALUE : x));
    }

    static short ROUND16(short x, short a) {
        return Inlines.EXTRACT16(Inlines.PSHR32(x, a));
    }

    static int ROUND16(int x, int a) {
        return Inlines.PSHR32(x, a);
    }

    static int PDIV32(int a, int b) {
        return a / b;
    }

    static short HALF16(short x) {
        return Inlines.SHR16(x, 1);
    }

    static int HALF16(int x) {
        return Inlines.SHR32(x, 1);
    }

    static int HALF32(int x) {
        return Inlines.SHR32(x, 1);
    }

    static short ADD16(short a, short b) {
        return (short)(a + b);
    }

    static int ADD16(int a, int b) {
        return a + b;
    }

    static short SUB16(short a, short b) {
        return (short)(a - b);
    }

    static int SUB16(int a, int b) {
        return a - b;
    }

    static int ADD32(int a, int b) {
        return a + b;
    }

    static int SUB32(int a, int b) {
        return a - b;
    }

    static short MULT16_16_16(short a, short b) {
        return (short)(a * b);
    }

    static int MULT16_16_16(int a, int b) {
        return a * b;
    }

    static int MULT16_16(int a, int b) {
        return a * b;
    }

    static int MULT16_16(short a, short b) {
        return a * b;
    }

    static int MAC16_16(short c, short a, short b) {
        return c + a * b;
    }

    static int MAC16_16(int c, short a, short b) {
        return c + a * b;
    }

    static int MAC16_16(int c, int a, int b) {
        return c + a * b;
    }

    static int MAC16_32_Q15(int c, short a, short b) {
        return Inlines.ADD32(c, Inlines.ADD32(Inlines.MULT16_16((int)a, Inlines.SHR(b, 15)), Inlines.SHR(Inlines.MULT16_16((int)a, b & Short.MAX_VALUE), 15)));
    }

    static int MAC16_32_Q15(int c, int a, int b) {
        return Inlines.ADD32(c, Inlines.ADD32(Inlines.MULT16_16(a, Inlines.SHR(b, 15)), Inlines.SHR(Inlines.MULT16_16(a, b & Short.MAX_VALUE), 15)));
    }

    static int MAC16_32_Q16(int c, short a, short b) {
        return Inlines.ADD32(c, Inlines.ADD32(Inlines.MULT16_16((int)a, Inlines.SHR(b, 16)), Inlines.SHR(Inlines.MULT16_16SU(a, b & 0xFFFF), 16)));
    }

    static int MAC16_32_Q16(int c, int a, int b) {
        return Inlines.ADD32(c, Inlines.ADD32(Inlines.MULT16_16(a, Inlines.SHR(b, 16)), Inlines.SHR(Inlines.MULT16_16SU(a, b & 0xFFFF), 16)));
    }

    static int MULT16_16_Q11_32(short a, short b) {
        return Inlines.SHR(Inlines.MULT16_16(a, b), 11);
    }

    static int MULT16_16_Q11_32(int a, int b) {
        return Inlines.SHR(Inlines.MULT16_16(a, b), 11);
    }

    static short MULT16_16_Q11(short a, short b) {
        return (short)Inlines.SHR(Inlines.MULT16_16(a, b), 11);
    }

    static int MULT16_16_Q11(int a, int b) {
        return Inlines.SHR(Inlines.MULT16_16(a, b), 11);
    }

    static short MULT16_16_Q13(short a, short b) {
        return (short)Inlines.SHR(Inlines.MULT16_16(a, b), 13);
    }

    static int MULT16_16_Q13(int a, int b) {
        return Inlines.SHR(Inlines.MULT16_16(a, b), 13);
    }

    static short MULT16_16_Q14(short a, short b) {
        return (short)Inlines.SHR(Inlines.MULT16_16(a, b), 14);
    }

    static int MULT16_16_Q14(int a, int b) {
        return Inlines.SHR(Inlines.MULT16_16(a, b), 14);
    }

    static short MULT16_16_Q15(short a, short b) {
        return (short)Inlines.SHR(Inlines.MULT16_16(a, b), 15);
    }

    static int MULT16_16_Q15(int a, int b) {
        return Inlines.SHR(Inlines.MULT16_16(a, b), 15);
    }

    static short MULT16_16_P13(short a, short b) {
        return (short)Inlines.SHR(Inlines.ADD32(4096, Inlines.MULT16_16(a, b)), 13);
    }

    static int MULT16_16_P13(int a, int b) {
        return Inlines.SHR(Inlines.ADD32(4096, Inlines.MULT16_16(a, b)), 13);
    }

    static short MULT16_16_P14(short a, short b) {
        return (short)Inlines.SHR(Inlines.ADD32(8192, Inlines.MULT16_16(a, b)), 14);
    }

    static int MULT16_16_P14(int a, int b) {
        return Inlines.SHR(Inlines.ADD32(8192, Inlines.MULT16_16(a, b)), 14);
    }

    static short MULT16_16_P15(short a, short b) {
        return (short)Inlines.SHR(Inlines.ADD32(16384, Inlines.MULT16_16(a, b)), 15);
    }

    static int MULT16_16_P15(int a, int b) {
        return Inlines.SHR(Inlines.ADD32(16384, Inlines.MULT16_16(a, b)), 15);
    }

    static short DIV32_16(int a, short b) {
        return (short)(a / b);
    }

    static int DIV32_16(int a, int b) {
        return a / b;
    }

    static int DIV32(int a, int b) {
        return a / b;
    }

    static short SAT16(int x) {
        return (short)(x > Short.MAX_VALUE ? Short.MAX_VALUE : (short)(x < Short.MIN_VALUE ? Short.MIN_VALUE : (short)x));
    }

    static short SIG2WORD16(int x) {
        x = Inlines.PSHR32(x, 12);
        x = Inlines.MAX32(x, Short.MIN_VALUE);
        x = Inlines.MIN32(x, Short.MAX_VALUE);
        return Inlines.EXTRACT16(x);
    }

    static short MIN(short a, short b) {
        return a < b ? a : b;
    }

    static short MAX(short a, short b) {
        return a > b ? a : b;
    }

    static short MIN16(short a, short b) {
        return a < b ? a : b;
    }

    static short MAX16(short a, short b) {
        return a > b ? a : b;
    }

    static int MIN16(int a, int b) {
        return a < b ? a : b;
    }

    static int MAX16(int a, int b) {
        return a > b ? a : b;
    }

    static float MIN16(float a, float b) {
        return a < b ? a : b;
    }

    static float MAX16(float a, float b) {
        return a > b ? a : b;
    }

    static int MIN(int a, int b) {
        return a < b ? a : b;
    }

    static int MAX(int a, int b) {
        return a > b ? a : b;
    }

    static int IMIN(int a, int b) {
        return a < b ? a : b;
    }

    static long IMIN(long a, long b) {
        return a < b ? a : b;
    }

    static int IMAX(int a, int b) {
        return a > b ? a : b;
    }

    static int MIN32(int a, int b) {
        return a < b ? a : b;
    }

    static int MAX32(int a, int b) {
        return a > b ? a : b;
    }

    static float MIN32(float a, float b) {
        return a < b ? a : b;
    }

    static float MAX32(float a, float b) {
        return a > b ? a : b;
    }

    static int ABS16(int x) {
        return x < 0 ? -x : x;
    }

    static float ABS16(float x) {
        return x < 0.0f ? -x : x;
    }

    static short ABS16(short x) {
        return x < 0 ? -x : x;
    }

    static int ABS32(int x) {
        return x < 0 ? -x : x;
    }

    static int celt_udiv(int n, int d) {
        Inlines.OpusAssert(d > 0);
        return n / d;
    }

    static int celt_sudiv(int n, int d) {
        Inlines.OpusAssert(d > 0);
        return n / d;
    }

    static int celt_div(int a, int b) {
        return Inlines.MULT32_32_Q31(a, Inlines.celt_rcp(b));
    }

    static int celt_ilog2(int x) {
        Inlines.OpusAssert(x > 0, "celt_ilog2() only defined for strictly positive numbers");
        return Inlines.EC_ILOG(x) - 1;
    }

    static int celt_zlog2(int x) {
        return x <= 0 ? 0 : Inlines.celt_ilog2(x);
    }

    static int celt_maxabs16(int[] x, int x_ptr, int len) {
        int maxval = 0;
        int minval = 0;
        for (int i = x_ptr; i < len + x_ptr; ++i) {
            maxval = Inlines.MAX32(maxval, x[i]);
            minval = Inlines.MIN32(minval, x[i]);
        }
        return Inlines.MAX32(Inlines.EXTEND32(maxval), -Inlines.EXTEND32(minval));
    }

    static int celt_maxabs32(int[] x, int x_ptr, int len) {
        int maxval = 0;
        int minval = 0;
        for (int i = x_ptr; i < x_ptr + len; ++i) {
            maxval = Inlines.MAX32(maxval, x[i]);
            minval = Inlines.MIN32(minval, x[i]);
        }
        return Inlines.MAX32(maxval, 0 - minval);
    }

    static short celt_maxabs32(short[] x, int x_ptr, int len) {
        short maxval = 0;
        short minval = 0;
        for (int i = x_ptr; i < x_ptr + len; ++i) {
            maxval = Inlines.MAX16(maxval, x[i]);
            minval = Inlines.MIN16(minval, x[i]);
        }
        return Inlines.MAX(maxval, (short)(0 - minval));
    }

    static int FRAC_MUL16(int a, int b) {
        return 16384 + (short)a * (short)b >> 15;
    }

    static int isqrt32(long _val) {
        int g = 0;
        int bshift = Inlines.EC_ILOG(_val) - 1 >> 1;
        int b = 1 << bshift;
        do {
            long t;
            if ((t = (long)((g << 1) + b << bshift)) <= _val) {
                g += b;
                _val -= t;
            }
            b >>= 1;
        } while (--bshift >= 0);
        return g;
    }

    static int celt_sqrt(int x) {
        if (x == 0) {
            return 0;
        }
        if (x >= 0x40000000) {
            return Short.MAX_VALUE;
        }
        int k = (Inlines.celt_ilog2(x) >> 1) - 7;
        x = Inlines.VSHR32(x, 2 * k);
        short n = (short)(x - 32768);
        int rt = Inlines.ADD16(sqrt_C[0], Inlines.MULT16_16_Q15(n, Inlines.ADD16(sqrt_C[1], Inlines.MULT16_16_Q15(n, Inlines.ADD16(sqrt_C[2], Inlines.MULT16_16_Q15(n, Inlines.ADD16(sqrt_C[3], Inlines.MULT16_16_Q15(n, sqrt_C[4]))))))));
        rt = Inlines.VSHR32(rt, 7 - k);
        return rt;
    }

    static int celt_rcp(int x) {
        Inlines.OpusAssert(x > 0, "celt_rcp() only defined for positive values");
        int i = Inlines.celt_ilog2(x);
        int n = Inlines.VSHR32(x, i - 15) - 32768;
        int r = Inlines.ADD16(30840, Inlines.MULT16_16_Q15(-15420, n));
        r = Inlines.SUB16(r, Inlines.MULT16_16_Q15(r, Inlines.ADD16(Inlines.MULT16_16_Q15(r, n), Inlines.ADD16(r, Short.MIN_VALUE))));
        r = Inlines.SUB16(r, Inlines.ADD16(1, Inlines.MULT16_16_Q15(r, Inlines.ADD16(Inlines.MULT16_16_Q15(r, n), Inlines.ADD16(r, Short.MIN_VALUE)))));
        return Inlines.VSHR32(Inlines.EXTEND32(r), i - 16);
    }

    static int celt_rsqrt_norm(int x) {
        int n = x - 32768;
        int r = Inlines.ADD16(23557, Inlines.MULT16_16_Q15(n, Inlines.ADD16(-13490, Inlines.MULT16_16_Q15(n, 6713))));
        int r2 = Inlines.MULT16_16_Q15(r, r);
        int y = Inlines.SHL16(Inlines.SUB16(Inlines.ADD16(Inlines.MULT16_16_Q15(r2, n), r2), 16384), 1);
        return Inlines.ADD16(r, Inlines.MULT16_16_Q15(r, Inlines.MULT16_16_Q15(y, Inlines.SUB16(Inlines.MULT16_16_Q15(y, 12288), 16384))));
    }

    static int frac_div32(int a, int b) {
        int shift = Inlines.celt_ilog2(b) - 29;
        a = Inlines.VSHR32(a, shift);
        b = Inlines.VSHR32(b, shift);
        int rcp = Inlines.ROUND16(Inlines.celt_rcp(Inlines.ROUND16(b, 16)), 3);
        int result = Inlines.MULT16_32_Q15(rcp, a);
        int rem = Inlines.PSHR32(a, 2) - Inlines.MULT32_32_Q31(result, b);
        if ((result = Inlines.ADD32(result, Inlines.SHL32(Inlines.MULT16_32_Q15(rcp, rem), 2))) >= 0x20000000) {
            return Integer.MAX_VALUE;
        }
        if (result <= -536870912) {
            return -2147483647;
        }
        return Inlines.SHL32(result, 2);
    }

    static int celt_log2(int x) {
        if (x == 0) {
            return -32767;
        }
        int i = Inlines.celt_ilog2(x);
        int n = Inlines.VSHR32(x, i - 15) - 32768 - 16384;
        int frac = Inlines.ADD16((int)log2_C0, Inlines.MULT16_16_Q15(n, Inlines.ADD16(15746, Inlines.MULT16_16_Q15(n, Inlines.ADD16(-5217, Inlines.MULT16_16_Q15(n, Inlines.ADD16(2545, Inlines.MULT16_16_Q15(n, -1401))))))));
        return Inlines.SHL16((short)(i - 13), 10) + Inlines.SHR16(frac, 4);
    }

    static int celt_exp2_frac(int x) {
        int frac = Inlines.SHL16(x, 4);
        return Inlines.ADD16(16383, Inlines.MULT16_16_Q15(frac, Inlines.ADD16(22804, Inlines.MULT16_16_Q15(frac, Inlines.ADD16(14819, Inlines.MULT16_16_Q15(10204, frac))))));
    }

    static int celt_exp2(int x) {
        int integer = Inlines.SHR16(x, 10);
        if (integer > 14) {
            return 0x7F000000;
        }
        if (integer < -15) {
            return 0;
        }
        short frac = (short)Inlines.celt_exp2_frac((short)(x - Inlines.SHL16((short)integer, 10)));
        return Inlines.VSHR32(Inlines.EXTEND32((int)frac), -integer - 2);
    }

    static int celt_atan01(int x) {
        return Inlines.MULT16_16_P15(x, Inlines.ADD32(Short.MAX_VALUE, Inlines.MULT16_16_P15(x, Inlines.ADD32(-21, Inlines.MULT16_16_P15(x, Inlines.ADD32(-11943, Inlines.MULT16_16_P15(4936, x)))))));
    }

    static int celt_atan2p(int y, int x) {
        if (y < x) {
            int arg = Inlines.celt_div(Inlines.SHL32(Inlines.EXTEND32(y), 15), x);
            if (arg >= Short.MAX_VALUE) {
                arg = Short.MAX_VALUE;
            }
            return Inlines.SHR32(Inlines.celt_atan01(Inlines.EXTRACT16(arg)), 1);
        }
        int arg = Inlines.celt_div(Inlines.SHL32(Inlines.EXTEND32(x), 15), y);
        if (arg >= Short.MAX_VALUE) {
            arg = Short.MAX_VALUE;
        }
        return 25736 - Inlines.SHR16(Inlines.celt_atan01(Inlines.EXTRACT16(arg)), 1);
    }

    static int celt_cos_norm(int x) {
        if ((x &= 0x1FFFF) > Inlines.SHL32(Inlines.EXTEND32(1), 16)) {
            x = Inlines.SUB32(Inlines.SHL32(Inlines.EXTEND32(1), 17), x);
        }
        if ((x & Short.MAX_VALUE) != 0) {
            if (x < Inlines.SHL32(Inlines.EXTEND32(1), 15)) {
                return Inlines._celt_cos_pi_2(Inlines.EXTRACT16(x));
            }
            return Inlines.NEG32(Inlines._celt_cos_pi_2(Inlines.EXTRACT16(65536 - x)));
        }
        if ((x & 0xFFFF) != 0) {
            return 0;
        }
        if ((x & 0x1FFFF) != 0) {
            return -32767;
        }
        return Short.MAX_VALUE;
    }

    static int _celt_cos_pi_2(int x) {
        int x2 = Inlines.MULT16_16_P15(x, x);
        return Inlines.ADD32(1, Inlines.MIN32(32766, Inlines.ADD32(Inlines.SUB16(Short.MAX_VALUE, x2), Inlines.MULT16_16_P15(x2, Inlines.ADD32(-7651, Inlines.MULT16_16_P15(x2, Inlines.ADD32(8277, Inlines.MULT16_16_P15(-626, x2))))))));
    }

    static short FLOAT2INT16(float x) {
        if ((x *= 32768.0f) < -32768.0f) {
            x = -32768.0f;
        }
        if (x > 32767.0f) {
            x = 32767.0f;
        }
        return (short)x;
    }

    static int silk_ROR32(int a32, int rot) {
        int m = 0 - rot;
        if (rot == 0) {
            return a32;
        }
        if (rot < 0) {
            return a32 << m | a32 >> 32 - m;
        }
        return a32 << 32 - rot | a32 >> rot;
    }

    static int silk_MUL(int a32, int b32) {
        int ret = a32 * b32;
        return ret;
    }

    static int silk_MLA(int a32, int b32, int c32) {
        int ret = Inlines.silk_ADD32(a32, b32 * c32);
        Inlines.OpusAssert((long)ret == (long)a32 + (long)b32 * (long)c32);
        return ret;
    }

    static int silk_SMULTT(int a32, int b32) {
        return (a32 >> 16) * (b32 >> 16);
    }

    static int silk_SMLATT(int a32, int b32, int c32) {
        return Inlines.silk_ADD32(a32, (b32 >> 16) * (c32 >> 16));
    }

    static long silk_SMLALBB(long a64, short b16, short c16) {
        return Inlines.silk_ADD64(a64, b16 * c16);
    }

    static long silk_SMULL(int a32, int b32) {
        return (long)a32 * (long)b32;
    }

    static int silk_ADD32_ovflw(int a, int b) {
        return (int)((long)a + (long)b);
    }

    static int silk_ADD32_ovflw(long a, long b) {
        return (int)(a + b);
    }

    static int silk_SUB32_ovflw(int a, int b) {
        return (int)((long)a - (long)b);
    }

    static int silk_MLA_ovflw(int a32, int b32, int c32) {
        return Inlines.silk_ADD32_ovflw((long)a32, (long)b32 * (long)c32);
    }

    static int silk_SMLABB_ovflw(int a32, int b32, int c32) {
        return Inlines.silk_ADD32_ovflw(a32, (short)b32 * (short)c32);
    }

    static int silk_SMULBB(int a32, int b32) {
        return (short)a32 * (short)b32;
    }

    static int silk_SMULWB(int a32, int b32) {
        return (int)((long)a32 * (long)((short)b32) >> 16);
    }

    static int silk_SMLABB(int a32, int b32, int c32) {
        return a32 + (short)b32 * (short)c32;
    }

    static int silk_DIV32_16(int a32, int b32) {
        return a32 / b32;
    }

    static int silk_DIV32(int a32, int b32) {
        return a32 / b32;
    }

    static short silk_ADD16(short a, short b) {
        short ret = (short)(a + b);
        return ret;
    }

    static int silk_ADD32(int a, int b) {
        int ret = a + b;
        return ret;
    }

    static long silk_ADD64(long a, long b) {
        long ret = a + b;
        Inlines.OpusAssert(ret == Inlines.silk_ADD_SAT64(a, b));
        return ret;
    }

    static short silk_SUB16(short a, short b) {
        short ret = (short)(a - b);
        Inlines.OpusAssert(ret == Inlines.silk_SUB_SAT16(a, b));
        return ret;
    }

    static int silk_SUB32(int a, int b) {
        int ret = a - b;
        Inlines.OpusAssert(ret == Inlines.silk_SUB_SAT32(a, b));
        return ret;
    }

    static long silk_SUB64(long a, long b) {
        long ret = a - b;
        Inlines.OpusAssert(ret == Inlines.silk_SUB_SAT64(a, b));
        return ret;
    }

    static int silk_SAT8(int a) {
        return a > 127 ? 127 : (a < -128 ? -128 : a);
    }

    static int silk_SAT16(int a) {
        return a > Short.MAX_VALUE ? Short.MAX_VALUE : (a < Short.MIN_VALUE ? Short.MIN_VALUE : a);
    }

    static int silk_SAT32(long a) {
        return a > Integer.MAX_VALUE ? Integer.MAX_VALUE : (a < Integer.MIN_VALUE ? Integer.MIN_VALUE : (int)a);
    }

    static short silk_ADD_SAT16(short a16, short b16) {
        short res = (short)Inlines.silk_SAT16(Inlines.silk_ADD32(a16, b16));
        Inlines.OpusAssert(res == Inlines.silk_SAT16(a16 + b16));
        return res;
    }

    static int silk_ADD_SAT32(int a32, int b32) {
        int res = ((long)a32 + (long)b32 & Integer.MIN_VALUE) == 0L ? ((a32 & b32 & Integer.MIN_VALUE) != 0 ? Integer.MIN_VALUE : a32 + b32) : (((a32 | b32) & Integer.MIN_VALUE) == 0 ? Integer.MAX_VALUE : a32 + b32);
        Inlines.OpusAssert(res == Inlines.silk_SAT32((long)a32 + (long)b32));
        return res;
    }

    static long silk_ADD_SAT64(long a64, long b64) {
        long res = (a64 + b64 & Long.MIN_VALUE) == 0L ? ((a64 & b64 & Long.MIN_VALUE) != 0L ? Long.MIN_VALUE : a64 + b64) : (((a64 | b64) & Long.MIN_VALUE) == 0L ? Long.MAX_VALUE : a64 + b64);
        return res;
    }

    static short silk_SUB_SAT16(short a16, short b16) {
        short res = (short)Inlines.silk_SAT16(Inlines.silk_SUB32(a16, b16));
        Inlines.OpusAssert(res == Inlines.silk_SAT16(a16 - b16));
        return res;
    }

    static int silk_SUB_SAT32(int a32, int b32) {
        int res = ((long)a32 - (long)b32 & Integer.MIN_VALUE) == 0L ? ((a32 & (b32 ^ Integer.MIN_VALUE) & Integer.MIN_VALUE) != 0 ? Integer.MIN_VALUE : a32 - b32) : (((a32 ^ Integer.MIN_VALUE) & b32 & Integer.MIN_VALUE) != 0 ? Integer.MAX_VALUE : a32 - b32);
        Inlines.OpusAssert(res == Inlines.silk_SAT32((long)a32 - (long)b32));
        return res;
    }

    static long silk_SUB_SAT64(long a64, long b64) {
        long res = (a64 - b64 & Long.MIN_VALUE) == 0L ? ((a64 & (b64 ^ Long.MIN_VALUE) & Long.MIN_VALUE) != 0L ? Long.MIN_VALUE : a64 - b64) : (((a64 ^ Long.MIN_VALUE) & b64 & Long.MIN_VALUE) != 0L ? Long.MAX_VALUE : a64 - b64);
        return res;
    }

    static byte silk_ADD_POS_SAT8(byte a, byte b) {
        return (byte)((a + b & 0x80) != 0 ? 127 : a + b);
    }

    static short silk_ADD_POS_SAT16(short a, short b) {
        return (short)((a + b & 0x8000) != 0 ? Short.MAX_VALUE : a + b);
    }

    static int silk_ADD_POS_SAT32(int a, int b) {
        return (a + b & Integer.MIN_VALUE) != 0 ? Integer.MAX_VALUE : a + b;
    }

    static long silk_ADD_POS_SAT64(long a, long b) {
        return (a + b & Long.MIN_VALUE) != 0L ? Long.MAX_VALUE : a + b;
    }

    static byte silk_LSHIFT8(byte a, int shift) {
        byte ret = (byte)(a << shift);
        return ret;
    }

    static short silk_LSHIFT16(short a, int shift) {
        short ret = (short)(a << shift);
        return ret;
    }

    static int silk_LSHIFT32(int a, int shift) {
        int ret = a << shift;
        return ret;
    }

    static long silk_LSHIFT64(long a, int shift) {
        long ret = a << shift;
        return ret;
    }

    static int silk_LSHIFT(int a, int shift) {
        int ret = a << shift;
        return ret;
    }

    static int silk_LSHIFT_ovflw(int a, int shift) {
        return a << shift;
    }

    static int silk_LSHIFT_SAT32(int a, int shift) {
        return Inlines.silk_LSHIFT32(Inlines.silk_LIMIT(a, Inlines.silk_RSHIFT32(Integer.MIN_VALUE, shift), Inlines.silk_RSHIFT32(Integer.MAX_VALUE, shift)), shift);
    }

    static byte silk_RSHIFT8(byte a, int shift) {
        return (byte)(a >> shift);
    }

    static short silk_RSHIFT16(short a, int shift) {
        return (short)(a >> shift);
    }

    static int silk_RSHIFT32(int a, int shift) {
        return a >> shift;
    }

    static int silk_RSHIFT(int a, int shift) {
        return a >> shift;
    }

    static long silk_RSHIFT64(long a, int shift) {
        return a >> shift;
    }

    static long silk_RSHIFT_uint(long a, int shift) {
        return Inlines.CapToUInt32(a) >> shift;
    }

    static int silk_ADD_LSHIFT(int a, int b, int shift) {
        int ret = a + (b << shift);
        return ret;
    }

    static int silk_ADD_LSHIFT32(int a, int b, int shift) {
        int ret = a + (b << shift);
        return ret;
    }

    static int silk_ADD_RSHIFT(int a, int b, int shift) {
        int ret = a + (b >> shift);
        return ret;
    }

    static int silk_ADD_RSHIFT32(int a, int b, int shift) {
        int ret = a + (b >> shift);
        return ret;
    }

    static long silk_ADD_RSHIFT_uint(long a, long b, int shift) {
        long ret = Inlines.CapToUInt32(a + (Inlines.CapToUInt32(b) >> shift));
        return ret;
    }

    static int silk_SUB_LSHIFT32(int a, int b, int shift) {
        int ret = a - (b << shift);
        return ret;
    }

    static int silk_SUB_RSHIFT32(int a, int b, int shift) {
        int ret = a - (b >> shift);
        return ret;
    }

    static int silk_RSHIFT_ROUND(int a, int shift) {
        int ret = shift == 1 ? (a >> 1) + (a & 1) : (a >> shift - 1) + 1 >> 1;
        return ret;
    }

    static long silk_RSHIFT_ROUND64(long a, int shift) {
        long ret = shift == 1 ? (a >> 1) + (a & 1L) : (a >> shift - 1) + 1L >> 1;
        return ret;
    }

    static int silk_min(int a, int b) {
        return a < b ? a : b;
    }

    static int silk_max(int a, int b) {
        return a > b ? a : b;
    }

    static float silk_min(float a, float b) {
        return a < b ? a : b;
    }

    static float silk_max(float a, float b) {
        return a > b ? a : b;
    }

    static int SILK_CONST(float number, int scale) {
        return (int)((double)(number * (float)(1L << scale)) + 0.5);
    }

    static int silk_min_int(int a, int b) {
        return a < b ? a : b;
    }

    static short silk_min_16(short a, short b) {
        return a < b ? a : b;
    }

    static int silk_min_32(int a, int b) {
        return a < b ? a : b;
    }

    static long silk_min_64(long a, long b) {
        return a < b ? a : b;
    }

    static int silk_max_int(int a, int b) {
        return a > b ? a : b;
    }

    static short silk_max_16(short a, short b) {
        return a > b ? a : b;
    }

    static int silk_max_32(int a, int b) {
        return a > b ? a : b;
    }

    static long silk_max_64(long a, long b) {
        return a > b ? a : b;
    }

    static float silk_LIMIT(float a, float limit1, float limit2) {
        return limit1 > limit2 ? (a > limit1 ? limit1 : (a < limit2 ? limit2 : a)) : (a > limit2 ? limit2 : (a < limit1 ? limit1 : a));
    }

    static int silk_LIMIT(int a, int limit1, int limit2) {
        return Inlines.silk_LIMIT_32(a, limit1, limit2);
    }

    static int silk_LIMIT_int(int a, int limit1, int limit2) {
        return Inlines.silk_LIMIT_32(a, limit1, limit2);
    }

    static short silk_LIMIT_16(short a, short limit1, short limit2) {
        return limit1 > limit2 ? (a > limit1 ? limit1 : (a < limit2 ? limit2 : a)) : (a > limit2 ? limit2 : (a < limit1 ? limit1 : a));
    }

    static int silk_LIMIT_32(int a, int limit1, int limit2) {
        return limit1 > limit2 ? (a > limit1 ? limit1 : (a < limit2 ? limit2 : a)) : (a > limit2 ? limit2 : (a < limit1 ? limit1 : a));
    }

    static int silk_abs(int a) {
        return a > 0 ? a : -a;
    }

    static int silk_abs_int16(int a) {
        return (a ^ a >> 15) - (a >> 15);
    }

    static int silk_abs_int32(int a) {
        return (a ^ a >> 31) - (a >> 31);
    }

    static long silk_abs_int64(long a) {
        return a > 0L ? a : -a;
    }

    static long silk_sign(int a) {
        return a > 0 ? 1L : (long)(a < 0 ? -1 : 0);
    }

    static int silk_RAND(int seed) {
        return Inlines.silk_MLA_ovflw(907633515, seed, 196314165);
    }

    static int silk_SMMUL(int a32, int b32) {
        return (int)Inlines.silk_RSHIFT64(Inlines.silk_SMULL(a32, b32), 32);
    }

    static int silk_SMLAWT(int a32, int b32, int c32) {
        int ret = a32 + (b32 >> 16) * (c32 >> 16) + ((b32 & 0xFFFF) * (c32 >> 16) >> 16);
        return ret;
    }

    static int silk_DIV32_varQ(int a32, int b32, int Qres) {
        Inlines.OpusAssert(b32 != 0);
        Inlines.OpusAssert(Qres >= 0);
        int a_headrm = Inlines.silk_CLZ32(Inlines.silk_abs(a32)) - 1;
        int a32_nrm = Inlines.silk_LSHIFT(a32, a_headrm);
        int b_headrm = Inlines.silk_CLZ32(Inlines.silk_abs(b32)) - 1;
        int b32_nrm = Inlines.silk_LSHIFT(b32, b_headrm);
        int b32_inv = Inlines.silk_DIV32_16(0x1FFFFFFF, Inlines.silk_RSHIFT(b32_nrm, 16));
        int result = Inlines.silk_SMULWB(a32_nrm, b32_inv);
        a32_nrm = Inlines.silk_SUB32_ovflw(a32_nrm, Inlines.silk_LSHIFT_ovflw(Inlines.silk_SMMUL(b32_nrm, result), 3));
        result = Inlines.silk_SMLAWB(result, a32_nrm, b32_inv);
        int lshift = 29 + a_headrm - b_headrm - Qres;
        if (lshift < 0) {
            return Inlines.silk_LSHIFT_SAT32(result, -lshift);
        }
        if (lshift < 32) {
            return Inlines.silk_RSHIFT(result, lshift);
        }
        return 0;
    }

    static int silk_INVERSE32_varQ(int b32, int Qres) {
        Inlines.OpusAssert(b32 != 0);
        Inlines.OpusAssert(Qres > 0);
        int b_headrm = Inlines.silk_CLZ32(Inlines.silk_abs(b32)) - 1;
        int b32_nrm = Inlines.silk_LSHIFT(b32, b_headrm);
        int b32_inv = Inlines.silk_DIV32_16(0x1FFFFFFF, (short)Inlines.silk_RSHIFT(b32_nrm, 16));
        int result = Inlines.silk_LSHIFT(b32_inv, 16);
        int err_Q32 = Inlines.silk_LSHIFT(0x20000000 - Inlines.silk_SMULWB(b32_nrm, b32_inv), 3);
        result = Inlines.silk_SMLAWW(result, err_Q32, b32_inv);
        int lshift = 61 - b_headrm - Qres;
        if (lshift <= 0) {
            return Inlines.silk_LSHIFT_SAT32(result, -lshift);
        }
        if (lshift < 32) {
            return Inlines.silk_RSHIFT(result, lshift);
        }
        return 0;
    }

    static int silk_SMLAWB(int a32, int b32, int c32) {
        int ret = a32 + Inlines.silk_SMULWB(b32, c32);
        return ret;
    }

    static int silk_SMULWT(int a32, int b32) {
        return (a32 >> 16) * (b32 >> 16) + ((a32 & 0xFFFF) * (b32 >> 16) >> 16);
    }

    static int silk_SMULBT(int a32, int b32) {
        return (short)a32 * (b32 >> 16);
    }

    static int silk_SMLABT(int a32, int b32, int c32) {
        return a32 + (short)b32 * (c32 >> 16);
    }

    static long silk_SMLAL(long a64, int b32, int c32) {
        return Inlines.silk_ADD64(a64, (long)b32 * (long)c32);
    }

    static int MatrixGetPointer(int row, int column, int N) {
        return row * N + column;
    }

    static int MatrixGet(int[] Matrix_base_adr, int row, int column, int N) {
        return Matrix_base_adr[row * N + column];
    }

    static short MatrixGet(short[] Matrix_base_adr, int row, int column, int N) {
        return Matrix_base_adr[row * N + column];
    }

    static PitchAnalysisCore.silk_pe_stage3_vals MatrixGet(PitchAnalysisCore.silk_pe_stage3_vals[] Matrix_base_adr, int row, int column, int N) {
        return Matrix_base_adr[row * N + column];
    }

    static int MatrixGet(int[] Matrix_base_adr, int matrix_ptr, int row, int column, int N) {
        return Matrix_base_adr[matrix_ptr + row * N + column];
    }

    static short MatrixGet(short[] Matrix_base_adr, int matrix_ptr, int row, int column, int N) {
        return Matrix_base_adr[matrix_ptr + row * N + column];
    }

    static void MatrixSet(int[] Matrix_base_adr, int matrix_ptr, int row, int column, int N, int value) {
        Matrix_base_adr[matrix_ptr + row * N + column] = value;
    }

    static void MatrixSet(short[] Matrix_base_adr, int matrix_ptr, int row, int column, int N, short value) {
        Matrix_base_adr[matrix_ptr + row * N + column] = value;
    }

    static void MatrixSet(int[] Matrix_base_adr, int row, int column, int N, int value) {
        Matrix_base_adr[row * N + column] = value;
    }

    static void MatrixSet(short[] Matrix_base_adr, int row, int column, int N, short value) {
        Matrix_base_adr[row * N + column] = value;
    }

    static int silk_SMULWW(int a32, int b32) {
        return Inlines.silk_MLA(Inlines.silk_SMULWB(a32, b32), a32, Inlines.silk_RSHIFT_ROUND(b32, 16));
    }

    static int silk_SMLAWW(int a32, int b32, int c32) {
        return Inlines.silk_MLA(Inlines.silk_SMLAWB(a32, b32, c32), b32, Inlines.silk_RSHIFT_ROUND(c32, 16));
    }

    static int silk_CLZ64(long input) {
        int in_upper = (int)Inlines.silk_RSHIFT64(input, 32);
        if (in_upper == 0) {
            return 32 + Inlines.silk_CLZ32((int)input);
        }
        return Inlines.silk_CLZ32(in_upper);
    }

    static int silk_CLZ32(int in32) {
        return in32 == 0 ? 32 : 32 - Inlines.EC_ILOG(in32);
    }

    static void silk_CLZ_FRAC(int input, BoxedValueInt lz, BoxedValueInt frac_Q7) {
        int lzeros;
        lz.Val = lzeros = Inlines.silk_CLZ32(input);
        frac_Q7.Val = Inlines.silk_ROR32(input, 24 - lzeros) & 0x7F;
    }

    static int silk_SQRT_APPROX(int x) {
        if (x <= 0) {
            return 0;
        }
        BoxedValueInt boxed_lz = new BoxedValueInt(0);
        BoxedValueInt boxed_frac_Q7 = new BoxedValueInt(0);
        Inlines.silk_CLZ_FRAC(x, boxed_lz, boxed_frac_Q7);
        int lz = boxed_lz.Val;
        int frac_Q7 = boxed_frac_Q7.Val;
        int y = (lz & 1) != 0 ? 32768 : 46214;
        y >>= Inlines.silk_RSHIFT(lz, 1);
        y = Inlines.silk_SMLAWB(y, y, Inlines.silk_SMULBB(213, frac_Q7));
        return y;
    }

    static int MUL32_FRAC_Q(int a32, int b32, int Q) {
        return (int)Inlines.silk_RSHIFT_ROUND64(Inlines.silk_SMULL(a32, b32), Q);
    }

    static int silk_lin2log(int inLin) {
        BoxedValueInt lz = new BoxedValueInt(0);
        BoxedValueInt frac_Q7 = new BoxedValueInt(0);
        Inlines.silk_CLZ_FRAC(inLin, lz, frac_Q7);
        return Inlines.silk_LSHIFT(31 - lz.Val, 7) + Inlines.silk_SMLAWB(frac_Q7.Val, Inlines.silk_MUL(frac_Q7.Val, 128 - frac_Q7.Val), 179);
    }

    static int silk_log2lin(int inLog_Q7) {
        if (inLog_Q7 < 0) {
            return 0;
        }
        if (inLog_Q7 >= 3967) {
            return Integer.MAX_VALUE;
        }
        int output = Inlines.silk_LSHIFT(1, Inlines.silk_RSHIFT(inLog_Q7, 7));
        int frac_Q7 = inLog_Q7 & 0x7F;
        output = inLog_Q7 < 2048 ? Inlines.silk_ADD_RSHIFT32(output, Inlines.silk_MUL(output, Inlines.silk_SMLAWB(frac_Q7, Inlines.silk_SMULBB(frac_Q7, 128 - frac_Q7), -174)), 7) : Inlines.silk_MLA(output, Inlines.silk_RSHIFT(output, 7), Inlines.silk_SMLAWB(frac_Q7, Inlines.silk_SMULBB(frac_Q7, 128 - frac_Q7), -174));
        return output;
    }

    static void silk_interpolate(short[] xi, short[] x0, short[] x1, int ifact_Q2, int d) {
        Inlines.OpusAssert(ifact_Q2 >= 0);
        Inlines.OpusAssert(ifact_Q2 <= 4);
        for (int i = 0; i < d; ++i) {
            xi[i] = (short)Inlines.silk_ADD_RSHIFT(x0[i], Inlines.silk_SMULBB(x1[i] - x0[i], ifact_Q2), 2);
        }
    }

    static int silk_inner_prod_aligned_scale(short[] inVec1, short[] inVec2, int scale, int len) {
        int sum = 0;
        for (int i = 0; i < len; ++i) {
            sum = Inlines.silk_ADD_RSHIFT32(sum, Inlines.silk_SMULBB(inVec1[i], inVec2[i]), scale);
        }
        return sum;
    }

    static void silk_scale_copy_vector16(short[] data_out, int data_out_ptr, short[] data_in, int data_in_ptr, int gain_Q16, int dataSize) {
        for (int i = 0; i < dataSize; ++i) {
            data_out[data_out_ptr + i] = (short)Inlines.silk_SMULWB(gain_Q16, data_in[data_in_ptr + i]);
        }
    }

    static void silk_scale_vector32_Q26_lshift_18(int[] data1, int data1_ptr, int gain_Q26, int dataSize) {
        for (int i = data1_ptr; i < data1_ptr + dataSize; ++i) {
            data1[i] = (int)Inlines.silk_RSHIFT64(Inlines.silk_SMULL(data1[i], gain_Q26), 8);
        }
    }

    static int silk_inner_prod(short[] inVec1, int inVec1_ptr, short[] inVec2, int inVec2_ptr, int len) {
        int xy = 0;
        for (int i = 0; i < len; ++i) {
            xy = Inlines.MAC16_16(xy, inVec1[inVec1_ptr + i], inVec2[inVec2_ptr + i]);
        }
        return xy;
    }

    static int silk_inner_prod_self(short[] inVec, int inVec_ptr, int len) {
        int xy = 0;
        for (int i = inVec_ptr; i < inVec_ptr + len; ++i) {
            xy = Inlines.MAC16_16(xy, inVec[i], inVec[i]);
        }
        return xy;
    }

    static long silk_inner_prod16_aligned_64(short[] inVec1, int inVec1_ptr, short[] inVec2, int inVec2_ptr, int len) {
        long sum = 0L;
        for (int i = 0; i < len; ++i) {
            sum = Inlines.silk_SMLALBB(sum, inVec1[inVec1_ptr + i], inVec2[inVec2_ptr + i]);
        }
        return sum;
    }

    static long EC_MINI(long a, long b) {
        return a + (b - a & (long)(b < a ? -1 : 0));
    }

    static int EC_ILOG(long x) {
        if (x == 0L) {
            return 1;
        }
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        long y = x - (x >> 1 & 0x55555555L);
        y = (y >> 2 & 0x33333333L) + (y & 0x33333333L);
        y = (y >> 4) + y & 0xF0F0F0FL;
        y += y >> 8;
        y += y >> 16;
        return (int)(y &= 0x3FL);
    }

    static int abs(int a) {
        if (a < 0) {
            return 0 - a;
        }
        return a;
    }

    static int SignedByteToUnsignedInt(byte b) {
        return b & 0xFF;
    }
}
