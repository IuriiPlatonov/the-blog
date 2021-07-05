package org.example.theblog.service;

import org.example.theblog.model.entity.Tag;
import org.example.theblog.model.repository.PostRepository;
import org.example.theblog.model.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TagService {
    record TagWeight(String name, double weight) {
    }

    public record TagResponse(Set<TagWeight> tags) {
    }

    private final TagRepository tagRepository;
    private final int postsCount;
    private final Set<TagWeight> tagsWeight;
    private final int maxPostsCountInTags;

    public TagService(TagRepository tagRepository, PostRepository postRepository) {
        this.tagRepository = tagRepository;
        this.postsCount = postRepository.findAll().size();
        this.tagsWeight = new HashSet<>();
        this.maxPostsCountInTags = tagRepository.findMaxPostsCountInTags();
    }

    public TagResponse getTags(String query) {
        List<Tag> tags = tagRepository.findAllTags();

        if (!query.isEmpty()) {
            tags.stream()
                    .filter(tag -> tag.getName().contains(query))
                    .forEach(this::addTagWeight);
        }

        if (query.isEmpty()) {
            tags.forEach(this::addTagWeight);
        }

        return new TagResponse(tagsWeight);
    }

    private void addTagWeight(Tag tag) {
        int postsWithTagCount = tag.getPosts().size();
        double normalizedTagWeight = normalizeTagWeight(postsWithTagCount);
        String tagName = tag.getName();
        tagsWeight.add(new TagWeight(tagName, normalizedTagWeight));
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
}
