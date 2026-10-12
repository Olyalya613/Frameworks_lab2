
package ua.edu.catalog.persistence.adapter;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.catalog.core.model.*;
import ua.edu.catalog.core.port.CatalogRepositoryPort;
import ua.edu.catalog.persistence.entity.BookEntity;
import ua.edu.catalog.persistence.repository.BookRepository;

import java.util.*;

@Repository
public class JpaCatalogAdapter implements CatalogRepositoryPort {

    private final BookRepository repository;

    public JpaCatalogAdapter(BookRepository repository) {
        this.repository = repository;
    }

    private static Book toDomain(BookEntity entity) {
        return new Book(
                entity.getId(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getYear()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> find(String q, String field, PageRequest request) {

        String needle = q == null
                ? ""
                : q.strip().toLowerCase(Locale.ROOT);

        List<Book> filtered = repository.findAll()
                .stream()
                .map(JpaCatalogAdapter::toDomain)
                .filter(book -> {
                    if (needle.isBlank()) {
                        return true;
                    }

                    return switch (field == null ? "all" : field) {
                        case "title" ->
                                book.title().toLowerCase(Locale.ROOT)
                                        .contains(needle);

                        case "author" ->
                                book.author().toLowerCase(Locale.ROOT)
                                        .contains(needle);

                        case "year" ->
                                String.valueOf(book.year())
                                        .contains(needle);

                        default ->
                                book.title().toLowerCase(Locale.ROOT)
                                        .contains(needle)
                                || book.author().toLowerCase(Locale.ROOT)
                                        .contains(needle)
                                || String.valueOf(book.year())
                                        .contains(needle);
                    };
                })
                .sorted(Comparator.comparing(
                        Book::title,
                        String.CASE_INSENSITIVE_ORDER
                ))
                .toList();

        int total = filtered.size();

        int start = (int) Math.min(
                (long) request.page() * request.size(),
                total
        );

        int end = Math.min(
                start + request.size(),
                total
        );

        int totalPages = (int) Math.ceil(
                total / (double) request.size()
        );

        return new Page<>(
                filtered.subList(start, end),
                request.page(),
                request.size(),
                total,
                totalPages
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findById(long id) {
        return repository.findById(id)
                .map(JpaCatalogAdapter::toDomain);
    }

    @Override
    @Transactional
    public Book save(Book book) {

        BookEntity entity = book.id() > 0
                ? repository.findById(book.id()).orElseThrow()
                : new BookEntity(
                        book.title(),
                        book.author(),
                        book.year()
                );

        entity.setTitle(book.title());
        entity.setAuthor(book.author());
        entity.setYear(book.year());

        return toDomain(repository.save(entity));
    }
}
