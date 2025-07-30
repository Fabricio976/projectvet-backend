package com.project.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.project.model.entitys.Role;
import com.project.model.exeptions.EmailAlreadyExistsException;
import com.project.model.exeptions.EmailNotFoundException;
import com.project.model.exeptions.InvalidRecoveryCodeException;
import com.project.config.SecurityConfigurations;
import com.project.services.details.ManagerAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.model.dto.RegisterUserDTO;
import com.project.model.entitys.Usuario;
import com.project.model.entitys.enums.RoleName;
import com.project.model.repositorys.UserRepository;

@Service
@Transactional
public class UsuarioService {

    private final UserRepository userRepository;

    private final SecurityConfigurations securityConfiguration;

    private final ManagerAdmin managerAdmin;

    public UsuarioService(UserRepository userRepository, SecurityConfigurations securityConfiguration, ManagerAdmin managerAdmin) {
        this.userRepository = userRepository;
        this.securityConfiguration = securityConfiguration;
        this.managerAdmin = managerAdmin;
    }

    public void registerUser(RegisterUserDTO data) {
        checkIfEmailExists(data.email());
        Usuario newUser = Usuario.builder()
                .name(data.name())
                .email(data.email())
                .password(securityConfiguration.passwordEncoder().encode(data.password()))
                .roles(List.of(Role.builder().name(RoleName.ROLE_CLIENT).build()))
                .cpf(data.cpf())
                .address(data.address())
                .phone(data.phone())
                .build();

        userRepository.save(newUser);
    }

    public void registerManager(RegisterUserDTO data) {
        checkIfEmailExists(data.email());
        Usuario newUser = Usuario.builder()
                .name(data.name())
                .email(data.email())
                .password(securityConfiguration.passwordEncoder().encode(data.password()))
                .roles(List.of(Role.builder().name(RoleName.ROLE_MANAGER).build()))
                .cpf(data.cpf())
                .address(data.address())
                .phone(data.phone())
                .build();

        userRepository.save(newUser);
    }

    public String requestRecoveryCode(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email))
                .map(user -> managerAdmin.solicitarCodigo(email))
                .orElseThrow(() -> new EmailNotFoundException("Email não encontrado: " + email));
    }

    public void changePassword(Usuario usuario) {
        Optional.of(managerAdmin.alterarSenha(usuario))
                .filter(msg -> msg.equals("Senha alterada com sucesso!"))
                .orElseThrow(() -> new RuntimeException("Erro ao alterar a senha"));
    }

    public void checkCode(String email, String code) {
        Usuario user = (Usuario) Optional.ofNullable(userRepository.findByEmailAndCodeRecoveryPassword(email, code))
                .orElseThrow(() -> new InvalidRecoveryCodeException("Código inválido"));

        Optional.ofNullable(user.getDateShippingCodigo())
                .filter(data -> (new Date().getTime() - data.getTime()) / 1000 <= 900)
                .orElseThrow(() -> new InvalidRecoveryCodeException("Código expirado"));
    }

    private void checkIfEmailExists(String email) {
        Optional.ofNullable(userRepository.findByEmail(email))
                .ifPresent(user -> {
                    throw new EmailAlreadyExistsException("Email já cadastrado!");
                });
    }
}
