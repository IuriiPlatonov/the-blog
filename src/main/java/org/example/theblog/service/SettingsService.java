package org.example.theblog.service;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import org.example.theblog.model.repository.GlobalSettingRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SettingsService {

    public record SettingsResponse(@JsonAnyGetter Map<String, Boolean> settingKeyValue){}

    private final GlobalSettingRepository globalSettingRepository;

    public SettingsService(GlobalSettingRepository globalSettingRepository) {
        this.globalSettingRepository = globalSettingRepository;
    }

    public SettingsResponse getGlobalSettings() {
        Map<String, Boolean> settingKeyValue = new HashMap<>();

        globalSettingRepository.findAll().forEach(globalSetting ->
                settingKeyValue.put(globalSetting.getCode(), globalSetting.getValue().equals("YES")));
        return new SettingsResponse(settingKeyValue);
    }
}
