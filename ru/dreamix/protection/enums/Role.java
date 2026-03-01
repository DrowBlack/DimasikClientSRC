package ru.dreamix.protection.enums;

public enum Role {
    DEFAULT("\u041f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u0435\u043b\u044c"),
    BETA("\u0411\u0435\u0442\u0430"),
    YOUTUBE("\u042e\u0442\u0443\u0411\u0435\u0440"),
    ADMIN("\u0410\u0434\u043c\u0438\u043d\u0438\u0441\u0442\u0440\u0430\u0442\u043e\u0440");

    private final String translatedName;

    private Role(String translatedName) {
        this.translatedName = translatedName;
    }

    public String getName() {
        return this.translatedName;
    }
}
