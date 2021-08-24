import org.example.theblog.exceptions.PostCommentException;
import org.example.theblog.model.entity.Post;
import org.example.theblog.model.entity.PostComment;
import org.example.theblog.model.entity.User;
import org.example.theblog.model.repository.PostCommentRepository;
import org.example.theblog.model.repository.PostRepository;
import org.example.theblog.model.repository.UserRepository;
import org.example.theblog.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

public class CommentServiceTest {

    PostCommentRepository postCommentRepository = Mockito.mock(PostCommentRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    PostRepository postRepository = Mockito.mock(PostRepository.class);
    Principal principal = Mockito.mock(Principal.class);
    PostComment postComment = Mockito.mock(PostComment.class);
    CommentService commentService;

    {
        commentService = new CommentService(postRepository, postCommentRepository, userRepository);
    }

    @Test
    @DisplayName("Post comment is successful")
    public void postCommentTest() {
        CommentService.CommentRequest commentRequest =
                new CommentService.CommentRequest("1", "2", "this good news");

        Mockito.when(postRepository.findById(Integer.parseInt(commentRequest.postId())))
                .thenReturn(Optional.of(new Post()));

        Mockito.when(postCommentRepository.getById(Integer.parseInt(commentRequest.parentId())))
                .thenReturn(new PostComment());

        Mockito.when(postCommentRepository.findById(Integer.parseInt(commentRequest.parentId())))
                .thenReturn(Optional.of(new PostComment()));

        Mockito.when(principal.getName()).thenReturn("testName");

        Mockito.when(userRepository.findUsersByEmail(principal.getName()))
                .thenReturn(new User());

        Mockito.when(postCommentRepository.save(any()))
                .thenReturn(postComment);

        int expected = commentService.postComment(commentRequest, principal);

        assertEquals(expected, 0);
    }

    @Test
    @DisplayName("Post comment, wrong postId")
    public void postCommentWithWrongPostIdTest() {
        CommentService.CommentRequest commentRequest =
                new CommentService.CommentRequest("1", " ", "this good news");

        Mockito.when(postRepository.findById(any()))
                .thenReturn(Optional.empty());

        Mockito.when(postCommentRepository.getById(Integer.parseInt(commentRequest.parentId())))
                .thenReturn(new PostComment());

        Mockito.when(postCommentRepository.findById(Integer.parseInt(commentRequest.parentId())))
                .thenReturn(Optional.of(new PostComment()));

        Mockito.when(principal.getName()).thenReturn("testName");

        Mockito.when(userRepository.findUsersByEmail(principal.getName()))
                .thenReturn(new User());

        Mockito.when(postCommentRepository.save(any()))
                .thenReturn(postComment);


        assertThrows(PostCommentException.class, () -> commentService.postComment(commentRequest, principal));
    }

    @Test
    @DisplayName("Post comment, wrong parentId")
    public void postCommentWithWrongParentIdTest() {
        CommentService.CommentRequest commentRequest =
                new CommentService.CommentRequest("1", "1", "this good news");

        Mockito.when(postRepository.findById(any()))
                .thenReturn(Optional.of(new Post()));

        Mockito.when(postCommentRepository.getById(any()))
                .thenReturn(new PostComment());

        Mockito.when(postCommentRepository.findById(any()))
                .thenReturn(Optional.empty());

        Mockito.when(principal.getName()).thenReturn("testName");

        Mockito.when(userRepository.findUsersByEmail(principal.getName()))
                .thenReturn(new User());

        Mockito.when(postCommentRepository.save(any()))
                .thenReturn(postComment);

        assertThrows(PostCommentException.class, () -> commentService.postComment(commentRequest, principal));
    }

    @Test
    @DisplayName("Post comment, empty text")
    public void postCommentWithEmptyTextTest() {
        CommentService.CommentRequest commentRequest =
                new CommentService.CommentRequest("1", "1", "&nbsp; ");

        Mockito.when(postRepository.findById(any()))
                .thenReturn(Optional.of(new Post()));

        Mockito.when(postCommentRepository.getById(any()))
                .thenReturn(new PostComment());

        Mockito.when(postCommentRepository.findById(any()))
                .thenReturn(Optional.of(new PostComment()));

        Mockito.when(principal.getName()).thenReturn("testName");

        Mockito.when(userRepository.findUsersByEmail(principal.getName()))
                .thenReturn(new User());

        Mockito.when(postCommentRepository.save(any()))
                .thenReturn(postComment);

        PostCommentException thrown = assertThrows(
                PostCommentException.class,
                () -> commentService.postComment(commentRequest, principal),
                "wrong text"
        );

        assertEquals(thrown.getCommentResponse().errors(),
                Map.of("text", "Текст комментария не задан или слишком короткий"));

    }


}
