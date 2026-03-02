package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.working;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Trace;

import java.io.Serializable;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@ConversationScoped
public class KnowledgeImpl implements Knowledge, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(KnowledgeImpl.class);
    private final Repository<Trace, Trace> repository;

    @Inject
    public KnowledgeImpl(@Semantic Repository<Trace, Trace> repository) {
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
        this.decay();
        return this.repository.findAll().stream()
            .sorted(Comparator.comparing(this::timestampOf))
            .toList();
    }

    @Override
    public void decay() {
        var all = this.repository.findAll();
        if (all.size() <= 1) return;
        var latest = all.stream()
            .collect(Collectors.toMap(
                t -> this.prefixOf(t.cue()),
                Function.identity(),
                (a, b) -> this.timestampOf(a).isAfter(
                    this.timestampOf(b)) ? a : b,
                LinkedHashMap::new));
        if (latest.size() >= all.size()) return;
        var kept = latest.values().stream()
            .map(Trace::cue)
            .collect(Collectors.toSet());
        var expired = all.stream()
            .map(Trace::cue)
            .filter(cue -> !kept.contains(cue))
            .toList();
        logger.debug("[DECAY] knowledge: {} → {}",
            all.size(), latest.size());
        this.repository.removeAll(expired);
    }

    private String prefixOf(String cue) {
        var at = cue.lastIndexOf('@');
        return at >= 0 ? cue.substring(0, at) : cue;
    }

    private Instant timestampOf(Trace trace) {
        var at = trace.cue().lastIndexOf('@');
        return Instant.parse(trace.cue().substring(at + 1));
    }
}
