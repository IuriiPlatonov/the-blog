package org.example.theblog.controllers;

import lombok.AllArgsConstructor;
import org.example.theblog.exceptions.PostCommentException;
import org.example.theblog.exceptions.UserUnauthorizedException;
import org.example.theblog.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
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
        return ResponseEntity.ok(settingsService.getGlobalSettings());
    }

    @PutMapping("/api/settings")
    public void setSettings(@RequestBody SettingsService.SettingsRequest request) {
        settingsService.setGlobalSettings(request);
    }

    @GetMapping("/api/init")
    public ResponseEntity<InitService.InitResponse> init() {
        return ResponseEntity.ok(initService.init());
    }

    @GetMapping("/api/tag")
    public ResponseEntity<TagService.TagResponse> getTags(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(tagService.getTags(query));
    }

    @GetMapping("/api/calendar")
    public ResponseEntity<CalendarService.CalendarResponse> getCalendar(@RequestParam int year) {
        return ResponseEntity.ok(calendarService.getCalendar(year));
    }

    @PostMapping("/api/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> postComment(@RequestBody CommentService.CommentRequest request, Principal principal) {
            return ResponseEntity.ok(commentService.postComment(request, principal));
    }

    @PostMapping("/api/image")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<String> postImage(@RequestParam("image") MultipartFile file) throws MaxUploadSizeExceededException {
        return ResponseEntity.ok(imageService.postImage(file));
    }


    @PostMapping("/api/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ModerationService.ModerationResponse> postModeration(
            @RequestBody ModerationService.ModerationRequest request, Principal principal) {
        return ResponseEntity.ok(moderationService.postModeration(request, principal));
    }

    @PostMapping(path = "/api/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ProfileService.ProfileResponse> editProfileWithoutPhoto(
            @RequestBody ProfileService.ProfileRequest request, Principal principal) {
        return ResponseEntity.ok(profileService.editProfileWithoutPhoto(request, principal));
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
        return ResponseEntity.ok(
                profileService.editProfileWithPhoto(photo, removePhoto, name, email, password, principal));
    }

    @GetMapping("/api/statistics/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<StatisticsService.StatisticsResponse> getMyStatistics(Principal principal) {
        return ResponseEntity.ok(statisticsService.getMyStatistics(principal));
    }

    @GetMapping("/api/statistics/all")
    public ResponseEntity<StatisticsService.StatisticsResponse> getAllStatistics(Principal principal) {
            return ResponseEntity.ok(statisticsService.getAllStatistics(principal));
    }
}
