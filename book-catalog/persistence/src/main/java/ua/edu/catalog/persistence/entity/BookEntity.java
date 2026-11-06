package ua.edu.catalog.persistence.entity;
import jakarta.persistence.*;
import java.util.*;
@Entity @Table(name="books")
public class BookEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String title;
 @Column(nullable=false) private String author;
 @Column(name="pub_year",nullable=false) private int year;
 @OneToMany(mappedBy="book",cascade=CascadeType.ALL,fetch=FetchType.LAZY,orphanRemoval=true) private List<CommentEntity> comments=new ArrayList<>();
 protected BookEntity() {}
 public BookEntity(String title,String author,int year){this.title=title;this.author=author;this.year=year;}
 public Long getId(){return id;} public String getTitle(){return title;} public String getAuthor(){return author;} public int getYear(){return year;}
 public void setTitle(String v){title=v;} public void setAuthor(String v){author=v;} public void setYear(int v){year=v;}
 public List<CommentEntity> getComments(){return comments;}
}
