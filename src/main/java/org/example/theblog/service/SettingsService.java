package org.example.theblog.service;

import org.example.theblog.api.response.SettingsResponse;
import org.example.theblog.model.repository.GlobalSettingRepository;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    private final GlobalSettingRepository globalSettingRepository;

    public SettingsService(GlobalSettingRepository globalSettingRepository) {
        this.globalSettingRepository = globalSettingRepository;
    }

    public SettingsResponse getGlobalSettings() {
        SettingsResponse settingsResponse = new SettingsResponse();
        globalSettingRepository.findAll().forEach(globalSetting ->
                settingsResponse.put(globalSetting.getCode(), globalSetting.getValue().equals("YES")));
        return settingsResponse;
    }
}
