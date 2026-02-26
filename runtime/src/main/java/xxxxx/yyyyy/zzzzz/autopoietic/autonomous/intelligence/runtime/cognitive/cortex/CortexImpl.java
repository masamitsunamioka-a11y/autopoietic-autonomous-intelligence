package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.Transducer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.mode.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@ApplicationScoped
public class CortexImpl implements Cortex {
    private static final Logger logger = LoggerFactory.getLogger(CortexImpl.class);
    private final Nucleus nucleus;
    private final Transducer transducer;
    private final Map<String, Mode> modes;
    private final ReentrantLock focus;

    @Inject
    public CortexImpl(Nucleus nucleus,
                      Transducer transducer,
                      @EmitMode Mode emit,
                      @FireMode Mode fire,
                      @PotentiateMode Mode potentiate,
                      @ProjectMode Mode project,
                      @InhibitMode Mode inhibit) {
        this.nucleus = nucleus;
        this.transducer = transducer;
        this.modes = Map.of(
            "EMIT", emit,
            "FIRE", fire,
            "POTENTIATE", potentiate,
            "PROJECT", project,
            "INHIBIT", inhibit);
        this.focus = new ReentrantLock();
    }

    @Override
    public Percept perceive(Stimulus stimulus) {
        this.focus.lock();
        try {
            return this.doPerceive(stimulus);
        } finally {
            this.focus.unlock();
        }
    }

    @Override
    public Optional<Percept> tryPerceive(Stimulus stimulus) {
        if (!this.focus.tryLock()) {
            return Optional.empty();
        }
        try {
            return Optional.of(this.doPerceive(stimulus));
        } finally {
            this.focus.unlock();
        }
    }

    private Percept doPerceive(Stimulus stimulus) {
        Objects.requireNonNull(stimulus.neuron());
        var signal = this.transducer.perception(stimulus);
        var output = this.nucleus.compute(signal, Decision.class);
        logger.debug("[NUCLEUS] Computing: ({}) [{}], Mode: {}, Effector: {}, PropagateTo: {}",
            output.confidence(),
            output.reasoning(),
            output.mode(),
            output.effector(),
            output.neuron());
        var mode = this.modes.get(output.mode().toUpperCase());
        Objects.requireNonNull(mode, "Unknown mode: " + output.mode());
        return mode.handle(stimulus, output);
    }
}
