package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Publisher;

@Monitored
@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 1)
public class StatusInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(StatusInterceptor.class);
    private final Publisher publisher;

    @Inject
    public StatusInterceptor(Publisher publisher) {
        this.publisher = publisher;
    }

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        var method = context.getMethod();
        var className = method.getDeclaringClass().getSimpleName();
        var methodName = method.getName();
        var status = this.resolve(className, methodName);
        if (status != null) {
            this.publisher.publish(
                new StatusEvent(status));
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
