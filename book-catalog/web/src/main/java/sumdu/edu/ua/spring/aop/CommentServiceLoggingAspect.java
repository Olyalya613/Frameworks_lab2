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
public class CommentServiceLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(CommentServiceLoggingAspect.class);

    @Around("execution(void ua.edu.catalog.core.service.CommentService.delete(long,long,java.time.Instant))")
    public Object logDeleteCall(ProceedingJoinPoint call) throws Throwable {
        Object bookId = call.getArgs()[0];
        Object commentId = call.getArgs()[1];
        long start = System.nanoTime();
        log.info("CommentService.delete started bookId={} commentId={}", bookId, commentId);
        try {
            Object result = call.proceed();
            log.info("CommentService.delete finished bookId={} commentId={} elapsedMs={}",
                    bookId, commentId, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            return result;
        } catch (Exception exception) {
            log.warn("CommentService.delete failed bookId={} commentId={} elapsedMs={}: {}",
                    bookId, commentId, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start),
                    exception.getMessage());
            throw exception;
        }
    }
}
