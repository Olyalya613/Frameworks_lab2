package ua.edu.catalog.persistence.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.catalog.persistence.entity.CommentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface CommentRepository extends JpaRepository<CommentEntity,Long> {
 List<CommentEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
 List<CommentEntity> findByBookIdOrderByCreatedAtDesc(Long bookId);
 List<CommentEntity> findByUserId(Long userId);

 @Modifying
 @Query("delete from CommentEntity c where c.id = :commentId and c.book.id = :bookId")
 int deleteForBook(@Param("bookId") long bookId, @Param("commentId") long commentId);
}
