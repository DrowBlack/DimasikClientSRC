package com.github.chen0040.rl.models;

import com.github.chen0040.rl.utils.IndexValue;
import com.github.chen0040.rl.utils.Matrix;
import com.github.chen0040.rl.utils.Vec;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class QModel {
    private Matrix Q;
    private Matrix alphaMatrix;
    private double gamma = 0.7;
    private int stateCount;
    private int actionCount;

    public QModel(int stateCount, int actionCount, double initialQ) {
        this.stateCount = stateCount;
        this.actionCount = actionCount;
        this.Q = new Matrix(stateCount, actionCount);
        this.alphaMatrix = new Matrix(stateCount, actionCount);
        this.Q.setAll(initialQ);
        this.alphaMatrix.setAll(0.1);
    }

    public QModel(int stateCount, int actionCount) {
        this(stateCount, actionCount, 0.1);
    }

    public QModel() {
    }

    public boolean equals(Object rhs) {
        if (rhs != null && rhs instanceof QModel) {
            QModel rhs2 = (QModel)rhs;
            if (this.gamma != rhs2.gamma) {
                return false;
            }
            if (this.stateCount != rhs2.stateCount || this.actionCount != rhs2.actionCount) {
                return false;
            }
            if (this.Q != null && rhs2.Q == null || this.Q == null && rhs2.Q != null) {
                return false;
            }
            if (this.alphaMatrix != null && rhs2.alphaMatrix == null || this.alphaMatrix == null && rhs2.alphaMatrix != null) {
                return false;
            }
            return !(this.Q != null && !this.Q.equals(rhs2.Q) || this.alphaMatrix != null && !this.alphaMatrix.equals(rhs2.alphaMatrix));
        }
        return false;
    }

    public QModel makeCopy() {
        QModel clone = new QModel();
        clone.copy(this);
        return clone;
    }

    public void copy(QModel rhs) {
        this.gamma = rhs.gamma;
        this.stateCount = rhs.stateCount;
        this.actionCount = rhs.actionCount;
        this.Q = rhs.Q == null ? null : rhs.Q.makeCopy();
        this.alphaMatrix = rhs.alphaMatrix == null ? null : rhs.alphaMatrix.makeCopy();
    }

    public double getQ(int stateId, int actionId) {
        return this.Q.get(stateId, actionId);
    }

    public void setQ(int stateId, int actionId, double Qij) {
        this.Q.set(stateId, actionId, Qij);
    }

    public double getAlpha(int stateId, int actionId) {
        return this.alphaMatrix.get(stateId, actionId);
    }

    public void setAlpha(double defaultAlpha) {
        this.alphaMatrix.setAll(defaultAlpha);
    }

    public IndexValue actionWithMaxQAtState(int stateId, Set<Integer> actionsAtState) {
        Vec rowVector = this.Q.rowAt(stateId);
        return rowVector.indexWithMaxValue(actionsAtState);
    }

    private void reset(double initialQ) {
        this.Q.setAll(initialQ);
    }

    public IndexValue actionWithSoftMaxQAtState(int stateId, Set<Integer> actionsAtState, Random random) {
        Vec rowVector = this.Q.rowAt(stateId);
        double sum = 0.0;
        if (actionsAtState == null) {
            actionsAtState = new HashSet<Integer>();
            for (int i = 0; i < this.actionCount; ++i) {
                actionsAtState.add(i);
            }
        }
        ArrayList<Integer> actions = new ArrayList<Integer>();
        for (Integer actionId : actionsAtState) {
            actions.add(actionId);
        }
        double[] acc = new double[actions.size()];
        for (int i = 0; i < actions.size(); ++i) {
            acc[i] = sum += rowVector.get((Integer)actions.get(i));
        }
        double r = random.nextDouble() * sum;
        IndexValue result = new IndexValue();
        for (int i = 0; i < actions.size(); ++i) {
            if (!(acc[i] >= r)) continue;
            int actionId = (Integer)actions.get(i);
            result.setIndex(actionId);
            result.setValue(rowVector.get(actionId));
            break;
        }
        return result;
    }

    public Matrix getQ() {
        return this.Q;
    }

    public Matrix getAlphaMatrix() {
        return this.alphaMatrix;
    }

    public double getGamma() {
        return this.gamma;
    }

    public int getStateCount() {
        return this.stateCount;
    }

    public int getActionCount() {
        return this.actionCount;
    }

    public void setQ(Matrix Q) {
        this.Q = Q;
    }

    public void setAlphaMatrix(Matrix alphaMatrix) {
        this.alphaMatrix = alphaMatrix;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public void setStateCount(int stateCount) {
        this.stateCount = stateCount;
    }

    public void setActionCount(int actionCount) {
        this.actionCount = actionCount;
    }
}
