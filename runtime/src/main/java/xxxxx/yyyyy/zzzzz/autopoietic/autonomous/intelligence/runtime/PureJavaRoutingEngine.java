package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Agent;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Context;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Intelligence;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.RoutingEngine;

@ApplicationScoped
public class PureJavaRoutingEngine implements RoutingEngine {
    private static final Logger logger = LoggerFactory.getLogger(PureJavaRoutingEngine.class);
    private static final String START_AGENT = "StartAgent";
    private final Intelligence intelligence;
    private final PromptAssembler promptAssembler;
    private final Repository<Agent> agentRepository;

    @Inject
    public PureJavaRoutingEngine(Intelligence intelligence,
                                 PromptAssembler promptAssembler,
                                 Repository<Agent> agentRepository) {
        this.intelligence = intelligence;
        this.promptAssembler = promptAssembler;
        this.agentRepository = agentRepository;
    }

    @Override
    public Agent route(Context context) {
        String prompt = this.promptAssembler.routing(context);
        Direction direction = this.intelligence.reason(prompt, Direction.class);
        logger.debug("[INTELLIGENCE] Reasoning: ({}) [{}], SelectedAgent: {}",
                direction.confidence(),
                direction.reasoning(),
                direction.agent()
        );
        Agent agent = this.agentRepository.find(direction.agent());
        if (agent == null) {
            /// log?
            return this.agentRepository.find(START_AGENT);
        }
        return agent;
    }
}
