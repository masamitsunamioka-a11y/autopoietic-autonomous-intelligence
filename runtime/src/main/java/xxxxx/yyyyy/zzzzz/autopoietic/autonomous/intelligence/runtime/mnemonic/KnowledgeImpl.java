package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;

import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

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
    public Map<String, Object> retrieve() {
        return this.semanticRepository.findAll().stream()
            .map(x -> (TraceImpl) x)
            .sorted(comparing(TraceImpl::timestampOf))
            .collect(Collectors.toMap(
                TraceImpl::id,
                TraceImpl::content,
                (x, y) -> y));
    }

    @Override
    public void decay() {
        var all = this.semanticRepository.findAll();
        if (all.size() <= 1) {
            return;
        }
        var seen = new HashSet<String>();
        var expired = all.stream()
            .map(x -> (TraceImpl) x)
            .sorted(comparing(TraceImpl::timestampOf).reversed())
            .filter(x -> !seen.add(x.prefixOf()))
            .map(TraceImpl::id)
            .toList();
        this.semanticRepository.removeAll(expired);
    }
}
