package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.autopoietic.Autopoiesis;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Transmitter;

@ApplicationScoped
public class AutopoiesisImpl implements Autopoiesis {
    private static final Logger logger = LoggerFactory.getLogger(AutopoiesisImpl.class);
    private final Episode episode;
    private final Repository<Area> areaRepository;
    private final Repository<Neuron> neuronRepository;
    private final Repository<Effector> effectorRepository;
    private final Transmitter transmitter;
    private final Nucleus nucleus;

    @Inject
    public AutopoiesisImpl(Episode episode,
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
    public void compensate(Impulse impulse) {
        var compensation = this.transmitter.transmit(impulse, Compensation.class);
        this.nucleus.integrate(compensation, () -> {
            this.reinforce(compensation, impulse.area());
            this.sprout(compensation);
        });
    }

    @Override
    public void conserve() {
        var conservation = this.transmitter.transmit(null, Conservation.class);
        this.nucleus.integrate(conservation, () -> {
            this.eliminate(conservation);
            this.consolidate(conservation);
            this.episode.decay();
        });
    }

    private void reinforce(Compensation compensation, Area area) {
        if (!compensation.newTuning().isEmpty()) {
            this.areaRepository.store(new NewArea(
                area.id(),
                compensation.newTuning(),
                area.neurons(),
                area.effectors()));
        }
    }

    private void sprout(Compensation compensation) {
        compensation.newEffectors()
            .forEach(this.effectorRepository::store);
        compensation.newNeurons()
            .forEach(this.neuronRepository::store);
        compensation.newAreas().stream()
            .map(this::sanitize)
            .forEach(this.areaRepository::store);
    }

    private void eliminate(Conservation conservation) {
        this.areaRepository.removeAll(
            conservation.mergedAreas().stream()
                .flatMap(x -> x.sources().stream())
                .toList());
        this.neuronRepository.removeAll(
            conservation.mergedNeurons().stream()
                .flatMap(x -> x.sources().stream())
                .toList());
    }

    private void consolidate(Conservation conservation) {
        conservation.mergedNeurons().stream()
            .map(Conservation.MergedNeuron::newNeuron)
            .forEach(this.neuronRepository::store);
        conservation.mergedAreas().stream()
            .map(Conservation.MergedArea::newArea)
            .map(this::sanitize)
            .forEach(this.areaRepository::store);
    }

    private NewArea sanitize(NewArea area) {
        return new NewArea(
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
