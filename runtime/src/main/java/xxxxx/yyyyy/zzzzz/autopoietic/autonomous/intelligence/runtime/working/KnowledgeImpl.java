package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.working;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Trace;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;

import static java.util.Comparator.comparing;

@ApplicationScoped
public class KnowledgeImpl implements Knowledge {
    private static final Logger logger = LoggerFactory.getLogger(KnowledgeImpl.class);
    private final Repository<Trace, Trace> repository;

    @Inject
    public KnowledgeImpl(@Semantic Repository<Trace, Trace> repository) {
        this.repository = repository;
    }

    @Override
    public void encode(Trace trace) {
        this.repository.store(trace);
        this.decay();
    }

    @Override
    public Trace retrieve(String cue) {
        return this.repository.find(cue);
    }

    @Override
    public List<Trace> retrieve() {
        return this.repository.findAll().stream()
            .sorted(comparing(this::timestampOf))
            .toList();
    }

    @Override
    public void decay() {
        var all = this.repository.findAll();
        if (all.size() <= 1) {
            return;
        }
        var seen = new HashSet<String>();
        var expired = all.stream()
            .sorted(comparing(this::timestampOf).reversed())
            .filter(x -> !seen.add(this.prefixOf(x.cue())))
            .map(Trace::cue)
            .toList();
        if (expired.isEmpty()) {
            return;
        }
        logger.debug("[DECAY] knowledge: {} → {}",
            all.size(), all.size() - expired.size());
        this.repository.removeAll(expired);
    }

    /// [Engineering] As detailed in docs/kandel.md
    private String prefixOf(String cue) {
        var at = cue.lastIndexOf('@');
        return at >= 0 ? cue.substring(0, at) : cue;
    }

    /// [Engineering] As detailed in docs/kandel.md
    private Instant timestampOf(Trace trace) {
        var at = trace.cue().lastIndexOf('@');
        return Instant.parse(trace.cue().substring(at + 1));
    }
}
