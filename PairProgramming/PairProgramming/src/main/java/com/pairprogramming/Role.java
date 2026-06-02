package com.pairprogramming;

public enum Role {
    DRIVER("Driver"),
    NAVIGATOR("Navigator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
