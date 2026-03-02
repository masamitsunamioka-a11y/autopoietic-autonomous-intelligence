package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.processual.Process;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.ImpulseImpl;
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
    private final Map<String, Process> processes;
    private final Encoder encoder;
    private final Nucleus nucleus;
    private final ReentrantLock focus;

    @Inject
    public CortexImpl(@Process.Vocalize Process vocalize, @Process.Fire Process fire,
                      @Process.Potentiate Process potentiate, @Process.Project Process project,
                      @Process.Inhibit Process inhibit,
                      Encoder encoder, Nucleus nucleus) {
        this.processes = Map.of(
            "VOCALIZE", vocalize,
            "FIRE", fire,
            "POTENTIATE", potentiate,
            "PROJECT", project,
            "INHIBIT", inhibit);
        this.encoder = encoder;
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

    private Decision integrate(Impulse impulse) {
        var signal = this.encoder.encode(impulse, Cortex.class);
        return this.nucleus.integrate(new ImpulseImpl(signal, impulse.area()), Decision.class);
    }

    private Percept doRespond(Impulse impulse) {
        Objects.requireNonNull(impulse.area());
        var output = this.integrate(impulse);
        var process = this.processes.get(output.process().toUpperCase());
        Objects.requireNonNull(process, "Unknown process: " + output.process());
        return process.handle(impulse, output);
    }
}
