package com.ibm.icu.number;

import com.ibm.icu.impl.number.DecimalQuantity;
import com.ibm.icu.impl.number.DecimalQuantity_DualStorageBCD;
import com.ibm.icu.number.FormattedNumberRange;
import com.ibm.icu.number.NumberRangeFormatterImpl;
import com.ibm.icu.number.NumberRangeFormatterSettings;

public class LocalizedNumberRangeFormatter
extends NumberRangeFormatterSettings<LocalizedNumberRangeFormatter> {
    private volatile NumberRangeFormatterImpl fImpl;

    LocalizedNumberRangeFormatter(NumberRangeFormatterSettings<?> parent, int key, Object value) {
        super(parent, key, value);
    }

    public FormattedNumberRange formatRange(int first, int second) {
        DecimalQuantity_DualStorageBCD dq1 = new DecimalQuantity_DualStorageBCD(first);
        DecimalQuantity_DualStorageBCD dq2 = new DecimalQuantity_DualStorageBCD(second);
        return this.formatImpl(dq1, dq2, first == second);
    }

    public FormattedNumberRange formatRange(double first, double second) {
        DecimalQuantity_DualStorageBCD dq1 = new DecimalQuantity_DualStorageBCD(first);
        DecimalQuantity_DualStorageBCD dq2 = new DecimalQuantity_DualStorageBCD(second);
        return this.formatImpl(dq1, dq2, first == second);
    }

    public FormattedNumberRange formatRange(Number first, Number second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Cannot format null values in range");
        }
        DecimalQuantity_DualStorageBCD dq1 = new DecimalQuantity_DualStorageBCD(first);
        DecimalQuantity_DualStorageBCD dq2 = new DecimalQuantity_DualStorageBCD(second);
        return this.formatImpl(dq1, dq2, first.equals(second));
    }

    FormattedNumberRange formatImpl(DecimalQuantity first, DecimalQuantity second, boolean equalBeforeRounding) {
        if (this.fImpl == null) {
            this.fImpl = new NumberRangeFormatterImpl(this.resolve());
        }
        return this.fImpl.format(first, second, equalBeforeRounding);
    }

    @Override
    LocalizedNumberRangeFormatter create(int key, Object value) {
        return new LocalizedNumberRangeFormatter(this, key, value);
    }
}
