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
import java.util.List;
import java.util.Objects;

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
        return this.repository.findAll().stream()
            .sorted(Comparator.comparing(this::timestampOf))
            .toList();
    }

    private Instant timestampOf(Trace trace) {
        var at = trace.cue().lastIndexOf('@');
        return Instant.parse(trace.cue().substring(at + 1));
    }

    @Override
    public void decay() {
        /// TODO: implement knowledge decay
    }
}
