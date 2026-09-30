package ua.edu.catalog.core.service;

import ua.edu.catalog.core.exception.ConflictException;
import ua.edu.catalog.core.exception.NotFoundException;
import ua.edu.catalog.core.model.Comment;
import ua.edu.catalog.core.port.CatalogRepositoryPort;
import ua.edu.catalog.core.port.CommentRepositoryPort;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class CommentService {
    private static final Duration DELETE_WINDOW = Duration.ofHours(24);
    private final CatalogRepositoryPort catalog;
    private final CommentRepositoryPort comments;
    private final Clock clock;

    public CommentService(CatalogRepositoryPort catalog, CommentRepositoryPort comments) {
        this(catalog, comments, Clock.systemUTC());
    }

    public CommentService(CatalogRepositoryPort catalog, CommentRepositoryPort comments, Clock clock) {
        this.catalog = catalog;
        this.comments = comments;
        this.clock = clock;
    }

    public List<Comment> forBook(long bookId) {
        ensureBook(bookId);
        return comments.findByBookId(bookId);
    }

    public Comment add(long bookId, String author, String text) {
        ensureBook(bookId);
        String a = author == null ? "" : author.trim();
        String t = text == null ? "" : text.trim();
        if (a.isEmpty()) throw new IllegalArgumentException("Author is required");
        if (t.isEmpty()) throw new IllegalArgumentException("Comment text is required");
        if (a.length() > 64) throw new IllegalArgumentException("Author must contain at most 64 characters");
        if (t.length() > 1000) throw new IllegalArgumentException("Comment must contain at most 1000 characters");
        return comments.add(bookId, a, t, clock.instant());
    }

    public Comment delete(long commentId) {
        if (commentId <= 0) throw new IllegalArgumentException("Comment id must be positive");
        Comment comment = comments.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        Instant deadline = comment.createdAt().plus(DELETE_WINDOW);
        if (!clock.instant().isBefore(deadline)) {
            throw new ConflictException("Comment can be deleted only within 24 hours after creation");
        }
        if (!comments.delete(commentId)) throw new NotFoundException("Comment not found");
        return comment;
    }

    private void ensureBook(long bookId) {
        if (bookId <= 0) throw new IllegalArgumentException("Book id must be positive");
        if (catalog.findById(bookId).isEmpty()) throw new NotFoundException("Book not found");
    }
}
