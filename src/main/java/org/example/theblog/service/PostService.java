package org.example.theblog.service;

import org.example.theblog.api.response.DTO.UserName;
import org.example.theblog.api.response.DTO.UserPost;
import org.example.theblog.api.response.PostResponse;
import org.example.theblog.model.entity.ModerationStatus;
import org.example.theblog.model.entity.Post;
import org.example.theblog.model.entity.PostVote;
import org.example.theblog.model.repository.OffsetLimitPageable;
import org.example.theblog.model.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostResponse getPosts(int offset, int limit, String mode) {
        Pageable pageable = new OffsetLimitPageable(offset, limit);

        return switch (mode) {
            case "popular" ->  createPostResponse(postRepository.findAllPageOrderByCommentDesc(pageable));
            case "best" ->  createPostResponse(postRepository.findAllPageOrderByVotesDesc(pageable));
            case "early" ->  createPostResponse(postRepository.findAllPageOrderByTime(pageable));
            default ->  createPostResponse(postRepository.findAllPageOrderByTimeDesc(pageable));
        };
    }

    /* public PostResponse searchPosts(int offset, int limit, String query) {
         List<Post> posts = StreamSupport.stream(postRepository.findAll().spliterator(), false)
                 .filter(post -> post.getTitle().contains(query) || post.getText().contains(query))
                 .toList();

         return query.trim().equals("")
                 ? getPosts(offset, limit, "recent")
                 : createPostResponse(posts, offset, limit, "recent");
     }
 */
    private PostResponse createPostResponse(Page<Post> posts) {
        PostResponse postResponse = new PostResponse();
        postResponse.setCount(posts.getTotalElements());
        postResponse.setPosts(getPosts(posts));
        return postResponse;
    }


    private List<UserPost> getPosts(Page<Post> list) {
        return list.stream()
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
