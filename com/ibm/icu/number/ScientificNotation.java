package com.ibm.icu.number;

import com.ibm.icu.impl.FormattedStringBuilder;
import com.ibm.icu.impl.number.ConstantAffixModifier;
import com.ibm.icu.impl.number.DecimalQuantity;
import com.ibm.icu.impl.number.MicroProps;
import com.ibm.icu.impl.number.MicroPropsGenerator;
import com.ibm.icu.impl.number.Modifier;
import com.ibm.icu.impl.number.MultiplierProducer;
import com.ibm.icu.number.Notation;
import com.ibm.icu.number.NumberFormatter;
import com.ibm.icu.number.Precision;
import com.ibm.icu.text.DecimalFormatSymbols;
import com.ibm.icu.text.NumberFormat;
import java.text.Format;

public class ScientificNotation
extends Notation
implements Cloneable {
    int engineeringInterval;
    boolean requireMinInt;
    int minExponentDigits;
    NumberFormatter.SignDisplay exponentSignDisplay;

    ScientificNotation(int engineeringInterval, boolean requireMinInt, int minExponentDigits, NumberFormatter.SignDisplay exponentSignDisplay) {
        this.engineeringInterval = engineeringInterval;
        this.requireMinInt = requireMinInt;
        this.minExponentDigits = minExponentDigits;
        this.exponentSignDisplay = exponentSignDisplay;
    }

    public ScientificNotation withMinExponentDigits(int minExponentDigits) {
        if (minExponentDigits >= 1 && minExponentDigits <= 999) {
            ScientificNotation other = (ScientificNotation)this.clone();
            other.minExponentDigits = minExponentDigits;
            return other;
        }
        throw new IllegalArgumentException("Integer digits must be between 1 and 999 (inclusive)");
    }

    public ScientificNotation withExponentSignDisplay(NumberFormatter.SignDisplay exponentSignDisplay) {
        ScientificNotation other = (ScientificNotation)this.clone();
        other.exponentSignDisplay = exponentSignDisplay;
        return other;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError((Object)e);
        }
    }

    MicroPropsGenerator withLocaleData(DecimalFormatSymbols symbols, boolean build, MicroPropsGenerator parent) {
        return new ScientificHandler(this, symbols, build, parent);
    }

    private static class ScientificModifier
    implements Modifier {
        final int exponent;
        final ScientificHandler handler;

        ScientificModifier(int exponent, ScientificHandler handler) {
            this.exponent = exponent;
            this.handler = handler;
        }

        @Override
        public int apply(FormattedStringBuilder output, int leftIndex, int rightIndex) {
            return this.handler.doApply(this.exponent, output, rightIndex);
        }

        @Override
        public int getPrefixLength() {
            return 0;
        }

        @Override
        public int getCodePointCount() {
            return 999;
        }

        @Override
        public boolean isStrong() {
            return true;
        }

        @Override
        public boolean containsField(Format.Field field) {
            assert (false);
            return false;
        }

        @Override
        public Modifier.Parameters getParameters() {
            return null;
        }

        @Override
        public boolean semanticallyEquivalent(Modifier other) {
            if (!(other instanceof ScientificModifier)) {
                return false;
            }
            ScientificModifier _other = (ScientificModifier)other;
            return this.exponent == _other.exponent;
        }
    }

    private static class ScientificHandler
    implements MicroPropsGenerator,
    MultiplierProducer,
    Modifier {
        final ScientificNotation notation;
        final DecimalFormatSymbols symbols;
        final ScientificModifier[] precomputedMods;
        final MicroPropsGenerator parent;
        int exponent;

        private ScientificHandler(ScientificNotation notation, DecimalFormatSymbols symbols, boolean safe, MicroPropsGenerator parent) {
            this.notation = notation;
            this.symbols = symbols;
            this.parent = parent;
            if (safe) {
                this.precomputedMods = new ScientificModifier[25];
                for (int i = -12; i <= 12; ++i) {
                    this.precomputedMods[i + 12] = new ScientificModifier(i, this);
                }
            } else {
                this.precomputedMods = null;
            }
        }

        @Override
        public MicroProps processQuantity(DecimalQuantity quantity) {
            int exponent;
            MicroProps micros = this.parent.processQuantity(quantity);
            assert (micros.rounder != null);
            if (quantity.isInfinite() || quantity.isNaN()) {
                micros.modInner = ConstantAffixModifier.EMPTY;
                return micros;
            }
            if (quantity.isZeroish()) {
                if (this.notation.requireMinInt && micros.rounder instanceof Precision.SignificantRounderImpl) {
                    ((Precision.SignificantRounderImpl)micros.rounder).apply(quantity, this.notation.engineeringInterval);
                    exponent = 0;
                } else {
                    micros.rounder.apply(quantity);
                    exponent = 0;
                }
            } else {
                exponent = -micros.rounder.chooseMultiplierAndApply(quantity, this);
            }
            if (this.precomputedMods != null && exponent >= -12 && exponent <= 12) {
                micros.modInner = this.precomputedMods[exponent + 12];
            } else if (this.precomputedMods != null) {
                micros.modInner = new ScientificModifier(exponent, this);
            } else {
                this.exponent = exponent;
                micros.modInner = this;
            }
            micros.rounder = Precision.constructPassThrough();
            return micros;
        }

        @Override
        public int getMultiplier(int magnitude) {
            int interval = this.notation.engineeringInterval;
            int digitsShown = this.notation.requireMinInt ? interval : (interval <= 1 ? 1 : (magnitude % interval + interval) % interval + 1);
            return digitsShown - magnitude - 1;
        }

        @Override
        public int getPrefixLength() {
            return 0;
        }

        @Override
        public int getCodePointCount() {
            return 999;
        }

        @Override
        public boolean isStrong() {
            return true;
        }

        @Override
        public boolean containsField(Format.Field field) {
            assert (false);
            return false;
        }

        @Override
        public Modifier.Parameters getParameters() {
            assert (false);
            return null;
        }

        @Override
        public boolean semanticallyEquivalent(Modifier other) {
            assert (false);
            return false;
        }

        @Override
        public int apply(FormattedStringBuilder output, int leftIndex, int rightIndex) {
            return this.doApply(this.exponent, output, rightIndex);
        }

        private int doApply(int exponent, FormattedStringBuilder output, int rightIndex) {
            int i = rightIndex;
            i += output.insert(i, this.symbols.getExponentSeparator(), NumberFormat.Field.EXPONENT_SYMBOL);
            if (exponent < 0 && this.notation.exponentSignDisplay != NumberFormatter.SignDisplay.NEVER) {
                i += output.insert(i, this.symbols.getMinusSignString(), NumberFormat.Field.EXPONENT_SIGN);
            } else if (exponent >= 0 && this.notation.exponentSignDisplay == NumberFormatter.SignDisplay.ALWAYS) {
                i += output.insert(i, this.symbols.getPlusSignString(), NumberFormat.Field.EXPONENT_SIGN);
            }
            int disp = Math.abs(exponent);
            for (int j = 0; j < this.notation.minExponentDigits || disp > 0; ++j, disp /= 10) {
                int d = disp % 10;
                String digitString = this.symbols.getDigitStringsLocal()[d];
                i += output.insert(i - j, digitString, NumberFormat.Field.EXPONENT);
            }
            return i - rightIndex;
        }
    }
}
