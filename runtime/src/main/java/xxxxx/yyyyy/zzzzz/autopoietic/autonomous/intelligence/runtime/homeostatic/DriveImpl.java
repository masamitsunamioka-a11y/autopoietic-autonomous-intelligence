package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.homeostatic;

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
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Drive;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Salience;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.integrative.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.integrative.Transmitter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.learning.Plasticity;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Thalamus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class DriveImpl implements Drive {
    private static final Logger logger = LoggerFactory.getLogger(DriveImpl.class);
    private final Salience salience;
    private final Plasticity plasticity;
    private final Event<Percept> event;
    private final Thalamus thalamus;
    private final Episode episode;
    private final Transmitter transmitter;
    private final Nucleus nucleus;
    private final Repository<Area> areaRepository;
    private final ScheduledExecutorService executorService;

    @Inject
    public DriveImpl(Salience salience, Plasticity plasticity,
                     Event<Percept> event, Thalamus thalamus,
                     Episode episode,
                     Transmitter transmitter, Nucleus nucleus,
                     Repository<Area> areaRepository) {
        this.salience = salience;
        this.plasticity = plasticity;
        this.event = event;
        this.thalamus = thalamus;
        this.episode = episode;
        this.transmitter = transmitter;
        this.nucleus = nucleus;
        this.areaRepository = areaRepository;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @PostConstruct
    void activate() {
        this.scheduleFire();
        this.scheduleConsolidation();
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
            var urge = this.transmitter.transmit(null, Urge.class);
            this.nucleus.integrate(urge, () -> {
                if (!urge.aroused()) {
                    return;
                }
                var area = this.areaRepository.find(urge.area());
                if (area == null) {
                    return;
                }
                this.introspect(urge, area);
                if (urge.vocalize()) {
                    this.salience.orient();
                }
                this.thalamus.relay(
                    new ImpulseImpl(urge.signal(), area));
            });
        } catch (Exception e) {
            logger.error("[DRIVE] fire failed", e);
        } finally {
            this.scheduleFire();
        }
    }

    private void introspect(Urge urge, Area area) {
        this.episode.encode(
            new TraceImpl("[DMN]", urge.signal()));
        this.event.fire(
            new PerceptImpl(urge.signal(), area.id()));
    }

    private void consolidate() {
        try {
            this.plasticity.prune();
        } catch (Exception e) {
            logger.error("[DRIVE] consolidation failed", e);
        } finally {
            this.scheduleConsolidation();
        }
    }

    /// [Engineering] As detailed in docs/kandel.md
    private void scheduleFire() {
        this.executorService.schedule(
            this::fire,
            ThreadLocalRandom.current().nextLong(10, 31),
            TimeUnit.SECONDS);
    }

    /// [Engineering] As detailed in docs/kandel.md
    private void scheduleConsolidation() {
        this.executorService.schedule(
            this::consolidate,
            ThreadLocalRandom.current().nextLong(30, 60),
            TimeUnit.SECONDS);
    }
}
