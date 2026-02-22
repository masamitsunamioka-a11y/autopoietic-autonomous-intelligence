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
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Context;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Receptor;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@ApplicationScoped
public class RegexivePromptAssembler implements PromptAssembler {
    private static final Logger logger = LoggerFactory.getLogger(RegexivePromptAssembler.class);
    private final Repository<Neuron> neuronRepository;
    private final Repository<Receptor> receptorRepository;
    private final Repository<Effector> effectorRepository;
    private final FileSystem fileSystem;
    private final JsonCodec jsonCodec;
    private final Path promptsSource;

    @Inject
    public RegexivePromptAssembler(Repository<Neuron> neuronRepository,
                                   Repository<Receptor> receptorRepository,
                                   Repository<Effector> effectorRepository,
                                   @Localic FileSystem fileSystem,
                                   JsonCodec jsonCodec) {
        this.neuronRepository = neuronRepository;
        this.receptorRepository = receptorRepository;
        this.effectorRepository = effectorRepository;
        this.fileSystem = fileSystem;
        this.jsonCodec = jsonCodec;
        var configuration = new Configuration("anticorruption.yaml");
        this.promptsSource = Path.of(configuration.get("anticorruption.prompts.source"), "");
    }

    @Override
    public String perception(Context context, Neuron self) {
        return this.assemble("perception.md", Map.of(
            "input", context.input(),
            "conversation", context.conversation().snapshot().toString(),
            "state", context.state().snapshot().toString(),
            "self", this.self(self),
            "receptors", this.marshal(self.receptors(), x -> {
                return Map.of(
                    "name", x.name(),
                    "description", x.description(),
                    "instructions", x.instructions(),
                    "effectors", x.effectors().stream().map(Effector::name).toList()
                );
            }),
            "effectors", this.marshal(self.receptors().stream()
                .flatMap(x -> x.effectors().stream())
                .toList(), x -> {
                return Map.of(
                    "name", x.name(),
                    "description", x.description());
            })
        ));
    }

    @Override
    public String relay(Context context) {
        return this.assemble("relay.md", Map.of(
            "input", context.input(),
            "conversation", context.conversation().snapshot().toString(),
            "state", context.state().snapshot().toString(),
            "neurons", this.marshal(this.neuronRepository.findAll(), x -> {
                return Map.of(
                    "name", x.name(),
                    "description", x.description());
            }),
            "receptors", this.marshal(this.receptorRepository.findAll(), x -> {
                return Map.of(
                    "name", x.name(),
                    "description", x.description());
            })
        ));
    }

    @Override
    public String potentiation(Context context, Neuron self) {
        return this.assemble("potentiation.md", Map.of(
            "input", context.input(),
            "conversation", context.conversation().snapshot().toString(),
            "state", context.state().snapshot().toString(),
            "self", this.self(self),
            "neurons", this.neurons(),
            "receptors", this.receptors(),
            "effectors", this.effectors()
        ));
    }

    @Override
    public String pruning() {
        return this.assemble("pruning.md", Map.of(
            "neurons", this.neurons(),
            "receptors", this.receptors(),
            "effectors", this.effectors()
        ));
    }

    private String self(Neuron self) {
        return this.jsonCodec.marshal(Map.of(
            "name", self.name(),
            "description", self.description(),
            "instructions", self.instructions()
        ));
    }

    private String neurons() {
        return this.marshal(this.neuronRepository.findAll(), x -> Map.of(
            "name", x.name(),
            "description", x.description(),
            "instructions", x.instructions(),
            "receptors", x.receptors().stream()
                .map(Receptor::name)
                .toList()));
    }

    private String receptors() {
        return this.marshal(this.receptorRepository.findAll(), x -> Map.of(
            "name", x.name(), "description", x.description(),
            "instructions", x.instructions(),
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
        return this.jsonCodec.marshal(list.stream().map(toMap).toList());
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
