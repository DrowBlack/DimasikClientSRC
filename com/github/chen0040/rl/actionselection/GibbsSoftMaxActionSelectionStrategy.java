package com.github.chen0040.rl.actionselection;

import com.github.chen0040.rl.actionselection.AbstractActionSelectionStrategy;
import com.github.chen0040.rl.models.QModel;
import com.github.chen0040.rl.utils.IndexValue;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class GibbsSoftMaxActionSelectionStrategy
extends AbstractActionSelectionStrategy {
    private Random random = null;

    public GibbsSoftMaxActionSelectionStrategy() {
        this.random = new Random();
    }

    public GibbsSoftMaxActionSelectionStrategy(Random random) {
        this.random = random;
    }

    @Override
    public Object clone() {
        GibbsSoftMaxActionSelectionStrategy clone = new GibbsSoftMaxActionSelectionStrategy();
        return clone;
    }

    @Override
    public IndexValue selectAction(int stateId, QModel model, Set<Integer> actionsAtState) {
        ArrayList<Integer> actions = new ArrayList<Integer>();
        if (actionsAtState == null) {
            for (int i = 0; i < model.getActionCount(); ++i) {
                actions.add(i);
            }
        } else {
            for (Integer actionId : actionsAtState) {
                actions.add(actionId);
            }
        }
        double sum = 0.0;
        ArrayList<Double> plist = new ArrayList<Double>();
        for (int i = 0; i < actions.size(); ++i) {
            int actionId = (Integer)actions.get(i);
            double p = Math.exp(model.getQ(stateId, actionId));
            plist.add(sum += p);
        }
        IndexValue iv = new IndexValue();
        iv.setIndex(-1);
        iv.setValue(Double.NEGATIVE_INFINITY);
        double r = sum * this.random.nextDouble();
        for (int i = 0; i < actions.size(); ++i) {
            if (!((Double)plist.get(i) >= r)) continue;
            int actionId = (Integer)actions.get(i);
            iv.setValue(model.getQ(stateId, actionId));
            iv.setIndex(actionId);
            break;
        }
        return iv;
    }
}
