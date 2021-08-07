package org.example.theblog.controllers;

import lombok.AllArgsConstructor;
import org.example.theblog.service.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@AllArgsConstructor
public class ApiGeneralController {

    private final CommentService commentService;
    private final SettingsService settingsService;
    private final InitService initService;
    private final TagService tagService;
    private final CalendarService calendarService;
    private final ModerationService moderationService;
    private final ProfileService profileService;
    private final ImageService imageService;
    private final StatisticsService statisticsService;

    @GetMapping("/api/settings")
    public ResponseEntity<SettingsService.SettingsResponse> getSettings() {
        return settingsService.getGlobalSettings();
    }

    @PutMapping("/api/settings")
    public void setSettings(@RequestBody SettingsService.SettingsRequest request) {
        settingsService.setGlobalSettings(request);
    }

    @GetMapping("/api/init")
    public ResponseEntity<InitService.InitResponse> init() {
        return initService.init();
    }

    @GetMapping("/api/tag")
    public ResponseEntity<TagService.TagResponse> getTags(@RequestParam(required = false) String query) {
        return tagService.getTags(query);
    }

    @GetMapping("/api/calendar")
    public ResponseEntity<CalendarService.CalendarResponse> getCalendar(@RequestParam int year) {
        return calendarService.getCalendar(year);
    }

    @PostMapping("/api/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> postComment(@RequestBody CommentService.CommentRequest request, Principal principal) {
        return commentService.postComment(request, principal);
    }

    @PostMapping("/api/image")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> postImage(@RequestParam("image") MultipartFile file) {
        return imageService.postImage(file);
    }

    @PostMapping("/api/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ModerationService.ModerationResponse> postModeration(
            @RequestBody ModerationService.ModerationRequest request, Principal principal) {
        return moderationService.postModeration(request, principal);
    }

    @PostMapping(path = "/api/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ProfileService.ProfileResponse> editProfileWithoutPhoto(
            @RequestBody ProfileService.ProfileRequest request, Principal principal) {
        return profileService.editProfileWithoutPhoto(request, principal);
    }

    @PostMapping(path = "/api/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ProfileService.ProfileResponse> editProfileWithPhoto(
            @RequestParam MultipartFile photo,
            @RequestParam int removePhoto,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam(required = false) String password,
            Principal principal) {
        return profileService.editProfileWithPhoto(photo, removePhoto, name, email, password, principal);
    }

    @GetMapping("/api/statistics/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<StatisticsService.StatisticsResponse> getMyStatistics(Principal principal) {
        return statisticsService.getMyStatistics(principal);
    }

    @GetMapping("/api/statistics/all")
    public ResponseEntity<StatisticsService.StatisticsResponse> getAllStatistics(Principal principal) {
        return statisticsService.getAllStatistics(principal);
    }
}
