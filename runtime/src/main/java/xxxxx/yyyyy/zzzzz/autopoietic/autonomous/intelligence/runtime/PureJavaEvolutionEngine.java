package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.*;

import java.util.stream.Collectors;

@ApplicationScoped
public class PureJavaEvolutionEngine implements EvolutionEngine {
    private static final Logger logger = LoggerFactory.getLogger(PureJavaEvolutionEngine.class);
    private final Intelligence intelligence;
    private final PromptAssembler promptAssembler;
    private final Repository<Agent> agentRepository;
    private final Repository<Topic> topicRepository;
    private final Repository<Action> actionRepository;

    @Inject
    public PureJavaEvolutionEngine(Intelligence intelligence,
                                   PromptAssembler promptAssembler,
                                   Repository<Agent> agentRepository,
                                   Repository<Topic> topicRepository,
                                   Repository<Action> actionRepository) {
        this.intelligence = intelligence;
        this.promptAssembler = promptAssembler;
        this.agentRepository = agentRepository;
        this.topicRepository = topicRepository;
        this.actionRepository = actionRepository;
    }

    @Override
    public void upgrade(Context context, Agent agent) {
        var prompt = this.promptAssembler.upgrade(context, agent);
        var upgrade = this.intelligence.reason(prompt, Upgrade.class);
        logger.debug("[INTELLIGENCE] Reasoning: ({}) [{}], " +
                "NewInstructions: {} chars, NewAgents: {}, NewTopics: {}, NewActions: {}",
            upgrade.confidence(),
            upgrade.reasoning(),
            upgrade.newInstructions().length(),
            upgrade.newAgents().size(),
            upgrade.newTopics().size(),
            upgrade.newActions().size()
        );
        this.createNewActions(upgrade);
        this.createNewTopics(upgrade);
        this.createNewAgents(upgrade);
        this.updateInstructions(upgrade, agent);
    }

    @Override
    public void consolidate() {
        var prompt = this.promptAssembler.consolidation();
        var consolidation = this.intelligence.reason(prompt, Consolidation.class);
        logger.debug("[INTELLIGENCE] Reasoning: ({}) [{}], " +
                "ConsolidatedAgents: {}, ConsolidatedTopics: {}",
            consolidation.confidence(),
            consolidation.reasoning(),
            consolidation.consolidatedAgents().size(),
            consolidation.consolidatedTopics().size()
        );
        this.removeConsolidants(consolidation);
        this.createConsolidated(consolidation);
    }

    private void createNewActions(Upgrade upgrade) {
        upgrade.newActions().forEach(x -> {
            this.actionRepository.store(x.name(), x);
        });
    }

    private void createNewTopics(Upgrade upgrade) {
        upgrade.newTopics().forEach(x -> {
            this.topicRepository.store(x.name(), x);
        });
    }

    private void createNewAgents(Upgrade upgrade) {
        upgrade.newAgents().forEach(x -> {
            this.agentRepository.store(x.name(), x);
        });
    }

    private void updateInstructions(Upgrade upgrade, Agent agent) {
        if (!upgrade.newInstructions().isEmpty()) {
            this.agentRepository.store(agent.name(), () -> """
                {
                  "name": "%s",
                  "label": "%s",
                  "description": "%s",
                  "instructions": "%s",
                  "topics": %s
                }
                """.formatted(agent.name(), agent.label(), agent.description(),
                upgrade.newInstructions()
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n"),
                agent.topics().stream()
                    .map(x -> "\"" + x.name() + "\"")
                    .collect(Collectors.joining(", ", "[", "]"))
            ));
        }
    }

    private void removeConsolidants(Consolidation consolidation) {
        consolidation.consolidatedAgents().stream()
            .flatMap(x -> x.consolidants().stream())
            .forEach(this.agentRepository::remove);
        consolidation.consolidatedTopics().stream()
            .flatMap(x -> x.consolidants().stream())
            .forEach(this.topicRepository::remove);
    }

    private void createConsolidated(Consolidation consolidation) {
        consolidation.consolidatedTopics().stream()
            .map(Consolidation.ConsolidatedTopic::consolidated)
            .forEach(x -> {
                this.topicRepository.store(x.name(), x);
            });
        consolidation.consolidatedAgents().stream()
            .map(Consolidation.ConsolidatedAgent::consolidated)
            .forEach(x -> {
                this.agentRepository.store(x.name(), x);
            });
    }
}
