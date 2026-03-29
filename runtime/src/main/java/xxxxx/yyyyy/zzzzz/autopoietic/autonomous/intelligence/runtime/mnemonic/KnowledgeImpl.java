package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.CommandPublisher;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;

import java.util.List;

/// In the future, scope to per-session
@ApplicationScoped
public class KnowledgeImpl implements Knowledge {
    private static final Logger logger = LoggerFactory.getLogger(KnowledgeImpl.class);
    private final Repository<Trace> semanticRepository;
    private final CommandPublisher commandPublisher;

    @Inject
    public KnowledgeImpl(@Semantic Repository<Trace> semanticRepository,
                         CommandPublisher commandPublisher) {
        this.semanticRepository = semanticRepository;
        this.commandPublisher = commandPublisher;
    }

    @Override
    public void encode(Trace trace) {
        this.commandPublisher.publish(new EncodeKnowledge(trace));
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
        this.commandPublisher.publish(new DecayKnowledge());
    }
}
