package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.networking;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.ImpulseImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Bindic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Diffusic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Releasic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Arousal;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.networking.Default;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.networking.Salience;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.networking.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Potential;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

/// In the future, scope to per-session
@ApplicationScoped
public class DefaultImpl implements Default {
    private static final Logger logger = LoggerFactory.getLogger(DefaultImpl.class);
    private final Salience salience;
    private final Thalamus thalamus;
    private final Arousal arousal;
    private final Nucleus nucleus;
    private final Service<Impulse, Potential> transmitter;
    private final ScheduledExecutorService executorService;

    @Inject
    public DefaultImpl(Salience salience, Thalamus thalamus,
                       Arousal arousal, Nucleus nucleus,
                       @Releasic @Diffusic @Bindic
                       Service<Impulse, Potential> transmitter) {
        this.salience = salience;
        this.thalamus = thalamus;
        this.arousal = arousal;
        this.nucleus = nucleus;
        this.transmitter = transmitter;
        this.executorService = newSingleThreadScheduledExecutor();
    }

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
            if (this.salience.inhibiting() || !this.arousal.isProjecting()) {
                return;
            }
            this.fire();
        } finally {
            this.activate();
        }
    }

    private void fire() {
        var fluctuation = (Fluctuation) this.transmitter.call(
            new ImpulseImpl(null, this.label(), null));
        this.nucleus.integrate(fluctuation, x -> {
            try {
                if (!x.aroused()) {
                    return;
                }
                this.salience.orient();
                this.thalamus.relay(new ImpulseImpl(
                    x.signal(), this.label(), null));
            } catch (Exception e) {
                logger.error("fire failed", e);
            }
        });
    }

    private String label() {
        return Default.class.getSimpleName();
    }
}
