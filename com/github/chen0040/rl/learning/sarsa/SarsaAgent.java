package com.github.chen0040.rl.learning.sarsa;

import com.github.chen0040.rl.learning.sarsa.SarsaLambdaLearner;
import com.github.chen0040.rl.learning.sarsa.SarsaLearner;
import com.github.chen0040.rl.utils.IndexValue;
import java.io.Serializable;
import java.util.Set;

public class SarsaAgent
implements Serializable {
    private SarsaLearner learner;
    private int currentState;
    private int currentAction;
    private double currentValue;
    private int prevState;
    private int prevAction;

    public int getCurrentState() {
        return this.currentState;
    }

    public int getCurrentAction() {
        return this.currentAction;
    }

    public int getPrevState() {
        return this.prevState;
    }

    public int getPrevAction() {
        return this.prevAction;
    }

    public void start(int currentState) {
        this.currentState = currentState;
        this.prevState = -1;
        this.prevAction = -1;
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

    public void update(int actionTaken, int newState, double immediateReward) {
        this.update(actionTaken, newState, null, immediateReward);
    }

    public void update(int actionTaken, int newState, Set<Integer> actionsAtNewState, double immediateReward) {
        IndexValue iv = this.learner.selectAction(this.currentState, actionsAtNewState);
        int futureAction = iv.getIndex();
        this.learner.update(this.currentState, actionTaken, newState, futureAction, immediateReward);
        this.prevState = this.currentState;
        this.prevAction = actionTaken;
        this.currentAction = futureAction;
        this.currentState = newState;
    }

    public SarsaLearner getLearner() {
        return this.learner;
    }

    public void setLearner(SarsaLearner learner) {
        this.learner = learner;
    }

    public SarsaAgent(int stateCount, int actionCount, double alpha, double gamma, double initialQ) {
        this.learner = new SarsaLearner(stateCount, actionCount, alpha, gamma, initialQ);
    }

    public SarsaAgent(int stateCount, int actionCount) {
        this.learner = new SarsaLearner(stateCount, actionCount);
    }

    public SarsaAgent(SarsaLearner learner) {
        this.learner = learner;
    }

    public SarsaAgent() {
    }

    public void enableEligibilityTrace(double lambda) {
        SarsaLambdaLearner acll = new SarsaLambdaLearner(this.learner);
        acll.setLambda(lambda);
        this.learner = acll;
    }

    public SarsaAgent makeCopy() {
        SarsaAgent clone = new SarsaAgent();
        clone.copy(this);
        return clone;
    }

    public void copy(SarsaAgent rhs) {
        this.learner.copy(rhs.learner);
        this.currentAction = rhs.currentAction;
        this.currentState = rhs.currentState;
        this.prevAction = rhs.prevAction;
        this.prevState = rhs.prevState;
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof SarsaAgent) {
            SarsaAgent rhs = (SarsaAgent)obj;
            return this.prevAction == rhs.prevAction && this.prevState == rhs.prevState && this.currentAction == rhs.currentAction && this.currentState == rhs.currentState && this.learner.equals(rhs.learner);
        }
        return false;
    }
}
