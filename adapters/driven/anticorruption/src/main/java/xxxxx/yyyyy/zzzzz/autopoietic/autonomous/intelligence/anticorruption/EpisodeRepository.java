package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.working.Episodic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.working.TraceImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Trace;

import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Episodic
@ConversationScoped
public class EpisodeRepository implements Repository<Trace, Trace>, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(EpisodeRepository.class);
    private final Storage storage;
    private final Serializer serializer;
    private final Path base;
    private final Path sessionDir;
    private final long sessions;
    private final List<Trace> traces;

    @Inject
    public EpisodeRepository(Storage storage, Serializer serializer) {
        this.storage = storage;
        this.serializer = serializer;
        var configuration = new Configuration();
        this.base = Path.of(configuration.get("anticorruption.memory.source"), "");
        var raw = Integer.parseInt(configuration.get("anticorruption.memory.sessions"));
        this.sessions = raw < 0 ? Long.MAX_VALUE : raw;
        this.sessionDir = this.base.resolve(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        this.traces = new ArrayList<>();
    }

    @Override
    public Trace find(String id) {
        return this.traces.stream()
            .filter(t -> t.cue().contains(id))
            .findFirst()
            .orElse(null);
    }

    @Override
    public List<Trace> findAll() {
        if (!this.storage.exists(this.base)) {
            return List.of();
        }
        try (var dirs = Files.list(this.base)) {
            return dirs
                .filter(Files::isDirectory)
                .sorted(Comparator.reverseOrder())
                .limit(this.sessions)
                .map(dir -> dir.resolve("episode.json"))
                .filter(this.storage::exists)
                .<List<Map<String, Object>>>map(file ->
                    this.serializer.deserialize(
                        this.storage.read(file, StandardCharsets.UTF_8),
                        List.class))
                .flatMap(List::stream)
                .map(m -> (Trace) new TraceImpl(
                    (String) m.get("cue"), m.get("content")))
                .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to list sessions: " + this.base, e);
        }
    }

    @Override
    public boolean exists(String id) {
        return this.traces.stream().anyMatch(t -> t.cue().contains(id));
    }

    @Override
    public void store(Trace trace) {
        this.traces.add(trace);
        var list = this.traces.stream()
            .map(t -> Map.of("cue", (Object) t.cue(), "content", t.content()))
            .toList();
        this.storage.write(
            this.sessionDir.resolve("episode.json"),
            this.serializer.serialize(list),
            StandardCharsets.UTF_8);
    }

    @Override
    public void remove(String id) {
        this.traces.removeIf(t -> t.cue().contains(id));
    }
}
