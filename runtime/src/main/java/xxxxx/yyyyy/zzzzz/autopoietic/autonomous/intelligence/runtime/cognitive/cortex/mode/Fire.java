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
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Conversation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.State;

@FireMode
@ApplicationScoped
public final class Fire implements Mode {
    private static final Logger logger = LoggerFactory.getLogger(Fire.class);
    private final Thalamus thalamus;
    private final Cortex cortex;
    private final Conversation conversation;
    private final State state;
    private final Repository<Effector> effectorRepository;
    private final RefractoryGuard refractoryGuard;

    @Inject
    public Fire(Thalamus thalamus, Cortex cortex, Conversation conversation, State state, Repository<Effector> effectorRepository) {
        this.thalamus = thalamus;
        this.cortex = cortex;
        this.conversation = conversation;
        this.state = state;
        this.effectorRepository = effectorRepository;
        this.refractoryGuard = new RefractoryGuard();
    }

    @Override
    public Percept handle(Stimulus stimulus, Decision decision) {
        var neuron = stimulus.neuron();
        var effectorName = decision.effector();
        logger.debug("[FIRE] effector: {}", effectorName);
        if (this.refractoryGuard.observe(effectorName)) {
            this.refractoryGuard.reset();
            this.conversation.encode("[SYSTEM]",
                "[SYSTEM WARNING] Effector " + effectorName + " fired 3+ consecutive times. Do not FIRE again.");
        }
        var output = neuron.modules().stream()
            .flatMap(m -> m.effectors().stream())
            .filter(e -> e.name().equals(effectorName))
            .findFirst()
            .orElseThrow()
            .fire(this.state.state());
        output.forEach((k, v) ->
            this.state.update("results." + effectorName + "." + k, v));
        logger.debug("\n--- act: {}\n--- own: {}\n--- all: {}",
            effectorName, this.own(neuron), this.all());
        return this.cortex.perceive(this.thalamus.relay(stimulus));
    }

    private Object own(Neuron neuron) {
        return neuron.modules().stream()
            .flatMap(m -> m.effectors().stream())
            .map(Effector::name)
            .toList();
    }

    private Object all() {
        return this.effectorRepository.findAll().stream()
            .map(Effector::name)
            .toList();
    }
}
