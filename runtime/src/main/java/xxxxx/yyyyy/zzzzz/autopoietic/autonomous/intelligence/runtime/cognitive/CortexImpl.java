package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Nucleus;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

@ApplicationScoped
public class CortexImpl implements Cortex {
    private static final Logger logger = LoggerFactory.getLogger(CortexImpl.class);
    private final Map<String, Mode> modes;
    private final Nucleus nucleus;
    private final ReentrantLock focus;

    @Inject
    public CortexImpl(@Mode.Vocalize Mode vocalize, @Mode.Fire Mode fire,
                      @Mode.Potentiate Mode potentiate, @Mode.Project Mode project,
                      @Mode.Inhibit Mode inhibit,
                      Nucleus nucleus) {
        this.modes = Map.of(
            "VOCALIZE", vocalize,
            "FIRE", fire,
            "POTENTIATE", potentiate,
            "PROJECT", project,
            "INHIBIT", inhibit);
        this.nucleus = nucleus;
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
        var output = this.nucleus.integrate(impulse, Cortex.class, Decision.class);
        var mode = this.modes.get(output.mode().toUpperCase());
        Objects.requireNonNull(mode, "Unknown mode: " + output.mode());
        return mode.handle(impulse, output);
    }
}
