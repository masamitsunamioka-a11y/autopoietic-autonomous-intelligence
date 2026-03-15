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
@Priority(Interceptor.Priority.APPLICATION)
public class TracedInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(TracedInterceptor.class);
    private final Publisher publisher;

    @Inject
    public TracedInterceptor(Publisher publisher) {
        this.publisher = publisher;
    }

    @AroundInvoke
    public Object trace(InvocationContext context) throws Exception {
        var method = context.getMethod();
        var className = method.getDeclaringClass().getSimpleName();
        var methodName = method.getName();
        this.publisher.publish(
            new TraceEvent(className, methodName));
        return context.proceed();
    }
}
