package com.github.chen0040.rl.actionselection;

import com.github.chen0040.rl.actionselection.AbstractActionSelectionStrategy;
import com.github.chen0040.rl.models.QModel;
import com.github.chen0040.rl.utils.IndexValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class EpsilonGreedyActionSelectionStrategy
extends AbstractActionSelectionStrategy {
    public static final String EPSILON = "epsilon";
    private Random random = new Random();

    @Override
    public Object clone() {
        EpsilonGreedyActionSelectionStrategy clone = new EpsilonGreedyActionSelectionStrategy();
        clone.copy(this);
        return clone;
    }

    public void copy(EpsilonGreedyActionSelectionStrategy rhs) {
        this.random = rhs.random;
        for (Map.Entry entry : rhs.attributes.entrySet()) {
            this.attributes.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof EpsilonGreedyActionSelectionStrategy) {
            EpsilonGreedyActionSelectionStrategy rhs = (EpsilonGreedyActionSelectionStrategy)obj;
            return this.epsilon() == rhs.epsilon();
        }
        return false;
    }

    private double epsilon() {
        return Double.parseDouble((String)this.attributes.get(EPSILON));
    }

    public EpsilonGreedyActionSelectionStrategy() {
        this.epsilon(0.1);
    }

    public EpsilonGreedyActionSelectionStrategy(HashMap<String, String> attributes) {
        super(attributes);
    }

    private void epsilon(double value) {
        this.attributes.put(EPSILON, "" + value);
    }

    public EpsilonGreedyActionSelectionStrategy(Random random) {
        this.random = random;
        this.epsilon(0.1);
    }

    @Override
    public IndexValue selectAction(int stateId, QModel model, Set<Integer> actionsAtState) {
        int actionId;
        if (this.random.nextDouble() < 1.0 - this.epsilon()) {
            return model.actionWithMaxQAtState(stateId, actionsAtState);
        }
        if (actionsAtState != null && !actionsAtState.isEmpty()) {
            ArrayList<Integer> actions = new ArrayList<Integer>(actionsAtState);
            actionId = (Integer)actions.get(this.random.nextInt(actions.size()));
        } else {
            actionId = this.random.nextInt(model.getActionCount());
        }
        double Q = model.getQ(stateId, actionId);
        return new IndexValue(actionId, Q);
    }
}
