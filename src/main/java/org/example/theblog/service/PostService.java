package org.example.theblog.service;

import org.example.theblog.api.response.DTO.UserName;
import org.example.theblog.api.response.DTO.UserPost;
import org.example.theblog.api.response.PostResponse;
import org.example.theblog.model.entity.ModerationStatus;
import org.example.theblog.model.entity.Post;
import org.example.theblog.model.entity.PostVote;
import org.example.theblog.model.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class PostService {

    PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostResponse getPosts(int offset, int limit, String mode) {
        List<Post> posts = StreamSupport.stream(postRepository.findAll().spliterator(), false).toList();

        return createPostResponse(posts, offset, limit, mode);
    }

    public PostResponse searchPosts(int offset, int limit, String query) {
        List<Post> posts = StreamSupport.stream(postRepository.findAll().spliterator(), false)
                .filter(post -> post.getTitle().contains(query) || post.getText().contains(query))
                .toList();

        return query.trim().equals("")
                ? getPosts(offset, limit, "recent")
                : createPostResponse(posts, offset, limit, "recent");
    }

    private PostResponse createPostResponse(List<Post> posts, int offset, int limit, String mode) {
        PostResponse postResponse = new PostResponse();
        postResponse.setCount(posts.size());
        switch (mode) {
            case "recent" -> postResponse.setPosts(getPosts(posts, offset, limit,
                    (o1, o2) -> Long.compare(o2.getTime().getTime(), o1.getTime().getTime())));

            case "popular" -> postResponse.setPosts(getPosts(posts, offset, limit,
                    (o1, o2) -> Integer.compare(o2.getPostComments().size(), o1.getPostComments().size())));

            case "best" -> postResponse.setPosts(getPosts(posts, offset, limit,
                    (o1, o2) -> Long.compare(getLikeCount(o2.getPostVotes()), getLikeCount(o1.getPostVotes()))));

            case "early" -> postResponse.setPosts(getPosts(posts, offset, limit,
                    Comparator.comparing(Post::getTime)));
        }
        return postResponse;
    }

    private List<UserPost> getPosts(List<Post> list, int offset, int limit, Comparator<Post> comparator) {
        return list.stream()
                .sorted(comparator)
                .skip(offset)
                .limit(limit)
                .filter(post -> post.getIsActive() == 1)
                .filter(post -> post.getModerationStatus().equals(ModerationStatus.ACCEPTED))
                .filter(post -> post.getTime().getTime() <= System.currentTimeMillis())
                .map(post -> new UserPost(post.getId(),
                        post.getTime().getTime() / 1000,
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
