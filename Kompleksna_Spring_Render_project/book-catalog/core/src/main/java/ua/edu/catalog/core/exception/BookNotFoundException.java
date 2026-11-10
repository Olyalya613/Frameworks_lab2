package ua.edu.catalog.core.exception;

public class BookNotFoundException extends NotFoundException {
    public BookNotFoundException() {
        super("Книгу не знайдено");
    }
}
