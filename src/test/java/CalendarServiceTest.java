import org.example.theblog.api.response.PostDateCountResponse;
import org.example.theblog.model.repository.PostRepository;
import org.example.theblog.service.CalendarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalendarServiceTest {

    PostRepository postRepository = Mockito.mock(PostRepository.class);
    CalendarService calendarService;

    {
        calendarService = new CalendarService(postRepository);
    }

    @Test
    @DisplayName("Get calendar, get count posts in day")
    public void getCalendarTest() {
        PostDateCountResponse postDateCountResponse =
                new PostDateCountResponse(String.valueOf(LocalDate.now().getYear()), 2);

        Mockito.when(postRepository.getPostsCountByDate(String.valueOf(LocalDate.now().getYear())))
                .thenReturn(List.of(postDateCountResponse));

        Long expected = Objects.requireNonNull(calendarService.getCalendar(0).getBody())
                .posts().get(String.valueOf(LocalDate.now().getYear()));

        assertEquals(expected, 2);
    }
}
