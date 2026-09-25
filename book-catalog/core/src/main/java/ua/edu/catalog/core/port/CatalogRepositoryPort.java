package ua.edu.catalog.core.port;
import ua.edu.catalog.core.model.Book;
import ua.edu.catalog.core.model.Page;
import ua.edu.catalog.core.model.PageRequest;
import java.util.Optional;

public interface CatalogRepositoryPort {
    Page<Book> find(String q, String field, PageRequest request);
    Optional<Book> findById(long id);
}
