package ua.edu.catalog.persistence.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.edu.catalog.persistence.entity.BookEntity;
public interface BookRepository extends JpaRepository<BookEntity,Long> {}
