package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.homeostatic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Arousal;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Modulator;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class ArousalImpl implements Arousal {
    private static final Logger logger = LoggerFactory.getLogger(ArousalImpl.class);
    private final AtomicBoolean projecting;
    private final AtomicInteger pressure;
    private final int threshold;

    public ArousalImpl() {
        this.projecting = new AtomicBoolean(true);
        this.pressure = new AtomicInteger(0);
        this.threshold = 10;
    }

    @Override
    public void awaken() {
        this.projecting.set(true);
    }

    @Override
    public void accumulate(@Observes Modulator modulator) {
        var current = this.pressure.incrementAndGet();
        if (current >= this.threshold) {
            this.projecting.set(false);
        }
    }

    @Override
    public boolean isAwake() {
        return this.projecting.get();
    }

    @Override
    public void reset() {
        this.pressure.set(0);
    }
}
