package com.ibm.icu.text;

import java.text.Format;
import java.util.Objects;

public class ConstrainedFieldPosition {
    private ConstraintType fConstraint;
    private Class<?> fClassConstraint;
    private Format.Field fField;
    private Object fValue;
    private int fStart;
    private int fLimit;
    private long fContext;

    public ConstrainedFieldPosition() {
        this.reset();
    }

    public void reset() {
        this.fConstraint = ConstraintType.NONE;
        this.fClassConstraint = Object.class;
        this.fField = null;
        this.fValue = null;
        this.fStart = 0;
        this.fLimit = 0;
        this.fContext = 0L;
    }

    public void constrainField(Format.Field field) {
        if (field == null) {
            throw new IllegalArgumentException("Cannot constrain on null field");
        }
        this.fConstraint = ConstraintType.FIELD;
        this.fClassConstraint = Object.class;
        this.fField = field;
        this.fValue = null;
    }

    public void constrainClass(Class<?> classConstraint) {
        if (classConstraint == null) {
            throw new IllegalArgumentException("Cannot constrain on null field class");
        }
        this.fConstraint = ConstraintType.CLASS;
        this.fClassConstraint = classConstraint;
        this.fField = null;
        this.fValue = null;
    }

    @Deprecated
    public void constrainFieldAndValue(Format.Field field, Object fieldValue) {
        this.fConstraint = ConstraintType.VALUE;
        this.fClassConstraint = Object.class;
        this.fField = field;
        this.fValue = fieldValue;
    }

    public Format.Field getField() {
        return this.fField;
    }

    public int getStart() {
        return this.fStart;
    }

    public int getLimit() {
        return this.fLimit;
    }

    public Object getFieldValue() {
        return this.fValue;
    }

    public long getInt64IterationContext() {
        return this.fContext;
    }

    public void setInt64IterationContext(long context) {
        this.fContext = context;
    }

    public void setState(Format.Field field, Object value, int start, int limit) {
        assert (this.matchesField(field, value));
        this.fField = field;
        this.fValue = value;
        this.fStart = start;
        this.fLimit = limit;
    }

    public boolean matchesField(Format.Field field, Object fieldValue) {
        if (field == null) {
            throw new IllegalArgumentException("field must not be null");
        }
        switch (this.fConstraint) {
            case NONE: {
                return true;
            }
            case CLASS: {
                return this.fClassConstraint.isAssignableFrom(field.getClass());
            }
            case FIELD: {
                return this.fField == field;
            }
            case VALUE: {
                return this.fField == field && Objects.equals(this.fValue, fieldValue);
            }
        }
        throw new AssertionError();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CFPos[");
        sb.append(this.fStart);
        sb.append('-');
        sb.append(this.fLimit);
        sb.append(' ');
        sb.append(this.fField);
        sb.append(']');
        return sb.toString();
    }

    private static enum ConstraintType {
        NONE,
        CLASS,
        FIELD,
        VALUE;

    }
}
