package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.mode.*;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Encoder;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

@ApplicationScoped
public class CortexImpl implements Cortex {
    private static final Logger logger = LoggerFactory.getLogger(CortexImpl.class);
    private final Nucleus nucleus;
    private final Encoder encoder;
    private final Map<String, Mode> modes;
    private final ReentrantLock focus;

    @Inject
    public CortexImpl(Nucleus nucleus, Encoder encoder,
                      @VocalizeMode Mode vocalize, @FireMode Mode fire,
                      @PotentiateMode Mode potentiate, @ProjectMode Mode project,
                      @InhibitMode Mode inhibit) {
        this.nucleus = nucleus;
        this.encoder = encoder;
        this.modes = Map.of(
            "VOCALIZE", vocalize,
            "FIRE", fire,
            "POTENTIATE", potentiate,
            "PROJECT", project,
            "INHIBIT", inhibit);
        this.focus = new ReentrantLock();
    }

    @Override
    public Percept respond(Impulse impulse) {
        this.focus.lock();
        try {
            return this.doRespond(impulse);
        } finally {
            this.focus.unlock();
        }
    }

    private Percept doRespond(Impulse impulse) {
        Objects.requireNonNull(impulse.area());
        var signal = this.encoder.encode(impulse, Cortex.class);
        var output = this.nucleus.integrate(signal, Decision.class);
        var mode = this.modes.get(output.mode().toUpperCase());
        Objects.requireNonNull(mode, "Unknown mode: " + output.mode());
        return mode.handle(impulse, output);
    }
}
