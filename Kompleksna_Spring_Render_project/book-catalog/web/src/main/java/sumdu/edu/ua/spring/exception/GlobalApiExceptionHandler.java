package sumdu.edu.ua.spring.exception;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import sumdu.edu.ua.spring.controller.BooksApiController;
import sumdu.edu.ua.spring.controller.CommentsController;
import ua.edu.catalog.core.exception.*;

@RestControllerAdvice(assignableTypes = {BooksApiController.class, CommentsController.class})
public class GlobalApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalApiExceptionHandler.class);

    @ExceptionHandler({InvalidCommentDeleteException.class, CommentTooOldException.class,
            CommentTextTooLongException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> badRequest(RuntimeException exception) {
        log.warn("REST 400: {}", exception.getMessage());
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> badParameter(Exception exception) {
        log.warn("REST 400: {}", exception.getClass().getSimpleName());
        return Map.of("error", "Перевірте наявність і формат параметрів запиту");
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Map<String, String> notFound(NotFoundException exception) {
        log.info("REST 404: {}", exception.getMessage());
        return Map.of("error", exception.getMessage());
    }
}
