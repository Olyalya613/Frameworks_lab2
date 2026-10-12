package ua.edu.catalog.persistence.adapter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.catalog.core.model.*;
import ua.edu.catalog.core.port.CommentRepositoryPort;
import ua.edu.catalog.persistence.entity.*;
import ua.edu.catalog.persistence.repository.*;
import java.time.Instant;
import java.util.*;
@Repository public class JpaCommentAdapter implements CommentRepositoryPort {
 private final CommentRepository comments; private final BookRepository books; private final UserRepository users;
 public JpaCommentAdapter(CommentRepository comments,BookRepository books,UserRepository users){this.comments=comments;this.books=books;this.users=users;}
 private static Comment toDomain(CommentEntity c){return new Comment(c.getId(),c.getBook().getId(),c.getUser().getUsername(),c.getText(),c.getCreatedAt());}
 private static UserComment toUserComment(CommentEntity c){return new UserComment(c.getId(),c.getBook().getId(),c.getBook().getTitle(),c.getUser().getId(),c.getUser().getUsername(),c.getText(),c.getCreatedAt());}
 @Override @Transactional(readOnly=true) public List<Comment> findByBookId(long id){return comments.findByBookIdOrderByCreatedAtDesc(id).stream().map(JpaCommentAdapter::toDomain).toList();}
 @Override @Transactional(readOnly=true) public Optional<Comment> findById(long id){return comments.findById(id).map(JpaCommentAdapter::toDomain);}
 @Override @Transactional public Comment add(long bookId,String author,String text,Instant createdAt){
  BookEntity book=books.findById(bookId).orElseThrow(()->new IllegalArgumentException("Book not found"));
  UserEntity user=users.findByUsername(author).orElseThrow(()->new IllegalArgumentException("Виберіть існуючого користувача"));
  return toDomain(comments.save(new CommentEntity(book,user,text,createdAt)));
 }
 @Override @Transactional public boolean delete(long id){if(!comments.existsById(id))return false;comments.deleteById(id);return true;}
 @Override @Transactional(readOnly=true) public List<UserComment> findByUserId(long id){return comments.findByUserIdOrderByCreatedAtDesc(id).stream().map(JpaCommentAdapter::toUserComment).toList();}
}
