package com.github.chen0040.rl.learning.qlearn;

import com.github.chen0040.rl.learning.qlearn.QLambdaLearner;
import com.github.chen0040.rl.learning.qlearn.QLearner;
import com.github.chen0040.rl.utils.IndexValue;
import java.io.Serializable;
import java.util.Set;

public class QAgent
implements Serializable {
    private QLearner learner;
    private int currentState;
    private int prevState;
    private int prevAction;

    public int getCurrentState() {
        return this.currentState;
    }

    public int getPrevState() {
        return this.prevState;
    }

    public int getPrevAction() {
        return this.prevAction;
    }

    public void start(int currentState) {
        this.currentState = currentState;
        this.prevAction = -1;
        this.prevState = -1;
    }

    public IndexValue selectAction() {
        return this.learner.selectAction(this.currentState);
    }

    public IndexValue selectAction(Set<Integer> actionsAtState) {
        return this.learner.selectAction(this.currentState, actionsAtState);
    }

    public void update(int actionTaken, int newState, double immediateReward) {
        this.update(actionTaken, newState, null, immediateReward);
    }

    public void update(int actionTaken, int newState, Set<Integer> actionsAtNewState, double immediateReward) {
        this.learner.update(this.currentState, actionTaken, newState, actionsAtNewState, immediateReward);
        this.prevState = this.currentState;
        this.prevAction = actionTaken;
        this.currentState = newState;
    }

    public void enableEligibilityTrace(double lambda) {
        QLambdaLearner acll = new QLambdaLearner(this.learner);
        acll.setLambda(lambda);
        this.learner = acll;
    }

    public QLearner getLearner() {
        return this.learner;
    }

    public void setLearner(QLearner learner) {
        this.learner = learner;
    }

    public QAgent(int stateCount, int actionCount, double alpha, double gamma, double initialQ) {
        this.learner = new QLearner(stateCount, actionCount, alpha, gamma, initialQ);
    }

    public QAgent(QLearner learner) {
        this.learner = learner;
    }

    public QAgent(int stateCount, int actionCount) {
        this.learner = new QLearner(stateCount, actionCount);
    }

    public QAgent() {
    }

    public QAgent makeCopy() {
        QAgent clone = new QAgent();
        clone.copy(this);
        return clone;
    }

    public void copy(QAgent rhs) {
        this.learner.copy(rhs.learner);
        this.prevAction = rhs.prevAction;
        this.prevState = rhs.prevState;
        this.currentState = rhs.currentState;
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof QAgent) {
            QAgent rhs = (QAgent)obj;
            return this.prevAction == rhs.prevAction && this.prevState == rhs.prevState && this.currentState == rhs.currentState && this.learner.equals(rhs.learner);
        }
        return false;
    }
}
