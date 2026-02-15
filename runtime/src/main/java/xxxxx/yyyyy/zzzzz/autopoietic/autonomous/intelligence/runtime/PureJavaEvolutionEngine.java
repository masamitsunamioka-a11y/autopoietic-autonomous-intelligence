package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ApplicationScoped
public class PureJavaEvolutionEngine implements EvolutionEngine {
    private static final Logger logger = LoggerFactory.getLogger(PureJavaEvolutionEngine.class);
    private final Intelligence intelligence;
    private final PromptAssembler promptAssembler;
    private final Repository<Agent> agentRepository;
    private final Repository<Topic> topicRepository;
    private final Repository<Action> actionRepository;
    private final JsonParser jsonParser;

    @Inject
    public PureJavaEvolutionEngine(Intelligence intelligence,
                                   PromptAssembler promptAssembler,
                                   Repository<Agent> agentRepository,
                                   Repository<Topic> topicRepository,
                                   Repository<Action> actionRepository,
                                   JsonParser jsonParser) {
        this.intelligence = intelligence;
        this.promptAssembler = promptAssembler;
        this.agentRepository = agentRepository;
        this.topicRepository = topicRepository;
        this.actionRepository = actionRepository;
        this.jsonParser = jsonParser;
    }

    @Override
    public void upgrade(String input, Conversation conversation, State state, Agent agent) {
        String prompt = this.promptAssembler.upgrade(input, conversation, state, agent);
        Upgrade upgrade = this.intelligence.reason(prompt, Upgrade.class);
        logger.debug("[INTELLIGENCE] Reasoning: ({}) [{}], " +
                        "NewInstructions: {} chars, NewAgents: {}, NewTopics: {}, NewActions: {}",
                upgrade.confidence(),
                upgrade.reasoning(),
                upgrade.newInstructions().length(),
                upgrade.newAgents().size(),
                upgrade.newTopics().size(),
                upgrade.newActions().size()
        );
        upgrade.newActions().forEach(x -> {
            this.actionRepository.store(x.name(), x.rawJson());
        });
        upgrade.newTopics().forEach(x -> {
            this.topicRepository.store(x.name(), x.rawJson());
            upgrade.newActions().stream()
                    .filter(y -> y.relatedTopics().contains(x.name()))
                    .forEach(y -> this.linkActionToTopic(y.name(), x.name()));
        });
        upgrade.newAgents().forEach(x -> {
            this.agentRepository.store(x.name(), x.rawJson());
        });
        if (!upgrade.newInstructions().isEmpty()) {
            agent.instructions(upgrade.newInstructions());
            this.agentRepository.store(agent.name(), agent.toString());
        }
    }

    @Override
    public void consolidate() {
        String prompt = this.promptAssembler.consolidation();
        Consolidation consolidation = this.intelligence.reason(prompt, Consolidation.class);
        logger.debug("[INTELLIGENCE] Reasoning: ({}) [{}], " +
                        "ConsolidatedAgents: {}, ConsolidatedTopics: {}",
                consolidation.confidence(),
                consolidation.reasoning(),
                consolidation.consolidatedAgents().size(),
                consolidation.consolidatedTopics().size()
        );
        /// Purge
        consolidation.consolidatedAgents().stream()
                .flatMap(x -> x.consolidants().stream())
                .forEach(this.agentRepository::remove);
        consolidation.consolidatedTopics().stream()
                .flatMap(x -> x.consolidants().stream())
                .forEach(this.topicRepository::remove);
        /// Merge
        consolidation.consolidatedTopics().stream()
                .map(Consolidation.ConsolidatedTopic::consolidated)
                .forEach(x -> this.topicRepository.store(x.name(), x.rawJson()));
        consolidation.consolidatedAgents().stream()
                .map(Consolidation.ConsolidatedAgent::consolidated)
                .forEach(x -> this.agentRepository.store(x.name(), x.rawJson()));
    }

    private void linkActionToTopic(String actionName, String topicName) {
        Topic topic = this.topicRepository.find(topicName);
        List<String> actionNames = Stream.concat(
                topic.actions().stream().map(Action::name),
                Stream.of(actionName)
        ).distinct().toList();
        Map<String, Object> attributes = this.jsonParser.from(topic.toString());
        attributes.put("actions", actionNames);
        this.topicRepository.store(topicName, this.jsonParser.to(attributes));
    }
}
