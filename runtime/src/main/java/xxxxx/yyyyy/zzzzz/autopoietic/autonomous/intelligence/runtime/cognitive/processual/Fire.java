package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.processual;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.Decision;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.RefractoryGuard;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.working.TraceImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engravable;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Trace;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Process.Fire
@ApplicationScoped
public final class Fire implements Process {
    private static final Logger logger = LoggerFactory.getLogger(Fire.class);
    private final Cortex cortex;
    private final Knowledge knowledge;
    private final Episode episode;
    private final Repository<Effector, Engravable> effectorRepository;
    private final RefractoryGuard refractoryGuard;

    @Inject
    public Fire(Cortex cortex, Knowledge knowledge, Episode episode,
                Repository<Effector, Engravable> effectorRepository) {
        this.cortex = cortex;
        this.knowledge = knowledge;
        this.episode = episode;
        this.effectorRepository = effectorRepository;
        this.refractoryGuard = new RefractoryGuard();
    }

    @Override
    public Percept handle(Impulse impulse, Decision decision) {
        var effectorName = decision.effector();
        if (this.refractoryGuard.observe(effectorName)) {
            this.refractoryGuard.reset();
            this.episode.encode(new TraceImpl("[SYSTEM]",
                "[SYSTEM WARNING] Effector " + effectorName + " fired 3+ consecutive times. Do not FIRE again."));
        }
        Effector effector;
        try {
            effector = this.effectorRepository.find(effectorName);
        } catch (RuntimeException e) {
            logger.warn("[FIRE] Effector '{}' not found", effectorName);
            this.episode.encode(new TraceImpl("[SYSTEM]",
                "[SYSTEM WARNING] Effector '" + effectorName
                    + "' does not exist. Choose a valid Effector or VOCALIZE."));
            return this.cortex.respond(impulse);
        }
        var context = this.knowledge.retrieve().stream()
            .collect(Collectors.toMap(
                Trace::cue, Trace::content, (a, b) -> b, LinkedHashMap::new));
        var output = effector.fire(context);
        output.forEach((k, v) ->
            this.knowledge.encode(new TraceImpl("results." + effectorName + "." + k, v)));
        logger.debug("--- fire: {}", effectorName);
        return this.cortex.respond(impulse);
    }
}
