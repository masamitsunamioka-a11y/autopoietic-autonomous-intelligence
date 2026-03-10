package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.modulatory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.PerceptImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.TraceImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.ImpulseImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.modulatory.Default;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.modulatory.Salience;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Transmitter;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

@ApplicationScoped
public class DefaultImpl implements Default {
    private static final Logger logger = LoggerFactory.getLogger(DefaultImpl.class);
    private final Salience salience;
    private final Event<Percept> event;
    private final Thalamus thalamus;
    private final Episode episode;
    private final Transmitter transmitter;
    private final Nucleus nucleus;
    private final Repository<Area> areaRepository;
    private final ScheduledExecutorService executorService;

    @Inject
    public DefaultImpl(Salience salience,
                       Event<Percept> event, Thalamus thalamus,
                       Episode episode,
                       Transmitter transmitter,
                       Nucleus nucleus,
                       Repository<Area> areaRepository) {
        this.salience = salience;
        this.event = event;
        this.thalamus = thalamus;
        this.episode = episode;
        this.transmitter = transmitter;
        this.nucleus = nucleus;
        this.areaRepository = areaRepository;
        this.executorService = newSingleThreadScheduledExecutor();
    }

    @PostConstruct
    void activate() {
        this.scheduleFire();
    }

    @PreDestroy
    void deactivate() {
        this.executorService.shutdownNow();
    }

    private void fire() {
        try {
            if (this.salience.isOriented()) {
                return;
            }
            var fluctuation = this.transmitter.transmit(null, Fluctuation.class);
            this.nucleus.integrate(fluctuation, () -> {
                if (!fluctuation.aroused()) {
                    return;
                }
                var area = this.areaRepository.find(fluctuation.area());
                if (area == null) {
                    return;
                }
                this.introspect(fluctuation, area);
                if (fluctuation.vocalize()) {
                    this.salience.orient();
                }
                this.thalamus.relay(
                    new ImpulseImpl(fluctuation.signal(), area));
            });
        } catch (Exception e) {
            logger.error("[DMN] fire failed", e);
        } finally {
            this.scheduleFire();
        }
    }

    private void introspect(Fluctuation fluctuation, Area area) {
        this.episode.encode(
            new TraceImpl("[DMN]", fluctuation.signal()));
        this.event.fire(
            new PerceptImpl(fluctuation.signal(), area.id()));
    }

    /// [Engineering] As detailed in docs/kandel.md
    private void scheduleFire() {
        this.executorService.schedule(
            this::fire,
            ThreadLocalRandom.current().nextLong(10, 31),
            TimeUnit.SECONDS);
    }
}
