package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.*;

import java.util.Map;

@ApplicationScoped
public class IntelligentInferenceEngine implements InferenceEngine {
    private static final Logger logger = LoggerFactory.getLogger(IntelligentInferenceEngine.class);
    private final Intelligence intelligence;
    private final PromptBuilder promptBuilder;
    private final RoutingEngine routingEngine;
    private final EvolutionEngine evolutionEngine;

    @Inject
    public IntelligentInferenceEngine(Intelligence intelligence,
                                      PromptBuilder promptBuilder,
                                      RoutingEngine routingEngine,
                                      EvolutionEngine evolutionEngine) {
        this.intelligence = intelligence;
        this.promptBuilder = promptBuilder;
        this.routingEngine = routingEngine;
        this.evolutionEngine = evolutionEngine;
    }

    @Override
    public Inference infer(String input, Conversation conversation, State state) {
        logger.debug("[INFERENCE] >> Initiating inference chain.");
        return this.recursiveInfer(input, conversation, state, 0);
    }

    private Inference recursiveInfer(String input, Conversation conversation, State state, int depth) {
        logger.debug("[INFERENCE] >> Phase start (Depth: {})", depth);
        if (depth >= 1000) {
            throw new RuntimeException("Max recursion depth reached in InferenceEngine.");
        }
        Agent agent = this.routingEngine.route(input, conversation, state);
        String prompt = this.promptBuilder.inference(input, conversation, state, agent);
        Conclusion conclusion = this.intelligence.reason(prompt, Conclusion.class);
        logger.debug("[INFERENCE] >> Thought: [{}] - {}", conclusion.phase(), conclusion.reasoning());
        switch (conclusion.phase()) {
            case "ANSWER" -> {
                logger.debug("[INFERENCE] << Answer phase reached. Chain completed.");
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
                logger.debug("[INFERENCE] >> Executing action: '{}'", conclusion.action());
                var action = agent.topics().stream()
                        .flatMap(x -> x.actions().stream())
                        .filter(x -> x.name().equalsIgnoreCase(conclusion.action()))
                        .findFirst();
                if (action.isEmpty()) {
                    logger.warn("[INFERENCE] !! Action '{}' not found in current agent context.", conclusion.action());
                    conversation.write("system", "[SYSTEM_WARNING] Action '" + conclusion.action() + "' not found in your current topics.");
                    return this.recursiveInfer(input, conversation, state, ++depth);
                }
                Map<String, Object> output = action.get().execute(state.snapshot());
                /// state.write("message", output.get("message"));
                conversation.write("system", "Action '" + conclusion.action() + "' executed: " + output.get("message"));
                logger.debug("[INFERENCE] >> Action execution finished.");
                return this.recursiveInfer(input, conversation, state, ++depth);
            }
            case "HANDOFF" -> {
                String target = conclusion.handoffTo();
                if (target == null || target.isBlank()) {
                    throw new IllegalStateException("[INFERENCE_ERROR] Handoff phase was selected but no target agent was specified.");
                }
                logger.debug("[INFERENCE] >> Handoff requested to: '{}'", target);
                conversation.write("system", "Handoff to: " + target);
                state.write("HANDOFF_HINT", target);
                return this.recursiveInfer(input, conversation, state, ++depth);
            }
            case "EVOLVE" -> {
                logger.debug("[INFERENCE] >> Diverging to Evolution for agent: '{}'", agent.name());
                this.evolutionEngine.upgrade(input, conversation, state, agent);
                this.evolutionEngine.consolidate();
                return this.recursiveInfer(input, conversation, state, ++depth);
            }
            default -> throw new IllegalStateException("Unexpected phase: " + conclusion.phase());
        }
    }
}
