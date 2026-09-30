package ua.edu.catalog.core.model;
import java.time.Instant;
public record Comment(long id, long bookId, String author, String text, Instant createdAt) {}
