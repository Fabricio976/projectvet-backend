package com.project.model.entitys.enums;

public enum Role {
    CLIENT("CLIENT"),
    MANAGER("MANAGER");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public String getAuthority() {
        return "ROLE_" + this.name();
    }


}
