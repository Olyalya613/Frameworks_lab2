package ua.edu.catalog.persistence.adapter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.catalog.core.model.User;
import ua.edu.catalog.core.port.UserRepositoryPort;
import ua.edu.catalog.persistence.repository.UserRepository;
import java.util.*;
@Repository public class JpaUserAdapter implements UserRepositoryPort {
 private final UserRepository repository;
 public JpaUserAdapter(UserRepository repository){this.repository=repository;}
 @Override @Transactional(readOnly=true) public Optional<User> findById(long id){return repository.findById(id).map(u->new User(u.getId(),u.getUsername(),u.getRole()));}
 @Override @Transactional(readOnly=true) public List<User> findAll(){return repository.findAll().stream().map(u->new User(u.getId(),u.getUsername(),u.getRole())).toList();}
}
