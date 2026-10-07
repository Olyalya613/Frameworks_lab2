package sumdu.edu.ua.spring.service;

import org.springframework.stereotype.Service;
import ua.edu.catalog.core.model.Comment;
import ua.edu.catalog.core.port.CatalogRepositoryPort;
import ua.edu.catalog.core.port.CommentRepositoryPort;
import ua.edu.catalog.core.service.CommentService;

import java.time.Clock;
import java.util.List;

@Service
public class CommentApplicationService {
    private final CommentService commentService;

    public CommentApplicationService(CatalogRepositoryPort catalog,
                                     CommentRepositoryPort comments,
                                     Clock clock) {
        this.commentService = new CommentService(catalog, comments, clock);
    }

    public List<Comment> forBook(long bookId) { return commentService.forBook(bookId); }
    public Comment add(long bookId, String author, String text) { return commentService.add(bookId, author, text); }
    public Comment delete(long id) { return commentService.delete(id); }
}
