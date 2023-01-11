package com.owobot.middleware.permission;

public enum PermissionType {
    USER(true, false),
    BOT(false, true),
    BOTH(true, true);

    private final boolean checkUser;
    private final boolean checkBot;

    PermissionType(boolean checkUser, boolean checkBot) {
        this.checkUser = checkUser;
        this.checkBot = checkBot;
    }

    public static PermissionType fromName(String name) {
        for (PermissionType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return PermissionType.USER;
    }

    public boolean isCheckUser() {
        return checkUser;
    }

    public boolean isCheckBot() {
        return checkBot;
    }
}
