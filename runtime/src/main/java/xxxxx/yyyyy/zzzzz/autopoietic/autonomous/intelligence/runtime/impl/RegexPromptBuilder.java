package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.PromptBuilder;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.*;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class RegexPromptBuilder implements PromptBuilder {
    private final Repository<String> promptRepository;
    private final Repository<Agent> agentRepository;
    private final Repository<Topic> topicRepository;
    private final Repository<Action> actionRepository;
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    @Inject
    public RegexPromptBuilder(Repository<String> promptRepository,
                              Repository<Agent> agentRepository,
                              Repository<Topic> topicRepository,
                              Repository<Action> actionRepository) {
        this.promptRepository = promptRepository;
        this.agentRepository = agentRepository;
        this.topicRepository = topicRepository;
        this.actionRepository = actionRepository;
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
                .map(a -> Map.of(
                        "name", a.name(),
                        "label", a.label(),
                        "description", a.description()
                )).toList();
        List<Map<String, String>> topics = this.topicRepository.findAll().stream()
                .map(t -> Map.of(
                        "name", t.name(),
                        "label", t.label(),
                        "description", t.description()
                )).toList();
        return this.render("routing.md", Map.of(
                "input", input,
                "conversation", conversation.toString(),
                "state", state.snapshot().toString(),
                "agents", this.gson.toJson(agents),
                "topics", this.gson.toJson(topics)
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
        var agents = this.agentRepository.findAll().stream()
                .map(a -> Map.of(
                        "name", a.name(),
                        "label", a.label(),
                        "description", a.description(),
                        "instructions", a.instructions(),
                        "topics", a.topics().stream().map(Topic::name).toList()
                )).toList();
        return this.render("consolidation.md", Map.of(
                "agents", this.gson.toJson(agents),
                "topics", this.topics(),
                "actions", this.actions()
        ));
    }

    private String agents() {
        return this.gson.toJson(this.agentRepository.findAll().stream()
                .map(a -> Map.of(
                        "name", a.name(),
                        "label", a.label(),
                        "description", a.description(),
                        "instructions", a.instructions()
                )).toList());
    }

    private String topics() {
        return this.gson.toJson(this.topicRepository.findAll().stream()
                .map(t -> Map.of(
                        "name", t.name(),
                        "label", t.label(),
                        "description", t.description(),
                        "instructions", t.instructions(),
                        "actions", t.actions().stream().map(Action::name).toList()
                )).toList());
    }

    private String actions() {
        return this.gson.toJson(this.actionRepository.findAll().stream()
                .map(a -> Map.of(
                        "name", a.name(),
                        "label", a.label(),
                        "description", a.description()
                )).toList());
    }

    private String render(String templateName, Map<String, Object> values) {
        String template = this.promptRepository.find(templateName);
        String guardrails = this.promptRepository.find("guardrails.md");
        String result = template.replace("{{guardrails}}", guardrails);
        for (var entry : values.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", String.valueOf(entry.getValue()));
        }
        return result;
    }
}
