package dimasik.ui.option.list;

import lombok.Generated;

public enum Type {
    THEME("Theme"),
    CONFIG("Config");

    private final String name;

    private Type(String name) {
        this.name = name;
    }

    @Generated
    public String getName() {
        return this.name;
    }
}
