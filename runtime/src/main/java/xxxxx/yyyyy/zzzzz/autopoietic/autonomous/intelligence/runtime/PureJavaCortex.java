package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.*;

import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class PureJavaCortex implements Cortex {
    private static final Logger logger = LoggerFactory.getLogger(PureJavaCortex.class);
    private final Intelligence intelligence;
    private final PromptAssembler promptAssembler;
    private final Thalamus thalamus;
    private final Plasticity plasticity;
    private final Repository<Effector> effectorRepository;

    @Inject
    public PureJavaCortex(Intelligence intelligence,
                          PromptAssembler promptAssembler,
                          Thalamus thalamus,
                          Plasticity plasticity,
                          Repository<Effector> effectorRepository) {
        this.intelligence = intelligence;
        this.promptAssembler = promptAssembler;
        this.thalamus = thalamus;
        this.plasticity = plasticity;
        this.effectorRepository = effectorRepository;
    }

    @Override
    public Inference perceive(Context context) {
        var neuron = this.thalamus.relay(context);
        var prompt = this.promptAssembler.perception(context, neuron);
        var conclusion = this.intelligence.reason(prompt, Signal.class);
        logger.debug("[INTELLIGENCE] Reasoning: ({}) [{}], Phase: {}, Effector: {}, Handoff: {}",
            conclusion.confidence(),
            conclusion.reasoning(),
            conclusion.phase(),
            conclusion.effector(),
            conclusion.propagateTo()
        );
        return switch (conclusion.phase()) {
            case "RESPOND" -> {
                yield this.respond(context.conversation(), neuron, conclusion);
            }
            case "FIRE" -> {
                this.fire(neuron, conclusion.effector(), context.state());
                yield this.perceive(context);
            }
            case "PROPAGATE" -> {
                this.propagate(context.state(), neuron, conclusion.propagateTo());
                yield this.perceive(context);
            }
            case "ADAPT" -> {
                this.adapt(context, neuron);
                yield this.perceive(context);
            }
            default -> throw new IllegalStateException();
        };
    }

    private Inference respond(Conversation conversation, Neuron neuron, Signal conclusion) {
        conversation.write(neuron.name(), conclusion.answer());
        return new InternalInference(
            neuron.name(),
            conclusion.reasoning(),
            conclusion.answer());
    }

    private static record InternalInference(
        String neuron,
        String reasoning,
        String answer) implements Inference {
        @Override
        public double confidence() {
            return 1.0;
        }
    }

    private void fire(Neuron neuron, String name, State state) {
        logger.trace("\n--- conclusion: {}\n--- own: {}\n--- all: {}",
            name,
            this.own(neuron),
            this.all());
        var output = neuron.receptors().stream()
            .flatMap(x -> x.effectors().stream())
            .filter(x -> x.name().equals(name))
            .findFirst()
            .orElseThrow()
            .fire(state.snapshot());
        output.forEach((k, v) -> {
            state.write("effector_results." + name + "." + k, v);
        });
    }

    private Set<String> own(Neuron neuron) {
        return neuron.receptors().stream()
            .flatMap(x -> x.effectors().stream())
            .map(Effector::name)
            .collect(Collectors.toSet());
    }

    private Set<String> all() {
        return this.effectorRepository.findAll().stream()
            .map(Effector::name)
            .collect(Collectors.toSet());
    }

    private void propagate(State state, Neuron neuron, String propagateTo) {
        state.write("last_propagate_from", neuron.name());
        state.write("last_propagate_to", propagateTo);
    }

    private void adapt(Context context, Neuron neuron) {
        this.plasticity.potentiate(context, neuron);
        this.plasticity.prune();
    }
}
