package org.example.theblog.controllers;

import lombok.AllArgsConstructor;
import org.example.theblog.api.response.InitResponse;
import org.example.theblog.service.CalendarService;
import org.example.theblog.service.CommentService;
import org.example.theblog.service.SettingsService;
import org.example.theblog.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@AllArgsConstructor
public class ApiGeneralController {

    private final CommentService commentService;
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

    @PostMapping("/api/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<CommentService.CommentResponse> postComment(
            @RequestBody CommentService.CommentRequest request, Principal principal) {
        CommentService.CommentResponse commentResponse = commentService.postComment(request, principal);
        return commentResponse.result()
                ? ResponseEntity.ok(commentResponse)
                : new ResponseEntity<>(commentResponse, HttpStatus.BAD_REQUEST);
    }
}
