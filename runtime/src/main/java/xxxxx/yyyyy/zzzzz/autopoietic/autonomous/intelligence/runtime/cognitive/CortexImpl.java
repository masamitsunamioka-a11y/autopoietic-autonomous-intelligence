package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.processual.Process;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.integrative.Nucleus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.integrative.Transmitter;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

import java.util.Map;

@ApplicationScoped
public class CortexImpl implements Cortex {
    private static final Logger logger = LoggerFactory.getLogger(CortexImpl.class);
    private final Map<String, Process> processes;
    private final Transmitter transmitter;
    private final Nucleus nucleus;
    private final Event<Percept> event;

    @Inject
    public CortexImpl(@Process.Vocalize Process vocalize,
                      @Process.Fire Process fire,
                      @Process.Potentiate Process potentiate,
                      @Process.Project Process project,
                      @Process.Inhibit Process inhibit,
                      Transmitter transmitter, Nucleus nucleus,
                      Event<Percept> event) {
        this.processes = Map.of(
            "VOCALIZE", vocalize,
            "FIRE", fire,
            "POTENTIATE", potentiate,
            "PROJECT", project,
            "INHIBIT", inhibit);
        this.transmitter = transmitter;
        this.nucleus = nucleus;
        this.event = event;
    }

    @Override
    public void respond(Impulse impulse) {
        var decision = this.transmitter.transmit(impulse, Decision.class);
        this.nucleus.integrate(decision, () -> {
            var process = this.processes.get(decision.process());
            var percept = process.handle(impulse, decision);
            if (percept != null) {
                this.event.fire(percept);
            }
        });
    }
}
