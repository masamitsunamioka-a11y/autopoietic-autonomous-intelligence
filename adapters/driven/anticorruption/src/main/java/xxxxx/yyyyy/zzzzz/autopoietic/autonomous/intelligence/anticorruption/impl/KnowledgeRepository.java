package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Storage;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.working.Semantic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.working.TraceImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Trace;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Semantic
@ApplicationScoped
public class KnowledgeRepository implements Repository<Trace, Trace> {
    private static final Logger logger = LoggerFactory.getLogger(KnowledgeRepository.class);
    private final Storage storage;
    private final Serializer serializer;
    private final Path file;

    @Inject
    public KnowledgeRepository(Storage storage, Serializer serializer) {
        this.storage = storage;
        this.serializer = serializer;
        var configuration = new Configuration().anticorruption();
        var target = Path.of(configuration.get("hippocampal.knowledge.target"), "");
        this.file = target.resolve("knowledge.json");
    }

    @Override
    public Trace find(String id) {
        return this.findAll().stream()
            .filter(x -> x.cue().contains(id))
            .findFirst()
            .orElse(null);
    }

    @Override
    public List<Trace> findAll() {
        if (!this.storage.exists(this.file)) {
            return List.of();
        }
        return List.of(this.read(this.file));
    }

    @Override
    public void store(Trace trace) {
        var all = new ArrayList<>(this.findAll());
        all.add(trace);
        this.write(this.file, all);
    }

    @Override
    public void remove(String id) {
        this.write(this.file, this.findAll().stream()
            .filter(x -> !x.cue().contains(id))
            .toList());
    }

    @Override
    public void removeAll(List<String> ids) {
        this.write(this.file, this.findAll().stream()
            .filter(x -> !ids.contains(x.cue()))
            .toList());
    }

    @Override
    public boolean exists(String id) {
        return Objects.nonNull(this.find(id));
    }

    private TraceImpl[] read(Path file) {
        return this.serializer.deserialize(
            this.storage.read(file, StandardCharsets.UTF_8),
            TraceImpl[].class);
    }

    private void write(Path file, List<Trace> traces) {
        this.storage.write(file,
            this.serializer.serialize(traces),
            StandardCharsets.UTF_8);
    }
}
