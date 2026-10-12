package ua.edu.catalog.persistence.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.catalog.persistence.entity.UserEntity;
import java.util.Optional;
public interface UserRepository extends JpaRepository<UserEntity,Long> { Optional<UserEntity> findByUsername(String username); }
