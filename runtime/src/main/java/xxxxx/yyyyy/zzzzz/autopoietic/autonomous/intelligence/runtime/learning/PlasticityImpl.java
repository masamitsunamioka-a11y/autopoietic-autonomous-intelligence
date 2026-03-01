package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.learning;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.learning.Plasticity;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engravable;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Encoder;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;

import static java.util.stream.Collectors.toSet;

@ApplicationScoped
public class PlasticityImpl implements Plasticity {
    private static final Logger logger = LoggerFactory.getLogger(PlasticityImpl.class);
    private final Repository<Area, Engravable> areaRepository;
    private final Repository<Neuron, Engravable> neuronRepository;
    private final Repository<Effector, Engravable> effectorRepository;
    private final Encoder encoder;
    private final Nucleus nucleus;

    @Inject
    public PlasticityImpl(Repository<Area, Engravable> areaRepository,
                          Repository<Neuron, Engravable> neuronRepository,
                          Repository<Effector, Engravable> effectorRepository,
                          Encoder encoder, Nucleus nucleus) {
        this.areaRepository = areaRepository;
        this.neuronRepository = neuronRepository;
        this.effectorRepository = effectorRepository;
        this.encoder = encoder;
        this.nucleus = nucleus;
    }

    @Override
    public void potentiate(Impulse impulse) {
        var output = this.integrate(impulse);
        this.reinforce(output, impulse.area());
        this.sprout(output);
    }

    @Override
    public void prune() {
        var output = this.integrate();
        this.eliminate(output);
        this.consolidate(output);
    }

    private Potentiation integrate(Impulse impulse) {
        var signal = this.encoder.encode(impulse, Plasticity.class);
        return this.nucleus.integrate(Impulse.of(signal, impulse.area()), Potentiation.class);
    }

    private Pruning integrate() {
        var signal = this.encoder.encode(null, Plasticity.class);
        return this.nucleus.integrate(Impulse.of(signal, null), Pruning.class);
    }

    private void reinforce(Potentiation potentiation, Area area) {
        if (!potentiation.newTuning().isEmpty()) {
            this.areaRepository.store(new Potentiation.Area(
                area.name(),
                potentiation.newTuning(),
                area.neurons().stream()
                    .map(Neuron::name)
                    .toList(),
                area.effectors().stream()
                    .map(Effector::name)
                    .toList()));
        }
    }

    private void sprout(Potentiation potentiation) {
        potentiation.newEffectors().forEach(this.effectorRepository::store);
        potentiation.newNeurons().forEach(this.neuronRepository::store);
        potentiation.newAreas().stream()
            .filter(a -> this.allNeuronsExist(a) && this.allEffectorsExist(a))
            .forEach(this.areaRepository::store);
    }

    private void eliminate(Pruning pruning) {
        pruning.mergedAreas().stream()
            .flatMap(x -> x.sources().stream()).forEach(this.areaRepository::remove);
        pruning.mergedNeurons().stream()
            .flatMap(x -> x.sources().stream()).forEach(this.neuronRepository::remove);
    }

    private void consolidate(Pruning pruning) {
        pruning.mergedAreas().stream()
            .map(Pruning.MergedArea::result).forEach(this.areaRepository::store);
        pruning.mergedNeurons().stream()
            .map(Pruning.MergedNeuron::result).forEach(this.neuronRepository::store);
    }

    private boolean allNeuronsExist(Potentiation.Area area) {
        var known = this.neuronRepository.findAll().stream()
            .map(Neuron::name)
            .collect(toSet());
        var missing = area.neurons().stream()
            .filter(n -> !known.contains(n))
            .toList();
        if (!missing.isEmpty())
            logger.warn("[PLASTICITY] Area '{}' skipped: missing neurons {}", area.name(), missing);
        return missing.isEmpty();
    }

    private boolean allEffectorsExist(Potentiation.Area area) {
        var known = this.effectorRepository.findAll().stream()
            .map(Effector::name)
            .collect(toSet());
        var missing = area.effectors().stream()
            .filter(n -> !known.contains(n))
            .toList();
        if (!missing.isEmpty())
            logger.warn("[PLASTICITY] Area '{}' skipped: missing effectors {}", area.name(), missing);
        return missing.isEmpty();
    }
}
