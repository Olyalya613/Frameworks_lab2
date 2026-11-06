package sumdu.edu.ua.spring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import sumdu.edu.ua.spring.controller.BookController;
import sumdu.edu.ua.spring.controller.UserCommentsController;
import ua.edu.catalog.core.exception.NotFoundException;

@ControllerAdvice(assignableTypes = {BookController.class, UserCommentsController.class})
public class WebExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String notFound() {
        return "error/404";
    }
}
