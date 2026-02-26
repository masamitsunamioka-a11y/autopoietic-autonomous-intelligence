package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import org.junit.jupiter.api.Test;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.Transducer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.plasticity.PlasticityImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.plasticity.Potentiation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engram;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Module;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlasticityImplTest {
    @Test
    void sproutSkipsNeuronWithMissingModuleReference() {
        var storedNeurons = new ArrayList<String>();
        var plasticity = new PlasticityImpl(
            nucleus(new Potentiation("r", 1.0, "",
                List.of(new Potentiation.Neuron("N1", "f", "d", List.of("M-missing"))),
                List.of(), List.of())),
            transducer(),
            trackingNeuronRepository(storedNeurons),
            staticModuleRepository(List.of()),
            staticEffectorRepository(List.of())
        );
        plasticity.potentiate(stimulus());
        assertFalse(storedNeurons.contains("N1"), "Neuron referencing missing module must not be stored");
    }

    @Test
    void sproutSkipsModuleWithMissingEffectorReference() {
        var storedModules = new ArrayList<String>();
        var plasticity = new PlasticityImpl(
            nucleus(new Potentiation("r", 1.0, "",
                List.of(),
                List.of(new Potentiation.Module("M1", "f", "d", List.of("E-missing"))),
                List.of())),
            transducer(),
            staticNeuronRepository(List.of()),
            trackingModuleRepository(storedModules),
            staticEffectorRepository(List.of())
        );
        plasticity.potentiate(stimulus());
        assertFalse(storedModules.contains("M1"), "Module referencing missing effector must not be stored");
    }

    @Test
    void sproutStoresNeuronReferencingNewModuleInSameResponse() {
        var storedModules = new ArrayList<String>();
        var storedNeurons = new ArrayList<String>();
        var plasticity = new PlasticityImpl(
            nucleus(new Potentiation("r", 1.0, "",
                List.of(new Potentiation.Neuron("N1", "f", "d", List.of("M1"))),
                List.of(new Potentiation.Module("M1", "f", "d", List.of("E1"))),
                List.of())),
            transducer(),
            trackingNeuronRepository(storedNeurons),
            trackingModuleRepository(storedModules),
            staticEffectorRepository(List.of(effector("E1")))
        );
        plasticity.potentiate(stimulus());
        assertTrue(storedModules.contains("M1"), "M1 created in same response must be stored");
        assertTrue(storedNeurons.contains("N1"), "N1 referencing same-response M1 must be stored");
    }

    @Test
    void sproutStoresNeuronWithAllExistingModules() {
        var storedNeurons = new ArrayList<String>();
        var plasticity = new PlasticityImpl(
            nucleus(new Potentiation("r", 1.0, "",
                List.of(new Potentiation.Neuron("N1", "f", "d", List.of("M-existing"))),
                List.of(), List.of())),
            transducer(),
            trackingNeuronRepository(storedNeurons),
            staticModuleRepository(List.of(module("M-existing"))),
            staticEffectorRepository(List.of())
        );
        plasticity.potentiate(stimulus());
        assertTrue(storedNeurons.contains("N1"), "Neuron with all existing module refs must be stored");
    }

    /// @formatter:off
    private static Nucleus nucleus(Potentiation potentiation) {
        return new Nucleus() {
            @SuppressWarnings("unchecked")
            public <T> T compute(String signal, Class<T> type) { return (T) potentiation; }
        };
    }
    private static Transducer transducer() {
        return new Transducer() {
            public String perception(Stimulus s) { return ""; }
            public String relay(Stimulus s) { return ""; }
            public String potentiation(Stimulus s) { return ""; }
            public String pruning() { return ""; }
            public String drive() { return ""; }
        };
    }
    private static Stimulus stimulus() {
        return new Stimulus() {
            public String input() { return "test"; }
            public Neuron neuron() {
                return new Neuron() {
                    public String name() { return "current"; }
                    public String function() { return ""; }
                    public String disposition() { return ""; }
                    public List<Module> modules() { return List.of(); }
                };
            }
        };
    }
    private static Effector effector(String name) {
        return new Effector() {
            public String name() { return name; }
            public String function() { return ""; }
            public Map<String, Object> fire(Map<String, Object> input) { return Map.of(); }
        };
    }
    private static Module module(String name) {
        return new Module() {
            public String name() { return name; }
            public String function() { return ""; }
            public String disposition() { return ""; }
            public List<Effector> effectors() { return List.of(); }
        };
    }
    private static Repository<Neuron> staticNeuronRepository(List<Neuron> items) {
        return new Repository<>() {
            public Neuron find(String id) { throw new UnsupportedOperationException(); }
            public List<Neuron> findAll() { return items; }
            public void store(Engram e) {}
            public void remove(String id) {}
        };
    }
    private static Repository<Neuron> trackingNeuronRepository(List<String> stored) {
        return new Repository<>() {
            public Neuron find(String id) { throw new UnsupportedOperationException(); }
            public List<Neuron> findAll() { return List.of(); }
            public void store(Engram e) { stored.add(e.name()); }
            public void remove(String id) {}
        };
    }
    private static Repository<Module> staticModuleRepository(List<Module> items) {
        return new Repository<>() {
            public Module find(String id) { throw new UnsupportedOperationException(); }
            public List<Module> findAll() { return items; }
            public void store(Engram e) {}
            public void remove(String id) {}
        };
    }
    private static Repository<Module> trackingModuleRepository(List<String> stored) {
        var modules = new ArrayList<Module>();
        return new Repository<>() {
            public Module find(String id) { throw new UnsupportedOperationException(); }
            public List<Module> findAll() { return List.copyOf(modules); }
            public void store(Engram e) {
                stored.add(e.name());
                var n = e.name();
                modules.add(new Module() {
                    public String name() { return n; }
                    public String function() { return ""; }
                    public String disposition() { return ""; }
                    public List<Effector> effectors() { return List.of(); }
                });
            }
            public void remove(String id) {}
        };
    }
    private static Repository<Effector> staticEffectorRepository(List<Effector> items) {
        return new Repository<>() {
            public Effector find(String id) { throw new UnsupportedOperationException(); }
            public List<Effector> findAll() { return items; }
            public void store(Engram e) {}
            public void remove(String id) {}
        };
    }
    /// @formatter:on
}
