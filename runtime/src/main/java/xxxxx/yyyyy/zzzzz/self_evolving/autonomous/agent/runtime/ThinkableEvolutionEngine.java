package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime.Util.isEmpty;

@ApplicationScoped
public class ThinkableEvolutionEngine implements EvolutionEngine {
    private static final Logger logger = LoggerFactory.getLogger(ThinkableEvolutionEngine.class);
    private final Repository<Agent> agentRepository;
    private final Repository<Topic> topicRepository;
    private final Repository<Action<?>> actionRepository;
    private final PromptBuilder promptBuilder;
    private final Thinker thinker;

    @Inject
    public ThinkableEvolutionEngine(Repository<Agent> agentRepository,
                                    Repository<Topic> topicRepository,
                                    Repository<Action<?>> actionRepository,
                                    PromptBuilder promptBuilder) {
        this.agentRepository = agentRepository;
        this.topicRepository = topicRepository;
        this.actionRepository = actionRepository;
        this.promptBuilder = promptBuilder;
        this.thinker = new Thinker();
    }

    @Override
    public void evolve(String input, Conversation conversation, State state, Agent agent) {
        logger.debug("[EVOLUTION] >> Starting evolution for agent: '{}'", agent.name());
        String prompt = this.buildPrompt(input, conversation, state, agent);
        Upgrade upgrade = this.thinker.think(prompt, Upgrade.class);
        logger.debug("[EVOLUTION] >> Thought complete. Plan: instruction_update={}, topics={}, agents={}, actions={}",
                upgrade.newInstructions() != null && !upgrade.newInstructions().isBlank(),
                upgrade.newTopics().size(),
                upgrade.newAgents().size(),
                upgrade.newTopics().stream().mapToLong(x -> x.actions().size()).sum());
        this.applyEvolution(agent, upgrade);
    }

    private void applyEvolution(Agent agent, Upgrade upgrade) {
        upgrade.newTopics().forEach(x ->
                x.actions().forEach(y -> {
                    logger.debug("[EVOLUTION] >> Registering new action placeholder: '{}'", y);
                    this.actionRepository.store(y, (Action<?>) null);
                    this.linkNewActionToRelatedTopics(y, agent);
                })
        );
        upgrade.newTopics().forEach(x -> {
            logger.debug("[EVOLUTION] >> Spawning new topic: '{}'", x.name());
            this.topicRepository.store(x.name(), x.rawJson());
        });
        upgrade.newAgents().forEach(x -> {
            logger.debug("[EVOLUTION] >> Spawning new agent: '{}'", x.name());
            this.agentRepository.store(x.name(), x.rawJson());
        });
        if (!isEmpty(upgrade.newInstructions())) {
            logger.debug("[EVOLUTION] >> Updating instructions for agent: '{}' (Length: {})",
                    agent.name(), upgrade.newInstructions().length());
            agent.instructions(upgrade.newInstructions());
            this.agentRepository.store(agent.name(), agent);
        }
        logger.debug("[EVOLUTION] << Evolution successfully applied for agent: '{}'", agent.name());
    }

    private void linkNewActionToRelatedTopics(String name, Agent agent) {
        Action<?> newAction = this.actionRepository.findByName(name);
        if (newAction == null) {
            throw new UnsupportedOperationException("[EVOLUTION_STOPPED] Action [" + name + "] has no Java implementation.");
        }
        List<Topic> relatedTopics = agent.topics();
        if (relatedTopics == null || relatedTopics.isEmpty()) {
            throw new IllegalStateException("Agent [" + agent.name() + "] has no assigned topics to link the action.");
        }
        relatedTopics.forEach(topic -> {
            logger.debug("[EVOLUTION] >> Linking action '{}' to specialized topic '{}'", name, topic.name());
            List<String> actions = Stream.concat(
                    Optional.ofNullable(topic.actions()).stream().flatMap(List::stream),
                    Stream.of(name)
            ).distinct().toList();
            this.topicRepository.store(topic.name(), new Topic() {
                /// @formatter:off
                @Override public String name() { return topic.name(); }
                @Override public String label() { return topic.label(); }
                @Override public String description() { return topic.description(); }
                @Override public String instructions() { return topic.instructions(); }
                @Override public void instructions(String i) { topic.instructions(i); }
                @Override public List<String> actions() { return actions; }
                /// @formatter:on
            });
        });
    }

    private String buildPrompt(String input, Conversation conversation, State state, Agent agent) {
        return this.promptBuilder.evolution()
                .guardrails()
                .input(input)
                .conversation(conversation)
                .state(state)
                .agents()
                .topics()
                .actions()
                .bind("agentName", agent.name())
                .bind("agentInstructions", agent.instructions())
                .render();
    }
}
