import org.example.theblog.model.entity.Post;
import org.example.theblog.model.entity.Tag;
import org.example.theblog.model.repository.PostRepository;
import org.example.theblog.model.repository.TagRepository;
import org.example.theblog.service.TagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TagServiceTest {
    TagRepository tagRepository = Mockito.mock(TagRepository.class);
    PostRepository postRepository = Mockito.mock(PostRepository.class);

    TagService tagService = new TagService(tagRepository, postRepository);

    @Test
    @DisplayName("Get tags is successful")
    public void getTagsTest() {
        Tag tagOne = new Tag();
        tagOne.setName("testOne");
        tagOne.setPosts(List.of(new Post(), new Post()));

        Tag tagTwo = new Tag();
        tagTwo.setName("testTwo");
        tagTwo.setPosts(List.of(new Post(), new Post(), new Post()));
        List<Tag> tags = List.of(tagOne, tagTwo);

        Mockito.when(tagRepository.findAll()).thenReturn(tags);
        Mockito.when(postRepository.count()).thenReturn(20L);
        Mockito.when(tagRepository.findMaxPostsCountInTags()).thenReturn(3);

        assertEquals(tagService.getTags(null).tags(),
                Set.of(new TagService.TagWeight("testTwo", 1.0),
                        new TagService.TagWeight("testOne", 0.6666666666666667)));
    }


    @Test
    @DisplayName("Get tags with QUERY successful")
    public void getTagsWithQueryTest() {
        Tag tagOne = new Tag();
        tagOne.setName("testOne");
        tagOne.setPosts(List.of(new Post(), new Post()));

        Tag tagTwo = new Tag();
        tagTwo.setName("testTwo");
        tagTwo.setPosts(List.of(new Post(), new Post(), new Post()));
        List<Tag> tags = List.of(tagOne, tagTwo);

        Mockito.when(tagRepository.findAll()).thenReturn(tags);
        Mockito.when(postRepository.count()).thenReturn(20L);
        Mockito.when(tagRepository.findMaxPostsCountInTags()).thenReturn(3);

        assertEquals(tagService.getTags("Two").tags(),
                Set.of(new TagService.TagWeight("testTwo", 1.0)));

    }
}
