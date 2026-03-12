package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.modulatory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.ImpulseImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.modulatory.Default;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.modulatory.Salience;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Transmitter;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.ImpulseImpl.Mode.DMN;

@ApplicationScoped
public class DefaultImpl implements Default {
    private static final Logger logger = LoggerFactory.getLogger(DefaultImpl.class);
    private final Salience salience;
    private final Thalamus thalamus;
    private final Transmitter transmitter;
    private final Nucleus nucleus;
    private final ScheduledExecutorService executorService;

    @Inject
    public DefaultImpl(Salience salience, Thalamus thalamus,
                       Transmitter transmitter, Nucleus nucleus) {
        this.salience = salience;
        this.thalamus = thalamus;
        this.transmitter = transmitter;
        this.nucleus = nucleus;
        this.executorService = newSingleThreadScheduledExecutor();
    }

    /// [Engineering] As detailed in docs/kandel.md
    @PostConstruct
    void activate() {
        this.executorService.schedule(
            this::fluctuate,
            ThreadLocalRandom.current().nextLong(10, 31),
            TimeUnit.SECONDS);
    }

    @PreDestroy
    void deactivate() {
        this.executorService.shutdownNow();
    }

    private void fluctuate() {
        try {
            if (this.salience.isOriented()) {
                return;
            }
            this.fire();
        } catch (Exception e) {
            logger.error("[DMN] fluctuate failed", e);
        } finally {
            this.activate();
        }
    }

    private void fire() {
        var fluctuation = this.transmitter.transmit(null, Fluctuation.class);
        this.nucleus.integrate(fluctuation, () -> {
            if (!fluctuation.aroused()) {
                return;
            }
            this.salience.orient();
            this.thalamus.relay(
                new ImpulseImpl(
                    fluctuation.signal(), DMN, null));
        });
    }
}
