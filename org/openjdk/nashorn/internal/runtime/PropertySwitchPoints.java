package org.openjdk.nashorn.internal.runtime;

import java.lang.invoke.SwitchPoint;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.LongAdder;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.ScriptObject;

public class PropertySwitchPoints {
    private final Map<Object, WeakSwitchPointSet> switchPointMap = new HashMap<Object, WeakSwitchPointSet>();
    private static final SwitchPoint[] EMPTY_SWITCHPOINT_ARRAY = new SwitchPoint[0];
    private static LongAdder switchPointsAdded;
    private static LongAdder switchPointsInvalidated;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private PropertySwitchPoints(PropertySwitchPoints switchPoints) {
        if (switchPoints != null) {
            PropertySwitchPoints propertySwitchPoints = switchPoints;
            synchronized (propertySwitchPoints) {
                for (Map.Entry<Object, WeakSwitchPointSet> entry : switchPoints.switchPointMap.entrySet()) {
                    this.switchPointMap.put(entry.getKey(), new WeakSwitchPointSet(entry.getValue()));
                }
            }
        }
    }

    public static long getSwitchPointsAdded() {
        return switchPointsAdded.longValue();
    }

    public static long getSwitchPointsInvalidated() {
        return switchPointsInvalidated.longValue();
    }

    public static int getSwitchPointCount(ScriptObject obj) {
        return obj.getMap().getSwitchPointCount();
    }

    int getSwitchPointCount() {
        return this.switchPointMap.size();
    }

    static PropertySwitchPoints addSwitchPoint(PropertySwitchPoints oldSwitchPoints, String key, SwitchPoint switchPoint) {
        if (oldSwitchPoints == null || !oldSwitchPoints.contains(key, switchPoint)) {
            PropertySwitchPoints newSwitchPoints = new PropertySwitchPoints(oldSwitchPoints);
            newSwitchPoints.add(key, switchPoint);
            return newSwitchPoints;
        }
        return oldSwitchPoints;
    }

    private synchronized boolean contains(String key, SwitchPoint switchPoint) {
        WeakSwitchPointSet set = this.switchPointMap.get(key);
        return set != null && set.contains(switchPoint);
    }

    private synchronized void add(String key, SwitchPoint switchPoint) {
        WeakSwitchPointSet set;
        if (Context.DEBUG) {
            switchPointsAdded.increment();
        }
        if ((set = this.switchPointMap.get(key)) == null) {
            set = new WeakSwitchPointSet();
            this.switchPointMap.put(key, set);
        }
        set.add(switchPoint);
    }

    Set<SwitchPoint> getSwitchPoints(Object key) {
        WeakSwitchPointSet switchPointSet = this.switchPointMap.get(key);
        if (switchPointSet != null) {
            return switchPointSet.elements();
        }
        return Collections.emptySet();
    }

    synchronized void invalidateProperty(Property prop) {
        WeakSwitchPointSet set = this.switchPointMap.get(prop.getKey());
        if (set != null) {
            if (Context.DEBUG) {
                switchPointsInvalidated.add(set.size());
            }
            SwitchPoint[] switchPoints = set.elements().toArray(EMPTY_SWITCHPOINT_ARRAY);
            SwitchPoint.invalidateAll(switchPoints);
            this.switchPointMap.remove(prop.getKey());
        }
    }

    synchronized void invalidateInheritedProperties(PropertyMap map) {
        for (Map.Entry<Object, WeakSwitchPointSet> entry : this.switchPointMap.entrySet()) {
            if (map.findProperty(entry.getKey()) != null) continue;
            if (Context.DEBUG) {
                switchPointsInvalidated.add(entry.getValue().size());
            }
            SwitchPoint[] switchPoints = entry.getValue().elements().toArray(EMPTY_SWITCHPOINT_ARRAY);
            SwitchPoint.invalidateAll(switchPoints);
        }
        this.switchPointMap.clear();
    }

    static {
        if (Context.DEBUG) {
            switchPointsAdded = new LongAdder();
            switchPointsInvalidated = new LongAdder();
        }
    }

    private static class WeakSwitchPointSet {
        private final WeakHashMap<SwitchPoint, Void> map;

        WeakSwitchPointSet() {
            this.map = new WeakHashMap();
        }

        WeakSwitchPointSet(WeakSwitchPointSet set) {
            this.map = new WeakHashMap<SwitchPoint, Void>(set.map);
        }

        void add(SwitchPoint switchPoint) {
            this.map.put(switchPoint, null);
        }

        boolean contains(SwitchPoint switchPoint) {
            return this.map.containsKey(switchPoint);
        }

        Set<SwitchPoint> elements() {
            return this.map.keySet();
        }

        int size() {
            return this.map.size();
        }
    }
}
