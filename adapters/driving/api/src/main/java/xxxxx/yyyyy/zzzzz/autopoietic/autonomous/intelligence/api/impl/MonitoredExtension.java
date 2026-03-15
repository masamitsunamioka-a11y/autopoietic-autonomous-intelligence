package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.WithAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitoredExtension implements Extension {
    private static final Logger logger = LoggerFactory.getLogger(MonitoredExtension.class);
    private static final String SPEC_PACKAGE =
        "xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification";

    public <T> void addMonitored(
        @Observes
        @WithAnnotations(ApplicationScoped.class)
        ProcessAnnotatedType<T> pat) {
        var type = pat.getAnnotatedType().getJavaClass();
        if (this.implementsSpecification(type)) {
            pat.configureAnnotatedType().add(Monitored.Literal.INSTANCE);
        }
    }

    private boolean implementsSpecification(Class<?> type) {
        for (var x : type.getInterfaces()) {
            if (x.getPackageName().startsWith(SPEC_PACKAGE)) {
                return true;
            }
        }
        return false;
    }
}
