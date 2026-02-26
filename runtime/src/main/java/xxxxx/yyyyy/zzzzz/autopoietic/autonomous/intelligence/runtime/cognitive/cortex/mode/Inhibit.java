package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.mode;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.Decision;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.Mode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.InternalPercept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Conversation;

@InhibitMode
@ApplicationScoped
public final class Inhibit implements Mode {
    private static final Logger logger = LoggerFactory.getLogger(Inhibit.class);
    private final Conversation memory;

    @Inject
    public Inhibit(Conversation memory) {
        this.memory = memory;
    }

    @Override
    public Percept handle(Stimulus stimulus, Decision decision) {
        var neuron = stimulus.neuron();
        logger.warn("[CORTEX] INHIBIT on {}: {}", neuron.name(), decision.response());
        this.memory.encode("[INHIBIT]", decision.response());
        return new InternalPercept(
            neuron.name(),
            decision.reasoning(),
            decision.confidence(),
            decision.response());
    }
}
