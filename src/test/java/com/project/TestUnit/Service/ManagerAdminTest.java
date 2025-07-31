package com.project.TestUnit.Service;
import com.project.model.entitys.Usuario;
import com.project.model.repositorys.UserRepository;
import com.project.services.EmailService;
import com.project.services.details.ManagerAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ManagerAdminTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ManagerAdmin managerAdmin;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId("12345");
        usuario.setEmail("teste@exemplo.com");
        usuario.setPassword("senha123");
    }

    @Test
    void testSolicitarCodigo_Sucesso() {
        String email = "teste@exemplo.com";
        when(userRepository.findByEmail(email)).thenReturn(usuario);
        when(emailService.enviarEmailTexto(anyString(), anyString(), anyString())).thenReturn("Email enviado");

        String resultado = managerAdmin.solicitarCodigo(email);

        assertEquals("Codigo enviado para o seu e-mail", resultado);
        verify(userRepository, times(1)).saveAndFlush(usuario);
        verify(emailService, times(1)).enviarEmailTexto(eq(email), eq("Código de Recuperação de Senha"), anyString());
        assertNotNull(usuario.getCodeRecoveryPassword());
        assertNotNull(usuario.getDateShippingCodigo());
    }

    @Test
    void testAlterarSenha_Sucesso() {
        String email = "teste@exemplo.com";
        String codigo = "1234ABCD";
        String novaSenha = "novaSenha123";
        usuario.setCodeRecoveryPassword(codigo);
        usuario.setDateShippingCodigo(new Date(System.currentTimeMillis() - 300_000)); // 5min max

        when(userRepository.findByEmailAndCodeRecoveryPassword(email, codigo)).thenReturn(usuario);
        when(passwordEncoder.encode(novaSenha)).thenReturn("senhaCriptografada");

        Usuario userInput = new Usuario();
        userInput.setEmail(email);
        userInput.setCodeRecoveryPassword(codigo);
        userInput.setPassword(novaSenha);

        String resultado = managerAdmin.alterarSenha(userInput);


        assertEquals("Senha alterada com sucesso!", resultado);
        verify(userRepository, times(1)).saveAndFlush(usuario);
        assertEquals("senhaCriptografada", usuario.getPassword());
        assertNull(usuario.getCodeRecoveryPassword());
    }

    @Test
    void testAlterarSenha_CodigoExpirado() {
        String email = "teste@exemplo.com";
        String codigo = "1234ABCD";
        usuario.setCodeRecoveryPassword(codigo);
        usuario.setDateShippingCodigo(new Date(System.currentTimeMillis() - 1_000_000)); // mais de 15min

        when(userRepository.findByEmailAndCodeRecoveryPassword(email, codigo)).thenReturn(usuario);

        Usuario userInput = new Usuario();
        userInput.setEmail(email);
        userInput.setCodeRecoveryPassword(codigo);
        userInput.setPassword("novaSenha123");

        String resultado = managerAdmin.alterarSenha(userInput);

        assertEquals("Tempo expirado, solicite um novo código", resultado);
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void testAlterarSenha_EmailNaoEncontrado() {
        String email = "teste@exemplo.com";
        String codigo = "1234ABCD";

        when(userRepository.findByEmailAndCodeRecoveryPassword(email, codigo)).thenReturn(null);

        Usuario userInput = new Usuario();
        userInput.setEmail(email);
        userInput.setCodeRecoveryPassword(codigo);
        userInput.setPassword("novaSenha123");

        String resultado = managerAdmin.alterarSenha(userInput);

        assertEquals("E-mail ou código de recuperação incorretos ou inesistentes!", resultado);
        verify(userRepository, never()).saveAndFlush(any());
    }

}
