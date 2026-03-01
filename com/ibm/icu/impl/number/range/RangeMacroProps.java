package com.ibm.icu.impl.number.range;

import com.ibm.icu.number.NumberRangeFormatter;
import com.ibm.icu.number.UnlocalizedNumberFormatter;
import com.ibm.icu.util.ULocale;
import java.util.Objects;

public class RangeMacroProps {
    public UnlocalizedNumberFormatter formatter1;
    public UnlocalizedNumberFormatter formatter2;
    public int sameFormatters = -1;
    public NumberRangeFormatter.RangeCollapse collapse;
    public NumberRangeFormatter.RangeIdentityFallback identityFallback;
    public ULocale loc;

    public int hashCode() {
        return Objects.hash(new Object[]{this.formatter1, this.formatter2, this.collapse, this.identityFallback, this.loc});
    }

    public boolean equals(Object _other) {
        if (_other == null) {
            return false;
        }
        if (this == _other) {
            return true;
        }
        if (!(_other instanceof RangeMacroProps)) {
            return false;
        }
        RangeMacroProps other = (RangeMacroProps)_other;
        return Objects.equals(this.formatter1, other.formatter1) && Objects.equals(this.formatter2, other.formatter2) && Objects.equals((Object)this.collapse, (Object)other.collapse) && Objects.equals((Object)this.identityFallback, (Object)other.identityFallback) && Objects.equals(this.loc, other.loc);
    }
}
