package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@ApplicationScoped
public class EpisodeImpl implements Episode {
    private static final Logger logger = LoggerFactory.getLogger(EpisodeImpl.class);
    private static final int CAPACITY_PER_AREA = 10;
    private final Repository<Area> areaRepository;
    private final Repository<Trace> episodicRepository;

    @Inject
    public EpisodeImpl(Repository<Area> areaRepository,
                       @Episodic Repository<Trace> episodicRepository) {
        this.areaRepository = areaRepository;
        this.episodicRepository = episodicRepository;
    }

    @Override
    public void encode(Trace trace) {
        this.episodicRepository.store(trace);
    }

    @Override
    public Trace retrieve(String cue) {
        return this.episodicRepository.find(cue);
    }

    @Override
    public Map<String, Object> retrieve() {
        return this.episodicRepository.findAll().stream()
            .sorted(comparing(this::timestampOf))
            .collect(Collectors.toMap(
                Trace::id,
                Trace::content,
                (x, y) -> y));
    }

    @Override
    public void decay() {
        var all = this.episodicRepository.findAll();
        var capacity = this.calculateCapacity();
        if (all.size() <= capacity) {
            return;
        }
        var expired = all.stream()
            .sorted(comparing(this::timestampOf))
            .limit(all.size() - capacity)
            .map(Trace::id)
            .toList();
        logger.debug("[DECAY] episode: {} → {}",
            all.size(), all.size() - expired.size());
        this.episodicRepository.removeAll(expired);
    }

    private int calculateCapacity() {
        return CAPACITY_PER_AREA
            * Math.max(1, this.areaRepository.findAll().size());
    }

    private Instant timestampOf(Trace trace) {
        var at = trace.id().lastIndexOf('@');
        return Instant.parse(trace.id().substring(at + 1));
    }
}
