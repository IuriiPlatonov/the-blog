import org.example.theblog.service.InitService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "application.yaml", classes = InitService.class)
public class InitServiceTest {

    @Autowired
    InitService initService;

    @Test
    @DisplayName("Init is successful")
    public void initFromPropertiesFileTest() {
        assertEquals(initService.init().phone(), "+7 905 655-41-53");
    }

}
