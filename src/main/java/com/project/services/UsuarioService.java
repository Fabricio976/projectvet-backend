package com.project.services;

import java.util.Date;
import java.util.List;

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
        Usuario newUser = new Usuario(data.name(), data.email(), encryptedPassword, data.cpf(),
                Role.CLIENT, data.address(), data.phone());
        return userRepository.save(newUser);
    }

    public Usuario registerManager(RegisterUserDTO data) {
        checkIfEmailExists(data.email());

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        Usuario newUser = new Usuario(data.name(), data.email(), encryptedPassword, data.cpf(),
                Role.MANAGER, data.address(), data.phone());
        return userRepository.save(newUser);
    }

    public String requestRecoveryCode(String email) {
        UserDetails foundUser = userRepository.findByEmail(email);
        if (foundUser == null) {
            throw new EmailNotFoundException("Email não encontrado: " + email);
        }
        return managerUser.solicitarCodigo(email);
    }

    public void changePassword(Usuario usuario) {
        String result = managerUser.alterarSenha(usuario);
        if (!"Senha alterada com sucesso!".equals(result)) {
            throw new RuntimeException(result); // pode ser substituída por uma exceção customizada
        }
    }

    public void checkCode(String email, String code) {
        Usuario user = (Usuario) userRepository.findByEmailAndCodeRecoveryPassword(email, code);
        if (user == null) {
            throw new InvalidRecoveryCodeException("Código inválido");
        }

        long segundos = (new Date().getTime() - user.getDateShippingCodigo().getTime()) / 1000;
        if (segundos > 900) {
            throw new InvalidRecoveryCodeException("Código expirado");
        }
    }

    private void checkIfEmailExists(String email) {
        if (userRepository.findByEmail(email) != null) {
            throw new EmailAlreadyExistsException("Email já cadastrado!");
        }
    }
}
