package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/// [Engineering] As detailed in docs/kandel.md
public class HabituationGuard {
    private static final Logger logger = LoggerFactory.getLogger(HabituationGuard.class);
    private final AtomicReference<String> last;
    private final AtomicInteger count;

    public HabituationGuard() {
        this.last = new AtomicReference<>("");
        this.count = new AtomicInteger(0);
    }

    public boolean observe(String effector) {
        if (effector.equals(this.last.get())) {
            if (this.count.incrementAndGet() >= 3) {
                this.count.set(0);
                this.last.set("");
                return true;
            }
            return false;
        }
        this.last.set(effector);
        this.count.set(1);
        return false;
    }
}
