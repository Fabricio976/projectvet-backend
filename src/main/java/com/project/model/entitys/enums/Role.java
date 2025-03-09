package com.project.model.entitys.enums;

public enum Role {
    CLIENT("Client"),
    MANAGER("Manager");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    
}
