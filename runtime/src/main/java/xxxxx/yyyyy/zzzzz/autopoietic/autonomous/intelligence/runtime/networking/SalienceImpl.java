package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.networking;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.networking.Salience;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@ApplicationScoped
public class SalienceImpl implements Salience {
    private static final Logger logger = LoggerFactory.getLogger(SalienceImpl.class);
    private final AtomicBoolean oriented;
    private final AtomicReference<CountDownLatch> latch;

    public SalienceImpl() {
        this.oriented = new AtomicBoolean(false);
        this.latch = new AtomicReference<>();
    }

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
        try {
            var snapshot = this.latch.get();
            if (snapshot != null) {
                snapshot.await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
