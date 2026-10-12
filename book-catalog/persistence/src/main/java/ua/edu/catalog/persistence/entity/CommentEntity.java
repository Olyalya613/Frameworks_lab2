package ua.edu.catalog.persistence.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="comments")
public class CommentEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="book_id",nullable=false) private BookEntity book;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) private UserEntity user;
 @Column(nullable=false,length=1000) private String text;
 @Column(name="created_at",nullable=false) private Instant createdAt;
 protected CommentEntity() {}
 public CommentEntity(BookEntity book,UserEntity user,String text,Instant createdAt){this.book=book;this.user=user;this.text=text;this.createdAt=createdAt;}
 public Long getId(){return id;} public BookEntity getBook(){return book;} public UserEntity getUser(){return user;} public String getText(){return text;} public Instant getCreatedAt(){return createdAt;}
}
