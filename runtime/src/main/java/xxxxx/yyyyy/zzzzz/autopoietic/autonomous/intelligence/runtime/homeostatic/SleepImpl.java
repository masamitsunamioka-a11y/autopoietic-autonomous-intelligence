package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.homeostatic;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.autopoietic.Autopoiesis;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Sleep;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

@ApplicationScoped
public class SleepImpl implements Sleep {
    private static final Logger logger = LoggerFactory.getLogger(SleepImpl.class);
    private final Autopoiesis autopoiesis;
    private final ScheduledExecutorService executorService;

    @Inject
    public SleepImpl(Autopoiesis autopoiesis) {
        this.autopoiesis = autopoiesis;
        this.executorService = newSingleThreadScheduledExecutor();
    }

    @PostConstruct
    void activate() {
        this.scheduleConsolidation();
    }

    @PreDestroy
    void deactivate() {
        this.executorService.shutdownNow();
    }

    private void consolidate() {
        try {
            this.autopoiesis.conserve();
        } catch (Exception e) {
            logger.error("[SLEEP] consolidation failed", e);
        } finally {
            this.scheduleConsolidation();
        }
    }

    /// [Engineering] As detailed in docs/kandel.md
    private void scheduleConsolidation() {
        this.executorService.schedule(
            this::consolidate,
            ThreadLocalRandom.current().nextLong(30, 60),
            TimeUnit.SECONDS);
    }
}
