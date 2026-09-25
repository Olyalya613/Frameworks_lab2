package ua.edu.catalog.core.port;
import ua.edu.catalog.core.model.Comment;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CommentRepositoryPort {
    List<Comment> findByBookId(long bookId);
    Optional<Comment> findById(long id);
    Comment add(long bookId, String author, String text, Instant createdAt);
    boolean delete(long id);
}
