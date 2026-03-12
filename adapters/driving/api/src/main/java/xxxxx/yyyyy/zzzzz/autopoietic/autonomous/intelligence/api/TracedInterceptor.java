package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Traced
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class TracedInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(TracedInterceptor.class);
    private final SseRegistry sseRegistry;

    @Inject
    public TracedInterceptor(SseRegistry sseRegistry) {
        this.sseRegistry = sseRegistry;
    }

    @AroundInvoke
    public Object trace(InvocationContext context) throws Exception {
        var method = context.getMethod();
        var className = method.getDeclaringClass().getSimpleName();
        var methodName = method.getName();
        logger.info("[TRACE] {}.{}", className, methodName);
        this.sseRegistry.broadcast(
            this.sseRegistry.buildJson(
                "trace", className, methodName));
        return context.proceed();
    }
}
