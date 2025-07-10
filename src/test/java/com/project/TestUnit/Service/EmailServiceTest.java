package com.project.TestUnit.Service;

import com.project.model.exeptions.EmailException;
import com.project.model.exeptions.TemplateProcessingException;
import com.project.services.EmailService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private Configuration fmConfiguration;

    @Mock
    private Template template;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        // Configura o remetente via reflexão
        try {
            java.lang.reflect.Field field = EmailService.class.getDeclaredField("remetente");
            field.setAccessible(true);
            field.set(emailService, "teste@exemplo.com");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testEnviarEmailTextoSucesso() {
        // Arrange
        String destinatario = "destinatario@exemplo.com";
        String titulo = "Teste Assunto";
        String mensagem = "Teste Mensagem";

        // Act
        String resultado = emailService.enviarEmailTexto(destinatario, titulo, mensagem);

        // Assert
        assertEquals("Email enviado", resultado);
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testEnviarEmailTextoErro() {
        // Arrange
        String destinatario = "destinatario@exemplo.com";
        String titulo = "Teste Assunto";
        String mensagem = "Teste Mensagem";
        doThrow(new MailException("Erro de envio") {}).when(javaMailSender).send(any(SimpleMailMessage.class));

        // Act
        String resultado = emailService.enviarEmailTexto(destinatario, titulo, mensagem);

        // Assert
        assertEquals("Erro ao enviar o email", resultado);
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testEnviarEmailTemplateSucesso() throws Exception {
        // Arrange
        String destinatario = "destinatario@exemplo.com";
        String titulo = "Teste Template";
        Map<String, Object> propriedades = new HashMap<>();
        propriedades.put("chave", "valor");

        when(fmConfiguration.getTemplate(anyString())).thenReturn(template);
        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        // Act
        String resultado = emailService.enviarEmailTemplate(destinatario, titulo, propriedades);

        // Assert
        assertEquals(destinatario, resultado);
        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }


    @Test
    void testGetConteudoTemplateSucesso() throws Exception {
        // Arrange
        Map<String, Object> model = new HashMap<>();
        model.put("chave", "valor");

        when(fmConfiguration.getTemplate("recuperacao-codigo.ftl")).thenReturn(template);

        String conteudoEsperado = "Conteúdo processado";
        try (MockedStatic<FreeMarkerTemplateUtils> mocked = mockStatic(FreeMarkerTemplateUtils.class)) {
            mocked.when(() -> FreeMarkerTemplateUtils.processTemplateIntoString(eq(template), eq(model)))
                    .thenReturn(conteudoEsperado);

            // Act
            String resultado = emailService.getConteudoTemplate(model);

            // Assert
            assertEquals(conteudoEsperado, resultado);
        }
    }

    @Test
    void testGetConteudoTemplateTemplateException() throws Exception {
        // Arrange
        Map<String, Object> model = new HashMap<>();
        model.put("chave", "valor");
        when(fmConfiguration.getTemplate("recuperacao-codigo.ftl")).thenReturn(template);

        try (MockedStatic<FreeMarkerTemplateUtils> mocked = mockStatic(FreeMarkerTemplateUtils.class)) {
            mocked.when(() -> FreeMarkerTemplateUtils.processTemplateIntoString(eq(template), eq(model)))
                    .thenThrow(new TemplateException("Erro no template", null));

            // Act & Assert
            assertThrows(TemplateProcessingException.class, () -> emailService.getConteudoTemplate(model));
        }
    }

    @Test
    void testGetConteudoTemplateIOException() throws Exception {
        // Arrange
        Map<String, Object> model = new HashMap<>();
        model.put("chave", "valor");
        when(fmConfiguration.getTemplate("recuperacao-codigo.ftl")).thenThrow(new IOException("Erro de IO"));

        // Act & Assert
        assertThrows(TemplateProcessingException.class, () -> emailService.getConteudoTemplate(model));
    }
}

