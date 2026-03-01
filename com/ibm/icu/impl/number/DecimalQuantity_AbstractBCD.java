package com.ibm.icu.impl.number;

import com.ibm.icu.impl.StandardPlural;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.impl.number.DecimalQuantity;
import com.ibm.icu.impl.number.RoundingUtils;
import com.ibm.icu.text.PluralRules;
import com.ibm.icu.text.UFieldPosition;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.FieldPosition;

public abstract class DecimalQuantity_AbstractBCD
implements DecimalQuantity {
    protected int scale;
    protected int precision;
    protected byte flags;
    protected static final int NEGATIVE_FLAG = 1;
    protected static final int INFINITY_FLAG = 2;
    protected static final int NAN_FLAG = 4;
    protected double origDouble;
    protected int origDelta;
    protected boolean isApproximate;
    protected int lReqPos = 0;
    protected int rReqPos = 0;
    private static final double[] DOUBLE_MULTIPLIERS = new double[]{1.0, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, 1.0E7, 1.0E8, 1.0E9, 1.0E10, 1.0E11, 1.0E12, 1.0E13, 1.0E14, 1.0E15, 1.0E16, 1.0E17, 1.0E18, 1.0E19, 1.0E20, 1.0E21};
    @Deprecated
    public boolean explicitExactDouble = false;
    static final byte[] INT64_BCD = new byte[]{9, 2, 2, 3, 3, 7, 2, 0, 3, 6, 8, 5, 4, 7, 7, 5, 8, 0, 8};
    private static final int SECTION_LOWER_EDGE = -1;
    private static final int SECTION_UPPER_EDGE = -2;

    @Override
    public void copyFrom(DecimalQuantity _other) {
        this.copyBcdFrom(_other);
        DecimalQuantity_AbstractBCD other = (DecimalQuantity_AbstractBCD)_other;
        this.lReqPos = other.lReqPos;
        this.rReqPos = other.rReqPos;
        this.scale = other.scale;
        this.precision = other.precision;
        this.flags = other.flags;
        this.origDouble = other.origDouble;
        this.origDelta = other.origDelta;
        this.isApproximate = other.isApproximate;
    }

    public DecimalQuantity_AbstractBCD clear() {
        this.lReqPos = 0;
        this.rReqPos = 0;
        this.flags = 0;
        this.setBcdToZero();
        return this;
    }

    @Override
    public void setMinInteger(int minInt) {
        assert (minInt >= 0);
        if (minInt < this.lReqPos) {
            minInt = this.lReqPos;
        }
        this.lReqPos = minInt;
    }

    @Override
    public void setMinFraction(int minFrac) {
        assert (minFrac >= 0);
        this.rReqPos = -minFrac;
    }

    @Override
    public void applyMaxInteger(int maxInt) {
        assert (maxInt >= 0);
        if (this.precision == 0) {
            return;
        }
        if (maxInt <= this.scale) {
            this.setBcdToZero();
            return;
        }
        int magnitude = this.getMagnitude();
        if (maxInt <= magnitude) {
            this.popFromLeft(magnitude - maxInt + 1);
            this.compact();
        }
    }

    @Override
    public long getPositionFingerprint() {
        long fingerprint = 0L;
        fingerprint ^= (long)(this.lReqPos << 16);
        return fingerprint ^= (long)this.rReqPos << 32;
    }

    @Override
    public void roundToIncrement(BigDecimal roundingIncrement, MathContext mathContext) {
        assert (roundingIncrement.stripTrailingZeros().precision() != 1 || roundingIncrement.stripTrailingZeros().unscaledValue().intValue() != 5 || roundingIncrement.stripTrailingZeros().unscaledValue().intValue() != 1);
        BigDecimal temp = this.toBigDecimal();
        if ((temp = temp.divide(roundingIncrement, 0, mathContext.getRoundingMode()).multiply(roundingIncrement).round(mathContext)).signum() == 0) {
            this.setBcdToZero();
        } else {
            this.setToBigDecimal(temp);
        }
    }

    @Override
    public void multiplyBy(BigDecimal multiplicand) {
        if (this.isZeroish()) {
            return;
        }
        BigDecimal temp = this.toBigDecimal();
        temp = temp.multiply(multiplicand);
        this.setToBigDecimal(temp);
    }

    @Override
    public void negate() {
        this.flags = (byte)(this.flags ^ 1);
    }

    @Override
    public int getMagnitude() throws ArithmeticException {
        if (this.precision == 0) {
            throw new ArithmeticException("Magnitude is not well-defined for zero");
        }
        return this.scale + this.precision - 1;
    }

    @Override
    public void adjustMagnitude(int delta) {
        if (this.precision != 0) {
            this.scale = Utility.addExact(this.scale, delta);
            this.origDelta = Utility.addExact(this.origDelta, delta);
            Utility.addExact(this.scale, this.precision);
        }
    }

    @Override
    public StandardPlural getStandardPlural(PluralRules rules) {
        if (rules == null) {
            return StandardPlural.OTHER;
        }
        String ruleString = rules.select(this);
        return StandardPlural.orOtherFromString(ruleString);
    }

    @Override
    public double getPluralOperand(PluralRules.Operand operand) {
        assert (!this.isApproximate);
        switch (operand) {
            case i: {
                return this.isNegative() ? (double)(-this.toLong(true)) : (double)this.toLong(true);
            }
            case f: {
                return this.toFractionLong(true);
            }
            case t: {
                return this.toFractionLong(false);
            }
            case v: {
                return this.fractionCount();
            }
            case w: {
                return this.fractionCountWithoutTrailingZeros();
            }
        }
        return Math.abs(this.toDouble());
    }

    @Override
    public void populateUFieldPosition(FieldPosition fp) {
        if (fp instanceof UFieldPosition) {
            ((UFieldPosition)fp).setFractionDigits((int)this.getPluralOperand(PluralRules.Operand.v), (long)this.getPluralOperand(PluralRules.Operand.f));
        }
    }

    @Override
    public int getUpperDisplayMagnitude() {
        assert (!this.isApproximate);
        int magnitude = this.scale + this.precision;
        int result = this.lReqPos > magnitude ? this.lReqPos : magnitude;
        return result - 1;
    }

    @Override
    public int getLowerDisplayMagnitude() {
        assert (!this.isApproximate);
        int magnitude = this.scale;
        int result = this.rReqPos < magnitude ? this.rReqPos : magnitude;
        return result;
    }

    @Override
    public byte getDigit(int magnitude) {
        assert (!this.isApproximate);
        return this.getDigitPos(magnitude - this.scale);
    }

    private int fractionCount() {
        return -this.getLowerDisplayMagnitude();
    }

    private int fractionCountWithoutTrailingZeros() {
        return Math.max(-this.scale, 0);
    }

    @Override
    public boolean isNegative() {
        return (this.flags & 1) != 0;
    }

    @Override
    public int signum() {
        return this.isNegative() ? -1 : (this.isZeroish() && !this.isInfinite() ? 0 : 1);
    }

    @Override
    public boolean isInfinite() {
        return (this.flags & 2) != 0;
    }

    @Override
    public boolean isNaN() {
        return (this.flags & 4) != 0;
    }

    @Override
    public boolean isZeroish() {
        return this.precision == 0;
    }

    public void setToInt(int n) {
        this.setBcdToZero();
        this.flags = 0;
        if (n < 0) {
            this.flags = (byte)(this.flags | 1);
            n = -n;
        }
        if (n != 0) {
            this._setToInt(n);
            this.compact();
        }
    }

    private void _setToInt(int n) {
        if (n == Integer.MIN_VALUE) {
            this.readLongToBcd(-((long)n));
        } else {
            this.readIntToBcd(n);
        }
    }

    public void setToLong(long n) {
        this.setBcdToZero();
        this.flags = 0;
        if (n < 0L) {
            this.flags = (byte)(this.flags | 1);
            n = -n;
        }
        if (n != 0L) {
            this._setToLong(n);
            this.compact();
        }
    }

    private void _setToLong(long n) {
        if (n == Long.MIN_VALUE) {
            this.readBigIntegerToBcd(BigInteger.valueOf(n).negate());
        } else if (n <= Integer.MAX_VALUE) {
            this.readIntToBcd((int)n);
        } else {
            this.readLongToBcd(n);
        }
    }

    public void setToBigInteger(BigInteger n) {
        this.setBcdToZero();
        this.flags = 0;
        if (n.signum() == -1) {
            this.flags = (byte)(this.flags | 1);
            n = n.negate();
        }
        if (n.signum() != 0) {
            this._setToBigInteger(n);
            this.compact();
        }
    }

    private void _setToBigInteger(BigInteger n) {
        if (n.bitLength() < 32) {
            this.readIntToBcd(n.intValue());
        } else if (n.bitLength() < 64) {
            this.readLongToBcd(n.longValue());
        } else {
            this.readBigIntegerToBcd(n);
        }
    }

    public void setToDouble(double n) {
        this.setBcdToZero();
        this.flags = 0;
        if (Double.doubleToRawLongBits(n) < 0L) {
            this.flags = (byte)(this.flags | 1);
            n = -n;
        }
        if (Double.isNaN(n)) {
            this.flags = (byte)(this.flags | 4);
        } else if (Double.isInfinite(n)) {
            this.flags = (byte)(this.flags | 2);
        } else if (n != 0.0) {
            this._setToDoubleFast(n);
            this.compact();
        }
    }

    private void _setToDoubleFast(double n) {
        int i;
        this.isApproximate = true;
        this.origDouble = n;
        this.origDelta = 0;
        long ieeeBits = Double.doubleToLongBits(n);
        int exponent = (int)((ieeeBits & 0x7FF0000000000000L) >> 52) - 1023;
        if (exponent <= 52 && (double)((long)n) == n) {
            this._setToLong((long)n);
            return;
        }
        int fracLength = (int)((double)(52 - exponent) / 3.32192809489);
        if (fracLength >= 0) {
            for (i = fracLength; i >= 22; i -= 22) {
                n *= 1.0E22;
            }
            n *= DOUBLE_MULTIPLIERS[i];
        } else {
            for (i = fracLength; i <= -22; i += 22) {
                n /= 1.0E22;
            }
            n /= DOUBLE_MULTIPLIERS[-i];
        }
        long result = Math.round(n);
        if (result != 0L) {
            this._setToLong(result);
            this.scale -= fracLength;
        }
    }

    private void convertToAccurateDouble() {
        double n = this.origDouble;
        assert (n != 0.0);
        int delta = this.origDelta;
        this.setBcdToZero();
        String dstr = Double.toString(n);
        if (dstr.indexOf(69) != -1) {
            assert (dstr.indexOf(46) == 1);
            int expPos = dstr.indexOf(69);
            this._setToLong(Long.parseLong(dstr.charAt(0) + dstr.substring(2, expPos)));
            this.scale += Integer.parseInt(dstr.substring(expPos + 1)) - (expPos - 1) + 1;
        } else if (dstr.charAt(0) == '0') {
            assert (dstr.indexOf(46) == 1);
            this._setToLong(Long.parseLong(dstr.substring(2)));
            this.scale += 2 - dstr.length();
        } else if (dstr.charAt(dstr.length() - 1) == '0') {
            assert (dstr.indexOf(46) == dstr.length() - 2);
            assert (dstr.length() - 2 <= 18);
            this._setToLong(Long.parseLong(dstr.substring(0, dstr.length() - 2)));
        } else {
            int decimalPos = dstr.indexOf(46);
            this._setToLong(Long.parseLong(dstr.substring(0, decimalPos) + dstr.substring(decimalPos + 1)));
            this.scale += decimalPos - dstr.length() + 1;
        }
        this.scale += delta;
        this.compact();
        this.explicitExactDouble = true;
    }

    @Override
    public void setToBigDecimal(BigDecimal n) {
        this.setBcdToZero();
        this.flags = 0;
        if (n.signum() == -1) {
            this.flags = (byte)(this.flags | 1);
            n = n.negate();
        }
        if (n.signum() != 0) {
            this._setToBigDecimal(n);
            this.compact();
        }
    }

    private void _setToBigDecimal(BigDecimal n) {
        int fracLength = n.scale();
        n = n.scaleByPowerOfTen(fracLength);
        BigInteger bi = n.toBigInteger();
        this._setToBigInteger(bi);
        this.scale -= fracLength;
    }

    public long toLong(boolean truncateIfOverflow) {
        assert (truncateIfOverflow || this.fitsInLong());
        long result = 0L;
        int upperMagnitude = this.scale + this.precision - 1;
        if (truncateIfOverflow) {
            upperMagnitude = Math.min(upperMagnitude, 17);
        }
        for (int magnitude = upperMagnitude; magnitude >= 0; --magnitude) {
            result = result * 10L + (long)this.getDigitPos(magnitude - this.scale);
        }
        if (this.isNegative()) {
            result = -result;
        }
        return result;
    }

    public long toFractionLong(boolean includeTrailingZeros) {
        long result = 0L;
        int magnitude = -1;
        int lowerMagnitude = this.scale;
        if (includeTrailingZeros) {
            lowerMagnitude = Math.min(lowerMagnitude, this.rReqPos);
        }
        while (magnitude >= lowerMagnitude && (double)result <= 1.0E17) {
            result = result * 10L + (long)this.getDigitPos(magnitude - this.scale);
            --magnitude;
        }
        if (!includeTrailingZeros) {
            while (result > 0L && result % 10L == 0L) {
                result /= 10L;
            }
        }
        return result;
    }

    public boolean fitsInLong() {
        if (this.isInfinite() || this.isNaN()) {
            return false;
        }
        if (this.isZeroish()) {
            return true;
        }
        if (this.scale < 0) {
            return false;
        }
        int magnitude = this.getMagnitude();
        if (magnitude < 18) {
            return true;
        }
        if (magnitude > 18) {
            return false;
        }
        for (int p = 0; p < this.precision; ++p) {
            byte digit = this.getDigit(18 - p);
            if (digit < INT64_BCD[p]) {
                return true;
            }
            if (digit <= INT64_BCD[p]) continue;
            return false;
        }
        return this.isNegative();
    }

    @Override
    public double toDouble() {
        assert (!this.isApproximate);
        if (this.isNaN()) {
            return Double.NaN;
        }
        if (this.isInfinite()) {
            return this.isNegative() ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        }
        StringBuilder sb = new StringBuilder();
        this.toScientificString(sb);
        return Double.valueOf(sb.toString());
    }

    @Override
    public BigDecimal toBigDecimal() {
        if (this.isApproximate) {
            this.convertToAccurateDouble();
        }
        return this.bcdToBigDecimal();
    }

    private static int safeSubtract(int a, int b) {
        int diff = a - b;
        if (b < 0 && diff < a) {
            return Integer.MAX_VALUE;
        }
        if (b > 0 && diff > a) {
            return Integer.MIN_VALUE;
        }
        return diff;
    }

    public void truncate() {
        if (this.scale < 0) {
            this.shiftRight(-this.scale);
            this.scale = 0;
            this.compact();
        }
    }

    @Override
    public void roundToNickel(int magnitude, MathContext mathContext) {
        this.roundToMagnitude(magnitude, mathContext, true);
    }

    @Override
    public void roundToMagnitude(int magnitude, MathContext mathContext) {
        this.roundToMagnitude(magnitude, mathContext, false);
    }

    private void roundToMagnitude(int magnitude, MathContext mathContext, boolean nickel) {
        int position = DecimalQuantity_AbstractBCD.safeSubtract(magnitude, this.scale);
        int _mcPrecision = mathContext.getPrecision();
        if (_mcPrecision > 0 && this.precision - _mcPrecision > position) {
            position = this.precision - _mcPrecision;
        }
        int trailingDigit = this.getDigitPos(position);
        if ((position > 0 || this.isApproximate || nickel && trailingDigit != 0 && trailingDigit != 5) && this.precision != 0) {
            int p;
            int section;
            byte leadingDigit = this.getDigitPos(DecimalQuantity_AbstractBCD.safeSubtract(position, 1));
            if (!this.isApproximate) {
                if (nickel && trailingDigit != 2 && trailingDigit != 7) {
                    section = trailingDigit < 2 ? 1 : (trailingDigit < 5 ? 3 : (trailingDigit < 7 ? 1 : 3));
                } else if (leadingDigit < 5) {
                    section = 1;
                } else if (leadingDigit > 5) {
                    section = 3;
                } else {
                    section = 2;
                    for (p = DecimalQuantity_AbstractBCD.safeSubtract(position, 2); p >= 0; --p) {
                        if (this.getDigitPos(p) == 0) continue;
                        section = 3;
                        break;
                    }
                }
            } else {
                int minP = Math.max(0, this.precision - 14);
                if (!(leadingDigit != 0 || nickel && trailingDigit != 0 && trailingDigit != 5)) {
                    section = -1;
                    for (p = DecimalQuantity_AbstractBCD.safeSubtract(position, 2); p >= minP; --p) {
                        if (this.getDigitPos(p) == 0) continue;
                        section = 1;
                        break;
                    }
                } else if (!(leadingDigit != 4 || nickel && trailingDigit != 2 && trailingDigit != 7)) {
                    section = 2;
                    while (p >= minP) {
                        if (this.getDigitPos(p) != 9) {
                            section = 1;
                            break;
                        }
                        --p;
                    }
                } else if (!(leadingDigit != 5 || nickel && trailingDigit != 2 && trailingDigit != 7)) {
                    section = 2;
                    while (p >= minP) {
                        if (this.getDigitPos(p) != 0) {
                            section = 3;
                            break;
                        }
                        --p;
                    }
                } else if (!(leadingDigit != 9 || nickel && trailingDigit != 4 && trailingDigit != 9)) {
                    section = -2;
                    while (p >= minP) {
                        if (this.getDigitPos(p) != 9) {
                            section = 3;
                            break;
                        }
                        --p;
                    }
                } else {
                    section = nickel && trailingDigit != 2 && trailingDigit != 7 ? (trailingDigit < 2 ? 1 : (trailingDigit < 5 ? 3 : (trailingDigit < 7 ? 1 : 3))) : (leadingDigit < 5 ? 1 : 3);
                }
                boolean roundsAtMidpoint = RoundingUtils.roundsAtMidpoint(mathContext.getRoundingMode().ordinal());
                if (DecimalQuantity_AbstractBCD.safeSubtract(position, 1) < this.precision - 14 || roundsAtMidpoint && section == 2 || !roundsAtMidpoint && section < 0) {
                    this.convertToAccurateDouble();
                    this.roundToMagnitude(magnitude, mathContext, nickel);
                    return;
                }
                this.isApproximate = false;
                this.origDouble = 0.0;
                this.origDelta = 0;
                if (!(position > 0 || nickel && trailingDigit != 0 && trailingDigit != 5)) {
                    return;
                }
                if (section == -1) {
                    section = 1;
                }
                if (section == -2) {
                    section = 3;
                }
            }
            boolean isEven = nickel ? trailingDigit < 2 || trailingDigit > 7 || trailingDigit == 2 && section != 3 || trailingDigit == 7 && section == 3 : trailingDigit % 2 == 0;
            boolean roundDown = RoundingUtils.getRoundingDirection(isEven, this.isNegative(), section, mathContext.getRoundingMode().ordinal(), this);
            if (position >= this.precision) {
                this.setBcdToZero();
                this.scale = magnitude;
            } else {
                this.shiftRight(position);
            }
            if (nickel) {
                if (trailingDigit < 5 && roundDown) {
                    this.setDigitPos(0, (byte)0);
                    this.compact();
                    return;
                }
                if (trailingDigit >= 5 && !roundDown) {
                    this.setDigitPos(0, (byte)9);
                    trailingDigit = 9;
                } else {
                    this.setDigitPos(0, (byte)5);
                    return;
                }
            }
            if (!roundDown) {
                if (trailingDigit == 9) {
                    int bubblePos = 0;
                    while (this.getDigitPos(bubblePos) == 9) {
                        ++bubblePos;
                    }
                    this.shiftRight(bubblePos);
                }
                byte digit0 = this.getDigitPos(0);
                assert (digit0 != 9);
                this.setDigitPos(0, (byte)(digit0 + 1));
                ++this.precision;
            }
            this.compact();
        }
    }

    @Override
    public void roundToInfinity() {
        if (this.isApproximate) {
            this.convertToAccurateDouble();
        }
    }

    @Deprecated
    public void appendDigit(byte value, int leadingZeros, boolean appendAsInteger) {
        assert (leadingZeros >= 0);
        if (value == 0) {
            if (appendAsInteger && this.precision != 0) {
                this.scale += leadingZeros + 1;
            }
            return;
        }
        if (this.scale > 0) {
            leadingZeros += this.scale;
            if (appendAsInteger) {
                this.scale = 0;
            }
        }
        this.shiftLeft(leadingZeros + 1);
        this.setDigitPos(0, value);
        if (appendAsInteger) {
            this.scale += leadingZeros + 1;
        }
    }

    @Override
    public String toPlainString() {
        StringBuilder sb = new StringBuilder();
        if (this.isNegative()) {
            sb.append('-');
        }
        if (this.precision == 0 || this.getMagnitude() < 0) {
            sb.append('0');
        }
        for (int m = this.getUpperDisplayMagnitude(); m >= this.getLowerDisplayMagnitude(); --m) {
            sb.append((char)(48 + this.getDigit(m)));
            if (m != 0) continue;
            sb.append('.');
        }
        return sb.toString();
    }

    public String toScientificString() {
        StringBuilder sb = new StringBuilder();
        this.toScientificString(sb);
        return sb.toString();
    }

    public void toScientificString(StringBuilder result) {
        assert (!this.isApproximate);
        if (this.isNegative()) {
            result.append('-');
        }
        if (this.precision == 0) {
            result.append("0E+0");
            return;
        }
        int upperPos = this.precision - 1;
        int lowerPos = 0;
        int p = upperPos;
        result.append((char)(48 + this.getDigitPos(p)));
        if (--p >= lowerPos) {
            result.append('.');
            while (p >= lowerPos) {
                result.append((char)(48 + this.getDigitPos(p)));
                --p;
            }
        }
        result.append('E');
        int _scale = upperPos + this.scale;
        if (_scale == Integer.MIN_VALUE) {
            result.append("-2147483648");
            return;
        }
        if (_scale < 0) {
            _scale *= -1;
            result.append('-');
        } else {
            result.append('+');
        }
        if (_scale == 0) {
            result.append('0');
        }
        int insertIndex = result.length();
        while (_scale > 0) {
            int quot = _scale / 10;
            int rem = _scale % 10;
            result.insert(insertIndex, (char)(48 + rem));
            _scale = quot;
        }
    }

    public boolean equals(Object other) {
        boolean basicEquals;
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof DecimalQuantity_AbstractBCD)) {
            return false;
        }
        DecimalQuantity_AbstractBCD _other = (DecimalQuantity_AbstractBCD)other;
        boolean bl = basicEquals = this.scale == _other.scale && this.precision == _other.precision && this.flags == _other.flags && this.lReqPos == _other.lReqPos && this.rReqPos == _other.rReqPos && this.isApproximate == _other.isApproximate;
        if (!basicEquals) {
            return false;
        }
        if (this.precision == 0) {
            return true;
        }
        if (this.isApproximate) {
            return this.origDouble == _other.origDouble && this.origDelta == _other.origDelta;
        }
        for (int m = this.getUpperDisplayMagnitude(); m >= this.getLowerDisplayMagnitude(); --m) {
            if (this.getDigit(m) == _other.getDigit(m)) continue;
            return false;
        }
        return true;
    }

    protected abstract byte getDigitPos(int var1);

    protected abstract void setDigitPos(int var1, byte var2);

    protected abstract void shiftLeft(int var1);

    protected abstract void shiftRight(int var1);

    protected abstract void popFromLeft(int var1);

    protected abstract void setBcdToZero();

    protected abstract void readIntToBcd(int var1);

    protected abstract void readLongToBcd(long var1);

    protected abstract void readBigIntegerToBcd(BigInteger var1);

    protected abstract BigDecimal bcdToBigDecimal();

    protected abstract void copyBcdFrom(DecimalQuantity var1);

    protected abstract void compact();
}
