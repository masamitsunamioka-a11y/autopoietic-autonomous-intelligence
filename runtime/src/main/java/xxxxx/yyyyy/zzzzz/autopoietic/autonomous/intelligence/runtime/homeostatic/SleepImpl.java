package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.homeostatic;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Arousal;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Sleep;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Thalamus;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

@ApplicationScoped
public class SleepImpl implements Sleep {
    private static final Logger logger = LoggerFactory.getLogger(SleepImpl.class);
    private final Arousal arousal;
    private final Thalamus thalamus;
    private final ScheduledExecutorService executorService;

    @Inject
    public SleepImpl(Arousal arousal, Thalamus thalamus) {
        this.arousal = arousal;
        this.thalamus = thalamus;
        this.executorService = newSingleThreadScheduledExecutor();
    }

    @PostConstruct
    void activate() {
        this.executorService.schedule(
            this::sleep,
            ThreadLocalRandom.current().nextLong(30, 60),
            TimeUnit.SECONDS);
    }

    @PreDestroy
    void deactivate() {
        this.executorService.shutdownNow();
    }

    private void sleep() {
        try {
            if (this.arousal.isAwake()) {
                return;
            }
            this.thalamus.burst();
            this.arousal.reset();
        } catch (Exception e) {
            logger.error("[SLEEP] failed", e);
        } finally {
            this.activate();
        }
    }
}
