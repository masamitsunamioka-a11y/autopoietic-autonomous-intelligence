package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Memory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@ConversationScoped
public class MemoryImpl implements Memory {
    private static final Logger logger = LoggerFactory.getLogger(MemoryImpl.class);
    private final Map<String, Object> conversations;
    private final Map<String, Object> states;

    @Inject
    public MemoryImpl() {
        this.conversations = new ConcurrentHashMap<>();
        this.states = new ConcurrentHashMap<>();
    }

    @Override
    public void record(String role, String text) {
        Objects.requireNonNull(role);
        Objects.requireNonNull(text);
        this.conversations.put(String.format("%s@%s", role, LocalDateTime.now()), text);
    }

    @Override
    public Map<String, Object> conversation() {
        return Map.copyOf(this.conversations);
    }

    @Override
    public void update(String key, Object value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        this.states.put(String.format("%s@%s", key, LocalDateTime.now()), value);
    }

    @Override
    public Object lookup(String key) {
        return this.states.entrySet().stream()
            .filter(x -> x.getKey().startsWith(key + "@"))
            .max(Map.Entry.comparingByKey())
            .map(Map.Entry::getValue)
            .orElse(null);
    }

    @Override
    public Map<String, Object> state() {
        return Map.copyOf(this.states);
    }
}
