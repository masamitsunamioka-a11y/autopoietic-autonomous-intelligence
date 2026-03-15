package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import com.anthropic.models.messages.TextBlock;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Extern;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic.Compensation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic.Conservation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.Decision;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.Promotion;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.networking.Fluctuation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.networking.Projection;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.networking.Spindle;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Bindic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Diffusic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Releasic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.autopoietic.Autopoiesis;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.networking.Default;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.networking.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Potential;

import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

@Releasic
@Diffusic
@Bindic
@ApplicationScoped
public class TransmissionService implements Service<Impulse, Potential> {
    private static final Logger logger = LoggerFactory.getLogger(TransmissionService.class);
    private static final Map<String, Class<?>> CALLERS = Map.of(
        "Cortex", Cortex.class,
        "Thalamus", Thalamus.class,
        "Default", Default.class,
        "Autopoiesis", Autopoiesis.class,
        "Episode", Episode.class);
    private static final Map<String, Class<?>> RESPONSES = ofEntries(
        entry("Cortex", Decision.class),
        entry("Thalamus:relay", Projection.class),
        entry("Default", Fluctuation.class),
        entry("Autopoiesis:compensate", Compensation.class),
        entry("Autopoiesis:conserve", Conservation.class),
        entry("Episode", Promotion.class));
    private static final Set<Class<?>> SONNET_CALLERS =
        Set.of(Cortex.class, Autopoiesis.class);
    private static final Set<String> AUTOPOIESIS =
        Set.of("compensation.md", "conservation.md");
    private static final Set<String> INNATE_AREAS = Set.of(
        "broca_area",
        "dorsolateral_prefrontal_area",
        "ventrolateral_prefrontal_area");
    private final Knowledge knowledge;
    private final Episode episode;
    private final Repository<Area> areaRepository;
    private final Repository<Neuron> neuronRepository;
    private final Repository<Effector> effectorRepository;
    private final Serializer serializer;
    private final Extern sharedExtern;
    private final Extern functionalExtern;
    private final Validator validator;

    @Inject
    public TransmissionService(Knowledge knowledge, Episode episode,
                               Repository<Area> areaRepository,
                               Repository<Neuron> neuronRepository,
                               Repository<Effector> effectorRepository,
                               Serializer serializer) {
        this.knowledge = knowledge;
        this.episode = episode;
        this.areaRepository = areaRepository;
        this.neuronRepository = neuronRepository;
        this.effectorRepository = effectorRepository;
        this.serializer = serializer;
        var configuration = new Configuration();
        var s = configuration.synaptic().shared();
        var f = configuration.synaptic().function();
        this.sharedExtern =
            new LocalFileSystem(Path.of(s.get("source"), ""));
        this.functionalExtern =
            new LocalFileSystem(Path.of(f.get("source"), ""));
        try (var validatorFactory =
                 Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @Override
    public Potential call(Impulse impulse) {
        var afferent = impulse.afferent();
        var key = this.pathwayKey(impulse, afferent);
        var caller = CALLERS.get(afferent);
        if (caller == null) {
            return new Spindle();
        }
        var response = RESPONSES.get(key);
        if (response == null) {
            return new Spindle();
        }
        var signal = this.release(impulse, afferent);
        if (logger.isTraceEnabled()) {
            logger.trace("\n{}", signal);
        }
        var raw = this.diffuse(signal, caller);
        if (logger.isTraceEnabled()) {
            logger.trace("\n{}", raw);
        }
        return this.bind(raw, response);
    }

    private String release(Impulse impulse, String afferent) {
        return switch (afferent) {
            case "Cortex" -> this.perception(impulse);
            case "Thalamus" -> this.relay(impulse);
            case "Autopoiesis" -> impulse.signal() != null
                ? this.compensation(impulse)
                : this.conservation();
            case "Default" -> this.defaultMode();
            case "Episode" -> this.promotion();
            default -> throw new IllegalArgumentException();
        };
    }

    private String diffuse(String prompt, Class<?> caller) {
        var client = AnthropicOkHttpClient.fromEnv();
        var params = MessageCreateParams.builder()
            .model(this.modelFor(caller))
            .maxTokens(8192L)
            .addUserMessage(prompt)
            .build();
        var message = client.messages().create(params);
        return message.content().stream()
            .flatMap(x -> x.text().stream())
            .map(TextBlock::text)
            .findFirst()
            .orElse("");
    }

    private Potential bind(String signal, Class<?> response) {
        var result = this.serializer.deserialize(
            signal, response);
        this.validate(result);
        return (Potential) result;
    }

    private String pathwayKey(Impulse impulse, String afferent) {
        return switch (afferent) {
            case "Thalamus" -> impulse.signal() != null
                ? "Thalamus:relay"
                : "Thalamus:burst";
            case "Autopoiesis" -> impulse.signal() != null
                ? "Autopoiesis:compensate"
                : "Autopoiesis:conserve";
            default -> afferent;
        };
    }

    private String perception(Impulse impulse) {
        var self = this.areaRepository.find(impulse.efferent());
        return this.render(
            "perception.md",
            List.of(
                entry("input", impulse.signal()),
                entry("episode", this.episode.retrieve()),
                entry("knowledge", this.knowledge.retrieve()),
                entry("self", self),
                entry("neurons", this.neurons(self)),
                entry("effectors", this.effectors(self))),
            Set.of("areas"));
    }

    private String relay(Impulse impulse) {
        return this.render(
            "relay.md",
            List.of(
                entry("input", impulse.signal()),
                entry("episode", this.episode.retrieve())),
            Set.of("areas"));
    }

    private String compensation(Impulse impulse) {
        return this.render(
            "compensation.md",
            List.of(
                entry("input", impulse.signal()),
                entry("episode", this.episode.retrieve()),
                entry("knowledge", this.knowledge.retrieve()),
                entry("self", this.areaRepository.find(
                    impulse.efferent()))),
            Set.of("areas", "neurons", "effectors"));
    }

    private String conservation() {
        var areas = this.areaRepository.findAll().stream()
            .filter(x -> !INNATE_AREAS.contains(x.id()))
            .toList();
        return this.render(
            "conservation.md",
            List.of(entry("areas", areas)),
            Set.of("neurons", "effectors"));
    }

    private String promotion() {
        return this.render(
            "promotion.md",
            List.of(
                entry("episode", this.episode.retrieve()),
                entry("knowledge", this.knowledge.retrieve())),
            Set.of());
    }

    private String defaultMode() {
        return this.render(
            "default.md",
            List.of(
                entry("episode", this.episode.retrieve()),
                entry("knowledge", this.knowledge.retrieve())),
            Set.of("areas"));
    }

    private String render(String template,
                          List<Entry<String, Object>> bindings,
                          Set<String> repositories) {
        var guardrails =
            this.read(this.sharedExtern, "executive_control.md");
        var integrity =
            this.read(this.sharedExtern, "output_integrity.md");
        var isAutopoiesis = AUTOPOIESIS.contains(template);
        var prompt = bindings.stream().reduce(
            this.read(this.functionalExtern, template)
                .replace("{{guardrails}}", guardrails)
                .replace("{{output_integrity}}", integrity),
            (x, y) -> x.replace(
                "{{" + y.getKey() + "}}",
                this.stringify(
                    y.getKey(), y.getValue(), isAutopoiesis)),
            (x, y) -> x);
        return repositories.stream().reduce(
            prompt,
            (x, y) -> x.replace(
                "{{" + y + "}}",
                this.stringify(
                    y, this.findAll(y), isAutopoiesis)),
            (x, y) -> x);
    }

    private List<Neuron> neurons(Area area) {
        return area.neurons().stream()
            .map(this.neuronRepository::find)
            .filter(Objects::nonNull)
            .toList();
    }

    private List<Effector> effectors(Area area) {
        return area.effectors().stream()
            .map(this.effectorRepository::find)
            .filter(Objects::nonNull)
            .toList();
    }

    private List<?> findAll(String key) {
        return switch (key) {
            case "areas" -> this.areaRepository.findAll();
            case "neurons" -> this.neuronRepository.findAll();
            case "effectors" -> this.effectorRepository.findAll();
            default -> throw new IllegalArgumentException();
        };
    }

    private String stringify(String key, Object value,
                             boolean isAutopoiesis) {
        return switch (value) {
            case String x -> x;
            case Area x when key.equals("areas")
                && isAutopoiesis -> {
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
                    .map(y -> this.stringify(
                        key, y, isAutopoiesis))
                    .toList());
            }
            default -> throw new IllegalArgumentException();
        };
    }

    private String traces(List<?> list) {
        return this.serializer.serialize(
            list.stream()
                .map(x -> (Trace) x)
                .sorted(Comparator.comparing(
                    this::timestampOf))
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

    private Model modelFor(Class<?> caller) {
        return SONNET_CALLERS.contains(caller)
            ? Model.CLAUDE_SONNET_4_6
            : Model.CLAUDE_HAIKU_4_5_20251001;
    }

    private <T> void validate(T result) {
        var violations = this.validator.validate(result);
        if (!violations.isEmpty()) {
            throw new IllegalStateException();
        }
    }
}
