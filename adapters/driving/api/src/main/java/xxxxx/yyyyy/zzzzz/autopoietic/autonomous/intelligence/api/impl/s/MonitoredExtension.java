package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.s;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.WithAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitoredExtension implements Extension {
    private static final Logger logger = LoggerFactory.getLogger(MonitoredExtension.class);
    private static final String SPECIFICATION =
        "xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification";

    public <T> void addMonitored(
        @Observes
        @WithAnnotations(ApplicationScoped.class)
        ProcessAnnotatedType<T> event) {
        var type = event.getAnnotatedType().getJavaClass();
        if (this.implementsSpecification(type)) {
            event.configureAnnotatedType().add(Monitored.Literal.INSTANCE);
        }
    }

    private boolean implementsSpecification(Class<?> type) {
        for (var x : type.getInterfaces()) {
            if (x.getPackageName().startsWith(SPECIFICATION)) {
                return true;
            }
        }
        return false;
    }
}
