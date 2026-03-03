package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.working;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engravable;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Trace;

import java.time.Instant;
import java.util.List;

import static java.util.Comparator.comparing;

@ApplicationScoped
public class EpisodeImpl implements Episode {
    private static final Logger logger = LoggerFactory.getLogger(EpisodeImpl.class);
    private static final int CAPACITY_PER_AREA = 10;
    private final Repository<Area, Engravable> areaRepository;
    private final Repository<Trace, Trace> repository;

    @Inject
    public EpisodeImpl(Repository<Area, Engravable> areaRepository,
                       @Episodic Repository<Trace, Trace> repository) {
        this.areaRepository = areaRepository;
        this.repository = repository;
    }

    @Override
    public void encode(Trace trace) {
        this.repository.store(trace);
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
        var capacity = this.calculateCapacity();
        if (all.size() <= capacity) {
            return;
        }
        var expired = all.stream()
            .sorted(comparing(this::timestampOf))
            .limit(all.size() - capacity)
            .map(Trace::cue)
            .toList();
        logger.debug("[DECAY] episode: {} → {}",
            all.size(), all.size() - expired.size());
        this.repository.removeAll(expired);
    }

    private int calculateCapacity() {
        return CAPACITY_PER_AREA
            * Math.max(1, this.areaRepository.findAll().size());
    }

    private Instant timestampOf(Trace trace) {
        var at = trace.cue().lastIndexOf('@');
        return Instant.parse(trace.cue().substring(at + 1));
    }
}
