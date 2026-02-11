package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

@ApplicationScoped
public class ThinkableReasoningEngine implements ReasoningEngine {
    private static final Logger logger = LoggerFactory.getLogger(ThinkableReasoningEngine.class);
    private final RoutingEngine routingEngine;
    private final EvolutionEngine evolutionEngine;
    private final Repository<Action<?>> actionRepository;
    private final PromptBuilder promptBuilder;
    private final Thinker thinker;

    @Inject
    public ThinkableReasoningEngine(RoutingEngine routingEngine,
                                    EvolutionEngine evolutionEngine,
                                    Repository<Action<?>> actionRepository,
                                    PromptBuilder promptBuilder) {
        this.routingEngine = routingEngine;
        this.evolutionEngine = evolutionEngine;
        this.actionRepository = actionRepository;
        this.promptBuilder = promptBuilder;
        this.thinker = new Thinker();
    }

    @Override
    public Inference infer(String input, Conversation conversation, State state) {
        logger.debug("[REASONING_START] Input: {}, Conv: {}, State: {}", input, conversation.snapshot(), state.snapshot());
        try {
            return this.recursiveInfer(input, conversation, state, new Context(0, new ArrayDeque<>()));
        } finally {
            logger.debug("[REASONING_COMPLETE] Input: {}", input);
        }
    }

    private Inference recursiveInfer(String input, Conversation conversation, State state, Context context) {
        logger.debug("[RECURSIVE_START] Depth: {}, Input: {}, Conv: {}, State: {}", context.depth(), input, conversation.snapshot(), state.snapshot());
        if (context.isExceeded()) {
            throw new RuntimeException("Max recursion depth reached in ReasoningEngine.");
        }
        Agent agent = this.routingEngine.route(input, conversation, state);
        logger.debug("[ROUTED_AGENT] Agent: {}", agent.name());
        if (context.isLooping()) {
            logger.debug("[LOOP_DETECTED] Forcing evolution for Agent: {}", agent.name());
            return this.forceEvolution(input, conversation, state, context);
        }
        String prompt = this.buildPrompt(input, conversation, state, agent);
        Conclusion conclusion = this.thinker.think(prompt, Conclusion.class);
        logger.debug("[THOUGHT_RESULT] Phase: {}, Thought: {}", conclusion.phase(), conclusion.thought());
        Context nextContext = context.next(conclusion.phase().name());
        switch (conclusion.phase()) {
            case ANSWER -> {
                logger.debug("[PHASE_ANSWER] Result: {}", conclusion.answer());
                return new Inference() {
                    /// @formatter:off
                    @Override public String agentName() { return agent.name(); }
                    @Override public String phase() { return conclusion.phase().name(); }
                    @Override public String thought() { return conclusion.thought(); }
                    @Override public String content() { return conclusion.answer(); }
                    @Override public String action() { return conclusion.action(); }
                    @Override public double confidence() { return 1.0; }
                    /// @formatter:on
                };
            }
            case ACT -> {
                logger.debug("[PHASE_ACT] Target Action: {}", conclusion.action());
                var actionOptional = this.actionRepository.findAll().stream()
                        .filter(x -> x.name().equalsIgnoreCase(conclusion.action()))
                        .findFirst()
                        .or(() -> this.actionRepository.findAll().stream()
                                .filter(x -> x.getClass().getSimpleName().equalsIgnoreCase(conclusion.action()))
                                .findFirst());
                if (actionOptional.isEmpty()) {
                    logger.warn("[MISSING_ACTION] Action [{}] does not exist in the repository. Triggering evolution.", conclusion.action());
                    conversation.write("system", "[SYSTEM_WARNING] Action '" + conclusion.action() + "' not found. You must create its Java implementation via 'phase: EVOLVE' before execution.");
                    return this.recursiveInfer(input, conversation, state, nextContext);
                }
                Output output = actionOptional.get().execute(state.snapshot());
                output.updates().forEach(state::write);
                conversation.write("system", "Action '" + conclusion.action() + "' executed: " + output.message());
                logger.debug("[ACTION_EXECUTED] Output: {}, New State: {}", output.message(), state.snapshot());
                return this.recursiveInfer(input, conversation, state, nextContext);
            }
            case HANDOFF -> {
                String target = conclusion.handoffTo();
                if (target == null || target.isBlank()) {
                    throw new IllegalStateException("[REASONING_ERROR] Handoff phase was selected but no target agent was specified.");
                }
                logger.debug("[HANDOFF_TRIGGER] Target: {}", target);
                conversation.write("system", "Handoff to: " + target);
                state.write("HANDOFF_HINT", target);
                return this.recursiveInfer(input, conversation, state, nextContext);
            }
            case EVOLVE -> {
                logger.debug("[PHASE_EVOLVE] Triggering evolution for: {}", agent.name());
                this.evolutionEngine.evolve(input, conversation, state, agent);
                return this.recursiveInfer(input, conversation, state, nextContext);
            }
            default -> throw new IllegalStateException("Unexpected phase: " + conclusion.phase());
        }
    }

    private Inference forceEvolution(String input, Conversation conversation, State state, Context context) {
        logger.debug("[FORCE_EVOLUTION_START] Input: {}, Conv: {}, State: {}", input, conversation.snapshot(), state.snapshot());
        Agent agent = this.routingEngine.route(input, conversation, state);
        logger.debug("[FORCE_EVOLUTION_TARGET] Agent to evolve: {}", agent.name());
        conversation.write("system", "[SYSTEM WARNING] Reasoning loop detected. Evolution is mandatory to break the cycle.");
        this.evolutionEngine.evolve(input, conversation, state, agent);
        logger.debug("[FORCE_EVOLUTION_EXECUTED] Evolution complete for Agent: {}. Restarting inference.", agent.name());
        return this.recursiveInfer(input, conversation, state, new Context(context.depth() + 1, new ArrayDeque<>()));
    }

    private String buildPrompt(String input, Conversation conversation, State state, Agent agent) {
        return this.promptBuilder.reasoning()
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
        private static final int MAX_DEPTH = 10;
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
