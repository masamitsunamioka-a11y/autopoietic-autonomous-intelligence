package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.cli;

import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.State;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryState implements State {
    private final Map<String, Object> memory = new ConcurrentHashMap<>();

    @Override
    public void write(String key, Object value) {
        Objects.requireNonNull(key, "Key must not be null.");
        Objects.requireNonNull(value, "Value must not be null.");
        String timestampedKey = String.format("%s@%s", key, LocalDateTime.now());
        this.memory.put(timestampedKey, value);
    }

    @Override
    public Object read(String key) {
        Objects.requireNonNull(key, "Read key must not be null.");
        return this.memory.entrySet().stream()
                .filter(x -> x.getKey().startsWith(key + "@"))
                .max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    @Override
    public Map<String, Object> snapshot() {
        return Map.copyOf(this.memory);
    }
}
