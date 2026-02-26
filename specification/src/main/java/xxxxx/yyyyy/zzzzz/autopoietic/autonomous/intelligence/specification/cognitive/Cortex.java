package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;

import java.util.Optional;

public interface Cortex {
    Percept perceive(Stimulus stimulus);

    Optional<Percept> tryPerceive(Stimulus stimulus);
}
