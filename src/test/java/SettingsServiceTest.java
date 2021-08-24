import org.example.theblog.model.entity.GlobalSetting;
import org.example.theblog.model.repository.GlobalSettingRepository;
import org.example.theblog.service.SettingsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SettingsServiceTest {

    GlobalSettingRepository globalSettingRepository = Mockito.mock(GlobalSettingRepository.class);
    SettingsService settingsService = new SettingsService(globalSettingRepository);

    @Test
    @DisplayName("Get global settings is successful")
    public void getGlobalSettings() {
        GlobalSetting globalSetting1 = new GlobalSetting();
        globalSetting1.setCode("testOne");
        globalSetting1.setValue("YES");

        GlobalSetting globalSetting2 = new GlobalSetting();
        globalSetting2.setCode("testTwo");
        globalSetting2.setValue("NO");
        List<GlobalSetting> globalSettings = List.of(globalSetting1, globalSetting2);

        Mockito.when(globalSettingRepository.findAll()).thenReturn(globalSettings);

        assertTrue(settingsService.getGlobalSettings().settingKeyValue().get("testOne"));
        assertFalse(settingsService.getGlobalSettings().settingKeyValue().get("testTwo"));
    }
}
