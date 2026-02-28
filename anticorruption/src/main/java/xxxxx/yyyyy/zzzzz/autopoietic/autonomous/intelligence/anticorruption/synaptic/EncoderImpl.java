package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.synaptic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.FileSystem;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.JsonCodec;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Localic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Encoder;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Knowledge;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;

@ApplicationScoped
public class EncoderImpl implements Encoder {
    private static final Logger logger = LoggerFactory.getLogger(EncoderImpl.class);
    private final Repository<Area, Engram> areaRepository;
    private final Repository<Neuron, Engram> neuronRepository;
    private final Repository<Effector, Organ> effectorRepository;
    private final JsonCodec jsonCodec;
    private final FileSystem fileSystem;
    private final Episode episode;
    private final Knowledge knowledge;
    private final Path phaseSource;
    private final Path sharedSource;

    @Inject
    public EncoderImpl(Repository<Area, Engram> areaRepository,
                       Repository<Neuron, Engram> neuronRepository,
                       Repository<Effector, Organ> effectorRepository,
                       JsonCodec jsonCodec,
                       @Localic FileSystem fileSystem,
                       Episode episode,
                       Knowledge knowledge) {
        this.areaRepository = areaRepository;
        this.neuronRepository = neuronRepository;
        this.effectorRepository = effectorRepository;
        this.jsonCodec = jsonCodec;
        this.fileSystem = fileSystem;
        this.episode = episode;
        this.knowledge = knowledge;
        var configuration = new Configuration("anticorruption.yaml");
        this.phaseSource = Path.of(configuration.get("anticorruption.encodings.phase"), "");
        this.sharedSource = Path.of(configuration.get("anticorruption.encodings.shared"), "");
    }

    @Override
    public String encode(Impulse impulse, Class<?> caller) {
        return switch (caller.getSimpleName()) {
            /// @formatter:off
            case "Cortex"      -> this.perception(impulse);
            case "Thalamus"    -> this.relay(impulse);
            case "Plasticity"  -> impulse != null ? this.potentiation(impulse) : this.pruning();
            case "Drive"       -> this.drive();
            default            -> throw new IllegalArgumentException("Unknown caller: " + caller.getSimpleName());
            /// @formatter:on
        };
    }

    private String perception(Impulse impulse) {
        var self = impulse.area();
        /// @formatter:off
        return this.assemble("perception.md", Map.of(
            "input",        impulse.signal(),
            "episode",      this.episode.retrieve().toString(),
            "knowledge",    this.knowledge.retrieve().toString(),
            "self",         this.self(self),
            "area_names",   this.areaNames(),
            "neurons",      this.marshal(self.neurons(), x -> Map.of(
                "name",         x.name(),
                "tuning",       x.tuning()
            )),
            "effectors",    this.marshal(self.effectors(), x -> Map.of(
                "name",         x.name(),
                "tuning",       x.tuning()))
        ));
        /// @formatter:on
    }

    private String relay(Impulse impulse) {
        /// @formatter:off
        return this.assemble("relay.md", Map.of(
            "input",        impulse.signal(),
            "episode",      this.episode.retrieve().toString(),
            "area_names",   this.areaNames(),
            "areas",        this.areasSummary()
        ));
        /// @formatter:on
    }

    private String potentiation(Impulse impulse) {
        var self = impulse.area();
        /// @formatter:off
        return this.assemble("potentiation.md", Map.of(
            "input",        impulse.signal(),
            "episode",      this.episode.retrieve().toString(),
            "knowledge",    this.knowledge.retrieve().toString(),
            "self",         this.self(self),
            "areas",        this.areas(),
            "neuron_names", this.neuronNames(),
            "neurons",      this.neurons(),
            "effectors",    this.effectors())
        );
        /// @formatter:on
    }

    private String pruning() {
        /// @formatter:off
        return this.assemble("pruning.md", Map.of(
            "areas",        this.areas(),
            "neurons",      this.neurons(),
            "effectors",    this.effectors()
        ));
        /// @formatter:on
    }

    private String drive() {
        /// @formatter:off
        return this.assemble("drive.md", Map.of(
            "episode",      this.episode.retrieve().toString(),
            "knowledge",    this.knowledge.retrieve().toString(),
            "areas",        this.areasSummary()
        ));
        /// @formatter:on
    }

    private String areaNames() {
        return this.areaRepository.findAll().stream()
            .map(Area::name).collect(joining(", "));
    }

    private String areasSummary() {
        return this.marshal(this.areaRepository.findAll(), x -> Map.of(
            "name", x.name(), "tuning", x.tuning()));
    }

    private String neuronNames() {
        return this.neuronRepository.findAll().stream()
            .map(Neuron::name).collect(joining(", "));
    }

    private String self(Area area) {
        return this.jsonCodec.marshal(Map.of(
            "name", area.name(), "tuning", area.tuning()));
    }

    private String areas() {
        return this.marshal(this.areaRepository.findAll(), x -> Map.of(
            "name", x.name(),
            "tuning", x.tuning(),
            "neurons", x.neurons().stream()
                .map(Neuron::name)
                .toList(),
            "effectors", x.effectors().stream()
                .map(Effector::name)
                .toList()));
    }

    private String neurons() {
        return this.marshal(this.neuronRepository.findAll(), x -> Map.of(
            "name", x.name(), "tuning", x.tuning()));
    }

    private String effectors() {
        return this.marshal(this.effectorRepository.findAll(), x -> Map.of(
            "name", x.name(), "tuning", x.tuning()));
    }

    private <T> String marshal(List<T> list, Function<T, Map<String, Object>> toMap) {
        if (list.isEmpty()) {
            return "None";
        }
        return this.jsonCodec.marshal(list.stream()
            .map(toMap)
            .toList());
    }

    private String assemble(String id, Map<String, Object> values) {
        return values.entrySet().stream()
            .reduce(this.readPhase(id)
                    .replace("{{guardrails}}", this.readShared("executive_control.md"))
                    .replace("{{output_integrity}}", this.readShared("output_integrity.md")),
                (x, y) -> x.replace("{{" + y.getKey() + "}}", String.valueOf(y.getValue())),
                (x, y) -> x);
    }

    private String readPhase(String id) {
        return this.fileSystem.read(this.phaseSource.resolve(id), StandardCharsets.UTF_8);
    }

    private String readShared(String id) {
        return this.fileSystem.read(this.sharedSource.resolve(id), StandardCharsets.UTF_8);
    }
}
