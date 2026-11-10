package ua.edu.catalog.core.service;

import ua.edu.catalog.core.exception.ConflictException;
import ua.edu.catalog.core.exception.BookNotFoundException;
import ua.edu.catalog.core.exception.CommentTextTooLongException;
import ua.edu.catalog.core.exception.CommentTooOldException;
import ua.edu.catalog.core.exception.InvalidCommentDeleteException;
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
        if (a.isEmpty()) throw new IllegalArgumentException("Author is required");
        if (a.length() > 64) throw new IllegalArgumentException("Author must contain at most 64 characters");
        return comments.add(bookId, a, validateText(text), clock.instant());
    }

    public void updateText(long bookId, long commentId, String text) {
        ensureBook(bookId);
        if (commentId <= 0) throw new IllegalArgumentException("Некоректний ідентифікатор коментаря");
        if (!comments.updateText(bookId, commentId, validateText(text))) {
            throw new NotFoundException("Коментар цієї книги не знайдено");
        }
    }

    private String validateText(String text) {
        String value = text == null ? "" : text.trim();
        if (value.isEmpty()) throw new IllegalArgumentException("Уведіть текст коментаря");
        if (value.length() > 1000) throw new CommentTextTooLongException();
        return value;
    }

    public void delete(long bookId, long commentId, Instant createdAt) {
        if (bookId <= 0 || commentId <= 0) {
            throw new InvalidCommentDeleteException("Ідентифікатори книги й коментаря мають бути додатними");
        }
        if (createdAt == null) {
            throw new InvalidCommentDeleteException("Укажіть дату створення коментаря");
        }
        Instant now = clock.instant();
        if (createdAt.isAfter(now)) {
            throw new InvalidCommentDeleteException("Дата створення коментаря не може бути в майбутньому");
        }
        Instant cutoff = now.minus(DELETE_WINDOW);
        if (createdAt.isBefore(cutoff)) {
            throw new CommentTooOldException();
        }

        ensureBook(bookId);
        Comment comment = comments.findById(commentId)
                .filter(candidate -> candidate.bookId() == bookId)
                .orElseThrow(() -> new NotFoundException("Коментар цієї книги не знайдено"));
        // The database timestamp is authoritative even if the client supplies a recent date.
        if (comment.createdAt().isBefore(cutoff)) {
            throw new CommentTooOldException();
        }
        if (!comments.delete(bookId, commentId)) {
            throw new NotFoundException("Коментар цієї книги не знайдено");
        }
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
        if (catalog.findById(bookId).isEmpty()) throw new BookNotFoundException();
    }
}
