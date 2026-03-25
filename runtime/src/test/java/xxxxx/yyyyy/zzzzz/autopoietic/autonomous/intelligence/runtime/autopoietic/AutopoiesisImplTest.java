package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic;

import org.junit.jupiter.api.Test;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.ImpulseImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Potential;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AutopoiesisImplTest {
    @Test
    void sproutStoresNewAreaAndNeuron() {
        var storedNeurons = new ArrayList<String>();
        var storedAreas = new ArrayList<String>();
        var compensation = new Compensation("r", 1.0, "",
            List.of(new NewArea("A1", "t", List.of("N1"), List.of())),
            List.of(new NewNeuron("N1", "tuning")),
            List.of());
        var autopoiesis = new AutopoiesisImpl(
            nucleus(),
            trackingAreaRepository(storedAreas),
            trackingNeuronRepository(storedNeurons),
            staticEffectorRepository(List.of()),
            transmitter(compensation)
        );
        autopoiesis.compensate(impulse());
        assertTrue(storedNeurons.contains("N1"), "Neuron must be stored");
        assertTrue(storedAreas.contains("A1"), "Area must be stored");
    }

    /// @formatter:off
    private static Nucleus nucleus() {
        return new Nucleus() {
            @Override
            public <T extends Potential> void integrate(T potential, Consumer<T> f) {
                f.accept(potential);
            }
        };
    }
    private static Service<Impulse, Potential> transmitter(Potential result) {
        return x -> result;
    }
    private static Impulse impulse() {
        return new ImpulseImpl("test", "Autopoiesis", "current");
    }
    private static Repository<Area> trackingAreaRepository(List<String> stored) {
        return new Repository<>() {
            public Area find(String id) {
                return new Area() {
                    public String id() { return id; }
                    public String tuning() { return ""; }
                    public List<String> neurons() { return List.of(); }
                    public List<String> effectors() { return List.of(); }
                };
            }
            public List<Area> findAll() { return List.of(); }
            public void store(Area area) { stored.add(area.id()); }
            public void storeAll(List<? extends Area> objects) { objects.forEach(this::store); }
            public void remove(String id) {}
            public void removeAll(List<String> ids) {}
            public boolean exists(String id) { throw new UnsupportedOperationException(); }
        };
    }
    private static Repository<Neuron> trackingNeuronRepository(List<String> stored) {
        return new Repository<>() {
            public Neuron find(String id) { throw new UnsupportedOperationException(); }
            public List<Neuron> findAll() { return List.of(); }
            public void store(Neuron neuron) { stored.add(neuron.id()); }
            public void storeAll(List<? extends Neuron> objects) { objects.forEach(this::store); }
            public void remove(String id) {}
            public void removeAll(List<String> ids) {}
            public boolean exists(String id) { return stored.contains(id); }
        };
    }
    private static Repository<Effector> staticEffectorRepository(List<Effector> items) {
        return new Repository<>() {
            public Effector find(String id) { throw new UnsupportedOperationException(); }
            public List<Effector> findAll() { return items; }
            public void store(Effector effector) {}
            public void storeAll(List<? extends Effector> objects) { objects.forEach(this::store); }
            public void remove(String id) {}
            public void removeAll(List<String> ids) { throw new UnsupportedOperationException(); }
            public boolean exists(String id) { return items.stream().anyMatch(e -> e.id().equals(id)); }
        };
    }
    /// @formatter:on
}
