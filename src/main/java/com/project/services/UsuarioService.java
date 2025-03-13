package com.project.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    public Usuario registerUser(RegisterUserDTO data) {
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        Usuario newUser = new Usuario(data.name(), data.email(), encryptedPassword, data.cpf(),
                Role.CLIENT,
                data.address(),
                data.phone());
        this.userRepository.save(newUser);

        return newUser;
    }

    public Usuario regiterManager(RegisterUserDTO data) {
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        Usuario newUser = new Usuario(data.name(), data.email(), encryptedPassword, data.cpf(),
                Role.MANAGER,
                data.address(),
                data.phone());
        this.userRepository.save(newUser);

        return newUser;
    }



}
