package dimasik.managers.staff.api;

import lombok.Generated;

public class Staff {
    private String name;

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public Staff(String name) {
        this.name = name;
    }
}
