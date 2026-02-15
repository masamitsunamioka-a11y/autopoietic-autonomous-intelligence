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
    private final Repository<Action> actionRepository;

    @Inject
    public IntelligentEvolutionEngine(Intelligence intelligence,
                                      PromptBuilder promptBuilder,
                                      Repository<Agent> agentRepository,
                                      Repository<Topic> topicRepository,
                                      Repository<Action> actionRepository) {
        this.intelligence = intelligence;
        this.promptBuilder = promptBuilder;
        this.agentRepository = agentRepository;
        this.topicRepository = topicRepository;
        this.actionRepository = actionRepository;
    }

    @Override
    public void upgrade(String input, Conversation conversation, State state, Agent agent) {
        logger.debug("[UPGRADE] >> Starting evolution for agent: '{}'", agent.name());
        String prompt = this.promptBuilder.upgrade(input, conversation, state, agent);
        Upgrade upgrade = this.intelligence.reason(prompt, Upgrade.class);
        logger.debug("[UPGRADE] >> Thought complete. Plan: instruction_update={}, topics={}, agents={}",
                !isEmpty(upgrade.newInstructions()),
                upgrade.newTopics().size(),
                upgrade.newAgents().size());
        upgrade.newActions().forEach(x -> {
            logger.debug("[UPGRADE] >> Spawning new action: '{}'", x.name());
            this.actionRepository.store(x.name(), x.rawJson());
        });
        upgrade.newTopics().forEach(x -> {
            logger.debug("[UPGRADE] >> Spawning new topic: '{}'", x.name());
            this.topicRepository.store(x.name(), x.rawJson());
            upgrade.newActions().stream()
                    .filter(y -> y.relatedTopics().contains(x.name()))
                    .forEach(y -> this.linkActionToTopic(y.name(), x.name()));
        });
        upgrade.newAgents().forEach(x -> {
            logger.debug("[UPGRADE] >> Spawning new agent: '{}'", x.name());
            this.agentRepository.store(x.name(), x.rawJson());
        });
        if (!isEmpty(upgrade.newInstructions())) {
            logger.debug("[UPGRADE] >> Updating instructions for agent: '{}' (Length: {})",
                    agent.name(), upgrade.newInstructions().length());
            agent.instructions(upgrade.newInstructions());
            this.agentRepository.store(agent.name(), agent.toString());
        }
        logger.debug("[UPGRADE] << Evolution successfully applied for agent: '{}'", agent.name());
    }

    @Override
    public void consolidate() {
        String prompt = this.promptBuilder.consolidation();
        Consolidation consolidation = this.intelligence.reason(prompt, Consolidation.class);
        logger.debug("[CONSOLIDATION] >> Thought complete. {}}", consolidation.toString());
        /// Purge
        Set<String> agentsToPurge = consolidation.consolidatedAgents().stream()
                .flatMap(x -> x.consolidants().stream())
                .collect(Collectors.toSet());
        agentsToPurge.forEach(this.agentRepository::remove);
        Set<String> topicsToPurge = consolidation.consolidatedTopics().stream()
                .flatMap(x -> x.consolidants().stream())
                .collect(Collectors.toSet());
        topicsToPurge.forEach(this.topicRepository::remove);
        /// Merge
        consolidation.consolidatedTopics().stream()
                .map(Consolidation.ConsolidatedTopic::consolidated)
                .forEach(x -> this.topicRepository.store(x.name(), x.rawJson()));
        consolidation.consolidatedAgents().stream()
                .map(Consolidation.ConsolidatedAgent::consolidated)
                .forEach(x -> this.agentRepository.store(x.name(), x.rawJson()));
        logger.info("[CONSOLIDATION] >> Refined intelligence. Purged {} agents and {} topics.",
                agentsToPurge.size(), topicsToPurge.size());
    }

    private void linkActionToTopic(String actionName, String topicName) {
        Topic topic = this.topicRepository.find(topicName);
        List<String> actionNames = Stream.concat(
                topic.actions().stream().map(Action::name),
                Stream.of(actionName)
        ).distinct().toList();
        logger.debug("[UPGRADE] >> Syncing action '{}' to topic '{}'", actionName, topicName);
        Gson gson = new GsonBuilder().create();
        Map<String, Object> attributes =
                /// @formatter:off
                gson.fromJson(topic.toString(), new TypeToken<Map<String, Object>>() {}.getType());
        /// @formatter:on
        attributes.put("actions", actionNames);
        this.topicRepository.store(topicName, gson.toJson(attributes));
    }
}
