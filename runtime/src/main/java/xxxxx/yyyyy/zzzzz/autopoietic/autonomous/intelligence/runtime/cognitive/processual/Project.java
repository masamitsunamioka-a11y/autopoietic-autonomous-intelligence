package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.processual;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.Decision;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.ImpulseImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.working.TraceImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engravable;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Episode;

@Process.Project
@ApplicationScoped
public final class Project implements Process {
    private static final Logger logger = LoggerFactory.getLogger(Project.class);
    private final Cortex cortex;
    private final Episode episode;
    private final Repository<Area, Engravable> areaRepository;

    @Inject
    public Project(Cortex cortex, Episode episode,
                   Repository<Area, Engravable> areaRepository) {
        this.cortex = cortex;
        this.episode = episode;
        this.areaRepository = areaRepository;
    }

    @Override
    public Percept handle(Impulse impulse, Decision decision) {
        var targetArea = this.areaRepository.find(decision.area());
        if (targetArea == null) {
            this.episode.encode(this.unresolvedWarning(decision));
            return this.cortex.respond(impulse);
        }
        var projected = new ImpulseImpl(impulse.signal(), targetArea);
        return this.cortex.respond(projected);
    }

    private TraceImpl unresolvedWarning(Decision decision) {
        return new TraceImpl("[SYSTEM]",
            "[SYSTEM WARNING] Area '" + decision.area()
                + "' does not exist. Choose a valid Area or VOCALIZE.");
    }
}
