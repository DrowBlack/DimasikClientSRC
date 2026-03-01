package com.ibm.icu.impl;

import com.ibm.icu.impl.FormattedStringBuilder;
import com.ibm.icu.impl.StaticUnicodeSets;
import com.ibm.icu.text.ConstrainedFieldPosition;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.UnicodeSet;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.FieldPosition;
import java.text.Format;

public class FormattedValueStringBuilderImpl {
    public static boolean nextFieldPosition(FormattedStringBuilder self, FieldPosition fp) {
        Format.Field rawField = fp.getFieldAttribute();
        if (rawField == null) {
            if (fp.getField() == 0) {
                rawField = NumberFormat.Field.INTEGER;
            } else if (fp.getField() == 1) {
                rawField = NumberFormat.Field.FRACTION;
            } else {
                return false;
            }
        }
        if (!(rawField instanceof NumberFormat.Field)) {
            throw new IllegalArgumentException("You must pass an instance of com.ibm.icu.text.NumberFormat.Field as your FieldPosition attribute.  You passed: " + rawField.getClass().toString());
        }
        ConstrainedFieldPosition cfpos = new ConstrainedFieldPosition();
        cfpos.constrainField(rawField);
        cfpos.setState(rawField, null, fp.getBeginIndex(), fp.getEndIndex());
        if (FormattedValueStringBuilderImpl.nextPosition(self, cfpos, null)) {
            fp.setBeginIndex(cfpos.getStart());
            fp.setEndIndex(cfpos.getLimit());
            return true;
        }
        if (rawField == NumberFormat.Field.FRACTION && fp.getEndIndex() == 0) {
            int i;
            boolean inside = false;
            for (i = self.zero; i < self.zero + self.length; ++i) {
                if (FormattedValueStringBuilderImpl.isIntOrGroup(self.fields[i]) || self.fields[i] == NumberFormat.Field.DECIMAL_SEPARATOR) {
                    inside = true;
                    continue;
                }
                if (inside) break;
            }
            fp.setBeginIndex(i - self.zero);
            fp.setEndIndex(i - self.zero);
        }
        return false;
    }

    public static AttributedCharacterIterator toCharacterIterator(FormattedStringBuilder self, Format.Field numericField) {
        ConstrainedFieldPosition cfpos = new ConstrainedFieldPosition();
        AttributedString as = new AttributedString(self.toString());
        while (FormattedValueStringBuilderImpl.nextPosition(self, cfpos, numericField)) {
            as.addAttribute(cfpos.getField(), cfpos.getField(), cfpos.getStart(), cfpos.getLimit());
        }
        return as.getIterator();
    }

    public static boolean nextPosition(FormattedStringBuilder self, ConstrainedFieldPosition cfpos, Format.Field numericField) {
        int fieldStart = -1;
        NullField currField = null;
        for (int i = self.zero + cfpos.getLimit(); i <= self.zero + self.length; ++i) {
            int j;
            NullField _field;
            Format.Field field = _field = i < self.zero + self.length ? self.fields[i] : NullField.END;
            if (currField != null) {
                if (currField == _field) continue;
                int end = i - self.zero;
                if (currField != NumberFormat.Field.GROUPING_SEPARATOR) {
                    end = FormattedValueStringBuilderImpl.trimBack(self, end);
                }
                if (end <= fieldStart) {
                    fieldStart = -1;
                    currField = null;
                    --i;
                    continue;
                }
                int start = fieldStart;
                if (currField != NumberFormat.Field.GROUPING_SEPARATOR) {
                    start = FormattedValueStringBuilderImpl.trimFront(self, start);
                }
                cfpos.setState(currField, null, start, end);
                return true;
            }
            if (cfpos.matchesField(NumberFormat.Field.INTEGER, null) && i > self.zero && i - self.zero > cfpos.getLimit() && FormattedValueStringBuilderImpl.isIntOrGroup(self.fields[i - 1]) && !FormattedValueStringBuilderImpl.isIntOrGroup(_field)) {
                for (j = i - 1; j >= self.zero && FormattedValueStringBuilderImpl.isIntOrGroup(self.fields[j]); --j) {
                }
                cfpos.setState(NumberFormat.Field.INTEGER, null, j - self.zero + 1, i - self.zero);
                return true;
            }
            if (numericField != null && cfpos.matchesField(numericField, null) && i > self.zero && (i - self.zero > cfpos.getLimit() || cfpos.getField() != numericField) && FormattedValueStringBuilderImpl.isNumericField(self.fields[i - 1]) && !FormattedValueStringBuilderImpl.isNumericField(_field)) {
                for (j = i - 1; j >= self.zero && FormattedValueStringBuilderImpl.isNumericField(self.fields[j]); --j) {
                }
                cfpos.setState(numericField, null, j - self.zero + 1, i - self.zero);
                return true;
            }
            if (_field == NumberFormat.Field.INTEGER) {
                _field = null;
            }
            if (_field == null || _field == NullField.END || !cfpos.matchesField(_field, null)) continue;
            fieldStart = i - self.zero;
            currField = _field;
        }
        assert (currField == null);
        return false;
    }

    private static boolean isIntOrGroup(Format.Field field) {
        return field == NumberFormat.Field.INTEGER || field == NumberFormat.Field.GROUPING_SEPARATOR;
    }

    private static boolean isNumericField(Format.Field field) {
        return field == null || NumberFormat.Field.class.isAssignableFrom(field.getClass());
    }

    private static int trimBack(FormattedStringBuilder self, int limit) {
        return StaticUnicodeSets.get(StaticUnicodeSets.Key.DEFAULT_IGNORABLES).spanBack(self, limit, UnicodeSet.SpanCondition.CONTAINED);
    }

    private static int trimFront(FormattedStringBuilder self, int start) {
        return StaticUnicodeSets.get(StaticUnicodeSets.Key.DEFAULT_IGNORABLES).span(self, start, UnicodeSet.SpanCondition.CONTAINED);
    }

    static class NullField
    extends Format.Field {
        private static final long serialVersionUID = 1L;
        static final NullField END = new NullField("end");

        private NullField(String name) {
            super(name);
        }
    }
}
