package sumdu.edu.ua.spring.controller;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.edu.catalog.core.exception.InvalidCommentDeleteException;
import ua.edu.catalog.core.service.CommentService;

@RestController
public class CommentsController {
    private final CommentService comments;

    public CommentsController(CommentService comments) {
        this.comments = comments;
    }

    @PostMapping("/comments/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam("bookId") long bookId,
                       @RequestParam("commentId") long commentId,
                       @RequestParam(name = "createdAt", required = false) String createdAt) {
        Instant timestamp = null;
        if (createdAt != null && !createdAt.isBlank()) {
            try {
                timestamp = Instant.parse(createdAt);
            } catch (DateTimeParseException e) {
                throw new InvalidCommentDeleteException("Дата створення має бути у форматі ISO-8601");
            }
        }
        comments.delete(bookId, commentId, timestamp);
    }
}
