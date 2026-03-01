package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.homeostatic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Drive;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Salience;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engravable;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
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
    private final ScheduledExecutorService executorService;

    @Inject
    public DriveImpl(Salience salience, Cortex cortex,
                     Repository<Area, Engravable> areaRepository,
                     Nucleus nucleus) {
        this.salience = salience;
        this.cortex = cortex;
        this.areaRepository = areaRepository;
        this.nucleus = nucleus;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void activate() {
        this.schedule();
    }

    @Override
    public void deactivate() {
        this.executorService.shutdownNow();
    }

    private void fire() {
        try {
            if (this.salience.isOriented()) {
                return;
            }
            var output = this.nucleus.integrate(null, Drive.class, Urge.class);
            if (output.aroused()) {
                var area = this.areaRepository.find(output.area());
                var impulse = Impulse.of(output.signal(), area);
                var percept = this.cortex.respond(impulse);
                if (output.vocalize()) {
                    System.out.printf("%n%s>%n%s%n",
                        percept.location(),
                        percept.content());
                } else {
                    System.out.printf("%n\u001B[90m[introspection] %s>%n%s\u001B[0m%n",
                        percept.location(),
                        percept.content());
                }
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
