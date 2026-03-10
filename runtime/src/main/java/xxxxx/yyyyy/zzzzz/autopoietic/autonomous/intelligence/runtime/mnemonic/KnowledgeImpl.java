package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Transmitter;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;

import static java.util.Comparator.comparing;

@ApplicationScoped
public class KnowledgeImpl implements Knowledge {
    private static final Logger logger = LoggerFactory.getLogger(KnowledgeImpl.class);
    private final Transmitter transmitter;
    private final Nucleus nucleus;
    private final Repository<Trace> semanticRepository;

    @Inject
    public KnowledgeImpl(Transmitter transmitter, Nucleus nucleus,
                         @Semantic Repository<Trace> semanticRepository) {
        this.transmitter = transmitter;
        this.nucleus = nucleus;
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
        return this.semanticRepository.findAll().stream()
            .sorted(comparing(this::timestampOf))
            .toList();
    }

    @Override
    public void promote() {
        var promotion = this.transmitter.transmit(null, Promotion.class);
        this.nucleus.integrate(promotion, () ->
            promotion.insights().forEach(insight ->
                this.encode(new TraceImpl(insight, insight))));
    }

    @Override
    public void decay() {
        var all = this.semanticRepository.findAll();
        if (all.size() <= 1) {
            return;
        }
        var seen = new HashSet<String>();
        var expired = all.stream()
            .sorted(comparing(this::timestampOf).reversed())
            .filter(x -> !seen.add(this.prefixOf(x.id())))
            .map(Trace::id)
            .toList();
        if (expired.isEmpty()) {
            return;
        }
        logger.debug("[DECAY] knowledge: {} → {}",
            all.size(), all.size() - expired.size());
        this.semanticRepository.removeAll(expired);
    }

    private String prefixOf(String id) {
        var at = id.lastIndexOf('@');
        return at >= 0 ? id.substring(0, at) : id;
    }

    private Instant timestampOf(Trace trace) {
        var at = trace.id().lastIndexOf('@');
        return Instant.parse(trace.id().substring(at + 1));
    }
}
