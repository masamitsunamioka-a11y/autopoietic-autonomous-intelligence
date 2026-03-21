package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;

import java.util.HashSet;
import java.util.List;

/// In the future, scope to per-session
@ApplicationScoped
public class KnowledgeImpl implements Knowledge {
    private static final Logger logger = LoggerFactory.getLogger(KnowledgeImpl.class);
    private final Repository<Trace> semanticRepository;

    @Inject
    public KnowledgeImpl(@Semantic Repository<Trace> semanticRepository) {
        this.semanticRepository = semanticRepository;
    }

    @Override
    public void encode(Trace trace) {
        this.semanticRepository.store(trace);
    }

    @Override
    public Trace retrieve(String cue) {
        return this.semanticRepository.find(cue);
    }

    @Override
    public List<Trace> retrieve() {
        return this.semanticRepository.findAll();
    }

    @Override
    public void decay() {
        var all = this.semanticRepository.findAll();
        if (all.size() <= 1) {
            return;
        }
        var seen = new HashSet<String>();
        var reversed = all.reversed();
        var expired = reversed.stream()
            .filter(x -> !seen.add(x.id()))
            .map(Trace::id)
            .toList();
        this.semanticRepository.removeAll(expired);
    }
}
