package com.github.chen0040.rl.models;

import com.github.chen0040.rl.utils.Vec;
import java.io.Serializable;

public class UtilityModel
implements Serializable {
    private Vec U;
    private int stateCount;
    private int actionCount;

    public void setU(Vec U) {
        this.U = U;
    }

    public Vec getU() {
        return this.U;
    }

    public double getU(int stateId) {
        return this.U.get(stateId);
    }

    public int getStateCount() {
        return this.stateCount;
    }

    public int getActionCount() {
        return this.actionCount;
    }

    public UtilityModel(int stateCount, int actionCount, double initialU) {
        this.stateCount = stateCount;
        this.actionCount = actionCount;
        this.U = new Vec(stateCount);
        this.U.setAll(initialU);
    }

    public UtilityModel(int stateCount, int actionCount) {
        this(stateCount, actionCount, 0.1);
    }

    public UtilityModel() {
    }

    public void copy(UtilityModel rhs) {
        this.U = rhs.U == null ? null : rhs.U.makeCopy();
        this.actionCount = rhs.actionCount;
        this.stateCount = rhs.stateCount;
    }

    public UtilityModel makeCopy() {
        UtilityModel clone = new UtilityModel();
        clone.copy(this);
        return clone;
    }

    public boolean equals(Object rhs) {
        if (rhs != null && rhs instanceof UtilityModel) {
            UtilityModel rhs2 = (UtilityModel)rhs;
            if (this.actionCount != rhs2.actionCount || this.stateCount != rhs2.stateCount) {
                return false;
            }
            if (this.U == null && rhs2.U != null && this.U != null && rhs2.U == null) {
                return false;
            }
            return this.U == null || this.U.equals(rhs2.U);
        }
        return false;
    }

    public void reset(double initialU) {
        this.U.setAll(initialU);
    }

    public void setStateCount(int stateCount) {
        this.stateCount = stateCount;
    }

    public void setActionCount(int actionCount) {
        this.actionCount = actionCount;
    }
}
