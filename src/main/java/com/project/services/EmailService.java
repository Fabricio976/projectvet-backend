package com.project.services;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.project.model.exeptions.EmailException;
import com.project.model.exeptions.TemplateProcessingException;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Service responsável pelo envio de emails, incluindo emails de texto simples e
 * emails baseados em templates.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private Configuration fmConfiguration;

    @Value("${spring.mail.username}")
    private String remetente;

    /**
     * envia um email de texto para mudança da senha
     *
     * @param destinatario endereço de email do destinatário
     * @param titulo       assunto do email
     * @param mensagem     corpo da mensagem
     * @return mensagem indicando o sucesso ou falha do envio do email
     */
    public String enviarEmailTexto(String destinatario, String titulo, String mensagem) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(remetente);
            message.setTo(destinatario);
            message.setSubject(titulo);
            message.setText(mensagem);
            javaMailSender.send(message);
            return "Email enviado";
        } catch (MailException ex) {
            return "Erro ao enviar o email";
        }
    }

    /**
     * Envia um email com um template
     *
     * @param destinatario endereço de email do destinatário
     * @param titulo       assunto do email
     * @param propriedades propriedades a serem usadas no template do email
     * @return
     */

    public String enviarEmailTemplate(String destinatario, String titulo, Map<String, Object> propriedades) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setSubject(titulo);
            helper.setFrom(remetente);
            helper.setTo(destinatario);

            String templateName;

            if (titulo.contains("Nova Solicitação")) {
                templateName = "appointment_request.ftl";
            } else if (titulo.contains("Confirmação")) {
                templateName = "appointment_confirmation.ftl";
            } else if (titulo.contains("Rejeição")) {
                templateName = "appointment_rejection.ftl";
            } else {
                templateName = "recuperacao-codigo.ftl";
            }

            helper.setText(getConteudoTemplate(propriedades, templateName), true);
            javaMailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            throw new EmailException("Error ao enviar email com template!", e);
        }
        return destinatario;
    }

    /**
     * gera o conteúdo do email a partir de um template
     *
     * @return conteúdo do email gerado a partir do template
     */
    public String getConteudoTemplate(Map<String, Object> model, String templateName) {
        try {
            return FreeMarkerTemplateUtils
                    .processTemplateIntoString(fmConfiguration.getTemplate(templateName), model);
        } catch (TemplateException | IOException e) {
            throw new TemplateProcessingException("Error ao processar template com template!", e);
        }
    }
}
