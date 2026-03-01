package com.github.chen0040.rl.actionselection;

import com.github.chen0040.rl.actionselection.ActionSelectionStrategy;
import com.github.chen0040.rl.models.QModel;
import com.github.chen0040.rl.models.UtilityModel;
import com.github.chen0040.rl.utils.IndexValue;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractActionSelectionStrategy
implements ActionSelectionStrategy {
    private String prototype;
    protected Map<String, String> attributes = new HashMap<String, String>();

    @Override
    public String getPrototype() {
        return this.prototype;
    }

    @Override
    public IndexValue selectAction(int stateId, QModel model, Set<Integer> actionsAtState) {
        return new IndexValue();
    }

    @Override
    public IndexValue selectAction(int stateId, UtilityModel model, Set<Integer> actionsAtState) {
        return new IndexValue();
    }

    public AbstractActionSelectionStrategy() {
        this.prototype = this.getClass().getCanonicalName();
    }

    public AbstractActionSelectionStrategy(HashMap<String, String> attributes) {
        this.attributes = attributes;
        if (attributes.containsKey("prototype")) {
            this.prototype = attributes.get("prototype");
        }
    }

    @Override
    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public boolean equals(Object obj) {
        ActionSelectionStrategy rhs = (ActionSelectionStrategy)obj;
        if (!this.prototype.equalsIgnoreCase(rhs.getPrototype())) {
            return false;
        }
        for (Map.Entry<String, String> entry : rhs.getAttributes().entrySet()) {
            if (!this.attributes.containsKey(entry.getKey())) {
                return false;
            }
            if (this.attributes.get(entry.getKey()).equals(entry.getValue())) continue;
            return false;
        }
        for (Map.Entry<String, String> entry : this.attributes.entrySet()) {
            if (!rhs.getAttributes().containsKey(entry.getKey())) {
                return false;
            }
            if (rhs.getAttributes().get(entry.getKey()).equals(entry.getValue())) continue;
            return false;
        }
        return true;
    }

    public abstract Object clone();
}
