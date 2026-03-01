package com.github.chen0040.rl.actionselection;

import com.github.chen0040.rl.actionselection.AbstractActionSelectionStrategy;
import com.github.chen0040.rl.models.QModel;
import com.github.chen0040.rl.utils.IndexValue;
import java.util.Random;
import java.util.Set;

public class SoftMaxActionSelectionStrategy
extends AbstractActionSelectionStrategy {
    private Random random = new Random();

    @Override
    public Object clone() {
        SoftMaxActionSelectionStrategy clone = new SoftMaxActionSelectionStrategy(this.random);
        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof SoftMaxActionSelectionStrategy;
    }

    public SoftMaxActionSelectionStrategy() {
    }

    public SoftMaxActionSelectionStrategy(Random random) {
        this.random = random;
    }

    @Override
    public IndexValue selectAction(int stateId, QModel model, Set<Integer> actionsAtState) {
        return model.actionWithSoftMaxQAtState(stateId, actionsAtState, this.random);
    }
}
