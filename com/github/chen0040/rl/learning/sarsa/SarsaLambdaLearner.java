package com.github.chen0040.rl.learning.sarsa;

import com.github.chen0040.rl.learning.sarsa.SarsaLearner;
import com.github.chen0040.rl.models.EligibilityTraceUpdateMode;
import com.github.chen0040.rl.utils.Matrix;

public class SarsaLambdaLearner
extends SarsaLearner {
    private double lambda = 0.9;
    private Matrix e;
    private EligibilityTraceUpdateMode traceUpdateMode = EligibilityTraceUpdateMode.ReplaceTrace;

    public EligibilityTraceUpdateMode getTraceUpdateMode() {
        return this.traceUpdateMode;
    }

    public void setTraceUpdateMode(EligibilityTraceUpdateMode traceUpdateMode) {
        this.traceUpdateMode = traceUpdateMode;
    }

    public double getLambda() {
        return this.lambda;
    }

    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    public Object clone() {
        SarsaLambdaLearner clone = new SarsaLambdaLearner();
        clone.copy(this);
        return clone;
    }

    @Override
    public void copy(SarsaLearner rhs) {
        super.copy(rhs);
        SarsaLambdaLearner rhs2 = (SarsaLambdaLearner)rhs;
        this.lambda = rhs2.lambda;
        this.e = rhs2.e.makeCopy();
        this.traceUpdateMode = rhs2.traceUpdateMode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (obj instanceof SarsaLambdaLearner) {
            SarsaLambdaLearner rhs = (SarsaLambdaLearner)obj;
            return rhs.lambda == this.lambda && this.e.equals(rhs.e) && this.traceUpdateMode == rhs.traceUpdateMode;
        }
        return false;
    }

    public SarsaLambdaLearner() {
    }

    public SarsaLambdaLearner(int stateCount, int actionCount) {
        super(stateCount, actionCount);
        this.e = new Matrix(stateCount, actionCount);
    }

    public SarsaLambdaLearner(int stateCount, int actionCount, double alpha, double gamma, double initialQ) {
        super(stateCount, actionCount, alpha, gamma, initialQ);
        this.e = new Matrix(stateCount, actionCount);
    }

    public SarsaLambdaLearner(SarsaLearner learner) {
        this.copy(learner);
        this.e = new Matrix(this.model.getStateCount(), this.model.getActionCount());
    }

    public Matrix getEligibility() {
        return this.e;
    }

    public void setEligibility(Matrix e) {
        this.e = e;
    }

    @Override
    public void update(int currentStateId, int currentActionId, int nextStateId, int nextActionId, double immediateReward) {
        double oldQ = this.model.getQ(currentStateId, currentActionId);
        double alpha = this.model.getAlpha(currentStateId, currentActionId);
        double gamma = this.model.getGamma();
        double nextQ = this.model.getQ(nextStateId, nextActionId);
        double td_error = immediateReward + gamma * nextQ - oldQ;
        int stateCount = this.model.getStateCount();
        int actionCount = this.model.getActionCount();
        this.e.set(currentStateId, currentActionId, this.e.get(currentStateId, currentActionId) + 1.0);
        for (int stateId = 0; stateId < stateCount; ++stateId) {
            for (int actionId = 0; actionId < actionCount; ++actionId) {
                oldQ = this.model.getQ(stateId, actionId);
                double newQ = oldQ + alpha * td_error * this.e.get(stateId, actionId);
                this.model.setQ(stateId, actionId, newQ);
                if (actionId != currentActionId) {
                    this.e.set(currentStateId, actionId, 0.0);
                    continue;
                }
                this.e.set(stateId, actionId, this.e.get(stateId, actionId) * gamma * this.lambda);
            }
        }
    }
}
