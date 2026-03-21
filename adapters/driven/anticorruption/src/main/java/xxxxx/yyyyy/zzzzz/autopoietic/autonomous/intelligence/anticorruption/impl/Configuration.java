package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    private static final String NAME = "configuration.yaml";
    private static final String BASE = System.getProperty("aai.home", "");
    private final Map<String, Object> yaml;
    private final String prefix;

    @SuppressWarnings("unchecked")
    public Configuration() {
        var classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(NAME)) {
            if (is == null) {
                throw new RuntimeException();
            }
            this.yaml = (Map<String, Object>) new Yaml().load(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.prefix = "anticorruption";
    }

    private Configuration(Map<String, Object> yaml, String prefix) {
        this.yaml = yaml;
        this.prefix = prefix;
    }

    private Configuration scope(String key) {
        var newPrefix = this.prefix.isEmpty()
            ? key
            : this.prefix + "." + key;
        return new Configuration(
            this.yaml, newPrefix);
    }

    /// @formatter:off
    public Configuration neural() {
        return this.scope("neural"); }
    public Configuration synaptic() {
        return this.scope("synaptic"); }
    public Configuration hippocampal() {
        return this.scope("hippocampal"); }
    public Configuration neocortical() {
        return this.scope("neocortical"); }
    public Configuration episode() {
        return this.scope("episode"); }
    public Configuration knowledge() {
        return this.scope("knowledge"); }
    public Configuration function() {
        return this.scope("function"); }
    public Configuration shared() {
        return this.scope("shared"); }
    /// @formatter:on
    public Configuration neural(Class<?> type) {
        return this
            .scope("neural")
            .scope(type.getSimpleName().toLowerCase() + "s");
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        var fullKey = this.prefix.isEmpty()
            ? key
            : this.prefix + "." + key;
        Object o = this.yaml;
        for (String k : fullKey.split("\\.")) {
            o = (o instanceof Map<?, ?> m)
                ? m.get(k)
                : null;
        }
        if (o == null) {
            throw new RuntimeException();
        }
        if (o instanceof String s
            && !BASE.isEmpty() && s.contains("/")) {
            return (T) (BASE + "/" + s);
        }
        return (T) o;
    }

    public long getLong(String key) {
        var raw = Integer.parseInt(this.get(key));
        return raw < 0
            ? Long.MAX_VALUE
            : raw;
    }
}
