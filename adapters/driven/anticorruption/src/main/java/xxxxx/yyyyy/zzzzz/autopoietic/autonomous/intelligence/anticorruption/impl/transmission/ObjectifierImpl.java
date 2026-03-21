package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.transmission;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Extern;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.LocalFileSystem;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@ApplicationScoped
public class ObjectifierImpl implements Objectifier {
    private static final Logger logger = LoggerFactory.getLogger(ObjectifierImpl.class);
    private final Knowledge knowledge;
    private final Episode episode;
    private final Repository<Area> areaRepository;
    private final Repository<Neuron> neuronRepository;
    private final Repository<Effector> effectorRepository;
    private final Extern sharedExtern;
    private static final Set<String> INNATE_AREAS = Set.of(
        "broca_area",
        "dorsolateral_prefrontal_area",
        "ventrolateral_prefrontal_area");

    @Inject
    public ObjectifierImpl(Knowledge knowledge, Episode episode,
                           Repository<Area> areaRepository,
                           Repository<Neuron> neuronRepository,
                           Repository<Effector> effectorRepository) {
        this.knowledge = knowledge;
        this.episode = episode;
        this.areaRepository = areaRepository;
        this.neuronRepository = neuronRepository;
        this.effectorRepository = effectorRepository;
        var configuration = new Configuration();
        this.sharedExtern =
            new LocalFileSystem(
                Path.of(configuration.synaptic().shared().get("source"), ""));
    }

    @Override
    public Object objectify(String key, Impulse impulse) {
        return switch (key) {
            case "guardrails" -> this.shared("executive_control.md");
            case "output_integrity" -> this.shared("output_integrity.md");
            case "input" -> impulse.signal();
            case "knowledge" -> this.knowledge.retrieve();
            case "episode" -> this.episode.retrieve();
            case "area_self" -> this.areaSelf(impulse);
            case "area_all" -> this.areaAll();
            case "area_non_innate" -> this.areaNonInnate();
            case "neuron_self" -> this.neuronSelf(impulse);
            case "neuron_all" -> this.neuronAll();
            case "effector_self" -> this.effectorSelf(impulse);
            case "effector_all" -> this.effectorAll();
            default -> throw new IllegalArgumentException();
        };
    }

    private String shared(String name) {
        return this.sharedExtern.get(
            this.sharedExtern.resolve(name)).content();
    }

    private Area areaSelf(Impulse impulse) {
        return this.areaRepository.find(impulse.efferent());
    }

    private List<Area> areaAll() {
        return this.areaRepository.findAll();
    }

    private List<Area> areaNonInnate() {
        return this.areaRepository.findAll().stream()
            .filter(x -> !INNATE_AREAS.contains(x.id()))
            .toList();
    }

    private List<Neuron> neuronSelf(Impulse impulse) {
        return this.areaSelf(impulse).neurons().stream()
            .map(this.neuronRepository::find)
            .filter(Objects::nonNull)
            .toList();
    }

    private List<Neuron> neuronAll() {
        return this.neuronRepository.findAll();
    }

    private List<Effector> effectorSelf(Impulse impulse) {
        return this.areaSelf(impulse).effectors().stream()
            .map(this.effectorRepository::find)
            .filter(Objects::nonNull)
            .toList();
    }

    private List<Effector> effectorAll() {
        return this.effectorRepository.findAll();
    }
}
