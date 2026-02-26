package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Conversation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.State;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@ConversationScoped
public class MemoryImpl implements Conversation, State, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(MemoryImpl.class);
    private final Map<String, Object> conversations;
    private final Map<String, Object> states;
    private final FileSystem fileSystem;
    private final JsonCodec jsonCodec;
    private final Path sessionDir;

    @Inject
    public MemoryImpl(@Localic FileSystem fileSystem, JsonCodec jsonCodec) {
        this.fileSystem = fileSystem;
        this.jsonCodec = jsonCodec;
        var configuration = new Configuration("anticorruption.yaml");
        var base = Path.of(configuration.get("anticorruption.memory.source"), "");
        var raw = Integer.parseInt(configuration.get("anticorruption.memory.sessions"));
        var sessions = raw < 0 ? Long.MAX_VALUE : raw;
        this.sessionDir = base.resolve(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        this.conversations = new ConcurrentHashMap<>();
        this.states = new ConcurrentHashMap<>();
        this.loadPastSessions(base, sessions);
    }

    @Override
    public void encode(String role, String text) {
        Objects.requireNonNull(role);
        Objects.requireNonNull(text);
        this.conversations.put(String.format("%s@%s", role, LocalDateTime.now()), text);
        this.persist();
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
        this.persist();
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

    @Override
    public void decay() {
        /// TODO: implement memory decay
    }

    private void loadPastSessions(Path base, long sessions) {
        if (!this.fileSystem.exists(base)) {
            return;
        }
        this.fileSystem.list(base)
            .sorted(Comparator.reverseOrder())
            .limit(sessions)
            .forEach(x -> {
                var conversation = base.resolve(x).resolve("conversation.json");
                var state = base.resolve(x).resolve("state.json");
                if (this.fileSystem.exists(conversation)) {
                    Map<String, Object> map = this.jsonCodec.unmarshal(
                        this.fileSystem.read(conversation, StandardCharsets.UTF_8));
                    this.conversations.putAll(map);
                }
                if (this.fileSystem.exists(state)) {
                    Map<String, Object> map = this.jsonCodec.unmarshal(
                        this.fileSystem.read(state, StandardCharsets.UTF_8));
                    this.states.putAll(map);
                }
            });
    }

    private void persist() {
        this.fileSystem.write(
            this.sessionDir.resolve("conversation.json"),
            this.jsonCodec.marshal(this.conversations),
            StandardCharsets.UTF_8);
        this.fileSystem.write(
            this.sessionDir.resolve("state.json"),
            this.jsonCodec.marshal(this.states),
            StandardCharsets.UTF_8);
    }
}
