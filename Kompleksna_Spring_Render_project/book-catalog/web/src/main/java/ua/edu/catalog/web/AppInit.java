package ua.edu.catalog.web;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import ua.edu.catalog.core.port.CatalogRepositoryPort;
import ua.edu.catalog.core.port.CommentRepositoryPort;
import ua.edu.catalog.core.service.CatalogService;
import ua.edu.catalog.core.service.CommentService;

public class AppInit implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            // Composition root without compile-time web -> persistence dependency.
            Class<?> type = Class.forName("ua.edu.catalog.persistence.PersistenceProvider");
            Object provider = type.getConstructor().newInstance();
            CatalogRepositoryPort catalogPort =
                    (CatalogRepositoryPort) type.getMethod("catalog").invoke(provider);
            CommentRepositoryPort commentPort =
                    (CommentRepositoryPort) type.getMethod("comments").invoke(provider);

            event.getServletContext().setAttribute("catalogService", new CatalogService(catalogPort));
            event.getServletContext().setAttribute("commentService", new CommentService(catalogPort, commentPort));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot initialize persistence adapter", e);
        }
    }
}
