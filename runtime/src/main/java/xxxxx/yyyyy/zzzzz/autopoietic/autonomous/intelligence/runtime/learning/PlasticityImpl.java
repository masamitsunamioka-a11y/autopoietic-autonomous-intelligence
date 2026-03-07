package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.learning;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.integrative.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.integrative.Transmitter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.learning.Plasticity;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

@ApplicationScoped
public class PlasticityImpl implements Plasticity {
    private static final Logger logger = LoggerFactory.getLogger(PlasticityImpl.class);
    private final Episode episode;
    private final Repository<Area> areaRepository;
    private final Repository<Neuron> neuronRepository;
    private final Repository<Effector> effectorRepository;
    private final Transmitter transmitter;
    private final Nucleus nucleus;

    @Inject
    public PlasticityImpl(Episode episode,
                          Repository<Area> areaRepository,
                          Repository<Neuron> neuronRepository,
                          Repository<Effector> effectorRepository,
                          Transmitter transmitter,
                          Nucleus nucleus) {
        this.episode = episode;
        this.areaRepository = areaRepository;
        this.neuronRepository = neuronRepository;
        this.effectorRepository = effectorRepository;
        this.transmitter = transmitter;
        this.nucleus = nucleus;
    }

    @Override
    public void potentiate(Impulse impulse) {
        var potentiation = this.transmitter.transmit(impulse, Potentiation.class);
        this.nucleus.integrate(potentiation, () -> {
            this.reinforce(potentiation, impulse.area());
            this.sprout(potentiation);
        });
    }

    @Override
    public void prune() {
        var pruning = this.transmitter.transmit(null, Pruning.class);
        this.nucleus.integrate(pruning, () -> {
            this.eliminate(pruning);
            this.consolidate(pruning);
            this.episode.decay();
        });
    }

    private void reinforce(Potentiation potentiation, Area area) {
        if (!potentiation.newTuning().isEmpty()) {
            this.areaRepository.store(new Potentiation.Area(
                area.id(),
                potentiation.newTuning(),
                area.neurons(),
                area.effectors()));
        }
    }

    private void sprout(Potentiation potentiation) {
        potentiation.newEffectors()
            .forEach(this.effectorRepository::store);
        potentiation.newNeurons()
            .forEach(this.neuronRepository::store);
        potentiation.newAreas().stream()
            .map(this::sanitize)
            .forEach(this.areaRepository::store);
    }

    private void eliminate(Pruning pruning) {
        this.areaRepository.removeAll(
            pruning.mergedAreas().stream()
                .flatMap(x -> x.sources().stream())
                .toList());
        this.neuronRepository.removeAll(
            pruning.mergedNeurons().stream()
                .flatMap(x -> x.sources().stream())
                .toList());
    }

    private void consolidate(Pruning pruning) {
        pruning.mergedNeurons().stream()
            .map(Pruning.MergedNeuron::result)
            .forEach(this.neuronRepository::store);
        pruning.mergedAreas().stream()
            .map(Pruning.MergedArea::result)
            .map(this::sanitize)
            .forEach(this.areaRepository::store);
    }

    private Potentiation.Area sanitize(Potentiation.Area area) {
        return new Potentiation.Area(
            area.id(),
            area.tuning(),
            area.neurons().stream()
                .filter(this.neuronRepository::exists)
                .toList(),
            area.effectors().stream()
                .filter(this.effectorRepository::exists)
                .toList());
    }
}
