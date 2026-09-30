package sumdu.edu.ua.web;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.catalog.core.exception.ConflictException;
import ua.edu.catalog.core.exception.NotFoundException;
import ua.edu.catalog.core.model.Comment;
import ua.edu.catalog.core.port.CatalogRepositoryPort;
import ua.edu.catalog.core.port.CommentRepositoryPort;
import ua.edu.catalog.core.service.CatalogService;
import ua.edu.catalog.core.service.CommentService;

import java.util.Map;

public class JavalinBookApp {
    private static final Logger log = LoggerFactory.getLogger(JavalinBookApp.class);

    public static void main(String[] args) {
        Services services = createServices();
        CatalogService catalog = services.catalog();
        CommentService comments = services.comments();

        Javalin app = Javalin.create().start(7070);

        // Middleware: виконується перед кожним HTTP-запитом.
        app.before(ctx -> log.info("HTTP {} {}", ctx.method(), ctx.path()));

        // GET /books?q=...&page=0&size=10&sort=title&field=all
        app.get("/books", ctx -> {
            String q = ctx.queryParam("q");
            String field = valueOr(ctx.queryParam("field"), "all");
            int page = intQuery(ctx, "page", 0);
            int size = intQuery(ctx, "size", 10);
            String sort = valueOr(ctx.queryParam("sort"), "title");
            ctx.json(catalog.search(q, field, page, size, sort));
        });

        // GET /books/{id}
        app.get("/books/{id}", ctx -> {
            long id = longPath(ctx, "id");
            ctx.json(Map.of(
                    "book", catalog.get(id),
                    "comments", comments.forBook(id)
            ));
        });

        // POST /books/{id}/comments, JSON: {"author":"...","text":"..."}
        app.post("/books/{id}/comments", ctx -> {
            long bookId = longPath(ctx, "id");
            CreateCommentRequest body = ctx.bodyAsClass(CreateCommentRequest.class);
            Comment created = comments.add(bookId, body.author(), body.text());
            log.info("Comment created: id={}, bookId={}, author={}",
                    created.id(), created.bookId(), created.author());
            ctx.status(201).json(created);
        });

        // DELETE /comments/{id}
        app.delete("/comments/{id}", ctx -> {
            long id = longPath(ctx, "id");
            Comment deleted = comments.delete(id);
            log.info("Comment deleted: id={}, bookId={}, author={}",
                    deleted.id(), deleted.bookId(), deleted.author());
            ctx.status(204);
        });

        // Централізована обробка винятків.
        app.exception(IllegalArgumentException.class, (e, ctx) -> {
            log.warn("400 {} {}: {}", ctx.method(), ctx.path(), e.getMessage());
            error(ctx, 400, "BAD_REQUEST", e.getMessage());
        });
        app.exception(NotFoundException.class, (e, ctx) -> {
            log.warn("404 {} {}: {}", ctx.method(), ctx.path(), e.getMessage());
            error(ctx, 404, "NOT_FOUND", e.getMessage());
        });
        app.exception(ConflictException.class, (e, ctx) -> {
            log.warn("409 {} {}: {}", ctx.method(), ctx.path(), e.getMessage());
            error(ctx, 409, "CONFLICT", e.getMessage());
        });
        app.exception(Exception.class, (e, ctx) -> {
            log.error("500 {} {}", ctx.method(), ctx.path(), e);
            error(ctx, 500, "INTERNAL_ERROR", "Internal server error");
        });

        log.info("Javalin Book API started: http://localhost:7070");
    }

    private static int intQuery(Context ctx, String name, int defaultValue) {
        String value = ctx.queryParam(name);
        if (value == null || value.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " must be an integer");
        }
    }

    private static long longPath(Context ctx, String name) {
        try {
            return Long.parseLong(ctx.pathParam(name));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " must be an integer");
        }
    }

    private static String valueOr(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static void error(Context ctx, int status, String code, String message) {
        ctx.status(status).json(Map.of("error", Map.of(
                "status", status,
                "code", code,
                "message", message
        )));
    }

    private static Services createServices() {
        try {
            // persistence залишається runtime-адаптером, core/persistence не змінюються.
            Class<?> type = Class.forName("ua.edu.catalog.persistence.PersistenceProvider");
            Object provider = type.getConstructor().newInstance();
            CatalogRepositoryPort catalogPort =
                    (CatalogRepositoryPort) type.getMethod("catalog").invoke(provider);
            CommentRepositoryPort commentPort =
                    (CommentRepositoryPort) type.getMethod("comments").invoke(provider);
            return new Services(new CatalogService(catalogPort), new CommentService(catalogPort, commentPort));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot initialize persistence adapter", e);
        }
    }

    public record CreateCommentRequest(String author, String text) {}
    private record Services(CatalogService catalog, CommentService comments) {}
}
