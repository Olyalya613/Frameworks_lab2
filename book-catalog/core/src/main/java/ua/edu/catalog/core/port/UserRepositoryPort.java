package ua.edu.catalog.core.port;
import ua.edu.catalog.core.model.User;
import java.util.*;
public interface UserRepositoryPort { Optional<User> findById(long id); List<User> findAll(); }
