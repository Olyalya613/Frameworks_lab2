package ua.edu.catalog.persistence;

import ua.edu.catalog.core.port.CatalogRepositoryPort;
import ua.edu.catalog.core.port.CommentRepositoryPort;

public class PersistenceProvider {
    private final CatalogRepositoryPort catalog = new JdbcCatalogRepository();
    private final CommentRepositoryPort comments = new JdbcCommentRepository();

    public PersistenceProvider() {
        Db.init();
    }

    public CatalogRepositoryPort catalog() { return catalog; }
    public CommentRepositoryPort comments() { return comments; }
}
