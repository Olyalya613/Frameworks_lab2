package ua.edu.catalog.core.exception;

public class CommentTooOldException extends RuntimeException {
    public CommentTooOldException() {
        super("Коментар створено більше ніж 24 години тому і не може бути видалений");
    }
}
