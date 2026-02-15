package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.*;

import java.util.Map;

@ApplicationScoped
public class PureJavaInferenceEngine implements InferenceEngine {
    private static final Logger logger = LoggerFactory.getLogger(PureJavaInferenceEngine.class);
    private final Intelligence intelligence;
    private final PromptAssembler promptAssembler;
    private final RoutingEngine routingEngine;
    private final EvolutionEngine evolutionEngine;

    @Inject
    public PureJavaInferenceEngine(Intelligence intelligence,
                                   PromptAssembler promptAssembler,
                                   RoutingEngine routingEngine,
                                   EvolutionEngine evolutionEngine) {
        this.intelligence = intelligence;
        this.promptAssembler = promptAssembler;
        this.routingEngine = routingEngine;
        this.evolutionEngine = evolutionEngine;
    }

    @Override
    public Inference infer(String input, Conversation conversation, State state) {
        return this.recursiveInfer(input, conversation, state, 0);
    }

    private Inference recursiveInfer(String input, Conversation conversation, State state, int depth) {
        if (depth >= 1000) {
            throw new RuntimeException("Max recursion depth reached in InferenceEngine.");
        }
        Agent agent = this.routingEngine.route(input, conversation, state);
        String prompt = this.promptAssembler.inference(input, conversation, state, agent);
        Conclusion conclusion = this.intelligence.reason(prompt, Conclusion.class);
        logger.debug("[INTELLIGENCE] Reasoning: ({}) [{}], Phase: {}, Action: {}, Handoff: {}",
                conclusion.confidence(),
                conclusion.reasoning(),
                conclusion.phase(),
                conclusion.action(),
                conclusion.handoffTo()
        );
        switch (conclusion.phase()) {
            case "ANSWER" -> {
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
                var action = agent.topics().stream()
                        .flatMap(x -> x.actions().stream())
                        .filter(x -> x.name().equalsIgnoreCase(conclusion.action()))
                        .findFirst();
                if (action.isEmpty()) {
                    /// log?
                    conversation.write("system", "[SYSTEM_WARNING] Action '" + conclusion.action() + "' not found in your current topics.");
                    return this.recursiveInfer(input, conversation, state, ++depth);
                }
                Map<String, Object> output = action.get().execute(state.snapshot());
                /// state.write("message", output.get("message"));
                conversation.write("system", "Action '" + conclusion.action() + "' executed: " + output.get("message"));
                return this.recursiveInfer(input, conversation, state, ++depth);
            }
            case "HANDOFF" -> {
                String target = conclusion.handoffTo();
                if (target == null || target.isBlank()) {
                    throw new IllegalStateException(
                            "Handoff phase was selected but no target agent was specified.");
                }
                conversation.write("system", "Handoff to: " + target);
                return this.recursiveInfer(input, conversation, state, ++depth);
            }
            case "EVOLVE" -> {
                this.evolutionEngine.upgrade(input, conversation, state, agent);
                this.evolutionEngine.consolidate();
                return this.recursiveInfer(input, conversation, state, ++depth);
            }
            default -> throw new IllegalStateException(
                    "Unexpected phase: " + conclusion.phase());
        }
    }
}
