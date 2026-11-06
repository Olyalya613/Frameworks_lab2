package ua.edu.catalog.core.service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ua.edu.catalog.core.exception.*;
import ua.edu.catalog.core.model.Book;
import ua.edu.catalog.core.model.Comment;
import ua.edu.catalog.core.port.CatalogRepositoryPort;
import ua.edu.catalog.core.port.CommentRepositoryPort;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CommentServiceTest {
    private static final Instant NOW = Instant.parse("2026-10-01T12:00:00Z");
    private static final Instant RECENT = NOW.minus(1, ChronoUnit.HOURS);
    private final CatalogRepositoryPort catalog = mock(CatalogRepositoryPort.class);
    private final CommentRepositoryPort repo = mock(CommentRepositoryPort.class);
    private final CommentService service = new CommentService(catalog, repo, Clock.fixed(NOW, ZoneOffset.UTC));

    @ParameterizedTest
    @CsvSource({"0,2", "1,-1"})
    void rejectsInvalidIds(long bookId, long commentId) {
        assertThrows(InvalidCommentDeleteException.class, () -> service.delete(bookId, commentId, RECENT));
        verifyNoInteractions(repo, catalog);
    }

    @Test
    void rejectsMissingTimestamp() {
        assertThrows(InvalidCommentDeleteException.class, () -> service.delete(1, 2, null));
        verifyNoInteractions(repo, catalog);
    }

    @Test
    void rejectsFutureTimestamp() {
        assertThrows(InvalidCommentDeleteException.class, () -> service.delete(1, 2, NOW.plusSeconds(1)));
        verifyNoInteractions(repo, catalog);
    }

    @Test
    void rejectsTimestampOlderThan24Hours() {
        Instant old = NOW.minus(25, ChronoUnit.HOURS);
        assertThrows(CommentTooOldException.class, () -> service.delete(1, 2, old));
        verifyNoInteractions(repo, catalog);
    }

    @Test
    void deletesCommentAtThe24HourBoundary() {
        Instant boundary = NOW.minus(24, ChronoUnit.HOURS);
        givenBook();
        when(repo.findById(2)).thenReturn(Optional.of(new Comment(2, 1, "admin", "Text", boundary)));
        when(repo.delete(1, 2)).thenReturn(true);

        service.delete(1, 2, boundary);

        verify(repo).delete(1, 2);
    }

    @Test
    void rejectsForgedRecentDateForOldStoredComment() {
        givenBook();
        when(repo.findById(2)).thenReturn(Optional.of(new Comment(2, 1, "admin", "Text",
                NOW.minus(25, ChronoUnit.HOURS))));

        assertThrows(CommentTooOldException.class, () -> service.delete(1, 2, RECENT));
        verify(repo, never()).delete(anyLong(), anyLong());
    }

    @Test
    void rejectsCommentFromAnotherBook() {
        givenBook();
        when(repo.findById(2)).thenReturn(Optional.of(new Comment(2, 3, "admin", "Text", RECENT)));

        assertThrows(NotFoundException.class, () -> service.delete(1, 2, RECENT));
        verify(repo, never()).delete(anyLong(), anyLong());
    }

    @Test
    void reportsMissingBookWithoutCallingCommentRepository() {
        when(catalog.findById(1)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> service.delete(1, 2, RECENT));
        verifyNoInteractions(repo);
    }

    @Test
    void rejectsLongCommentText() {
        givenBook();

        assertThrows(CommentTextTooLongException.class, () -> service.add(1, "admin", "x".repeat(1001)));
        verifyNoInteractions(repo);
    }

    @Test
    void legacyDeleteRejectsAt24Hours() {
        Instant boundary = NOW.minus(24, ChronoUnit.HOURS);
        when(repo.findById(2)).thenReturn(Optional.of(new Comment(2, 1, "admin", "Text", boundary)));

        assertThrows(ConflictException.class, () -> service.delete(2));
        verify(repo, never()).delete(2);
    }

    private void givenBook() {
        when(catalog.findById(1)).thenReturn(Optional.of(new Book(1, "Book", "Author", 2026)));
    }
}
