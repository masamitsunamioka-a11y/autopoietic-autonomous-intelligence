package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engravable;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Encoder;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Trace;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@ApplicationScoped
public class EncoderImpl implements Encoder {
    private static final Logger logger = LoggerFactory.getLogger(EncoderImpl.class);
    private final Knowledge knowledge;
    private final Episode episode;
    private final Repository<Area, Engravable> areaRepository;
    private final Repository<Neuron, Engravable> neuronRepository;
    private final Repository<Effector, Engravable> effectorRepository;
    private final Repository<String, String> templateRepository;
    private final Serializer serializer;

    @Inject
    public EncoderImpl(Knowledge knowledge, Episode episode,
                       Repository<Area, Engravable> areaRepository,
                       Repository<Neuron, Engravable> neuronRepository,
                       Repository<Effector, Engravable> effectorRepository,
                       Repository<String, String> templateRepository,
                       Serializer serializer) {
        this.knowledge = knowledge;
        this.episode = episode;
        this.areaRepository = areaRepository;
        this.neuronRepository = neuronRepository;
        this.effectorRepository = effectorRepository;
        this.templateRepository = templateRepository;
        this.serializer = serializer;
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
            "episode",      this.traces(this.episode.retrieve()),
            "knowledge",    this.traces(this.knowledge.retrieve()),
            "self",         this.self(self),
            "area_names",   this.areaNames(),
            "neurons",      this.serialize(self.neurons().stream()
                .map(this.neuronRepository::find)
                .filter(Objects::nonNull)
                .toList(), x -> Map.of(
                    "name",         x.name(),
                    "tuning",       x.tuning())),
            "effectors",    this.serialize(self.effectors().stream()
                .map(this.effectorRepository::find)
                .filter(Objects::nonNull)
                .toList(), x -> Map.of(
                    "name",         x.name(),
                    "tuning",       x.tuning())),
            "effector_names", self.effectors().stream()
                .map(this.effectorRepository::find)
                .filter(Objects::nonNull)
                .map(Effector::name)
                .collect(joining(", "))
        ));
        /// @formatter:on
    }

    private String relay(Impulse impulse) {
        /// @formatter:off
        return this.assemble("relay.md", Map.of(
            "input",        impulse.signal(),
            "episode",      this.traces(this.episode.retrieve()),
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
            "episode",      this.traces(this.episode.retrieve()),
            "knowledge",    this.traces(this.knowledge.retrieve()),
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
            "episode",      this.traces(this.episode.retrieve()),
            "knowledge",    this.traces(this.knowledge.retrieve()),
            "areas",        this.areasSummary()
        ));
        /// @formatter:on
    }

    private String traces(List<Trace> traces) {
        return this.serializer.serialize(
            traces.stream()
                .collect(Collectors.toMap(
                    Trace::cue,
                    Trace::content,
                    (x, y) -> y, LinkedHashMap::new)));
    }

    private String areaNames() {
        return this.areaRepository.findAll().stream()
            .map(Area::name)
            .collect(joining(", "));
    }

    private String areasSummary() {
        return this.serialize(this.areaRepository.findAll(), x -> Map.of(
            "name", x.name(),
            "tuning", x.tuning()));
    }

    private String neuronNames() {
        return this.neuronRepository.findAll().stream()
            .map(Neuron::name)
            .collect(joining(", "));
    }

    private String self(Area area) {
        return this.serializer.serialize(Map.of(
            "name", area.name(),
            "tuning", area.tuning()));
    }

    private String areas() {
        return this.serialize(this.areaRepository.findAll(), x -> Map.of(
            "name", x.name(),
            "tuning", x.tuning(),
            "neurons", x.neurons(),
            "effectors", x.effectors()));
    }

    private String neurons() {
        return this.serialize(this.neuronRepository.findAll(), x -> Map.of(
            "name", x.name(),
            "tuning", x.tuning()));
    }

    private String effectors() {
        return this.serialize(this.effectorRepository.findAll(), x -> Map.of(
            "name", x.name(),
            "tuning", x.tuning()));
    }

    private <T> String serialize(List<T> list, Function<T, Map<String, Object>> toMap) {
        if (list.isEmpty()) {
            return "None";
        }
        return this.serializer.serialize(list.stream()
            .map(toMap)
            .toList());
    }

    /// [Engineering] As detailed in docs/kandel.md
    private String assemble(String id, Map<String, Object> values) {
        var executiveControl = this.templateRepository.find("executive_control.md");
        var outputIntegrity = this.templateRepository.find("output_integrity.md");
        return values.entrySet().stream()
            .reduce(this.templateRepository.find(id)
                    .replace("{{guardrails}}", executiveControl)
                    .replace("{{output_integrity}}", outputIntegrity),
                (x, y) -> {
                    return x.replace(
                        "{{" + y.getKey() + "}}",
                        String.valueOf(y.getValue()));
                },
                (x, y) -> x);
    }
}
