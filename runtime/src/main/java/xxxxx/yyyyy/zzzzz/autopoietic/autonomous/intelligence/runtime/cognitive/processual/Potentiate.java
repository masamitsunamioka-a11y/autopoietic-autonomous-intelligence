package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.processual;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.Decision;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.autopoietic.Autopoiesis;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

@Process.Potentiate
@ApplicationScoped
public final class Potentiate implements Process {
    private static final Logger logger = LoggerFactory.getLogger(Potentiate.class);
    private final Autopoiesis autopoiesis;
    private final Cortex cortex;

    @Inject
    public Potentiate(Autopoiesis autopoiesis, Cortex cortex) {
        this.autopoiesis = autopoiesis;
        this.cortex = cortex;
    }

    @Override
    public Percept handle(Impulse impulse, Decision decision) {
        this.autopoiesis.compensate(impulse);
        this.cortex.respond(impulse);
        return null;
    }
}
