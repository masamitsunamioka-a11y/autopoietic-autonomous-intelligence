package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic;

import org.junit.jupiter.api.Test;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.AggregateRoot;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Entity;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EncoderImplTest {
    @Test
    void perceptionShouldNotLeakEffectorsFromOtherAreas() {
        var e1 = effector("E1");
        var e2 = effector("E2");
        var n1 = neuron("N1");
        var n2 = neuron("N2");
        var a1 = area("A1", List.of("N1"), List.of("E1"));
        var a2 = area("A2", List.of("N2"), List.of("E2"));
        var encoder = new EncoderImpl(
            knowledge(), episode(),
            repository(a1, a2), repository(n1, n2),
            repository(e1, e2), assembleService());
        var result = encoder.encode(impulse("test", "A1"), Cortex.class);
        assertTrue(result.contains("E1"), "E1 should be in perception prompt of A1");
        assertFalse(result.contains("E2"), "E2 must not leak into perception prompt of A1");
    }

    /// @formatter:off
    private static Service<EncoderImpl.Input, String> assembleService() {
        return input -> input.bindings().stream()
            .map(x -> x.getValue() instanceof List<?> y
                ? y.stream().filter(z -> z instanceof Entity)
                    .map(z -> ((Entity) z).id()).collect(Collectors.joining(" "))
                : String.valueOf(x.getValue()))
            .collect(Collectors.joining(" "));
    }
    private static Effector effector(String name) {
        return new Effector() {
            public String id() { return name; }
            public String tuning() { return name + "-tuning"; }
            public Map<String, Object> fire(Map<String, Object> state) { return Map.of(); }
        };
    }
    private static Neuron neuron(String name) {
        return new Neuron() {
            public String id() { return name; }
            public String tuning() { return name + "-tuning"; }
        };
    }
    private static Area area(String name, List<String> neurons, List<String> effectors) {
        return new Area() {
            public String id() { return name; }
            public String tuning() { return name + "-tuning"; }
            public List<String> neurons() { return neurons; }
            public List<String> effectors() { return effectors; }
        };
    }
    @SafeVarargs
    private static <T extends AggregateRoot> Repository<T> repository(T... items) {
        var map = java.util.Arrays.stream(items)
            .collect(java.util.stream.Collectors.toMap(AggregateRoot::id, x -> x));
        return new Repository<>() {
            @SuppressWarnings("unchecked")
            public T find(String id) { return (T) map.get(id); }
            public List<T> findAll() { return List.of(items); }
            public void store(T t) { }
            public void remove(String id) { }
            public void removeAll(java.util.List<String> ids) { throw new UnsupportedOperationException(); }
            public boolean exists(String id) { throw new UnsupportedOperationException(); }
        };
    }
    private static Episode episode() {
        return new Episode() {
            public void encode(Trace trace) { }
            public Trace retrieve(String id) { return null; }
            public Map<String, Object> retrieve() { return Map.of(); }
            public void decay() { }
        };
    }
    private static Knowledge knowledge() {
        return new Knowledge() {
            public void encode(Trace trace) { }
            public Trace retrieve(String id) { return null; }
            public Map<String, Object> retrieve() { return Map.of(); }
            public void promote() { }
            public void decay() { }
        };
    }
    private static Impulse impulse(String signal, String direction) {
        return new Impulse() {
            public Object signal() { return signal; }
            public String direction() { return direction; }
        };
    }
    /// @formatter:on
}
