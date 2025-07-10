package com.project.TestUnit.Service;

import com.project.model.exeptions.EmailException;
import com.project.model.exeptions.TemplateProcessingException;
import com.project.services.EmailService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
    void setUp() throws Exception {
        var field = EmailService.class.getDeclaredField("remetente");
        field.setAccessible(true);
        field.set(emailService, "teste@exemplo.com");
    }

    @Test
    void testEnviarEmailTextoSucesso() {
        String resultado = emailService.enviarEmailTexto("dest@exemplo.com", "Assunto", "Mensagem");
        assertEquals("Email enviado", resultado);
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testEnviarEmailTextoErro() {
        doThrow(new MailException("Erro") {}).when(javaMailSender).send(any(SimpleMailMessage.class));
        String resultado = emailService.enviarEmailTexto("dest@exemplo.com", "Assunto", "Mensagem");
        assertEquals("Erro ao enviar o email", resultado);
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @ParameterizedTest
    @CsvSource({
            "Nova Solicitação,appointment_request.ftl",
            "Confirmação da Consulta,appointment_confirmation.ftl",
            "Rejeição de Agendamento,appointment_rejection.ftl",
            "Outro Assunto,recuperacao-codigo.ftl"
    })
    void testEnviarEmailTemplateEscolhaDeTemplate(String titulo, String templateEsperado) throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(fmConfiguration.getTemplate(templateEsperado)).thenReturn(template);

        Map<String, Object> props = Map.of("chave", "valor");

        try (MockedStatic<FreeMarkerTemplateUtils> mocked = mockStatic(FreeMarkerTemplateUtils.class)) {
            mocked.when(() -> FreeMarkerTemplateUtils.processTemplateIntoString(eq(template), eq(props)))
                    .thenReturn("<html>email</html>");

            String retorno = emailService.enviarEmailTemplate("dest@exemplo.com", titulo, props);

            assertEquals("dest@exemplo.com", retorno);
            verify(fmConfiguration).getTemplate(templateEsperado);
            verify(javaMailSender).send(any(MimeMessage.class));
        }
    }

    @Test
    void testGetConteudoTemplateComSucesso() throws Exception {
        Map<String, Object> model = Map.of("codigo", "123456");
        when(fmConfiguration.getTemplate("recuperacao-codigo.ftl")).thenReturn(template);

        try (MockedStatic<FreeMarkerTemplateUtils> mocked = mockStatic(FreeMarkerTemplateUtils.class)) {
            mocked.when(() -> FreeMarkerTemplateUtils.processTemplateIntoString(eq(template), eq(model)))
                    .thenReturn("Email processado");

            String resultado = emailService.getConteudoTemplate(model, "recuperacao-codigo.ftl");
            assertEquals("Email processado", resultado);
        }
    }

    @Test
    void testGetConteudoTemplateLancaTemplateException() throws Exception {
        Map<String, Object> model = Map.of("codigo", "123456");
        when(fmConfiguration.getTemplate("recuperacao-codigo.ftl")).thenReturn(template);

        try (MockedStatic<FreeMarkerTemplateUtils> mocked = mockStatic(FreeMarkerTemplateUtils.class)) {
            mocked.when(() -> FreeMarkerTemplateUtils.processTemplateIntoString(eq(template), eq(model)))
                    .thenThrow(new TemplateException("Erro", null));

            assertThrows(TemplateProcessingException.class, () -> emailService.getConteudoTemplate(model, "recuperacao-codigo.ftl"));
        }
    }

    @Test
    void testGetConteudoTemplateLancaIOException() throws Exception {
        when(fmConfiguration.getTemplate("recuperacao-codigo.ftl")).thenThrow(new IOException("Erro de IO"));
        assertThrows(TemplateProcessingException.class, () ->
                emailService.getConteudoTemplate(Map.of(), "recuperacao-codigo.ftl"));
    }


}
