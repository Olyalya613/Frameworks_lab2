package sumdu.edu.ua.spring.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.catalog.core.model.User;
import ua.edu.catalog.core.port.UserRepositoryPort;
import java.util.*;
@Service public class UserApplicationService {
 private final UserRepositoryPort users;
 public UserApplicationService(UserRepositoryPort users){this.users=users;}
 @Transactional(readOnly=true) public User get(long id){return users.findById(id).orElseThrow(()->new IllegalArgumentException("Користувача не знайдено"));}
 @Transactional(readOnly=true) public List<User> all(){return users.findAll();}
}
