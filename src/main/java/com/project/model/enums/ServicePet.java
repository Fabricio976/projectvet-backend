package com.project.model.enums;

public enum ServicePet {
    PETSHOP("PetShop"),
    VETERINARY("Clinica"),
    PETCLINIC("Clinica e PetShop");

    private final String servicePet;

    ServicePet(String servicePet) {
        this.servicePet = servicePet;
    }

    public String getserv() {
        return servicePet;
    }
}
