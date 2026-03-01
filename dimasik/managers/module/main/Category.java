package dimasik.managers.module.main;

import lombok.Generated;

public enum Category {
    COMBAT("Combat", "F"),
    MOVEMENT("Movement", "G"),
    RENDER("Render", "H"),
    PLAYER("Player", "N"),
    MISC("Misc", "I");

    private final String name;
    private final String path;

    private Category(String name, String path) {
        this.name = name;
        this.path = path;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public String getPath() {
        return this.path;
    }
}
