package com.github.chen0040.rl.utils;

import com.github.chen0040.rl.utils.DoubleUtils;
import com.github.chen0040.rl.utils.IndexValue;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Vec
implements Serializable {
    private Map<Integer, Double> data = new HashMap<Integer, Double>();
    private int dimension;
    private double defaultValue;
    private int id = -1;

    public Vec() {
    }

    public Vec(double[] v) {
        for (int i = 0; i < v.length; ++i) {
            this.set(i, v[i]);
        }
    }

    public Vec(int dimension) {
        this.dimension = dimension;
        this.defaultValue = 0.0;
    }

    public Vec(int dimension, Map<Integer, Double> data) {
        this.dimension = dimension;
        this.defaultValue = 0.0;
        for (Map.Entry<Integer, Double> entry : data.entrySet()) {
            this.set(entry.getKey(), entry.getValue());
        }
    }

    public Vec makeCopy() {
        Vec clone = new Vec(this.dimension);
        clone.copy(this);
        return clone;
    }

    public void copy(Vec rhs) {
        this.defaultValue = rhs.defaultValue;
        this.dimension = rhs.dimension;
        this.id = rhs.id;
        this.data.clear();
        for (Map.Entry<Integer, Double> entry : rhs.data.entrySet()) {
            this.data.put(entry.getKey(), entry.getValue());
        }
    }

    public void set(int i, double value) {
        if (value == this.defaultValue) {
            return;
        }
        this.data.put(i, value);
        if (i >= this.dimension) {
            this.dimension = i + 1;
        }
    }

    public double get(int i) {
        return this.data.getOrDefault(i, this.defaultValue);
    }

    public boolean equals(Object rhs) {
        if (rhs != null && rhs instanceof Vec) {
            Vec rhs2 = (Vec)rhs;
            if (this.dimension != rhs2.dimension) {
                return false;
            }
            if (this.data.size() != rhs2.data.size()) {
                return false;
            }
            for (Integer index : this.data.keySet()) {
                if (!rhs2.data.containsKey(index)) {
                    return false;
                }
                if (DoubleUtils.equals(this.data.get(index), rhs2.data.get(index))) continue;
                return false;
            }
            if (this.defaultValue != rhs2.defaultValue) {
                for (int i = 0; i < this.dimension; ++i) {
                    if (!this.data.containsKey(i)) continue;
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void setAll(double value) {
        this.defaultValue = value;
        for (Integer index : this.data.keySet()) {
            this.data.put(index, this.defaultValue);
        }
    }

    public IndexValue indexWithMaxValue(Set<Integer> indices) {
        if (indices == null) {
            return this.indexWithMaxValue();
        }
        IndexValue iv = new IndexValue();
        iv.setIndex(-1);
        iv.setValue(Double.NEGATIVE_INFINITY);
        for (Integer index : indices) {
            double value = this.data.getOrDefault(index, Double.NEGATIVE_INFINITY);
            if (!(value > iv.getValue())) continue;
            iv.setIndex(index);
            iv.setValue(value);
        }
        return iv;
    }

    public IndexValue indexWithMaxValue() {
        IndexValue iv = new IndexValue();
        iv.setIndex(-1);
        iv.setValue(Double.NEGATIVE_INFINITY);
        for (Map.Entry<Integer, Double> entry : this.data.entrySet()) {
            double value;
            if (entry.getKey() >= this.dimension || !((value = entry.getValue().doubleValue()) > iv.getValue())) continue;
            iv.setValue(value);
            iv.setIndex(entry.getKey());
        }
        if (!iv.isValid()) {
            iv.setValue(this.defaultValue);
        } else if (iv.getValue() < this.defaultValue) {
            for (int i = 0; i < this.dimension; ++i) {
                if (this.data.containsKey(i)) continue;
                iv.setValue(this.defaultValue);
                iv.setIndex(i);
                break;
            }
        }
        return iv;
    }

    public Vec projectOrthogonal(Iterable<Vec> vlist) {
        Vec b = this;
        for (Vec v : vlist) {
            b = b.minus(b.projectAlong(v));
        }
        return b;
    }

    public Vec projectOrthogonal(List<Vec> vlist, Map<Integer, Double> alpha) {
        Vec b = this;
        for (int i = 0; i < vlist.size(); ++i) {
            Vec v = vlist.get(i);
            double norm_a = v.multiply(v);
            if (DoubleUtils.isZero(norm_a)) {
                return new Vec(this.dimension);
            }
            double sigma = this.multiply(v) / norm_a;
            Vec v_parallel = v.multiply(sigma);
            alpha.put(i, sigma);
            b = b.minus(v_parallel);
        }
        return b;
    }

    public Vec projectAlong(Vec rhs) {
        double norm_a = rhs.multiply(rhs);
        if (DoubleUtils.isZero(norm_a)) {
            return new Vec(this.dimension);
        }
        double sigma = this.multiply(rhs) / norm_a;
        return rhs.multiply(sigma);
    }

    public Vec multiply(double rhs) {
        Vec clone = this.makeCopy();
        for (Integer i : this.data.keySet()) {
            clone.data.put(i, rhs * this.data.get(i));
        }
        return clone;
    }

    public double multiply(Vec rhs) {
        double productSum = 0.0;
        if (this.defaultValue == 0.0) {
            for (Map.Entry<Integer, Double> entry : this.data.entrySet()) {
                productSum += entry.getValue() * rhs.get(entry.getKey());
            }
        } else {
            for (int i = 0; i < this.dimension; ++i) {
                productSum += this.get(i) * rhs.get(i);
            }
        }
        return productSum;
    }

    public Vec pow(double scalar) {
        Vec result = new Vec(this.dimension);
        for (Map.Entry<Integer, Double> entry : this.data.entrySet()) {
            result.data.put(entry.getKey(), Math.pow(entry.getValue(), scalar));
        }
        return result;
    }

    public Vec add(Vec rhs) {
        int index;
        Vec result = new Vec(this.dimension);
        for (Map.Entry<Integer, Double> entry : this.data.entrySet()) {
            index = entry.getKey();
            result.data.put(index, entry.getValue() + rhs.data.get(index));
        }
        for (Map.Entry<Integer, Double> entry : rhs.data.entrySet()) {
            index = entry.getKey();
            if (result.data.containsKey(index)) continue;
            result.data.put(index, entry.getValue() + this.data.get(index));
        }
        return result;
    }

    public Vec minus(Vec rhs) {
        int index;
        Vec result = new Vec(this.dimension);
        for (Map.Entry<Integer, Double> entry : this.data.entrySet()) {
            index = entry.getKey();
            result.data.put(index, entry.getValue() - rhs.data.get(index));
        }
        for (Map.Entry<Integer, Double> entry : rhs.data.entrySet()) {
            index = entry.getKey();
            if (result.data.containsKey(index)) continue;
            result.data.put(index, this.data.get(index) - entry.getValue());
        }
        return result;
    }

    public double sum() {
        double sum = 0.0;
        for (Map.Entry<Integer, Double> entry : this.data.entrySet()) {
            sum += entry.getValue().doubleValue();
        }
        return sum += this.defaultValue * (double)(this.dimension - this.data.size());
    }

    public boolean isZero() {
        return DoubleUtils.isZero(this.sum());
    }

    public double norm(int level) {
        if (level == 1) {
            double sum = 0.0;
            for (Double val : this.data.values()) {
                sum += Math.abs(val);
            }
            if (!DoubleUtils.isZero(this.defaultValue)) {
                sum += Math.abs(this.defaultValue) * (double)(this.dimension - this.data.size());
            }
            return sum;
        }
        if (level == 2) {
            double sum = this.multiply(this);
            if (!DoubleUtils.isZero(this.defaultValue)) {
                sum += (double)(this.dimension - this.data.size()) * (this.defaultValue * this.defaultValue);
            }
            return Math.sqrt(sum);
        }
        double sum = 0.0;
        for (Double val : this.data.values()) {
            sum += Math.pow(Math.abs(val), level);
        }
        if (!DoubleUtils.isZero(this.defaultValue)) {
            sum += Math.pow(Math.abs(this.defaultValue), level) * (double)(this.dimension - this.data.size());
        }
        return Math.pow(sum, 1.0 / (double)level);
    }

    public Vec normalize() {
        double norm = this.norm(2);
        if (DoubleUtils.isZero(norm)) {
            return new Vec(this.dimension);
        }
        Vec clone = new Vec(this.dimension);
        clone.setAll(this.defaultValue / norm);
        for (Integer k : this.data.keySet()) {
            clone.data.put(k, this.data.get(k) / norm);
        }
        return clone;
    }

    public Map<Integer, Double> getData() {
        return this.data;
    }

    public int getDimension() {
        return this.dimension;
    }

    public double getDefaultValue() {
        return this.defaultValue;
    }

    public int getId() {
        return this.id;
    }

    public void setData(Map<Integer, Double> data) {
        this.data = data;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public void setDefaultValue(double defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setId(int id) {
        this.id = id;
    }
}
