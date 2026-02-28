package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import org.junit.jupiter.api.Test;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.homeostatic.plasticity.PlasticityImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.homeostatic.plasticity.Potentiation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.*;
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
            nucleus(new Potentiation("r", 1.0, "",
                List.of(new Potentiation.Area("A1", "t", List.of("N-missing"), List.of())),
                List.of(), List.of())),
            encoder(),
            trackingAreaRepository(storedAreas),
            staticNeuronRepository(List.of()),
            staticEffectorRepository(List.of())
        );
        plasticity.potentiate(impulse());
        assertFalse(storedAreas.contains("A1"), "Area referencing missing neuron must not be stored");
    }

    @Test
    void sproutSkipsAreaWithMissingEffectorReference() {
        var storedAreas = new ArrayList<String>();
        var plasticity = new PlasticityImpl(
            nucleus(new Potentiation("r", 1.0, "",
                List.of(new Potentiation.Area("A1", "t", List.of(), List.of("E-missing"))),
                List.of(), List.of())),
            encoder(),
            trackingAreaRepository(storedAreas),
            staticNeuronRepository(List.of()),
            staticEffectorRepository(List.of())
        );
        plasticity.potentiate(impulse());
        assertFalse(storedAreas.contains("A1"), "Area referencing missing effector must not be stored");
    }

    @Test
    void sproutStoresAreaReferencingNewNeuronInSameResponse() {
        var storedNeurons = new ArrayList<String>();
        var storedAreas = new ArrayList<String>();
        var plasticity = new PlasticityImpl(
            nucleus(new Potentiation("r", 1.0, "",
                List.of(new Potentiation.Area("A1", "t", List.of("N1"), List.of())),
                List.of(new Potentiation.Neuron("N1", "tuning")),
                List.of())),
            encoder(),
            trackingAreaRepository(storedAreas),
            trackingNeuronRepository(storedNeurons),
            staticEffectorRepository(List.of())
        );
        plasticity.potentiate(impulse());
        assertTrue(storedNeurons.contains("N1"), "N1 created in same response must be stored");
        assertTrue(storedAreas.contains("A1"), "A1 referencing same-response N1 must be stored");
    }

    @Test
    void sproutStoresAreaWithAllExistingRefs() {
        var storedAreas = new ArrayList<String>();
        var plasticity = new PlasticityImpl(
            nucleus(new Potentiation("r", 1.0, "",
                List.of(new Potentiation.Area("A1", "t", List.of("N-existing"), List.of())),
                List.of(), List.of())),
            encoder(),
            trackingAreaRepository(storedAreas),
            staticNeuronRepository(List.of(neuron("N-existing"))),
            staticEffectorRepository(List.of())
        );
        plasticity.potentiate(impulse());
        assertTrue(storedAreas.contains("A1"), "Area with all existing neuron refs must be stored");
    }

    /// @formatter:off
    private static Nucleus nucleus(Potentiation potentiation) {
        return new Nucleus() {
            @SuppressWarnings("unchecked")
            public <T> T integrate(String signal, Class<T> type) { return (T) potentiation; }
        };
    }
    private static Encoder encoder() {
        return (impulse, caller) -> "";
    }
    private static Impulse impulse() {
        return new Impulse() {
            public String signal() { return "test"; }
            public Area area() {
                return new Area() {
                    public String name() { return "current"; }
                    public String tuning() { return ""; }
                    public List<Neuron> neurons() { return List.of(); }
                    public List<Effector> effectors() { return List.of(); }
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
    private static Repository<Area, Engram> trackingAreaRepository(List<String> stored) {
        return new Repository<>() {
            public Area find(String id) { throw new UnsupportedOperationException(); }
            public List<Area> findAll() { return List.of(); }
            public void store(Engram engram) { stored.add(engram.name()); }
            public void remove(String id) {}
        };
    }
    private static Repository<Neuron, Engram> staticNeuronRepository(List<Neuron> items) {
        return new Repository<>() {
            public Neuron find(String id) { throw new UnsupportedOperationException(); }
            public List<Neuron> findAll() { return items; }
            public void store(Engram engram) {}
            public void remove(String id) {}
        };
    }
    private static Repository<Neuron, Engram> trackingNeuronRepository(List<String> stored) {
        var neurons = new ArrayList<Neuron>();
        return new Repository<>() {
            public Neuron find(String id) { throw new UnsupportedOperationException(); }
            public List<Neuron> findAll() { return List.copyOf(neurons); }
            public void store(Engram engram) {
                stored.add(engram.name());
                var n = engram.name();
                neurons.add(new Neuron() {
                    public String name() { return n; }
                    public String tuning() { return ""; }
                });
            }
            public void remove(String id) {}
        };
    }
    private static Repository<Effector, Organ> staticEffectorRepository(List<Effector> items) {
        return new Repository<>() {
            public Effector find(String id) { throw new UnsupportedOperationException(); }
            public List<Effector> findAll() { return items; }
            public void store(Organ organ) {}
            public void remove(String id) {}
        };
    }
    /// @formatter:on
}
