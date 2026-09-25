package ua.edu.catalog.persistence;

import ua.edu.catalog.core.model.Comment;
import ua.edu.catalog.core.port.CommentRepositoryPort;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCommentRepository implements CommentRepositoryPort {
    @Override
    public List<Comment> findByBookId(long bookId) {
        String sql = "SELECT id,book_id,author,text,created_at FROM comments WHERE book_id=? ORDER BY created_at DESC";
        List<Comment> result = new ArrayList<>();
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException("Database error while reading comments", e);
        }
    }

    @Override
    public Optional<Comment> findById(long id) {
        String sql = "SELECT id,book_id,author,text,created_at FROM comments WHERE id=?";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Database error while reading comment", e);
        }
    }

    @Override
    public Comment add(long bookId, String author, String text, Instant createdAt) {
        String sql = "INSERT INTO comments(book_id,author,text,created_at) VALUES(?,?,?,?)";
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, bookId);
            ps.setString(2, author);
            ps.setString(3, text);
            ps.setObject(4, createdAt);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("No generated id");
                return new Comment(keys.getLong(1), bookId, author, text, createdAt);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Database error while creating comment", e);
        }
    }

    @Override
    public boolean delete(long id) {
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement("DELETE FROM comments WHERE id=?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Database error while deleting comment", e);
        }
    }

    private Comment map(ResultSet rs) throws SQLException {
        return new Comment(rs.getLong("id"), rs.getLong("book_id"), rs.getString("author"),
                rs.getString("text"), rs.getObject("created_at", java.time.OffsetDateTime.class).toInstant());
    }
}
