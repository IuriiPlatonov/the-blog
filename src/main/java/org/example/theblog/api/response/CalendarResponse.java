package org.example.theblog.api.response;

import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class CalendarResponse {
    private Set<String> years;
    private Map<String, Integer> posts;

    public CalendarResponse(Set<String> years, Map<String, Integer> posts) {
        this.years = years;
        this.posts = posts;
    }
}
