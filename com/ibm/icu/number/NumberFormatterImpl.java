package com.ibm.icu.number;

import com.ibm.icu.impl.CurrencyData;
import com.ibm.icu.impl.FormattedStringBuilder;
import com.ibm.icu.impl.StandardPlural;
import com.ibm.icu.impl.number.CompactData;
import com.ibm.icu.impl.number.ConstantAffixModifier;
import com.ibm.icu.impl.number.DecimalQuantity;
import com.ibm.icu.impl.number.DecimalQuantity_DualStorageBCD;
import com.ibm.icu.impl.number.Grouper;
import com.ibm.icu.impl.number.LongNameHandler;
import com.ibm.icu.impl.number.MacroProps;
import com.ibm.icu.impl.number.MicroProps;
import com.ibm.icu.impl.number.MicroPropsGenerator;
import com.ibm.icu.impl.number.MultiplierFormatHandler;
import com.ibm.icu.impl.number.MutablePatternModifier;
import com.ibm.icu.impl.number.Padder;
import com.ibm.icu.impl.number.PatternStringParser;
import com.ibm.icu.impl.number.RoundingUtils;
import com.ibm.icu.number.CompactNotation;
import com.ibm.icu.number.IntegerWidth;
import com.ibm.icu.number.NumberFormatter;
import com.ibm.icu.number.Precision;
import com.ibm.icu.number.ScientificNotation;
import com.ibm.icu.text.DecimalFormatSymbols;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.NumberingSystem;
import com.ibm.icu.text.PluralRules;
import com.ibm.icu.util.Currency;
import com.ibm.icu.util.MeasureUnit;

class NumberFormatterImpl {
    private static final Currency DEFAULT_CURRENCY = Currency.getInstance("XXX");
    final MicroProps micros = new MicroProps(true);
    final MicroPropsGenerator microPropsGenerator;

    public NumberFormatterImpl(MacroProps macros) {
        this.microPropsGenerator = NumberFormatterImpl.macrosToMicroGenerator(macros, this.micros, true);
    }

    public static int formatStatic(MacroProps macros, DecimalQuantity inValue, FormattedStringBuilder outString) {
        MicroProps micros = NumberFormatterImpl.preProcessUnsafe(macros, inValue);
        int length = NumberFormatterImpl.writeNumber(micros, inValue, outString, 0);
        length += NumberFormatterImpl.writeAffixes(micros, outString, 0, length);
        return length;
    }

    public static int getPrefixSuffixStatic(MacroProps macros, byte signum, StandardPlural plural, FormattedStringBuilder output) {
        MicroProps micros = new MicroProps(false);
        MicroPropsGenerator microPropsGenerator = NumberFormatterImpl.macrosToMicroGenerator(macros, micros, false);
        return NumberFormatterImpl.getPrefixSuffixImpl(microPropsGenerator, signum, output);
    }

    public int format(DecimalQuantity inValue, FormattedStringBuilder outString) {
        MicroProps micros = this.preProcess(inValue);
        int length = NumberFormatterImpl.writeNumber(micros, inValue, outString, 0);
        length += NumberFormatterImpl.writeAffixes(micros, outString, 0, length);
        return length;
    }

    public MicroProps preProcess(DecimalQuantity inValue) {
        MicroProps micros = this.microPropsGenerator.processQuantity(inValue);
        micros.rounder.apply(inValue);
        if (micros.integerWidth.maxInt == -1) {
            inValue.setMinInteger(micros.integerWidth.minInt);
        } else {
            inValue.setMinInteger(micros.integerWidth.minInt);
            inValue.applyMaxInteger(micros.integerWidth.maxInt);
        }
        return micros;
    }

    private static MicroProps preProcessUnsafe(MacroProps macros, DecimalQuantity inValue) {
        MicroProps micros = new MicroProps(false);
        MicroPropsGenerator microPropsGenerator = NumberFormatterImpl.macrosToMicroGenerator(macros, micros, false);
        micros = microPropsGenerator.processQuantity(inValue);
        micros.rounder.apply(inValue);
        if (micros.integerWidth.maxInt == -1) {
            inValue.setMinInteger(micros.integerWidth.minInt);
        } else {
            inValue.setMinInteger(micros.integerWidth.minInt);
            inValue.applyMaxInteger(micros.integerWidth.maxInt);
        }
        return micros;
    }

    public int getPrefixSuffix(byte signum, StandardPlural plural, FormattedStringBuilder output) {
        return NumberFormatterImpl.getPrefixSuffixImpl(this.microPropsGenerator, signum, output);
    }

    private static int getPrefixSuffixImpl(MicroPropsGenerator generator, byte signum, FormattedStringBuilder output) {
        DecimalQuantity_DualStorageBCD quantity = new DecimalQuantity_DualStorageBCD(0);
        if (signum < 0) {
            quantity.negate();
        }
        MicroProps micros = generator.processQuantity(quantity);
        micros.modMiddle.apply(output, 0, 0);
        return micros.modMiddle.getPrefixLength();
    }

    public MicroProps getRawMicroProps() {
        return this.micros;
    }

    private static boolean unitIsCurrency(MeasureUnit unit) {
        return unit != null && "currency".equals(unit.getType());
    }

    private static boolean unitIsNoUnit(MeasureUnit unit) {
        return unit == null || "none".equals(unit.getType());
    }

    private static boolean unitIsPercent(MeasureUnit unit) {
        return unit != null && "percent".equals(unit.getSubtype());
    }

    private static boolean unitIsPermille(MeasureUnit unit) {
        return unit != null && "permille".equals(unit.getSubtype());
    }

    private static MicroPropsGenerator macrosToMicroGenerator(MacroProps macros, MicroProps micros, boolean safe) {
        CurrencyData.CurrencyFormatInfo info;
        MicroPropsGenerator chain = micros;
        boolean isCurrency = NumberFormatterImpl.unitIsCurrency(macros.unit);
        boolean isNoUnit = NumberFormatterImpl.unitIsNoUnit(macros.unit);
        boolean isPercent = NumberFormatterImpl.unitIsPercent(macros.unit);
        boolean isPermille = NumberFormatterImpl.unitIsPermille(macros.unit);
        boolean isAccounting = macros.sign == NumberFormatter.SignDisplay.ACCOUNTING || macros.sign == NumberFormatter.SignDisplay.ACCOUNTING_ALWAYS || macros.sign == NumberFormatter.SignDisplay.ACCOUNTING_EXCEPT_ZERO;
        Currency currency = isCurrency ? (Currency)macros.unit : DEFAULT_CURRENCY;
        NumberFormatter.UnitWidth unitWidth = NumberFormatter.UnitWidth.SHORT;
        if (macros.unitWidth != null) {
            unitWidth = macros.unitWidth;
        }
        boolean isCldrUnit = !isCurrency && !isNoUnit && (unitWidth == NumberFormatter.UnitWidth.FULL_NAME || !isPercent && !isPermille);
        PluralRules rules = macros.rules;
        NumberingSystem ns = macros.symbols instanceof NumberingSystem ? (NumberingSystem)macros.symbols : NumberingSystem.getInstance(macros.loc);
        micros.nsName = ns.getName();
        micros.symbols = macros.symbols instanceof DecimalFormatSymbols ? (DecimalFormatSymbols)macros.symbols : DecimalFormatSymbols.forNumberingSystem(macros.loc, ns);
        String pattern = null;
        if (isCurrency && (info = CurrencyData.provider.getInstance(macros.loc, true).getFormatInfo(currency.getCurrencyCode())) != null) {
            pattern = info.currencyPattern;
            micros.symbols = (DecimalFormatSymbols)micros.symbols.clone();
            micros.symbols.setMonetaryDecimalSeparatorString(info.monetaryDecimalSeparator);
            micros.symbols.setMonetaryGroupingSeparatorString(info.monetaryGroupingSeparator);
        }
        if (pattern == null) {
            int patternStyle = isCldrUnit ? 0 : (isPercent || isPermille ? 2 : (!isCurrency || unitWidth == NumberFormatter.UnitWidth.FULL_NAME ? 0 : (isAccounting ? 7 : 1)));
            pattern = NumberFormat.getPatternForStyleAndNumberingSystem(macros.loc, micros.nsName, patternStyle);
        }
        PatternStringParser.ParsedPatternInfo patternInfo = PatternStringParser.parseToPatternInfo(pattern);
        if (macros.scale != null) {
            chain = new MultiplierFormatHandler(macros.scale, chain);
        }
        micros.rounder = macros.precision != null ? macros.precision : (macros.notation instanceof CompactNotation ? Precision.COMPACT_STRATEGY : (isCurrency ? Precision.MONETARY_STANDARD : Precision.DEFAULT_MAX_FRAC_6));
        if (macros.roundingMode != null) {
            micros.rounder = micros.rounder.withMode(RoundingUtils.mathContextUnlimited(macros.roundingMode));
        }
        micros.rounder = micros.rounder.withLocaleData(currency);
        micros.grouping = macros.grouping instanceof Grouper ? (Grouper)macros.grouping : (macros.grouping instanceof NumberFormatter.GroupingStrategy ? Grouper.forStrategy((NumberFormatter.GroupingStrategy)((Object)macros.grouping)) : (macros.notation instanceof CompactNotation ? Grouper.forStrategy(NumberFormatter.GroupingStrategy.MIN2) : Grouper.forStrategy(NumberFormatter.GroupingStrategy.AUTO)));
        micros.grouping = micros.grouping.withLocaleData(macros.loc, patternInfo);
        micros.padding = macros.padder != null ? macros.padder : Padder.NONE;
        micros.integerWidth = macros.integerWidth != null ? macros.integerWidth : IntegerWidth.DEFAULT;
        micros.sign = macros.sign != null ? macros.sign : NumberFormatter.SignDisplay.AUTO;
        micros.decimal = macros.decimal != null ? macros.decimal : NumberFormatter.DecimalSeparatorDisplay.AUTO;
        micros.useCurrency = isCurrency;
        if (macros.notation instanceof ScientificNotation) {
            chain = ((ScientificNotation)macros.notation).withLocaleData(micros.symbols, safe, chain);
        } else {
            micros.modInner = ConstantAffixModifier.EMPTY;
        }
        MutablePatternModifier patternMod = new MutablePatternModifier(false);
        patternMod.setPatternInfo(macros.affixProvider != null ? macros.affixProvider : patternInfo, null);
        patternMod.setPatternAttributes(micros.sign, isPermille);
        if (patternMod.needsPlurals()) {
            if (rules == null) {
                rules = PluralRules.forLocale(macros.loc);
            }
            patternMod.setSymbols(micros.symbols, currency, unitWidth, rules);
        } else {
            patternMod.setSymbols(micros.symbols, currency, unitWidth, null);
        }
        chain = safe ? patternMod.createImmutableAndChain(chain) : patternMod.addToChain(chain);
        if (isCldrUnit) {
            if (rules == null) {
                rules = PluralRules.forLocale(macros.loc);
            }
            chain = LongNameHandler.forMeasureUnit(macros.loc, macros.unit, macros.perUnit, unitWidth, rules, chain);
        } else if (isCurrency && unitWidth == NumberFormatter.UnitWidth.FULL_NAME) {
            if (rules == null) {
                rules = PluralRules.forLocale(macros.loc);
            }
            chain = LongNameHandler.forCurrencyLongNames(macros.loc, currency, rules, chain);
        } else {
            micros.modOuter = ConstantAffixModifier.EMPTY;
        }
        if (macros.notation instanceof CompactNotation) {
            if (rules == null) {
                rules = PluralRules.forLocale(macros.loc);
            }
            CompactData.CompactType compactType = macros.unit instanceof Currency && macros.unitWidth != NumberFormatter.UnitWidth.FULL_NAME ? CompactData.CompactType.CURRENCY : CompactData.CompactType.DECIMAL;
            chain = ((CompactNotation)macros.notation).withLocaleData(macros.loc, micros.nsName, compactType, rules, safe ? patternMod : null, chain);
        }
        return chain;
    }

    public static int writeAffixes(MicroProps micros, FormattedStringBuilder string, int start, int end) {
        int length = micros.modInner.apply(string, start, end);
        if (micros.padding.isValid()) {
            micros.padding.padAndApply(micros.modMiddle, micros.modOuter, string, start, end + length);
        } else {
            length += micros.modMiddle.apply(string, start, end + length);
            length += micros.modOuter.apply(string, start, end + length);
        }
        return length;
    }

    public static int writeNumber(MicroProps micros, DecimalQuantity quantity, FormattedStringBuilder string, int index) {
        int length = 0;
        if (quantity.isInfinite()) {
            length += string.insert(length + index, micros.symbols.getInfinity(), NumberFormat.Field.INTEGER);
        } else if (quantity.isNaN()) {
            length += string.insert(length + index, micros.symbols.getNaN(), NumberFormat.Field.INTEGER);
        } else {
            length += NumberFormatterImpl.writeIntegerDigits(micros, quantity, string, length + index);
            if (quantity.getLowerDisplayMagnitude() < 0 || micros.decimal == NumberFormatter.DecimalSeparatorDisplay.ALWAYS) {
                length += string.insert(length + index, micros.useCurrency ? micros.symbols.getMonetaryDecimalSeparatorString() : micros.symbols.getDecimalSeparatorString(), NumberFormat.Field.DECIMAL_SEPARATOR);
            }
            length += NumberFormatterImpl.writeFractionDigits(micros, quantity, string, length + index);
        }
        return length;
    }

    private static int writeIntegerDigits(MicroProps micros, DecimalQuantity quantity, FormattedStringBuilder string, int index) {
        int length = 0;
        int integerCount = quantity.getUpperDisplayMagnitude() + 1;
        for (int i = 0; i < integerCount; ++i) {
            if (micros.grouping.groupAtPosition(i, quantity)) {
                length += string.insert(index, micros.useCurrency ? micros.symbols.getMonetaryGroupingSeparatorString() : micros.symbols.getGroupingSeparatorString(), NumberFormat.Field.GROUPING_SEPARATOR);
            }
            byte nextDigit = quantity.getDigit(i);
            if (micros.symbols.getCodePointZero() != -1) {
                length += string.insertCodePoint(index, micros.symbols.getCodePointZero() + nextDigit, NumberFormat.Field.INTEGER);
                continue;
            }
            length += string.insert(index, micros.symbols.getDigitStringsLocal()[nextDigit], NumberFormat.Field.INTEGER);
        }
        return length;
    }

    private static int writeFractionDigits(MicroProps micros, DecimalQuantity quantity, FormattedStringBuilder string, int index) {
        int length = 0;
        int fractionCount = -quantity.getLowerDisplayMagnitude();
        for (int i = 0; i < fractionCount; ++i) {
            byte nextDigit = quantity.getDigit(-i - 1);
            if (micros.symbols.getCodePointZero() != -1) {
                length += string.insertCodePoint(length + index, micros.symbols.getCodePointZero() + nextDigit, NumberFormat.Field.FRACTION);
                continue;
            }
            length += string.insert(length + index, micros.symbols.getDigitStringsLocal()[nextDigit], NumberFormat.Field.FRACTION);
        }
        return length;
    }
}
