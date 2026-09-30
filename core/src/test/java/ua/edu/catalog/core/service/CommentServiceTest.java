package ua.edu.catalog.core.service;

import org.junit.jupiter.api.Test;
import ua.edu.catalog.core.exception.ConflictException;
import ua.edu.catalog.core.model.Book;
import ua.edu.catalog.core.model.Comment;
import ua.edu.catalog.core.port.CatalogRepositoryPort;
import ua.edu.catalog.core.port.CommentRepositoryPort;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CommentServiceTest {
    @Test
    void cannotDeleteAfter24Hours() {
        Instant now = Instant.parse("2026-09-23T10:00:00Z");
        CatalogRepositoryPort books = new CatalogRepositoryPort() {
            public ua.edu.catalog.core.model.Page<Book> find(String q,String f,ua.edu.catalog.core.model.PageRequest p){throw new UnsupportedOperationException();}
            public Optional<Book> findById(long id){return Optional.of(new Book(id,"Book","Author",2026));}
        };
        Comment old = new Comment(1,1,"Olha","Text",now.minus(Duration.ofHours(24)));
        CommentRepositoryPort comments = new CommentRepositoryPort() {
            public List<Comment> findByBookId(long id){return List.of(old);}
            public Optional<Comment> findById(long id){return Optional.of(old);}
            public Comment add(long b,String a,String t,Instant c){throw new UnsupportedOperationException();}
            public boolean delete(long id){return true;}
        };
        CommentService service = new CommentService(books, comments, Clock.fixed(now, ZoneOffset.UTC));
        assertThrows(ConflictException.class, () -> service.delete(1));
    }
}
