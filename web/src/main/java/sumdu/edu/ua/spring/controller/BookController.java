package sumdu.edu.ua.spring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.spring.service.BookApplicationService;
import sumdu.edu.ua.spring.service.CommentApplicationService;
import ua.edu.catalog.core.model.Book;
import ua.edu.catalog.core.model.Comment;

import java.util.List;

@RestController
public class BookController {

    private final BookApplicationService books;
    private final CommentApplicationService comments;

    public BookController(
            BookApplicationService books,
            CommentApplicationService comments) {

        this.books = books;
        this.comments = comments;
    }

    // Отримання списку книг + пошук + пагінація + сортування
    @GetMapping("/books")
    public Object getBooks(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "field", defaultValue = "title") String field,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "title") String sort) {

        return books.search(q, field, page, size, sort);
    }

    // Отримання однієї книги
    @GetMapping("/books/{id}")
    public Book book(
            @PathVariable(name = "id") long id) {

        return books.get(id);
    }

    // Отримання коментарів до книги
    @GetMapping("/books/{bookId}/comments")
    public List<Comment> comments(
            @PathVariable(name = "bookId") long bookId) {

        return comments.forBook(bookId);
    }

    // Додавання коментаря
    @PostMapping("/books/{bookId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public Comment addComment(
            @PathVariable(name = "bookId") long bookId,
            @RequestBody CommentRequest request) {

        return comments.add(
                bookId,
                request.author(),
                request.text()
        );
    }

    // Видалення коментаря
    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable(name = "id") long id) {

        comments.delete(id);
    }

    public record CommentRequest(
            String author,
            String text
    ) {}
}