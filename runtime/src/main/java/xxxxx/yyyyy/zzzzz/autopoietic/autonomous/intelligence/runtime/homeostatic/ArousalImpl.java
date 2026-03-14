package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.homeostatic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Arousal;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Modulator;

import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class ArousalImpl implements Arousal {
    private static final Logger logger = LoggerFactory.getLogger(ArousalImpl.class);
    private final AtomicInteger pressure;
    private final int threshold;

    public ArousalImpl() {
        this.pressure = new AtomicInteger(0);
        this.threshold = 10;
    }

    @Override
    public void awaken() {
    }

    @Override
    public void accumulate(@Observes Modulator modulator) {
        this.pressure.incrementAndGet();
    }

    @Override
    public boolean isAwake() {
        return this.pressure.get() < this.threshold;
    }

    @Override
    public void reset() {
        this.pressure.set(0);
    }
}
