package org.example.theblog.controllers;

import lombok.AllArgsConstructor;
import org.example.theblog.api.response.InitResponse;
import org.example.theblog.service.*;
import org.springframework.http.HttpStatus;
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
    private final InitResponse initResponse;
    private final TagService tagService;
    private final CalendarService calendarService;
    private final ModerationService moderationService;
    private final ProfileService profileService;
    private final ImageService imageService;

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

    @PostMapping("/api/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<CommentService.CommentResponse> postComment(
            @RequestBody CommentService.CommentRequest request, Principal principal) {
        CommentService.CommentResponse commentResponse = commentService.postComment(request, principal);
        return commentResponse.result()
                ? ResponseEntity.ok(commentResponse)
                : new ResponseEntity<>(commentResponse, HttpStatus.BAD_REQUEST);
    }


    @PostMapping("/api/image")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> postImage(
            @RequestParam("image") MultipartFile file) {
        ImageService.ImageResponse imageResponse = imageService.postImage(file);
        return imageResponse.result() == null
                ? ResponseEntity.ok(imageResponse.url())
                : new ResponseEntity<>(imageResponse, HttpStatus.BAD_REQUEST);
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
        return ResponseEntity.ok(profileService.editProfileWithPhoto(photo, removePhoto, name, email, password, principal));
    }
//


}
