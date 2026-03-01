package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.learning.Plasticity;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

@Mode.Potentiate
@ApplicationScoped
public final class Potentiate implements Mode {
    private static final Logger logger = LoggerFactory.getLogger(Potentiate.class);
    private final Plasticity plasticity;
    private final Cortex cortex;

    @Inject
    public Potentiate(Plasticity plasticity, Cortex cortex) {
        this.plasticity = plasticity;
        this.cortex = cortex;
    }

    @Override
    public Percept handle(Impulse impulse, Decision decision) {
        this.plasticity.potentiate(impulse);
        this.plasticity.prune();
        return this.cortex.respond(impulse);
    }
}
