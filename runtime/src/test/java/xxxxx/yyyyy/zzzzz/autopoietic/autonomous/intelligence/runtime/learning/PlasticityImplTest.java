package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.learning;

import org.junit.jupiter.api.Test;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.integrative.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.integrative.Transmitter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PlasticityImplTest {
    @Test
    void sproutStoresNewAreaAndNeuron() {
        var storedNeurons = new ArrayList<String>();
        var storedAreas = new ArrayList<String>();
        var potentiation = new Potentiation("r", 1.0, "",
            List.of(new Potentiation.NewArea("A1", "t", List.of("N1"), List.of())),
            List.of(new Potentiation.NewNeuron("N1", "tuning")),
            List.of());
        var plasticity = new PlasticityImpl(
            episode(),
            trackingAreaRepository(storedAreas),
            trackingNeuronRepository(storedNeurons),
            staticEffectorRepository(List.of()),
            transmitter(potentiation), nucleus()
        );
        plasticity.potentiate(impulse());
        assertTrue(storedNeurons.contains("N1"), "Neuron must be stored");
        assertTrue(storedAreas.contains("A1"), "Area must be stored");
    }

    /// @formatter:off
    private static Episode episode() {
        return new Episode() {
            public void encode(Trace trace) {}
            public Trace retrieve(String cue) { return null; }
            public List<Trace> retrieve() { return List.of(); }
            public void decay() {}
        };
    }
    private static Transmitter transmitter(Object result) {
        return new Transmitter() {
            @SuppressWarnings("unchecked")
            public <T> T transmit(Impulse i, Class<T> r) { return (T) result; }
        };
    }
    private static Nucleus nucleus() {
        return new Nucleus() {
            public <T> void integrate(T transmitted, Runnable propagation) { propagation.run(); }
        };
    }
    private static Impulse impulse() {
        return new Impulse() {
            public String signal() { return "test"; }
            public Area area() {
                return new Area() {
                    public String id() { return "current"; }
                    public String tuning() { return ""; }
                    public List<String> neurons() { return List.of(); }
                    public List<String> effectors() { return List.of(); }
                };
            }
        };
    }
    private static Repository<Area> trackingAreaRepository(List<String> stored) {
        return new Repository<>() {
            public Area find(String id) { throw new UnsupportedOperationException(); }
            public List<Area> findAll() { return List.of(); }
            public void store(Area area) { stored.add(area.id()); }
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
            public void remove(String id) {}
            public void removeAll(List<String> ids) { throw new UnsupportedOperationException(); }
            public boolean exists(String id) { return items.stream().anyMatch(e -> e.id().equals(id)); }
        };
    }
    /// @formatter:on
}
