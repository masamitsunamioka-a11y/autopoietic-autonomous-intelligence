package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.JsonParser;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.PromptAssembler;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.*;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class RegexivePromptAssembler implements PromptAssembler {
    private final Repository<String> promptRepository;
    private final Repository<Agent> agentRepository;
    private final Repository<Topic> topicRepository;
    private final Repository<Action> actionRepository;
    private final JsonParser jsonParser;

    @Inject
    public RegexivePromptAssembler(Repository<String> promptRepository,
                                   Repository<Agent> agentRepository,
                                   Repository<Topic> topicRepository,
                                   Repository<Action> actionRepository,
                                   JsonParser jsonParser) {
        this.promptRepository = promptRepository;
        this.agentRepository = agentRepository;
        this.topicRepository = topicRepository;
        this.actionRepository = actionRepository;
        this.jsonParser = jsonParser;
    }

    @Override
    public String inference(String input, Conversation conversation, State state, Agent self) {
        return this.render("inference.md", Map.of(
                "input", input,
                "conversation", conversation.toString(),
                "state", state.snapshot().toString(),
                "agentName", self.name(),
                "agentDescription", self.description(),
                "agentInstructions", self.instructions(),
                "topics", this.topics(),
                "actions", this.actions()
        ));
    }

    @Override
    public String routing(String input, Conversation conversation, State state) {
        List<Map<String, String>> agents = this.agentRepository.findAll().stream()
                .map(x -> Map.of(
                        "name", x.name(),
                        "label", x.label(),
                        "description", x.description()
                )).toList();
        List<Map<String, String>> topics = this.topicRepository.findAll().stream()
                .map(x -> Map.of(
                        "name", x.name(),
                        "label", x.label(),
                        "description", x.description()
                )).toList();
        return this.render("routing.md", Map.of(
                "input", input,
                "conversation", conversation.toString(),
                "state", state.snapshot().toString(),
                "agents", this.jsonParser.to(agents),
                "topics", this.jsonParser.to(topics)
        ));
    }

    @Override
    public String upgrade(String input, Conversation conversation, State state, Agent self) {
        return this.render("upgrade.md", Map.of(
                "input", input,
                "conversation", conversation.toString(),
                "state", state.snapshot().toString(),
                "agentName", self.name(),
                "agentDescription", self.description(),
                "agentInstructions", self.instructions(),
                "agents", this.agents(),
                "topics", this.topics(),
                "actions", this.actions()
        ));
    }

    @Override
    public String consolidation() {
        return this.render("consolidation.md", Map.of(
                "agents", this.agents(),
                "topics", this.topics(),
                "actions", this.actions()
        ));
    }

    private String agents() {
        return this.jsonParser.to(this.agentRepository.findAll().stream()
                .map(x -> Map.of(
                        "name", x.name(),
                        "label", x.label(),
                        "description", x.description(),
                        "instructions", x.instructions(),
                        "topics", x.topics().stream().map(Topic::name).toList()
                )).toList());
    }

    private String topics() {
        return this.jsonParser.to(this.topicRepository.findAll().stream()
                .map(x -> Map.of(
                        "name", x.name(),
                        "label", x.label(),
                        "description", x.description(),
                        "instructions", x.instructions(),
                        "actions", x.actions().stream().map(Action::name).toList()
                )).toList());
    }

    private String actions() {
        return this.jsonParser.to(this.actionRepository.findAll().stream()
                .map(x -> Map.of(
                        "name", x.name(),
                        "label", x.label(),
                        "description", x.description()
                )).toList());
    }

    private String render(String templateName, Map<String, Object> values) {
        return values.entrySet().stream()
                .reduce(this.promptRepository.find(templateName)
                                .replace("{{guardrails}}",
                                        this.promptRepository.find("guardrails.md")),
                        (x, y) -> x.replace("{{" + y.getKey() + "}}",
                                String.valueOf(y.getValue())),
                        (x, y) -> x
                );
    }
}
