package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.networking;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.networking.Salience;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Potential;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

/// In the future, scope to per-session
@ApplicationScoped
public class SalienceImpl implements Salience {
    private static final Logger logger = LoggerFactory.getLogger(SalienceImpl.class);
    private static final long IDLE_THRESHOLD_SECONDS = 120;
    private final AtomicBoolean oriented;
    private final AtomicReference<Instant> lastCollateral;
    private final ScheduledExecutorService executorService;

    public SalienceImpl() {
        this.oriented = new AtomicBoolean(false);
        this.lastCollateral = new AtomicReference<>(Instant.now());
        this.executorService = newSingleThreadScheduledExecutor();
    }

    @PostConstruct
    void activate() {
        this.executorService.scheduleAtFixedRate(
            this::monitor,
            5,
            5,
            TimeUnit.SECONDS);
    }

    @PreDestroy
    void deactivate() {
        this.executorService.shutdownNow();
    }

    @Override
    public void orient() {
        this.lastCollateral.set(Instant.now());
        this.oriented.set(true);
    }

    public void receive(@Observes Potential potential) {
        this.lastCollateral.set(Instant.now());
    }

    @Override
    public boolean inhibiting() {
        return this.oriented.get();
    }

    private void monitor() {
        if (!this.oriented.get()) {
            return;
        }
        var elapsed = Duration.between(this.lastCollateral.get(), Instant.now());
        if (elapsed.toSeconds() >= IDLE_THRESHOLD_SECONDS) {
            this.oriented.set(false);
        }
    }
}
