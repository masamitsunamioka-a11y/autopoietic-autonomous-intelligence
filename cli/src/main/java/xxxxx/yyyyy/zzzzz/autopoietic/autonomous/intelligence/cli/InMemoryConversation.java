package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.cli;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Conversation;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryConversation implements Conversation {
    private final Map<String, Object> memory = new ConcurrentHashMap<>();

    @Override
    public void write(String role, String text) {
        Objects.requireNonNull(role, "Role must not be null.");
        Objects.requireNonNull(text, "Text must not be null.");
        var key = String.format("%s@%s", role, LocalDateTime.now());
        this.memory.put(key, text);
    }

    @Override
    public Map<String, Object> snapshot() {
        return Map.copyOf(this.memory);
    }
}
