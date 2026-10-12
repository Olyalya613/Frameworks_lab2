package sumdu.edu.ua.spring.component;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class BeanInspector {
    private static final Logger log = LoggerFactory.getLogger(BeanInspector.class);

    // Field injection is shown intentionally because it is required by the lab.
    @Autowired
    private ApplicationContext applicationContext;

    @Value("${catalog.welcome-message}")
    private String welcomeMessage;

    @PostConstruct
    public void showLoadedBeans() {
        log.info("FIELD DI works: ApplicationContext injected = {}", applicationContext != null);
        log.info("Custom property: {}", welcomeMessage);
        log.info("Spring beans loaded: bookApplicationService={}, commentApplicationService={}, applicationClock={}",
                applicationContext.containsBean("bookApplicationService"),
                applicationContext.containsBean("commentApplicationService"),
                applicationContext.containsBean("applicationClock"));
    }
}
