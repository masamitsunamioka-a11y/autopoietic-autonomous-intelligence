package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    private final Map<String, Object> yaml;
    private final String prefix;

    public Configuration() {
        this("configuration.yaml");
    }

    @SuppressWarnings("unchecked")
    private Configuration(String name) {
        var classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(name)) {
            if (is == null) {
                throw new RuntimeException();
            }
            this.yaml = (Map<String, Object>) new Yaml().load(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.prefix = "";
    }

    private Configuration(Map<String, Object> yaml, String prefix) {
        this.yaml = yaml;
        this.prefix = prefix;
    }

    public Configuration anticorruption() {
        return new Configuration(this.yaml, "anticorruption");
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        var fullKey = this.prefix.isEmpty()
            ? key : this.prefix + "." + key;
        Object current = this.yaml;
        for (String k : fullKey.split("\\.")) {
            current = (current instanceof Map<?, ?> m) ? m.get(k) : null;
        }
        if (current == null) {
            throw new RuntimeException();
        }
        return (T) current;
    }
}
