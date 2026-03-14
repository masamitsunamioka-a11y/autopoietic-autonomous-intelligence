package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.ImpulseImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Bindic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Diffusic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Releasic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Potential;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@ApplicationScoped
public class KnowledgeImpl implements Knowledge {
    private static final Logger logger = LoggerFactory.getLogger(KnowledgeImpl.class);
    private final Nucleus nucleus;
    private final Repository<Trace> semanticRepository;
    private final Service<Impulse, Potential> transmitter;

    @Inject
    public KnowledgeImpl(Nucleus nucleus,
                         @Semantic Repository<Trace> semanticRepository,
                         @Releasic @Diffusic @Bindic
                         Service<Impulse, Potential> transmitter) {
        this.nucleus = nucleus;
        this.semanticRepository = semanticRepository;
        this.transmitter = transmitter;
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
            .sorted(comparing(this::timestampOf))
            .collect(Collectors.toMap(
                Trace::id, Trace::content, (x, y) -> y));
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

    @Override
    public void promote() {
        var promotion = (Promotion) this.transmitter.call(
            new ImpulseImpl(null, this.getClass(), null, null));
        this.nucleus.integrate(promotion, x -> {
            x.insights().forEach((y, z) ->
                this.encode(new TraceImpl(y, z)));
        });
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
