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

    public List<Usuario> searchAllUser() {
        return userRepository.findAll();
    }
    
    public Usuario findByCpf(String cpf) {
        Usuario usuario = userRepository.findByCpf(cpf);
        if (usuario == null) {
            throw new RgNotFoundException("Não existe usuário com esse CPF: " + cpf);
        }
        return usuario;
    }

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

    public String editRegister(Usuario usuario) {
        userRepository.saveAndFlush(usuario);
        return ("Editado com Sucesso!");
    }

    public void excluir(String id) {
        Usuario usuario = userRepository.findById(id).get();
        userRepository.delete(usuario);
    }

}
