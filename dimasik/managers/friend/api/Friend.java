package dimasik.managers.friend.api;

import dimasik.helpers.interfaces.IFastAccess;
import lombok.Generated;

public class Friend
implements IFastAccess {
    private String name;

    public Friend(String name) {
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
