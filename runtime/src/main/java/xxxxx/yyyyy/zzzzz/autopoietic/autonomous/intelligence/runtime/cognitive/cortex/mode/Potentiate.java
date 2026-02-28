package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.mode;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.Decision;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.Mode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Plasticity;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

@PotentiateMode
@ApplicationScoped
public final class Potentiate implements Mode {
    private static final Logger logger = LoggerFactory.getLogger(Potentiate.class);
    private final Cortex cortex;
    private final Plasticity plasticity;

    @Inject
    public Potentiate(Cortex cortex, Plasticity plasticity) {
        this.cortex = cortex;
        this.plasticity = plasticity;
    }

    @Override
    public Percept handle(Impulse impulse, Decision decision) {
        this.plasticity.potentiate(impulse);
        this.plasticity.prune();
        return this.cortex.respond(impulse);
    }
}
