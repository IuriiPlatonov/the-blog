package org.example.theblog.config;

public enum Permission {
    USER("user:write"),
    MODERATE("user:moderate");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
