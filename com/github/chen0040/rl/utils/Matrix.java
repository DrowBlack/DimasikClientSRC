package com.github.chen0040.rl.utils;

import com.alibaba.fastjson.annotation.JSONField;
import com.github.chen0040.rl.utils.DoubleUtils;
import com.github.chen0040.rl.utils.Vec;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Matrix
implements Serializable {
    private Map<Integer, Vec> rows = new HashMap<Integer, Vec>();
    private int rowCount;
    private int columnCount;
    private double defaultValue;

    public Matrix() {
    }

    public Matrix(double[][] A) {
        for (int i = 0; i < A.length; ++i) {
            double[] B = A[i];
            for (int j = 0; j < B.length; ++j) {
                this.set(i, j, B[j]);
            }
        }
    }

    public void setRow(int rowIndex, Vec rowVector) {
        rowVector.setId(rowIndex);
        this.rows.put(rowIndex, rowVector);
    }

    public static Matrix identity(int dimension) {
        Matrix m = new Matrix(dimension, dimension);
        for (int i = 0; i < m.getRowCount(); ++i) {
            m.set(i, i, 1.0);
        }
        return m;
    }

    public boolean equals(Object rhs) {
        if (rhs != null && rhs instanceof Matrix) {
            Matrix rhs2 = (Matrix)rhs;
            if (this.rowCount != rhs2.rowCount || this.columnCount != rhs2.columnCount) {
                return false;
            }
            if (this.defaultValue == rhs2.defaultValue) {
                for (Integer index : this.rows.keySet()) {
                    if (!rhs2.rows.containsKey(index)) {
                        return false;
                    }
                    if (this.rows.get(index).equals(rhs2.rows.get(index))) continue;
                    System.out.println("failed!");
                    return false;
                }
                for (Integer index : rhs2.rows.keySet()) {
                    if (!this.rows.containsKey(index)) {
                        return false;
                    }
                    if (rhs2.rows.get(index).equals(this.rows.get(index))) continue;
                    System.out.println("failed! 22");
                    return false;
                }
            } else {
                for (int i = 0; i < this.rowCount; ++i) {
                    for (int j = 0; j < this.columnCount; ++j) {
                        if (this.get(i, j) == rhs2.get(i, j)) continue;
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public Matrix makeCopy() {
        Matrix clone = new Matrix(this.rowCount, this.columnCount);
        clone.copy(this);
        return clone;
    }

    public void copy(Matrix rhs) {
        this.rowCount = rhs.rowCount;
        this.columnCount = rhs.columnCount;
        this.defaultValue = rhs.defaultValue;
        this.rows.clear();
        for (Map.Entry<Integer, Vec> entry : rhs.rows.entrySet()) {
            this.rows.put(entry.getKey(), entry.getValue().makeCopy());
        }
    }

    public void set(int rowIndex, int columnIndex, double value) {
        Vec row = this.rowAt(rowIndex);
        row.set(columnIndex, value);
        if (rowIndex >= this.rowCount) {
            this.rowCount = rowIndex + 1;
        }
        if (columnIndex >= this.columnCount) {
            this.columnCount = columnIndex + 1;
        }
    }

    public Matrix(int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.defaultValue = 0.0;
    }

    public Vec rowAt(int rowIndex) {
        Vec row = this.rows.get(rowIndex);
        if (row == null) {
            row = new Vec(this.columnCount);
            row.setAll(this.defaultValue);
            row.setId(rowIndex);
            this.rows.put(rowIndex, row);
        }
        return row;
    }

    public void setAll(double value) {
        this.defaultValue = value;
        for (Vec row : this.rows.values()) {
            row.setAll(value);
        }
    }

    public double get(int rowIndex, int columnIndex) {
        Vec row = this.rowAt(rowIndex);
        return row.get(columnIndex);
    }

    public List<Vec> columnVectors() {
        Matrix A = this;
        int n = A.getColumnCount();
        int rowCount = A.getRowCount();
        ArrayList<Vec> Acols = new ArrayList<Vec>();
        for (int c = 0; c < n; ++c) {
            Vec Acol = new Vec(rowCount);
            Acol.setAll(this.defaultValue);
            Acol.setId(c);
            for (int r = 0; r < rowCount; ++r) {
                Acol.set(r, A.get(r, c));
            }
            Acols.add(Acol);
        }
        return Acols;
    }

    public Matrix multiply(Matrix rhs) {
        if (this.getColumnCount() != rhs.getRowCount()) {
            System.err.println("A.columnCount must be equal to B.rowCount in multiplication");
            return null;
        }
        Matrix result = new Matrix(this.getRowCount(), rhs.getColumnCount());
        result.setAll(this.defaultValue);
        List<Vec> rhsColumns = rhs.columnVectors();
        for (Map.Entry<Integer, Vec> entry : this.rows.entrySet()) {
            int r1 = entry.getKey();
            Vec row1 = entry.getValue();
            for (int c2 = 0; c2 < rhsColumns.size(); ++c2) {
                Vec col2 = rhsColumns.get(c2);
                result.set(r1, c2, row1.multiply(col2));
            }
        }
        return result;
    }

    @JSONField(serialize=false)
    public boolean isSymmetric() {
        if (this.getRowCount() != this.getColumnCount()) {
            return false;
        }
        for (Map.Entry<Integer, Vec> rowEntry : this.rows.entrySet()) {
            int row = rowEntry.getKey();
            Vec rowVec = rowEntry.getValue();
            for (Integer col : rowVec.getData().keySet()) {
                if (row == col || !DoubleUtils.equals(rowVec.get(col), this.get(col, row))) continue;
                return false;
            }
        }
        return true;
    }

    public Vec multiply(Vec rhs) {
        if (this.getColumnCount() != rhs.getDimension()) {
            System.err.println("columnCount must be equal to the size of the vector for multiplication");
        }
        Vec result = new Vec(this.getRowCount());
        for (Map.Entry<Integer, Vec> entry : this.rows.entrySet()) {
            Vec row1 = entry.getValue();
            result.set(entry.getKey(), row1.multiply(rhs));
        }
        return result;
    }

    public Map<Integer, Vec> getRows() {
        return this.rows;
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public double getDefaultValue() {
        return this.defaultValue;
    }

    public void setRows(Map<Integer, Vec> rows) {
        this.rows = rows;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public void setDefaultValue(double defaultValue) {
        this.defaultValue = defaultValue;
    }
}
