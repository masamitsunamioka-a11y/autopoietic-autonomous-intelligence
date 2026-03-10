package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newCachedThreadPool;

@ApplicationScoped
public class NucleusImpl implements Nucleus {
    private static final Logger logger = LoggerFactory.getLogger(NucleusImpl.class);
    /// [Engineering] As detailed in docs/kandel.md
    private final ExecutorService executorService;
    private final Deque<Object> signals;

    @Inject
    public NucleusImpl() {
        this.executorService = newCachedThreadPool();
        this.signals = new ConcurrentLinkedDeque<>();
    }

    @PreDestroy
    void deactivate() {
        this.executorService.shutdownNow();
    }

    @Override
    public <T> void integrate(T signal, Runnable fire) {
        this.executorService.submit(() -> {
            this.signals.add(signal);
            if (!this.shouldFire()) {
                return;
            }
            this.signals.clear();
            fire.run();
        });
    }

    private boolean shouldFire() {
        /// Kandel Ch.9, 12: temporal summation -> T6
        /// Kandel Ch.51: sleep state threshold -> T7
        return !this.signals.isEmpty();
    }
}
