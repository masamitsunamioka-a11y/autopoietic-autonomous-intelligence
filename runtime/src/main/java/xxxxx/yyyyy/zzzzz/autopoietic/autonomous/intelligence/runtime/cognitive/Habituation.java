package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Habituation {
    private static final Logger logger = LoggerFactory.getLogger(Habituation.class);
    private static final int THRESHOLD = 3;
    private final AtomicReference<String> last;
    private final AtomicInteger count;

    public Habituation() {
        this.last = new AtomicReference<>("");
        this.count = new AtomicInteger(0);
    }

    public boolean habituated(Decision decision) {
        var key = "FIRE".equals(decision.process())
            ? decision.process() + ":" + decision.effector()
            : decision.process();
        if (key.equals(this.last.get())) {
            if (this.count.incrementAndGet() >= THRESHOLD) {
                this.count.set(0);
                this.last.set("");
                return true;
            }
            return false;
        }
        this.last.set(key);
        this.count.set(1);
        return false;
    }
}
