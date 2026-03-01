package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.working;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Trace;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@ConversationScoped
public class KnowledgeImpl implements Knowledge, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(KnowledgeImpl.class);
    private final List<Trace> memory;
    private final Repository<Trace, Trace> repository;

    @Inject
    public KnowledgeImpl(@Semantic Repository<Trace, Trace> repository) {
        this.repository = repository;
        this.memory = new CopyOnWriteArrayList<>(this.repository.findAll());
    }

    @Override
    public void encode(Trace trace) {
        Objects.requireNonNull(trace);
        var stamped = Trace.of(trace.key(), trace.value());
        this.memory.add(stamped);
        this.repository.store(stamped);
    }

    @Override
    public Trace retrieve(String cue) {
        return this.memory.stream()
            .filter(t -> t.key().equals(cue))
            .max(Comparator.comparing(Trace::timestamp))
            .orElse(null);
    }

    @Override
    public List<Trace> retrieve() {
        return this.memory.stream()
            .sorted(Comparator.comparing(Trace::timestamp))
            .toList();
    }

    @Override
    public void decay() {
        /// TODO: implement knowledge decay
    }
}
