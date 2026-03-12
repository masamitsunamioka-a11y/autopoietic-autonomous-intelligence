package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.WithAnnotations;

public class TracedExtension implements Extension {
    private static final String SPEC_PACKAGE =
        "xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification";

    public <T> void addTraced(
        @Observes
        @WithAnnotations(ApplicationScoped.class)
        ProcessAnnotatedType<T> pat) {
        var type = pat.getAnnotatedType().getJavaClass();
        if (this.implementsSpecification(type)) {
            pat.configureAnnotatedType()
                .add(Traced.Literal.INSTANCE);
        }
    }

    private boolean implementsSpecification(Class<?> type) {
        for (var iface : type.getInterfaces()) {
            if (iface.getPackageName().startsWith(SPEC_PACKAGE)) {
                return true;
            }
        }
        return false;
    }
}
