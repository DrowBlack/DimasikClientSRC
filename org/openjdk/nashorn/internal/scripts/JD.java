package org.openjdk.nashorn.internal.scripts;

import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.ScriptObject;

public class JD
extends ScriptObject {
    private static final PropertyMap map$ = PropertyMap.newMap(JD.class);

    public static PropertyMap getInitialMap() {
        return map$;
    }

    public JD(PropertyMap map) {
        super(map);
    }

    public JD(ScriptObject proto) {
        super(proto, JD.getInitialMap());
    }

    public JD(PropertyMap map, long[] primitiveSpill, Object[] objectSpill) {
        super(map, primitiveSpill, objectSpill);
    }

    public static ScriptObject allocate(PropertyMap map) {
        return new JD(map);
    }
}
