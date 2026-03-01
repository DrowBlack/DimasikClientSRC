package lombok.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LombokInternalAliasing {
    public static final Map<String, String> ALIASES;
    public static final Map<String, Collection<String>> REVERSE_ALIASES;

    static {
        Collection c;
        HashMap<String, String> m1 = new HashMap<String, String>();
        m1.put("lombok.experimental.Value", "lombok.Value");
        m1.put("lombok.experimental.Builder", "lombok.Builder");
        m1.put("lombok.experimental.var", "lombok.var");
        m1.put("lombok.Delegate", "lombok.experimental.Delegate");
        m1.put("lombok.experimental.Wither", "lombok.With");
        ALIASES = Collections.unmodifiableMap(m1);
        HashMap<String, Collection> m2 = new HashMap<String, Collection>();
        for (Map.Entry entry : m1.entrySet()) {
            c = (Collection)m2.get(entry.getValue());
            if (c == null) {
                m2.put((String)entry.getValue(), Collections.singleton((String)entry.getKey()));
                continue;
            }
            if (c.size() == 1) {
                ArrayList newC = new ArrayList(2);
                newC.addAll(c);
                m2.put((String)entry.getValue(), c);
                continue;
            }
            c.add((String)entry.getKey());
        }
        for (Map.Entry entry : m2.entrySet()) {
            c = (Collection)entry.getValue();
            if (c.size() <= 1) continue;
            entry.setValue(Collections.unmodifiableList((ArrayList)c));
        }
        REVERSE_ALIASES = Collections.unmodifiableMap(m2);
    }

    public static String processAliases(String in) {
        if (in == null) {
            return null;
        }
        String ret = ALIASES.get(in);
        return ret == null ? in : ret;
    }
}
