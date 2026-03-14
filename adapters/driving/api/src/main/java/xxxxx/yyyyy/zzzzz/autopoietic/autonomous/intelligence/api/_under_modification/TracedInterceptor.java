package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api._under_modification;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Events;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.EventImpl;

@Interceptable
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class TracedInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(TracedInterceptor.class);
    private final Events events;

    @Inject
    public TracedInterceptor(Events events) {
        this.events = events;
    }

    @AroundInvoke
    public Object trace(InvocationContext context) throws Exception {
        var method = context.getMethod();
        var className = method.getDeclaringClass().getSimpleName();
        var methodName = method.getName();
        this.events.queue(
            new EventImpl("trace", className, methodName));
        return context.proceed();
    }
}
