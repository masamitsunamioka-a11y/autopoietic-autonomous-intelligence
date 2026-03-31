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
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.autopoietic.Autopoiesis;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Arousal;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.networking.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Potential;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

/// In the future, scope to per-session
@ApplicationScoped
public class ThalamusImpl implements Thalamus {
    private static final Logger logger = LoggerFactory.getLogger(ThalamusImpl.class);
    private final Arousal arousal;
    private final Autopoiesis autopoiesis;
    private final Cortex cortex;
    private final Knowledge knowledge;
    private final Episode episode;
    private final Nucleus nucleus;
    private final Service<Impulse, Potential> transmitter;
    private final ScheduledExecutorService executorService;

    @Inject
    public ThalamusImpl(Arousal arousal,
                        Autopoiesis autopoiesis, Cortex cortex,
                        Knowledge knowledge, Episode episode,
                        Nucleus nucleus,
                        @Releasic @Diffusic @Bindic
                        Service<Impulse, Potential> transmitter) {
        this.arousal = arousal;
        this.autopoiesis = autopoiesis;
        this.cortex = cortex;
        this.knowledge = knowledge;
        this.episode = episode;
        this.nucleus = nucleus;
        this.transmitter = transmitter;
        this.executorService = newSingleThreadScheduledExecutor();
    }

    @PostConstruct
    void activate() {
        this.executorService.schedule(
            this::oscillate,
            ThreadLocalRandom.current().nextLong(10, 21),
            TimeUnit.SECONDS);
    }

    @PreDestroy
    void deactivate() {
        this.executorService.shutdownNow();
    }

    private void oscillate() {
        try {
            if (this.arousal.isProjecting()) {
                return;
            }
            this.burst();
        } finally {
            this.activate();
        }
    }

    @Override
    public void relay(Impulse impulse) {
        var projection = (Projection) this.transmitter.call(
            new ImpulseImpl(impulse.signal(), this.label(), null));
        this.nucleus.integrate(projection, x -> {
            try {
                this.cortex.respond(new ImpulseImpl(impulse.signal(), impulse.afferent(), x.area()));
            } catch (Exception e) {
                logger.error("relay failed", e);
            }
        });
    }

    @Override
    public void burst() {
        this.nucleus.integrate(new Spindle(1.0), x -> {
            try {
                this.autopoiesis.conserve();
                this.episode.consolidate();
                allOf(
                    runAsync(this.episode::decay),
                    runAsync(this.knowledge::decay)
                ).join();
            } catch (Exception e) {
                logger.error("burst failed", e);
            }
        });
    }

    private String label() {
        return Thalamus.class.getSimpleName();
    }
}
