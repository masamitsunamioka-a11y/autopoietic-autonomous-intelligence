package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.mode;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.Decision;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.Mode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.RefractoryGuard;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Organ;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Knowledge;

@FireMode
@ApplicationScoped
public final class Fire implements Mode {
    private static final Logger logger = LoggerFactory.getLogger(Fire.class);
    private final Repository<Effector, Organ> effectorRepository;
    private final Cortex cortex;
    private final Episode episode;
    private final Knowledge knowledge;
    private final RefractoryGuard refractoryGuard;

    @Inject
    public Fire(Repository<Effector, Organ> effectorRepository,
                Cortex cortex, Episode episode, Knowledge knowledge) {
        this.effectorRepository = effectorRepository;
        this.cortex = cortex;
        this.episode = episode;
        this.knowledge = knowledge;
        this.refractoryGuard = new RefractoryGuard();
    }

    @Override
    public Percept handle(Impulse impulse, Decision decision) {
        var effectorName = decision.effector();
        if (this.refractoryGuard.observe(effectorName)) {
            this.refractoryGuard.reset();
            this.episode.encode("[SYSTEM]",
                "[SYSTEM WARNING] Effector " + effectorName + " fired 3+ consecutive times. Do not FIRE again.");
        }
        var output = this.effectorRepository.find(effectorName)
            .fire(this.knowledge.retrieve());
        output.forEach((k, v) ->
            this.knowledge.encode("results." + effectorName + "." + k, v));
        logger.debug("--- fire: {}", effectorName);
        return this.cortex.respond(impulse);
    }
}
