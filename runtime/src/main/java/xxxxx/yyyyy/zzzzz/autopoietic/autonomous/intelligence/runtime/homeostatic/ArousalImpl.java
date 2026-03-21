package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.homeostatic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Arousal;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Modulator;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/// In the future, scope to per-session
@ApplicationScoped
public class ArousalImpl implements Arousal {
    private static final Logger logger = LoggerFactory.getLogger(ArousalImpl.class);
    private static final int THRESHOLD = 10;
    private final AtomicBoolean projecting;
    private final AtomicInteger pressure;

    public ArousalImpl() {
        this.projecting = new AtomicBoolean(true);
        this.pressure = new AtomicInteger(0);
    }

    @Override
    public void project() {
        this.reset();
        this.projecting.set(true);
    }

    public void receive(@Observes Modulator modulator) {
        var current = this.pressure.incrementAndGet();
        if (current >= THRESHOLD) {
            this.projecting.set(false);
        }
    }

    public void reset() {
        this.pressure.set(0);
    }

    @Override
    public boolean isProjecting() {
        return this.projecting.get();
    }
}
