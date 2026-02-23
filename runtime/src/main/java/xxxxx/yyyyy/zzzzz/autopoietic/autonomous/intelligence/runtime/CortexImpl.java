package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.*;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class CortexImpl implements Cortex {
    private static final Logger logger = LoggerFactory.getLogger(CortexImpl.class);
    private final Nucleus nucleus;
    private final Encoder encoder;
    private final Thalamus thalamus;
    private final Plasticity plasticity;
    private final Repository<Effector> effectorRepository;
    private final Memory memory;

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
    }

    @Override
    public Percept perceive(Impulse impulse) {
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
                yield this.perceive(
                    this.thalamus.relay(impulse));
            }
            case "LEARN" -> {
                this.learn(impulse);
                yield this.perceive(
                    this.thalamus.relay(impulse));
            }
            case "PROPAGATE" -> {
                this.propagate(impulse, output);
                yield this.perceive(
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
        logger.debug("\n--- act: {}\n--- own: {}\n--- all: {}",
            decision.effector(),
            this.own(neuron),
            this.all());
        var output = neuron.schemas().stream()
            .flatMap(x -> x.effectors().stream())
            .filter(x -> x.name().equals(decision.effector()))
            .findFirst()
            .orElseThrow()
            .fire(this.memory.state());
        output.forEach((k, v) -> {
            this.memory.update(
                "results." + decision.effector() + "." + k, v);
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

    private static record InternalPercept(
        String neuron,
        String reasoning,
        double confidence,
        String answer) implements Percept {
    }

    private Set<String> own(Neuron neuron) {
        return neuron.schemas().stream()
            .flatMap(x -> x.effectors().stream())
            .map(Effector::name)
            .collect(Collectors.toSet());
    }

    private Set<String> all() {
        return this.effectorRepository.findAll().stream()
            .map(Effector::name)
            .collect(Collectors.toSet());
    }
}
