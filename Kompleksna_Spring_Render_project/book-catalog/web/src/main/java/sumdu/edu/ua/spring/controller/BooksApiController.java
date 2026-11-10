package sumdu.edu.ua.spring.controller;

import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.spring.service.BookApplicationService;
import ua.edu.catalog.core.model.Book;
import ua.edu.catalog.core.model.Page;

@RestController
@RequestMapping("/api/books")
public class BooksApiController {
    private final BookApplicationService books;

    public BooksApiController(BookApplicationService books) {
        this.books = books;
    }

    @GetMapping
    public Page<Book> list(@RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "size", defaultValue = "10") int size) {
        return books.search(null, "all", page, size, "title");
    }
}
