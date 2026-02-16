package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.experimental;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.FileSystem;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.JsonParser;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Localic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.PromptAssembler;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class RegexivePromptAssembler implements PromptAssembler {
    private static final Logger logger = LoggerFactory.getLogger(RegexivePromptAssembler.class);
    private final Configuration configuration;
    private final Repository<Agent> agentRepository;
    private final Repository<Topic> topicRepository;
    private final Repository<Action> actionRepository;
    private final FileSystem fileSystem;
    private final JsonParser jsonParser;

    @Inject
    public RegexivePromptAssembler(Repository<Agent> agentRepository,
                                   Repository<Topic> topicRepository,
                                   Repository<Action> actionRepository,
                                   @Localic FileSystem fileSystem,
                                   JsonParser jsonParser) {
        this.configuration = new Configuration("anticorruption.yaml");
        this.agentRepository = agentRepository;
        this.topicRepository = topicRepository;
        this.actionRepository = actionRepository;
        this.fileSystem = fileSystem;
        this.jsonParser = jsonParser;
    }

    @Override
    public String inference(String input, Conversation conversation, State state, Agent self) {
        return this.assemble("inference.md", Map.of(
                "input", input,
                "conversation", conversation.snapshot().toString(),
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
        return this.assemble("routing.md", Map.of(
                "input", input,
                "conversation", conversation.snapshot().toString(),
                "state", state.snapshot().toString(),
                "agents", this.jsonParser.toString(agents),
                "topics", this.jsonParser.toString(topics)
        ));
    }

    @Override
    public String upgrade(String input, Conversation conversation, State state, Agent self) {
        return this.assemble("upgrade.md", Map.of(
                "input", input,
                "conversation", conversation.snapshot().toString(),
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
        return this.assemble("consolidation.md", Map.of(
                "agents", this.agents(),
                "topics", this.topics(),
                "actions", this.actions()
        ));
    }

    private String agents() {
        return this.jsonParser.toString(this.agentRepository.findAll().stream()
                .map(x -> Map.of(
                        "name", x.name(),
                        "label", x.label(),
                        "description", x.description(),
                        "instructions", x.instructions(),
                        "topics", x.topics().stream().map(Topic::name).toList()
                )).toList());
    }

    private String topics() {
        return this.jsonParser.toString(this.topicRepository.findAll().stream()
                .map(x -> Map.of(
                        "name", x.name(),
                        "label", x.label(),
                        "description", x.description(),
                        "instructions", x.instructions(),
                        "actions", x.actions().stream().map(Action::name).toList()
                )).toList());
    }

    private String actions() {
        return this.jsonParser.toString(this.actionRepository.findAll().stream()
                .map(x -> Map.of(
                        "name", x.name(),
                        "label", x.label(),
                        "description", x.description()
                )).toList());
    }

    private String assemble(String templateName, Map<String, Object> values) {
        return values.entrySet().stream()
                .reduce(this.read(templateName)
                                .replace("{{guardrails}}",
                                        this.read("guardrails.md")),
                        (x, y) -> x.replace("{{" + y.getKey() + "}}",
                                String.valueOf(y.getValue())),
                        (x, y) -> x
                );
    }

    private String read(String id) {
        return this.fileSystem.read(
                Paths.get(this.promptsSource().toString(), id),
                StandardCharsets.UTF_8
        );
    }

    private Path promptsSource() {
        String promptsSource = this.configuration.get("anticorruption.prompts.source");
        return Path.of(promptsSource);
    }
}
