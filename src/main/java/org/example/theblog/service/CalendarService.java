package org.example.theblog.service;

import org.example.theblog.api.response.CalendarResponse;
import org.example.theblog.model.entity.Post;
import org.example.theblog.model.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CalendarService {

    DateFormat yearFormat = new SimpleDateFormat("yyyy");
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    int queryYear;

    PostRepository postRepository;

    public CalendarService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public CalendarResponse getCalendar(int year) {
        queryYear = year;
        List<Post> posts = StreamSupport.stream(postRepository.findAll().spliterator(), false).toList();
        return new CalendarResponse(getYears(posts), getPosts(posts));
    }

    private Set<String> getYears(List<Post> posts) {
        return posts.stream()
                .map(post -> yearFormat.format(post.getTime().getTime()))
                .collect(Collectors.toSet());
    }

    private Map<String, Integer> getPosts(List<Post> posts) {
        Map<String, Integer> postsMap = new HashMap<>();

        if (queryYear == 0) {
            queryYear = Integer.parseInt(yearFormat.format(System.currentTimeMillis()));
        }

        posts.stream()
                .map(this::getDate)
                .filter(this::isPostYearEqualsQueryYear)
                .forEach(date -> putPostsCountToMap(date, postsMap));

        return postsMap;
    }

    private boolean isPostYearEqualsQueryYear(Date date) {
        return yearFormat.format(date).equals(String.valueOf(queryYear));
    }

    private void putPostsCountToMap(Date date, Map<String, Integer> postsMap) {
        String formatDate = dateFormat.format(date);
        postsMap.put(formatDate, postsMap.getOrDefault(formatDate, 0) + 1);
    }

    private Date getDate(Post post) {
        Date date = null;
        try {
            date = dateFormat.parse(post.getTime().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
