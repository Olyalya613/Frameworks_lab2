package ua.edu.catalog.core.service;

import ua.edu.catalog.core.exception.NotFoundException;
import ua.edu.catalog.core.model.Book;
import ua.edu.catalog.core.model.Page;
import ua.edu.catalog.core.model.PageRequest;
import ua.edu.catalog.core.port.CatalogRepositoryPort;
import java.util.Set;

public class CatalogService {
    private static final Set<String> SORTS = Set.of("title", "author", "year");
    private static final Set<String> FIELDS = Set.of("all", "title", "author", "year");
    private final CatalogRepositoryPort repository;

    public CatalogService(CatalogRepositoryPort repository) {
        this.repository = repository;
    }

    public Page<Book> search(String q, String field, int page, int size, String sort) {
        String safeSort = (sort == null || sort.isBlank()) ? "title" : sort.toLowerCase();
        String safeField = (field == null || field.isBlank()) ? "all" : field.toLowerCase();
        if (!SORTS.contains(safeSort)) throw new IllegalArgumentException("sort must be title, author or year");
        if (!FIELDS.contains(safeField)) throw new IllegalArgumentException("field must be all, title, author or year");
        return repository.find(q == null ? "" : q.trim(), safeField, new PageRequest(page, size, safeSort));
    }

    public Book get(long id) {
        if (id <= 0) throw new IllegalArgumentException("Book id must be positive");
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Book not found"));
    }
}
