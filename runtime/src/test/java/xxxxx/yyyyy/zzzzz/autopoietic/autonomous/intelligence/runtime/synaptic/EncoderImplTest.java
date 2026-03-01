package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic;

import org.junit.jupiter.api.Test;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Trace;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EncoderImplTest {
    /// A1->E1 and A2->E2: perception for A1 must not include E2
    @Test
    void perceptionShouldNotLeakEffectorsFromOtherAreas() {
        var e1 = effector("E1");
        var e2 = effector("E2");
        var n1 = neuron("N1");
        var n2 = neuron("N2");
        var a1 = area("A1", List.of(n1), List.of(e1));
        var a2 = area("A2", List.of(n2), List.of(e2));
        var encoder = new EncoderImpl(
            knowledge(), episode(),
            repository(a1, a2), repository(n1, n2), repository(e1, e2),
            templateRepository(), serializer());
        var result = encoder.encode(impulse("test", a1), Cortex.class);
        assertTrue(result.contains("E1"), "E1 should be in perception prompt of A1");
        assertFalse(result.contains("E2"), "E2 must not leak into perception prompt of A1");
    }

    /// @formatter:off
    private static Effector effector(String name) {
        return new Effector() {
            public String name() { return name; }
            public String tuning() { return name + "-tuning"; }
            public Map<String, Object> fire(Map<String, Object> state) { return Map.of(); }
        };
    }
    private static Neuron neuron(String name) {
        return new Neuron() {
            public String name() { return name; }
            public String tuning() { return name + "-tuning"; }
        };
    }
    private static Area area(String name, List<Neuron> neurons, List<Effector> effectors) {
        return new Area() {
            public String name() { return name; }
            public String tuning() { return name + "-tuning"; }
            public List<Neuron> neurons() { return neurons; }
            public List<Effector> effectors() { return effectors; }
        };
    }
    @SafeVarargs
    private static <T, E> Repository<T, E> repository(T... items) {
        return new Repository<T, E>() {
            public T find(String id) { throw new UnsupportedOperationException(); }
            public List<T> findAll() { return List.of(items); }
            public void store(E e) { }
            public void remove(String id) { }
        };
    }
    private static Repository<String, String> templateRepository() {
        return new Repository<>() {
            public String find(String id) { return "{{neurons}}{{effectors}}"; }
            public List<String> findAll() { return List.of(); }
            public void store(String content) { }
            public void remove(String id) { }
        };
    }
    private static Serializer serializer() {
        return new Serializer() {
            public <T> T deserialize(String json, Type type) {
                throw new UnsupportedOperationException();
            }
            public String serialize(Object object) { return String.valueOf(object); }
        };
    }
    private static Episode episode() {
        return new Episode() {
            public void encode(Trace trace) { }
            public Trace retrieve(String cue) { return null; }
            public List<Trace> retrieve() { return List.of(); }
            public void decay() { }
        };
    }
    private static Knowledge knowledge() {
        return new Knowledge() {
            public void encode(Trace trace) { }
            public Trace retrieve(String cue) { return null; }
            public List<Trace> retrieve() { return List.of(); }
            public void decay() { }
        };
    }
    private static Impulse impulse(String signal, Area area) {
        return new Impulse() {
            public String signal() { return signal; }
            public Area area() { return area; }
        };
    }
    /// @formatter:on
}
