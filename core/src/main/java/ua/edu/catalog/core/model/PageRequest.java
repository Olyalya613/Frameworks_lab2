package ua.edu.catalog.core.model;

public record PageRequest(int page, int size, String sort) {
    public PageRequest {
        if (page < 0) throw new IllegalArgumentException("page must be >= 0");
        if (size < 1 || size > 100) throw new IllegalArgumentException("size must be between 1 and 100");
        if (sort == null || sort.isBlank()) sort = "title";
    }
}
