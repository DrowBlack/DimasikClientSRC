package com.ibm.icu.number;

import com.ibm.icu.impl.FormattedStringBuilder;
import com.ibm.icu.impl.StandardPlural;
import com.ibm.icu.impl.number.DecimalQuantity;
import com.ibm.icu.impl.number.DecimalQuantity_DualStorageBCD;
import com.ibm.icu.impl.number.LocalizedNumberFormatterAsFormat;
import com.ibm.icu.impl.number.MacroProps;
import com.ibm.icu.number.FormattedNumber;
import com.ibm.icu.number.NumberFormatterImpl;
import com.ibm.icu.number.NumberFormatterSettings;
import com.ibm.icu.util.Measure;
import com.ibm.icu.util.MeasureUnit;
import java.text.Format;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class LocalizedNumberFormatter
extends NumberFormatterSettings<LocalizedNumberFormatter> {
    static final AtomicLongFieldUpdater<LocalizedNumberFormatter> callCount = AtomicLongFieldUpdater.newUpdater(LocalizedNumberFormatter.class, "callCountInternal");
    volatile long callCountInternal;
    volatile LocalizedNumberFormatter savedWithUnit;
    volatile NumberFormatterImpl compiled;

    LocalizedNumberFormatter(NumberFormatterSettings<?> parent, int key, Object value) {
        super(parent, key, value);
    }

    public FormattedNumber format(long input) {
        return this.format(new DecimalQuantity_DualStorageBCD(input));
    }

    public FormattedNumber format(double input) {
        return this.format(new DecimalQuantity_DualStorageBCD(input));
    }

    public FormattedNumber format(Number input) {
        return this.format(new DecimalQuantity_DualStorageBCD(input));
    }

    public FormattedNumber format(Measure input) {
        MeasureUnit unit = input.getUnit();
        Number number = input.getNumber();
        if (Objects.equals(this.resolve().unit, unit)) {
            return this.format(number);
        }
        LocalizedNumberFormatter withUnit = this.savedWithUnit;
        if (withUnit == null || !Objects.equals(withUnit.resolve().unit, unit)) {
            this.savedWithUnit = withUnit = new LocalizedNumberFormatter(this, 3, unit);
        }
        return withUnit.format(number);
    }

    public Format toFormat() {
        return new LocalizedNumberFormatterAsFormat(this, this.resolve().loc);
    }

    private FormattedNumber format(DecimalQuantity fq) {
        FormattedStringBuilder string = new FormattedStringBuilder();
        this.formatImpl(fq, string);
        return new FormattedNumber(string, fq);
    }

    @Deprecated
    public void formatImpl(DecimalQuantity fq, FormattedStringBuilder string) {
        if (this.computeCompiled()) {
            this.compiled.format(fq, string);
        } else {
            NumberFormatterImpl.formatStatic(this.resolve(), fq, string);
        }
    }

    @Deprecated
    public String getAffixImpl(boolean isPrefix, boolean isNegative) {
        FormattedStringBuilder string = new FormattedStringBuilder();
        byte signum = (byte)(isNegative ? -1 : 1);
        StandardPlural plural = StandardPlural.OTHER;
        int prefixLength = this.computeCompiled() ? this.compiled.getPrefixSuffix(signum, plural, string) : NumberFormatterImpl.getPrefixSuffixStatic(this.resolve(), signum, plural, string);
        if (isPrefix) {
            return string.subSequence(0, prefixLength).toString();
        }
        return string.subSequence(prefixLength, string.length()).toString();
    }

    private boolean computeCompiled() {
        MacroProps macros = this.resolve();
        long currentCount = callCount.incrementAndGet(this);
        if (currentCount == macros.threshold) {
            this.compiled = new NumberFormatterImpl(macros);
            return true;
        }
        return this.compiled != null;
    }

    @Override
    LocalizedNumberFormatter create(int key, Object value) {
        return new LocalizedNumberFormatter(this, key, value);
    }
}
