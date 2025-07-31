package com.project.model.entitys.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ServicePet {
    PETSHOP("Pet Shop"),
    VETERINARY("Clínica Veterinária"),
    PETCLINIC("Clínica e PetShop");

    private final String displayName;

    ServicePet(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static ServicePet fromDisplayName(String displayName) {
        for (ServicePet service : ServicePet.values()) {
            if (service.displayName.equalsIgnoreCase(displayName)) {
                return service;
            }
        }
        throw new IllegalArgumentException("Serviço inválido: " + displayName);
    }
}
