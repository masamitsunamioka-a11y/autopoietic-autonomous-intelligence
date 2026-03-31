package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.s;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Adapter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.CommandHandler;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Event;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.EventStore;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.AggregateNotFoundException;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

import java.io.UncheckedIOException;
import java.util.List;

@ApplicationScoped
public class AutopoieticCommandHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(AutopoieticCommandHandler.class);
    private final Adapter<Neuron> neuronAdapter;
    private final Adapter<Effector> effectorAdapter;
    private final EventStore eventStore;

    @Inject
    public AutopoieticCommandHandler(Adapter<Neuron> neuronAdapter,
                                     Adapter<Effector> effectorAdapter,
                                     EventStore eventStore) {
        this.neuronAdapter = neuronAdapter;
        this.effectorAdapter = effectorAdapter;
        this.eventStore = eventStore;
    }

    public void handle(@Observes CompensateNeural command) {
        this.onCompensate(command.payload());
    }

    public void handle(@Observes ConserveNeural command) {
        this.onConserve(command.payload());
    }

    private void onCompensate(Compensation compensation) {
        var now = System.currentTimeMillis();
        var area = this.sanitizeArea(compensation.newArea());
        this.saveArea(area, now);
        compensation.newEffectors()
            .forEach(x -> this.saveEffector(x, now));
        compensation.newNeurons().stream()
            .map(this::sanitizeNeuron)
            .forEach(x -> this.saveNeuron(x, now));
        compensation.newAreas().stream()
            .map(this::sanitizeArea)
            .forEach(x -> this.saveArea(x, now));
    }

    private void onConserve(Conservation conservation) {
        var now = System.currentTimeMillis();
        conservation.obsoleteAreas()
            .forEach(x -> this.save("neural/areas/" + x, new AreaRemoved(x, now, this.nextVersion("neural/areas/" + x)), -1));
        conservation.obsoleteNeurons()
            .forEach(x -> this.save("neural/neurons/" + x, new NeuronRemoved(x, now, this.nextVersion("neural/neurons/" + x)), -1));
        conservation.newNeurons().stream()
            .map(this::sanitizeNeuron)
            .forEach(x -> this.saveNeuron(x, now));
        conservation.newAreas().stream()
            .map(this::sanitizeArea)
            .forEach(x -> this.saveArea(x, now));
    }

    private void saveArea(NewArea area, long now) {
        var aggregateId = "neural/areas/" + area.id();
        try {
            var existing = this.eventStore.eventsForAggregate(aggregateId);
            var lastVersion = existing.getLast().version();
            var event = new AreaUpdated(area.id(), now, lastVersion + 1, area.tuning(), area.neurons());
            this.save(aggregateId, event, lastVersion);
        } catch (AggregateNotFoundException e) {
            var event = new AreaCreated(area.id(), now, 1, area.tuning(), area.neurons());
            this.save(aggregateId, event, 0);
        }
    }

    private void saveNeuron(NewNeuron neuron, long now) {
        var aggregateId = "neural/neurons/" + neuron.id();
        var version = this.nextVersion(aggregateId);
        var event = new NeuronCreated(neuron.id(), now, version, neuron.tuning(), neuron.effectors());
        this.save(aggregateId, event, version - 1);
    }

    private void saveEffector(NewEffector effector, long now) {
        var aggregateId = "neural/effectors/" + effector.id();
        var version = this.nextVersion(aggregateId);
        var event = new EffectorCreated(effector.id(), now, version, effector.program());
        this.save(aggregateId, event, version - 1);
    }

    private int nextVersion(String aggregateId) {
        try {
            var existing = this.eventStore.eventsForAggregate(aggregateId);
            return existing.getLast().version() + 1;
        } catch (AggregateNotFoundException e) {
            return 1;
        }
    }

    private void save(String aggregateId, Event event, int expectedVersion) {
        this.eventStore.save(aggregateId, List.of(event), expectedVersion);
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
