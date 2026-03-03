package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Storage;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Configuration;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.working.Episodic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.working.TraceImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Trace;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Collections.reverseOrder;

@Episodic
@ApplicationScoped
public class EpisodeRepository implements Repository<Trace, Trace> {
    private static final Logger logger = LoggerFactory.getLogger(EpisodeRepository.class);
    private static final DateTimeFormatter yyyyMMddHHmmss = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final Storage storage;
    private final Serializer serializer;
    private final Path target;
    private final Path file;
    private final long limit;

    @Inject
    public EpisodeRepository(Storage storage, Serializer serializer) {
        this.storage = storage;
        this.serializer = serializer;
        var configuration = new Configuration().anticorruption();
        this.target = Path.of(configuration.get("hippocampal.episode.target"), "");
        this.file = this.target.resolve(
            "episode_" + LocalDateTime.now().format(yyyyMMddHHmmss) + ".json");
        this.limit = this.calculateLimit(
            Integer.parseInt(configuration.get("hippocampal.episode.limit")));
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
        return this.recentFiles()
            .map(this::read)
            .<Trace>flatMap(Arrays::stream)
            .toList();
    }

    @Override
    public void store(Trace trace) {
        var all = new ArrayList<>(this.findOne());
        all.add(trace);
        this.write(this.file, all);
    }

    @Override
    public void remove(String id) {
        this.write(this.file, this.findOne().stream()
            .filter(x -> !x.cue().contains(id))
            .toList());
    }

    @Override
    public void removeAll(List<String> ids) {
        this.recentFiles()
            .forEach(x -> {
                this.write(x, Stream.<Trace>of(this.read(x))
                    .filter(y -> !ids.contains(y.cue()))
                    .toList());
            });
    }

    @Override
    public boolean exists(String id) {
        return Objects.nonNull(this.find(id));
    }

    private long calculateLimit(int limit) {
        return limit < 0 ? Long.MAX_VALUE : limit;
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

    private Stream<Path> recentFiles() {
        return this.storage.list(this.target)
            .filter(x -> x.toString().endsWith(".json"))
            .sorted(reverseOrder())
            .limit(this.limit);
    }

    private List<Trace> findOne() {
        if (!this.storage.exists(this.file)) {
            return List.of();
        }
        return List.of(this.read(this.file));
    }
}
