import org.example.theblog.service.InitService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "application.yaml", classes = InitService.class)
public class InitServiceTest {

    @Autowired
    InitService initService;

    @Test
    @DisplayName("Init is successful")
    public void initFromPropertiesFileTest() {
        assertEquals(Objects.requireNonNull(initService.init().getBody(),
                        "In the initFromPropertiesFileTest, the assertEquals parameter is null").phone(),
                "+7 905 655-41-53");
    }

}
