package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.homeostatic;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Arousal;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Sleep;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

/// In the future, scope to per-session
@ApplicationScoped
public class SleepImpl implements Sleep {
    private static final Logger logger = LoggerFactory.getLogger(SleepImpl.class);
    private final Arousal arousal;
    private final ScheduledExecutorService executorService;

    @Inject
    public SleepImpl(Arousal arousal) {
        this.arousal = arousal;
        this.executorService = newSingleThreadScheduledExecutor();
    }

    @PostConstruct
    void activate() {
        this.executorService.schedule(
            this::sleep,
            ThreadLocalRandom.current().nextLong(60, 121),
            TimeUnit.SECONDS);
    }

    @PreDestroy
    void deactivate() {
        this.executorService.shutdownNow();
    }

    private void sleep() {
        try {
            if (this.arousal.isProjecting()) {
                return;
            }
            this.arousal.project();
        } finally {
            this.activate();
        }
    }
}
