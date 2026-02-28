package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.mode;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.Decision;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.Mode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.ImpulseImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engram;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

@ProjectMode
@ApplicationScoped
public final class Project implements Mode {
    private static final Logger logger = LoggerFactory.getLogger(Project.class);
    private final Repository<Area, Engram> areaRepository;
    private final Cortex cortex;

    @Inject
    public Project(Repository<Area, Engram> areaRepository, Cortex cortex) {
        this.areaRepository = areaRepository;
        this.cortex = cortex;
    }

    @Override
    public Percept handle(Impulse impulse, Decision decision) {
        var targetArea = this.areaRepository.find(decision.area());
        var projected = new ImpulseImpl(impulse.signal(), targetArea);
        return this.cortex.respond(projected);
    }
}
