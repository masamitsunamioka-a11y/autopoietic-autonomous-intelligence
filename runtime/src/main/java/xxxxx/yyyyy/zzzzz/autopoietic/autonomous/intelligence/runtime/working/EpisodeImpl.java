package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.working;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Trace;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class EpisodeImpl implements Episode {
    private static final Logger logger = LoggerFactory.getLogger(EpisodeImpl.class);
    private static final int CAPACITY = 50;
    private final Repository<Trace, Trace> repository;

    @Inject
    public EpisodeImpl(@Episodic Repository<Trace, Trace> repository) {
        this.repository = repository;
    }

    @Override
    public void encode(Trace trace) {
        Objects.requireNonNull(trace);
        this.repository.store(trace);
    }

    @Override
    public Trace retrieve(String cue) {
        return this.repository.findAll().stream()
            .filter(t -> t.cue().contains(cue))
            .max(Comparator.comparing(this::timestampOf))
            .orElse(null);
    }

    @Override
    public List<Trace> retrieve() {
        return this.repository.findAll().stream()
            .sorted(Comparator.comparing(this::timestampOf))
            .toList();
    }

    @Override
    public void decay() {
        var all = this.repository.findAll();
        if (all.size() <= CAPACITY) return;
        var expired = all.stream()
            .sorted(Comparator.comparing(this::timestampOf))
            .limit(all.size() - CAPACITY)
            .map(Trace::cue)
            .toList();
        logger.debug("[DECAY] episode: {} → {}",
            all.size(), all.size() - expired.size());
        this.repository.removeAll(expired);
    }

    private Instant timestampOf(Trace trace) {
        var at = trace.cue().lastIndexOf('@');
        return Instant.parse(trace.cue().substring(at + 1));
    }
}
