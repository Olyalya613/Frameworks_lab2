package sumdu.edu.ua.spring.repository;

import org.springframework.stereotype.Repository;
import ua.edu.catalog.core.model.Book;
import ua.edu.catalog.core.model.Page;
import ua.edu.catalog.core.model.PageRequest;
import ua.edu.catalog.core.port.CatalogRepositoryPort;
import ua.edu.catalog.persistence.JdbcCatalogRepository;
import ua.edu.catalog.persistence.Db;

import java.util.Optional;

@Repository
public class CatalogRepositoryBean implements CatalogRepositoryPort {
    private final JdbcCatalogRepository delegate = new JdbcCatalogRepository();

    public CatalogRepositoryBean() {
        // Initializes the H2 schema and demo data from the previous laboratory work.
        Db.init();
    }

    @Override
    public Page<Book> find(String q, String field, PageRequest request) {
        return delegate.find(q, field, request);
    }

    @Override
    public Optional<Book> findById(long id) {
        return delegate.findById(id);
    }
}
