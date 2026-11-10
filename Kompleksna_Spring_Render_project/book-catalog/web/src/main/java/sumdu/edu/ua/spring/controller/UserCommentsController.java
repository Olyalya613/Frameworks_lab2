
package sumdu.edu.ua.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sumdu.edu.ua.spring.service.*;

@Controller
public class UserCommentsController {

    private final UserApplicationService users;
    private final CommentApplicationService comments;
    private final BookApplicationService books;

    public UserCommentsController(
            UserApplicationService users,
            CommentApplicationService comments,
            BookApplicationService books) {

        this.users = users;
        this.comments = comments;
        this.books = books;
    }

    @GetMapping("/comments/error")
    public String commentError() {
        return "error/comment";
    }

    // Сторінка коментарів конкретного користувача
    @GetMapping("/users/{id}/comments")
    public String userComments(
            @PathVariable("id") long id,
            Model model) {

        var user = users.get(id);

        model.addAttribute("username", user.username());
        model.addAttribute("comments", comments.forUser(id));
        model.addAttribute("users", users.all());

        return "user-comments";
    }

    // Картка книги та коментарі до неї
    @GetMapping("/books/{id}")
    public String bookDetails(
            @PathVariable("id") long id,
            Model model) {

        model.addAttribute("book", books.get(id));
        model.addAttribute("comments", comments.forBook(id));
        model.addAttribute("users", users.all());

        return "book-details";
    }

    @PostMapping("/books/{bookId}/comments/{commentId}/edit")
    public String editComment(@PathVariable("bookId") long bookId,
                              @PathVariable("commentId") long commentId,
                              @RequestParam("text") String text,
                              RedirectAttributes redirect) {
        try {
            comments.updateText(bookId, commentId, text);
            redirect.addFlashAttribute("commentSuccess", "Коментар оновлено");
        } catch (IllegalArgumentException exception) {
            redirect.addFlashAttribute("commentError", exception.getMessage());
        }
        return "redirect:/books/" + bookId;
    }

    // Додавання нового коментаря
    @PostMapping("/books/{id}/comments")
    public String addComment(
            @PathVariable("id") long id,
            java.security.Principal principal,
            @RequestParam("text") String text,
            RedirectAttributes redirect) {

        try {
            comments.add(id, principal.getName(), text);

            redirect.addFlashAttribute(
                    "commentSuccess",
                    "Коментар додано"
            );

        } catch (IllegalArgumentException e) {

            redirect.addFlashAttribute(
                    "commentError",
                    e.getMessage()
            );
        }

        return "redirect:/books/" + id;
    }
}
