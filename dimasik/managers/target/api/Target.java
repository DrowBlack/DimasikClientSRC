package dimasik.managers.target.api;

import dimasik.helpers.interfaces.IFastAccess;
import lombok.Generated;

public class Target
implements IFastAccess {
    private String name;

    public Target(String name) {
        this.name = name;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }
}
