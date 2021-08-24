package org.example.theblog.service;

import lombok.RequiredArgsConstructor;
import org.example.theblog.model.repository.PostRepository;
import org.example.theblog.model.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    public TagResponse getTags(String query) {

        Set<TagWeight> tags = tagRepository.findAll()
                .stream()
                .filter(tag -> Objects.isNull(query) || tag.getName().contains(query))
                .map(tag -> new TagWeight(tag.getName(), normalizeTagWeight(tag.getPosts().size())))
                .collect(Collectors.toSet());

        return new TagResponse(tags);
    }

    private double normalizeTagWeight(double postsWithTagCount) {
        double tagWeight = calculateTagWeight(postsWithTagCount);
        return tagWeight * calculateTagRatio();
    }

    private double calculateTagWeight(double postsWithTagCount) {
        long postsCount = postRepository.count();
        return postsWithTagCount / postsCount;
    }

    private double calculateTagRatio() {
        int maxPostsCountInTags = tagRepository.findMaxPostsCountInTags();
        return 1.0d / calculateTagWeight(maxPostsCountInTags);
    }

    public record TagWeight(String name, double weight) {
    }

    public record TagResponse(Set<TagWeight> tags) {
    }
}
