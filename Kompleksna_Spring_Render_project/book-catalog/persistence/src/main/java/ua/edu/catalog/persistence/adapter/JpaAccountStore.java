package ua.edu.catalog.persistence.adapter;

import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import ua.edu.catalog.core.exception.ConflictException;
import ua.edu.catalog.core.model.Account;
import ua.edu.catalog.core.model.Role;
import ua.edu.catalog.core.port.AccountStore;
import ua.edu.catalog.persistence.entity.UserEntity;
import ua.edu.catalog.persistence.repository.UserRepository;

@Repository
public class JpaAccountStore implements AccountStore {
    private final UserRepository repository;

    public JpaAccountStore(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Account> findByUsername(String username) {
        return repository.findByUsername(username)
                .map(user -> new Account(user.getUsername(), user.getPassword(),
                        Role.valueOf(user.getRole())));
    }

    @Override
    public void create(Account account) {
        try {
            repository.saveAndFlush(new UserEntity(account.username(),
                    account.passwordHash(), account.role().name()));
        } catch (DataIntegrityViolationException e) {
            if (repository.existsByUsername(account.username())) {
                throw new ConflictException("Цей логін уже зайнято.");
            }
            throw e;
        }
    }
}
