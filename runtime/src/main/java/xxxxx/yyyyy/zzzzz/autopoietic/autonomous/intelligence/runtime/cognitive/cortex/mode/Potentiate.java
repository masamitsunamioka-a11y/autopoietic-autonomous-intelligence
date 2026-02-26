package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.mode;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.Decision;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex.Mode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Plasticity;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;

@PotentiateMode
@ApplicationScoped
public final class Potentiate implements Mode {
    private static final Logger logger = LoggerFactory.getLogger(Potentiate.class);
    private final Plasticity plasticity;
    private final Cortex cortex;
    private final Thalamus thalamus;

    @Inject
    public Potentiate(Plasticity plasticity, Cortex cortex, Thalamus thalamus) {
        this.plasticity = plasticity;
        this.cortex = cortex;
        this.thalamus = thalamus;
    }

    @Override
    public Percept handle(Stimulus stimulus, Decision decision) {
        this.plasticity.potentiate(stimulus);
        this.plasticity.prune();
        return this.cortex.perceive(this.thalamus.relay(stimulus));
    }
}
