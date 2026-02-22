package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.*;

import java.util.Set;
import java.util.stream.Collectors;

/// FIXME
@ApplicationScoped
public class PureJavaInferenceEngine implements InferenceEngine {
    private static final Logger logger = LoggerFactory.getLogger(PureJavaInferenceEngine.class);
    private final Intelligence intelligence;
    private final PromptAssembler promptAssembler;
    private final RoutingEngine routingEngine;
    private final EvolutionEngine evolutionEngine;
    private final Repository<Action> actionRepository;

    @Inject
    public PureJavaInferenceEngine(Intelligence intelligence,
                                   PromptAssembler promptAssembler,
                                   RoutingEngine routingEngine,
                                   EvolutionEngine evolutionEngine,
                                   Repository<Action> actionRepository) {
        this.intelligence = intelligence;
        this.promptAssembler = promptAssembler;
        this.routingEngine = routingEngine;
        this.evolutionEngine = evolutionEngine;
        this.actionRepository = actionRepository;
    }

    @Override
    public Inference infer(Context context) {
        return this.recursiveInfer(context, 0);
    }

    private Inference recursiveInfer(Context context, int depth) {
        if (depth >= 1000) {
            throw new RuntimeException("Max recursion depth reached in InferenceEngine.");
        }
        var agent = this.routingEngine.route(context);
        var prompt = this.promptAssembler.inference(context, agent);
        var conclusion = this.intelligence.reason(prompt, Conclusion.class);
        logger.debug("[INTELLIGENCE] Reasoning: ({}) [{}], Phase: {}, Action: {}, Handoff: {}",
            conclusion.confidence(),
            conclusion.reasoning(),
            conclusion.phase(),
            conclusion.action(),
            conclusion.handoffTo()
        );
        var conversation = context.conversation();
        var state = context.state();
        switch (conclusion.phase()) {
            case "ANSWER" -> {
                conversation.write(agent.name(), conclusion.answer());
                return new Inference() {
                    /// @formatter:off
                    @Override public String agent() { return agent.name(); }
                    @Override public String reasoning() { return conclusion.reasoning(); }
                    @Override public double confidence() { return 1.0; }
                    @Override public String answer() { return conclusion.answer(); }
                    /// @formatter:on
                };
            }
            case "ACT" -> {
                logger.trace("\n--- conclusion: {}\n--- own: {}\n--- all: {}",
                    conclusion.action(),
                    this.own(agent),
                    this.all());
                var output = agent.topics().stream()
                    .flatMap(x -> x.actions().stream())
                    .filter(x -> x.name().equals(conclusion.action()))
                    .findFirst()
                    .orElseThrow()
                    .execute(state.snapshot());
                output.forEach((k, v) -> {
                    state.write("action_results." + conclusion.action() + "." + k, v);
                });
                return this.recursiveInfer(context, ++depth);
            }
            case "HANDOFF" -> {
                state.write("last_handoff_from", agent.name());
                state.write("last_handoff_to", conclusion.handoffTo());
                return this.recursiveInfer(context, ++depth);
            }
            case "EVOLVE" -> {
                this.evolutionEngine.upgrade(context, agent);
                this.evolutionEngine.consolidate();
                return this.recursiveInfer(context, ++depth);
            }
            default -> throw new IllegalStateException();
        }
    }

    private Set<String> own(Agent agent) {
        return agent.topics().stream()
            .flatMap(x -> x.actions().stream())
            .map(Action::name)
            .collect(Collectors.toSet());
    }

    private Set<String> all() {
        return this.actionRepository.findAll().stream()
            .map(Action::name)
            .collect(Collectors.toSet());
    }
}
