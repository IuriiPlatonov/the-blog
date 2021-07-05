package org.example.theblog.service;

import lombok.AllArgsConstructor;
import org.example.theblog.model.entity.Post;
import org.example.theblog.model.entity.PostVote;
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
    record UserName(int id, String name) {
    }

    record UserPost(int id, long timestamp, UserName user, String title, String announce,
                    long likeCount, long dislikeCount, int commentCount, int viewCount) {
    }

    public record PostResponse(long count, List<UserPost> posts) {
    }

    PostRepository postRepository;

    public PostResponse getPosts(int offset, int limit, String mode) {
        Pageable pageable = new OffsetLimitPageable(offset, limit);

        return switch (mode) {
            case "popular" -> createPostResponse(postRepository.findAllPageOrderByCommentDesc(pageable));
            case "best" -> createPostResponse(postRepository.findAllPageOrderByVotesDesc(pageable));
            case "early" -> createPostResponse(postRepository.findAllPageOrderByTime(pageable));
            default -> createPostResponse(postRepository.findAllPageOrderByTimeDesc(pageable));
        };
    }

    public PostResponse searchPosts(int offset, int limit, String query) {
        Pageable pageable = new OffsetLimitPageable(offset, limit);
        Page<Post> posts = postRepository.searchPageByQuery(query, pageable);
        return query.isBlank()
                ? getPosts(offset, limit, "recent")
                : createPostResponse(posts);
    }

    private PostResponse createPostResponse(Page<Post> posts) {
        return new PostResponse(posts.getTotalElements(), getPosts(posts));
    }

    private List<UserPost> getPosts(Page<Post> list) {
        return list.stream()
                .map(post -> new UserPost(post.getId(),
                        post.getTime().toEpochSecond(ZoneOffset.UTC),
                        new UserName(post.getUser().getId(), post.getUser().getName()),
                        post.getTitle(),
                        getAnnounce(post.getText()),
                        getLikeCount(post.getPostVotes()),
                        getDislikeCount(post.getPostVotes()),
                        post.getPostComments().size(),
                        1
                ))
                .toList();

    }

    private String getAnnounce(String text) {
        return text.length() > 150 ?
                text.substring(0, 149).replaceAll("<a>", "") + "..." :
                text.replaceAll("<a>", "");
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
}
