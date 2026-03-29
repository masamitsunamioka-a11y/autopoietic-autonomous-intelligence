package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

import java.io.UncheckedIOException;
import java.util.ArrayList;

@ApplicationScoped
public class AutopoieticCommandHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(AutopoieticCommandHandler.class);
    private final Adapter<Neuron> neuronAdapter;
    private final Adapter<Effector> effectorAdapter;
    private final EventStore eventStore;
    private final EventPublisher eventPublisher;

    @Inject
    public AutopoieticCommandHandler(Adapter<Neuron> neuronAdapter,
                                     Adapter<Effector> effectorAdapter,
                                     EventStore eventStore,
                                     EventPublisher eventPublisher) {
        this.neuronAdapter = neuronAdapter;
        this.effectorAdapter = effectorAdapter;
        this.eventStore = eventStore;
        this.eventPublisher = eventPublisher;
    }

    public void handle(@Observes CompensateNeural command) {
        this.onCompensate(command.payload());
    }

    public void handle(@Observes ConserveNeural command) {
        this.onConserve(command.payload());
    }

    private void onCompensate(Compensation compensation) {
        var events = new ArrayList<Event>();
        var now = System.currentTimeMillis();
        var area = this.sanitizeArea(compensation.newArea());
        events.add(new AreaCreated(area.id(), now, area.tuning(), area.neurons()));
        compensation.newEffectors().forEach(x -> events.add(new EffectorCreated(x.id(), now, x.program())));
        compensation.newNeurons().stream()
            .map(this::sanitizeNeuron)
            .forEach(x -> events.add(new NeuronCreated(x.id(), now, x.tuning(), x.effectors())));
        compensation.newAreas().stream()
            .map(this::sanitizeArea)
            .forEach(x -> events.add(new AreaCreated(x.id(), now, x.tuning(), x.neurons())));
        this.eventStore.save(events);
        events.forEach(this.eventPublisher::publish);
    }

    private void onConserve(Conservation conservation) {
        var events = new ArrayList<Event>();
        var now = System.currentTimeMillis();
        conservation.obsoleteAreas().forEach(x -> events.add(new AreaRemoved(x, now)));
        conservation.obsoleteNeurons().forEach(x -> events.add(new NeuronRemoved(x, now)));
        conservation.newNeurons().stream()
            .map(this::sanitizeNeuron)
            .forEach(neuron -> events.add(new NeuronCreated(neuron.id(), now, neuron.tuning(), neuron.effectors())));
        conservation.newAreas().stream()
            .map(this::sanitizeArea)
            .forEach(a -> events.add(new AreaCreated(a.id(), now, a.tuning(), a.neurons())));
        this.eventStore.save(events);
        events.forEach(this.eventPublisher::publish);
    }

    private NewArea sanitizeArea(NewArea area) {
        return new NewArea(area.id(), area.tuning(), area.neurons().stream().filter(this::neuronExists).toList());
    }

    private NewNeuron sanitizeNeuron(NewNeuron neuron) {
        return new NewNeuron(neuron.id(), neuron.tuning(), neuron.effectors().stream().filter(this::effectorExists).toList());
    }

    private boolean neuronExists(String id) {
        try {
            return this.neuronAdapter.fetch(id) != null;
        } catch (UncheckedIOException e) {
            return false;
        }
    }

    private boolean effectorExists(String id) {
        try {
            return this.effectorAdapter.fetch(id) != null;
        } catch (UncheckedIOException e) {
            return false;
        }
    }
}
