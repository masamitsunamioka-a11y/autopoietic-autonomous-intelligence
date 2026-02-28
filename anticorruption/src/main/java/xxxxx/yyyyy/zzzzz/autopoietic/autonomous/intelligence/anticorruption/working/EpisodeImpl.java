package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.working;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.FileSystem;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.JsonCodec;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Localic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Episode;

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
public class EpisodeImpl implements Episode, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(EpisodeImpl.class);
    private final Map<String, Object> memory;
    private final FileSystem fileSystem;
    private final JsonCodec jsonCodec;
    private final Path sessionDir;

    @Inject
    public EpisodeImpl(@Localic FileSystem fileSystem, JsonCodec jsonCodec) {
        this.fileSystem = fileSystem;
        this.jsonCodec = jsonCodec;
        var configuration = new Configuration("anticorruption.yaml");
        var base = Path.of(configuration.get("anticorruption.memory.source"), "");
        var raw = Integer.parseInt(configuration.get("anticorruption.memory.sessions"));
        var sessions = raw < 0 ? Long.MAX_VALUE : raw;
        this.sessionDir = base.resolve(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        this.memory = new ConcurrentHashMap<>();
        this.loadPastSessions(base, sessions);
    }

    @Override
    public void encode(String key, Object value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        this.memory.put(String.format("%s@%s", key, LocalDateTime.now()), value);
        this.persist();
    }

    @Override
    public Object retrieve(String key) {
        return this.memory.entrySet().stream()
            .filter(x -> x.getKey().startsWith(key + "@"))
            .max(Map.Entry.comparingByKey())
            .map(Map.Entry::getValue)
            .orElse(null);
    }

    @Override
    public Map<String, Object> retrieve() {
        return Map.copyOf(this.memory);
    }

    @Override
    public void decay() {
        /// TODO: implement episode decay
    }

    private void loadPastSessions(Path base, long sessions) {
        if (!this.fileSystem.exists(base)) {
            return;
        }
        this.fileSystem.list(base)
            .filter(x -> !x.equals("knowledge.json"))
            .sorted(Comparator.reverseOrder())
            .limit(sessions)
            .forEach(x -> {
                var episodeFile = base.resolve(x).resolve("episode.json");
                if (this.fileSystem.exists(episodeFile)) {
                    Map<String, Object> map = this.jsonCodec.unmarshal(
                        this.fileSystem.read(episodeFile, StandardCharsets.UTF_8));
                    this.memory.putAll(map);
                }
            });
    }

    private void persist() {
        this.fileSystem.write(
            this.sessionDir.resolve("episode.json"),
            this.jsonCodec.marshal(this.memory),
            StandardCharsets.UTF_8);
    }
}
