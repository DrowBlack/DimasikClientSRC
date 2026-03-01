package dimasik.managers.mods.voicechat.decoder;

import dimasik.managers.mods.voicechat.decoder.Arrays;
import dimasik.managers.mods.voicechat.decoder.Inlines;

class EntropyCoder {
    private final int EC_WINDOW_SIZE = 32;
    private final int EC_UINT_BITS = 8;
    static final int BITRES = 3;
    private final int EC_SYM_BITS = 8;
    private final int EC_CODE_BITS = 32;
    private final long EC_SYM_MAX = 255L;
    private final int EC_CODE_SHIFT = 23;
    private final long EC_CODE_TOP = 0x80000000L;
    private final long EC_CODE_BOT = 0x800000L;
    private final int EC_CODE_EXTRA = 7;
    private byte[] buf;
    private int buf_ptr;
    int storage;
    int end_offs;
    long end_window;
    int nend_bits;
    int nbits_total;
    int offs;
    long rng;
    long val;
    long ext;
    int rem;
    int error;
    private static final int[] correction = new int[]{35733, 38967, 42495, 46340, 50535, 55109, 60097, 65535};

    EntropyCoder() {
        this.Reset();
    }

    void Reset() {
        this.buf = null;
        this.buf_ptr = 0;
        this.storage = 0;
        this.end_offs = 0;
        this.end_window = 0L;
        this.nend_bits = 0;
        this.offs = 0;
        this.rng = 0L;
        this.val = 0L;
        this.ext = 0L;
        this.rem = 0;
        this.error = 0;
    }

    void Assign(EntropyCoder other) {
        this.buf = other.buf;
        this.buf_ptr = other.buf_ptr;
        this.storage = other.storage;
        this.end_offs = other.end_offs;
        this.end_window = other.end_window;
        this.nend_bits = other.nend_bits;
        this.nbits_total = other.nbits_total;
        this.offs = other.offs;
        this.rng = other.rng;
        this.val = other.val;
        this.ext = other.ext;
        this.rem = other.rem;
        this.error = other.error;
    }

    byte[] get_buffer() {
        byte[] convertedBuf = new byte[this.storage];
        System.arraycopy(this.buf, this.buf_ptr, convertedBuf, 0, this.storage);
        return convertedBuf;
    }

    void write_buffer(byte[] data, int data_ptr, int target_offset, int size) {
        System.arraycopy(data, data_ptr, this.buf, this.buf_ptr + target_offset, size);
    }

    int read_byte() {
        return this.offs < this.storage ? Inlines.SignedByteToUnsignedInt(this.buf[this.buf_ptr + this.offs++]) : 0;
    }

    int read_byte_from_end() {
        return this.end_offs < this.storage ? Inlines.SignedByteToUnsignedInt(this.buf[this.buf_ptr + (this.storage - ++this.end_offs)]) : 0;
    }

    int write_byte(long _value) {
        if (this.offs + this.end_offs >= this.storage) {
            return -1;
        }
        this.buf[this.buf_ptr + this.offs++] = (byte)(_value & 0xFFL);
        return 0;
    }

    int write_byte_at_end(long _value) {
        if (this.offs + this.end_offs >= this.storage) {
            return -1;
        }
        this.buf[this.buf_ptr + (this.storage - ++this.end_offs)] = (byte)(_value & 0xFFL);
        return 0;
    }

    void dec_normalize() {
        while (this.rng <= 0x800000L) {
            this.nbits_total += 8;
            this.rng = Inlines.CapToUInt32(this.rng << 8);
            int sym = this.rem;
            this.rem = this.read_byte();
            sym = (sym << 8 | this.rem) >> 1;
            this.val = Inlines.CapToUInt32((this.val << 8) + (0xFFL & (long)(~sym)) & Integer.MAX_VALUE);
        }
    }

    void dec_init(byte[] _buf, int _buf_ptr, int _storage) {
        this.buf = _buf;
        this.buf_ptr = _buf_ptr;
        this.storage = _storage;
        this.end_offs = 0;
        this.end_window = 0L;
        this.nend_bits = 0;
        this.nbits_total = 9;
        this.offs = 0;
        this.rng = 128L;
        this.rem = this.read_byte();
        this.val = Inlines.CapToUInt32(this.rng - 1L - (long)(this.rem >> 1));
        this.error = 0;
        this.dec_normalize();
    }

    long decode(long _ft) {
        _ft = Inlines.CapToUInt32(_ft);
        this.ext = Inlines.CapToUInt32(this.rng / _ft);
        long s = Inlines.CapToUInt32(this.val / this.ext);
        return Inlines.CapToUInt32(_ft - Inlines.EC_MINI(Inlines.CapToUInt32(s + 1L), _ft));
    }

    long decode_bin(int _bits) {
        this.ext = this.rng >> _bits;
        long s = Inlines.CapToUInt32(this.val / this.ext);
        return Inlines.CapToUInt32(Inlines.CapToUInt32(1L << _bits) - Inlines.EC_MINI(Inlines.CapToUInt32(s + 1L), 1L << _bits));
    }

    void dec_update(long _fl, long _fh, long _ft) {
        _fl = Inlines.CapToUInt32(_fl);
        _fh = Inlines.CapToUInt32(_fh);
        _ft = Inlines.CapToUInt32(_ft);
        long s = Inlines.CapToUInt32(this.ext * (_ft - _fh));
        this.val -= s;
        this.rng = _fl > 0L ? Inlines.CapToUInt32(this.ext * (_fh - _fl)) : this.rng - s;
        this.dec_normalize();
    }

    int dec_bit_logp(long _logp) {
        int ret;
        long d = this.val;
        long r = this.rng;
        long s = r >> (int)_logp;
        int n = ret = d < s ? 1 : 0;
        if (ret == 0) {
            this.val = Inlines.CapToUInt32(d - s);
        }
        this.rng = ret != 0 ? s : r - s;
        this.dec_normalize();
        return ret;
    }

    int dec_icdf(short[] _icdf, int _ftb) {
        long t;
        long s = this.rng;
        long d = this.val;
        long r = s >> _ftb;
        int ret = -1;
        do {
            t = s;
        } while (d < (s = Inlines.CapToUInt32(r * (long)_icdf[++ret])));
        this.val = Inlines.CapToUInt32(d - s);
        this.rng = Inlines.CapToUInt32(t - s);
        this.dec_normalize();
        return ret;
    }

    int dec_icdf(short[] _icdf, int _icdf_offset, int _ftb) {
        long t;
        long s = this.rng;
        long d = this.val;
        long r = s >> _ftb;
        int ret = _icdf_offset - 1;
        do {
            t = s;
        } while (d < (s = Inlines.CapToUInt32(r * (long)_icdf[++ret])));
        this.val = Inlines.CapToUInt32(d - s);
        this.rng = Inlines.CapToUInt32(t - s);
        this.dec_normalize();
        return ret - _icdf_offset;
    }

    long dec_uint(long _ft) {
        Inlines.OpusAssert((_ft = Inlines.CapToUInt32(_ft)) > 1L);
        int ftb = Inlines.EC_ILOG(--_ft);
        if (ftb > 8) {
            long ft = Inlines.CapToUInt32((_ft >> (ftb -= 8)) + 1L);
            long s = Inlines.CapToUInt32(this.decode(ft));
            this.dec_update(s, s + 1L, ft);
            long t = Inlines.CapToUInt32(s << ftb | (long)this.dec_bits(ftb));
            if (t <= _ft) {
                return t;
            }
            this.error = 1;
            return _ft;
        }
        long s = Inlines.CapToUInt32(this.decode(++_ft));
        this.dec_update(s, s + 1L, _ft);
        return s;
    }

    int dec_bits(int _bits) {
        long window = this.end_window;
        int available = this.nend_bits;
        if (available < _bits) {
            do {
                window = Inlines.CapToUInt32(window | (long)(this.read_byte_from_end() << available));
            } while ((available += 8) <= 24);
        }
        int ret = (int)(0xFFFFFFFFFFFFFFFFL & (window & (long)((1 << _bits) - 1)));
        this.end_window = Inlines.CapToUInt32(window >>= _bits);
        this.nend_bits = available -= _bits;
        this.nbits_total += _bits;
        return ret;
    }

    void enc_carry_out(int _c) {
        if ((long)_c != 255L) {
            int carry = _c >> 8;
            if (this.rem >= 0) {
                this.error |= this.write_byte(Inlines.CapToUInt32(this.rem + carry));
            }
            if (this.ext > 0L) {
                long sym = 255L + (long)carry & 0xFFL;
                do {
                    this.error |= this.write_byte(sym);
                } while (--this.ext > 0L);
            }
            this.rem = (int)((long)_c & 0xFFL);
        } else {
            ++this.ext;
        }
    }

    void enc_normalize() {
        while (this.rng <= 0x800000L) {
            this.enc_carry_out((int)(this.val >> 23));
            this.val = Inlines.CapToUInt32(this.val << 8 & Integer.MAX_VALUE);
            this.rng = Inlines.CapToUInt32(this.rng << 8);
            this.nbits_total += 8;
        }
    }

    void enc_init(byte[] _buf, int buf_ptr, int _size) {
        this.buf = _buf;
        this.buf_ptr = buf_ptr;
        this.end_offs = 0;
        this.end_window = 0L;
        this.nend_bits = 0;
        this.nbits_total = 33;
        this.offs = 0;
        this.rng = Inlines.CapToUInt32(0x80000000L);
        this.rem = -1;
        this.val = 0L;
        this.ext = 0L;
        this.storage = _size;
        this.error = 0;
    }

    void encode(long _fl, long _fh, long _ft) {
        _fl = Inlines.CapToUInt32(_fl);
        _fh = Inlines.CapToUInt32(_fh);
        _ft = Inlines.CapToUInt32(_ft);
        long r = Inlines.CapToUInt32(this.rng / _ft);
        if (_fl > 0L) {
            this.val += Inlines.CapToUInt32(this.rng - r * (_ft - _fl));
            this.rng = Inlines.CapToUInt32(r * (_fh - _fl));
        } else {
            this.rng = Inlines.CapToUInt32(this.rng - r * (_ft - _fh));
        }
        this.enc_normalize();
    }

    void encode_bin(long _fl, long _fh, int _bits) {
        _fl = Inlines.CapToUInt32(_fl);
        _fh = Inlines.CapToUInt32(_fh);
        long r = Inlines.CapToUInt32(this.rng >> _bits);
        if (_fl > 0L) {
            this.val = Inlines.CapToUInt32(this.val + Inlines.CapToUInt32(this.rng - r * ((long)(1 << _bits) - _fl)));
            this.rng = Inlines.CapToUInt32(r * (_fh - _fl));
        } else {
            this.rng = Inlines.CapToUInt32(this.rng - r * ((long)(1 << _bits) - _fh));
        }
        this.enc_normalize();
    }

    void enc_bit_logp(int _val, int _logp) {
        long r = this.rng;
        long l = this.val;
        long s = r >> _logp;
        r -= s;
        if (_val != 0) {
            this.val = Inlines.CapToUInt32(l + r);
        }
        this.rng = _val != 0 ? s : r;
        this.enc_normalize();
    }

    void enc_icdf(int _s, short[] _icdf, int _ftb) {
        long r = Inlines.CapToUInt32(this.rng >> _ftb);
        if (_s > 0) {
            this.val += Inlines.CapToUInt32(this.rng - Inlines.CapToUInt32(r * (long)_icdf[_s - 1]));
            this.rng = r * Inlines.CapToUInt32(_icdf[_s - 1] - _icdf[_s]);
        } else {
            this.rng = Inlines.CapToUInt32(this.rng - r * (long)_icdf[_s]);
        }
        this.enc_normalize();
    }

    void enc_icdf(int _s, short[] _icdf, int icdf_ptr, int _ftb) {
        long r = Inlines.CapToUInt32(this.rng >> _ftb);
        if (_s > 0) {
            this.val += Inlines.CapToUInt32(this.rng - Inlines.CapToUInt32(r * (long)_icdf[icdf_ptr + _s - 1]));
            this.rng = Inlines.CapToUInt32(r * Inlines.CapToUInt32(_icdf[icdf_ptr + _s - 1] - _icdf[icdf_ptr + _s]));
        } else {
            this.rng = Inlines.CapToUInt32(this.rng - r * (long)_icdf[icdf_ptr + _s]);
        }
        this.enc_normalize();
    }

    void enc_uint(long _fl, long _ft) {
        _fl = Inlines.CapToUInt32(_fl);
        Inlines.OpusAssert((_ft = Inlines.CapToUInt32(_ft)) > 1L);
        int ftb = Inlines.EC_ILOG(--_ft);
        if (ftb > 8) {
            long ft = Inlines.CapToUInt32((_ft >> (ftb -= 8)) + 1L);
            long fl = Inlines.CapToUInt32(_fl >> ftb);
            this.encode(fl, fl + 1L, ft);
            this.enc_bits(_fl & Inlines.CapToUInt32((1 << ftb) - 1), ftb);
        } else {
            this.encode(_fl, _fl + 1L, _ft + 1L);
        }
    }

    void enc_bits(long _fl, int _bits) {
        _fl = Inlines.CapToUInt32(_fl);
        long window = this.end_window;
        int used = this.nend_bits;
        Inlines.OpusAssert(_bits > 0);
        if (used + _bits > 32) {
            do {
                this.error |= this.write_byte_at_end(window & 0xFFL);
                window >>= 8;
            } while ((used -= 8) >= 8);
        }
        window |= Inlines.CapToUInt32(_fl << used);
        this.end_window = window;
        this.nend_bits = used += _bits;
        this.nbits_total += _bits;
    }

    void enc_patch_initial_bits(long _val, int _nbits) {
        Inlines.OpusAssert(_nbits <= 8);
        int shift = 8 - _nbits;
        long mask = (1 << _nbits) - 1 << shift;
        if (this.offs > 0) {
            this.buf[this.buf_ptr] = (byte)((long)this.buf[this.buf_ptr] & (mask ^ 0xFFFFFFFFFFFFFFFFL) | Inlines.CapToUInt32(_val << shift));
        } else if (this.rem >= 0) {
            this.rem = (int)Inlines.CapToUInt32(Inlines.CapToUInt32((long)this.rem & (mask ^ 0xFFFFFFFFFFFFFFFFL) | _val) << shift);
        } else if (this.rng <= 0x80000000L >> _nbits) {
            this.val = Inlines.CapToUInt32(this.val & (mask << 23 ^ 0xFFFFFFFFFFFFFFFFL) | Inlines.CapToUInt32(Inlines.CapToUInt32(_val) << 23 + shift));
        } else {
            this.error = -1;
        }
    }

    void enc_shrink(int _size) {
        Inlines.OpusAssert(this.offs + this.end_offs <= _size);
        Arrays.MemMove(this.buf, this.buf_ptr + _size - this.end_offs, this.buf_ptr + this.storage - this.end_offs, this.end_offs);
        this.storage = _size;
    }

    int range_bytes() {
        return this.offs;
    }

    int get_error() {
        return this.error;
    }

    int tell() {
        int returnVal = this.nbits_total - Inlines.EC_ILOG(this.rng);
        return returnVal;
    }

    int tell_frac() {
        long b;
        int nbits = this.nbits_total << 3;
        int l = Inlines.EC_ILOG(this.rng);
        int r = (int)(this.rng >> l - 16);
        b = Inlines.CapToUInt32(b + (long)(r > correction[(int)(b = Inlines.CapToUInt32((r >> 12) - 8))] ? 1 : 0));
        l = (int)((long)(l << 3) + b);
        return nbits - l;
    }

    void enc_done() {
        int used;
        int l = 32 - Inlines.EC_ILOG(this.rng);
        long msk = Inlines.CapToUInt32(Integer.MAX_VALUE >>> l);
        long end = Inlines.CapToUInt32(Inlines.CapToUInt32(this.val + msk) & (msk ^ 0xFFFFFFFFFFFFFFFFL));
        if ((end | msk) >= this.val + this.rng) {
            ++l;
            end = Inlines.CapToUInt32(Inlines.CapToUInt32(this.val + (msk >>= 1)) & (msk ^ 0xFFFFFFFFFFFFFFFFL));
        }
        while (l > 0) {
            this.enc_carry_out((int)(end >> 23));
            end = Inlines.CapToUInt32(end << 8 & Integer.MAX_VALUE);
            l -= 8;
        }
        if (this.rem >= 0 || this.ext > 0L) {
            this.enc_carry_out(0);
        }
        long window = this.end_window;
        for (used = this.nend_bits; used >= 8; used -= 8) {
            this.error |= this.write_byte_at_end(window & 0xFFL);
            window >>= 8;
        }
        if (this.error == 0) {
            Arrays.MemSetWithOffset(this.buf, (byte)0, this.buf_ptr + this.offs, this.storage - this.offs - this.end_offs);
            if (used > 0) {
                if (this.end_offs >= this.storage) {
                    this.error = -1;
                } else {
                    l = -l;
                    if (this.offs + this.end_offs >= this.storage && l < used) {
                        window = Inlines.CapToUInt32(window & (long)((1 << l) - 1));
                        this.error = -1;
                    }
                    int z = this.buf_ptr + this.storage - this.end_offs - 1;
                    this.buf[z] = (byte)(this.buf[z] | (byte)(window & 0xFFL));
                }
            }
        }
    }
}
