package sumdu.edu.ua.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sumdu.edu.ua.spring.service.BookApplicationService;
import ua.edu.catalog.core.model.Page;
import ua.edu.catalog.core.model.Book;

@Controller
public class BookController {
    private final BookApplicationService books;
    private final sumdu.edu.ua.spring.service.MailService mailService;

    public BookController(BookApplicationService books, sumdu.edu.ua.spring.service.MailService mailService) {
        this.books = books;
        this.mailService = mailService;
    }

    @GetMapping({"/", "/books"})
    public String books(@RequestParam(name="q", required=false) String q,
                        @RequestParam(name="field", defaultValue="all") String field,
                        @RequestParam(name="page", defaultValue="0") int page,
                        @RequestParam(name="size", defaultValue="10") int size,
                        @RequestParam(name="sort", defaultValue="title") String sort,
                        Model model) {
        Page<Book> result = books.search(q, field, page, size, sort);
        model.addAttribute("books", result.items());
        model.addAttribute("page", result);
        model.addAttribute("q", q == null ? "" : q);
        return "books";
    }

    @GetMapping("/books/add")
    public String addForm(Model model) {
        model.addAttribute("book", new BookForm());
        return "book-form";
    }

    @PostMapping("/books/add")
    public String addBook(@ModelAttribute("book") BookForm form, Model model, RedirectAttributes redirect) {
        try {
            Book saved = books.create(form.getTitle(), form.getAuthor(), form.getYear());
            try {
                mailService.sendNewBookEmail(saved);
                redirect.addFlashAttribute("mailStatus", "Лист сформовано / відправлено (див. журнал сервера)");
            } catch (IllegalStateException mailError) {
                // The book was saved successfully: do not ask the user to submit again.
                redirect.addFlashAttribute("mailStatus", "Книгу збережено, але лист не надіслано. Перевірте журнал сервера.");
            }
            redirect.addFlashAttribute("success", true);
            return "redirect:/books";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "book-form";
        }
    }

    public static class BookForm {
        private String title = "";
        private String author = "";
        private int year = 2026;
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }
    }
}
