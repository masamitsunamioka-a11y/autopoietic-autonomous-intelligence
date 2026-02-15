package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.*;

@ApplicationScoped
public class IntelligentRoutingEngine implements RoutingEngine {
    private static final Logger logger = LoggerFactory.getLogger(IntelligentRoutingEngine.class);
    private static final String START_AGENT = "StartAgent";
    private final Intelligence intelligence;
    private final PromptBuilder promptBuilder;
    private final Repository<Agent> agentRepository;

    @Inject
    public IntelligentRoutingEngine(Intelligence intelligence,
                                    PromptBuilder promptBuilder,
                                    Repository<Agent> agentRepository) {
        this.intelligence = intelligence;
        this.promptBuilder = promptBuilder;
        this.agentRepository = agentRepository;
    }

    @Override
    public Agent route(String input, Conversation conversation, State state) {
        logger.debug("[ROUTING] >> Starting agent resolution.");
        String hint = String.class.cast(state.read("HANDOFF_HINT"));
        if (hint != null) {
            Agent agent = this.agentRepository.find(hint);
            if (agent != null) {
                logger.debug("[ROUTING] >> Static match: Hint '{}' -> Agent '{}'", hint, agent.name());
                return agent;
            }
            logger.debug("[ROUTING] !! Static miss: Hint '{}' not found. Using StartAgent.", hint);
            return this.agentRepository.find(START_AGENT);
        }
        String prompt = this.promptBuilder.routing(input, conversation, state);
        Direction direction = this.intelligence.reason(prompt, Direction.class);
        logger.debug("[ROUTING] >> Dynamic decision: '{}' (Confidence: {}, Reasoning: {})", direction.agent(), direction.confidence(), direction.reasoning());
        Agent agent = this.agentRepository.find(direction.agent());
        if (agent == null) {
            logger.debug("[ROUTING] !! Fallback: Target '{}' not found. Using StartAgent.", direction.agent());
            return this.agentRepository.find(START_AGENT);
        }
        logger.debug("[ROUTING] << Resolved: '{}'", agent.name());
        return agent;
    }
}
