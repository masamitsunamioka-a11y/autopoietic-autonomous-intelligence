package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.processual;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.Decision;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.HabituationGuard;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic.TraceImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Knowledge;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Process.Fire
@ApplicationScoped
public final class Fire implements Process {
    private static final Logger logger = LoggerFactory.getLogger(Fire.class);
    private final Cortex cortex;
    private final Knowledge knowledge;
    private final Episode episode;
    private final Repository<Effector> effectorRepository;
    private final HabituationGuard habituationGuard;

    @Inject
    public Fire(Cortex cortex, Knowledge knowledge, Episode episode,
                Repository<Effector> effectorRepository) {
        this.cortex = cortex;
        this.knowledge = knowledge;
        this.episode = episode;
        this.effectorRepository = effectorRepository;
        this.habituationGuard = new HabituationGuard();
    }

    @Override
    public Percept handle(Impulse impulse, Decision decision) {
        var effectorName = decision.effector();
        if (this.habituationGuard.observe(effectorName)) {
            this.habituationGuard.reset();
            this.episode.encode(this.habituationWarning(effectorName));
        }
        Effector effector;
        try {
            effector = this.effectorRepository.find(effectorName);
        } catch (RuntimeException e) {
            this.episode.encode(this.unresolvedWarning(effectorName));
            this.cortex.respond(impulse);
            return null;
        }
        var context = this.knowledge.retrieve().stream()
            .collect(Collectors.toMap(
                Trace::id,
                Trace::content,
                (x, y) -> y, LinkedHashMap::new));
        var output = effector.fire(context);
        output.forEach((k, v) ->
            this.knowledge.encode(
                new TraceImpl("results." + effectorName + "." + k, v)));
        logger.debug("--- fire: {}", effectorName);
        this.cortex.respond(impulse);
        return null;
    }

    private TraceImpl unresolvedWarning(String effectorName) {
        return new TraceImpl("[SYSTEM]",
            "[SYSTEM WARNING] Effector '" + effectorName
                + "' does not exist. Choose a valid Effector or VOCALIZE.");
    }

    private TraceImpl habituationWarning(String effectorName) {
        return new TraceImpl("[SYSTEM]",
            "[SYSTEM WARNING] Effector " + effectorName
                + " fired 3+ consecutive times. Do not FIRE again.");
    }
}
