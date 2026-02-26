package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.mode;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.Decision;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.Mode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.State;

@ProjectMode
@ApplicationScoped
public final class Project implements Mode {
    private static final Logger logger = LoggerFactory.getLogger(Project.class);
    private final State memory;
    private final Cortex cortex;
    private final Thalamus thalamus;

    @Inject
    public Project(State memory, Cortex cortex, Thalamus thalamus) {
        this.memory = memory;
        this.cortex = cortex;
        this.thalamus = thalamus;
    }

    @Override
    public Percept handle(Stimulus stimulus, Decision decision) {
        this.memory.update("last_propagate_from", stimulus.neuron().name());
        this.memory.update("last_propagate_to", decision.neuron());
        return this.cortex.perceive(this.thalamus.relay(stimulus));
    }
}
