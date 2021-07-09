package org.example.theblog.service;

import lombok.AllArgsConstructor;
import org.example.theblog.model.entity.Post;
import org.example.theblog.model.entity.PostComment;
import org.example.theblog.model.entity.PostVote;
import org.example.theblog.model.entity.Tag;
import org.example.theblog.model.repository.OffsetLimitPageable;
import org.example.theblog.model.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;

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

    record PostOwner(int id, String name) {
    }

    record CommentOwner(int id, String name, String photo) {

    }

    record UserPost(int id, long timestamp, PostOwner user, String title, String announce,
                    long likeCount, long dislikeCount, int commentCount, int viewCount) {
    }

    record Comment(int id, long timestamp, String text, CommentOwner user) {
    }

    public record SmallViewPostResponse(long count, List<UserPost> posts) {
    }

    public record FullViewPostResponse(int id, long timestamp, byte active, PostOwner user, String title, String text,
                                       long likeCount, long dislikeCount, int viewCount, List<Comment> comments,
                                       List<String> tags) {
    }
}
