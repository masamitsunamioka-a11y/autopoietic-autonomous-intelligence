package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import org.junit.jupiter.api.Test;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engram;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Module;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Conversation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.State;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransducerImplTest {
    /// N1->M1->E1 and N2->M2->E2: perception for N1 must not include E2
    @Test
    void perceptionShouldNotLeakEffectorsFromOtherNeurons() {
        var e1 = effector("E1");
        var e2 = effector("E2");
        var m1 = module("M1", e1);
        var m2 = module("M2", e2);
        var n1 = neuron("N1", m1);
        var n2 = neuron("N2", m2);
        var encoder = new TransducerImpl(
            repository(n1, n2),
            repository(m1, m2),
            repository(e1, e2),
            fileSystem(),
            jsonCodec(),
            conversation(),
            state()
        );
        var result = encoder.perception(stimulus("test", n1));
        assertTrue(result.contains("E1"), "E1 should be in perception prompt of N1");
        assertFalse(result.contains("E2"), "E2 must not leak into perception prompt of N1");
    }

    /// @formatter:off
    private static Effector effector(String name) {
        return new Effector() {
            public String name() {
                return name;
            }
            public String function() {
                return name + "-function";
            }
            public Map<String, Object> fire(Map<String, Object> state) {
                return Map.of();
            }
        };
    }
    private static Module module(String name, Effector... effectors) {
        return new Module() {
            public String name() {
                return name;
            }
            public String function() {
                return name + "-function";
            }
            public String disposition() {
                return name + "-disposition";
            }
            public List<Effector> effectors() {
                return List.of(effectors);
            }
        };
    }
    private static Neuron neuron(String name, Module... modules) {
        return new Neuron() {
            public String name() {
                return name;
            }
            public String function() {
                return name + "-function";
            }
            public String disposition() {
                return name + "-disposition";
            }
            public List<Module> modules() {
                return List.of(modules);
            }
        };
    }
    @SafeVarargs
    private static <T> Repository<T> repository(T... items) {
        return new Repository<T>() {
            public T find(String id) {
                throw new UnsupportedOperationException();
            }
            public List<T> findAll() {
                return List.of(items);
            }
            public void store(Engram engram) {
            }
            public void remove(String id) {
            }
        };
    }
    private static FileSystem fileSystem() {
        return new FileSystem() {
            public String read(Path path, Charset charset) {
                return "{{modules}}{{effectors}}";
            }
            public void write(Path path, String content, Charset charset) {
            }
            public boolean exists(Path path) {
                return true;
            }
            public Stream<String> walk(Path path, boolean recursive) {
                return Stream.empty();
            }
            public void delete(Path path) {
            }
        };
    }
    private static JsonCodec jsonCodec() {
        return new JsonCodec() {
            public <T> T unmarshal(String json, Type type) {
                throw new UnsupportedOperationException();
            }
            public String marshal(Object object) {
                return String.valueOf(object);
            }
        };
    }
    private static Conversation conversation() {
        return new Conversation() {
            public void decay() {
            }
            public void encode(String role, String text) {
            }
            public Map<String, Object> conversation() {
                return Map.of();
            }
        };
    }
    private static State state() {
        return new State() {
            public void decay() {
            }
            public void update(String key, Object value) {
            }
            public Object lookup(String key) {
                return null;
            }
            public Map<String, Object> state() {
                return Map.of();
            }
        };
    }
    private static Stimulus stimulus(String input, Neuron neuron) {
        return new Stimulus() {
            public String input() {
                return input;
            }
            public Neuron neuron() {
                return neuron;
            }
        };
    }
    /// @formatter:on
}
