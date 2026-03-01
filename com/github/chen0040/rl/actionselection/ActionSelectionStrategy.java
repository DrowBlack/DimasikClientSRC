package com.github.chen0040.rl.actionselection;

import com.github.chen0040.rl.models.QModel;
import com.github.chen0040.rl.models.UtilityModel;
import com.github.chen0040.rl.utils.IndexValue;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface ActionSelectionStrategy
extends Serializable,
Cloneable {
    public IndexValue selectAction(int var1, QModel var2, Set<Integer> var3);

    public IndexValue selectAction(int var1, UtilityModel var2, Set<Integer> var3);

    public String getPrototype();

    public Map<String, String> getAttributes();
}
