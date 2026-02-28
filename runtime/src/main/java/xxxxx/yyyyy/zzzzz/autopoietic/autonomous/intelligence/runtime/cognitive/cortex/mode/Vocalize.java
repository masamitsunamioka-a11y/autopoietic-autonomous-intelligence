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

@VocalizeMode
@ApplicationScoped
public final class Vocalize implements Mode {
    private static final Logger logger = LoggerFactory.getLogger(Vocalize.class);
    private final Episode episode;

    @Inject
    public Vocalize(Episode episode) {
        this.episode = episode;
    }

    @Override
    public Percept handle(Impulse impulse, Decision decision) {
        var area = impulse.area();
        this.episode.encode(area.name(), decision.response());
        return new PerceptImpl(
            area,
            decision.reasoning(),
            decision.confidence(),
            decision.response());
    }
}
