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

@EmitMode
@ApplicationScoped
public final class Emit implements Mode {
    private static final Logger logger = LoggerFactory.getLogger(Emit.class);
    private final Conversation memory;

    @Inject
    public Emit(Conversation memory) {
        this.memory = memory;
    }

    @Override
    public Percept handle(Stimulus stimulus, Decision decision) {
        var neuron = stimulus.neuron();
        this.memory.encode(neuron.name(), decision.response());
        return new InternalPercept(
            neuron.name(),
            decision.reasoning(),
            decision.confidence(),
            decision.response());
    }
}
