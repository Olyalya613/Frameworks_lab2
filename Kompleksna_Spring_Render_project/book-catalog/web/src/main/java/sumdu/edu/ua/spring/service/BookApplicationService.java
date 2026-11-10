package sumdu.edu.ua.spring.service;

import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import ua.edu.catalog.core.model.Book;
import ua.edu.catalog.core.model.Page;
import ua.edu.catalog.core.service.CatalogService;

@Service
public class BookApplicationService {
    private final CatalogService catalogService;

    public BookApplicationService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public Page<Book> search(String q, String field, int page, int size, String sort) {
        return catalogService.search(q, field, page, size, sort);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Book create(String title, String author, int year) { return catalogService.create(title, author, year); }


    @PreAuthorize("hasRole('ADMIN')")
    public Book update(long id, String title, String author, int year) {
        return catalogService.update(id, title, author, year);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(long id) {
        catalogService.delete(id);
    }

    public Book get(long id) { return catalogService.get(id); }
}
