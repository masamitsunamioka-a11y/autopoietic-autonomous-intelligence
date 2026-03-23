package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.s;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Publisher;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.e.MethodInvoked;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.e.NetworkSwitched;

import java.util.Map;

@Monitored
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class MonitoredInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(MonitoredInterceptor.class);
    private static final Map<String, String> STATUSES = Map.of(
        "SalienceImpl.orient", "CEN",
        "SalienceImpl.release", "DMN",
        "ThalamusImpl.burst", "SLEEP");
    private final Publisher publisher;

    @Inject
    public MonitoredInterceptor(Publisher publisher) {
        this.publisher = publisher;
    }

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        var method = context.getMethod();
        var className = method.getDeclaringClass().getSimpleName();
        var methodName = method.getName();
        this.publisher.submit(new MethodInvoked(className, methodName));
        var status = STATUSES.get(className + "." + methodName);
        if (status != null) {
            this.publisher.submit(new NetworkSwitched(status));
        }
        return context.proceed();
    }
}
