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

/**
 * Controller responsável pela autenticação e registro de usuários.
 * Contém endpoints para login, recuperação e alteração de senha, e registro de
 * clientes e funcionários.
 */
@RestController
@RequestMapping("/projectvet")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ManagerUser managerUser;

    /**
     * Solicita um código de recuperação de senha para o usuário.
     *
     * @param usuario Dados do usuário, incluindo o e-mail.
     * @return Código enviado para recuperação de senha.
     * @throws EmailNotFoundException Se o e-mail não for encontrado.
     */
    @PostMapping("/code-forgot")
    public ResponseEntity<Map<String, String>> recoverCode(@RequestBody Usuario usuario) {
        String email = usuario.getEmail();
        UserDetails foundUser = userRepository.findByEmail(email);
        Map<String, String> response = new HashMap<>();
        if (foundUser == null) {
            response.put("message", "Email não encontrado: " + email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        String result = managerUser.solicitarCodigo(email);
        response.put("message", result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, String>> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("codeRecoveryPassword");
        System.out.println("Verificando - Email: " + email + ", Código: " + code);
        Usuario user = (Usuario) userRepository.findByEmailAndCodeRecoveryPassword(email, code);
        Map<String, String> response = new HashMap<>();
        if (user != null) {
            Date diferenca = new Date(new Date().getTime() - user.getDateShippingCodigo().getTime());
            if (diferenca.getTime() / 1000 < 900) {
                response.put("message", "Código válido");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Código expirado");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }
        response.put("message", "Código inválido");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    /**
     * Altera a senha do usuário.
     *
     * @param usuario O objeto que contém os dados do usuário, incluindo a nova
     *                senha.
     * @return Uma mensagem indicando o resultado da operação de alteração de senha.
     */
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Usuario usuario) {
        System.out.println("Recebido em /change-password - Email: " + usuario.getEmail() +
                ", Código: " + usuario.getCodeRecoveryPassword() +
                ", Nova senha: " + usuario.getPassword());
        Map<String, String> response = new HashMap<>();
        String result = managerUser.alterarSenha(usuario);
        response.put("message", result);
        System.out.println("Resposta enviada: " + response);
        if (result.equals("Senha alterada com sucesso!")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    /**
     * endpoint para autenticação do usuário e geração de token JWT
     *
     * @param data Dados de autenticação recebidos no corpo da requisição
     * @return ResponseEntity contendo o token JWT e os detalhes do usuário
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO data) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);
            var token = tokenService.generateToken((Usuario) auth.getPrincipal());

            // Busca o usuário pelo email para obter o ID e o nome
            UserDetails user = userRepository.findByEmail(data.email());
            if (user == null) {
                throw new InvalidCredentialsException("Usuário não encontrado");
            }
            Usuario usuario = (Usuario) user;
            String userId = usuario.getId();
            String userName = usuario.getNome();

            return ResponseEntity.ok(new LoginResponseDTO(token, userId, userName));
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Email ou senha incorretos");
        }
    }

    /**
     * endpoint para registrar um novo cliente.
     *
     * @param data dados de registro recebidos no corpo da requisição
     * @return ResponseEntity indicando o resultado da operação de registro
     */
    @PostMapping("/register/client")
    public ResponseEntity<?> registerClient(@RequestBody @Valid RegisterUserDTO data) {
        if (this.userRepository.findByEmail(data.email()) != null) {
            throw new EmailAlreadyExistsException("Email já cadastrado!");
        }
        usuarioService.registerUser(data);
        return ResponseEntity.ok().body("Usuário cadastrado com sucesso!");
    }

    /**
     * Endpoint para registrar um novo funcionario.
     *
     * @param data Dados de registro recebidos no corpo da requisição
     * @return ResponseEntity indicando o resultado da operação de registro
     */
    @PostMapping("/register/funcionario")
    public ResponseEntity<?> registerFuncionario(@RequestBody @Valid RegisterUserDTO data) {
        if (this.userRepository.findByEmail(data.email()) != null) {
            throw new EmailAlreadyExistsException("Email já cadastrado!");
        }
        usuarioService.regiterManager(data);
        return ResponseEntity.ok().body("Usuário cadastrado com sucesso!");
    }
}
