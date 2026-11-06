package sumdu.edu.ua.spring.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class SecurityAudit {
    private static final Logger log = LoggerFactory.getLogger(SecurityAudit.class);

    @EventListener
    public void login(InteractiveAuthenticationSuccessEvent event) {
        log.info("LOGIN_SUCCESS user={}", event.getAuthentication().getName());
    }

    @EventListener
    public void logout(LogoutSuccessEvent event) {
        log.info("LOGOUT_SUCCESS user={}", event.getAuthentication().getName());
    }
}
