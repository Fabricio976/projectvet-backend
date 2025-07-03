package com.project.controllers;



import com.project.model.exeptions.EmailAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.project.model.dto.AuthenticationDTO;
import com.project.model.dto.LoginResponseDTO;
import com.project.model.dto.RegisterUserDTO;
import com.project.model.entitys.Usuario;
import com.project.model.exeptions.EmailNotFoundException;
import com.project.model.exeptions.InvalidCredentialsException;
import com.project.model.repositorys.UserRepository;
import com.project.services.UsuarioService;
import com.project.services.details.ManagerUser;
import com.project.services.details.TokenService;

import jakarta.validation.Valid;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/projectvet")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/code-forgot")
    public ResponseEntity<Map<String, String>> recoveryCode(@RequestBody Usuario usuario) {
        String result = usuarioService.requestRecoveryCode(usuario.getEmail());
        return ResponseEntity.ok(Map.of("message", result));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, String>> verifyCode(@RequestBody Map<String, String> request) {
        usuarioService.checkCode(request.get("email"), request.get("codeRecoveryPassword"));
        return ResponseEntity.ok(Map.of("message", "Código válido"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Usuario usuario) {
        usuarioService.changePassword(usuario);
        return ResponseEntity.ok(Map.of("message", "Senha alterada com sucesso!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO data) {
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(data.email(), data.password())
            );
            Usuario usuario = (Usuario) auth.getPrincipal();
            String token = tokenService.generateToken(usuario);

            return ResponseEntity.ok(new LoginResponseDTO(token, usuario.getId(), usuario.getName()));
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Email ou senha incorretos");
        }
    }

    @PostMapping("/register/client")
    public ResponseEntity<?> registerClient(@RequestBody @Valid RegisterUserDTO data) {
        usuarioService.registerUser(data);
        return ResponseEntity.ok("Usuário cadastrado com sucesso!");
    }

    @PostMapping("/register/funcionario")
    public ResponseEntity<?> registerFuncionario(@RequestBody @Valid RegisterUserDTO data) {
        usuarioService.registerManager(data);
        return ResponseEntity.ok("Usuário cadastrado com sucesso!");
    }
}

