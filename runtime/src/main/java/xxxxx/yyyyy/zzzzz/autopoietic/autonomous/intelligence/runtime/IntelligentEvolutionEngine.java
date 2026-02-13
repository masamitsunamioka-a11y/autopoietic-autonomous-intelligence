package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Util.isEmpty;

@ApplicationScoped
public class IntelligentEvolutionEngine implements EvolutionEngine {
    private static final Logger logger = LoggerFactory.getLogger(IntelligentEvolutionEngine.class);
    private final Intelligence intelligence;
    private final PromptBuilder promptBuilder;
    private final Repository<Agent> agentRepository;
    private final Repository<Topic> topicRepository;
    private final Repository<Action<?>> actionRepository;

    @Inject
    public IntelligentEvolutionEngine(Intelligence intelligence,
                                      PromptBuilder promptBuilder,
                                      Repository<Agent> agentRepository,
                                      Repository<Topic> topicRepository,
                                      Repository<Action<?>> actionRepository) {
        this.intelligence = intelligence;
        this.promptBuilder = promptBuilder;
        this.agentRepository = agentRepository;
        this.topicRepository = topicRepository;
        this.actionRepository = actionRepository;
    }

    @Override
    public void upgrade(String input, Conversation conversation, State state, Agent agent) {
        logger.debug("[EVOLUTION] >> Starting evolution for agent: '{}'", agent.name());
        String prompt = this.buildPrompt(input, conversation, state, agent);
        Upgrade upgrade = this.intelligence.reason(prompt, Upgrade.class);
        logger.debug("[EVOLUTION] >> Thought complete. Plan: instruction_update={}, topics={}, agents={}",
                !isEmpty(upgrade.newInstructions()),
                upgrade.newTopics().size(),
                upgrade.newAgents().size());
        this.applyEvolution(agent, upgrade);
    }

    @Override
    public void consolidate() {
        String prompt = this.promptBuilder.consolidation()
                .guardrails()
                .agents()
                .topics()
                .actions()
                .render();
        Consolidation consolidation = this.intelligence.reason(prompt, Consolidation.class);
        logger.debug("[CONSOLIDATION] >> Thought complete. {}}", consolidation.toString());
        Set<String> agentsToPurge = consolidation.consolidatedAgents().stream()
                .flatMap(x -> x.consolidants().stream())
                .collect(Collectors.toSet());
        agentsToPurge.forEach(this.agentRepository::remove);
        consolidation.consolidatedAgents().stream()
                .map(Consolidation.ConsolidatedAgent::consolidated)
                .forEach(x -> this.agentRepository.store(x.name(), x.rawJson()));
        Set<String> topicsToPurge = consolidation.consolidatedTopics().stream()
                .flatMap(x -> x.consolidants().stream())
                .collect(Collectors.toSet());
        topicsToPurge.forEach(this.topicRepository::remove);
        consolidation.consolidatedTopics().stream()
                .map(Consolidation.ConsolidatedTopic::consolidated)
                .forEach(x -> this.topicRepository.store(x.name(), x.rawJson()));
        logger.info("[CONSOLIDATION] >> Refined intelligence. Purged {} agents and {} topics.",
                agentsToPurge.size(), topicsToPurge.size());
    }

    private void applyEvolution(Agent agent, Upgrade upgrade) {
        upgrade.newTopics().forEach(x ->
                x.actions().forEach(y -> {
                    logger.debug("[EVOLUTION] >> Registering new action placeholder: '{}'", y);
                    this.actionRepository.store(y, null);
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
            this.agentRepository.store(agent.name(), agent.toString());
        }
        logger.debug("[EVOLUTION] << Evolution successfully applied for agent: '{}'", agent.name());
    }

    private void linkNewActionToRelatedTopics(String name, Agent agent) {
        Action<?> newAction = this.actionRepository.find(name);
        if (newAction == null) {
            throw new UnsupportedOperationException("[EVOLUTION_STOPPED] Action [" + name + "] has no Java implementation.");
        }
        List<Topic> relatedTopics = agent.topics();
        if (relatedTopics == null || relatedTopics.isEmpty()) {
            throw new IllegalStateException("Agent [" + agent.name() + "] has no assigned topics to link the action.");
        }
        relatedTopics.forEach(x -> {
            logger.debug("[EVOLUTION] >> Linking action '{}' to topic '{}'", name, x.name());
            List<String> actionNames = Stream.concat(
                    x.actions().stream().map(Action::name),
                    Stream.of(name)
            ).distinct().toList();
            Gson gson = new GsonBuilder().create();
            Map<String, Object> attributes = gson.fromJson(
                    x.toString(),
                    new TypeToken<Map<String, Object>>() {
                    }.getType()
            );
            attributes.put("actions", actionNames);
            this.topicRepository.store(x.name(), gson.toJson(attributes));
        });
    }

    private String buildPrompt(String input, Conversation conversation, State state, Agent agent) {
        return this.promptBuilder.upgrade()
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
