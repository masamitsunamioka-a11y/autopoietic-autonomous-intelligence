package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification;

import java.util.Optional;

public interface Cortex {
    Percept perceive(Impulse impulse);

    Optional<Percept> tryPerceive(Impulse impulse);
}
