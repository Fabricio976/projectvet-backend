package com.project.controllers;

import com.project.model.dto.AuthenticationDTO;
import com.project.model.dto.LoginResponseDTO;
import com.project.model.dto.RegisterUserDTO;
import com.project.model.entitys.Usuario;
import com.project.model.exeptions.InvalidCredentialsException;
import com.project.services.UsuarioService;
import com.project.services.details.TokenService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

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
        return response("message", result);
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, String>> verifyCode(@RequestBody Map<String, String> request) {
        usuarioService.checkCode(request.get("email"), request.get("codeRecoveryPassword"));
        return response("message", "Código válido");
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Usuario usuario) {
        usuarioService.changePassword(usuario);
        return response("message", "Senha alterada com sucesso!");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        return Optional.of(data)
                .map(d -> new UsernamePasswordAuthenticationToken(d.email(), d.password()))
                .map(authenticationManager::authenticate)
                .map(auth -> {
                    Usuario usuario = (Usuario) auth.getPrincipal();
                    String token = tokenService.generateToken(usuario);
                    return new LoginResponseDTO(token, usuario.getId(), usuario.getName(), auth.getAuthorities());
                })
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new InvalidCredentialsException("Erro inesperado no login")); // fallback seguro
    }


    @PostMapping("/register/client")
    public ResponseEntity<Map<String, String>> registerClient(@RequestBody @Valid RegisterUserDTO data) {
        usuarioService.registerUser(data);
        return response("message", "Usuário cadastrado com sucesso!");
    }

    @PostMapping("/register/manager")
    public ResponseEntity<Map<String, String>> registerFuncionario(@RequestBody @Valid RegisterUserDTO data) {
        usuarioService.registerManager(data);
        return response("message", "Usuário cadastrado com sucesso!");
    }

    private ResponseEntity<Map<String, String>> response(String key, String value) {
        return ResponseEntity.ok(Map.of(key, value));
    }
}
