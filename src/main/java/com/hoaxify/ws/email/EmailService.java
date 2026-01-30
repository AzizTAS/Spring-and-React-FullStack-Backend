package com.hoaxify.ws.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.hoaxify.ws.configuration.HoaxifyProperties;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    private final HoaxifyProperties hoaxifyProperties;
    private final MessageSource messageSource;

    public EmailService(HoaxifyProperties hoaxifyProperties, MessageSource messageSource) {
        this.hoaxifyProperties = hoaxifyProperties;
        this.messageSource = messageSource;
        logger.info("EmailService initialized with SendGrid");
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
        logger.info("Attempting to send activation email to: {} via SendGrid", email);
        try {
            var activationUrl = hoaxifyProperties.getClient().host() + "/activation/" + activationToken;
            var title = messageSource.getMessage("hoaxify.mail.user.created.title", null, LocaleContextHolder.getLocale());
            var clickHere = messageSource.getMessage("hoaxify.mail.click.here", null, LocaleContextHolder.getLocale());

            var mailBody = activationEmail
                    .replace("${url}", activationUrl)
                    .replace("${title}", title)
                    .replace("${clickHere}", clickHere);

            Email from = new Email(fromEmail);
            Email to = new Email(email);
            Content content = new Content("text/html", mailBody);
            Mail mail = new Mail(from, title, to, content);

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            logger.info("Sending activation email from: {} to: {}", fromEmail, email);
            Response response = sg.api(request);
            logger.info("SendGrid response - Status: {}, Body: {}", response.getStatusCode(), response.getBody());
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                logger.info("Activation email sent successfully to: {}", email);
            } else {
                logger.error("SendGrid returned non-success status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error sending activation email to {}: {}", email, e.getMessage(), e);
        }
    }

    @Async
    public void sendPasswordResetEmail(String email, String passwordResetToken) {
        logger.info("Attempting to send password reset email to: {} via SendGrid", email);
        try {
            String passwordResetUrl = hoaxifyProperties.getClient().host() + "/password-reset/set?tk=" + passwordResetToken;
            var title = "Reset your password";
            var clickHere = messageSource.getMessage("hoaxify.mail.click.here", null, LocaleContextHolder.getLocale());
            var mailBody = activationEmail.replace("${url}", passwordResetUrl).replace("${title}", title)
                    .replace("${clickHere}", clickHere);

            Email from = new Email(fromEmail);
            Email to = new Email(email);
            Content content = new Content("text/html", mailBody);
            Mail mail = new Mail(from, title, to, content);

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            logger.info("Sending password reset email to: {}", email);
            Response response = sg.api(request);
            logger.info("SendGrid response - Status: {}", response.getStatusCode());
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                logger.info("Password reset email sent successfully to: {}", email);
            } else {
                logger.error("SendGrid returned non-success status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error sending password reset email to {}: {}", email, e.getMessage(), e);
        }
    }

}
