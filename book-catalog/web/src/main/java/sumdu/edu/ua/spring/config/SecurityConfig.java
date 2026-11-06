package sumdu.edu.ua.spring.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import ua.edu.catalog.core.port.AccountStore;
import ua.edu.catalog.core.service.DatabaseUserDetailsService;
import ua.edu.catalog.core.service.RegistrationService;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(AccountStore accounts) {
        return new DatabaseUserDetailsService(accounts);
    }

    @Bean
    RegistrationService registrationService(AccountStore accounts, PasswordEncoder encoder) {
        return new RegistrationService(accounts, encoder);
    }

    @Bean
    @Order(1)
    SecurityFilterChain api(HttpSecurity http) throws Exception {
        var entry = new BasicAuthenticationEntryPoint();
        entry.setRealmName("books");
        entry.afterPropertiesSet();
        return http.securityMatcher("/api/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("ADMIN"))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(errors -> errors.authenticationEntryPoint(entry))
                .httpBasic(basic -> basic.authenticationEntryPoint(entry))
                .build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain web(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD).permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**",
                                "/login", "/register", "/confirm").permitAll()
                        .requestMatchers("/403").authenticated()
                        .requestMatchers("/books/add", "/books/{id}/edit", "/books/{id}/delete").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/comments/delete").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/", "/books", "/books/{id}",
                                "/comments/**", "/users/{id}/comments").hasAnyRole("USER", "ADMIN")
                        .anyRequest().hasRole("ADMIN"))
                .formLogin(form -> form.loginPage("/login")
                        .defaultSuccessUrl("/books", true)
                        .failureUrl("/login?error").permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
                .exceptionHandling(errors -> errors.accessDeniedPage("/403"))
                .build();
    }
}
