package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

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
        return this.recursiveInfer(input, conversation, state, new Context(0, new ArrayDeque<>()));
    }

    private Inference recursiveInfer(String input, Conversation conversation, State state, Context context) {
        logger.debug("[INFERENCE] >> Phase start (Depth: {})", context.depth());
        if (context.isExceeded()) {
            throw new RuntimeException("Max recursion depth reached in InferenceEngine.");
        }
        Agent agent = this.routingEngine.route(input, conversation, state);
        if (context.isLooping()) {
            logger.debug("[INFERENCE] !! Loop detected in history: {}. Forcing evolution.", context.inferenceHistory());
            return this.forceEvolution(input, conversation, state, context);
        }
        String prompt = this.buildPrompt(input, conversation, state, agent);
        Conclusion conclusion = this.intelligence.reason(prompt, Conclusion.class);
        logger.debug("[INFERENCE] >> Thought: [{}] - {}", conclusion.phase(), conclusion.thought());
        Context nextContext = context.next(conclusion.phase().name());
        switch (conclusion.phase()) {
            case ANSWER -> {
                logger.debug("[INFERENCE] << Answer phase reached. Chain completed.");
                return new Inference() {
                    /// @formatter:off
                    @Override public String agent() { return agent.name(); }
                    @Override public String phase() { return conclusion.phase().name(); }
                    @Override public String thought() { return conclusion.thought(); }
                    @Override public String content() { return conclusion.answer(); }
                    @Override public String action() { return conclusion.action(); }
                    @Override public double confidence() { return 1.0; }
                    /// @formatter:on
                };
            }
            case ACT -> {
                logger.debug("[INFERENCE] >> Executing action: '{}'", conclusion.action());
                var action = agent.topics().stream()
                        .flatMap(x -> x.actions().stream())
                        .filter(x -> x.name().equalsIgnoreCase(conclusion.action()))
                        .findFirst();
                if (action.isEmpty()) {
                    logger.warn("[INFERENCE] !! Action '{}' not found in current agent context.", conclusion.action());
                    conversation.write("system", "[SYSTEM_WARNING] Action '" + conclusion.action() + "' not found in your current topics.");
                    return this.recursiveInfer(input, conversation, state, nextContext);
                }
                Output output = action.get().execute(state.snapshot());
                output.updates().forEach(state::write);
                conversation.write("system", "Action '" + conclusion.action() + "' executed: " + output.message());
                logger.debug("[INFERENCE] >> Action execution finished. Output length: {}", output.message() == null ? 0 : output.message().length());
                return this.recursiveInfer(input, conversation, state, nextContext);
            }
            case HANDOFF -> {
                String target = conclusion.handoffTo();
                if (target == null || target.isBlank()) {
                    throw new IllegalStateException("[INFERENCE_ERROR] Handoff phase was selected but no target agent was specified.");
                }
                logger.debug("[INFERENCE] >> Handoff requested to: '{}'", target);
                conversation.write("system", "Handoff to: " + target);
                state.write("HANDOFF_HINT", target);
                return this.recursiveInfer(input, conversation, state, nextContext);
            }
            case EVOLVE -> {
                logger.debug("[INFERENCE] >> Diverging to Evolution for agent: '{}'", agent.name());
                this.evolutionEngine.upgrade(input, conversation, state, agent);
                this.evolutionEngine.consolidate();
                return this.recursiveInfer(input, conversation, state, nextContext);
            }
            default -> throw new IllegalStateException("Unexpected phase: " + conclusion.phase());
        }
    }

    private Inference forceEvolution(String input, Conversation conversation, State state, Context context) {
        logger.debug("[INFERENCE] >> Initializing mandatory evolution.");
        Agent agent = this.routingEngine.route(input, conversation, state);
        conversation.write("system", "[SYSTEM WARNING] Inferring loop detected. Evolution is mandatory to break the cycle.");
        this.evolutionEngine.upgrade(input, conversation, state, agent);
        this.evolutionEngine.consolidate();
        logger.debug("[INFERENCE] << Evolution complete. Restarting inference.");
        return this.recursiveInfer(input, conversation, state, new Context(context.depth() + 1, new ArrayDeque<>()));
    }

    private String buildPrompt(String input, Conversation conversation, State state, Agent agent) {
        return this.promptBuilder.inference()
                .guardrails()
                .input(input)
                .conversation(conversation)
                .state(state)
                .actions()
                .bind("agentInstructions", agent.instructions())
                .render();
    }

    /// @formatter:off
    private static record Context(int depth, Deque<String> inferenceHistory) {
        private static final int MAX_DEPTH = 50;
        private static final int LOOP_THRESHOLD = 5;
        public Context {
            Objects.requireNonNull(inferenceHistory, "inferenceHistory must not be null.");
        }
        public boolean isExceeded() {
            return this.depth >= MAX_DEPTH;
        }
        public Context next(String phase) {
            Deque<String> history = new ArrayDeque<>(this.inferenceHistory);
            history.push(phase);
            if (history.size() > LOOP_THRESHOLD) {
                history.removeLast();
            }
            return new Context(this.depth + 1, history);
        }
        public boolean isLooping() {
            if (this.inferenceHistory.size() < LOOP_THRESHOLD) {
                return false;
            }
            String latest = this.inferenceHistory.peek();
            return this.inferenceHistory.stream()
                    .allMatch(x -> x.equals(latest));
        }
    }
    /// @formatter:on
}
