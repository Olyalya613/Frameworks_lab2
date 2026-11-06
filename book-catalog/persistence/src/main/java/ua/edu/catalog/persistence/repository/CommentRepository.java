package ua.edu.catalog.persistence.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.catalog.persistence.entity.CommentEntity;
import java.util.List;
public interface CommentRepository extends JpaRepository<CommentEntity,Long> {
 List<CommentEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
 List<CommentEntity> findByBookIdOrderByCreatedAtDesc(Long bookId);
 List<CommentEntity> findByUserId(Long userId);
}
