package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.homeostatic;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.ImpulseImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Drive;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Salience;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engravable;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Encoder;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class DriveImpl implements Drive {
    private static final Logger logger = LoggerFactory.getLogger(DriveImpl.class);
    private final Salience salience;
    private final Cortex cortex;
    private final Repository<Area, Engravable> areaRepository;
    private final Nucleus nucleus;
    private final Encoder encoder;
    private final Event<Expression> event;
    private final ScheduledExecutorService executorService;

    @Inject
    public DriveImpl(Salience salience, Cortex cortex,
                     Repository<Area, Engravable> areaRepository,
                     Nucleus nucleus, Encoder encoder,
                     Event<Expression> event) {
        this.salience = salience;
        this.cortex = cortex;
        this.areaRepository = areaRepository;
        this.nucleus = nucleus;
        this.encoder = encoder;
        this.event = event;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @PostConstruct
    void activate() {
        this.schedule();
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
            var output = this.integrate();
            if (output.aroused()) {
                var area = this.areaRepository.find(output.area());
                if (area == null) {
                    logger.debug("[DRIVE] subthreshold — area not found: {}",
                        output.area());
                    return;
                }
                var impulse = new ImpulseImpl(output.signal(), area);
                var percept = this.cortex.respond(impulse);
                this.event.fire(new Expression(percept, output.vocalize()));
            }
        } catch (Exception e) {
            logger.error("[DRIVE] fire failed", e);
        } finally {
            this.schedule();
        }
    }

    private Urge integrate() {
        var signal = this.encoder.encode(null, Drive.class);
        return this.nucleus.integrate(
            new ImpulseImpl(signal, null), Urge.class);
    }

    /// [Engineering] As detailed in docs/kandel.md
    private void schedule() {
        this.executorService.schedule(
            this::fire,
            ThreadLocalRandom.current().nextLong(10, 31),
            TimeUnit.SECONDS);
    }
}
