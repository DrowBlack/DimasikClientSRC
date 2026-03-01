package org.openjdk.nashorn.internal.runtime.doubleconv;

import org.openjdk.nashorn.internal.runtime.doubleconv.DtoaBuffer;
import org.openjdk.nashorn.internal.runtime.doubleconv.IeeeDouble;

class FixedDtoa {
    static final int kDoubleSignificandSize = 53;

    FixedDtoa() {
    }

    static void fillDigits32FixedLength(int number, int requested_length, DtoaBuffer buffer) {
        for (int i = requested_length - 1; i >= 0; --i) {
            buffer.chars[buffer.length + i] = (char)(48 + Integer.remainderUnsigned(number, 10));
            number = Integer.divideUnsigned(number, 10);
        }
        buffer.length += requested_length;
    }

    static void fillDigits32(int number, DtoaBuffer buffer) {
        int number_length = 0;
        while (number != 0) {
            int digit = Integer.remainderUnsigned(number, 10);
            number = Integer.divideUnsigned(number, 10);
            buffer.chars[buffer.length + number_length] = (char)(48 + digit);
            ++number_length;
        }
        int i = buffer.length;
        for (int j = buffer.length + number_length - 1; i < j; ++i, --j) {
            char tmp = buffer.chars[i];
            buffer.chars[i] = buffer.chars[j];
            buffer.chars[j] = tmp;
        }
        buffer.length += number_length;
    }

    static void fillDigits64FixedLength(long number, DtoaBuffer buffer) {
        int kTen7 = 10000000;
        int part2 = (int)Long.remainderUnsigned(number, 10000000L);
        number = Long.divideUnsigned(number, 10000000L);
        int part1 = (int)Long.remainderUnsigned(number, 10000000L);
        int part0 = (int)Long.divideUnsigned(number, 10000000L);
        FixedDtoa.fillDigits32FixedLength(part0, 3, buffer);
        FixedDtoa.fillDigits32FixedLength(part1, 7, buffer);
        FixedDtoa.fillDigits32FixedLength(part2, 7, buffer);
    }

    static void FillDigits64(long number, DtoaBuffer buffer) {
        int kTen7 = 10000000;
        int part2 = (int)Long.remainderUnsigned(number, 10000000L);
        number = Long.divideUnsigned(number, 10000000L);
        int part1 = (int)Long.remainderUnsigned(number, 10000000L);
        int part0 = (int)Long.divideUnsigned(number, 10000000L);
        if (part0 != 0) {
            FixedDtoa.fillDigits32(part0, buffer);
            FixedDtoa.fillDigits32FixedLength(part1, 7, buffer);
            FixedDtoa.fillDigits32FixedLength(part2, 7, buffer);
        } else if (part1 != 0) {
            FixedDtoa.fillDigits32(part1, buffer);
            FixedDtoa.fillDigits32FixedLength(part2, 7, buffer);
        } else {
            FixedDtoa.fillDigits32(part2, buffer);
        }
    }

    static void roundUp(DtoaBuffer buffer) {
        if (buffer.length == 0) {
            buffer.chars[0] = 49;
            buffer.decimalPoint = 1;
            buffer.length = 1;
            return;
        }
        int n = buffer.length - 1;
        buffer.chars[n] = (char)(buffer.chars[n] + '\u0001');
        for (int i = buffer.length - 1; i > 0; --i) {
            if (buffer.chars[i] != ':') {
                return;
            }
            buffer.chars[i] = 48;
            int n2 = i - 1;
            buffer.chars[n2] = (char)(buffer.chars[n2] + '\u0001');
        }
        if (buffer.chars[0] == ':') {
            buffer.chars[0] = 49;
            ++buffer.decimalPoint;
        }
    }

    static void fillFractionals(long fractionals, int exponent, int fractional_count, DtoaBuffer buffer) {
        assert (-128 <= exponent && exponent <= 0);
        if (-exponent <= 64) {
            int digit;
            assert (fractionals >>> 56 == 0L);
            int point = -exponent;
            for (int i = 0; i < fractional_count && fractionals != 0L; fractionals -= (long)digit << point, ++i) {
                digit = (int)((fractionals *= 5L) >>> --point);
                assert (digit <= 9);
                buffer.chars[buffer.length] = (char)(48 + digit);
                ++buffer.length;
            }
            assert (fractionals == 0L || point - 1 >= 0);
            if (fractionals != 0L && (fractionals >>> point - 1 & 1L) == 1L) {
                FixedDtoa.roundUp(buffer);
            }
        } else {
            assert (64 < -exponent && -exponent <= 128);
            UInt128 fractionals128 = new UInt128(fractionals, 0L);
            fractionals128.shift(-exponent - 64);
            int point = 128;
            for (int i = 0; i < fractional_count && !fractionals128.isZero(); ++i) {
                fractionals128.multiply(5);
                int digit = fractionals128.divModPowerOf2(--point);
                assert (digit <= 9);
                buffer.chars[buffer.length] = (char)(48 + digit);
                ++buffer.length;
            }
            if (fractionals128.bitAt(point - 1) == 1) {
                FixedDtoa.roundUp(buffer);
            }
        }
    }

    static void trimZeros(DtoaBuffer buffer) {
        int first_non_zero;
        while (buffer.length > 0 && buffer.chars[buffer.length - 1] == '0') {
            --buffer.length;
        }
        for (first_non_zero = 0; first_non_zero < buffer.length && buffer.chars[first_non_zero] == '0'; ++first_non_zero) {
        }
        if (first_non_zero != 0) {
            for (int i = first_non_zero; i < buffer.length; ++i) {
                buffer.chars[i - first_non_zero] = buffer.chars[i];
            }
            buffer.length -= first_non_zero;
            buffer.decimalPoint -= first_non_zero;
        }
    }

    static boolean fastFixedDtoa(double v, int fractional_count, DtoaBuffer buffer) {
        long kMaxUInt32 = 0xFFFFFFFFL;
        long l = IeeeDouble.doubleToLong(v);
        long significand = IeeeDouble.significand(l);
        int exponent = IeeeDouble.exponent(l);
        if (exponent > 20) {
            return false;
        }
        if (fractional_count > 20) {
            return false;
        }
        if (exponent + 53 > 64) {
            long remainder;
            int quotient;
            long kFive17 = 762939453125L;
            long divisor = 762939453125L;
            int divisor_power = 17;
            long dividend = significand;
            if (exponent > 17) {
                quotient = (int)Long.divideUnsigned(dividend <<= exponent - 17, divisor);
                remainder = Long.remainderUnsigned(dividend, divisor) << 17;
            } else {
                quotient = (int)Long.divideUnsigned(dividend, divisor <<= 17 - exponent);
                remainder = Long.remainderUnsigned(dividend, divisor) << exponent;
            }
            FixedDtoa.fillDigits32(quotient, buffer);
            FixedDtoa.fillDigits64FixedLength(remainder, buffer);
            buffer.decimalPoint = buffer.length;
        } else if (exponent >= 0) {
            FixedDtoa.FillDigits64(significand <<= exponent, buffer);
            buffer.decimalPoint = buffer.length;
        } else if (exponent > -53) {
            long integrals = significand >>> -exponent;
            long fractionals = significand - (integrals << -exponent);
            if (Long.compareUnsigned(integrals, 0xFFFFFFFFL) > 0) {
                FixedDtoa.FillDigits64(integrals, buffer);
            } else {
                FixedDtoa.fillDigits32((int)integrals, buffer);
            }
            buffer.decimalPoint = buffer.length;
            FixedDtoa.fillFractionals(fractionals, exponent, fractional_count, buffer);
        } else if (exponent < -128) {
            assert (fractional_count <= 20);
            buffer.reset();
            buffer.decimalPoint = -fractional_count;
        } else {
            buffer.decimalPoint = 0;
            FixedDtoa.fillFractionals(significand, exponent, fractional_count, buffer);
        }
        FixedDtoa.trimZeros(buffer);
        if (buffer.length == 0) {
            buffer.decimalPoint = -fractional_count;
        }
        return true;
    }

    static class UInt128 {
        private static final long kMask32 = 0xFFFFFFFFL;
        private long high_bits_;
        private long low_bits_;

        UInt128(long high_bits, long low_bits) {
            this.high_bits_ = high_bits;
            this.low_bits_ = low_bits;
        }

        void multiply(int multiplicand) {
            long accumulator = (this.low_bits_ & 0xFFFFFFFFL) * (long)multiplicand;
            long part = accumulator & 0xFFFFFFFFL;
            accumulator >>>= 32;
            this.low_bits_ = ((accumulator += (this.low_bits_ >>> 32) * (long)multiplicand) << 32) + part;
            accumulator >>>= 32;
            part = (accumulator += (this.high_bits_ & 0xFFFFFFFFL) * (long)multiplicand) & 0xFFFFFFFFL;
            accumulator >>>= 32;
            this.high_bits_ = ((accumulator += (this.high_bits_ >>> 32) * (long)multiplicand) << 32) + part;
            assert (accumulator >>> 32 == 0L);
        }

        void shift(int shift_amount) {
            assert (-64 <= shift_amount && shift_amount <= 64);
            if (shift_amount == 0) {
                return;
            }
            if (shift_amount == -64) {
                this.high_bits_ = this.low_bits_;
                this.low_bits_ = 0L;
            } else if (shift_amount == 64) {
                this.low_bits_ = this.high_bits_;
                this.high_bits_ = 0L;
            } else if (shift_amount <= 0) {
                this.high_bits_ <<= -shift_amount;
                this.high_bits_ += this.low_bits_ >>> 64 + shift_amount;
                this.low_bits_ <<= -shift_amount;
            } else {
                this.low_bits_ >>>= shift_amount;
                this.low_bits_ += this.high_bits_ << 64 - shift_amount;
                this.high_bits_ >>>= shift_amount;
            }
        }

        int divModPowerOf2(int power) {
            if (power >= 64) {
                int result = (int)(this.high_bits_ >>> power - 64);
                this.high_bits_ -= (long)result << power - 64;
                return result;
            }
            long part_low = this.low_bits_ >>> power;
            long part_high = this.high_bits_ << 64 - power;
            int result = (int)(part_low + part_high);
            this.high_bits_ = 0L;
            this.low_bits_ -= part_low << power;
            return result;
        }

        boolean isZero() {
            return this.high_bits_ == 0L && this.low_bits_ == 0L;
        }

        int bitAt(int position) {
            if (position >= 64) {
                return (int)(this.high_bits_ >>> position - 64) & 1;
            }
            return (int)(this.low_bits_ >>> position) & 1;
        }
    }
}
