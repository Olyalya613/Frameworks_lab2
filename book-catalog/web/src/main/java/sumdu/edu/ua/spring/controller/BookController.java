package sumdu.edu.ua.spring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.spring.service.BookApplicationService;
import sumdu.edu.ua.spring.service.CommentApplicationService;
import ua.edu.catalog.core.model.Book;
import ua.edu.catalog.core.model.Comment;
import ua.edu.catalog.core.model.Page;
import java.util.List;

@RestController
@RequestMapping
public class BookController {
    private final BookApplicationService books;
    private final CommentApplicationService comments;

    public BookController(BookApplicationService books, CommentApplicationService comments) {
        this.books = books;
        this.comments = comments;
    }

    @GetMapping("/books")
    public Page<Book> books(@RequestParam(name="q", required=false) String q,
                            @RequestParam(name="field", defaultValue="all") String field,
                            @RequestParam(name="page", defaultValue="0") int page,
                            @RequestParam(name="size", defaultValue="10") int size,
                            @RequestParam(name="sort", defaultValue="title") String sort) {
        return books.search(q, field, page, size, sort);
    }

    @GetMapping("/books/{id}")
    public BookDetailsResponse book(@PathVariable(name="id") long id) {
        Book book = books.get(id);
        return new BookDetailsResponse(book, comments.forBook(id));
    }

    @GetMapping("/books/{bookId}/comments")
    public List<Comment> comments(@PathVariable(name="bookId") long bookId) {
        return comments.forBook(bookId);
    }

    @PostMapping("/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public Comment addComment(@RequestBody CreateCommentRequest request) {
        if (request.bookId() <= 0) throw new IllegalArgumentException("Book id must be positive");
        return comments.add(request.bookId(), request.author(), request.text());
    }

    @PostMapping("/books/{bookId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public Comment addCommentForBook(@PathVariable(name="bookId") long bookId,
                                     @RequestBody CommentRequest request) {
        return comments.add(bookId, request.author(), request.text());
    }

    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable(name="id") long id) { comments.delete(id); }

    public record BookDetailsResponse(Book book, List<Comment> comments) {}
    public record CreateCommentRequest(long bookId, String author, String text) {}
    public record CommentRequest(String author, String text) {}
}
