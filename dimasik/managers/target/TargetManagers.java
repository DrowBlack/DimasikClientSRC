package dimasik.managers.target;

import dimasik.helpers.interfaces.IFinderModules;
import dimasik.helpers.interfaces.IManager;
import dimasik.managers.target.api.Target;
import lombok.Generated;

public class TargetManagers
implements IManager<Target>,
IFinderModules<Target> {
    private Target currentTarget = null;

    public void set(String name) {
        this.currentTarget = new Target(name);
    }

    public void clear() {
        this.currentTarget = null;
    }

    public boolean isActive() {
        return this.currentTarget != null;
    }

    public String getName() {
        return this.currentTarget != null ? this.currentTarget.getName() : null;
    }

    @Override
    public <T extends Target> T findName(String name) {
        if (this.currentTarget != null && this.currentTarget.getName().equalsIgnoreCase(name)) {
            return (T)this.currentTarget;
        }
        return null;
    }

    @Override
    public <T extends Target> T findClass(Class<T> clazz) {
        if (this.currentTarget != null && this.currentTarget.getClass() == clazz) {
            return (T)this.currentTarget;
        }
        return null;
    }

    @Override
    public void register(Target target) {
        this.currentTarget = target;
    }

    @Generated
    public Target getCurrentTarget() {
        return this.currentTarget;
    }
}
