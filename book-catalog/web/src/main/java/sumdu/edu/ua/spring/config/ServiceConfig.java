package sumdu.edu.ua.spring.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.edu.catalog.core.port.CatalogRepositoryPort;
import ua.edu.catalog.core.port.CommentRepositoryPort;
import ua.edu.catalog.core.service.CommentService;
import ua.edu.catalog.core.service.CatalogService;

@Configuration
public class ServiceConfig {
    @Bean
    CatalogService catalogService(CatalogRepositoryPort catalog) {
        return new CatalogService(catalog);
    }

    @Bean
    CommentService commentService(CatalogRepositoryPort catalog, CommentRepositoryPort comments, Clock clock) {
        return new CommentService(catalog, comments, clock);
    }
}
