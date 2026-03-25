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
                impulse.signal(), this.label(),
                impulse.efferent()));
        this.nucleus.integrate(compensation, x -> {
            try {
                this.transform(x, this.areaRepository.find(impulse.efferent()));
                this.produce(x);
            } catch (Exception e) {
                logger.error("compensate failed", e);
            }
        });
    }

    @Override
    public void conserve() {
        var conservation = (Conservation) this.transmitter.call(
            new ImpulseImpl(null, this.label(), null));
        this.nucleus.integrate(conservation, x -> {
            try {
                this.destroy(x);
                this.produce(x);
            } catch (Exception e) {
                logger.error("conserve failed", e);
            }
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
        this.effectorRepository.storeAll(compensation.newEffectors());
        this.neuronRepository.storeAll(compensation.newNeurons());
        this.areaRepository.storeAll(compensation.newAreas().stream().map(this::sanitize).toList());
    }

    private void destroy(Conservation conservation) {
        this.areaRepository.removeAll(conservation.obsoleteAreas());
        this.neuronRepository.removeAll(conservation.obsoleteNeurons());
    }

    private void produce(Conservation conservation) {
        this.neuronRepository.storeAll(conservation.newNeurons());
        this.areaRepository.storeAll(conservation.newAreas().stream().map(this::sanitize).toList());
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

    private String label() {
        return Autopoiesis.class.getSimpleName();
    }
}
