package sumdu.edu.ua.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AppConfig {
    // Custom bean required by the laboratory assignment.
    @Bean
    public Clock applicationClock() {
        return Clock.systemUTC();
    }
}
