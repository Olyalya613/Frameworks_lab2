package sumdu.edu.ua.spring.component;

import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ua.edu.catalog.core.exception.ConflictException;
import ua.edu.catalog.core.model.Account;
import ua.edu.catalog.core.model.Role;
import ua.edu.catalog.core.port.AccountStore;

@Component
@Profile("prod")
public class ProductionAdmin implements ApplicationRunner {
    private final AccountStore accounts;
    private final PasswordEncoder encoder;
    private final String password;

    public ProductionAdmin(AccountStore accounts, PasswordEncoder encoder,
                           @Value("${APP_ADMIN_PASSWORD}") String password) {
        this.accounts = accounts;
        this.encoder = encoder;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (password.length() < 16 || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalStateException("APP_ADMIN_PASSWORD must have at least 16 characters and at most 72 UTF-8 bytes");
        }
        var existing = accounts.findByUsername("admin");
        if (existing.isEmpty()) {
            try {
                accounts.create(new Account("admin", encoder.encode(password), Role.ADMIN));
                return;
            } catch (ConflictException concurrentInsert) {
                existing = accounts.findByUsername("admin");
                if (existing.isEmpty()) throw concurrentInsert;
            }
        }
        if (existing.get().role() != Role.ADMIN) {
            throw new IllegalStateException("The reserved admin account must have role ADMIN");
        }
    }
}
