package com.github.chen0040.rl.learning.actorcritic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.chen0040.rl.actionselection.AbstractActionSelectionStrategy;
import com.github.chen0040.rl.actionselection.ActionSelectionStrategy;
import com.github.chen0040.rl.actionselection.ActionSelectionStrategyFactory;
import com.github.chen0040.rl.actionselection.GibbsSoftMaxActionSelectionStrategy;
import com.github.chen0040.rl.models.QModel;
import com.github.chen0040.rl.utils.IndexValue;
import java.io.Serializable;
import java.util.Set;
import java.util.function.Function;

public class ActorCriticLearner
implements Serializable {
    protected QModel P;
    protected ActionSelectionStrategy actionSelectionStrategy;

    public String toJson() {
        return JSON.toJSONString((Object)this, (SerializerFeature[])new SerializerFeature[]{SerializerFeature.BrowserCompatible});
    }

    public static ActorCriticLearner fromJson(String json) {
        return (ActorCriticLearner)JSON.parseObject((String)json, ActorCriticLearner.class);
    }

    public Object makeCopy() {
        ActorCriticLearner clone = new ActorCriticLearner();
        clone.copy(this);
        return clone;
    }

    public void copy(ActorCriticLearner rhs) {
        this.P = rhs.P.makeCopy();
        this.actionSelectionStrategy = (ActionSelectionStrategy)((AbstractActionSelectionStrategy)rhs.actionSelectionStrategy).clone();
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ActorCriticLearner) {
            ActorCriticLearner rhs = (ActorCriticLearner)obj;
            return this.P.equals(rhs.P) && this.getActionSelection().equals(rhs.getActionSelection());
        }
        return false;
    }

    public ActorCriticLearner() {
    }

    public ActorCriticLearner(int stateCount, int actionCount) {
        this(stateCount, actionCount, 1.0, 0.7, 0.01);
    }

    public int selectAction(int stateId, Set<Integer> actionsAtState) {
        IndexValue iv = this.actionSelectionStrategy.selectAction(stateId, this.P, actionsAtState);
        return iv.getIndex();
    }

    public int selectAction(int stateId) {
        return this.selectAction(stateId, null);
    }

    public ActorCriticLearner(int stateCount, int actionCount, double beta, double gamma, double initialP) {
        this.P = new QModel(stateCount, actionCount, initialP);
        this.P.setAlpha(beta);
        this.P.setGamma(gamma);
        this.actionSelectionStrategy = new GibbsSoftMaxActionSelectionStrategy();
    }

    public void update(int currentStateId, int currentActionId, int newStateId, double immediateReward, Function<Integer, Double> V) {
        this.update(currentStateId, currentActionId, newStateId, null, immediateReward, V);
    }

    public void update(int currentStateId, int currentActionId, int newStateId, Set<Integer> actionsAtNewState, double immediateReward, Function<Integer, Double> V) {
        double td_error = immediateReward + V.apply(newStateId) - V.apply(currentStateId);
        double oldP = this.P.getQ(currentStateId, currentActionId);
        double beta = this.P.getAlpha(currentStateId, currentActionId);
        double newP = oldP + beta * td_error;
        this.P.setQ(currentStateId, currentActionId, newP);
    }

    public String getActionSelection() {
        return ActionSelectionStrategyFactory.serialize(this.actionSelectionStrategy);
    }

    public void setActionSelection(String conf) {
        this.actionSelectionStrategy = ActionSelectionStrategyFactory.deserialize(conf);
    }

    public QModel getP() {
        return this.P;
    }

    public void setP(QModel p) {
        this.P = p;
    }
}
