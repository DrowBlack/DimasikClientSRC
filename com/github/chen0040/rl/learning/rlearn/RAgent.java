package com.github.chen0040.rl.learning.rlearn;

import com.github.chen0040.rl.learning.rlearn.RLearner;
import com.github.chen0040.rl.utils.IndexValue;
import java.io.Serializable;
import java.util.Set;

public class RAgent
implements Serializable {
    private RLearner learner;
    private int currentState;
    private int currentAction;
    private double currentValue;

    public int getCurrentState() {
        return this.currentState;
    }

    public int getCurrentAction() {
        return this.currentAction;
    }

    public void start(int currentState) {
        this.currentState = currentState;
    }

    public RAgent makeCopy() {
        RAgent clone = new RAgent();
        clone.copy(this);
        return clone;
    }

    public void copy(RAgent rhs) {
        this.currentState = rhs.currentState;
        this.currentAction = rhs.currentAction;
        this.learner.copy(rhs.learner);
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof RAgent) {
            RAgent rhs = (RAgent)obj;
            if (!this.learner.equals(rhs.learner)) {
                return false;
            }
            if (this.currentAction != rhs.currentAction) {
                return false;
            }
            return this.currentState == rhs.currentState;
        }
        return false;
    }

    public IndexValue selectAction() {
        return this.selectAction(null);
    }

    public IndexValue selectAction(Set<Integer> actionsAtState) {
        if (this.currentAction == -1) {
            IndexValue iv = this.learner.selectAction(this.currentState, actionsAtState);
            this.currentAction = iv.getIndex();
            this.currentValue = iv.getValue();
        }
        return new IndexValue(this.currentAction, this.currentValue);
    }

    public void update(int newState, double immediateReward) {
        this.update(newState, null, immediateReward);
    }

    public void update(int newState, Set<Integer> actionsAtState, double immediateReward) {
        if (this.currentAction != -1) {
            this.learner.update(this.currentState, this.currentAction, newState, actionsAtState, immediateReward);
            this.currentState = newState;
            this.currentAction = -1;
        }
    }

    public RAgent() {
    }

    public RLearner getLearner() {
        return this.learner;
    }

    public void setLearner(RLearner learner) {
        this.learner = learner;
    }

    public RAgent(int stateCount, int actionCount, double alpha, double beta, double rho, double initialQ) {
        this.learner = new RLearner(stateCount, actionCount, alpha, beta, rho, initialQ);
    }

    public RAgent(int stateCount, int actionCount) {
        this.learner = new RLearner(stateCount, actionCount);
    }
}
