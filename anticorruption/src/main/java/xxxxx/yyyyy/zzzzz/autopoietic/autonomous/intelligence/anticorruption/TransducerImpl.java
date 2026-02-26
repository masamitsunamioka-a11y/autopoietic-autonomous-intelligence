package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.Transducer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Module;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Conversation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.State;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;

@ApplicationScoped
public class TransducerImpl implements Transducer {
    private static final Logger logger = LoggerFactory.getLogger(TransducerImpl.class);
    private final Repository<Neuron> neuronRepository;
    private final Repository<Module> moduleRepository;
    private final Repository<Effector> effectorRepository;
    private final FileSystem fileSystem;
    private final JsonCodec jsonCodec;
    private final Conversation conversation;
    private final State state;
    private final Path phaseSource;
    private final Path sharedSource;

    @Inject
    public TransducerImpl(Repository<Neuron> neuronRepository,
                          Repository<Module> moduleRepository,
                          Repository<Effector> effectorRepository,
                          @Localic FileSystem fileSystem,
                          JsonCodec jsonCodec,
                          Conversation conversation,
                          State state) {
        this.neuronRepository = neuronRepository;
        this.moduleRepository = moduleRepository;
        this.effectorRepository = effectorRepository;
        this.fileSystem = fileSystem;
        this.jsonCodec = jsonCodec;
        this.conversation = conversation;
        this.state = state;
        var configuration = new Configuration("anticorruption.yaml");
        this.phaseSource = Path.of(configuration.get("anticorruption.encodings.phase"), "");
        this.sharedSource = Path.of(configuration.get("anticorruption.encodings.shared"), "");
    }

    @Override
    public String perception(Stimulus stimulus) {
        var self = stimulus.neuron();
        /// @formatter:off
        return this.assemble("perception.md", Map.of(
            "input",        stimulus.input(),
            "conversation", this.conversation.conversation().toString(),
            "state",        this.state.state().toString(),
            "self",         this.self(self),
            "neuron_names", this.neuronNames(),
            "modules",      this.marshal(this.modules(self), x -> Map.of(
                "name",         x.name(),
                "function",     x.function(),
                "disposition",  x.disposition(),
                "effectors",    x.effectors().stream().map(Effector::name).toList()
            )),
            "effectors",    this.marshal(this.effectors(self), x -> Map.of(
                "name",         x.name(),
                "function",     x.function()))
        ));
        /// @formatter:on
    }

    private List<Module> modules(Neuron self) {
        return self.modules();
    }

    private List<Effector> effectors(Neuron self) {
        return this.modules(self).stream()
            .flatMap(x -> x.effectors().stream())
            .toList();
    }

    @Override
    public String relay(Stimulus stimulus) {
        /// @formatter:off
        return this.assemble("relay.md", Map.of(
            "input",        stimulus.input(),
            "conversation", this.conversation.conversation().toString(),
            "state",        this.state.state().toString(),
            "neuron_names", this.neuronNames(),
            "neurons",      this.neuronsSummary()
        ));
        /// @formatter:on
    }

    @Override
    public String potentiation(Stimulus stimulus) {
        var self = stimulus.neuron();
        /// @formatter:off
        return this.assemble("potentiation.md", Map.of(
            "input",        stimulus.input(),
            "conversation", this.conversation.conversation().toString(),
            "state",        this.state.state().toString(),
            "self",         this.self(self),
            "neurons",      this.neurons(),
            "module_names", this.moduleNames(),
            "modules",      this.modules(),
            "effectors",    this.effectors())
        );
        /// @formatter:on
    }

    @Override
    public String pruning() {
        /// @formatter:off
        return this.assemble("pruning.md", Map.of(
            "neurons",      this.neurons(),
            "modules",      this.modules(),
            "effectors",    this.effectors()
        ));
        /// @formatter:on
    }

    @Override
    public String drive() {
        /// @formatter:off
        return this.assemble("drive.md", Map.of(
            "conversation", this.conversation.conversation().toString(),
            "state",        this.state.state().toString(),
            "neurons",      this.neuronsSummary()
        ));
        /// @formatter:on
    }

    private String neuronNames() {
        return this.neuronRepository.findAll().stream()
            .map(Neuron::name)
            .collect(joining(", "));
    }

    private String neuronsSummary() {
        return this.marshal(this.neuronRepository.findAll(), x -> Map.of(
            "name", x.name(),
            "function", x.function()));
    }

    private String moduleNames() {
        return this.moduleRepository.findAll().stream()
            .map(Module::name)
            .collect(joining(", "));
    }

    private String self(Neuron self) {
        return this.jsonCodec.marshal(Map.of(
            "name", self.name(),
            "function", self.function(),
            "disposition", self.disposition()));
    }

    private String neurons() {
        return this.marshal(this.neuronRepository.findAll(), x -> Map.of(
            "name", x.name(),
            "function", x.function(),
            "disposition", x.disposition(),
            "modules", x.modules().stream()
                .map(Module::name)
                .toList()));
    }

    private String modules() {
        return this.marshal(this.moduleRepository.findAll(), x -> Map.of(
            "name", x.name(),
            "function", x.function(),
            "disposition", x.disposition(),
            "effectors", x.effectors().stream()
                .map(Effector::name)
                .toList()));
    }

    private String effectors() {
        return this.marshal(this.effectorRepository.findAll(), x -> Map.of(
            "name", x.name(),
            "function", x.function()));
    }

    private <T> String marshal(List<T> list, Function<T, Map<String, Object>> toMap) {
        if (list.isEmpty()) {
            return "None";
        }
        return this.jsonCodec.marshal(list.stream().map(toMap).toList());
    }

    private String assemble(String id, Map<String, Object> values) {
        return values.entrySet().stream()
            .reduce(this.read(id)
                    .replace("{{guardrails}}", this.readShared("executive_control.md"))
                    .replace("{{output_integrity}}", this.readShared("output_integrity.md")),
                (x, y) -> x.replace("{{" + y.getKey() + "}}", String.valueOf(y.getValue())),
                (x, y) -> x);
    }

    private String read(String id) {
        return this.fileSystem.read(this.phaseSource.resolve(id), StandardCharsets.UTF_8);
    }

    private String readShared(String id) {
        return this.fileSystem.read(this.sharedSource.resolve(id), StandardCharsets.UTF_8);
    }
}
