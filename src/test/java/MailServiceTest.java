import org.example.theblog.model.entity.User;
import org.example.theblog.model.repository.UserRepository;
import org.example.theblog.service.MailService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

public class MailServiceTest {

    private static final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private static MailService mailService;
    MailService.MailRequest mailRequest = new MailService.MailRequest("test@test.com");

    @BeforeAll
    static void beforeAll() {
        mailService = new MailService(userRepository);
    }

    @Test
    @DisplayName("Restore password is successful")
    public void restoreTest() {
        Mockito.when(userRepository.findByEmail(any())).thenReturn(Optional.of(new User()));
        assertTrue(Objects.requireNonNull(mailService.restore(mailRequest).getBody(),
                "In the restoreTest, the assertTrue parameter is null").result());
    }

    @Test
    @DisplayName("Restore password, user not found")
    public void restorePasswordUserNotFoundTest() {
        Mockito.when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        assertFalse(Objects.requireNonNull(mailService.restore(mailRequest).getBody(),
                "In the restorePasswordUserNotFoundTest, the assertFalse parameter is null").result());
    }
}
