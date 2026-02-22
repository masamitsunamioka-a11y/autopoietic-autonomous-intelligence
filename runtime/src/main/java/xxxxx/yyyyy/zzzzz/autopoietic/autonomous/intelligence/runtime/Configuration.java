package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class Configuration {
    private final Map<String, Object> yaml;

    @SuppressWarnings("unchecked")
    public Configuration(String name) {
        var classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(name)) {
            if (is == null) {
                throw new RuntimeException();
            }
            this.yaml = (Map<String, Object>) new Yaml().load(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        Object current = this.yaml;
        for (String k : key.split("\\.")) {
            current = (current instanceof Map<?, ?> m) ? m.get(k) : null;
        }
        if (current == null) {
            throw new RuntimeException();
        }
        return (T) current;
    }
}
