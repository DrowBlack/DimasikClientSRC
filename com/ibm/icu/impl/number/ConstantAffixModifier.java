package com.ibm.icu.impl.number;

import com.ibm.icu.impl.FormattedStringBuilder;
import com.ibm.icu.impl.number.Modifier;
import java.text.Format;

public class ConstantAffixModifier
implements Modifier {
    public static final ConstantAffixModifier EMPTY = new ConstantAffixModifier();
    private final String prefix;
    private final String suffix;
    private final Format.Field field;
    private final boolean strong;

    public ConstantAffixModifier(String prefix, String suffix, Format.Field field, boolean strong) {
        this.prefix = prefix == null ? "" : prefix;
        this.suffix = suffix == null ? "" : suffix;
        this.field = field;
        this.strong = strong;
    }

    public ConstantAffixModifier() {
        this.prefix = "";
        this.suffix = "";
        this.field = null;
        this.strong = false;
    }

    @Override
    public int apply(FormattedStringBuilder output, int leftIndex, int rightIndex) {
        int length = output.insert(rightIndex, this.suffix, this.field);
        return length += output.insert(leftIndex, this.prefix, this.field);
    }

    @Override
    public int getPrefixLength() {
        return this.prefix.length();
    }

    @Override
    public int getCodePointCount() {
        return this.prefix.codePointCount(0, this.prefix.length()) + this.suffix.codePointCount(0, this.suffix.length());
    }

    @Override
    public boolean isStrong() {
        return this.strong;
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
        if (!(other instanceof ConstantAffixModifier)) {
            return false;
        }
        ConstantAffixModifier _other = (ConstantAffixModifier)other;
        return this.prefix.equals(_other.prefix) && this.suffix.equals(_other.suffix) && this.field == _other.field && this.strong == _other.strong;
    }

    public String toString() {
        return String.format("<ConstantAffixModifier prefix:'%s' suffix:'%s'>", this.prefix, this.suffix);
    }
}
