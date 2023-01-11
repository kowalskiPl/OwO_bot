package com.owobot.admin;

public class AdminUser {
    private final long userId;

    public AdminUser() {
        userId = 0;
    }

    public AdminUser(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }
}
