package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.*;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.stream.Collectors.toSet;

@ApplicationScoped
public class CortexImpl implements Cortex {
    private static final Logger logger = LoggerFactory.getLogger(CortexImpl.class);
    private final Nucleus nucleus;
    private final Encoder encoder;
    private final Thalamus thalamus;
    private final Plasticity plasticity;
    private final Repository<Effector> effectorRepository;
    private final Memory memory;
    private final ReentrantLock focus;
    private final AtomicReference<String> lastEffector;
    private final AtomicInteger consecutiveFireCount;

    @Inject
    public CortexImpl(Nucleus nucleus,
                      Encoder encoder,
                      Thalamus thalamus,
                      Plasticity plasticity,
                      Repository<Effector> effectorRepository,
                      Memory memory) {
        this.nucleus = nucleus;
        this.encoder = encoder;
        this.thalamus = thalamus;
        this.plasticity = plasticity;
        this.effectorRepository = effectorRepository;
        this.memory = memory;
        this.focus = new ReentrantLock();
        this.lastEffector = new AtomicReference<>("");
        this.consecutiveFireCount = new AtomicInteger(0);
    }

    @Override
    public Percept perceive(Impulse impulse) {
        this.focus.lock();
        try {
            return this.doPerceive(impulse);
        } finally {
            this.focus.unlock();
        }
    }

    @Override
    public Optional<Percept> tryPerceive(Impulse impulse) {
        if (!this.focus.tryLock()) {
            return Optional.empty();
        }
        try {
            return Optional.of(this.doPerceive(impulse));
        } finally {
            this.focus.unlock();
        }
    }

    private Percept doPerceive(Impulse impulse) {
        Objects.requireNonNull(impulse.neuron());
        var encoding = this.encoder.perception(impulse);
        var output = this.nucleus.compute(encoding, Decision.class);
        logger.debug("[NUCLEUS] Computing: ({}) [{}], " +
                "Phase: {}, Effector: {}, PropagateTo: {}",
            output.confidence(),
            output.reasoning(),
            output.phase(),
            output.effector(),
            output.target()
        );
        return switch (output.phase()) {
            case "RESPOND" -> {
                yield this.respond(impulse, output);
            }
            case "FIRE" -> {
                this.fire(impulse, output);
                yield this.doPerceive(
                    this.thalamus.relay(impulse));
            }
            case "LEARN" -> {
                this.learn(impulse);
                yield this.doPerceive(
                    this.thalamus.relay(impulse));
            }
            case "PROPAGATE" -> {
                this.propagate(impulse, output);
                yield this.doPerceive(
                    this.thalamus.relay(impulse));
            }
            default -> {
                throw new IllegalStateException();
            }
        };
    }

    private Percept respond(Impulse impulse, Decision decision) {
        var neuron = impulse.neuron();
        this.memory.record(neuron.name(), decision.response());
        return new InternalPercept(
            neuron.name(),
            decision.reasoning(),
            decision.confidence(),
            decision.response());
    }

    private void fire(Impulse impulse, Decision decision) {
        var neuron = impulse.neuron();
        var effector = decision.effector();
        logger.debug("\n--- act: {}\n--- own: {}\n--- all: {}",
            effector,
            this.own(neuron),
            this.all());
        if (effector.equals(this.lastEffector.get())) {
            if (this.consecutiveFireCount.incrementAndGet() >= 3) {
                logger.warn("[CORTEX] Loop detected on {}, injecting SYSTEM WARNING", effector);
                this.consecutiveFireCount.set(0);
                this.lastEffector.set("");
                this.memory.record("[SYSTEM]",
                    "[SYSTEM WARNING] Effector " + effector + " fired 3+ consecutive times. Do not FIRE again.");
            }
        } else {
            this.lastEffector.set(effector);
            this.consecutiveFireCount.set(1);
        }
        var output = neuron.schemas().stream()
            .flatMap(x -> x.effectors().stream())
            .filter(x -> x.name().equals(effector))
            .findFirst()
            .orElseThrow()
            .fire(this.memory.state());
        output.forEach((k, v) -> {
            this.memory.update(
                "results." + effector + "." + k, v);
        });
    }

    private void learn(Impulse impulse) {
        this.plasticity.potentiate(impulse);
        this.plasticity.prune();
    }

    private void propagate(Impulse impulse, Decision decision) {
        this.memory.update(
            "last_propagate_from", impulse.neuron().name());
        this.memory.update(
            "last_propagate_to", decision.target());
    }

    /// @formatter:off
    private static record InternalPercept(
            String neuron,
            String reasoning,
            double confidence,
            String answer) implements Percept {
    }
    /// @formatter:on
    private Set<String> own(Neuron neuron) {
        return neuron.schemas().stream()
            .flatMap(x -> x.effectors().stream())
            .map(Effector::name)
            .collect(toSet());
    }

    private Set<String> all() {
        return this.effectorRepository.findAll().stream()
            .map(Effector::name)
            .collect(toSet());
    }
}
