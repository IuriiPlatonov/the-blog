import org.example.theblog.model.entity.User;
import org.example.theblog.model.repository.UserRepository;
import org.example.theblog.service.MailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest(properties = "application.yaml", classes = MailService.class)
public class MailServiceTest {

    @MockBean
    UserRepository userRepository;

    @Autowired
    MailService mailService;

    MailService.MailRequest mailRequest = new MailService.MailRequest("platonov230388@gmail.com");

    @Test
    @DisplayName("Restore password is successful")
    public void restoreTest() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new User()));
        assertTrue(mailService.restore(mailRequest).result());
    }

    @Test
    @DisplayName("Restore password, user not found")
    public void restorePasswordUserNotFoundTest() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        assertFalse(mailService.restore(mailRequest).result());
    }
}
