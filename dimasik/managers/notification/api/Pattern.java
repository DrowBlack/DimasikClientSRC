package dimasik.managers.notification.api;

import lombok.Generated;

public enum Pattern {
    ENABLE("J"),
    DISABLE("K"),
    WARN(""),
    ERROR(""),
    NONE("");

    private final String text;

    @Generated
    public String getText() {
        return this.text;
    }

    @Generated
    private Pattern(String text) {
        this.text = text;
    }
}
