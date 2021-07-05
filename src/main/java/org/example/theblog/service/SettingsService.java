package org.example.theblog.service;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.AllArgsConstructor;
import org.example.theblog.model.repository.GlobalSettingRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class SettingsService {

    public record SettingsResponse(@JsonAnyGetter Map<String, Boolean> settingKeyValue){}

    private final GlobalSettingRepository globalSettingRepository;

    public SettingsResponse getGlobalSettings() {
        Map<String, Boolean> settingKeyValue = new HashMap<>();

        globalSettingRepository.findAll().forEach(globalSetting ->
                settingKeyValue.put(globalSetting.getCode(), globalSetting.getValue().equals("YES")));
        return new SettingsResponse(settingKeyValue);
    }
}
