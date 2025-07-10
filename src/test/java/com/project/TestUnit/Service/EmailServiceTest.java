package com.project.TestUnit.Service;
import com.project.model.exeptions.EmailException;
import com.project.model.exeptions.TemplateProcessingException;
import com.project.services.EmailService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private Configuration fmConfiguration;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEnviarEmailTextoComSucesso() {
        String result = emailService.enviarEmailTexto("dest@teste.com", "Assunto", "Mensagem do corpo");
        assertEquals("Email enviado", result);
    }

    @Test
    void testEnviarEmailTextoComFalha() {
        doThrow(new MailException("Erro ao enviar") {}).when(javaMailSender).send(any(SimpleMailMessage.class));

        String result = emailService.enviarEmailTexto("dest@teste.com", "Assunto", "Mensagem do corpo");
        assertEquals("Erro ao enviar o email", result);
    }

    @Test
    void testEnviarEmailTemplateComSucesso() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        Template template = mock(Template.class);
        when(fmConfiguration.getTemplate("recuperacao-codigo.ftl")).thenReturn(template);
        when(FreeMarkerTemplateUtils.processTemplateIntoString(eq(template), any())).thenReturn("<html>conteudo</html>");

        Map<String, Object> props = new HashMap<>();
        props.put("codigo", "123456");

        assertDoesNotThrow(() -> emailService.enviarEmailTemplate("dest@teste.com", "Recuperação", props));
        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    void testEnviarEmailTemplateFalhaMensagem() throws Exception {
        when(javaMailSender.createMimeMessage()).thenThrow(new MessagingException("Erro ao criar"));

        Map<String, Object> props = new HashMap<>();
        props.put("codigo", "123456");

        assertThrows(EmailException.class, () -> emailService.enviarEmailTemplate("dest@teste.com", "Assunto", props));
    }

    @Test
    void testGetConteudoTemplateSucesso() throws Exception {
        Template template = mock(Template.class);
        when(fmConfiguration.getTemplate("recuperacao-codigo.ftl")).thenReturn(template);
        when(FreeMarkerTemplateUtils.processTemplateIntoString(eq(template), any())).thenReturn("<html>Email</html>");

        Map<String, Object> model = Map.of("codigo", "123456");
        String result = emailService.getConteudoTemplate(model);

        assertEquals("<html>Email</html>", result);
    }

    @Test
    void testGetConteudoTemplateFalha() throws Exception {
        when(fmConfiguration.getTemplate("recuperacao-codigo.ftl")).thenThrow(new IOException("Template não encontrado"));

        Map<String, Object> model = Map.of("codigo", "123456");
        assertThrows(TemplateProcessingException.class, () -> emailService.getConteudoTemplate(model));
    }
}
