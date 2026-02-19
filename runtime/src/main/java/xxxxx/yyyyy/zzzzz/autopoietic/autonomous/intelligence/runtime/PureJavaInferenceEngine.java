package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.*;

import java.util.List;
import java.util.Map;

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
        Agent agent = this.routingEngine.route(context);
        String prompt = this.promptAssembler.inference(context, agent);
        Conclusion conclusion = this.intelligence.reason(prompt, Conclusion.class);
        logger.debug("[INTELLIGENCE] Reasoning: ({}) [{}], Phase: {}, Action: {}, Handoff: {}",
            conclusion.confidence(),
            conclusion.reasoning(),
            conclusion.phase(),
            conclusion.action(),
            conclusion.handoffTo()
        );
        Conversation conversation = context.conversation();
        State state = context.state();
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
                List<String> allActions = this.actionRepository.findAll().stream()
                    .map(Action::name)
                    .toList();
                List<String> availables = agent.topics().stream()
                    .flatMap(x -> x.actions().stream())
                    .map(Action::name)
                    .toList();
                logger.trace("""
                    Conclusion : [{}]
                    Availables : {} in {}
                    AllActions : {}
                    """, conclusion.action(), availables, agent.name(), allActions);
                Action action = agent.topics().stream()
                    .flatMap(x -> x.actions().stream())
                    .filter(x -> x.name().equals(conclusion.action()))
                    .findFirst()
                    .orElseThrow();
                logger.info("Executing Action: [{}]", action.name());
                Map<String, Object> output = action.execute(state.snapshot());
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
}
