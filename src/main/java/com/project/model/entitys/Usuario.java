package com.project.model.entitys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.project.model.entitys.enums.RoleName;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@JsonIgnoreProperties(value = {"roles",
        "password",
        "codeRecoveryPassword",
        "dateShippingCodigo",
        "codeRecoveryPassword",
        "authorities",
        "accountNonLocked",
        "accountNonExpired",
        "credentialsNonExpired"
}, allowGetters = false, allowSetters = true)
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String cpf;
    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER,  cascade = CascadeType.PERSIST)
    @JoinTable(name="users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id"))
    private List<Role> roles;
    private String name;
    private String address;
    private String phone;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateShippingCodigo;
    private String codeRecoveryPassword;

    @JsonIgnore
    @OneToMany(mappedBy = "responsible")
    private List<Animal> animalsResponsible = new ArrayList<>();

    public Usuario(String name, String email, String password, String cpf, List<Role> roles, String address, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.cpf = cpf;
        this.roles = roles;
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
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority(r.getName().name()))
                .toList();
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
