package com.github.chen0040.rl.learning.actorcritic;

import com.github.chen0040.rl.learning.actorcritic.ActorCriticLearner;
import com.github.chen0040.rl.models.EligibilityTraceUpdateMode;
import com.github.chen0040.rl.utils.Matrix;
import java.util.Set;
import java.util.function.Function;

public class ActorCriticLambdaLearner
extends ActorCriticLearner {
    private Matrix e;
    private double lambda = 0.9;
    private EligibilityTraceUpdateMode traceUpdateMode = EligibilityTraceUpdateMode.ReplaceTrace;

    public ActorCriticLambdaLearner() {
    }

    public ActorCriticLambdaLearner(int stateCount, int actionCount) {
        super(stateCount, actionCount);
        this.e = new Matrix(stateCount, actionCount);
    }

    public ActorCriticLambdaLearner(ActorCriticLearner learner) {
        this.copy(learner);
        this.e = new Matrix(this.P.getStateCount(), this.P.getActionCount());
    }

    public ActorCriticLambdaLearner(int stateCount, int actionCount, double alpha, double gamma, double lambda, double initialP) {
        super(stateCount, actionCount, alpha, gamma, initialP);
        this.lambda = lambda;
        this.e = new Matrix(stateCount, actionCount);
    }

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

    @Override
    public ActorCriticLambdaLearner makeCopy() {
        ActorCriticLambdaLearner clone = new ActorCriticLambdaLearner();
        clone.copy(this);
        return clone;
    }

    @Override
    public void copy(ActorCriticLearner rhs) {
        super.copy(rhs);
        ActorCriticLambdaLearner rhs2 = (ActorCriticLambdaLearner)rhs;
        this.e = rhs2.e.makeCopy();
        this.lambda = rhs2.lambda;
        this.traceUpdateMode = rhs2.traceUpdateMode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (obj instanceof ActorCriticLambdaLearner) {
            ActorCriticLambdaLearner rhs = (ActorCriticLambdaLearner)obj;
            return this.e.equals(rhs.e) && this.lambda == rhs.lambda && this.traceUpdateMode == rhs.traceUpdateMode;
        }
        return false;
    }

    public Matrix getEligibility() {
        return this.e;
    }

    public void setEligibility(Matrix e) {
        this.e = e;
    }

    @Override
    public void update(int currentStateId, int currentActionId, int newStateId, Set<Integer> actionsAtNewState, double immediateReward, Function<Integer, Double> V) {
        double td_error = immediateReward + V.apply(newStateId) - V.apply(currentStateId);
        int stateCount = this.P.getStateCount();
        int actionCount = this.P.getActionCount();
        double gamma = this.P.getGamma();
        this.e.set(currentStateId, currentActionId, this.e.get(currentStateId, currentActionId) + 1.0);
        for (int stateId = 0; stateId < stateCount; ++stateId) {
            for (int actionId = 0; actionId < actionCount; ++actionId) {
                double oldP = this.P.getQ(stateId, actionId);
                double alpha = this.P.getAlpha(currentStateId, currentActionId);
                double newP = oldP + alpha * td_error * this.e.get(stateId, actionId);
                this.P.setQ(stateId, actionId, newP);
                if (actionId != currentActionId) {
                    this.e.set(currentStateId, actionId, 0.0);
                    continue;
                }
                this.e.set(stateId, actionId, this.e.get(stateId, actionId) * gamma * this.lambda);
            }
        }
    }
}
