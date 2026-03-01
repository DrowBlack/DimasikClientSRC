package com.ibm.icu.impl;

import com.ibm.icu.text.ConstrainedFieldPosition;
import java.io.Serializable;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.FieldPosition;
import java.text.Format;
import java.util.List;

public class FormattedValueFieldPositionIteratorImpl {
    private FormattedValueFieldPositionIteratorImpl() {
    }

    public static boolean nextPosition(List<FieldPosition> attributes, ConstrainedFieldPosition cfpos) {
        int i;
        int numFields = attributes.size();
        for (i = (int)cfpos.getInt64IterationContext(); i < numFields; ++i) {
            FieldPosition fpos = attributes.get(i);
            Format.Field field = fpos.getFieldAttribute();
            Integer value = null;
            if (field instanceof FieldWithValue) {
                value = ((FieldWithValue)field).value;
                field = ((FieldWithValue)field).field;
            }
            if (!cfpos.matchesField(field, value)) continue;
            int start = fpos.getBeginIndex();
            int limit = fpos.getEndIndex();
            cfpos.setState(field, value, start, limit);
            break;
        }
        cfpos.setInt64IterationContext(i == numFields ? (long)i : (long)(i + 1));
        return i < numFields;
    }

    public static AttributedCharacterIterator toCharacterIterator(CharSequence cs, List<FieldPosition> attributes) {
        AttributedString as = new AttributedString(cs.toString());
        for (int i = 0; i < attributes.size(); ++i) {
            Format.Field field;
            FieldPosition fp = attributes.get(i);
            Serializable value = field = fp.getFieldAttribute();
            if (field instanceof FieldWithValue) {
                value = Integer.valueOf(((FieldWithValue)field).value);
                field = ((FieldWithValue)field).field;
            }
            as.addAttribute(field, value, fp.getBeginIndex(), fp.getEndIndex());
        }
        return as.getIterator();
    }

    public static void addOverlapSpans(List<FieldPosition> attributes, Format.Field spanField, int firstIndex) {
        int s1a = Integer.MAX_VALUE;
        int s1b = 0;
        int s2a = Integer.MAX_VALUE;
        int s2b = 0;
        int numFields = attributes.size();
        block0: for (int i = 0; i < numFields; ++i) {
            FieldPosition fp1 = attributes.get(i);
            for (int j = i + 1; j < numFields; ++j) {
                FieldPosition fp2 = attributes.get(j);
                if (fp1.getFieldAttribute() != fp2.getFieldAttribute()) continue;
                s1a = Math.min(s1a, fp1.getBeginIndex());
                s1b = Math.max(s1b, fp1.getEndIndex());
                s2a = Math.min(s2a, fp2.getBeginIndex());
                s2b = Math.max(s2b, fp2.getEndIndex());
                continue block0;
            }
        }
        if (s1a != Integer.MAX_VALUE) {
            FieldPosition newPos = new FieldPosition(new FieldWithValue(spanField, firstIndex));
            newPos.setBeginIndex(s1a);
            newPos.setEndIndex(s1b);
            attributes.add(newPos);
            newPos = new FieldPosition(new FieldWithValue(spanField, 1 - firstIndex));
            newPos.setBeginIndex(s2a);
            newPos.setEndIndex(s2b);
            attributes.add(newPos);
        }
    }

    public static void sort(List<FieldPosition> attributes) {
        boolean isSorted;
        int numFields = attributes.size();
        do {
            isSorted = true;
            for (int i = 0; i < numFields - 1; ++i) {
                FieldPosition fp1 = attributes.get(i);
                FieldPosition fp2 = attributes.get(i + 1);
                long comparison = 0L;
                if (fp1.getBeginIndex() != fp2.getBeginIndex()) {
                    comparison = fp2.getBeginIndex() - fp1.getBeginIndex();
                } else if (fp1.getEndIndex() != fp2.getEndIndex()) {
                    comparison = fp1.getEndIndex() - fp2.getEndIndex();
                } else if (fp1.getFieldAttribute() != fp2.getFieldAttribute()) {
                    boolean fp1isSpan = fp1.getFieldAttribute() instanceof FieldWithValue;
                    boolean fp2isSpan = fp2.getFieldAttribute() instanceof FieldWithValue;
                    comparison = fp1isSpan && !fp2isSpan ? 1L : (fp2isSpan && !fp1isSpan ? -1L : (long)(fp1.hashCode() - fp2.hashCode()));
                }
                if (comparison >= 0L) continue;
                isSorted = false;
                attributes.set(i, fp2);
                attributes.set(i + 1, fp1);
            }
        } while (!isSorted);
    }

    private static class FieldWithValue
    extends Format.Field {
        private static final long serialVersionUID = -3850076447157793465L;
        public final Format.Field field;
        public final int value;

        public FieldWithValue(Format.Field field, int value) {
            super(field.toString());
            this.field = field;
            this.value = value;
        }
    }
}
