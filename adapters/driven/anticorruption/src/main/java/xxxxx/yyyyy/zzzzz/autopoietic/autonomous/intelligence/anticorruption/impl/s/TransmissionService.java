package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import com.anthropic.models.messages.TextBlock;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Extern;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.LocalFileSystem;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.r.MarkdownResource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic.Compensation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic.Conservation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.Decision;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.Consolidation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.networking.Fluctuation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.networking.Projection;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Bindic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Diffusic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Releasic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Entity;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

@Releasic
@Diffusic
@Bindic
@ApplicationScoped
public class TransmissionService implements Service<Impulse, Potential> {
    private static final Logger logger = LoggerFactory.getLogger(TransmissionService.class);
    private final Dispatcher dispatcher;
    private final Promptifier promptifier;
    private final Llm llm;
    private final Potentifier potentifier;

    private record Pathway(
        Class<?> caller,
        Class<?> response,
        String template
    ) {
    }

    @Inject
    public TransmissionService(Serializer serializer,
                               Knowledge knowledge, Episode episode,
                               Repository<Area> areaRepository,
                               Repository<Neuron> neuronRepository,
                               Repository<Effector> effectorRepository,
                               Validator validator) {
        this.dispatcher = new Dispatcher();
        this.promptifier = new Promptifier(serializer, knowledge, episode, areaRepository, neuronRepository, effectorRepository);
        this.llm = new Llm();
        this.potentifier = new Potentifier(serializer, validator);
    }

    @Override
    public Potential call(Impulse impulse) {
        var pathway = this.dispatcher.dispatch(impulse);
        var released = this.release(pathway, impulse);
        if (logger.isTraceEnabled()) {
            logger.trace("\n{}", released);
        }
        var diffused = this.diffuse(pathway, released);
        if (logger.isTraceEnabled()) {
            logger.trace("\n{}", diffused);
        }
        return this.bind(pathway, diffused);
    }

    private String release(Pathway pathway, Impulse impulse) {
        return this.promptifier.promptify(pathway.template(), impulse);
    }

    private String diffuse(Pathway pathway, String released) {
        return this.llm.call(released, pathway.caller());
    }

    private Potential bind(Pathway pathway, String diffused) {
        return this.potentifier.potentify(diffused, pathway.response());
    }

    private static class Dispatcher {
        private static final Map<String, Pathway> PATHWAYS = ofEntries(
            entry("Cortex", new Pathway(Cortex.class, Decision.class, "perception.md")),
            entry("Thalamus", new Pathway(Thalamus.class, Projection.class, "relay.md")),
            entry("Default", new Pathway(Default.class, Fluctuation.class, "default.md")),
            entry("Autopoiesis:compensate", new Pathway(Autopoiesis.class, Compensation.class, "compensation.md")),
            entry("Autopoiesis:conserve", new Pathway(Autopoiesis.class, Conservation.class, "conservation.md")),
            entry("Episode", new Pathway(Episode.class, Consolidation.class, "consolidation.md")));

        public Pathway dispatch(Impulse impulse) {
            return PATHWAYS.get(this.pathwayKey(impulse));
        }

        private String pathwayKey(Impulse impulse) {
            return switch (impulse.afferent()) {
                case "Autopoiesis" -> impulse.signal() != null
                    ? "Autopoiesis:compensate"
                    : "Autopoiesis:conserve";
                default -> impulse.afferent();
            };
        }
    }

    private static class Promptifier {
        private static final Pattern VARIABLE = Pattern.compile("\\{\\{(\\w+)}}");
        private static final Set<String> INNATE_AREAS = Set.of(
            "broca_area",
            "dorsolateral_prefrontal_area",
            "ventrolateral_prefrontal_area");
        private final Serializer serializer;
        private final Knowledge knowledge;
        private final Episode episode;
        private final Repository<Area> areaRepository;
        private final Repository<Neuron> neuronRepository;
        private final Repository<Effector> effectorRepository;
        private final Extern functionalExtern;
        private final Extern sharedExtern;

        public Promptifier(Serializer serializer,
                           Knowledge knowledge, Episode episode,
                           Repository<Area> areaRepository,
                           Repository<Neuron> neuronRepository,
                           Repository<Effector> effectorRepository) {
            this.serializer = serializer;
            this.knowledge = knowledge;
            this.episode = episode;
            this.areaRepository = areaRepository;
            this.neuronRepository = neuronRepository;
            this.effectorRepository = effectorRepository;
            var configuration = new Configuration();
            this.functionalExtern = new LocalFileSystem(Path.of(configuration.scope("synaptic").scope("function").get("path"), ""));
            this.sharedExtern = new LocalFileSystem(Path.of(configuration.scope("synaptic").scope("shared").get("path"), ""));
        }

        public String promptify(String template, Impulse impulse) {
            var function = this.function(template);
            var bindings = this.keys(function).stream()
                .collect(toMap(
                    k -> k,
                    k -> this.objectify(k, impulse),
                    (x, y) -> y));
            return this.bind(function, bindings);
        }

        private String objectify(String key, Impulse impulse) {
            var efferent = impulse.efferent();
            /// @formatter:off
            return switch (key) {
                case "guardrails"       -> this.shared("executive_control.md");
                case "output_integrity" -> this.shared("output_integrity.md");
                case "input"            -> impulse.signal();
                default -> this.serialize(switch (key) {
                    case "knowledge"        -> this.knowledge();
                    case "episode"          -> this.episode();
                    case "area_self"        -> this.areaSelf(efferent);
                    case "area_all"         -> this.areaAll();
                    case "area_non_innate"  -> this.areaNonInnate();
                    case "neuron_self"      -> this.neuronSelf(efferent);
                    case "neuron_all"       -> this.neuronAll();
                    case "effector_self"    -> this.effectorSelf(efferent);
                    case "effector_all"     -> this.effectorAll();
                    default -> throw new IllegalArgumentException();
                });
            };
            /// @formatter:on
        }

        private String function(String name) {
            return ((MarkdownResource) this.functionalExtern.get(this.functionalExtern.resolve(name))).content();
        }

        private List<String> keys(String template) {
            return VARIABLE.matcher(template).results()
                .map(x -> x.group(1))
                .toList();
        }

        private String bind(String function, Map<String, String> bindings) {
            return bindings.entrySet().stream()
                .reduce(function,
                    (x, y) -> x.replace("{{" + y.getKey() + "}}", y.getValue()),
                    (x, y) -> x);
        }

        private String shared(String name) {
            return ((MarkdownResource) this.sharedExtern.get(this.sharedExtern.resolve(name))).content();
        }

        private List<Trace> knowledge() {
            return this.knowledge.retrieve();
        }

        private List<Trace> episode() {
            return this.episode.retrieve();
        }

        private List<Area> areaSelf(String efferent) {
            return List.of(this.areaRepository.find(efferent));
        }

        private List<Area> areaAll() {
            return this.areaRepository.findAll();
        }

        private List<Area> areaNonInnate() {
            return this.areaRepository.findAll().stream()
                .filter(x -> !INNATE_AREAS.contains(x.id()))
                .toList();
        }

        private List<Neuron> neuronSelf(String efferent) {
            return this.areaRepository.find(efferent).neurons().stream()
                .map(this.neuronRepository::find)
                .filter(Objects::nonNull)
                .toList();
        }

        private List<Neuron> neuronAll() {
            return this.neuronRepository.findAll();
        }

        private List<Effector> effectorSelf(String efferent) {
            return this.areaRepository.find(efferent).neurons().stream()
                .map(this.neuronRepository::find).filter(Objects::nonNull)
                .flatMap(x -> x.effectors().stream())
                .map(this.effectorRepository::find).filter(Objects::nonNull)
                .toList();
        }

        private List<Effector> effectorAll() {
            return this.effectorRepository.findAll();
        }

        private String serialize(List<? extends Entity> entities) {
            return entities.isEmpty()
                ? "None"
                : this.serializer.serialize(entities);
        }
    }

    private static class Llm {
        private static final Set<Class<?>> SONNET_CALLERS = Set.of(Cortex.class, Autopoiesis.class);
        private final AnthropicClient client;

        public Llm() {
            this.client = AnthropicOkHttpClient.fromEnv();
        }

        public String call(String prompt, Class<?> caller) {
            var params = MessageCreateParams.builder()
                .model(this.modelFor(caller))
                .maxTokens(8192L)
                .addUserMessage(prompt)
                .build();
            var message = this.client.messages().create(params);
            return message.content().stream()
                .flatMap(x -> x.text().stream())
                .map(TextBlock::text)
                .findFirst()
                .orElse("");
        }

        private Model modelFor(Class<?> caller) {
            return SONNET_CALLERS.contains(caller)
                ? Model.CLAUDE_SONNET_4_6
                : Model.CLAUDE_HAIKU_4_5_20251001;
        }
    }

    private static class Potentifier {
        private final Serializer serializer;
        private final Validator validator;

        public Potentifier(Serializer serializer, Validator validator) {
            this.serializer = serializer;
            this.validator = validator;
        }

        public Potential potentify(String raw, Class<?> response) {
            var mapped = raw.replace("\"confidence\"", "\"amplitude\"");
            var result = this.serializer.deserialize(mapped, response);
            this.validate(result);
            return (Potential) result;
        }

        private <T> void validate(T result) {
            var violations = this.validator.validate(result);
            if (!violations.isEmpty()) {
                throw new IllegalStateException();
            }
            switch (result) {
                case Decision x -> this.validateDecision(x);
                case Fluctuation x -> this.validateFluctuation(x);
                default -> {
                }
            }
        }

        private void validateDecision(Decision decision) {
            switch (decision.process().toUpperCase()) {
                case "VOCALIZE", "INHIBIT" -> requireNonNull(decision.response(), "response required");
                case "FIRE" -> requireNonNull(decision.effector(), "effector required");
                case "PROJECT" -> requireNonNull(decision.area(), "area required");
                default -> {
                }
            }
        }

        private void validateFluctuation(Fluctuation fluctuation) {
            if (fluctuation.aroused()) {
                requireNonNull(fluctuation.signal(), "signal required");
            }
        }
    }
}
