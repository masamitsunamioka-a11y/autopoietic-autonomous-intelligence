package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.modulatory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.modulatory.Salience;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.currentThread;

@ApplicationScoped
public class SalienceImpl implements Salience {
    private static final Logger logger = LoggerFactory.getLogger(SalienceImpl.class);
    private final AtomicBoolean oriented = new AtomicBoolean(false);
    private final AtomicReference<CountDownLatch> latch = new AtomicReference<>();

    @Override
    public void orient() {
        this.latch.set(new CountDownLatch(1));
        this.oriented.set(true);
    }

    @Override
    public void release(@Observes Percept percept) {
        if (!this.oriented.get()) {
            return;
        }
        this.oriented.set(false);
        var snapshot = this.latch.get();
        if (snapshot != null) {
            snapshot.countDown();
        }
    }

    @Override
    public boolean isOriented() {
        return this.oriented.get();
    }

    @Override
    public void await() {
        var snapshot = this.latch.get();
        if (snapshot != null) {
            try {
                snapshot.await();
            } catch (InterruptedException e) {
                currentThread().interrupt();
            }
        }
    }
}
