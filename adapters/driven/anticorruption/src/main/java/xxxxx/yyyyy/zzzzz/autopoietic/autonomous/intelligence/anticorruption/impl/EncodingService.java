package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Extern;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.EncoderImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EncodingService implements Service<EncoderImpl.Input, String> {
    private static final Logger logger = LoggerFactory.getLogger(EncodingService.class);
    private static final Set<String> AUTOPOIESIS = Set.of("compensation.md", "conservation.md");
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
        var isAutopoiesis = AUTOPOIESIS.contains(template);
        var prompt = bindings.stream().reduce(
            this.read(this.functionalExtern, template)
                .replace("{{guardrails}}", guardrails)
                .replace("{{output_integrity}}", integrity),
            (x, y) -> x.replace(
                "{{" + y.getKey() + "}}",
                this.stringify(y.getKey(), y.getValue(), isAutopoiesis)),
            (x, y) -> x);
        return repositories.stream().reduce(
            prompt,
            (x, y) -> x.replace(
                "{{" + y + "}}",
                this.stringify(y, this.findAll(y), isAutopoiesis)),
            (x, y) -> x);
    }

    private List<?> findAll(String key) {
        return switch (key) {
            case "areas" -> this.areaRepository.findAll();
            case "neurons" -> this.neuronRepository.findAll();
            case "effectors" -> this.effectorRepository.findAll();
            default -> throw new IllegalArgumentException(key);
        };
    }

    private String stringify(String key, Object value, boolean isAutopoiesis) {
        return switch (value) {
            case String x -> x;
            case Area x when key.equals("areas") && isAutopoiesis -> {
                yield this.serializer.serialize(Map.of(
                    "id", x.id(),
                    "tuning", x.tuning(),
                    "neurons", (Object) x.neurons(),
                    "effectors", x.effectors()));
            }
            case Area x -> this.serializer.serialize(
                Map.of("id", x.id(), "tuning", x.tuning()));
            case Neuron x -> this.serializer.serialize(
                Map.of("id", x.id(), "tuning", x.tuning()));
            case Effector x -> this.serializer.serialize(
                Map.of("id", x.id(), "tuning", x.tuning()));
            case Trace x -> this.serializer.serialize(
                Map.of(x.id(), x.content()));
            case Map<?, ?> x -> this.serializer.serialize(x);
            case List<?> x when x.isEmpty() -> "None";
            case List<?> x when x.getFirst() instanceof Trace -> {
                yield this.traces(x);
            }
            case List<?> x -> {
                yield this.serializer.serialize(x.stream()
                    .map(y -> this.stringify(key, y, isAutopoiesis))
                    .toList());
            }
            default -> throw new IllegalArgumentException(
                value.getClass().getName());
        };
    }

    private String traces(List<?> list) {
        return this.serializer.serialize(
            list.stream()
                .map(x -> (Trace) x)
                .sorted(Comparator.comparing(this::timestampOf))
                .collect(Collectors.toMap(
                    Trace::id,
                    Trace::content,
                    (x, y) -> y,
                    java.util.LinkedHashMap::new)));
    }

    private Instant timestampOf(Trace trace) {
        var at = trace.id().lastIndexOf('@');
        return Instant.parse(trace.id().substring(at + 1));
    }

    private String read(Extern extern, String name) {
        return extern.get(extern.resolve(name)).data();
    }
}
