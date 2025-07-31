package com.project.TestUnit.Service;


import com.project.model.dto.RegisterUserDTO;
import com.project.model.entitys.Usuario;
import com.project.model.entitys.enums.RoleName;
import com.project.model.exeptions.EmailAlreadyExistsException;
import com.project.model.exeptions.EmailNotFoundException;
import com.project.model.exeptions.InvalidRecoveryCodeException;
import com.project.model.repositorys.UserRepository;
import com.project.config.SecurityConfigurations;
import com.project.services.UsuarioService;
import com.project.services.details.ManagerAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityConfigurations securityConfigurations;

    @Mock
    private ManagerAdmin managerAdmin;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(securityConfigurations.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
    }

    @Test
    void testRegisterUser_Success() {
        RegisterUserDTO dto = new RegisterUserDTO("Nome", "email@teste.com", "senha123", "12345678900",RoleName.ROLE_CLIENT, "Rua", "83 1234-4123");

        when(userRepository.findByEmail(dto.email())).thenReturn(null);

        assertDoesNotThrow(() -> usuarioService.registerUser(dto));
        verify(userRepository).save(any(Usuario.class));
    }

    @Test
    void testRegisterUserEmailExists() {
        RegisterUserDTO dto = new RegisterUserDTO("Nome", "email@teste.com", "senha123", "12345678900",RoleName.ROLE_MANAGER, "Rua", "83 1234-4232");
        when(userRepository.findByEmail(dto.email())).thenReturn(new Usuario());

        assertThrows(EmailAlreadyExistsException.class, () -> usuarioService.registerUser(dto));
    }

    @Test
    void testRegisterManager_Success() {
        RegisterUserDTO dto = new RegisterUserDTO("Gerente", "gerente@teste.com", "senha123", "32165498700", RoleName.ROLE_MANAGER, "Rua", "83 1234-4123");
        when(userRepository.findByEmail(dto.email())).thenReturn(null);

        assertDoesNotThrow(() -> usuarioService.registerManager(dto));
        verify(userRepository).save(any(Usuario.class));
    }

    @Test
    void testRequestRecoveryCode_Success() {
        Usuario user = new Usuario();
        when(userRepository.findByEmail("email@teste.com")).thenReturn(user);
        when(managerAdmin.solicitarCodigo("email@teste.com")).thenReturn("123456");

        String result = usuarioService.requestRecoveryCode("email@teste.com");
        assertEquals("123456", result);
    }

    @Test
    void testSolicitarRecuperação_CodeEmailNotFound() {
        when(userRepository.findByEmail("naoexiste@teste.com")).thenReturn(null);

        assertThrows(EmailNotFoundException.class, () -> usuarioService.requestRecoveryCode("naoexiste@teste.com"));
    }

    @Test
    void testMudarSenha_Success() {
        Usuario user = new Usuario();
        when(managerAdmin.alterarSenha(user)).thenReturn("Senha alterada com sucesso!");

        assertDoesNotThrow(() -> usuarioService.changePassword(user));
    }

    @Test
    void testMudarSenha_Falhou() {
        Usuario user = new Usuario();
        when(managerAdmin.alterarSenha(user)).thenReturn("Erro interno!");

        assertThrows(RuntimeException.class, () -> usuarioService.changePassword(user));
    }

    @Test
    void testCheckCodeSuccess() {
        Usuario user = new Usuario();
        user.setDateShippingCodigo(new Date());

        when(userRepository.findByEmailAndCodeRecoveryPassword("email@teste.com", "123456")).thenReturn(user);

        assertDoesNotThrow(() -> usuarioService.checkCode("email@teste.com", "123456"));
    }

    @Test
    void testCheckCodeInvalid() {
        when(userRepository.findByEmailAndCodeRecoveryPassword("email@teste.com", "123456")).thenReturn(null);

        assertThrows(InvalidRecoveryCodeException.class, () -> usuarioService.checkCode("email@teste.com", "123456"));
    }

    @Test
    void testCheckCodeExpired() {
        Usuario user = new Usuario();
        Date dateExpirada = new Date(System.currentTimeMillis() - (16 * 60 * 1000)); // 16 minutos atrás
        user.setDateShippingCodigo(dateExpirada);

        when(userRepository.findByEmailAndCodeRecoveryPassword("email@teste.com", "123456")).thenReturn(user);

        assertThrows(InvalidRecoveryCodeException.class, () -> usuarioService.checkCode("email@teste.com", "123456"));
    }
}
