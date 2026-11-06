package sumdu.edu.ua.spring.aop;

import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceTimingAspect {
    private static final Logger log = LoggerFactory.getLogger(ServiceTimingAspect.class);

    @Around("execution(public * ua.edu.catalog.core.service..*(..))")
    public Object timeServiceMethod(ProceedingJoinPoint call) throws Throwable {
        long start = System.nanoTime();
        try {
            return call.proceed();
        } finally {
            log.debug("Core {} elapsedMs={}", call.getSignature().toShortString(),
                    TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
        }
    }
}
