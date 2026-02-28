package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import org.junit.jupiter.api.Test;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.synaptic.EncoderImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Knowledge;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
            repository(a1, a2), repository(n1, n2), repository(e1, e2),
            jsonCodec(), fileSystem(), episode(), knowledge());
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
    private static FileSystem fileSystem() {
        return new FileSystem() {
            public String read(Path path, Charset charset) {
                return "{{neurons}}{{effectors}}";
            }
            public void write(Path path, String content, Charset charset) { }
            public boolean exists(Path path) { return true; }
            public Stream<String> walk(Path path, boolean recursive) { return Stream.empty(); }
            public void delete(Path path) { }
        };
    }
    private static JsonCodec jsonCodec() {
        return new JsonCodec() {
            public <T> T unmarshal(String json, Type type) {
                throw new UnsupportedOperationException();
            }
            public String marshal(Object object) { return String.valueOf(object); }
        };
    }
    private static Episode episode() {
        return new Episode() {
            public void encode(String key, Object value) { }
            public Object retrieve(String key) { return null; }
            public Map<String, Object> retrieve() { return Map.of(); }
            public void decay() { }
        };
    }
    private static Knowledge knowledge() {
        return new Knowledge() {
            public void encode(String key, Object value) { }
            public Object retrieve(String key) { return null; }
            public Map<String, Object> retrieve() { return Map.of(); }
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
