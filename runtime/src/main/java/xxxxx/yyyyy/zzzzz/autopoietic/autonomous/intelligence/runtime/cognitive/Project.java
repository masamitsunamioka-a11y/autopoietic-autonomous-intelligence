package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.ImpulseImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engravable;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

@Mode.Project
@ApplicationScoped
public final class Project implements Mode {
    private static final Logger logger = LoggerFactory.getLogger(Project.class);
    private final Cortex cortex;
    private final Repository<Area, Engravable> areaRepository;

    @Inject
    public Project(Cortex cortex, Repository<Area, Engravable> areaRepository) {
        this.cortex = cortex;
        this.areaRepository = areaRepository;
    }

    @Override
    public Percept handle(Impulse impulse, Decision decision) {
        var targetArea = this.areaRepository.find(decision.area());
        var projected = new ImpulseImpl(impulse.signal(), targetArea);
        return this.cortex.respond(projected);
    }
}
