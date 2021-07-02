package org.example.theblog.service;

import org.example.theblog.api.response.DTO.TagWeight;
import org.example.theblog.api.response.TagResponse;
import org.example.theblog.model.entity.Tag;
import org.example.theblog.model.repository.PostRepository;
import org.example.theblog.model.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TagService {

    TagRepository tagRepository;
    PostRepository postRepository;

    public TagService(TagRepository tagRepository, PostRepository postRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
    }

    public TagResponse getTags(String query) {
        TagResponse tagResponse = new TagResponse();
        Set<TagWeight> tagsWeight = new HashSet<>();

        List<Tag> tags = StreamSupport.stream(tagRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        long postsCount = StreamSupport.stream(postRepository.findAll().spliterator(), false).count();
        int postsWithTagMaxCount = tags.stream()
                .max(Comparator.comparingInt(o -> o.getPosts().size()))
                .orElse(new Tag())
                .getPosts().size();

        double k = calculateTagRatio(postsWithTagMaxCount, postsCount);

        if (!query.isEmpty()) {
            List<Tag> queryTags = tags.stream().filter(tag -> tag.getName().contains(query)).toList();
            queryTags.forEach(tag -> addTagWeight(tagsWeight, tag, postsCount, k));
        }

        if (query.isEmpty()) {
            tags.forEach(tag -> addTagWeight(tagsWeight, tag, postsCount, k));
        }

        tagResponse.setTags(tagsWeight);

        return tagResponse;
    }

    private void addTagWeight(Set<TagWeight> tagsWeight, Tag tag, double postsCount, double ratio) {
        int postsWithTagCount = tag.getPosts().size();
        double normalizedTagWeight = normalizeTagWeight(postsWithTagCount, postsCount, ratio);
        String tagName = tag.getName();
        tagsWeight.add(new TagWeight(tagName, normalizedTagWeight));
    }

    private double normalizeTagWeight(double postsWithTagCount, double postsCount, double ratio) {
        double tagWeight = calculateTagWeight(postsWithTagCount, postsCount);
        return tagWeight * ratio;
    }

    private double calculateTagWeight(double postsWithTagCount, double postsCount) {
        return postsWithTagCount / postsCount;
    }

    private double calculateTagRatio(double postsWithTagMaxCount, double postsCount) {
        return 1.0d / calculateTagWeight(postsWithTagMaxCount, postsCount);
    }
}
