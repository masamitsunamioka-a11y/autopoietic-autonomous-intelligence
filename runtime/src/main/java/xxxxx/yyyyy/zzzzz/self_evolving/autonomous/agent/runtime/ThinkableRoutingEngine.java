package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Agent;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Conversation;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.RoutingEngine;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.State;

@ApplicationScoped
public class ThinkableRoutingEngine implements RoutingEngine {
    private static final Logger logger = LoggerFactory.getLogger(ThinkableRoutingEngine.class);
    private static final String START_AGENT = "StartAgent";
    private final Repository<Agent> agentRepository;
    private final PromptBuilder promptBuilder;
    private final Thinker thinker;

    @Inject
    public ThinkableRoutingEngine(Repository<Agent> agentRepository,
                                  PromptBuilder promptBuilder) {
        this.agentRepository = agentRepository;
        this.promptBuilder = promptBuilder;
        this.thinker = new Thinker();
    }

    @Override
    public Agent route(String input, Conversation conversation, State state) {
        logger.debug("[ROUTING_START] Input: {}, Conv: {}, State: {}", input, conversation.snapshot(), state.snapshot());
        String hint = String.class.cast(state.read("HANDOFF_HINT"));
        if (hint != null) {
            Agent agent = this.agentRepository.findByName(hint);
            if (agent != null) {
                logger.debug("[ROUTING_STATIC] Hint matched: {}, Agent: {}", hint, agent.name());
                return agent;
            }
            logger.debug("[ROUTING_STATIC_NOT_FOUND] Hint '{}' not found, resolving to StartAgent", hint);
            return this.agentRepository.findByName(START_AGENT);
        }
        String prompt = this.buildPrompt(input, conversation, state);
        Direction direction = this.thinker.think(prompt, Direction.class);
        logger.debug("[ROUTING_THINK] Decision: {}, Confidence: {}, Reasoning: {}", direction.agent(), direction.confidence(), direction.reasoning());
        Agent agent = this.agentRepository.findByName(direction.agent());
        if (agent == null) {
            logger.debug("[ROUTING_FALLBACK] Selected agent '{}' not found, resolving to StartAgent", direction.agent());
            return this.agentRepository.findByName(START_AGENT);
        }
        logger.debug("[ROUTING_COMPLETE] Final Target: {}", agent.name());
        return agent;
    }

    private String buildPrompt(String input, Conversation conversation, State state) {
        return this.promptBuilder.routing()
                .guardrails()
                .input(input)
                .conversation(conversation)
                .state(state)
                .agents()
                .render();
    }
}
