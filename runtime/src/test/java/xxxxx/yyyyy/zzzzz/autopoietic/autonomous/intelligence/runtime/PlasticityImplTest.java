package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import org.junit.jupiter.api.Test;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.learning.PlasticityImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.learning.Potentiation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engravable;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Encoder;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Trace;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PlasticityImplTest {
    @Test
    void sproutStoresNewAreaAndNeuron() {
        var storedNeurons = new ArrayList<String>();
        var storedAreas = new ArrayList<String>();
        var plasticity = new PlasticityImpl(
            episode(),
            trackingAreaRepository(storedAreas),
            trackingNeuronRepository(storedNeurons),
            staticEffectorRepository(List.of()),
            encoder(), nucleus(new Potentiation("r", 1.0, "",
            List.of(new Potentiation.Area("A1", "t", List.of("N1"), List.of())),
            List.of(new Potentiation.Neuron("N1", "tuning")),
            List.of()))
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
    private static Encoder encoder() {
        return (impulse, caller) -> "";
    }
    private static Nucleus nucleus(Potentiation potentiation) {
        return new Nucleus() {
            @SuppressWarnings("unchecked")
            public <T> T integrate(Impulse impulse, Class<T> type) { return (T) potentiation; }
        };
    }
    private static Impulse impulse() {
        return new Impulse() {
            public String signal() { return "test"; }
            public Area area() {
                return new Area() {
                    public String name() { return "current"; }
                    public String tuning() { return ""; }
                    public List<String> neurons() { return List.of(); }
                    public List<String> effectors() { return List.of(); }
                };
            }
        };
    }
    private static Repository<Area, Engravable> trackingAreaRepository(List<String> stored) {
        return new Repository<>() {
            public Area find(String id) { throw new UnsupportedOperationException(); }
            public List<Area> findAll() { return List.of(); }
            public void store(Engravable engravable) { stored.add(engravable.name()); }
            public void remove(String id) {}
        };
    }
    private static Repository<Neuron, Engravable> trackingNeuronRepository(List<String> stored) {
        return new Repository<>() {
            public Neuron find(String id) { throw new UnsupportedOperationException(); }
            public List<Neuron> findAll() { return List.of(); }
            public void store(Engravable engravable) { stored.add(engravable.name()); }
            public void remove(String id) {}
        };
    }
    private static Repository<Effector, Engravable> staticEffectorRepository(List<Effector> items) {
        return new Repository<>() {
            public Effector find(String id) { throw new UnsupportedOperationException(); }
            public List<Effector> findAll() { return items; }
            public void store(Engravable engravable) {}
            public void remove(String id) {}
        };
    }
    /// @formatter:on
}
