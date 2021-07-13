package org.example.theblog.service;

import lombok.RequiredArgsConstructor;
import org.example.theblog.model.entity.Tag;
import org.example.theblog.model.repository.PostRepository;
import org.example.theblog.model.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private long postsCount;
    private Map<String, Double> tags;
    private int maxPostsCountInTags;


    public TagResponse getTags(String query) {
        maxPostsCountInTags = tagRepository.findMaxPostsCountInTags();
        postsCount = postRepository.count();
        tags = new HashMap<>();
        tagRepository.findAllTags().stream()
                .filter(tag -> query.isEmpty() || tag.getName().contains(query))
                .forEach(this::addTagWeight);

        return new TagResponse(tags.entrySet().stream()
                .map((key) -> new TagWeight(key.getKey(), key.getValue()))
                .collect(Collectors.toSet()));
    }

    private void addTagWeight(Tag tag) {
        int postsWithTagCount = tag.getPosts().size();
        double normalizedTagWeight = normalizeTagWeight(postsWithTagCount);
        String tagName = tag.getName();
        tags.put(tagName, normalizedTagWeight);
    }

    private double normalizeTagWeight(double postsWithTagCount) {
        double tagWeight = calculateTagWeight(postsWithTagCount);
        return tagWeight * calculateTagRatio();
    }

    private double calculateTagWeight(double postsWithTagCount) {
        return postsWithTagCount / postsCount;
    }

    private double calculateTagRatio() {
        return 1.0d / calculateTagWeight(maxPostsCountInTags);
    }

    record TagWeight(String name, double weight) {
    }

    public record TagResponse(Set<TagWeight> tags) {
    }
}
