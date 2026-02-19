package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.experimental;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.FileSystem;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.JsonCodec;
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
    private final JsonCodec jsonCodec;

    @Inject
    public RegexivePromptAssembler(Repository<Agent> agentRepository,
                                   Repository<Topic> topicRepository,
                                   Repository<Action> actionRepository,
                                   @Localic FileSystem fileSystem,
                                   JsonCodec jsonCodec) {
        this.configuration = new Configuration("anticorruption.yaml");
        this.agentRepository = agentRepository;
        this.topicRepository = topicRepository;
        this.actionRepository = actionRepository;
        this.fileSystem = fileSystem;
        this.jsonCodec = jsonCodec;
    }

    @Override
    public String inference(Context context, Agent self) {
        Conversation conversation = context.conversation();
        State state = context.state();
        return this.assemble("inference.md", Map.of(
            "input", context.input(),
            "conversation", conversation.snapshot().toString(),
            "state", state.snapshot().toString(),
            "self", this.self(self),
            "topics", this.topics(self.topics()),
            "actions", this.actions(
                self.topics().stream()
                    .flatMap(x -> x.actions().stream())
                    .toList())
        ));
    }

    @Override
    public String routing(Context context) {
        Conversation conversation = context.conversation();
        State state = context.state();
        return this.assemble("routing.md", Map.of(
            "input", context.input(),
            "conversation", conversation.snapshot().toString(),
            "state", state.snapshot().toString(),
            "agents", this.agentsForRouting(),
            "topics", this.topicsForRouting()
        ));
    }

    @Override
    public String upgrade(Context context, Agent self) {
        Conversation conversation = context.conversation();
        State state = context.state();
        return this.assemble("upgrade.md", Map.of(
            "input", context.input(),
            "conversation", conversation.snapshot().toString(),
            "state", state.snapshot().toString(),
            "self", this.self(self),
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

    private String self(Agent self) {
        return this.jsonCodec.marshal(Map.of(
            "name", self.name(),
            "description", self.description(),
            "instructions", self.instructions()
        ));
    }

    private String agents() {
        return this.jsonCodec.marshal(this.agentRepository.findAll().stream()
            .map(x -> Map.of(
                "name", x.name(),
                "description", x.description(),
                "instructions", x.instructions(),
                "topics", x.topics().stream().map(Topic::name).toList()
            ))
            .toList());
    }

    private String agentsForRouting() {
        return this.jsonCodec.marshal(this.agentRepository.findAll().stream()
            .map(x -> Map.of(
                "name", x.name(),
                "description", x.description()
            ))
            .toList());
    }

    private String topics() {
        return this.topics(this.topicRepository.findAll());
    }

    private String topics(List<Topic> topics) {
        return this.jsonCodec.marshal(topics.stream()
            .map(x -> Map.of(
                "name", x.name(),
                "description", x.description(),
                "instructions", x.instructions(),
                "actions", x.actions().stream().map(Action::name).toList()
            ))
            .toList());
    }

    private String topicsForRouting() {
        return this.jsonCodec.marshal(this.topicRepository.findAll().stream()
            .map(x -> Map.of(
                "name", x.name(),
                "description", x.description()
            ))
            .toList());
    }

    private String actions() {
        return this.actions(this.actionRepository.findAll());
    }

    private String actions(List<Action> actions) {
        return this.jsonCodec.marshal(actions.stream()
            .map(x -> Map.of(
                "name", x.name(),
                "description", x.description()
            ))
            .toList());
    }

    private String assemble(String id, Map<String, Object> values) {
        return values.entrySet().stream()
            .reduce(this.read(id).replace("{{guardrails}}", this.read("guardrails.md")),
                (x, y) -> x.replace("{{" + y.getKey() + "}}", String.valueOf(y.getValue())),
                (x, y) -> x);
    }

    private String read(String id) {
        return this.fileSystem.read(
            Paths.get(this.promptsSource().toString(), id),
            StandardCharsets.UTF_8);
    }

    private Path promptsSource() {
        String promptsSource = this.configuration.get("anticorruption.prompts.source");
        return Path.of(promptsSource);
    }
}
