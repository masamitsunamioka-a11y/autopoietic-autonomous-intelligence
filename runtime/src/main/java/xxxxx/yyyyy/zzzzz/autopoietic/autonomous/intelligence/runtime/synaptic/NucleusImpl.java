package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.homeostatic.ModulatorImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Modulator;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Potential;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import static java.util.concurrent.Executors.newCachedThreadPool;

/// In the future, per-caller Qualifier + @SessionScoped
@ApplicationScoped
public class NucleusImpl implements Nucleus {
    private static final Logger logger = LoggerFactory.getLogger(NucleusImpl.class);
    private final Event<Modulator> event;
    private final ExecutorService executorService;
    private final Deque<Object> signals;

    @Inject
    public NucleusImpl(Event<Modulator> event) {
        this.event = event;
        this.executorService = newCachedThreadPool();
        this.signals = new ConcurrentLinkedDeque<>();
    }

    @PreDestroy
    void deactivate() {
        this.executorService.shutdownNow();
    }

    @Override
    public <T extends Potential>
    void integrate(T potential, Consumer<T> consumer) {
        this.executorService.submit(() -> {
            synchronized (this.signals) {
                this.signals.add(potential);
                if (!this.shouldFire()) {
                    return;
                }
                this.signals.clear();
            }
            consumer.accept(potential);
            this.event.fire(new ModulatorImpl());
        });
    }

    private boolean shouldFire() {
        /// Kandel Ch.9, 12: temporal summation -> T6
        return !this.signals.isEmpty();
    }
}
