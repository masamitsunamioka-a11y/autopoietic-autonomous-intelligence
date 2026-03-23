package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestingExtension implements Extension {
    private static volatile CountDownLatch latch = new CountDownLatch(1);

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery event) {
        event.addBean()
            .types(Validator.class)
            .scope(ApplicationScoped.class)
            .createWith(x -> {
                try (var factory = Validation.buildDefaultValidatorFactory()) {
                    return factory.getValidator();
                }
            });
        event.addObserverMethod()
            .observedType(Percept.class)
            .notifyWith(x -> latch.countDown());
    }

    public static void reset() {
        latch = new CountDownLatch(1);
    }

    public static void await(long timeout, TimeUnit unit) {
        try {
            latch.await(timeout, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
