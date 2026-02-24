package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Drive;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Thalamus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class DriveImpl implements Drive {
    private static final Logger logger = LoggerFactory.getLogger(DriveImpl.class);
    private final Nucleus nucleus;
    private final Encoder encoder;
    private final Thalamus thalamus;
    private final Cortex cortex;
    private final ScheduledExecutorService executorService;

    @Inject
    public DriveImpl(Nucleus nucleus,
                     Encoder encoder,
                     Thalamus thalamus,
                     Cortex cortex) {
        this.nucleus = nucleus;
        this.encoder = encoder;
        this.thalamus = thalamus;
        this.cortex = cortex;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void start() {
        this.schedule();
    }

    @Override
    public void stop() {
        this.executorService.shutdownNow();
    }

    private void fire() {
        try {
            var encoding = this.encoder.drive();
            var output = this.nucleus.compute(encoding, Urge.class);
            logger.debug("[NUCLEUS] Computing: ({}) [{}], ShouldFire: {}",
                output.confidence(),
                output.reasoning(),
                output.shouldFire());
            if (output.shouldFire()) {
                this.cortex.tryPerceive(
                        this.thalamus.relay(
                            new ImpulseImpl(output.answer(), null)))
                    .ifPresent(percept -> System.out.printf("%n\u001B[90m[introspection] %s>%n%s\u001B[0m%n",
                        percept.neuron(),
                        percept.answer()));
            }
        } catch (Exception e) {
            logger.error("[DRIVE] fire failed", e);
        } finally {
            this.schedule();
        }
    }

    private void schedule() {
        this.executorService.schedule(
            this::fire,
            ThreadLocalRandom.current().nextLong(5, 11),
            TimeUnit.SECONDS);
    }
}
