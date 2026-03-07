package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Extern;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.integrative.EncoderImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EncodingService implements Service<EncoderImpl.Input, String> {
    private static final Logger logger = LoggerFactory.getLogger(EncodingService.class);
    private static final Set<String> PLASTICITY = Set.of("potentiation.md", "pruning.md");
    private final Serializer serializer;
    private final Extern sharedExtern;
    private final Extern functionalExtern;
    private final Repository<Area> areaRepository;
    private final Repository<Neuron> neuronRepository;
    private final Repository<Effector> effectorRepository;

    public EncodingService(Serializer serializer,
                           Extern sharedExtern, Extern functionalExtern,
                           Repository<Area> areaRepository,
                           Repository<Neuron> neuronRepository,
                           Repository<Effector> effectorRepository) {
        this.serializer = serializer;
        this.sharedExtern = sharedExtern;
        this.functionalExtern = functionalExtern;
        this.areaRepository = areaRepository;
        this.neuronRepository = neuronRepository;
        this.effectorRepository = effectorRepository;
    }

    @Override
    public String call(EncoderImpl.Input input) {
        var template = input.template();
        var bindings = input.bindings();
        var repositories = input.repositories();
        var guardrails = this.read(this.sharedExtern, "executive_control.md");
        var integrity = this.read(this.sharedExtern, "output_integrity.md");
        var isPlasticity = PLASTICITY.contains(template);
        var prompt = bindings.stream().reduce(
            this.read(this.functionalExtern, template)
                .replace("{{guardrails}}", guardrails)
                .replace("{{output_integrity}}", integrity),
            (x, y) -> x.replace(
                "{{" + y.getKey() + "}}",
                this.stringify(y.getKey(), y.getValue(), isPlasticity)),
            (x, y) -> x);
        return repositories.stream().reduce(prompt,
            (x, y) -> x.replace(
                "{{" + y + "}}",
                this.stringify(y, this.findAll(y), isPlasticity)),
            (x, y) -> x);
    }

    private List<?> findAll(String key) {
        /// @formatter:off
        return switch (key) {
            case "areas"     -> this.areaRepository.findAll();
            case "neurons"   -> this.neuronRepository.findAll();
            case "effectors" -> this.effectorRepository.findAll();
            default -> throw new IllegalArgumentException(key);
        };
        /// @formatter:on
    }

    private String stringify(String key, Object value, boolean isPlasticity) {
        return switch (value) {
            case String x -> {
                yield x;
            }
            case Area x -> {
                yield this.serializer.serialize(Map.of(
                    "id", x.id(),
                    "tuning", x.tuning()));
            }
            case List<?> x when x.isEmpty() -> {
                yield "None";
            }
            case List<?> x when x.getFirst() instanceof Trace -> {
                yield this.traces(x);
            }
            case List<?> x when key.equals("areas") && isPlasticity -> {
                yield this.plasticityAreas(x);
            }
            default -> {
                yield this.summaries(value);
            }
        };
    }

    private String traces(List<?> list) {
        return this.serializer.serialize(
            list.stream()
                .map(x -> (Trace) x)
                .collect(Collectors.toMap(
                    Trace::id,
                    Trace::content,
                    (x, y) -> y)));
    }

    private String plasticityAreas(List<?> list) {
        return this.serializer.serialize(list.stream()
            .map(x -> (Area) x)
            .map(x -> Map.of(
                "id", x.id(),
                "tuning", x.tuning(),
                "neurons", (Object) x.neurons(),
                "effectors", x.effectors()))
            .toList());
    }

    private String summaries(Object object) {
        if (object instanceof List<?> list) {
            return this.serializer.serialize(list.stream()
                .map(this::summary)
                .toList());
        }
        return String.valueOf(object);
    }

    private Map<String, Object> summary(Object object) {
        /// @formatter:off
        return switch (object) {
            case Area y     -> Map.of("id", y.id(), "tuning", y.tuning());
            case Neuron y   -> Map.of("id", y.id(), "tuning", y.tuning());
            case Effector y -> Map.of("id", y.id(), "tuning", y.tuning());
            default         -> Map.of("id", String.valueOf(object));
        };
        /// @formatter:on
    }

    private String read(Extern extern, String name) {
        return extern.get(extern.resolve(name)).data();
    }
}
