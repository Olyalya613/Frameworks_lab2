package sumdu.edu.ua.spring.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class InfoController {
    private final ApplicationContext context;
    private final Clock clock;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${catalog.owner}")
    private String owner;

    @Value("${catalog.welcome-message}")
    private String welcomeMessage;

    public InfoController(ApplicationContext context, Clock clock) {
        this.context = context;
        this.clock = clock;
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("application", applicationName);
        result.put("owner", owner);
        result.put("message", welcomeMessage);
        result.put("timeUtc", clock.instant().toString());
        result.put("customClockBean", context.containsBean("applicationClock"));
        result.put("bookServiceBean", context.containsBean("bookApplicationService"));
        result.put("commentServiceBean", context.containsBean("commentApplicationService"));
        result.put("repositoryBean", context.containsBean("catalogRepositoryBean"));
        return result;
    }
}
