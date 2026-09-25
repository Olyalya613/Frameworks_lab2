package ua.edu.catalog.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.catalog.core.exception.ConflictException;
import ua.edu.catalog.core.exception.NotFoundException;
import ua.edu.catalog.core.model.Comment;
import ua.edu.catalog.core.service.CatalogService;
import ua.edu.catalog.core.service.CommentService;

import java.io.IOException;
import java.util.Map;

public class ApiServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(ApiServlet.class);
    private final ObjectMapper json = new ObjectMapper().registerModule(new JavaTimeModule());
    private CatalogService catalog;
    private CommentService comments;

    @Override
    public void init() throws ServletException {
        catalog = (CatalogService) getServletContext().getAttribute("catalogService");
        comments = (CommentService) getServletContext().getAttribute("commentService");
        if (catalog == null || comments == null) throw new ServletException("Application services are not initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handle(req, resp, () -> {
            String path = path(req);

            if (path.equals("/books")) {
                int page = intParam(req, "page", 0);
                int size = intParam(req, "size", 10);
                String sort = param(req, "sort", "title");
                String field = param(req, "field", "all"); // optional UI extension
                write(resp, 200, catalog.search(req.getParameter("q"), field, page, size, sort));
                return;
            }

            if (path.matches("/books/\\d+")) {
                long id = lastId(path);
                write(resp, 200, Map.of(
                        "book", catalog.get(id),
                        "comments", comments.forBook(id)
                ));
                return;
            }

            throw new NotFoundException("Endpoint not found");
        });
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handle(req, resp, () -> {
            String path = path(req);
            if (path.matches("/books/\\d+/comments")) {
                String[] parts = path.split("/");
                long bookId = Long.parseLong(parts[2]);
                Comment created = comments.add(bookId, req.getParameter("author"), req.getParameter("text"));
                log.info("Comment created: id={}, bookId={}, author={}",
                        created.id(), created.bookId(), created.author());
                write(resp, 201, created);
                return;
            }
            throw new NotFoundException("Endpoint not found");
        });
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handle(req, resp, () -> {
            String path = path(req);
            if (path.matches("/comments/\\d+")) {
                Comment deleted = comments.delete(lastId(path));
                log.info("Comment deleted: id={}, bookId={}, author={}",
                        deleted.id(), deleted.bookId(), deleted.author());
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                return;
            }
            throw new NotFoundException("Endpoint not found");
        });
    }

    private void handle(HttpServletRequest req, HttpServletResponse resp, Action action) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        try {
            action.run();
        } catch (IllegalArgumentException e) {
            log.warn("400 {} {}: {}", req.getMethod(), path(req), e.getMessage());
            error(resp, 400, "BAD_REQUEST", e.getMessage());
        } catch (NotFoundException e) {
            log.warn("404 {} {}: {}", req.getMethod(), path(req), e.getMessage());
            error(resp, 404, "NOT_FOUND", e.getMessage());
        } catch (ConflictException e) {
            log.warn("409 {} {}: {}", req.getMethod(), path(req), e.getMessage());
            error(resp, 409, "CONFLICT", e.getMessage());
        } catch (Exception e) {
            log.error("500 {} {}", req.getMethod(), path(req), e);
            error(resp, 500, "INTERNAL_ERROR", "Internal server error");
        }
    }

    private void write(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        json.writeValue(resp.getWriter(), body);
    }

    private void error(HttpServletResponse resp, int status, String code, String message) throws IOException {
        write(resp, status, Map.of("error", Map.of(
                "status", status,
                "code", code,
                "message", message
        )));
    }

    private String path(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String ctx = req.getContextPath();
        String p = uri.substring(ctx.length());
        if (p.equals("/api")) p = "/";
        else if (p.startsWith("/api/")) p = p.substring(4);
        return p.length() > 1 && p.endsWith("/") ? p.substring(0, p.length() - 1) : p;
    }

    private long lastId(String path) {
        String[] p = path.split("/");
        return Long.parseLong(p[p.length - 1]);
    }

    private int intParam(HttpServletRequest req, String name, int def) {
        String value = req.getParameter(name);
        if (value == null || value.isBlank()) return def;
        try { return Integer.parseInt(value); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(name + " must be an integer"); }
    }

    private String param(HttpServletRequest req, String name, String def) {
        String value = req.getParameter(name);
        return value == null || value.isBlank() ? def : value;
    }

    @FunctionalInterface
    private interface Action { void run() throws Exception; }
}
