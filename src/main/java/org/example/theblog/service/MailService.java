package org.example.theblog.service;

import lombok.RequiredArgsConstructor;
import org.example.theblog.model.entity.User;
import org.example.theblog.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {

    private final UserRepository userRepository;
    @Value("${blog.hostAddress}")
    private String url;

    public ResponseEntity<MailResponse> restore(MailRequest request) {
        return ResponseEntity.ok(sendMail(request.email()));
    }

    private MailResponse sendMail(String recipient) {
        User user = userRepository.findByEmail(recipient).orElse(null);

        if (Objects.nonNull(user)) {
            String code = UUID.randomUUID().toString();

            String text = String.format("Для восстановления пароля, " +
                                        "пройдите по этой ссылке: %s/login/change-password/%s", url, code);
            user.setCode(code);
            userRepository.flush();

            String blogAccountEmail = "platonov230388@gmail.com";
            String password = "DdSwGQm1667{U*";

            var session = getSession(getProperties(), blogAccountEmail, password);

            Message message = new MimeMessage(session);
            try {
                message.setFrom(new InternetAddress(blogAccountEmail));
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
                message.setSubject("Восстановление пароля the-blog");
                message.setText(text);
                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return Objects.nonNull(user) ? new MailResponse(true) : new MailResponse(false);
    }

    private Properties getProperties() {
        var properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        return properties;
    }

    private Session getSession(Properties properties, String blogAccountEmail, String password) {
        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(blogAccountEmail, password);
            }
        });
    }

    public record MailResponse(boolean result) {
    }

    public record MailRequest(String email) {
    }
}
