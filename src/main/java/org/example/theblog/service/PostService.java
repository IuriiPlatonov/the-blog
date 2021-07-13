package org.example.theblog.service;

import lombok.AllArgsConstructor;
import org.example.theblog.model.entity.*;
import org.example.theblog.model.repository.OffsetLimitPageable;
import org.example.theblog.model.repository.PostRepository;
import org.example.theblog.model.repository.TagRepository;
import org.example.theblog.model.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    public SmallViewPostResponse getPosts(int offset, int limit, String mode) {
        Pageable pageable = new OffsetLimitPageable(offset, limit);

        return switch (mode) {
            case "popular" -> createSmallViewPostResponse(postRepository.findAllPageOrderByCommentDesc(pageable));
            case "best" -> createSmallViewPostResponse(postRepository.findAllPageOrderByVotesDesc(pageable));
            case "early" -> createSmallViewPostResponse(postRepository.findAllPageOrderByTime(pageable));
            default -> createSmallViewPostResponse(postRepository.findAllPageOrderByTimeDesc(pageable));
        };
    }

    public SmallViewPostResponse searchPosts(int offset, int limit, String query) {
        Pageable pageable = new OffsetLimitPageable(offset, limit);
        Page<Post> posts = postRepository.searchPageByQuery(query, pageable);
        return query.isBlank()
                ? getPosts(offset, limit, "recent")
                : createSmallViewPostResponse(posts);
    }

    public SmallViewPostResponse getPostsByDate(int offset, int limit, String date) {
        Pageable pageable = new OffsetLimitPageable(offset, limit);
        return createSmallViewPostResponse(postRepository.findAllPostsByDate(date, pageable));
    }

    public SmallViewPostResponse getPostsByTag(int offset, int limit, String tag) {
        Pageable pageable = new OffsetLimitPageable(offset, limit);
        return createSmallViewPostResponse(postRepository.findAllPostsByTag(tag, pageable));
    }

    public FullViewPostResponse getPostsByID(int id) {
        Post post = postRepository.getPostById(id);
        return post == null ? null : createFullViewPostResponse(post);
    }

    public SmallViewPostResponse getMyPosts(int offset, int limit, String status, Principal principal) {
        Pageable pageable = new OffsetLimitPageable(offset, limit);

        String email = principal.getName();
        return switch (status) {
            case "inactive" -> createSmallViewPostResponse(postRepository.findMyInactivePosts(email, pageable));
            case "pending" -> createSmallViewPostResponse(postRepository.findMyPendingPosts(email, pageable));
            case "declined" -> createSmallViewPostResponse(postRepository.findMyDeclinedPosts(email, pageable));
            default -> createSmallViewPostResponse(postRepository.findMyPublishedPosts(email, pageable));
        };
    }

    private FullViewPostResponse createFullViewPostResponse(Post post) {
        viewCountIncrement(post);
        return new FullViewPostResponse(
                post.getId(),
                post.getTime().toEpochSecond(ZoneOffset.UTC),
                post.getIsActive(),
                new PostOwner(post.getUser().getId(),
                        post.getUser().getName()),
                post.getTitle(),
                post.getText(),
                getLikeCount(post.getPostVotes()),
                getDislikeCount(post.getPostVotes()),
                post.getViewCount(),
                getComments(post.getPostComments()),
                getTags(post.getTags()));
    }

    private void viewCountIncrement(Post post) {
        post.setViewCount(post.getViewCount() + 1);
        postRepository.flush();
    }

    private SmallViewPostResponse createSmallViewPostResponse(Page<Post> posts) {
        return new SmallViewPostResponse(posts.getTotalElements(), getPosts(posts));
    }

    private List<UserPost> getPosts(Page<Post> list) {
        return list.stream()
                .map(post -> new UserPost(post.getId(),
                        post.getTime().toEpochSecond(ZoneOffset.UTC),
                        new PostOwner(post.getUser().getId(), post.getUser().getName()),
                        post.getTitle(),
                        getAnnounce(post.getText()),
                        getLikeCount(post.getPostVotes()),
                        getDislikeCount(post.getPostVotes()),
                        post.getPostComments().size(),
                        post.getViewCount()
                ))
                .toList();

    }

    private String getAnnounce(String text) {
        return text.length() > 150 ?
                text.substring(0, 149).replaceAll("<\\D+>", "").concat("...") :
                text.replaceAll("<\\D+>", "");
    }

    private List<String> getTags(List<Tag> tagsList) {
        return tagsList.stream()
                .map(Tag::getName)
                .toList();
    }

    private List<Comment> getComments(List<PostComment> commentsList) {
        return commentsList.stream()
                .map(com -> new Comment(com.getId(),
                        com.getTime().toEpochSecond(ZoneOffset.UTC),
                        com.getText(),
                        new CommentOwner(com.getUser().getId(),
                                com.getUser().getName(),
                                com.getUser().getPhoto())))
                .toList();
    }

    private long getLikeCount(List<PostVote> postVoteList) {
        return postVoteList.stream()
                .filter(a -> a.getValue() > 0)
                .count();
    }

    private long getDislikeCount(List<PostVote> postVoteList) {
        return postVoteList.stream()
                .filter(a -> a.getValue() < 0)
                .count();
    }

    public writePostErrorResponse writePost(PostRequest request, Principal principal) {
        Map<String, String> errors = new HashMap<>();

        if (request.title.isBlank()) {
            errors.put("title", "Заголовок не установлен");
        }

        if (request.title.length() < 4) {
            errors.put("title", "Заголовок слишком короткий");
        }

        if (request.text.isBlank()) {
            errors.put("text", "Текст публикации не установлен");
        }

        if (request.text.length() < 51) {
            errors.put("text", "Текст публикации слишком короткий");
        }

        if (errors.size() == 0) {
            Post post = new Post();
            post.setIsActive(request.active());
            post.setModerationStatus(ModerationStatus.NEW);
            post.setTime(request.timestamp() <= LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                    ? LocalDateTime.now()
                    : LocalDateTime.ofEpochSecond(request.timestamp(), 0, ZoneOffset.UTC));
            post.setTitle(request.title());
            post.setText(request.text());
            post.setUser(userRepository.findUsersByEmail(principal.getName()));
            post.setTags(addTagsToPost(request.tags()));
            postRepository.save(post);
            System.out.println(request.timestamp() < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) );
        }
        return new writePostErrorResponse(errors.size() == 0, errors);
    }

    private List<Tag> addTagsToPost(List<String> tagsNames) {
        return tagsNames.stream()
                .map(name -> tagRepository.findTagByName(name).orElse(new Tag(name)))
                .peek(tag -> {
                    if (tag.getId() == 0) tagRepository.save(tag);
                })
                .toList();
    }

    record PostOwner(int id, String name) {
    }

    record CommentOwner(int id, String name, String photo) {

    }

    record UserPost(int id, long timestamp, PostOwner user, String title, String announce,
                    long likeCount, long dislikeCount, int commentCount, int viewCount) {
    }

    record Comment(int id, long timestamp, String text, CommentOwner user) {
    }

    public record writePostErrorResponse(boolean result, Map<String, String> errors) {
    }

    public record SmallViewPostResponse(long count, List<UserPost> posts) {
    }

    public record FullViewPostResponse(int id, long timestamp, byte active, PostOwner user, String title, String text,
                                       long likeCount, long dislikeCount, int viewCount, List<Comment> comments,
                                       List<String> tags) {
    }

    public record PostRequest(long timestamp, byte active, String title, List<String> tags, String text) {
    }
}
