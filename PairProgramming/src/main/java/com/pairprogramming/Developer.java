package com.pairprogramming;

public class Developer {
    private String name;
    private Role role;

    public Developer(String name, Role role) {
        this.name = name == null ? "" : name.trim();
        this.role = role == null ? Role.NAVIGATOR : role;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public void setName(String name) {
        this.name = name == null ? "" : name.trim();
    }

    public void setRole(Role role) {
        this.role = role == null ? Role.NAVIGATOR : role;
    }

    public boolean isDriver() {
        return role == Role.DRIVER;
    }

    public boolean isNavigator() {
        return role == Role.NAVIGATOR;
    }

    public boolean hasName(String candidate) {
        return candidate != null && candidate.trim().equalsIgnoreCase(name);
    }
}
