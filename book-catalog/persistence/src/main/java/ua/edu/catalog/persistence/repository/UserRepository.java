package ua.edu.catalog.persistence.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.catalog.persistence.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}
