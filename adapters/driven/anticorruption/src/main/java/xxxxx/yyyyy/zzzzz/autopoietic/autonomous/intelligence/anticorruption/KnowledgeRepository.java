package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.working.Semantic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Trace;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Semantic
@ApplicationScoped
public class KnowledgeRepository implements Repository<Trace, Trace> {
    private static final Logger logger = LoggerFactory.getLogger(KnowledgeRepository.class);
    private final Storage storage;
    private final Serializer serializer;
    private final Path knowledgeFile;
    private final List<Trace> traces;

    @Inject
    public KnowledgeRepository(Storage storage, Serializer serializer) {
        this.storage = storage;
        this.serializer = serializer;
        var configuration = new Configuration("anticorruption.yaml");
        var base = Path.of(configuration.get("anticorruption.memory.source"), "");
        this.knowledgeFile = base.resolve("knowledge.json");
        this.traces = new ArrayList<>();
    }

    @Override
    public Trace find(String id) {
        return this.traces.stream()
            .filter(t -> t.key().equals(id))
            .findFirst()
            .orElse(null);
    }

    @Override
    public List<Trace> findAll() {
        if (!this.storage.exists(this.knowledgeFile)) {
            return List.of();
        }
        Map<String, Object> map = this.serializer.deserialize(
            this.storage.read(this.knowledgeFile, StandardCharsets.UTF_8));
        return map.entrySet().stream()
            .map(this::decode)
            .toList();
    }

    @Override
    public boolean exists(String id) {
        return this.traces.stream().anyMatch(t -> t.key().equals(id));
    }

    @Override
    public void store(Trace trace) {
        this.traces.add(trace);
        var map = new java.util.LinkedHashMap<String, Object>();
        this.traces.forEach(t -> map.put(t.encoded(), t.value()));
        this.storage.write(
            this.knowledgeFile,
            this.serializer.serialize(map),
            StandardCharsets.UTF_8);
    }

    @Override
    public void remove(String id) {
        this.traces.removeIf(t -> t.key().equals(id));
    }

    private Trace decode(Map.Entry<String, Object> entry) {
        return Trace.decode(entry.getKey(), entry.getValue());
    }
}
