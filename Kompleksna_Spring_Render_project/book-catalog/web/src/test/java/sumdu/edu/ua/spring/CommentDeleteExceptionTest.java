package sumdu.edu.ua.spring;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ua.edu.catalog.core.exception.BookNotFoundException;
import ua.edu.catalog.core.exception.CommentTooOldException;
import ua.edu.catalog.core.exception.InvalidCommentDeleteException;
import ua.edu.catalog.core.service.CommentService;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SpringBookApplication.class, properties = {
        "spring.datasource.url=jdbc:h2:mem:lab10mockmvc;DB_CLOSE_DELAY=-1",
        "spring.jpa.show-sql=false"
})
@AutoConfigureMockMvc
class CommentDeleteExceptionTest {
    private static final Instant CREATED = Instant.parse("2024-01-01T00:00:00Z");

    @Autowired MockMvc mockMvc;
    @MockitoBean CommentService commentService;

    @Test
    void returns400ForOldComment() throws Exception {
        var exception = new CommentTooOldException();
        doThrow(exception).when(commentService).delete(1, 2, CREATED);

        mockMvc.perform(deleteRequest("1", "ADMIN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "Коментар створено більше ніж 24 години тому і не може бути видалений"));
    }

    @Test
    void returns400ForInvalidIds() throws Exception {
        doThrow(new InvalidCommentDeleteException("Некоректний ідентифікатор"))
                .when(commentService).delete(0, 2, CREATED);

        mockMvc.perform(deleteRequest("0", "ADMIN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Некоректний ідентифікатор"));
    }

    @Test
    void returns404ForMissingBook() throws Exception {
        doThrow(new BookNotFoundException()).when(commentService).delete(1, 2, CREATED);

        mockMvc.perform(deleteRequest("1", "ADMIN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Книгу не знайдено"));
    }

    @Test
    void returns204OnSuccess() throws Exception {
        mockMvc.perform(deleteRequest("1", "ADMIN")).andExpect(status().isNoContent());

        verify(commentService).delete(1, 2, CREATED);
    }

    @Test
    void userCannotDeleteComments() throws Exception {
        mockMvc.perform(deleteRequest("1", "USER")).andExpect(status().isForbidden());

        verifyNoInteractions(commentService);
    }

    private MockHttpServletRequestBuilder deleteRequest(String bookId, String role) {
        return post("/comments/delete").with(user("tester").roles(role)).with(csrf())
                .param("bookId", bookId)
                .param("commentId", "2")
                .param("createdAt", CREATED.toString());
    }
}
