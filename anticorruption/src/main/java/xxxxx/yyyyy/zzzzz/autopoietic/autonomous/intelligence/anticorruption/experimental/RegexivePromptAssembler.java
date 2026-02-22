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
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Action;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Agent;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Context;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Topic;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/// FIXME
@ApplicationScoped
public class RegexivePromptAssembler implements PromptAssembler {
    private static final Logger logger = LoggerFactory.getLogger(RegexivePromptAssembler.class);
    private final Repository<Agent> agentRepository;
    private final Repository<Topic> topicRepository;
    private final Repository<Action> actionRepository;
    private final FileSystem fileSystem;
    private final JsonCodec jsonCodec;
    private final Path promptsSource;

    @Inject
    public RegexivePromptAssembler(Repository<Agent> agentRepository,
                                   Repository<Topic> topicRepository,
                                   Repository<Action> actionRepository,
                                   @Localic FileSystem fileSystem,
                                   JsonCodec jsonCodec) {
        this.agentRepository = agentRepository;
        this.topicRepository = topicRepository;
        this.actionRepository = actionRepository;
        this.fileSystem = fileSystem;
        this.jsonCodec = jsonCodec;
        var configuration = new Configuration("anticorruption.yaml");
        this.promptsSource = Path.of(configuration.get("anticorruption.prompts.source"), "");
    }

    @Override
    public String inference(Context context, Agent self) {
        return this.assemble("inference.md", Map.of(
            "input", context.input(),
            "conversation", context.conversation().snapshot().toString(),
            "state", context.state().snapshot().toString(),
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
        return this.assemble("routing.md", Map.of(
            "input", context.input(),
            "conversation", context.conversation().snapshot().toString(),
            "state", context.state().snapshot().toString(),
            "agents", this.agentsForRouting(),
            "topics", this.topicsForRouting()
        ));
    }

    @Override
    public String upgrade(Context context, Agent self) {
        return this.assemble("upgrade.md", Map.of(
            "input", context.input(),
            "conversation", context.conversation().snapshot().toString(),
            "state", context.state().snapshot().toString(),
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
            this.promptsSource.resolve(id),
            StandardCharsets.UTF_8);
    }
}
