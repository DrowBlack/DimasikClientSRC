package com.ibm.icu.number;

import com.ibm.icu.impl.FormattedStringBuilder;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.PatternProps;
import com.ibm.icu.impl.SimpleFormatterImpl;
import com.ibm.icu.impl.StandardPlural;
import com.ibm.icu.impl.UResource;
import com.ibm.icu.impl.number.DecimalQuantity;
import com.ibm.icu.impl.number.MicroProps;
import com.ibm.icu.impl.number.Modifier;
import com.ibm.icu.impl.number.SimpleModifier;
import com.ibm.icu.impl.number.range.PrefixInfixSuffixLengthHelper;
import com.ibm.icu.impl.number.range.RangeMacroProps;
import com.ibm.icu.impl.number.range.StandardPluralRanges;
import com.ibm.icu.number.FormattedNumberRange;
import com.ibm.icu.number.NumberFormatter;
import com.ibm.icu.number.NumberFormatterImpl;
import com.ibm.icu.number.NumberRangeFormatter;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.util.MissingResourceException;

class NumberRangeFormatterImpl {
    final NumberFormatterImpl formatterImpl1;
    final NumberFormatterImpl formatterImpl2;
    final boolean fSameFormatters;
    final NumberRangeFormatter.RangeCollapse fCollapse;
    final NumberRangeFormatter.RangeIdentityFallback fIdentityFallback;
    String fRangePattern;
    SimpleModifier fApproximatelyModifier;
    final StandardPluralRanges fPluralRanges;

    int identity2d(NumberRangeFormatter.RangeIdentityFallback a, NumberRangeFormatter.RangeIdentityResult b) {
        return a.ordinal() | b.ordinal() << 4;
    }

    private static void getNumberRangeData(ULocale locale, String nsName, NumberRangeFormatterImpl out) {
        StringBuilder sb = new StringBuilder();
        NumberRangeDataSink sink = new NumberRangeDataSink(sb);
        ICUResourceBundle resource = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt66b", locale);
        sb.append("NumberElements/");
        sb.append(nsName);
        sb.append("/miscPatterns");
        String key = sb.toString();
        try {
            resource.getAllItemsWithFallback(key, sink);
        }
        catch (MissingResourceException missingResourceException) {
            // empty catch block
        }
        if (!sink.isComplete()) {
            resource.getAllItemsWithFallback("NumberElements/latn/miscPatterns", sink);
        }
        sink.fillInDefaults();
        out.fRangePattern = sink.rangePattern;
        out.fApproximatelyModifier = new SimpleModifier(sink.approximatelyPattern, null, false);
    }

    public NumberRangeFormatterImpl(RangeMacroProps macros) {
        this.formatterImpl1 = new NumberFormatterImpl(macros.formatter1 != null ? macros.formatter1.resolve() : NumberFormatter.withLocale(macros.loc).resolve());
        this.formatterImpl2 = new NumberFormatterImpl(macros.formatter2 != null ? macros.formatter2.resolve() : NumberFormatter.withLocale(macros.loc).resolve());
        this.fSameFormatters = macros.sameFormatters != 0;
        this.fCollapse = macros.collapse != null ? macros.collapse : NumberRangeFormatter.RangeCollapse.AUTO;
        this.fIdentityFallback = macros.identityFallback != null ? macros.identityFallback : NumberRangeFormatter.RangeIdentityFallback.APPROXIMATELY;
        String nsName = this.formatterImpl1.getRawMicroProps().nsName;
        if (nsName == null || !nsName.equals(this.formatterImpl2.getRawMicroProps().nsName)) {
            throw new IllegalArgumentException("Both formatters must have same numbering system");
        }
        NumberRangeFormatterImpl.getNumberRangeData(macros.loc, nsName, this);
        this.fPluralRanges = new StandardPluralRanges(macros.loc);
    }

    public FormattedNumberRange format(DecimalQuantity quantity1, DecimalQuantity quantity2, boolean equalBeforeRounding) {
        FormattedStringBuilder string = new FormattedStringBuilder();
        MicroProps micros1 = this.formatterImpl1.preProcess(quantity1);
        MicroProps micros2 = this.fSameFormatters ? this.formatterImpl1.preProcess(quantity2) : this.formatterImpl2.preProcess(quantity2);
        if (!(micros1.modInner.semanticallyEquivalent(micros2.modInner) && micros1.modMiddle.semanticallyEquivalent(micros2.modMiddle) && micros1.modOuter.semanticallyEquivalent(micros2.modOuter))) {
            this.formatRange(quantity1, quantity2, string, micros1, micros2);
            return new FormattedNumberRange(string, quantity1, quantity2, NumberRangeFormatter.RangeIdentityResult.NOT_EQUAL);
        }
        NumberRangeFormatter.RangeIdentityResult identityResult = equalBeforeRounding ? NumberRangeFormatter.RangeIdentityResult.EQUAL_BEFORE_ROUNDING : (quantity1.equals(quantity2) ? NumberRangeFormatter.RangeIdentityResult.EQUAL_AFTER_ROUNDING : NumberRangeFormatter.RangeIdentityResult.NOT_EQUAL);
        switch (this.identity2d(this.fIdentityFallback, identityResult)) {
            case 3: 
            case 19: 
            case 32: 
            case 33: 
            case 34: 
            case 35: {
                this.formatRange(quantity1, quantity2, string, micros1, micros2);
                break;
            }
            case 2: 
            case 17: 
            case 18: {
                this.formatApproximately(quantity1, quantity2, string, micros1, micros2);
                break;
            }
            case 0: 
            case 1: 
            case 16: {
                this.formatSingleValue(quantity1, quantity2, string, micros1, micros2);
                break;
            }
            default: {
                assert (false);
                break;
            }
        }
        return new FormattedNumberRange(string, quantity1, quantity2, identityResult);
    }

    private void formatSingleValue(DecimalQuantity quantity1, DecimalQuantity quantity2, FormattedStringBuilder string, MicroProps micros1, MicroProps micros2) {
        if (this.fSameFormatters) {
            int length = NumberFormatterImpl.writeNumber(micros1, quantity1, string, 0);
            NumberFormatterImpl.writeAffixes(micros1, string, 0, length);
        } else {
            this.formatRange(quantity1, quantity2, string, micros1, micros2);
        }
    }

    private void formatApproximately(DecimalQuantity quantity1, DecimalQuantity quantity2, FormattedStringBuilder string, MicroProps micros1, MicroProps micros2) {
        if (this.fSameFormatters) {
            int length = NumberFormatterImpl.writeNumber(micros1, quantity1, string, 0);
            length += micros1.modInner.apply(string, 0, length);
            length += micros1.modMiddle.apply(string, 0, length);
            length += this.fApproximatelyModifier.apply(string, 0, length);
            micros1.modOuter.apply(string, 0, length);
        } else {
            this.formatRange(quantity1, quantity2, string, micros1, micros2);
        }
    }

    private void formatRange(DecimalQuantity quantity1, DecimalQuantity quantity2, FormattedStringBuilder string, MicroProps micros1, MicroProps micros2) {
        boolean repeatOuter;
        boolean collapseInner;
        boolean collapseMiddle;
        boolean collapseOuter;
        switch (this.fCollapse) {
            case ALL: 
            case AUTO: 
            case UNIT: {
                collapseOuter = micros1.modOuter.semanticallyEquivalent(micros2.modOuter);
                if (!collapseOuter) {
                    collapseMiddle = false;
                    collapseInner = false;
                    break;
                }
                collapseMiddle = micros1.modMiddle.semanticallyEquivalent(micros2.modMiddle);
                if (!collapseMiddle) {
                    collapseInner = false;
                    break;
                }
                Modifier mm = micros1.modMiddle;
                if (this.fCollapse == NumberRangeFormatter.RangeCollapse.UNIT) {
                    if (!mm.containsField(NumberFormat.Field.CURRENCY) && !mm.containsField(NumberFormat.Field.PERCENT)) {
                        collapseMiddle = false;
                    }
                } else if (this.fCollapse == NumberRangeFormatter.RangeCollapse.AUTO && mm.getCodePointCount() <= 1) {
                    collapseMiddle = false;
                }
                if (!collapseMiddle || this.fCollapse != NumberRangeFormatter.RangeCollapse.ALL) {
                    collapseInner = false;
                    break;
                }
                collapseInner = micros1.modInner.semanticallyEquivalent(micros2.modInner);
                break;
            }
            default: {
                collapseOuter = false;
                collapseMiddle = false;
                collapseInner = false;
            }
        }
        PrefixInfixSuffixLengthHelper h = new PrefixInfixSuffixLengthHelper();
        SimpleModifier.formatTwoArgPattern(this.fRangePattern, string, 0, h, null);
        assert (h.lengthInfix > 0);
        boolean repeatInner = !collapseInner && micros1.modInner.getCodePointCount() > 0;
        boolean repeatMiddle = !collapseMiddle && micros1.modMiddle.getCodePointCount() > 0;
        boolean bl = repeatOuter = !collapseOuter && micros1.modOuter.getCodePointCount() > 0;
        if (repeatInner || repeatMiddle || repeatOuter) {
            if (!PatternProps.isWhiteSpace(string.charAt(h.index1()))) {
                h.lengthInfix += string.insertCodePoint(h.index1(), 32, null);
            }
            if (!PatternProps.isWhiteSpace(string.charAt(h.index2() - 1))) {
                h.lengthInfix += string.insertCodePoint(h.index2(), 32, null);
            }
        }
        h.length1 += NumberFormatterImpl.writeNumber(micros1, quantity1, string, h.index0());
        h.length2 += NumberFormatterImpl.writeNumber(micros2, quantity2, string, h.index2());
        if (collapseInner) {
            Modifier mod = this.resolveModifierPlurals(micros1.modInner, micros2.modInner);
            h.lengthInfix += mod.apply(string, h.index0(), h.index3());
        } else {
            h.length1 += micros1.modInner.apply(string, h.index0(), h.index1());
            h.length2 += micros2.modInner.apply(string, h.index2(), h.index3());
        }
        if (collapseMiddle) {
            Modifier mod = this.resolveModifierPlurals(micros1.modMiddle, micros2.modMiddle);
            h.lengthInfix += mod.apply(string, h.index0(), h.index3());
        } else {
            h.length1 += micros1.modMiddle.apply(string, h.index0(), h.index1());
            h.length2 += micros2.modMiddle.apply(string, h.index2(), h.index3());
        }
        if (collapseOuter) {
            Modifier mod = this.resolveModifierPlurals(micros1.modOuter, micros2.modOuter);
            h.lengthInfix += mod.apply(string, h.index0(), h.index3());
        } else {
            h.length1 += micros1.modOuter.apply(string, h.index0(), h.index1());
            h.length2 += micros2.modOuter.apply(string, h.index2(), h.index3());
        }
    }

    Modifier resolveModifierPlurals(Modifier first, Modifier second) {
        Modifier.Parameters firstParameters = first.getParameters();
        if (firstParameters == null) {
            return first;
        }
        Modifier.Parameters secondParameters = second.getParameters();
        if (secondParameters == null) {
            return first;
        }
        StandardPlural resultPlural = this.fPluralRanges.resolve(firstParameters.plural, secondParameters.plural);
        assert (firstParameters.obj == secondParameters.obj);
        assert (firstParameters.signum == secondParameters.signum);
        Modifier mod = firstParameters.obj.getModifier(firstParameters.signum, resultPlural);
        assert (mod != null);
        return mod;
    }

    private static final class NumberRangeDataSink
    extends UResource.Sink {
        String rangePattern;
        String approximatelyPattern;
        StringBuilder sb;

        NumberRangeDataSink(StringBuilder sb) {
            this.sb = sb;
        }

        @Override
        public void put(UResource.Key key, UResource.Value value, boolean noFallback) {
            UResource.Table miscTable = value.getTable();
            int i = 0;
            while (miscTable.getKeyAndValue(i, key, value)) {
                String pattern;
                if (key.contentEquals("range") && !this.hasRangeData()) {
                    pattern = value.getString();
                    this.rangePattern = SimpleFormatterImpl.compileToStringMinMaxArguments(pattern, this.sb, 2, 2);
                }
                if (key.contentEquals("approximately") && !this.hasApproxData()) {
                    pattern = value.getString();
                    this.approximatelyPattern = SimpleFormatterImpl.compileToStringMinMaxArguments(pattern, this.sb, 1, 1);
                }
                ++i;
            }
        }

        private boolean hasRangeData() {
            return this.rangePattern != null;
        }

        private boolean hasApproxData() {
            return this.approximatelyPattern != null;
        }

        public boolean isComplete() {
            return this.hasRangeData() && this.hasApproxData();
        }

        public void fillInDefaults() {
            if (!this.hasRangeData()) {
                this.rangePattern = SimpleFormatterImpl.compileToStringMinMaxArguments("{0}\u2013{1}", this.sb, 2, 2);
            }
            if (!this.hasApproxData()) {
                this.approximatelyPattern = SimpleFormatterImpl.compileToStringMinMaxArguments("~{0}", this.sb, 1, 1);
            }
        }
    }
}
