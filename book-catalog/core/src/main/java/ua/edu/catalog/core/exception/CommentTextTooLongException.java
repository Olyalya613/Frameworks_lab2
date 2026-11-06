package ua.edu.catalog.core.exception;

public class CommentTextTooLongException extends RuntimeException {
    public CommentTextTooLongException() {
        super("Коментар має містити не більше 1000 символів");
    }
}
