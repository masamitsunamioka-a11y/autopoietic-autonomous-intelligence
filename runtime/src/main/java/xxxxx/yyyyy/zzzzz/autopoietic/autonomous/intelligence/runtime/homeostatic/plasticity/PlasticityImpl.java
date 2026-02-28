package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.homeostatic.plasticity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Plasticity;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Encoder;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;

import static java.util.stream.Collectors.toSet;

@ApplicationScoped
public class PlasticityImpl implements Plasticity {
    private static final Logger logger = LoggerFactory.getLogger(PlasticityImpl.class);
    private final Nucleus nucleus;
    private final Encoder encoder;
    private final Repository<Area, Engram> areaRepository;
    private final Repository<Neuron, Engram> neuronRepository;
    private final Repository<Effector, Organ> effectorRepository;

    @Inject
    public PlasticityImpl(Nucleus nucleus, Encoder encoder,
                          Repository<Area, Engram> areaRepository,
                          Repository<Neuron, Engram> neuronRepository,
                          Repository<Effector, Organ> effectorRepository) {
        this.nucleus = nucleus;
        this.encoder = encoder;
        this.areaRepository = areaRepository;
        this.neuronRepository = neuronRepository;
        this.effectorRepository = effectorRepository;
    }

    @Override
    public void potentiate(Impulse impulse) {
        var signal = this.encoder.encode(impulse, Plasticity.class);
        var output = this.nucleus.integrate(signal, Potentiation.class);
        this.reinforce(output, impulse.area());
        this.sprout(output);
    }

    @Override
    public void prune() {
        var signal = this.encoder.encode(null, Plasticity.class);
        var output = this.nucleus.integrate(signal, Pruning.class);
        this.eliminate(output);
        this.consolidate(output);
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
