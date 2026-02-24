package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Encoder;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class EncoderImpl implements Encoder {
    private static final Logger logger = LoggerFactory.getLogger(EncoderImpl.class);
    private final Repository<Neuron> neuronRepository;
    private final Repository<Schema> schemaRepository;
    private final Repository<Effector> effectorRepository;
    private final FileSystem fileSystem;
    private final JsonCodec jsonCodec;
    private final Memory memory;
    private final Path phasedSource;
    private final Path sharedSource;

    @Inject
    public EncoderImpl(Repository<Neuron> neuronRepository,
                       Repository<Schema> schemaRepository,
                       Repository<Effector> effectorRepository,
                       @Localic FileSystem fileSystem,
                       JsonCodec jsonCodec,
                       Memory memory) {
        this.neuronRepository = neuronRepository;
        this.schemaRepository = schemaRepository;
        this.effectorRepository = effectorRepository;
        this.fileSystem = fileSystem;
        this.jsonCodec = jsonCodec;
        this.memory = memory;
        var configuration = new Configuration("anticorruption.yaml");
        this.phasedSource = Path.of(configuration.get("anticorruption.encodings.phased"), "");
        this.sharedSource = Path.of(configuration.get("anticorruption.encodings.shared"), "");
    }

    @Override
    public String perception(Impulse impulse) {
        var self = impulse.neuron();
        return this.assemble("perception.md", Map.of(
            "input", impulse.input(),
            "conversation", this.memory.conversation().toString(),
            "state", this.memory.state().toString(),
            "self", this.self(self),
            "neuron_names", this.neuronRepository.findAll().stream()
                .map(Neuron::name)
                .collect(Collectors.joining(", ")),
            "schemas", this.marshal(self.schemas(), x -> {
                return Map.of(
                    "name", x.name(),
                    "description", x.description(),
                    "protocol", x.protocol(),
                    "effectors", x.effectors().stream().map(Effector::name).toList()
                );
            }),
            "effectors", this.marshal(self.schemas().stream()
                .flatMap(x -> x.effectors().stream())
                .toList(), x -> {
                return Map.of(
                    "name", x.name(),
                    "description", x.description());
            })
        ));
    }

    @Override
    public String relay(Impulse impulse) {
        var neurons = this.neuronRepository.findAll();
        return this.assemble("relay.md", Map.of(
            "input", impulse.input(),
            "conversation", this.memory.conversation().toString(),
            "state", this.memory.state().toString(),
            "neuron_names", neurons.stream()
                .map(Neuron::name)
                .collect(Collectors.joining(", ")),
            "neurons", this.marshal(neurons, x -> {
                return Map.of(
                    "name", x.name(),
                    "description", x.description());
            })
        ));
    }

    @Override
    public String potentiation(Impulse impulse) {
        var self = impulse.neuron();
        return this.assemble("potentiation.md", Map.of(
            "input", impulse.input(),
            "conversation", this.memory.conversation().toString(),
            "state", this.memory.state().toString(),
            "self", this.self(self),
            "neurons", this.neurons(),
            "schema_names", this.schemaRepository.findAll().stream()
                .map(Schema::name)
                .collect(Collectors.joining(", ")),
            "schemas", this.schemas(),
            "effectors", this.effectors()
        ));
    }

    @Override
    public String pruning() {
        return this.assemble("pruning.md", Map.of(
            "neurons", this.neurons(),
            "schemas", this.schemas(),
            "effectors", this.effectors()
        ));
    }

    @Override
    public String drive() {
        return this.assemble("drive.md", Map.of(
            "conversation", this.memory.conversation().toString(),
            "state", this.memory.state().toString(),
            "neurons", this.marshal(this.neuronRepository.findAll(), x -> Map.of(
                "name", x.name(),
                "description", x.description()))
        ));
    }

    private String self(Neuron self) {
        return this.jsonCodec.marshal(Map.of(
            "name", self.name(),
            "description", self.description(),
            "protocol", self.protocol()
        ));
    }

    private String neurons() {
        return this.marshal(this.neuronRepository.findAll(), x -> Map.of(
            "name", x.name(),
            "description", x.description(),
            "protocol", x.protocol(),
            "schemas", x.schemas().stream()
                .map(Schema::name)
                .toList()));
    }

    private String schemas() {
        return this.marshal(this.schemaRepository.findAll(), x -> Map.of(
            "name", x.name(), "description", x.description(),
            "protocol", x.protocol(),
            "effectors", x.effectors().stream()
                .map(Effector::name)
                .toList()));
    }

    private String effectors() {
        return this.marshal(this.effectorRepository.findAll(), x -> Map.of(
            "name", x.name(),
            "description", x.description()));
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
                    .replace("{{guardrails}}", this.readShared("guardrails.md"))
                    .replace("{{output_integrity}}", this.readShared("output_integrity.md")),
                (x, y) -> x.replace("{{" + y.getKey() + "}}", String.valueOf(y.getValue())),
                (x, y) -> x);
    }

    private String read(String id) {
        return this.fileSystem.read(
            this.phasedSource.resolve(id),
            StandardCharsets.UTF_8);
    }

    private String readShared(String id) {
        return this.fileSystem.read(
            this.sharedSource.resolve(id),
            StandardCharsets.UTF_8);
    }
}
