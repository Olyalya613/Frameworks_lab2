package ua.edu.catalog.core.model;
import java.time.Instant;
public record UserComment(long id,long bookId,String bookTitle,long userId,String username,String text,Instant createdAt) {}
