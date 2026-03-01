package com.ibm.icu.number;

import com.ibm.icu.impl.FormattedStringBuilder;
import com.ibm.icu.impl.FormattedValueStringBuilderImpl;
import com.ibm.icu.impl.number.DecimalQuantity;
import com.ibm.icu.number.NumberRangeFormatter;
import com.ibm.icu.text.ConstrainedFieldPosition;
import com.ibm.icu.text.FormattedValue;
import com.ibm.icu.util.ICUUncheckedIOException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.AttributedCharacterIterator;
import java.text.FieldPosition;
import java.util.Arrays;

public class FormattedNumberRange
implements FormattedValue {
    final FormattedStringBuilder string;
    final DecimalQuantity quantity1;
    final DecimalQuantity quantity2;
    final NumberRangeFormatter.RangeIdentityResult identityResult;

    FormattedNumberRange(FormattedStringBuilder string, DecimalQuantity quantity1, DecimalQuantity quantity2, NumberRangeFormatter.RangeIdentityResult identityResult) {
        this.string = string;
        this.quantity1 = quantity1;
        this.quantity2 = quantity2;
        this.identityResult = identityResult;
    }

    @Override
    public String toString() {
        return this.string.toString();
    }

    @Override
    public <A extends Appendable> A appendTo(A appendable) {
        try {
            appendable.append(this.string);
        }
        catch (IOException e) {
            throw new ICUUncheckedIOException(e);
        }
        return appendable;
    }

    @Override
    public char charAt(int index) {
        return this.string.charAt(index);
    }

    @Override
    public int length() {
        return this.string.length();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return this.string.subString(start, end);
    }

    @Override
    public boolean nextPosition(ConstrainedFieldPosition cfpos) {
        return FormattedValueStringBuilderImpl.nextPosition(this.string, cfpos, null);
    }

    public boolean nextFieldPosition(FieldPosition fieldPosition) {
        return FormattedValueStringBuilderImpl.nextFieldPosition(this.string, fieldPosition);
    }

    @Override
    public AttributedCharacterIterator toCharacterIterator() {
        return FormattedValueStringBuilderImpl.toCharacterIterator(this.string, null);
    }

    public BigDecimal getFirstBigDecimal() {
        return this.quantity1.toBigDecimal();
    }

    public BigDecimal getSecondBigDecimal() {
        return this.quantity2.toBigDecimal();
    }

    public NumberRangeFormatter.RangeIdentityResult getIdentityResult() {
        return this.identityResult;
    }

    public int hashCode() {
        return Arrays.hashCode(this.string.toCharArray()) ^ Arrays.hashCode(this.string.toFieldArray()) ^ this.quantity1.toBigDecimal().hashCode() ^ this.quantity2.toBigDecimal().hashCode();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof FormattedNumberRange)) {
            return false;
        }
        FormattedNumberRange _other = (FormattedNumberRange)other;
        return Arrays.equals(this.string.toCharArray(), _other.string.toCharArray()) && Arrays.equals(this.string.toFieldArray(), _other.string.toFieldArray()) && this.quantity1.toBigDecimal().equals(_other.quantity1.toBigDecimal()) && this.quantity2.toBigDecimal().equals(_other.quantity2.toBigDecimal());
    }
}
