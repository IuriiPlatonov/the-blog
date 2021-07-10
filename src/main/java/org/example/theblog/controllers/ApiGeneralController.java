package org.example.theblog.controllers;

import lombok.AllArgsConstructor;
import org.example.theblog.api.response.InitResponse;
import org.example.theblog.service.CalendarService;
import org.example.theblog.service.SettingsService;
import org.example.theblog.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ApiGeneralController {

    private final SettingsService settingsService;
    private final InitResponse initResponse;
    private final TagService tagService;
    private final CalendarService calendarService;

    @GetMapping("/api/settings")
    public SettingsService.SettingsResponse getSettings() {
        return settingsService.getGlobalSettings();
    }

    @GetMapping("/api/init")
    public InitResponse init() {
        return initResponse;
    }

    @GetMapping("/api/tag")
    public TagService.TagResponse getTags() {
        return tagService.getTags("");
    }

    @GetMapping("/api/calendar")
    public CalendarService.CalendarResponse getCalendar(@RequestParam int year) {
        return calendarService.getCalendar(year);
    }
}
