package sumdu.edu.ua.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages={"sumdu.edu.ua.spring","ua.edu.catalog.persistence.adapter"})
@EntityScan("ua.edu.catalog.persistence.entity")
@EnableJpaRepositories("ua.edu.catalog.persistence.repository")
public class SpringBookApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBookApplication.class, args);
    }
}
