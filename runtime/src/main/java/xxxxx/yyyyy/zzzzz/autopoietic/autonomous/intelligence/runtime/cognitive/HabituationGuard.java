package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/// [Engineering] As detailed in docs/kandel.md
public class HabituationGuard {
    private final AtomicReference<String> last;
    private final AtomicInteger count;

    public HabituationGuard() {
        this.last = new AtomicReference<>("");
        this.count = new AtomicInteger(0);
    }

    public boolean observe(String effector) {
        if (effector.equals(this.last.get())) {
            return this.count.incrementAndGet() >= 3;
        }
        this.last.set(effector);
        this.count.set(1);
        return false;
    }

    public void reset() {
        this.count.set(0);
        this.last.set("");
    }
}
