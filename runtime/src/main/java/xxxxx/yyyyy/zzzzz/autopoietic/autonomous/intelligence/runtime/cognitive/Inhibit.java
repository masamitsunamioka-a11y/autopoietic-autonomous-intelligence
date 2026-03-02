package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.working.TraceImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Episode;

@Mode.Inhibit
@ApplicationScoped
public final class Inhibit implements Mode {
    private static final Logger logger = LoggerFactory.getLogger(Inhibit.class);
    private final Episode episode;

    @Inject
    public Inhibit(Episode episode) {
        this.episode = episode;
    }

    @Override
    public Percept handle(Impulse impulse, Decision decision) {
        var area = impulse.area();
        logger.warn("[CORTEX] INHIBIT on {}: {}", area.name(), decision.response());
        this.episode.encode(new TraceImpl("[INHIBIT]", decision.response()));
        return new PerceptImpl(decision.response(), area.name());
    }
}
