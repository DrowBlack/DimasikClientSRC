package com.github.chen0040.rl.learning.qlearn;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.chen0040.rl.actionselection.AbstractActionSelectionStrategy;
import com.github.chen0040.rl.actionselection.ActionSelectionStrategy;
import com.github.chen0040.rl.actionselection.ActionSelectionStrategyFactory;
import com.github.chen0040.rl.actionselection.EpsilonGreedyActionSelectionStrategy;
import com.github.chen0040.rl.models.QModel;
import com.github.chen0040.rl.utils.IndexValue;
import java.io.Serializable;
import java.util.Set;

public class QLearner
implements Serializable,
Cloneable {
    protected QModel model;
    private ActionSelectionStrategy actionSelectionStrategy = new EpsilonGreedyActionSelectionStrategy();

    public QLearner makeCopy() {
        QLearner clone = new QLearner();
        clone.copy(this);
        return clone;
    }

    public String toJson() {
        return JSON.toJSONString((Object)this, (SerializerFeature[])new SerializerFeature[]{SerializerFeature.BrowserCompatible});
    }

    public static QLearner fromJson(String json) {
        return (QLearner)JSON.parseObject((String)json, QLearner.class);
    }

    public void copy(QLearner rhs) {
        this.model = rhs.model.makeCopy();
        this.actionSelectionStrategy = (ActionSelectionStrategy)((AbstractActionSelectionStrategy)rhs.actionSelectionStrategy).clone();
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof QLearner) {
            QLearner rhs = (QLearner)obj;
            if (!this.model.equals(rhs.model)) {
                return false;
            }
            return this.actionSelectionStrategy.equals(rhs.actionSelectionStrategy);
        }
        return false;
    }

    public QModel getModel() {
        return this.model;
    }

    public void setModel(QModel model) {
        this.model = model;
    }

    public String getActionSelection() {
        return ActionSelectionStrategyFactory.serialize(this.actionSelectionStrategy);
    }

    public void setActionSelection(String conf) {
        this.actionSelectionStrategy = ActionSelectionStrategyFactory.deserialize(conf);
    }

    public QLearner() {
    }

    public QLearner(int stateCount, int actionCount) {
        this(stateCount, actionCount, 0.1, 0.7, 0.1);
    }

    public QLearner(QModel model, ActionSelectionStrategy actionSelectionStrategy) {
        this.model = model;
        this.actionSelectionStrategy = actionSelectionStrategy;
    }

    public QLearner(int stateCount, int actionCount, double alpha, double gamma, double initialQ) {
        this.model = new QModel(stateCount, actionCount, initialQ);
        this.model.setAlpha(alpha);
        this.model.setGamma(gamma);
        this.actionSelectionStrategy = new EpsilonGreedyActionSelectionStrategy();
    }

    protected double maxQAtState(int stateId, Set<Integer> actionsAtState) {
        IndexValue iv = this.model.actionWithMaxQAtState(stateId, actionsAtState);
        double maxQ = iv.getValue();
        return maxQ;
    }

    public IndexValue selectAction(int stateId, Set<Integer> actionsAtState) {
        return this.actionSelectionStrategy.selectAction(stateId, this.model, actionsAtState);
    }

    public IndexValue selectAction(int stateId) {
        return this.selectAction(stateId, null);
    }

    public void update(int stateId, int actionId, int nextStateId, double immediateReward) {
        this.update(stateId, actionId, nextStateId, null, immediateReward);
    }

    public void update(int stateId, int actionId, int nextStateId, Set<Integer> actionsAtNextStateId, double immediateReward) {
        double oldQ = this.model.getQ(stateId, actionId);
        double alpha = this.model.getAlpha(stateId, actionId);
        double gamma = this.model.getGamma();
        double maxQ = this.maxQAtState(nextStateId, actionsAtNextStateId);
        double newQ = oldQ + alpha * (immediateReward + gamma * maxQ - oldQ);
        this.model.setQ(stateId, actionId, newQ);
    }
}
