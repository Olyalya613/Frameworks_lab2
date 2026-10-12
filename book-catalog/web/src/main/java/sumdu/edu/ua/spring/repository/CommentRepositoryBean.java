package sumdu.edu.ua.spring.repository;

import org.springframework.stereotype.Repository;
import ua.edu.catalog.core.model.Comment;
import ua.edu.catalog.core.port.CommentRepositoryPort;
import ua.edu.catalog.persistence.JdbcCommentRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepositoryBean implements CommentRepositoryPort {
    private final JdbcCommentRepository delegate = new JdbcCommentRepository();

    @Override
    public List<Comment> findByBookId(long bookId) { return delegate.findByBookId(bookId); }

    @Override
    public Optional<Comment> findById(long id) { return delegate.findById(id); }

    @Override
    public Comment add(long bookId, String author, String text, Instant createdAt) {
        return delegate.add(bookId, author, text, createdAt);
    }

    @Override
    public boolean delete(long id) { return delegate.delete(id); }
}
