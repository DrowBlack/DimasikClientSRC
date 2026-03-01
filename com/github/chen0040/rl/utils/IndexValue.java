package com.github.chen0040.rl.utils;

public class IndexValue {
    private int index;
    private double value;

    public IndexValue() {
    }

    public IndexValue(int index, double value) {
        this.index = index;
        this.value = value;
    }

    public IndexValue makeCopy() {
        IndexValue clone = new IndexValue();
        clone.setValue(this.value);
        clone.setIndex(this.index);
        return clone;
    }

    public boolean equals(Object rhs) {
        if (rhs != null && rhs instanceof IndexValue) {
            IndexValue rhs2 = (IndexValue)rhs;
            return this.index == rhs2.index && this.value == rhs2.value;
        }
        return false;
    }

    public boolean isValid() {
        return this.index != -1;
    }

    public int getIndex() {
        return this.index;
    }

    public double getValue() {
        return this.value;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
