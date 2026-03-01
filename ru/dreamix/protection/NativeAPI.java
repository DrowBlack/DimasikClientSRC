package ru.dreamix.protection;

import ru.dreamix.protection.enums.Role;

public class NativeAPI {
    public static int getUserIdentifier() {
        return 0;
    }

    public static String getUserName() {
        return System.getProperty("user.name");
    }

    public static String getUserSubscribeTill() {
        return "01-01-2048";
    }

    public static Role getRole() {
        return Role.ADMIN;
    }
}
