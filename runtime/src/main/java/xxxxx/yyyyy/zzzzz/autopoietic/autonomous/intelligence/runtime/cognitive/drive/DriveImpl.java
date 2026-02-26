package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.drive;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.Transducer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.StimulusImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Drive;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Thalamus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class DriveImpl implements Drive {
    private static final Logger logger = LoggerFactory.getLogger(DriveImpl.class);
    private final Nucleus nucleus;
    private final Transducer transducer;
    private final Thalamus thalamus;
    private final Cortex cortex;
    private final ScheduledExecutorService executorService;

    @Inject
    public DriveImpl(Nucleus nucleus,
                     Transducer transducer,
                     Thalamus thalamus,
                     Cortex cortex) {
        this.nucleus = nucleus;
        this.transducer = transducer;
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
            var signal = this.transducer.drive();
            var output = this.nucleus.compute(signal, Urge.class);
            logger.debug("[NUCLEUS] Computing: ({}) [{}], Aroused: {}",
                output.confidence(),
                output.reasoning(),
                output.aroused());
            if (output.aroused()) {
                this.cortex.tryPerceive(
                        this.thalamus.relay(
                            new StimulusImpl(output.ideation(), null)))
                    .ifPresent(percept -> {
                        if (output.vocalize()) {
                            System.out.printf("%n%s>%n%s%n",
                                percept.neuron(),
                                percept.answer());
                        } else {
                            System.out.printf("%n\u001B[90m[introspection] %s>%n%s\u001B[0m%n",
                                percept.neuron(),
                                percept.answer());
                        }
                    });
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
