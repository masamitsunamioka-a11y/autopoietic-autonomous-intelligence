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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlasticityImplTest {
    @Test
    void sproutSkipsAreaWithMissingNeuronReference() {
        var storedAreas = new ArrayList<String>();
        var plasticity = new PlasticityImpl(
            trackingAreaRepository(storedAreas),
            staticNeuronRepository(List.of()),
            staticEffectorRepository(List.of()),
            encoder(), nucleus(new Potentiation("r", 1.0, "",
            List.of(new Potentiation.Area("A1", "t", List.of("N-missing"), List.of())),
            List.of(), List.of()))
        );
        plasticity.potentiate(impulse());
        assertFalse(storedAreas.contains("A1"), "Area referencing missing neuron must not be stored");
    }

    @Test
    void sproutSkipsAreaWithMissingEffectorReference() {
        var storedAreas = new ArrayList<String>();
        var plasticity = new PlasticityImpl(
            trackingAreaRepository(storedAreas),
            staticNeuronRepository(List.of()),
            staticEffectorRepository(List.of()),
            encoder(), nucleus(new Potentiation("r", 1.0, "",
            List.of(new Potentiation.Area("A1", "t", List.of(), List.of("E-missing"))),
            List.of(), List.of()))
        );
        plasticity.potentiate(impulse());
        assertFalse(storedAreas.contains("A1"), "Area referencing missing effector must not be stored");
    }

    @Test
    void sproutStoresAreaReferencingNewNeuronInSameResponse() {
        var storedNeurons = new ArrayList<String>();
        var storedAreas = new ArrayList<String>();
        var plasticity = new PlasticityImpl(
            trackingAreaRepository(storedAreas),
            trackingNeuronRepository(storedNeurons),
            staticEffectorRepository(List.of()),
            encoder(), nucleus(new Potentiation("r", 1.0, "",
            List.of(new Potentiation.Area("A1", "t", List.of("N1"), List.of())),
            List.of(new Potentiation.Neuron("N1", "tuning")),
            List.of()))
        );
        plasticity.potentiate(impulse());
        assertTrue(storedNeurons.contains("N1"), "N1 created in same response must be stored");
        assertTrue(storedAreas.contains("A1"), "A1 referencing same-response N1 must be stored");
    }

    @Test
    void sproutStoresAreaWithAllExistingRefs() {
        var storedAreas = new ArrayList<String>();
        var plasticity = new PlasticityImpl(
            trackingAreaRepository(storedAreas),
            staticNeuronRepository(List.of(neuron("N-existing"))),
            staticEffectorRepository(List.of()),
            encoder(), nucleus(new Potentiation("r", 1.0, "",
            List.of(new Potentiation.Area("A1", "t", List.of("N-existing"), List.of())),
            List.of(), List.of()))
        );
        plasticity.potentiate(impulse());
        assertTrue(storedAreas.contains("A1"), "Area with all existing neuron refs must be stored");
    }

    @Test
    void reconcileStripsDanglingEffectorReference() {
        var stored = new ArrayList<String>();
        var removed = new ArrayList<String>();
        var existingArea = area("A1", List.of("N1"), List.of("E-gone"));
        var plasticity = new PlasticityImpl(
            reconcilingAreaRepository(List.of(existingArea), stored, removed),
            staticNeuronRepository(List.of(neuron("N1"))),
            staticEffectorRepository(List.of()),
            encoder(), nucleus(new Potentiation("r", 1.0, "",
            List.of(), List.of(), List.of()))
        );
        plasticity.potentiate(impulse());
        assertTrue(stored.contains("A1"), "Area must be re-stored with valid references");
        assertFalse(removed.contains("A1"), "Area with valid neurons must not be removed");
    }

    @Test
    void reconcileRemovesAreaWithNoValidNeurons() {
        var stored = new ArrayList<String>();
        var removed = new ArrayList<String>();
        var existingArea = area("A1", List.of("N-gone"), List.of());
        var plasticity = new PlasticityImpl(
            reconcilingAreaRepository(List.of(existingArea), stored, removed),
            staticNeuronRepository(List.of()),
            staticEffectorRepository(List.of()),
            encoder(), nucleus(new Potentiation("r", 1.0, "",
            List.of(), List.of(), List.of()))
        );
        plasticity.potentiate(impulse());
        assertTrue(removed.contains("A1"), "Area with no valid neurons must be removed");
    }

    /// @formatter:off
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
    private static Effector effector(String name) {
        return new Effector() {
            public String name() { return name; }
            public String tuning() { return ""; }
            public Map<String, Object> fire(Map<String, Object> input) { return Map.of(); }
        };
    }
    private static Neuron neuron(String name) {
        return new Neuron() {
            public String name() { return name; }
            public String tuning() { return ""; }
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
    private static Repository<Neuron, Engravable> staticNeuronRepository(List<Neuron> items) {
        return new Repository<>() {
            public Neuron find(String id) { throw new UnsupportedOperationException(); }
            public List<Neuron> findAll() { return items; }
            public void store(Engravable engravable) {}
            public void remove(String id) {}
        };
    }
    private static Repository<Neuron, Engravable> trackingNeuronRepository(List<String> stored) {
        var neurons = new ArrayList<Neuron>();
        return new Repository<>() {
            public Neuron find(String id) { throw new UnsupportedOperationException(); }
            public List<Neuron> findAll() { return List.copyOf(neurons); }
            public void store(Engravable engravable) {
                stored.add(engravable.name());
                var n = engravable.name();
                neurons.add(new Neuron() {
                    public String name() { return n; }
                    public String tuning() { return ""; }
                });
            }
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
    private static Area area(String name, List<String> neuronNames, List<String> effectorNames) {
        return new Area() {
            public String name() { return name; }
            public String tuning() { return "t"; }
            public List<String> neurons() { return neuronNames; }
            public List<String> effectors() { return effectorNames; }
        };
    }
    private static Repository<Area, Engravable> reconcilingAreaRepository(
            List<Area> existing, List<String> stored, List<String> removed) {
        return new Repository<>() {
            public Area find(String id) { throw new UnsupportedOperationException(); }
            public List<Area> findAll() { return existing; }
            public void store(Engravable engravable) { stored.add(engravable.name()); }
            public void remove(String id) { removed.add(id); }
        };
    }
    /// @formatter:on
}
