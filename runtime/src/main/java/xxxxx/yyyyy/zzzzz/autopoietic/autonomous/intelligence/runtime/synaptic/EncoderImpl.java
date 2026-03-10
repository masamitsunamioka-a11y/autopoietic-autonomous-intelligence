package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Encoder;

import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import static java.util.Map.entry;

@ApplicationScoped
public class EncoderImpl implements Encoder {
    private static final Logger logger = LoggerFactory.getLogger(EncoderImpl.class);
    private final Knowledge knowledge;
    private final Episode episode;
    private final Repository<Neuron> neuronRepository;
    private final Repository<Effector> effectorRepository;
    private final Service<Input, String> encodicService;

    public record Input(
        String template,
        List<Entry<String, Object>> bindings,
        Set<String> repositories) {
    }

    @Inject
    public EncoderImpl(Knowledge knowledge, Episode episode,
                       Repository<Neuron> neuronRepository,
                       Repository<Effector> effectorRepository,
                       @Encodic Service<Input, String> encodicService) {
        this.knowledge = knowledge;
        this.episode = episode;
        this.neuronRepository = neuronRepository;
        this.effectorRepository = effectorRepository;
        this.encodicService = encodicService;
    }

    @Override
    public String encode(Impulse impulse, Class<?> caller) {
        return switch (caller.getSimpleName()) {
            /// @formatter:off
            case "Cortex"       -> this.perception(impulse);
            case "Thalamus"     -> this.relay(impulse);
            case "Autopoiesis"  -> {
                yield impulse != null
                    ? this.compensation(impulse)
                    : this.conservation();
            }
            case "Default"      -> this.defaultMode();
            case "Knowledge"    -> this.promotion();
            default -> throw new IllegalArgumentException();
            /// @formatter:on
        };
    }

    private String perception(Impulse impulse) {
        var self = impulse.area();
        return this.encodicService.call(new Input("perception.md",
            /// @formatter:off
            List.of(
                entry("input",      impulse.signal()),
                entry("episode",    this.episode.retrieve()),
                entry("knowledge",  this.knowledge.retrieve()),
                entry("self",       self),
                entry("neurons",    this.neurons(self)),
                entry("effectors",  this.effectors(self))),
            /// @formatter:on
            Set.of("areas")));
    }

    private String relay(Impulse impulse) {
        return this.encodicService.call(new Input("relay.md",
            /// @formatter:off
            List.of(
                entry("input",    impulse.signal()),
                entry("episode",  this.episode.retrieve())),
            /// @formatter:on
            Set.of("areas")));
    }

    private String compensation(Impulse impulse) {
        return this.encodicService.call(new Input("compensation.md",
            /// @formatter:off
            List.of(
                entry("input",      impulse.signal()),
                entry("episode",    this.episode.retrieve()),
                entry("knowledge",  this.knowledge.retrieve()),
                entry("self",       impulse.area())),
            /// @formatter:on
            Set.of("areas", "neurons", "effectors")));
    }

    private String conservation() {
        return this.encodicService.call(new Input("conservation.md",
            List.of(),
            Set.of("areas", "neurons", "effectors")));
    }

    private String promotion() {
        return this.encodicService.call(new Input("promotion.md",
            /// @formatter:off
            List.of(
                entry("episode",    this.episode.retrieve()),
                entry("knowledge",  this.knowledge.retrieve())),
            /// @formatter:on
            Set.of()));
    }

    private String defaultMode() {
        return this.encodicService.call(new Input("default.md",
            /// @formatter:off
            List.of(
                entry("episode",    this.episode.retrieve()),
                entry("knowledge",  this.knowledge.retrieve())),
            /// @formatter:on
            Set.of("areas")));
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
}
