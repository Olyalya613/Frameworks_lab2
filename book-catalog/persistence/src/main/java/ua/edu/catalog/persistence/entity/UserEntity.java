package ua.edu.catalog.persistence.entity;
import jakarta.persistence.*;
import java.util.*;
@Entity @Table(name="users")
public class UserEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=64) private String username;
 @Column(nullable=false) private String password;
 @Column(nullable=false,length=32) private String role;
 @OneToMany(mappedBy="user",cascade=CascadeType.ALL,fetch=FetchType.LAZY) private List<CommentEntity> comments=new ArrayList<>();
 protected UserEntity() {}
 public Long getId(){return id;} public String getUsername(){return username;} public String getPassword(){return password;} public String getRole(){return role;}
 public List<CommentEntity> getComments(){return comments;}
}
