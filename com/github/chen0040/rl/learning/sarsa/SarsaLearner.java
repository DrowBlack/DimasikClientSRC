package com.github.chen0040.rl.learning.sarsa;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.chen0040.rl.actionselection.AbstractActionSelectionStrategy;
import com.github.chen0040.rl.actionselection.ActionSelectionStrategy;
import com.github.chen0040.rl.actionselection.ActionSelectionStrategyFactory;
import com.github.chen0040.rl.actionselection.EpsilonGreedyActionSelectionStrategy;
import com.github.chen0040.rl.models.QModel;
import com.github.chen0040.rl.utils.IndexValue;
import java.io.Serializable;
import java.util.Random;
import java.util.Set;

public class SarsaLearner
implements Serializable,
Cloneable {
    protected QModel model;
    private ActionSelectionStrategy actionSelectionStrategy;

    public String toJson() {
        return JSON.toJSONString((Object)this, (SerializerFeature[])new SerializerFeature[]{SerializerFeature.BrowserCompatible});
    }

    public static SarsaLearner fromJson(String json) {
        return (SarsaLearner)JSON.parseObject((String)json, SarsaLearner.class);
    }

    public SarsaLearner makeCopy() {
        SarsaLearner clone = new SarsaLearner();
        clone.copy(this);
        return clone;
    }

    public void copy(SarsaLearner rhs) {
        this.model = rhs.model.makeCopy();
        this.actionSelectionStrategy = (ActionSelectionStrategy)((AbstractActionSelectionStrategy)rhs.actionSelectionStrategy).clone();
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof SarsaLearner) {
            SarsaLearner rhs = (SarsaLearner)obj;
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

    public SarsaLearner() {
    }

    public SarsaLearner(int stateCount, int actionCount) {
        this(stateCount, actionCount, 0.1, 0.7, 0.1);
    }

    public SarsaLearner(QModel model, ActionSelectionStrategy actionSelectionStrategy) {
        this.model = model;
        this.actionSelectionStrategy = actionSelectionStrategy;
    }

    public SarsaLearner(int stateCount, int actionCount, double alpha, double gamma, double initialQ) {
        this.model = new QModel(stateCount, actionCount, initialQ);
        this.model.setAlpha(alpha);
        this.model.setGamma(gamma);
        this.actionSelectionStrategy = new EpsilonGreedyActionSelectionStrategy();
    }

    public static void main(String[] args) {
        int stateCount = 100;
        int actionCount = 10;
        SarsaLearner learner = new SarsaLearner(stateCount, actionCount);
        double reward = 0.0;
        Random random = new Random();
        int currentStateId = random.nextInt(stateCount);
        int currentActionId = learner.selectAction(currentStateId).getIndex();
        for (int time = 0; time < 1000; ++time) {
            System.out.println("Controller does action-" + currentActionId);
            int newStateId = random.nextInt(actionCount);
            reward = random.nextDouble();
            System.out.println("Now the new state is " + newStateId);
            System.out.println("Controller receives Reward = " + reward);
            int futureActionId = learner.selectAction(newStateId).getIndex();
            System.out.println("Controller is expected to do action-" + futureActionId);
            learner.update(currentStateId, currentActionId, newStateId, futureActionId, reward);
            currentStateId = newStateId;
            currentActionId = futureActionId;
        }
    }

    public IndexValue selectAction(int stateId, Set<Integer> actionsAtState) {
        return this.actionSelectionStrategy.selectAction(stateId, this.model, actionsAtState);
    }

    public IndexValue selectAction(int stateId) {
        return this.selectAction(stateId, null);
    }

    public void update(int stateId, int actionId, int nextStateId, int nextActionId, double immediateReward) {
        double oldQ = this.model.getQ(stateId, actionId);
        double alpha = this.model.getAlpha(stateId, actionId);
        double gamma = this.model.getGamma();
        double nextQ = this.model.getQ(nextStateId, nextActionId);
        double newQ = oldQ + alpha * (immediateReward + gamma * nextQ - oldQ);
        this.model.setQ(stateId, actionId, newQ);
    }
}
