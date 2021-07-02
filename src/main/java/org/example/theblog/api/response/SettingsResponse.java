package org.example.theblog.api.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class SettingsResponse {
    @JsonAnyGetter
    private Map<String, Boolean> settingKeyValue = new HashMap<>();

    public void put(String code, boolean value) {
        settingKeyValue.put(code, value);
    }
}
