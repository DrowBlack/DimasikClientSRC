package org.openjdk.nashorn.internal.runtime;

import java.lang.invoke.SwitchPoint;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;

public final class SharedPropertyMap
extends PropertyMap {
    private SwitchPoint switchPoint = new SwitchPoint();
    private static final long serialVersionUID = 2166297719721778876L;

    SharedPropertyMap(PropertyMap map) {
        super(map);
    }

    @Override
    public void propertyChanged(Property property) {
        this.invalidateSwitchPoint();
        super.propertyChanged(property);
    }

    @Override
    synchronized boolean isValidSharedProtoMap() {
        return this.switchPoint != null;
    }

    @Override
    synchronized SwitchPoint getSharedProtoSwitchPoint() {
        return this.switchPoint;
    }

    synchronized void invalidateSwitchPoint() {
        if (this.switchPoint != null) {
            assert (!this.switchPoint.hasBeenInvalidated());
            SwitchPoint.invalidateAll(new SwitchPoint[]{this.switchPoint});
            this.switchPoint = null;
        }
    }
}
