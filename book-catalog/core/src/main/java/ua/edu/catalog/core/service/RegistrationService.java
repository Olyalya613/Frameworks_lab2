package ua.edu.catalog.core.service;

import java.nio.charset.StandardCharsets;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.edu.catalog.core.model.Account;
import ua.edu.catalog.core.model.Role;
import ua.edu.catalog.core.port.AccountStore;

public final class RegistrationService {
    private final AccountStore accounts;
    private final PasswordEncoder encoder;

    public RegistrationService(AccountStore accounts, PasswordEncoder encoder) {
        this.accounts = accounts;
        this.encoder = encoder;
    }

    public void register(String username, String password) {
        String login = username.strip();
        if (!login.matches("[A-Za-z0-9_.-]{3,64}") || login.equalsIgnoreCase("admin")) {
            throw new IllegalArgumentException("Оберіть логін із 3–64 латинських літер, цифр або символів _ . -");
        }
        if (password.length() < 8 || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Пароль має містити від 8 символів і займати не більше 72 байтів.");
        }
        accounts.create(new Account(login, encoder.encode(password), Role.USER));
    }
}
