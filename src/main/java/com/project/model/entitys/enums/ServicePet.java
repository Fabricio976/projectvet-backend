package com.project.model.entitys.enums;

public enum ServicePet {
    PETSHOP("PetShop"),
    VETERINARY("Clinica Veterinária"),
    PETCLINIC("Clinica e PetShop");

    private final String servicePet;

    ServicePet(String servicePet) {
        this.servicePet = servicePet;
    }

    public String getserv() {
        return servicePet;
    }
}
