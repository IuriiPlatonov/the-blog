package org.example.theblog.service;

import lombok.AllArgsConstructor;
import org.example.theblog.model.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
public class CalendarService {

    PostRepository postRepository;

    public CalendarResponse getCalendar(int year) {
        Map<String, Long> dateByPostsCount = new HashMap<>();
        postRepository.getPostsCountByDate(year == 0 ? String.valueOf(LocalDate.now().getYear()) : String.valueOf(year))
                .forEach(o -> dateByPostsCount.put(o.getData(), o.getCount()));
        return new CalendarResponse(postRepository.getYearsSet(), dateByPostsCount);
    }

    public record CalendarResponse(Set<Integer> years, Map<String, Long> posts) {
    }
}
