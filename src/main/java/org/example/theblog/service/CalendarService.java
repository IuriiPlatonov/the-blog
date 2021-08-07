package org.example.theblog.service;

import lombok.AllArgsConstructor;
import org.example.theblog.model.repository.PostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
public class CalendarService {

    PostRepository postRepository;

    public ResponseEntity<CalendarResponse> getCalendar(int year) {
        Map<String, Long> postsMap = new HashMap<>();
        postRepository.getYearsListList(year == 0 ? String.valueOf(LocalDate.now().getYear()) : String.valueOf(year))
                .forEach(o -> postsMap.put(o.getData(), o.getCount()));
        return ResponseEntity.ok(new CalendarResponse(postRepository.getYearsList(), postsMap));
    }

    public record CalendarResponse(Set<Integer> years, Map<String, Long> posts) {
    }
}
