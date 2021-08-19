package org.example.theblog.service;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import org.example.theblog.model.entity.GlobalSetting;
import org.example.theblog.model.repository.GlobalSettingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class SettingsService {

    private final GlobalSettingRepository globalSettingRepository;

    public ResponseEntity<SettingsResponse> getGlobalSettings() {
        Map<String, Boolean> settingKeyValue = new HashMap<>();

        List<GlobalSetting> globalSettings = globalSettingRepository.findAll();
        for (GlobalSetting globalSetting : globalSettings) {
            boolean enabled = globalSetting.getValue().equals("YES");
            String mode = globalSetting.getCode();
            settingKeyValue.put(mode, enabled);
        }

        return ResponseEntity.ok(new SettingsResponse(settingKeyValue));
    }

    public void setGlobalSettings(SettingsRequest request) {
        List<GlobalSetting> globalSettings = globalSettingRepository.findAll();

        for (GlobalSetting globalSetting : globalSettings) {
            switch (globalSetting.getCode()) {
                case "MULTIUSER_MODE" -> globalSetting.setValue(request.multiuserMode() ? "YES" : "NO");
                case "POST_PREMODERATION" -> globalSetting.setValue(request.postPremoderation() ? "YES" : "NO");
                case "STATISTICS_IS_PUBLIC" -> globalSetting.setValue(request.statisticsIsPublic() ? "YES" : "NO");
            }
        }

        globalSettingRepository.flush();
    }

    public record SettingsRequest(@JsonProperty("MULTIUSER_MODE") boolean multiuserMode,
                                  @JsonProperty("POST_PREMODERATION") boolean postPremoderation,
                                  @JsonProperty("STATISTICS_IS_PUBLIC") boolean statisticsIsPublic) {

    }

    public record SettingsResponse(@JsonAnyGetter Map<String, Boolean> settingKeyValue) {
    }
}
