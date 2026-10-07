package sumdu.edu.ua.spring.service;

import org.springframework.stereotype.Service;
import ua.edu.catalog.core.model.Book;
import ua.edu.catalog.core.model.Page;
import ua.edu.catalog.core.port.CatalogRepositoryPort;
import ua.edu.catalog.core.service.CatalogService;

@Service
public class BookApplicationService {
    private final CatalogService catalogService;

    // Constructor injection: Spring passes CatalogRepositoryPort automatically.
    public BookApplicationService(CatalogRepositoryPort repository) {
        this.catalogService = new CatalogService(repository);
    }

    public Page<Book> search(String q, String field, int page, int size, String sort) {
        return catalogService.search(q, field, page, size, sort);
    }

    public Book get(long id) { return catalogService.get(id); }
}
