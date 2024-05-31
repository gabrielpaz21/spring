package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private final SpringTemplateEngine engine;
    private final JavaMailSender mailSender;

    public MailService(SpringTemplateEngine engine,
                       JavaMailSender mailSender) {
        this.engine = engine;
        this.mailSender = mailSender;
    }

    // render html template
    public void sendEmailFromTemplate(String email, String templateName, String subject){
        Context context = new Context();
        context.setVariable("message1", "Hello World");
        context.setVariable("message2", "Goodbye world");
        String content = engine.process(templateName, context);
        sendEmail(email, subject, content, false, true);
    }

    // send the email
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.info("Sending email to: {} with subject {}", to, subject);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            var helper = new MimeMessageHelper(message, isMultipart, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("noreply@company.com");
            helper.setText(content, isHtml);
            mailSender.send(message);
            log.info("Sent email to: {} with subject {}", to, subject);
        } catch (MessagingException e) {
            log.error("Error sending email to: {} with subject {}", to, subject);

        }

    }
}
