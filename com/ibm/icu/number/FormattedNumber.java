package com.ibm.icu.number;

import com.ibm.icu.impl.FormattedStringBuilder;
import com.ibm.icu.impl.FormattedValueStringBuilderImpl;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.impl.number.DecimalQuantity;
import com.ibm.icu.text.ConstrainedFieldPosition;
import com.ibm.icu.text.FormattedValue;
import com.ibm.icu.text.PluralRules;
import java.math.BigDecimal;
import java.text.AttributedCharacterIterator;
import java.text.FieldPosition;
import java.util.Arrays;

public class FormattedNumber
implements FormattedValue {
    final FormattedStringBuilder string;
    final DecimalQuantity fq;

    FormattedNumber(FormattedStringBuilder nsb, DecimalQuantity fq) {
        this.string = nsb;
        this.fq = fq;
    }

    @Override
    public String toString() {
        return this.string.toString();
    }

    @Override
    public int length() {
        return this.string.length();
    }

    @Override
    public char charAt(int index) {
        return this.string.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return this.string.subString(start, end);
    }

    @Override
    public <A extends Appendable> A appendTo(A appendable) {
        return Utility.appendTo(this.string, appendable);
    }

    @Override
    public boolean nextPosition(ConstrainedFieldPosition cfpos) {
        return FormattedValueStringBuilderImpl.nextPosition(this.string, cfpos, null);
    }

    @Override
    public AttributedCharacterIterator toCharacterIterator() {
        return FormattedValueStringBuilderImpl.toCharacterIterator(this.string, null);
    }

    public boolean nextFieldPosition(FieldPosition fieldPosition) {
        this.fq.populateUFieldPosition(fieldPosition);
        return FormattedValueStringBuilderImpl.nextFieldPosition(this.string, fieldPosition);
    }

    public BigDecimal toBigDecimal() {
        return this.fq.toBigDecimal();
    }

    @Deprecated
    public PluralRules.IFixedDecimal getFixedDecimal() {
        return this.fq;
    }

    public int hashCode() {
        return Arrays.hashCode(this.string.toCharArray()) ^ Arrays.hashCode(this.string.toFieldArray()) ^ this.fq.toBigDecimal().hashCode();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof FormattedNumber)) {
            return false;
        }
        FormattedNumber _other = (FormattedNumber)other;
        return Arrays.equals(this.string.toCharArray(), _other.string.toCharArray()) && Arrays.equals(this.string.toFieldArray(), _other.string.toFieldArray()) && this.fq.toBigDecimal().equals(_other.fq.toBigDecimal());
    }
}
