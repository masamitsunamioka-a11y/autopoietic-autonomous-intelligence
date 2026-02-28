package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.mode;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.Decision;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.Mode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.PerceptImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Episode;

@InhibitMode
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
        this.episode.encode("[INHIBIT]", decision.response());
        return new PerceptImpl(
            area,
            decision.reasoning(),
            decision.confidence(),
            decision.response());
    }
}
