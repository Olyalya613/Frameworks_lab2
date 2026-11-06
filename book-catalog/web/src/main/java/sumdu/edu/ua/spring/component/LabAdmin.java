package sumdu.edu.ua.spring.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ua.edu.catalog.core.model.Account;
import ua.edu.catalog.core.model.Role;
import ua.edu.catalog.core.port.AccountStore;

@Component
@Profile("lab9")
public class LabAdmin implements ApplicationRunner {
    private final AccountStore accounts;
    private final PasswordEncoder encoder;
    private final String password;

    public LabAdmin(AccountStore accounts, PasswordEncoder encoder,
                    @Value("${LAB9_ADMIN_PASSWORD:Lab9-Admin-2026}") String password) {
        this.accounts = accounts;
        this.encoder = encoder;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (accounts.findByUsername("admin").isEmpty()) {
            accounts.create(new Account("admin", encoder.encode(password), Role.ADMIN));
        }
    }
}
