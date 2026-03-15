package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.ImpulseImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Bindic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Diffusic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Releasic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.autopoietic.Autopoiesis;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Potential;

@ApplicationScoped
public class AutopoiesisImpl implements Autopoiesis {
    private static final Logger logger = LoggerFactory.getLogger(AutopoiesisImpl.class);
    private final Nucleus nucleus;
    private final Repository<Area> areaRepository;
    private final Repository<Neuron> neuronRepository;
    private final Repository<Effector> effectorRepository;
    private final Service<Impulse, Potential> transmitter;

    @Inject
    public AutopoiesisImpl(Nucleus nucleus,
                           Repository<Area> areaRepository,
                           Repository<Neuron> neuronRepository,
                           Repository<Effector> effectorRepository,
                           @Releasic @Diffusic @Bindic
                           Service<Impulse, Potential> transmitter) {
        this.nucleus = nucleus;
        this.areaRepository = areaRepository;
        this.neuronRepository = neuronRepository;
        this.effectorRepository = effectorRepository;
        this.transmitter = transmitter;
    }

    @Override
    public void compensate(Impulse impulse) {
        var compensation = (Compensation) this.transmitter.call(
            new ImpulseImpl(
                impulse.signal(), this.getClass(),
                impulse.efferent(), ((ImpulseImpl) impulse).mode()));
        this.nucleus.integrate(compensation, x -> {
            this.transform(x, this.areaRepository.find(impulse.efferent()));
            this.produce(x);
        });
    }

    @Override
    public void conserve() {
        var conservation = (Conservation) this.transmitter.call(
            new ImpulseImpl(null, this.getClass(), null, null));
        this.nucleus.integrate(conservation, x -> {
            this.destroy(x);
            this.produce(x);
        });
    }

    private void transform(Compensation compensation, Area area) {
        if (!compensation.newTuning().isEmpty()) {
            this.areaRepository.store(
                new NewArea(
                    area.id(),
                    compensation.newTuning(),
                    area.neurons(),
                    area.effectors()));
        }
    }

    private void produce(Compensation compensation) {
        compensation.newEffectors()
            .forEach(this.effectorRepository::store);
        compensation.newNeurons()
            .forEach(this.neuronRepository::store);
        compensation.newAreas().stream()
            .map(this::sanitize)
            .forEach(this.areaRepository::store);
    }

    private void destroy(Conservation conservation) {
        this.areaRepository.removeAll(
            conservation.mergedAreas().stream()
                .flatMap(x -> x.sources().stream())
                .toList());
        this.neuronRepository.removeAll(
            conservation.mergedNeurons().stream()
                .flatMap(x -> x.sources().stream())
                .toList());
    }

    private void produce(Conservation conservation) {
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
