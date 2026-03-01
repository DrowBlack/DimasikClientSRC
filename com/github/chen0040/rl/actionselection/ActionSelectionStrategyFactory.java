package com.github.chen0040.rl.actionselection;

import com.github.chen0040.rl.actionselection.ActionSelectionStrategy;
import com.github.chen0040.rl.actionselection.EpsilonGreedyActionSelectionStrategy;
import com.github.chen0040.rl.actionselection.GibbsSoftMaxActionSelectionStrategy;
import com.github.chen0040.rl.actionselection.GreedyActionSelectionStrategy;
import com.github.chen0040.rl.actionselection.SoftMaxActionSelectionStrategy;
import java.util.HashMap;
import java.util.Map;

public class ActionSelectionStrategyFactory {
    public static ActionSelectionStrategy deserialize(String conf) {
        String prototype;
        String[] comps = conf.split(";");
        HashMap<String, String> attributes = new HashMap<String, String>();
        for (int i = 0; i < comps.length; ++i) {
            String comp = comps[i];
            String[] field = comp.split("=");
            if (field.length < 2) continue;
            String fieldname = field[0].trim();
            String fieldvalue = field[1].trim();
            attributes.put(fieldname, fieldvalue);
        }
        if (attributes.isEmpty()) {
            attributes.put("prototype", conf);
        }
        if ((prototype = (String)attributes.get("prototype")).equals(GreedyActionSelectionStrategy.class.getCanonicalName())) {
            return new GreedyActionSelectionStrategy();
        }
        if (prototype.equals(SoftMaxActionSelectionStrategy.class.getCanonicalName())) {
            return new SoftMaxActionSelectionStrategy();
        }
        if (prototype.equals(EpsilonGreedyActionSelectionStrategy.class.getCanonicalName())) {
            return new EpsilonGreedyActionSelectionStrategy(attributes);
        }
        if (prototype.equals(GibbsSoftMaxActionSelectionStrategy.class.getCanonicalName())) {
            return new GibbsSoftMaxActionSelectionStrategy();
        }
        return null;
    }

    public static String serialize(ActionSelectionStrategy strategy) {
        Map<String, String> attributes = strategy.getAttributes();
        attributes.put("prototype", strategy.getPrototype());
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(";");
            }
            sb.append(entry.getKey() + "=" + entry.getValue());
        }
        return sb.toString();
    }
}
