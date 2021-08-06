package org.example.theblog.service;

import lombok.RequiredArgsConstructor;
import org.example.theblog.model.entity.User;
import org.example.theblog.model.repository.UserRepository;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class MailService {

    private final UserRepository userRepository;
    private final Environment environment;

    public MailResponse restore(MailRequest request) {
        return sendMail(request.email());
    }

    private MailResponse sendMail(String recipient) {
        User user = userRepository.findByEmail(recipient).orElse(null);

        if (user != null) {
            String code = new BCryptPasswordEncoder(12)
                    .encode(String.valueOf(Math.random() * 1_000_000)).replaceAll("/", "");
            final String port = environment.getProperty("server.port");
            System.out.println(Arrays.toString(environment.getDefaultProfiles()));
            final String hostName = InetAddress.getLoopbackAddress().getHostAddress();
            final String url = String.format("https://%s:%s", hostName, port);
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
        return user != null ? new MailResponse(true) : new MailResponse(false);
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
