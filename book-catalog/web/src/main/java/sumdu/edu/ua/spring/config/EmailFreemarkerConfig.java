package sumdu.edu.ua.spring.config;

import freemarker.template.Configuration;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class EmailFreemarkerConfig {
    @Bean("freemarkerEmailConfig")
    public Configuration freemarkerEmailConfig() {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "/mail-templates/");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        return cfg;
    }
}
