package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    private static final String NAME = "configuration.yaml";
    private static final Map<String, String> SCOPES = ofEntries(
        entry("Area", "neural.areas"),
        entry("Neuron", "neural.neurons"),
        entry("Effector", "neural.effectors"),
        entry("Knowledge", "neocortical.knowledge"),
        entry("Episode", "hippocampal.episode"));
    private final Map yaml;

    public Configuration() {
        try {
            this.yaml = (Map) new ObjectMapper(new YAMLFactory())
                .readValue(this.loadResource(NAME), Map.class)
                .get("anticorruption");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Configuration(Map yaml) {
        this.yaml = yaml;
    }

    public Configuration scope(String key) {
        return new Configuration((Map) this.yaml.get(key));
    }

    public Configuration resolve(Class<?> type) {
        var path = SCOPES.get(type.getSimpleName());
        return stream(path.split("\\."))
            .reduce(this,
                Configuration::scope,
                (x, y) -> y);
    }

    public <T> T get(String key) {
        return (T) stream(key.split("\\."))
            .reduce((Object) this.yaml,
                (x, y) -> ((Map) x).get(y),
                (x, y) -> y);
    }

    public long getLong(String key) {
        var v = Integer.parseInt(this.get(key));
        return v < 0 ? Long.MAX_VALUE : v;
    }

    private String loadResource(String name) {
        try (var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(name)) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
