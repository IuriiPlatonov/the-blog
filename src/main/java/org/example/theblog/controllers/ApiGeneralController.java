package org.example.theblog.controllers;

import org.example.theblog.api.response.InitResponse;
import org.example.theblog.api.response.SettingsResponse;
import org.example.theblog.api.response.TagResponse;
import org.example.theblog.service.SettingsService;
import org.example.theblog.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiGeneralController {

    private final SettingsService settingsService;
    private final InitResponse initResponse;
    private final TagService tagService;

    public ApiGeneralController(SettingsService settingsService, InitResponse initResponse, TagService tagService) {
        this.settingsService = settingsService;
        this.initResponse = initResponse;
        this.tagService = tagService;
    }

    @GetMapping("/api/settings")
    private SettingsResponse getSettings() {
        return settingsService.getGlobalSettings();
    }


    @GetMapping("/api/init")
    private InitResponse init() {
        return initResponse;
    }

    @GetMapping("/api/tag")
    private TagResponse getTags() {
        return tagService.getTags("");
    }
}
