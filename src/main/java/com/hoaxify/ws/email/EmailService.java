package com.hoaxify.ws.email;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.hoaxify.ws.configuration.HoaxifyProperties;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    JavaMailSenderImpl mailSender;

    private final HoaxifyProperties hoaxifyProperties;

    private final MessageSource messageSource;

    public EmailService(HoaxifyProperties hoaxifyProperties, MessageSource messageSource) {
        this.hoaxifyProperties = hoaxifyProperties;
        this.messageSource = messageSource;
    }

    @PostConstruct
    public void initialize() {
        logger.info("Initializing EmailService with host: {}", hoaxifyProperties.getEmail().host());
        this.mailSender = new JavaMailSenderImpl();
        mailSender.setHost(hoaxifyProperties.getEmail().host());
        mailSender.setPort(hoaxifyProperties.getEmail().port());
        mailSender.setUsername(hoaxifyProperties.getEmail().username());
        mailSender.setPassword(hoaxifyProperties.getEmail().password());

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true"); // SSL for port 465
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.connectiontimeout", "15000"); // 15 seconds
        properties.put("mail.smtp.timeout", "15000"); // 15 seconds
        properties.put("mail.smtp.writetimeout", "15000"); // 15 seconds
        properties.put("mail.debug", "true"); // Debug logging
        
        logger.info("SMTP Configuration - Port: {}, SSL: enabled", hoaxifyProperties.getEmail().port());
        
        logger.info("EmailService initialized successfully");
    }

    String activationEmail = """
            <html>
                <body>
                    <h1>${title}</h1>
                    <a href="${url}">${clickHere}</a>
                </body>
            </html>
            """;

    @Async
    public void sendActivationEmail(String email, String activationToken) {
        logger.info("Attempting to send activation email to: {}", email);
        try {
            var activationUrl = hoaxifyProperties.getClient().host() + "/activation/" + activationToken;
            var title = messageSource.getMessage("hoaxify.mail.user.created.title", null, LocaleContextHolder.getLocale());
            var clickHere = messageSource.getMessage("hoaxify.mail.click.here", null, LocaleContextHolder.getLocale());

            var mailBody = activationEmail
                    .replace("${url}", activationUrl)
                    .replace("${title}", title)
                    .replace("${clickHere}", clickHere);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
            message.setFrom(hoaxifyProperties.getEmail().from());
            message.setTo(email);
            message.setSubject(title);
            message.setText(mailBody, true);
            
            logger.info("Sending activation email from: {} to: {}", hoaxifyProperties.getEmail().from(), email);
            this.mailSender.send(mimeMessage);
            logger.info("Activation email sent successfully to: {}", email);
        } catch (MessagingException e) {
            logger.error("MessagingException while sending activation email to {}: {}", email, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while sending activation email to {}: {}", email, e.getMessage(), e);
        }
    }

    @Async
    public void sendPasswordResetEmail(String email, String passwordResetToken) {
        logger.info("Attempting to send password reset email to: {}", email);
        try {
            String passwordResetUrl = hoaxifyProperties.getClient().host() + "/password-reset/set?tk=" + passwordResetToken;
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
            var title = "Reset your password";
            var clickHere = messageSource.getMessage("hoaxify.mail.click.here", null, LocaleContextHolder.getLocale());
            var mailBody = activationEmail.replace("${url}", passwordResetUrl).replace("${title}", title)
                    .replace("${clickHere}", clickHere);
            
            message.setFrom(hoaxifyProperties.getEmail().from());
            message.setTo(email);
            message.setSubject(title);
            message.setText(mailBody, true);
            
            logger.info("Sending password reset email to: {}", email);
            this.mailSender.send(mimeMessage);
            logger.info("Password reset email sent successfully to: {}", email);
        } catch (MessagingException e) {
            logger.error("MessagingException while sending password reset email to {}: {}", email, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while sending password reset email to {}: {}", email, e.getMessage(), e);
        }
    }

}