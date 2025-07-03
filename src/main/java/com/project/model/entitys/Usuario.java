package com.project.model.entitys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.project.model.entitys.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String cpf;
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
    private String name;
    private String address;
    private String phone;

    private String codeRecoveryPassword;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateShippingCodigo;

    @JsonIgnore
    @OneToMany(mappedBy = "responsible")
    private List<Animal> animalsResponsible = new ArrayList<>();

    public Usuario(String name, String email, String password,  String cpf, Role role,  String address, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.cpf = cpf;
        this.role = role;
        this.address = address;
        this.phone = phone;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getRole().toUpperCase()));
    }

    @Override
    public boolean isEnabled() {
        return true; // Verificar se a conta está habilitada
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Verificar se as credenciais estão expiradas
    }
    @Override
    public boolean isAccountNonExpired() {
        return true; // Verificar se a conta está expirada
    }
    @Override
    public boolean isAccountNonLocked() {
        return true; // Verifica se a conta esta b
    }

}
