package com.project.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.project.model.exeptions.EmailAlreadyExistsException;
import com.project.model.exeptions.EmailNotFoundException;
import com.project.model.exeptions.InvalidRecoveryCodeException;
import com.project.services.details.ManagerUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.model.dto.RegisterUserDTO;
import com.project.model.entitys.Usuario;
import com.project.model.entitys.enums.Role;
import com.project.model.exeptions.RgNotFoundException;
import com.project.model.repositorys.UserRepository;
@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ManagerUser managerUser;

    public Usuario registerUser(RegisterUserDTO data) {
        checkIfEmailExists(data.email());

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        Usuario newUser = new Usuario(
                data.name(), data.email(), encryptedPassword,
                data.cpf(), Role.CLIENT, data.address(), data.phone()
        );
        return userRepository.save(newUser);
    }

    public Usuario registerManager(RegisterUserDTO data) {
        checkIfEmailExists(data.email());

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        Usuario newUser = new Usuario(
                data.name(), data.email(), encryptedPassword,
                data.cpf(), Role.MANAGER, data.address(), data.phone()
        );
        return userRepository.save(newUser);
    }

    public String requestRecoveryCode(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email))
                .map(user -> managerUser.solicitarCodigo(email))
                .orElseThrow(() -> new EmailNotFoundException("Email não encontrado: " + email));
    }

    public void changePassword(Usuario usuario) {
        Optional.of(managerUser.alterarSenha(usuario))
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
                .ifPresent(user -> { throw new EmailAlreadyExistsException("Email já cadastrado!"); });
    }
}
