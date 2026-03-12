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
@Priority(Interceptor.Priority.APPLICATION + 1)
public class StatusInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(StatusInterceptor.class);
    private final SseRegistry sseRegistry;

    @Inject
    public StatusInterceptor(SseRegistry sseRegistry) {
        this.sseRegistry = sseRegistry;
    }

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        var method = context.getMethod();
        var className = method.getDeclaringClass().getSimpleName();
        var methodName = method.getName();
        var status = this.resolve(className, methodName);
        if (status != null) {
            this.sseRegistry.broadcast(
                this.sseRegistry.buildJson(
                    "status", "", status));
        }
        return context.proceed();
    }

    private String resolve(String className, String methodName) {
        if ("SalienceImpl".equals(className)
            && "orient".equals(methodName)) {
            return "CEN";
        }
        if ("SalienceImpl".equals(className)
            && "release".equals(methodName)) {
            return "DMN";
        }
        if ("ThalamusImpl".equals(className)
            && "burst".equals(methodName)) {
            return "SLEEP";
        }
        return null;
    }
}
