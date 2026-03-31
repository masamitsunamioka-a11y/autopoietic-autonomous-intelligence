package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.CommandPublisher;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Service;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.ImpulseImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Bindic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Diffusic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.synaptic.Releasic;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Potential;

import java.util.List;

/// In the future, scope to per-session
@ApplicationScoped
public class EpisodeImpl implements Episode {
    private static final Logger logger = LoggerFactory.getLogger(EpisodeImpl.class);
    private final Knowledge knowledge;
    private final Nucleus nucleus;
    private final Repository<Trace> episodicRepository;
    private final Service<Impulse, Potential> transmitter;
    private final CommandPublisher commandPublisher;

    @Inject
    public EpisodeImpl(Knowledge knowledge, Nucleus nucleus,
                       @Episodic Repository<Trace> episodicRepository,
                       @Releasic @Diffusic @Bindic
                       Service<Impulse, Potential> transmitter,
                       CommandPublisher commandPublisher) {
        this.knowledge = knowledge;
        this.nucleus = nucleus;
        this.episodicRepository = episodicRepository;
        this.transmitter = transmitter;
        this.commandPublisher = commandPublisher;
    }

    @Override
    public void encode(Trace trace) {
        this.commandPublisher.publish(new EncodeEpisode(trace));
    }

    @Override
    public Trace retrieve(String cue) {
        return this.episodicRepository.find(cue);
    }

    @Override
    public List<Trace> retrieve() {
        return this.episodicRepository.findAll();
    }

    @Override
    public void decay() {
        this.commandPublisher.publish(new DecayEpisode());
    }

    @Override
    public void consolidate() {
        var consolidation = (Consolidation) this.transmitter.call(
            new ImpulseImpl(null, this.label(), null));
        this.nucleus.integrate(consolidation, x -> {
            try {
                x.insights().forEach((k, v) -> this.knowledge.encode(new TraceImpl(k, v)));
            } catch (Exception e) {
                logger.error("consolidate failed", e);
            }
        });
    }

    private String label() {
        return Episode.class.getSimpleName();
    }
}
