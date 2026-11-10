package ua.edu.catalog.core.port;

import java.util.Optional;
import ua.edu.catalog.core.model.Account;

public interface AccountStore {
    Optional<Account> findByUsername(String username);
    void create(Account account);
}
