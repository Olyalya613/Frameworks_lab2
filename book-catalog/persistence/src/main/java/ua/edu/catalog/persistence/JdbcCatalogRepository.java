package ua.edu.catalog.persistence;

import ua.edu.catalog.core.model.Book;
import ua.edu.catalog.core.model.Page;
import ua.edu.catalog.core.model.PageRequest;
import ua.edu.catalog.core.port.CatalogRepositoryPort;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCatalogRepository implements CatalogRepositoryPort {
    @Override
    public Page<Book> find(String q, String field, PageRequest request) {
        String term = q == null ? "" : q.trim();
        String orderBy = switch (request.sort()) {
            case "author" -> "author";
            case "year" -> "pub_year";
            default -> "title";
        };

        Search search = buildSearch(term, field);
        String countSql = "SELECT COUNT(*) FROM books WHERE " + search.where;
        String dataSql = "SELECT id,title,author,pub_year FROM books WHERE " + search.where
                + " ORDER BY " + orderBy + " LIMIT ? OFFSET ?";

        try (Connection c = Db.get()) {
            long total;
            try (PreparedStatement ps = c.prepareStatement(countSql)) {
                search.bind(ps, 1);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    total = rs.getLong(1);
                }
            }

            List<Book> items = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(dataSql)) {
                int next = search.bind(ps, 1);
                ps.setInt(next++, request.size());
                ps.setInt(next, request.page() * request.size());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        items.add(new Book(rs.getLong("id"), rs.getString("title"),
                                rs.getString("author"), rs.getInt("pub_year")));
                    }
                }
            }

            int totalPages = total == 0 ? 0 : (int) Math.ceil(total / (double) request.size());
            return new Page<>(items, request.page(), request.size(), total, totalPages);
        } catch (SQLException e) {
            throw new IllegalStateException("Database error while reading books", e);
        }
    }

    @Override
    public Optional<Book> findById(long id) {
        String sql = "SELECT id,title,author,pub_year FROM books WHERE id=?";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new Book(rs.getLong("id"), rs.getString("title"),
                        rs.getString("author"), rs.getInt("pub_year")));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Database error while reading a book", e);
        }
    }

    private Search buildSearch(String term, String field) {
        if (term.isBlank()) return new Search("1=1", null, false);

        if ("year".equals(field)) {
            try {
                return new Search("pub_year=?", Integer.parseInt(term), true);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("For year search enter a whole number");
            }
        }

        String like = "%" + term.toLowerCase() + "%";
        return switch (field) {
            case "title" -> new Search("LOWER(title) LIKE ?", like, false);
            case "author" -> new Search("LOWER(author) LIKE ?", like, false);
            default -> new Search(
                    "(LOWER(title) LIKE ? OR LOWER(author) LIKE ? OR CAST(pub_year AS VARCHAR) LIKE ?)",
                    like, false);
        };
    }

    private static final class Search {
        private final String where;
        private final Object value;
        private final boolean integer;
        private Search(String where, Object value, boolean integer) {
            this.where = where; this.value = value; this.integer = integer;
        }
        private int bind(PreparedStatement ps, int start) throws SQLException {
            if (value == null) return start;
            if (where.contains(" OR ")) {
                ps.setString(start++, value.toString());
                ps.setString(start++, value.toString());
                ps.setString(start++, value.toString());
                return start;
            }
            if (integer) ps.setInt(start++, (Integer) value);
            else ps.setString(start++, value.toString());
            return start;
        }
    }
}
