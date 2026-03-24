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
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Potential;

import java.util.List;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;

/// In the future, scope to per-session
@ApplicationScoped
public class EpisodeImpl implements Episode {
    private static final Logger logger = LoggerFactory.getLogger(EpisodeImpl.class);
    private static final int CAPACITY_PER_AREA = 10;
    private final Knowledge knowledge;
    private final Nucleus nucleus;
    private final Repository<Area> areaRepository;
    private final Repository<Trace> episodicRepository;
    private final Service<Impulse, Potential> transmitter;

    @Inject
    public EpisodeImpl(Knowledge knowledge, Nucleus nucleus,
                       Repository<Area> areaRepository,
                       @Episodic Repository<Trace> episodicRepository,
                       @Releasic @Diffusic @Bindic
                       Service<Impulse, Potential> transmitter) {
        this.knowledge = knowledge;
        this.nucleus = nucleus;
        this.areaRepository = areaRepository;
        this.episodicRepository = episodicRepository;
        this.transmitter = transmitter;
    }

    /// [Engineering] hippocampus timestamps traces at encoding
    @Override
    public void encode(Trace trace) {
        var timestamp = now().format(ofPattern("yyyyMMddHHmmss"));
        this.episodicRepository.store(
            new TraceImpl(timestamp + "_" + trace.id(), trace.content()));
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
        var all = this.episodicRepository.findAll();
        var capacity = this.calculateCapacity();
        if (all.size() <= capacity) {
            return;
        }
        var expired = all.stream()
            .limit(all.size() - capacity)
            .map(Trace::id)
            .toList();
        this.episodicRepository.removeAll(expired);
    }

    @Override
    public void promote() {
        var promotion = (Consolidation) this.transmitter.call(
            new ImpulseImpl(null, this.label(), null));
        this.nucleus.integrate(promotion, x -> {
            try {
                x.insights().forEach((y, z) ->
                    this.knowledge.encode(new TraceImpl(y, z)));
            } catch (Exception e) {
                logger.error("promote failed", e);
            }
        });
    }

    private int calculateCapacity() {
        int size = this.areaRepository.findAll().size();
        return CAPACITY_PER_AREA * size;
    }

    private String label() {
        return Episode.class.getSimpleName();
    }
}
